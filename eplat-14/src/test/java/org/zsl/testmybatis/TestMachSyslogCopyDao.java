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
import com.cn.eplat.dao.IMachSyslogCopyDao;
import com.cn.eplat.datasource.DataSourceContextHolder;
import com.cn.eplat.datasource.DataSourceType;
import com.cn.eplat.model.MachSyslogCopy;
import com.cn.eplat.utils.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestMachSyslogCopyDao {
	
	
	private static Logger logger = Logger.getLogger(TestMachSyslogCopyDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	
	@Resource
	private IMachSyslogCopyDao machSyslogCopyDao;
	
	
	
	
	
	
	@Test
	public void testBatchUpdateMachSyslogCopyById() {
		MachSyslogCopy msc1 = new MachSyslogCopy();
		MachSyslogCopy msc2 = new MachSyslogCopy();
		MachSyslogCopy msc3 = new MachSyslogCopy();
		MachSyslogCopy msc4 = new MachSyslogCopy();
		
		msc1.setId(12l);
		msc2.setId(10l);
		
		msc1.setOperator("kkk44545");
		msc2.setOperator("tttppp");
		msc3.setOperator("快乐分手");
		msc4.setOperator("9999");
		
		msc1.setLog_descr("从设备下载记录数据");
		msc2.setLog_descr("从设备下载记录人员信息333222");
		msc3.setLog_descr("人员维护");
		msc4.setLog_descr("管理员设置");
		
		msc1.setSyslog_id(991);
		msc1.setLog_tag(123);
		msc1.setLog_time(new Date());
		msc1.setMachine_alias("pppeee");
		msc1.setStatus("已处理");
		
		msc2.setProc_result("已成功修改处理结果222");
		
		List<MachSyslogCopy> logs = new ArrayList<MachSyslogCopy>();
		
		logs.add(msc1);
		logs.add(msc2);
		logs.add(msc3);
		logs.add(msc4);
		
		String proc_result = "品牌额额000   ---   kkmmss";
		
//		int upd_ret = machSyslogCopyDao.batchUpdateMachSyslogCopyById(logs);
		int upd_ret = machSyslogCopyDao.batchUpdateMachSyslogCopyProcResultById(logs, proc_result);
		
		logger.info("update ret = " + upd_ret);
		
	}
	
	
	
	
	@Test
	public void testQueryProcessedSyslogsWithNullProcResult() {
		List<MachSyslogCopy> results = machSyslogCopyDao.queryProcessedSyslogsWithNullProcResult();
		
		if(results == null || results.size() == 0) {
			logger.error("查询出现异常，或结果为空");
			return;
		}
		
		logger.info("查询成功，results.size() = " + results.size());
		for(MachSyslogCopy result : results) {
			System.out.println(result);
			
		}
	}
	
	
	
	
	@Test
	public void testBatchInsertMachSyslogCopys() {
		MachSyslogCopy msc1 = new MachSyslogCopy();
		MachSyslogCopy msc2 = new MachSyslogCopy();
		MachSyslogCopy msc3 = new MachSyslogCopy();
		MachSyslogCopy msc4 = new MachSyslogCopy();
		
		msc1.setOperator("kkk44545");
		msc2.setOperator("ttt");
		msc3.setOperator("快乐分手");
		msc4.setOperator("9999");
		
		msc1.setLog_descr("从设备下载记录数据");
		msc2.setLog_descr("从设备下载记录人员信息");
		msc3.setLog_descr("人员维护");
		msc4.setLog_descr("管理员设置");
		
		msc1.setSyslog_id(991);
		msc1.setLog_tag(123);
		msc1.setLog_time(new Date());
		msc1.setMachine_alias("pppeee");
		msc1.setStatus("已处理");
		
		List<MachSyslogCopy> logs = new ArrayList<MachSyslogCopy>();
		
		logs.add(msc1);
		logs.add(msc2);
		logs.add(msc3);
		logs.add(msc4);
		
		int res = machSyslogCopyDao.batchInsertMachSyslogCopys(logs);
		
		logger.info("res = " + res);
		
		
	}
	
	
	
	
	@Test
	public void testQueryMachSyslogCopyByGivenTimeRange() {
		Date start = DateUtil.parse2date(2, "2017-01-16 08:00:00");
		Date end = DateUtil.parse2date(2, "2017-02-06 08:00:00");
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
//		List<MachSyslogCopy> results = machSyslogCopyDao.queryMachSyslogCopyByGivenTimeRange(start, end);
//		List<MachSyslogCopy> results = machSyslogCopyDao.queryMachSyslogCopyByGivenTimeRange(null, end);
//		List<MachSyslogCopy> results = machSyslogCopyDao.queryMachSyslogCopyByGivenTimeRange(null, null);
		List<MachSyslogCopy> results = machSyslogCopyDao.queryAllMachSyslogCopys();
		
		if(results == null || results.size() == 0) {
			logger.error("查询失败或结果为空");
			return;
		}
		
		logger.info("查询成功，results.size() = " + results.size());
		for(MachSyslogCopy msc : results) {
			System.out.println(msc);
			
		}
		
	}
	
	
	
	
	@Test
	public void testQueryLastestLogTime() {
		
		
		Date res = machSyslogCopyDao.queryLastestLogTime();
		
		if(res == null) {
			logger.error("查询失败");
			return;
		}
		
		logger.info("查询成功，res = " + DateUtil.formatDate(2, res));
		
	}
	
	
	
	
	@Test
	public void testQueryMachSyslogCopyAfterGivenTime() {
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		Date time = DateUtil.parse2date(2, "2017-02-06 10:05:00");
		
		List<MachSyslogCopy> syslogs = machSyslogCopyDao.queryMachSyslogCopyAfterGivenTime(time);
		
		if(syslogs == null || syslogs.size() == 0) {
			logger.error("查询失败");
			return;
		}
		
		logger.info("查询成功");
		for(MachSyslogCopy msc : syslogs) {
			System.out.println(msc);
		}
		
	}
	
	
	
	@Test
	public void testQueryMachSyslogCopyGtGivenId() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		
		Integer id = 12;
		
		List<MachSyslogCopy> syslogs = machSyslogCopyDao.queryMachSyslogCopyGtGivenId(id);
		
		if(syslogs == null) {
			logger.error("查询失败");
			return;
		}
		
		if(syslogs.size() == 0) {
			logger.info("查询结果为空");
			return;
		}
		
		logger.info("查询成功，syslogs.size() = " + syslogs.size());
		for(MachSyslogCopy msc : syslogs) {
			System.out.println(msc);
			
		}
		
	}
	
	
	
	@Test
	public void testQueryMaxSyslogId() {
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		
		Long max_id = machSyslogCopyDao.queryMaxSyslogId();
		
		if(max_id == null) {
			logger.error("查询失败");
			return;
		}
		
		logger.info("查询成功 + max_id = " + max_id);
		
	}
	
}
