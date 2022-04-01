package com.lolo;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;


/*
Create the following 2 tables in Cassandra

CREATE keyspace if not exists demo
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE table demo.movies (
    movieId int primary key,
    title text,
    genres text
);

CREATE TABLE demo.ratings(
    userId int, 
    movieId int, 
    rating float, 
    timestamp bigint,
    primary key ((userId), timestamp, movieId)
) with clustering order by (timestamp desc);

USE demo;

SELECT * FROM movies LIMIT 10;
SELECT * FROM ratings LIMIT 10;

SELECT * FROM movies;
SELECT * FROM ratings; 
*/

public class _001_CsvToCassandra {
	private static SparkSession spark = null;
	
	public static void loadCsvToCassandraTable(String path, String keyspace, String table) {
		Dataset<Row> ds = spark
				.read()
				.format("csv")
				.option("header", true)
				.option("inferSchema", true)
				.load(path);
		ds = cleanColumns(ds);
		
		Map<String, String> options = new HashMap<>();
		options.put("table", table);
		options.put("keyspace", keyspace);
		
		ds
			.write()
			.format("org.apache.spark.sql.cassandra")
			.options(options)
			.mode(SaveMode.Append)
			.save();			
		
	}
	
	public static Dataset<Row> cleanColumns(Dataset<Row> df){
		Dataset<Row> dataset = df;
		for(String col:df.columns()) {
			dataset = dataset.withColumnRenamed(col, col.toLowerCase());
		}
		return dataset;
	}
	
	public static void main(String[] args) {
	//  String basePath = args[0];
        String basePath = "data/rdd/input";
		System.out.println("Base path: " + basePath);
		SparkConf conf = new SparkConf()
				.setAppName(_001_CsvToCassandra.class.getName())
				.setIfMissing("spark.master", "local[*]")
                .setIfMissing("spark.cassandra.auth.username", "cassandra")
                .setIfMissing("spark.cassandra.auth.password", "cassandra")
                .setIfMissing("spark.cassandra.connection.host", "127.0.0.1");
		
		spark = SparkSession.builder().config(conf).getOrCreate();
		loadCsvToCassandraTable(basePath + "/movies.csv", "demo", "movies");
		loadCsvToCassandraTable(basePath + "/ratings.csv", "demo", "ratings");		
		spark.close();
		
	}

}
