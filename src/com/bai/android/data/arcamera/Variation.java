package com.bai.android.data.arcamera;

public class Variation {
	private String form;
	private String tense;
	// object for containing the verb/adjective/noun forms and tenses
	public Variation (String form, String tense){
		this.setForm(form);
		this.setTense(tense);
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getTense() {
		return tense;
	}
	public void setTense(String tense) {
		this.tense = tense;
	}
}
