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
package com.bai.android.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.bai.android.data.SettingsManager;
import com.bai.androiddev.R;

/**
 * Emoticons.
 * 
 */
public class Emoticons {

	public static final Map<Pattern, Integer> ANDROID_EMOTICONS = new HashMap<Pattern, Integer>();
	public static final Map<Pattern, Integer> NONE_EMOTICONS = new HashMap<Pattern, Integer>();

	private static final Factory spannableFactory = Spannable.Factory
			.getInstance();

	static {
		addPattern(ANDROID_EMOTICONS, ":)", R.drawable.emo_im_happy);
		addPattern(ANDROID_EMOTICONS, ":-)", R.drawable.emo_im_happy);
		addPattern(ANDROID_EMOTICONS, ":(", R.drawable.emo_im_sad);
		addPattern(ANDROID_EMOTICONS, ":-(", R.drawable.emo_im_sad);
		addPattern(ANDROID_EMOTICONS, ";)", R.drawable.emo_im_winking);
		addPattern(ANDROID_EMOTICONS, ";-)", R.drawable.emo_im_winking);
		addPattern(ANDROID_EMOTICONS, ":P",
				R.drawable.emo_im_tongue_sticking_out);
		addPattern(ANDROID_EMOTICONS, ":-P",
				R.drawable.emo_im_tongue_sticking_out);
		addPattern(ANDROID_EMOTICONS, "=-O", R.drawable.emo_im_surprised);
		addPattern(ANDROID_EMOTICONS, ":*", R.drawable.emo_im_kissing);
		addPattern(ANDROID_EMOTICONS, ":-*", R.drawable.emo_im_kissing);
//		addPattern(ANDROID_EMOTICONS, ":O", R.drawable.emo_im_wtf);
//		addPattern(ANDROID_EMOTICONS, ":-O", R.drawable.emo_im_wtf);
		addPattern(ANDROID_EMOTICONS, "B)", R.drawable.emo_im_cool);
		addPattern(ANDROID_EMOTICONS, "B-)", R.drawable.emo_im_cool);
		addPattern(ANDROID_EMOTICONS, "8)", R.drawable.emo_im_cool);
		addPattern(ANDROID_EMOTICONS, "8-)", R.drawable.emo_im_cool);
//		addPattern(ANDROID_EMOTICONS, ":$", R.drawable.emo_im_money_mouth);
//		addPattern(ANDROID_EMOTICONS, ":-$", R.drawable.emo_im_money_mouth);
//		addPattern(ANDROID_EMOTICONS, ":-!", R.drawable.emo_im_foot_in_mouth);
//		addPattern(ANDROID_EMOTICONS, ":-[", R.drawable.emo_im_embarrassed);
		addPattern(ANDROID_EMOTICONS, "O:)", R.drawable.emo_im_angel);
		addPattern(ANDROID_EMOTICONS, "O:-)", R.drawable.emo_im_angel);
		addPattern(ANDROID_EMOTICONS, ":\\", R.drawable.emo_im_undecided);
		addPattern(ANDROID_EMOTICONS, ":-\\", R.drawable.emo_im_undecided);
		addPattern(ANDROID_EMOTICONS, ":'(", R.drawable.emo_im_crying);
		addPattern(ANDROID_EMOTICONS, ":D", R.drawable.emo_im_laughing);
		addPattern(ANDROID_EMOTICONS, ":-D", R.drawable.emo_im_laughing);
//		addPattern(ANDROID_EMOTICONS, "O_o", R.drawable.emo_im_wtf);
//		addPattern(ANDROID_EMOTICONS, "o_O", R.drawable.emo_im_wtf);
		addPattern(ANDROID_EMOTICONS, ">:O", R.drawable.emo_im_yelling);
		addPattern(ANDROID_EMOTICONS, ">:0", R.drawable.emo_im_yelling);
		addPattern(ANDROID_EMOTICONS, ":S", R.drawable.emo_im_lips_are_sealed);
		addPattern(ANDROID_EMOTICONS, ":-S", R.drawable.emo_im_lips_are_sealed);
		addPattern(ANDROID_EMOTICONS, "(Y)", R.drawable.emo_im_thumb_up);
		addPattern(ANDROID_EMOTICONS, "<3", R.drawable.emo_im_heart);
		addPattern(ANDROID_EMOTICONS, "</3", R.drawable.emo_im_broken_heart);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
			int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	private Emoticons() {
	}

	/**
	 * @param text
	 * @return new spannable.
	 */
	public static Spannable newSpannable(CharSequence text) {
		return spannableFactory.newSpannable(text);
	}

	/**
	 * @param context
	 * @param spannable
	 * @return Whether smiles have been added into <code>spannable</code>.
	 */
	public static boolean getSmiledText(Context context, Spannable spannable) {
		boolean hasChanges = false;
		Map<Pattern, Integer> emoticons = SettingsManager.interfaceSmiles();
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(),
						matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start()
							&& spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					spannable.setSpan(new ImageSpan(context, entry.getValue()),
							matcher.start(), matcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					hasChanges = true;
				}
			}
		}
		return hasChanges;
	}

	/**
	 * @param context
	 * @param text
	 * @return New spannable with added smiles if needed.
	 */
	public static Spannable getSmiledText(Context context, CharSequence text) {
		Spannable spannable = spannableFactory.newSpannable(text);
		getSmiledText(context, spannable);
		return spannable;
	}

}
