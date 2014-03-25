/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 * 
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 * 
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package com.bai.android.ui.helper;

import org.jivesoftware.smackx.ChatState;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bai.android.data.account.AccountManager;
import com.bai.android.data.extension.cs.ChatStateManager;
import com.bai.android.data.roster.AbstractContact;
import com.bai.android.utils.Emoticons;
import com.bai.androiddev.R;

/**
 * Helper class to update <code>contact_title.xml</code>.
 * 
 * @author alexander.ivanov
 * 
 */
public class ContactTitleInflater {

	/**
	 * Fill title with information about {@link AbstractContact} and provides
	 * back button callback.
	 * 
	 * @param titleView
	 * @param activity
	 * @param abstractContact
	 */
	public static void updateTitle(View titleView, final Activity activity,
			AbstractContact abstractContact) {
		final TypedArray typedArray = activity
				.obtainStyledAttributes(R.styleable.ContactList);
		typedArray.recycle();
		final TextView nameView = (TextView) titleView.findViewById(R.id.name);
		final ImageView avatarView = (ImageView) titleView
				.findViewById(R.id.avatar);
		final ImageView statusModeView = (ImageView) titleView
				.findViewById(R.id.status_mode);
		final TextView statusTextView = (TextView) titleView
				.findViewById(R.id.status_text);
		final View shadowView = titleView.findViewById(R.id.shadow);
		final ImageButton backButton = (ImageButton) titleView.findViewById(R.id.back_button);
		String displayName = abstractContact.getName();
		if (displayName != null && displayName != "" && displayName.indexOf("@") > 0) {
			displayName = displayName.substring(0, displayName.indexOf("@")); // clear the domain part
		}
		nameView.setText(displayName);
		statusModeView.setImageLevel(abstractContact.getStatusMode()
				.getStatusLevel());
		titleView.getBackground().setLevel(
				AccountManager.getInstance().getColorLevel(
						abstractContact.getAccount()));
		avatarView.setImageDrawable(abstractContact.getAvatar());
		ChatState chatState = ChatStateManager.getInstance().getChatState(
				abstractContact.getAccount(), abstractContact.getUser());
		final CharSequence statusText;
		if (chatState == ChatState.composing)
			statusText = activity.getString(R.string.chat_state_composing);
		else if (chatState == ChatState.paused)
			statusText = activity.getString(R.string.chat_state_paused);
		else
			statusText = Emoticons.getSmiledText(activity,
					abstractContact.getStatusText());
		statusTextView.setText(statusText);
		final Bitmap bitmap = BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.shadow);
		final BitmapDrawable shadowDrawable = new BitmapDrawable(bitmap);
		shadowDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		shadowView.setBackgroundDrawable(shadowDrawable);
		if (abstractContact.isConnected())
			shadowView.setVisibility(View.GONE);
		else
			shadowView.setVisibility(View.VISIBLE);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
	}

}
