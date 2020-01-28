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

	@Field(fielddata = true)
	private LocalDate date;
	
	@Field(fielddata = true, type=FieldType.Text)
	private String message;
	
	@Field(fielddata = true)
	private String sentBy;

	@Field(fielddata = true, type = FieldType.Nested)
	private List<String> emojiList;


}
