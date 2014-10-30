package com.taobao.zeus.store.mysql.tool;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.GroupDescriptor;

public class GroupValidate {
	public static boolean valide(GroupDescriptor group) throws ZeusException{
		if(group.getName()==null || group.getName().trim().equals("")){
			throw new ZeusException("name字段不能为空");
		}
		
		return true;
	}
}
