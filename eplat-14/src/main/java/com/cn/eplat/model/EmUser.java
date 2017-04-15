package com.cn.eplat.model;

/**
 * 
 * @author Administrator
 * 
 * 环信用户实体类
 * 
 */
public class EmUser {
	private int id;
	private String username;
	private String password;
	private String nickname;
	private String uuid;
	private Integer activated;
	private Long created;
	private Long modified;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Integer getActivated() {
		return activated;
	}
	public void setActivated(Integer activated) {
		this.activated = activated;
	}
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}
	public Long getModified() {
		return modified;
	}
	public void setModified(Long modified) {
		this.modified = modified;
	}
	
	@Override
	public String toString() {
		return "EmUser [id=" + id + ", username=" + username + ", password="
				+ password + ", nickname=" + nickname + ", uuid=" + uuid
				+ ", activated=" + activated + ", created=" + created
				+ ", modified=" + modified + "]";
	}
	
}
