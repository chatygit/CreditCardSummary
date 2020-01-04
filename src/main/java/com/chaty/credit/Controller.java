package com.chaty.credit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

	@RequestMapping(method = RequestMethod.GET, path = "/year/{year}")
	public List<CreditFileModel> getTransactionByYear(@PathVariable("year") final Integer year) {

		List<CreditFileModel> dataList = dataProcessor.processFile();

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

	@RequestMapping(method = RequestMethod.GET, path = "/map")
	public List<TotalByLocation> getMapData() {

		Map<String, Double> mappedDate = new ConcurrentHashMap<String, Double>();

		dataProcessor.processFile().stream().forEach(row ->

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
		formattedMap.sort((a, b) -> a.getCreditTotal().compareTo(b.getCreditTotal()));

		return formattedMap;

	}

	@RequestMapping(method = RequestMethod.GET, path = "/aggregate/year")
	public List<TotalByLocation> getAggregateYear() {

		return aggregateByYear(dataProcessor.processFile());

	}

	@RequestMapping(method = RequestMethod.GET, path = "/aggregate/location/{year}")
	public List<TotalByLocation> getAggregateLocation(@PathVariable("year") final Integer year) {

		return aggregateByLocation(getTransactionByYear(year));

	}

	private List<TotalByLocation> aggregateByYear(final List<CreditFileModel> dataList) {

		Map<Integer, List<CreditFileModel>> mappedDate = new ConcurrentHashMap<Integer, List<CreditFileModel>>();

		dataList.stream().forEach(row -> {

			if (mappedDate.containsKey(row.getDate().getYear())) {

				List<CreditFileModel> itemList = mappedDate.get(row.getDate().getYear());
				if (row.getDate() != null && row.getCredit().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					itemList.add(row);
					mappedDate.put(row.getDate().getYear(), itemList);
				}

			} else {
				if (row.getDate() != null && row.getCredit().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					List<CreditFileModel> itemList2 = new ArrayList<>();
					itemList2.add(row);
					mappedDate.put(row.getDate().getYear(), itemList2);
				}
			}

		});

		List<TotalByLocation> totalByLocation = new ArrayList<TotalByLocation>();

		mappedDate.entrySet().stream().forEach(entry -> {
			TotalByLocation totalLoc = new TotalByLocation();
			totalLoc.setLocation(entry.getKey().toString());
			totalLoc.setPurchaseList(entry.getValue());
			Double total = entry.getValue().stream().mapToDouble(pp -> Double.parseDouble(pp.getCredit())).sum();
			totalLoc.setCreditTotal(total.intValue());
			totalByLocation.add(totalLoc);
		});
		totalByLocation.sort((a, b) -> a.getCreditTotal().compareTo(b.getCreditTotal()));

		return totalByLocation;

	}

	private List<TotalByLocation> aggregateByLocation(final List<CreditFileModel> dataList) {

		Map<String, List<CreditFileModel>> maptoLocation = new ConcurrentHashMap<String, List<CreditFileModel>>();

		dataList.stream().forEach(row ->

		{
			if (maptoLocation.containsKey(row.getLocation())) {

				List<CreditFileModel> itemList = maptoLocation.get(row.getLocation());
				if (row.getCredit().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					itemList.add(row);
					maptoLocation.put(row.getLocation(), itemList);
				}

			} else {
				if (row.getCredit().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					List<CreditFileModel> itemList2 = new ArrayList<>();
					itemList2.add(row);
					maptoLocation.put(row.getLocation(), itemList2);
				}
			}

		});

		List<TotalByLocation> totalByLocation = new ArrayList<TotalByLocation>();

		maptoLocation.entrySet().stream().forEach(entry -> {
			TotalByLocation totalLoc = new TotalByLocation();
			totalLoc.setLocation(entry.getKey());
			totalLoc.setPurchaseList(entry.getValue());
			Double total = entry.getValue().stream().mapToDouble(pp -> Double.parseDouble(pp.getCredit())).sum();
			totalLoc.setCreditTotal(total.intValue());
			totalByLocation.add(totalLoc);
		});
		totalByLocation.sort((a, b) -> a.getCreditTotal().compareTo(b.getCreditTotal()));

		return totalByLocation;

	}

}
