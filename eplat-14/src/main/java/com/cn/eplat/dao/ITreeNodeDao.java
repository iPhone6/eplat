package com.cn.eplat.dao;

import java.util.List;

import com.test.tree.TreeNode;

public interface ITreeNodeDao {
	// 根据cid获取节点对象(SELECT * FROM tb_tree t WHERE t.cid=?)
	public TreeNode getreeNode(Integer cid);
	// 查询cid下的所有子节点(SELECT * FROM tb_tree t WHERE t.pid=?)
	public List<TreeNode> queryTreeNode(Integer cid);
	
	// 查询出所有树节点
	public List<TreeNode> queryAllTreeNodes();
}
