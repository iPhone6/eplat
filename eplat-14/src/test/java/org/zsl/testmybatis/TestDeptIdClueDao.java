package org.zsl.testmybatis;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IDeptClueDao;
import com.cn.eplat.dao.IDeptIdClueDao;
import com.cn.eplat.model.DeptClue;
import com.cn.eplat.model.DeptIdClue;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestDeptIdClueDao {

	private static Logger logger = Logger.getLogger(TestDeptIdClueDao.class);
	
	@Resource
	private IDeptIdClueDao deptIdClueDao;
	
	@Test
	public void testInsertOneDeptIdClue() {
		DeptIdClue dic = new DeptIdClue();
		dic.setDept_id_clue("-0-3-6-19-20-22-");
		dic.setLevel_count(6);
		dic.setCompany_name("ppkkmm");
		
		System.out.println("插入前，dic id = " + dic.getId());
		int ret_ins = deptIdClueDao.insertOneDeptIdClue(dic);
		if(ret_ins > 0) {
			logger.info("插入一条部门id线索字符串信息成功，ret_ins = " + ret_ins);
			System.out.println("插入后，dic id = " + dic.getId());
			return;
		}
		logger.error("插入一条部门id线索字符串信息失败");
	}
	
	@Test
	public void testQueryAllDeptIdClueByDeptIdIncluded() {
		int dept_id = 3;
		List<DeptIdClue> res_dics = deptIdClueDao.queryAllDeptIdClueByDeptIdIncluded(dept_id);
		if(res_dics == null) {
			logger.error("根据部门id查询所有包含该部门id的部门id线索信息串时出现异常");
			return;
		}
		if(res_dics.size() == 0) {
			logger.info("根据部门id查询所有包含该部门id的部门id线索信息串，列表为空。res_dics.size() = 0");
			return;
		}
		
		int dics_count = res_dics.size();
		logger.info("根据部门id查询所有包含该部门id的部门id线索信息串，列表不为空。res_dics.size() = " + dics_count);
		for(DeptIdClue dic:res_dics) {
			System.out.println(dic);
		}
	}
	
	@Test
	public void testDeleteDeptIdClueByRootDid() {
		int root_did = 111;
		int del_count = deptIdClueDao.deleteDeptIdClueByRootDid(root_did);
		logger.info("已删除行数：del_count = " + del_count);
	}
	
}
