package com.loloia.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.loloia.model.Vote;

@Configuration
@Controller
public class WelcomeController {

	@Autowired
	Session session;

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {

		final ResultSet rows = session.execute("SELECT * FROM votes");

		ArrayList<Vote> results = new ArrayList<>();

		for (Row row : rows.all()) {
			results.add(new Vote(row.getString("name"), row.getInt("votes")));
		}

		Collections.sort(results, (a, b) -> b.getVotes().compareTo(a.getVotes()));

		model.put("results", results);

		return "welcome";
	}
}