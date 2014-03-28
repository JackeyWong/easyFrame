package com.idemo.lucene_enhance.customscorequery;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.function.CustomScoreProvider;

public class FileNameScoreProvider extends CustomScoreProvider{

	private static String[] filenames;
	public FileNameScoreProvider(IndexReader reader) {
		super(reader);
		try {
			filenames = FieldCache.DEFAULT.getStrings(reader, "filename");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public float customScore(int doc, float subQueryScore, float valSrcScore)
			throws IOException {
		String file = filenames[doc];
		if(file.endsWith(".java")){
			subQueryScore *= 3;
		}
		return super.customScore(doc, subQueryScore, valSrcScore);
	}
	@Override
	public float customScore(int doc, float subQueryScore, float[] valSrcScores)
			throws IOException {
		return super.customScore(doc, subQueryScore, valSrcScores);
	}
}
