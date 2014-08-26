package com.taobao.zeus.web.platform.client.util;

import com.taobao.zeus.web.platform.client.util.filesystem.FileSystem;

public class PlatformContext {

	private ZUser user;
	private final PlatformBus platformBus=new PlatformBus();
	private final FileSystem fileSystem=new FileSystem(this);

	public PlatformContext(ZUser user){
		this.user=user;
	}
	
	public PlatformBus getPlatformBus() {
		return platformBus;
	}
	public FileSystem getFileSystem() {
		return fileSystem;
	}

	public ZUser getUser() {
		return user;
	}
}
