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

import java.util.ArrayList;
import java.util.Arrays;

/* grand array of all the possible basic tenses of an adjective/noun
 * - plain or polite
 * - positive or negative
 * - all tenses
 */
public class AdjectiveNoun {
  final String[] tenses = {"plain,+,present indicative","plain,-,present indicative","polite,+,present indicative","polite,-,present indicative",
      "plain,+,past indicative","plain,-,past indicative","polite,+,past indicative","polite,-,past indicative",
      "plain,+,provisional","plain,-,provisional",
      "plain,+,conditional","plain,-,conditional"

      // dictionary form
      //"plain,present,can-do", "polite,present,can-do","plain,past,can-do", "polite,past,can-do","plain,present,can't-do", "polite,present,can't-do","plain,past,can't-do", "polite,past,can't-do",
      //"order", "plain,must not", "polite, must not","before doing",

      // universal plain form
      //"plain, explanation", "polite, explanation", "explanation to a request", "hearsay", "interrogative", "whether", "about to just start;now doing;have just finished",
      //"plain,looks like", "plain,doesn't look like", "polite,looks like", "polite,doesn't look like"
  };
  // constructor
  public AdjectiveNoun () {

  }

  /* main function, decides which method to call depending in the adjective's secondary grammatical type
   * attributes:
   * - the secondary type of the adjective
   * - the dictionary form of the adjective
   * returns:
   * - all the possible variations of the adjective in a custom arraylist
   */
  public ArrayList<com.bai.android.data.arcamera.Variation> variations (String wordType, String dictForm) {
    String type = wordType;
    String dict = dictForm;
    // special arraylist for sending back the data
    ArrayList<com.bai.android.data.arcamera.Variation> variations;

    if (type.matches("adj-i")) {
      //i-adjectives
      variations = iAdj(dict);
    } else if(type.matches("adj-na") || type.matches("adj-no") || type.matches("n")) {
      // na-adjectives and nouns
      variations = naAdjNoun(dict);
    } else {
      // misc cases
      variations = misc(type,dict);
    }
    return variations;
  }

  private ArrayList<com.bai.android.data.arcamera.Variation> misc(String verb, String type) {
    // TODO if time is left
    return null;
  }

  // i-adjectives
  private ArrayList<com.bai.android.data.arcamera.Variation> iAdj(String dict) {
    String dictForm = dict;
    String stem = dictForm.substring(0, dictForm.length()-1);
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(stem+"い",tenses[1]),
        new Variation(stem+"くない",tenses[2]),
        new Variation(stem+"いです",tenses[3]),
        new Variation(stem+"くないです",tenses[4]),
        new Variation(stem+"かった",tenses[5]),
        new Variation(stem+"なかった",tenses[6]),
        new Variation(stem+"かったです",tenses[7]),
        new Variation(stem+"くなかったです",tenses[8]),
        new Variation(stem+"ければ",tenses[9]),
        new Variation(stem+"なければ",tenses[10]),
        new Variation(stem+"かったら",tenses[11]),
        new Variation(stem+"なかったら",tenses[12]),
        new Variation(stem+"くて",tenses[13]),
        new Variation(stem,tenses[14])
        ));
    return result;
  }

  // na-adjectives and nouns
  private ArrayList<com.bai.android.data.arcamera.Variation> naAdjNoun(String dict) {
    String dictForm = dict;
    String stem = dictForm;
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(stem+"だ",tenses[1]),
        new Variation(stem+"じゃない",tenses[2]),
        new Variation(stem+"じゃありません",tenses[2]),
        new Variation(stem+"です",tenses[3]),
        new Variation(stem+"ではない",tenses[4]),
        new Variation(stem+"ではありません",tenses[4]),
        new Variation(stem+"だった",tenses[5]),
        new Variation(stem+"じゃなかった",tenses[6]),
        new Variation(stem+"じゃありませんでした",tenses[6]),
        new Variation(stem+"でした",tenses[7]),
        new Variation(stem+"ではありまんでした",tenses[8]),
        new Variation(stem+"ではなかった",tenses[8]),
        new Variation(stem+"なら",tenses[9]),
        new Variation(stem+"ならば",tenses[9]),
        new Variation(stem+"であれば",tenses[9]),
        new Variation(stem+"じゃなければ",tenses[10]),
        new Variation(stem+"でなければ",tenses[10]),
        new Variation(stem+"でないなら",tenses[10]),
        new Variation(stem+"だったら",tenses[11]),
        new Variation(stem+"じゃなかったら",tenses[12]),
        new Variation(stem+"で",tenses[13]),
        new Variation(stem,tenses[14])
        ));
    return result;
  }
}