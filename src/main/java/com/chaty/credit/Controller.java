package com.chaty.credit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.credit.model.CreditFileModel;
import com.chaty.credit.model.TotalByLocation;

@RestController
@RequestMapping("/credit/api")
public class Controller {

	@Autowired
	private DataProcessor dataProcessor;

	@GetMapping
	public List<CreditFileModel> test() {

		List<CreditFileModel> dataList = dataProcessor.processFile();

		return dataList;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/map")
	public List<TotalByLocation> getMapData() {

		Map<String, Double> mappedDate = new ConcurrentHashMap<String, Double>();

		test().stream().forEach(row ->

		{
			if (mappedDate.containsKey(row.getLocation())) {

				Double number = mappedDate.get(row.getLocation());
				if (row.getCredit().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					mappedDate.put(row.getLocation(), number + Double.parseDouble(row.getCredit()));
				}

			} else {
				if (row.getCredit().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					mappedDate.put(row.getLocation(), Double.parseDouble(row.getCredit()));
				}
			}

		});

		double totalevent = mappedDate.values().stream().mapToDouble(Double::doubleValue).sum();

		mappedDate.put("TOTAL", totalevent);

		List<TotalByLocation> formattedMap = new ArrayList<TotalByLocation>();

		mappedDate.entrySet().stream().forEach(entry -> {
			TotalByLocation totalLoc = new TotalByLocation();
			totalLoc.setLocation(entry.getKey());
			totalLoc.setCreditTotal(entry.getValue().intValue());
			formattedMap.add(totalLoc);
		});
		formattedMap.sort((a,b)-> a.getCreditTotal().compareTo(b.getCreditTotal()));

		return formattedMap;

	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/aggregate")
	public List<TotalByLocation> getMapDataAggregate() {

		Map<String, List<CreditFileModel>> mappedDate = new ConcurrentHashMap<String, List<CreditFileModel>>();

		test().stream().forEach(row ->

		{
			if (mappedDate.containsKey(row.getLocation())) {

				List<CreditFileModel> itemList = mappedDate.get(row.getLocation());
				if (row.getCredit().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					itemList.add(row);
					mappedDate.put(row.getLocation(), itemList);
				}

			} else {
				if (row.getCredit().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					List<CreditFileModel> itemList2 = new ArrayList<>();
					itemList2.add(row);
					mappedDate.put(row.getLocation(), itemList2);
				}
			}

		});

		List<TotalByLocation> formattedMap = new ArrayList<TotalByLocation>();

		mappedDate.entrySet().stream().forEach(entry -> {
			TotalByLocation totalLoc = new TotalByLocation();
			totalLoc.setLocation(entry.getKey());
			totalLoc.setPurchaseList(entry.getValue());
			Double total = entry.getValue().stream().mapToDouble(pp -> Double.parseDouble(pp.getCredit())).sum();
			totalLoc.setCreditTotal(total.intValue());
			formattedMap.add(totalLoc);
		});
		formattedMap.sort((a,b)-> a.getCreditTotal().compareTo(b.getCreditTotal()));

		return formattedMap;

	}

}
