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

import android.graphics.Typeface;
import android.os.Environment;

public class Constant {
  public static final String 	DATA_PATH 				= Environment.getExternalStorageDirectory().toString() + "/WasedaConnect/";
  public static final String 	TESS_FOLDER				= "LOCATION1/";
  public static final String 	DICT_FOLDER				= "LOCATION2/";
  public static final String  SERVER_LOC				= "YOUR SERVER LOCATION";
  public static final String  DICT_ZIP_FILENAME		= "dictionary.zip";
  public static final String  TESS_ZIP_FILENAME		= "jpn.zip";
  public static final String  DICT_FILENAME			= "LOCATION1/DICTIONARY FILE NAME";
  public static final String  TESS_FILENAME			= "LOCATION2/TRAINED DATA FILE NAME";

  public static final Typeface typeface				=  Typeface.create(Typeface.SERIF, Typeface.NORMAL);

  public static final String  SERVER_POST				= "post.php";

  public static final String	PUBLIC_ACCOUNT_PASS		= "SOME PASSWORD";

  /** List of language files and their names from here: http://code.google.com/p/tesseract-ocr/downloads/list */
  public static final String 	LANGUAGE				= "jpn";
  public static final String 	APP_SHR_PRF_KEY 		= "BaiWasedaConnect";
  public static final String 	PRF_KEY_REQ_CONFIDENCE 	= "RequiredRecognitionConfidence";
  public static final int		DEFAULT_REQ_CONFIDENCE 	= 66;
  public static final String 	PRF_KEY_REQ_TIME 		= "RequiredCaptureDelay";
  public static final int		DEFAULT_REQ_TIME 		= 3000;

  public static final String 	REF_IS_DOWNLOADED 		= "DownloadBoolean";
  public static final boolean	IS_DOWNLOADED	 		= false;

  public static final String 	REF_IS_PREVIEW	 		= "PreviewBoolean";
  public static final boolean	IS_PREVIEW_STOPED 		= false;

  //	public static final String	PRF_KEY_DOWNLOAD_IN_PROGRESS	= "DowloadInProgress";
  //	public static final String	PRF_KEY_STATIONS_ON		= "StationsOn";
  //	public static final String	PRF_KEY_LANDMARKS_ON	= "LandmarksOn";
  //	public static final String	PRF_KEY_TARGET_SEARCH	= "TargetSearchPhrase";

  public static final String 	PRF_KEY_CHECK 			= "CheckBoxKey";
  public static final boolean	DEFAULT_CHECK 			= false;

  public static final String PRF_KEY_TARGET_SIZE	 	= "targetSize";
  public static int TARGET_SIZE_DEFAULT				= 70;
  public static int TARGET_SIZE_MAX					= 120;

  public static final String PRF_KEY_MAX_DISTANCE	 	= "maxDistance";
  public static int MAX_DISTANCE_DEFAULT				= 11;
  public static int MAX_DISTANCE_MAX					= 30;

  public static final String PRF_KEY_NR_OF_TARGETS	= "nrOfTargets";
  public static int NR_OF_TARGETS_DEFAULT				= 10;
  public static int NR_OF_TARGETS_MAX					= 20;

  public static final String PRF_KEY_TEXT_SIZE	 	= "textSize";
  public static int TEXT_SIZE_DEFAULT					= 22;
  public static int TEXT_SIZE_MAX						= 40;

  public static final String	LOW_CONFIDENCE_TEXT		= "LOW CONFIDENCE LEVEL, TRY AGAIN"; //TODO MOVE TO STRINGS
  public static final String 	PRF_SELEC_X1		 	= "SelectionX1Ratio";
  public static final String 	PRF_SELEC_Y1		 	= "SelectionY1Ratio";
  public static final String 	PRF_SELEC_X2		 	= "SelectionX2Ratio";
  public static final String 	PRF_SELEC_Y2		 	= "SelectionY2Ratio";
  public static final int		LONG_WORDS_MIN_LENGTH	= 10;	//exclusive, i.e. same as >=11
  public static final int		LONG_WORDS_MAX_LENGTH	= 18;	//exclusive, i.e. same as <=17
  public static final int		MAX_TRANS_RESULTS		= 20;	
  public static final int		SIMILAR_WORD_MAX_LENGTH = 3;	//exclusive, i.e. same as <=17

  public static class DownloadFileCode{
    public static final int DICT = 1;
    public static final int TESS = 2;
  }

  public static final String	PRF_KEY_DOWNLOAD_IN_PROGRESS	= "DowloadInProgress";
}