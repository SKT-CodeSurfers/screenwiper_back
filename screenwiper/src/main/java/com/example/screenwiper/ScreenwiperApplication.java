package com.example.screenwiper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.screenwiper.repository") // 추가된 부분
public class ScreenwiperApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScreenwiperApplication.class, args);
	}
}
