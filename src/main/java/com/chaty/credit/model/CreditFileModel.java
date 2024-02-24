package com.chaty.credit.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

@Data
@Document(indexName = "chaty")
public class CreditFileModel {

	@Id
	private String id;

	@Field(type = FieldType.Date)
	private LocalDate date;
	
	@Field(type = FieldType.Text)
	private String location;

	@Field(type = FieldType.Float)
	private String credit;

	@Field(type = FieldType.Float)
	private String debit;
	
	@Field(type = FieldType.Text)
	private String category;

}
