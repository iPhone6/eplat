package com.test.tree2;

import java.util.Iterator;

/**
 * 测试
 * 
 * @author jzj
 * @data 2009-12-17
 */
public class TestTreeOrder {
	public static void main(String[] args) {
		TreeNode root = new TreeBranchNode(1, "one");
		TreeNode n2 = new TreeBranchNode(2, "two");
		TreeNode n3 = new TreeBranchNode(3, "three");
		TreeNode n4 = new TreeBranchNode(4, "four");
		TreeNode n5 = new TreeBranchNode(5, "five");
		TreeNode n6 = new TreeLeafNode(6, "six");
		TreeNode n7 = new TreeLeafNode(7, "seven");
		TreeNode n8 = new TreeBranchNode(8, "eight");
		TreeNode n9 = new TreeLeafNode(9, "nine");
		TreeNode n10 = new TreeBranchNode(10, "ten");
		TreeNode n11 = new TreeLeafNode(11, "eleven");
		TreeNode n12 = new TreeLeafNode(12, "twelve");
		TreeNode n13 = new TreeLeafNode(13, "thirteen");
		TreeNode n14 = new TreeLeafNode(14, "fourteen");
		TreeNode n15 = new TreeLeafNode(15, "fifteen");
		TreeNode n16 = new TreeLeafNode(16, "sixteen");
		TreeNode n17 = new TreeLeafNode(17, "seventeen");
		TreeNode n18 = new TreeLeafNode(18, "eighteen");
		root.addSubNode(n2);
		root.addSubNode(n3);
		root.addSubNode(n4);
		n2.addSubNode(n5);
		n3.addSubNode(n6);
		n3.addSubNode(n7);
		n3.addSubNode(n8);
		n3.addSubNode(n9);
		n4.addSubNode(n10);
		n5.addSubNode(n11);
		n5.addSubNode(n12);
		n8.addSubNode(n13);
		n8.addSubNode(n14);
		n8.addSubNode(n15);
		n10.addSubNode(n16);
		n10.addSubNode(n17);
		n10.addSubNode(n18);

		root.print();

		Iterator itr = root.createDepthOrderIterator();
		System.out.print("深度（先根）遍历 - ");
		while (itr.hasNext()) {
			System.out.print(((TreeNode) itr.next()).getNodeId() + " ");
		}
		System.out.println();
		itr = root.createLayerOrderIterator();
		System.out.print("广度（层次）遍历 - ");
		while (itr.hasNext()) {
			System.out.print(((TreeNode) itr.next()).getNodeId() + " ");
		}

		System.out.println();
		System.out.print("先序遍历 - ");
		TreeInOrder.preOrder(root);
		System.out.println();
		System.out.print("后序遍历 - ");
		TreeInOrder.postOrder(root);
		System.out.println();
		for (int i = 1; i <= 18; i++) {
			System.out.print(root.getTreeNode(i).getNodeId() + " ");
		}
		/*
		 * print:
		 * 
		 *+--[nodeId=1 nodeName=one]
		 * +--[nodeId=2 nodeName=two]
		 *  +--[nodeId=5 nodeName=five]
		 *   ---[nodeId=11 nodeName=eleven]
		 *   ---[nodeId=12 nodeName=twelve]
		 * +--[nodeId=3 nodeName=three]
		 *  ---[nodeId=6 nodeName=six]
		 *  ---[nodeId=7 nodeName=seven]
		 *  +--[nodeId=8 nodeName=eight]
		 *   ---[nodeId=13 nodeName=thirteen]
		 *   ---[nodeId=14 nodeName=fourteen]
		 *   ---[nodeId=15 nodeName=fifteen]
		 *  ---[nodeId=9 nodeName=nine]
		 * +--[nodeId=4 nodeName=four]
		 *  +--[nodeId=10 nodeName=ten]
		 *   ---[nodeId=16 nodeName=sixteen]
		 *   ---[nodeId=17 nodeName=seventeen]
		 *   ---[nodeId=18 nodeName=eighteen]
		 *   
		 *   深度（先根）遍历 - 1 2 5 11 12 3 6 7 8 13 14 15 9 4 10 16 17 18 
		 *   广度（层次）遍历 - 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 
		 *   先序遍历 - 1 2 5 11 12 3 6 7 8 13 14 15 9 4 10 16 17 18 
		 *   后序遍历 - 11 12 5 2 6 7 13 14 15 8 9 3 16 17 18 10 4 1 
		 *   1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 
		 */
	}
}
