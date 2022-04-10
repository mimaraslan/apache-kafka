package com.loloia.rest;


import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Client {

	public Response response;
	
	public Client(String baseUri, Map<String, String> parametersMap, String basePath) {
		this.response = RestAssured
				.given()
					.contentType("application/json")
					.queryParams(parametersMap)
					.baseUri(baseUri)
					.basePath(basePath)
				.when()
					.get()
				.thenReturn(); 
	}
}