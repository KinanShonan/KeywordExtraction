/**
 * 
 */
package vn.com.datasection.keywordextraction.data;

import java.util.Map;

/**
 * @author myname2
 * 
 */
public class TermInfo implements Comparable {

	private String content;
	private Map<String, Integer> cooccurTermTable;
	private int freq;
	private double chiSquare;

	public TermInfo(String content, Map<String, Integer> cooccurTermTable,
			int freq, double chiSquare) {
		this.content = content;
		this.cooccurTermTable = cooccurTermTable;
		this.freq = freq;
		this.chiSquare = chiSquare;
	}

	public TermInfo(String content, Map<String, Integer> cooccurTermTable,
			int freq) {
		this.content = content;
		this.cooccurTermTable = cooccurTermTable;
		this.freq = freq;
		this.chiSquare = 0.0;
	}

	public int compareTo(TermInfo term2) {
		int freq1 = this.freq;
		int freq2 = term2.getFreq();

		if (freq1 > freq2)
			return 1;
		else if (freq1 == freq2)
			return 0;
		else
			return -1;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, Integer> getCooccurTermTable() {
		return cooccurTermTable;
	}

	public void setCooccurTermTable(Map<String, Integer> cooccurTermTable) {
		this.cooccurTermTable = cooccurTermTable;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public double getChiSquare() {
		return chiSquare;
	}

	public void setChiSquare(double chiSquare) {
		this.chiSquare = chiSquare;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		int freq1 = this.freq;
		int freq2 = ((TermInfo) o).getFreq();

		if (freq1 > freq2)
			return 1;
		else if (freq1 == freq2)
			return 0;
		else
			return -1;
	}

}
