package com.lolo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppBeans {

	@Bean
	public SpringBootKafkaProducer initProducer() {
		return new SpringBootKafkaProducer();
	}
}