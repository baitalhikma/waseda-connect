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

import java.util.HashMap;
import java.util.Map;

public class Particle {
  private Map<String, String> funcParts = new HashMap<String, String>();
  private Map<String, String> meanParts = new HashMap<String, String>();

  // constructor
  public Particle() {
    funcParts.put("が", "");
    funcParts.put("を", "");
    funcParts.put("をば", "");
    funcParts.put("は", "");

    meanParts.put("下って", "myself");
    meanParts.put("降って", "myself");
    meanParts.put("も", "too");
    meanParts.put("つ", "");
    meanParts.put("もだから", "because");
    meanParts.put("もんだから", "because");
    meanParts.put("から", "because");
    meanParts.put("わいな", "!");
    meanParts.put("わいの", "!");
    meanParts.put("わいのう", "!");
    meanParts.put("い", "!?");
    meanParts.put("か", "?");
    meanParts.put("かい", "or ...?");
    meanParts.put("かいな","??");
    meanParts.put("かえ", "or ...?");
    meanParts.put("かね",", right?");
    meanParts.put("かや", "");
    meanParts.put("かよ", "Really?");
    meanParts.put("がな","");
    meanParts.put("がなあ","");
    meanParts.put("け","?");
    meanParts.put("けぇ", "?");
    meanParts.put("こと", "");
    meanParts.put("ぜ", "!");
    meanParts.put("ぜえ", "");
    meanParts.put("ぞ", "!");
    meanParts.put("ぞい", "");
    meanParts.put("ぞえ", "");
    meanParts.put("たり", "and");
    meanParts.put("だい", "?");
    meanParts.put("だり", "and");
    meanParts.put("っけ","?");
    meanParts.put("って", "");
    meanParts.put("ってば","");
    meanParts.put("て", "");
    meanParts.put("てば","");
    meanParts.put("で", "in");
    meanParts.put("でもって", "by");
    meanParts.put("とは", "in other words");
    meanParts.put("とやら","I heard");
    meanParts.put("どころ","not at all");
    meanParts.put("な", "!");
    meanParts.put("に","at");
    meanParts.put("にて", "in");
    meanParts.put("ね",", right?");
    meanParts.put("ねえ",", right?");
    meanParts.put("ねん", "!");
    meanParts.put("の", "'s");
    meanParts.put("のう", "!");
    meanParts.put("のか", "");
    meanParts.put("のん", "");
    meanParts.put("ば", "");
    meanParts.put("ばい", "!");
    meanParts.put("へ", "to");
    meanParts.put("ヘ", "to");
    meanParts.put("もがな","");
    meanParts.put("ものか","");
    meanParts.put("もんか","");
    meanParts.put("や", "and");
    meanParts.put("やら", "");
    meanParts.put("よ","!");
    meanParts.put("わ", "!");
    meanParts.put("わい", "!");
    meanParts.put("わえ", "!");
    meanParts.put("わよ", "!");
    meanParts.put("し", "and");
    meanParts.put("とか", "or");
    meanParts.put("もので","because");
    meanParts.put("もんで","because");
    meanParts.put("ので","because");
    meanParts.put("のに","instead");
  }
  public String particleMeaning(String particle) {
    String part = particle;
    String meaning="";
    // if particle can be translated into some meaningful word(s) (found among the hashmap keys)
    if (meanParts.containsKey(part)){
      meaning = meanParts.get(part); // get meaning
    } else if (funcParts.containsKey(part)){
      meaning = funcParts.get(part); // get functional stuff TODO
    } else {
      meaning = part;
    }
    return meaning;
  }
}
