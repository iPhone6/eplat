package com.cn.eplat.model;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author Administrator
 * 
 * 公司用户实体类
 * 
 */
public class EpUser {
	private Integer id;	// 主键id（企业人员id）
	private String code;	// 用户代码（一般由英文或数字字符组成，作为用户登录名）
	private String name;	// 用户姓名（一般表示用户的真实姓名，可以是中文名）
	private String english_name;	// 英文名（企业微信通讯录中的字段）
	private String pwd;	// 密码密文（采用md5加密）
	private String mima;	// 密码明文
	private Integer em_uid;	// 对应环信系统用户id
	private Integer role_id;	// 角色id
	private String role_name;	// 角色名
	private String userid;	// 员工UserID（参照钉钉管理后台）
	private String work_no;	// 工号
	private String gender;	// 性别
	private String job_title;	// 职位（钉钉企业通讯录中的字段）/职务（企业微信通讯录中的字段）
	private Date on_position_date;	// 入职日期（“yyyy-MM-dd”日期格式）
	private String mobile_phone;	// 手机号
	private String fixed_phone;	// 分机号（钉钉企业通讯录中的字段）/座机号（企业微信通讯录中的字段）
	private String email;	// 邮箱
	private String base_place;	// base地
	private String work_place;	// 办公地点
	private String notes;	// 备注
	private String dept_name;	// 部门名（暂时将部门名放在用户信息表中）
	private String project_name;	// 项目名（暂时将项目名放在用户信息表中）
	private String origin_pwd;   // 原始密码
	private String identity_no;   // 身份证号
	private String ver_code;   // 验证码
	private Date vercode_time;   // 身份证号
	private Integer max_device_num;	// 最多同时绑定的设备数量（默认初始值为1台）
	private Integer max_bind_times;	// 有效时间内累计最多绑定次数（默认初始值为3次）
	private Integer bind_times_count;	// 有效时间内累计已绑定次数（在所有设备上绑定过的次数之和）
	private Date bind_start_time;	// 累计已绑定次数的生效时间
	private Integer bind_limit_time;	// 累计已绑定次数的有效时间（以天为单位）
	private Integer mach_userid;	// 打卡机用户id
	public static final Integer DEFAULT_MAX_DEVICE_NUM = 1;	// 每个用户默认最多同时能绑定的设备个数为1
	public static final Integer DEFAULT_BIND_LIMIT_TIME = 365;	// 每个用户默认已绑定次数有效时间为365天（即一年内累计已绑定次数不重置）
	public static final Integer DEFAULT_MAX_BIND_TIMES = 5;	// 每个用户默认最多的累计绑定次数为5次
	private String type;
	private Boolean push2hw_atten;	// 表示是否要将当前用户的考勤数据推送给HW考勤系统
	
	public EpUser() {
		
	}
	
	public EpUser(EpUser epu) {
		if(epu == null) {
			this.id = null;
			this.code = null;
			this.name = null;
			this.english_name = null;
			this.pwd = null;
			this.mima = null;
			this.em_uid = null;
			this.role_id = null;
			this.role_name = null;
			this.userid = null;
			this.work_no = null;
			this.gender = null;
			this.job_title = null;
			this.on_position_date = null;
			this.mobile_phone = null;
			this.fixed_phone = null;
			this.email = null;
			this.base_place = null;
			this.work_place = null;
			this.notes = null;
			this.dept_name = null;
			this.project_name = null;
			this.origin_pwd = null;
			this.identity_no = null;
			this.ver_code = null;
			this.vercode_time = null;
			this.max_device_num = null;
			this.max_bind_times = null;
			this.bind_times_count = null;
			this.bind_start_time = null;
			this.bind_limit_time = null;
			this.mach_userid = null;
		}
		this.id = epu.id;
		this.code = epu.code;
		this.name = epu.name;
		this.english_name = epu.english_name;
		this.pwd = epu.pwd;
		this.mima = epu.mima;
		this.em_uid = epu.em_uid;
		this.role_id = epu.role_id;
		this.role_name = epu.role_name;
		this.userid = epu.userid;
		this.work_no = epu.work_no;
		this.gender = epu.gender;
		this.job_title = epu.job_title;
		if(epu.on_position_date == null) {
			this.on_position_date =  null;
		} else {
			this.on_position_date = (Date) epu.on_position_date.clone();
		}
		this.mobile_phone = epu.mobile_phone;
		this.fixed_phone = epu.fixed_phone;
		this.email = epu.email;
		this.base_place = epu.base_place;
		this.work_place = epu.work_place;
		this.notes = epu.notes;
		this.dept_name = epu.dept_name;
		this.project_name = epu.project_name;
		this.origin_pwd = epu.origin_pwd;
		this.identity_no = epu.identity_no;
		this.ver_code = epu.ver_code;
		if(epu.vercode_time == null) {
			this.vercode_time = null;
		} else {
			this.vercode_time = (Date) epu.vercode_time.clone();
		}
		this.max_device_num = epu.max_device_num;
		this.max_bind_times = epu.max_bind_times;
		this.bind_times_count = epu.bind_times_count;
		if(epu.bind_start_time == null) {
			this.bind_start_time = null;
		} else {
			this.bind_start_time = (Date) epu.bind_start_time.clone();
		}
		this.bind_limit_time = epu.bind_limit_time;
		this.mach_userid = epu.mach_userid;
		this.type = epu.type;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnglish_name() {
		return english_name;
	}
	public void setEnglish_name(String english_name) {
		this.english_name = english_name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getMima() {
		return mima;
	}
	public void setMima(String mima) {
		this.mima = mima;
	}
	public Integer getEm_uid() {
		return em_uid;
	}
	public void setEm_uid(Integer em_uid) {
		this.em_uid = em_uid;
	}
	public Integer getRole_id() {
		return role_id;
	}
	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getWork_no() {
		return work_no;
	}
	public void setWork_no(String work_no) {
		this.work_no = work_no;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getJob_title() {
		return job_title;
	}
	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}
	public Date getOn_position_date() {
		return on_position_date;
	}
	public void setOn_position_date(Date on_position_date) {
		this.on_position_date = on_position_date;
	}
	public String getMobile_phone() {
		return mobile_phone;
	}
	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}
	public String getFixed_phone() {
		return fixed_phone;
	}
	public void setFixed_phone(String fixed_phone) {
		this.fixed_phone = fixed_phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBase_place() {
		return base_place;
	}
	public void setBase_place(String base_place) {
		this.base_place = base_place;
	}
	public String getWork_place() {
		return work_place;
	}
	public void setWork_place(String work_place) {
		this.work_place = work_place;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getDept_name() {
		return dept_name;
	}
	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getOrigin_pwd() {
		return origin_pwd;
	}
	public void setOrigin_pwd(String origin_pwd) {
		this.origin_pwd = origin_pwd;
	}
	public String getIdentity_no() {
		return identity_no;
	}
	public void setIdentity_no(String identity_no) {
		this.identity_no = identity_no;
	}
	public String getVer_code() {
		return ver_code;
	}
	public void setVer_code(String ver_code) {
		this.ver_code = ver_code;
	}
	public Date getVercode_time() {
		return vercode_time;
	}
	public void setVercode_time(Date vercode_time) {
		this.vercode_time = vercode_time;
	}
	public Integer getMax_device_num() {
		return max_device_num;
	}
	public void setMax_device_num(Integer max_device_num) {
		this.max_device_num = max_device_num;
	}
	public Integer getMax_bind_times() {
		return max_bind_times;
	}
	public void setMax_bind_times(Integer max_bind_times) {
		this.max_bind_times = max_bind_times;
	}
	public Integer getBind_times_count() {
		return bind_times_count;
	}
	public void setBind_times_count(Integer bind_times_count) {
		this.bind_times_count = bind_times_count;
	}
	public Date getBind_start_time() {
		return bind_start_time;
	}
	public void setBind_start_time(Date bind_start_time) {
		this.bind_start_time = bind_start_time;
	}
	public Integer getBind_limit_time() {
		return bind_limit_time;
	}
	public void setBind_limit_time(Integer bind_limit_time) {
		this.bind_limit_time = bind_limit_time;
	}
	public Integer getMach_userid() {
		return mach_userid;
	}
	public void setMach_userid(Integer mach_userid) {
		this.mach_userid = mach_userid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getPush2hw_atten() {
		return push2hw_atten;
	}
	public void setPush2hw_atten(Boolean push2hw_atten) {
		this.push2hw_atten = push2hw_atten;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this, SerializerFeature.WriteMapNullValue);
	}
	
	public static void main(String[] args) {
		/*
		EpUser epu = new EpUser();
		epu.setId(10);
		epu.setName("123fsf");
		epu.setVercode_time(new Date());
		epu.setEm_uid(14);
		epu.setNotes("你们009oop");
		
		EpUser epu2 = new EpUser(epu);
		System.out.println(epu2 == epu);
		System.out.println(epu);
		System.out.println(epu2);
		*/
		
		/*
		EpUser epu = new EpUser();
		epu.setId(12);
		
		Integer a = epu.getId();
		
		Integer b = a;
		System.out.println("1. epu id = " + epu.getId());
		System.out.println(a==b);
		System.out.println("1. a = " + a);
		System.out.println("1. b = " + b);
		
		System.out.println("----------------------");
		a = new Integer(33);
		System.out.println("2. a = " + a);
		System.out.println("2. b = " + b);
		System.out.println(a==b);
		System.out.println("2. epu id = " + epu.getId());
		*/
		
	}
	
}
