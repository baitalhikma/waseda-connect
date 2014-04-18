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
package com.bai.android.ui;

import java.util.Collection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.bai.android.data.ActivityManager;
import com.bai.android.data.Application;
import com.bai.android.data.LogManager;
import com.bai.android.data.account.OnAccountChangedListener;
import com.bai.android.service.BaiService;
import com.bai.android.ui.helper.SingleActivity;
import com.bai.androiddev.R;

public class LoadActivity extends SingleActivity implements
		OnAccountChangedListener {

//	private Animation animation;
//	private View disconnectedView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load);
//		animation = AnimationUtils.loadAnimation(this, R.anim.connection);
//		disconnectedView = findViewById(R.id.disconnected);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Application.getInstance().addUIListener(OnAccountChangedListener.class,
				this);
		if (Application.getInstance().isClosing()) {
			((TextView) findViewById(R.id.text))
					.setText(R.string.application_state_closing);
		} else {
			startService(BaiService.createIntent(this));
//			disconnectedView.startAnimation(animation);
			update();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Application.getInstance().removeUIListener(
				OnAccountChangedListener.class, this);
//		disconnectedView.clearAnimation();
	}

	@Override
	public void onAccountsChanged(Collection<String> accounts) {
		update();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			cancel();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void update() {
		if (Application.getInstance().isInitialized()
				&& !Application.getInstance().isClosing() && !isFinishing()) {
			LogManager.i(this, "Initialized");
			finish();
		}
	}

	private void cancel() {
		finish();
		ActivityManager.getInstance().cancelTask(this);
	}

	public static Intent createIntent(Context context) {
		return new Intent(context, LoadActivity.class);
	}

}
