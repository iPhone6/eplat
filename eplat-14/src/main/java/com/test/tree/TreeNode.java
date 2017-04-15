package com.test.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {
	private static final long serialVersionUID = 7280223321963878154L;
	
	private Integer cid;
	private String cname;
	private Integer pid;
	private List<TreeNode> nodes = new ArrayList<TreeNode>();
	
	public TreeNode() {
	}
	
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public List<TreeNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<TreeNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		return "TreeNode [cid=" + cid + ", cname=" + cname + ", pid=" + pid + ", nodes=" + nodes + "]";
	}
	
}
