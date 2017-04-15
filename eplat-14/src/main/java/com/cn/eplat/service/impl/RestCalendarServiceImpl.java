package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IRestCalendarDao;
import com.cn.eplat.service.IRestCalendarService;

@Service("restCalendarService")
@Transactional
public class RestCalendarServiceImpl implements IRestCalendarService {
	
	@Resource
	private IRestCalendarDao restCalendarDao;
	
	@Override
	public int insertRestCalendar(List<HashMap<String, Object>> datas) {
		return restCalendarDao.insertRestCalendar(datas);
	}

	@Override
	public List<HashMap<String, Object>> getDatesBetweenStartAndEnd(Date startDate, Date endDate) {
		return restCalendarDao.getDatesBetweenStartAndEnd(startDate, endDate);
	}

	@Override
	public int deleteRecordsByDates(List<Date> dates) {
		return restCalendarDao.deleteRecordsByDates(dates);
	}

	@Override
	public List<Date> getDatesBetweenStartAndEndByType(Date startDate,Date endDate, String type) {
		return restCalendarDao.getDatesBetweenStartAndEndByType(startDate, endDate, type);
	}

}
