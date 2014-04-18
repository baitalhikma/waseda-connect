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

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bai.androiddev.R;

public class OtherSubTab implements View.OnClickListener {

  private View contentchild;
  private LinearLayout contentHolder;
  private LayoutParams contentHolderLP;

  private OtherActivity mainActivity;
  private int selectedBkgrCol, defaultBkgrCol;
  private Button currentTab;

  public OtherSubTab(OtherActivity mainActivity, final int currentView) {

    // Inflating SubTabBar
    LinearLayout item = (LinearLayout) mainActivity.findViewById(R.id.sub_tab_holder);

    // Adding layout params which got lost due to inflating
    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    this.mainActivity = mainActivity;

    selectedBkgrCol = mainActivity.getResources().getColor(R.color.bordeaux);
    defaultBkgrCol = mainActivity.getResources().getColor(R.color.dark_bordeaux);

    View child = mainActivity.getLayoutInflater().inflate(R.layout.sub_tab_bar_other, null);
    child.setLayoutParams(lp);
    item.addView(child);

    ((Button) mainActivity.findViewById(R.id.profile_button)).setOnClickListener(this);
    ((Button) mainActivity.findViewById(R.id.setting_button)).setOnClickListener(this);
    ((Button) mainActivity.findViewById(R.id.notice_button)).setOnClickListener(this);
    ((Button) mainActivity.findViewById(R.id.map_button)).setOnClickListener(this);

    contentHolder = (LinearLayout) mainActivity.findViewById(R.id.content_holder_other);
    contentHolderLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    //Inflate profile when the activity is started
    ((Button) mainActivity.findViewById(currentView)).performClick();
  }

  public void removePreviousView() {
    contentHolder.removeView(contentchild);
  }

  public void inflateContent(int targetContent) {
    contentchild = mainActivity.getLayoutInflater().inflate(targetContent, null);

    // Adding layout params which got lost due to inflating
    contentchild.setLayoutParams(contentHolderLP);
    contentHolder.addView(contentchild);
  }

  public void openTab(int targetViewId) {
    ((Button) mainActivity.findViewById(targetViewId)).performClick();
  }

  @Override
  public void onClick(View view) {
    if (currentTab != null) {
      // don't change the view if it's the current one
      if (view.getId() == currentTab.getId()) {
        return;
      }
      currentTab.setBackgroundColor(defaultBkgrCol);

      // the map view is a fragment (by G Api) and is better to just be set to GONE, instead of removed 
      // all other view are removed and then the container is reinflated
      if (currentTab.getId() == R.id.map_button) {
        mainActivity.removeMap();
      } else {
        removePreviousView();
      }
    }

    currentTab = (Button) view;
    currentTab.setBackgroundColor(selectedBkgrCol);

    switch (currentTab.getId()) {
    case R.id.profile_button:
      inflateContent(R.layout.profile);
      mainActivity.callProfile();
      break;
    case R.id.setting_button:
      inflateContent(R.layout.setting);
      mainActivity.callSettings();
      break;
    case R.id.notice_button:
      inflateContent(R.layout.notice);
      mainActivity.callNotice();
      break;
    case R.id.map_button:
      // if the map view is not inflated yet - do it (done only once)
      if (!mainActivity.mapInflated()){
        inflateContent(R.layout.map);
      }
      mainActivity.callMap();
      break;
    default:
      break;
    }
  }

  public int getCurrentTabId() {
    return currentTab.getId();
  }
}
