package com.cn.eplat.timedtask;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cn.eplat.controller.EpDataController;
import com.cn.eplat.service.IEpAttenService;
import com.cn.eplat.service.IEpUserService;
import com.cn.eplat.service.IMachCheckInOutService;
import com.cn.eplat.service.IPushFilterLogService;
import com.cn.eplat.service.IPushToHwService;
import com.cn.eplat.utils.DateUtil;

/**
 * 每天定时筛选打卡数据
 * 
 * @author Administrator
 *
 */
@Component
public class FilterPunchCardDatas {
	
	private static Logger logger = Logger.getLogger(FilterPunchCardDatas.class);
	
	@Resource
	private IEpUserService epUserService;
	@Resource
	private IEpAttenService epAttenService;
	@Resource
	private IMachCheckInOutService machCheckInOutService;
	@Resource
	private IPushToHwService pushToHwService;
	@Resource
	private IPushFilterLogService pushFilterLogService;
	@Resource
	private EpDataController epDataController;
	
	private static int filter_times = 0;	// 筛选次数
	
	public static void addFilterTimes() {	// 筛选次数加1
		filter_times ++;
	}
	
	public static int getFilter_times() {
		return filter_times;
	}
	public static void setFilter_times(int filter_times) {
		FilterPunchCardDatas.filter_times = filter_times;
	}



//	@Scheduled(cron = "0 0 1 * * ? ")	// "0 0 1 * * ?"  （每天凌晨1点整开始执行）(正式上线时用的定时设置)
//	@Scheduled(cron = "0/5 * * * * ? ")	// （快速测试用定时设置。。。）
	public void filter() {
		System.out.println("执行定时筛选任务。。。");
		
		long start_time = System.currentTimeMillis();	// 记录筛选开始时间毫秒数
		Date now_time = new Date();
		FilterPunchCardDatas.addFilterTimes();
		logger.info("当前是第（" + FilterPunchCardDatas.getFilter_times() + "）次筛选考勤数据，开始时间：" + DateUtil.formatDate(2, now_time));
		
		/*
		// TODO: 临时代码 (Start)
		int ret = epDataController.filterPush2HwAttenOperation(null, null);
		System.out.println("ret = " + ret);
		// TODO: 临时代码 (End)
		*/
		
		Date earliest_pfl_time = pushFilterLogService.getEarliestPushFilterLogTime();
		
		if(earliest_pfl_time == null) {
			HashMap<String, Object> first_last_punch_time = epAttenService.getFirstAndLastPunchTimeValid();
			
			if(first_last_punch_time == null) {
				// 历史有效打卡数据为空
				logger.error("历史有效打卡数据为空。。。");
			} else {
				Object first_punch_time_obj = first_last_punch_time.get("first_punch_time");
				Object last_punch_time_obj = first_last_punch_time.get("last_punch_time");
				
				if(first_punch_time_obj instanceof Timestamp && last_punch_time_obj instanceof Timestamp) {
					Date first_punch_time = (Date) first_punch_time_obj;
					Date last_punch_time = (Date) last_punch_time_obj;
					
					int hist_filter_ret = epDataController.filterPush2HwAttenOperation(first_punch_time, last_punch_time);
					
					if(hist_filter_ret > 0) {
						logger.info("筛选准备推送华为的考勤数据（历史数据）成功, hist_filter_ret = " + hist_filter_ret);
					} else {
						logger.error("筛选准备推送华为的考勤数据（历史数据）失败, hist_filter_ret = " + hist_filter_ret);
					}
					
				} else {
					logger.error("今天0点之前的历史打卡数据中，最早和最晚一次有效打卡时间出现null异常。。。");
				}
			}
		} else {
//			Date two_days_ago = DateUtil.calcXDaysAfterADate(-2, now_time);
			Date yesterday = DateUtil.calcXDaysAfterADate(-1, now_time);
			
			int new_filter_ret = epDataController.filterPush2HwAttenOperation(yesterday, yesterday);
			
			if(new_filter_ret > 0) {
				logger.info("筛选准备推送华为的考勤数据（新数据）成功, new_filter_ret = " + new_filter_ret);
			} else if(new_filter_ret == -6 || new_filter_ret == -8) {
				logger.info("没有更多新的考勤打卡数据需要筛选了, new_filter_ret = " + new_filter_ret);
			} else {
				logger.error("筛选准备推送华为的考勤数据（新数据）失败, new_filter_ret = " + new_filter_ret);
			}
		}
		
		
		
		long end_time = System.currentTimeMillis();
		long use_time = (end_time - start_time);
		
		System.out.println("第（" + FilterPunchCardDatas.getFilter_times() + "）次筛选考勤数据，结束时间：" + DateUtil.formatDate(2, new Date()) + "，本次筛选耗时：" + DateUtil.timeMills2ReadableStr(use_time) + " (over)");
		
		
	}
	
	
}
