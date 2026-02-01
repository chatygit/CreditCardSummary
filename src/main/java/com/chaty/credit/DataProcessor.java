package com.chaty.credit;

import com.chaty.credit.model.CreditFileModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Loads different sources of data into a generic {@link CreditFileModel}
 * objects.
 * <p>
 * Currently has support for CSV, with Date("MM/dd/yyyy"),location,debit,credit
 * as columns.
 * <p>
 * Since Current Major Canadian Banks mostly have PDF, you can copy paste the
 * content onto text files in the following format.
 * <p>
 * <p>
 * Debit : MAR 23 MAR 25 THE BEER STORE #2369 NORTH YORK $112.85 Credit: MAR 25
 * MAR 26 PAYMENT - THANK YOU -$300.00
 * <p>
 * ```
 *
 * @author chaty
 */
@Service
@Slf4j
public class DataProcessor {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    String line = "";
    String cvsSplitBy = ",";

    public List<CreditFileModel> processFile() {

        List<CreditFileModel> yearList = new ArrayList<CreditFileModel>();

        try (Stream<Path> paths = Files.walk(Paths.get("/Users/babachaitanyakothapalli/Documents/RAHMAN/csv"))) {
            paths.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith("csv")).forEach(file -> {
                log.info(file.getFileName().toString());
                try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {

                    while ((line = br.readLine()) != null) {

                        if (line.contains("BOOSTER JUICE, STORE")) {

                            String[] country = line.split(cvsSplitBy);

                            CreditFileModel model = new CreditFileModel();
                            model.setDate(LocalDate.parse(country[0], formatter));
                            model.setLocation(country[1].replaceAll("\"", ""));
                            model.setDebit(country[3].replaceAll(",", ""));
                            model.setCredit(country[4].replaceAll(",", ""));
                            yearList.add(model);
                        } else {

                            String[] country = line.split(cvsSplitBy);

                            CreditFileModel model = new CreditFileModel();
                            model.setDate(LocalDate.parse(country[0], formatter));
                            model.setLocation(country[1]);
                            model.setDebit(country[2].replaceAll(",", ""));
                            model.setCredit(country[3].replaceAll(",", ""));
                            yearList.add(model);

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        yearList.addAll(processTextfileDec2025Format());

        return yearList;

    }

    public List<CreditFileModel> processTextile() {

        List<CreditFileModel> yearList = new ArrayList<>();

        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("JAN", 1);
        monthMap.put("FEB", 2);
        monthMap.put("MAR", 3);
        monthMap.put("APR", 4);
        monthMap.put("MAY", 5);
        monthMap.put("JUN", 6);
        monthMap.put("JUL", 7);
        monthMap.put("AUG", 8);
        monthMap.put("SEP", 9);
        monthMap.put("OCT", 10);
        monthMap.put("NOV", 11);
        monthMap.put("DEC", 12);

        try (Stream<Path> paths = Files.walk(Paths.get("/Users/babachaitanyakothapalli/Documents/RAHMAN/text"))) {
            paths.filter(Files::isRegularFile).forEach(file -> {

                try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {

                   // log.info(file.getFileName().toString());
                    while ((line = br.readLine()) != null) {
                        //log.info(line);

                        String val = line.substring(line.lastIndexOf("$") + 1);
                        boolean isCredit = line.substring(line.lastIndexOf("$") - 1).contains("-$");
                        String month = line.substring(0, 3);
                        String day = line.substring(4, 6).replace(" ", "");

                        int location = StringUtils.ordinalIndexOf(line, " ", 4);
                        int year = Integer.parseInt(
                                file.getFileName().toString().substring(0, file.getFileName().toString().length() - 4));

                        LocalDate date = LocalDate.now().withYear(year).withMonth(monthMap.get(month))
                                .withDayOfMonth(Integer.parseInt(day));

                        CreditFileModel model = new CreditFileModel();
                        if (isCredit) {
                            model.setCredit(val.replaceAll(",", ""));
                        } else {
                            model.setDebit(val.replaceAll(",", ""));
                        }

                        model.setDate(date);
                        model.setLocation(line.substring(location, line.lastIndexOf("$")));
                        yearList.add(model);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return yearList;
    }

    public List<CreditFileModel> processTextfileDec2025Format() {

        List<CreditFileModel> list = new ArrayList<>();

        Map<String, Integer> monthMap = Map.ofEntries(
                Map.entry("JAN", 1), Map.entry("FEB", 2), Map.entry("MAR", 3), Map.entry("APR", 4),
                Map.entry("MAY", 5), Map.entry("JUN", 6), Map.entry("JUL", 7), Map.entry("AUG", 8),
                Map.entry("SEP", 9), Map.entry("OCT", 10), Map.entry("NOV", 11), Map.entry("DEC", 12)
        );

        // Matches lines like "NOV 03" (your new file format)
        Pattern dateLine = Pattern.compile("^[A-Z]{3}\\s\\d{2}$");

        // Matches money lines like "$19.18" or "-$1,000.00"
        Pattern moneyLine = Pattern.compile("^-?\\$[0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]{2})$");

        // Reference line in your sample looks like long digits (e.g., "74703415308101709581599")
        Pattern longDigits = Pattern.compile("^\\d{10,}$");

        try (Stream<Path> paths = Files.walk(Paths.get("/Users/babachaitanyakothapalli/Documents/RAHMAN/text"))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt"))
                    .forEach(file -> {

                        int year = extractYearFromFilename(file.getFileName().toString());

                        try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {

                            // Read all non-empty lines (new format is block-based)
                            List<String> lines = new ArrayList<>();
                            String line;
                            while ((line = br.readLine()) != null) {
                                String trimmed = line.trim();
                                if (!trimmed.isEmpty()) {
                                    lines.add(trimmed);
                                }
                            }

                            for (int i = 0; i < lines.size(); i++) {

                                String l1 = lines.get(i);

                                // Start of a transaction block
                                if (!dateLine.matcher(l1).matches()) continue;

                                // date2 should be the next line (posting date)
                                if (i + 1 >= lines.size() || !dateLine.matcher(lines.get(i + 1)).matches()) {
                                    continue; // malformed block; skip safely
                                }

                                String transDateStr = l1;          // e.g., "NOV 03"
                                // String postDateStr = lines.get(i + 1); // e.g., "NOV 05" (not used, but available)
                                i += 2;

                                if (i >= lines.size()) break;

                                // Description line
                                String description = lines.get(i);
                                i++;

                                // Optional: reference number line (digits) – ignore
                                if (i < lines.size() && longDigits.matcher(lines.get(i)).matches()) {
                                    i++;
                                }

                                // Optional: "Foreign Currency..." line – attach to description (useful context)
                                if (i < lines.size() && lines.get(i).startsWith("Foreign Currency")) {
                                    description = description + " (" + lines.get(i) + ")";
                                    i++;
                                }

                                // Amount line (required)
                                if (i >= lines.size() || !moneyLine.matcher(lines.get(i)).matches()) {
                                    // if not found, rewind one step so loop continues robustly
                                    continue;
                                }

                                String amountRaw = lines.get(i); // e.g., "$19.18" or "-$55.26"
                                boolean isCredit = amountRaw.startsWith("-$");

                                String numeric = amountRaw
                                        .replace("$", "")
                                        .replace(",", "")
                                        .replace("-", ""); // keep as positive number for storing

                                LocalDate date = parseMonthDayWithYear(transDateStr, year, monthMap);
                                if (date == null) continue;

                                CreditFileModel model = new CreditFileModel();
                                model.setDate(date);
                                model.setLocation(description);

                                if (isCredit) {
                                    model.setCredit(numeric);
                                } else {
                                    model.setDebit(numeric);
                                }

                                list.add(model);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private int extractYearFromFilename(String filename) {
        // Works for "dec-2025.txt" or "DEC-2025.TXT" etc.
        // Also falls back to any 4-digit year present in the name.
        String lower = filename.toLowerCase(Locale.ROOT);
        // common pattern: "dec-2025.txt"
        int dash = lower.lastIndexOf('-');
        int dot = lower.lastIndexOf('.');
        if (dash >= 0 && dot > dash + 1) {
            String maybeYear = lower.substring(dash + 1, dot);
            if (maybeYear.matches("\\d{4}")) return Integer.parseInt(maybeYear);
        }
        // fallback: find any 4 digit year
        for (int i = 0; i <= filename.length() - 4; i++) {
            String sub = filename.substring(i, i + 4);
            if (sub.matches("\\d{4}")) return Integer.parseInt(sub);
        }
        // last resort: current year
        return LocalDate.now().getYear();
    }

    private LocalDate parseMonthDayWithYear(String monDay, int year, Map<String, Integer> monthMap) {
        // monDay like "NOV 03"
        if (monDay == null || monDay.length() < 6) return null;
        String mon = monDay.substring(0, 3).toUpperCase(Locale.ROOT);
        String dayStr = monDay.substring(4, 6);
        Integer month = monthMap.get(mon);
        if (month == null) return null;

        int day = Integer.parseInt(dayStr);
        return LocalDate.of(year, month, day);
    }

}
