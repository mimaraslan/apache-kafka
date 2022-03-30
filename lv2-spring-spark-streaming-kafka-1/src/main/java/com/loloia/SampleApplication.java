package com.loloia;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.google.gson.Gson;
import com.loloia.sparkstream.ApplicationStartup;

@SpringBootApplication
public class SampleApplication {
	
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(SampleApplication.class);
		springApplication.addListeners(new ApplicationStartup());
		springApplication.run(args);
	}
	
	@Bean
	public Gson gson() {
		return new Gson();
	}
}
