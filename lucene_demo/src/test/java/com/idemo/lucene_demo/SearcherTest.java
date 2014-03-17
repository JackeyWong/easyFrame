package com.idemo.lucene_demo;

import org.junit.Before;
import org.junit.Test;

import com.idemo.searcher.SearcherUtil;

public class SearcherTest {

	private SearcherUtil searcherUtil;

	@Before
	public void init() {
		searcherUtil = new SearcherUtil();
	}

	@Test
	public void createIndexTest() {
		searcherUtil.createIndex();
	}

	@Test
	public void queryTest() {
		searcherUtil.query();
	}

	@Test
	public void searchTest() throws InterruptedException {
		for(int i = 0;i<5;i++){
			searcherUtil.search("content", "like");
			System.out.println("--------------------");
			Thread.sleep(3000);
		}
	}

	@Test
	public void updateTest() {
		searcherUtil.update();
	}

	@Test
	public void deleteTest() {
		searcherUtil.delete();
	}

	@Test
	public void addTest() {
		searcherUtil.add();
	}

	@Test
	public void forceMergeTest() {
		searcherUtil.forceMerge();
	}

	@Test
	public void mergeDeleteTest() {
		searcherUtil.merge();
	}
}
