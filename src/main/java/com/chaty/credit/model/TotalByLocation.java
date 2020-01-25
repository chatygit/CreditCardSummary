package com.chaty.credit.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

@Data
@Document(indexName = "chaty", type = "totallocation")
public class TotalByLocation {
	
	@Id
	private Long id;
	
	private String location;
	
	private Integer creditTotal;
	
	private Integer debitTotal;
	
	@Field(type = FieldType.Nested, includeInParent = true)
	private List<CreditFileModel> purchaseList;


}
