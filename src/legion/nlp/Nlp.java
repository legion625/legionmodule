package legion.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import legion.util.DataFO;

public class Nlp {
	private StanfordCoreNLP pipeline;
	
	private Nlp() {
		 Properties props = new Properties();    
//       props.put("annotators", "tokenize, ssplit, lemma");    // 七种Annotators
       props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");    // 七种Annotators
       pipeline = new StanfordCoreNLP(props);    // 依次处理
	}
	private final static Nlp INSTANCE = new Nlp();
	public final static Nlp getInstance() {
		return INSTANCE;
	}
	
	// -------------------------------------------------------------------------------
	public  List<CoreMap> parseSentences(String _text){
//		System.out.println("parseSentences::start");
		
		if (DataFO.isEmptyString(_text))
			return new ArrayList<>();
		
		/**
         * 创建一个StanfordCoreNLP object
         * tokenize(分词)、ssplit(断句)、 pos(词性标注)、lemma(词形还原)、
         * ner(命名实体识别)、parse(语法解析)、指代消解？同义词分辨？
         */
        
//        Properties props = new Properties();    
////        props.put("annotators", "tokenize, ssplit, lemma");    // 七种Annotators
//        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");    // 七种Annotators
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);    // 依次处理
//        
//        String text = "This is a test.";               // 输入文本
//        String text ="Fig 4 is A method [12] and system of processing an information technology (IT) electronic request is provided. The electronic request is received in natural language from a user. Parameters of the electronic request are extracted. A risk of the electronic request is determined. A policy based on the parameters and the risk of the electronic request is determined and executed. A level of trust between the user and the computer device is calculated based on the determined risk and an outcome of the execution of the policy."; 
        
        Annotation document = new Annotation(_text);    // 利用text创建一个空的Annotation
        pipeline.annotate(document);                   // 对text执行所有的Annotators（七种）
        
        // 下面的sentences 中包含了所有分析结果，遍历即可获知结果。
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
//        System.out.println("word\tpos\tlemma\tner");
        
        /*for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                
                String word = token.get(TextAnnotation.class);            // 获取分词
                String pos = token.get(PartOfSpeechAnnotation.class);     // 获取词性标注
                String ne = token.get(NamedEntityTagAnnotation.class);    // 获取命名实体识别结果
                String lemma = token.get(LemmaAnnotation.class);          // 获取词形还原结果
               
                System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
            }
            
        }*/
        
//        System.out.println("parseSentences::end");
        return sentences;
	}
	
	
	private final static String REGEX_INVALID_POS ="WP|LS|NFP|DT|TO|IN|CC|CD|WRB|.|-LRB-|-RRB-"; 
	
	
	public  List<String> parseLemma(List<CoreMap> _sentences){
//		long l1,l2;
//		l1 = System.currentTimeMillis();
//		System.out.println("parseLemma::start");
		List<CoreLabel> tokens = _sentences.stream().flatMap(s -> s.get(TokensAnnotation.class).stream())
				.collect(Collectors.toList());
//		System.out.println("words count before filtering POS:\t" + tokens.size());
		List<String> lemmas = tokens.stream()
				.filter(token->!token.get(PartOfSpeechAnnotation.class).matches(REGEX_INVALID_POS))
				.map(token -> token.get(LemmaAnnotation.class)).collect(Collectors.toList());
//		System.out.println("words count after filtering POS:\t" + lemmas.size());
//		l2 = System.currentTimeMillis();
//		System.out.println("parseLemma(ms):\t"+ (l2-l1));
//		System.out.println("parseLemma::end");
		return lemmas;
	}
//	
//	/**
//	 * @param _text 文本 i.e., 整篇文章
//	 * @return 詞性還原後的lemma列表
//	 */
//	@Deprecated
//	public  List<String> parseLemma(String _text){
//		return parseLemma(parseSentences(_text));
//	}
	
	// -------------------------------------------------------------------------------
	 final List<String> defaultStopWordList = Arrays.asList(
		        "a", "an", "and", "are", "as", "at", "be", "but", "by",
		        "for", "if", "in", "into", "is", "it",
		        "no", "not", "of", "on", "or", "such",
		        "that", "the", "their", "then", "there", "these",
		        "they", "this", "to", "was", "will", "with"
		    );
	 
	 
	 public List<String> filterStopWords(List<String> _wordList, Set<String> _customStopWordSet) {
		List<String> swList = new ArrayList<>(defaultStopWordList);
		if (_customStopWordSet != null)
			swList.addAll(_customStopWordSet.stream().map(w->w.toLowerCase().trim()).distinct().collect(Collectors.toList()));

		Set<String> swSet = new HashSet<>(swList);
		return _wordList.stream().filter(w -> {
			// 只保留英文數字、再轉成小寫
			w = w.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
			// empty string excluded
			if(DataFO.isEmptyString(w))
				return false;
			// number excluded
			if (w.matches("[0-9]+")) {
				return false;
			}
			// stopwords
			if (swSet.contains(w.toLowerCase()))
				return false;
			return true;
		}).collect(Collectors.toList());
	}
}
