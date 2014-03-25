package com.bai.android.data.arcamera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.database.Cursor;
import android.util.Log;

import com.bai.android.data.arcamera.Constant;
import com.bai.android.data.arcamera.DictionaryDatabaseAdapter;

public class Engine {
	/** incoming string or phrase to analyze */
	private String 			stringToAnalyze;
	/** array to store the results from the morhpology analysis */
	private ArrayList<Word> words;
	/** hashmap storing the longest words, which will provide very fast lookups */
	private HashMap<String, QuickLookupResult> longWords;
	/** Database Adapter for searching the db for results */
	private DictionaryDatabaseAdapter db;
	/** the current index (offset from left of @stringToAnalyze) during parsing */
	private int				parseIndex;
	/** punctuation signs list */
	private String punctuationSigns = " ,.!?/|\\;:<>{}()[]-=\"`\'「�?‘�?�??�?��?�?��＞？｛｝（）＝�?";
	/** list storing all variations for a given word */
	private ArrayList<Variation> variations;
	/** general use cursor for getting db results */
	private Cursor cursor;
	/** flag which will be set to false if the user cancels the operation */
	private boolean shouldContinue = true;
	/** saves the highest word frequency factor, when trying to find substitute for unrecognised kanji in tryToReplaceUnknownKanji() */
	private int bestResult;
	/** save the character to replace an unrecognised kanji with */
	private char replacementChar;

	/**
	 * Class constructor. Will generate a hashmap containing all word longer than LONG_WORDS_MIN_LENGTH 
	 * and LONG_WORDS_MAX_LENGTH, appr 10K record and 6 Mb of memory. 
	 * ! Should call @release() when finished using the engine,
	 * in order to free memory.
	 * @param dbAdapter an instantiated and preferably open adapter. The Calling activity should close
	 * the adapter on its own after it finishes using the engine.
	 */
	public Engine(DictionaryDatabaseAdapter dbAdapter){
		this.db = dbAdapter;
		longWords = new HashMap<String, QuickLookupResult>();
		//fill in the long words hash map
		if (!db.isOpen()) db.open();
		cursor = db.getLongWords();
		int wordIndexInCursor = cursor.getColumnIndex("word");
		int speechPartIndexInCursor = cursor.getColumnIndex("speech_part");
		int bestGuessIndexInCursor = cursor.getColumnIndex("best_guess");
		while (cursor.moveToNext()){
			longWords.put(
					cursor.getString(wordIndexInCursor), 
					new QuickLookupResult(
							cursor.getString(speechPartIndexInCursor), 
							cursor.getString(bestGuessIndexInCursor)));
		}
	}

	/**
	 * Analyzes an input string and returns an array with information for all words found and translated.
	 * @param stringToAnalyze the text to analyze - should be trimmed
	 * @return ArrayList<Word> found words, may include punctuations as well
	 */
	public ArrayList<Word> analyzeString(String stringToAnalyze){
		setShouldContinue(true);
		this.stringToAnalyze = stringToAnalyze;
		words = new  ArrayList<Word>();
		parseIndex = 0;
		while (parseIndex < this.stringToAnalyze.length() && shouldContinue){
			
			//first check for compounds, tesseract constantly breaks particles into separate kanji
			if (tryToRecoverCompoundKanji()){
				//a kanji match was found for unrecognised characters
				
				words = new  ArrayList<Word>();
				words.add(new Word("", "", "", "", "("+this.stringToAnalyze+") ","")); //save the edited string to display it to the user
//				Log.d(Constant.TAG,"REPLACED WITH COMPUND KANJI: "+this.stringToAnalyze);
				parseIndex = 0;	//restart the analysis
			} 
			
			//			if (punctuationSigns.indexOf(stringToAnalyze.charAt(parseIndex))!=-1){
			//				words.add(new Word(stringToAnalyze.substring(parseIndex, parseIndex+1), "punc", null, null, null)); //NOTE NO PUNCTUATION RIGHT NOW< THEY ARE REMOVED BY CAMERA ACTIVITY
			//			} else 
			if (!processChunk()){
				//	if (!searchSimilar()) addUnknownWordToList(stringToAnalyze.substring(parseIndex, parseIndex+1));
				
				//still unrecognised, check if it can be part of a phrase
				if (tryToReplaceUnknownKanji()) {
					//if we have recovered a symbol, break and restart;
					this.stringToAnalyze = new String(this.stringToAnalyze.replace(this.stringToAnalyze.charAt(parseIndex),replacementChar));
//					Log.d(Constant.TAG,"REPLACED WITH "+this.stringToAnalyze);
					words = new  ArrayList<Word>();
					words.add(new Word("", "", "", "", "("+this.stringToAnalyze+") ","")); //save the edited string to display it to the user
					parseIndex = 0; //restart the analysis
				} 
				//still no result - just add the underognised symbol to the result list
				else addUnknownWordToList(this.stringToAnalyze.substring(parseIndex, parseIndex+1));
			} 
		}
		if (shouldContinue)	return words;
		words.add(new Word("", "", "", "", "...interrupted",""));
		return words;
	}

	/**
	 * Generates parts (chunks) of string starting from the current index (@parseIndex) and tries to find any matches.
	 * If a match is found, true is returned and the match word is added to the results array, otherwise false is returned.
	 */
	private boolean processChunk(){
		int limiter = ( parseIndex + Constant.LONG_WORDS_MAX_LENGTH) >= stringToAnalyze.length() ? 
				stringToAnalyze.length() - 1 : parseIndex + Constant.LONG_WORDS_MAX_LENGTH; 
		return parse(limiter, true);
	}

//	/**
//	 * Searches for similar string, based on a substring of 4 char string from the current position
//	 * @return true if any result was found
//	 */
//	private boolean searchSimilar(){
//		if (shouldContinue){
//			//get the max length of the word
//			int limiter = (	parseIndex + Constant.SIMILAR_WORD_MAX_LENGTH) >= stringToAnalyze.length() ? 
//					stringToAnalyze.length() - 1 : parseIndex + Constant.SIMILAR_WORD_MAX_LENGTH;
//					return parse(limiter, false);
//		} 
//		return false;
//	}

	private boolean processSearchResult(Cursor cursor, int index){
		if (cursor.moveToFirst()){
			if (cursor.getInt(cursor.getColumnIndex("freq"))>bestResult){
				bestResult = cursor.getInt(cursor.getColumnIndex("freq"));
				replacementChar = cursor.getString(cursor.getColumnIndex("word")).charAt(0);
			}
			return true;
		}
		return false;
	}
	


	/** Tries to see if there is another kanji which could replace the unrecognized kanji, so that the combination with the
	 * other kanji around it will have a meaning. 
	 * The function will search for maximum of 3 characters back and/or ahead
	 * @return true if it found a match and saves the character to the global @replacementChar
	 */
	private boolean tryToReplaceUnknownKanji(){
		if (shouldContinue){
			String searchPhrase = "?";
			replacementChar = stringToAnalyze.charAt(parseIndex);
			bestResult = 0;
			boolean betterMatchFound = false;

			// like **?*
			if (parseIndex>1 && stringToAnalyze.length()-parseIndex>2){
				searchPhrase = "'"+ stringToAnalyze.charAt(parseIndex-2) + stringToAnalyze.charAt(parseIndex-1) + "?" +  stringToAnalyze.charAt(parseIndex+1)+"'";
				if (processSearchResult(db.unrecognizedKanji(searchPhrase),2)) return betterMatchFound = true; //prefer long results 
			}

			// like ?*** and ?**
			if (shouldContinue && stringToAnalyze.length() - parseIndex > 3) {
				searchPhrase = "'?"+ stringToAnalyze.charAt(parseIndex+1) + stringToAnalyze.charAt(parseIndex+2) + stringToAnalyze.charAt(parseIndex+3)+"'";
				if (processSearchResult(db.unrecognizedKanji(searchPhrase),0)) return betterMatchFound = true; //prefer long results 
				searchPhrase = "'?"+ stringToAnalyze.charAt(parseIndex+1) + stringToAnalyze.charAt(parseIndex+2) + "'";
				if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),0)) betterMatchFound = true;
			} 
			// like ?**
			else if (stringToAnalyze.length() - parseIndex == 3){
				searchPhrase = "'?"+ stringToAnalyze.charAt(parseIndex+1) + stringToAnalyze.charAt(parseIndex+2) + "'";
				if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),0)) betterMatchFound = true;
			}

			// like ***? and **?
			if (parseIndex>2){
				searchPhrase = "'"+stringToAnalyze.substring(parseIndex-3,parseIndex)+"?'";
				if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),4)) return betterMatchFound = true; //prefer long results 
				searchPhrase = "'"+stringToAnalyze.substring(parseIndex-2,parseIndex)+"?'";
				if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),3)) betterMatchFound = true;
			} 
			// like **?
			else if (parseIndex==2){
				searchPhrase = "'"+stringToAnalyze.substring(parseIndex-2,parseIndex)+"?'";
				if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),3)) betterMatchFound = true;
			}

			// like *?** and *?*
			if (parseIndex>0 && stringToAnalyze.length()-parseIndex>2){
				searchPhrase = "'"+ stringToAnalyze.charAt(parseIndex-1) + "?" + stringToAnalyze.charAt(parseIndex+1) + stringToAnalyze.charAt(parseIndex+2)+"'";
				if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),1)) return betterMatchFound = true; //prefer long results 
				searchPhrase = "'"+ stringToAnalyze.charAt(parseIndex-1) + "?" + stringToAnalyze.charAt(parseIndex+1) + "'";
				if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),1)) betterMatchFound = true;
			} 
			// like *?*
			else if ((parseIndex>0 && stringToAnalyze.length()-parseIndex==2)){
				searchPhrase = "'"+ stringToAnalyze.charAt(parseIndex-1) + "?" + stringToAnalyze.charAt(parseIndex+1) + "'";
				if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),1)) betterMatchFound = true;
			}

			//final resort - search for word of two kanji which contain the unrecognized symbol
			if (!betterMatchFound){
				// like *? or ?*
				if (shouldContinue && parseIndex>0 && stringToAnalyze.length()-parseIndex>1){
					cursor = db.lastResort(searchPhrase);
					if (cursor.moveToFirst()){
						bestResult = cursor.getInt(cursor.getColumnIndex("freq"));
						String result = cursor.getString(cursor.getColumnIndex("word"));
						if (result.charAt(0) == stringToAnalyze.charAt(parseIndex-1)) replacementChar =  result.charAt(1);
						else replacementChar =  result.charAt(0);
						return betterMatchFound = true;
					} return false;
				}
				//like *?
				else if (parseIndex>0){
					searchPhrase = "'" + stringToAnalyze.charAt(parseIndex-1) + "?" + "'";
					if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),2)) return betterMatchFound = true;
				} 
				// like ?*
				else if (stringToAnalyze.length()-parseIndex>1){
					searchPhrase = "'" + "?" + stringToAnalyze.charAt(parseIndex+1) +"'";
					if (shouldContinue && processSearchResult(db.unrecognizedKanji(searchPhrase),2)) return betterMatchFound = true;
				}
			}
			return betterMatchFound;
		} 
		return false;
	}

	/**
	 * Parses the current string starting from @currentIndex and decreasing length from the right
	 * @param limiter - the maximum length of the string
	 * @param exactMatch - true if searching for exact matches, false for similar matches
	 * @return true if result found, word will be automatically adds to the words resul list
	 */
	private boolean parse(int limiter, boolean exactMatch){
		int absoluteWordLimit = stringToAnalyze.length() -1;
		int punctuationSignIndex = 0;
		//check for any punctuation marks inside and marks the absolute maximum length for a word		
		for (int i = parseIndex; i< stringToAnalyze.length(); i++){
			if (punctuationSigns.indexOf(stringToAnalyze.charAt(i))!=-1){
				if (i<limiter) limiter = i;
				punctuationSignIndex = i;
				break;
			}
		}
		if (punctuationSignIndex != 0) absoluteWordLimit = punctuationSignIndex;

		//		//if this is a long word - search first in the hashmap for exact matches
		//		if (limiter - parseIndex >=Constant.LONG_WORDS_MIN_LENGTH){
		//			while (limiter - parseIndex >=Constant.LONG_WORDS_MIN_LENGTH){
		//				if (longWords.containsKey(stringToAnalyze.substring(parseIndex, limiter+1))){
		//					String wordFound = stringToAnalyze.substring(parseIndex, limiter+1);
		//					QuickLookupResult wordFoundInfo = longWords.get(wordFound);
		//					//GENERATE VARIATIONS AND REMATCH TODO
		//					parseIndex += wordFound.length() - 1;
		//					return true;
		//				}
		//				limiter--;
		//			}
		//		}

		//parse a non long word
		while (limiter >= parseIndex && shouldContinue){

			String chunk = stringToAnalyze.substring(parseIndex, limiter+1);
			if (exactMatch) cursor = db.quickLookUp(chunk);
			else cursor = db.lastResort(chunk);
			if (cursor!= null && cursor.getCount()>0){
				//word object to save the best result
				Word wordToAdd = null;

				while (cursor.moveToNext()){ 
					//there may be several results - save the first one (wordToAdd will be null in this case)
					if (wordToAdd == null) {
						wordToAdd = new Word(
								stringToAnalyze.substring(parseIndex, limiter+1),
								cursor.getString(cursor.getColumnIndex("speech_part")),
								null,
								stringToAnalyze.substring(parseIndex, limiter+1),
								cursor.getString(cursor.getColumnIndex("best_guess")),
								cursor.getString(cursor.getColumnIndex("meaning")));
					} 

					//generate the variations of the detected word
					if (generateWordVariations(wordToAdd, absoluteWordLimit)){
						//iterate through the generation variation
						for (Variation oneVaration : variations){
							//if we have a match
							if (stringToAnalyze.substring(parseIndex, parseIndex + oneVaration.getForm().length() + 1).equalsIgnoreCase(oneVaration.getForm())){
								//if it is longer than the one we already have
								if (oneVaration.getForm().length() >  wordToAdd.getWord().length()){
									wordToAdd.setWord(oneVaration.getForm());
									wordToAdd.setPartOfSpeech(cursor.getString(cursor.getColumnIndex("speech_part")));
									wordToAdd.setTranslation(cursor.getString(cursor.getColumnIndex("best_guess")));
								}
								break;
							}
						}
					}
				}

				//add the word to the results
				addWordToList(wordToAdd);

				return true;
			} else {
				limiter--;
			}
		}
		return false;
	}

	/**
	 * A custom class for comparing Variation type of objects, based on their length.
	 */
	private class VariatonsComparator implements Comparator<Variation>{
		@Override
		public int compare(Variation lhs, Variation rhs) {
			return lhs.getForm().length() - rhs.getForm().length();
		}
	}

	/**
	 * Generates different variations of a word taken from its dictionary form, based on the type of speech, eg
	 * verb, noun, adj etc. This is necessary because the database stores only the dictionary form and not any 
	 * of the many variations of a word. 
	 * @param foundWord the word for which we are searching variations. Part of speech and dictionary_form
	 * (equivalent) to the "word" column from the DB should not be null
	 * @param wordLimit the maximum length which we are searching for, for example the length of the word, or any 
	 * punctuation marks denoting the end of a word
	 * @return true if any variations where constructed for the word. the results are stored in the global @variations
	 */
	private boolean generateWordVariations(Word foundWord, int wordLimit) {
		variations = new ArrayList<Variation>();
		if (foundWord.getPartOfSpeech().matches("^[v]")){
			Verb v = new Verb();
			variations=v.variations(foundWord.getDictionaryForm(), foundWord.getPartOfSpeech());
		} else if (foundWord.getPartOfSpeech().matches("^[an]")){
			AdjectiveNoun an = new AdjectiveNoun();
			variations= an.variations(foundWord.getDictionaryForm(), foundWord.getPartOfSpeech());
		} 

		if (variations != null && variations.size()>0){
			//sort the list by length and remove anything which is above the requried length
			Collections.sort(variations, new VariatonsComparator());
			for (Variation oneVariation : variations){
				if (wordLimit > oneVariation.getForm().length()) variations.remove(oneVariation);
			}
			return true;
		}
		return false;
	}

	/**
	 * Called when there is unrecognized kanji. This typically happens when the kanji consists of left/right particles (han/tsukuri), 
	 * which are recognised as separate symbols. The function will check if there is a match for this and the next symbol in a 
	 * dedicated table in the database and
	 * @return true if a replacement was found. The global @stringToAnalyze will be replaced with the respectful kanji
	 */
	private boolean tryToRecoverCompoundKanji(){
		if (shouldContinue){
			if (parseIndex+1<stringToAnalyze.length()){
				cursor = db.getCompoundKanji(stringToAnalyze.substring(parseIndex, parseIndex+2));
				if (cursor.moveToFirst()){
					stringToAnalyze = new String(stringToAnalyze.replace(stringToAnalyze.substring(parseIndex, parseIndex+2), cursor.getString(cursor.getColumnIndex("kanji"))));
					return true;
				} return false;
			} return false;
		} return false;
	}

	/**
	 * Adds an unrecognized word/character to the results
	 */
	private boolean addUnknownWordToList(String word){
		words.add(new Word(word, "?", null, word, word,word));
		parseIndex ++;
		return true;
	}

	/**
	 * Adds a word to the results list
	 */
	private boolean addWordToList(Word word){
		if (word!=null){
			if (word.getPartOfSpeech().contains("prt") || word.getPartOfSpeech().contains("conj")) {
//Log.d(Constant.TAG,"PARTICLE "+ word.getWord());
				Particle p = new Particle();
				String particle = p.particleMeaning(word.getWord());
				Word w = new Word();
				w.setWord(particle);
				words.add(w);
				parseIndex += word.getWord().length();
				return true;
			} else {
				words.add(word);
				parseIndex += word.getWord().length();
				return true;
			}
		} return false;
	}

	/** Sets the hashmap storing long words to null, so it can be collected by the GC */ 
	public boolean release(){
		longWords = null;
		return true;
	}

	public boolean isShouldContinue() {
		return shouldContinue;
	}

	public void setShouldContinue(boolean shouldContinue) {
		this.shouldContinue = shouldContinue;
	}
}