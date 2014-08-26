package com.taobao.zeus.web.platform.client.util.filesystem;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.data.shared.TreeStore;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModelFactory;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModelProperties;

public class FSUtil {

	private static FileModelFactory dataFactory;

	public static FileModelFactory getDataFactory() {
		if (dataFactory == null) {
			dataFactory = GWT.create(FileModelFactory.class);
		}
		return dataFactory;
	}
	private static FileModelProperties fileModelProperties;
	public static FileModelProperties getFileModelProperties() {
		if (fileModelProperties == null) {
			fileModelProperties = GWT.create(FileModelProperties.class);
		}
		return fileModelProperties;
	}

	public static String getNextUntitledFileName(FileModel parentFileModel,
			boolean folder, String suffix, TreeStore<FileModel> store) {
		String nextUntitledFileName;
		List<FileModel> children;
		if (parentFileModel == null) {
			children = store.getRootItems();
		} else {
			children = store.getChildren(parentFileModel);
		}
		int index = 1;
		String fileNameTemplate =folder?"文件夹 ":"文件 ";
		do {
			nextUntitledFileName = fileNameTemplate + index;
			if (suffix != null && !"".equals(suffix)) {
				nextUntitledFileName += "." + suffix;
			}
			index++;
		} while (containsName(children, nextUntitledFileName));
		return nextUntitledFileName;
	}

	protected static boolean containsName(List<FileModel> children, String name) {
		for (FileModel fileModel : children) {
			if (name.equals(fileModel.getName())) {
				return true;
			}
		}
		return false;
	}
	
}
