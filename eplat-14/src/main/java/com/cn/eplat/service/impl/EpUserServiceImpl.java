package com.cn.eplat.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.eplat.dao.IEmUserDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EmUser;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.service.IEpUserService;
import com.mysql.jdbc.StringUtils;

@Service("epUserService")
@Transactional
public class EpUserServiceImpl implements IEpUserService {
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEmUserDao emUserDao;

	@Override
	public EpUser getEpUserByCode(String code) {
		return epUserDao.queryEpUserByCode(code);
	}

	@Override
	public int modifyEpUserById(EpUser epu) {
		return epUserDao.updateEpUserById(epu);
	}
	
	@Override
	public int modifyEpUserByIdIncludingNull(EpUser epu) {
		return epUserDao.updateEpUserByIdIncludingNull(epu);
	}
	
	@Override
	public List<EmUser> findEmUsersByKey(String key) {
		
		if(StringUtils.isNullOrEmpty(key)) {
			return null;
		}
		
		List<EpUser> epus = epUserDao.queryEpUsersByKey(key);
		if(epus != null) {
			if(epus.size() > 0) {
				int num = epus.size();
				int[] emu_ids = new int[num];
//				for(EpUser epu:epus) {
//					Arrays.fill(emu_ids, epu.getEm_uid());
//				}
				for(int i=0; i<num; i++) {
					emu_ids[i] = epus.get(i).getEm_uid();
				}
				
				return emUserDao.queryEmUsersByIds(emu_ids);
			}
		}
		
		return null;
	}

	@Override
	public int addEpUser(EpUser epu) {
		return epUserDao.addEpUser(epu);
	}

	@Override
	public boolean isMobilePhoneDuplicate(EpUser epu) {
		return epUserDao.queryEpUserByMobilePhone(epu) > 0;
	}

	@Override
	public List<EpUser> getEpUserByCriteria(EpUser epu) {
		return epUserDao.queryEpUserByCriteria(epu);
	}

	@Override
	public EpUser getEpUserById(int id) {
		return epUserDao.queryEpUserById(id);
	}
	
	@Override
	public EpUser getEpUserByMobileNum(String mobilePhone) {
		return epUserDao.queryEpUserByMobileNum(mobilePhone);
	}

	@Override
	public EpUser getEpUserByEmail(String email) {
		return epUserDao.queryEpUserByEmail(email);
	}

	@Override
	public EpUser getEpUserByWorkNum(String workNum) {
		return epUserDao.queryEpUserByWorkNum(workNum);
	}

	@Override
	public EpUser getSimpleEpUserByWorkNum(String workNum) {
		return epUserDao.querySimpleEpUserByWorkNum(workNum);
	}

	@Override
	public List<EpUser> getOneEpUserByNameAndSpecialCriterion(String name) {
		return epUserDao.queryOneEpUserByNameAndSpecialCriterion(name);
	}

	@Override
	public int updateOriginPwdById(Integer id, String pwd, String md5Pwd) {
		return epUserDao.updateOriginPwdById(id, pwd, md5Pwd);
	}
	
	@Override
	public int updatePwdById(Integer id, String pwd, String md5Pwd) {
		return epUserDao.updatePwdById(id, pwd, md5Pwd);
	}

	@Override
	public int updateVercodeById(Integer id, String ver_code,
			Date vercode_time) {
		return epUserDao.updateVercodeById(id, ver_code, vercode_time);
	}

	@Override
	public EpUser getVerCodeEpUserByEmail(String email) {
		return epUserDao.queryVerCodeEpUserByEmail(email);
	}

	@Override
	public int updateResetPwdByEmail(EpUser user) {
		return epUserDao.updateResetPwdByEmail(user);
	}

	@Override
	public List<Map<String, Object>> fuzzyFindEpUserAndEmUserByName(String name_key) {
		return epUserDao.fuzzyQueryEpUserAndEmUserByName(name_key);
	}

	@Override
	public int batchModifyEpUsersUnderSomeConditions(List<EpUser> epus) {
		if(epus == null) {
			return -1;
		}
		
		List<EpUser> epus_valid = new ArrayList<EpUser>();
		
		for(EpUser epu:epus) {	// 筛选出有效的用户信息
			if(epu.getId() != null && epu.getId() > 0 && epu.getMach_userid() != null) {
				epus_valid.add(epu);
			}
		}
		
		Map<String, List<EpUser>> data_map = new HashMap<String, List<EpUser>>();
		
		data_map.put("data_map", epus_valid);
		
		return epUserDao.batchUpdateEpUsersUnderSomeConditions(data_map);
	}

}
