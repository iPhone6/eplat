package org.zsl.testmybatis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.eplat.dao.IEpAttenDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpAtten;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.utils.DateUtil;
import com.cn.eplat.utils.LocationUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestEpAttenDao {
	private static Logger logger = Logger.getLogger(TestEpAttenDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpAttenDao epAttenDao;
	
	
	
	
	@Test
	public void updateAllEpAttenGPSDistance() {
		
		List<EpAtten> all_gps_atts = epAttenDao.queryAllGPSEpAttensWithNullGPSDistance();
		
		if(all_gps_atts == null || all_gps_atts.size() == 0) {
			logger.error("查询失败");
			return;
		}
		
		logger.info("查询成功，all_gps_atts.size() = " + all_gps_atts.size());
		
		int count = 0;
		
		for(EpAtten epa : all_gps_atts) {
			count ++;
			Long epa_id = epa.getId();
			Double lat = epa.getLatitude();
			Double lng = epa.getLongtitude();
			
			if("GPS".equals(epa.getType()) && lat != null && lng != null) {
				StringBuffer sbuf = new StringBuffer();
				JSONObject json = LocationUtil.isGPSLocationValidCommonsWithDistance(lat, lng, epa.getPlatform(), sbuf);
				logger.debug("epa.id = " + epa_id + ", sbuf=" + sbuf);
				
				Boolean is_gps_location_valid = (Boolean) json.get("is_gps_location_valid");
				Double nearest_distance = (Double) json.get("nearest_distance");
				
				if(is_gps_location_valid) {
					logger.info("第[ " + count + " ]个：此次GPS打卡距离某中心点在有效范围内，计算得到距离所有中心点的最近距离为：nearest_distance = " + nearest_distance);
				} else {
					logger.error("第[ " + count + " ]个：此次GPS打卡距离所有中心点都不在有效范围内，计算得到距离所有中心点的最近距离为：nearest_distance = " + nearest_distance);
				}
				
				if(epa.getGps_distance() == null) {
					EpAtten epa_upd = new EpAtten();
					epa_upd.setId(epa_id);
					epa_upd.setGps_distance(nearest_distance);
					int upd_ret = epAttenDao.updateEpAttenById(epa_upd);
					if(upd_ret > 0) {
						logger.info("更新GPS打卡数据(epa_id=" + epa_id + ")的离所有中心点坐标最近距离信息成功");
					} else {
						logger.error("更新GPS打卡数据(epa_id=" + epa_id + ")的离所有中心点坐标最近距离信息失败");
					}
				} else {
					logger.info("该GPS打卡数据中已有GPS距离信息");
				}
				
			} else {
				logger.error("出现无效的GPS打卡数据：epa = " + epa);
			}
		}
		
	}
	
	
	
	
	@Test
	public void testQueryFirstAndLastPunchTimeValid() {
		HashMap<String, Object> res = epAttenDao.queryFirstAndLastPunchTimeValid();
		
		if(res == null) {
			logger.error("查询失败");
			return;
		}
		
		logger.info("查询成功");
		System.out.println("res = " + res);
		
	}
	
	
	
	@Test
	public void testQueryFirstAndLastPunchTimeValidByDatesAndEpUidsBeforeToday() {
		Date date1 = DateUtil.parse2date(2, "2016-12-03 16:33:58");
		Date date2 = DateUtil.parse2date(2, "2017-02-15 09:50:03");
		
		List<Date> dates = DateUtil.getDatesBetweenTwoDates(date1, date2);
		
		List<EpUser> epus = new ArrayList<EpUser>();
		
		for(int i=100; i<500; i++) {
			EpUser epu = new EpUser();
			epu.setId(i);
			epus.add(epu);
		}
		
		List<HashMap<String, Object>> results = epAttenDao.queryFirstAndLastPunchTimeValidByDatesAndEpUidsBeforeToday(dates, epus);
		
		if(results == null) {
			logger.error("查询异常");
			return;
		}
		
		if(results.size() == 0) {
			logger.info("查询结果为空");
			return;
		}
		
		logger.info("查询成功，结果条数为：results.size() = " + results.size());
		for(HashMap<String, Object> result : results) {
			System.out.println(result);
			
		}
		
	}
	
	
	
	@Test
	public void testBatchInsertEpAttens() {
		Date now = new Date();
		
		EpAtten epa1 = new EpAtten();
		epa1.setEp_uid(10);
		epa1.setType("IC_Card");
		epa1.setIs_valid(true);
		epa1.setTime(now);
		epa1.setPlatform("PunchCardMachine");
		epa1.setMach_sn("12365487822ddddd");
		
		EpAtten epa2 = new EpAtten();
		epa2.setEp_uid(11);
		epa2.setType("fingerprint");
		epa2.setIs_valid(true);
		epa2.setTime(now);
		epa2.setPlatform("PunchCardMachine");
		epa2.setMach_sn("bfbfbffdfdfds");
		
		EpAtten epa3 = new EpAtten();
		epa3.setEp_uid(120);
		epa3.setType("");
		epa3.setIs_valid(false);
		epa3.setTime(now);
		epa3.setPlatform("<UNKNOWN>");
		epa3.setMach_sn("zzzzcccc");
		
		List<EpAtten> epas = new ArrayList<EpAtten>();
		epas.add(epa1);
		epas.add(epa2);
		epas.add(epa3);
		
		int batch_ret = epAttenDao.batchInsertEpAttens(epas);
		
		if(batch_ret <= 0) {
			logger.error("批量添加打卡数据失败，batch_ret = " + batch_ret);
			return;
		}
		
		logger.error("批量添加打卡数据成功，batch_ret = " + batch_ret);
		
	}
	
	
	
	@Test
	public void testInsertEpAtten() {
		EpAtten epa = new EpAtten();
		epa.setEp_uid(33);
		epa.setTime(new Date());
		epa.setType("GPS");
		epa.setLatitude(21.5415580174);
		epa.setLongtitude(113.0623638967);
//		epa.setIs_valid(false);
		epa.setCount(2);
		
		logger.info("添加打卡记录信息之前 + epa = " + epa);
		
		int ret = epAttenDao.insertEpAtten(epa);
		if(ret > 0) {
			logger.info("添加一条打卡记录信息成功 + ret = " + ret);
			logger.info("添加成功后，epa = " + epa);
		} else {
			logger.error("添加打卡记录信息失败");
		}
	}
	
	@Test
	public void testQueryLastPunchCardCountToday() {
		EpAtten epa = new EpAtten();
		epa.setEp_uid(5);
		Integer max_count = epAttenDao.queryLastPunchCardCountToday(epa);
		logger.info("最后一次打卡记录是第几次？ + max_count = " + max_count);
	}
	
	@Test
	public void testQueryEpAttenByUidAndDayRange() {
		int uid = 16;
		int day_range = 2;
		
		List<EpAtten> epas = epAttenDao.queryEpAttenByUidAndDayRange(uid, day_range);
		
		if(epas != null) {
			System.out.println("epas Class = " + epas.getClass());
			if(epas.size() > 0) {
				logger.info("根据用户id和时间范围查询用户打卡记录信息成功");
				for(EpAtten epa:epas) {
					System.out.println(epa);
				}
			} else {
				logger.error("根据用户id和时间范围未查询到该用户的打卡记录信息");
			}
		} else {
			logger.error("查询数据库失败");
		}
	}
	
	@Test
	public void testQueryEpAttenByStartDateAndEndDate() {
		Date start_date = DateUtil.parse2date(2, "2016-12-11 01:33:20");
		Date end_date = DateUtil.parse2date(2, "2016-12-09 08:56:20");
		
		List<EpAtten> ret_epas = epAttenDao.queryEpAttenByStartDateAndEndDate(start_date, end_date);
		
		if(ret_epas != null) {
			if(ret_epas.size() > 0) {
				logger.info("根据给定的日期范围查询打卡记录成功：ret_epas.size() = " + ret_epas.size());
//				for(EpAtten epa:ret_epas) {
//					System.out.println(epa);
//				}
				for(EpAtten epa:ret_epas) {
					System.out.println("用户id = " + epa.getEp_uid() + "，打卡时间：" + DateUtil.formatDate(2, epa.getTime()));
				}
			} else {
				logger.info("根据给定的日期范围查询打卡记录，结果为空");
			}
		} else {
			logger.error("根据给定的日期范围查询打卡记录信息失败");
		}
		
	}
	
	@Test
	public void testQueryEpAttenByEpUidAndStartDateAndEndDate() {
		int ep_uid = 1454;
		Date start_date = DateUtil.parse2date(2, "2016-12-01 01:33:20");
		Date end_date = DateUtil.parse2date(2, "2016-12-12 08:56:20");
		
		List<EpAtten> ret_epas = epAttenDao.queryEpAttenByEpUidAndStartDateAndEndDate(ep_uid, start_date, end_date);
		
		if(ret_epas != null) {
			if(ret_epas.size() > 0) {
				logger.info("根据用户id和给定的日期范围查询打卡记录成功：ret_epas.size() = " + ret_epas.size());
				for(EpAtten epa:ret_epas) {
					System.out.println("用户id = " + epa.getEp_uid() + "，打卡时间：" + DateUtil.formatDate(2, epa.getTime()));
				}
			} else {
				logger.info("根据用户id和给定的日期范围查询打卡记录，结果为空");
			}
		} else {
			logger.error("根据用户id和给定的日期范围查询打卡记录信息失败");
		}
	}
	
	@Test
	public void testQueryValidEpAttenByEpUidAndStartDateAndEndDate() {
		int ep_uid = 1454;
		Date start_date = DateUtil.parse2date(2, "2016-12-01 01:33:20");
		Date end_date = DateUtil.parse2date(2, "2016-12-12 08:56:20");
		
		List<EpAtten> ret_epas = epAttenDao.queryValidEpAttenByEpUidAndStartDateAndEndDate(ep_uid, start_date, end_date);
		
		if(ret_epas != null) {
			if(ret_epas.size() > 0) {
				logger.info("根据用户id和给定的日期范围查询全部有效的打卡记录成功：ret_epas.size() = " + ret_epas.size());
				for(EpAtten epa:ret_epas) {
					System.out.println("用户id = " + epa.getEp_uid() + "，打卡时间：" + DateUtil.formatDate(2, epa.getTime()) + "，是否有效：" + epa.getIs_valid());
				}
			} else {
				logger.info("根据用户id和给定的日期范围查询有效的打卡记录，结果为空");
			}
		} else {
			logger.error("根据用户id和给定的日期范围查询全部有效的打卡记录信息失败");
		}
	}
	
}
