package legion.kmeans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.util.StreamReaderDelegate;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.binary.XSSFBCommentsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.amazonaws.services.pinpointemail.model.PinpointDestination;
import com.github.pjfanning.xlsx.StreamingReader;
import com.github.pjfanning.xlsx.StreamingReader.Builder;

import legion.kmeans.Kmeans;
import legion.kmeans.Kmeans.Cluster;
import legion.kmeans.Kmeans.Point;
import legion.util.NumberFormatUtil;

public class KmeansTest {
//
//	private List<Point> loadDatas(Kmeans _kmeans, File _file) {
//		if (_file == null)
//			return null;
//		
//		
//		
//		return pointList;
//	}
	
	
	@Test
	public void testKmeans() {
//		File file = new File("D:\\TFIDF_raw_cnt_over_100_tfidf_only.xlsx"); // XXX
//		File file = new File("D:\\TFIDF_raw_cnt_over_200_time_in_doc_less_200.xlsx"); // XXX
		File file = new File("D:\\kmeans_test.xlsx");
//		File file = new File("D:\\nlp_chatbot_base_3402.xlsx");
//		File file = new File("D:\\kmeans_demo_data.xlsx");
		String fileName = file.getName().replace(".xlsx", "");
		System.out.println("fileName:\t" + fileName);
		
		
		Kmeans kmeans = new Kmeans();
		
		/* 讀檔案 */
		Builder b = StreamingReader.builder();
		Workbook wb = b.open(file);
		Sheet sheet =wb.getSheet("TFIDF");
		
		int dimension = 0; // 文字的維度
		
		String[] words = null ;
		List<Point> pointList = new ArrayList<>();
		
		for(Row row:sheet) {
			// words
			if(row.getRowNum() == 0) {
				dimension = row.getPhysicalNumberOfCells()-1;
				 words = new String[dimension];
				for(Cell cell:row) {
					int colIdx = cell.getColumnIndex();
					if (colIdx == 0)
						continue;
					words[colIdx - 1] = cell.getStringCellValue();
				}
			}
			// vectors
			else {
				int idx = row.getRowNum();
				double[] v = new double[dimension];
				for (Cell cell : row) {
					int colIdx = cell.getColumnIndex();
					if (colIdx == 0)
						continue;
					v[colIdx - 1] = cell.getNumericCellValue();
				}
				Point p = kmeans.new Point(idx-1, v);
				pointList.add(p);
//				if(row.getRowNum() == 11) {
//					System.out.println("row.getPhysicalNumberOfCells():\t"+row.getPhysicalNumberOfCells());
//					break;
//				}
			}
//			System.out.println(" ");
//
		}

		
	
//		for(Point p: pointList) {
//			System.out.println("point "+p.getIdx());
//			for(double d: p.getV()) {
//				System.out.print(NumberFormatUtil.getDecimalString(d, 4)+"\t");
//			}
//			System.out.println(" ");
//		}
		System.out.println("words.length:\t"+words.length);
		System.out.println("pointList.size():\t"+pointList.size());
		
		
//		List<Point> pointList = loadDatas(kmeans, file);
		
		/* 分群 */
		int k = 3; // XXX
		kmeans.init(pointList, k);
		
		List<Cluster> clusterList = kmeans.getClusterList();
		for (Cluster c : clusterList) {
			System.out.println("Cluster " + c.getIdx()+"\t"+c.getPointList().size());
			for (Point p : c.getPointList()) {
				System.out.print(p.getIdx() + "\t");
			}
			System.out.println();
		}
		
		/* 計算各群topwords */
		sheet = wb.getSheet("WORDCNT");
		int[][] wordCntArray = new int[pointList.size()][dimension];
		for (Row row : sheet) {
			if (row.getRowNum() == 0)
				continue;
			for (Cell cell : row) {
				int colIdx = cell.getColumnIndex();
				if (colIdx == 0)
					continue;
				wordCntArray[row.getRowNum() - 1][colIdx - 1] = (int) cell.getNumericCellValue();
			}
		}
		
		/* display */
		SXSSFWorkbook wbResult = new SXSSFWorkbook();
		// 
		SXSSFSheet sheet1 = wbResult.createSheet("CLUSTER");
		int rowIdx = 0;
		int colIdx;
		{
			Row row = sheet1.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue("Cluster Index");
			row.createCell(colIdx++).setCellValue("Count");
			row.createCell(colIdx++).setCellValue("Point List");
		}
		
		for(Cluster c: clusterList) {
			Row row = sheet1.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue("Cluster " + c.getIdx());
			row.createCell(colIdx++).setCellValue(c.getPointList().size());
			row.createCell(colIdx++).setCellValue(c.getPointList().stream().map(p->p.getIdx()+"").collect(Collectors.joining(" ")));
			
//			for (Point p : c.getPointList()) {
////				row.createCell(colIdx++).setCellValue("Doc " + p.getIdx());
//				row.createCell(colIdx++).setCellValue( p.getIdx());
//			}
		}
		{
			Row row;
			// RS
			row = sheet1.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue("RS");
			row.createCell(colIdx++).setCellValue(kmeans.getRs());
			// RMSSTD
			row = sheet1.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue("RMSSTD");
			row.createCell(colIdx++).setCellValue(kmeans.getRmsstd());
			// RS/RMSSTD
			row = sheet1.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue("RS/RMSSTD");
			row.createCell(colIdx++).setCellValue(kmeans.getRsRmsstdRatio());
			
			/* Silhouette */
			// A
			row = sheet1.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue("Silhouette A");
			row.createCell(colIdx++).setCellValue(kmeans.getA());
			// B
			row = sheet1.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue("Silhouette B");
			row.createCell(colIdx++).setCellValue(kmeans.getB());
			// S
			row = sheet1.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue("Silhouette S");
			row.createCell(colIdx++).setCellValue(kmeans.getS());
		}
		
		
//		// cluster words cnt
//		int[][] clusterWordCntArray = new int[k][dimension];
//		for (Cluster c : clusterList) {
//			for (int j = 0; j < dimension; j++) {
//				final int thisJ = j;
//				clusterWordCntArray[c.getIdx()][j] = c.getPointList().parallelStream()
//						.mapToInt(p -> wordCntArray[p.getIdx()][thisJ]).sum();
//			}
//		}

//		int[][] clusterTop10WordIdxArray = new int[k][10];
//		 class IdxCnt{
//			int idx, cnt;
//
//			private IdxCnt(int idx, int cnt) {
//				this.idx = idx;
//				this.cnt = cnt;
//			}
//			public int getIdx() {
//				return idx;
//			}
//
//			public int getCnt() {
//				return cnt;
//			}
//			
//		}
//		// 取出top10的index
//		for (Cluster c : clusterList) {
//			List<IdxCnt> icList = new ArrayList<>();
//			for (int j = 0; j < dimension; j++) {
//				icList.add(new IdxCnt(j, clusterWordCntArray[c.getIdx()][j]));
//			}
//			icList = icList.stream().sorted(Comparator.comparingInt(IdxCnt::getCnt).reversed())
//					.collect(Collectors.toList());
//			for (int x = 0; x < 10; x++)
//				clusterTop10WordIdxArray[c.getIdx()][x] = icList.get(x).getIdx();
//		}
		
//		
//		SXSSFSheet topwordsSheet = wbResult.createSheet("TOPWORDS");
//		{
//			rowIdx = 0;
//			// title
//			Row row = topwordsSheet.createRow(rowIdx++);
//			colIdx = 0;
//			row.createCell(colIdx++).setCellValue("RANK");
//			for (int i = 0; i < k; i++) {
//				row.createCell(colIdx++).setCellValue("Cluster " + i + " WORD");
//				row.createCell(colIdx++).setCellValue("Cluster " + i + " CNT");
//			}
//			//
//			for (int x = 0; x < 10; x++) {
//				row = topwordsSheet.createRow(rowIdx++);
//				colIdx = 0;
//				row.createCell(colIdx++).setCellValue(x + 1);
//				for(int i=0;i<k;i++) {
//					int j = clusterTop10WordIdxArray[i][x];
//					row.createCell(colIdx++).setCellValue(words[j]);
//					row.createCell(colIdx++).setCellValue(clusterWordCntArray[i][j]);
//				}
//			}
//		}
//		
		
//		SXSSFSheet clusterWordCntSheet = wbResult.createSheet("ALLWORDCNT");
//		{
//			rowIdx = 0;
//			Row row = clusterWordCntSheet.createRow(rowIdx++);
//			colIdx = 0;
//			row.createCell(colIdx++).setCellValue("Word");
//			for (int i = 0; i < k; i++)
//				row.createCell(colIdx++).setCellValue("Cluster " + i);
//			//
//			for (int j = 0; j < dimension; j++) {
//				row = clusterWordCntSheet.createRow(rowIdx++);
//				colIdx = 0;
//				row.createCell(colIdx++).setCellValue(words[j]);
//				for (int i = 0; i < k; i++)
//					row.createCell(colIdx++).setCellValue(clusterWordCntArray[i][j]);
//			}
//		}
		
		
		try {
			FileOutputStream fosResult = new FileOutputStream(
					"D:/" + fileName + "_result_" + System.currentTimeMillis() + ".xlsx");
			wbResult.write(fosResult);
			fosResult.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
}
