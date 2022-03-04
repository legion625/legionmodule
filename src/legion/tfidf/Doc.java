package legion.tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;
import legion.nlp.Nlp;
import legion.tfidf.Tfidf.TfidfWordCnt;

public class Doc {
	private int idx;
	private String sn;
	private String title;
	private String rawText;
	
	/* data */
	private int wordsCntRaw, wordsCntAfterPos, wordsCntAfterStop;
	private Map<String, DocWordCnt> docWordMap;

	// -------------------------------------------------------------------------------
	public String getSn() {
		return sn;
	}
	
	// -------------------------------------------------------------------------------
	public Doc(int idx, String sn, String title, String rawText) {
		this.idx = idx;
		this.sn = sn;
		this.title = title;
		this.rawText = rawText;
	}
	
	// -------------------------------------------------------------------------------
	public void init(Set<String> _customStopWordSet) {
		Nlp nlp = Nlp.getInstance();
		// 斷句/斷詞
		List<CoreMap> sentences = nlp.parseSentences(rawText);
		wordsCntRaw = sentences.parallelStream().mapToInt(s -> s.get(TokensAnnotation.class).size()).sum();
		// 過濾POS並取出word
		List<String> lemmaList = nlp.parseLemma(sentences);
		wordsCntAfterPos = lemmaList.size();
		// 過濾stopwords
		lemmaList = nlp.filterStopWords(lemmaList, _customStopWordSet);
		wordsCntAfterStop = lemmaList.size();

		docWordMap = new HashMap<>();
		for (String word : lemmaList) {
			docWordMap.putIfAbsent(word, new DocWordCnt(word));
			DocWordCnt dw = docWordMap.get(word);
			dw.add();
		}
	}
	
	public Map<String,DocWordCnt> getDocWordCntMap(){
		return docWordMap;
	}
	
	// -------------------------------------------------------------------------------
	public class DocWordCnt {
		private String word;
		private int cnt;
		
		private TfidfWordCnt twc;

		public DocWordCnt(String word) {
			this.word = word;
			this.cnt = 0;
		}

		private void add() {
			cnt++;
		}

		public String getWord() {
			return word;
		}

		public int getCnt() {
			return cnt;
		}

		public double getTf() {
			return wordsCntAfterStop <= 0 ? 0 : ((double)getCnt() / (double)wordsCntAfterStop);
		}
		
		public void setTwc(TfidfWordCnt twc) {
			this.twc = twc;
		}
		
		public double getTfidf() {
			return getTf() * twc.getIdf();
		}
		
		public int getDocIdx() {
			return idx;
		}

	}

}
