package com.test.tree2;

/**
 * 树的叶子节点
 *
 * @author jzj
 * @data 2009-12-17
 *
 */
public class TreeLeafNode extends TreeNode {
	public TreeLeafNode(int nodeId, String nodeName) {
		this.setNodeId(nodeId);
		this.setNodeName(nodeName);
	}

	// 获取叶子节点信息
	protected StringBuffer getNodeInfo() {
		StringBuffer sb = new StringBuffer();

		// 打印缩进
		for (int i = 0; i < this.getDepth(); i++) {
			sb.append(' ');
		}
		sb.append("---");

		sb.append("[nodeId=");
		sb.append(this.getNodeId());
		sb.append(" nodeName=");

		sb.append(this.getNodeName());
		sb.append(']');

		return sb;
	}

	public String toString() {
		return getNodeInfo().toString();
	}
}
