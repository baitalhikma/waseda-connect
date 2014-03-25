package com.bai.android.data.arcamera;

public class Word {
	
	private String word, partOfSpeech, reading, dictionaryForm, translation, meaning;
	
	public Word(){
	}
	
	public Word(String word, String partOfSpeech, String reading, String dictionaryForm, String translation, String meaning){
		this.word 			= word;
		this.partOfSpeech	= partOfSpeech;
		this.reading		= reading;
		this.dictionaryForm	= dictionaryForm;
		this.translation	= translation;
		this.meaning		= meaning;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getDictionaryForm() {
		return dictionaryForm;
	}

	public void setDictionaryForm(String dictionaryForm) {
		this.dictionaryForm = dictionaryForm;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public String getYomi() {
		return reading;
	}

	public void setReading(String reading) {
		this.reading = reading;
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
}