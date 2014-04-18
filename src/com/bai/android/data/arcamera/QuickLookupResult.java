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