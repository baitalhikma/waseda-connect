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
package com.bai.android.data.account;

import android.content.Intent;

import com.bai.android.data.Application;
import com.bai.android.data.entity.AccountRelated;
import com.bai.android.data.notification.AccountNotificationItem;
import com.bai.androiddev.R;

public class PasswordRequest extends AccountRelated implements
		AccountNotificationItem {

	public PasswordRequest(String account) {
		super(account);
	}

	@Override
	public Intent getIntent() {
		return com.bai.android.ui.PasswordRequest.createIntent(
				Application.getInstance(), account);
	}

	@Override
	public String getTitle() {
		return Application.getInstance().getString(R.string.PASSWORD_REQUIRED);
	}

	@Override
	public String getText() {
		return AccountManager.getInstance().getVerboseName(account);
	}

}
