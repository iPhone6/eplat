package org.zsl.testmybatis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.eplat.dao.IPushLogDao;
import com.cn.eplat.model.PushLog;

@RunWith(SpringJUnit4ClassRunner.class)		//表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestPushLogDao {
	
	@Resource
	private IPushLogDao pushLogDao;
	
	@Test
	public void testInsert2PushLog(){
		List<PushLog> logs = new ArrayList<PushLog>();
		Date date = new Date();
		for (int i = 0; i < 100; i++) {
			PushLog log = new PushLog();
			log.setName("linmin"+i);
			log.setWork_no(String.valueOf(i*30+new Random().nextInt(500)));
			log.setResult( i%3==0?true:false);
			log.setTime( new Date(date.getTime()+new Random().nextInt(10000)*5000000));
			logs.add(log);
		}
		pushLogDao.insert2PushLog(logs);
	}
}
