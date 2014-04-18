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

import android.graphics.Matrix;

public class ImageCalculations 
{ 	
  private int cameraPreviewWidth, cameraPreviewHeight, selectionWidth, selectionHeight, displayWidth, displayHeight;
  public int selectionX, selectionY, x, y, width, height;
  private boolean imagePortrait, imagePortraitReversed, imageReversedLandscape;	
  public Matrix rotationMatrix;
  private float heightRatio, widthRatio;//ratios between the camera image and the display preview, taking into consideration the orientation

  public void setSelectionSize(int disWidth, int disHeight, int selectX, int selectY, int selectWidth, int selectHeight, int previewImageWidth,int previewImageHeight)
  {
    displayWidth = disWidth;
    displayHeight = disHeight;
    selectionX = selectX;
    selectionY = selectY;
    selectionWidth = selectWidth;
    selectionHeight = selectHeight;
    cameraPreviewWidth = previewImageWidth;
    cameraPreviewHeight = previewImageHeight;
  }

  public void setOrientation(boolean portrait, boolean portraitReversed, boolean reversedLandscape)
  {
    imagePortrait = portrait;
    imagePortraitReversed = portraitReversed;
    imageReversedLandscape = reversedLandscape;
  }
  //Method that checks for the screen rotation and rotates the image corresponding to it 
  //also adjust the height and width ratio matrix depending on orientation
  public void calculateSelection()
  {
    rotationMatrix = new Matrix();
    if(imageReversedLandscape) 
    {
      widthRatio	= (float) cameraPreviewWidth  / displayWidth;
      heightRatio	= (float) cameraPreviewHeight / displayHeight;
      width		= (int) (selectionWidth * widthRatio);
      height		= (int) (selectionHeight * heightRatio);
      x			= (int) (cameraPreviewWidth  - (selectionX * widthRatio) - width);
      y			= (int) (cameraPreviewHeight - (selectionY * heightRatio) - height);
      rotationMatrix.postRotate(180);
    }
    else if(imagePortrait)	
    {
      widthRatio	= (float) cameraPreviewWidth  / displayHeight;
      heightRatio	= (float) cameraPreviewHeight / displayWidth;
      width		= (int) (selectionHeight * heightRatio);
      height		= (int) (selectionWidth * widthRatio);
      x			= (int) (selectionY * heightRatio);
      y			= (int) (cameraPreviewHeight - (selectionX * widthRatio) - height);
      rotationMatrix.postRotate(90);
    } 
    else if(imagePortraitReversed)	
    {
      widthRatio	= (float) cameraPreviewWidth  / displayHeight;
      heightRatio	= (float) cameraPreviewHeight / displayWidth;
      width  		= (int) (selectionHeight * heightRatio);
      height		= (int) (selectionWidth * widthRatio);
      x			= (int) (cameraPreviewWidth - (selectionY * heightRatio) - width);
      y			= (int) (selectionX * widthRatio);
      rotationMatrix.postRotate(270);
    } 
    else//landscape position
    { 
      widthRatio	= (float) cameraPreviewWidth  / displayWidth;
      heightRatio	= (float) cameraPreviewHeight / displayHeight;
      width		= (int) (selectionWidth * widthRatio);
      height		= (int) (selectionHeight * heightRatio);
      x			= (int) (selectionX * widthRatio);
      y			= (int) (selectionY * heightRatio);
      //no rotation needed
    }
  }
}
