package com.cn.eplat.utils.gps;

public class PointLatLng {
	public double Lat;
	public double Lng;
	
	public PointLatLng() {
		
	}
	
	public PointLatLng(double lat, double lng) {
		super();
		Lat = lat;
		Lng = lng;
	}
	
	public double getLat() {
		return Lat;
	}
	public void setLat(double lat) {
		Lat = lat;
	}
	public double getLng() {
		return Lng;
	}
	public void setLng(double lng) {
		Lng = lng;
	}
	
	@Override
	public String toString() {
		return "PointLatLng [Lat=" + Lat + ", Lng=" + Lng + "]";
	}
	
}
