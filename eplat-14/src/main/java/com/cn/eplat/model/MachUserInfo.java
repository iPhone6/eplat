package com.cn.eplat.model;

import java.util.Date;

/**
 * 打卡机用户信息 实体类
 * 
 * @author Administrator
 *
 */
public class MachUserInfo {
	// 注：省略了Access数据库中Userinfo表的部分字段
	private Integer userid;
	private String badgenumber;
	private String name;
	private String gender;
	private String title;
	private Date birthday;
	private Date hiredday;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String ophone;
	private String fphone;
	
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getBadgenumber() {
		return badgenumber;
	}
	public void setBadgenumber(String badgenumber) {
		this.badgenumber = badgenumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public Date getHiredday() {
		return hiredday;
	}
	public void setHiredday(Date hiredday) {
		this.hiredday = hiredday;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getOphone() {
		return ophone;
	}
	public void setOphone(String ophone) {
		this.ophone = ophone;
	}
	public String getFphone() {
		return fphone;
	}
	public void setFphone(String fphone) {
		this.fphone = fphone;
	}
	
	@Override
	public String toString() {
		return "MachUserInfo [userid=" + userid + ", badgenumber=" + badgenumber + ", name=" + name + ", gender=" + gender + ", title=" + title + ", birthday=" + birthday
				+ ", hiredday=" + hiredday + ", street=" + street + ", city=" + city + ", state=" + state + ", zip=" + zip + ", ophone=" + ophone + ", fphone=" + fphone + "]";
	}
	
}
