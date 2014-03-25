package com.bai.android.ui;

import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bai.android.data.Application;
import com.bai.android.data.SettingsManager;
import com.bai.android.data.SettingsManager.ChatsHideKeyboard;
import com.bai.android.data.account.AccountManager;
import com.bai.android.data.entity.BaseEntity;
import com.bai.android.data.extension.muc.MUCManager;
import com.bai.android.data.message.AbstractChat;
import com.bai.android.data.message.MessageManager;
import com.bai.android.data.message.OnChatChangedListener;
import com.bai.android.data.roster.OnContactChangedListener;
import com.bai.android.ui.adapter.ChatListAdapter;
import com.bai.android.ui.adapter.ChatMessageAdapter;
import com.bai.androiddev.R;

public class BulletinBoardActivity extends Activity implements OnChatChangedListener, View.OnClickListener,
	OnItemClickListener, OnContactChangedListener {

	private ChatMessageAdapter chatMsgAdapter;
	private ChatListAdapter timelineAdapter;
	private View chatView;
	private String account, chatRoom;
	private ListView itemList;
	/** the view displaying timeline chats or global chat messages */
	private LinearLayout targetView;
	private BulletinSubTab subTab;
	private boolean timelineVisible;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		account = getAccount(intent);

		setContentView(R.layout.bulletin_board);

		// prepare list and chat adapters 
		chatMsgAdapter = new ChatMessageAdapter(this, true);
		timelineAdapter = new ChatListAdapter(this);
		timelineAdapter.setTimelineLayout(true);

		targetView = (LinearLayout) findViewById(R.id.targetChat);
	}

	@Override
	protected void onPause() {
		Application.getInstance().removeUIListener(OnContactChangedListener.class, this);
		Application.getInstance().removeUIListener(OnChatChangedListener.class, this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		new TabBar(this, R.id.bulletinboard_button);

		subTab = new BulletinSubTab(this, R.id.timeline_button);

		Application.getInstance().addUIListener(OnChatChangedListener.class, this);
		Application.getInstance().addUIListener(OnContactChangedListener.class, this);

		super.onResume();
	}

	private void sendMessage() {
		EditText editView = (EditText) chatView.findViewById(R.id.chat_input);
		String text = editView.getText().toString();
		int start = 0;
		int end = text.length();
		while (start < end && (text.charAt(start) == ' ' || text.charAt(start) == '\n'))
			start += 1;
		while (start < end && (text.charAt(end - 1) == ' ' || text.charAt(end - 1) == '\n'))
			end -= 1;
		text = text.substring(start, end);
		if ("".equals(text))
			return;
		editView.setText("");
		sendMessage(text);
		if (SettingsManager.chatsHideKeyboard() == ChatsHideKeyboard.always
				|| (getResources().getBoolean(R.bool.landscape) && SettingsManager
						.chatsHideKeyboard() == ChatsHideKeyboard.landscape)) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
		}
	}

	private void sendMessage(String text) {
		MessageManager.getInstance().sendMessage(account, chatRoom, text);
		chatMsgAdapter.onChange();
	}

	private static String getAccount(Intent intent) {
		if (AccountManager.getInstance().getAccounts().size() > 0)
			return AccountManager.getInstance().getAccounts().iterator().next();
		return null;
	}

	@Override
	public void onChatChanged(String account, String user, boolean incoming) {
		chatMsgAdapter.onChange();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chat_send:
			sendMessage();
			break;
		default:
			break;
		}
	}

	/** Change the view to a global chat room (Post or Shop Recommended). */
	public void changeRoom(String chatRoom) {
		targetView.setBackgroundResource(R.drawable.bkg);

		this.chatRoom = chatRoom; 
		chatMsgAdapter.setChat(account, chatRoom);
		MUCManager.getInstance().createRoom(account, chatRoom, account, null, true);

		targetView.removeAllViews();
		chatView = getLayoutInflater().inflate(R.layout.chat_viewer_item, null);
		itemList = (ListView) chatView.findViewById(android.R.id.list);
		itemList.setAdapter(chatMsgAdapter);
		
		removeListViewBkgSepar();
			
		View title = chatView.findViewById(R.id.title);
		title.setVisibility(View.GONE);
		targetView.addView(chatView);

		chatMsgAdapter.updateInfo();
		View send = chatView.findViewById(R.id.chat_send);
		send.setOnClickListener(this);

		timelineVisible = false;
	}

	/** Display the history/ timeline in the view. */
	public void showTimeline() {
		targetView.setBackgroundColor(getResources().getColor(R.color.bright_foreground_light));

		targetView.removeAllViews();

		chatView = getLayoutInflater().inflate(R.layout.chat_viewer_item, null);

		itemList = new ListView(getApplicationContext());
		itemList.setAdapter(timelineAdapter);
		itemList.setOnItemClickListener(this);

		removeListViewBkgSepar();

		targetView.addView(itemList);

		timelineAdapter.onChange();

		timelineVisible = true;
	}

	private void removeListViewBkgSepar() {
		itemList.setTextFilterEnabled(true);
		itemList.setBackgroundColor(Color.TRANSPARENT);
		itemList.setCacheColorHint(Color.TRANSPARENT);
		itemList.setDivider(null);
		itemList.setDividerHeight(0);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AbstractChat abstractChat = (AbstractChat) parent.getAdapter().getItem(position);
		if (abstractChat.getUser().equalsIgnoreCase(MUCManager.PersistentMUCChats.GLOBAL)) {
			subTab.openView(R.id.post_button);
		} else if (abstractChat.getUser().equalsIgnoreCase(MUCManager.PersistentMUCChats.RECOMMENDED)) {
			subTab.openView(R.id.shop_recommended_button);
		} else {
			startActivity(
					ChatViewer.createIntent(
					this,
					abstractChat.getAccount(),
					abstractChat.getUser()));
		}
	}

	@Override
	public void onContactsChanged(Collection<BaseEntity> entities) {
		if (timelineVisible) timelineAdapter.onChange();
	}
}
