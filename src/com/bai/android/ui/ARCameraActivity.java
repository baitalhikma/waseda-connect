package com.bai.android.ui;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bai.android.data.arcamera.CheckNetwork;
import com.bai.android.data.arcamera.Constant;
import com.bai.android.data.arcamera.DictionaryDatabaseAdapter;
import com.bai.android.data.arcamera.DownloadContent;
import com.bai.android.data.arcamera.Engine;
import com.bai.android.data.arcamera.ImageCalculations;
import com.bai.android.data.arcamera.ManageFileService;
import com.bai.android.data.arcamera.Selection;
import com.bai.android.data.arcamera.TextRecognition;
import com.bai.android.data.arcamera.Word;
import com.bai.androiddev.R;

public class ARCameraActivity extends Activity implements SurfaceHolder.Callback, View.OnTouchListener  
{
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	public ImageView touchView;
	private TextView confidenceText, frequencyText;
	private LinearLayout translation;

	private boolean previewing, focusRequested, takePictureRequest, displayTouched, processingImage, scrollableMaxHeight,flashOnOff, isPreviewStoped;
	private boolean portrait, reversedLandscape, portraitReversed;
	public boolean firstSelection, wasStoped = false;
	private int displayHeight, displayWidth, errorCount;
	private int currentapiVersion		= android.os.Build.VERSION.SDK_INT;
	private long lastPictureTimestamp 	= 0;
	private float firstX, firstY, displayDensity;
	private boolean visible = false;
	private String message;

	private AutoFocusCallback myAutoFocusCallback;
	private Bitmap targetBitmap;

	private YuvImage 			yuvImage;
	private Camera 				camera;
	private Camera.Parameters 	cameraParams;
	private BitmapFactory.Options bitmapOptions;

	private TextRecognition		ocr;
	private Engine				morphologyEngine;
	private DictionaryDatabaseAdapter 	db;
	public ImageCalculations 	imageCalculations;
	public Selection 			selectionClass;
	private ScrollView 			translationScroll;
	private ProgressDialog 		processDialog,translateDialog;
	private String recognizedTextJapanese="";

	private Dialog ds;
	private SeekBar confSeekBar, freqSeekBar; 
	private CheckBox checkBox;
	private Button save, cancel, flashButton, stopButton, download;

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private int i, n;
	private ArrayList<Word> analyzedText;
	private RelativeLayout root;

	private Bitmap tempBitmap;
	private ProgressDialog dialog;
	private final Handler handler = new Handler();

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_camera);

		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		getWindow().setFormat(PixelFormat.UNKNOWN);
		imageCalculations = new ImageCalculations();

		root 			= (RelativeLayout)	findViewById(R.id.root);

		surfaceView 	= (SurfaceView)		findViewById(R.id.camerapreview);
		translation		= (LinearLayout) 	findViewById(R.id.ocrText);
		touchView 		= (ImageView)		findViewById(R.id.touchView);
		translationScroll = (ScrollView) 	findViewById(R.id.translationScroll);
		flashButton 	= (Button) findViewById(R.id.flash_button);
		stopButton 		= (Button) findViewById(R.id.stop_button);

		//		flashButton	.getBackground().setColorFilter(new LightingColorFilter(0xAAFFFFFF, 0xAAFFFFFF));
		//		stopButton	.getBackground().setColorFilter(new LightingColorFilter(0xAAFFFFFF, 0xAAFFFFFF));

		//instantiating the shared preferences so it is possible to save and obtain values from them
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = preferences.edit();

		//adjust the height of the scroll, eg the device was rotated
		translationScroll.post(setScrollHeight);

		surfaceHolder 	= surfaceView.getHolder();
		surfaceHolder.addCallback(this);

		//Getting the display density
		displayDensity = getResources().getDisplayMetrics().density;
		// Part of the code that gets the screen width and screen height and using them calculates ratio
		if (currentapiVersion < 11) {	
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		if (currentapiVersion < 13) {
			Display display = getWindowManager().getDefaultDisplay();
			displayHeight = display.getHeight(); 
			displayWidth = display.getWidth();
		} else {
			Point size = new Point();
			Display display = getWindowManager().getDefaultDisplay();
			display.getSize(size);
			displayHeight = size.y; 
			displayWidth = size.x; 
		}
		TabBar tabBar = new TabBar(this, R.id.arcamera_button);

		//Instantiating the selectionClass and sending the activity information and display resolution to the selectionClass
		selectionClass = new Selection(this, displayWidth, displayHeight);

		//add an observer to restore the previous selection once the main layout finished loading
		//the observer is going to be called everytime there are changes in the layout, eg. the scroll grows
		ViewTreeObserver vto = root.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				selectionClass.getPastSelection();
			}
		});

		touchView.setOnTouchListener(this);

		bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 1;
		bitmapOptions.inPreferQualityOverSpeed = true;
		bitmapOptions.inDither = true;
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

		//Autofocus callback 
		myAutoFocusCallback = new AutoFocusCallback() {
			public void onAutoFocus(boolean success, Camera arg1) {
				focusRequested = false;
				if(previewing && !displayTouched && (lastPictureTimestamp + preferences.getInt(Constant.PRF_KEY_REQ_TIME, Constant.DEFAULT_REQ_TIME) < System.currentTimeMillis()) && success)
				{
					lastPictureTimestamp = System.currentTimeMillis();
					takePictureRequest = true;
				}
			}
		};
		//ProcessDialog for printing out the current process
		processDialog = new ProgressDialog(this);
		processDialog.setCancelable(true);
		processDialog.setCanceledOnTouchOutside(false);


		translateDialog = new ProgressDialog(this);
		translateDialog.setCancelable(true);
		translateDialog.setCanceledOnTouchOutside(false);
		translateDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//if the process dialog is canceled - stop the morhplogy analysis and translation
				morphologyEngine.setShouldContinue(false);		
			}
		});
		//		
		//		processDialog.setOnCancelListener(new OnCancelListener() {
		//			@Override
		//			public void onCancel(DialogInterface dialog) {
		//				morphologyEngine.setShouldContinue(false);		
		//			}
		//		});

		//Part for setting the flashButton layout to root view and setting the button parameters and settings
		//for calculations of the view height we multiply value of dp which is 50 in our case by screen density
		//and then we add 0.5f to round the figure up to the nearest whole number(can be used for width as well)
		RelativeLayout.LayoutParams parameters = new RelativeLayout.LayoutParams((int) (180.0f * displayDensity + 0.5f),(int) (60.0f * displayDensity + 0.5f));		

		parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		//flashButton.setLayoutParams(parameters);
		//flashButton.getBackground().setColorFilter(new LightingColorFilter(0xAAFF0000, 0xAAFF0000));
		//root.addView(flashButton);
		flashButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				flashOnOff();
			}
		});


		//root.addView(stopButton);
		stopButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				pauseStartPreview();
			}
		});

		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = preferences.edit();


		visible = true;




		//		downloadButton.setOnClickListener(new OnClickListener()	{
		//			public void onClick(View v) {
		//				startDownload();
		//			}
		//				
		//		});


	}

	//Runnable that stops increasing of the scroll views height untill it has reached one third of the screen
	private Runnable setScrollHeight = new Runnable() {
		@Override
		public void run() {
			int height = translationScroll.getHeight();
			if(height>(displayHeight/3) && visible)
			{
				scrollableMaxHeight=true;
				ViewGroup.LayoutParams scrollParams = translationScroll.getLayoutParams();
				scrollParams.height = Math.round(displayHeight/3);
				translationScroll.setLayoutParams(scrollParams);
			}
		}
	};

	@Override
	protected void onResume() {


		//first check if there is a download in progress, if yes show the progress dialog
		if (dialog==null) dialog = new ProgressDialog(this);

		//check if a download of libraries is already in progress
		preferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (preferences.getBoolean(Constant.PRF_KEY_DOWNLOAD_IN_PROGRESS, false)){
			dialog.setMessage(getResources().getString(R.string.progress_dictionary));
			dialog.show();
		}

		if (!isDictionaryAvailable()) 
		{
			showDownload();
		} else init();

		super.onResume();
	}

	private boolean isDictionaryAvailable(){
		return ManageFileService.fileExists(Constant.DATA_PATH + Constant.DICT_FILENAME) && 
				ManageFileService.fileExists(Constant.DATA_PATH + Constant.TESS_FILENAME);
	}

	private void init() {
		ocr = new TextRecognition(getApplicationContext()); 

		db = new DictionaryDatabaseAdapter(getApplicationContext());
		db.open();
		morphologyEngine = new Engine(db);

		processingImage = false;
		isPreviewStoped = preferences.getBoolean(Constant.REF_IS_PREVIEW, Constant.IS_PREVIEW_STOPED);
		if(isPreviewStoped){
			stopButton.setText(getString(R.string.start));
			stopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
		} else {
			stopButton.setText(getString(R.string.stop));
			stopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop, 0, 0, 0);
		}
		displayTouched = false;
		focusRequested = false;
		visible = true;
		camera = Camera.open();
		previewCamera(displayWidth, displayHeight); 
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	//Releases the camera and stops the preview
	@Override
	protected void onPause() {
		editor.putBoolean(Constant.REF_IS_PREVIEW, isPreviewStoped);
		editor.commit();
		if (camera != null) {
			camera.setPreviewCallback(null);
		}
		if (previewing) {
			camera.stopPreview();
			previewing = false;
		}
		if (camera != null) camera.release();
		if (processDialog.isShowing() && processDialog != null) {
			processDialog.dismiss();
		}
		//in case there is any translation happening - interrupt it
		if (morphologyEngine != null) morphologyEngine.setShouldContinue(false);
		visible = false;
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		camera = null;
		if (morphologyEngine != null) {
			morphologyEngine.release();
		}
		if (db != null && db.isOpen()) {
			db.close();
		}
		if (ocr != null) {
			ocr.finishOCR();
		}
		System.gc();
		super.onDestroy();
	}

	//If surface changes stops the preview of camera
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		if (previewing) {
			camera.stopPreview();
			previewing = false;
		}
		previewCamera(displayWidth, displayHeight);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	private PreviewCallback onPreview = new PreviewCallback() { 
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) 
		{
			if (!processingImage && !focusRequested && !isPreviewStoped && !displayTouched && (selectionClass.selectionFrame.getVisibility() == ImageView.VISIBLE) && (lastPictureTimestamp + preferences.getInt(Constant.PRF_KEY_REQ_TIME, Constant.DEFAULT_REQ_TIME) <  System.currentTimeMillis())){
				requestCameraFocus();
			}
			if (takePictureRequest && !isPreviewStoped){
				takePictureRequest = false;
				processingImage=true;
				//dialogHandler.post(showDialog);
				/**getting preview size from camera parameters and setting it to the Yuv image object
				then compressing image to jpeg object and acquiring a byteArray from it*/
				Size size = cameraParams.getPreviewSize(); 

				yuvImage = new YuvImage(data, cameraParams.getPreviewFormat(), size.width, size.height, null); 
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				yuvImage.compressToJpeg( new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 90, outputStream);
				data = outputStream.toByteArray();
				targetBitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888);
				// Creating the original bitmap object received from the preview with predefined options
				targetBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
				//Calculating the selectedImage dimension and rotation
				imageCalculations.setSelectionSize(displayWidth, displayHeight, selectionClass.selectionX, selectionClass.selectionY, selectionClass.selectionWidth,  selectionClass.selectionHeight, yuvImage.getWidth(), yuvImage.getHeight());
				imageCalculations.setOrientation(portrait, portraitReversed, reversedLandscape);
				imageCalculations.calculateSelection(); 

				//rotate and crop bitmap
				if (currentapiVersion >= 11){
					targetBitmap = Bitmap.createBitmap(targetBitmap, imageCalculations.x, imageCalculations.y, imageCalculations.width, imageCalculations.height, imageCalculations.rotationMatrix, true);
				} else {///By default API 10 (2.3) uses and RGB 565 config....
					tempBitmap = Bitmap.createBitmap(targetBitmap, imageCalculations.x, imageCalculations.y, imageCalculations.width, imageCalculations.height, imageCalculations.rotationMatrix, true);
					if (tempBitmap.getConfig() != Bitmap.Config.ARGB_8888){ 
						targetBitmap = Bitmap.createBitmap(targetBitmap.getWidth(), targetBitmap.getHeight(), Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas();
						canvas.setBitmap(targetBitmap);
						canvas.drawBitmap(tempBitmap, 0, 0, null);
						tempBitmap.recycle();
					}
				}

				new Thread(new Runnable() 
				{
					public void run() 
					{
						dialogHandler.post(showDialog);
						setBitmap();
					}
				}).start();
			}
		}
	};

	// Function that updates the camera display and preview size
	// depending on the rotation of the device
	public void updateCameraSettings(int width, int height) {
		// Initializing camera parameters
		cameraParams = camera.getParameters();

		Display display = getWindowManager().getDefaultDisplay();

		if (display.getRotation() == Surface.ROTATION_0)
		{
			cameraParams.setPreviewSize(height, width);
			camera.setDisplayOrientation(90);
			portrait=true;
		}
		else if (display.getRotation() == Surface.ROTATION_90)
		{
			cameraParams.setPreviewSize(width, height);
		}
		else if (display.getRotation() == Surface.ROTATION_180)
		{
			portraitReversed=true;
			cameraParams.setPreviewSize(height, width); 
			camera.setDisplayOrientation(270);
		}
		else if (display.getRotation() == Surface.ROTATION_270)
		{
			reversedLandscape=true;
			cameraParams.setPreviewSize(width, height);
			camera.setDisplayOrientation(180);
		}
		cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		camera.setParameters(cameraParams);		
	}

	public void previewCamera(int width, int height) {
		if (!previewing) try {
			updateCameraSettings(width, height);
			camera.setPreviewDisplay(surfaceHolder);
			camera.setPreviewCallback(onPreview);
			camera.startPreview();
			previewing = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setTabBarHeight(int height){
		selectionClass.setOffsetFromTop(height);
	}

	//Sending the bitmap image to tesseract
	protected boolean setBitmap() {
		//lockRotation(true);
		recognizedTextJapanese = ocr.processImage(targetBitmap); 		//COMMENT IF YOU DONT NEED TRANSLATION
		if(visible)
		{
			dialogHandler.post(dismissDialog);
			if (recognizedTextJapanese == Constant.LOW_CONFIDENCE_TEXT)	{
				errorCount=errorCount+1;
				if(errorCount%3==0)	dialogHandler.post(displayErrorToast);
			} 
			else {
				dialogHandler.post(showTranslationDialog);
				errorCount=0;
				recognizedTextJapanese = recognizedTextJapanese.replaceAll("\\W", ""); //cuts non alphabetical symbols
				analyzedText = morphologyEngine.analyzeString(recognizedTextJapanese);
				recognizedTextJapanese += " - ";
				// set translated text
				translation.post(displayTranslation);
				//Calls the runnable to check scrollVIew height
				if(!scrollableMaxHeight) translation.post(setScrollHeight); 
				dialogHandler.post(dismissTranslationDialog);
				dialogHandler.post(checkForStop);
			} 
		}
		//lockRotation(false);
		targetBitmap.recycle();
		targetBitmap = null;
		processingImage=false;

		return true;
	}

	public void requestCameraFocus()
	{
		if (selectionClass.selectionFrame.getVisibility() == ImageView.VISIBLE){
			if (!focusRequested){
				focusRequested = true;
				camera.autoFocus(myAutoFocusCallback);
			}
		} 
		else {
			Toast.makeText(getApplicationContext(),  getResources().getString(R.string.toast_selection), Toast.LENGTH_LONG).show();
		}
	}

	//Ontouch listener for surface view that registers the amount of pointers that currently touch 
	//the screen as well as its coordinates and sends these coordinates to updateSelection();
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		displayTouched = true;
		int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_MOVE:	{
			//If there are two pointers registers and sends the both coordinates to updateSelection
			if(event.getPointerCount()==2)	{
				firstSelection = false;
				selectionClass.updateSelection2Pointers(event.getX(0), event.getY(0), event.getX(1), event.getY(1));	
				selectionClass.repositionSelection();
			} 
			else if(event.getPointerCount()==1) {
				//If there is only one pointer checks if screen is touched already and sends coordinates to updateSelection
				if (firstSelection){
					selectionClass.updateSelection2Pointers(firstX, firstY, event.getX(0), event.getY(0));	
					selectionClass.repositionSelection();
				} 
				else {
					selectionClass.updateSelection1Pointer(event.getX(0), event.getY(0));
					selectionClass.repositionSelection();
				}
			}
			break;
		}
		//Focuses camera in case if one pointersetSelectionSize leaves the screen
		case MotionEvent.ACTION_UP:	{
			displayTouched = false;
			requestCameraFocus();
			break;
		}
		//If there is only one pointer down registers the first coordinates
		case MotionEvent.ACTION_DOWN:	{
			if((event.getPointerCount()==1) && !(selectionClass.selectionFrame.getVisibility() == ImageView.VISIBLE)) {
				firstX = event.getX(0);
				firstY = event.getY(0);
				firstSelection = true;
			}
			break;
		}
		default: break;
		}				
		return true;
	}
	//Function for turning camera flashlight on and off
	public void flashOnOff()
	{
		cameraParams = camera.getParameters();
		if(!flashOnOff)
		{
			cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			camera.setParameters(cameraParams);
			flashButton.setText(getString(R.string.flashOff));
			flashButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_flash, 0, 0, 0);
		}
		else
		{
			cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			camera.setParameters(cameraParams);
			flashButton.setText(getString(R.string.flashOn));
			flashButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flash, 0, 0, 0);
		}
		flashOnOff=!flashOnOff;
	}

	//part of the code that changes the boolean value and color of the stop/start button
	public void pauseStartPreview()
	{
		if (!isPreviewStoped) { 
			stopButton.setText(getString(R.string.start));
			stopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
		}
		else {
			stopButton.setText(getString(R.string.stop));
			stopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop, 0, 0, 0);
		}
		isPreviewStoped=!isPreviewStoped;
	}

	//Handler for running of dialogs
	private final Handler dialogHandler = new Handler();

	//Runable for makind the dialog visible
	final Runnable showDialog = new Runnable() {
		public void run() {
			if (visible && !processDialog.isShowing()) {
				processDialog.setMessage( getResources().getString(R.string.progress_processing));
				processDialog.show();
			}
		}
	};

	//Runable for displaying the transaltion dialog
	final Runnable showTranslationDialog = new Runnable() {
		public void run() {
			if (visible && !translateDialog.isShowing()) {
				translateDialog.setMessage(getResources().getString(R.string.progress_translating)+recognizedTextJapanese);
				translateDialog.show(); //FIXME E/WindowManager(26485): android.view.WindowLeaked: Activity com.bai.decipher_japan.cameradetect.CameraActivity has leaked window com.android.internal.
				//				policy.impl.PhoneWindow$DecorView@429d6850 that was originally added here
			}
		}
	};

	//Runnable that dismisses the translation dialog
	final Runnable dismissTranslationDialog = new Runnable() {
		public void run() {
			if (visible && translateDialog.isShowing())	translateDialog.dismiss();
		}
	};

	//Runnable that dismisses the processing dialog
	final Runnable dismissDialog = new Runnable() {
		public void run() {
			if (visible && processDialog.isShowing()) processDialog.dismiss();
		}
	};

	//Runnable that displays the translation
	private Runnable displayTranslation = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			if(visible) 
			{
				View[] views = new View[analyzedText.size()+1]; 
				TextView originalText = new TextView(ARCameraActivity.this);
				originalText.setTextColor(ColorStateList.valueOf(Color.BLACK));
				originalText.setText(recognizedTextJapanese);

				views[0]=originalText;

				recognizedTextJapanese = "";

				for (i=0;i<analyzedText.size();i++) 
					if (analyzedText.get(i).getTranslation()!=null) {
						recognizedTextJapanese = analyzedText.get(i).getTranslation();

						List<String> spinnerArray = new ArrayList<String>();
						spinnerArray.add(recognizedTextJapanese);

						String meaning =analyzedText.get(i).getMeaning();
						meaning = meaning.replaceAll("\\([^\\(]*\\)", "");
						String[] splitedMeanings = meaning.split("/");
						for(n=0;n<splitedMeanings.length;n++)
							if(!recognizedTextJapanese.equals(splitedMeanings[n].trim()) && !splitedMeanings[n].trim().equals(""))
								spinnerArray.add(splitedMeanings[n].trim());

						Spinner spinner	= (Spinner) LayoutInflater.from(getBaseContext()).inflate(R.layout.spinner_layout, translation, false);

						ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_list, spinnerArray);
						spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner);
						spinner.setAdapter(spinnerArrayAdapter);
						views[i+1]=spinner;
					}
				populateText(translation,views, getApplicationContext());
			}
		}
	};

	//Runnable that displays an error toast 
	private Runnable displayErrorToast = new Runnable() {
		@Override
		public void run() {
			if(visible) Toast.makeText(getApplicationContext(),  getResources().getString(R.string.toast_suggestion), Toast.LENGTH_LONG).show();
		}
	};

	//Runnable that checks if preview needs to be stopped after translation 
	private Runnable checkForStop = new Runnable() {
		@Override
		public void run() 
		{
			if(preferences.getBoolean(Constant.PRF_KEY_CHECK, Constant.DEFAULT_CHECK)==false)
			{
				isPreviewStoped=true;
				stopButton.setText(getString(R.string.start));
				stopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
			}
		}
	};

	// Enable options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.menu, menu);
		return true;
	}

	// Options menu selections (About or Settings)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(!isPreviewStoped)
		{
			isPreviewStoped=true;
			wasStoped=true;
		}
		ds = new Dialog(this);
		ds.setContentView(R.layout.settings);
		ds.setTitle("Settings");
		ds.setCancelable(true);
		ds.show();
		initializeSettings();
		return false;
	}

	private void initializeSettings()
	{
		confSeekBar 	= (SeekBar)	ds.findViewById(R.id.sbConfLev);
		freqSeekBar 	= (SeekBar) ds.findViewById(R.id.sbCapFreq);
		checkBox 		= (CheckBox) ds.findViewById(R.id.cbAuRes);
		save 			= (Button)	ds.findViewById(R.id.btnSave);
		cancel 			= (Button)	ds.findViewById(R.id.btnCancel);
		confidenceText 	= (TextView) ds.findViewById(R.id.confText);
		frequencyText 	= (TextView) ds.findViewById(R.id.capFreq);

		confidenceText.setText("" + preferences.getInt(Constant.PRF_KEY_REQ_CONFIDENCE, Constant.DEFAULT_REQ_CONFIDENCE));
		frequencyText.setText("" + (preferences.getInt(Constant.PRF_KEY_REQ_TIME, Constant.DEFAULT_REQ_TIME)/1000));
		checkBox.setChecked(preferences.getBoolean(Constant.PRF_KEY_CHECK, Constant.DEFAULT_CHECK));

		confSeekBar.setMax(40);
		confSeekBar.setProgress((preferences.getInt(Constant.PRF_KEY_REQ_CONFIDENCE, Constant.DEFAULT_REQ_CONFIDENCE)-40));
		confSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				confidenceText.setText("" + (progress + 40));
			}
		});

		freqSeekBar.setMax(10);
		freqSeekBar.setProgress((preferences.getInt(Constant.PRF_KEY_REQ_TIME, Constant.DEFAULT_REQ_TIME)/1000));
		freqSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				frequencyText.setText(""+ progress);

			}
		});

		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editor.putInt(Constant.PRF_KEY_REQ_CONFIDENCE, (confSeekBar.getProgress()+40));
				editor.putInt(Constant.PRF_KEY_REQ_TIME, (freqSeekBar.getProgress()*1000));
				if(checkBox.isChecked()) {
					editor.putBoolean(Constant.PRF_KEY_CHECK, true);	
				}
				else {
					editor.putBoolean(Constant.PRF_KEY_CHECK, false);
				}
				editor.commit();
				ds.dismiss();

				if(wasStoped) {
					wasStoped=false;	
					isPreviewStoped=false;
				}
			}
		});		

		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ds.dismiss();
				finish();
				if (wasStoped) {
					wasStoped=false;
					isPreviewStoped=false;
				}
			}
		});
	}

	//function that locks the screen while there is a translation operation in progress
	//	private void lockRotation(boolean lock)
	//	{
	//		if(lock) {
	//			Display display = getWindowManager().getDefaultDisplay();
	//			if(display.getRotation() == Surface.ROTATION_0)
	//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
	//			else if(display.getRotation() == Surface.ROTATION_90)
	//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
	//			else if(display.getRotation() == Surface.ROTATION_180)
	//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
	//			else if(display.getRotation() == Surface.ROTATION_270)
	//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
	//		}
	//		else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
	//	}

	/**
	 * function that adds new lines to the translation layout depending on the free space in layout
	 * @param translationLayout
	 * @param views
	 * @param mContext
	 */
	private void populateText(LinearLayout translationLayout, View[] views , Context mContext) 
	{ 
		int maxWidth = displayWidth - 20;
		int numberOfLines = 0;

		LinearLayout.LayoutParams params;
		LinearLayout lineWrapper = new LinearLayout(mContext);
		lineWrapper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lineWrapper.setGravity(Gravity.LEFT);
		lineWrapper.setOrientation(LinearLayout.HORIZONTAL);


		int widthSoFar = 0;

		for (int i = 0 ; i < views.length ; i++ )
		{
			LinearLayout elementWrapper = new LinearLayout(mContext);
			elementWrapper.setOrientation(LinearLayout.HORIZONTAL);
			elementWrapper.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
			elementWrapper.setLayoutParams(new ListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			elementWrapper.setGravity(Gravity.LEFT);

			views[i].measure(0,0);//FIXME null poinetr exception after cancel on translating progress dialog 
			params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 0, 0);
			elementWrapper.addView(views[i], params);
			elementWrapper.measure(0, 0);
			widthSoFar += views[i].getMeasuredWidth();

			if (widthSoFar >= maxWidth) 
			{
				translationLayout.addView(lineWrapper,numberOfLines);
				numberOfLines++;
				lineWrapper = new LinearLayout(mContext);
				lineWrapper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				lineWrapper.setOrientation(LinearLayout.HORIZONTAL);
				lineWrapper.setGravity(Gravity.LEFT);
				params = new LinearLayout.LayoutParams(views[i].getMeasuredWidth(),
						LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 0, 0);
				lineWrapper.addView(elementWrapper, params);
				widthSoFar = 0;
				// widthSoFar = LL.getMeasuredWidth();
			} 
			else {
				params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 0, 0);
				lineWrapper.addView(elementWrapper, params);
			}
		}
		translationLayout.addView(lineWrapper,numberOfLines);
	}

	private void startDownload() {
		CheckNetwork cn = new CheckNetwork(getApplicationContext());
		if (cn.isConnectedToInternet())
			new Thread(new Runnable(){
				public void run(){
					//lock the screen from rotating
					//handler.post(lockScreen);
					//note that the download has started
					SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					Editor editor = preferences.edit();
					editor.putBoolean(Constant.PRF_KEY_DOWNLOAD_IN_PROGRESS, true).commit();
					//show the progress dialog for download
					handler.post(startDialog);
					message = getResources().getString(R.string.progress_dictionary);
					handler.post(updateMessage);

					// download both zipped trained db and zipped dictionary db sequentially
					DownloadContent dcdict = new DownloadContent(Constant.DownloadFileCode.DICT);
					try {
						dcdict.downloadFile();
					} catch (Exception e) {
						e.printStackTrace();
					}
					message = getResources().getString(R.string.progress_library);
					handler.post(updateMessage);

					DownloadContent dctess = new DownloadContent(Constant.DownloadFileCode.TESS);
					try {
						dctess.downloadFile();
					} catch (Exception e) {
						e.printStackTrace();
					}

					//remove the progress dialog
					handler.post(stopDialogAndInit);
					//note download has been complete
					editor.putBoolean(Constant.PRF_KEY_DOWNLOAD_IN_PROGRESS, false).commit();
					//unlock screen rotation
					//handler.post(lockScreen);
				}
			}).start();
		else Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_network), Toast.LENGTH_LONG).show();


	}



	private void showDownload() 
	{
		Log.e("er", "//////////////////////////////////////////////////Beginning");

		Log.e("er", "//////////////////////////////////////////////////if");
		ds = new Dialog(this);
		ds.setContentView(R.layout.download_layout);
		ds.setCancelable(true);
		ds.setTitle(R.string.down_title);
		ds.show();
		download = (Button)	ds.findViewById(R.id.downButton);
		cancel 	= (Button)	ds.findViewById(R.id.cancelButton);
		checkBox = (CheckBox) ds.findViewById(R.id.downloadCheckBox);

		download.setOnClickListener(new OnClickListener() {
			public void onClick(View v) 
			{
				CheckNetwork cn = new CheckNetwork(getApplicationContext());
				if (cn.isConnectedToInternet())
				{
					ds.dismiss();
					startDownload();
				}
				else Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_network), Toast.LENGTH_SHORT).show();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ds.dismiss();
			}
		});

	}


	Runnable startDialog = new Runnable() {
		@Override
		public void run() {
			dialog.show();
		}
	};

	Runnable updateMessage = new Runnable() {
		@Override
		public void run() {
			dialog.setMessage(message);
		}
	};

	Runnable stopDialogAndInit = new Runnable() {
		@Override
		public void run() {
			dialog.dismiss();
			init();
		}
	};
}