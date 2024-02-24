package com.chaty.credit.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

@Data
@Document(indexName = "whatsapp")
public class ChatModel {

	@Id
	private String id;

	@Field(type = FieldType.Date)
	private LocalDate date;
	
	@Field(type=FieldType.Text)
	private String message;

	private String sentBy;

	@Field(type = FieldType.Nested)
	private List<String> emojiList;


}
