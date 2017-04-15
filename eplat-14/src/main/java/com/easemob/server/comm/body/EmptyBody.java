package com.easemob.server.comm.body;

import com.easemob.server.comm.wrapper.BodyWrapper;
import com.fasterxml.jackson.databind.node.ContainerNode;

public class EmptyBody implements BodyWrapper {

	@Override
	public ContainerNode<?> getBody() {
		return null;
	}

	@Override
	public Boolean validate() {
		return true;
	}

}
