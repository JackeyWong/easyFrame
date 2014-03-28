package com.idemo.lucene_enhance;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

	public static final String SCOPE_DATE = "date";
	public static final String SCOPE_SIZE = "size";
	public static final String SCOPE_FILENAME = "filename";
	public static final String SCOPE_CONTENT = "content";
	public static final String DIR_PATH = "E:/lucene_index/index_01";
	public static final String FILE_PATH = "E:/lucene_search";
	public static final Version version = Version.LUCENE_35;
	private SearcherUtil(){
	}
	private static Directory dir;
	static {
		try {
			dir = FSDirectory.open(new File(DIR_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createIndex(){
		try {
			IndexWriter writer = 
					new IndexWriter(dir, new IndexWriterConfig(version, new StandardAnalyzer(version)));
			writer.deleteAll();
			try {
				File[] filesList = new File(FILE_PATH).listFiles();
				Document doc;
				for (File f : filesList) {
					doc = new Document();
					doc.add(new Field(SCOPE_CONTENT, new FileReader(f)));
					doc.add(new Field(SCOPE_FILENAME, f.getName(), Store.YES,
							Index.NOT_ANALYZED_NO_NORMS));
					doc.add(new NumericField(SCOPE_SIZE, Store.YES, true)
							.setLongValue(f.length()));
					doc.add(new NumericField(SCOPE_DATE, Store.YES, true)
							.setLongValue(f.lastModified()));
					writer.addDocument(doc);
				}
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static IndexReader reader = null;
	public static IndexSearcher getSearcher(){
		try {
			if(reader == null){
				reader = IndexReader.open(dir);
			}else{
				IndexReader newReader = IndexReader.openIfChanged(reader);
				if(newReader != null) reader = newReader;
			}
			return new IndexSearcher(reader);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
