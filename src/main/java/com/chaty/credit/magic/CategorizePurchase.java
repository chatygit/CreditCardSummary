package com.chaty.credit.magic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.chaty.credit.model.Category;
import com.chaty.credit.model.CreditFileModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CategorizePurchase {

	@Autowired
	ResourceLoader resourceLoader;

	public Optional<Category> findCategoryByLocation(final String location) throws IOException {

		Resource resource = resourceLoader.getResource("classpath:data/category.json");

		ObjectMapper mapper = new ObjectMapper();
		InputStream is = resource.getInputStream();
		List<Category> categList = mapper.readValue(is, new TypeReference<List<Category>>() {
		});

	   Optional<Category> categoryOpt =  categList.stream().filter( cat -> cat.getOptions().stream().anyMatch(optn -> location.contains(optn))).findFirst();
				
	   return categoryOpt;

	}

	public List<CreditFileModel> categorizePurchases(List<CreditFileModel> purchaseList) {

		final List<CreditFileModel> catagorizedList = new ArrayList<>();

		purchaseList.stream().forEach(purchase -> {

			Optional<Category> category;
			try {
				category = findCategoryByLocation(purchase.getLocation());

				if (category.isPresent()) {
					purchase.setCategory(category.get().getName());
					catagorizedList.add(purchase);
				} else {
					purchase.setCategory("OTHER");
					catagorizedList.add(purchase);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		return catagorizedList;

	}

}
