package api.services;

import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import api.parser.gmaps.models.BusinessGMaps;
import api.parser.heremaps.models.BusinessHere;
import api.parser.interfaces.PublishDataInterface;

public class PublishDataInExcel implements PublishDataInterface {
	HSSFWorkbook workbook;
	HSSFSheet sheet;
	String filename;

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public PublishDataInExcel setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
		return this;
	}

	public HSSFSheet getSheet() {
		return sheet;
	}

	public PublishDataInExcel setSheet(HSSFSheet sheet) {
		this.sheet = sheet;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public PublishDataInExcel setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	@Override
	public void publishFromGmaps(BusinessGMaps business, String region,
			int lineNumber) {
		try {
			HSSFRow row = sheet.createRow((short) lineNumber);
			row.createCell(0).setCellValue(region);
			row.createCell(1).setCellValue(business.name);
			String subCategories = "";
			for (int j = 0; j < business.types.length; j++) {
				subCategories += business.types[j] + ", ";
			}
			row.createCell(2).setCellValue(subCategories);
			row.createCell(3).setCellValue(business.vicinity);
			row.createCell(4).setCellValue(
					business.geometry.location.lat + ","
							+ business.geometry.location.lng);
			FileOutputStream fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();
			System.out.println("Your excel file has been modified!");

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	@Override
	public void publishFromHere(BusinessHere businessHere, String region,
			int lineNumber) {
		try {
			HSSFRow row = sheet.createRow(lineNumber);
			if (businessHere.category != null)
				row.createCell(0).setCellValue(businessHere.category.title);
			row.createCell(1).setCellValue(businessHere.title);
			String tags = "";
			if (businessHere.tags != null)
				for (int j = 0; j < businessHere.tags.length; j++) {
					tags += businessHere.tags[j].title + ", ";
				}
			if (businessHere.vicinity != null)
				row.createCell(2).setCellValue(tags);
			row.createCell(3).setCellValue(
					businessHere.vicinity.replaceAll("<br/>", " "));
			row.createCell(4).setCellValue(
					businessHere.position[0] + "," + businessHere.position[1]);
			FileOutputStream fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();
			System.out.println("Your excel file has been modified!");

		} catch (Exception ex) {
			System.out.println(ex);
		}

	}

}
