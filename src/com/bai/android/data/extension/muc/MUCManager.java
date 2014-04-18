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
package com.bai.android.data.extension.muc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.MUCUser;

import android.database.Cursor;

import com.bai.android.data.Application;
import com.bai.android.data.LogManager;
import com.bai.android.data.NetworkException;
import com.bai.android.data.OnLoadListener;
import com.bai.android.data.account.AccountItem;
import com.bai.android.data.account.AccountManager;
import com.bai.android.data.connection.ConnectionItem;
import com.bai.android.data.connection.ConnectionManager;
import com.bai.android.data.connection.ConnectionThread;
import com.bai.android.data.connection.OnPacketListener;
import com.bai.android.data.entity.BaseEntity;
import com.bai.android.data.message.AbstractChat;
import com.bai.android.data.message.ChatAction;
import com.bai.android.data.message.MessageManager;
import com.bai.android.data.notification.EntityNotificationProvider;
import com.bai.android.data.notification.NotificationManager;
import com.bai.android.data.roster.RosterManager;
import com.bai.android.ui.InvitationCallback;
import com.bai.androiddev.R;
import com.bai.xmpp.muc.MUC;

/**
 * Manage multi user chats.
 * 
 * Warning: We are going to remove SMACK components.
 * 
 */
public class MUCManager implements OnLoadListener, OnPacketListener, InvitationCallback {

  private final EntityNotificationProvider<RoomInvite> inviteProvider;

  private final EntityNotificationProvider<RoomAuthorizationError> authorizationErrorProvider;

  private final static MUCManager instance;

  public class PersistentMUCChats {
    public final static String GLOBAL = "room1@muc.HOSTNAME";
    public final static String RECOMMENDED = "room2@muc.HOSTNAME";
    public final static String NOTICE = "room3@muc.HOSTNAME";
  }

  static {
    instance = new MUCManager();
    Application.getInstance().addManager(instance);
  }

  public static MUCManager getInstance() {
    return instance;
  }

  private MUCManager() {
    inviteProvider = new EntityNotificationProvider<RoomInvite>(R.drawable.ic_stat_subscribe);
    authorizationErrorProvider = new EntityNotificationProvider<RoomAuthorizationError>(R.drawable.ic_stat_auth_failed);
  }

  @Override
  public void onLoad() {
    final Collection<RoomChat> roomChats = new ArrayList<RoomChat>();
    final Collection<RoomChat> needJoins = new ArrayList<RoomChat>();
    Cursor cursor = RoomTable.getInstance().list();
    try {
      if (cursor.moveToFirst()) {
        do {
          RoomChat roomChat = new RoomChat(
              RoomTable.getAccount(cursor),
              RoomTable.getRoom(cursor),
              RoomTable.getNickname(cursor),
              RoomTable.getPassword(cursor));
          if (RoomTable.needJoin(cursor))
            needJoins.add(roomChat);
          roomChats.add(roomChat);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    Application.getInstance().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        onLoaded(roomChats, needJoins);
      }
    });
  }

  private void onLoaded(Collection<RoomChat> roomChats,
      Collection<RoomChat> needJoins) {
    for (RoomChat roomChat : roomChats) {
      AbstractChat abstractChat = MessageManager.getInstance().getChat(
          roomChat.getAccount(), roomChat.getUser());
      if (abstractChat != null)
        MessageManager.getInstance().removeChat(abstractChat);
      MessageManager.getInstance().addChat(roomChat);
      if (needJoins.contains(roomChat))
        roomChat.setState(RoomState.waiting);
    }
    NotificationManager.getInstance().registerNotificationProvider(inviteProvider);
    NotificationManager.getInstance().registerNotificationProvider(authorizationErrorProvider);
  }

  /**
   * @param account
   * @param room
   * @return <code>null</code> if does not exists.
   */
  public RoomChat getRoomChat(String account, String room) {
    AbstractChat chat = MessageManager.getInstance().getChat(account, room);
    if (chat != null && chat instanceof RoomChat)
      return (RoomChat) chat;
    return null;
  }

  /**
   * @param account
   * @param room
   * @return Whether there is such room.
   */
  public boolean hasRoom(String account, String room) {
    return getRoomChat(account, room) != null;
  }

  /**
   * @param account
   * @param room
   * @return nickname or empty string if room does not exists.
   */
  public String getNickname(String account, String room) {
    RoomChat roomChat = getRoomChat(account, room);
    if (roomChat == null)
      return "";
    return roomChat.getNickname();
  }

  /**
   * @param account
   * @param room
   * @return password or empty string if room does not exists.
   */
  public String getPassword(String account, String room) {
    RoomChat roomChat = getRoomChat(account, room);
    if (roomChat == null)
      return "";
    return roomChat.getPassword();
  }

  /**
   * @param account
   * @param room
   * @return list of occupants or empty list.
   */
  public Collection<Occupant> getOccupants(String account, String room) {
    RoomChat roomChat = getRoomChat(account, room);
    if (roomChat == null)
      return Collections.emptyList();
    return roomChat.getOccupants();
  }

  /**
   * @param account
   * @param room
   * @return <code>null</code> if there is no such invite.
   */
  public RoomInvite getInvite(String account, String room) {
    return inviteProvider.get(account, room);
  }

  public void removeInvite(RoomInvite abstractRequest) {
    inviteProvider.remove(abstractRequest);
  }

  public void removeRoom(final String account, final String room) {
    removeInvite(getInvite(account, room));
    RoomChat roomChat = getRoomChat(account, room);
    if (roomChat == null)
      return;
    leaveRoom(account, room);
    MessageManager.getInstance().removeChat(roomChat);
    RosterManager.getInstance().onContactChanged(account, room);
    Application.getInstance().runInBackground(new Runnable() {
      @Override
      public void run() {
        RoomTable.getInstance().remove(account, room);
      }
    });
  }

  public void setupRoom() throws XMPPException {
  }


  /**
   * Creates or updates existed room.
   * 
   * @param account
   * @param room
   * @param nickname
   * @param password
   * @throws XMPPException 
   */
  public void createRoom(String account, String room, String nickname, String password, boolean join) { 
    removeInvite(getInvite(account, room));
    AbstractChat chat = MessageManager.getInstance().getChat(account, room);
    RoomChat roomChat;
    if (chat == null || !(chat instanceof RoomChat)) {
      if (chat != null)
        MessageManager.getInstance().removeChat(chat);
      roomChat = new RoomChat(account, room, nickname, password);
      MessageManager.getInstance().addChat(roomChat);
    } else {
      roomChat = (RoomChat) chat;
      roomChat.setNickname(nickname);
      roomChat.setPassword(password);
    }

    requestToWriteRoom(account, room, nickname, password, join);
    if (join) joinRoom(account, room, true);
  }

  // current user account
  private String account;
  // list of user to act with
  private ArrayList<BaseEntity> userList;
  // current roomnName
  private String roomName;
  // server related limit for jid max char length
  private final int maxRoomNameLength = 128;
  // send chat invitations flag
  private boolean invitationsPending = false;
  private boolean pendingIncomingRoomInvitation = false;

  /** Callback after MUC room creation. */
  @Override
  public void invitationCallback() {
    if (invitationsPending == false) return; //no invitation are pending - abort

    if (account == null || roomName == null || userList == null) return;

    try {
      for (int i = 0; i < userList.size(); i++)
        MUCManager.getInstance().invite(account, roomName, userList.get(i).getUser());
      invitationsPending = false;
    } catch (NetworkException e) {
      e.printStackTrace();
    }
  }

  /**
   * Auto assign a room name, create room and join with 2+ users (excluding client) 
   * return - the room name for the group
   */
  public String createJoinInviteRoom(String account, ArrayList<BaseEntity> users, String nickname, String password) {
    this.account = account;
    userList = users;

    String username = null;
    if (account.indexOf("@") != -1) {
      username = account.substring(0, account.indexOf("@"));
    }
    StringBuilder roomNameBuild = new StringBuilder(username + "|");

    if (userList.size() < 2) return null;

    for (int i = 0; i < userList.size() - 1; i++) {
      roomNameBuild.append(userList.get(i).getNode());
      roomNameBuild.append("|");
    }

    roomNameBuild.append(userList.get(userList.size() - 1).getNode());
    roomNameBuild.append("@muc.");
    roomNameBuild.append(Application.getInstance().getResources().getString(R.string.host_name));

    roomName = roomNameBuild.toString();
    if (roomName.length() > maxRoomNameLength) return null;

    //Create room
    removeInvite(getInvite(account, roomName));
    AbstractChat chat = MessageManager.getInstance().getChat(account, roomName);
    RoomChat roomChat;
    if (chat == null || !(chat instanceof RoomChat)) {
      if (chat != null)
        MessageManager.getInstance().removeChat(chat);
      roomChat = new RoomChat(account, roomName, nickname, password);
      MessageManager.getInstance().addChat(roomChat);
    } else {
      roomChat = (RoomChat) chat;
      roomChat.setNickname(nickname);
      roomChat.setPassword(password);
    }

    requestToWriteRoom(account, roomName, nickname, password, true);
    joinRoom(account, roomName, true);

    // When room is ready - invite users to join
    invitationsPending = true;
    roomChat.registerInvitationCallback(this);

    return roomName;
  }

  private void requestToWriteRoom(final String account, final String room,
      final String nickname, final String password, final boolean join) {
    Application.getInstance().runInBackground(new Runnable() {
      @Override
      public void run() {
        RoomTable.getInstance().write(account, room, nickname, password, join);
      }
    });
  }

  /**
   * @param account
   * @param room
   * @return Whether room is disabled.
   */
  public boolean isDisabled(final String account, final String room) {
    RoomChat roomChat = getRoomChat(account, room);
    return roomChat == null || roomChat.getState() == RoomState.unavailable;
  }

  /**
   * @param account
   * @param room
   * @return Whether connected is establish or connection is in progress.
   */
  public boolean inUse(final String account, final String room) {
    RoomChat roomChat = getRoomChat(account, room);
    return roomChat != null && roomChat.getState().inUse();
  }

  /**
   * Requests to join to the room.
   * 
   * @param account
   * @param room
   * @param requested
   *            Whether user request to join the room.
   */
  public void joinRoom(final String account, final String room, boolean requested) {
    final XMPPConnection xmppConnection;
    final RoomChat roomChat;
    final String nickname;
    final String password;
    final Thread thread;
    roomChat = getRoomChat(account, room);
    if (roomChat == null) {
      Application.getInstance().onError(R.string.ENTRY_IS_NOT_FOUND);
      return;
    }
    RoomState state = roomChat.getState();
    if (state == RoomState.available || state == RoomState.occupation) {
      //			Application.getInstance().onError(R.string.ALREADY_JOINED);
      return;
    }
    if (state == RoomState.creating || state == RoomState.joining) {
      Application.getInstance().onError(R.string.ALREADY_IN_PROGRESS);
      return;
    }
    nickname = roomChat.getNickname();
    password = roomChat.getPassword();
    //		requestToWriteRoom(account, room, nickname, password, true); NOT NECCESSARY, ROOM SHOULD ALREADY EXIST AT THIS POINT

    ConnectionThread connectionThread = AccountManager.getInstance().getAccount(account).getConnectionThread();
    if (connectionThread == null) {
      Application.getInstance().onError(R.string.NOT_CONNECTED);
      return;
    }
    xmppConnection = connectionThread.getXMPPConnection();
    if (xmppConnection == null) {
      Application.getInstance().onError(R.string.NOT_CONNECTED);
      return;
    }
    final MultiUserChat multiUserChat;
    try {
      multiUserChat = new MultiUserChat(xmppConnection, room);
    } catch (IllegalStateException e) {
      Application.getInstance().onError(R.string.NOT_CONNECTED);
      return;
    }
    roomChat.setState(RoomState.joining);
    roomChat.setMultiUserChat(multiUserChat);
    roomChat.setRequested(requested);
    thread = new Thread("Join to room " + room + " from " + account) {
      @Override
      public void run() {
        try {
          if (roomChat.getMultiUserChat() != multiUserChat)
            return;
          multiUserChat.join(nickname, password);
          Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (roomChat.getMultiUserChat() != multiUserChat)
                return;
              if (roomChat.getState() == RoomState.joining)
                roomChat.setState(RoomState.occupation);
              removeAuthorizationError(account, room);
              RosterManager.getInstance().onContactChanged(
                  account, room);
            }
          });
          return;
        } catch (final XMPPException e) {
          Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (roomChat.getMultiUserChat() != multiUserChat)
                return;
              roomChat.setState(RoomState.error);
              addAuthorizationError(account, room);
              if (e.getXMPPError() != null && e.getXMPPError().getCode() == 409)
                Application.getInstance().onError(R.string.NICK_ALREADY_USED);
              else if (e.getXMPPError() != null && e.getXMPPError().getCode() == 401)
                Application.getInstance().onError(R.string.AUTHENTICATION_FAILED);
              else
                Application.getInstance().onError(R.string.NOT_CONNECTED);
              RosterManager.getInstance().onContactChanged(account, room);
            }
          });
          return;
        } catch (IllegalStateException e) {
        } catch (RuntimeException e) {
          LogManager.exception(this, e);
        } catch (Exception e) {
          LogManager.exception(this, e);
        }
        Application.getInstance().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (roomChat.getMultiUserChat() != multiUserChat)
              return;
            roomChat.setState(RoomState.waiting);
            Application.getInstance().onError(R.string.NOT_CONNECTED);
            RosterManager.getInstance().onContactChanged(account, room);
          }
        });
      }
    };
    thread.setDaemon(true);
    thread.start();
  }

  public void leaveRoom(String account, String room) {
    final MultiUserChat multiUserChat;
    RoomChat roomChat = getRoomChat(account, room);
    if (roomChat == null)
      return;
    multiUserChat = roomChat.getMultiUserChat();
    roomChat.setState(RoomState.unavailable);
    roomChat.setRequested(false);
    roomChat.newAction(roomChat.getNickname(), null, ChatAction.leave);
    requestToWriteRoom(account, room, roomChat.getNickname(),
        roomChat.getPassword(), false);
    if (multiUserChat != null) {
      Thread thread = new Thread("Leave to room " + room + " from "
          + account) {
        @Override
        public void run() {
          try {
            multiUserChat.leave();
          } catch (IllegalStateException e) {
            // Do nothing
          }
        }
      };
      thread.setDaemon(true);
      thread.start();
    }
    RosterManager.getInstance().onContactChanged(account, room);
  }

  @Override
  public void onPacket(ConnectionItem connection, String bareAddress,
      Packet packet) {
    if (!(connection instanceof AccountItem))
      return;
    String account = ((AccountItem) connection).getAccount();
    if (bareAddress == null || !(packet instanceof Message))
      return;
    Message message = (Message) packet;
    if (message.getType() != Message.Type.normal
        && message.getType() != Message.Type.chat)
      return;
    MUCUser mucUser = MUC.getMUCUserExtension(packet);
    if (mucUser == null || mucUser.getInvite() == null)
      return;
    RoomChat roomChat = getRoomChat(account, bareAddress);
    if (roomChat == null || !roomChat.getState().inUse()) {
      String inviter = mucUser.getInvite().getFrom();
      if (inviter == null)
        inviter = bareAddress;
      inviteProvider.add(new RoomInvite(account, bareAddress, inviter,
          mucUser.getInvite().getReason(), mucUser.getPassword()),
          true);
    }
  }

  /**
   * Sends invitation.
   * 
   * @param account
   * @param room
   * @param user
   * @throws NetworkException
   */
  public void invite(String account, String room, String user) throws NetworkException {

    RoomChat roomChat = getRoomChat(account, room);
    if (roomChat == null || roomChat.getState() != RoomState.available) {
      Application.getInstance().onError(R.string.NOT_CONNECTED);
      return;
    }
    Message message = new Message(room);
    MUCUser mucUser = new MUCUser();
    MUCUser.Invite invite = new MUCUser.Invite();
    invite.setTo(user);
    invite.setReason("");
    mucUser.setInvite(invite);
    message.addExtension(mucUser);
    ConnectionManager.getInstance().sendPacket(account, message);
    roomChat.putInvite(message.getPacketID(), user);
    //		roomChat.newAction(roomChat.getNickname(), user, ChatAction.invite_sent); Prints to the chat view that users were invited
  }

  public void removeAuthorizationError(String account, String room) {
    authorizationErrorProvider.remove(account, room);
  }

  public void addAuthorizationError(String account, String room) {
    authorizationErrorProvider.add(
        new RoomAuthorizationError(account, room), null);
  }

  public void setPendingIncomingInvitation(boolean val) {
    pendingIncomingRoomInvitation = val;
  }

  public boolean getPendingIncomingInvitation() {
    return pendingIncomingRoomInvitation;
  }


  /** Iterates over a list of persistent chats and creates the rooms if they don't exist. */
  public void preparePersistentChats() {
    //		Cursor mucs = RoomTable.getInstance().getRooms(); GETTING OF CURRENT ROOMS IN DB
    //		while (mucs.moveToNext()) {
    //			StringBuilder msg = new StringBuilder();
    //			for (int i = 0; i < mucs.getColumnCount(); i ++) msg.append(mucs.getString(i)+ " ");
    //		}
    createPersistenRoom(PersistentMUCChats.GLOBAL);
    createPersistenRoom(PersistentMUCChats.RECOMMENDED);
    createPersistenRoom(PersistentMUCChats.NOTICE);
    //Create room
  }

  /** Checks if a persistent room exists and creates it if not. */
  public void createPersistenRoom(final String roomName) {
    if (AccountManager.getInstance().getAccounts().size() > 0){
      final String account = AccountManager.getInstance().getAccounts().iterator().next();

      //			AccountItem accountItem = AccountManager.getInstance().getAccount(account);
      //			ConnectionState state = accountItem.getState();
      //			if (!state.isConnected()) {
      //				return;
      //			}
      ConnectionThread connectionThread = AccountManager.getInstance().getAccount(account).getConnectionThread();
      if (connectionThread == null) {
        return;
      }
      XMPPConnection xmppcon = connectionThread.getXMPPConnection();

      if (xmppcon != null && xmppcon.isConnected() && xmppcon.isAlive()) {

        //Check if room exists
        try {
          RoomInfo roomInfo = MultiUserChat.getRoomInfo(xmppcon, roomName);
          //if there is roomInfo - the room exists, so return (no need to create it)
          if (roomInfo != null) {
            //						joinRoom(account, roomName, true);
            MUCManager.getInstance().createRoom(account, roomName, AccountManager.getInstance().getNickName(account), null, true);
            return;
          }
        } catch (XMPPException e) {
          // nothing to do, continue with creating the persistent rooms
          e.printStackTrace();
        }
        try {
          MultiUserChat multiUserChat = new MultiUserChat(
              xmppcon,
              roomName);
          Form form = multiUserChat.getConfigurationForm();
          Form submitForm = form.createAnswerForm();
          for (Iterator<FormField> fields = submitForm.getFields(); fields.hasNext();) {
            FormField field = (FormField) fields.next();
            if (!FormField.TYPE_HIDDEN.equals(field.getType())
                && field.getVariable() != null) {
              submitForm.setDefaultAnswer(field.getVariable());
            }
          }
          submitForm.setAnswer("muc#roomconfig_roomname", roomName);
          //					submitForm.setAnswer("muc#roomconfig_maxusers", 250);
          submitForm.setAnswer("muc#roomconfig_membersonly", false);
          submitForm.setAnswer("muc#roomconfig_roomdesc", "Global chat room");
          submitForm.setAnswer("muc#roomconfig_publicroom", true);
          submitForm.setAnswer("muc#roomconfig_persistentroom", true);

          multiUserChat.sendConfigurationForm(submitForm);
          multiUserChat.join(AccountManager.getInstance().getNickName(account), null);
          createRoom(account, roomName, AccountManager.getInstance().getNickName(account), null, true);

        } catch (XMPPException e) {
          e.printStackTrace();
        }


        if (roomName.equals(PersistentMUCChats.NOTICE)) {
          removeInvite(getInvite(account, roomName));
          AbstractChat chat = MessageManager.getInstance().getChat(account, roomName);
          RoomChat roomChat;
          roomChat = (RoomChat) chat;

          if (chat == null || AccountManager.getInstance() == null) {
            return; //TODO INVESTIGATE, SOMETIMES ON REGISTRAITON IT CRASHES ON THE LINE BELOW WITH NPE
          }

          roomChat.setNickname(AccountManager.getInstance().getNickName(account));
          roomChat.setPassword(null);

          requestToWriteRoom(account, roomName, AccountManager.getInstance().getNickName(account), null, true);
          joinRoom(account, roomName, true);

          // When room is ready - invite users to join
          invitationsPending = true;
          roomChat.registerInvitationCallback(this);
        }
      }
    }
  }
}
