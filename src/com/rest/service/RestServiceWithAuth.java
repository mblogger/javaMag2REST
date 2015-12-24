package com.rest.service;

import java.io.File;
import java.io.FileReader;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestServiceWithAuth {

	static final String MAGENTO_REST_API_URL = "http://local.magento2.com/index.php/rest/V1/integration/admin/token";
	static final String MAGENTO_REST_API_URL_CAT = "http://local.magento2.com//index.php/rest/V1/categories";

	public static void main(String[] args) {
		try {
			// Get the access token
			Client client = Client.create();
			WebResource webResource = client.resource(MAGENTO_REST_API_URL);

			JSONParser parser = new JSONParser();
			Object parsedObj = parser.parse(new FileReader(new File("/home/admin/creds.json")));
			JSONObject json = (JSONObject) parsedObj;

			ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("Content-Type", MediaType.APPLICATION_JSON).post(ClientResponse.class, json.toJSONString());

			if (response.getStatus() != 200) {
				new Exception().getStackTrace();
				// throw new RuntimeException("Failed : HTTP error code : " +
				// response.getStatus());
			}

			String token = response.getEntity(String.class);
			System.out.println("Token : " + token);

			// Get the access token
			webResource = client.resource(MAGENTO_REST_API_URL_CAT);
			parser = new JSONParser();
			parsedObj = parser.parse(new FileReader(new File("/home/admin/input.json")));
			json = (JSONObject) parsedObj;

			response = webResource.header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
					.header("Authorization", "Bearer " + token.replaceAll("\"", ""))
					.post(ClientResponse.class, json.toJSONString());

			if (response.getStatus() != 200) {
				new Exception().getStackTrace();
				// throw new RuntimeException("Failed : HTTP error code :: " +
				// response.getStatus());
			}

			String output = response.getEntity(String.class);
			System.out.println("Server response : " + output);

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}
