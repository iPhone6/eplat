package com.cn.eplat.model;

import java.util.Date;


/**
 * 设备实体类
 * 例如iPhone手机、各品牌安卓手机等设备
 * 
 * @author Administrator
 *
 */
public class EpDevice {
	private Integer id;	// 主键id
	private String name;	// 设备名称
	private String imei;	// 设备唯一标识码（一般可以用设备的IMEI码，也即设备串号）
	private String udid;	// 设备唯一标识码（UDID）
	private Integer ep_uid;	// 公司系统用户id
	private Date bind_start_time;	// 绑定该设备的开始生效时间（如果超过有效时间后，会自动重置该时间）
	private Integer bind_count;	// 有效时间内该设备已绑定的次数（初值为0）
	private Integer bind_valid_time;	// 绑定次数有效时间（单位为天），默认为一年（即365天）
	private Boolean bind_status;	// 绑定状态（是否已绑定某一个用户）
	private String platform;	// 区分设备平台类型的字段（目前只有iOS或Android两种）
	private Integer max_bound_times;	// （绑定次数有效时间内）每台设备最多的绑定次数
	private Date last_bound_time;	// 该设备最近的一次绑定时间
	private Date last_unbound_time;	// 该设备最近的一次解绑时间
	public static final Integer DEFAULT_BIND_VALID_TIME = 365;	// 每个设备默认的绑定次数有效时间为365天
	public static final Integer DEFAULT_MAX_BOUND_TIMES = 5;	// （有效时间内）每台设备默认的最多累计绑定次数为5次
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public Integer getEp_uid() {
		return ep_uid;
	}
	public void setEp_uid(Integer ep_uid) {
		this.ep_uid = ep_uid;
	}
	public Date getBind_start_time() {
		return bind_start_time;
	}
	public void setBind_start_time(Date bind_start_time) {
		this.bind_start_time = bind_start_time;
	}
	public Integer getBind_count() {
		return bind_count;
	}
	public void setBind_count(Integer bind_count) {
		this.bind_count = bind_count;
	}
	public Integer getBind_valid_time() {
		return bind_valid_time;
	}
	public void setBind_valid_time(Integer bind_valid_time) {
		this.bind_valid_time = bind_valid_time;
	}
	public Boolean getBind_status() {
		return bind_status;
	}
	public void setBind_status(Boolean bind_status) {
		this.bind_status = bind_status;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public Integer getMax_bound_times() {
		return max_bound_times;
	}
	public void setMax_bound_times(Integer max_bound_times) {
		this.max_bound_times = max_bound_times;
	}
	public Date getLast_bound_time() {
		return last_bound_time;
	}
	public void setLast_bound_time(Date last_bound_time) {
		this.last_bound_time = last_bound_time;
	}
	public Date getLast_unbound_time() {
		return last_unbound_time;
	}
	public void setLast_unbound_time(Date last_unbound_time) {
		this.last_unbound_time = last_unbound_time;
	}
	
	@Override
	public String toString() {
		return "EpDevice [id=" + id + ", name=" + name + ", imei=" + imei + ", udid=" + udid + ", ep_uid=" + ep_uid + ", bind_start_time=" + bind_start_time + ", bind_count="
				+ bind_count + ", bind_valid_time=" + bind_valid_time + ", bind_status=" + bind_status + ", platform=" + platform + ", max_bound_times=" + max_bound_times
				+ ", last_bound_time=" + last_bound_time + ", last_unbound_time=" + last_unbound_time + "]";
	}
	
}
