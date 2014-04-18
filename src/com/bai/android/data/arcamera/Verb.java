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
    String teForm = stem+"‚Ä";
    String taForm = stem+"‚½";
    String naForm;
    String iForm = stem;
    if (dictForm.equalsIgnoreCase("‚­‚é")) {
      naForm = "‚±‚È‚¢";
    }
    else {
      naForm = "—ˆ‚È‚¢";
    }
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(dictForm,tenses[1]),
        new Variation(naForm,tenses[2]),
        new Variation(iForm+"‚Ü‚·",tenses[3]),
        new Variation(iForm+"‚Ü‚¹‚ñ",tenses[4]),
        new Variation(taForm,tenses[5]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚©‚Á‚½",tenses[6]),
        new Variation(iForm+"‚Ü‚µ‚½",tenses[7]),
        new Variation(iForm+"‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[8]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚æ‚¤",tenses[9]),
        new Variation(dictForm+"‚¾‚ë‚¤",tenses[9]),
        new Variation(naForm+"‚¾‚ë‚¤",tenses[10]),
        new Variation(iForm+"‚Ü‚µ‚å‚¤",tenses[11]),
        new Variation(dictForm+"‚Å‚µ‚å‚¤",tenses[11]),
        new Variation(naForm+"‚Å‚µ‚å‚¤",tenses[12]),
        new Variation(taForm+"‚¾‚ë‚¤",tenses[13]),
        new Variation(taForm+"‚ë‚¤",tenses[13]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚©‚Á‚½‚¾‚ë‚¤",tenses[14]),
        new Variation(taForm+"‚Å‚µ‚å‚¤",tenses[15]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚©‚Á‚½‚Å‚µ‚å‚¤",tenses[16]),
        new Variation(teForm+"‚¢‚é",tenses[17]),
        new Variation(teForm+"‚¢‚È‚¢",tenses[18]),
        new Variation(teForm+"‚¢‚Ü‚·",tenses[19]),
        new Variation(teForm+"‚¢‚Ü‚¹‚ñ",tenses[20]),
        new Variation(teForm+"‚¢‚½",tenses[21]),
        new Variation(teForm+"‚¢‚È‚©‚Á‚½",tenses[22]),
        new Variation(teForm+"‚¢‚Ü‚µ‚½",tenses[23]),
        new Variation(teForm+"‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[24]),
        new Variation(dictForm.substring(0, naForm.length()-1)+"‚ê‚Î",tenses[25]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚¯‚ê‚Î",tenses[26]),
        new Variation(dictForm+"‚È‚ç",tenses[27]),
        new Variation(iForm+"‚Ü‚¹‚ñ‚È‚ç",tenses[28]),
        new Variation(taForm+"‚ç",tenses[29]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚©‚Á‚½‚ç",tenses[30]),
        new Variation(iForm+"‚Ü‚µ‚½‚ç",tenses[31]),
        new Variation(iForm+"‚Ü‚¹‚ñ‚Å‚µ‚½‚ç",tenses[32]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚ç‚ê‚é",tenses[33]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚ç‚ê‚È‚¢",tenses[34]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚ç‚ê‚Ü‚·",tenses[35]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚ç‚ê‚Ü‚¹‚ñ",tenses[36]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚³‚¹‚é",tenses[37]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚³‚¹‚È‚¢",tenses[38]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚³‚¹‚Ü‚·",tenses[39]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚³‚¹‚Ü‚¹‚ñ",tenses[40]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚¢",tenses[41]),
        new Variation(dictForm+"‚È",tenses[42]),
        new Variation(iForm+"‚­‚¾‚³‚¢",tenses[43]),
        new Variation(naForm+"‚Å‚­‚¾‚³‚¢",tenses[44]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚ç‚ê‚é",tenses[45]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚ç‚ê‚È‚¢",tenses[46]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚ç‚ê‚Ü‚·",tenses[47]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚ç‚ê‚Ü‚¹‚ñ",tenses[48]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚³‚¹‚ç‚ê‚é",tenses[49]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚³‚¹‚ç‚ê‚È‚¢",tenses[50]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚³‚¹‚ç‚ê‚Ü‚·",tenses[51]),
        new Variation(naForm.substring(0, naForm.length()-2)+"‚³‚¹‚ç‚ê‚Ü‚¹‚ñ",tenses[52]),
        new Variation(iForm+"‚½‚¢",tenses[53]),
        new Variation(iForm+"‚½‚­‚È‚¢",tenses[54]),
        new Variation(iForm+"‚½‚¢‚Å‚·",tenses[55]),
        new Variation(iForm+"‚½‚­‚È‚¢‚Å‚·",tenses[56]),
        new Variation(iForm+"‚½‚©‚Á‚½",tenses[57]),
        new Variation(iForm+"‚½‚È‚©‚Á‚½",tenses[58]),
        new Variation(iForm+"‚½‚©‚Á‚½‚ç",tenses[59]),
        new Variation(iForm+"‚½‚©‚Á‚½‚Å‚·",tenses[60]),
        new Variation(iForm+"‚½‚È‚©‚Á‚½‚Å‚·",tenses[61]),
        new Variation(iForm+"‚½‚¯‚ê‚Î",tenses[62]),
        new Variation(iForm+"‚½‚È‚¯‚ê‚Î",tenses[63]),
        new Variation(iForm+"‚½‚©‚Á‚½‚ç",tenses[64]),
        new Variation(iForm+"‚½‚È‚©‚Á‚½‚ç",tenses[65]),
        new Variation("‚¨"+iForm+"‚µ‚Ü‚·",tenses[66]),
        new Variation("‚¨"+iForm+"‚µ‚Ü‚¹‚ñ",tenses[67]),
        new Variation("‚¨"+iForm+"‚µ‚Ü‚µ‚½",tenses[68]),
        new Variation("‚¨"+iForm+"‚µ‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[69]),
        new Variation("‚¨"+iForm+"‚µ‚Ä‚­‚¾‚³‚¢",tenses[70]),
        new Variation("‚¨"+iForm+"‚µ‚Ä‰º‚³‚¢",tenses[70]),
        new Variation("‚¨"+naForm+"‚Å‚­‚¾‚³‚¢",tenses[71]),
        new Variation("‚¨"+naForm+"‚Å‰º‚³‚¢",tenses[71]),
        new Variation("‚¨"+iForm+"‚­‚¾‚³‚¢",tenses[72]),
        new Variation("‚¨"+iForm+"‰º‚³‚¢",tenses[72]),
        new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚·",tenses[73]),
        new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚¹‚ñ",tenses[74]),
        new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚µ‚½",tenses[75]),
        new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[76]),
        new Variation("‚¨"+iForm+"‚É‚È‚Á‚Ä‚­‚¾‚³‚¢",tenses[77]),
        new Variation("‚¨"+iForm+"‚É‚È‚Á‚Ä‰º‚³‚¢",tenses[77]),
        new Variation("‚¨"+iForm+"‚É‚È‚ç‚È‚¢‚Å‚­‚¾‚³‚¢",tenses[78]),
        new Variation("‚¨"+iForm+"‚É‚È‚ç‚È‚¢‚Å‰º‚³‚¢",tenses[78]),
        new Variation(teForm+"‚ ‚é",tenses[79]),
        new Variation(teForm+"‚È‚¢",tenses[80]),
        new Variation(teForm+"‚ ‚è‚Ü‚·",tenses[81]),
        new Variation(teForm+"‚ ‚è‚Ü‚¹‚ñ",tenses[82]),
        new Variation(teForm+"‚ ‚Á‚½",tenses[83]),
        new Variation(teForm+"‚È‚©‚Á‚½",tenses[84]),
        new Variation(teForm+"‚ ‚è‚Ü‚µ‚½",tenses[85]),
        new Variation(teForm+"‚ ‚è‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[86]),
        new Variation(teForm+"‚¢‚é",tenses[87]),
        new Variation(teForm+"‚¢‚È‚¢",tenses[88]),
        new Variation(teForm+"‚¢‚Ü‚·",tenses[89]),
        new Variation(teForm+"‚¢‚Ü‚¹‚ñ",tenses[90]),
        new Variation(teForm+"‚¢‚½",tenses[91]),
        new Variation(teForm+"‚¢‚È‚©‚Á‚½",tenses[92]),
        new Variation(teForm+"‚¢‚Ü‚µ‚½",tenses[93]),
        new Variation(teForm+"‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[94]),
        new Variation(iForm+"‚È‚³‚¢",tenses[95])
        ));
    return result;
  }
  // irregulars ‚“uru
  private ArrayList<Variation> specialSuru(String dict) {
    String dictForm = dict;
    // "stem" needed for the 'noun+suru' e.g.: benkyou suru
    String stem = dictForm.substring(0, dictForm.length()-2);
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(stem+"‚·‚é",tenses[1]),
        new Variation(stem+"‚µ‚È‚¢",tenses[2]),
        new Variation(stem+"‚µ‚Ü‚·",tenses[3]),
        new Variation(stem+"‚µ‚Ü‚¹‚ñ",tenses[4]),
        new Variation(stem+"‚µ‚½",tenses[5]),
        new Variation(stem+"‚µ‚È‚©‚Á‚½",tenses[6]),
        new Variation(stem+"‚µ‚Ü‚µ‚½",tenses[7]),
        new Variation(stem+"‚µ‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[8]),
        new Variation(stem+"‚µ‚æ‚¤",tenses[9]),
        new Variation(stem+"‚·‚é‚¾‚ë‚¤",tenses[9]),
        new Variation(stem+"‚µ‚È‚¢‚¾‚ë‚¤",tenses[10]),
        new Variation(stem+"‚µ‚Ü‚µ‚å‚¤",tenses[11]),
        new Variation(stem+"‚µ‚é‚Å‚µ‚å‚¤",tenses[11]),
        new Variation(stem+"‚µ‚È‚¢‚Å‚µ‚å‚¤",tenses[12]),
        new Variation(stem+"‚µ‚½‚¾‚ë‚¤",tenses[13]),
        new Variation(stem+"‚µ‚½‚ë‚¤",tenses[13]),
        new Variation(stem+"‚µ‚È‚©‚Á‚½‚¾‚ë‚¤",tenses[14]),
        new Variation(stem+"‚µ‚Ü‚µ‚½‚ë‚¤",tenses[15]),
        new Variation(stem+"‚µ‚È‚©‚Á‚½‚Å‚µ‚å‚¤",tenses[16]),
        new Variation(stem+"‚µ‚Ä‚¢‚é",tenses[17]),
        new Variation(stem+"‚µ‚Ä‚¢‚È‚¢",tenses[18]),
        new Variation(stem+"‚µ‚Ä‚¢‚Ü‚·",tenses[19]),
        new Variation(stem+"‚µ‚Ä‚¢‚Ü‚¹‚ñ",tenses[20]),
        new Variation(stem+"‚µ‚Ä‚¢‚½",tenses[21]),
        new Variation(stem+"‚µ‚Ä‚¢‚È‚©‚Á‚½",tenses[22]),
        new Variation(stem+"‚µ‚Ä‚¢‚Ü‚µ‚½",tenses[23]),
        new Variation(stem+"‚µ‚Ä‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[24]),
        new Variation(stem+"‚·‚ê‚Î",tenses[25]),
        new Variation(stem+"‚µ‚È‚¯‚ê‚Î",tenses[26]),
        new Variation(stem+"‚·‚é‚È‚ç",tenses[27]),
        new Variation(stem+"‚µ‚È‚¢‚È‚ç",tenses[28]),
        new Variation(stem+"‚µ‚½‚ç",tenses[29]),
        new Variation(stem+"‚µ‚È‚©‚Á‚½‚ç",tenses[30]),
        new Variation(stem+"‚µ‚Ü‚µ‚½‚ç",tenses[31]),
        new Variation(stem+"‚µ‚Ü‚¹‚ñ‚Å‚µ‚½‚ç",tenses[32]),
        new Variation(stem+"‚Å‚«‚é",tenses[33]),
        new Variation(stem+"‚Å‚«‚È‚¢",tenses[34]),
        new Variation(stem+"‚Å‚«‚Ü‚·",tenses[35]),
        new Variation(stem+"‚Å‚«‚Ü‚¹‚ñ",tenses[36]),
        new Variation(stem+"‚³‚¹‚é",tenses[37]),
        new Variation(stem+"‚³‚¹‚È‚¢",tenses[38]),
        new Variation(stem+"‚³‚¹‚Ü‚·",tenses[39]),
        new Variation(stem+"‚³‚¹‚Ü‚¹‚ñ",tenses[40]),
        new Variation(stem+"‚µ‚ë",tenses[41]),
        new Variation(stem+"‚·‚é‚È",tenses[42]),
        new Variation(stem+"‚µ‚Ä‚­‚¾‚³‚¢",tenses[43]),
        new Variation(stem+"‚µ‚È‚¢‚Å‚­‚¾‚³‚¢",tenses[44]),
        new Variation(stem+"‚³‚ê‚é",tenses[45]),
        new Variation(stem+"‚³‚ê‚È‚¢",tenses[46]),
        new Variation(stem+"‚³‚ê‚Ü‚·",tenses[47]),
        new Variation(stem+"‚³‚ê‚Ü‚¹‚ñ",tenses[48]),
        new Variation(stem+"‚³‚¹‚ç‚ê‚é",tenses[49]),
        new Variation(stem+"‚³‚¹‚ç‚ê‚È‚¢",tenses[50]),
        new Variation(stem+"‚³‚¹‚ç‚ê‚Ü‚·",tenses[51]),
        new Variation(stem+"‚³‚¹‚ç‚ê‚Ü‚¹‚ñ",tenses[52]),
        new Variation(stem+"‚µ‚½‚¢",tenses[53]),
        new Variation(stem+"‚µ‚½‚­‚È‚¢",tenses[54]),
        new Variation(stem+"‚µ‚½‚¢‚Å‚·",tenses[55]),
        new Variation(stem+"‚µ‚½‚­‚È‚¢‚Å‚·",tenses[56]),
        new Variation(stem+"‚µ‚½‚©‚Á‚½",tenses[57]),
        new Variation(stem+"‚µ‚½‚È‚©‚Á‚½",tenses[58]),
        new Variation(stem+"‚µ‚½‚©‚Á‚½‚ç",tenses[59]),
        new Variation(stem+"‚µ‚½‚©‚Á‚½‚Å‚·",tenses[60]),
        new Variation(stem+"‚µ‚½‚È‚©‚Á‚½‚Å‚·",tenses[61]),
        new Variation(stem+"‚µ‚½‚¯‚ê‚Î",tenses[62]),
        new Variation(stem+"‚µ‚½‚È‚¯‚ê‚Î",tenses[63]),
        new Variation(stem+"‚µ‚½‚©‚Á‚½‚ç",tenses[64]),
        new Variation(stem+"‚µ‚½‚È‚©‚Á‚½‚ç",tenses[65]),
        new Variation(stem+"‚¢‚½‚µ‚Ü‚·",tenses[66]),
        new Variation(stem+"‚¢‚½‚µ‚Ü‚¹‚ñ",tenses[67]),
        new Variation(stem+"‚¢‚½‚µ‚µ‚Ü‚µ‚½",tenses[68]),
        new Variation(stem+"‚¢‚½‚µ‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[69]),
        new Variation(stem+"’v‚µ‚Ü‚·",tenses[66]),
        new Variation(stem+"’v‚µ‚Ü‚¹‚ñ",tenses[67]),
        new Variation(stem+"’v‚µ‚Ü‚µ‚½",tenses[68]),
        new Variation(stem+"’v‚µ‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[69]),
        new Variation(stem+"‚È‚³‚¢‚Ü‚·",tenses[73]),
        new Variation(stem+"‚È‚³‚¢‚Ü‚¹‚ñ",tenses[74]),
        new Variation(stem+"‚È‚³‚¢‚Ü‚µ‚½",tenses[75]),
        new Variation(stem+"‚È‚³‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[76]),
        new Variation(stem+"‚È‚³‚Á‚Ä‚­‚¾‚³‚¢",tenses[77]),
        new Variation(stem+"‚È‚³‚Á‚Ä‰º‚³‚¢",tenses[77]),
        new Variation(stem+"‚È‚³‚È‚¢‚Å‚­‚¾‚³‚¢",tenses[78]),
        new Variation(stem+"‚È‚³‚È‚¢‚Å‰º‚³‚¢",tenses[78]),
        new Variation(stem+"ˆ×‚³‚¢‚Ü‚·",tenses[73]),
        new Variation(stem+"ˆ×‚³‚¢‚Ü‚¹‚ñ",tenses[74]),
        new Variation(stem+"ˆ×‚³‚¢‚Ü‚µ‚½",tenses[75]),
        new Variation(stem+"ˆ×‚³‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[76]),
        new Variation(stem+"ˆ×‚³‚Á‚Ä‚­‚¾‚³‚¢",tenses[77]),
        new Variation(stem+"ˆ×‚³‚Á‚Ä‰º‚³‚¢",tenses[77]),
        new Variation(stem+"ˆ×‚³‚È‚¢‚Å‚­‚¾‚³‚¢",tenses[78]),
        new Variation(stem+"ˆ×‚³‚È‚¢‚Å‰º‚³‚¢",tenses[78]),
        new Variation(stem+"‚µ‚Ä‚ ‚é",tenses[79]),
        new Variation(stem+"‚µ‚Ä‚È‚¢",tenses[80]),
        new Variation(stem+"‚µ‚Ä‚ ‚è‚Ü‚·",tenses[81]),
        new Variation(stem+"‚µ‚Ä‚ ‚è‚Ü‚¹‚ñ",tenses[82]),
        new Variation(stem+"‚µ‚Ä‚ ‚Á‚½",tenses[83]),
        new Variation(stem+"‚µ‚Ä‚È‚©‚Á‚½",tenses[84]),
        new Variation(stem+"‚µ‚Ä‚ ‚è‚Ü‚µ‚½",tenses[85]),
        new Variation(stem+"‚µ‚Ä‚ ‚è‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[86]),
        new Variation(stem+"‚µ‚Ä‚¢‚é",tenses[87]),
        new Variation(stem+"‚µ‚Ä‚¢‚È‚¢",tenses[88]),
        new Variation(stem+"‚µ‚Ä‚¢‚Ü‚·",tenses[89]),
        new Variation(stem+"‚µ‚Ä‚¢‚Ü‚¹‚ñ",tenses[90]),
        new Variation(stem+"‚µ‚Ä‚¢‚½",tenses[91]),
        new Variation(stem+"‚µ‚Ä‚¢‚È‚©‚Á‚½",tenses[92]),
        new Variation(stem+"‚µ‚Ä‚¢‚Ü‚µ‚½",tenses[93]),
        new Variation(stem+"‚µ‚Ä‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[94]),
        new Variation(stem+"‚µ‚È‚³‚¢",tenses[95])
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
    String teForm = stem+"‚Ä";
    String taForm = stem+"‚½";
    String naForm = stem+"‚È‚¢";
    String iForm = stem;
    // building the array
    ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
        new Variation(dictForm,tenses[1]),
        new Variation(naForm,tenses[2]),
        new Variation(iForm+"‚Ü‚·",tenses[3]),
        new Variation(iForm+"‚Ü‚¹‚ñ",tenses[4]),
        new Variation(taForm,tenses[5]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚©‚Á‚½",tenses[6]),
        new Variation(iForm+"‚Ü‚µ‚½",tenses[7]),
        new Variation(iForm+"‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[8]),
        new Variation(iForm+"‚æ‚¤",tenses[9]),
        new Variation(dictForm+"‚¾‚ë‚¤",tenses[9]),
        new Variation(naForm+"‚¾‚ë‚¤",tenses[10]),
        new Variation(iForm+"‚Ü‚µ‚å‚¤",tenses[11]),
        new Variation(dictForm+"‚Å‚µ‚å‚¤",tenses[11]),
        new Variation(naForm+"‚Å‚µ‚å‚¤",tenses[12]),
        new Variation(taForm+"‚¾‚ë‚¤",tenses[13]),
        new Variation(taForm+"‚ë‚¤",tenses[13]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚©‚Á‚½‚¾‚ë‚¤",tenses[14]),
        new Variation(taForm+"‚Å‚µ‚å‚¤",tenses[15]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚©‚Á‚½‚Å‚µ‚å‚¤",tenses[16]),
        new Variation(teForm+"‚¢‚é",tenses[17]),
        new Variation(teForm+"‚¢‚È‚¢",tenses[18]),
        new Variation(teForm+"‚¢‚Ü‚·",tenses[19]),
        new Variation(teForm+"‚¢‚Ü‚¹‚ñ",tenses[20]),
        new Variation(teForm+"‚¢‚½",tenses[21]),
        new Variation(teForm+"‚¢‚È‚©‚Á‚½",tenses[22]),
        new Variation(teForm+"‚¢‚Ü‚µ‚½",tenses[23]),
        new Variation(teForm+"‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[24]),
        new Variation(iForm+"‚ê‚Î",tenses[25]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚¯‚ê‚Î",tenses[26]),
        new Variation(iForm+"‚Ü‚·‚È‚ç",tenses[27]),
        new Variation(iForm+"‚Ü‚¹‚ñ‚È‚ç",tenses[28]),
        new Variation(taForm+"‚ç",tenses[29]),
        new Variation(naForm.substring(0, naForm.length()-1)+"‚©‚Á‚½‚ç",tenses[30]),
        new Variation(iForm+"‚Ü‚µ‚½‚ç",tenses[31]),
        new Variation(iForm+"‚Ü‚¹‚ñ‚Å‚µ‚½‚ç",tenses[32]),
        new Variation(iForm+"‚ç‚ê‚é",tenses[33]),
        new Variation(iForm+"‚ç‚ê‚È‚¢",tenses[34]),
        new Variation(iForm+"‚ç‚ê‚Ü‚·",tenses[35]),
        new Variation(iForm+"‚ç‚ê‚Ü‚¹‚ñ",tenses[36]),
        new Variation(iForm+"‚³‚¹‚é",tenses[37]),
        new Variation(iForm+"‚³‚¹‚È‚¢",tenses[38]),
        new Variation(iForm+"‚³‚¹‚Ü‚·",tenses[39]),
        new Variation(iForm+"‚³‚¹‚Ü‚¹‚ñ",tenses[40]),
        new Variation(iForm+"‚ë",tenses[41]),
        new Variation(dictForm+"‚È",tenses[42]),
        new Variation(teForm+"‚­‚¾‚³‚¢",tenses[43]),
        new Variation(naForm+"‚Å‚­‚¾‚³‚¢",tenses[44]),
        new Variation(iForm+"‚ç‚ê‚é",tenses[45]),
        new Variation(iForm+"‚ç‚ê‚È‚¢",tenses[46]),
        new Variation(iForm+"‚ç‚ê‚Ü‚·",tenses[47]),
        new Variation(iForm+"‚ç‚ê‚Ü‚¹‚ñ",tenses[48]),
        new Variation(iForm+"‚³‚¹‚ç‚ê‚é",tenses[49]),
        new Variation(iForm+"‚³‚¹‚ç‚ê‚È‚¢",tenses[50]),
        new Variation(iForm+"‚³‚¹‚ç‚ê‚Ü‚·",tenses[51]),
        new Variation(iForm+"‚³‚¹‚ç‚ê‚Ü‚¹‚ñ",tenses[52]),
        new Variation(iForm+"‚½‚¢",tenses[53]),
        new Variation(iForm+"‚½‚­‚È‚¢",tenses[54]),
        new Variation(iForm+"‚½‚¢‚Å‚·",tenses[55]),
        new Variation(iForm+"‚½‚­‚È‚¢‚Å‚·",tenses[56]),
        new Variation(iForm+"‚½‚©‚Á‚½",tenses[57]),
        new Variation(iForm+"‚½‚È‚©‚Á‚½",tenses[58]),
        new Variation(iForm+"‚½‚©‚Á‚½‚ç",tenses[59]),
        new Variation(iForm+"‚½‚©‚Á‚½‚Å‚·",tenses[60]),
        new Variation(iForm+"‚½‚È‚©‚Á‚½‚Å‚·",tenses[61]),
        new Variation(iForm+"‚½‚¯‚ê‚Î",tenses[62]),
        new Variation(iForm+"‚½‚È‚¯‚ê‚Î",tenses[63]),
        new Variation(iForm+"‚½‚©‚Á‚½‚ç",tenses[64]),
        new Variation(iForm+"‚½‚È‚©‚Á‚½‚ç",tenses[65]),
        // honorific TODO
        new Variation("‚¨"+iForm+"‚µ‚Ü‚·",tenses[66]),
        new Variation("‚¨"+iForm+"‚µ‚Ü‚¹‚ñ",tenses[67]),
        new Variation("‚¨"+iForm+"‚µ‚Ü‚µ‚½",tenses[68]),
        new Variation("‚¨"+iForm+"‚µ‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[69]),
        new Variation("‚¨"+iForm+"‚µ‚Ä‚­‚¾‚³‚¢",tenses[70]),
        new Variation("‚¨"+iForm+"‚µ‚Ä‰º‚³‚¢",tenses[70]),
        new Variation("‚¨"+naForm+"‚Å‚­‚¾‚³‚¢",tenses[71]),
        new Variation("‚¨"+naForm+"‚Å‰º‚³‚¢",tenses[71]),
        new Variation("‚¨"+iForm+"‚­‚¾‚³‚¢",tenses[72]),
        new Variation("‚¨"+iForm+"‰º‚³‚¢",tenses[72]),
        new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚·",tenses[73]),
        new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚¹‚ñ",tenses[74]),
        new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚µ‚½",tenses[75]),
        new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[76]),
        new Variation("‚¨"+iForm+"‚É‚È‚Á‚Ä‚­‚¾‚³‚¢",tenses[77]),
        new Variation("‚¨"+iForm+"‚É‚È‚Á‚Ä‰º‚³‚¢",tenses[77]),
        new Variation("‚¨"+iForm+"‚É‚È‚ç‚È‚¢‚Å‚­‚¾‚³‚¢",tenses[78]),
        new Variation("‚¨"+iForm+"‚É‚È‚ç‚È‚¢‚Å‰º‚³‚¢",tenses[78]),
        new Variation(iForm+"‚È‚³‚¢",tenses[95])
        ));
    if(verbType.matches(",vi")){
      result.add(new Variation(teForm+"‚¢‚é",tenses[87]));
      result.add(new Variation(teForm+"‚¢‚È‚¢",tenses[88]));
      result.add(new Variation(teForm+"‚¢‚Ü‚·",tenses[89]));
      result.add(new Variation(teForm+"‚¢‚Ü‚¹‚ñ",tenses[90]));
      result.add(new Variation(teForm+"‚¢‚½",tenses[91]));
      result.add(new Variation(teForm+"‚¢‚È‚©‚Á‚½",tenses[92]));
      result.add(new Variation(teForm+"‚¢‚Ü‚µ‚½",tenses[93]));
      result.add(new Variation(teForm+"‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[94]));
    } else if (verbType.matches(",vt")){
      result.add(new Variation(teForm+"‚ ‚é",tenses[79]));
      result.add(new Variation(teForm+"‚È‚¢",tenses[80]));
      result.add(new Variation(teForm+"‚ ‚è‚Ü‚·",tenses[81]));
      result.add(new Variation(teForm+"‚ ‚è‚Ü‚¹‚ñ",tenses[82]));
      result.add(new Variation(teForm+"‚ ‚Á‚½",tenses[83]));
      result.add(new Variation(teForm+"‚È‚©‚Á‚½",tenses[84]));
      result.add(new Variation(teForm+"‚ ‚è‚Ü‚µ‚½",tenses[85]));
      result.add(new Variation(teForm+"‚ ‚è‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[86]));
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
      conjugation[0]="‚Î";
      conjugation[1]="‚Ñ";
      conjugation[2]="‚×";
      conjugation[3]="‚Ú";
      conjugation[4]="‚Ô";
    } else if(type.matches("v5g")) {
      conjugation[0]="‚ª";
      conjugation[1]="‚¬";
      conjugation[2]="‚°";
      conjugation[3]="‚²";
      conjugation[4]="‚®";
    } else if(type.matches("v5k")) {
      conjugation[0]="‚©";
      conjugation[1]="‚«";
      conjugation[2]="‚¯";
      conjugation[3]="‚±";
      conjugation[4]="‚­";
    } else if(type.matches("v5m")) {
      conjugation[0]="‚Ü";
      conjugation[1]="‚Ý";
      conjugation[2]="‚ß";
      conjugation[3]="‚à";
      conjugation[4]="‚Þ";
    } else if(type.matches("v5n")) {
      conjugation[0]="‚È";
      conjugation[1]="‚É";
      conjugation[2]="‚Ë";
      conjugation[3]="‚Ì";
      conjugation[4]="‚Ê";
    } else if(type.matches("v5r")) {
      conjugation[0]="‚ç";
      conjugation[1]="‚è";
      conjugation[2]="‚ê";
      conjugation[3]="‚ë";
      conjugation[4]="‚é";
    } else if(type.matches("v5t")) {
      conjugation[0]="‚½";
      conjugation[1]="‚¿";
      conjugation[2]="‚Ä";
      conjugation[3]="‚Æ";
      conjugation[4]="‚Â";
    } else if(type.matches("v5u")) {
      conjugation[0]="‚í";
      conjugation[1]="‚¢";
      conjugation[2]="‚¦";
      conjugation[3]="‚¨";
      conjugation[4]="‚¤";
    } else if(type.matches("v5z")) {
      conjugation[0]="‚´";
      conjugation[1]="‚¶";
      conjugation[2]="‚º";
      conjugation[3]="‚¼";
      conjugation[4]="‚¸";
    } else if(type.matches("v5s")) {
      conjugation[0]="‚³";
      conjugation[1]="‚µ";
      conjugation[2]="‚¹";
      conjugation[3]="‚»";
      conjugation[4]="‚·";
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
        new Variation(stem+conjugation[0]+"‚È‚¢",tenses[2]),
        new Variation(stem+conjugation[1]+"‚Ü‚·",tenses[3]),
        new Variation(stem+conjugation[1]+"‚Ü‚¹‚ñ",tenses[4]),
        // past indicative, positive, plain
        new Variation(stem+conjugation[0]+"‚È‚©‚Á‚½",tenses[6]),
        new Variation(stem+conjugation[1]+"‚Ü‚µ‚½",tenses[7]),
        new Variation(stem+conjugation[1]+"‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[8]),
        new Variation(stem+conjugation[3]+"‚¤",tenses[9]),
        new Variation(stem+conjugation[4]+"‚¾‚ë‚¤",tenses[9]),
        new Variation(stem+conjugation[0]+"‚È‚¢‚¾‚ë‚¤",tenses[10]),
        new Variation(stem+conjugation[1]+"‚Ü‚µ‚å‚¤",tenses[11]),
        new Variation(stem+conjugation[4]+"‚Å‚µ‚å‚¤",tenses[11]),
        new Variation(stem+conjugation[0]+"‚È‚¢‚Å‚µ‚å‚¤",tenses[12]),
        // past presumptive, positive, plain
        new Variation(stem+conjugation[0]+"‚È‚©‚Á‚½‚¾‚ë‚¤",tenses[14]),
        new Variation(stem+conjugation[1]+"‚Ü‚µ‚½‚ë‚¤",tenses[15]),
        new Variation(stem+conjugation[0]+"‚È‚©‚Á‚½‚Å‚µ‚å‚¤",tenses[16]),
        // te-forms
        new Variation(stem+conjugation[2]+"‚Î",tenses[25]),
        new Variation(stem+conjugation[0]+"‚È‚¯‚ê‚Î",tenses[26]),
        new Variation(stem+conjugation[4]+"‚È‚ç",tenses[27]),
        new Variation(stem+conjugation[0]+"‚È‚¢‚È‚ç",tenses[28]),
        // past conditional, positive, plain
        new Variation(stem+conjugation[0]+"‚È‚©‚Á‚½‚ç",tenses[30]),
        new Variation(stem+conjugation[1]+"‚Ü‚µ‚½‚ç",tenses[31]),
        new Variation(stem+conjugation[1]+"‚Ü‚¹‚ñ‚Å‚µ‚½‚ç",tenses[32]),
        new Variation(stem+conjugation[2]+"‚é",tenses[33]),
        new Variation(stem+conjugation[2]+"‚È‚¢",tenses[34]),
        new Variation(stem+conjugation[2]+"‚Ü‚·",tenses[35]),
        new Variation(stem+conjugation[2]+"‚Ü‚¹‚ñ",tenses[36]),
        new Variation(stem+conjugation[0]+"‚¹‚é",tenses[37]),
        new Variation(stem+conjugation[0]+"‚¹‚È‚¢",tenses[38]),
        new Variation(stem+conjugation[0]+"‚¹‚Ü‚·",tenses[39]),
        new Variation(stem+conjugation[0]+"‚¹‚Ü‚¹‚ñ",tenses[40]),
        new Variation(stem+conjugation[2],tenses[41]),
        new Variation(stem+conjugation[4]+"‚È",tenses[42]),
        // tekudasai, positive
        new Variation(stem+conjugation[0]+"‚È‚¢‚Å‚­‚¾‚³‚¢",tenses[44]),
        new Variation(stem+conjugation[0]+"‚È‚¢‚Å‰º‚³‚¢",tenses[44]),
        new Variation(stem+conjugation[0]+"‚ê‚é",tenses[45]),
        new Variation(stem+conjugation[0]+"‚ê‚È‚¢",tenses[46]),
        new Variation(stem+conjugation[0]+"‚ê‚Ü‚·",tenses[47]),
        new Variation(stem+conjugation[0]+"‚ê‚Ü‚¹‚ñ",tenses[48]),
        new Variation(stem+conjugation[0]+"‚¹‚ç‚ê‚é",tenses[49]),
        new Variation(stem+conjugation[0]+"‚¹‚ç‚ê‚È‚¢",tenses[50]),
        new Variation(stem+conjugation[0]+"‚¹‚ç‚ê‚Ü‚·",tenses[51]),
        new Variation(stem+conjugation[0]+"‚¹‚ç‚ê‚Ü‚¹‚ñ",tenses[52])
        ));

    // godan irregularities of te- and ta-form
    String taForm="";
    String teForm="";
    String naForm = stem+conjugation[0]+"‚È‚¢";
    String iForm = stem+conjugation[1];

    if(verbType.matches("v5[b|n|m]")){
      taForm = stem+"‚ñ‚¾";
      teForm = stem+"‚ñ‚Å";
    } else if(verbType.matches("v5[u|t|r]")) {
      taForm = stem+"‚Á‚½";
      teForm = stem+"‚Á‚Ä";
    } else if(verbType.matches("v5k")) {
      taForm = stem+"‚¢‚½";
      teForm = stem+"‚¢‚Ä";
    } else if(verbType.matches("v5g")) {
      taForm = stem+"‚¢‚¾";
      teForm = stem+"‚¢‚Å";
    } else if(verbType.matches("v5s")) {
      taForm = stem+"‚µ‚½";
      teForm = stem+"‚µ‚Ä";
    }
    // passive causative v2 for all godan verbs except su type
    if (verbType.matches("v5[b|n|m|u|t|r|k|g]")) {
      result.add(new Variation(stem+conjugation[0]+"‚³‚ê‚é",tenses[49]));
      result.add(new Variation(stem+conjugation[0]+"‚³‚ê‚È‚¢",tenses[50]));
      result.add(new Variation(stem+conjugation[0]+"‚³‚ê‚Ü‚·",tenses[51]));
      result.add(new Variation(stem+conjugation[0]+"‚³‚ê‚Ü‚¹‚ñ",tenses[52]));
    }
    result.add(new Variation(stem+taForm,tenses[5]));
    result.add(new Variation(stem+taForm+"‚¾‚ë‚¤",tenses[13]));
    result.add(new Variation(stem+taForm+"‚ë‚¤",tenses[13]));
    result.add(new Variation(stem+teForm+"‚¢‚é",tenses[17]));
    result.add(new Variation(stem+teForm+"‚¢‚È‚¢",tenses[18]));
    result.add(new Variation(stem+teForm+"‚¢‚Ü‚·",tenses[19]));
    result.add(new Variation(stem+teForm+"‚¢‚Ü‚¹‚ñ",tenses[20]));
    result.add(new Variation(stem+teForm+"‚¢‚½",tenses[21]));
    result.add(new Variation(stem+teForm+"‚¢‚È‚©‚Á‚½",tenses[22]));
    result.add(new Variation(stem+teForm+"‚¢‚Ü‚µ‚½",tenses[23]));
    result.add(new Variation(stem+teForm+"‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[24]));
    result.add(new Variation(stem+taForm+"‚ç",tenses[29]));
    result.add(new Variation(stem+teForm+"‚­‚¾‚³‚¢",tenses[43]));
    result.add(new Variation(stem+teForm+"‰º‚³‚¢",tenses[43]));
    result.add(new Variation(iForm+"‚½‚¢",tenses[53]));
    result.add(new Variation(iForm+"‚½‚­‚È‚¢",tenses[54]));
    result.add(new Variation(iForm+"‚½‚¢‚Å‚·",tenses[55]));
    result.add(new Variation(iForm+"‚½‚­‚È‚¢‚Å‚·",tenses[56]));
    result.add(new Variation(iForm+"‚½‚©‚Á‚½",tenses[57]));
    result.add(new Variation(iForm+"‚½‚È‚©‚Á‚½",tenses[58]));
    result.add(new Variation(iForm+"‚½‚©‚Á‚½‚ç",tenses[59]));
    result.add(new Variation(iForm+"‚½‚©‚Á‚½‚Å‚·",tenses[60]));
    result.add(new Variation(iForm+"‚½‚È‚©‚Á‚½‚Å‚·",tenses[61]));
    result.add(new Variation(iForm+"‚½‚¯‚ê‚Î",tenses[62]));
    result.add(new Variation(iForm+"‚½‚È‚¯‚ê‚Î",tenses[63]));
    result.add(new Variation(iForm+"‚½‚©‚Á‚½‚ç",tenses[64]));
    result.add(new Variation(iForm+"‚½‚È‚©‚Á‚½‚ç",tenses[65]));
    result.add(new Variation("‚¨"+iForm+"‚µ‚Ü‚·",tenses[66]));
    result.add(new Variation("‚¨"+iForm+"‚µ‚Ü‚¹‚ñ",tenses[67]));
    result.add(new Variation("‚¨"+iForm+"‚µ‚Ü‚µ‚½",tenses[68]));
    result.add(new Variation("‚¨"+iForm+"‚µ‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[69]));
    result.add(new Variation("‚¨"+iForm+"‚µ‚Ä‚­‚¾‚³‚¢",tenses[70]));
    result.add(new Variation("‚¨"+iForm+"‚µ‚Ä‰º‚³‚¢",tenses[70]));
    result.add(new Variation("‚¨"+naForm+"‚Å‚­‚¾‚³‚¢",tenses[71]));
    result.add(new Variation("‚¨"+naForm+"‚Å‰º‚³‚¢",tenses[71]));
    result.add(new Variation("‚¨"+iForm+"‚­‚¾‚³‚¢",tenses[72]));
    result.add(new Variation("‚¨"+iForm+"‰º‚³‚¢",tenses[72]));
    result.add(new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚·",tenses[73]));
    result.add(new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚¹‚ñ",tenses[74]));
    result.add(new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚µ‚½",tenses[75]));
    result.add(new Variation("‚¨"+iForm+"‚É‚È‚è‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[76]));
    result.add(new Variation("‚¨"+iForm+"‚É‚È‚Á‚Ä‚­‚¾‚³‚¢",tenses[77]));
    result.add(new Variation("‚¨"+iForm+"‚É‚È‚Á‚Ä‰º‚³‚¢",tenses[77]));
    result.add(new Variation("‚¨"+iForm+"‚É‚È‚ç‚È‚¢‚Å‚­‚¾‚³‚¢",tenses[78]));
    result.add(new Variation("‚¨"+iForm+"‚É‚È‚ç‚È‚¢‚Å‰º‚³‚¢",tenses[78]));
    result.add(new Variation(iForm+"‚È‚³‚¢",tenses[95]));
    // intransitive
    if(verbType.matches(",vi")){
      result.add(new Variation(teForm+"‚¢‚é",tenses[87]));
      result.add(new Variation(teForm+"‚¢‚È‚¢",tenses[88]));
      result.add(new Variation(teForm+"‚¢‚Ü‚·",tenses[89]));
      result.add(new Variation(teForm+"‚¢‚Ü‚¹‚ñ",tenses[90]));
      result.add(new Variation(teForm+"‚¢‚½",tenses[91]));
      result.add(new Variation(teForm+"‚¢‚È‚©‚Á‚½",tenses[92]));
      result.add(new Variation(teForm+"‚¢‚Ü‚µ‚½",tenses[93]));
      result.add(new Variation(teForm+"‚¢‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[94]));
      // transitive
    } else if (verbType.matches(",vt")){
      result.add(new Variation(teForm+"‚ ‚é",tenses[79]));
      result.add(new Variation(teForm+"‚È‚¢",tenses[80]));
      result.add(new Variation(teForm+"‚ ‚è‚Ü‚·",tenses[81]));
      result.add(new Variation(teForm+"‚ ‚è‚Ü‚¹‚ñ",tenses[82]));
      result.add(new Variation(teForm+"‚ ‚Á‚½",tenses[83]));
      result.add(new Variation(teForm+"‚È‚©‚Á‚½",tenses[84]));
      result.add(new Variation(teForm+"‚ ‚è‚Ü‚µ‚½",tenses[85]));
      result.add(new Variation(teForm+"‚ ‚è‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[86]));
    } else {
      // no transitive or intransitive usage
    }
    return result;
  }
}