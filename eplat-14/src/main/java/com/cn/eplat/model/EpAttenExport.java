package com.cn.eplat.model;

import java.util.Date;

/**
 * 
 * 
 * @author Administrator
 *
 */
public class EpAttenExport {
	private String name;
	private String email;
	private String type;	// 打卡方式（主要包括GPS定位经纬度方式和连接的WiFi MAC地址方式）
	private Double latitude;	// 纬度值（精确到小数点后15位）
	private Double longtitude;	// 经度值（精确到小数点后15位）
	private String gps_addr;	// GPS打卡方式下，经纬度对应的地理位置信息
	private String wifi_mac;	// 所连接的路由器的WiFi MAC地址
	private String wifi_name;	// 打卡时WiFi的名字
	private Boolean is_valid;	// 用于指示打卡是否有效（如果是GPS定位方式，则表示经纬度是否在有效范围内(以公司所在地点为中心的半径为200米的圆形区域内为有效范围)；如果是WiFi打卡方式，则表示所连接的WiFi MAC地址是否正确）
	private Date time;	// 打卡时间（精确到秒）
	private int count;	// 表示当天第几次打卡
	private String platform;	// 表示打卡时所使用的平台（目前主要是iOS和Android两大平台）
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}
	public String getGps_addr() {
		return gps_addr;
	}
	public void setGps_addr(String gps_addr) {
		this.gps_addr = gps_addr;
	}
	public String getWifi_mac() {
		return wifi_mac;
	}
	public void setWifi_mac(String wifi_mac) {
		this.wifi_mac = wifi_mac;
	}
	public String getWifi_name() {
		return wifi_name;
	}
	public void setWifi_name(String wifi_name) {
		this.wifi_name = wifi_name;
	}
	public Boolean getIs_valid() {
		return is_valid;
	}
	public void setIs_valid(Boolean is_valid) {
		this.is_valid = is_valid;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "EpAttenExport [name=" + name + ", type=" + type + ", latitude="
				+ latitude + ", longtitude=" + longtitude + ", gps_addr="
				+ gps_addr + ", wifi_mac=" + wifi_mac + ", wifi_name="
				+ wifi_name + ", is_valid=" + is_valid + ", time=" + time
				+ ", count=" + count + ", platform=" + platform + "]";
	}
	
}
