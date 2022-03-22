package com.loloia.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class StreamingContext {
	
	public JavaStreamingContext streamingContext;
    
    public StreamingContext() {
    	
    	SparkConf sparkConf = new SparkConf()
    		.setAppName("NewsParsing")
    		.setMaster("local[2]")
    		.set("spark.streaming.stopGracefullyOnShutdown", "true")
    		.set("spark.cassandra.connection.host", "127.0.0.1")
    		
    		//This will create 3 partitions with 2GB of RAM per each
    		.set("spark.cores.max", "2")
    		.set("spark.executor.cores", "1")
    		.set("spark.executor.memory", "1GB")
    	
    		//.set("spark.locality.wait", "100")
    		.set("spark.streaming.backpressure.enabled", "true")
    		.set("spark.streaming.kafka.consumer.poll.ms", "512");
    	
//		sparkConf.set("spark.cassandra.connection.native.port", "9042");
//		sparkConf.set("spark.cassandra.connection.rpc.port", "9160");
//		sparkConf.set("spark.cassandra.connection.timeout_ms", "5000");
//		sparkConf.set("spark.cassandra.read.timeout_ms", "200000");
    	
//		sparkConf.set("spark.cassandra.auth.username", "test_user");
//		sparkConf.set("spark.cassandra.auth.password", "test_password");
    	 
    	this.streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(5));
    	//streamingContext.checkpoint("./.checkpoint");
    }
}