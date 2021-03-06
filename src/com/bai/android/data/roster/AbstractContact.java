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

import java.util.Collection;
import java.util.Collections;

import android.graphics.drawable.Drawable;

import com.bai.android.data.account.AccountManager;
import com.bai.android.data.account.StatusMode;
import com.bai.android.data.entity.BaseEntity;
import com.bai.android.data.extension.avatar.AvatarManager;
import com.bai.android.data.extension.vcard.VCardManager;

/**
 * Basic contact representation.
 * 
 */
public class AbstractContact extends BaseEntity {

  public AbstractContact(String account, String user) {
    super(account, user);
  }

  /**
   * vCard and roster can be used for name resolving.
   * 
   * @return Verbose name.
   */
  public String getName() {
    String vCardName = VCardManager.getInstance().getName(user);
    if (!"".equals(vCardName))
      return vCardName;
    return user;
  }

  public StatusMode getStatusMode() {
    return PresenceManager.getInstance().getStatusMode(account, user);
  }

  public String getStatusText() {
    return PresenceManager.getInstance().getStatusText(account, user);
  }

  public Collection<? extends Group> getGroups() {
    return Collections.emptyList();
  }

  public Drawable getAvatar() {
    return AvatarManager.getInstance().getUserAvatar(user);

  }

  /**
   * @return Cached avatar's drawable for contact list.
   */
  public Drawable getAvatarForContactList() {
    return AvatarManager.getInstance().getUserAvatarForContactList(user);
  }

  /**
   * @return Account's color level.
   */
  public int getColorLevel() {
    return AccountManager.getInstance().getColorLevel(account);
  }

  /**
   * @return Whether contact is connected.
   */
  public boolean isConnected() {
    return true;
  }

}
