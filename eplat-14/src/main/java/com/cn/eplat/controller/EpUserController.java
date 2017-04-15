package com.cn.eplat.controller;

import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.util.StringUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zsl.testmybatis.TestEpDeviceDao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cn.eplat.model.EmUser;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.service.IEmUserService;
import com.cn.eplat.service.IEpUserService;
import com.cn.eplat.utils.CreatePasswordHelper;
import com.cn.eplat.utils.CryptionUtil;
import com.cn.eplat.utils.EplatEmUserHelper;
import com.cn.eplat.utils.rsa.Base64;
import com.cn.eplat.utils.rsa.RSAEncrypt;
import com.easemob.server.api.IMUserAPI;
import com.easemob.server.comm.ClientContext;
import com.easemob.server.comm.EasemobRestAPIFactory;
import com.easemob.server.comm.body.AuthPwdTokenBody;
import com.easemob.server.comm.body.IMUserBody;
import com.easemob.server.comm.body.ModifyNicknameBody;
import com.easemob.server.comm.body.ResetPasswordBody;
import com.easemob.server.comm.wrapper.BodyWrapper;
import com.easemob.server.comm.wrapper.ResponseWrapper;
import com.elead.util.mail.MailUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/epUserController")
public class EpUserController {
	// 邮箱的正则表达式
	private final String emailRegExp = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	// 手机号码的正则表达式
	private final String mobileRegExp = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

	@Resource
	private IEpUserService epUserService;
	@Resource
	private IEmUserService emUserService;
	// 加密密钥
	private String ENCRYP_KEY = "eplat2016";
	// 客户端上下文（访问环信IM系统API接口的客户端）
	private static ClientContext context = ClientContext.getInstance();
	// 环信用户系统API
	private static EasemobRestAPIFactory erf_factory = context.init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
	private static IMUserAPI em_user_api = (IMUserAPI) erf_factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
	
	private static Logger logger = Logger.getLogger(EpUserController.class);
	
	public static IMUserAPI getEm_user_api() {
		return em_user_api;
	}


	@RequestMapping(params = "loginCheck", produces = "application/json; charset=UTF-8")
	@ResponseBody
	// 1.更新接口文档
	// 2.统一前后台的密码字段加密和解密算法（采用RSA加密算法，前台传入参数时，使用公钥加密，后台接收到加密的密码参数后，使用私钥解密）
	// a.增加一个修改密码的功能
	// TODO: b.对于不同的用户区分不同的角色，从而拥有不同的权限，不同角色的用户能查看到的数据也不同
	// TODO: c.以后会有一些流程操作相关的处理的需求，例如审批流程
	// d.在查找添加好友时，通过模糊匹配公司账号用户名，返回匹配的公司账号列表给前台
	// e.暂时给安卓端增加一个参数（plain_pwd），用于直接传递明文密码，等后面弄好了RSA加密后，再取消这个明文密码参数
	public String loginCheckJson(HttpServletRequest request) {

		JSONObject jsonObj = new JSONObject();

		/*
		 * 注1：@RequestBody 注解表示其后所跟的参数是请求体中的参数，请求发送方需要以json格式对请求参数进行封装。
		 * 注2：request.getParameter("xxx") 获取的参数是请求的查询参数，如请求URL中类似 a=10&b=20
		 * 这样的参数，每个参数名等于一个值，不同参数之间用&符号分隔开。 注3：如果需要获取路径参数，则需要在接收的参数前添加
		 * 
		 * @PathVariable 注解。
		 */
		String user_code = request.getParameter("user_code");
		String user_pwd_encrypted = request.getParameter("user_pwd");
		String platform = request.getParameter("platform");	// 区分移动端平台的参数（必填，目前只包含iOS和Android两个平台）
		String plain_pwd = request.getParameter("plain_pwd_abc"); // TODO:
																// 用于临时明文传递密码参数，后面安卓端弄好RSA加密后要取消掉！
		
		String user_pwd = "";

		// user_pwd = request.getParameter("user_pwd");

		/*
		 * boolean is_usercode_empty = false; // 表示传入的用户代码是否有空值的标志 boolean
		 * is_userpwd_empty = false; // 表示传入的用户密码是否有空值的标志
		 * if(StringUtils.isNullOrEmpty(user_code)) { is_usercode_empty = true;
		 * } if(StringUtils.isNullOrEmpty(user_pwd_encrypted)) {
		 * is_userpwd_empty = true; } else {
		 * if(StringUtils.isNullOrEmpty(user_pwd_encrypted)) { is_userpwd_empty
		 * = true; } } if(is_usercode_empty || is_userpwd_empty) { //
		 * jsonObj.put("ret_code", 0); // jsonObj.put("ret_message",
		 * "用户名或密码为空！"); // return JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); }
		 */
		if (plain_pwd == null) {
			if (StringUtils.isNullOrEmpty(user_code)
					|| StringUtils.isNullOrEmpty(user_pwd_encrypted) || StringUtils.isNullOrEmpty(platform)) {
				jsonObj.put("ret_code", 0);
				jsonObj.put("ret_message", "用户名或密码或平台参数为空！");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// 对于传入的参数，采用的解密方案：前台使用RSA公钥加密密码参数，后台接收到后使用RSA私钥解密密码参数
			// 私钥解密过程
			String filepath = RSAEncrypt.KEYS_FILE_PATH;
			byte[] res;
			
			if("iOS".equalsIgnoreCase(platform)) {
				// 首先尝试使用默认的RSA解密算法，如果是iOS端加密后的密文，则可以一次性解密成功，如果是安卓端加密后的密文，则第一次解密会出现异常，然后尝试采用针对安卓端的RSA解密算法
				try {
					String private_key_str = RSAEncrypt.loadPrivateKeyByFile(filepath);
					RSAPrivateKey rsa_private_key = RSAEncrypt.loadPrivateKeyByStr(private_key_str);
					byte[] user_pwd_decode = Base64.decode(user_pwd_encrypted);
					long decrypt_start = System.currentTimeMillis();
					res = RSAEncrypt.decrypt(rsa_private_key, user_pwd_decode);
					long decrypt_end = System.currentTimeMillis();
					System.out.println("[iOS] 解密成功，耗时：" + (decrypt_end - decrypt_start) + " 毫秒。");
					user_pwd = new String(res);
				} catch (Exception e) {
					// e.printStackTrace();
					// 第一次尝试RSA解密出现异常后，下面开始尝试第二次RSA解密（专门针对安卓端的解密算法decryptAndroid）
//				try {
//					String private_key_str = RSAEncrypt.loadPrivateKeyByFile(filepath);
//					RSAPrivateKey rsa_private_key = RSAEncrypt.loadPrivateKeyByStr(private_key_str);
//					byte[] user_pwd_decode = Base64.decode(user_pwd_encrypted);
//					long decrypt_start = System.currentTimeMillis();
//					res = RSAEncrypt.decryptAndroid(rsa_private_key, user_pwd_decode);
//					long decrypt_end = System.currentTimeMillis();
//					System.out.println("[Android] 第二次解密成功，耗时：" + (decrypt_end - decrypt_start) + " 毫秒。");
//					user_pwd = new String(res);
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
					jsonObj.put("ret_code", -6);
					jsonObj.put("ret_message", "[iOS] 解密出现异常！");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
			} else if("Android".equalsIgnoreCase(platform)) {
				try {
					String private_key_str = RSAEncrypt.loadPrivateKeyByFile(filepath);
					RSAPrivateKey rsa_private_key = RSAEncrypt.loadPrivateKeyByStr(private_key_str);
					byte[] user_pwd_decode = Base64.decode(user_pwd_encrypted);
					long decrypt_start = System.currentTimeMillis();
					res = RSAEncrypt.decryptAndroid(rsa_private_key, user_pwd_decode);
					long decrypt_end = System.currentTimeMillis();
					System.out.println("[Android] 解密成功，耗时：" + (decrypt_end - decrypt_start) + " 毫秒。");
					user_pwd = new String(res);
				} catch (Exception e1) {
					e1.printStackTrace();
					jsonObj.put("ret_code", -6);
					jsonObj.put("ret_message", "[Android] 解密出现异常！");
					return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
				}
			} else {
				jsonObj.put("ret_code", -8);
				jsonObj.put("ret_message", "未知的平台参数异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			

//			String mop = request.getParameter("mop");
//			System.out.println("mop = " + mop + "  end.");
			// String pwd1 = request.getParameter("pwd");
			// String www = request.getParameter("www");
			// System.out.println("pwd1 = " + pwd1 + ", " + "www = " + www +
			// " .");

			// if(epu == null) {
			// jsonObj.put("ret_code", -3);
			// jsonObj.put("ret_message", "参数为空！");
			// return JSONObject.toJSONString(jsonObj,
			// SerializerFeature.WriteMapNullValue);
			// }
			//
			// String user_code = epu.getCode();
			// String user_pwd = epu.getPwd();

			// String plain_text = "中文字";
			// String encrypt_text = CryptionUtil.encryption(plain_text,
			// "12345678");
			// System.out.println("==---=== 加密后的文本：" + encrypt_text);

			/** add by zhangshun the start */
			Pattern emailPattern = Pattern.compile(emailRegExp);
			Matcher emailMatcher = emailPattern.matcher(user_code);

			Pattern mobilePhonePattern = Pattern.compile(mobileRegExp);
			Matcher mobilePhoneMatcher = mobilePhonePattern.matcher(user_code);

			EpUser ep_user = null;
			if (emailMatcher.matches()) {// 邮箱登录
				ep_user = epUserService.getEpUserByEmail(user_code);
			} else if (mobilePhoneMatcher.matches()) {// 手机号码登录
				ep_user = epUserService.getEpUserByMobileNum(user_code);
			} else if (org.apache.commons.lang3.StringUtils
					.isNumeric(user_code)) {// 工号登录
				ep_user = epUserService.getEpUserByWorkNum(user_code);
			}
			/** add by zhangshun the end */

			if (ep_user == null) {
				jsonObj.put("ret_code", -1);
				jsonObj.put("ret_message", "不存在该用户！");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			String pwd_md5 = CryptionUtil.md5Hex(user_pwd);

			if (!ep_user.getPwd().equals(pwd_md5)) {
				jsonObj.put("ret_code", -2);
				jsonObj.put("ret_message", "用户名或密码错误！");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			if (pwd_md5.equals(ep_user.getOrigin_pwd())) {
				jsonObj.put("ret_code", -7);
				jsonObj.put("ret_message", "密码与原始密码一样，请修改密码后再登录!");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}
			
			// 只要登录公司账号成功，就在返回的Json数据中加入当前登录用户的姓名和角色名信息
			JSONObject ep_user_info = new JSONObject();
			ep_user_info.put("name", ep_user.getName());
			ep_user_info.put("userid", ep_user.getId()); // 将当前用户在数据库中的主键id返回给前台（方便在调用如打卡接口时参入用户id参数）
			ep_user_info.put("work_no", ep_user.getWork_no());
			if (!StringUtils.isNullOrEmpty(ep_user.getRole_name())) {
				ep_user_info.put("role_name", ep_user.getRole_name());
			} else { // 如果当前用户的角色名为空时，返回一个默认的角色名“职员”
				ep_user_info.put("role_name", "职员");
			}
			jsonObj.put("ep_user_info", ep_user_info);

			// 登录公司系统账号成功后，如果在后台数据库中找到与该公司系统账号相对应的环信系统账号，则直接返回该已有的环信系统账号
			// 根据公司系统账号查询对应的环信系统账号
			Integer em_user_id = ep_user.getEm_uid(); // 得到公司系统账号对应的环信系统账号
			if (em_user_id != null && em_user_id > 0) { // 如果该公司系统账号已经对应了一个环信系统账号，则可直接使用这个已有的环信账号，直接返回给前台
				EmUser em_user = emUserService.getEmUserById(em_user_id);
				if(em_user != null && em_user.getNickname() == null) {	// 如果用户的环信用户昵称为null，则默认修改该用户的环信用户昵称为该用户的姓名
//					ResponseWrapper emu_ret_info = (ResponseWrapper) em_user_api.getIMUsersByUserName(em_user.getUsername());
					
					BodyWrapper nickname_body = new ModifyNicknameBody(ep_user.getName());
					ResponseWrapper resw = (ResponseWrapper) em_user_api.modifyIMUserNickNameWithAdminToken(em_user.getUsername(), nickname_body);
					if (resw.getResponseStatus() == 200) { // 当返回状态码为200时，表示调用修改环信用户昵称的接口成功，也即是修改环信用户昵称的操作成功！
						logger.info("====修改环信系统默认昵称为用户姓名成功====");
					} else {
						logger.error("====修改环信系统默认昵称为用户姓名失败====");
					}
					
					EmUser emu_upd = new EmUser();
					emu_upd.setId(em_user.getId());
					emu_upd.setNickname(ep_user.getName());
//					em_user.setNickname(ep_user.getName());
					int mod_ret = emUserService.modifyEmUserById(emu_upd);
					if(mod_ret > 0) {
						logger.info("====修改公司系统默认昵称为用户姓名成功====");
					} else {
						logger.error("====修改公司系统默认昵称为用户姓名失败====");
					}
				}
				jsonObj.put("ret_code", 1);
				jsonObj.put("ret_message", "在数据库中找到已有环信用户！");
				jsonObj.put("im_user_name", em_user.getUsername()); // 返回给前台的环信用户名字段未做加密处理，即为用户名原文
				// 下面返回给前台的环信用户密码字段，由于在数据库中存储的是加密后的密码，所以应当先对密码进行解码，然后按照前台能解码的方式进行加密（如Base64加密），最后才返回给前台
				// jsonObj.put("im_user_pwd", em_user.getPassword()); //
				// 返回给前台的环信用户密码字段应该是加密后的密码
				jsonObj.put("token",
						context.init(ClientContext.INIT_FROM_PROPERTIES)
								.getAuthToken()); // 返回给前台的token字段

				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// 登录公司系统账号成功后，如果在后台数据库中未找到与该公司系统账号相对应的环信系统账号，才开始尝试登录环信系统账号
			if (!StringUtils.isNullOrEmpty(ep_user.getEmail())
					&& !StringUtils.isNullOrEmpty(ep_user.getWork_no())) {
				// 环信账号是名字首字母加工号
				String em_login_name = ep_user.getEmail().substring(0, 1)
						+ ep_user.getWork_no();

				// 在新创建环信用户时，将密码使用MD5加密处理（以防环信系统被攻击后用户密码泄漏）
				String em_login_pwd = new Md5Hash(user_pwd).toString();

				EasemobRestAPIFactory factory = context.init(
						ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
				IMUserAPI user = (IMUserAPI) factory
						.newInstance(EasemobRestAPIFactory.USER_CLASS);

				// login a easemob(环信) IM user

				BodyWrapper pwd_body = new AuthPwdTokenBody(em_login_name,
						em_login_pwd);
				ResponseWrapper login_response = (ResponseWrapper) user
						.loginIMUser(pwd_body);
				// System.out.println("longin response:");
				// System.out.println(login_response);
				// System.out.println("------------");
				// System.out.println(login_response.getResponseBody().getClass());
				// ObjectNode resbd = (ObjectNode)
				// login_response.getResponseBody();
				// System.out.println("resbd = " + resbd);
				// System.out.println("access_token = " +
				// resbd.get("access_token"));
				// System.out.println("user node value's type = " +
				// resbd.get("user").getClass());
				// System.out.println("user = " + resbd.get("user"));
				if (login_response.getResponseStatus() == 200) { // 如果环信IM登录用户接口返回状态为200
																	// OK
					ObjectNode resbd = (ObjectNode) login_response
							.getResponseBody();
					if (resbd.has("user")) { // 如果环信IM登录用户接口返回的json数据中包含user字段，说明用公司用户账户密码登录环信系统成功
						jsonObj.put("ret_code", 2);
						jsonObj.put("ret_message", "环信用户登录成功！");
						jsonObj.put("im_user_name", em_login_name); // 返回给前台的环信用户名字段未做加密处理，即为名字拼音首字母+工号
						// jsonObj.put("im_user_pwd",
						// CryptionUtil.encryption(user_pwd, ENCRYP_KEY)); //
						// 返回给前台的环信用户密码字段是加密后的密码
						jsonObj.put(
								"token",
								context.init(ClientContext.INIT_FROM_PROPERTIES)
										.getAuthToken()); // 返回给前台的token字段

						// 将公司系统账号与环信系统账号相关联起来（一对一）
						ObjectNode resbd_user = (ObjectNode) resbd.get("user");
						// System.out.println("resbd_user ==> " + resbd_user);
						TextNode resbd_user_username = (TextNode) resbd_user
								.get("username");
						
						EmUser emu = new EmUser();
						if (resbd_user_username != null) {
							emu.setUsername(resbd_user_username.textValue());
						}
						emu.setPassword(CryptionUtil.encryption(em_login_pwd,
								ENCRYP_KEY));
						if (resbd_user.get("uuid") != null) {
							emu.setUuid(((TextNode) resbd_user.get("uuid"))
									.textValue());
						}
						if (resbd_user.get("nickname") != null) {
							emu.setNickname(((TextNode) resbd_user
									.get("nickname")).textValue());
						} else {
							if(resbd_user_username != null) {	// 如果用户的环信用户昵称为null，则默认修改该用户的环信用户昵称为该用户的姓名
								BodyWrapper nickname_body = new ModifyNicknameBody(ep_user.getName());
								ResponseWrapper resw = (ResponseWrapper) em_user_api.modifyIMUserNickNameWithAdminToken(resbd_user_username.textValue(), nickname_body);
								if (resw.getResponseStatus() == 200) { // 当返回状态码为200时，表示调用修改环信用户昵称的接口成功，也即是修改环信用户昵称的操作成功！
									logger.info("====修改默认昵称为用户姓名成功====");
								} else {
									logger.error("====修改默认昵称为用户姓名失败====");
								}
								emu.setNickname(ep_user.getName());
							}
						}
						
						if (resbd_user.get("activated") != null) {
							emu.setActivated(((BooleanNode) resbd_user
									.get("activated")).asInt());
						}
						if (resbd_user.get("created") != null) {
							emu.setCreated(((LongNode) resbd_user
									.get("created")).asLong());
						}
						if (resbd_user.get("modified") != null) {
							emu.setModified(((LongNode) resbd_user
									.get("modified")).asLong());
						}

						int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库
						if (ret > 0) {
							EpUser epu = new EpUser();
							epu.setId(ep_user.getId());
							epu.setEm_uid(emu.getId());
							epUserService.modifyEpUserById(epu); // 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来
						}

						return JSONObject.toJSONString(jsonObj,
								SerializerFeature.WriteMapNullValue);
					}
				} else if (login_response.getResponseStatus() == 404) { // 如果登录环信系统返回状态码为404，表示环信系统中不存在该用户名的用户
					// 这种情况要新创建一个环信用户，并与公司账号相关联起来（一对一）
					// 新创建环信用户的用户名命名规则：EplatEmUseryyyyMMddHHmmssSSSrrrr（前面EplatEmUser为固定字符，后面的以当前服务器系统时间为准，yyyy表示4位年份，MM表示2位月份，dd表示2位日，HH表示24小时的小时数，mm表示分钟数，ss表示秒数，SSS表示毫秒数，rrrr表示4位随机数）
					// String epem_username =
					// EplatEmUserHelper.genEpEmUsername();
					// String epem_userpwd = EplatEmUserHelper.genEpEmUserpwd();

					// Create a easemob（环信） IM user
					BodyWrapper userBody = new IMUserBody(em_login_name,
							em_login_pwd, ep_user.getName());	// 默认将公司系统用户的姓名作为环信系统的昵称
					ResponseWrapper rw = (ResponseWrapper) user
							.createNewIMUserSingle(userBody);
					// System.out.println(" rw = " + rw);

					if (rw.getResponseStatus() == 200) { // 如果新创建环信用户成功
						ObjectNode rw_body = (ObjectNode) rw.getResponseBody();
						if (rw_body.has("entities")) {
							ArrayNode rwbd_entities = (ArrayNode) rw_body
									.get("entities");
							JsonNode rwbd_data = rwbd_entities.get(0);
							EmUser emu = new EmUser();
							if (rwbd_data.get("username") != null) {
								emu.setUsername(rwbd_data.get("username")
										.textValue());
							}
							emu.setPassword(CryptionUtil.encryption(
									em_login_pwd, ENCRYP_KEY));
							if (rwbd_data.get("uuid") != null) {
								emu.setUuid(rwbd_data.get("uuid").textValue());
							}
							if (rwbd_data.get("nickname") != null) {
								emu.setNickname(rwbd_data.get("nickname")
										.textValue());
							}
							if (rwbd_data.get("activated") != null) {
								emu.setActivated(rwbd_data.get("activated")
										.asInt());
							}
							if (rwbd_data.get("created") != null) {
								emu.setCreated(rwbd_data.get("created")
										.asLong());
							}
							if (rwbd_data.get("modified") != null) {
								emu.setModified(rwbd_data.get("modified")
										.asLong());
							}

							int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库
							if (ret > 0) {
								EpUser epu = new EpUser();
								epu.setId(ep_user.getId());
								epu.setEm_uid(emu.getId());
								epUserService.modifyEpUserById(epu); // 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来
							}

							jsonObj.put("ret_code", 3);
							jsonObj.put("ret_message", "新创建环信用户成功！");
							jsonObj.put("im_user_name", em_login_name); // 返回给前台的环信用户名字段未做加密处理，即为用户名原文
							// jsonObj.put("im_user_pwd",
							// CryptionUtil.encryption(epem_userpwd,
							// ENCRYP_KEY)); // 返回给前台的环信用户密码字段是加密后的密码
							jsonObj.put(
									"token",
									context.init(
											ClientContext.INIT_FROM_PROPERTIES)
											.getAuthToken()); // 返回给前台的token字段

							return JSONObject.toJSONString(jsonObj,
									SerializerFeature.WriteMapNullValue);
						}
					} else {
						jsonObj.put("ret_code", -3);
						jsonObj.put("ret_message", "新创建环信用户失败！");
						// jsonObj.put("im_user_name", epem_username); //
						// 返回给前台的环信用户名字段未做加密处理，即为用户名原文
						// jsonObj.put("im_user_pwd",
						// CryptionUtil.encryption(epem_userpwd,
						// ENCRYP_KEY));
						// // 返回给前台的环信用户密码字段是加密后的密码
					}
				} else if (login_response.getResponseStatus() == 400) { // 如果登录环信系统返回状态码为400，表示环信系统中存在该用户名的用户，但密码不正确；
					// 这种情况要新创建一个环信用户，用户名由系统自动生成一个
					// 如果第一次尝试用公司账号用户名创建环信账号时，环信系统中已存在与该公司账号重名的账号，则由系统自动生成一个环信账号用户名，但密码还是和之前公司账号密码保持一致
					String epem_username2 = EplatEmUserHelper.genEpEmUsername();

					// 第二次尝试创建环信用户 Create a easemob（环信） IM user
					BodyWrapper userBody2 = new IMUserBody(epem_username2,
							em_login_pwd, ep_user.getName());	// 默认将公司系统用户的姓名作为环信系统的昵称
					ResponseWrapper rw2 = (ResponseWrapper) user
							.createNewIMUserSingle(userBody2);

					if (rw2.getResponseStatus() == 200) { // 如果新创建环信用户成功
						ObjectNode rw_body2 = (ObjectNode) rw2
								.getResponseBody();
						if (rw_body2.has("entities")) {
							ArrayNode rwbd_entities2 = (ArrayNode) rw_body2
									.get("entities");
							JsonNode rwbd_data2 = rwbd_entities2.get(0);
							EmUser emu = new EmUser();
							if (rwbd_data2.get("username") != null) {
								emu.setUsername(rwbd_data2.get("username")
										.textValue());
							}
							emu.setPassword(CryptionUtil.encryption(
									em_login_pwd, ENCRYP_KEY));
							if (rwbd_data2.get("uuid") != null) {
								emu.setUuid(rwbd_data2.get("uuid").textValue());
							}
							if (rwbd_data2.get("nickname") != null) {
								emu.setNickname(rwbd_data2.get("nickname")
										.textValue());
							}
							if (rwbd_data2.get("activated") != null) {
								emu.setActivated(rwbd_data2.get("activated")
										.asInt());
							}
							if (rwbd_data2.get("created") != null) {
								emu.setCreated(rwbd_data2.get("created")
										.asLong());
							}
							if (rwbd_data2.get("modified") != null) {
								emu.setModified(rwbd_data2.get("modified")
										.asLong());
							}
							int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库
							if (ret > 0) {
								EpUser epu = new EpUser();
								epu.setId(ep_user.getId());
								epu.setEm_uid(emu.getId());
								epUserService.modifyEpUserById(epu); // 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来
							}

							jsonObj.put("ret_code", 4);
							jsonObj.put("ret_message", "第2次新创建环信用户成功！");
							jsonObj.put("im_user_name", epem_username2); // 返回给前台的环信用户名字段未做加密处理，即为用户名原文
							// jsonObj.put("im_user_pwd",
							// CryptionUtil.encryption(epem_userpwd,
							// ENCRYP_KEY));
							// // 返回给前台的环信用户密码字段是加密后的密码
							jsonObj.put(
									"token",
									context.init(
											ClientContext.INIT_FROM_PROPERTIES)
											.getAuthToken()); // 返回给前台的token字段

							return JSONObject.toJSONString(jsonObj,
									SerializerFeature.WriteMapNullValue);
						}
					} else {
						jsonObj.put("ret_code", -4);
						jsonObj.put("ret_message", "第2次新创建环信用户失败！");
					}
				} else {
					jsonObj.put("ret_code", -5);
					jsonObj.put("ret_message", "登录环信系统出现未知错误！");
				}
			} else {
				jsonObj.put("ret_code", -6);
				jsonObj.put("ret_message", "邮箱或者工号不存在！");
			}
			return JSONObject.toJSONString(jsonObj,
					SerializerFeature.WriteMapNullValue);

		} else {
			if (StringUtils.isNullOrEmpty(user_code)
					|| StringUtils.isNullOrEmpty(plain_pwd)) {
				jsonObj.put("ret_code", 0);
				jsonObj.put("ret_message", "用户名或密码为空！");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			user_pwd = plain_pwd;
			/*
			 * // 对于传入的参数，采用的解密方案：前台使用RSA公钥加密密码参数，后台接收到后使用RSA私钥解密密码参数 // 私钥解密过程
			 * String filepath = RSAEncrypt.KEYS_FILE_PATH; byte[] res; try {
			 * res =
			 * RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt
			 * .loadPrivateKeyByFile(filepath)),
			 * Base64.decode(user_pwd_encrypted)); user_pwd = new String(res); }
			 * catch (Exception e) { // e.printStackTrace();
			 * jsonObj.put("ret_code", -6); jsonObj.put("ret_message",
			 * "解密出现异常！"); return JSONObject.toJSONString(jsonObj,
			 * SerializerFeature.WriteMapNullValue); }
			 */

			String mop = request.getParameter("mop");
			System.out.println("mop = " + mop + "  end.");
			// String pwd1 = request.getParameter("pwd");
			// String www = request.getParameter("www");
			// System.out.println("pwd1 = " + pwd1 + ", " + "www = " + www +
			// " .");

			// if(epu == null) {
			// jsonObj.put("ret_code", -3);
			// jsonObj.put("ret_message", "参数为空！");
			// return JSONObject.toJSONString(jsonObj,
			// SerializerFeature.WriteMapNullValue);
			// }
			//
			// String user_code = epu.getCode();
			// String user_pwd = epu.getPwd();

			// String plain_text = "中文字";
			// String encrypt_text = CryptionUtil.encryption(plain_text,
			// "12345678");
			// System.out.println("==---=== 加密后的文本：" + encrypt_text);

			/** add by zhangshun the start */
			Pattern emailPattern = Pattern.compile(emailRegExp);
			Matcher emailMatcher = emailPattern.matcher(user_code);

			Pattern mobilePhonePattern = Pattern.compile(mobileRegExp);
			Matcher mobilePhoneMatcher = mobilePhonePattern.matcher(user_code);
			EpUser ep_user = null;

			if (emailMatcher.matches()) {// 邮箱登录
				ep_user = epUserService.getEpUserByEmail(user_code);
			} else if (mobilePhoneMatcher.matches()) {// 手机号码登录
				ep_user = epUserService.getEpUserByMobileNum(user_code);
			} else if (org.apache.commons.lang3.StringUtils
					.isNumeric(user_code)) {// 工号登录
				ep_user = epUserService.getEpUserByWorkNum(user_code);
			}
			/** add by zhangshun the end */

			if (ep_user == null) {
				jsonObj.put("ret_code", -1);
				jsonObj.put("ret_message", "不存在该用户！");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			String pwd_md5 = CryptionUtil.md5Hex(user_pwd);
			if (!ep_user.getPwd().equals(pwd_md5)) {
				jsonObj.put("ret_code", -2);
				jsonObj.put("ret_message", "用户名或密码错误！");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			if (pwd_md5.equals(ep_user.getOrigin_pwd())) {
				jsonObj.put("ret_code", -7);
				jsonObj.put("ret_message", "密码与原始密码一样，请修改密码后再登录!");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}
			
			// 只要登录公司账号成功，就在返回的Json数据中加入当前登录用户的姓名和角色名信息
			// jsonObj.put("role_name", ep_user.getRole_name());
			JSONObject ep_user_info = new JSONObject();
			ep_user_info.put("name", ep_user.getName());
			ep_user_info.put("userid", ep_user.getId()); // 将当前用户在数据库中的主键id返回给前台（方便在调用如打卡接口时参入用户id参数）
			ep_user_info.put("work_no", ep_user.getWork_no());
			if (!StringUtils.isNullOrEmpty(ep_user.getRole_name())) {
				ep_user_info.put("role_name", ep_user.getRole_name());
			} else { // 如果当前用户的角色名为空时，返回一个默认的角色名“职员”
				ep_user_info.put("role_name", "职员");
			}
			jsonObj.put("ep_user_info", ep_user_info);

			// 登录公司系统账号成功后，如果在后台数据库中找到与该公司系统账号相对应的环信系统账号，则直接返回该已有的环信系统账号
			// 根据公司系统账号查询对应的环信系统账号
			Integer em_user_id = ep_user.getEm_uid(); // 得到公司系统账号对应的环信系统账号
			if (em_user_id != null && em_user_id > 0) { // 如果该公司系统账号已经对应了一个环信系统账号，则可直接使用这个已有的环信账号，直接返回给前台
				EmUser em_user = emUserService.getEmUserById(em_user_id);

				jsonObj.put("ret_code", 1);
				jsonObj.put("ret_message", "在数据库中找到已有环信用户！");
				jsonObj.put("im_user_name", em_user.getUsername()); // 返回给前台的环信用户名字段未做加密处理，即为用户名原文
				// 下面返回给前台的环信用户密码字段，由于在数据库中存储的是加密后的密码，所以应当先对密码进行解码，然后按照前台能解码的方式进行加密（如Base64加密），最后才返回给前台
				// jsonObj.put("im_user_pwd", em_user.getPassword()); //
				// 返回给前台的环信用户密码字段应该是加密后的密码
				jsonObj.put("token",
						context.init(ClientContext.INIT_FROM_PROPERTIES)
								.getAuthToken()); // 返回给前台的token字段

				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// 登录公司系统账号成功后，如果在后台数据库中未找到与该公司系统账号相对应的环信系统账号，才开始尝试登录环信系统账号
			if (!StringUtils.isNullOrEmpty(ep_user.getEmail())
					&& !StringUtils.isNullOrEmpty(ep_user.getWork_no())) {
				// 环信账号是名字首字母加工号
				String em_login_name = ep_user.getEmail().substring(0, 1)
						+ ep_user.getWork_no();

				// 在新创建环信用户时，将密码使用MD5加密处理（以防环信系统被攻击后用户密码泄漏）
				String em_login_pwd = new Md5Hash(user_pwd).toString();

				EasemobRestAPIFactory factory = context.init(
						ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
				IMUserAPI user = (IMUserAPI) factory
						.newInstance(EasemobRestAPIFactory.USER_CLASS);

				// login a easemob(环信) IM user
				BodyWrapper pwd_body = new AuthPwdTokenBody(em_login_name,
						em_login_pwd);
				ResponseWrapper login_response = (ResponseWrapper) user
						.loginIMUser(pwd_body);
				// System.out.println("longin response:");
				// System.out.println(login_response);
				// System.out.println("------------");
				// System.out.println(login_response.getResponseBody().getClass());
				// ObjectNode resbd = (ObjectNode)
				// login_response.getResponseBody();
				// System.out.println("resbd = " + resbd);
				// System.out.println("access_token = " +
				// resbd.get("access_token"));
				// System.out.println("user node value's type = " +
				// resbd.get("user").getClass());
				// System.out.println("user = " + resbd.get("user"));
				if (login_response.getResponseStatus() == 200) { // 如果环信IM登录用户接口返回状态为200
																	// OK
					ObjectNode resbd = (ObjectNode) login_response
							.getResponseBody();
					if (resbd.has("user")) { // 如果环信IM登录用户接口返回的json数据中包含user字段，说明用公司用户账户密码登录环信系统成功
						jsonObj.put("ret_code", 2);
						jsonObj.put("ret_message", "环信用户登录成功！");
						jsonObj.put("im_user_name", em_login_name); // 返回给前台的环信用户名字段未做加密处理，即为用户名原文
						// jsonObj.put("im_user_pwd",
						// CryptionUtil.encryption(user_pwd, ENCRYP_KEY)); //
						// 返回给前台的环信用户密码字段是加密后的密码
						jsonObj.put(
								"token",
								context.init(ClientContext.INIT_FROM_PROPERTIES)
										.getAuthToken()); // 返回给前台的token字段

						// 将公司系统账号与环信系统账号相关联起来（一对一）
						ObjectNode resbd_user = (ObjectNode) resbd.get("user");
						// System.out.println("resbd_user ==> " + resbd_user);
						EmUser emu = new EmUser();
						TextNode resbd_user_username = (TextNode) resbd_user
								.get("username");
						if (resbd_user_username != null) {
							emu.setUsername(resbd_user_username.textValue());
						}
						emu.setPassword(CryptionUtil.encryption(em_login_pwd,
								ENCRYP_KEY));
						if (resbd_user.get("uuid") != null) {
							emu.setUuid(((TextNode) resbd_user.get("uuid"))
									.textValue());
						}
						if (resbd_user.get("nickname") != null) {
							emu.setNickname(((TextNode) resbd_user
									.get("nickname")).textValue());
						}
						if (resbd_user.get("activated") != null) {
							emu.setActivated(((BooleanNode) resbd_user
									.get("activated")).asInt());
						}
						if (resbd_user.get("created") != null) {
							emu.setCreated(((LongNode) resbd_user
									.get("created")).asLong());
						}
						if (resbd_user.get("modified") != null) {
							emu.setModified(((LongNode) resbd_user
									.get("modified")).asLong());
						}

						int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库
						if (ret > 0) {
							EpUser epu = new EpUser();
							epu.setId(ep_user.getId());
							epu.setEm_uid(emu.getId());
							epUserService.modifyEpUserById(epu); // 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来
						}

						return JSONObject.toJSONString(jsonObj,
								SerializerFeature.WriteMapNullValue);
					}
				} else if (login_response.getResponseStatus() == 404) { // 如果登录环信系统返回状态码为404，表示环信系统中不存在该用户名的用户
					// 这种情况要新创建一个环信用户，并与公司账号相关联起来（一对一）
					// 新创建环信用户的用户名命名规则：EplatEmUseryyyyMMddHHmmssSSSrrrr（前面EplatEmUser为固定字符，后面的以当前服务器系统时间为准，yyyy表示4位年份，MM表示2位月份，dd表示2位日，HH表示24小时的小时数，mm表示分钟数，ss表示秒数，SSS表示毫秒数，rrrr表示4位随机数）
					// String epem_username =
					// EplatEmUserHelper.genEpEmUsername();
					// String epem_userpwd = EplatEmUserHelper.genEpEmUserpwd();

					// Create a easemob（环信） IM user
					BodyWrapper userBody = new IMUserBody(em_login_name,
							em_login_pwd, null);
					ResponseWrapper rw = (ResponseWrapper) user
							.createNewIMUserSingle(userBody);
					// System.out.println(" rw = " + rw);

					if (rw.getResponseStatus() == 200) { // 如果新创建环信用户成功
						ObjectNode rw_body = (ObjectNode) rw.getResponseBody();
						if (rw_body.has("entities")) {
							ArrayNode rwbd_entities = (ArrayNode) rw_body
									.get("entities");
							JsonNode rwbd_data = rwbd_entities.get(0);
							EmUser emu = new EmUser();
							if (rwbd_data.get("username") != null) {
								emu.setUsername(rwbd_data.get("username")
										.textValue());
							}
							emu.setPassword(CryptionUtil.encryption(
									em_login_pwd, ENCRYP_KEY));
							if (rwbd_data.get("uuid") != null) {
								emu.setUuid(rwbd_data.get("uuid").textValue());
							}
							if (rwbd_data.get("nickname") != null) {
								emu.setNickname(rwbd_data.get("nickname")
										.textValue());
							}
							if (rwbd_data.get("activated") != null) {
								emu.setActivated(rwbd_data.get("activated")
										.asInt());
							}
							if (rwbd_data.get("created") != null) {
								emu.setCreated(rwbd_data.get("created")
										.asLong());
							}
							if (rwbd_data.get("modified") != null) {
								emu.setModified(rwbd_data.get("modified")
										.asLong());
							}

							int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库
							if (ret > 0) {
								EpUser epu = new EpUser();
								epu.setId(ep_user.getId());
								epu.setEm_uid(emu.getId());
								epUserService.modifyEpUserById(epu); // 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来
							}

							jsonObj.put("ret_code", 3);
							jsonObj.put("ret_message", "新创建环信用户成功！");
							jsonObj.put("im_user_name", em_login_name); // 返回给前台的环信用户名字段未做加密处理，即为用户名原文
							// jsonObj.put("im_user_pwd",
							// CryptionUtil.encryption(epem_userpwd,
							// ENCRYP_KEY));
							// // 返回给前台的环信用户密码字段是加密后的密码
							jsonObj.put(
									"token",
									context.init(
											ClientContext.INIT_FROM_PROPERTIES)
											.getAuthToken()); // 返回给前台的token字段

							return JSONObject.toJSONString(jsonObj,
									SerializerFeature.WriteMapNullValue);
						}
					} else {
						jsonObj.put("ret_code", -3);
						jsonObj.put("ret_message", "新创建环信用户失败！");
						// jsonObj.put("im_user_name", epem_username); //
						// 返回给前台的环信用户名字段未做加密处理，即为用户名原文
						// jsonObj.put("im_user_pwd",
						// CryptionUtil.encryption(epem_userpwd, ENCRYP_KEY));
						// //
						// 返回给前台的环信用户密码字段是加密后的密码
					}

				} else if (login_response.getResponseStatus() == 400) { // 如果登录环信系统返回状态码为400，表示环信系统中存在该用户名的用户，但密码不正确；
					// 这种情况要新创建一个环信用户，用户名由系统自动生成一个
					// 如果第一次尝试用公司账号用户名创建环信账号时，环信系统中已存在与该公司账号重名的账号，则由系统自动生成一个环信账号用户名，但密码还是和之前公司账号密码保持一致
					String epem_username2 = EplatEmUserHelper.genEpEmUsername();

					// 第二次尝试创建环信用户 Create a easemob（环信） IM user
					BodyWrapper userBody2 = new IMUserBody(epem_username2,
							em_login_pwd, null);
					ResponseWrapper rw2 = (ResponseWrapper) user
							.createNewIMUserSingle(userBody2);

					if (rw2.getResponseStatus() == 200) { // 如果新创建环信用户成功
						ObjectNode rw_body2 = (ObjectNode) rw2
								.getResponseBody();
						if (rw_body2.has("entities")) {
							ArrayNode rwbd_entities2 = (ArrayNode) rw_body2
									.get("entities");
							JsonNode rwbd_data2 = rwbd_entities2.get(0);
							EmUser emu = new EmUser();
							if (rwbd_data2.get("username") != null) {
								emu.setUsername(rwbd_data2.get("username")
										.textValue());
							}
							emu.setPassword(CryptionUtil.encryption(
									em_login_pwd, ENCRYP_KEY));
							if (rwbd_data2.get("uuid") != null) {
								emu.setUuid(rwbd_data2.get("uuid").textValue());
							}
							if (rwbd_data2.get("nickname") != null) {
								emu.setNickname(rwbd_data2.get("nickname")
										.textValue());
							}
							if (rwbd_data2.get("activated") != null) {
								emu.setActivated(rwbd_data2.get("activated")
										.asInt());
							}
							if (rwbd_data2.get("created") != null) {
								emu.setCreated(rwbd_data2.get("created")
										.asLong());
							}
							if (rwbd_data2.get("modified") != null) {
								emu.setModified(rwbd_data2.get("modified")
										.asLong());
							}

							int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库
							if (ret > 0) {
								EpUser epu = new EpUser();
								epu.setId(ep_user.getId());
								epu.setEm_uid(emu.getId());
								epUserService.modifyEpUserById(epu); // 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来
							}

							jsonObj.put("ret_code", 4);
							jsonObj.put("ret_message", "第2次新创建环信用户成功！");
							jsonObj.put("im_user_name", epem_username2); // 返回给前台的环信用户名字段未做加密处理，即为用户名原文
							// jsonObj.put("im_user_pwd",
							// CryptionUtil.encryption(epem_userpwd,
							// ENCRYP_KEY));
							// // 返回给前台的环信用户密码字段是加密后的密码
							jsonObj.put(
									"token",
									context.init(
											ClientContext.INIT_FROM_PROPERTIES)
											.getAuthToken()); // 返回给前台的token字段

							return JSONObject.toJSONString(jsonObj,
									SerializerFeature.WriteMapNullValue);
						}
					} else {
						jsonObj.put("ret_code", -4);
						jsonObj.put("ret_message", "第2次新创建环信用户失败！");
					}
				} else {
					jsonObj.put("ret_code", -5);
					jsonObj.put("ret_message", "登录环信系统出现未知错误！");
				}
			} else {
				jsonObj.put("ret_code", -6);
				jsonObj.put("ret_message", "邮箱或者工号不存在！");
			}
			return JSONObject.toJSONString(jsonObj,
					SerializerFeature.WriteMapNullValue);
		}

		/*
		 * // 对于传入的参数，采用的解密方案：前台使用RSA公钥加密密码参数，后台接收到后使用RSA私钥解密密码参数 // 私钥解密过程
		 * String filepath = RSAEncrypt.KEYS_FILE_PATH; byte[] res; try { res =
		 * RSAEncrypt
		 * .decrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile
		 * (filepath)), Base64.decode(user_pwd_encrypted)); user_pwd = new
		 * String(res); } catch (Exception e) { // e.printStackTrace();
		 * jsonObj.put("ret_code", -6); jsonObj.put("ret_message", "解密出现异常！");
		 * return JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); }
		 * 
		 * 
		 * String mop = request.getParameter("mop"); System.out.println("mop = "
		 * + mop + "  end."); // String pwd1 = request.getParameter("pwd"); //
		 * String www = request.getParameter("www"); //
		 * System.out.println("pwd1 = " + pwd1 + ", " + "www = " + www + " .");
		 * 
		 * 
		 * // if(epu == null) { // jsonObj.put("ret_code", -3); //
		 * jsonObj.put("ret_message", "参数为空！"); // return
		 * JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); // } // // String user_code =
		 * epu.getCode(); // String user_pwd = epu.getPwd();
		 * 
		 * // String plain_text = "中文字"; // String encrypt_text =
		 * CryptionUtil.encryption(plain_text, "12345678"); //
		 * System.out.println("==---=== 加密后的文本：" + encrypt_text);
		 * 
		 * 
		 * EpUser ep_user = epUserService.getEpUserByCode(user_code); if(ep_user
		 * == null) { jsonObj.put("ret_code", -1); jsonObj.put("ret_message",
		 * "不存在该用户！"); return JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); }
		 * 
		 * String pwd_md5 = CryptionUtil.md5Hex(user_pwd);
		 * if(!ep_user.getPwd().equals(pwd_md5)) { jsonObj.put("ret_code", -2);
		 * jsonObj.put("ret_message", "用户名或密码错误！"); return
		 * JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); }
		 * 
		 * // 登录公司系统账号成功后，如果在后台数据库中找到与该公司系统账号相对应的环信系统账号，则直接返回该已有的环信系统账号 //
		 * 根据公司系统账号查询对应的环信系统账号 int em_user_id = ep_user.getEm_uid(); //
		 * 得到公司系统账号对应的环信系统账号 if(em_user_id > 0) { //
		 * 如果该公司系统账号已经对应了一个环信系统账号，则可直接使用这个已有的环信账号，直接返回给前台 EmUser em_user =
		 * emUserService.getEmUserById(em_user_id);
		 * 
		 * jsonObj.put("ret_code", 1); jsonObj.put("ret_message",
		 * "在数据库中找到已有环信用户！"); jsonObj.put("im_user_name",
		 * em_user.getUsername()); // 返回给前台的环信用户名字段未做加密处理，即为用户名原文 //
		 * 下面返回给前台的环信用户密码字段
		 * ，由于在数据库中存储的是加密后的密码，所以应当先对密码进行解码，然后按照前台能解码的方式进行加密（如Base64加密），最后才返回给前台
		 * // jsonObj.put("im_user_pwd", em_user.getPassword()); //
		 * 返回给前台的环信用户密码字段应该是加密后的密码 jsonObj.put("token",
		 * context.init(ClientContext.INIT_FROM_PROPERTIES).getAuthToken()); //
		 * 返回给前台的token字段
		 * 
		 * return JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); }
		 * 
		 * // 登录公司系统账号成功后，如果在后台数据库中未找到与该公司系统账号相对应的环信系统账号，才开始尝试登录环信系统账号
		 * EasemobRestAPIFactory factory =
		 * context.init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
		 * IMUserAPI user =
		 * (IMUserAPI)factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		 * 
		 * 
		 * // login a easemob(环信) IM user BodyWrapper pwd_body = new
		 * AuthPwdTokenBody(user_code, user_pwd); ResponseWrapper login_response
		 * = (ResponseWrapper) user.loginIMUser(pwd_body); //
		 * System.out.println("longin response:"); //
		 * System.out.println(login_response); //
		 * System.out.println("------------"); //
		 * System.out.println(login_response.getResponseBody().getClass()); //
		 * ObjectNode resbd = (ObjectNode) login_response.getResponseBody(); //
		 * System.out.println("resbd = " + resbd); //
		 * System.out.println("access_token = " + resbd.get("access_token")); //
		 * System.out.println("user node value's type = " +
		 * resbd.get("user").getClass()); // System.out.println("user = " +
		 * resbd.get("user")); if(login_response.getResponseStatus() == 200) {
		 * // 如果环信IM登录用户接口返回状态为200 OK ObjectNode resbd = (ObjectNode)
		 * login_response.getResponseBody(); if(resbd.has("user")) { //
		 * 如果环信IM登录用户接口返回的json数据中包含user字段，说明用公司用户账户密码登录环信系统成功
		 * jsonObj.put("ret_code", 2); jsonObj.put("ret_message", "环信用户登录成功！");
		 * jsonObj.put("im_user_name", user_code); //
		 * 返回给前台的环信用户名字段未做加密处理，即为用户名原文 // jsonObj.put("im_user_pwd",
		 * CryptionUtil.encryption(user_pwd, ENCRYP_KEY)); //
		 * 返回给前台的环信用户密码字段是加密后的密码 jsonObj.put("token",
		 * context.init(ClientContext.INIT_FROM_PROPERTIES).getAuthToken()); //
		 * 返回给前台的token字段
		 * 
		 * // 将公司系统账号与环信系统账号相关联起来（一对一） ObjectNode resbd_user = (ObjectNode)
		 * resbd.get("user"); // System.out.println("resbd_user ==> " +
		 * resbd_user); TextNode resbd_user_username = (TextNode)
		 * resbd_user.get("username"); EmUser emu = new EmUser();
		 * emu.setUsername(resbd_user_username.textValue());
		 * emu.setPassword(CryptionUtil.encryption(user_pwd, ENCRYP_KEY));
		 * emu.setUuid(((TextNode)resbd_user.get("uuid")).textValue());
		 * emu.setNickname(((TextNode)resbd_user.get("nickname")).textValue());
		 * emu.setActivated(((BooleanNode)resbd_user.get("activated")).asInt());
		 * emu.setCreated(((LongNode)resbd_user.get("created")).asLong());
		 * emu.setModified(((LongNode)resbd_user.get("modified")).asLong());
		 * 
		 * int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库 if(ret > 0)
		 * { EpUser epu = new EpUser(); epu.setId(ep_user.getId());
		 * epu.setEm_uid(emu.getId()); epUserService.modifyEpUserById(epu); //
		 * 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来 }
		 * 
		 * return JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); } } else
		 * if(login_response.getResponseStatus() == 404) { //
		 * 如果登录环信系统返回状态码为404，表示环信系统中不存在该用户名的用户 //
		 * 这种情况要新创建一个环信用户，并与公司账号相关联起来（一对一） //
		 * 新创建环信用户的用户名命名规则：EplatEmUseryyyyMMddHHmmssSSSrrrr
		 * （前面EplatEmUser为固定字符，后面的以当前服务器系统时间为准
		 * ，yyyy表示4位年份，MM表示2位月份，dd表示2位日，HH表示24小时的小时数
		 * ，mm表示分钟数，ss表示秒数，SSS表示毫秒数，rrrr表示4位随机数） // String epem_username =
		 * EplatEmUserHelper.genEpEmUsername(); // String epem_userpwd =
		 * EplatEmUserHelper.genEpEmUserpwd(); String epem_username = user_code;
		 * // 在新创建环信用户时，将密码使用MD5加密处理（以防环信系统被攻击后用户密码泄漏） String epem_userpwd = new
		 * Md5Hash(user_pwd).toString();
		 * 
		 * // Create a easemob（环信） IM user BodyWrapper userBody = new
		 * IMUserBody(epem_username, epem_userpwd, null); ResponseWrapper rw =
		 * (ResponseWrapper) user.createNewIMUserSingle(userBody); //
		 * System.out.println(" rw = " + rw);
		 * 
		 * if(rw.getResponseStatus() == 200) { // 如果新创建环信用户成功 ObjectNode rw_body
		 * = (ObjectNode) rw.getResponseBody(); if(rw_body.has("entities")) {
		 * ArrayNode rwbd_entities = (ArrayNode) rw_body.get("entities");
		 * JsonNode rwbd_data = rwbd_entities.get(0); EmUser emu = new EmUser();
		 * emu.setUsername(rwbd_data.get("username").textValue());
		 * emu.setPassword(CryptionUtil.encryption(epem_userpwd, ENCRYP_KEY));
		 * emu.setUuid(rwbd_data.get("uuid").textValue());
		 * if(rwbd_data.get("nickname") != null) {
		 * emu.setNickname(rwbd_data.get("nickname").textValue()); }
		 * emu.setActivated(rwbd_data.get("activated").asInt());
		 * emu.setCreated(rwbd_data.get("created").asLong());
		 * emu.setModified(rwbd_data.get("modified").asLong());
		 * 
		 * int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库 if(ret > 0)
		 * { EpUser epu = new EpUser(); epu.setId(ep_user.getId());
		 * epu.setEm_uid(emu.getId()); epUserService.modifyEpUserById(epu); //
		 * 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来 }
		 * 
		 * jsonObj.put("ret_code", 3); jsonObj.put("ret_message", "新创建环信用户成功！");
		 * jsonObj.put("im_user_name", epem_username); //
		 * 返回给前台的环信用户名字段未做加密处理，即为用户名原文 // jsonObj.put("im_user_pwd",
		 * CryptionUtil.encryption(epem_userpwd, ENCRYP_KEY)); //
		 * 返回给前台的环信用户密码字段是加密后的密码 jsonObj.put("token",
		 * context.init(ClientContext.INIT_FROM_PROPERTIES).getAuthToken()); //
		 * 返回给前台的token字段
		 * 
		 * return JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); } } else {
		 * jsonObj.put("ret_code", -3); jsonObj.put("ret_message",
		 * "新创建环信用户失败！"); // jsonObj.put("im_user_name", epem_username); //
		 * 返回给前台的环信用户名字段未做加密处理，即为用户名原文 // jsonObj.put("im_user_pwd",
		 * CryptionUtil.encryption(epem_userpwd, ENCRYP_KEY)); //
		 * 返回给前台的环信用户密码字段是加密后的密码 }
		 * 
		 * } else if(login_response.getResponseStatus() == 400) { //
		 * 如果登录环信系统返回状态码为400，表示环信系统中存在该用户名的用户，但密码不正确； //
		 * 这种情况要新创建一个环信用户，用户名由系统自动生成一个 //
		 * 如果第一次尝试用公司账号用户名创建环信账号时，环信系统中已存在与该公司账号重名的账号
		 * ，则由系统自动生成一个环信账号用户名，但密码还是和之前公司账号密码保持一致 String epem_username2 =
		 * EplatEmUserHelper.genEpEmUsername(); String epem_userpwd2 = new
		 * Md5Hash(user_pwd).toString();
		 * 
		 * // 第二次尝试创建环信用户 Create a easemob（环信） IM user BodyWrapper userBody2 =
		 * new IMUserBody(epem_username2, epem_userpwd2, null); ResponseWrapper
		 * rw2 = (ResponseWrapper) user.createNewIMUserSingle(userBody2);
		 * 
		 * if(rw2.getResponseStatus() == 200) { // 如果新创建环信用户成功 ObjectNode
		 * rw_body2 = (ObjectNode) rw2.getResponseBody();
		 * if(rw_body2.has("entities")) { ArrayNode rwbd_entities2 = (ArrayNode)
		 * rw_body2.get("entities"); JsonNode rwbd_data2 =
		 * rwbd_entities2.get(0); EmUser emu = new EmUser();
		 * emu.setUsername(rwbd_data2.get("username").textValue());
		 * emu.setPassword(CryptionUtil.encryption(epem_userpwd2, ENCRYP_KEY));
		 * emu.setUuid(rwbd_data2.get("uuid").textValue());
		 * if(rwbd_data2.get("nickname") != null) {
		 * emu.setNickname(rwbd_data2.get("nickname").textValue()); }
		 * emu.setActivated(rwbd_data2.get("activated").asInt());
		 * emu.setCreated(rwbd_data2.get("created").asLong());
		 * emu.setModified(rwbd_data2.get("modified").asLong());
		 * 
		 * int ret = emUserService.addEmUser(emu); // 将环信用户信息存入后台数据库 if(ret > 0)
		 * { EpUser epu = new EpUser(); epu.setId(ep_user.getId());
		 * epu.setEm_uid(emu.getId()); epUserService.modifyEpUserById(epu); //
		 * 通过ep_user表中的em_uid字段与em_user表的id字段一一对应关联起来 }
		 * 
		 * jsonObj.put("ret_code", 4); jsonObj.put("ret_message",
		 * "第2次新创建环信用户成功！"); jsonObj.put("im_user_name", epem_username2); //
		 * 返回给前台的环信用户名字段未做加密处理，即为用户名原文 // jsonObj.put("im_user_pwd",
		 * CryptionUtil.encryption(epem_userpwd, ENCRYP_KEY)); //
		 * 返回给前台的环信用户密码字段是加密后的密码 jsonObj.put("token",
		 * context.init(ClientContext.INIT_FROM_PROPERTIES).getAuthToken()); //
		 * 返回给前台的token字段
		 * 
		 * return JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue); } } else {
		 * jsonObj.put("ret_code", -4); jsonObj.put("ret_message",
		 * "第2次新创建环信用户失败！"); } } jsonObj.put("ret_code", -5);
		 * jsonObj.put("ret_message", "登录环信系统出现未知错误！"); return
		 * JSONObject.toJSONString(jsonObj,
		 * SerializerFeature.WriteMapNullValue);
		 */
	}
	
	
	/**
	 * 根据环信用户名获取环信好友
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getEmFriends", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getEmFriendsJson(HttpServletRequest request) {

		String em_username_str = request.getParameter("em_username");
		JSONObject jsonObj = new JSONObject();
		
		if(StringUtils.isNullOrEmpty(em_username_str)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "环信用户名参数为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		String em_username = em_username_str.trim();
		
		ResponseWrapper resw = (ResponseWrapper) em_user_api.getFriends(em_username);
		
//		EasemobRestAPIFactory tmp_factory = context.init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
//		IMUserAPI tmp_user_api = (IMUserAPI) tmp_factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
//		ResponseWrapper resw = (ResponseWrapper) tmp_user_api.getFriends(em_username);
		
		if (resw.getResponseStatus() != null && resw.getResponseStatus() == 200) { // 当返回状态码为200时，表示调用环信查看好友的接口成功
			logger.info("====查看好友成功====");
			Object resp_body = resw.getResponseBody();
			
		} else {
			logger.error("====查看好友失败====");
			System.out.println("status:" + resw.getResponseStatus());
			System.out.println("messages:" + resw.getMessages());
			System.out.println("body:" + resw.getResponseBody());
		}
		
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	

	@RequestMapping(params = "searchEpUsers", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String searchEpUsersJson(HttpServletRequest request) {

		String search_key = request.getParameter("key");
		JSONObject jsonObj = new JSONObject();

		if (StringUtils.isNullOrEmpty(search_key)) {
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "搜索关键字为空");

			return JSONObject.toJSONString(jsonObj,
					SerializerFeature.WriteMapNullValue);
		}

		List<EmUser> emus = epUserService.findEmUsersByKey(search_key);

		if (emus == null) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "搜索结果为空");

			return JSONObject.toJSONString(jsonObj,
					SerializerFeature.WriteMapNullValue);
		}

		JSONArray jsonArr = new JSONArray();
		for (EmUser emu : emus) {
			jsonArr.add(emu.getUsername());
		}
		// jsonArr.addAll(emus);

		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "搜索成功");
		jsonObj.put("em_users", jsonArr);

		return JSONObject.toJSONString(jsonObj,
				SerializerFeature.WriteMapNullValue);
	}
	
	/**
	 * 根据用户姓名（中文名）关键字模糊搜索匹配的用户信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "searchEpUserByName", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String searchEpUserByNameJson(HttpServletRequest request) {
		
		String search_key = request.getParameter("name_key");
		JSONObject jsonObj = new JSONObject();
		
		if (StringUtils.isNullOrEmpty(search_key)) {
			jsonObj.put("ret_code", -1);
			jsonObj.put("ret_message", "搜索关键字为空");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		List<Map<String, Object>> results = epUserService.fuzzyFindEpUserAndEmUserByName(search_key.trim());
		
		if (results == null) {
			jsonObj.put("ret_code", -2);
			jsonObj.put("ret_message", "搜索出现异常");
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		JSONArray jsonArr = new JSONArray();
		
		if(results.size() == 0) {
			jsonObj.put("ret_code", 0);
			jsonObj.put("ret_message", "搜索结果为空");
			jsonObj.put("match_users", jsonArr);
			jsonObj.put("match_count", 0);
			return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
		}
		
		for (Map<String, Object> res : results) {
			JSONObject json_tmp = new JSONObject();
			
			String name = (String) res.get("name");
			String work_no = (String) res.get("work_no");
			String em_username = (String) res.get("em_username");
			
			json_tmp.put("name", name);
			json_tmp.put("work_no", work_no);
			json_tmp.put("em_username", em_username);
			
			jsonArr.add(json_tmp);
		}
		
		jsonObj.put("ret_code", 1);
		jsonObj.put("ret_message", "搜索成功");
		jsonObj.put("match_users", jsonArr);
		jsonObj.put("match_count", results.size());
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
	}
	
	@RequestMapping(params = "changePassword", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String changePasswordJson(HttpServletRequest request) {

		String user_code = request.getParameter("user_code");
		String old_pwd_encrypted = request.getParameter("old_pwd"); // 旧密码和下面的新密码都要用RSA加密处理
		String new_pwd_encrypted = request.getParameter("new_pwd");
		String platform = request.getParameter("platform");	// 区分iOS和Android两个平台的参数，必填
		String plain_flag = request.getParameter("plain_flag_abc"); // 临时用于判断是否启用明文传递密码参数的标志，如果其值为“on”，则表示要启用；否则，不启用。
		String plain_old_pwd = request.getParameter("plain_old_pwd_abc"); // 临时用于接收安卓端明文传入的旧密码字段
		String plain_new_pwd = request.getParameter("plain_new_pwd_abc"); // 临时用于接收安卓端明文传入的新密码字段
		JSONObject jsonObj = new JSONObject();

		if (StringUtils.isNullOrEmpty(plain_flag)) {
			if (StringUtils.isNullOrEmpty(user_code)
					|| StringUtils.isNullOrEmpty(old_pwd_encrypted)
					|| StringUtils.isNullOrEmpty(new_pwd_encrypted) || StringUtils.isNullOrEmpty(platform)) {
				jsonObj.put("ret_code", 0);
				jsonObj.put("ret_message", "参数不能为空!");

				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// 如果新旧密码相同，则直接返回，无须进行修改密码操作
			if (old_pwd_encrypted.equals(new_pwd_encrypted)) {
				jsonObj.put("ret_code", -1);
				jsonObj.put("ret_message", "新旧密码相同");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// RSA解密新旧密码参数
			// 对于传入的参数，采用的解密方案：前台使用RSA公钥加密密码参数，后台接收到后使用RSA私钥解密密码参数
			// 私钥解密过程
			String filepath = RSAEncrypt.KEYS_FILE_PATH;
			byte[] res1, res2;
			String old_pwd, new_pwd;
			
			if("iOS".equalsIgnoreCase(platform)) {
				try {
					res1 = RSAEncrypt.decrypt(RSAEncrypt
							.loadPrivateKeyByStr(RSAEncrypt
									.loadPrivateKeyByFile(filepath)), Base64
									.decode(old_pwd_encrypted));
					res2 = RSAEncrypt.decrypt(RSAEncrypt
							.loadPrivateKeyByStr(RSAEncrypt
									.loadPrivateKeyByFile(filepath)), Base64
									.decode(new_pwd_encrypted));
					old_pwd = new String(res1);
					new_pwd = new String(res2);
				} catch (Exception e) {
					jsonObj.put("ret_code", -2);
					jsonObj.put("ret_message", "[iOS] 解密出现异常！");
					return JSONObject.toJSONString(jsonObj,
							SerializerFeature.WriteMapNullValue);
				}
			} else if("Android".equalsIgnoreCase(platform)) {
				try {
					res1 = RSAEncrypt.decryptAndroid(RSAEncrypt
							.loadPrivateKeyByStr(RSAEncrypt
									.loadPrivateKeyByFile(filepath)), Base64
									.decode(old_pwd_encrypted));
					res2 = RSAEncrypt.decryptAndroid(RSAEncrypt
							.loadPrivateKeyByStr(RSAEncrypt
									.loadPrivateKeyByFile(filepath)), Base64
									.decode(new_pwd_encrypted));
					old_pwd = new String(res1);
					new_pwd = new String(res2);
				} catch (Exception e) {
					jsonObj.put("ret_code", -2);
					jsonObj.put("ret_message", "[Android] 解密出现异常！");
					return JSONObject.toJSONString(jsonObj,
							SerializerFeature.WriteMapNullValue);
				}
			} else {
				jsonObj.put("ret_code", -8);
				jsonObj.put("ret_message", "未知的平台参数异常");
				return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
			}
			
			/** add by zhangshun the start */
			Pattern emailPattern = Pattern.compile(emailRegExp);
			Matcher emailMatcher = emailPattern.matcher(user_code);

			Pattern mobilePhonePattern = Pattern.compile(mobileRegExp);
			Matcher mobilePhoneMatcher = mobilePhonePattern.matcher(user_code);

			EpUser epu = null;
			if (emailMatcher.matches()) {// 邮箱登录
				epu = epUserService.getEpUserByEmail(user_code);
			} else if (mobilePhoneMatcher.matches()) {// 手机号码登录
				epu = epUserService.getEpUserByMobileNum(user_code);
			} else if (org.apache.commons.lang3.StringUtils
					.isNumeric(user_code)) {// 工号登录
				epu = epUserService.getEpUserByWorkNum(user_code);
			}
			/** add by zhangshun the end */

			if (epu == null) {
				jsonObj.put("ret_code", -3);
				jsonObj.put("ret_message", "不存在该用户!");

				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// 先修改公司系统用户密码，然后修改对应的环信用户密码，并将修改结果保存到数据库
			// 1. 修改公司系统用户密码
			// 1.1 首先，验证旧密码是否正确
			String old_pwd_md5 = CryptionUtil.md5Hex(old_pwd);
			if (!old_pwd_md5.equals(epu.getPwd())) { // 如果所提供的旧密码不正确
				jsonObj.put("ret_code", -4);
				jsonObj.put("ret_message", "旧密码错误");

				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// 1.2 验证旧密码通过后，开始用新密码修改密码
			int code = 999;
			String message = "";
			if (epu.getEm_uid() > 0) {
				EasemobRestAPIFactory factory = context.init(
						ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
				IMUserAPI user_api = (IMUserAPI) factory
						.newInstance(EasemobRestAPIFactory.USER_CLASS);

				EmUser emu = emUserService.getEmUserById(epu.getEm_uid());
				// 然后修改环信用户密码
				String new_pwd_md5_ = new Md5Hash(new_pwd).toString();
				BodyWrapper pwd_body = new ResetPasswordBody(new_pwd_md5_);
				ResponseWrapper resw = (ResponseWrapper) user_api.modifyIMUserPasswordWithAdminToken(
								emu.getUsername(), pwd_body);
				if (resw.getResponseStatus() == 200) { // 当返回状态码为200时，表示调用修改环信用户密码的接口成功，也即是修改环信用户密码的操作成功！
					// 接着更新后台数据库中em_user表中存放着的环信用户密码信息
					EmUser _emu = new EmUser();
					_emu.setId(emu.getId());
					_emu.setPassword(CryptionUtil.encryption(new_pwd_md5_,ENCRYP_KEY));
					int ret = emUserService.modifyEmUserById(_emu);
					if (ret > 0) {
						String new_pwd_md5 = CryptionUtil.md5Hex(new_pwd);
						EpUser _epu = new EpUser();
						_epu.setId(epu.getId());
						_epu.setMima(new_pwd);
						_epu.setPwd(new_pwd_md5);
						int result = epUserService.modifyEpUserById(_epu);// 修改公司用户密码
						if (result > 0) {
							code = 1;
							message = "修改用户密码成功";
						}else{
							code = -7;
							message = "修改e-platfrom密码失败!";
							//TODO:不知道要不要把em_user表里的密码和环信的密码改回来
						}
					}else{		//修改本地数据库对应的密码失败，则尝试把环信的密码修改回来
						String old_pwd_md5_ = new Md5Hash(old_pwd).toString();
						BodyWrapper old_pwd_body = new ResetPasswordBody(old_pwd_md5_);
						ResponseWrapper old_resw = (ResponseWrapper) user_api
								.modifyIMUserPasswordWithAdminToken(
										emu.getUsername(), old_pwd_body);
						if(old_resw.getResponseStatus() ==200){
							code = -5;
							message = "修改密码失败!";
						}else{
							code = -6;
							message = "修改密码失败，但是环信密码已被修改!";
						}
					}
				}else{
					code = -5;
					message = "修改密码失败!";
				}
			}else{
				String new_pwd_md5 = CryptionUtil.md5Hex(new_pwd);
				EpUser _epu = new EpUser();
				_epu.setId(epu.getId());
				_epu.setMima(new_pwd);
				_epu.setPwd(new_pwd_md5);
				int result = epUserService.modifyEpUserById(_epu);//修改公司用户密码
				if (result > 0) {
					code = 1;
					message = "修改用户密码成功";
				}else{
					code = -5;
					message = "修改密码失败!";
				}
			}
			
			jsonObj.put("ret_code", code);
			jsonObj.put("ret_message", message);
			return JSONObject.toJSONString(jsonObj,
					SerializerFeature.WriteMapNullValue);

		} else if ("on".equals(plain_flag)) {

			if (StringUtils.isNullOrEmpty(user_code)
					|| StringUtils.isNullOrEmpty(plain_old_pwd)
					|| StringUtils.isNullOrEmpty(plain_new_pwd)) {
				jsonObj.put("ret_code", 0);
				jsonObj.put("ret_message", "参数不能为空!");

				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}
			
			// 如果新旧密码相同，则直接返回，无须进行修改密码操作
			if (plain_old_pwd.equals(plain_new_pwd)) {
				jsonObj.put("ret_code", -1);
				jsonObj.put("ret_message", "新旧密码相同");
				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			String old_pwd, new_pwd;
			// 不经过RSA解密，直接使用明文新旧密码
			old_pwd = plain_old_pwd;
			new_pwd = plain_new_pwd;

			/** add by zhangshun the start */
			Pattern emailPattern = Pattern.compile(emailRegExp);
			Matcher emailMatcher = emailPattern.matcher(user_code);

			Pattern mobilePhonePattern = Pattern.compile(mobileRegExp);
			Matcher mobilePhoneMatcher = mobilePhonePattern.matcher(user_code);

			EpUser epu = null;
			if (emailMatcher.matches()) {// 邮箱登录
				epu = epUserService.getEpUserByEmail(user_code);
			} else if (mobilePhoneMatcher.matches()) {// 手机号码登录
				epu = epUserService.getEpUserByMobileNum(user_code);
			} else if (org.apache.commons.lang3.StringUtils
					.isNumeric(user_code)) {// 工号登录
				epu = epUserService.getEpUserByWorkNum(user_code);
			}
			/** add by zhangshun the end */

			if (epu == null) {
				jsonObj.put("ret_code", -3);
				jsonObj.put("ret_message", "不存在该用户!");

				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// 先修改公司系统用户密码，然后修改对应的环信用户密码，并将修改结果保存到数据库
			// 1. 修改公司系统用户密码
			// 1.1 首先，验证旧密码是否正确
			String old_pwd_md5 = CryptionUtil.md5Hex(old_pwd);
			if (!old_pwd_md5.equals(epu.getPwd())) { // 如果所提供的旧密码不正确
				jsonObj.put("ret_code", -4);
				jsonObj.put("ret_message", "旧密码错误");

				return JSONObject.toJSONString(jsonObj,
						SerializerFeature.WriteMapNullValue);
			}

			// 1.2 验证旧密码通过后，开始用新密码修改密码
			int code = 999;
			String message = "";
			if (epu.getEm_uid() > 0) {
				EasemobRestAPIFactory factory = context.init(
						ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
				IMUserAPI user_api = (IMUserAPI) factory
						.newInstance(EasemobRestAPIFactory.USER_CLASS);

				EmUser emu = emUserService.getEmUserById(epu.getEm_uid());
				// 然后修改环信用户密码
				String new_pwd_md5_ = new Md5Hash(new_pwd).toString();
				BodyWrapper pwd_body = new ResetPasswordBody(new_pwd_md5_);
				ResponseWrapper resw = (ResponseWrapper) user_api.modifyIMUserPasswordWithAdminToken(
								emu.getUsername(), pwd_body);
				if (resw.getResponseStatus() == 200) { // 当返回状态码为200时，表示调用修改环信用户密码的接口成功，也即是修改环信用户密码的操作成功！
					// 接着更新后台数据库中em_user表中存放着的环信用户密码信息
					EmUser _emu = new EmUser();
					_emu.setId(emu.getId());
					_emu.setPassword(CryptionUtil.encryption(new_pwd_md5_,ENCRYP_KEY));
					int ret = emUserService.modifyEmUserById(_emu);
					if (ret > 0) {
						String new_pwd_md5 = CryptionUtil.md5Hex(new_pwd);
						EpUser _epu = new EpUser();
						_epu.setId(epu.getId());
						_epu.setMima(new_pwd);
						_epu.setPwd(new_pwd_md5);
						int result = epUserService.modifyEpUserById(_epu);// 修改公司用户密码
						if (result > 0) {
							code = 1;
							message = "修改用户密码成功";
						}else{
							code = -7;
							message = "修改e-platfrom密码失败!";
							//TODO:不知道要不要把em_user表里的密码和环信的密码改回来
						}
					}else{		//修改本地数据库对应的密码失败，则尝试把环信的密码修改回来
						String old_pwd_md5_ = new Md5Hash(old_pwd).toString();
						BodyWrapper old_pwd_body = new ResetPasswordBody(old_pwd_md5_);
						ResponseWrapper old_resw = (ResponseWrapper) user_api
								.modifyIMUserPasswordWithAdminToken(
										emu.getUsername(), old_pwd_body);
						if(old_resw.getResponseStatus() ==200){
							code = -5;
							message = "修改密码失败!";
						}else{
							code = -6;
							message = "修改密码失败，但是环信密码已被修改!";
						}
					}
				}else{
					code = -5;
					message = "修改密码失败!";
				}
			}else{
				String new_pwd_md5 = CryptionUtil.md5Hex(new_pwd);
				EpUser _epu = new EpUser();
				_epu.setId(epu.getId());
				_epu.setMima(new_pwd);
				_epu.setPwd(new_pwd_md5);
				int result = epUserService.modifyEpUserById(_epu);//修改公司用户密码
				if (result > 0) {
					code = 1;
					message = "修改用户密码成功";
				}else{
					code = -5;
					message = "修改密码失败!";
				}
			}
			
			jsonObj.put("ret_code", code);
			jsonObj.put("ret_message", message);
			return JSONObject.toJSONString(jsonObj,
					SerializerFeature.WriteMapNullValue);
		} else {
			jsonObj.put("ret_code", -7);
			jsonObj.put("ret_message", "未做处理的plain_flag参数值");

			return JSONObject.toJSONString(jsonObj,
					SerializerFeature.WriteMapNullValue);
		}
	}

	/**
	 * 根据用户工号查询一部分用户信息(非全部)
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "simpleUser", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getSimpleEpUserByWorkNum(HttpServletRequest request) {
		String workNum = request.getParameter("work_no");
		JSONObject object = new JSONObject();
		if (StringUtils.isNullOrEmpty(workNum)) {
			object.put("ret_code", -1);
			object.put("ret_message", "工号为空!");
		} else {
			EpUser epUser = epUserService.getSimpleEpUserByWorkNum(workNum);
			if (epUser == null) {
				object.put("ret_code", -2);
				object.put("ret_message", "用户不存在!");
			} else {
				object.put("ret_code", 1);
				object.put("ret_message", "请求成功!");
				JSONObject userObj = new JSONObject();
				userObj.put("name", epUser.getName());
				userObj.put("email", epUser.getEmail());
				userObj.put("work_no", epUser.getWork_no());
				if(StringUtils.isNullOrEmpty(epUser.getMobile_phone())||"<NONE>".equals(epUser.getMobile_phone())){
					userObj.put("mobile_phone", "");
				}else{
					userObj.put("mobile_phone", epUser.getMobile_phone());
				}
				userObj.put("dept_name", epUser.getDept_name());
				userObj.put("project_name", epUser.getProject_name());
				object.put("ep_user_info", userObj);
			}
		}
		return JSONObject.toJSONString(object,
				SerializerFeature.WriteMapNullValue);
	}
	
	/**
	 * 根据用户邮箱重设密码
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "resetPassword", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String resetPasswordByEmail(HttpServletRequest request) {
		String email = request.getParameter("email");
		String ver_code = request.getParameter("ver_code");
		String requestPwd = request.getParameter("pwd");
		String platform = request.getParameter("platform");	// 区分iOS或Android平台的参数
		String plainPwd = request.getParameter("plain_pwd_abc");
		String pwd = "";
		JSONObject object = new JSONObject();

		if (!StringUtils.isNullOrEmpty(plainPwd)) {
			pwd = plainPwd;
		} else if (!StringUtils.isNullOrEmpty(requestPwd)) {
			if(StringUtils.isNullOrEmpty(platform)) {
				object.put("ret_code", -9);
				object.put("ret_message", "平台参数为空");
				return JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue);
			}
			
			String filepath = RSAEncrypt.KEYS_FILE_PATH;
			byte[] res;
			
			if("iOS".equalsIgnoreCase(platform)) {
				try {
					res = RSAEncrypt.decrypt(RSAEncrypt
							.loadPrivateKeyByStr(RSAEncrypt
									.loadPrivateKeyByFile(filepath)), Base64
									.decode(requestPwd));
					pwd = new String(res);
				} catch (Exception e) {
					e.printStackTrace();
					object.put("ret_code", -7);
					object.put("ret_message", "[iOS] 解密出现异常！");
					return JSONObject.toJSONString(object,
							SerializerFeature.WriteMapNullValue);
				}
			} else if("Android".equalsIgnoreCase(platform)) {
				try {
					res = RSAEncrypt.decryptAndroid(RSAEncrypt
							.loadPrivateKeyByStr(RSAEncrypt
									.loadPrivateKeyByFile(filepath)), Base64
									.decode(requestPwd));
					pwd = new String(res);
				} catch (Exception e) {
					e.printStackTrace();
					object.put("ret_code", -7);
					object.put("ret_message", "[Android] 解密出现异常！");
					return JSONObject.toJSONString(object,
							SerializerFeature.WriteMapNullValue);
				}
			} else {
				object.put("ret_code", -10);
				object.put("ret_message", "未识别的平台参数");
				return JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue);
			}
			
		}

		if (StringUtils.isNullOrEmpty(email)) {
			object.put("ret_code", -1);
			object.put("ret_message", "邮箱为空!");
		} else {
			EpUser epUser = epUserService.getVerCodeEpUserByEmail(email);
			if (epUser == null) {
				object.put("ret_code", -2);
				object.put("ret_message", "用户不存在!");
			} else {
				if (StringUtils.isNullOrEmpty(ver_code)) {
					object.put("ret_code", -3);
					object.put("ret_message", "请传验证码!");
				} else {
					if (ver_code.equals(epUser.getVer_code())) {
						Date time = epUser.getVercode_time();
						if (isVerCodeValid(time)) {
							if (epUser.getEm_uid() !=null && epUser.getEm_uid() > 0) {
								EasemobRestAPIFactory factory = context.init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
								IMUserAPI user_api = (IMUserAPI) factory.newInstance(EasemobRestAPIFactory.USER_CLASS);

								EmUser emu = emUserService.getEmUserById(epUser.getEm_uid());
								// 然后修改环信用户密码
								String new_pwd_md5_ = new Md5Hash(pwd).toString();
								BodyWrapper pwd_body = new ResetPasswordBody(new_pwd_md5_);
								ResponseWrapper resw = (ResponseWrapper) user_api.modifyIMUserPasswordWithAdminToken(emu.getUsername(), pwd_body);
								if (resw.getResponseStatus() == 200) { // 当返回状态码为200时，表示调用修改环信用户密码的接口成功，也即是修改环信用户密码的操作成功！
									// 接着更新后台数据库中em_user表中存放着的环信用户密码信息
									EmUser _emu = new EmUser();
									_emu.setId(emu.getId());
									_emu.setPassword(CryptionUtil.encryption(new_pwd_md5_, ENCRYP_KEY));
									int ret = emUserService.modifyEmUserById(_emu);
									if (ret > 0) {
										String md5Pwd = CryptionUtil.md5Hex(pwd);
										epUser.setPwd(md5Pwd);
										epUser.setMima(pwd);
										epUser.setVer_code("");
										epUser.setVercode_time(null);
										epUser.setEmail(email);
										int isSuccess = epUserService.updateResetPwdByEmail(epUser);
										if (isSuccess > 0) {
											object.put("ret_code", 1);
											object.put("ret_message", "重设密码成功!");
										} else {
											object.put("ret_code", -8);
											object.put("ret_message","重设密码失败,但是环信密码已被修改!");
											// TODO:此时环信密码以及环信表里的数据已经被改了，不知道要不要改回来
										}
									} else { // 修改本地数据库对应的密码失败，则尝试把环信的密码修改回来
										object.put("ret_code", -8);
										object.put("ret_message","重设密码失败,但是环信密码已被修改!");
										// TODO:此时环信密码已经被改了，不知道要不要改回来
									}
								} else {
									object.put("ret_code", -6);
									object.put("ret_message", "重设密码失败,请稍后再试!");
								}
							} else {
								String md5Pwd = CryptionUtil.md5Hex(pwd);
								epUser.setPwd(md5Pwd);
								epUser.setMima(pwd);
								epUser.setVer_code("");
								epUser.setVercode_time(null);
								epUser.setEmail(email);
								int isSuccess = epUserService.updateResetPwdByEmail(epUser);
								if (isSuccess > 0) {
									object.put("ret_code", 1);
									object.put("ret_message", "重设密码成功!");
								} else {
									object.put("ret_code", -6);
									object.put("ret_message", "重设密码失败,请稍后再试!");
								}
							}
						} else {
							object.put("ret_code", -5);
							object.put("ret_message", "验证码已过期,请重新获取!");
						}
					} else {
						object.put("ret_code", -4);
						object.put("ret_message", "验证码不正确!");
					}
				}
			}
		}
		return JSONObject.toJSONString(object,
				SerializerFeature.WriteMapNullValue);
	}
	
	/**
	 * 验证码是否有效
	 * 
	 * @param createDate
	 *            验证码生成日期(目前的有效期是一天)
	 * @return
	 */
	public boolean isVerCodeValid(Date createDate) {
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(createDate);
		long createTime = calendar.getTimeInMillis();
		calendar.setTime(new Date());
		long nowTime = calendar.getTimeInMillis();
		if (nowTime - createTime > 24 * 60 * 60 * 1000) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 生成验证码并发送到对应的邮箱
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getVerificationCode", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String createVerificationCode(HttpServletRequest request) {
		String email = request.getParameter("email");
		JSONObject object = new JSONObject();
		if (StringUtils.isNullOrEmpty(email)) {
			object.put("ret_code", -1);
			object.put("ret_message", "邮箱为空!");
		} else {
			EpUser epUser = epUserService.getEpUserByEmail(email);
			if (epUser == null) {
				object.put("ret_code", -2);
				object.put("ret_message", "用户不存在!");
			} else {
				String verCode = CreatePasswordHelper.createPassword(6);
				int isSuccess = epUserService.updateVercodeById(epUser.getId(), verCode, new Date());
				if(isSuccess>0){
					object.put("ret_code", 1);
					object.put("ret_message", "验证码已发送至Elead邮箱!");
					MailUtil.sendEmail("E-Platform的验证码", "验证码是:      " + verCode, new String[] { epUser.getEmail() });
				}else{
					object.put("ret_code", -3);
					object.put("ret_message", "生成验证码失败,请稍后再试!");
				}
			}
		}
		return JSONObject.toJSONString(object,
				SerializerFeature.WriteMapNullValue);
	}
	
	@RequestMapping(params = "isNewUser", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String wetherIsNewUser(HttpServletRequest request) {
		String email = request.getParameter("email");
		JSONObject object = new JSONObject();
		if (StringUtils.isNullOrEmpty(email)) {
			object.put("ret_code", -1);
			object.put("ret_message", "邮箱为空!");
		} else {
			EpUser epUser = epUserService.getEpUserByEmail(email);
			if (epUser == null) {
				object.put("ret_code", -2);
				object.put("ret_message", "用户不存在!");
			} else {
				String pwd = epUser.getPwd();
				String origin_pwd = epUser.getOrigin_pwd();
				if (!StringUtils.isNullOrEmpty(pwd) && pwd.equals(origin_pwd)) {
					object.put("ret_code", 1);
					object.put("ret_message", "新用户!");
				} else {
					object.put("ret_code", 2);
					object.put("ret_message", "旧用户!");
				}
			}
		}

		return JSONObject.toJSONString(object,SerializerFeature.WriteMapNullValue);
	}
}
