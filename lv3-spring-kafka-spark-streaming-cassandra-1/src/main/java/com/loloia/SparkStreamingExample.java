package com.loloia;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import com.loloia.model.Vote;

import kafka.serializer.StringDecoder;
import scala.Tuple2;

public class SparkStreamingExample {

	public static JavaSparkContext sc;

	public static void main(String[] args) throws IOException, InterruptedException {

		String brokers = "localhost:9092,localhost:9093";
		String topics = "votes";

		SparkConf sparkConf = new SparkConf();
		sparkConf.setMaster("local[2]");
		sparkConf.setAppName("SparkStreamingExample");
		sparkConf.set("spark.cassandra.connection.host", "127.0.0.1");

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(10));

		HashSet<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
		HashMap<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", brokers);

		JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);

		JavaDStream<String> lines = messages.map((Function<Tuple2<String, String>, String>) Tuple2::_2);

		JavaPairDStream<String, Integer> voteCount = lines
				.mapToPair((PairFunction<String, String, Integer>) s -> new Tuple2<>(s, 1))
				.reduceByKey((Function2<Integer, Integer, Integer>) (i1, i2) -> i1 + i2);

		sc = jssc.sparkContext();

		voteCount.foreachRDD((v1, v2) -> {
			v1.foreach((x) -> {
				CassandraTableScanJavaRDD<CassandraRow> previousVotes = javaFunctions(sc)
						.cassandraTable("voting", "votes").where("name = '" + x._1() + "'");

				Integer oldVotes = 0;
				if (previousVotes.count() > 0) {
					oldVotes = previousVotes.first().getInt("votes");
				}

				Integer newVotes = oldVotes + x._2();

				List<Vote> votes = Arrays.asList(new Vote(x._1(), newVotes));
				JavaRDD<Vote> rdd = sc.parallelize(votes);

				javaFunctions(rdd).writerBuilder("voting", "votes", mapToRow(Vote.class)).saveToCassandra();
			});

			// return null;
		});

		voteCount.print();

		jssc.start();
		jssc.awaitTermination();
	}
}