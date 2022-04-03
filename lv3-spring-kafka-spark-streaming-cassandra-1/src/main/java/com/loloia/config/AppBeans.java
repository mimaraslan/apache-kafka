package com.loloia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Session;
import com.loloia.service.SpringBootKafkaProducer;

@Configuration
public class AppBeans {

	// kafka
	@Bean
	public SpringBootKafkaProducer initProducer() {
		return new SpringBootKafkaProducer();
	}
	
	// cassandra
	@Bean
	public Session session() {
		return sessionManager().getSession();
	}

	@Bean
	public CassandraSessionManager sessionManager() {
		return new CassandraSessionManager();
	}
}