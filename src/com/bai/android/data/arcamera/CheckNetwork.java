package com.bai.android.data.arcamera;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetwork {
	private Context context;

	/**
	 * Constructor
	 * @param context - application Context
	 */
	public CheckNetwork(Context context){
		this.context = context;
	}
	/**
	 * checks if user has mobile or wifi connection, or is in the process of connecting to a network
	 * does not guarantee actual internet access or that the resources on the server are available or that the server is online
	 * @returns true or false
	 */
	public boolean isConnectedToInternet(){
		  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo ni = cm.getActiveNetworkInfo();
		  boolean wifiConnected = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		  boolean mobileConnected = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		  if ( (ni != null && ni.isConnected()) || wifiConnected || mobileConnected) {
			  return true;
		  } else {
			  return false;
		  }
		 }
	}