package com.test.tree2;

/**
 * 树的两种 内部 遍历方式：前序遍历、后序遍历
 * 
 * @author jzj
 * @data 2009-12-17
 */
public class TreeInOrder {

	/**
	 * 树的前序递归遍历 pre=prefix(前缀)
	 * 
	 * @param node
	 *            要遍历的节点
	 */
	public static void preOrder(TreeNode node) {
		// 如果传进来的节点不为空，则遍历，注，叶子节点的子节点为null
		if (node != null) {
			System.out.print(node.getNodeId() + " ");// 先遍历父节点

			if (node instanceof TreeBranchNode) {
				for (int i = 0; i < node.getSubNodes().size(); i++) {
					preOrder((TreeNode) node.getSubNodes().get(i));// 再遍历子节点
				}
			}

		}
	}

	/**
	 * 树的后序递归遍历 post=postfix(后缀)
	 * 
	 * @param node
	 *            要遍历的节点
	 */
	public static void postOrder(TreeNode node) {
		// 如果传进来的节点不为空，则遍历
		if (node != null) {
			// 如果为分支节点，则遍历子节点
			if (node instanceof TreeBranchNode) {

				for (int i = 0; i < node.getSubNodes().size(); i++) {
					postOrder((TreeNode) node.getSubNodes().get(i));// 先遍历子节点
				}
			}
			System.out.print(node.getNodeId() + " ");// 后遍历父节点
		}
	}
}
