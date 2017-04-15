package com.cn.eplat.model;

import java.util.Date;

public class BindHistory {
	private Integer id;	// 主键id
	private Integer ep_uid;	// 用户id，表示是谁进行了绑定或解绑操作
	private Integer device_id;	// 设备id，表示是对哪个设备进行的操作
	private String type;	// 操作类型（绑定：bind，解绑：unbind）
	private Date time;	// 操作时间
	private Integer which_round_same;	// 对于同一用户、同一台设备而言，计数是第几次进行绑定（或解绑）操作？
	private Integer which_round_diff;	// 对于同一用户、不同设备而言，计数是第几次进行绑定（或解绑）操作？
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getEp_uid() {
		return ep_uid;
	}
	public void setEp_uid(Integer ep_uid) {
		this.ep_uid = ep_uid;
	}
	public Integer getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Integer getWhich_round_same() {
		return which_round_same;
	}
	public void setWhich_round_same(Integer which_round_same) {
		this.which_round_same = which_round_same;
	}
	public Integer getWhich_round_diff() {
		return which_round_diff;
	}
	public void setWhich_round_diff(Integer which_round_diff) {
		this.which_round_diff = which_round_diff;
	}
	
	@Override
	public String toString() {
		return "BindHistory [id=" + id + ", ep_uid=" + ep_uid + ", device_id=" + device_id + ", type=" + type + ", time=" + time + ", which_round_same=" + which_round_same
				+ ", which_round_diff=" + which_round_diff + "]";
	}
	
}
