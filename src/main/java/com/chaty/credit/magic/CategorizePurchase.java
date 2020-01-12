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

/**
 * Tags a category from a predefined list compiled by me.
 * 
 * Since the combinations are endless to tag a given location to a category, i
 * decided to go with just a few to see how the data will be grouped.
 * 
 * 
 * 
 * @author chaty
 *
 */
@Component
public class CategorizePurchase {

	@Autowired
	ResourceLoader resourceLoader;

	private Resource resource;

	public CategorizePurchase(ResourceLoader resourceLoader) {
		super();
		this.resourceLoader = resourceLoader;
		this.resource = resourceLoader.getResource("classpath:data/category.json");
	}

	/**
	 * Loads a static JSON file into a list of {@link Category} objects.
	 * 
	 * @param location
	 * @return {@link Optional} {@link Category}
	 * @throws IOException
	 */
	public Optional<Category> findCategoryByLocation(final String location) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		InputStream is = resource.getInputStream();
		List<Category> categList = mapper.readValue(is, new TypeReference<List<Category>>() {
		});

		Optional<Category> categoryOpt = categList.stream()
				.filter(cat -> cat.getOptions().parallelStream().anyMatch(optn -> location.contains(optn))).findFirst();

		return categoryOpt;

	}

	/**
	 * 
	 * Tags a found Category or links OTHER as a category.
	 * 
	 * @param purchaseList
	 * @return List of {@link CreditFileModel}
	 */
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
