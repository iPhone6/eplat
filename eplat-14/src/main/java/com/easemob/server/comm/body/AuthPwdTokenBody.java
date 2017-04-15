package com.easemob.server.comm.body;

import org.apache.commons.lang3.StringUtils;

import com.easemob.server.comm.wrapper.BodyWrapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class AuthPwdTokenBody implements BodyWrapper {
	
	private String grantType = "password";
	
	private String username;
	
	private String password;

	public AuthPwdTokenBody(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ContainerNode<?> getBody() {
		return JsonNodeFactory.instance.objectNode().put("grant_type", grantType).put("username", username).put("password", password);
	}

	public Boolean validate() {
		return StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
	}

}
