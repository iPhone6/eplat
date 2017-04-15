package com.easemob.server.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shiro.crypto.hash.Md5Hash;

import com.cn.eplat.utils.CryptionUtil;
import com.easemob.server.api.*;
import com.easemob.server.comm.wrapper.BodyWrapper;
import com.easemob.server.comm.ClientContext;
import com.easemob.server.comm.EasemobRestAPIFactory;
import com.easemob.server.comm.body.AuthPwdTokenBody;
import com.easemob.server.comm.body.IMUserBody;
import com.easemob.server.comm.body.IMUsersBody;
import com.easemob.server.comm.body.ResetPasswordBody;
//import com.easemob.server.comm.body.IMUsersBody;
import com.easemob.server.comm.wrapper.ResponseWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.jdbc.StringUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		EasemobRestAPIFactory factory = ClientContext.getInstance().init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
		
		IMUserAPI user = (IMUserAPI)factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
//		ChatMessageAPI chat = (ChatMessageAPI)factory.newInstance(EasemobRestAPIFactory.MESSAGE_CLASS);
//		FileAPI file = (FileAPI)factory.newInstance(EasemobRestAPIFactory.FILE_CLASS);
//		SendMessageAPI message = (SendMessageAPI)factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);
//		ChatGroupAPI chatgroup = (ChatGroupAPI)factory.newInstance(EasemobRestAPIFactory.CHATGROUP_CLASS);
//		ChatRoomAPI chatroom = (ChatRoomAPI)factory.newInstance(EasemobRestAPIFactory.CHATROOM_CLASS);

		/*
        ResponseWrapper fileResponse = (ResponseWrapper) file.uploadFile(new File("s:/S05/logo.png"));
        String uuid = ((ObjectNode) fileResponse.getResponseBody()).get("entities").get(0).get("uuid").asText();
        String shareSecret = ((ObjectNode) fileResponse.getResponseBody()).get("entities").get(0).get("share-secret").asText();
        InputStream in = (InputStream) ((ResponseWrapper) file.downloadFile(uuid, shareSecret, false)).getResponseBody();
        FileOutputStream fos = new FileOutputStream("s:/S05/logo1.png");
        byte[] buffer = new byte[1024];
        int len1 = 0;
        while ((len1 = in.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);
        }
        fos.close();
        */
        
		/*
		// login a IM user
		String username = "c3005";
		String userpwd = new Md5Hash("123456").toString();
//		userpwd = CryptionUtil.md5Hex("123");
		BodyWrapper pwd_body = new AuthPwdTokenBody(username, userpwd);
		ResponseWrapper login_response = (ResponseWrapper) user.loginIMUser(pwd_body);
		System.out.println("longin response:");
		System.out.println(login_response);
		System.out.println("------------");
		System.out.println(login_response.getResponseBody().getClass());
		ObjectNode resbd = (ObjectNode) login_response.getResponseBody();
		System.out.println("resbd = " + resbd);
		System.out.println("access_token = " + resbd.get("access_token"));
		System.out.println("user node value's type = " + resbd.get("user").getClass());
		System.out.println("user = " + resbd.get("user"));
		*/
		
		/*
		// Create a IM user
//		BodyWrapper userBody = new IMUserBody("User004", "123456", "HelloWorld");
		BodyWrapper userBody = new IMUserBody("us09", "123", "us09nick");
		ResponseWrapper rw = (ResponseWrapper) user.createNewIMUserSingle(userBody);
		System.out.println(" rw = " + rw);
		*/
		
		/*
		// 修改环信用户密码
		String em_name = "us09";	// 环信用户的用户名（username）
		String new_pwd = "1234";	// 将要修改的新密码
		
		BodyWrapper pwd_body = new ResetPasswordBody(new Md5Hash(new_pwd).toString());
		ResponseWrapper resw = (ResponseWrapper) user.modifyIMUserPasswordWithAdminToken(em_name, pwd_body);
		if(resw.getResponseStatus() == 200) {
			System.out.println("修改环信用户密码成功！");
		}
		*/
		
		
		
		/*
		// Create some IM users
		List<IMUserBody> users = new ArrayList<IMUserBody>();
		users.add(new IMUserBody("User002", "123456", null));
		users.add(new IMUserBody("User003", "123456", null));
		BodyWrapper usersBody = new IMUsersBody(users);
		user.createNewIMUserBatch(usersBody);
		
		// Get a IM user
		user.getIMUsersByUserName("User001");
		
		// Get a fake user
		user.getIMUsersByUserName("FakeUser001");
		*/
		// Get 12 users
		user.getIMUsersBatch(21l, null);
		
		
		/*
		Long time_mills = 1482462088926L;
		Date date = new Date(time_mills);
		System.out.println(date);
		*/
		
//		String flag = "";
//		System.out.println(StringUtils.isNullOrEmpty(flag));
		
	}

}
