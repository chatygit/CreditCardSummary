package com.chaty.credit.magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.chaty.credit.model.CreditFileModel;
import com.chaty.credit.model.TotalByLocation;

/**
 * Does some basic stream filtering and aggregating for easier display of data
 * by client facing applications.
 *
 * @author chaty
 */
@Component
@Slf4j
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

        final Map<String, List<CreditFileModel>> sortedMap = new HashMap<String, List<CreditFileModel>>();

        mappedDate.entrySet().stream().forEach(entry -> {
            List<CreditFileModel> list = entry.getValue();
            list.sort((a, b) -> a.getDate().compareTo(b.getDate()));
            sortedMap.put(entry.getKey(), list);
        });

        return aggregateTotals(sortedMap);

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

        final Map<String, List<CreditFileModel>> sortedMap = new HashMap<String, List<CreditFileModel>>();

        maptoLocation.entrySet().stream().forEach(entry -> {
            List<CreditFileModel> list = entry.getValue();
            list.sort((a, b) -> a.getDate().compareTo(b.getDate()));
            sortedMap.put(entry.getKey(), list);
        });

        return aggregateTotals(sortedMap);

    }

    /**
     * Aggregates given list by Month.
     *
     * @param dataList
     * @return List of {@link TotalByLocation}
     */
    public List<TotalByLocation> aggregateByMonth(final List<CreditFileModel> dataList) {

        final Map<String, List<CreditFileModel>> mappedDate = new ConcurrentHashMap<String, List<CreditFileModel>>();

        dataList.stream().forEach(row -> {

            if (mappedDate.containsKey(Integer.toString(row.getDate().getMonth().getValue()))) {

                List<CreditFileModel> itemList = mappedDate.get(Integer.toString(row.getDate().getMonth().getValue()));
                if (row.getDate() != null) {
                    itemList.add(row);
                    mappedDate.put(Integer.toString(row.getDate().getMonth().getValue()), itemList);
                }

            } else {
                if (row.getDate() != null) {
                    List<CreditFileModel> itemList2 = new ArrayList<>();
                    itemList2.add(row);
                    mappedDate.put(Integer.toString(row.getDate().getMonth().getValue()), itemList2);
                }
            }

        });

        TreeMap<String, List<CreditFileModel>> sortedMap = new TreeMap<>();

        mappedDate.entrySet().stream().forEach(entry -> {
            List<CreditFileModel> list = entry.getValue();
            list.sort((a, b) -> a.getDate().compareTo(b.getDate()));
            sortedMap.put(entry.getKey(), list);
        });

        return aggregateTotals(sortedMap);
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
                log.info(pp.toString());
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

    /**
     * Aggregates given list by Category.
     *
     * @param dataList
     * @return List of {@link TotalByLocation}
     */
    public List<TotalByLocation> aggregateByCategory(final List<CreditFileModel> dataList) {

        final Map<String, List<CreditFileModel>> maptoCategory = new ConcurrentHashMap<String, List<CreditFileModel>>();

        dataList.stream().forEach(row ->

        {
            if (maptoCategory.containsKey(row.getCategory())) {
                List<CreditFileModel> itemList = maptoCategory.get(row.getCategory());
                itemList.add(row);
                maptoCategory.put(row.getCategory(), itemList);

            } else {
                List<CreditFileModel> itemList2 = new ArrayList<>();
                itemList2.add(row);
                maptoCategory.put(row.getCategory(), itemList2);
            }

        });

        final Map<String, List<CreditFileModel>> sortedMap = new HashMap<String, List<CreditFileModel>>();

        maptoCategory.entrySet().stream().forEach(entry -> {
            List<CreditFileModel> list = entry.getValue();
            list.sort((a, b) -> a.getDate().compareTo(b.getDate()));
            sortedMap.put(entry.getKey(), list);
        });

        return aggregateTotals(sortedMap);

    }

}
