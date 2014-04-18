/**
 * Copyright (c) 2014, Bait Al-Hikma LTD. All rights reserved.
 * 
 * This file is part of Waseda Connect.
 *
 * Waseda Connect is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Waseda Connect is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Waseda Connect. If not, see <http://www.gnu.org/licenses/>.
 */
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