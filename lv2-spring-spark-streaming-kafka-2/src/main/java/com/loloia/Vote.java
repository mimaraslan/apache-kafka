package com.loloia;

import java.io.Serializable;

public class Vote implements Serializable {
	private String name;
	private Integer votes;

	public Vote(String name, Integer votes) {
		this.name = name;
		this.votes = votes;
	}

	public Vote() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}
}