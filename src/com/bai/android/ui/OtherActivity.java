package com.bai.android.ui;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bai.android.data.Application;
import com.bai.android.data.account.AccountItem;
import com.bai.android.data.account.AccountManager;
import com.bai.android.data.connection.ConnectionState;
import com.bai.android.data.connection.ConnectionThread;
import com.bai.android.data.extension.avatar.AvatarManager;
import com.bai.android.data.extension.muc.MUCManager;
import com.bai.android.data.extension.vcard.OnVCardListener;
import com.bai.android.data.extension.vcard.VCardManager;
import com.bai.android.data.extension.vcard.VcardUpdateCallback;
import com.bai.android.data.message.OnChatChangedListener;
import com.bai.android.data.message.phrase.PhraseManager;
import com.bai.android.ui.adapter.ChatMessageAdapter;
import com.bai.androiddev.R;
import com.bai.xmpp.vcard.VCard;
import com.bai.xmpp.vcard.VCardProperty;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class OtherActivity extends FragmentActivity implements OnVCardListener, OnClickListener, OnChatChangedListener,
VcardUpdateCallback {

	private VCard vCard;

	private Button cancelButton;
	private Button editProfileSubmit;
	/** the login user id, not editable */
	//	private EditText editNickName;
	private EditText usernameEditText, fullNameEditText, emailEditText;

	private Spinner countrySpinner;
	private Spinner statusSpinner;
	private Spinner facultySpinner;
	private Spinner graduationSpinner;

	private String account;
	private String bareAddress;

	private TextView versionTextView;

	private String versionName="";
	private Button logoutButton;
	private Button changePWButton;
	private Button changeSoundButton;
	private Button changeAvatarButton;

	private EditText oldPWEditText;
	private EditText newPWEditText;
	private EditText confirmPWEditText;
	private String newpassword;
	private String confirmpassword;
	private GoogleMap map;
	private Button satelliteButton;
	private String oldpassword;

	private Bitmap avatarPicture;
	private ImageView avatar;

	/** Marker image. */
	private BitmapDescriptor image;

	private FragmentManager fm;
	private SupportMapFragment fragment;
	private RelativeLayout mapContainer;

	/** Notice layout */
	private View chatView;
	private ListView itemList;
	private LinearLayout targetView;

	private final int TARGET_WIDTH_AND_HEIGHT = 162;

	private AlertDialog.Builder alertDialog;

	/** Codes used in conjunction with camera and gallery intents. */ 
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 11;
	private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 22;

	private static final int CHANGE_RINGTONE_REQUEST_CODE = 33;

	public static final String SHR_PRF_APP_KEY = "WasedaMobile"; //TODO: Change when final app name is decided
	public static final String SHR_PRF_IMG_URI = "camereImageUri";
	public static final String APP_FOLDER = "WasedaMobile";

	private org.jivesoftware.smack.AccountManager am;
	private ConnectionThread connectionThread;

	private ChatMessageAdapter noticeMsgAdapter;
	private OtherSubTab subtab;
	
	public Drawable originalSpinnerDrawable;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.other);

		Collection<String> accounts = AccountManager.getInstance().getAccounts();
		account = accounts.iterator().next();

		connectionThread = AccountManager.getInstance().getAccount(account).getConnectionThread();
		if (connectionThread != null) {
			
	
			if (AccountManager.getInstance().getAccounts().size() > 0){
				final String account = AccountManager.getInstance().getAccounts().iterator().next();
				AccountItem accountItem = AccountManager.getInstance().getAccount(account);
				ConnectionState state = accountItem.getState();
				if (state.isConnected()) {
					online = true;
				}
			}
			
			XMPPConnection xmppconnection = connectionThread.getXMPPConnection();

			xmppconnection.forceAddConnectionListener(new ConnectionListener() {

				@Override
				public void reconnectionSuccessful() {
					online = true;
					if (subtab.getCurrentTabId() == R.id.other_button) {
						activateProfile();
					}
				}

				@Override
				public void reconnectionFailed(Exception e) {
				}

				@Override
				public void reconnectingIn(int seconds) {
				}

				@Override
				public void connectionClosedOnError(Exception e) {
					online = false;
					if (subtab.getCurrentTabId() == R.id.other_button) {
						deactivateProfile();
					}
				}

				@Override
				public void connectionClosed() {
					online = false;
					if (subtab.getCurrentTabId() == R.id.other_button) {
						deactivateProfile();
					}
				}
			});
		}

		new TabBar(this, R.id.other_button);
		subtab = new OtherSubTab(this, R.id.profile_button);

		//if coming from "notice" notification - direct to "notice" tab
		if (getIntent() != null && getIntent().getExtras() != null) {
			String targetView = getIntent().getExtras().getString("targetView");
			if (targetView.equals("notice")) {
				subtab.openTab(R.id.notice_button);
			}
		}
	}

	@Override
	protected void onResume() {
		Application.getInstance().addUIListener(OnChatChangedListener.class, this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		Application.getInstance().removeUIListener(OnChatChangedListener.class, this);
		super.onPause();
	}

	@Override
	public void onChatChanged(String account, String user, boolean incoming) {
		if (noticeMsgAdapter != null) {
			noticeMsgAdapter.onChange();
		}
	}

	@Override
	public void onVCardReceived(String account, String bareAddress, VCard vCard) {
		if (!this.account.equals(account) || !this.bareAddress.equals(bareAddress))
			return;
		this.vCard = vCard;
		updateVCard();
	}

	@Override
	public void onVCardFailed(String account, String bareAddress) {
		if (!this.account.equals(account) || !this.bareAddress.equals(bareAddress)) {
			return;
		}
		this.vCard = null;
		updateVCard();
		Application.getInstance().onError(R.string.XMPP_EXCEPTION);
	}

	private void updateVCard() {
		if (vCard == null)
			return;
		// get vCard values
		usernameEditText.setText(bareAddress.substring(0, bareAddress.indexOf('@')));
		fullNameEditText.setText(vCard.getField(VCardProperty.FN));
		emailEditText.setText(vCard.getEmail().trim());

		selectSpinnerItem(countrySpinner, vCard.getNationality());

		selectSpinnerItem(statusSpinner, vCard.getStatus());

		selectSpinnerItem(graduationSpinner, vCard.getGraduation());

		selectSpinnerItem(facultySpinner, vCard.getFaculty());
	}

	/** Selects a String value in a spinner if the spinner contains that value. */
	private void selectSpinnerItem(Spinner targetSpinner, String searchValue) {
		// if the user doesn't have an assigned value - don't select anything
		if (searchValue == null) {
			return;
		}

		// find the position of the value in the spinner
		int position = -1;
		for (int i = 0; i < targetSpinner.getCount(); i++) {
			if (searchValue.equals(targetSpinner.getItemAtPosition(i))) {
				position = i;
				break;
			}
		}
		// select the value in the spinner
		if (position != -1 ) {
			targetSpinner.setSelection(position);
		}
	}

	public void loadProfileData (){
		Application.getInstance().addUIListener(OnVCardListener.class, this);
		VCardManager.getInstance().request(account, bareAddress, null); //will be received by the listener
		Drawable avatarDrawable = AvatarManager.getInstance().getAccountAvatar(account);
		avatar.setImageDrawable(avatarDrawable);
		if (avatarDrawable instanceof BitmapDrawable) {
			avatarPicture = ((BitmapDrawable) avatarDrawable).getBitmap();
		}
	}

	private boolean online;

	public void callProfile(){
		//initialize elements
		editProfileSubmit = (Button) findViewById(R.id.edit_profile_submit);
		editProfileSubmit.setOnClickListener(this);

		usernameEditText = (EditText) findViewById(R.id.username);
		fullNameEditText = (EditText) findViewById(R.id.full_name_edit_text);
		emailEditText = (EditText) findViewById(R.id.email_edittext);

		cancelButton = (Button) findViewById(R.id.profile_cancel_button);
		cancelButton.setOnClickListener(this);

		changeAvatarButton = (Button) findViewById(R.id.change_avatar_button);
		changeAvatarButton.setOnClickListener(this);
		avatar = (ImageView) findViewById(R.id.avatar);

		bareAddress = account.substring(0, account.indexOf("/"));

		countrySpinner = (Spinner) findViewById(R.id.nationality_spinner);
		statusSpinner = (Spinner) findViewById(R.id.status_spinner);
		facultySpinner = (Spinner) findViewById(R.id.faculty_spinner);
		graduationSpinner = (Spinner) findViewById(R.id.years_spinner);

		originalSpinnerDrawable = countrySpinner.getBackground();

		if (!online) {
			Application.getInstance().onError(R.string.NOT_CONNECTED);
			deactivateProfile();
		} else {
			activateProfile();
			loadProfileData();
		}
	}

	private void deactivateProfile() {
		if (account != null && account.indexOf("@") > 0) {
			usernameEditText.setText(account.substring(0, account.indexOf("@")));
		}
		fullNameEditText.setBackgroundResource(R.drawable.shadow_repeat);
		countrySpinner.setBackgroundResource(R.drawable.shadow_repeat);
		statusSpinner.setBackgroundResource(R.drawable.shadow_repeat);
		facultySpinner.setBackgroundResource(R.drawable.shadow_repeat);
		graduationSpinner.setBackgroundResource(R.drawable.shadow_repeat);
	}

	private void activateProfile() {
		fullNameEditText.setBackground(null);
		countrySpinner.setBackground(originalSpinnerDrawable);
		statusSpinner.setBackground(originalSpinnerDrawable);
		facultySpinner.setBackground(originalSpinnerDrawable);
		graduationSpinner.setBackground(originalSpinnerDrawable);
	}

	public void callSettings(){

		versionTextView = (TextView)findViewById(R.id.version);
		try {
			versionName = getApplicationContext().getPackageManager()
					.getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		versionTextView.setText(versionName);

		logoutButton = (Button) findViewById(R.id.logout_button);
		logoutButton.setOnClickListener(this);

		changePWButton = (Button) findViewById(R.id.change_pw_button);
		changePWButton.setOnClickListener(this);

		changeSoundButton = (Button) findViewById(R.id.change_sound_button);
		changeSoundButton.setOnClickListener(this);

		oldPWEditText = (EditText) findViewById(R.id.old_pw_edit);
		newPWEditText = (EditText) findViewById(R.id.new_pw_edit);
		confirmPWEditText = (EditText) findViewById(R.id.confirm_pw_edit);

		newPWEditText.setEnabled(false);
		confirmPWEditText.setEnabled(false);
		changePWButton.setEnabled(false);

		newPWEditText = (EditText) findViewById(R.id.new_pw_edit);
		confirmPWEditText = (EditText) findViewById(R.id.confirm_pw_edit);

		XMPPConnection xmppConnection;
		connectionThread = AccountManager.getInstance().getAccount(account).getConnectionThread();
		if (connectionThread == null) {
			Application.getInstance().onError(R.string.NOT_CONNECTED);
			return;
		}
		xmppConnection = connectionThread.getXMPPConnection();
		if (xmppConnection == null) {
			Application.getInstance().onError(R.string.NOT_CONNECTED);
			return;
		}

		am = new org.jivesoftware.smack.AccountManager(xmppConnection);

		oldPWEditText.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
			}
			public void onTextChanged(CharSequence s, int start, int before, int count){
				oldpassword = oldPWEditText.getText().toString();
				if(oldpassword.equals(connectionThread.getConnectionItem().getConnectionSettings().getPassword())) {
					newPWEditText.setEnabled(true);
					confirmPWEditText.setEnabled(true);
					changePWButton.setEnabled(true);
				} 
			}
		}); 
	}


	public boolean mapInflated() {
		return mapContainer != null;
	}

	/** Hides the maps container. */
	public void removeMap() {
		// Commented code actually removed the fragment. However next time the user reopens the Map tab
		// from within this activity - it will be rest. Also, since the other tabs arenn't memory expensive
		// we can afford to keep the map alive (it will open faster second time its reoppened).
		//		fm.beginTransaction().remove(fragment).commit();
		//		fragment = null;
		//		map = null;
		mapContainer.setVisibility(LinearLayout.GONE);
	}

	public void callMap() {

		if (mapContainer == null) {
			if (fm == null) {
				fm = getSupportFragmentManager();
			}
			mapContainer = (RelativeLayout) findViewById(R.id.map_container);

			if (fragment == null) {
				fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
				//				fragment = SupportMapFragment.newInstance();
				//				fm.beginTransaction().replace(R.id.map, fragment).commit();
			}

			if (map == null) {
				map = fragment.getMap();
				if (map != null) {
					// focus to Waseda University 
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.710097, 139.721772), 15));
				}
			}

			// Parse the POI file and display markers for them on the map
			try {
				getPOI();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			}

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.710097, 139.721772), 15));

			satelliteButton = (Button) findViewById(R.id.satellite_button);
			satelliteButton.setOnClickListener(this);
		} else {
			mapContainer.setVisibility(LinearLayout.VISIBLE);
		}
	}

	public void getPOI() throws Exception, IOException {

		InputStream jsonStream = getApplicationContext().getResources().openRawResource(R.raw.points);

		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(jsonStream));
		String read = br.readLine();
		while(read != null) {
			sb.append(read);
			read = br.readLine();
		}
		String inputstr = sb.toString();
		JSONObject jsonObject = new JSONObject(inputstr);
		JSONArray jsonPoints = jsonObject.getJSONArray("points");

		BitmapDescriptor logo = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
		BitmapDescriptor supermarket = BitmapDescriptorFactory.fromResource(R.drawable.ic_supermarket_sign);
		BitmapDescriptor restaurant = BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_sign);


		for (int i = 0; i < jsonPoints.length(); i++) {
			JSONObject jsonPoint = jsonPoints.getJSONObject(i);

			switch(Integer.parseInt(jsonPoint.getString("pic"))) {
			case 1 :
				image = logo;
				break;
			case 2 :
				image = supermarket;
				break;
			case 3 :
				image = restaurant;
				break;
			}

			map.addMarker(
					new MarkerOptions()
					.position(new LatLng(Double.parseDouble(jsonPoint.getString("lat")), Double.parseDouble(jsonPoint.getString("long"))))
					.title(jsonPoint.getString("name"))
					.snippet(jsonPoint.getString("description"))
					.icon(image));
		}
	}


	// displays and loads the Notice view
	public void callNotice() {
		if (noticeMsgAdapter == null) {
			// prepare messages advvapter
			noticeMsgAdapter = new ChatMessageAdapter(this, true);
			noticeMsgAdapter.setChat(account, MUCManager.PersistentMUCChats.NOTICE);
			MUCManager.getInstance().createRoom(account, MUCManager.PersistentMUCChats.NOTICE, account, null, true);
		}

		// add the chat view
		//		if (targetView == null) {
		targetView = (LinearLayout) findViewById(R.id.notices);
		targetView.removeAllViews();
		chatView = getLayoutInflater().inflate(R.layout.chat_viewer_item, null);
		itemList = (ListView) chatView.findViewById(android.R.id.list);
		itemList.setAdapter(noticeMsgAdapter);

		//remove the title and send buttons
		chatView.findViewById(R.id.title).setVisibility(View.GONE);
		chatView.findViewById(R.id.chat_send).setVisibility(View.GONE);
		chatView.findViewById(R.id.chat_input).setVisibility(View.GONE);

		chatView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		targetView.addView(chatView);

		noticeMsgAdapter.updateInfo();
		//		}
	}

	public void changeAvatar() {
		// Prompt the user to select image from gallery or camera.
		alertDialog = new AlertDialog.Builder(OtherActivity.this);
		alertDialog.setMessage(getResources().getString(R.string.select_image_source));

		alertDialog.setPositiveButton(getResources().getString(R.string.gallery),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// remove the dialog to prevent "leakage"
				dialog.dismiss();

				Intent intent = new Intent(
						Intent.ACTION_GET_CONTENT,
						null);
				intent.setType("image/*");

				startActivityForResult(intent, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});


		alertDialog.setNeutralButton(getResources().getString(R.string.camera),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Uri imageUri = getOutputMediaFileUri(); // create a file to save the image 
				if (imageUri != null) {
					SharedPreferences.Editor prefEditor = getSharedPreferences(SHR_PRF_APP_KEY, MODE_PRIVATE).edit();
					prefEditor.putString(SHR_PRF_IMG_URI, imageUri.getPath());
					prefEditor.commit();
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // set the image file name

					// start the image capture Intent
					startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				}


			}

		});


		alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// close this dialog
				dialog.dismiss();
			}
		});

		alertDialog.show();

	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CHANGE_RINGTONE_REQUEST_CODE && resultCode == RESULT_OK) {
			Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null) {
				String ringTonePath = uri.toString();

				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = pref.edit();
				editor.putString(getResources().getString(R.string.default_sound), ringTonePath);
				editor.commit();

			}	
		} else {

			int rotation = ExifInterface.ORIENTATION_NORMAL;

			if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
				Uri imageUri = data.getData();
				setAvatarFromInputStreamUri(imageUri);
				rotation = getOrientationFromMediaStore(getApplicationContext(), imageUri);
				rotateImageIfNecessary(rotation);
				if (avatarPicture != null) {
					avatar.setImageBitmap(avatarPicture);
				}
			} else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
				String imagePath = getSharedPreferences(SHR_PRF_APP_KEY, MODE_PRIVATE).getString(SHR_PRF_IMG_URI, null);

				setAvatarFromFilePath(imagePath);
				if (avatarPicture == null) {
					Toast.makeText(getApplicationContext(), "Image cannot be decoded", Toast.LENGTH_LONG).show();
					return;
				}

				rotation = getOrientationFromFile(imagePath);
				//			}
				rotateImageIfNecessary(rotation);
				if (avatarPicture != null) {
					avatar.setImageBitmap(avatarPicture);
				}

			}

		}
	}


	private Uri getOutputMediaFileUri(){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "WasedaMobile");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Toast.makeText(getApplicationContext(), "Failed to create directory", Toast.LENGTH_LONG).show();
				return null;
			}
		}

		// Create a media file name
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator +
				"WasedaMobile_Temp.jpg");

		return Uri.fromFile(mediaFile);
	}

	private static int getOrientationFromMediaStore(Context context, Uri photoUri) {
		/* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	private static int getOrientationFromFile(String imagePath) {
		int orientation = ExifInterface.ORIENTATION_NORMAL;
		int rotation;
		// get orientation of file
		try {
			ExifInterface exif = new ExifInterface(imagePath);
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set the rotation from the file orientation
		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotation = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotation = 270;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotation = 180;
			break;
		default: 
			rotation = ExifInterface.ORIENTATION_NORMAL;
			break;
		}
		return rotation;
	}

	/** This is used for MediaStorage results **/
	private void setAvatarFromInputStreamUri(Uri imageUri) {
		// recycle the old image
		if (avatarPicture != null) {
			avatarPicture.recycle();
		}

		try {

			InputStream input = getContentResolver().openInputStream(imageUri);
			Bitmap tempbmp = BitmapFactory.decodeStream(input, null,  null);

			avatarPicture = scaleBitmapToAvatarSize(tempbmp);
			tempbmp = null;
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setAvatarFromFilePath(String imagePath) {
		File imgFile = new  File(imagePath);
		if (imgFile.exists()){
			if (avatarPicture != null) {
				avatarPicture.recycle();
				avatarPicture = null;
			}
			BitmapFactory.Options bmo = new BitmapFactory.Options();
			bmo.inSampleSize = 2;
			Bitmap tempbmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmo);

			avatarPicture = scaleBitmapToAvatarSize(tempbmp);
			tempbmp = null;
		}
	}

	private Bitmap scaleBitmapToAvatarSize(Bitmap bitmapToResize) {
		int newHeight = TARGET_WIDTH_AND_HEIGHT;
		int newWidth = TARGET_WIDTH_AND_HEIGHT;
		if (bitmapToResize.getHeight() > bitmapToResize.getWidth()){
			newWidth = (int) (bitmapToResize.getWidth() * newHeight / bitmapToResize.getHeight());
		} else {
			newHeight = (int) (bitmapToResize.getHeight() * newWidth / bitmapToResize.getWidth());
		}
		return Bitmap.createScaledBitmap(bitmapToResize, newWidth, newHeight, false);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.edit_profile_submit:
			submit();
			break;
		case R.id.profile_cancel_button:
			loadProfileData();
			break;
		case R.id.change_pw_button:
			changePswd();
			break;
		case R.id.logout_button:
			logout();
			break;
		case R.id.satellite_button:
			changeMapType();
			break;
		case R.id.change_sound_button:

			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String currentRingtone = pref.getString(getResources().getString(R.string.default_sound), null);

			Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

			if (currentRingtone == null)
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, PhraseManager.getInstance().getSound(account, null, null));
			else { 
				Uri currentRingtonUri = Uri.parse(currentRingtone);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,currentRingtonUri);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,currentRingtonUri);
			}

			startActivityForResult(intent, CHANGE_RINGTONE_REQUEST_CODE);

			break;
		case R.id.change_avatar_button:
			changeAvatar();
		default:
			break;
		}
	}
	private void submit() {
		vCard = new VCard();

		String fullName = fullNameEditText.getText().toString();
		if (fullName == null || fullName.trim().length() == 0) {
			Toast.makeText(getApplicationContext(), "Please enter a display name.", Toast.LENGTH_LONG).show();
			return;
		} else if (fullName.length() > 64) {
			Toast.makeText(getApplicationContext(), "Username too long. (Max 64 characters).", Toast.LENGTH_LONG).show();
			return;
		}

		//Email
		String email = emailEditText.getText().toString().trim();
		if (email == null) {
			Toast.makeText(getApplicationContext(), "Empty email address", Toast.LENGTH_LONG).show();
			return;
		} else if (email.indexOf("@") == -1 || email.indexOf(".") == -1 || // contains zero or 2 or more "@", contains no "." , there is no "." after "@"
				email.lastIndexOf(".") < email.indexOf("@") ||  email.indexOf("@") !=  email.lastIndexOf("@")) {
			Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_LONG).show();
			return;
		}

		if (countrySpinner.getSelectedItemPosition() == 0) {
			Toast.makeText(getApplicationContext(), "Please select nationality.", Toast.LENGTH_LONG).show();
			return;
		}

		if (statusSpinner.getSelectedItemPosition() == 0) {
			Toast.makeText(getApplicationContext(), "Please select state.", Toast.LENGTH_LONG).show();
			return;
		}

		vCard.setFormattedName(fullName);
		if (email != null) vCard.setEmail(email);
		vCard.setNationality(countrySpinner.getItemAtPosition(countrySpinner.getSelectedItemPosition()).toString());
		vCard.setStatus(statusSpinner.getSelectedItem().toString());
		if (graduationSpinner.getSelectedItemPosition() > 0) {
			vCard.setGraduation(graduationSpinner.getSelectedItem().toString());
		}
		if (facultySpinner.getSelectedItemPosition() > 0) {
			vCard.setFaculty(facultySpinner.getSelectedItem().toString());
		}

		VCardManager.getInstance().registerUpdateCallback(this, vCard.getPacketID());

		// set profile picture
		if (avatarPicture != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			avatarPicture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] avatar = stream.toByteArray();
			vCard.setAvatar(avatar);
		}

		VCardManager.getInstance().set(account, bareAddress, vCard);

		//VCardManager.getInstance().request(account, bareAddress, null);
	}

	private void rotateImageIfNecessary(int rotation) {
		if (rotation != 0) {
			// if image was stored rotated - rotate to normal
			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);

			avatarPicture = Bitmap.createBitmap(avatarPicture, 0, 0, avatarPicture.getWidth(), avatarPicture.getHeight(), matrix, true);
		} else {
			// not necessary to rotate  image
			//avatarPicture = Bitmap.createScaledBitmap(avatarPicture, avatarPicture.getWidth(), avatarPicture.getHeight(), true);
		}

	}

	private void changePswd() {
		newpassword = newPWEditText.getText().toString();
		confirmpassword = confirmPWEditText.getText().toString();

		if (newpassword.equals(confirmpassword)) {
			if (newpassword.length() >= 6) {
				try {
					am.changePassword(newpassword);
					Toast.makeText(getApplicationContext(), "Password was changed sucessfully", Toast.LENGTH_LONG).show(); //TODO: Put into string file
				} catch (XMPPException e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			else {
				Toast.makeText(getApplicationContext(), "Password too short. (Min 6 characters long).", Toast.LENGTH_LONG).show(); //TODO: Put into string file
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "The passwords don't match!", Toast.LENGTH_LONG).show(); //TODO: Put into string file
		}
	}

	private void logout() {
		AccountManager.getInstance().removeAccount(account);
		Intent intent = new Intent(getApplicationContext(), ContactList.class);
		startActivity(intent);
		finish();
	}

	private void changeMapType() {
		if(satelliteButton.getText() == getResources().getString(R.string.satellite)) {
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			satelliteButton.setText(getResources().getString(R.string.map));
			satelliteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_map_mapview, 0, 0);
		} else {
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			satelliteButton.setText(getResources().getString(R.string.satellite));
			satelliteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_map_satelliteview, 0, 0);
		}
	}

	@Override
	public void onCallback(String result, boolean successful) {
		Toast.makeText(Application.getInstance().getApplicationContext(), result, Toast.LENGTH_LONG).show();
		if (successful) {
			includeAvatarHashInPresence();
		}
	}

	private void includeAvatarHashInPresence() {
		VCardManager.getInstance().includeAvatarHashInPresenceBroadcast(account, vCard.getUploadedAvatarHash());
	}
}