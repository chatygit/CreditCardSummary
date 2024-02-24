package com.chaty.credit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories
public class CreditCardSummaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditCardSummaryApplication.class, args);
	}

}
