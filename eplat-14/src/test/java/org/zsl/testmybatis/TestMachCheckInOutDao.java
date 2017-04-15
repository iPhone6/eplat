package org.zsl.testmybatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.datasource.DataSourceContextHolder;
import com.cn.eplat.datasource.DataSourceType;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.dao.IMachCheckInOutDao;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.model.MachCheckInOut;
import com.cn.eplat.utils.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestMachCheckInOutDao {
	
	private static Logger logger = Logger.getLogger(TestMachCheckInOutDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	
	
	
	
	
	
	@Test
	public void testQueryMachCheckInOutByCheckTimeExcludeSomeUseridsTop100ByMachSn() {
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		Date check_time = DateUtil.parse2date(2, "2017-02-08 08:18:16");
		Integer[] exclude_uids_arr = {3};
		List<Integer> exclude_uids = new ArrayList<Integer>();
		
		Collections.addAll(exclude_uids, exclude_uids_arr);
		
		System.out.println("exclude_uids = " + exclude_uids);
		
		String sn = "3567170100018";
		
		List<MachCheckInOut> results = machCheckInOutDao.queryMachCheckInOutByCheckTimeExcludeSomeUseridsTop100ByMachSn(check_time, exclude_uids, sn);
		
		if(results == null || results.size() == 0) {
			logger.error("将部分userid排除在外后，按给定打卡时间开始往后查询前100条打卡机打卡数据失败");
			return;
		}
		
		logger.info("将部分userid排除在外后，按给定打卡时间开始往后查询前100条打卡机打卡数据成功，results.size() = " + results.size());
		for(MachCheckInOut mc : results) {
			System.out.println(mc);
		}
	}
	
	
	
	
	
	@Test
	public void testDeleteAllAccessCheckInOutsCopyInMySQL() {
		int del_ret = machCheckInOutDao.deleteAllAccessCheckInOutsCopyInMySQL();
		
		System.out.println("delete ret = " + del_ret);
		
	}
	
	
	
	
	@Test
	public void testBatchInsertAllAccessCheckinoutsToMySQLMachCheckInOutCopy() {
		
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		
		MachCheckInOut mc1 = new MachCheckInOut();
		MachCheckInOut mc2 = new MachCheckInOut();
		MachCheckInOut mc3 = new MachCheckInOut();
		MachCheckInOut mc4 = new MachCheckInOut();
		MachCheckInOut mc5 = new MachCheckInOut();
		
		mc1.setBadge_number("99");
		mc1.setUserid(133);
		
		mc2.setBadge_number("ggg");
		mc2.setUserid(32);
		
		mc3.setBadge_number("听听歌");
		mc3.setUserid(554);
		
		mc4.setBadge_number("肯佩斯dddee");
		mc4.setUserid(4);
		
		mc5.setBadge_number("pppppppdddd");
		mc5.setUserid(80);
		
		List<MachCheckInOut> mcs = new ArrayList<MachCheckInOut>();
		
		mcs.add(mc1);
		mcs.add(mc2);
		mcs.add(mc3);
		mcs.add(mc4);
		mcs.add(mc5);
		
		int res = machCheckInOutDao.batchInsertAllAccessCheckinoutsToMySQLMachCheckInOutCopy(mcs);
		
		System.out.println("res = " + res);
		
	}
	
	
	
	@Test
	public void testQueryMissedMachCheckInOutsByCompareAccessAndMySQLDatas() {
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		
		List<MachCheckInOut> missed_datas = machCheckInOutDao.queryMissedMachCheckInOutsByCompareAccessAndMySQLDatas();
		
		if(missed_datas == null || missed_datas.size() == 0) {
			logger.info("查询失败或结果为空");
			return;
		}
		
		logger.info("查询成功，missed_datas.size() = " + missed_datas.size());
		for(MachCheckInOut mc : missed_datas) {
			System.out.println(mc);
			
		}
		
	}
	
	
	
	
	@Test
	public void testQueryMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		Date check_time = DateUtil.parse2date(2, "2017-01-03 08:18:16");
		Integer[] exclude_uids_arr = {3};
		List<Integer> exclude_uids = new ArrayList<Integer>();
		
		Collections.addAll(exclude_uids, exclude_uids_arr);
		
		System.out.println("exclude_uids = " + exclude_uids);
		
		List<MachCheckInOut> results = machCheckInOutDao.queryMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100(check_time, exclude_uids);
		
		if(results == null || results.size() == 0) {
			logger.error("将部分userid排除在外后，按给定打卡时间开始往后查询前100条打卡机打卡数据失败");
			return;
		}
		
		logger.info("将部分userid排除在外后，按给定打卡时间开始往后查询前100条打卡机打卡数据成功，results.size() = " + results.size());
		for(MachCheckInOut mc : results) {
			System.out.println(mc);
		}
		
	}
	
	
	
	
	@Test
	public void testQueryLatestCheckTime() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		
		Date lct = machCheckInOutDao.queryLatestCheckTime();
		
		System.out.println("查询出的最晚的一次打卡记录的打卡时间是：" + DateUtil.formatDate(2, lct));
		
	}
	
	
	
	@Test
	public void testQueryMachCheckInOutWithMaxCheckTime() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		
		List<MachCheckInOut> mcios = machCheckInOutDao.queryMachCheckInOutWithMaxCheckTime();
		
		if(mcios == null || mcios.size() == 0) {
			logger.error("查询打卡时间最晚的打卡机打卡记录失败，或打卡时间最晚的打卡记录条数为0");
			return;
		}
		
		logger.info("查询打卡时间最晚的打卡机打卡记录成功");
		for(MachCheckInOut mcio : mcios) {
			System.out.println(mcio);
		}
		
	}
	
	
	@Test
	public void testQueryMachCheckInOutWithMaxCheckTimeByMachSn() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		
		String sn = "3567165200003";
		
		List<MachCheckInOut> res = machCheckInOutDao.queryMachCheckInOutWithMaxCheckTimeByMachSn(sn);
		
		if(res == null || res.size() == 0) {
			logger.error("查询失败");
			return;
		}
		
		logger.info("查询成功, res.size() = " + res.size());
		for(MachCheckInOut mc : res) {
			System.out.println(mc);
			
		}
		
	}
	
	
	@Test
	public void testQueryMachCheckInOutWithMaxId() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		
		MachCheckInOut mcio_maxid = machCheckInOutDao.queryMachCheckInOutWithMaxId();
//		MachCheckInOut mcio_maxid = machCheckInOutDao.queryMachCheckInOutWithMaxCheckTime();
		
		if(mcio_maxid == null) {
			logger.error("查询最后一条打卡机打卡数据失败");
			return;
		}
		
		logger.info("查询最后一条打卡机打卡数据成功");
		System.out.println(mcio_maxid);
	}
	
	
	
	
	@Test
	public void testQueryMachCheckInOutByNormalAndSpecialCriteria() {
		MachCheckInOut mcio_que = new MachCheckInOut();
		
		List<MachCheckInOut> match_mcio = machCheckInOutDao.queryMachCheckInOutByNormalAndSpecialCriteria(mcio_que);
		
		if(match_mcio == null || match_mcio.size() == 0) {
			logger.error("根据条件查询打卡机打卡数据失败");
			return;
		}
		
		logger.info("根据条件查询打卡机打卡数据成功，match_mcio.size() = " + match_mcio.size());
		System.out.println(match_mcio);
	}
	
	
	
	@Test
	public void testQueryMachCheckInOutByCriteria() {
		MachCheckInOut mcio_que = new MachCheckInOut();
//		mcio_que.setId(1);
		mcio_que.setPush_count(111);
		
		List<MachCheckInOut> match_mcio = machCheckInOutDao.queryMachCheckInOutByCriteria(mcio_que);
		
		if(match_mcio == null || match_mcio.size() == 0) {
			logger.error("根据条件查询打卡机打卡数据失败");
			return;
		}
		
		logger.info("根据条件查询打卡机打卡数据成功，match_mcio.size() = " + match_mcio.size());
		System.out.println(match_mcio);
		
	}
	
	
	@Test
	public void testBatchUpdateMachCheckInOutById() {
		
		Date now = new Date();
		
		MachCheckInOut mcio1 = new MachCheckInOut();
		mcio1.setId(1);
		mcio1.setCheck_time(now);
		mcio1.setPush_status("success");
		
		MachCheckInOut mcio2 = new MachCheckInOut();
		mcio2.setId(2);
		mcio2.setCheck_time(now);
		mcio2.setPush_status("failed");
		
		MachCheckInOut mcio3 = new MachCheckInOut();
		mcio3.setId(3);
		mcio3.setCheck_time(now);
		mcio3.setPush_status("push_success");
		
		MachCheckInOut mcio4 = new MachCheckInOut();
		mcio4.setId(5);
		mcio4.setCheck_time(now);
		mcio4.setPush_status("request_failed");
		mcio4.setPush_count(111);
		
		MachCheckInOut mcio5 = new MachCheckInOut();
		mcio5.setId(6);
		mcio5.setCheck_time(now);
		mcio5.setPush_status("uuuuu");
		mcio5.setPush_count(2);
		mcio5.setCheck_type("ttttppppp");
		mcio5.setVerify_code(19999);
		mcio5.setMemo_info("kkkk蓝绿色");
		mcio5.setSn("888uuuuuuu");
		mcio5.setUser_ext_fmt(99888);
		
		
		List<MachCheckInOut> mcios = new ArrayList<MachCheckInOut>();
		
		mcios.add(mcio1);
		mcios.add(mcio2);
		mcios.add(mcio3);
		mcios.add(mcio4);
		mcios.add(mcio5);
		
//		int bat_ret = machCheckInOutDao.batchUpdateMachCheckInOutById(mcios);
		int bat_ret = machCheckInOutDao.batchInsertMachCheckInOut(mcios);
		
		if(bat_ret <= 0) {
			logger.error("批量更新打卡机打卡数据失败，bat_ret = " + bat_ret);
			return;
		}
		
		logger.info("批量更新打卡机打卡数据成功，bat_ret = " + bat_ret);
		
	}
	
	
	@Test
	public void testQueryAllMachCheckInOut() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		List<MachCheckInOut> results = machCheckInOutDao.queryAllMachCheckInOut();
		
		if(results == null) {
			System.out.println("查询失败");
			return;
		}
		
		if(results.size() == 0) {
			System.out.println("查询结果为空");
			return;
		}
		
		for(MachCheckInOut ret : results) {
			System.out.println(ret);
		}
	}
	
	
	
	@Test
	public void testQueryMachCheckInOutByCheckTime() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		Date dt = DateUtil.parse2date(1, "2017-01-03");
//		List<MachCheckInOut> rets = machCheckInOutDao.queryMachCheckInOutByCheckTime(dt);
//		List<MachCheckInOut> rets = machCheckInOutDao.queryMachCheckInOutByCheckTimeTop100(dt);
		List<MachCheckInOut> rets = machCheckInOutDao.queryMachCheckInOutAndUserInfoByCheckTimeTop100(dt);
		
		if(rets == null) {
			System.out.println("查询失败");
			return;
		}
		
		if(rets.size() == 0) {
			System.out.println("查询结果为空");
			return;
		}
		
		for(MachCheckInOut ret : rets) {
			System.out.println(ret);
		}
	}
	
	
	
	@Test
	public void testQueryMachCheckInOutById() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		List<MachCheckInOut> mcios = machCheckInOutDao.queryMachCheckInOutById(1);
		if(mcios == null) {
			System.out.println("查询失败");
			return;
		}
		
		if(mcios.size() == 0) {
			System.out.println("查询结果为空");
			return;
		}
		
		for(MachCheckInOut mcio : mcios) {
			System.out.println(mcio);
		}
		
	}
	
	
	@Test
	public void testQueryEpUserById() {
		int id = 47;
		EpUser epu = epUserDao.queryEpUserById(id);
		if(epu != null) {
			logger.info("根据id查询用户信息成功");
			logger.info("epu = " + epu);
		} else {
			logger.info("根据id未查询到用户信息");
		}
	}
	
}
