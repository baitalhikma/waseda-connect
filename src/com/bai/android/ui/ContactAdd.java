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
package com.bai.android.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bai.android.data.Application;
import com.bai.android.data.NetworkException;
import com.bai.android.data.account.AccountManager;
import com.bai.android.data.intent.EntityIntentBuilder;
import com.bai.android.data.message.MessageManager;
import com.bai.android.data.roster.PresenceManager;
import com.bai.android.data.roster.RosterManager;
import com.bai.android.data.roster.SubscriptionRequest;
import com.bai.android.ui.dialog.ConfirmDialogBuilder;
import com.bai.android.ui.dialog.ConfirmDialogListener;
import com.bai.android.ui.dialog.DialogBuilder;
import com.bai.androiddev.R;

public class ContactAdd extends GroupListActivity implements
View.OnClickListener, ConfirmDialogListener, OnItemSelectedListener {

	/**
	 * Action for subscription request to be show.
	 * 
	 * Clear action on dialog dismiss.
	 */
	private static final String ACTION_SUBSCRIPTION_REQUEST = "com.bai.android.data.SUBSCRIPTION_REQUEST";

	private static final String SAVED_ACCOUNT = "com.bai.android.ui.ContactAdd.SAVED_ACCOUNT";
	private static final String SAVED_USER = "com.bai.android.ui.ContactAdd.SAVED_USER";
	private static final String SAVED_NAME = "com.bai.android.ui.ContactAdd.SAVED_NAME";

	private static final int DIALOG_SUBSCRIPTION_REQUEST_ID = 0x20;

	private String account;
	private String user;

	private SubscriptionRequest subscriptionRequest;

	/**
	 * Views
	 */
	private EditText userView;
	private EditText nameView;

	@Override
	protected void onInflate(Bundle savedInstanceState) {
		setContentView(R.layout.contact_add);

		ListView listView = getListView();
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.contact_add_header, listView, false);
		listView.addHeaderView(view, null, false);

		userView = (EditText) view.findViewById(R.id.contact_user);
		nameView = (EditText) view.findViewById(R.id.contact_name);
		((Button) view.findViewById(R.id.ok)).setOnClickListener(this);

		String name;
		Intent intent = getIntent();
		if (savedInstanceState != null) {
			account = savedInstanceState.getString(SAVED_ACCOUNT);
			user = savedInstanceState.getString(SAVED_USER);
			name = savedInstanceState.getString(SAVED_NAME);
		} else {
			account = getAccount(intent);
			user = getUser(intent);
			if (account == null || user == null)
				name = null;
			else {
				name = RosterManager.getInstance().getName(account, user);
				if (user.equals(name))
					name = null;
			}
		}
		if (account == null) {
			Collection<String> accounts = AccountManager.getInstance().getAccounts();
			if (accounts.size() == 1)
				account = accounts.iterator().next();
		}
		if (user != null) {
			if (user.indexOf("@") != -1) {
				userView.setText(user.substring(0, user.indexOf("@")));
			} else {
			userView.setText(user);
			}
		}
		if (name != null)
			nameView.setText(name);
		if (ACTION_SUBSCRIPTION_REQUEST.equals(intent.getAction())) {
			subscriptionRequest = PresenceManager.getInstance()
					.getSubscriptionRequest(account, user);
			if (subscriptionRequest == null) {
				Application.getInstance().onError(R.string.ENTRY_IS_NOT_FOUND);
				finish();
				return;
			}
		} else {
			subscriptionRequest = null;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SAVED_ACCOUNT, account);
		outState.putString(SAVED_USER, userView.getText().toString());
		outState.putString(SAVED_NAME, nameView.getText().toString());
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (subscriptionRequest != null)
			showDialog(DIALOG_SUBSCRIPTION_REQUEST_ID);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ok:
			String user = userView.getText().toString();
			if ("".equals(user)) {
				Toast.makeText(this, getString(R.string.EMPTY_USER_NAME),
						Toast.LENGTH_LONG).show();
				return;
			}

			user += "@" + getString(R.string.host_name);

			if (account == null) {
				Toast.makeText(this, getString(R.string.EMPTY_ACCOUNT),
						Toast.LENGTH_LONG).show();
				return;
			}
//			if (userNameExists(user)) { TODO - checking if such users exist on the server
				try {
					RosterManager.getInstance().createContact(account, user,
							nameView.getText().toString(), getSelected());
					PresenceManager.getInstance()
					.requestSubscription(account, user);
				} catch (NetworkException e) {
					Application.getInstance().onError(e);
					finish();
					return;
				}
//			} else {
//				Toast.makeText(getApplicationContext(), "User doesn't exist.", Toast.LENGTH_LONG).show(); //TODO MOVE TO Strings
//				return;
//			}
			MessageManager.getInstance().openChat(account, user);
			finish();
			break;
		default:
			break;
		}
	}

	//TODO = SERVE SIDE NOT IMPLEMENTED YET
//	private boolean userNameExists(String username) {
//		Log.e(" Clue. ", "Checking");
//		try {
//			XMPPConnection xmppConnection = AccountManager.getInstance().getAccount(account).getConnectionThread().getXMPPConnection();
//			UserSearchManager search = new UserSearchManager(xmppConnection);
//			String searchService = "" + xmppConnection.getServiceName();
//			Log.e("-------------","search service: " +searchService);
//			Form searchForm = search.getSearchForm(searchService);
//			Log.e("-------------","after search form ");
//			Form answerForm = searchForm.createAnswerForm();
//			Log.e("-------------","after create answer form");
//			answerForm.setAnswer("username", true);
//			answerForm.setAnswer("search", username);
//			
//			ReportedData data = search.getSearchResults(searchForm, searchService);
//			if (data.getRows() != null) {
//				Log.e(" Clue. ", "EXISTS");
//				Toast.makeText(getApplicationContext(), "ID EXISTS", 1).show();
//				return true;
//			} else {
//				Log.e(" Clue. ", "NO");
//				Toast.makeText(getApplicationContext(), "ID DOESNT EXIST", 1).show();
//				return false;
//			}
//			// Use Returned Data
//		} catch (XMPPException e) {
//			Log.e(" Clue. ", e.getMessage());
//			e.printStackTrace();
//		}
//		return false;
//	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = super.onCreateDialog(id);
		if (dialog != null)
			return dialog;
		switch (id) {
		case DIALOG_SUBSCRIPTION_REQUEST_ID:
			return new ConfirmDialogBuilder(this,
					DIALOG_SUBSCRIPTION_REQUEST_ID, this).setMessage(
							subscriptionRequest.getConfirmation()).create();
		default:
			return null;
		}
	}

	@Override
	public void onAccept(DialogBuilder dialogBuilder) {
		super.onAccept(dialogBuilder);
		switch (dialogBuilder.getDialogId()) {
		case DIALOG_SUBSCRIPTION_REQUEST_ID:
			try {
				PresenceManager.getInstance().acceptSubscription(
						subscriptionRequest.getAccount(),
						subscriptionRequest.getUser());
			} catch (NetworkException e) {
				Application.getInstance().onError(e);
			}
			getIntent().setAction(null);
//			break; will stay in the add contact screen
//			finish(); ?! 
			((Button) findViewById(R.id.ok)).performClick();
		}
	}

	@Override
	public void onDecline(DialogBuilder dialogBuilder) {
		super.onDecline(dialogBuilder);
		switch (dialogBuilder.getDialogId()) {
		case DIALOG_SUBSCRIPTION_REQUEST_ID:
			try {
				PresenceManager.getInstance().discardSubscription(
						subscriptionRequest.getAccount(),
						subscriptionRequest.getUser());
			} catch (NetworkException e) {
				Application.getInstance().onError(e);
			}
			finish();
			break;
		}
	}

	@Override
	public void onCancel(DialogBuilder dialogBuilder) {
		super.onCancel(dialogBuilder);
		switch (dialogBuilder.getDialogId()) {
		case DIALOG_SUBSCRIPTION_REQUEST_ID:
			finish();
			break;
		}
	}

	@Override
	Collection<String> getInitialGroups() {
		if (account == null)
			return Collections.emptyList();
		return RosterManager.getInstance().getGroups(account);
	}

	@Override
	Collection<String> getInitialSelected() {
		return Collections.emptyList();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (account == null) {
			onNothingSelected(parent);
		} else {
			HashSet<String> groups = new HashSet<String>(RosterManager
					.getInstance().getGroups(account));
			groups.addAll(getSelected());
			setGroups(groups, getSelected());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		setGroups(getSelected(), getSelected());
	}

	public static Intent createIntent(Context context) {
		return createIntent(context, null);
	}

	private static Intent createIntent(Context context, String account,
			String user) {
		return new EntityIntentBuilder(context, ContactAdd.class)
		.setAccount(account).setUser(user).build();
	}

	public static Intent createIntent(Context context, String account) {
		return createIntent(context, account, null);
	}

	public static Intent createSubscriptionIntent(Context context,
			String account, String user) {
		Intent intent = createIntent(context, account, user);
		intent.setAction(ACTION_SUBSCRIPTION_REQUEST);
		return intent;
	}

	private static String getAccount(Intent intent) {
		return EntityIntentBuilder.getAccount(intent);
	}

	private static String getUser(Intent intent) {
		return EntityIntentBuilder.getUser(intent);
	}

}
