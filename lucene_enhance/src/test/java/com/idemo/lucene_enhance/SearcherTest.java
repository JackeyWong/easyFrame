package com.idemo.lucene_enhance;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.function.DocValues;
import org.apache.lucene.search.function.FieldScoreQuery;
import org.apache.lucene.search.function.FieldScoreQuery.Type;
import org.apache.lucene.search.function.OrdFieldSource;
import org.apache.lucene.search.function.ValueSource;
import org.apache.lucene.search.function.ValueSourceQuery;
import org.junit.Test;

import com.idemo.lucene_enhance.customscorequery.FileNameScoreQuery;

public class SearcherTest {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Test public void createIndexTest(){
		SearcherUtil.createIndex();
	}
	@Test public void searchTest() throws ParseException{
		try {
			IndexSearcher searcher = SearcherUtil.getSearcher();
			Query query = new TermQuery(new Term(SearcherUtil.SCOPE_CONTENT, "java"));
//			排序
//			TopDocs topDocs = searcher.search(query, 10);
//			TopDocs topDocs = searcher.search(query, 20,Sort.INDEXORDER);
//			TopDocs topDocs = searcher.search(query, 20,Sort.RELEVANCE);
//			TopDocs topDocs = searcher.search(query, 20,new Sort(new SortField(SearcherUtil.SCOPE_DATE, SortField.LONG, true)));
//			过滤
//			TopDocs topDocs = 
//			 searcher.search(query,new TermRangeFilter(SearcherUtil.SCOPE_FILENAME, "a", "w", true, true),20);
			
//			TopDocs topDocs = 
//				searcher.search(query,NumericRangeFilter.newLongRange(SearcherUtil.SCOPE_SIZE, 1L, 1000L, true, true),20);
			
			//			TopDocs topDocs = 
//				searcher.search(
//						query,
//						new QueryWrapperFilter(
//								new QueryParser(
//										SearcherUtil.version, 
//										"content", 
//										new StandardAnalyzer(SearcherUtil.version))
//								.parse("linux")),20);
			FileNameScoreQuery fileNameScoreQuery = new FileNameScoreQuery(query);
			TopDocs topDocs = searcher.search(fileNameScoreQuery, 10);
			for (ScoreDoc score : topDocs.scoreDocs) {
				Document doc = searcher.doc(score.doc);
				System.out.print(score+"\t"+doc.getBoost()+"\t");
				System.out.print(SearcherUtil.SCOPE_FILENAME +":"+doc.get(SearcherUtil.SCOPE_FILENAME)+"\t");
				System.out.print(SearcherUtil.SCOPE_SIZE +":"+doc.get(SearcherUtil.SCOPE_SIZE)+"\t");
				System.out.print(SearcherUtil.SCOPE_DATE +":"+sdf.format(new Date(Long.valueOf(doc.get(SearcherUtil.SCOPE_DATE))))+"\t");
				System.out.print(SearcherUtil.SCOPE_CONTENT +":"+doc.get(SearcherUtil.SCOPE_CONTENT)+"\t");
				System.out.print("\n");
			}
			searcher.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
