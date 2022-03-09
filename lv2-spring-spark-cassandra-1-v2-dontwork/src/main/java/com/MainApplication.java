package com;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lolo.utils.PropertyFileReader;

@SpringBootApplication
public class MainApplication {

	private static final Logger logger = Logger.getLogger(MainApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
		
		Properties prop = new Properties();
		try {
			prop = PropertyFileReader.readPropertyFile();
		} catch (Exception e1) {
			logger.error(e1.getMessage());
		}

		SparkConf conf = new SparkConf();
		conf.setAppName(prop.getProperty("app.name"));
		conf.setMaster("local[*]");
		conf.set("spark.cassandra.connection.host", prop.getProperty("spring.data.cassandra.host"));
		conf.set("spark.cassandra.connection.port", prop.getProperty("spring.data.cassandra.port"));

		JavaSparkContext sc = new JavaSparkContext(conf);
		sc.stop();
	}

}