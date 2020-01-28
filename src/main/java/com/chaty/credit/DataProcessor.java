package com.chaty.credit;

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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.chaty.credit.model.ChatModel;
import com.chaty.credit.model.CreditFileModel;

/**
 * 
 * Loads different sources of data into a generic {@link CreditFileModel}
 * objects.
 * 
 * Currently has support for CSV, with Date("MM/dd/yyyy"),location,debit,credit
 * as columns.
 * 
 * Since Current Major Canadian Banks mostly have PDF, you can copy paste the
 * content onto text files in the following format.
 * 
 * 
 * Debit : MAR 23 MAR 25 THE BEER STORE #2369 NORTH YORK $112.85 Credit: MAR 25
 * MAR 26 PAYMENT - THANK YOU -$300.00
 * 
 * ```
 * 
 * @author chaty
 *
 */
@Component
public class DataProcessor {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	String line = "";
	String cvsSplitBy = ",";

	public List<CreditFileModel> processFile() {

		List<CreditFileModel> yearList = new ArrayList<CreditFileModel>();

		try (Stream<Path> paths = Files.walk(Paths.get("/Users/chaty/Documents/TaxDocs/Test"))) {
			paths.filter(Files::isRegularFile).forEach(file -> {

				try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {

					while ((line = br.readLine()) != null) {

						if (line.contains("BOOSTER JUICE, STORE")) {

							String[] country = line.split(cvsSplitBy);

							CreditFileModel model = new CreditFileModel();
							model.setDate(LocalDate.parse(country[0].toString(), formatter));
							model.setLocation(country[1].replaceAll("\"", ""));
							model.setDebit(country[3].replaceAll(",", ""));
							model.setCredit(country[4].replaceAll(",", ""));
							yearList.add(model);
						} else {

							String[] country = line.split(cvsSplitBy);

							CreditFileModel model = new CreditFileModel();
							model.setDate(LocalDate.parse(country[0].toString(), formatter));
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

		yearList.addAll(processTextile());

		return yearList;

	}

	public List<CreditFileModel> processTextile() {

		List<CreditFileModel> yearList = new ArrayList<CreditFileModel>();

		Map<String, Integer> monthMap = new HashMap<String, Integer>();
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

		try (Stream<Path> paths = Files.walk(Paths.get("/Users/chaty/Documents/TaxDocs/TextVisa"))) {
			paths.filter(Files::isRegularFile).forEach(file -> {

				try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {

					while ((line = br.readLine()) != null) {

						String val = line.substring(line.lastIndexOf("$") + 1);
						boolean isCredit = line.substring(line.lastIndexOf("$") - 1).contains("-$");
						String month = line.substring(0, 3);
						String day = line.substring(4, 6).replace(" ", "");

						Integer location = StringUtils.ordinalIndexOf(line, " ", 4);
						Integer year = Integer.parseInt(
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

	public List<ChatModel> processWhatsAPP() {

		List<ChatModel> chatList = new ArrayList<ChatModel>();
		String siri = "+91 95130 99158‬:";
		String chaty = "Chaty:";
		
		DateTimeFormatter formatterWhatsapp = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		try (BufferedReader br = new BufferedReader(new FileReader("/Users/chaty/Documents/_chat.txt"))) {

			while ((line = br.readLine()) != null) {
				if(line.startsWith("[")) {
					
					String dateTime = line.substring(0,line.lastIndexOf("M]") + 1);
					String[] splitDate = dateTime.replaceAll("\\[", "").split(",");
					String date = splitDate[0];
					String time = splitDate[1];
					
					LocalDate localDate = LocalDate.parse(date, formatterWhatsapp);
					
					ChatModel chat = new ChatModel();
					chat.setDate(localDate);
					
					if(line.contains(chaty)) {
						String message = line.substring(line.lastIndexOf(chaty)+chaty.length()+1);
						chat.setSentBy("Chaty");
						chat.setMessage(message);
					}else {
						String message = line.substring(line.lastIndexOf(siri)+siri.length()+1);
						chat.setSentBy("Siri");
						chat.setMessage(message);
					}
					
					
					chatList.add(chat);
					
				}else {
					System.out.println("Long One");
				}
			}

		} catch (IOException e) {
			System.out.println("EXCE");
			e.printStackTrace();
		}
		
		//System.out.println(chatList);

		return chatList;

	}

}
