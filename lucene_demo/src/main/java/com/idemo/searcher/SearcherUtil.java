package com.idemo.searcher;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
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
	private IndexReader indexReader;

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
				writer.deleteAll();
				Document doc = null;
				for (int i = 0; i < ids.length; i++) {
					doc = new Document();
					doc.add(new Field("id", ids[i], Store.YES, Index.ANALYZED));
					doc.add(new Field("content", contents[i], Store.NO,
							Index.ANALYZED));
					doc.add(new Field("name", names[i], Store.YES,
							Index.NOT_ANALYZED));
					doc.add(new Field("email", emails[i], Store.YES,
							Index.NOT_ANALYZED));
					doc.add(new NumericField("attach", Store.YES, true)
							.setIntValue(attachs[i]));
					doc.add(new NumericField("date", Store.YES, true)
							.setLongValue(dates[i].getTime()));
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

	public void query() {
		try {
			IndexReader reader = IndexReader.open(directory);
			log("numDocs = " + reader.numDocs());
			log("maxDoc = " + reader.maxDoc());
			log("numDeleteDocs " + reader.numDeletedDocs());
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void log(String msg) {
		System.out.println(msg);
	}

	public IndexSearcher getsearcher() {
		try {
			if (indexReader == null) {
				indexReader = IndexReader.open(directory);
			} else {
				IndexReader ir = IndexReader.openIfChanged(indexReader);
				if (ir != null)
					this.indexReader = ir;
			}
			return new IndexSearcher(indexReader);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void search(String scope, String keyword) {
		IndexSearcher searcher = getsearcher();
		QueryParser parser = new QueryParser(VERSION, "content",
				new StandardAnalyzer(VERSION));
		try {
			TopDocs topDocs = searcher.search(
					parser.parse(scope + ":" + keyword), 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println("(" + scoreDoc.doc + "-" + doc.getBoost()
						+ "-" + scoreDoc.score + ")" + doc.get("name") + "["
						+ doc.get("email") + "]-->" + doc.get("id") + ","
						+ doc.get("attach") + "," + doc.get("date") + ","
						+ doc.getValues("email")[0]+"---->"+doc.get("id"));
			}
			searcher.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.apache.lucene.queryParser.ParseException e) {
			e.printStackTrace();
		}
	}

	public void add() {
		try {
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(VERSION,
							new StandardAnalyzer(VERSION)));
			try {
				Document doc = new Document();
				doc.add(new Field("id", "7", Store.YES, Index.ANALYZED));
				doc.add(new Field("content", "l'm like computer.", Store.NO,
						Index.ANALYZED));
				doc.add(new Field("name", "wangjie", Store.YES,
						Index.NOT_ANALYZED));
				doc.add(new Field("email", "wangjie@126.com", Store.YES,
						Index.NOT_ANALYZED));
				doc.add(new NumericField("attach", Store.YES, true)
						.setIntValue(5));
				doc.add(new NumericField("date", Store.YES, true)
						.setLongValue(new Date().getTime()));
				writer.addDocument(doc);
				writer.commit();
			} finally {
				if (writer != null)
					writer.close();
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		try {
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(VERSION,
							new StandardAnalyzer(VERSION)));
			try {
				writer.deleteDocuments(new Term("id", "1"));
				writer.commit();
			} finally {
				if (writer != null)
					writer.close();
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		try {
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(VERSION,
							new StandardAnalyzer(VERSION)));
			try {
				Document doc = new Document();
				doc.add(new Field("id", "130", Store.YES, Index.ANALYZED));
				doc.add(new Field("content", "l'm like programming.", Store.NO,
						Index.ANALYZED));
				doc.add(new Field("name", "jack wang", Store.YES,
						Index.NOT_ANALYZED));
				doc.add(new Field("email", "jackwang@gmail.com", Store.YES,
						Index.NOT_ANALYZED));
				doc.add(new NumericField("attach", Store.YES, true)
						.setIntValue(5));
				doc.add(new NumericField("date", Store.YES, true)
						.setLongValue(new Date().getTime()));
				writer.updateDocument(new Term("id", "7"), doc);

				writer.commit();
			} finally {
				if (writer != null)
					writer.close();
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void merge() {
		try {
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(VERSION,
							new StandardAnalyzer(VERSION)));
			try {
				writer.forceMergeDeletes();
				// equels call : writer.forceMergeDeletes(true);
				writer.commit();
			} finally {
				if (writer != null)
					writer.close();
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 会将索引合并为2段，这两段中的被删除的数据会被清空 特别注意：
	 * 此处Lucene在3.5之后不建议使用，因为会消耗大量的开销，
	 * Lucene会根据情况自动处理的
	 */
	public void forceMerge() {
		try {
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(VERSION,
							new StandardAnalyzer(VERSION)));
			try {
				writer.forceMerge(1);
				writer.commit();
			} finally {
				if (writer != null)
					writer.close();
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
