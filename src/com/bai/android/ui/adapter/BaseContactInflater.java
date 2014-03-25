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
package com.bai.android.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bai.android.data.SettingsManager;
import com.bai.android.data.roster.AbstractContact;
import com.bai.android.ui.helper.AbstractAvatarInflaterHelper;
import com.bai.androiddev.R;

/**
 * Provides views and fills them with data for {@link BaseContactAdapter}.
 * 
 * @author alexander.ivanov
 * 
 */
public abstract class BaseContactInflater {

	private static boolean mucSelection = false;
	
	final Activity activity;

	final LayoutInflater layoutInflater;

	final AbstractAvatarInflaterHelper avatarInflaterHelper;

	/**
	 * Repeated shadow for drawable.
	 */
	final BitmapDrawable shadowDrawable;

	/**
	 * Managed adapter.
	 */
	BaseAdapter adapter;

	public BaseContactInflater(Activity activity) {
		this.activity = activity;
		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		avatarInflaterHelper = AbstractAvatarInflaterHelper.createAbstractContactInflaterHelper();

		Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.shadow);
		shadowDrawable = new BitmapDrawable(bitmap);
		shadowDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	}

	/**
	 * Sets managed adapter.
	 * 
	 * @param adapter
	 */
	void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Creates new view for specified position.
	 * 
	 * @param position
	 * @param parent
	 * @return
	 */
	abstract View createView(int position, ViewGroup parent);

	/**
	 * Creates new instance of ViewHolder.
	 * 
	 * @param position
	 * @param view
	 * @return
	 */
	abstract ViewHolder createViewHolder(int position, View view);

	/**
	 * Returns status text.
	 * 
	 * @param abstractContact
	 * @return
	 */
	String getStatusText(AbstractContact abstractContact) {
		return abstractContact.getStatusText();
	}

	/**
	 * Fills view for {@link BaseContactAdapter}.
	 * 
	 * @param view - view to be inflated.
	 * @param abstractContact - contact to be shown.
	 */
	public void getView(View view, final AbstractContact abstractContact) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		if (abstractContact.isConnected())
			viewHolder.shadow.setVisibility(View.GONE);
		else
			viewHolder.shadow.setVisibility(View.VISIBLE);

		viewHolder.color.setImageLevel(abstractContact.getColorLevel());

		if (SettingsManager.contactsShowAvatars()) {
			viewHolder.avatar.setVisibility(View.VISIBLE);
			viewHolder.avatar.setImageDrawable(abstractContact
					.getAvatarForContactList());
			avatarInflaterHelper.updateAvatar(viewHolder.avatar,
					abstractContact);
			((RelativeLayout.LayoutParams) viewHolder.panel.getLayoutParams())
					.addRule(RelativeLayout.RIGHT_OF, R.id.avatar);
		} else {
			viewHolder.avatar.setVisibility(View.GONE);
			((RelativeLayout.LayoutParams) viewHolder.panel.getLayoutParams())
					.addRule(RelativeLayout.RIGHT_OF, R.id.color);
		}
		
		String name = abstractContact.getName();
		if (name != null && name != "" && name.indexOf("@") > 0) {
			name = name.substring(0, name.indexOf("@")); // clear the domain part
		}
		
		viewHolder.name.setText(name);
		final String statusText = getStatusText(abstractContact);
		if ("".equals(statusText)) {
			viewHolder.name.getLayoutParams().height = activity.getResources()
					.getDimensionPixelSize(
							R.dimen.contact_name_height_hide_status);
			viewHolder.name.setGravity(Gravity.CENTER_VERTICAL);
			viewHolder.status.setVisibility(View.GONE);
		} else {
			viewHolder.name.getLayoutParams().height = activity.getResources()
					.getDimensionPixelSize(
							R.dimen.contact_name_height_show_status);
			viewHolder.name.setGravity(Gravity.BOTTOM);
			viewHolder.status.setText(statusText);
			viewHolder.status.setVisibility(View.VISIBLE);
		}
		if (mucSelection) {
			viewHolder.mucCheckBox.setVisibility(View.VISIBLE);
			viewHolder.mucCheckBox.setChecked(abstractContact.isSelected());
		}
		else viewHolder.mucCheckBox.setVisibility(View.GONE);
		viewHolder.shadow.setBackgroundDrawable(shadowDrawable);
	}

	public static void toggleMucSelector() {
		mucSelection = !mucSelection;
	}

	/**
	 * Holder for views in contact item.
	 */
	static class ViewHolder {

		final CheckBox mucCheckBox;
		final ImageView color;
		final ImageView avatar;
		final RelativeLayout panel;
		final TextView name;
		final TextView status;
		final ImageView shadow;

		public ViewHolder(View view) {
			mucCheckBox = (CheckBox) view.findViewById(R.id.muc_checkbox);
			color = (ImageView) view.findViewById(R.id.color);
			avatar = (ImageView) view.findViewById(R.id.avatar);
			panel = (RelativeLayout) view.findViewById(R.id.panel);
			name = (TextView) view.findViewById(R.id.name);
			status = (TextView) view.findViewById(R.id.status);
			shadow = (ImageView) view.findViewById(R.id.shadow);
		}
	}
}
