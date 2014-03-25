package com.bai.android.data.arcamera;

/**
 *	Object storing result information for a quick lookup in the database. See quickLookUp() in the DB adapter.
 *	Holds only the sentence part and the best guess meaning. 
 *	- part of speech is necessary to produce wod variations and search for them in the recognition result
 *	- best guess is required for a quick display of the result to the user
 */
public class QuickLookupResult {
	private String speechPart, bestGuess;
	
	public QuickLookupResult(){
	}
	
	public QuickLookupResult(String speechPart, String bestGuess){
		this.speechPart = speechPart;
		this.bestGuess	= bestGuess;
	}

	public String getSpeechPart() {
		return speechPart;
	}

	public void setSpeechPart(String speechPart) {
		this.speechPart = speechPart;
	}

	public String getBestGuess() {
		return bestGuess;
	}

	public void setBestGuess(String bestGuess) {
		this.bestGuess = bestGuess;
	}
}