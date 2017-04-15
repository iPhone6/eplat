package org.zsl.testmybatis;

import java.util.ArrayList;
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
import com.cn.eplat.dao.IPushToHwDao;
import com.cn.eplat.model.PushToHw;
import com.cn.eplat.utils.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestPushToHwDao {
	

	private static Logger logger = Logger.getLogger(TestPushToHwDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	
	@Resource
	private IPushFilterLogDao pushFilterLogDao;
	
	@Resource
	private IPushToHwDao pushToHwDao;
	
	
	
	@Test
	public void testBatchInsertPushToHws() {
		List<PushToHw> pthws = new ArrayList<PushToHw>();
		
		Date now = new Date();
		
		for(int i=0; i<5; i++) {
			PushToHw pthw = new PushToHw();
			pthw.setDayof_week("qqqqqqqqqq-" + i + "-ppppppp");
			pthw.setDayof_date(DateUtil.calcXDaysAfterADate(i, now));
			pthws.add(pthw);
		}
		
		Integer ret = pushToHwDao.batchInsertPushToHws(pthws);
		
		if(ret == null || ret <= 0) {
			logger.error("批量插入数据失败, ret = " + ret);
			return;
		}
		
		logger.info("批量插入数据成功, ret = " + ret);
		
	}
	
	
}
