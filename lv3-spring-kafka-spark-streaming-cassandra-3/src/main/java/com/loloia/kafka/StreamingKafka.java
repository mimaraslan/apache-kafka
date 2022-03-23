package com.loloia.kafka;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

public class StreamingKafka {

	public JavaInputDStream<ConsumerRecord<String, String>> streamingKafka;
	
	public StreamingKafka(JavaStreamingContext javaStreamingContext) {
		
		Map<String, Object> kafkaParams = new HashMap<String, Object>();
		kafkaParams.put("bootstrap.servers", "localhost:9093, localhost:9094, localhost:9095");
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
		kafkaParams.put("auto.offset.reset", "latest");
		kafkaParams.put("enable.auto.commit", false);
		Collection<String> topics = Arrays.asList("messages");
		 
		this.streamingKafka = KafkaUtils.createDirectStream(
						javaStreamingContext, 
						LocationStrategies.PreferConsistent(), 
						ConsumerStrategies.<String, String> Subscribe(topics, kafkaParams));
	}
}
