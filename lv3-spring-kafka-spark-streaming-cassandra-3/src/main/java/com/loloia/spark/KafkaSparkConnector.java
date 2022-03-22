package com.loloia.spark;

import java.util.Arrays;
import java.util.Collection;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.loloia.helpers.KafkaProperties;

import scala.Tuple2;

public class KafkaSparkConnector {

	public StreamingContext sparkContext;
	
	public JavaPairDStream<String, String> createKafkaDirectStream(Collection<String> topics) {
		
		this.sparkContext = new StreamingContext();
		
		KafkaProperties props = new KafkaProperties();

		JavaInputDStream<ConsumerRecord<String, String>> directKafkaStream = KafkaUtils.createDirectStream(
				sparkContext.streamingContext,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String> Subscribe(topics, props.kafkaParams));
		
		//Mapping Kafka message to Key | Value
		JavaPairDStream<String, String> results = directKafkaStream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
		
		return results;
	}
	
	public JavaPairDStream<String, Integer> processKafkaMessage(JavaPairDStream<String, String> kafkaStream) {
		//Taking only Value (News Title)
		JavaDStream<String> lines = kafkaStream.map(tuple2 -> tuple2._2());
		
		//Making a List of words
		JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(x.split(" ")).iterator());

		//Filtering, Grouping and counting words
		return words
				  .mapToPair(s->new Tuple2<>(s.replace("[^a-zA-Z0-9]", ""), 1))
				  .reduceByKey((i1, i2)->i1 + i2)
				  .filter(f->f._1.length() > 3);
	}
}