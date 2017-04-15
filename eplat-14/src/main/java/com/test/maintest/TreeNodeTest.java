package com.test.maintest;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zsl.testmybatis.TestDeptClueDao;

import com.alibaba.fastjson.JSONObject;
import com.cn.eplat.dao.IDeptClueDao;
import com.cn.eplat.dao.ITreeNodeDao;
import com.test.tree.TreeNode;

import com.test.imprv.TreeUtil;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TreeNodeTest {
	
	private static Logger logger = Logger.getLogger(TestDeptClueDao.class);
	
	@Resource
	private ITreeNodeDao treeNodeDao;
	
	@Test
	public void loadTree() throws Exception {
		System.out.println(JSONObject.toJSONString(recursiveTree(1)));
	}

	/**
	 * 递归算法解析成树形结构
	 *
	 * @param cid
	 * @return
	 * @author jiqinlin
	 */
	public TreeNode recursiveTree(int cid) {
		// 根据cid获取节点对象(SELECT * FROM tb_tree t WHERE t.cid=?)
		TreeNode node = treeNodeDao.getreeNode(cid);
		// 查询cid下的所有子节点(SELECT * FROM tb_tree t WHERE t.pid=?)
		List<TreeNode> childTreeNodes = treeNodeDao.queryTreeNode(cid);
		// 遍历子节点
		for (TreeNode child : childTreeNodes) {
			TreeNode n = recursiveTree(child.getCid()); // 递归
			node.getNodes().add(n);
		}

		return node;
	}
	
	
	@Test
	public void testTreeUtil() {
		List<TreeNode> all_nodes = treeNodeDao.queryAllTreeNodes();
		TreeUtil tu = new TreeUtil(all_nodes);
		TreeNode gen_tree_node = tu.generateTreeNode(1);
		System.out.println(JSONObject.toJSONString(gen_tree_node));
		
		
		
	}
	
	
	
	
}
