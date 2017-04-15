package com.cn.eplat.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cn.eplat.model.DeptIdClue;
import com.cn.eplat.model.EpDept;
import com.cn.eplat.service.IDeptIdClueService;
import com.cn.eplat.service.IEpDeptService;
import com.test.maintest.DeptIdClueUtil;

@Controller
@RequestMapping("/deptIdClueController")
public class DeptIdClueController {
	private static Logger logger = Logger.getLogger(EpDataController.class);
	
	@Resource
	private IEpDeptService epDeptService;
	@Resource
	private IDeptIdClueService deptIdClueService;
//	@Resource
//	private DeptIdClueUtil deptIdClueUtil;
	
	/**
	 * 刷新部门id线索信息
	 * </br>
	 * 当传入的根部门id为null或负数或0时，默认刷新所有根部门下的部门id线索；</br>
	 * 当传入的根部门id不为null且为正数时，如果这个部门不存在或不是根部门（根部门：上级id为0的部门），则不进行刷新操作；</br>
	 * 		如果这个部门存在且是根部门，才开始进行刷新操作。</br>
	 * @param root_did
	 */
	public void refreshDeptIdClue(Integer root_did) {
		EpDept epd_que = new EpDept();
		epd_que.setSuperior_id(0);
		DeptIdClueUtil dicu = new DeptIdClueUtil();
		
		if(root_did == null || root_did <= 0) {	// 刷新所有根部门下的部门id线索
			List<EpDept> root_epds = epDeptService.findEpDeptByCriteria(epd_que);
			if(root_epds.size() > 0) {
				for(EpDept epd:root_epds) {
					Integer epd_id = epd.getId();
					if(epd_id == null || epd_id <= 0) {
						continue;
					}
					refreshDeptIdClue(epd_id);
				}
			}
		} else {
			epd_que.setId(root_did);
//			epd_que.setSuperior_id(0);
			List<EpDept> root_epd = epDeptService.findEpDeptByCriteria(epd_que);
			if(root_epd.size() == 1) {
				EpDept root_one = root_epd.get(0);
				EpDept epd_tree = genEpDeptTree(root_did);
				dicu.iterateEpDept(epd_tree, new Stack<EpDept>());
				TreeMap<Integer, ArrayList<EpDept>> path_map = dicu.getPath_map();
				int rmv_count = deptIdClueService.removeDeptIdClueByRootDid(root_did);
				logger.info("已移除部门id线索信息串( " + rmv_count + " )条");
				TreeMap<Integer, String> trans_path_map = dicu.transPathMap(path_map);
				Collection<String> path_values = trans_path_map.values();
				for(String path:path_values) {
					DeptIdClue dic_new = new DeptIdClue();
					dic_new.setDept_id_clue(path);
//					dic_new.setLevel_count(values_count);
					dic_new.setCompany_name(root_one.getName());
					deptIdClueService.addOneDeptIdClue(dic_new);
				}
			}
		}
	}
	
	public EpDept genEpDeptTree(Integer epd_id) {
		if(epd_id == null || epd_id <= 0) {
			return null;
		}
		
		EpDept epd_que = new EpDept();
		epd_que.setId(epd_id);
		List<EpDept> epd_ret = epDeptService.findEpDeptByCriteria(epd_que);
		if(epd_ret == null || epd_ret.size() == 0) {
			return null;
		}
		
		EpDept epd_one = epd_ret.get(0);
		EpDept epd_query = new EpDept();
		epd_query.setSuperior_id(epd_id);
		List<EpDept> epd_childs = epDeptService.findEpDeptByCriteria(epd_query);
		for(EpDept epd:epd_childs) {
			EpDept edt = genEpDeptTree(epd.getId());
			epd_one.getChilds().add(edt);
		}
		
		return epd_one;
	}
	
	@RequestMapping(params = "refreshDeptIdClue", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String refreshDeptIdClueJson(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		String root_did_str = request.getParameter("root_did");
		int root_did = -1;
		try {
			root_did = Integer.parseInt(root_did_str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "根部门id转换异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
//		DeptIdClueUtil dicu = new DeptIdClueUtil();
		refreshDeptIdClue(root_did);
		
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "刷新部门id线索信息串完成");
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	
}
