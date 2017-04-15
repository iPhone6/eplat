package com.cn.eplat.timedtask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.util.TextUtils;
import org.junit.runner.RunWith;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.dao.IPushLogDao;
import com.cn.eplat.dao.IPushToHwDao;
import com.cn.eplat.model.PushLog;
import com.cn.eplat.model.PushToHw;
import com.cn.eplat.utils.DateUtil;
import com.cn.eplat.utils.elead2huawei.Constant;
import com.cn.eplat.utils.elead2huawei.ExportData2HWHelper;
import com.cn.eplat.utils.elead2huawei.GetTokenHelper;

/**
 * 每天定时推送打卡数据
 */
@RunWith(SpringJUnit4ClassRunner.class)// 表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = { "classpath:spring-mybatis.xml" })
@Component
public class PushToHwTask {

	@Resource
	private IPushToHwDao pushToHwDao;

	@Resource
	private IPushLogDao pushLogDao;

	@Resource
	private IEpUserDao epUserDao;
	
	//将epuid-work_no缓存起来   key-epuid  value-work_no  
	private static Map<String,String>  workNumMap = new HashMap<String, String>();
	
	private static int push_times = 0;	// 筛选次数
	
	public static void addPushTimes() {	// 筛选次数加1
		push_times ++;
	}
	
	public static int getPush_times() {
		return push_times;
	}
	public static void setPush_times(int push_times) {
		PushToHwTask.push_times = push_times;
	}

//	 @Scheduled(cron = "0 30 1 * * ? ") // "0 0 1 * * ?"      // （每天凌晨1点30分开始执行）(正式上线时用的定时设置)
//	@Scheduled(cron = "0/8 * * * * ? ")     // （快速测试用定时设置。。。）
	public void pushDatasToHw() { // 将从数据库里查出来的数据组装成推送需要的数据
		long start_time = System.currentTimeMillis();	// 记录推送开始时间毫秒数
		System.out.println("本次推送到华为,开始时间   "+DateUtil.formatDate(2, new Date()));
		List<PushToHw> allNeedsDatas = getAllNeedsDatas();
		
		/*
		// TODO: 临时代码
		List<PushToHw> allNeedsDatas = null;
		try {
			allNeedsDatas = pushToHwDao.getPushToHwsByIdSeq(2208l, null);
		} catch (Exception e) {
			System.err.println("获取数据异常，error info = " + e.getMessage());
		}
		*/
		
		/*
		// TODO: 临时代码
		List<PushToHw> allNeedsDatas = new ArrayList<PushToHw>();
		PushToHw ptw1 = new PushToHw();
//		PushToHw ptw2 = new PushToHw();
		
		ptw1.setId(1000l);
		ptw1.setEp_uid(566);
		ptw1.setName("肖海涛");
		ptw1.setId_no("410311197901125016");
		ptw1.setDayof_date(DateUtil.parse2date(1, "2017-02-14"));
		ptw1.setDayof_week("2");
		ptw1.setOn_duty_time(DateUtil.parse2date(2, "2017-02-14 07:55:45"));	//2017-02-14 07:55:45
		ptw1.setOff_duty_time(DateUtil.parse2date(2, "2017-02-14 18:10:57"));
		
		allNeedsDatas.add(ptw1);
		*/
		
		if (allNeedsDatas != null && !allNeedsDatas.isEmpty()) {
			String token = GetTokenHelper.getToken();
			ArrayList<Map<String, String>> lists = new ArrayList<>();
//			int count = 0;
			for (PushToHw pushToHw : allNeedsDatas) {
				if (pushToHw.getOn_duty_time() != null) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("staffIdNo", pushToHw.getId_no());
					map.put("staffName", pushToHw.getName());
					map.put("swipeDate",DateUtil.formatDate(1, pushToHw.getDayof_date()));
					map.put("swipeTime",DateUtil.formatDate(2, pushToHw.getOn_duty_time()));
					map.put("week", pushToHw.getDayof_week());
					map.put("pth_id", String.valueOf(pushToHw.getId()));
					map.put("ep_uid", String.valueOf(pushToHw.getEp_uid()));
					lists.add(map);
				}

				if (lists.size() == Constant.COUNTS_PER_REQUEST) {// 满足推送条数就推送出去
					addPushTimes();
					System.out.println("这是第    "+getPush_times()+"    次推送到华为，共推送   "+Constant.COUNTS_PER_REQUEST+"    条数据");
					boolean isSuccess = ExportData2HWHelper.getInstance().insert2HW(lists, token);
					dealResult(lists, isSuccess);
					lists.clear();
				}
				
				if (pushToHw.getOff_duty_time() != null) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("staffIdNo", pushToHw.getId_no());
					map.put("staffName", pushToHw.getName());
					map.put("swipeDate",DateUtil.formatDate(1, pushToHw.getDayof_date()));
					map.put("swipeTime",DateUtil.formatDate(2, pushToHw.getOff_duty_time()));
					map.put("week", pushToHw.getDayof_week());
					map.put("pth_id", String.valueOf(pushToHw.getId()));
					map.put("ep_uid", String.valueOf(pushToHw.getEp_uid()));
					lists.add(map);
				}

				if (lists.size() == Constant.COUNTS_PER_REQUEST) {// 满足推送条数就推送出去
					addPushTimes();
					System.out.println("这是第    "+getPush_times()+"    次推送到华为，共推送   "+Constant.COUNTS_PER_REQUEST+"    条数据");
					boolean isSuccess = ExportData2HWHelper.getInstance().insert2HW(lists, token);
					dealResult(lists, isSuccess);
					lists.clear();
				}
			}

			if (!lists.isEmpty()) {
				addPushTimes();
				System.out.println("这是第    "+getPush_times()+"    次推送到华为，共推送   "+lists.size()+"    条数据");
				boolean isSuccess = ExportData2HWHelper.getInstance().insert2HW(lists, token);
				dealResult(lists, isSuccess);
				lists.clear();
			}
		}else{
			System.out.println("没有更多数据需要推送");
		}
		
		long end_time = System.currentTimeMillis();	// 记录推送结束时间毫秒数
		System.out.println("本次推送到华为,结束时间   "+DateUtil.formatDate(2, new Date()));
		System.out.println("本次推送共花费时间：    "+DateUtil.timeMills2ReadableStr(end_time-start_time)+"    毫秒");
	}

	//获取所有需要推送的数据
	public List<PushToHw> getAllNeedsDatas() {
		List<PushToHw> result = new ArrayList<PushToHw>();
		
		/*
		String name = "符边正";
		name = "康贺梁";
		result = pushToHwDao.findPushToHwsByName(name);
		*/
		
		Date time = pushLogDao.getLatestPushLogTime();
		
		if (time == null) {
			List<PushToHw> allDatas = pushToHwDao.getPushToHwsByDate(null,null);// 获取所有的数据
			if (allDatas != null) {
				return allDatas;
			}
		} else {
			// 1.先去查所有之前推送失败的数据
			List<Long> pthIds = pushLogDao.getAllFailPthIds();
			if (pthIds != null && !pthIds.isEmpty()) {
				pushLogDao.deletePushLogsByPthids(pthIds);//先把日志表（push_log）里失败(也许带一条成功)了的数据删除
				List<PushToHw> failDatas = pushToHwDao.getPushToHwsByIds(pthIds);
				if (failDatas != null && !failDatas.isEmpty()) {
					result.addAll(failDatas);
				}
			}

			// 2.获取待推送的数据(截至到昨天)
			Date startDate = DateUtil.parse2date(1, DateUtil.formatDate(1, time));
			Date date = new Date();
			Date simpleDate = DateUtil.parse2date(2, (DateUtil.formatDate(1, date)+" 23:59:59"));
			Date endDate = DateUtil.calcXDaysAfterADate(-1, simpleDate);
			
			List<PushToHw> waitingDatas = pushToHwDao.getPushToHwsByDate(startDate,endDate);
			if (waitingDatas != null && !waitingDatas.isEmpty()) {
				result.addAll(waitingDatas);
			}
		}
		
		
		return result;
	}

	// 处理推送结果
	public void dealResult(List<Map<String, String>> lists, boolean isSuccess) {
		if (lists == null || lists.isEmpty())  return;
		List<PushLog> result = new ArrayList<PushLog>();
		for (Map<String, String> map : lists) {
			PushLog log = new PushLog();
			log.setName(map.get("staffName"));
			log.setPth_id(Long.valueOf(map.get("pth_id")));
			log.setResult(isSuccess);
			log.setTime(new Date());
			String ep_uid = map.get("ep_uid");
			String workNum = "";
			if(workNumMap.containsKey(ep_uid) && !TextUtils.isEmpty(workNumMap.get(ep_uid))){
				workNum = workNumMap.get(ep_uid);
			}else{
				workNum = epUserDao.queryWorkNumById(Integer.valueOf(ep_uid));
				workNumMap.put(ep_uid, workNum);
			}
			log.setWork_no(workNum);
			result.add(log);
		}
		pushLogDao.insert2PushLog(result);
	}
}
