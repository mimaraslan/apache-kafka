package com.lolo;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


public class _004_MysqlToCassandra1 {
	
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("Cassandra Spark Connection")
				.setIfMissing("spark.master", "local[*]")
				.setIfMissing("spark.cassandra.connection.host", "127.0.0.1");
		
		SparkSession session = SparkSession.builder().config(conf).getOrCreate();
		
		Dataset<Row> df = session
		.read()
		.format("org.apache.spark.sql.cassandra")
	    .option("table", "ratings")
	    .option("keyspace", "movielens")
	    .load()
	    .cache();
	    
	    df.createOrReplaceTempView("ratings");
	    
	    session
	    .read()
	    .format("jdbc")
	    .option("url", "jdbc:mysql://localhost/movielens")
	    .option("driver", "com.mysql.jdbc.Driver")
	    .option("dbtable", "movies")
	    .option("user", "katerina")
	    .option("password", "123456789")
	    .load()
	    .cache()
	    .createOrReplaceTempView("movies");
	    
	    
		
	    Dataset<Row> agg = session
	    .sql("select t1.movieid, t1.title, avg(t2.rating) from movies t1 join ratings t2 on t1.movieid = t2.movieid group by t1.movieid, t1.title");
	    
	    agg.coalesce(1)
	    .write()
	    .format("csv")
	    .option("header", "true")
	    .mode("overwrite")
	    .save("ratings");
		
		agg.show();
		
		session.close();	
	}

}