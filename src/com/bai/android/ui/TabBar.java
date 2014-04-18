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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bai.androiddev.R;

public class TabBar implements OnClickListener {

  private Activity mainActivity;
  private int selectedBkgrCol, selectedTextCol, defaultBkgrCol, defaultTextCol;
  private LinearLayout item;
  private Button currentTab;

  public TabBar(final Activity mainActivity, final int currentView) {

    this.mainActivity = mainActivity;

    // Inflating TabBar
    item = (LinearLayout) mainActivity.findViewById(R.id.tab_holder);
    View child = mainActivity.getLayoutInflater().inflate(R.layout.tab_bar, null);
    // Adding layout params which got lost due to inflating
    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    child.setLayoutParams(lp);
    // Add the view
    item.addView(child);

    // Instantiate buttons
    Button friendsButton = (Button) mainActivity.findViewById(R.id.friends_button);
    Button chatButton = (Button) mainActivity.findViewById(R.id.chat_button);
    Button bulletinboardButton = (Button) mainActivity.findViewById(R.id.bulletinboard_button);
    Button arcameraButton = (Button) mainActivity.findViewById(R.id.arcamera_button);
    Button otherButton = (Button) mainActivity.findViewById(R.id.other_button);

    friendsButton.setOnClickListener(this);
    chatButton.setOnClickListener(this);
    bulletinboardButton.setOnClickListener(this);
    arcameraButton.setOnClickListener(this);
    otherButton.setOnClickListener(this);

    selectedBkgrCol = mainActivity.getResources().getColor(R.color.dark_bordeaux);
    selectedTextCol = mainActivity.getResources().getColor(R.color.text_white);
    defaultBkgrCol = mainActivity.getResources().getColor(R.color.light_bordeaux);
    defaultTextCol = mainActivity.getResources().getColor(R.color.text_black);

    // select the current tab
    ((Button) mainActivity.findViewById(currentView)).performClick();
  }

  @Override
  public void onClick(View view) {

    // deselect the previous tab
    if (currentTab != null) {
      // ignore clicks on the same view
      if (view.getId() == currentTab.getId()) {
        return;
      }
      currentTab.setBackgroundColor(defaultBkgrCol);
      currentTab.setTextColor(defaultTextCol);
    }

    // assign and select the current tab
    currentTab = (Button) view;

    currentTab.setBackgroundColor(selectedBkgrCol);
    currentTab.setTextColor(selectedTextCol);

    String targetActivityClass = null;

    switch (view.getId()) {
    case R.id.friends_button:
      targetActivityClass = ContactList.class.getName();
      break;
    case R.id.chat_button:
      targetActivityClass = ContactList.class.getName();
      break;
    case R.id.bulletinboard_button:
      targetActivityClass = BulletinBoardActivity.class.getName();
      break;
    case R.id.arcamera_button:
      targetActivityClass = ARCameraActivity.class.getName();
      registerHeightObserver();
      //			Toast.makeText(mainActivity.getApplicationContext(), "This feature is currently disabled", Toast.LENGTH_LONG).show();
      break;
    case R.id.other_button:
      targetActivityClass = OtherActivity.class.getName();
      break;
    default:
      break;
    }

    if (!mainActivity.getClass().getName().equals(targetActivityClass) && targetActivityClass != null) {
      Intent intent;
      try {
        intent = new Intent(mainActivity.getApplicationContext(), Class.forName(targetActivityClass));

        if (targetActivityClass.equals(ContactList.class.getName())) {
          if (view.getId() == R.id.chat_button) {
            intent = ContactList.createChatIntent(mainActivity);
          }
        }

        mainActivity.startActivity(intent);
        // remove transition animation
        mainActivity.finish();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }

    } else if (mainActivity.getClass() == ContactList.class) {
      if (view.getId() == R.id.friends_button) {
        ((ContactList) mainActivity).showFriends();
      }
      else {
        ((ContactList) mainActivity).showChat();
      }
    };
  }

  public int getHeight() {
    return item.getHeight();
  }

  private void registerHeightObserver(){
    ViewTreeObserver vto = item.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

      @SuppressWarnings("deprecation")
      @SuppressLint("NewApi")
      @Override
      public void onGlobalLayout() {
        // TODO Auto-generated method stub
        int height = item.getHeight();
        ((ARCameraActivity) mainActivity).setTabBarHeight(height);
        ViewTreeObserver vto2 = item.getViewTreeObserver();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
          vto2.removeGlobalOnLayoutListener(this);
        } else {
          vto2.removeOnGlobalLayoutListener(this);
        }

      }

    });
  }

  public int getCurrentTab() {
    return currentTab.getId();
  }
}