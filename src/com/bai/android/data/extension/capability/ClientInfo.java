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
package com.bai.android.data.extension.capability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represent information about client.
 * 
 */
public class ClientInfo {

	private final String type;

	private final String name;

	private final Collection<String> features;

//	private final ClientSoftware clientSoftware;

	public ClientInfo(String type, String name, String node,
			Collection<String> features) {
		super();
		this.type = type;
		this.name = name;
		this.features = Collections
				.unmodifiableCollection(new ArrayList<String>(features));
//		this.clientSoftware = ClientSoftware.getByName(name, node);
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

//	public ClientSoftware getClientSoftware() {
//		return clientSoftware;
//	}

	public Collection<String> getFeatures() {
		return features;
	}

}
