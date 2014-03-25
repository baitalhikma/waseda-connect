package com.bai.android.data.arcamera;

import java.util.HashMap;
import java.util.Map;

public class Particle {
	private Map<String, String> funcParts = new HashMap<String, String>();
	private Map<String, String> meanParts = new HashMap<String, String>();

	// constructor
	public Particle() {
		funcParts.put("‚ª", "");
		funcParts.put("‚ğ", "");
		funcParts.put("‚ğ‚Î", "");
		funcParts.put("‚Í", "");
		
		meanParts.put("‰º‚Á‚Ä", "myself");
		meanParts.put("~‚Á‚Ä", "myself");
		meanParts.put("‚à", "too");
		meanParts.put("‚Â", "");
		meanParts.put("‚à‚¾‚©‚ç", "because");
		meanParts.put("‚à‚ñ‚¾‚©‚ç", "because");
		meanParts.put("‚©‚ç", "because");
		meanParts.put("‚í‚¢‚È", "!");
		meanParts.put("‚í‚¢‚Ì", "!");
		meanParts.put("‚í‚¢‚Ì‚¤", "!");
		meanParts.put("‚¢", "!?");
		meanParts.put("‚©", "?");
		meanParts.put("‚©‚¢", "or ...?");
		meanParts.put("‚©‚¢‚È","??");
		meanParts.put("‚©‚¦", "or ...?");
		meanParts.put("‚©‚Ë",", right?");
		meanParts.put("‚©‚â", "");
		meanParts.put("‚©‚æ", "Really?");
		meanParts.put("‚ª‚È","");
		meanParts.put("‚ª‚È‚ ","");
		meanParts.put("‚¯","?");
		meanParts.put("‚¯‚¥", "?");
		meanParts.put("‚±‚Æ", "");
		meanParts.put("‚º", "!");
		meanParts.put("‚º‚¦", "");
		meanParts.put("‚¼", "!");
		meanParts.put("‚¼‚¢", "");
		meanParts.put("‚¼‚¦", "");
		meanParts.put("‚½‚è", "and");
		meanParts.put("‚¾‚¢", "?");
		meanParts.put("‚¾‚è", "and");
		meanParts.put("‚Á‚¯","?");
		meanParts.put("‚Á‚Ä", "");
		meanParts.put("‚Á‚Ä‚Î","");
		meanParts.put("‚Ä", "");
		meanParts.put("‚Ä‚Î","");
		meanParts.put("‚Å", "in");
		meanParts.put("‚Å‚à‚Á‚Ä", "by");
		meanParts.put("‚Æ‚Í", "in other words");
		meanParts.put("‚Æ‚â‚ç","I heard");
		meanParts.put("‚Ç‚±‚ë","not at all");
		meanParts.put("‚È", "!");
		meanParts.put("‚É","at");
		meanParts.put("‚É‚Ä", "in");
		meanParts.put("‚Ë",", right?");
		meanParts.put("‚Ë‚¦",", right?");
		meanParts.put("‚Ë‚ñ", "!");
		meanParts.put("‚Ì", "'s");
		meanParts.put("‚Ì‚¤", "!");
		meanParts.put("‚Ì‚©", "");
		meanParts.put("‚Ì‚ñ", "");
		meanParts.put("‚Î", "");
		meanParts.put("‚Î‚¢", "!");
		meanParts.put("‚Ö", "to");
		meanParts.put("ƒw", "to");
		meanParts.put("‚à‚ª‚È","");
		meanParts.put("‚à‚Ì‚©","");
		meanParts.put("‚à‚ñ‚©","");
		meanParts.put("‚â", "and");
		meanParts.put("‚â‚ç", "");
		meanParts.put("‚æ","!");
		meanParts.put("‚í", "!");
		meanParts.put("‚í‚¢", "!");
		meanParts.put("‚í‚¦", "!");
		meanParts.put("‚í‚æ", "!");
		meanParts.put("‚µ", "and");
		meanParts.put("‚Æ‚©", "or");
		meanParts.put("‚à‚Ì‚Å","because");
		meanParts.put("‚à‚ñ‚Å","because");
		meanParts.put("‚Ì‚Å","because");
		meanParts.put("‚Ì‚É","instead");
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
