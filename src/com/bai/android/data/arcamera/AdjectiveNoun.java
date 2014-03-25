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
				new Variation(stem+"‚¢",tenses[1]),
				new Variation(stem+"‚­‚È‚¢",tenses[2]),
				new Variation(stem+"‚¢‚Å‚·",tenses[3]),
				new Variation(stem+"‚­‚È‚¢‚Å‚·",tenses[4]),
				new Variation(stem+"‚©‚Á‚½",tenses[5]),
				new Variation(stem+"‚È‚©‚Á‚½",tenses[6]),
				new Variation(stem+"‚©‚Á‚½‚Å‚·",tenses[7]),
				new Variation(stem+"‚­‚È‚©‚Á‚½‚Å‚·",tenses[8]),
				new Variation(stem+"‚¯‚ê‚Î",tenses[9]),
				new Variation(stem+"‚È‚¯‚ê‚Î",tenses[10]),
				new Variation(stem+"‚©‚Á‚½‚ç",tenses[11]),
				new Variation(stem+"‚È‚©‚Á‚½‚ç",tenses[12]),
				new Variation(stem+"‚­‚Ä",tenses[13]),
				new Variation(stem,tenses[14])
				));
				return result;
	}

	// na-adjectives and nouns
	private ArrayList<com.bai.android.data.arcamera.Variation> naAdjNoun(String dict) {
		String dictForm = dict;
		String stem = dictForm;
		ArrayList<Variation> result = new ArrayList<Variation>(Arrays.asList(
				new Variation(stem+"‚¾",tenses[1]),
				new Variation(stem+"‚¶‚á‚È‚¢",tenses[2]),
				new Variation(stem+"‚¶‚á‚ ‚è‚Ü‚¹‚ñ",tenses[2]),
				new Variation(stem+"‚Å‚·",tenses[3]),
				new Variation(stem+"‚Å‚Í‚È‚¢",tenses[4]),
				new Variation(stem+"‚Å‚Í‚ ‚è‚Ü‚¹‚ñ",tenses[4]),
				new Variation(stem+"‚¾‚Á‚½",tenses[5]),
				new Variation(stem+"‚¶‚á‚È‚©‚Á‚½",tenses[6]),
				new Variation(stem+"‚¶‚á‚ ‚è‚Ü‚¹‚ñ‚Å‚µ‚½",tenses[6]),
				new Variation(stem+"‚Å‚µ‚½",tenses[7]),
				new Variation(stem+"‚Å‚Í‚ ‚è‚Ü‚ñ‚Å‚µ‚½",tenses[8]),
				new Variation(stem+"‚Å‚Í‚È‚©‚Á‚½",tenses[8]),
				new Variation(stem+"‚È‚ç",tenses[9]),
				new Variation(stem+"‚È‚ç‚Î",tenses[9]),
				new Variation(stem+"‚Å‚ ‚ê‚Î",tenses[9]),
				new Variation(stem+"‚¶‚á‚È‚¯‚ê‚Î",tenses[10]),
				new Variation(stem+"‚Å‚È‚¯‚ê‚Î",tenses[10]),
				new Variation(stem+"‚Å‚È‚¢‚È‚ç",tenses[10]),
				new Variation(stem+"‚¾‚Á‚½‚ç",tenses[11]),
				new Variation(stem+"‚¶‚á‚È‚©‚Á‚½‚ç",tenses[12]),
				new Variation(stem+"‚Å",tenses[13]),
				new Variation(stem,tenses[14])
				));
		return result;
	}
}