package com.lolo.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
 
import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
 
@Configuration
public class SpringBootKafkaProducer {
 
    @Value("${brokerList}")
    private String brokerList;
 
    @Value("${sync}")
    private String sync;
 
    @Value("${topic}")
    private String topic;
 
    private Producer<String, String> producer;
 
    public SpringBootKafkaProducer() {
    }
 
    @PostConstruct
    public void initIt() {
		Properties kafkaProps = new Properties();

		kafkaProps.put("bootstrap.servers", brokerList);
		kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		kafkaProps.put("acks", "1");
		kafkaProps.put("retries", "1");
		kafkaProps.put("linger.ms", 5);
 
        producer = new KafkaProducer<>(kafkaProps);
    }
 
	public void send(String value) throws ExecutionException, InterruptedException {
		if ("sync".equalsIgnoreCase(sync)) {
			sendSync(value);
		} else {
			sendAsync(value);
		}
	}
 
    private void sendSync(String value) throws ExecutionException, InterruptedException {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
        producer.send(record).get();
    }
 
    private void sendAsync(String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
        producer.send(record, (RecordMetadata recordMetadata, Exception e) -> {
            if (e != null) {
                e.printStackTrace();
            }
        });
    }
}