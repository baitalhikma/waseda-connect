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
package com.bai.android.data.extension.otr;

import android.content.Intent;

import com.bai.android.data.Application;
import com.bai.android.data.entity.BaseEntity;
import com.bai.android.data.notification.EntityNotificationItem;
import com.bai.android.data.roster.RosterManager;
import com.bai.android.ui.QuestionViewer;
import com.bai.androiddev.R;

public class SMProgress extends BaseEntity implements EntityNotificationItem {

	public SMProgress(String account, String user) {
		super(account, user);
	}

	@Override
	public Intent getIntent() {
		return QuestionViewer.createCanelIntent(
				Application.getInstance(), account, user);
	}

	@Override
	public String getTitle() {
		return Application.getInstance().getString(R.string.otr_verification)
				+ " " + RosterManager.getInstance().getName(account, user);
	}

	@Override
	public String getText() {
		return Application.getInstance().getString(
				R.string.otr_verification_in_progress);
	}

}