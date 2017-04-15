package com.cn.eplat.timedtask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.eplat.controller.EpAttenController;
import com.cn.eplat.datasource.DataSourceContextHolder;
import com.cn.eplat.datasource.DataSourceType;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.model.MachCheckInOut;
import com.cn.eplat.model.MachSyslogCopy;
import com.cn.eplat.model.MachUserInfo;
import com.cn.eplat.service.IEpAttenService;
import com.cn.eplat.service.IEpUserService;
import com.cn.eplat.service.IMachCheckInOutService;
import com.cn.eplat.service.IMachMachinesService;
import com.cn.eplat.service.IMachSyslogCopyService;
import com.cn.eplat.service.IMachUserInfoService;
import com.cn.eplat.utils.DateUtil;
import com.easemob.server.comm.body.PunchDatasBody;
import com.easemob.server.comm.constant.HTTPMethod;
import com.easemob.server.comm.invoker.JerseyRestAPIInvoker;
import com.easemob.server.comm.wrapper.HeaderWrapper;
import com.easemob.server.comm.wrapper.ResponseWrapper;

@Component
public class PushPunchCardDatas {
	
	private static Logger logger = Logger.getLogger(PushPunchCardDatas.class);
	
	@Resource
	private IEpUserService epUserService;
	@Resource
	private IEpAttenService epAttenService;
	@Resource
	private IMachCheckInOutService machCheckInOutService;
	@Resource
	private IMachMachinesService machMachinesService;
	@Resource
	private IMachSyslogCopyService machSyslogCopyService;
	@Resource
	private IMachUserInfoService machUserInfoService;
	
	
	private static JerseyRestAPIInvoker rest_invoker = new JerseyRestAPIInvoker();
	private static String push_mach_data_url = "https://hr.e-lead.cn:8443/eplat-pro/epAttenController.do?pushMachPunchDataCHECKINOUT";	// 生产环境配置（https）
//	private static String push_mach_data_url = "https://192.168.1.8:8443/eplat-08/epAttenController.do?pushMachPunchDataCHECKINOUT";
//	private static String push_mach_data_url = "https://192.168.1.102:9443/eplat-08/epAttenController.do?pushMachPunchDataCHECKINOUT";	// 本地测试环境配置（https）
//	private static String push_mach_data_url = "https://localhost:9443/eplat-08/epAttenController.do?pushMachPunchDataCHECKINOUT";
//	private static String push_mach_data_url = "http://192.168.1.8:8080/eplat-08/epAttenController.do?pushMachPunchDataCHECKINOUT";		// 本地测试环境配置（http）
	
	private static int push_times = 0;	// 推送次数
	
	private static int tolerance_days = 0;	// 允许的打卡时间误差天数
	private static int toerance_hours = 0;	// 允许的打卡时间误差小时数
	private static int toerance_minutes = 30;	// 允许的打卡时间误差分钟数
	private static int toerance_seconds = 0;	// 允许的打卡时间误差秒数
	
	private static long tolerance_time_mills = (tolerance_days*24*3600 + toerance_hours*3600 + toerance_minutes*60 + toerance_seconds)*1000l;	// 允许的打卡时间误差毫秒总数（上面的所有允许的打卡时间误差之和）
	
	private static List<MachUserInfo> mach_userinfos;	// 用于存放打卡机用户信息的静态变量
	private static Map<Integer, String> mach_userinfos_map = new TreeMap<Integer, String>();
	
	public static int getPush_times() {
		return push_times;
	}
	public static void setPush_times(int push_times) {
		PushPunchCardDatas.push_times = push_times;
	}
	// 推送次数加1
	public static void addPushTimes() {
		PushPunchCardDatas.push_times++;
	}
	
	public static String getPush_mach_data_url() {
		return push_mach_data_url;
	}
	public static void setPush_mach_data_url(String push_mach_data_url) {
		PushPunchCardDatas.push_mach_data_url = push_mach_data_url;
	}
	
	public static long getTolerance_time_mills() {
		return tolerance_time_mills;
	}
//	public static void setTolerance_time_mills(long tolerance_time_mills) {
//		PushPunchCardDatas.tolerance_time_mills = tolerance_time_mills;
//	}
	
	public static List<MachUserInfo> getMach_userinfos() {
		return mach_userinfos;
	}
	public static void setMach_userinfos(List<MachUserInfo> mach_userinfos) {
		PushPunchCardDatas.mach_userinfos = mach_userinfos;
	}
	
	
	@Scheduled(cron = "0/60 * * * * ? ")	// 间隔60秒执行
	public void push() {
		
		// 监控打卡机用户信息表（Userinfo），如果用户信息条数发生了变化，则更新打卡机用户信息静态变量对象的值
		logger.info("开始监控打卡机用户信息。。。");
		long monitor_userinfo_start = System.currentTimeMillis();
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		int mui_num = machUserInfoService.queryMachUserInfoNumber();
		if(mui_num < 0) {
			logger.error("查询打卡机用户信息条数时出现异常");
		} else if(mui_num == 0) {
			logger.info("打卡机用户信息条数为0");
		} else {
			if(mach_userinfos == null || mach_userinfos.size() != mui_num) {
				DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
				List<MachUserInfo> all_muis = machUserInfoService.queryAllMachUserInfos();
				if(all_muis == null || all_muis.size() == 0) {
					logger.error("查询所有打卡机用户信息条数为0，或出现了异常");
				} else {
//					mach_userinfos = all_muis;
					setMach_userinfos(all_muis);
					mach_userinfos_map = new TreeMap<Integer, String>();
					
					for(MachUserInfo mui : all_muis) {
						Integer userid = mui.getUserid();
						String badgenumber = mui.getBadgenumber();
						if(userid != null && badgenumber != null) {
							mach_userinfos_map.put(userid, badgenumber);
						} else {
							logger.error("打卡机用户信息出现userid或工号为空/null的数据！+ mui = " + mui);
						}
					}
				}
			} else {
//				int muis_size = mach_userinfos.size();
//				if(muis_size != mui_num) {
//					List<MachUserInfo> all_muis = machUserInfoService.queryAllMachUserInfos();
//					
//				}
				logger.info("打卡机用户信息条数(共 " + mui_num + " 条)未发生变化，暂不需要更新...");
			}
		}
		
		long monitor_userinfo_end = System.currentTimeMillis();
		logger.info("监控打卡机用户信息结束，总共用时：" + DateUtil.timeMills2ReadableStr(monitor_userinfo_end - monitor_userinfo_start));
		
		
		
		// 监控打卡机系统日志表（SystemLog），如果出现新的“从设备下载记录信息”的日志信息时，开始执行查找遗漏未上传阿里云的打卡机打卡数据
		logger.info("开始监控打卡机系统日志信息。。。");
		Date proc_time = new Date();
		long monitor_syslog_time = System.currentTimeMillis();
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
		Date latest_log_time = machSyslogCopyService.queryLastestLogTime();
		
		List<MachSyslogCopy> system_logs = new ArrayList<MachSyslogCopy>();
		
		if(latest_log_time == null) {	// 如果最晚的系统日志时间为null，则说明系统目前尚未读取过打卡机系统日志，或打卡机系统日志表中无数据
			DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
			List<MachSyslogCopy> all_syslogs = machSyslogCopyService.queryAllMachSyslogCopys();
			if(all_syslogs == null || all_syslogs.size() == 0) {
				logger.error("打卡机系统日志表中无数据，或查询打卡机系统日志异常...");
			} else {
//				int batch_ret = machSyslogCopyService.batchInsertMachSyslogCopys(all_syslogs);
				system_logs = all_syslogs;
			}
		} else {
			DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
			List<MachSyslogCopy> new_syslogs = machSyslogCopyService.queryMachSyslogCopyAfterGivenTime(latest_log_time);
			if(new_syslogs == null || new_syslogs.size() == 0) {
				logger.error("打卡机系统日志表中没有新数据，或查询打卡机系统日志异常...");
			} else {
				system_logs = new_syslogs;
			}
		}
		
		boolean need_search_missed = false;	// 表示是否需要查找遗漏未被上传至阿里云服务器的打卡机打卡数据的标志
		boolean need_update_userinfo = false;	// 表示是否需要更新静态变量中的人员信息/打卡机用户信息/员工信息的标志
		
		Date tolerant_time = DateUtil.calcDatePlusGivenTimeMills(proc_time, tolerance_time_mills);
		
		if(system_logs.size() == 0) {
			logger.info("打卡机系统日志中没有更多新数据了。。。");
		} else {
			for(MachSyslogCopy msc : system_logs) {
				msc.setProc_time(proc_time);
				msc.setStatus("未处理");
				Date log_time = msc.getLog_time();
				if(log_time != null) {
					if(log_time.after(tolerant_time)) {	// 如果打卡机系统日志时间晚于允许的误差时间，则认为这条系统日志时间明显有误
						msc.setProc_result("打卡机系统日志时间有误");
					} else {
						String log_descr = msc.getLog_descr();
						if(StringUtils.isBlank(log_descr)) {
							msc.setProc_result("打卡机系统日志の描述信息为空");
						} else {
							if(log_descr.contains("下载记录")) {	// 如果打卡机系统日志描述信息中包含“下载记录”关键字（即对应“从设备下载记录数据”这个日志描述信息），则表示需要开始查找遗漏未上传阿里云的打卡机打卡数据
								if(need_search_missed == false) {
									need_search_missed = true;
								}
								msc.setStatus("已处理");
							} else if(log_descr.contains("人员维护") || log_descr.contains("上传人员信息")) {	
								if(need_update_userinfo == false) {
									need_update_userinfo = true;
								}
								msc.setStatus("已处理");
								msc.setProc_result("已更新打卡机用户信息");
							} else {	// 其余打卡机系统日志描述信息（如“从设备下载人员信息”、“管理员设置”等）则不处理
								msc.setStatus("不处理");
								msc.setProc_result("无处理结果");
							}
						}
					}
				} else {
					msc.setProc_result("打卡机系统日志时间为空/null");
				}
			}
			
			DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
			int bat_ret = machSyslogCopyService.batchInsertMachSyslogCopys(system_logs);
			
			if(bat_ret <= 0) {
				logger.error("批量插入打卡机系统日志信息到本地MySQL数据库时出现异常，或插入信息条数为0。bat_ret = " + bat_ret);
			} else {
				logger.info("批量插入打卡机系统日志信息到本地MySQL数据库成功，插入信息条数为：bat_ret = " + bat_ret);
			}
			
		}
		
		if(need_update_userinfo) {
			// 针对可能出现的打卡机上的人员信息个数不变，但部分人员的信息（如工号、userid等）发生改变时，静态变量中的人员信息未同步更新导致无法通过userid找到对应的用户工号，
			// 并最终导致上传阿里云的打卡数据出现工号为空/null的异常，从而丢失了该员工的打卡数据的情况
			// 所以，一旦打卡机系统日志中出现了与人员维护相关的动作，则应当立即更新静态变量中的人员信息
			DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
			List<MachUserInfo> all_muis = machUserInfoService.queryAllMachUserInfos();
			if(all_muis == null || all_muis.size() == 0) {
				logger.error("查询所有打卡机用户信息条数为0，或出现了异常2");
			} else {
				setMach_userinfos(all_muis);
				mach_userinfos_map = new TreeMap<Integer, String>();
				
				for(MachUserInfo mui : all_muis) {
					Integer userid = mui.getUserid();
					String badgenumber = mui.getBadgenumber();
					if(userid != null && badgenumber != null) {
						mach_userinfos_map.put(userid, badgenumber);
					} else {
						logger.error("打卡机用户信息出现userid或工号为空/null的数据2！+ mui = " + mui);
					}
				}
			}
		}
		
		if(need_search_missed) {
			DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
			List<MachCheckInOut> all_pc_datas = machCheckInOutService.getAllMachCheckInOut();
			
			if(all_pc_datas == null || all_pc_datas.size() == 0) {
				logger.error("查询打卡机全部打卡数据时出现异常，或打卡机打卡数据记录表为空");
			} else {
				DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
				int insert_all_ret = machCheckInOutService.batchInsertAllAccessCheckinoutsToMySQLMachCheckInOutCopy(all_pc_datas);
				
				if(insert_all_ret <= 0) {
					logger.error("批量插入打卡机全部打卡数据到本地MySQL数据库时出现异常，或打卡数据条数为0。");
				} else {
					logger.info("批量插入打卡机全部打卡数据到本地MySQL数据库成功，共插入（" + insert_all_ret + "）条打卡机打卡数据。");
					DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
					List<MachCheckInOut> missed_cios = machCheckInOutService.queryMissedMachCheckInOutsByCompareAccessAndMySQLDatas();
					
					DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
					int del_ret = machCheckInOutService.deleteAllAccessCheckInOutsCopyInMySQL();
					logger.info("清空本地打卡机打卡数据的临时拷贝数据成功，已删除临时数据条数为：" + del_ret);
					
					if(missed_cios == null || missed_cios.size() == 0) {
						logger.info("本次查找暂未找到遗漏的未被上传到阿里云服务器的打卡机打卡数据");
						DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
						List<MachSyslogCopy> logs_no_proc_results = machSyslogCopyService.queryProcessedSyslogsWithNullProcResult();
						
						if(logs_no_proc_results == null || logs_no_proc_results.size() == 0) {
							logger.error("处理结果为空的打卡机系统日志信息条数为0，或出现了其它异常");
							DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
							machSyslogCopyService.batchUpdateMachSyslogCopyProcResultById(logs_no_proc_results, "本次遗漏的打卡机打卡数据条数为0，或出现了其它异常");
						} else {
							logger.info("处理结果为空的打卡机系统日志信息条数为：" + logs_no_proc_results.size());
							DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
							machSyslogCopyService.batchUpdateMachSyslogCopyProcResultById(logs_no_proc_results, "本次暂无遗漏未被上传阿里云服务器的打卡机打卡数据");
						}
					} else {
						logger.info("本次查找已找到（" + missed_cios.size() + "）条遗漏的未被上传到阿里云服务器的打卡机打卡数据");
						
						String mixed_sn = "<MIXED>";
						
						List<MachCheckInOut> elim_missed_cios = eliminatePunchCardDatasWithErrorPunchTime(missed_cios, tolerant_time, mixed_sn);
						
						if(elim_missed_cios == null || elim_missed_cios.size() == 0) {
							logger.error("排除掉打卡时间明显有误的遗漏未被上传阿里云的打卡机打卡数据后，有效的打卡数据条数为0，或出现了其它异常。");
						} else {
							int push_miss_ret = pushOperation(elim_missed_cios, "push_missed", mixed_sn);
							
							if(push_miss_ret <= 0) {
								logger.error("推送遗漏的打卡机(" + mixed_sn + ")打卡数据失败");
							} else {
								logger.info("推送遗漏的打卡机(" + mixed_sn + ")打卡数据成功");
								DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
								List<MachSyslogCopy> logs_no_proc_results = machSyslogCopyService.queryProcessedSyslogsWithNullProcResult();
								
								if(logs_no_proc_results == null || logs_no_proc_results.size() == 0) {
									logger.error("处理结果为空的打卡机系统日志信息条数为0，或出现了其它异常");
									DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
									machSyslogCopyService.batchUpdateMachSyslogCopyProcResultById(logs_no_proc_results, "推送遗漏的打卡机打卡数据条数为0，或出现了其它异常");
								} else {
									logger.info("处理结果为空的打卡机系统日志信息条数为：" + logs_no_proc_results.size());
									DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
									machSyslogCopyService.batchUpdateMachSyslogCopyProcResultById(logs_no_proc_results, "已成功推送遗漏的打卡机打卡数据，推送数据条数为：" + elim_missed_cios.size());
								}
							}
						}
					}
				}
			}
		} else {
			logger.info("目前暂不需要查找遗漏未被上传阿里云的打卡机打卡数据");
		}
		
		
		// A计时点 \\
		long A_checkpoint_time = System.currentTimeMillis();
		logger.info("A计时点用时：" + DateUtil.timeMills2ReadableStr(A_checkpoint_time - monitor_syslog_time));
		
		
		logger.info("推送打卡机打卡数据...");
		long start_time = System.currentTimeMillis();	// 记录推送开始时间毫秒数
		PushPunchCardDatas.addPushTimes();
		logger.info("当前是第（" + PushPunchCardDatas.getPush_times() + "）次推送打卡机打卡数据，开始时间：" + DateUtil.formatDate(2, new Date()));
		
//		EpUser epu_ret = epUserService.getEpUserById(1);
//		System.out.println(epu_ret);
		
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
//		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_PARTNER);
		List<MachCheckInOut> all_failed_mcios = machCheckInOutService.getMachCheckInOutByNormalAndSpecialCriteria(null);
		
//		Date repush_tolerance_time = DateUtil.calcDatePlusGivenTimeMills(new Date(), tolerance_time_mills);	// 计算出允许的打卡误差时间（如果打卡机上的打卡时间晚于允许的误差时间，则认为是明显错误的打卡数据）
		
		if(all_failed_mcios == null || all_failed_mcios.size() == 0) {
			logger.info("本次推送暂无推送失败的打卡数据...");
		} else {
			
			JSONArray all_failed_arr = new JSONArray();
			
//			for(MachCheckInOut mcio : all_failed_mcios) {
//				JSONObject json = new JSONObject();
//				json.put("USERID", mcio.getUserid());
//				json.put("Badgenumber", mcio.getUserid());
//				json.put("USERID", mcio.getUserid());
//				json.put("USERID", mcio.getUserid());
//				json.put("USERID", mcio.getUserid());
//				json.put("USERID", mcio.getUserid());
//				json.put("USERID", mcio.getUserid());
//				json.put("USERID", mcio.getUserid());
//				json.put("USERID", mcio.getUserid());
//			}
			
			all_failed_arr.addAll(all_failed_mcios);
			
			HeaderWrapper header = new HeaderWrapper();
	    	header.addHeader("Content-Type", "text/plain");
	    	PunchDatasBody pdb = new PunchDatasBody(all_failed_arr.toJSONString());
	    	
			ResponseWrapper response = rest_invoker.sendRequest(HTTPMethod.METHOD_POST, push_mach_data_url, header, pdb, null);
			
			Date repush_time = new Date();
			List<MachCheckInOut> mcios_upd = new ArrayList<MachCheckInOut>();
			
			if(response != null && response.getResponseStatus() == 200) {
				Object rspb = response.getResponseBody();
//				System.out.println("rspb class = " + rspb.getClass());
				
				if(rspb != null) {
					String rspb_str = rspb.toString();
					Object rspb_obj = JSONObject.parse(rspb_str);
					if(rspb_obj != null && rspb_obj instanceof JSONObject) {
						JSONObject rspb_json = (JSONObject) rspb_obj;
//						Integer ret_code = (Integer) rspb_json.get("ret_code");
						Integer ret_code = null;
						Object ret_code_obj = rspb_json.get("ret_code");
						if(ret_code_obj instanceof Integer) {
							ret_code = (Integer) ret_code_obj;
							if(ret_code > 0 || ret_code == -3) {
								logger.info("重新推送失败的打卡机打卡数据成功...，ret_code = " + ret_code);
								
								Object invalid_bgns_obj = rspb_json.get("invalid_badgenumbers");
								if(invalid_bgns_obj instanceof JSONArray) {
									JSONArray invalid_bgns = (JSONArray) invalid_bgns_obj;
									for(MachCheckInOut mcio : all_failed_mcios) {
										MachCheckInOut mcio_tmp = new MachCheckInOut();
										mcio_tmp.setId(mcio.getId());
										if(invalid_bgns.contains(mcio.getBadge_number())) {
											mcio_tmp.setPush_status("repush_invalid_badgenumber");
										} else {
											mcio_tmp.setPush_status("repush_success");
										}
										mcio_tmp.setLast_push_time(repush_time);
										Integer last_push_count = mcio.getPush_count();
										mcio_tmp.setPush_count(last_push_count==null?1:(last_push_count+1));
										mcios_upd.add(mcio_tmp);
									}
									DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
									int batch_mod_ret = machCheckInOutService.batchModifyMachCheckInOutById(mcios_upd);
									
									if(batch_mod_ret <= 0) {
										logger.error("(repush success)-批量修改打卡机打卡数据推送状态出现异常，batch_mod_ret = " + batch_mod_ret);
									} else {
										logger.info("(repush success)-批量修改打卡机打卡数据推送状态成功，batch_mod_ret = " + batch_mod_ret);
									}
								} else {
									logger.error("invalid_bgns_obj 类型异常...(1)...");
								}
							} else {
								logger.error("远程重新推送失败的打卡机打卡数据出现异常，ret_code = " + ret_code);
							}
						} else {
							logger.error("远程推送打卡机打卡数据接口返回的 ret_code 非Integer类型异常...(1)...");
						}
					}
				} else {
					logger.error("rspb对象为空异常。。。(1)");
				}
			} else {
				logger.error("重新推送失败的打卡机打卡数据出现异常...");
				
				for(MachCheckInOut mcio : all_failed_mcios) {
					MachCheckInOut mcio_tmp = new MachCheckInOut();
					mcio_tmp.setId(mcio.getId());
					mcio_tmp.setPush_status("repush_failed");
					mcio_tmp.setLast_push_time(repush_time);
					Integer last_push_count = mcio.getPush_count();
					mcio_tmp.setPush_count(last_push_count==null?1:(last_push_count+1));
					mcios_upd.add(mcio_tmp);
				}
				DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
				int batch_mod_ret = machCheckInOutService.batchModifyMachCheckInOutById(mcios_upd);
				
				if(batch_mod_ret <= 0) {
					logger.error("(repush failed)-批量修改打卡机打卡数据推送状态出现异常，batch_mod_ret = " + batch_mod_ret);
				} else {
					logger.info("(repush failed)-批量修改打卡机打卡数据推送状态成功，batch_mod_ret = " + batch_mod_ret);
				}
			}
		}
		
		
		// B计时点 \\
		long B_checkpoint_time = System.currentTimeMillis();
		logger.info("A~B计时点用时：" + DateUtil.timeMills2ReadableStr(B_checkpoint_time - A_checkpoint_time));
		
		
		logger.info("开始推送新一批的打卡机打卡数据...");
		
		// 获取所有的打卡机序列号，然后按照序列号分别查找最新的打卡数据，最后再推送到阿里云服务器上
		DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
		List<String> machine_sns = machMachinesService.queryAllMachineSns();
		
		// B1计时点 \\
		long B1_checkpoint_time = System.currentTimeMillis();
		logger.info("B~B1计时点用时：" + DateUtil.timeMills2ReadableStr(B1_checkpoint_time - B_checkpoint_time));
		
		if(machine_sns == null || machine_sns.size() == 0) {
			logger.error("打卡机序列号信息为空");
		} else {
			for(String mach_sn : machine_sns) {
				
				// B2计时点 \\
				long B2_checkpoint_time = System.currentTimeMillis();
				logger.info("B1~B2计时点用时：" + DateUtil.timeMills2ReadableStr(B2_checkpoint_time - B1_checkpoint_time));
				
				DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
//				DataSourceContextHolder.setDbType(DataSourceType.SOURCE_PARTNER);
//				MachCheckInOut mcio_max_id = machCheckInOutService.getMachCheckInOutWithMaxId();
				List<MachCheckInOut> mcios_mct = machCheckInOutService.queryMachCheckInOutWithMaxCheckTimeByMachSn(mach_sn);
				
				// B3计时点 \\
				long B3_checkpoint_time = System.currentTimeMillis();
				logger.info("B2~B3计时点用时：" + DateUtil.timeMills2ReadableStr(B3_checkpoint_time - B2_checkpoint_time));
				
				Date push_time = new Date();
				Date tolerance_time = DateUtil.calcDatePlusGivenTimeMills(push_time, tolerance_time_mills);	// 计算出允许的打卡误差时间（如果打卡机上的打卡时间晚于允许的误差时间，则认为是明显错误的打卡数据）
				List<MachCheckInOut> mcios_add = new ArrayList<MachCheckInOut>();
				
				if(mcios_mct == null || mcios_mct.size() == 0) {
					logger.error("打卡机(" + mach_sn + ")打卡数据推送记录表中暂无数据...");
					DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
					List<MachCheckInOut> fresh_mcios = machCheckInOutService.queryMachCheckInOutAndUserInfoTop100ByMachSn(mach_sn);
					List<MachCheckInOut> eliminated_fresh_mcios = eliminatePunchCardDatasWithErrorPunchTime(fresh_mcios, tolerance_time, mach_sn);
					if(eliminated_fresh_mcios == null || eliminated_fresh_mcios.size() == 0) {
						logger.info("打卡机(" + mach_sn + ")打卡数据为空。。。");
					} else {
						int push_ret = pushOperation(eliminated_fresh_mcios, "first_push", mach_sn);
						
						if(push_ret <=0) {
							logger.error("首次推送打卡机(" + mach_sn + ")打卡数据失败");
						} else {
							logger.info("首次推送打卡机(" + mach_sn + ")打卡数据成功");
						}
					}
				} else {
//					Date last_machpd_check_time = mcio_max_id.getCheck_time();
					
					MachCheckInOut mcio_mct_one = mcios_mct.get(0);
					Date latest_punch_check_time = mcio_mct_one.getCheck_time();
					
					if(latest_punch_check_time != null) {
//						mcio_mct_one.getUserid();
						List<Integer> userids_exclude = new ArrayList<Integer>();	// 取新一批打卡机打卡数据时需要排除在外的可能出现同一秒内的多个用户的打卡记录的用户id数组
						for(MachCheckInOut mc : mcios_mct) {
							userids_exclude.add(mc.getUserid());
						}
						
						// B4计时点 \\
						long B4_checkpoint_time = System.currentTimeMillis();
						logger.info("B3~B4计时点用时：" + DateUtil.timeMills2ReadableStr(B4_checkpoint_time - B3_checkpoint_time));
						
						DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ACCESS);
//						List<MachCheckInOut> top100_macios = machCheckInOutService.getMachCheckInOutAndUserInfoByCheckTimeTop100(latest_punch_check_time);
//						List<MachCheckInOut> top100_macios = 
//								machCheckInOutService.queryMachCheckInOutAndUserInfoByCheckTimeExcludeSomeUseridsTop100ByMachSn(latest_punch_check_time, userids_exclude, mach_sn);
						
						List<MachCheckInOut> top100_macios = 
								machCheckInOutService.queryMachCheckInOutByCheckTimeExcludeSomeUseridsTop100ByMachSn(latest_punch_check_time, userids_exclude, mach_sn);
						
						if(top100_macios == null || top100_macios.size() == 0) {
							logger.error("打卡机(" + mach_sn + ")没有新的打卡数据需要推送了。。。，或者出现其它异常。。。");
						} else {
//							if(mach_userinfos == null || mach_userinfos.size() == 0) {
//								logger.error("打卡机用户信息为空，或出现了其它异常。。。");
//							} else {
//								
//							}
							
							if(mach_userinfos_map.size() == 0) {
								logger.error("打卡机用户信息map中信息条数为0");
							} else {
								for(MachCheckInOut mcio : top100_macios) {
									if(mcio.getBadge_number() == null) {
										mcio.setBadge_number(mach_userinfos_map.get(mcio.getUserid()));
									} else {
										logger.info("打卡数据中用户工号信息不为null + mcio = " + mcio);
									}
								}
							}
						}
						
						
						// B5计时点 \\
						long B5_checkpoint_time = System.currentTimeMillis();
						logger.info("B4~B5计时点用时：" + DateUtil.timeMills2ReadableStr(B5_checkpoint_time - B4_checkpoint_time));	// 耗时最多！！！待优化！！！@@@@ ---> 已于2017.02.11优化
						
						List<MachCheckInOut> eliminated_pc_datas = eliminatePunchCardDatasWithErrorPunchTime(top100_macios, tolerance_time, mach_sn);
						if(eliminated_pc_datas == null || eliminated_pc_datas.size() == 0) {
							logger.info("打卡机(" + mach_sn + ")没有新的打卡数据需要推送了。。。");
							
						} else {
							JSONArray top100_mcios_arr = new JSONArray();
							
							top100_mcios_arr.addAll(eliminated_pc_datas);
							
							HeaderWrapper header = new HeaderWrapper();
					    	header.addHeader("Content-Type", "text/plain");
					    	PunchDatasBody pdb = new PunchDatasBody(top100_mcios_arr.toJSONString());
					    	
							ResponseWrapper response = rest_invoker.sendRequest(HTTPMethod.METHOD_POST, push_mach_data_url, header, pdb, null);
							
							if(response != null && response.getResponseStatus() == 200) {
								Object rspb = response.getResponseBody();
//								System.out.println("rspb class = " + rspb.getClass());
								
								if(rspb != null) {
									String rspb_str = rspb.toString();
									Object rspb_obj = JSONObject.parse(rspb_str);
									if(rspb_obj != null && rspb_obj instanceof JSONObject) {
										JSONObject rspb_json = (JSONObject) rspb_obj;
//										Integer ret_code = (Integer) rspb_json.get("ret_code");
										Integer ret_code = null;
										
										Object ret_code_obj = rspb_json.get("ret_code");
										if(ret_code_obj instanceof Integer) {
											ret_code = (Integer) ret_code_obj;
											if(ret_code > 0 || ret_code == -3) {
												logger.info("推送新一批的打卡机(" + mach_sn + ")打卡数据成功...，ret_code = " + ret_code);
												
												Object invalid_bgns_obj = rspb_json.get("invalid_badgenumbers");
												if(invalid_bgns_obj instanceof JSONArray) {
													JSONArray invalid_bgns = (JSONArray) invalid_bgns_obj;
													
													for(MachCheckInOut mcio : eliminated_pc_datas) {
//												MachCheckInOut mcio_tmp = new MachCheckInOut();
//												mcio_tmp.setId(mcio.getId());
//												mcio_tmp.setPush_status("push_success");
//												mcio_tmp.setLast_push_time(push_time);
//												Integer last_push_count = mcio.getPush_count();
//												mcio_tmp.setPush_count(last_push_count==null?1:(last_push_count+1));
//												mcios_add.add(mcio_tmp);
														if(invalid_bgns.contains(mcio.getBadge_number())) {
															mcio.setPush_status("push_invalid_badgenumber");
														} else {
															mcio.setPush_status("push_success");
														}
														mcio.setLast_push_time(push_time);
														Integer last_push_count = mcio.getPush_count();
														mcio.setPush_count(last_push_count==null?1:(last_push_count+1));
													}
													mcios_add = eliminated_pc_datas;
													DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
													int batch_mod_ret = machCheckInOutService.batchAddMachCheckInOut(mcios_add);
													
													if(batch_mod_ret <= 0) {
														logger.error("(push success)-批量添加打卡机(" + mach_sn + ")打卡数据出现异常，batch_mod_ret = " + batch_mod_ret);
													} else {
														logger.info("(push success)-批量添加打卡机(" + mach_sn + ")打卡数据成功，batch_mod_ret = " + batch_mod_ret);
													}
												} else {
													logger.error("invalid_bgns_obj 类型异常...(2)...");
												}
											} else {
												logger.error("远程推送新一批的打卡机(" + mach_sn + ")打卡数据出现异常，ret_code = " + ret_code);
											}
										} else {
											logger.error("远程推送打卡机(" + mach_sn + ")打卡数据接口返回的 ret_code 非Integer类型异常...(2)...");
										}
									}
								} else {
									logger.error("远程推送新一批的打卡机(" + mach_sn + ")打卡数据时，出现rspb对象为空异常。。。(2)");
								}
							} else {
								logger.error("推送新一批的打卡机(" + mach_sn + ")打卡数据出现异常...");
								
								for(MachCheckInOut mcio : eliminated_pc_datas) {
//									MachCheckInOut mcio_tmp = new MachCheckInOut();
//									mcio_tmp.setId(mcio.getId());
//									mcio_tmp.setPush_status("push_failed");
//									mcio_tmp.setLast_push_time(push_time);
//									Integer last_push_count = mcio.getPush_count();
//									mcio_tmp.setPush_count(last_push_count==null?1:(last_push_count+1));
//									mcios_add.add(mcio_tmp);
									
									mcio.setPush_status("push_failed");
									mcio.setLast_push_time(push_time);
									Integer last_push_count = mcio.getPush_count();
									mcio.setPush_count(last_push_count==null?1:(last_push_count+1));
								}
								mcios_add = eliminated_pc_datas;
								DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
								int batch_mod_ret = machCheckInOutService.batchAddMachCheckInOut(mcios_add);
								
								if(batch_mod_ret <= 0) {
									logger.error("(push failed)-批量添加打卡机(" + mach_sn + ")打卡数据出现异常，batch_mod_ret = " + batch_mod_ret);
								} else {
									logger.info("(push failed)-批量添加打卡机(" + mach_sn + ")打卡数据成功，batch_mod_ret = " + batch_mod_ret);
								}
							}
						}
					} else {
						logger.error("打卡机(" + mach_sn + ")打卡数据记录表中最晚打卡的打卡时间为空异常！");
					}
				} 
			}
		}
		
//		else {	// 当 mcios_mct.size() > 1 时
//			
//		}
		
		
		// C计时点 \\
//		long C_checkpoint_time = System.currentTimeMillis();
		long end_time = System.currentTimeMillis();
		
		logger.info("B~C计时点用时：" + DateUtil.timeMills2ReadableStr(end_time - B_checkpoint_time));
		
		long use_time = (end_time - start_time);
		long total_time = (end_time - monitor_syslog_time);
		
		logger.info("第（" + PushPunchCardDatas.getPush_times() + "）次推送结束时间：" + DateUtil.formatDate(2, new Date()) + "，本次推送耗时：" + DateUtil.timeMills2ReadableStr(use_time) + " (over)");
		logger.info("过程总耗时：" + DateUtil.timeMills2ReadableStr(total_time));
		
	}
	
	
	
	/**
	 * 排除掉打卡时间明显超出了允许的误差时间的打卡数据，然后返回打卡时间正常的打卡数据
	 * 
	 * @param pc_datas
	 * @param tolerance_time
	 * @return
	 */
	private List<MachCheckInOut> eliminatePunchCardDatasWithErrorPunchTime(List<MachCheckInOut> pc_datas, Date tolerance_time, String mach_sn) {
		if(pc_datas == null || tolerance_time == null) {
			return null;
		}
		
		if(pc_datas.size() == 0) {
			return pc_datas;
		}
		
		List<MachCheckInOut> elim_pc_datas = new ArrayList<MachCheckInOut>();	// 用于存放排除掉了打卡时间明显错误的打卡数据的数组
		List<MachCheckInOut> left_pc_datas = new ArrayList<MachCheckInOut>();	// 用于存放被排除掉的打卡时间明显错误的打卡数据的数组
		
		for(MachCheckInOut mcio : pc_datas) {
			Date check_time = mcio.getCheck_time();
			if(check_time == null) {
				logger.error("打卡数据中的打卡时间为空异常。。。");
				left_pc_datas.add(mcio);
			} else {
				if(check_time.after(tolerance_time)) {
					left_pc_datas.add(mcio);
				} else {
					elim_pc_datas.add(mcio);
				}
			}
		}
		
		Date now_time = new Date();
		
		if(left_pc_datas.size() == 0) {
			logger.info("本次将要推送到阿里云服务器的打卡机打卡数据中无打卡时间错误的打卡数据");
		} else {
			for(MachCheckInOut mc : left_pc_datas) {
				mc.setLast_push_time(now_time);
				mc.setPush_status("check_time_error");
			}
			DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
			int batch_add_ret = machCheckInOutService.batchAddMachCheckInOutWithErrorCheckTime(left_pc_datas);
			
			if(batch_add_ret <= 0) {
				logger.error("(checktime error)-批量添加打卡时间有误的打卡机(" + mach_sn + ")打卡数据出现异常，batch_add_ret = " + batch_add_ret);
			} else {
				logger.info("(checktime error)-批量添加打卡时间有误的打卡机(" + mach_sn + ")打卡数据成功，batch_add_ret = " + batch_add_ret);
			}
		}
		
		return elim_pc_datas;
	}
	
	
	
	/**
	 * 将时间毫秒数转化成易于人可读的字符串形式
	 * 
	 * @param timeMills
	 * @return
	 */
	public String timeMills2ReadableStr(long timeMills) {
		if(timeMills < 0) {
			return null;
		}
		
		if(timeMills < 1000) {
			return timeMills + " (毫秒).";
		}
		
		long timeSeconds = timeMills/1000;
		long timeMills_remain = timeMills%1000;
		
		if(timeSeconds < 60) {
			return timeSeconds + " (秒), " + timeMills_remain + " (毫秒).";
		}
		
		long timeMinutes = timeSeconds/60;
		long timeSeconds_remain = timeSeconds%60;
		
		if(timeMinutes < 60) {
			return timeMinutes + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		}
		
		long timeHours = timeMinutes/60;
		long timeMinutes_remain = timeMinutes%60;
		
		if(timeHours < 24) {
			return timeHours + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		}
		
		long timeDays = timeHours/24;
		long timeHours_remain = timeHours%24;
		
		if(timeDays < 7) {
			return timeDays + " (天), " + timeHours_remain + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		} else if(timeDays < 30) {
			long timeWeeks = timeDays/7;
			long timeDays_remain = timeDays%7;
			
//			if(timeWeeks < 4) {
//			}
			return timeWeeks + " (周), " + timeDays_remain + " (天), " + timeHours_remain + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		} else if(timeDays < 365) {
			long timeMonths = timeDays/30;
			long timeDays_30 = timeDays%30;
			long timeWeeks = timeDays_30/7;
			long timeDays_remain = timeDays_30%7;
			return timeMonths + " (月), " + timeWeeks + " (周), " + timeDays_remain + " (天), " + timeHours_remain + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		} else {
			long timeYears = timeDays/365;
			long timeDays_365 = timeDays%365;
			long timeMonths = timeDays_365/30;
			long timeDays_365_30 = timeDays_365%30;
			long timeWeeks = timeDays_365_30/7;
			long timeDays_remain = timeDays_365_30%7;
			
			return timeYears + " (年), " + timeMonths + " (月), " + timeWeeks + " (周), " + timeDays_remain + " (天), " + timeHours_remain + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		}
		
//		return null;
	}
	
	
	public static void main(String[] args) {
		
		
		JSONObject obj = null;
		if(obj instanceof JSONObject) {
			System.out.println("obj 是JSONObject类的实例");
		} else {
			System.out.println("obj 不是JSONObject类的实例");
		}
		
		
		/*
		long current_timeMills = System.currentTimeMillis();
		
		PushPunchCardDatas ppcd = new PushPunchCardDatas();
		
		String time_str = ppcd.timeMills2ReadableStr(5145379200l);
		
		System.out.println("转换时间毫秒数结果为：");
		System.out.println(time_str);
		
		*/
		
		/*
		String str = "";
		str = null;
		
		if(str != null && str.length() > 1) {
			System.out.println("字符串有效");
		} else {
			System.out.println("字符串无效");
		}
		*/
		
	}
	
	
	/**
	 * 推送打卡机打卡数据公共操作方法
	 * 
	 * @return
	 */
	private int pushOperation(List<MachCheckInOut> mcios, String desc, String sn) {
		if(mcios == null || mcios.size() == 0) {
			return -1;
		}
		
//		if(StringUtils.isBlank(desc) || (!"first_push".equals(desc) && !"push".equals(desc) && !"repush".equals(desc))) {
//			return -2;
//		}
		
		if(StringUtils.isBlank(desc) || !desc.contains("push")) {
			return -2;
		}
		
		Date push_time = new Date();
		List<MachCheckInOut> mcios_to_add = new ArrayList<MachCheckInOut>();
		
		JSONArray top100_mcios_arr = new JSONArray();
		top100_mcios_arr.addAll(mcios);
		
		HeaderWrapper header = new HeaderWrapper();
    	header.addHeader("Content-Type", "text/plain");
    	PunchDatasBody pdb = new PunchDatasBody(top100_mcios_arr.toJSONString());
    	
		ResponseWrapper response = rest_invoker.sendRequest(HTTPMethod.METHOD_POST, push_mach_data_url, header, pdb, null);
		
		if(response != null && response.getResponseStatus() == 200) {
			Object rspb = response.getResponseBody();
//			System.out.println("rspb class = " + rspb.getClass());
			
			if(rspb != null) {
				String rspb_str = rspb.toString();
				Object rspb_obj = JSONObject.parse(rspb_str);
				if(rspb_obj != null && rspb_obj instanceof JSONObject) {
					JSONObject rspb_json = (JSONObject) rspb_obj;
//					Integer ret_code = (Integer) rspb_json.get("ret_code");
					Integer ret_code = null;
					
					Object ret_code_obj = rspb_json.get("ret_code");
					if(ret_code_obj instanceof Integer) {
						ret_code = (Integer) ret_code_obj;
						if(ret_code > 0 || ret_code == -3) {
							logger.info("推送新一批的打卡机(" + sn + ")打卡数据成功...，ret_code = " + ret_code);
							
							Object invalid_bgns_obj = rspb_json.get("invalid_badgenumbers");
							if(invalid_bgns_obj instanceof JSONArray) {
								JSONArray invalid_bgns = (JSONArray) invalid_bgns_obj;
								
								for(MachCheckInOut mcio : mcios) {
//							MachCheckInOut mcio_tmp = new MachCheckInOut();
//							mcio_tmp.setId(mcio.getId());
//							mcio_tmp.setPush_status(desc + "_success");
//							mcio_tmp.setLast_push_time(push_time);
//							Integer last_push_count = mcio.getPush_count();
//							mcio_tmp.setPush_count(last_push_count==null?1:(last_push_count+1));
//							mcios_to_add.add(mcio_tmp);
									
									if(invalid_bgns.contains(mcio.getBadge_number())) {
										mcio.setPush_status(desc + "_invalid_badgenumber");
									} else {
//										mcio.setPush_status("push_success");
										mcio.setPush_status(desc + "_success");
									}
									mcio.setLast_push_time(push_time);
									Integer last_push_count = mcio.getPush_count();
									mcio.setPush_count(last_push_count==null?1:(last_push_count+1));
								}
								mcios_to_add = mcios;
								DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
								int batch_mod_ret = machCheckInOutService.batchAddMachCheckInOut(mcios_to_add);
								
								if(batch_mod_ret <= 0) {
									logger.error("(" + desc + " success)-批量添加打卡机(" + sn + ")打卡数据出现异常，batch_mod_ret = " + batch_mod_ret);
								} else {
									logger.info("(" + desc + " success)-批量添加打卡机(" + sn + ")打卡数据成功，batch_mod_ret = " + batch_mod_ret);
								}
								
								return 1;
							} else {
								logger.error("invalid_bgns_obj 类型异常...(3)...");
							}
							
						} else {
							logger.error("远程推送新一批的打卡机(" + sn + ")打卡数据出现异常，ret_code = " + ret_code);
						}
					} else {
						logger.error("远程推送打卡机(" + sn + ")打卡数据接口返回的 ret_code 非Integer类型异常...(3)...");
					}
				}
			} else {
				logger.error("rspb对象为空异常。。。(3)");
			}
			return 0;
		} else {
			logger.error("推送新一批的打卡机(" + sn + ")打卡数据出现异常...");
			
			for(MachCheckInOut mcio : mcios) {
//				MachCheckInOut mcio_tmp = new MachCheckInOut();
//				mcio_tmp.setId(mcio.getId());
//				mcio_tmp.setPush_status(desc + "_failed");
//				mcio_tmp.setLast_push_time(push_time);
//				Integer last_push_count = mcio.getPush_count();
//				mcio_tmp.setPush_count(last_push_count==null?1:(last_push_count+1));
//				mcios_to_add.add(mcio_tmp);
				
				mcio.setPush_status(desc + "_failed");
				mcio.setLast_push_time(push_time);
				Integer last_push_count = mcio.getPush_count();
				mcio.setPush_count(last_push_count==null?1:(last_push_count+1));
			}
			mcios_to_add = mcios;
			DataSourceContextHolder.setDbType(DataSourceType.SOURCE_ADMIN);
			int batch_mod_ret = machCheckInOutService.batchAddMachCheckInOut(mcios_to_add);
			
			if(batch_mod_ret <= 0) {
				logger.error("(" + desc + " failed)-批量添加打卡机(" + sn + ")打卡数据出现异常，batch_mod_ret = " + batch_mod_ret);
			} else {
				logger.info("(" + desc + " failed)-批量添加打卡机(" + sn + ")打卡数据成功，batch_mod_ret = " + batch_mod_ret);
			}
			
			return -3;
		}
		
//		return -3;
	}
	
}
