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
package com.bai.android.data.extension.ping;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.Ping;

import com.bai.android.data.Application;
import com.bai.android.data.LogManager;
import com.bai.android.data.NetworkException;
import com.bai.android.data.account.AccountItem;
import com.bai.android.data.connection.ConnectionItem;
import com.bai.android.data.connection.ConnectionManager;
import com.bai.android.data.connection.OnPacketListener;

/**
 * Reply on incoming ping requests.
 * 
 * @author alexander.ivanov
 * 
 */
public class PingManager implements OnPacketListener {

	private final static PingManager instance;

	static {
		instance = new PingManager();
		Application.getInstance().addManager(instance);

		Connection
				.addConnectionCreationListener(new ConnectionCreationListener() {
					@Override
					public void connectionCreated(final Connection connection) {
						ServiceDiscoveryManager.getInstanceFor(connection)
								.addFeature("urn:xmpp:ping");
					}
				});
	}

	public static PingManager getInstance() {
		return instance;
	}

	private PingManager() {
	}

	@Override
	public void onPacket(ConnectionItem connection, final String bareAddress,
			Packet packet) {
		if (!(connection instanceof AccountItem))
			return;
		final String account = ((AccountItem) connection).getAccount();
		if (!(packet instanceof Ping))
			return;
		final Ping ping = (Ping) packet;
		if (ping.getType() != IQ.Type.GET)
			return;
		try {
			ConnectionManager.getInstance().sendPacket(account,
					IQ.createResultIQ(ping));
		} catch (NetworkException e) {
			LogManager.exception(this, e);
		}
	}

}
