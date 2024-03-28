package com.intuit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootApplication
@EnableJms
public class UserMgtApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(UserMgtApplication.class, args);

	}

}
