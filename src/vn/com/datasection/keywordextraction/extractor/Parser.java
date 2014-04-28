package vn.com.datasection.keywordextraction.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

import org.jsoup.Jsoup;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import vn.com.datasection.keywordextraction.data.Article;
import vn.com.datasection.keywordextraction.data.TermInfo;
import vn.hus.nlp.sd.SentenceDetector;
import vn.hus.nlp.sd.SentenceDetectorFactory;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.VietTokenizer;

import com.google.gson.Gson;

public class Parser {

	private ArrayList<String> stopWord = new ArrayList<>();
	private String[] SentencesList;

	public Parser() {
		createStopWord();
	}

	public void createStopWord() {
		stopWord.add(".");
		stopWord.add(",");
		stopWord.add(":");
		stopWord.add("(");
		stopWord.add(")");
		stopWord.add("/");
		stopWord.add("…");
		stopWord.add("-");
		stopWord.add("'");
	}

	public Map<String, TermInfo> textContentParse(String text)
			throws IOException {

		StringBuffer content = new StringBuffer(text);

		// Segment sentences from content string
		VietTokenizer tokenizer = new VietTokenizer();
		SentenceDetector sentenceDetector = SentenceDetectorFactory
				.create("vietnamese");
		StringReader reader = new StringReader(content.toString());
		SentencesList = sentenceDetector.detectSentences(reader);

		// List terms from sentences
		Map<String, TermInfo> termTable = new HashMap<String, TermInfo>();

		for (String sentence : SentencesList) {
			sentence = Character.toLowerCase(sentence.charAt(0))
					+ sentence.substring(1);
			String[] terms = tokenizer.tokenize(sentence);
			terms = terms[0].split(" ");

			for (int i = 0; i < terms.length; i++) {
				if (!stopWord.contains(terms[i])) {
					if (!Character.isDigit(terms[i].charAt(0))) {
						terms[i] = terms[i].replace('_', ' ');
						if (termTable.containsKey(terms[i])) {
							TermInfo info = termTable.get(terms[i].toString());
							Map<String, Integer> cooccurTermTable = info
									.getCooccurTermTable();
							for (int j = 0; j < terms.length; j++) {
								if (!stopWord.contains(terms[j])) {
									if (!Character.isDigit(terms[j].charAt(0))) {
										if (!terms[j].equals(terms[i])) {
											if (cooccurTermTable
													.containsKey(terms[j])) {
												cooccurTermTable
														.put(terms[j].replace(
																'_', ' '),
																cooccurTermTable
																		.get(terms[j]) + 1);
											} else {
												cooccurTermTable.put(terms[j]
														.replace('_', ' '), 1);
											}
										}
									}
								}
							}
							info.setCooccurTermTable(cooccurTermTable);
							info.setFreq(info.getFreq() + 1);
							termTable.put(terms[i], info);
						} else {
							TermInfo info = new TermInfo(terms[i], null, 0);
							Map<String, Integer> cooccurTermTable = new HashMap<String, Integer>();
							for (int j = 0; j < terms.length; j++) {
								if (!stopWord.contains(terms[j])) {
									if (!Character.isDigit(terms[j].charAt(0))) {
										if (!terms[j].equals(terms[i])) {
											if (cooccurTermTable
													.containsKey(terms[j])) {
												cooccurTermTable
														.put(terms[j].replace(
																'_', ' '),
																cooccurTermTable
																		.get(terms[j]) + 1);
											} else {
												cooccurTermTable.put(terms[j]
														.replace('_', ' '), 1);
											}
										}
									}
								}
							}
							info.setCooccurTermTable(cooccurTermTable);
							info.setFreq(info.getFreq() + 1);
							termTable.put(terms[i], info);
						}
					}
				}
			}
		}
		return termTable;
	}

	public Map<String, TermInfo> fileContentParse(String filename)
			throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(filename));
		String currentLine;
		StringBuffer strBuff = new StringBuffer();

		// Get text from file json
		System.out.println("Parsing content from file: " + filename);
		while ((currentLine = br.readLine()) != null) {
			strBuff.append(currentLine);
		}

		// Get content from json string
		Article art = new Article();
		Gson gson = new Gson();
		art = gson.fromJson(strBuff.toString(), Article.class);
		StringBuffer content = new StringBuffer(art.getDescription());
		content.append(" " + Jsoup.parse(art.getContent()).text());
		//
		// // Segment sentences from content string
		// VietTokenizer tokenizer = new VietTokenizer();
		// SentenceDetector sentenceDetector = SentenceDetectorFactory
		// .create("vietnamese");
		// StringReader reader = new StringReader(content.toString());
		// SentencesList = sentenceDetector.detectSentences(reader);
		//
		// // List terms from sentences
		// Map<String, TermInfo> termTable = new HashMap<String, TermInfo>();
		//
		// for (String sentence : SentencesList) {
		// sentence = Character.toLowerCase(sentence.charAt(0))
		// + sentence.substring(1);
		// String[] terms = tokenizer.tokenize(sentence);
		// terms = terms[0].split(" ");
		//
		// for (int i = 0; i < terms.length; i++) {
		// if (!stopWord.contains(terms[i])) {
		// if (!Character.isDigit(terms[i].charAt(0))) {
		// terms[i] = terms[i].replace('_', ' ');
		// if (termTable.containsKey(terms[i])) {
		// TermInfo info = termTable.get(terms[i].toString());
		// Map<String, Integer> cooccurTermTable = info
		// .getCooccurTermTable();
		// for (int j = 0; j < terms.length; j++) {
		// if (!terms[j].equals(terms[i])) {
		// if (cooccurTermTable.containsKey(terms[j])) {
		// cooccurTermTable
		// .put(terms[j].replace('_', ' '),
		// cooccurTermTable
		// .get(terms[j]) + 1);
		// } else {
		// cooccurTermTable.put(
		// terms[j].replace('_', ' '), 1);
		// }
		// }
		// }
		// info.setCooccurTermTable(cooccurTermTable);
		// info.setFreq(info.getFreq() + 1);
		// termTable.put(terms[i], info);
		// } else {
		// TermInfo info = new TermInfo(terms[i], null, 0);
		// Map<String, Integer> cooccurTermTable = new HashMap<String,
		// Integer>();
		// for (int j = 0; j < terms.length; j++) {
		// if (!terms[j].equals(terms[i])) {
		// if (cooccurTermTable.containsKey(terms[j])) {
		// cooccurTermTable
		// .put(terms[j], cooccurTermTable
		// .get(terms[j]) + 1);
		// } else {
		// cooccurTermTable.put(terms[j], 1);
		// }
		// }
		// }
		// info.setCooccurTermTable(cooccurTermTable);
		// info.setFreq(info.getFreq() + 1);
		// termTable.put(terms[i], info);
		// }
		// }
		// }
		// }
		// }
		//
		// // for(Map.Entry term : termTable.entrySet()){
		// // System.out.println(term.getKey().toString() + " - " +
		// // ((TermInfo)term.getValue()).getFreq());
		// // }

		return textContentParse(content.toString());
	}
}
