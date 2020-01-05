package com.chaty.credit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.fit.pdfdom.PDFDomTree;
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
							model.setLocation(country[1].replaceAll("\"", ""));
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
		
		yearList.addAll(processTextile());

		return yearList;

	}

	public void processPDFFile() {

		try (PDDocument document = PDDocument.load(
				new File("/Users/chaty/Documents/TaxDocs/OLD-VSA/2013/TD_REWARDS_VISA_CARD_5035_Apr_10-2013.pdf"))) {

			document.getClass();

			Writer output = new PrintWriter("/Users/chaty/Downloads/pdf.json");
			new PDFDomTree().writeText(document, output);

			output.close();

			if (!document.isEncrypted()) {

				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);

				PDFTextStripper tStripper = new PDFTextStripper();

				String pdfFileInText = tStripper.getText(document);
				System.out.println("Text:" + pdfFileInText);

				// split by whitespace
				String lines[] = pdfFileInText.split("\\r?\\n");
				for (String line : lines) {
					System.out.println(line);
				}

			} else {
				System.out.println("ENCRYPTED");

			}

		} catch (IOException | ParserConfigurationException e) {
			System.out.println(e);
		}

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
						String month = line.substring(0, 3);
						String day = line.substring(4, 5);

						Integer location = StringUtils.ordinalIndexOf(line, " ", 5);
						Integer year = Integer.parseInt(
								file.getFileName().toString().substring(0, file.getFileName().toString().length() - 4));

						LocalDate date = LocalDate.now().withYear(year).withMonth(monthMap.get(month))
								.withDayOfMonth(Integer.parseInt(day));

						CreditFileModel model = new CreditFileModel();
						model.setCredit(val);
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

}
