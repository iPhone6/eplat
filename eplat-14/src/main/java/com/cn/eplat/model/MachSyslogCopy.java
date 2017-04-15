package com.cn.eplat.model;

import java.util.Date;

import com.cn.eplat.utils.DateUtil;

/**
 * 打卡机系统日志数据拷贝表 实体类
 * 
 * @author Administrator
 *
 */
public class MachSyslogCopy {
	private Long id;	// 打卡机系统日志数据拷贝表的主键id
	private Integer syslog_id;	// 打卡机系统日志表的id字段
	private String operator;	//
	private Date log_time;	
	private String machine_alias;
	private Integer log_tag;
	private String log_descr;
	private String status;	// 当前这条打卡机系统日志处理状态（'不处理'，'未处理'，'已处理'），注：只处理log_descr字段中含有“下载记录”的操作
	private Date proc_time;	// 处理当前这条系统日志的时间
	private String proc_result;	// 处理结果描述
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getSyslog_id() {
		return syslog_id;
	}
	public void setSyslog_id(Integer syslog_id) {
		this.syslog_id = syslog_id;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Date getLog_time() {
		return log_time;
	}
	public void setLog_time(Date log_time) {
		this.log_time = log_time;
	}
	public String getMachine_alias() {
		return machine_alias;
	}
	public void setMachine_alias(String machine_alias) {
		this.machine_alias = machine_alias;
	}
	public Integer getLog_tag() {
		return log_tag;
	}
	public void setLog_tag(Integer log_tag) {
		this.log_tag = log_tag;
	}
	public String getLog_descr() {
		return log_descr;
	}
	public void setLog_descr(String log_descr) {
		this.log_descr = log_descr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getProc_time() {
		return proc_time;
	}
	public void setProc_time(Date proc_time) {
		this.proc_time = proc_time;
	}
	public String getProc_result() {
		return proc_result;
	}
	public void setProc_result(String proc_result) {
		this.proc_result = proc_result;
	}
	
	@Override
	public String toString() {
		return "MachSyslogCopy [id=" + id + ", syslog_id=" + syslog_id + ", operator=" + operator + ", log_time=" + DateUtil.formatDate(2, log_time) + ", machine_alias=" + machine_alias + ", log_tag="
				+ log_tag + ", log_descr=" + log_descr + ", status=" + status + ", proc_time=" + proc_time + ", proc_result=" + proc_result + "]";
	}
	
}
