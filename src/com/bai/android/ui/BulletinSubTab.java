package com.bai.android.ui;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bai.android.data.extension.muc.MUCManager;
import com.bai.androiddev.R;

public class BulletinSubTab implements View.OnClickListener {

	private Button currentTab;
	private BulletinBoardActivity bulletinBoardActivity;
	private int colorSelected, colorNotSelected;

	public BulletinSubTab(BulletinBoardActivity mainActivity, final int currentView) {

		// Inflating SubTabBar
		LinearLayout item = (LinearLayout) mainActivity.findViewById(R.id.sub_tab_holder);

		// Adding layout params which got lost due to inflating
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		bulletinBoardActivity = mainActivity;

		colorSelected = mainActivity.getResources().getColor(R.color.bordeaux);
		colorNotSelected = mainActivity.getResources().getColor(R.color.dark_bordeaux);

		View child = mainActivity.getLayoutInflater().inflate(R.layout.sub_tab_bar_bulletinboard, null);
		child.setLayoutParams(lp);
		item.addView(child);

		((Button) mainActivity.findViewById(R.id.timeline_button)).setOnClickListener(this);
		((Button) mainActivity.findViewById(R.id.post_button)).setOnClickListener(this);
		((Button) mainActivity.findViewById(R.id.shop_recommended_button)).setOnClickListener(this);

		openView(currentView);
	}

	@Override
	public void onClick(View v) {
		openView(v.getId());
	}

	public void openView(int id) {

		if (currentTab != null) {
			currentTab.setBackgroundColor(colorNotSelected);

			// if the tab is already selected - do nothing
			if (currentTab.getId() == id) {
				return;
			}
		}

		currentTab = (Button) bulletinBoardActivity.findViewById(id);
		currentTab.setBackgroundColor(colorSelected);

		switch (id) {
		case R.id.timeline_button:
			bulletinBoardActivity.showTimeline();
			break;
		case R.id.post_button:
			bulletinBoardActivity.changeRoom(MUCManager.PersistentMUCChats.GLOBAL);
			break;
		case R.id.shop_recommended_button:
			bulletinBoardActivity.changeRoom(MUCManager.PersistentMUCChats.RECOMMENDED);
			break;
		default:
			break;
		}
	}
}
