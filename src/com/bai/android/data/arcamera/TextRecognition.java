package com.bai.android.data.arcamera;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Provides connection with the Tesseract API and exposes function for getting of text from image.
 */
public class TextRecognition{
//	public ArrayList<String> timeLog = new ArrayList<String>(); //TODO REMOVE 
//	public long lastTimeStamp = 0; 								//TODO REMOVE
//	public long newTimeStamp = 0; 								//TODO REMOVE

	/** The Tesseract instance for carrying out OCR */
	private TessBaseAPI baseApi;
	/** The required level of confidence, which needs to be overcome to return any text found in the image.
	 * 	Values are 1..100, 100 being perfect match. Typically good results are above 70. */
	private int requiredConfidence;

	protected Context applicationContext;

	/**
	 * Class constructor. Checks for language training data (doesn't download it yet - should be done
	 * manually) and initializes tesseract.
	 */
	public TextRecognition(Context applicationContext){
//		lastTimeStamp = System.nanoTime();						//TODO REMOVE

		//get an instance of the application context for use in this class
		this.applicationContext = applicationContext;		
		//get the saved required confidence for this system
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext); 
		requiredConfidence = preferences.getInt(Constant.PRF_KEY_REQ_CONFIDENCE, Constant.DEFAULT_REQ_CONFIDENCE);

		//check and create folder for tessearct trained data if it doesnt exist 
		String[] paths = new String[] { Constant.DATA_PATH, Constant.DATA_PATH + "tessdata/" };
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
//					Log.e("Waseda Community", "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
//				} else {
//					Log.i("Waseda Community", "Created directory " + path + " on sdcard");
				}
			}
		}

		// get the trained data "lang.traineddata" file
		if (!(new File(Constant.DATA_PATH + Constant.TESS_FOLDER + Constant.LANGUAGE + ".traineddata")).exists()) {
			try {
				// THIS IS CODE FOR COPYING OF ASSETS (assets folder - eg. trained data) to device
				//				AssetManager assetManager = getAssets();
				//				InputStream in = assetManager.open("tessdata/"+lang+".traineddata");
				//				//GZIPInputStream gin = new GZIPInputStream(in);
				//				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"+lang+".traineddata");
				//				// Transfer bytes from in to out
				//				byte[] buf = new byte[1024];
				//				int len;
				//				//while ((lenf = gin.read(buff)) > 0) {
				//				while ((len = in.read(buf)) > 0) {	out.write(buf, 0, len);	}
				//				in.close();
				//				//gin.close();
				//				out.close();
				//				Toast.makeText(getApplicationContext(), "TODO - INTERFACE FOR DOWNLOADING OF TRAINED DATA FROM INET. " +
				//						"FOR NOW - PASTE 'jpn.traineddata' TO '/sdcard/DecipherJapan/tessdata/'",  Toast.LENGTH_LONG).show();
//				Log.e(Constant.TAG, "CANT FIND " + Constant.DATA_PATH + "tessdata/" + Constant.LANGUAGE + ".traineddata");
			} catch (Exception e) {
//				Log.e(Constant.TAG, "Was unable to copy " + Constant.LANGUAGE + ".traineddata : " + e.toString());
				e.printStackTrace();
			}
		}

		//instantiate tesseract
		baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(Constant.DATA_PATH, Constant.LANGUAGE);
		baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);

//		newTimeStamp = System.nanoTime();													//TODO REMOVE
//		timeLog.add("OCR init:           "+(newTimeStamp-lastTimeStamp)/1000000+" milis");	//TODO REMOVE
//		lastTimeStamp = newTimeStamp;														//TODO REMOVE
	}

	/** Returns the current confidence requirement setting */
	public int getRequiredConfidence() {
		return requiredConfidence;
	}

	/** Sets the current confidence requirement setting. Values above 
	 * 70 typically mean good match, but are harder to get. */
	public void setRequiredConfidence(int requiredConfidence) {
		this.requiredConfidence = requiredConfidence;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext); 
		SharedPreferences.Editor editor = preferences.edit();	
		editor.putInt(Constant.PRF_KEY_REQ_CONFIDENCE, requiredConfidence);
		editor.commit();
	}

	/**
	 * Pass an image to Tesseract for processing and attempt to get text from it. 
	 * @param Bitmap imageToScan - the image that we want to OCR, required configuration ARGB_8888 
	 * @return String result - any text recognized by Tesseract, if recognition confidence is low -
	 * Constant.LOW_CONFIDENCE_TEXT will be returned.
	 */
	public String processImage(Bitmap imageToScan) {
		//TODO PREFELTERING OF IMAGE TO SEE IF THERE IS ANYTHING LIKE CHARACTER, IF YES - THEN PROCESS IMAGE WITH TESS

		//pass image to Tesseract
		baseApi.setImage(imageToScan);

//		lastTimeStamp = System.nanoTime();													//TODO REMOVE

		//get any recognized text
		String recognizedText = baseApi.getUTF8Text();
		
//		Log.d(Constant.TAG,"====------ > "+recognizedText+" < --------====");	//TODO REMOVE
//		recognizedText = recognizedText.replace("'ー`", "�?); //unique case, than you Tesseract
		while (recognizedText.contains("ーー"))	recognizedText = recognizedText.replace("ー","");
		
//		newTimeStamp = System.nanoTime();													//TODO REMOVE
//		timeLog.add("Get text:                 "+(newTimeStamp-lastTimeStamp)/1000000+" milis");	//TODO REMOVE
//		lastTimeStamp = newTimeStamp;														//TODO REMOVE
//
//		//		Log.d(Constant.TAG,"STRING: "+recognizedText);										//TODO REMOVE
		//get the confidence of the OCRed text
		int[] confidences =  baseApi.wordConfidences();
		if ((confidences.length > 0) && (recognizedText.length()>= confidences.length)){
			int confidence = 0;
			for (int i = 0; i< confidences.length; i++){
//				timeLog.add("Word["+i+"] | " + recognizedText.charAt(i)+" | "+confidences[i]);
				confidence += confidences[i];
			}

			baseApi.clear();
//			newTimeStamp = System.nanoTime();													//TODO REMOVE
//			timeLog.add("Clear base api:            "+(newTimeStamp-lastTimeStamp)/1000000+" milis");	//TODO REMOVE
//			lastTimeStamp = newTimeStamp;		

			//remove unwanted characters and trim string
//			recognizedText = recognizedText.replaceAll("[a-zA-Z\"\'~^=}{%,`;:*_″|-]+\t\n","");
			//		recognizedText = recognizedText.trim();

//			Log.d(Constant.TAG," ------------- TIME LOG ----------- ");							//TODO REMOVE
//			for (int i = 0; i <timeLog.size(); i++){											//TODO REMOVE
//				Log.d(Constant.TAG,timeLog.get(i));												//TODO REMOVE
//			}	

//			timeLog.clear();

			confidence = Math.round(confidence / confidences.length);

			if (confidence < requiredConfidence) recognizedText = Constant.LOW_CONFIDENCE_TEXT;
			imageToScan = null;
			return recognizedText;
		} 
		return Constant.LOW_CONFIDENCE_TEXT;
	}

	/** Close tesseract instance and free memory still used by it */
	public void finishOCR(){
		baseApi.end();
	}
}