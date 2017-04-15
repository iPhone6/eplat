package com.test.tree2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 树的分支节点
 *
 * @author jzj
 * @data 2009-12-17
 */
public class TreeBranchNode extends TreeNode {

	// 存储子节点
	List subNodesList = new ArrayList();

	public TreeBranchNode(int nodeId, String nodeName) {
		this.setNodeId(nodeId);
		this.setNodeName(nodeName);
	}

	// 添加子节点
	public void addSubNode(TreeNode menuComponent) {
		// 设置父节点
		menuComponent.setPMenuComponent(this);

		// 设置节点的深度
		menuComponent.setDepth(this.getDepth() + 1);
		subNodesList.add(menuComponent);
	}

	// 删除一个子节点
	public void removeSubNode(TreeNode menuComponent) {
		subNodesList.remove(menuComponent);
	}

	// 获取子节点
	public List getSubNodes() {
		return subNodesList;
	}

	// 打印节点信息，以树的形式展示，所以它包括了所有子节点信息
	public void print() {
		System.out.println(this.getNodeInfo());
	}

	// 打印节点本身信息，不递归打印子节点信息
	public String toString() {
		return getSefNodeInfo().toString();
	}

	// 递归打印节点信息实现
	protected StringBuffer getNodeInfo() {

		StringBuffer sb = getSefNodeInfo();
		sb.append(System.getProperty("line.separator"));
		// 如果有子节点
		for (Iterator iter = subNodesList.iterator(); iter.hasNext();) {
			TreeNode node = (TreeNode) iter.next();
			// 递归打印子节点信息
			sb.append(node.getNodeInfo());

			if (iter.hasNext()) {
				sb.append(System.getProperty("line.separator"));
			}

		}
		return sb;
	}

	// 节点本身信息，不含子节点信息
	private StringBuffer getSefNodeInfo() {
		StringBuffer sb = new StringBuffer();

		// 打印缩进
		for (int i = 0; i < this.getDepth(); i++) {
			sb.append(' ');
		}
		sb.append("+--");

		sb.append("[nodeId=");
		sb.append(this.getNodeId());
		sb.append(" nodeName=");

		sb.append(this.getNodeName());
		sb.append(']');
		return sb;
	}

	// 为外界提供遍历组合结构的迭代器
	public Iterator createDepthOrderIterator() {
		return new TreeOutOrder.DepthOrderIterator(this);
	}

	// 为外界提供遍历组合结构的迭代器
	public Iterator createLayerOrderIterator() {
		return new TreeOutOrder.LevelOrderIterator(this);
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

		// 如果找到，则停止后续搜索，并把查找到的节点返回给上层调用者
		if (treeNode.getNodeId() == treeId) {// 1、先与父节点比对
			return treeNode;
		}

		TreeNode tmp = null;

		// 如果为分支节点，则遍历子节点
		if (treeNode instanceof TreeBranchNode) {

			for (int i = 0; i < treeNode.getSubNodes().size(); i++) {// 2、再与子节点比对
				tmp = getNode((TreeNode) treeNode.getSubNodes().get(i), treeId);
				if (tmp != null) {// 如果查找到，则返回上层调用者
					return tmp;
				}
			}
		}

		// 如果没有找到，返回上层调用者
		return null;
	}
}
