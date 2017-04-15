package org.zsl.testmybatis;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.eplat.dao.IEpDeptDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpDept;
import com.cn.eplat.model.EpUser;
import com.test.maintest.DeptIdClueUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestEpDeptDao {
	
	private static Logger logger = Logger.getLogger(TestEpDeptDao.class);
	
	@Resource
	private IEpDeptDao epDeptDao;
	
	@Test
	public void testAddEpDept() {
		EpDept epd = new EpDept();
		epd.setName("hyperloop three");
		epd.setLevel(3);
		epd.setSuperior_id(2);
		
		logger.info("添加前，epd id = " + epd.getId());
		
		int ret = epDeptDao.addEpDept(epd);
		
		if(ret > 0) {
			logger.info("添加一条部门信息成功！+ ret = " + ret);
			logger.info("添加后，epd id = " + epd.getId());
		} else {
			logger.error("添加部门信息失败！+ ret = " + ret);
		}
	}
	
	@Test
	public void testQueryEpDeptByCriteria() {
		EpDept ed = new EpDept();
		ed.setId(2);
		ed.setName("");
		ed.setSuperior_id(0);
		ed.setLevel(0);
		ed.setOrder(1);
		
		List<EpDept> res = epDeptDao.queryEpDeptByCriteria(ed);
		if(res != null) {
			logger.info("根据条件查询部门信息成功 + res.size() = " + res.size());
			for(EpDept epd:res) {
				System.out.println(epd);
			}
		} else {
			logger.error("根据条件查询部门信息失败");
		}
	}
	
	public EpDept genEpDeptTree(Integer epd_id) {
		if(epd_id == null || epd_id <= 0) {
			return null;
		}
		
		EpDept epd_que = new EpDept();
		epd_que.setId(epd_id);
		List<EpDept> epd_ret = epDeptDao.queryEpDeptByCriteria(epd_que);
		if(epd_ret == null || epd_ret.size() == 0) {
			return null;
		}
		
		EpDept epd_one = epd_ret.get(0);
		EpDept epd_query = new EpDept();
		epd_query.setSuperior_id(epd_id);
		List<EpDept> epd_childs = epDeptDao.queryEpDeptByCriteria(epd_query);
		for(EpDept epd:epd_childs) {
			EpDept edt = genEpDeptTree(epd.getId());
			epd_one.getChilds().add(edt);
		}
		
		return epd_one;
	}
	
	/**
	 * 构造部门树形结构
	 */
	@Test
	public void constructEpDeptTree() {
		EpDept gen_ret = genEpDeptTree(57);
//		System.out.println(gen_ret);
		
		DeptIdClueUtil dicu = new DeptIdClueUtil();
		dicu.iterateEpDept(gen_ret, new Stack<EpDept>());
//		System.out.println(dicu.getPath_map());
		TreeMap<Integer, ArrayList<EpDept>> path_map = dicu.getPath_map();
		Set<Integer> keys = path_map.keySet();
		System.out.println("keys count = " + keys.size());
		Iterator<Integer> keys_iterator = keys.iterator();
		while(keys_iterator.hasNext()) {
			Integer next_key = keys_iterator.next();
//			System.out.println("key: " + next_key);
			ArrayList<EpDept> dept_list = path_map.get(next_key);
			for(EpDept ed:dept_list) {
				System.out.print(ed.getId() + "-");
			}
			System.out.println();
		}
		
		System.out.println("========================");
		TreeMap<Integer, String> trans_path_map = dicu.transPathMap(path_map);
		System.out.println(JSONObject.toJSONString(trans_path_map));
		
		System.out.println("========================");
//		dicu.traverseEpDeptTreeWidthPrior(gen_ret, new LinkedList<EpDept>());
		dicu.preOrderTraverse(gen_ret);
		System.out.println("\n深度优先遍历/先根遍历：");
		dicu.traverseEpDeptTreeDepthOrder(gen_ret, new LinkedList<EpDept>());
		System.out.println("\n深度优先遍历/先根遍历2：");
		dicu.traverseEpDeptTreeDepthOrder2(gen_ret);
		System.out.println("\n深度优先遍历/先根遍历3，并输出从根节点到叶子节点的路径：");
		dicu.traverseEpDeptTreeDepthOrder3(gen_ret);
		System.out.println("\n========================");
//		dicu.traverseEpDeptTreeLevelOrder(gen_ret, new LinkedList<EpDept>());
		dicu.traverseEpDeptTreeLevelOrder2(gen_ret);
		System.out.println();
		dicu.postOrderTraverse(gen_ret);
		System.out.println("\n----------------------------\n");
		dicu.fromRootToLeafPath(gen_ret, new LinkedList<EpDept>());
		
		
	}
	
	
	@Test
	public void testRefreshDeptIdClue() {
		DeptIdClueUtil dicu = new DeptIdClueUtil();
		dicu.refreshDeptIdClue(57);
	}
	
	
	public static void main(String[] args) {
		
	}
	
	
}
