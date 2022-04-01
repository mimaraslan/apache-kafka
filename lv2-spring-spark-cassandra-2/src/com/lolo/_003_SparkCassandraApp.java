package com.lolo;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;

public class _003_SparkCassandraApp {

	private static SparkSession spark = null;

	public static void createSparkView(String path, String tableName) {
		spark.read().format("csv").option("header", true).option("inferSchema", true).load(path)
				.createOrReplaceTempView(tableName);
	}

	public static void createSparkView(String keySpace, String tableName, String viewName) {
		spark.read().format("org.apache.spark.sql.cassandra").option("keyspace", keySpace)
				.option("table", tableName).load().createOrReplaceTempView(viewName);

	}

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setAppName(_003_SparkCassandraApp.class.getName())
				.setIfMissing("spark.master", "local[*]")
				.setIfMissing("connection.host", "127.0.0.1")
				.setIfMissing("connection.port", "9042");
		spark = SparkSession.builder().config(conf).getOrCreate();

        String basePath = "data/rdd/input";
		System.out.println("Base path: " + basePath);
		
		String moviesPath = basePath + "/movies.csv";
		String ratingsPath = basePath + "/ratings.csv";

		createSparkView(moviesPath, "movies");
		createSparkView(ratingsPath, "ratings");

		Dataset<Row> moviesAgg = spark.sql("SELECT t1.movieId movieid, t1.title, avg(t2.rating) avg_rating from "
				+ " movies t1 JOIN ratings t2 on t1.movieId = t2.movieId GROUP BY "
				+ " t1.movieId, t1.title ORDER BY avg_rating desc");
		moviesAgg.show();

		// Let's create a direct Cassandra session to create a table in Cassandra
		CassandraConnector cassandraConnector = CassandraConnector.apply(spark.sparkContext());
		Session cassandraConnection = cassandraConnector.openSession();

		cassandraConnection.execute(
				"create table if not exists demo.movies_agg(movieid int primary key, title text, avg_rating float)");

		cassandraConnection.close();

		moviesAgg.write().format("org.apache.spark.sql.cassandra").option("keyspace", "demo")
				.option("table", "movies_agg").mode(SaveMode.Append).save();

		createSparkView("demo", "movies_agg", "movies_agg");

		System.out.println("Showing data from cassandra table");

		spark.sql("select * from movies_agg").show();

		spark.close();

	}

}
