package org.zsl.testmybatis;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IDeptClueDao;
import com.cn.eplat.dao.IDeptUserDao;
import com.cn.eplat.model.DeptUser;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestDeptUserDao {
	
	private static Logger logger = Logger.getLogger(TestEpUserDao.class);
	
	@Resource
	private IDeptUserDao deptUserDao;
	
	@Test
	public void testQueryDeptUserByDeptIdAndUid() {
		DeptUser du = new DeptUser();
		du.setDept_id(1);
		du.setEp_uid(3);
		DeptUser res = deptUserDao.queryDeptUserByDeptIdAndUid(du);
		if(res != null) {
			logger.info("根据部门id和用户uid查询到匹配的部门_用户对应关系信息");
			logger.info("res = " + res);
		} else {
			logger.error("未查询到相关信息");
		}
	}
	
	@Test
	public void testAddDeptUserInfo() {
		DeptUser du = new DeptUser();
		du.setDept_id(2);
		du.setEp_uid(2);
		du.setIs_manager(1);
		
		logger.info("添加前，du id = " + du.getId());
		int ret = deptUserDao.addDeptUserInfo(du);
		if(ret > 0) {
			logger.info("添加一条部门-员工对应关系信息成功");
			logger.info("添加成功后，du id = " + du.getId());
		} else {
			logger.error("添加部门-员工对应关系信息失败");
		}
	}
	
}
