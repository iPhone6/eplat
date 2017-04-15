package com.test.tree2;

import java.util.Iterator;
import java.util.List;

/**
 * 树节点抽象接口
 * 
 * @author jzj
 * @data 2009-12-17
 */
public abstract class TreeNode implements Comparable {

	// 父节点
	private TreeNode pNode;

	// 数据域，节点编号，不能修改
	private int nodeId;

	// 数据域，节点名字，能修改
	private String nodeName;

	// 节点深度，根默认为0
	private int depth;

	public TreeNode getPMenuComponent() {
		return pNode;
	}

	public void setPMenuComponent(TreeNode menuComponent) {
		pNode = menuComponent;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	// 添加子节点 默认不支持，叶子节点不支持此功能
	public void addSubNode(TreeNode menuComponent) {
		throw new UnsupportedOperationException();
	}

	// 删除子节点 默认不支持，叶子节点不支持此功能
	public void removeSubNode(TreeNode menuComponent) {
		throw new UnsupportedOperationException();
	}

	// 修改节点信息
	public void modiNodeInfo(String nodeName) {
		this.setNodeName(nodeName);
	}

	// 获取子节点 默认不支持，叶子节点不支持此功能
	public List getSubNodes() {
		throw new UnsupportedOperationException();
	}

	// 打印节点信息
	public void print() {
		throw new UnsupportedOperationException();
	}

	// 获取节点信息
	protected abstract StringBuffer getNodeInfo();

	// 提供深度迭代器 默认不支持，叶子节点不支持此功能
	public Iterator createDepthOrderIterator() {
		throw new UnsupportedOperationException();
	}

	// 提供广度优先迭代器 默认不支持，叶子节点不支持此功能
	public Iterator createLayerOrderIterator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 根据树节点id，在当然节点与子节点中搜索指定的节点
	 * 
	 * @param treeId
	 * @return TreeNode
	 */
	public TreeNode getTreeNode(int treeId) {
		return getNode(this, treeId);
	}

	/**
	 * 使用树的先序遍历递归方式查找指定的节点
	 * 
	 * @param treeNode
	 *            查找的起始节点
	 * @param treeId
	 *            节点编号
	 * @return
	 */
	protected TreeNode getNode(TreeNode treeNode, int treeId) {
		throw new UnsupportedOperationException();
	}

	public int compareTo(Object o) {

		TreeNode temp = (TreeNode) o;

		return this.getNodeId() > temp.getNodeId() ? 1 : (this.getNodeId() < temp.getNodeId() ? -1 : 0);
	}

	public boolean equals(Object menuComponent) {

		if (!(menuComponent instanceof TreeNode)) {
			return false;
		}
		TreeNode menu = (TreeNode) menuComponent;

		// 如果两个节点的nodeID相应则认为是同一节点
		return this.getNodeId() == menu.getNodeId();
	}
}
