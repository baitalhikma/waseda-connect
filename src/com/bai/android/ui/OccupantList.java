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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bai.android.data.Application;
import com.bai.android.data.account.OnAccountChangedListener;
import com.bai.android.data.entity.BaseEntity;
import com.bai.android.data.extension.muc.MUCManager;
import com.bai.android.data.intent.AccountIntentBuilder;
import com.bai.android.data.intent.EntityIntentBuilder;
import com.bai.android.data.roster.OnContactChangedListener;
import com.bai.android.ui.adapter.OccupantListAdapter;
import com.bai.android.ui.helper.ManagedListActivity;
import com.bai.xmpp.address.Jid;
import com.bai.androiddev.R;

/**
 * Represent list of occupants in the room.
 * 
 * @author alexander.ivanov
 * 
 */
public class OccupantList extends ManagedListActivity implements
		OnAccountChangedListener, OnContactChangedListener {

	private String account;
	private String room;
	private OccupantListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFinishing())
			return;

		account = getAccount(getIntent());
		room = Jid.getBareAddress(getUser(getIntent()));
		if (account == null || room == null
				|| !MUCManager.getInstance().hasRoom(account, room)) {
			Application.getInstance().onError(R.string.ENTRY_IS_NOT_FOUND);
			finish();
			return;
		}
		setContentView(R.layout.list);
		listAdapter = new OccupantListAdapter(this, account, room);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Application.getInstance().addUIListener(OnAccountChangedListener.class,
				this);
		Application.getInstance().addUIListener(OnContactChangedListener.class,
				this);
		listAdapter.onChange();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Application.getInstance().removeUIListener(
				OnAccountChangedListener.class, this);
		Application.getInstance().removeUIListener(
				OnContactChangedListener.class, this);
	}

	@Override
	public void onContactsChanged(Collection<BaseEntity> entities) {
		if (entities.contains(new BaseEntity(account, room)))
			listAdapter.onChange();
	}

	@Override
	public void onAccountsChanged(Collection<String> accounts) {
		if (accounts.contains(account))
			listAdapter.onChange();
	}

	public static Intent createIntent(Context context, String account,
			String user) {
		return new EntityIntentBuilder(context, OccupantList.class)
				.setAccount(account).setUser(user).build();
	}

	private static String getAccount(Intent intent) {
		return AccountIntentBuilder.getAccount(intent);
	}

	private static String getUser(Intent intent) {
		return EntityIntentBuilder.getUser(intent);
	}

}
