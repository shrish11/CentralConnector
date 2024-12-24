package com.freshworks.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.netflix.conductor", "com.freshworks" })
public class CentralConnector {

	public static void main(String[] args) {
		SpringApplication.run(CentralConnector.class, args);
	}

}
