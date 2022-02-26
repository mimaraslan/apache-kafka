package com.lolo.model;

import java.io.Serializable;

public class Vote implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String personName;

	public Vote(String personName) {
		this.personName = personName;
	}

	public Vote() {
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	
}