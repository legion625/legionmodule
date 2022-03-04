package legion.stanfordcorenlp;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections4.ListUtils;
import org.junit.Test;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;

public class StanfordCorenlpTest {
	
	/**
	 * 
	 */
	@Test
	public void testStanfordCorenlp4_2_0() {
		/**
         * 创建一个StanfordCoreNLP object
         * tokenize(分词)、ssplit(断句)、 pos(词性标注)、lemma(词形还原)、
         * ner(命名实体识别)、parse(语法解析)、指代消解？同义词分辨？
         */
        
        Properties props = new Properties();    
//        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");    // 七种Annotators
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner");    // 七种Annotators
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);    // 依次处理
        
//        String text = "This is a test.";               // 输入文本
        String text ="Fig 4 is A method [12] and system of processing an information technology (IT) electronic request is provided. The electronic request is received in natural language from a user. Parameters of the electronic request are extracted. A risk of the electronic request is determined. A policy based on the parameters and the risk of the electronic request is determined and executed. A level of trust between the user and the computer device is calculated based on the determined risk and an outcome of the execution of the policy."; 
        
        Annotation document = new Annotation(text);    // 利用text创建一个空的Annotation
        pipeline.annotate(document);                   // 对text执行所有的Annotators（七种）
        
        // 下面的sentences 中包含了所有分析结果，遍历即可获知结果。
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        System.out.println("word\tpos\tlemma\tner");
        
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                
                String word = token.get(TextAnnotation.class);            // 获取分词
                String pos = token.get(PartOfSpeechAnnotation.class);     // 获取词性标注
                String ne = token.get(NamedEntityTagAnnotation.class);    // 获取命名实体识别结果
                String lemma = token.get(LemmaAnnotation.class);          // 获取词形还原结果
               
                System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
            }
            
//            // 获取parse tree
//            Tree tree = sentence.get(TreeAnnotation.class);    
//            System.out.println(tree.toString());
            
//            // 获取dependency graph
//            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
//            System.out.println(dependencies);
        }
//        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
	}
	

	
	// -------------------------------------------------------------------------------
	@Test
	public void testGetLemmas() {
		/**
         * 创建一个StanfordCoreNLP object
         * tokenize(分词)、ssplit(断句)、 pos(词性标注)、lemma(词形还原)、
         * ner(命名实体识别)、parse(语法解析)、指代消解？同义词分辨？
         */
        
        Properties props = new Properties();    
//        props.put("annotators", "tokenize, ssplit, lemma");    // 七种Annotators
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");    // 七种Annotators
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);    // 依次处理
        
//        String text = "This is a test.";               // 输入文本
//        String text ="Fig 4 is A method [12] and system of processing an information technology (IT) electronic request is provided. The electronic request is received in natural language from a user. Parameters of the electronic request are extracted. A risk of the electronic request is determined. A policy based on the parameters and the risk of the electronic request is determined and executed. A level of trust between the user and the computer device is calculated based on the determined risk and an outcome of the execution of the policy."; 
        String text ="process processing processed";
        
        Annotation document = new Annotation(text);    // 利用text创建一个空的Annotation
        pipeline.annotate(document);                   // 对text执行所有的Annotators（七种）
        
        // 下面的sentences 中包含了所有分析结果，遍历即可获知结果。
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        System.out.println("word\tpos\tlemma\tner");
        
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                
                String word = token.get(TextAnnotation.class);            // 获取分词
                String pos = token.get(PartOfSpeechAnnotation.class);     // 获取词性标注
                String ne = token.get(NamedEntityTagAnnotation.class);    // 获取命名实体识别结果
                String lemma = token.get(LemmaAnnotation.class);          // 获取词形还原结果
               
                System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
//                System.out.println(word+"\t"+lemma);
            }
            
        }
	}
	
}
