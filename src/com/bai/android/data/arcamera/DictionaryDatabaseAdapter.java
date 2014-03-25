/*
 * Copyright (C) 2013 BAIT AL- HIKMA All Rights Reserved.
 */
package com.bai.android.data.arcamera;


import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.bai.android.data.arcamera.Constant;

/**
 * Database adapter - provides methods to access the database. Since the database is external,
 * only read methods are provided.
 */
public class DictionaryDatabaseAdapter {
	private boolean isOpen = false;

	private static final String DATABASE_NAME 		= "dictionary.sqlite";
	private static final int 	DATABASE_VERSION 	= 11;
	private static final File 	DBDIR 				= new File(Constant.DATA_PATH + Constant.DICT_FOLDER);

	private Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	// converter instance for hiragana to katakana
	private HiraToKataConverter hkc;

	/** constructor */
	public DictionaryDatabaseAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context, null, null, 0);
		hkc = new HiraToKataConverter();
	}

	/** check if database is open */
	public boolean isOpen() {
		return isOpen;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, DBDIR + "/" + DATABASE_NAME, factory, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	/** Opens the database
		@return DBAdapter
		@throws SQLException */
	public DictionaryDatabaseAdapter open() throws SQLException {
		isOpen = true;
//		db = DBHelper.getWritableDatabase();
		db = DBHelper.getReadableDatabase();
		return this;
	}

	/** Closes the database */
	public void close() throws SQLException {
		isOpen = false;
		DBHelper.close();
	}

	/**
	 * Get all words from the database that are longer than the specified minimum (see Constant.java - LONG_WORDS_MIN_LENGTH)
	 */
	public Cursor getLongWords(){
		return db.rawQuery("SELECT word,speech_part,best_guess FROM jap_eng WHERE LENGTH(word)>"+
				Constant.LONG_WORDS_MIN_LENGTH+" AND LENGTH(word)<"+
				Constant.LONG_WORDS_MAX_LENGTH, null);
	}

	/**
	 * Fast search for an exact match of a word. Will search in the word column and will only return information
	 *  about the part of speech and the best translation of the word. For further data - getDatailedByword can 
	 *  be used.
	 * @return only information about part of speech and best guess translation
	 */
	public Cursor quickLookUp(String searchPhrase){
		return db.rawQuery("SELECT speech_part, best_guess, meaning FROM jap_eng WHERE word=?",
				new String[] { searchPhrase });
	}
	
	/**
	 * As a last chance, suggestions from the db can be obtained by searching for similar words like the searchPhrase
	 * replacing parts of the string with the sql wildcard %, but with the same length like to original word
	 * @return suggestions
	 */
	public Cursor lastResort(String searchPhrase){
		if (searchPhrase!= null && searchPhrase.length()>1){
			int phraseLen = searchPhrase.length();
			// constructing the query dynamically e.g. ABCD => %BCD,A%CD,AB%D,ABC%
			StringBuilder query = new StringBuilder("SELECT * FROM jap_eng WHERE length(word)="+phraseLen+" and (word GLOB '?"+searchPhrase.substring(1,phraseLen)+"'");
			for (int i = 0; i < phraseLen-2; i++) {
				query.append(" OR word GLOB '" + searchPhrase.substring(0, i + 1) + "?" + searchPhrase.substring(i + 2, phraseLen) + "'");
			}
			query.append("OR word GLOB '" + searchPhrase.substring(0, phraseLen - 1) + "?') ORDER BY freq DESC");
			return db.rawQuery(query.toString(), null);
		} return null;
	}

	/**
	 * As a last chance, suggestions from the db can be obtained by searching for similar words like the searchPhrase
	 * replacing parts of the string with the sql wildcard %, but with the same length like to original word
	 * @return suggestions
	 */
//	public Cursor unrecognizedKanji(String searchPhrase){
//		if (searchPhrase!= null && searchPhrase.length()>1){
//			String query = "SELECT * FROM jap_eng WHERE length(word)="+searchPhrase.length()+" and (word LIKE "+searchPhrase+") ORDER BY freq DESC";
//			return db.rawQuery(query, null);
//		} return null;
//	}
	public Cursor unrecognizedKanji(String searchPhrase){
		if (searchPhrase!= null && searchPhrase.length()>1){
			String query = "SELECT * FROM jap_eng WHERE length(word)="+searchPhrase.length()+" and (word GLOB "+searchPhrase+") ORDER BY freq DESC";
			return db.rawQuery(query, null);
		} return null;
	}

	/**
	 * Return information for the exact match(es) of the word
	 * @param japanese word
	 * @returns exact match(es) with attributes(columns): word, reading, best_guess
	 */
	public Cursor getExactMatchJapEng(String word){
		return db.rawQuery(
			"SELECT word, reading, meaning FROM jap_eng WHERE word='"+word+"' ORDER BY freq DESC",
			null);
	}

//	/**
//	 * Return information for the exact and semi-exact match(es) of the reading, words starting with a given string.
//	 * NB - convert hiragana to katakana beforehand.
//	 * @param japanese word
//	 * @returns exact match(es) with attributes(columns): word, reading, best_guess
//	 */
//	public Cursor getExactMatchOnReadingJapEng(String word){
//		String origWord=word;
//		String kataWord = "";
//		//disassemble original word char by char
//		String [] chars = origWord.split("(?!^)");
//		int i;
//		HiraToKataConverter hkc = new HiraToKataConverter();
//		// send each char one by one and reconstruct the phrase to be searched
//		for(i=0;i<origWord.length();i++){
//			kataWord += hkc.convertToKata(chars[i]);
//		}
//
//		return db.rawQuery(
//			"SELECT word, reading, meaning FROM jap_eng WHERE reading GLOB '" +kataWord+ "*' ORDER BY freq DESC",
//			null);
//	}

	/**
	 * 
	 * @param particles
	 * @return
	 */
	public Cursor getCompoundKanji(String particles){
		return db.rawQuery("SELECT * FROM compound_kanji WHERE particles='"+particles+"'", null);
	}

	/**
	 * UPDATED 20131218. THIS IS THE BEST FUNCITON TO CALL FOR A JAP - ENG TRANSLATION. RETURNS
	 * BOTH WORD CONTAINING A KANJI AND WORDS WHICH READING MATCHES THAT OF THE KANJI.
	 * @param japanese word
	 * @returns match(es) with attributes(columns): word, reading, best_guess 
	 * ordered descending by frequency up to 40 rows, BUT no exact match(es)
	 */
	public Cursor getJapEngWord(String word){
		// try to transform the word to katakana only characters
		String wordAsKana = hkc.convertWordToKata(word);
		String sqlQuery;
		if (wordAsKana != null) {
			// word can be converted to katakana only, we can search for the word and by reading
			//sqlQuery = "SELECT word, reading, meaning FROM jap_eng WHERE word GLOB '*" + word + "*' OR reading GLOB '*" + wordAsKana + "*' ORDER BY freq DESC;";
			sqlQuery = "SELECT word, reading, meaning, freq, 1 as SORT_KEY FROM jap_eng WHERE word = '" + word + 
					"' UNION SELECT word, reading, meaning, freq, 2 as SORT_KEY FROM jap_eng WHERE word <> '" + 
					word + "' AND (word GLOB '*" + word +"*' OR reading GLOB '*" + wordAsKana + "*') ORDER BY SORT_KEY ASC, freq DESC";		
		} else {
			// the word can't be converted to katakana only (eg. it contains kanji), we can only search only by word
			//sqlQuery = "SELECT word, reading, meaning FROM jap_eng WHERE word GLOB '*" + word + "*' ORDER BY freq DESC;";
			sqlQuery = "SELECT word, reading, meaning, freq, 1 as SORT_KEY FROM jap_eng WHERE word = '" + word + 
					"' UNION SELECT word, reading, meaning, freq, 2 as SORT_KEY FROM jap_eng WHERE word <> '" + 
					word + "' AND word GLOB '*" + word +"*' ORDER BY SORT_KEY ASC, freq DESC";		
		}
		return db.rawQuery(sqlQuery, null);
	}

	/**
	 * Return information about expressions/words containing the searchphrase
	 * @param japanese word
	 * @returns match(es) with attributes(columns): word, reading, best_guess 
	 * ordered descending by frequency up to 40 rows, BUT no exact match(es)
	 */
	public Cursor getSuggestionJapEng(String word){
		return db.rawQuery(
				"SELECT word, reading, meaning FROM jap_eng WHERE word GLOB '*"+word+"*' AND (word<>'"+word+"' OR reading<>'"+word+"') ORDER BY freq DESC LIMIT 40",
				null);
	}

	/**
	 * Return information for the exact match of the searchphrase in best guess
	 * @param english word
	 * @returns exact match(es) with attributes(columns): best_guess, word, reading
	 */
	public Cursor getExactMatchEngJap(String word){
		return db.rawQuery("SELECT meaning, word, reading FROM jap_eng WHERE best_guess='"+word+"' ORDER BY freq DESC",
				null);
	}

	/**
	 * Return information about expressions/words containing the searchphrase in meaning column
	 * @param english word
	 * @returns match(es) with attributes(columns): best_guess, word, reading
	 * ordered descending by frequency up to 40 rows, BUT no exact match(es)
	 */
	public Cursor getSuggestionEngJap(String word){
		return db.rawQuery("SELECT meaning, word, reading FROM jap_eng WHERE meaning GLOB '*"+word+"*' AND best_guess<>'"+word+"' ORDER BY freq DESC LIMIT 40",null);
	}
	
	public void beginTransaction(){
		db.beginTransaction();
	}

	public void setTransactionSuccessful(){
		db.setTransactionSuccessful();
	}

	public void endTransaction(){
		db.endTransaction();
	}
}