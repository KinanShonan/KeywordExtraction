/**
 * 
 */
package vn.com.datasection.keywordextraction.extractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import vn.com.datasection.keywordextraction.data.TermInfo;

/**
 * @author myname2
 * 
 */
public class Processor {

	private Map<String, TermInfo> termTable;
	private ArrayList<Map<String, TermInfo>> frequentTermClusters;
	private Map<String, Double> chiSquareTable;

	public Processor(Map<String, TermInfo> termTable) {
		this.termTable = sortByValues(termTable);
		this.frequentTermClusters = createFrequentTermClusters();
		this.chiSquareTable = new HashMap<String, Double>();
	}

	public void printTable(Map<String, TermInfo> table) {
		for (Map.Entry term : table.entrySet()) {
			System.out.println(term.getKey().toString() + " - "
					+ ((TermInfo) term.getValue()).getFreq() + " - "
					+ ((TermInfo) term.getValue()).getChiSquare() + " - "
					+ ((TermInfo) term.getValue()).getCooccurTermTable());
		}
	}

	public void createChiSquareTable() {
		calculateChiSquare2();
		for (Map.Entry term : termTable.entrySet()) {
			if (((term.getKey().toString().contains(" ")) || (Character
					.isUpperCase(term.getKey().toString().charAt(0))))) {
				this.chiSquareTable.put(term.getKey().toString(),
						((TermInfo) term.getValue()).getChiSquare());
			}
		}
		this.chiSquareTable = sortByValues(this.chiSquareTable);
	}

	public void printKeyword(int numberOfKeyWord) {
		int count = 0;
		System.out.println("Keywords: ");
		for (Map.Entry keyword : chiSquareTable.entrySet()) {
			if (count < numberOfKeyWord) {
				count++;
				System.out.println(keyword.getKey().toString());
			} else
				break;
		}
	}

	public ArrayList<Map<String, TermInfo>> createFrequentTermClusters() {
		Map<String, TermInfo> frequentTermTable = new HashMap<String, TermInfo>();
		int numberOfFrequentTerms = (Integer) termTable.size() * 30 / 100;
		int count = 0;
		ArrayList<Map<String, TermInfo>> frequentTermClusters = new ArrayList<>();

		// printTable(termTable);

		// Extract the most frequent terms
		for (Map.Entry term : termTable.entrySet()) {
			if (count < numberOfFrequentTerms) {
				frequentTermTable.put((String) term.getKey(),
						(TermInfo) term.getValue());
			} else
				break;
			count++;
		}

		// printTable(frequentTermTable);

		frequentTermTable = sortByValues(frequentTermTable);

		for (Map.Entry frequentTerm : frequentTermTable.entrySet()) {
			// If Cluster Table is empty, create it
			if (frequentTermClusters.isEmpty()) {
				Map<String, TermInfo> newCluster = new HashMap<String, TermInfo>();
				newCluster.put((String) frequentTerm.getKey(),
						(TermInfo) frequentTerm.getValue());
				frequentTermClusters.add(newCluster);
			} else {
				boolean termWasAdded = false;
				for (Map<String, TermInfo> cluster : frequentTermClusters) {
					if (termWasAdded == false) {
						for (Map.Entry term : cluster.entrySet()) {
							if (isSameClass(frequentTermTable, frequentTerm
									.getKey().toString(), term.getKey()
									.toString())) {
								cluster.put(frequentTerm.getKey().toString(),
										(TermInfo) frequentTerm.getValue());
								termWasAdded = true;
							}
						}
					}
				}
				if (termWasAdded == false) {
					Map<String, TermInfo> newCluster = new HashMap<String, TermInfo>();
					newCluster.put(frequentTerm.getKey().toString(),
							(TermInfo) frequentTerm.getValue());
					frequentTermClusters.add(newCluster);
				}
			}
		}

		// for (Map<String, TermInfo> cluster : frequentTermClusters) {
		// if (cluster.size() > 1) {
		// System.out.println("Cum: " + cluster.size());
		// printTable(cluster);
		// }
		// }
		return frequentTermClusters;
	}

	private double p(Map<String, TermInfo> cluster) {
		int nTotal = termTable.size();
		int nC = 0;

		for (Map.Entry term : termTable.entrySet()) {
			boolean isCooccur = false;
			for (Map.Entry frequentTerm : cluster.entrySet()) {
				if (isCooccur == false) {
					if (((TermInfo) term.getValue()).getCooccurTermTable()
							.containsKey(frequentTerm.getKey().toString())) {
						nC++;
						isCooccur = true;
					}
				}
			}
		}
		return (nC * 1.0) / nTotal;
	}

	private boolean isSameClass(Map<String, TermInfo> frequentTermTable,
			String term1, String term2) {
		if ((js(frequentTermTable, term1, term2) >= (0.95 * Math.log(2)))
				&& (mutualInformation(term1, term2) >= Math.log(2))) {
			return true;
		} else
			return false;
	}

	// Generic Sort HashMap
	/*
	 * Java method to sort Map in Java by value e.g. HashMap or Hashtable throw
	 * NullPointerException if Map contains null values It also sort values even
	 * if they are duplicates
	 */
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(
			Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// LinkedHashMap will keep the keys in the order they are inserted
		// which is currently sorted on natural ordering
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	private double h(double x) {
		if (x == 0.0)
			return 0.0;
		else if (x == 1)
			return 0.0;
		else
			return (-1) * x * Math.log(x);
	}

	private double p2(String term1, String term2) {
		int freqOccur = 0;
		if (termTable.get(term1).getCooccurTermTable().containsKey(term2)) {
			freqOccur = termTable.get(term1).getCooccurTermTable().get(term2);
		}
		int freq1 = termTable.get(term1).getFreq();
		return (freqOccur * 1.0) / freq1;
	}

	private double mutualInformation(String term1, String term2) {
		if (termTable.get(term1).getCooccurTermTable().containsKey(term2)) {
			return Math.log((termTable.size() * termTable.get(term1)
					.getCooccurTermTable().get(term2))
					/ (termTable.get(term1).getFreq() * termTable.get(term2)
							.getFreq()));
		} else
			return 0.0;
	}

	private double js(Map<String, TermInfo> frequentTermTable, String term1,
			String term2) {
		double j = 0.0;
		for (Map.Entry term : frequentTermTable.entrySet()) {
			String tempTerm = term.getKey().toString();
			// System.out.println("p2(" + term1 + ", " + tempTerm + ", " + term2
			// + ") = " + (p2(tempTerm, term1) + p2(tempTerm, term2))
			// + " - " + h(p2(tempTerm, term1) + p2(tempTerm, term2))
			// + " - " + p2(tempTerm, term1) + " - "
			// + h(p2(tempTerm, term1)) + " - " + p2(tempTerm, term2)
			// + " - " + h(p2(tempTerm, term2)));

			j += h(p2(tempTerm, term1) + p2(tempTerm, term2))
					- h(p2(tempTerm, term1)) - h(p2(tempTerm, term2));
		}
		j = Math.log(2) + (j / 2);
		// System.out.println("J(" + term1 + ", " + term2 + ") = " + j);
		return j;
	}

	private int freq(String term, Map<String, TermInfo> cluster) {
		int freq = 0;
		for (Map.Entry frequentTerm : cluster.entrySet()) {
			if (termTable.get(term).getCooccurTermTable()
					.containsKey(frequentTerm.getKey())) {
				freq += termTable.get(term).getCooccurTermTable()
						.get(frequentTerm.getKey());
			}
		}
		return freq;
	}

	private double chiSquare(String term, Map<String, TermInfo> cluster) {
		double chiSquare = 0.0;

		double temp1 = freq(term, cluster);
		double temp2 = termTable.get(term).getCooccurTermTable().size()
				* p(cluster);

		chiSquare = Math.pow(temp1 - temp2, 2.0) / temp2;

		return chiSquare;
	}

	private double chiSquare2(String term) {
		double chiSquare2 = 0.0;
		double maxChiSquare = 0.0;

		for (Map<String, TermInfo> cluster : frequentTermClusters) {
			double chiSquare = chiSquare(term, cluster);
			if (chiSquare > maxChiSquare) {
				maxChiSquare = chiSquare;
			}
			chiSquare2 += chiSquare;
		}

		return chiSquare2 - maxChiSquare;
	}

	private void calculateChiSquare2() {
		for (Map.Entry term : termTable.entrySet()) {
			((TermInfo) term.getValue()).setChiSquare(chiSquare2(term.getKey()
					.toString()));
		}
	}

	public Map<String, TermInfo> getTermTable() {
		return termTable;
	}

	public void setTermTable(Map<String, TermInfo> termTable) {
		this.termTable = termTable;
	}

	public ArrayList<Map<String, TermInfo>> getFrequentTermClusters() {
		return frequentTermClusters;
	}

	public void setFrequentTermClusters(
			ArrayList<Map<String, TermInfo>> frequentTermClusters) {
		this.frequentTermClusters = frequentTermClusters;
	}
}
