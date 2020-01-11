package com.chaty.credit.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Category {
	
	private String name;
	private String type;
	private List<String> options;

}
