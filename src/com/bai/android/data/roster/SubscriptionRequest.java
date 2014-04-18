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
package com.bai.android.data.roster;

import android.content.Intent;

import com.bai.android.data.Application;
import com.bai.android.data.entity.BaseEntity;
import com.bai.android.data.notification.EntityNotificationItem;
import com.bai.android.ui.ContactAdd;
import com.bai.androiddev.R;

public class SubscriptionRequest extends BaseEntity implements
EntityNotificationItem {

  public SubscriptionRequest(String account, String user) {
    super(account, user);
  }

  @Override
  public Intent getIntent() {
    return ContactAdd.createSubscriptionIntent(Application.getInstance(),
        account, user);
  }

  @Override
  public String getText() {
    return Application.getInstance().getString(
        R.string.subscription_request_message);
  }

  @Override
  public String getTitle() {
    String returnValue = user;
    if (user != null && user.indexOf("@") != -1) {
      returnValue = user.substring(0, user.indexOf("@"));
    }
    return returnValue;
  }

  public String getConfirmation() {
    //		String accountName = AccountManager.getInstance().getVerboseName(
    //				account);
    String userName = RosterManager.getInstance().getName(account, user);
    if (userName != null && userName.indexOf("@") != -1) {
      userName = userName.substring(0, userName.indexOf("@"));
    }
    //		if (accountName != null && accountName.indexOf("@") != -1) {
    //			accountName = accountName.substring(0, accountName.indexOf("@"));
    //		}
    return Application.getInstance().getString(
        R.string.contact_subscribe_confirm, userName);
  }

}
