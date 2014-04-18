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