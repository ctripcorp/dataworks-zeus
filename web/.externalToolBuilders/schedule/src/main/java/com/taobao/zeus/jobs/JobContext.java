package com.taobao.zeus.jobs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.zeus.model.DebugHistory;
import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.store.HierarchyProperties;
import com.taobao.zeus.util.DateUtil;

/**
 * Job上下文
 * 当存在多个Job顺序处理时，通过上下文莱传递Job状态与信息
 * @author zhoufang
 *
 */
public class JobContext {
	
	public static JobContext getTempJobContext(int runType){
		JobContext jobContext=new JobContext(runType);
		JobHistory history=new JobHistory();
		jobContext.setJobHistory(history);
		File f=new File("/tmp/zeus/"+DateUtil.getToday());
		if(!f.exists()){
			f.mkdir();
		}
		jobContext.setWorkDir(f.getAbsolutePath());
		jobContext.setProperties(new HierarchyProperties(new HashMap<String, String>()));
		return jobContext;
	}

    //调度执行
	public static final int SCHEDULE_RUN=1;
    //手动执行
	public static final int MANUAL_RUN=2;
    //DEBUG执行
	public static final int DEBUG_RUN=3;
    //系统命令执行
	public static final int SYSTEM_RUN=4;
	
	private final int runType;

	private Map<String, Object> data=new HashMap<String, Object>();
	
	private Integer preExitCode;
	private Integer coreExitCode;
	
	private String workDir;
	//设置默认值，防止JobUtils.createJob()方法中产生空指针（92行）
	private HierarchyProperties properties=new HierarchyProperties(new HashMap<String, String>());
	private List<Map<String, String>> resources;
	
	private JobHistory jobHistory;
	
	private DebugHistory debugHistory;
	
	public JobContext(){
		this(MANUAL_RUN);
	}
	
	public JobContext(int runType){
		this.runType=runType;
	}
	
	public Object getData(String key){
		return data.get(key);
	}
	public void putData(String key,Object d){
		data.put(key, d);
	}
	public Integer getPreExitCode() {
		return preExitCode;
	}
	public void setPreExitCode(Integer preExitCode) {
		this.preExitCode = preExitCode;
	}
	public Integer getCoreExitCode() {
		return coreExitCode;
	}
	public void setCoreExitCode(Integer coreExitCode) {
		this.coreExitCode = coreExitCode;
	}
	public String getWorkDir() {
		return workDir;
	}
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}
	public HierarchyProperties getProperties() {
		return properties;
	}
	public void setProperties(HierarchyProperties properties) {
		this.properties = properties;
	}
	public JobHistory getJobHistory() {
		return jobHistory;
	}
	public void setJobHistory(JobHistory jobHistory) {
		this.jobHistory = jobHistory;
	}
	public List<Map<String, String>> getResources() {
		return resources;
	}
	public void setResources(List<Map<String, String>> resources) {
		this.resources = resources;
	}
	public DebugHistory getDebugHistory() {
		return debugHistory;
	}
	public void setDebugHistory(DebugHistory debugHistory) {
		this.debugHistory = debugHistory;
	}

	public int getRunType() {
		return runType;
	}
	
}
