package com.loloia.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loloia.model.Status;
import com.loloia.model.Vote;
import com.loloia.service.SpringBootKafkaProducer;

@RestController
public class SpringBootKafkaController {

	@Autowired
	SpringBootKafkaProducer springBootKafkaProducer;

	
	/*
	 
	 http://localhost:8080/vote
	 
	 { "name": "Test1" }
	*/
	@RequestMapping("/vote")
	public Status vote(@RequestBody Vote vote) throws ExecutionException, InterruptedException {

		springBootKafkaProducer.send(vote.getName());

		return new Status("ok");
	}

}