package com.taobao.zeus.web.platform.client.module.jobmanager;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class TreeKeyProviderTool {
	private static final ModelKeyProvider<GroupJobTreeModel> modelKeyProvider=new ModelKeyProvider<GroupJobTreeModel>() {
		public String getKey(GroupJobTreeModel item) {
			if(item.isJob()){
				return "job-"+item.getId();
			}else{
				return "group-"+item.getId();
			}
		}
	};
	
	public static ModelKeyProvider<GroupJobTreeModel> getModelKeyProvider(){
		return modelKeyProvider;
	}
	
	public static String genGroupProviderKey(String groupId){
		return "group-"+groupId;
	}
	public static String genJobProviderKey(String jobId){
		return "job-"+jobId;
	}
	
	public static boolean parseIsJob(String providerKey){
		if(providerKey.startsWith("job-")){
			return true;
		}
		return false;
	}
	public static String parseId(String providerKey){
		return providerKey.substring(providerKey.indexOf("-")+1);
	}
}
