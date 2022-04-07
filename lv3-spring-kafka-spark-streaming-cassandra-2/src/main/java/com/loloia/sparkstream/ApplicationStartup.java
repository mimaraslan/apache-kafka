package com.loloia.sparkstream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.loloia.sparkstream.executor.SparkKafkaStreamExecutor;

public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext ac = event.getApplicationContext();
		SparkKafkaStreamExecutor sparkKafkaStreamExecutor= ac.getBean(SparkKafkaStreamExecutor.class);
		Thread thread = new Thread(sparkKafkaStreamExecutor);
		thread.start();
	}
}