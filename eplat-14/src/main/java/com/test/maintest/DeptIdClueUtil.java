package com.test.maintest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IDeptIdClueDao;
import com.cn.eplat.dao.IEpDeptDao;
import com.cn.eplat.model.DeptIdClue;
import com.cn.eplat.model.EpDept;
import com.cn.eplat.utils.DateUtil;

public class DeptIdClueUtil {
	
	@Resource
	private IEpDeptDao epDeptDao;
	@Resource
	private IDeptIdClueDao deptIdClueDao;
	
	private static Logger logger = Logger.getLogger(DeptIdClueUtil.class);
	
	private TreeMap<Integer, ArrayList<EpDept>> path_map = new TreeMap<Integer, ArrayList<EpDept>>();

	public TreeMap<Integer, ArrayList<EpDept>> getPath_map() {
		return path_map;
	}
	public void setPath_map(TreeMap<Integer, ArrayList<EpDept>> path_map) {
		this.path_map = path_map;
	}
	
	public TreeMap<Integer, String> transPathMap(TreeMap<Integer, ArrayList<EpDept>> path_map) {
		if(path_map == null) {
			return null;
		}
		
		TreeMap<Integer, String> trans_path_map = new TreeMap<Integer, String>();
		
		Set<Entry<Integer, ArrayList<EpDept>>> entrys = path_map.entrySet();
		Iterator<Entry<Integer, ArrayList<EpDept>>> entrys_iterator = entrys.iterator();
		while(entrys_iterator.hasNext()) {
			String path_str = "-";
			Entry<Integer, ArrayList<EpDept>> next_entry = entrys_iterator.next();
			ArrayList<EpDept> entry_value = next_entry.getValue();
			if(entry_value == null) {
				continue;
			}
			for(EpDept ed:entry_value) {
				path_str = path_str + ed.getId() + "-";
			}
			trans_path_map.put(next_entry.getKey(), path_str);
		}
		
		return trans_path_map;
	}
	
	/**
	 * 刷新部门id线索信息
	 * </br>
	 * 当传入的根部门id为null或负数或0时，默认刷新所有根部门下的部门id线索；</br>
	 * 当传入的根部门id不为null且为正数时，如果这个部门不存在或不是根部门（根部门：上级id为0的部门），则不进行刷新操作；</br>
	 * 		如果这个部门存在且是根部门，才开始进行刷新操作。</br>
	 * @param root_did
	 */
	@Test
	public void refreshDeptIdClue(Integer root_did) {
		EpDept epd_que = new EpDept();
		epd_que.setSuperior_id(0);
		DeptIdClueUtil dicu = new DeptIdClueUtil();
		
		if(root_did == null || root_did <= 0) {	// 刷新所有根部门下的部门id线索
			List<EpDept> root_epds = epDeptDao.queryEpDeptByCriteria(epd_que);
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
			List<EpDept> root_epd = epDeptDao.queryEpDeptByCriteria(epd_que);
			if(root_epd.size() == 1) {
				EpDept root_one = root_epd.get(0);
				EpDept epd_tree = dicu.genEpDeptTree(root_did);
				dicu.iterateEpDept(epd_tree, new Stack<EpDept>());
				TreeMap<Integer, ArrayList<EpDept>> path_map = dicu.getPath_map();
				int rmv_count = deptIdClueDao.deleteDeptIdClueByRootDid(root_did);
				logger.info("已移除部门id线索信息串( " + rmv_count + " )条");
				TreeMap<Integer, String> trans_path_map = dicu.transPathMap(path_map);
				Collection<String> path_values = trans_path_map.values();
				for(String path:path_values) {
					DeptIdClue dic_new = new DeptIdClue();
					dic_new.setDept_id_clue(path);
					dic_new.setCompany_name(root_one.getName());
					deptIdClueDao.insertOneDeptIdClue(dic_new);
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
	 * 广度优先搜索算法遍历部门树形结构</br>
	 * 输出：遍历过程中部门的id</br>
	 * 
	 * @param root_epd	根部门节点对象
	 */
	public void traverseEpDeptTreeWidthPrior(EpDept root_epd, LinkedList<EpDept> queue) {
		if(root_epd == null || queue == null) {
			return;
		}
		
		Integer root_id = root_epd.getId();
		if(root_id == null || root_id <= 0) {
			return;
		}
		queue.offer(root_epd);
//		System.out.println(root_id + "--");
		
		List<EpDept> childs_epd = root_epd.getChilds();
		if(childs_epd == null || childs_epd.size() == 0) {
			return;
		}
		
		for(EpDept epd:childs_epd) {
			queue.offer(epd);
			System.out.println("--" + epd.getId());
		}
		
		for(EpDept epd:childs_epd) {
			traverseEpDeptTreeWidthPrior(epd, queue);
		}
	}
	
	/** 【OK】
	 * 层次遍历（即广度优先遍历）算法
	 * 
	 * @param root_epd
	 * @param queue
	 */
	public void traverseEpDeptTreeLevelOrder(EpDept root_epd, LinkedList<EpDept> queue) {
		if(root_epd == null || queue == null) {
			return;
		}
		
		Integer root_id = root_epd.getId();
		if(root_id == null || root_id <= 0) {
			return;
		}
		
		queue.offer(root_epd);
		
		while(queue.peek() != null) {
			EpDept epd_out = queue.poll();
			System.out.print(epd_out.getId() + " - ");
			
			List<EpDept> childs_epd = epd_out.getChilds();
			for(EpDept epd:childs_epd) {
				queue.offer(epd);
			}
		}
		
	}
	
	/** 【OK】
	 * 层次遍历（即广度优先遍历）算法2
	 * 
	 * @param root_epd
	 * @param queue
	 */
	public void traverseEpDeptTreeLevelOrder2(EpDept root_epd) {
		if(root_epd == null) {
			return;
		}
		
		Integer root_id = root_epd.getId();
		if(root_id == null || root_id <= 0) {
			return;
		}
		
		LinkedList<EpDept> queue = new LinkedList<EpDept>();
		
		queue.offer(root_epd);
		
		while(queue.peek() != null) {
			EpDept epd_out = queue.poll();
			System.out.print(epd_out.getId() + " - ");
			
			List<EpDept> childs_epd = epd_out.getChilds();
			for(EpDept epd:childs_epd) {
				queue.offer(epd);
			}
		}
		
	}
	
	/**
	 * 深度优先搜索算法遍历部门树形结构</br>
	 * 输出：遍历过程中的部门id</br>
	 * 
	 * @param root_epd	根部门节点对象
	 * @param queue
	 */
	public void traverseEpDeptTreeDepthPrior(EpDept root_epd, LinkedList<Iterator<EpDept>> queue) {
		if(root_epd == null || queue == null) {
			return;
		}
		
		
		
		
		
	}
	
	/** 【OK】
	 * 先根遍历（即深度优先遍历）算法
	 * 
	 * @param root_epd
	 * @param stack
	 */
	public void traverseEpDeptTreeDepthOrder(EpDept root_epd, LinkedList<EpDept> stack) {
		if(root_epd == null || stack == null) {
			return;
		}
		
		Integer root_id = root_epd.getId();
		if(root_id == null || root_id <= 0) {
			return;
		}
		
		stack.offerFirst(root_epd);
		
		while(stack.peekFirst() != null) {
			EpDept epd_top = stack.pollFirst();
			System.out.print(epd_top.getId() + " -> ");
			
			List<EpDept> childs_epd = epd_top.getChilds();
			int childs_count = childs_epd.size();
			for(int i=childs_count-1; i>=0; i--) {
				stack.offerFirst(childs_epd.get(i));
			}
		}
		
	}
	
	/** 【OK】
	 * 先根遍历（即深度优先遍历）算法2
	 * 
	 * @param root_epd
	 * @param queue
	 */
	public void traverseEpDeptTreeDepthOrder2(EpDept root_epd) {
		if(root_epd == null) {
			return;
		}
		
		Integer root_id = root_epd.getId();
		if(root_id == null || root_id <= 0) {
			return;
		}
		
		LinkedList<EpDept> stack = new LinkedList<EpDept>();
		
		stack.offerFirst(root_epd);
		
		while(stack.peekFirst() != null) {
			EpDept epd_top = stack.pollFirst();
			System.out.print(epd_top.getId() + " -> ");
			
			List<EpDept> childs_epd = epd_top.getChilds();
//			if(childs_epd.isEmpty()) {
//				return;
//			}
			for(EpDept epd:childs_epd) {
				stack.offerFirst(epd);
			}
		}
	}
	
	/** 【OK】
	 * 利用双栈实现边深度优先遍历，边记录从根节点到每个叶子节点的路径链的非递归算法
	 * 
	 * @param root_epd 根部门节点
	 */
	public void traverseEpDeptTreeDepthOrder3(EpDept root_epd) {
		if(root_epd == null) {
			return;
		}
		
		Integer root_id = root_epd.getId();
		if(root_id == null || root_id <= 0) {
			return;
		}
		
		LinkedList<EpDept> stack = new LinkedList<EpDept>();
		LinkedList<EpDept> path_stack = new LinkedList<EpDept>();
		
		stack.offerFirst(root_epd);
		while(stack.peekFirst() != null) {
			EpDept epd_top = stack.pollFirst();
			List<EpDept> childs_epd = epd_top.getChilds();
			for(EpDept epd:childs_epd) {
				stack.offerFirst(epd);
			}
			
			path_stack.offerFirst(epd_top);
			if(epd_top.getChilds().isEmpty()) {
				EpDept top2 = stack.peekFirst();
				Iterator<EpDept> stack_iter = path_stack.iterator();
				boolean do_pop = true;	// 是否需要对path_stack栈做pop操作的标志，初值设为true，若遇到有一个节点的子节点还在栈stack里，则设do_pop为false（即停止对path_stack栈的pop操作）
				StringBuffer path_sb = new StringBuffer();
				
				while(stack_iter.hasNext()) {
					EpDept stack_next = stack_iter.next();
//					System.out.print(stack_next.getId() + "..");
					path_sb.insert(0, ".." + stack_next.getId());
					if(top2 != null) {
						boolean bcc = stack_next.getChilds().contains(top2);
						if(bcc) {
							do_pop = false;
						}
						if(do_pop) {
							stack_iter.remove();
						}
					}
				}
//				System.out.println();
				System.out.println(path_sb);
			}
		}
	}
	
	
	/** 【OK】
	 * 先序遍历/前序遍历
	 * 
	 * @param root_epd
	 */
	public void preOrderTraverse(EpDept root_epd) {
		if(root_epd == null) {
			return;
		}
		
		System.out.print(root_epd.getId() + "--");
		
		List<EpDept> childs_epd = root_epd.getChilds();
		
		if(childs_epd == null || childs_epd.isEmpty()) {
			return;
		}
		
		for(EpDept epd:childs_epd) {
			preOrderTraverse(epd);
		}
	}
	
	/** 【OK】
	 * 后序遍历
	 * 
	 * @param root_epd
	 */
	public void postOrderTraverse(EpDept root_epd) {
		if(root_epd == null) {
			return;
		}
		
		List<EpDept> childs_epd = root_epd.getChilds();
		
		for(EpDept epd:childs_epd) {
			postOrderTraverse(epd);
		}
		
		System.out.print(root_epd.getId() + "--");
		
		if(childs_epd == null || childs_epd.isEmpty()) {
//			System.out.print(root_epd.getId() + "--");
			return;
		}
	}
	
	
	/** 【OK】
	 * 递归迭代打印从根节点到叶子节点的路径
	 * 
	 * @param root_epd
	 * @param stack
	 */
	public void fromRootToLeafPath(EpDept root_epd, LinkedList<EpDept> stack) {
		if(root_epd == null || stack == null) {
			return;
		}
		
		stack.offerLast(root_epd);
		List<EpDept> childs_epd = root_epd.getChilds();
		if(childs_epd.isEmpty()) {
			Iterator<EpDept> iterator = stack.iterator();
			while(iterator.hasNext()) {
				EpDept next_epd = iterator.next();
				System.out.print("..." + next_epd.getId());
			}
			System.out.println();
			return;
		} else {
			for(EpDept epd:childs_epd) {
//				queue.offerLast(epd);
				fromRootToLeafPath(epd, stack);
				stack.pollLast();
			}
		}
	}
	
	/**
	 * 非递归方式打印从根节点到叶子节点的路径
	 * 
	 * @param root_epd
	 * @param stack
	 */
	public void fromRootToLeafPath2(EpDept root_epd) {
		if(root_epd == null) {
			return;
		}
		
		LinkedList<EpDept> stack = new LinkedList<EpDept>();
		
		stack.offerFirst(root_epd);
		
	}
	

	public void iterateEpDept(EpDept epd, Stack<EpDept> pathstack) {
		pathstack.push(epd);
		List<EpDept> epd_childs = epd.getChilds();
		if(epd_childs.size() == 0) {
			ArrayList<EpDept> list = new ArrayList<EpDept>();
			Iterator<EpDept> stack_iterator = pathstack.iterator();
			while(stack_iterator.hasNext()) {
				list.add(stack_iterator.next());
			}
			path_map.put(epd.getId(), list);
//			System.out.println("---->" + list);
			return;
		} else {
			Iterator<EpDept> childs_iterator = epd_childs.iterator();
			while(childs_iterator.hasNext()) {
				EpDept child = childs_iterator.next();
				iterateEpDept(child, pathstack);
				pathstack.pop();
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		long time_mills = 1483406001411l;
		Date date = new Date(time_mills);
		String format_date = DateUtil.formatDate(2, date);
		System.out.println(format_date);
	}
	
	
}
