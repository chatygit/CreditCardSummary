package com.chaty.credit.magic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.chaty.credit.model.CreditFileModel;
import com.chaty.credit.model.TotalByLocation;




/**
 * 
 * Does some basic stream filtering and aggregating for easier display of data
 * by client facing applications.
 * 
 * @author chaty
 *
 */
@Component
public class AggregatePurchase {

	/**
	 * Aggregates given list by year.
	 * 
	 * @param dataList
	 * @return List of {@link TotalByLocation}
	 */
	public List<TotalByLocation> aggregateByYear(final List<CreditFileModel> dataList) {

		final Map<String, List<CreditFileModel>> mappedDate = new ConcurrentHashMap<String, List<CreditFileModel>>();

		dataList.stream().forEach(row -> {

			if (mappedDate.containsKey(Integer.toString(row.getDate().getYear()))) {

				List<CreditFileModel> itemList = mappedDate.get(Integer.toString(row.getDate().getYear()));
				if (row.getDate() != null) {
					itemList.add(row);
					mappedDate.put(Integer.toString(row.getDate().getYear()), itemList);
				}

			} else {
				if (row.getDate() != null) {
					List<CreditFileModel> itemList2 = new ArrayList<>();
					itemList2.add(row);
					mappedDate.put(Integer.toString(row.getDate().getYear()), itemList2);
				}
			}

		});

		return aggregateTotals(mappedDate);

	}

	/**
	 * Aggregates given list by Location.
	 * 
	 * @param dataList
	 * @return List of {@link TotalByLocation}
	 */
	public List<TotalByLocation> aggregateByLocation(final List<CreditFileModel> dataList) {

		final Map<String, List<CreditFileModel>> maptoLocation = new ConcurrentHashMap<String, List<CreditFileModel>>();

		dataList.stream().forEach(row ->

		{
			if (maptoLocation.containsKey(row.getLocation())) {
				List<CreditFileModel> itemList = maptoLocation.get(row.getLocation());
				itemList.add(row);
				maptoLocation.put(row.getLocation(), itemList);

			} else {
				List<CreditFileModel> itemList2 = new ArrayList<>();
				itemList2.add(row);
				maptoLocation.put(row.getLocation(), itemList2);
			}

		});

		return aggregateTotals(maptoLocation);

	}

	/**
	 * Sums all the Credit and Debt values.
	 * 
	 * @param maptoLocation
	 * @return List of {@link TotalByLocation}
	 */
	private List<TotalByLocation> aggregateTotals(final Map<String, List<CreditFileModel>> maptoLocation) {

		List<TotalByLocation> totalByLocation = new ArrayList<TotalByLocation>();

		maptoLocation.entrySet().stream().forEach(entry -> {
			TotalByLocation totalLoc = new TotalByLocation();
			totalLoc.setLocation(entry.getKey());
			totalLoc.setPurchaseList(entry.getValue());
			Double totalCredit = entry.getValue().stream().mapToDouble(pp -> {
				if (pp.getCredit() != null && !pp.getCredit().isEmpty()) {
					return Double.parseDouble(pp.getCredit());
				} else {
					return new Double(0.00);
				}

			}).sum();

			Double totalDebit = entry.getValue().stream().mapToDouble(pp -> {
				if (pp.getDebit() != null && !pp.getDebit().isEmpty()) {
					return Double.parseDouble(pp.getDebit());
				} else {
					return new Double(0.00);
				}

			}).sum();
			totalLoc.setCreditTotal(totalCredit.intValue());
			totalLoc.setDebitTotal(totalDebit.intValue());
			totalByLocation.add(totalLoc);
		});
		totalByLocation.sort((a, b) -> a.getCreditTotal().compareTo(b.getCreditTotal()));

		return totalByLocation;

	}

}
