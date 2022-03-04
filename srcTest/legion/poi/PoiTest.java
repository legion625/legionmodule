package legion.poi;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;

import legion.LegionTest;

public class PoiTest {
	/**
	 * Test poi-4.1.2
	 * Other libs required:
	 * commons-collections4-4.4
	 * commons-compress-1.19
	 * commons-math3-3.6.1
	 * xmlbeans-3.1.0
	 * @throws Exception
	 */
	@Test
	public void testPoi4_1_2() throws Exception {
		Log log = LogFactory.getLog(LegionTest.class);
		
		File file = new File("excel2020-11-21-16-40-43_business.xlsx");
		log.info(file);
		log.info(file.getAbsolutePath());
		assert file.exists();

		// 工作表
		Workbook workbook = WorkbookFactory.create(file);

		// 表個數。
		int numberOfSheets = workbook.getNumberOfSheets();
		
		log.info("numberOfSheets:\t " + numberOfSheets);
		
		// 遍歷表。
		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = workbook.getSheetAt(i);

			// 行數。
			int rowNumbers = sheet.getLastRowNum() + 1;

			// Excel第一行。
			Row temp = sheet.getRow(0);
			if (temp == null) {
				continue;
			}

			int cells = temp.getPhysicalNumberOfCells();
			System.out.println("cells:\t"+cells );

			// 讀資料。
//			for (int row = 0; row < rowNumbers; row++) {
//				Row r = sheet.getRow(row);
			Row r = sheet.getRow(2);
//				for (int col = 0; col < cells; col++) {
			for (int col = 0; col < 4; col++) {
					Cell c = r.getCell(col);
					System.out.print("col"+col+":\t"+(c==null?"":r.getCell(col).toString()+" "));
					System.out.println();	
				}

				// 換行。
				System.out.println("----------------------------------------------------------");
//			}
		}
		
	}
	
	@Test
	public void testWrite() {
		try {
		SXSSFWorkbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = wb.createSheet("TFIDF");
		
		
		FileOutputStream fileOut = new FileOutputStream("D:/test.xlsx");
	    wb.write(fileOut);

	    fileOut.close();  
//	    response.getOutputStream().flush();
	    } catch ( Exception ex ) {
	          ex.printStackTrace();
	    }
	}
}
