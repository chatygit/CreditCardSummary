package com.chaty.credit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.credit.magic.AggregatePurchase;
import com.chaty.credit.magic.CategorizePurchase;
import com.chaty.credit.model.CreditFileModel;
import com.chaty.credit.model.TotalByLocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RestController
@RequestMapping("/credit/api")
public class Controller {

	@Autowired
	private DataProcessor dataProcessor;

	@Autowired
	private AggregatePurchase aggregator;
	
	@Autowired
	private CategorizePurchase categorize;

	@GetMapping
	public String test() {
		//writeToFiles();
		return "chaty";
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/aggregate/year")
	public List<TotalByLocation> getAggregateYear() {
		return aggregator.aggregateByYear(dataProcessor.processFile());
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/aggregate/{year}/month")
	public List<TotalByLocation> getAggregateMonth(@PathVariable("year") final Integer year) {
		return aggregator.aggregateByMonth(getTransactionByYear(year));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/aggregate/location/{year}")
	public List<TotalByLocation> getAggregateLocation(@PathVariable("year") final Integer year) {
		return aggregator.aggregateByLocation(getTransactionByYear(year));
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/aggregate/category/{year}")
	public List<TotalByLocation> getAggregateCategory(@PathVariable("year") final Integer year) {
		return aggregator.aggregateByCategory(getTransactionByYear(year));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/list/{year}")
	public List<CreditFileModel> getTransactionByYear(@PathVariable("year") final Integer year) {

		List<CreditFileModel> dataList = dataProcessor.processFile();
		
		dataList = categorize.categorizePurchases(dataList);

		Map<Integer, List<CreditFileModel>> mappedDate = new ConcurrentHashMap<Integer, List<CreditFileModel>>();

		dataList.stream().forEach(row -> {
			if (mappedDate.containsKey(row.getDate().getYear())) {
				List<CreditFileModel> itemList = mappedDate.get(row.getDate().getYear());
				if (row.getDate() != null) {
					itemList.add(row);
					mappedDate.put(row.getDate().getYear(), itemList);
				}
			} else {
				if (row.getDate() != null) {
					List<CreditFileModel> itemList2 = new ArrayList<>();
					itemList2.add(row);
					mappedDate.put(row.getDate().getYear(), itemList2);
				}
			}
		});

		if (mappedDate.containsKey(year)) {
			return mappedDate.get(year);
		} else {
			return new ArrayList<CreditFileModel>();
		}
	}
	
	
	public void writeToFiles() {

		List<Integer> yearList = Arrays.asList(2013,2014,2015,2016,2017,2018,2019);

		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		yearList.stream().forEach(year -> {

			try {
				mapper.writeValue(
						new File("/Users/chaty/Documents/AngularWorkspace/ActivityAPP/src/assets/old-list/visa-" + year
								+ ".json"),
						getTransactionByYear(year));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}



}
