package com.chaty.credit.magic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.chaty.credit.model.CreditFileModel;
import com.chaty.credit.model.TotalByLocation;

@Component
public class AggregatePurchase {

	public List<TotalByLocation> aggregateByYear(final List<CreditFileModel> dataList) {

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

	public List<TotalByLocation> aggregateByLocation(final List<CreditFileModel> dataList) {

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
