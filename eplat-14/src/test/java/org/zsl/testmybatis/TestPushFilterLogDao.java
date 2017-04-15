package org.zsl.testmybatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.dao.IMachCheckInOutDao;
import com.cn.eplat.dao.IPushFilterLogDao;
import com.cn.eplat.datasource.DataSourceContextHolder;
import com.cn.eplat.datasource.DataSourceType;
import com.cn.eplat.model.MachCheckInOut;
import com.cn.eplat.model.PushFilterLog;
import com.cn.eplat.utils.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestPushFilterLogDao {
	
	private static Logger logger = Logger.getLogger(TestPushFilterLogDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	
	@Resource
	private IPushFilterLogDao pushFilterLogDao;
	
	
	
	
	@Test
	public void testQueryEarliestPushFilterLogTime() {
		Date earliest_date = pushFilterLogDao.queryEarliestPushFilterLogTime();
		
		if(earliest_date == null) {
			logger.error("查询失败");
			return;
		}
		
		logger.info("查询成功, earliest_date = " + DateUtil.formatDate(2, earliest_date));
		
	}
	
	
	
	
	@Test
	public void testBatchInsertPushFilterLogs() {
		
		List<PushFilterLog> pfls = new ArrayList<PushFilterLog>();
		
		Date now = new Date();
		
		for(int i=1; i<10; i++) {
			PushFilterLog pfl = new PushFilterLog();
			pfl.setStatus("bad boy!");
			pfl.setDayof_date(DateUtil.calcXDaysAfterADate(i-4, now));
			pfl.setDescribe("这是一个测试。。。2");
			pfls.add(pfl);
		}
		
		int ret = pushFilterLogDao.batchInsertPushFilterLogs(pfls);
		
		if(ret <= 0) {
			logger.error("批量插入数据失败, ret = " + ret);
			return;
		}
		
		logger.info("批量插入数据成功, ret = " + ret);
		
		
	}
	
	
	
	@Test
	public void testQueryFilteredDates() {
		List<Date> filtered_dates = pushFilterLogDao.queryFilteredDates();
		
		if(filtered_dates == null || filtered_dates.size() == 0) {
			logger.error("查询已筛选过的考勤日期失败");
			return;
		}
		
		logger.info("查询已筛选过的考勤日期成功，filtered_dates.size() = " + filtered_dates.size());
		for(Date date : filtered_dates) {
			System.out.println(DateUtil.formatDate(2, date));
			
		}
	}
	
}
