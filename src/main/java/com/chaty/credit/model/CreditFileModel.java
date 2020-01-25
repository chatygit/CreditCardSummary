package com.chaty.credit.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import lombok.Data;

@Data
@Document(indexName = "chaty")
public class CreditFileModel {

	@Id
	private Long id;

	@Field(fielddata = true)
	private LocalDate date;
	@Field(fielddata = true)
	private String location;

	private String credit;

	private String debit;
	@Field(fielddata = true)
	private String category;

}
