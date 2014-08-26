package com.taobao.zeus.web.platform.server.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.GroupDescriptor;
import com.taobao.zeus.model.ZeusFollow;
import com.taobao.zeus.store.FollowManager;
import com.taobao.zeus.store.GroupBean;
import com.taobao.zeus.store.PermissionManager;
import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.web.LoginUser;
import com.taobao.zeus.web.PermissionGroupManager;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.util.GwtException;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.shared.rpc.GroupService;

public class GroupServiceImpl implements GroupService{
	private static Logger log=LoggerFactory.getLogger(GroupServiceImpl.class);
	@Autowired
	private PermissionGroupManager permissionGroupManager;
	@Autowired
	private FollowManager followManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private PermissionManager permissionManager;
	@Override
	public String createGroup(String groupName, String parentGroupId,
			boolean isDirectory) throws GwtException {
		try {
			GroupDescriptor gd=permissionGroupManager.createGroup(LoginUser.getUser().getUid(), groupName, parentGroupId, isDirectory);
			return gd.getId();
		} catch (ZeusException e) {
			throw new GwtException(e.getMessage());
		}
	}

	@Override
	public void deleteGroup(String groupId) throws GwtException {
		try {
			permissionGroupManager.deleteGroup(LoginUser.getUser().getUid(), groupId);
		} catch (ZeusException e) {
			throw new GwtException(e.getMessage());
		}
	}

	@Override
	public GroupModel getGroup(String groupId) throws GwtException {
		GroupDescriptor gd=permissionGroupManager.getGroupDescriptor(groupId);
		GroupModel model=new GroupModel();
		model.setLocalResources(gd.getResources());
		model.setLocalProperties(gd.getProperties());
		model.setDesc(gd.getDesc());
		model.setDirectory(gd.isDirectory());
		model.setId(gd.getId());
		model.setName(gd.getName());
		model.setOwner(gd.getOwner());
		model.setParent(gd.getParent());
		model.setAdmin(permissionGroupManager.hasGroupPermission(LoginUser.getUser().getUid(), groupId));
		List<ZeusFollow> follows=followManager.findGroupFollowers(Arrays.asList(groupId));
		if(follows!=null){
			List<String> followsName=new ArrayList<String>();
			for(ZeusFollow zf:follows){
				String name=userManager.findByUid(zf.getUid()).getName();
				if(name==null){
					name=zf.getUid();
				}
				followsName.add(name);
			}
			model.setFollows(followsName);
		}
		return model;
	}
	
	
	public GroupModel getUpstreamGroup(String groupId) throws GwtException{
		GroupBean bean=permissionGroupManager.getUpstreamGroupBean(groupId);
		GroupDescriptor gd=bean.getGroupDescriptor();
		GroupModel model=new GroupModel();
		model.setParent(bean.getParentGroupBean()==null?null:bean.getParentGroupBean().getGroupDescriptor().getId());
		model.setLocalResources(gd.getResources());
		model.setAllResources(bean.getHierarchyResources());
		model.setLocalProperties(new HashMap<String, String>(gd.getProperties()));
		model.setDesc(gd.getDesc());
		model.setDirectory(gd.isDirectory());
		model.setId(gd.getId());
		model.setName(gd.getName());
		model.setOwner(gd.getOwner());
		String ownerName=userManager.findByUid(gd.getOwner()).getName();
		if(ownerName==null || "".equals(ownerName.trim()) || "null".equals(ownerName)){
			ownerName=gd.getOwner();
		}
		model.setOwnerName(ownerName);
		model.setParent(gd.getParent());
		model.setAllProperties(bean.getHierarchyProperties().getAllProperties());
		model.setAdmin(permissionGroupManager.hasGroupPermission(LoginUser.getUser().getUid(), groupId));
		List<ZeusFollow> follows=followManager.findGroupFollowers(Arrays.asList(groupId));
		if(follows!=null){
			List<String> followsName=new ArrayList<String>();
			for(ZeusFollow zf:follows){
				String name=userManager.findByUid(zf.getUid()).getName();
				if(name==null || "".equals(name.trim())){
					name=zf.getUid();
				}
				followsName.add(name);
			}
			model.setFollows(followsName);
		}
		
		List<String> ladmins=permissionManager.getGroupAdmins(bean.getGroupDescriptor().getId());
		List<String> admins=new ArrayList<String>();
		for(String s:ladmins){
			String name=userManager.findByUid(s).getName();
			if(name==null || "".equals(name.trim())){
				name=s;
			}
			admins.add(name);
		}
		model.setAdmins(admins);
		
		List<String> owners=new ArrayList<String>();
		owners.add(bean.getGroupDescriptor().getOwner());
		GroupBean parent=bean.getParentGroupBean();
		while(parent!=null){
			if(!owners.contains(parent.getGroupDescriptor().getOwner())){
				owners.add(parent.getGroupDescriptor().getOwner());
			}
			parent=parent.getParentGroupBean();
		}
		model.setOwners(owners);
		
		//所有secret. 开头的配置项都进行权限控制
		for(String key:model.getAllProperties().keySet()){
			boolean isLocal=model.getLocalProperties().get(key)==null?false:true;
			if(key.startsWith("secret.")){
				if(!isLocal){
					model.getAllProperties().put(key, "*");
				}else{
					if(!model.isAdmin() && !model.getOwner().equals(LoginUser.getUser().getUid())){
						model.getLocalProperties().put(key, "*");
					}
				}
			}
		}
		//本地配置项中的hadoop.hadoop.job.ugi 只有管理员和owner才能查看，继承配置项不能查看
		String SecretKey="hadoop.hadoop.job.ugi";
		if(model.getLocalProperties().containsKey(SecretKey)){
			String value=model.getLocalProperties().get(SecretKey);
			if(value.lastIndexOf("#")==-1){
				value="*";
			}else{
				value=value.substring(0, value.lastIndexOf("#"));
				value+="#*";
			}
			if(!model.isAdmin() && !model.getOwner().equals(LoginUser.getUser().getUid())){
				model.getLocalProperties().put(SecretKey, value);
			}
			model.getAllProperties().put(SecretKey, value);
		}else if(model.getAllProperties().containsKey(SecretKey)){
			String value=model.getAllProperties().get(SecretKey);
			if(value.lastIndexOf("#")==-1){
				value="*";
			}else{
				value=value.substring(0, value.lastIndexOf("#"));
				value+="#*";
			}
			model.getAllProperties().put(SecretKey, value);
		}
		return model;
	}



	@Override
	public void updateGroup(GroupModel group) throws GwtException {
		GroupDescriptor gd=new GroupDescriptor();
		gd.setResources(group.getLocalResources());
		gd.setDesc(group.getDesc());
		gd.setId(group.getId());
		gd.setName(group.getName());
		gd.setProperties(group.getLocalProperties());
		
		
		try {
			permissionGroupManager.updateGroup(LoginUser.getUser().getUid(), gd);
		} catch (ZeusException e) {
			throw new GwtException(e.getMessage());
		}
	}

	@Override
	public void addGroupAdmin(String groupId, String uid) throws GwtException {
		try {
			permissionGroupManager.addGroupAdmin(LoginUser.getUser().getUid(),uid, groupId);
		} catch (ZeusException e) {
			throw new GwtException(e.getMessage());
		}
	}

	@Override
	public List<ZUser> getGroupAdmins(String groupId) {
		List<ZeusUser> users= permissionGroupManager.getGroupAdmins(groupId);
		List<ZUser> result=new ArrayList<ZUser>();
		for(ZeusUser zu:users){
			ZUser z=new ZUser();
			z.setName(zu.getName());
			z.setUid(zu.getUid());
			result.add(z);
		}
		return result;
	}

	@Override
	public void removeGroupAdmin(String groupId, String uid)
			throws GwtException {
		try {
			permissionGroupManager.removeGroupAdmin(LoginUser.getUser().getUid(),uid, groupId);
		} catch (ZeusException e) {
			throw new GwtException(e.getMessage());
		}
	}

	@Override
	public void transferOwner(String groupId, String uid) throws GwtException {
		try {
			permissionGroupManager.grantGroupOwner(LoginUser.getUser().getUid(), uid, groupId);
		} catch (ZeusException e) {
			throw new GwtException(e.getMessage());
		}
	}

	@Override
	public void move(String groupId, String newParentGroupId)
			throws GwtException {
		try {
			permissionGroupManager.moveGroup(LoginUser.getUser().getUid(), groupId, newParentGroupId);
		} catch (ZeusException e) {
			log.error("move",e);
			throw new GwtException(e.getMessage());
		}
	}

}