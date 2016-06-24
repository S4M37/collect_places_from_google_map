import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class WriteLineExel {
public static void writeLine(Business business,int i){

	try {
		String filename = "D:/NewExcelFile.xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("FirstSheet");

		HSSFRow row = sheet.createRow((short) i);
		row.createCell(0).setCellValue(business.name);
		row.createCell(1).setCellValue("");
		row.createCell(2).setCellValue(business.tel);
		row.createCell(3).setCellValue(business.vicinity);
		row.createCell(4).setCellValue(business.geometry.location.lat+","+business.geometry.location.lng);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workbook.write(fileOut);
		fileOut.close();
		System.out.println("Your excel file has been generated!");

	} catch (Exception ex) {
		System.out.println(ex);
	}
}
}
