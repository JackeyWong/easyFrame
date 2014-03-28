package com.idemo.lucene_enhance.customscorequery;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.function.CustomScoreProvider;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.ValueSourceQuery;

public class FileNameScoreQuery extends CustomScoreQuery {

	private static final long serialVersionUID = -8166583262608035366L;

	public FileNameScoreQuery(Query subQuery) {
		super(subQuery);
	}

	public FileNameScoreQuery(Query subQuery, ValueSourceQuery... valSrcQueries) {
		super(subQuery, valSrcQueries);
	}

	public FileNameScoreQuery(Query subQuery, ValueSourceQuery valSrcQuery) {
		super(subQuery, valSrcQuery);
	}

	@Override
	protected CustomScoreProvider getCustomScoreProvider(IndexReader reader)
			throws IOException {
		return new FileNameScoreProvider(reader);
	}
	
}
