package org.zsl.testmybatis;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IDeptClueDao;
import com.cn.eplat.dao.IEpDeptDao;
import com.cn.eplat.model.DeptClue;
import com.cn.eplat.model.EpDept;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestDeptClueDao {
	
	private static Logger logger = Logger.getLogger(TestDeptClueDao.class);
	
	@Resource
	private IDeptClueDao deptClueDao;
	@Resource
	private IEpDeptDao epDeptDao;
	
	@Test
	public void testQueryDeptClueByDeptClueStr() {
		String dept_clue_str = "123";
		DeptClue dc = deptClueDao.queryDeptClueByDeptClueStr(dept_clue_str);
		if(dc != null) {
			logger.info("根据部门线索信息字符串查询部门线索信息成功！");
			logger.info("dc = " + dc);
		} else {
			logger.debug("根据部门线索信息字符串查询部门线索信息失败！");
		}
	}
	
	@Test
	public void testAddDeptClue() {
		DeptClue dc = new DeptClue();
		dc.setClue_str("klsf-  llpp33");
		dc.setCompany_name("Mndd解决");
		dc.setLast_dept_id(3);
		logger.info("添加前，dc = " + dc);
		
		int ret = deptClueDao.addDeptClue(dc);
		if(ret > 0) {
			logger.info("添加一条部门线索信息成功 + ret = " + ret);
			logger.info("添加成功后，dc = " + dc);
		} else {
			logger.error("添加部门线索信息失败");
		}
	}
	
	
	public String[] genDeptIdClueStrsByRootDeptId(Integer rootDid) {
		if(rootDid == null || rootDid <= 0) {	// 如果传入的根部门id为null或负数或0时，直接返回null
			return null;
		}
		
//		if(rootDid == 0) {
//			return null;
//		}
		
		EpDept ed_que = new EpDept();
		ed_que.setSuperior_id(rootDid);
		List<EpDept> sub_depts = epDeptDao.queryEpDeptByCriteria(ed_que);
		if(sub_depts == null) {
			return null;
		}
		
		int sub_count = sub_depts.size();
		if(sub_count == 0) {
			return new String[]{"-" + rootDid + "-"};
		}
		
		String[] strs1 = new String[sub_count];
		int strs1_len = strs1.length;
		
		for(int i=0; i<strs1_len; i++) {
			EpDept epd = sub_depts.get(i);
			Integer epd_id = epd.getId();
			if(epd_id == null || epd_id <= 0) {
				return null;
			}
//			EpDept epd_que = new EpDept();
//			epd_que.setSuperior_id(epd_id);
//			List<EpDept> sub_epds = epDeptService.findEpDeptByCriteria(epd_que);
//			if(sub_epds == null) {
//				return null;
//			}
			String[] strs2 = genDeptIdClueStrsByRootDeptId(epd_id);
			int strs2_len = strs2.length;
			
			for(int j=0; j< strs2_len; j++) {
				strs1[i] = "-" + epd_id + strs2[j];
			}
//			return strs2;
		}
		
		return strs1;
	}
	
	
	@Test
	public void testGenTreeClueStrs() {
		String[] gen_strs = genDeptIdClueStrsByRootDeptId(57);
		for(String str:gen_strs) {
			System.out.println(str);
		}
	}
	
}
