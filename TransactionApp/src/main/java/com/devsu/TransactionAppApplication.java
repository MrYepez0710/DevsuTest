package com.devsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;

@SpringBootApplication(exclude = {RedisReactiveAutoConfiguration.class})
public class TransactionAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionAppApplication.class, args);
	}

}
