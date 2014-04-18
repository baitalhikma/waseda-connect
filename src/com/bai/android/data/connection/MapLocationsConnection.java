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
package com.bai.android.data.connection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.bai.android.ui.OtherActivity;

/**
 * Takes two arguments - current db version, server uri for getting db version and server uri for getting locations
 * @author Nikolov
 *
 */
public class MapLocationsConnection extends AsyncTask<String, Integer, JSONArray> {

  private OtherActivity mainActivity;

  private String mapVersion;

  public MapLocationsConnection(OtherActivity callingActivity) {
    this.mainActivity = callingActivity;
  }

  @Override
  protected JSONArray doInBackground(String... args) {
    InputStream is = null; 
    String serverResponse = null; 
    JSONObject json = new JSONObject();
    StringEntity entity = null;

    mapVersion = args[0];
    String getDBVersionUri = args[1];
    String getLocationsUri = args[2];

    HttpParams httpParameters = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
    HttpConnectionParams.setSoTimeout(httpParameters, 15000);
    HttpConnectionParams.setTcpNoDelay(httpParameters, true);

    HttpClient httpClient = new DefaultHttpClient(httpParameters);

    HttpPost httpPost = new HttpPost(getDBVersionUri);

    try {
      json.put("pass", "your identification password");
      entity = new StringEntity(json.toString());
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }

    entity.setContentType("application/json;charset=UTF-8");
    httpPost.setEntity(entity);

    try {
      HttpResponse response = httpClient.execute(httpPost); 
      HttpEntity responseEntity = response.getEntity(); 
      is = responseEntity.getContent();
    }	catch(Exception e) { 
      return null;
    } 

    //convert response to string 
    try { 
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8); 
      serverResponse = reader.readLine();
      if (serverResponse.indexOf("404 Not Found") != -1) return null;
      is.close(); 
    } catch (Exception e) { 
      e.printStackTrace();
      return null;
    } 

    try {
      JSONArray dbversionArr = new JSONArray(serverResponse);
      if (dbversionArr != null && dbversionArr.length() > 0) {
        JSONObject dbversionArrObj = dbversionArr.getJSONObject(0);
        String currentDbVersion = dbversionArrObj.getString("version");
        if (mapVersion == null || !currentDbVersion.equalsIgnoreCase(mapVersion)) {
          mapVersion = currentDbVersion;
        } else {
          //NOTHING TO DO - SAME VERSION
          return null;
        }
      } else {
        return null;
      }
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }

    // DB VERSION CHECKED AND DIFFENRET - GET LOCATIONS
    httpPost = new HttpPost(getLocationsUri);

    httpPost.setEntity(entity);
    try {
      HttpResponse response = httpClient.execute(httpPost); 
      HttpEntity responseEntity = response.getEntity(); 
      is = responseEntity.getContent();
    }	catch(Exception e) {
      e.printStackTrace();
      return null;
    } 

    //convert response to string 
    try { 
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 8); 
      serverResponse = reader.readLine();
      if (serverResponse.indexOf("404 Not Found") != -1) return null;
      is.close(); 
    } catch (Exception e) { 
      e.printStackTrace();
      return null;
    } 
    try {
      return new JSONArray(serverResponse);
    } catch (Exception e) { 
      e.printStackTrace();
      return null;
    }
  }


  @Override
  protected void onPostExecute(JSONArray result) {
    super.onPostExecute(result);

    if (result != null) {
      JSONObject location = null;
      MapDataAdapter mapAdapter = new MapDataAdapter(mainActivity.getApplicationContext());
      mapAdapter.open();
      mapAdapter.beginTransaction();
      mapAdapter.deleteAll();
      try {
        for (int i = 0; i < result.length(); i++) {
          location = result.getJSONObject(i);
          mapAdapter.insertLocation(
              location.getString("title"),
              location.getString("descr"),
              location.getInt("type"),
              location.getDouble("lat"),
              location.getDouble("lon"));
        }
        mapAdapter.setTransactionSuccessful();
      } catch (JSONException e) {
        e.printStackTrace();
      } finally {
        mapAdapter.endTransaction();
        mapAdapter.close();
      }
      mainActivity.displayMapContent();
      mainActivity.updateMapVersion(mapVersion);
    }
  }
}
