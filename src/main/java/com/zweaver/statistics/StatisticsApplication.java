package com.zweaver.statistics;

import com.zweaver.statistics.entity.Customer;
import com.zweaver.statistics.repository.CustomerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StatisticsApplication {

	private static final Logger log = LoggerFactory.getLogger(StatisticsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(StatisticsApplication.class, args);
	}
}
