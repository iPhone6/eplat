package com.cn.eplat.service;

import java.util.Date;
import java.util.List;

import com.cn.eplat.model.BindHistory;

public interface IBindHistoryService {
	// 新增一条绑定历史信息数据
	public int addBindHistory(BindHistory bh);
	// 根据id获取一条绑定历史信息
	public BindHistory getBindHistoryById(int id);
	// 根据id修改一条绑定历史信息
	public int modifyBindHistoryById(BindHistory bh);
	// 根据除id以外的条件获取绑定历史信息
	public List<BindHistory> getBindHistoryByCriterion(BindHistory bh);
	// 根据除id以外的条件获取绑定历史信息，并按操作时间从早到晚排序
	public List<BindHistory> getBindHistoryByCriterionOrderByTime(BindHistory bh);
	// 根据除id以外的条件获取有效时间内的绑定历史信息，并按操作时间从早到晚排序
	public List<BindHistory> getBindHistoryByCriterionValidOrderByTime(BindHistory bh, Date start, int range);
	// 根据用户id获取该用户有效时间内的最后一条绑定历史记录信息
	public BindHistory getLastBindHistoryValidByEpUid(int ep_uid, Date start, int range);
}
