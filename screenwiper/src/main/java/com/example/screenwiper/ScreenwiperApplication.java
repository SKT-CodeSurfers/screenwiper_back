package com.example.screenwiper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ScreenwiperApplication {

	public static void main(String[] args) {
		// Load the .env file
		Dotenv dotenv = Dotenv.load();

		// Optionally, you can use the dotenv instance to access environment variables
		// For example:
		// String accessKey = dotenv.get("cloud.aws.credentials.accessKey");

		// Start the Spring Boot application
		SpringApplication.run(ScreenwiperApplication.class, args);
	}
}
