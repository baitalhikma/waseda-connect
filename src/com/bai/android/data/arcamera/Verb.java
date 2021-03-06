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

/* grand array of all the possible basic tenses of a verb
 * - plain or polite
 * - positive or negative
 * - all tenses + special cases
 */
public class Verb {
  final String[] tenses = {
      "plain,+,present indicative","plain,-,present indicative","polite,+,present indicative","polite,-,present indicative",
      "plain,+,past indicative","plain,-,past indicative","polite,+,past indicative","polite,-,past indicative",
      "plain,+,presumptive","plain,-,presumptive","polite,+,presumptive","polite,-,presumptive",
      "plain,+,past presumptive","plain,-,past presumptive","polite,+,past presumptive","polite,-,past presumptive",
      "plain,+,progressive","plain,-,progressive","polite,+,progressive","polite,-,progressive",
      "plain,+,past progressive","plain,-,past progressive","polite,+,past progressive","polite,-,past progressive",
      "plain,+,present conditional","plain,-,present conditional","polite,+,present conditional","polite,-,present conditional",
      "plain,+,past conditional","plain,-,past conditional","polite,+,past conditional","polite,-,past conditional",
      "plain,+,potential","plain,-,potential","polite,+,potential","polite,-,potential",
      "plain,+,causative","plain,-,causative","polite,+,causative","polite,-,causative",
      "plain,+,imperative","plain,-,imperative","polite,+,imperative","polite,-,imperative",
      "plain,+,passive","plain,-,passive","polite,+,passive","polite,-,passive",
      "plain,+,causative passive","plain,-,causative passive","polite,+,causative passive","polite,-,causative passive",
      "plain,+,present want to", "plain,-,present want to", "polite,+,present want to", "polite,-,present want to",
      "plain,+,past want to", "plain,-,past want to", "polite,+,past want to", "polite,-,past want to", "plain,+,want to present conditional","plain,-,want to present conditional","plain,+,want to past conditional","plain,-,want to past conditional",
      "humble,+,present","humble,-,present","humble,+,past","humble,-,past","respect,+,request","respect,-,request","humble,+,request","humble,-,request","humble,+,request","humble,-,request","respect,+,present","respect,-,present","respect,+,past","respect,-,past","humble,+,request","humble,-,request",
      "plain,+,present intransitive","plain,-,present intransitive","polite,+,present intransitive","polite,-,present intransitive","plain,+,past intransitive","plain,-,past intransitive","polite,+,past intransitive","polite,-,past intransitive",
      "plain,+,present transitive","plain,-,present transitive","polite,+,present transitive","polite,-,present transitive","plain,+,past transitive","plain,-,past transitive","polite,+,past transitive","polite,-,past transitive",
      "please do"

      //N3 TODO
  };
  // constructor
  public Verb () {

  }

  /* main function, decides which method to call depending in the verb's secondary grammatical type
   * attributes:
   * - the secondary type of the verb
   * - the dictionary form of the verb
   * returns:
   * - all the possible variations of the verb in a custom arraylist
   */
  public ArrayList<Variation> variations (String verbType, String dictForm) {
    String type = verbType;
    String dict = dictForm;
    // special arraylist for sending back the data
    ArrayList<Variation> variations;

    if (type.matches("v5[b|g|k|m|n|r|s|t|u|z]")) {
      // godan verbs (group 1)
      variations = godan(type,dict);
    } else if(type.matches("v5[aru|k-s|u-s|uru|r-i]") || type=="v5") {
      // irregular godan verbs (group 1)
      variations = godanSpec(type,dict);
    } else if(type=="v1" || type=="vz"){
      // ichidan verbs (group 2)
      variations = ichidan(dict,type);
    } else if(type=="vs-s" || type=="vs") {
      // irregular verbs: suru (group 3)
      variations = specialSuru(dict);
    } else if(type=="vk") {
      // irregular verbs: kuru (group 3)
      variations = specialKuru(dict);
    } else {
      // old irregular and archaic verbs
      variations = misc(type,dict);
    }
    return variations;
  }

  private ArrayList<Variation> misc(String type, String dict) {
    // TODO if time is left
    return null;
  }

  // irregulars kuru
  private ArrayList<Variation> specialKuru(String dict) {
    String dictForm = dict;
    String stem = dictForm.substring(0, dictForm.length()-1);
    String teForm = stem+"て";
    String taForm = stem+"た";
    String naForm;
    String iForm = stem;
    if (dictForm.equalsIgnoreCase("くる")) {
      naForm = "こない";
    }
    else {
      naForm = "来ない";
    }
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(dictForm,tenses[1]),
        new Variation(naForm,tenses[2]),
        new Variation(iForm+"ます",tenses[3]),
        new Variation(iForm+"ません",tenses[4]),
        new Variation(taForm,tenses[5]),
        new Variation(naForm.substring(0, naForm.length()-1)+"かった",tenses[6]),
        new Variation(iForm+"ました",tenses[7]),
        new Variation(iForm+"ませんでした",tenses[8]),
        new Variation(naForm.substring(0, naForm.length()-2)+"よう",tenses[9]),
        new Variation(dictForm+"だろう",tenses[9]),
        new Variation(naForm+"だろう",tenses[10]),
        new Variation(iForm+"ましょう",tenses[11]),
        new Variation(dictForm+"でしょう",tenses[11]),
        new Variation(naForm+"でしょう",tenses[12]),
        new Variation(taForm+"だろう",tenses[13]),
        new Variation(taForm+"ろう",tenses[13]),
        new Variation(naForm.substring(0, naForm.length()-1)+"かっただろう",tenses[14]),
        new Variation(taForm+"でしょう",tenses[15]),
        new Variation(naForm.substring(0, naForm.length()-1)+"かったでしょう",tenses[16]),
        new Variation(teForm+"いる",tenses[17]),
        new Variation(teForm+"いない",tenses[18]),
        new Variation(teForm+"います",tenses[19]),
        new Variation(teForm+"いません",tenses[20]),
        new Variation(teForm+"いた",tenses[21]),
        new Variation(teForm+"いなかった",tenses[22]),
        new Variation(teForm+"いました",tenses[23]),
        new Variation(teForm+"いませんでした",tenses[24]),
        new Variation(dictForm.substring(0, naForm.length()-1)+"れば",tenses[25]),
        new Variation(naForm.substring(0, naForm.length()-1)+"ければ",tenses[26]),
        new Variation(dictForm+"なら",tenses[27]),
        new Variation(iForm+"ませんなら",tenses[28]),
        new Variation(taForm+"ら",tenses[29]),
        new Variation(naForm.substring(0, naForm.length()-1)+"かったら",tenses[30]),
        new Variation(iForm+"ましたら",tenses[31]),
        new Variation(iForm+"ませんでしたら",tenses[32]),
        new Variation(naForm.substring(0, naForm.length()-2)+"られる",tenses[33]),
        new Variation(naForm.substring(0, naForm.length()-1)+"られない",tenses[34]),
        new Variation(naForm.substring(0, naForm.length()-1)+"られます",tenses[35]),
        new Variation(naForm.substring(0, naForm.length()-1)+"られません",tenses[36]),
        new Variation(naForm.substring(0, naForm.length()-1)+"させる",tenses[37]),
        new Variation(naForm.substring(0, naForm.length()-1)+"させない",tenses[38]),
        new Variation(naForm.substring(0, naForm.length()-1)+"させます",tenses[39]),
        new Variation(naForm.substring(0, naForm.length()-1)+"させません",tenses[40]),
        new Variation(naForm.substring(0, naForm.length()-2)+"い",tenses[41]),
        new Variation(dictForm+"な",tenses[42]),
        new Variation(iForm+"ください",tenses[43]),
        new Variation(naForm+"でください",tenses[44]),
        new Variation(naForm.substring(0, naForm.length()-2)+"られる",tenses[45]),
        new Variation(naForm.substring(0, naForm.length()-2)+"られない",tenses[46]),
        new Variation(naForm.substring(0, naForm.length()-2)+"られます",tenses[47]),
        new Variation(naForm.substring(0, naForm.length()-2)+"られません",tenses[48]),
        new Variation(naForm.substring(0, naForm.length()-2)+"させられる",tenses[49]),
        new Variation(naForm.substring(0, naForm.length()-2)+"させられない",tenses[50]),
        new Variation(naForm.substring(0, naForm.length()-2)+"させられます",tenses[51]),
        new Variation(naForm.substring(0, naForm.length()-2)+"させられません",tenses[52]),
        new Variation(iForm+"たい",tenses[53]),
        new Variation(iForm+"たくない",tenses[54]),
        new Variation(iForm+"たいです",tenses[55]),
        new Variation(iForm+"たくないです",tenses[56]),
        new Variation(iForm+"たかった",tenses[57]),
        new Variation(iForm+"たなかった",tenses[58]),
        new Variation(iForm+"たかったら",tenses[59]),
        new Variation(iForm+"たかったです",tenses[60]),
        new Variation(iForm+"たなかったです",tenses[61]),
        new Variation(iForm+"たければ",tenses[62]),
        new Variation(iForm+"たなければ",tenses[63]),
        new Variation(iForm+"たかったら",tenses[64]),
        new Variation(iForm+"たなかったら",tenses[65]),
        new Variation("お"+iForm+"します",tenses[66]),
        new Variation("お"+iForm+"しません",tenses[67]),
        new Variation("お"+iForm+"しました",tenses[68]),
        new Variation("お"+iForm+"しませんでした",tenses[69]),
        new Variation("お"+iForm+"してください",tenses[70]),
        new Variation("お"+iForm+"して下さい",tenses[70]),
        new Variation("お"+naForm+"でください",tenses[71]),
        new Variation("お"+naForm+"で下さい",tenses[71]),
        new Variation("お"+iForm+"ください",tenses[72]),
        new Variation("お"+iForm+"下さい",tenses[72]),
        new Variation("お"+iForm+"になります",tenses[73]),
        new Variation("お"+iForm+"になりません",tenses[74]),
        new Variation("お"+iForm+"になりました",tenses[75]),
        new Variation("お"+iForm+"になりませんでした",tenses[76]),
        new Variation("お"+iForm+"になってください",tenses[77]),
        new Variation("お"+iForm+"になって下さい",tenses[77]),
        new Variation("お"+iForm+"にならないでください",tenses[78]),
        new Variation("お"+iForm+"にならないで下さい",tenses[78]),
        new Variation(teForm+"ある",tenses[79]),
        new Variation(teForm+"ない",tenses[80]),
        new Variation(teForm+"あります",tenses[81]),
        new Variation(teForm+"ありません",tenses[82]),
        new Variation(teForm+"あった",tenses[83]),
        new Variation(teForm+"なかった",tenses[84]),
        new Variation(teForm+"ありました",tenses[85]),
        new Variation(teForm+"ありませんでした",tenses[86]),
        new Variation(teForm+"いる",tenses[87]),
        new Variation(teForm+"いない",tenses[88]),
        new Variation(teForm+"います",tenses[89]),
        new Variation(teForm+"いません",tenses[90]),
        new Variation(teForm+"いた",tenses[91]),
        new Variation(teForm+"いなかった",tenses[92]),
        new Variation(teForm+"いました",tenses[93]),
        new Variation(teForm+"いませんでした",tenses[94]),
        new Variation(iForm+"なさい",tenses[95])
        ));
    return result;
  }
  // irregulars ｓuru
  private ArrayList<Variation> specialSuru(String dict) {
    String dictForm = dict;
    // "stem" needed for the 'noun+suru' e.g.: benkyou suru
    String stem = dictForm.substring(0, dictForm.length()-2);
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(stem+"する",tenses[1]),
        new Variation(stem+"しない",tenses[2]),
        new Variation(stem+"します",tenses[3]),
        new Variation(stem+"しません",tenses[4]),
        new Variation(stem+"した",tenses[5]),
        new Variation(stem+"しなかった",tenses[6]),
        new Variation(stem+"しました",tenses[7]),
        new Variation(stem+"しませんでした",tenses[8]),
        new Variation(stem+"しよう",tenses[9]),
        new Variation(stem+"するだろう",tenses[9]),
        new Variation(stem+"しないだろう",tenses[10]),
        new Variation(stem+"しましょう",tenses[11]),
        new Variation(stem+"しるでしょう",tenses[11]),
        new Variation(stem+"しないでしょう",tenses[12]),
        new Variation(stem+"しただろう",tenses[13]),
        new Variation(stem+"したろう",tenses[13]),
        new Variation(stem+"しなかっただろう",tenses[14]),
        new Variation(stem+"しましたろう",tenses[15]),
        new Variation(stem+"しなかったでしょう",tenses[16]),
        new Variation(stem+"している",tenses[17]),
        new Variation(stem+"していない",tenses[18]),
        new Variation(stem+"しています",tenses[19]),
        new Variation(stem+"していません",tenses[20]),
        new Variation(stem+"していた",tenses[21]),
        new Variation(stem+"していなかった",tenses[22]),
        new Variation(stem+"していました",tenses[23]),
        new Variation(stem+"していませんでした",tenses[24]),
        new Variation(stem+"すれば",tenses[25]),
        new Variation(stem+"しなければ",tenses[26]),
        new Variation(stem+"するなら",tenses[27]),
        new Variation(stem+"しないなら",tenses[28]),
        new Variation(stem+"したら",tenses[29]),
        new Variation(stem+"しなかったら",tenses[30]),
        new Variation(stem+"しましたら",tenses[31]),
        new Variation(stem+"しませんでしたら",tenses[32]),
        new Variation(stem+"できる",tenses[33]),
        new Variation(stem+"できない",tenses[34]),
        new Variation(stem+"できます",tenses[35]),
        new Variation(stem+"できません",tenses[36]),
        new Variation(stem+"させる",tenses[37]),
        new Variation(stem+"させない",tenses[38]),
        new Variation(stem+"させます",tenses[39]),
        new Variation(stem+"させません",tenses[40]),
        new Variation(stem+"しろ",tenses[41]),
        new Variation(stem+"するな",tenses[42]),
        new Variation(stem+"してください",tenses[43]),
        new Variation(stem+"しないでください",tenses[44]),
        new Variation(stem+"される",tenses[45]),
        new Variation(stem+"されない",tenses[46]),
        new Variation(stem+"されます",tenses[47]),
        new Variation(stem+"されません",tenses[48]),
        new Variation(stem+"させられる",tenses[49]),
        new Variation(stem+"させられない",tenses[50]),
        new Variation(stem+"させられます",tenses[51]),
        new Variation(stem+"させられません",tenses[52]),
        new Variation(stem+"したい",tenses[53]),
        new Variation(stem+"したくない",tenses[54]),
        new Variation(stem+"したいです",tenses[55]),
        new Variation(stem+"したくないです",tenses[56]),
        new Variation(stem+"したかった",tenses[57]),
        new Variation(stem+"したなかった",tenses[58]),
        new Variation(stem+"したかったら",tenses[59]),
        new Variation(stem+"したかったです",tenses[60]),
        new Variation(stem+"したなかったです",tenses[61]),
        new Variation(stem+"したければ",tenses[62]),
        new Variation(stem+"したなければ",tenses[63]),
        new Variation(stem+"したかったら",tenses[64]),
        new Variation(stem+"したなかったら",tenses[65]),
        new Variation(stem+"いたします",tenses[66]),
        new Variation(stem+"いたしません",tenses[67]),
        new Variation(stem+"いたししました",tenses[68]),
        new Variation(stem+"いたしませんでした",tenses[69]),
        new Variation(stem+"致します",tenses[66]),
        new Variation(stem+"致しません",tenses[67]),
        new Variation(stem+"致しました",tenses[68]),
        new Variation(stem+"致しませんでした",tenses[69]),
        new Variation(stem+"なさいます",tenses[73]),
        new Variation(stem+"なさいません",tenses[74]),
        new Variation(stem+"なさいました",tenses[75]),
        new Variation(stem+"なさいませんでした",tenses[76]),
        new Variation(stem+"なさってください",tenses[77]),
        new Variation(stem+"なさって下さい",tenses[77]),
        new Variation(stem+"なさないでください",tenses[78]),
        new Variation(stem+"なさないで下さい",tenses[78]),
        new Variation(stem+"為さいます",tenses[73]),
        new Variation(stem+"為さいません",tenses[74]),
        new Variation(stem+"為さいました",tenses[75]),
        new Variation(stem+"為さいませんでした",tenses[76]),
        new Variation(stem+"為さってください",tenses[77]),
        new Variation(stem+"為さって下さい",tenses[77]),
        new Variation(stem+"為さないでください",tenses[78]),
        new Variation(stem+"為さないで下さい",tenses[78]),
        new Variation(stem+"してある",tenses[79]),
        new Variation(stem+"してない",tenses[80]),
        new Variation(stem+"してあります",tenses[81]),
        new Variation(stem+"してありません",tenses[82]),
        new Variation(stem+"してあった",tenses[83]),
        new Variation(stem+"してなかった",tenses[84]),
        new Variation(stem+"してありました",tenses[85]),
        new Variation(stem+"してありませんでした",tenses[86]),
        new Variation(stem+"している",tenses[87]),
        new Variation(stem+"していない",tenses[88]),
        new Variation(stem+"しています",tenses[89]),
        new Variation(stem+"していません",tenses[90]),
        new Variation(stem+"していた",tenses[91]),
        new Variation(stem+"していなかった",tenses[92]),
        new Variation(stem+"していました",tenses[93]),
        new Variation(stem+"していませんでした",tenses[94]),
        new Variation(stem+"しなさい",tenses[95])
        ));
    return result;
  }

  private ArrayList<Variation> godanSpec(String type, String dict) {
    // TODO if time is left
    return null;
  }

  // ichidan
  private ArrayList<Variation> ichidan(String dict, String type) {
    String dictForm = dict;
    String verbType = type;
    String stem = dictForm.substring(0, dictForm.length()-1);
    String teForm = stem+"て";
    String taForm = stem+"た";
    String naForm = stem+"ない";
    String iForm = stem;
    // building the array
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(dictForm,tenses[1]),
        new Variation(naForm,tenses[2]),
        new Variation(iForm+"ます",tenses[3]),
        new Variation(iForm+"ません",tenses[4]),
        new Variation(taForm,tenses[5]),
        new Variation(naForm.substring(0, naForm.length()-1)+"かった",tenses[6]),
        new Variation(iForm+"ました",tenses[7]),
        new Variation(iForm+"ませんでした",tenses[8]),
        new Variation(iForm+"よう",tenses[9]),
        new Variation(dictForm+"だろう",tenses[9]),
        new Variation(naForm+"だろう",tenses[10]),
        new Variation(iForm+"ましょう",tenses[11]),
        new Variation(dictForm+"でしょう",tenses[11]),
        new Variation(naForm+"でしょう",tenses[12]),
        new Variation(taForm+"だろう",tenses[13]),
        new Variation(taForm+"ろう",tenses[13]),
        new Variation(naForm.substring(0, naForm.length()-1)+"かっただろう",tenses[14]),
        new Variation(taForm+"でしょう",tenses[15]),
        new Variation(naForm.substring(0, naForm.length()-1)+"かったでしょう",tenses[16]),
        new Variation(teForm+"いる",tenses[17]),
        new Variation(teForm+"いない",tenses[18]),
        new Variation(teForm+"います",tenses[19]),
        new Variation(teForm+"いません",tenses[20]),
        new Variation(teForm+"いた",tenses[21]),
        new Variation(teForm+"いなかった",tenses[22]),
        new Variation(teForm+"いました",tenses[23]),
        new Variation(teForm+"いませんでした",tenses[24]),
        new Variation(iForm+"れば",tenses[25]),
        new Variation(naForm.substring(0, naForm.length()-1)+"ければ",tenses[26]),
        new Variation(iForm+"ますなら",tenses[27]),
        new Variation(iForm+"ませんなら",tenses[28]),
        new Variation(taForm+"ら",tenses[29]),
        new Variation(naForm.substring(0, naForm.length()-1)+"かったら",tenses[30]),
        new Variation(iForm+"ましたら",tenses[31]),
        new Variation(iForm+"ませんでしたら",tenses[32]),
        new Variation(iForm+"られる",tenses[33]),
        new Variation(iForm+"られない",tenses[34]),
        new Variation(iForm+"られます",tenses[35]),
        new Variation(iForm+"られません",tenses[36]),
        new Variation(iForm+"させる",tenses[37]),
        new Variation(iForm+"させない",tenses[38]),
        new Variation(iForm+"させます",tenses[39]),
        new Variation(iForm+"させません",tenses[40]),
        new Variation(iForm+"ろ",tenses[41]),
        new Variation(dictForm+"な",tenses[42]),
        new Variation(teForm+"ください",tenses[43]),
        new Variation(naForm+"でください",tenses[44]),
        new Variation(iForm+"られる",tenses[45]),
        new Variation(iForm+"られない",tenses[46]),
        new Variation(iForm+"られます",tenses[47]),
        new Variation(iForm+"られません",tenses[48]),
        new Variation(iForm+"させられる",tenses[49]),
        new Variation(iForm+"させられない",tenses[50]),
        new Variation(iForm+"させられます",tenses[51]),
        new Variation(iForm+"させられません",tenses[52]),
        new Variation(iForm+"たい",tenses[53]),
        new Variation(iForm+"たくない",tenses[54]),
        new Variation(iForm+"たいです",tenses[55]),
        new Variation(iForm+"たくないです",tenses[56]),
        new Variation(iForm+"たかった",tenses[57]),
        new Variation(iForm+"たなかった",tenses[58]),
        new Variation(iForm+"たかったら",tenses[59]),
        new Variation(iForm+"たかったです",tenses[60]),
        new Variation(iForm+"たなかったです",tenses[61]),
        new Variation(iForm+"たければ",tenses[62]),
        new Variation(iForm+"たなければ",tenses[63]),
        new Variation(iForm+"たかったら",tenses[64]),
        new Variation(iForm+"たなかったら",tenses[65]),
        // honorific TODO
        new Variation("お"+iForm+"します",tenses[66]),
        new Variation("お"+iForm+"しません",tenses[67]),
        new Variation("お"+iForm+"しました",tenses[68]),
        new Variation("お"+iForm+"しませんでした",tenses[69]),
        new Variation("お"+iForm+"してください",tenses[70]),
        new Variation("お"+iForm+"して下さい",tenses[70]),
        new Variation("お"+naForm+"でください",tenses[71]),
        new Variation("お"+naForm+"で下さい",tenses[71]),
        new Variation("お"+iForm+"ください",tenses[72]),
        new Variation("お"+iForm+"下さい",tenses[72]),
        new Variation("お"+iForm+"になります",tenses[73]),
        new Variation("お"+iForm+"になりません",tenses[74]),
        new Variation("お"+iForm+"になりました",tenses[75]),
        new Variation("お"+iForm+"になりませんでした",tenses[76]),
        new Variation("お"+iForm+"になってください",tenses[77]),
        new Variation("お"+iForm+"になって下さい",tenses[77]),
        new Variation("お"+iForm+"にならないでください",tenses[78]),
        new Variation("お"+iForm+"にならないで下さい",tenses[78]),
        new Variation(iForm+"なさい",tenses[95])
        ));
    if(verbType.matches(",vi")){
      result.add(new Variation(teForm+"いる",tenses[87]));
      result.add(new Variation(teForm+"いない",tenses[88]));
      result.add(new Variation(teForm+"います",tenses[89]));
      result.add(new Variation(teForm+"いません",tenses[90]));
      result.add(new Variation(teForm+"いた",tenses[91]));
      result.add(new Variation(teForm+"いなかった",tenses[92]));
      result.add(new Variation(teForm+"いました",tenses[93]));
      result.add(new Variation(teForm+"いませんでした",tenses[94]));
    } else if (verbType.matches(",vt")){
      result.add(new Variation(teForm+"ある",tenses[79]));
      result.add(new Variation(teForm+"ない",tenses[80]));
      result.add(new Variation(teForm+"あります",tenses[81]));
      result.add(new Variation(teForm+"ありません",tenses[82]));
      result.add(new Variation(teForm+"あった",tenses[83]));
      result.add(new Variation(teForm+"なかった",tenses[84]));
      result.add(new Variation(teForm+"ありました",tenses[85]));
      result.add(new Variation(teForm+"ありませんでした",tenses[86]));
    } else {
      // no transitive or intransitive usage
    }
    return result;
  }

  // godan
  private ArrayList<Variation> godan(String type, String dict) {
    String dictForm = dict;
    String verbType = type;
    String stem = dictForm.substring(0, dictForm.length()-1);
    String [] conjugation = new String[5];
    // conjugation variations in godan
    // b|g|k|m|n|r|s|t|u|z
    if (verbType.matches("v5b")) {
      conjugation[0]="ば";
      conjugation[1]="び";
      conjugation[2]="べ";
      conjugation[3]="ぼ";
      conjugation[4]="ぶ";
    } else if(type.matches("v5g")) {
      conjugation[0]="が";
      conjugation[1]="ぎ";
      conjugation[2]="げ";
      conjugation[3]="ご";
      conjugation[4]="ぐ";
    } else if(type.matches("v5k")) {
      conjugation[0]="か";
      conjugation[1]="き";
      conjugation[2]="け";
      conjugation[3]="こ";
      conjugation[4]="く";
    } else if(type.matches("v5m")) {
      conjugation[0]="ま";
      conjugation[1]="み";
      conjugation[2]="め";
      conjugation[3]="も";
      conjugation[4]="む";
    } else if(type.matches("v5n")) {
      conjugation[0]="な";
      conjugation[1]="に";
      conjugation[2]="ね";
      conjugation[3]="の";
      conjugation[4]="ぬ";
    } else if(type.matches("v5r")) {
      conjugation[0]="ら";
      conjugation[1]="り";
      conjugation[2]="れ";
      conjugation[3]="ろ";
      conjugation[4]="る";
    } else if(type.matches("v5t")) {
      conjugation[0]="た";
      conjugation[1]="ち";
      conjugation[2]="て";
      conjugation[3]="と";
      conjugation[4]="つ";
    } else if(type.matches("v5u")) {
      conjugation[0]="わ";
      conjugation[1]="い";
      conjugation[2]="え";
      conjugation[3]="お";
      conjugation[4]="う";
    } else if(type.matches("v5z")) {
      conjugation[0]="ざ";
      conjugation[1]="じ";
      conjugation[2]="ぜ";
      conjugation[3]="ぞ";
      conjugation[4]="ず";
    } else if(type.matches("v5s")) {
      conjugation[0]="さ";
      conjugation[1]="し";
      conjugation[2]="せ";
      conjugation[3]="そ";
      conjugation[4]="す";
    } else {
      conjugation[0]="";
      conjugation[1]="";
      conjugation[2]="";
      conjugation[3]="";
      conjugation[4]="";
    }
    // building the arraylist
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(stem+conjugation[4],tenses[1]),
        new Variation(stem+conjugation[0]+"ない",tenses[2]),
        new Variation(stem+conjugation[1]+"ます",tenses[3]),
        new Variation(stem+conjugation[1]+"ません",tenses[4]),
        // past indicative, positive, plain
        new Variation(stem+conjugation[0]+"なかった",tenses[6]),
        new Variation(stem+conjugation[1]+"ました",tenses[7]),
        new Variation(stem+conjugation[1]+"ませんでした",tenses[8]),
        new Variation(stem+conjugation[3]+"う",tenses[9]),
        new Variation(stem+conjugation[4]+"だろう",tenses[9]),
        new Variation(stem+conjugation[0]+"ないだろう",tenses[10]),
        new Variation(stem+conjugation[1]+"ましょう",tenses[11]),
        new Variation(stem+conjugation[4]+"でしょう",tenses[11]),
        new Variation(stem+conjugation[0]+"ないでしょう",tenses[12]),
        // past presumptive, positive, plain
        new Variation(stem+conjugation[0]+"なかっただろう",tenses[14]),
        new Variation(stem+conjugation[1]+"ましたろう",tenses[15]),
        new Variation(stem+conjugation[0]+"なかったでしょう",tenses[16]),
        // te-forms
        new Variation(stem+conjugation[2]+"ば",tenses[25]),
        new Variation(stem+conjugation[0]+"なければ",tenses[26]),
        new Variation(stem+conjugation[4]+"なら",tenses[27]),
        new Variation(stem+conjugation[0]+"ないなら",tenses[28]),
        // past conditional, positive, plain
        new Variation(stem+conjugation[0]+"なかったら",tenses[30]),
        new Variation(stem+conjugation[1]+"ましたら",tenses[31]),
        new Variation(stem+conjugation[1]+"ませんでしたら",tenses[32]),
        new Variation(stem+conjugation[2]+"る",tenses[33]),
        new Variation(stem+conjugation[2]+"ない",tenses[34]),
        new Variation(stem+conjugation[2]+"ます",tenses[35]),
        new Variation(stem+conjugation[2]+"ません",tenses[36]),
        new Variation(stem+conjugation[0]+"せる",tenses[37]),
        new Variation(stem+conjugation[0]+"せない",tenses[38]),
        new Variation(stem+conjugation[0]+"せます",tenses[39]),
        new Variation(stem+conjugation[0]+"せません",tenses[40]),
        new Variation(stem+conjugation[2],tenses[41]),
        new Variation(stem+conjugation[4]+"な",tenses[42]),
        // tekudasai, positive
        new Variation(stem+conjugation[0]+"ないでください",tenses[44]),
        new Variation(stem+conjugation[0]+"ないで下さい",tenses[44]),
        new Variation(stem+conjugation[0]+"れる",tenses[45]),
        new Variation(stem+conjugation[0]+"れない",tenses[46]),
        new Variation(stem+conjugation[0]+"れます",tenses[47]),
        new Variation(stem+conjugation[0]+"れません",tenses[48]),
        new Variation(stem+conjugation[0]+"せられる",tenses[49]),
        new Variation(stem+conjugation[0]+"せられない",tenses[50]),
        new Variation(stem+conjugation[0]+"せられます",tenses[51]),
        new Variation(stem+conjugation[0]+"せられません",tenses[52])
        ));

    // godan irregularities of te- and ta-form
    String taForm="";
    String teForm="";
    String naForm = stem+conjugation[0]+"ない";
    String iForm = stem+conjugation[1];

    if(verbType.matches("v5[b|n|m]")){
      taForm = stem+"んだ";
      teForm = stem+"んで";
    } else if(verbType.matches("v5[u|t|r]")) {
      taForm = stem+"った";
      teForm = stem+"って";
    } else if(verbType.matches("v5k")) {
      taForm = stem+"いた";
      teForm = stem+"いて";
    } else if(verbType.matches("v5g")) {
      taForm = stem+"いだ";
      teForm = stem+"いで";
    } else if(verbType.matches("v5s")) {
      taForm = stem+"した";
      teForm = stem+"して";
    }
    // passive causative v2 for all godan verbs except su type
    if (verbType.matches("v5[b|n|m|u|t|r|k|g]")) {
      result.add(new Variation(stem+conjugation[0]+"される",tenses[49]));
      result.add(new Variation(stem+conjugation[0]+"されない",tenses[50]));
      result.add(new Variation(stem+conjugation[0]+"されます",tenses[51]));
      result.add(new Variation(stem+conjugation[0]+"されません",tenses[52]));
    }
    result.add(new Variation(stem+taForm,tenses[5]));
    result.add(new Variation(stem+taForm+"だろう",tenses[13]));
    result.add(new Variation(stem+taForm+"ろう",tenses[13]));
    result.add(new Variation(stem+teForm+"いる",tenses[17]));
    result.add(new Variation(stem+teForm+"いない",tenses[18]));
    result.add(new Variation(stem+teForm+"います",tenses[19]));
    result.add(new Variation(stem+teForm+"いません",tenses[20]));
    result.add(new Variation(stem+teForm+"いた",tenses[21]));
    result.add(new Variation(stem+teForm+"いなかった",tenses[22]));
    result.add(new Variation(stem+teForm+"いました",tenses[23]));
    result.add(new Variation(stem+teForm+"いませんでした",tenses[24]));
    result.add(new Variation(stem+taForm+"ら",tenses[29]));
    result.add(new Variation(stem+teForm+"ください",tenses[43]));
    result.add(new Variation(stem+teForm+"下さい",tenses[43]));
    result.add(new Variation(iForm+"たい",tenses[53]));
    result.add(new Variation(iForm+"たくない",tenses[54]));
    result.add(new Variation(iForm+"たいです",tenses[55]));
    result.add(new Variation(iForm+"たくないです",tenses[56]));
    result.add(new Variation(iForm+"たかった",tenses[57]));
    result.add(new Variation(iForm+"たなかった",tenses[58]));
    result.add(new Variation(iForm+"たかったら",tenses[59]));
    result.add(new Variation(iForm+"たかったです",tenses[60]));
    result.add(new Variation(iForm+"たなかったです",tenses[61]));
    result.add(new Variation(iForm+"たければ",tenses[62]));
    result.add(new Variation(iForm+"たなければ",tenses[63]));
    result.add(new Variation(iForm+"たかったら",tenses[64]));
    result.add(new Variation(iForm+"たなかったら",tenses[65]));
    result.add(new Variation("お"+iForm+"します",tenses[66]));
    result.add(new Variation("お"+iForm+"しません",tenses[67]));
    result.add(new Variation("お"+iForm+"しました",tenses[68]));
    result.add(new Variation("お"+iForm+"しませんでした",tenses[69]));
    result.add(new Variation("お"+iForm+"してください",tenses[70]));
    result.add(new Variation("お"+iForm+"して下さい",tenses[70]));
    result.add(new Variation("お"+naForm+"でください",tenses[71]));
    result.add(new Variation("お"+naForm+"で下さい",tenses[71]));
    result.add(new Variation("お"+iForm+"ください",tenses[72]));
    result.add(new Variation("お"+iForm+"下さい",tenses[72]));
    result.add(new Variation("お"+iForm+"になります",tenses[73]));
    result.add(new Variation("お"+iForm+"になりません",tenses[74]));
    result.add(new Variation("お"+iForm+"になりました",tenses[75]));
    result.add(new Variation("お"+iForm+"になりませんでした",tenses[76]));
    result.add(new Variation("お"+iForm+"になってください",tenses[77]));
    result.add(new Variation("お"+iForm+"になって下さい",tenses[77]));
    result.add(new Variation("お"+iForm+"にならないでください",tenses[78]));
    result.add(new Variation("お"+iForm+"にならないで下さい",tenses[78]));
    result.add(new Variation(iForm+"なさい",tenses[95]));
    // intransitive
    if(verbType.matches(",vi")){
      result.add(new Variation(teForm+"いる",tenses[87]));
      result.add(new Variation(teForm+"いない",tenses[88]));
      result.add(new Variation(teForm+"います",tenses[89]));
      result.add(new Variation(teForm+"いません",tenses[90]));
      result.add(new Variation(teForm+"いた",tenses[91]));
      result.add(new Variation(teForm+"いなかった",tenses[92]));
      result.add(new Variation(teForm+"いました",tenses[93]));
      result.add(new Variation(teForm+"いませんでした",tenses[94]));
      // transitive
    } else if (verbType.matches(",vt")){
      result.add(new Variation(teForm+"ある",tenses[79]));
      result.add(new Variation(teForm+"ない",tenses[80]));
      result.add(new Variation(teForm+"あります",tenses[81]));
      result.add(new Variation(teForm+"ありません",tenses[82]));
      result.add(new Variation(teForm+"あった",tenses[83]));
      result.add(new Variation(teForm+"なかった",tenses[84]));
      result.add(new Variation(teForm+"ありました",tenses[85]));
      result.add(new Variation(teForm+"ありませんでした",tenses[86]));
    } else {
      // no transitive or intransitive usage
    }
    return result;
  }
}