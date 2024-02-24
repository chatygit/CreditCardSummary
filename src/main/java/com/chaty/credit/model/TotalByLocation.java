package com.chaty.credit.model;

import lombok.Data;

import java.util.List;

@Data
public class TotalByLocation {

	private Long id;
	
	private String location;
	
	private Integer creditTotal;
	
	private Integer debitTotal;
	private List<CreditFileModel> purchaseList;


}
