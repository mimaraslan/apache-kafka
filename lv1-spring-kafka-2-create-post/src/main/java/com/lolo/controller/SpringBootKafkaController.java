package com.lolo.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lolo.config.SpringBootKafkaProducer;
import com.lolo.model.Status;
import com.lolo.model.Vote;

@RestController
//@RequestMapping("/api")
public class SpringBootKafkaController {

	@Autowired
	SpringBootKafkaProducer springBootKafkaProducer;

	/*
	 POST
	 http://localhost:8082/vote
	 
	 { "name": "Test1" }
	*/
	@PostMapping("/vote")
	public Status vote(@RequestBody Vote vote) throws ExecutionException, InterruptedException {

		springBootKafkaProducer.send(vote.getPersonName());

		return new Status("ok");
	}

}