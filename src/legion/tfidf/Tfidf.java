package legion.tfidf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.poi.ddf.EscherColorRef.SysIndexSource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.JSONArray;

import com.amazonaws.services.comprehend.model.transform.SyntaxTokenJsonUnmarshaller;

import legion.nlp.Nlp;
import legion.tfidf.Doc.DocWordCnt;
import legion.util.NumberFormatUtil;

public class Tfidf {

	// -------------------------------------------------------------------------------
//	private Set<String> stopWordSet;
	private Nlp nlp; 

	/* data */
	private List<Doc> docList;
	private int d; // 總文件數量
	private List<TfidfWordCnt> twcList;
	
	// -------------------------------------------------------------------------------
//	private Tfidf(Set<String> stopWordSet) {
	private Tfidf() {
//		this.stopWordSet = stopWordSet;
		nlp = Nlp.getInstance();
	}

//	public static Tfidf of(Set<String> stopWordSet) {
	public static Tfidf of() {
//		Tfidf tfidf = new Tfidf(stopWordSet);
		Tfidf tfidf = new Tfidf();
		return tfidf;
	}
	
	// -------------------------------------------------------------------------------
	/**
	 * @param _textList
	 */
//	public void process(List<String> _textList) {
	public void process(List<DocCreateObj> _docCreateObjList, Set<String> stopWordSet) {
		
		/* 1.preprocess */
		docList = new ArrayList<>();
		
//		for(String text: _textList) {
		for (int i = 0; i < _docCreateObjList.size(); i++) {
			long l1,l2;
			DocCreateObj docCreateObj = _docCreateObjList.get(i);
			System.out.println("Start processing doc " +i+": "+docCreateObj.getSn());
			l1 = System.currentTimeMillis();
			Doc doc = new Doc(i, docCreateObj.getSn(), docCreateObj.getTitle(), docCreateObj.getText());
			doc.init(stopWordSet);
			docList.add(doc);
			l2 = System.currentTimeMillis();
			System.out.println("Finish processing doc " +i+".\t"+NumberFormatUtil.getIntegerString((l2-l1))+"(ms)");
		}
		d = docList.size(); // 文件數量
		
		/* 2. */
		// 所有的word
		List<DocWordCnt> allDocWordList = docList.stream().flatMap(d -> d.getDocWordCntMap().values().stream())
				.collect(Collectors.toList());
				
		// 計算總詞頻
		Map<String,TfidfWordCnt > tempMap = new HashMap<>();
		for(DocWordCnt dwc: allDocWordList) {
			tempMap.putIfAbsent(dwc.getWord(), new TfidfWordCnt(dwc.getWord()));
			TfidfWordCnt twc = tempMap.get(dwc.getWord());
			twc.addDocIdx(dwc.getDocIdx()); // 互相關連
			dwc.setTwc(twc); // 互相關連
		}
		twcList = new ArrayList<>(tempMap.values());
		
	}
	
	public SXSSFWorkbook getDisplayRaw() {
		/* display */
		SXSSFWorkbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = wb.createSheet("TFIDF");
		int rowIdx =0;
		int colIdx;
		// title
		Row row = sheet.createRow(rowIdx++);
		colIdx = 0;
		row.createCell(colIdx++).setCellValue("Word");
		row.createCell(colIdx++).setCellValue("Times in Docs");
		row.createCell(colIdx++).setCellValue("IDF");
		for (int i = 0; i < d; i++) {
			row.createCell(colIdx++).setCellValue("Doc"+i+"-sn");
			row.createCell(colIdx++).setCellValue("Doc"+i+"-cnt");
			row.createCell(colIdx++).setCellValue("Doc"+i+"-tf");
			row.createCell(colIdx++).setCellValue("Doc"+i+"-tfidf");
		}
		
		// data
		List<TfidfWordCnt> twcList = this.twcList.stream().sorted(Comparator.comparing(TfidfWordCnt::getWord)).collect(Collectors.toList());
		for (TfidfWordCnt twc : twcList) {
			row = sheet.createRow(rowIdx++);
			colIdx = 0;
			row.createCell(colIdx++).setCellValue(twc.getWord());
			row.createCell(colIdx++).setCellValue(twc.getTimesInDocs());
			row.createCell(colIdx++).setCellValue(twc.getIdf());
			for(int i=0;i<d;i++) {
				Doc doc = docList.get(i);
				Map<String, DocWordCnt> docWordCntMap = doc.getDocWordCntMap();
				int cnt = 0;
				double tf= 0, tfidf= 0; 
				if(docWordCntMap.containsKey(twc.getWord())) {
					DocWordCnt dwc =docWordCntMap.get(twc.getWord()); 
					cnt = dwc.getCnt();
					tf = dwc.getTf();
					tfidf = dwc.getTfidf();
				}
//				row.createCell(colIdx++).setCellValue(NumberFormatUtil.getIntegerString(cnt));
//				row.createCell(colIdx++).setCellValue(NumberFormatUtil.getDecimalString(tf, 4));
//				row.createCell(colIdx++).setCellValue(NumberFormatUtil.getDecimalString(tfidf, 4));
				
				row.createCell(colIdx++).setCellValue(doc.getSn());
				row.createCell(colIdx++).setCellValue(cnt);
				row.createCell(colIdx++).setCellValue(tf);
				row.createCell(colIdx++).setCellValue(tfidf);
			}
		}
		return wb;
	}
	
	
	// -------------------------------------------------------------------------------
	public int getD() {
		return d;
	}

	public List<TfidfWordCnt> getTwcList() {
		return twcList;
	}
	
	// -------------------------------------------------------------------------------
	public class TfidfWordCnt {
		private String word;
		
		private Set<Integer> appeardDocIndexSet;// size就是在有在多少份文件中出現，必大於0。

		protected TfidfWordCnt(String word) {
			this.word = word;
			appeardDocIndexSet = new HashSet<>();
		}

		private void addDocIdx(int idx) {
			appeardDocIndexSet.add(idx);
		}

		public String getWord() {
			return word;
		}
		
		public int getTimesInDocs() {
			return appeardDocIndexSet.size();
		}
		
		public double getIdf() {
			return Math.log10(((double)d) /((double)getTimesInDocs()));
		}

	}

	// -------------------------------------------------------------------------------
	
	
}
