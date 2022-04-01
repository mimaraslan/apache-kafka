package com.lolo;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

public class _005_MysqlToCassandra2 {
	private static SparkSession spark = null;
	
	public static void registerTable(String serverName, String dbName, String table, String username, String password) {
		spark
			.read()
			.format("jdbc")
			.option("url", "jdbc:mysql://" + serverName + "/" + dbName+"?autoReconnect=true&useSSL=false")
			.option("driver", "com.mysql.jdbc.Driver")
			.option("dbtable", table)
			.option("user", username)
			.option("password", password)
			.load()
			.createOrReplaceTempView(table);
	}
	
	
	public static void main(String[] args) {
		
		String keyspace = "mycompanydb";
		String table = "employees";
		
		SparkConf conf = new SparkConf()
				.setAppName(_005_MysqlToCassandra2.class.getName())
				.setIfMissing("spark.master", "local[*]");
		
		spark = SparkSession.builder().config(conf).getOrCreate();
		registerTable("localhost", "mycompanydb", "employees", "katerina", "123456789");
		
		spark.sql("select * from employees").show();
		
		
		spark.table("employees")
		.write()
		.format("csv")
		.mode(SaveMode.Overwrite)
		.save("employees-csv");
		

		Map<String, String> options = new HashMap<>();
		options.put("table", table);
		options.put("keyspace", keyspace);
		
		spark.table("employees")
		.write()
		.format("org.apache.spark.sql.cassandra")
		.options(options)
		.mode(SaveMode.Append)
		.save();
	}
}
