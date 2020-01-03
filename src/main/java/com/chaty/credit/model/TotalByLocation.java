package com.chaty.credit.model;

import java.util.List;

import lombok.Data;

@Data
public class TotalByLocation {
	
	private String location;
	
	private Integer creditTotal;
	
	private Integer debitTotal;
	
	private List<CreditFileModel> purchaseList;


}
