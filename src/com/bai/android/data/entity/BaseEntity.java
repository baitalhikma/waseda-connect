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
package com.bai.android.data.entity;


/**
 * Object with account and user fields.
 */
public class BaseEntity extends AccountRelated implements
		Comparable<BaseEntity> {

	protected final String user;
	/** convenience object storing the node part of a jid */ 
	private String node;
	private boolean selected = false;

	public BaseEntity(String account, String user) {
		super(account);
		this.user = user;
		if (user != null) {
			int atInd = user.indexOf("@");
			if ( atInd > 0) {
				this.node = user.substring(0, user.indexOf("@")); 
			}
		}
	}

	public BaseEntity(BaseEntity baseEntity) {
		this(baseEntity.account, baseEntity.user);
	}

	public String getUser() {
		return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	public boolean equals(String account, String user) {
		if (this.account == null) {
			if (account != null)
				return false;
		} else {
			if (!this.account.equals(account))
				return false;
		}
		if (this.user == null) {
			if (user != null)
				return false;
		} else {
			if (!this.user.equals(user))
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public int compareTo(BaseEntity another) {
		if (account == null) {
			if (another.account != null)
				return -1;
		} else {
			if (another.account == null)
				return 1;
			else {
				int result = account.compareTo(another.account);
				if (result != 0)
					return result;
			}
		}
		if (user == null) {
			if (another.user != null)
				return -1;
		} else {
			if (another.user == null)
				return 1;
			else {
				int result = user.compareTo(another.user);
				if (result != 0)
					return result;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return account + ":" + user;
	}

	public boolean isSelected() {
		return selected;
	}

	public void toggleSelected() {
		selected = !selected;
	}

	public String getNode() {
		return node;
	}
}
