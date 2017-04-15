package com.cn.eplat.model;

public class EpData {
	private Integer id;
	private Integer app_id;
	private Integer role_id;
	private Integer file_id;
	private String file_path;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getApp_id() {
		return app_id;
	}
	public void setApp_id(Integer app_id) {
		this.app_id = app_id;
	}
	public Integer getRole_id() {
		return role_id;
	}
	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}
	public Integer getFile_id() {
		return file_id;
	}
	public void setFile_id(Integer file_id) {
		this.file_id = file_id;
	}
	public String getFile_path() {
		return file_path;
	}
	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}
	
	@Override
	public String toString() {
		return "EpData [id=" + id + ", app_id=" + app_id + ", role_id="
				+ role_id + ", file_id=" + file_id + ", file_path=" + file_path
				+ "]";
	}
	
}
