package com.taobao.zeus.web.platform.client.util;

import com.google.gwt.core.shared.GWT;
import com.taobao.zeus.web.platform.shared.rpc.FileManagerService;
import com.taobao.zeus.web.platform.shared.rpc.FileManagerServiceAsync;
import com.taobao.zeus.web.platform.shared.rpc.GroupService;
import com.taobao.zeus.web.platform.shared.rpc.GroupServiceAsync;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugService;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugServiceAsync;
import com.taobao.zeus.web.platform.shared.rpc.JobService;
import com.taobao.zeus.web.platform.shared.rpc.JobServiceAsync;
import com.taobao.zeus.web.platform.shared.rpc.ProfileManagerService;
import com.taobao.zeus.web.platform.shared.rpc.ProfileManagerServiceAsync;
import com.taobao.zeus.web.platform.shared.rpc.ReportService;
import com.taobao.zeus.web.platform.shared.rpc.ReportServiceAsync;
import com.taobao.zeus.web.platform.shared.rpc.TableManagerService;
import com.taobao.zeus.web.platform.shared.rpc.TableManagerServiceAsync;
import com.taobao.zeus.web.platform.shared.rpc.TreeService;
import com.taobao.zeus.web.platform.shared.rpc.TreeServiceAsync;
import com.taobao.zeus.web.platform.shared.rpc.UserService;
import com.taobao.zeus.web.platform.shared.rpc.UserServiceAsync;

public class RPCS {

	public static ProfileManagerServiceAsync profileService=GWT.create(ProfileManagerService.class);
	
	private static JobServiceAsync jobService=GWT.create(JobService.class);
	
	private static GroupServiceAsync groupService=GWT.create(GroupService.class);
	
	private static UserServiceAsync userService=GWT.create(UserService.class);
	
	private static TreeServiceAsync treeService=GWT.create(TreeService.class);
	
	private static ReportServiceAsync reportService=GWT.create(ReportService.class);
	
	private static TableManagerServiceAsync tableManagerService=GWT.create(TableManagerService.class);
	
	private static FileManagerServiceAsync fileManagerService=GWT.create(FileManagerService.class);
	
	private static JobDebugServiceAsync jobDebugService = GWT
			.create(JobDebugService.class);
	
	public static FileManagerServiceAsync getFileManagerService(){
		return fileManagerService;
	}
	
	public static TableManagerServiceAsync getTableManagerService() {
		return tableManagerService;
	}

	public static ReportServiceAsync getReportService(){
		return reportService;
	}
	
	public static ProfileManagerServiceAsync getProfileService(){
		return profileService;
	}

	public static JobServiceAsync getJobService() {
		return jobService;
	}

	public static GroupServiceAsync getGroupService() {
		return groupService;
	}

	public static UserServiceAsync getUserService() {
		return userService;
	}

	public static TreeServiceAsync getTreeService() {
		return treeService;
	}

	public static JobDebugServiceAsync getJobDebugService() {
		return jobDebugService;
	}
	
}
