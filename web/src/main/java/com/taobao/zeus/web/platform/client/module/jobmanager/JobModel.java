package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, String> allProperties = new HashMap<String, String>();
	private Map<String, String> localProperties = new HashMap<String, String>();
	private String cronExpression;
	private List<String> dependencies;
	private String id;
	private String name;
	private String desc;
	private String groupId;
	private String owner;
	private List<String> owners;
	private String ownerName;
	private List<Map<String, String>> localResources = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> allResources = new ArrayList<Map<String, String>>();
	private String jobRunType;
	private String jobScheduleType;
	private Boolean auto;
	private String script;
	private List<String> preProcessers = new ArrayList<String>();
	private List<String> postProcessers = new ArrayList<String>();
	private Boolean admin;
	private List<String> admins = new ArrayList<String>();
	private List<String> follows = new ArrayList<String>();
	private List<String> importantContacts = new ArrayList<String>();
	private Map<String, String> readyDependencies = new HashMap<String, String>();
	private String status;
	private String lastStatus;
	private String historyId;
	private String defaultTZ;
	private String offRaw;
	private String jobCycle;
	private String host;
	private String workerGroupId;

	public static final String MapReduce = "MapReduce程序";
	public static final String SHELL = "shell 脚本";
	public static final String HIVE = "hive 脚本";

	public static final String INDEPEN_JOB = "定时调度";
	public static final String DEPEND_JOB = "依赖调度";
	public static final String CYCLE_JOB = "周期调度";
	
	public static final String JOB_CYCLE_HOUR="小时任务";
	public static final String JOB_CYCLE_DAY="天任务";

	public JobModel copy() {
		JobModel model = new JobModel();
		model.setAdmin(getAdmin());
		model.setAdmins(new ArrayList<String>(getAdmins()));
		model.setImportantContacts(new ArrayList<String>(getImportantContacts()));
		model.setAllProperties(new HashMap<String, String>(getAllProperties()));
		model.setAllResources(new ArrayList<Map<String, String>>(
				getAllResources()));
		model.setAuto(getAuto());
		model.setCronExpression(getCronExpression());
		model.setDependencies(new ArrayList<String>(getDependencies()));
		model.setDesc(getDesc());
		model.setFollows(new ArrayList<String>(getFollows()));
		model.setGroupId(getGroupId());
		model.setId(getId());
		model.setJobRunType(getJobRunType());
		model.setJobScheduleType(getJobScheduleType());
		model.setLocalProperties(new HashMap<String, String>(
				getLocalProperties()));
		model.setLocalResources(new ArrayList<Map<String, String>>(
				getLocalResources()));
		model.setName(getName());
		model.setOwner(getOwner());
		model.setOwnerName(getOwnerName());
		model.setOwners(new ArrayList<String>(getOwners()));
		model.setPostProcessers(new ArrayList<String>(getPostProcessers()));
		model.setPreProcessers(new ArrayList<String>(getPreProcessers()));
		model.setScript(getScript());
		model.setDefaultTZ(getDefaultTZ());
		model.setOffRaw(getOffRaw());
		model.setJobCycle(getJobCycle());
		model.setHost(getHost());
		model.setWorkerGroupId(getWorkerGroupId());
		return model;
	}

	
	public Map<String, String> getAllProperties() {
		return allProperties;
	}

	public void setAllProperties(Map<String, String> allProperties) {
		this.allProperties = allProperties;
	}

	public Map<String, String> getLocalProperties() {
		return localProperties;
	}

	public void setLocalProperties(Map<String, String> localProperties) {
		this.localProperties = localProperties;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<String> getOwners() {
		return owners;
	}

	public void setOwners(List<String> owners) {
		this.owners = owners;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public List<Map<String, String>> getLocalResources() {
		return localResources;
	}

	public void setLocalResources(List<Map<String, String>> localResources) {
		this.localResources = localResources;
	}

	public List<Map<String, String>> getAllResources() {
		return allResources;
	}

	public void setAllResources(List<Map<String, String>> allResources) {
		this.allResources = allResources;
	}

	public String getJobRunType() {
		return jobRunType;
	}

	public void setJobRunType(String jobRunType) {
		this.jobRunType = jobRunType;
	}

	public String getJobScheduleType() {
		return jobScheduleType;
	}

	public void setJobScheduleType(String jobScheduleType) {
		this.jobScheduleType = jobScheduleType;
	}

	public Boolean getAuto() {
		return auto;
	}

	public void setAuto(Boolean auto) {
		this.auto = auto;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public List<String> getPreProcessers() {
		return preProcessers;
	}

	public void setPreProcessers(List<String> preProcessers) {
		this.preProcessers = preProcessers;
	}

	public List<String> getPostProcessers() {
		return postProcessers;
	}

	public void setPostProcessers(List<String> postProcessers) {
		this.postProcessers = postProcessers;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public List<String> getAdmins() {
		return admins;
	}

	public void setAdmins(List<String> admins) {
		this.admins = admins;
	}

	public List<String> getFollows() {
		return follows;
	}

	public void setFollows(List<String> follows) {
		this.follows = follows;
	}

	public static String getMapreduce() {
		return MapReduce;
	}

	public static String getShell() {
		return SHELL;
	}

	public static String getHive() {
		return HIVE;
	}

	public static String getIndepenJob() {
		return INDEPEN_JOB;
	}

	public static String getDependJob() {
		return DEPEND_JOB;
	}

	public Map<String, String> getReadyDependencies() {
		return readyDependencies;
	}

	public void setReadyDependencies(Map<String, String> readyDependencies) {
		this.readyDependencies = readyDependencies;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public String getHistoryId() {
		return historyId;
	}

	public void setHistoryId(String historyId) {
		this.historyId = historyId;
	}

	public String getDefaultTZ() {
		return defaultTZ;
	}

	public void setDefaultTZ(String defaultTZ) {
		this.defaultTZ = defaultTZ;
	}

	public String getOffRaw() {
		return offRaw;
	}

	public void setOffRaw(String offRaw) {
		this.offRaw = offRaw;
	}

	public String getJobCycle() {
		return jobCycle;
	}

	public void setJobCycle(String jobCycle) {
		this.jobCycle = jobCycle;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}


	public List<String> getImportantContacts() {
		return importantContacts;
	}


	public void setImportantContacts(List<String> importantContacts) {
		this.importantContacts = importantContacts;
	}


	public String getWorkerGroupId() {
		return workerGroupId;
	}


	public void setWorkerGroupId(String workerGroupId) {
		this.workerGroupId = workerGroupId;
	}
}
