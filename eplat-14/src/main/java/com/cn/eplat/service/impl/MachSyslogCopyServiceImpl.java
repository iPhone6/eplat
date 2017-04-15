package com.cn.eplat.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IMachCheckInOutDao;
import com.cn.eplat.dao.IMachSyslogCopyDao;
import com.cn.eplat.datasource.DataSourceContextHolder;
import com.cn.eplat.datasource.DataSourceType;
import com.cn.eplat.model.MachSyslogCopy;
import com.cn.eplat.service.IMachSyslogCopyService;

@Service("machSyslogCopyService")
@Transactional
public class MachSyslogCopyServiceImpl implements IMachSyslogCopyService {
	
	@Resource
	private IMachCheckInOutDao machCheckInOutDao;
	@Resource
	private IMachSyslogCopyDao machSyslogCopyDao;
	
	@Override
	public List<MachSyslogCopy> queryMachSyslogCopyGtGivenId(Integer id) {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		return machSyslogCopyDao.queryMachSyslogCopyGtGivenId(id);
	}

	@Override
	public List<MachSyslogCopy> queryMachSyslogCopyAfterGivenTime(Date time) {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		return machSyslogCopyDao.queryMachSyslogCopyAfterGivenTime(time);
	}
	
	@Override
	public List<MachSyslogCopy> queryMachSyslogCopyByGivenTimeRange(Date start, Date end) {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		return machSyslogCopyDao.queryMachSyslogCopyByGivenTimeRange(start, end);
	}

	@Override
	public List<MachSyslogCopy> queryAllMachSyslogCopys() {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		return machSyslogCopyDao.queryAllMachSyslogCopys();
	}
	
	@Override
	public List<MachSyslogCopy> queryMachSyslogCopyWithMaxSyslogId() {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		return machSyslogCopyDao.queryMachSyslogCopyWithMaxSyslogId();
	}

	@Override
	public Long queryMaxSyslogId() {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		return machSyslogCopyDao.queryMaxSyslogId();
	}

	@Override
	public Date queryLastestLogTime() {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		return machSyslogCopyDao.queryLastestLogTime();
	}

	@Override
	public int batchInsertMachSyslogCopys(List<MachSyslogCopy> logs) {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		return machSyslogCopyDao.batchInsertMachSyslogCopys(logs);
	}

	@Override
	public List<MachSyslogCopy> queryProcessedSyslogsWithNullProcResult() {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		return machSyslogCopyDao.queryProcessedSyslogsWithNullProcResult();
	}

	@Override
	public int batchUpdateMachSyslogCopyById(List<MachSyslogCopy> logs) {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		return machSyslogCopyDao.batchUpdateMachSyslogCopyById(logs);
	}

	@Override
	public int batchUpdateMachSyslogCopyProcResultById(List<MachSyslogCopy> logs, String proc_result) {
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		return machSyslogCopyDao.batchUpdateMachSyslogCopyProcResultById(logs, proc_result);
	}
	
}
