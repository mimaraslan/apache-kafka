package com.loloia.kafka;

import java.io.NotSerializableException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.SerializationException;

import com.loloia.helpers.KafkaProperties;
import com.loloia.model.Article;

public class Kafka {
	
	public KafkaProperties kafkaProperties;
	
	private String topicName;
	
	public Kafka(String topicName) {
		this.topicName = topicName;
	}
	
	public KafkaProducer<String, String> createProducer(Properties producerConfProps) {
		return new KafkaProducer<String, String>(producerConfProps);
	}
	
	public void runKafkaProducer(Article article, KafkaProducer<String, String> producer) throws NotSerializableException {		
		long time = System.currentTimeMillis();
		try {
			final ProducerRecord<String, String> record = new ProducerRecord<>(topicName, article.getArticleKey(), 
					//article.getArticleAuthor() + " " +
					//article.getArticleCompany() + " " +
					article.getArticleTitle());
				
            RecordMetadata metadata = null;
			try {
				metadata = producer.send(record).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
            long elapsedTime = System.currentTimeMillis() - time;
            System.out.printf("sent record(key=%s | value=%s) | meta(partition=%d, | offset=%d) time=%d\n",
                      record.key(), record.value(), metadata.partition(),
                      metadata.offset(), elapsedTime);
		}
		catch(SerializationException e) {
			System.out.println("Serialization exception");
			e.printStackTrace();
		}
		catch(IllegalStateException e) {
			System.out.println("Illegal State exception. Maybe a Producer is already closed");
			e.printStackTrace();			
		}
	}
}