package com.chaty.credit.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CreditFileModel {
	
	private LocalDate date;
	
	private String location;
	
	private String credit;
	
	private String debit;


}
