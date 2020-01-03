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
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.chaty.credit.model.CreditFileModel;

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
							model.setLocation(country[1]);
							model.setCredit(country[3]);
							model.setDebit(country[4]);
							yearList.add(model);
						} else {
							
							String[] country = line.split(cvsSplitBy);

							CreditFileModel model = new CreditFileModel();
							model.setDate(LocalDate.parse(country[0].toString(), formatter));
							model.setLocation(country[1]);
							model.setCredit(country[2]);
							model.setDebit(country[3]);
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

		return yearList;

	}

}
