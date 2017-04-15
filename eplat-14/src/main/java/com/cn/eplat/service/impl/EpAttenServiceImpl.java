package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEpAttenDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpAtten;
import com.cn.eplat.model.EpAttenExport;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.service.IEpAttenService;
import com.cn.eplat.utils.db2excel.DbToExcelUtil;

@Service("epAttenService")
@Transactional
public class EpAttenServiceImpl implements IEpAttenService {
	
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpAttenDao epAttenDao;
	
	@Override
	public int addEpAtten(EpAtten epa) {
		return epAttenDao.insertEpAtten(epa);
	}

	@Override
	public int getLastPunchCardCountByEpUid(EpAtten epa) {
		Integer ret = epAttenDao.queryLastPunchCardCountToday(epa);
		
		if(ret == null) {	// 如果查询当天没有打卡记录，或打卡记录的次数字段（count）都为null时，则返回0
			return 0;
		}
		
		return ret;
	}

	@Override
	public List<EpAtten> getEpAttenByUidAndDayRange(int uid, int day_range) {
		return epAttenDao.queryEpAttenByUidAndDayRange(uid, day_range);
	}

	@Override
	public EpAtten getEpAttenById(Long id) {
		return epAttenDao.queryEpAttenById(id);
	}

	@Override
	public List<EpAtten> getEpAttenByStartDateAndEndDate(Date start_date, Date end_date) {
		if(start_date == null || end_date == null) {	// 如果开始日期或结束日期为null，则直接返回null
			return null;
		}
		
		if(start_date.after(end_date)) {	// 如果开始日期晚于结束日期，则自动交换开始日期和结束日期
			Date tmp_date = start_date;
			end_date = tmp_date;
			start_date = end_date;
		}
		
		return epAttenDao.queryEpAttenByStartDateAndEndDate(start_date, end_date);
	}

	@Override
	public List<EpAtten> getEpAttenByEpUidAndStartDateAndEndDate(int ep_uid, Date start_date, Date end_date) {
		if(start_date == null || end_date == null) {	// 如果开始日期或结束日期为null，则直接返回null
			return null;
		}
		
		if(start_date.after(end_date)) {	// 如果开始日期晚于结束日期，则自动交换开始日期和结束日期
			Date tmp_date = start_date;
			end_date = tmp_date;
			start_date = end_date;
		}
		
		return epAttenDao.queryEpAttenByEpUidAndStartDateAndEndDate(ep_uid, start_date, end_date);
	}
	
	@Override
	public List<EpAtten> getValidEpAttenByEpUidAndStartDateAndEndDate(int ep_uid, Date start_date, Date end_date) {
		if(start_date == null || end_date == null) {	// 如果开始日期或结束日期为null，则直接返回null
			return null;
		}
		
		if(start_date.after(end_date)) {	// 如果开始日期晚于结束日期，则自动交换开始日期和结束日期
			Date tmp_date = start_date;
			start_date = end_date;
			end_date = tmp_date;
		}
		
		return epAttenDao.queryValidEpAttenByEpUidAndStartDateAndEndDate(ep_uid, start_date, end_date);
	}

	@Override
	public String getAllEpAttenExportDatas(List<String> emails,Date startDdate,Date endDate) {
		List<EpAttenExport> attenDatas = epAttenDao.queryAllEpAttenExportDatas(emails,startDdate,endDate);
		return DbToExcelUtil.exportAttendDatasToExcel(attenDatas);
	}

	@Override
	public int batchAddEpAttens(List<EpAtten> epas) {
		return epAttenDao.batchInsertEpAttens(epas);
	}

	@Override
	public List<HashMap<String, Object>> getFirstAndLastPunchTimeValidByDatesAndEpUidsBeforeToday(List<Date> dates, List<EpUser> epus) {
		return epAttenDao.queryFirstAndLastPunchTimeValidByDatesAndEpUidsBeforeToday(dates, epus);
	}

	@Override
	public HashMap<String, Object> getFirstAndLastPunchTimeValid() {
		return epAttenDao.queryFirstAndLastPunchTimeValid();
	}
	
}
