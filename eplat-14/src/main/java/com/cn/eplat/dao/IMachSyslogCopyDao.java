package com.cn.eplat.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.MachSyslogCopy;

public interface IMachSyslogCopyDao {
	
	// // 打卡机系统日志表相关的操作	// //
	// 查询出ID大于给定值的系统日志数据
	public List<MachSyslogCopy> queryMachSyslogCopyGtGivenId(Integer id);
	
	// 查出在给定时间之后的所有系统日志信息
	public List<MachSyslogCopy> queryMachSyslogCopyAfterGivenTime(Date time);
	
	// 查出给定时间范围内的系统日志信息
	public List<MachSyslogCopy> queryMachSyslogCopyByGivenTimeRange(@Param("start") Date start, @Param("end") Date end);
	
	// 查出全部系统日志信息
	public List<MachSyslogCopy> queryAllMachSyslogCopys();
	
	
	
	
	// // 打卡机系统日志拷贝表相关的操作	// //
	// 查询出打卡机系统日志拷贝表中syslog_id最大的那条数据
	public List<MachSyslogCopy> queryMachSyslogCopyWithMaxSyslogId();
	
	// 查询出打卡机系统日志拷贝表中最大的syslog_id
	public Long queryMaxSyslogId();
	
	// 查出打卡机系统日志拷贝表中最晚的日志时间
	public Date queryLastestLogTime();
	
	// 批量插入打卡机系统日志数据
	public int batchInsertMachSyslogCopys(List<MachSyslogCopy> logs);
	
	// 查出所有已处理但没有处理结果的系统日志拷贝数据
	public List<MachSyslogCopy> queryProcessedSyslogsWithNullProcResult();
	
	// 批量修改打卡机系统日志数据
	public int batchUpdateMachSyslogCopyById(List<MachSyslogCopy> logs);
	
	// 批量修改打卡机系统日志数据的处理结果字段(proc_result)
	public int batchUpdateMachSyslogCopyProcResultById(@Param("upd_logs") List<MachSyslogCopy> logs, @Param("proc_result") String proc_result);
	
	
	
}
