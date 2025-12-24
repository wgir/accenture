package com.accenture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class FranchiseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FranchiseApiApplication.class, args);
	}

}
