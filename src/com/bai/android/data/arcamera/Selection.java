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
package com.bai.android.data.arcamera;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.bai.androiddev.R;

public class Selection {
  public float x1, y1, x2, y2;
  public ImageView selectionFrame, dimmed1, dimmed2, dimmed3, dimmed4;;
  public int selectionWidth, selectionHeight, selectionX, selectionY,
  displayWidth, displayHeight;
  private SharedPreferences preferences;
  private SharedPreferences.Editor editor;
  private int offsetFromTop;

  public Selection(Activity act, int displayWidth, int displayHeight) {
    selectionFrame = (ImageView) act.findViewById(R.id.selectionFrame);
    dimmed1 = (ImageView) act.findViewById(R.id.dimmed1);
    dimmed2 = (ImageView) act.findViewById(R.id.dimmed2);
    dimmed3 = (ImageView) act.findViewById(R.id.dimmed3);
    dimmed4 = (ImageView) act.findViewById(R.id.dimmed4);
    this.displayWidth = displayWidth;
    this.displayHeight = displayHeight;
    preferences = PreferenceManager.getDefaultSharedPreferences(act
        .getApplicationContext());
    editor = preferences.edit();
    offsetFromTop = 0;
  }
  public void setOffsetFromTop(int height){
    this.offsetFromTop = height;
  }

  /**
   * reassigns a new size and position to the selection layout and stores the
   * values
   */
  public void repositionSelection() {
    selectionFrame.layout(Math.round(x1), Math.round(y1), Math.round(x2),
        Math.round(y2));
    dimmed1.layout(0, offsetFromTop, displayWidth, Math.round(y1));// top dimmed element
    dimmed2.layout(0, Math.round(y2), displayWidth, displayHeight); // bottom
    // dimmed
    // element
    dimmed3.layout(0, Math.round(y1), Math.round(x1), Math.round(y2));// left
    // dimmed
    // element
    dimmed4.layout(Math.round(x2), Math.round(y1), displayWidth,
        Math.round(y2));// right dimmed element
    // Setting the desired output image width and height and starting
    // coordinates
    // Which are used to determine the starting point of the image
    selectionWidth = Math.round(x2 - x1);
    selectionHeight = Math.round(y2 - y1);
    selectionX = Math.round(x1);
    selectionY = Math.round(y1);

    saveCurrentSelection();
  }

  /**
   * Calculations for the getPastSelection
   */
  public void getPastSelectionCalculations(float selectionX1Ratio,
      float selectionY1Ratio, float selectionX2Ratio,
      float selectionY2Ratio) {
    if ((selectionX2Ratio > selectionX1Ratio)
        && (selectionY2Ratio > selectionY1Ratio)
        && (selectionX1Ratio > 0) && (selectionY1Ratio > 0)
        && (selectionX2Ratio < 1) && (selectionY2Ratio < 1)) {
      float calcSelX1 = selectionX1Ratio * displayWidth;
      float calcSelY1 = selectionY1Ratio * displayHeight;
      float calcSelX2 = selectionX2Ratio * displayWidth;
      float calcSelY2 = selectionY2Ratio * displayHeight;
      updateSelection2Pointers(calcSelX1, calcSelY1, calcSelX2, calcSelY2);
    }

  }

  /**
   * Function that updates the selectionFrame size and position x1,y1,x2,y2
   * are coordinates acquired from TouchView onTouchEvent
   */
  public void updateSelection2Pointers(float x1, float y1, float x2, float y2) {
    selectionFrame.setVisibility(ImageView.VISIBLE);
    if (x2 < x1) {
      float temp = x1;
      x1 = x2;
      x2 = temp;
    }
    if (y2 < y1) {
      float temp = y1;
      y1 = y2;
      y2 = temp;
    }
    this.x1 = x1;
    if (y1 < offsetFromTop) {
      y1 = offsetFromTop;
    }
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;

  }

  /**
   * Function that updates the selectionFrame size and position by moving the
   * closer of the corners to the point selected by the user
   * 
   * @x x coordinate of touch point
   * @y y coorinate of touch point
   */
  public void updateSelection1Pointer(float x, float y) {
    float x1 = selectionFrame.getLeft();
    float y1 = selectionFrame.getTop();
    float x2 = x1 + selectionFrame.getWidth();
    float y2 = y1 + selectionFrame.getHeight();
    if (Math.abs(x1 - x) < Math.abs(x2 - x)) {
      x1 = x;
    } else {
      x2 = x;
    }

    if (Math.abs(y1 - y) < Math.abs(y2 - y)) {
      y1 = y;
    } else {
      y2 = y;
    }

    this.x1 = x1;
    if (y1 < offsetFromTop) {
      y1 = offsetFromTop;
    }
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  /**
   * Restores last selection coordinates, reproducing the last selection ratio
   * based on coordinates and screen size.
   */
  public void getPastSelection() {
    getPastSelectionCalculations(
        preferences.getFloat(Constant.PRF_SELEC_X1, 0),
        preferences.getFloat(Constant.PRF_SELEC_Y1, 0),
        preferences.getFloat(Constant.PRF_SELEC_X2, 0),
        preferences.getFloat(Constant.PRF_SELEC_Y2, 0));
    repositionSelection();
  }

  /**
   * Saves the last selection coordinates in the screen, so they can be
   * reproduced later. Stores information about the ratio between coordinates
   * and screen size.
   * 
   * @param x1
   *            - left pixel coordinate of selection view
   * @param y1
   *            - top pixel coordinate of selection view
   * @param x2
   *            - right pixel coordinate of selection view
   * @param y2
   *            - bottom pixel coordinate of selection view
   */
  private boolean saveCurrentSelection() {
    int x1 = selectionFrame.getLeft();
    int y1 = selectionFrame.getTop();
    int x2 = selectionFrame.getRight();
    int y2 = selectionFrame.getBottom();
    if ((x2 > x1) && (y2 > y1) && (x1 > 0) && (y2 > 0)
        && (x2 < displayWidth) && (y2 < displayHeight)) {
      editor = preferences.edit();
      editor.putFloat(Constant.PRF_SELEC_X1, (float) x1 / displayWidth);
      editor.putFloat(Constant.PRF_SELEC_Y1, (float) y1 / displayHeight);
      editor.putFloat(Constant.PRF_SELEC_X2, (float) x2 / displayWidth);
      editor.putFloat(Constant.PRF_SELEC_Y2, (float) y2 / displayHeight);
      return editor.commit();
    }
    return false;
  }
}