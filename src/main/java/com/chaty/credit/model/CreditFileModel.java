package com.chaty.credit.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CreditFileModel {

	private String id;

	private LocalDate date;

	private String location;

	private String credit;

	private String debit;

	private String category;

}
