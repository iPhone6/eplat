package com.cn.eplat.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.eplat.model.BindHistory;

public interface IBindHistoryDao {
	// 添加一条绑定历史信息数据
	public int insertBindHistory(BindHistory bh);
	// 根据id查询一条绑定历史信息
	public BindHistory queryBindHistoryById(int id);
	// 根据id修改一条绑定历史信息
	public int updateBindHistoryById(BindHistory bh);
	// 根据除id以外的条件查询绑定历史信息
	public List<BindHistory> queryBindHistoryByCriterion(BindHistory bh);
	// 根据除id以外的条件查询绑定历史信息，并按操作时间排序
	public List<BindHistory> queryBindHistoryByCriterionOrderByTime(BindHistory bh);
	// TODO:（这个在传参时有问题，待查mybatis <if> 标签的问题）根据除id以外的条件查询有效时间内的绑定历史信息，并按操作时间排序
	public List<BindHistory> queryBindHistoryByCriterionValidOrderByTime(@Param("bh") BindHistory bh, @Param("start") Date start, @Param("range") int range);
	// 根据除id以外的条件查询有效时间内的绑定历史信息，并按操作时间排序（版本2）
	public List<BindHistory> queryBindHistoryByCriterionValidOrderByTimeV2(@Param("bh") BindHistory bh, @Param("start") Date start, @Param("range") int range);
	// 测试注解方式多参数
	public List<BindHistory> testQueryMultiParams(@Param("bh") BindHistory bh, @Param("did") int did);
	// 根据用户id查询该用户最后一条绑定历史记录信息
	public BindHistory queryLastBindHistoryByEpUid(int ep_uid);
	// 根据用户id查询该用户有效时间内的最后一条绑定历史记录信息
	public BindHistory queryLastBindHistoryValidByEpUid(@Param("uid") int ep_uid, @Param("start") Date start, @Param("range") int range);
}
