package com.chaty.credit.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatModel {


	private String id;

	private LocalDate date;

	private String message;

	private String sentBy;

	private List<String> emojiList;


}
