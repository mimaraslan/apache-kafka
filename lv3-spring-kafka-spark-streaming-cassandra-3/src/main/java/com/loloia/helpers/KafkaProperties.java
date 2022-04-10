package com.loloia.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

public class KafkaProperties {

	public static final String KAFKA_BROKERS = "localhost:9093, localhost:9094, localhost:9095";
	public static final int QUEUE_BOUNDARY = 1000;
	public static final String CLIENT_ID = "client1";
	public static final String TOPIC_NAME = "NewsAPI";
	public static final String GROUP_ID_CONFIG = "consumerGroup1";
	public final int NO_MESSAGE_FOUND_TRESHOLD = 25;
	public static final String OFFSET_RESET_LATEST = "latest";
	public static final String OFFSET_RESET_EARLIER = "earliest";
	public static final Integer MAX_POLL_RECORDS = 1;
	public Map<String, Object> kafkaParams;
	public final String outputPath = System.getProperty("user.dir") + "data/output/kafkaSpark";

	public KafkaProperties() {
		this.kafkaParams = new HashMap<String, Object>();
		this.kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
		this.kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		this.kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		this.kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
		this.kafkaParams.put("auto.offset.reset", OFFSET_RESET_LATEST);
		this.kafkaParams.put("enable.auto.commit", false);
	}

	public Properties KafkaProducerProperties() {
		Properties producerConfProps = new Properties();
		producerConfProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
		producerConfProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerConfProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return producerConfProps;
	}
}