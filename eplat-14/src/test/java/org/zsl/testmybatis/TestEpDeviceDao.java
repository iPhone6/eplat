package org.zsl.testmybatis;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IEpAttenDao;
import com.cn.eplat.dao.IEpDeviceDao;
import com.cn.eplat.dao.IEpUserDao;
import com.cn.eplat.model.EpAtten;
import com.cn.eplat.model.EpDevice;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestEpDeviceDao {
	private static Logger logger = Logger.getLogger(TestEpDeviceDao.class);
	
	@Resource
	private IEpUserDao epUserDao;
	@Resource
	private IEpAttenDao epAttenDao;
	@Resource
	private IEpDeviceDao epDeviceDao;
	
	
	@Test
	public void testQueryDeviceBoundCount() {
		EpDevice epd = new EpDevice();
		epd.setEp_uid(12);
		int ret = epDeviceDao.queryDeviceBoundCount(epd);
		if(ret > 0) {
			logger.info("根据用户id查询已绑定的设备个数为：" + ret);
		} else {
			logger.error("根据用户id查询已绑定的设备个数为0");
		}
	}
	
	@Test
	public void testQueryEpDeviceById() {
		int id = 2;
		EpDevice epd = epDeviceDao.queryEpDeviceById(id);
		if(epd != null) {
			logger.info("根据id查询设备信息成功，epd = " + epd);
		} else {
			logger.error("根据id查询设备信息失败");
		}
	}
	
	@Test
	public void testInsertEpDevice() {
		EpDevice epd = new EpDevice();
		epd.setName("华为 P9");
		epd.setImei("12312909090");
		epd.setBind_start_time(new Date());
		epd.setBind_count(2);
		epd.setBind_status(true);
		epd.setBind_valid_time(300);
		
		System.out.println("新增设备信息前，设备信息：" + epd);
		int ret = epDeviceDao.insertEpDevice(epd);
		if(ret > 0) {
			logger.info("新增一条设备信息成功 + ret = " + ret);
			System.out.println("新增之后，设备信息详情：" + epd);
		} else {
			logger.error("新增一条设备信息失败 + ret = " + ret);
		}
	}
	
	@Test
	public void testUpdateEpDeviceById() {
		EpDevice epd = new EpDevice();
		epd.setId(6);
//		epd.setName("华为 P9 Plus");
//		epd.setImei("12312909090");
//		epd.setBind_start_time(new Date());
		epd.setBind_start_time(new Date());
//		epd.setBind_count(4);
//		epd.setBind_status(false);
//		epd.setBind_valid_time(322);
		
		int ret = epDeviceDao.updateEpDeviceById(epd);
		if(ret > 0) {
			logger.info("根据id修改设备信息成功 + ret = " + ret);
		} else {
			logger.error("根据id修改设备信息失败 + ret = " + ret);
		}
	}
	
	@Test
	public void testUpdateEpDeviceByIdIncludingNull() {
		EpDevice epd = new EpDevice();
		epd.setId(9);
//		epd.setName("华为 P9 Plus");
//		epd.setImei("12312909090");
//		epd.setBind_start_time(new Date());
		epd.setBind_start_time(new Date());
//		epd.setBind_count(4);
//		epd.setBind_status(false);
//		epd.setBind_valid_time(322);
		
		int ret = epDeviceDao.updateEpDeviceByIdIncludingNull(epd);
		if(ret > 0) {
			logger.info("根据id修改设备信息成功 + ret = " + ret);
		} else {
			logger.error("根据id修改设备信息失败 + ret = " + ret);
		}
	}
	
	@Test
	public void testQueryEpDeviceByCriterion() {
		EpDevice epd = new EpDevice();
//		epd.setImei("8833399900");
//		epd.setBind_status(false);
//		epd.setBind_count(-99);
//		epd.setBind_valid_time(-10);
//		epd.setEp_uid(-11);
		List<EpDevice> epds = epDeviceDao.queryEpDeviceByCriterion(epd);
		
		if(epds == null) {
			logger.error("根据条件查询设备信息失败");
		} else {
			int count = epds.size();
			logger.info("根据条件查询设备信息成功 + epds.size() = " + count);
			for(EpDevice ed:epds) {
				System.out.println(ed);
			}
		}
		
	}
	
}
