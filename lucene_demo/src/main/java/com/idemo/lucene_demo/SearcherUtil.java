package com.idemo.lucene_demo;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearcherUtil {

	private static final Version VERSION = Version.LUCENE_35;
	private static final String INDEX_PATH = "f:/lucene_index/index01";
	private String[] ids = { "1", "2", "3", "4", "5", "6" };
	private String[] emails = { "aa@itat.org", "bb@itat.org", "cc@cc.org",
			"dd@sina.org", "ee@zttc.edu", "ff@itat.org" };
	private String[] contents = { "welcome to visited the space,I like book",
			"hello boy, I like pingpeng ball", "my name is cc I like game",
			"I like football", "I like football and I like basketball too",
			"I like movie and swim" };
	private Date[] dates = null;
	private int[] attachs = { 2, 3, 1, 4, 5, 5 };
	private String[] names = { "zhangsan", "lisi", "john", "jetty", "mike",
			"jake" };
	private Directory directory;

	private void setDates() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dates = new Date[ids.length];
			dates[0] = sdf.parse("2010-02-19");
			dates[1] = sdf.parse("2012-01-11");
			dates[2] = sdf.parse("2011-09-19");
			dates[3] = sdf.parse("2010-12-22");
			dates[4] = sdf.parse("2012-01-01");
			dates[5] = sdf.parse("2011-05-19");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public SearcherUtil() {
		try {
			setDates();
			directory = FSDirectory.open(new File(INDEX_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createIndex() {
		try {
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(VERSION,
							new StandardAnalyzer(VERSION)));
			try {
				Document doc = null;
				for (int i = 0; i < ids.length; i++) {
					doc = new Document();
					doc.add(new Field("content", contents[i], Store.NO,
							Index.ANALYZED));
					doc.add(new Field("name", names[i], Store.YES,
							Index.NOT_ANALYZED));
					doc.add(new Field("email", emails[i], Store.YES,
							Index.NOT_ANALYZED));
					doc.add(new NumericField("attach").setIntValue(attachs[i]));
					doc.add(new NumericField("date").setLongValue(dates[i]
							.getTime()));
					writer.addDocument(doc);
				}
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} finally {
						if (IndexWriter.isLocked(directory)) {
							IndexWriter.unlock(directory);
						}
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void query(){
		try {
			IndexReader reader = IndexReader.open(directory);
//			reader.
			log("numDocs = "+reader.numDocs());
			log("maxDoc = "+reader.maxDoc());
			log("numDeleteDocs "+ reader.numDeletedDocs());
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void log(String msg){
		System.out.println(msg);
	}
	private IndexSearcher getsearcher() {
		
		return null;
	}
}
