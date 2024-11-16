package com.chaty.credit.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Category {
	
	private String name;
	private String type;
	private List<String> options;

}
