package vn.com.datasection.keywordextraction.extractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import vn.com.datasection.keywordextraction.data.TermInfo;

public class Main {

	private static String path = "tintuc/thegioi/";

	public static void main(String[] args) throws IOException {

		Parser parser = new Parser();
		Processor processor = new Processor(parser.fileContentParse(path
				+ "000077" + ".json"));
		processor.createChiSquareTable();
		processor.printKeyword(15);
	}
}
