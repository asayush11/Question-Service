package com.example.questions_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuestionsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuestionsServiceApplication.class, args);
	}

}
