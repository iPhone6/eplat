package com.test.maintest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.test.tree.node;

/**
 * @author lei
 *
 */
public class IteratorNodeTool {
	Map<String, List> pathMap = new HashMap();// 记录所有从根节点到叶子结点的路径

	private void print(List lst)// 打印出路径
	{
		Iterator it = lst.iterator();
		while (it.hasNext()) {
			node n = (node) it.next();
			System.out.print(n.getText() + "-");
		}
		System.out.println();
	}

	public void iteratorNode(node n, Stack<node> pathstack) {
		pathstack.push(n);// 入栈
		List childlist = n.getChildList();
		if (childlist == null)// 没有孩子 说明是叶子结点
		{
			List lst = new ArrayList();
			Iterator stackIt = pathstack.iterator();
			while (stackIt.hasNext()) {
				lst.add(stackIt.next());
			}
			print(lst);// 打印路径
			pathMap.put(n.getText(), lst);// 保存路径信息
			return;
		} else {
			Iterator it = childlist.iterator();
			while (it.hasNext()) {
				node child = (node) it.next();
				iteratorNode(child, pathstack);// 深度优先 进入递归
				pathstack.pop();// 回溯时候出栈
			}

		}

	}

	public static void main(String[] args) {
		Stack<node> pathstack = new Stack();
		node n = node.getInitNode();
		IteratorNodeTool tool = new IteratorNodeTool();
		tool.iteratorNode(n, pathstack);
		System.out.println(tool.pathMap);
		System.out.println("Over");
	}

}
