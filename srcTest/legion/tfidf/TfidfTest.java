package legion.tfidf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.util.logging.Log;

public class TfidfTest {
	
	private static Tfidf tfidf;
	
	@BeforeClass
	public static void beforeClass()  {
		System.out.println("Start::beforeClass");
		tfidf = Tfidf.of();
		System.out.println("End::beforeClass");
	}

	
	
//	@Test
//	public void test0() {}
	
	// -------------------------------------------------------------------------------
	private List<DocCreateObj> parseDocCreateObjList(File _file){
		assert _file.exists();
		
		List<DocCreateObj> list = new ArrayList<>();
		
//		File file = new File("stopwords.xlsx");
		// 工作表
		try {
			Workbook workbook = WorkbookFactory.create(_file);
			Sheet sheet = workbook.getSheetAt(0);
			int rowCnt = sheet.getPhysicalNumberOfRows();
			System.out.println("rowCnt:\t"+rowCnt);
			for (int i = 2; i < rowCnt; i++) { // XXX
//			for (int i = 2; i < 10; i++) { // XXX
				Row row = sheet.getRow(i);
				if (row == null)
					continue;
				
				String sn = row.getCell(0).getStringCellValue();
				String title = row.getCell(1).getStringCellValue();
				String abstr = row.getCell(2).getStringCellValue();
				String claim = row.getCell(3).getStringCellValue();

				DocCreateObj docCreateObj = new DocCreateObj();
				docCreateObj.setSn(sn);
				docCreateObj.setTitle(title);
				docCreateObj.setText(abstr + claim);

				list.add(docCreateObj);
			}
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	private Set<String> parseStopWord(File file) {
		assert file.exists();
		
		/* init stopwords */
		Set<String> stopWordSet = new HashSet<>();
//		File file = new File("stopwords.xlsx");

		// 工作表
		try {
			Workbook workbook = WorkbookFactory.create(file);
			Sheet sheet = workbook.getSheetAt(0);
			int rowCnt = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rowCnt; i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;
				Cell cell = row.getCell(0);
				if (cell == null)
					continue;
				String stopword =cell.getStringCellValue();
//				System.out.println(stopword);
				stopWordSet.add(stopword);
			}
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
			return null;
		}
		
		System.out.println("stopWordSet.size():\t"+stopWordSet.size());
		return stopWordSet;
	}
	
	@Test
	public void test() {
		// parse doc
//		File docFile = new File("nlp_chatbot_base_3402.xlsx");
		File docFile = new File("D:\\nlp_chatbot_base_3402.xlsx");
		List<DocCreateObj> docCreateObjList = parseDocCreateObjList(docFile);

		System.out.println("docCreateObjList.size():\t"+ docCreateObjList.size());
		
		// parse stopword
		File stopWordfile = new File("stopwords.xlsx");
		System.out.println("stopWordfile.getAbsolutePath():\t"+stopWordfile.getAbsolutePath());
		Set<String> stopWordSet = parseStopWord(stopWordfile);
		System.out.println("stopWordSet.size():\t"+stopWordSet.size());
		tfidf.process(docCreateObjList, stopWordSet);
		
		SXSSFWorkbook wb = tfidf.getDisplayRaw();
		try {		
		FileOutputStream fileOut = new FileOutputStream("D:/TFIDF"+System.currentTimeMillis()+".xlsx");
	    wb.write(fileOut);

	    fileOut.close();  
	    } catch ( Exception ex ) {
	          ex.printStackTrace();
	    }
		
		System.out.println("test");
	}
	

}
