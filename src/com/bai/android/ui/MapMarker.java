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

import com.google.android.gms.maps.model.BitmapDescriptor;


public class MapMarker {

  private double lat, lon;
  private String description, title;
  private BitmapDescriptor icon;

  public MapMarker(double lat, double lon, String description, String title, BitmapDescriptor icon) {
    super();
    this.lat = lat;
    this.lon = lon;
    this.description = description;
    this.title = title;
    this.icon = icon;
  }

  public double getLat() {
    return lat;
  }
  public void setLat(double lat) {
    this.lat = lat;
  }
  public double getLon() {
    return lon;
  }
  public void setLon(double lon) {
    this.lon = lon;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public BitmapDescriptor getIcon() {
    return icon;
  }
  public void setIcon(BitmapDescriptor icon) {
    this.icon = icon;
  }
}
