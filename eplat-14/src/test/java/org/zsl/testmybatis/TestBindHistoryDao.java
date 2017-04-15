package org.zsl.testmybatis;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IBindHistoryDao;
import com.cn.eplat.dao.IEpAttenDao;
import com.cn.eplat.dao.IEpDeviceDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.BindHistory;
import com.cn.eplat.model.EpDevice;
import com.cn.eplat.utils.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestBindHistoryDao {
	
	private static Logger logger = Logger.getLogger(TestEpDeviceDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpAttenDao epAttenDao;
	@Resource
	private IEpDeviceDao epDeviceDao;
	@Resource
	private IBindHistoryDao bindHistoryDao;
	
	
	
	@Test
	public void testInsertBindHistory() {
		BindHistory new_bh = new BindHistory();
		new_bh.setEp_uid(10);
		new_bh.setDevice_id(12);
		new_bh.setTime(new Date());
		new_bh.setType("bind");
		
		System.out.println("插入前：new_bh = " + new_bh);
		int ret = bindHistoryDao.insertBindHistory(new_bh);
		if(ret <= 0) {
			logger.error("插入一条绑定历史信息失败");
			return;
		}
		logger.info("插入一条绑定历史信息成功，ret = " + ret);
		System.out.println("插入后：new_bh = " + new_bh);
	}
	
	@Test
	public void testQueryBindHistoryById() {
		int id = 13;
		BindHistory bh = bindHistoryDao.queryBindHistoryById(id);
		if(bh == null) {
			logger.error("未查询到匹配的绑定历史信息记录");
			return;
		}
		logger.info("查询到匹配的绑定历史信息记录");
		System.out.println("bh = " + bh);
	}
	
	@Test
	public void testUpdateBindHistoryById() {
		BindHistory bh_upd = new BindHistory();
		bh_upd.setId(9);
		bh_upd.setTime(new Date());
		bh_upd.setType("unbind");
		bh_upd.setWhich_round_same(9);
		bh_upd.setWhich_round_diff(20);
		int res = bindHistoryDao.updateBindHistoryById(bh_upd);
		if(res <= 0) {
			logger.error("根据id修改绑定历史信息失败");
			return;
		}
		logger.info("根据id修改绑定历史信息成功，res = " + res);
	}
	
	
	@Test
	public void testQueryBindHistoryByCriterion() {
		BindHistory bh_que = new BindHistory();
		bh_que.setEp_uid(2);
		
		List<BindHistory> bhs = bindHistoryDao.queryBindHistoryByCriterion(bh_que);
		if(bhs == null) {
			logger.error("根据除id以外的条件查询绑定历史信息失败");
			return;
		}
		
		int bhs_size = bhs.size();
		if(bhs_size == 0) {
			logger.error("根据除id以外的条件查询绑定历史信息为空");
			return;
		}
		
		logger.info("根据除id以外的条件查询绑定历史信息成功，bhs_size = " + bhs_size);
		for(BindHistory bh:bhs) {
			System.out.println(bh);
		}
	}
	
	@Test
	public void testQueryBindHistoryByCriterionOrderByTime() {
		BindHistory bh_que = new BindHistory();
		bh_que.setEp_uid(2);
		
		List<BindHistory> bhs = bindHistoryDao.queryBindHistoryByCriterionOrderByTime(bh_que);
		if(bhs == null) {
			logger.error("根据除id以外的条件查询绑定历史信息失败");
			return;
		}
		
		int bhs_size = bhs.size();
		if(bhs_size == 0) {
			logger.error("根据除id以外的条件查询绑定历史信息为空");
			return;
		}
		
		logger.info("根据除id以外的条件查询绑定历史信息成功，bhs_size = " + bhs_size);
		for(BindHistory bh:bhs) {
			System.out.println(bh);
		}
	}
	
	@Test
	public void testQueryBindHistoryByCriterionValidOrderByTime() {
		BindHistory bh_que = new BindHistory();
		bh_que.setEp_uid(2);
		Date start_date = DateUtil.parse2date(1, "2016-12-19");
		int range = 4;
		
		List<BindHistory> bhs = bindHistoryDao.queryBindHistoryByCriterionValidOrderByTime(bh_que, start_date, range);
		if(bhs == null) {
			logger.error("根据除id以外的条件查询绑定历史信息失败");
			return;
		}
		
		int bhs_size = bhs.size();
		if(bhs_size == 0) {
			logger.error("根据除id以外的条件查询绑定历史信息为空");
			return;
		}
		
		logger.info("根据除id以外的条件查询绑定历史信息成功，bhs_size = " + bhs_size);
		for(BindHistory bh:bhs) {
			System.out.println(bh);
		}
	}
	
	@Test
	public void testQueryBindHistoryByCriterionValidOrderByTimeV2() {
		BindHistory bh_que = new BindHistory();
		bh_que.setEp_uid(2);
		Date start_date = DateUtil.parse2date(1, "2016-12-19");
		int range = 4;
		
		List<BindHistory> bhs = bindHistoryDao.queryBindHistoryByCriterionValidOrderByTimeV2(bh_que, start_date, range);
		if(bhs == null) {
			logger.error("根据除id以外的条件查询绑定历史信息失败");
			return;
		}
		
		int bhs_size = bhs.size();
		if(bhs_size == 0) {
			logger.error("根据除id以外的条件查询绑定历史信息为空");
			return;
		}
		
		logger.info("根据除id以外的条件查询绑定历史信息成功，bhs_size = " + bhs_size);
		for(BindHistory bh:bhs) {
			System.out.println(bh);
		}
	}
	
	
	@Test
	public void testQueryMultiParams() {
		BindHistory bh = new BindHistory();
		bh.setEp_uid(2);
		int did = 3;
		
		List<BindHistory> bhs = bindHistoryDao.testQueryMultiParams(bh, did);
		System.out.println("bhs.size() = " + bhs.size());
		System.out.println(bhs);
	}
	
	
	@Test
	public void testQueryLastBindHistoryValidByEpUid() {
		int ep_uid = 6;
		Date start_date = DateUtil.parse2date(1, "2016-12-19");
		int range = 4;
		
		BindHistory ret_bh = bindHistoryDao.queryLastBindHistoryValidByEpUid(ep_uid, start_date, range);
		if(ret_bh == null) {
			logger.error("查询该用户最后一条绑定历史记录信息失败");
			return;
		}
		logger.error("查询该用户最后一条绑定历史记录信息成功");
		System.out.println(ret_bh);
	}
	
	
}
