package com.taobao.zeus.store.mysql.tool;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.mortbay.log.Log;

import com.taobao.zeus.model.DebugHistory;
import com.taobao.zeus.model.FileDescriptor;
import com.taobao.zeus.model.GroupDescriptor;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.Profile;
import com.taobao.zeus.model.ZeusFollow;
import com.taobao.zeus.model.JobDescriptor.JobRunType;
import com.taobao.zeus.model.JobDescriptor.JobScheduleType;
import com.taobao.zeus.model.JobStatus.Status;
import com.taobao.zeus.model.JobStatus.TriggerType;
import com.taobao.zeus.model.processer.Processer;
import com.taobao.zeus.store.mysql.persistence.DebugHistoryPersistence;
import com.taobao.zeus.store.mysql.persistence.FilePersistence;
import com.taobao.zeus.store.mysql.persistence.GroupPersistence;
import com.taobao.zeus.store.mysql.persistence.JobHistoryPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.ProfilePersistence;
import com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence;
import com.taobao.zeus.util.DateUtil;
import com.taobao.zeus.util.Tuple;

public class PersistenceAndBeanConvert {

	public static ZeusFollow convert(ZeusFollowPersistence persist) {
		if (persist == null) {
			return null;
		}
		ZeusFollow zf = new ZeusFollow();
		zf.setId(persist.getId());
		zf.setUid(persist.getUid());
		zf.setTargetId(persist.getTargetId().toString());
		zf.setType(persist.getType());
		return zf;
	}

	public static ZeusFollowPersistence convert(ZeusFollow zf) {
		if (zf == null) {
			return null;
		}
		ZeusFollowPersistence persiste = new ZeusFollowPersistence();
		persiste.setId(zf.getId());
		persiste.setTargetId(Long.valueOf(zf.getTargetId()));
		persiste.setType(zf.getType());
		persiste.setUid(zf.getUid());
		return persiste;
	}

	public static JobPersistence convert(Tuple<JobDescriptor, JobStatus> job) {
		if (job == null) {
			return null;
		}
		JobPersistence persist = convert(job.getX());
		JobPersistence p2 = convert(job.getY());
		persist.setReadyDependency(p2.getReadyDependency());
		persist.setStatus(p2.getStatus());
		persist.setHistoryId(p2.getHistoryId());
		return persist;
	}

	public static Tuple<JobDescriptor, JobStatus> convert(JobPersistence persist) {
		if (persist == null) {
			return null;
		}
		JobDescriptor jd = new JobDescriptor();
		jd.setId(String.valueOf(persist.getId()));
		jd.setToJobId(String.valueOf(persist.getToJobId() == null ? null : persist.getToJobId()));
		jd.setName(persist.getName());
		jd.setDesc(persist.getDescr());
		jd.setOwner(persist.getOwner());
		jd.setGroupId(persist.getGroupId() == null ? null : String
				.valueOf(persist.getGroupId()));
		jd.setAuto((persist.getAuto() != null && persist.getAuto() == 1) ? true
				: false);
		jd.setCronExpression(persist.getCronExpression());
		if (persist.getConfigs() != null) {
			JSONObject object = JSONObject.fromObject(persist.getConfigs());
			jd.setProperties(new HashMap<String, String>());
			for (Object key : object.keySet()) {
				jd.getProperties().put(key.toString(),
						object.getString(key.toString()));
			}
		}
		if (persist.getDependencies() != null&&!"".equals(persist.getDependencies().trim())) {
			jd.setDependencies(Arrays.asList(persist.getDependencies().split(
					",")));
		}

		jd.setJobType(JobRunType.parser(persist.getRunType()));
		jd.setScheduleType(JobScheduleType.parser(persist.getScheduleType()));
		String res = persist.getResources();
		if (res != null) {
			List<Map<String, String>> tempRes = new ArrayList<Map<String, String>>();
			JSONArray resArray = JSONArray.fromObject(res);
			for (int i = 0; i < resArray.size(); i++) {
				JSONObject o = resArray.getJSONObject(i);
				Map<String, String> map = new HashMap<String, String>();
				for (Object key : o.keySet()) {
					map.put(key.toString(), o.getString(key.toString()));
				}
				tempRes.add(map);
			}
			jd.setResources(tempRes);
		}

		jd.setScript(persist.getScript());

		if (persist.getPreProcessers() != null
				&& !"".equals(persist.getPreProcessers().trim())) {
			JSONArray preArray = JSONArray.fromObject(persist
					.getPreProcessers());
			List<Processer> preProcessers = new ArrayList<Processer>();
			for (int i = 0; i < preArray.size(); i++) {
				Processer p = ProcesserUtil.parse(preArray.getJSONObject(i));
				if (p != null) {
					preProcessers.add(p);
				}

			}
			jd.setPreProcessers(preProcessers);
		}
		if (persist.getPostProcessers() != null
				&& !"".equals(persist.getPostProcessers().trim())) {
			JSONArray postArray = JSONArray.fromObject(persist
					.getPostProcessers());
			List<Processer> postProcessers = new ArrayList<Processer>();
			for (int i = 0; i < postArray.size(); i++) {
				Processer p = ProcesserUtil.parse(postArray.getJSONObject(i));
				if (p != null) {
					postProcessers.add(p);
				}
			}
			jd.setPostProcessers(postProcessers);
		}
		jd.setTimezone(persist.getTimezone());
		jd.setCycle(persist.getCycle());
		jd.setOffRaw(String.valueOf(persist.getOffset()));
		jd.setStartTimestamp(persist.getStartTimestamp());
		jd.setStartTime(persist.getStartTime()==null?null:DateUtil.date2String(persist.getStartTime()));
		jd.setStatisStartTime(persist.getStatisStartTime()==null?null:DateUtil.date2String(persist.getStatisStartTime()));
		jd.setStatisEndTime(persist.getStatisEndTime()==null?null:DateUtil.date2String(persist.getStatisEndTime()));
		jd.setHost(persist.getHost());
		JobStatus status = new JobStatus();
		status.setJobId(String.valueOf(persist.getId()));
		status.setStatus(Status.parser(persist.getStatus()));
		status.setHistoryId(persist.getHistoryId() == null ? null : persist
				.getHistoryId().toString());
		status.setReadyDependency(new HashMap<String, String>());
		if (persist.getReadyDependency() != null) {
			JSONObject o = JSONObject.fromObject(persist.getReadyDependency());
			for (Object key : o.keySet()) {
				status.getReadyDependency().put(key.toString(),
						o.getString(key.toString()));
			}
		}
		return new Tuple<JobDescriptor, JobStatus>(jd, status);
	}

	public static JobPersistence convert(JobStatus jobStatus) {
		if (jobStatus == null) {
			return null;
		}
		JobPersistence persist = new JobPersistence();
		persist.setId(Long.valueOf(jobStatus.getJobId()));
		persist.setStatus(jobStatus.getStatus() == null ? null : jobStatus
				.getStatus().getId());
		JSONObject o = new JSONObject();
		for (String key : jobStatus.getReadyDependency().keySet()) {
			o.put(key, jobStatus.getReadyDependency().get(key));
		}
		persist.setReadyDependency(o.toString());
		persist.setHistoryId(jobStatus.getHistoryId() == null ? null : Long
				.valueOf(jobStatus.getHistoryId()));
		return persist;
	}

	public static JobPersistence convert(JobDescriptor jd) {
		if (jd == null) {
			return null;
		}
		JobPersistence persist = new JobPersistence();
		JSONArray resArray = new JSONArray();
		for (Map<String, String> map : jd.getResources()) {
			JSONObject o = new JSONObject();
			for (String key : map.keySet()) {
				o.put(key, map.get(key));
			}
			resArray.add(o);
		}
		persist.setResources(resArray.toString());
		JSONObject object = new JSONObject();
		for (Object key : jd.getProperties().keySet()) {
			object.put(key, jd.getProperties().get(key.toString()));
		}
		persist.setAuto(jd.getAuto() ? 1 : 0);
		persist.setToJobId(Long.valueOf(jd.getToJobId()));
		persist.setConfigs(object.toString());
		persist.setCronExpression(jd.getCronExpression());
		persist.setDependencies(StringUtils.join(jd.getDependencies()
				.iterator(), ","));
		persist.setDescr(jd.getDesc());
		persist.setGroupId(jd.getGroupId() == null ? null : Integer.valueOf(jd
				.getGroupId()));
		if (jd.getId() != null) {
			persist.setId(Long.valueOf(jd.getId()));
		}
		persist.setName(jd.getName());
		persist.setOwner(jd.getOwner());
		persist.setRunType(jd.getJobType() == null ? null : jd.getJobType()
				.toString());
		persist.setScheduleType(jd.getScheduleType() == null ? null : jd
				.getScheduleType().getType());

		persist.setScript(jd.getScript());

		persist.setTimezone(jd.getTimezone());

		JSONArray preArray = new JSONArray();
		for (Processer p : jd.getPreProcessers()) {
			JSONObject o = new JSONObject();
			o.put("id", p.getId());
			o.put("config", p.getConfig());
			preArray.add(o);
		}
		persist.setPreProcessers(preArray.toString());

		JSONArray postArray = new JSONArray();
		for (Processer p : jd.getPostProcessers()) {
			JSONObject o = new JSONObject();
			o.put("id", p.getId());
			o.put("config", p.getConfig());
			postArray.add(o);
		}
		persist.setPostProcessers(postArray.toString());
		persist.setTimezone(jd.getTimezone());
		persist.setCycle(jd.getCycle());

		try {
			persist.setOffset(Integer.parseInt(jd.getOffRaw()));
		} catch (NumberFormatException e) {
			// 发生类型转化错误时，默认为1分钟
			persist.setOffset(1);
		}
		persist.setStartTimestamp(jd.getStartTimestamp());
		try {
			persist.setStartTime(jd.getStartTime() == null ? null : DateUtil
					.string2Date(jd.getStartTime()));
			persist.setStatisStartTime(jd.getStatisStartTime() == null ? null
					: DateUtil.string2Date(jd.getStatisStartTime()));
			persist.setStatisEndTime(jd.getStatisEndTime() == null ? null
					: DateUtil.string2Date(jd.getStatisEndTime()));
		} catch (ParseException e) {
			Log.warn("parse str to date failed", e);
		}
		persist.setHost(jd.getHost());
		return persist;
	}

	public static GroupDescriptor convert(GroupPersistence persist) {
		if (persist == null) {
			return null;
		}
		GroupDescriptor gd = new GroupDescriptor();
		gd.setId(String.valueOf(persist.getId()));
		gd.setParent(persist.getParent() == null ? null : String
				.valueOf(persist.getParent()));
		gd.setName(persist.getName());
		gd.setOwner(persist.getOwner());
		gd.setDesc(persist.getDescr());
		gd.setDirectory(persist.getDirectory() == 0 ? true : false);
		if (persist.getConfigs() != null) {
			JSONObject object = JSONObject.fromObject(persist.getConfigs());
			gd.setProperties(new HashMap<String, String>());
			for (Object key : object.keySet()) {
				gd.getProperties().put(key.toString(),
						object.getString(key.toString()));
			}
		}
		String cp = persist.getResources();
		gd.setResources(new ArrayList<Map<String, String>>());

		if (persist.getResources() != null) {
			JSONArray resArray = JSONArray.fromObject(cp);
			for (int i = 0; i < resArray.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject o = resArray.getJSONObject(i);
				for (Object key : o.keySet()) {
					map.put(key.toString(), o.getString(key.toString()));
				}
				gd.getResources().add(map);
			}
		}

		return gd;
	}

	public static GroupPersistence convert(GroupDescriptor gd) {
		if (gd == null) {
			return null;
		}
		GroupPersistence persist = new GroupPersistence();
		JSONArray resArray = new JSONArray();
		for (Map<String, String> map : gd.getResources()) {
			JSONObject o = new JSONObject();
			for (String key : map.keySet()) {
				o.put(key, map.get(key));
			}
			resArray.add(o);
		}
		persist.setResources(resArray.toString());
		JSONObject object = new JSONObject();
		for (Object key : gd.getProperties().keySet()) {
			object.put(key, gd.getProperties().get(key.toString()));
		}
		persist.setConfigs(object.toString());
		persist.setDescr(gd.getDesc());
		persist.setDirectory(gd.isDirectory() ? 0 : 1);
		if (gd.getId() != null) {
			persist.setId(Integer.valueOf(gd.getId()));
		}
		persist.setName(gd.getName());
		persist.setOwner(gd.getOwner());
		persist.setParent(gd.getParent() == null ? null : Integer.valueOf(gd
				.getParent()));
		return persist;
	}

	public static JobHistory convert(JobHistoryPersistence persist) {
		if (persist == null) {
			return null;
		}
		JobHistory history = new JobHistory();
		history.setId(String.valueOf(persist.getId()));
		history.setJobId(String.valueOf(persist.getJobId()));
		history.setToJobId(String.valueOf(persist.getToJobId() == null ? null : persist.getToJobId()));
		history.setStartTime(persist.getStartTime());
		history.setEndTime(persist.getEndTime());
		history.setLog(persist.getLog());
		history.setExecuteHost(persist.getExecuteHost());
		history.setStatus(Status.parser(persist.getStatus()));
		history.setTriggerType(persist.getTriggerType() == null ? null
				: TriggerType.parser(persist.getTriggerType()));
		history.setIllustrate(persist.getIllustrate());
		history.setOperator(persist.getOperator());
		Map<String, String> prop = new HashMap<String, String>();
		if (persist.getProperties() != null) {
			JSONObject json = JSONObject.fromObject(persist.getProperties());
			for (Object key : json.keySet()) {
				prop.put(key.toString(), json.getString(key.toString()));
			}
		}
		history.setProperties(prop);
		history.setStatisEndTime(persist.getStatisEndTime()==null?null:DateUtil.date2String(persist.getStatisEndTime()));
		history.setTimezone(persist.getTimezone());
		history.setCycle(persist.getCycle());
		return history;
	}

	public static JobHistoryPersistence convert(JobHistory history) {
		if (history == null) {
			return null;
		}
		JobHistoryPersistence persist = new JobHistoryPersistence();
		persist.setEndTime(history.getEndTime());
		persist.setExecuteHost(history.getExecuteHost());
		persist.setId(history.getId() == null ? null : Long.valueOf(history
				.getId()));
		persist.setJobId(Long.valueOf(history.getJobId()));
		persist.setToJobId(history.getToJobId()==null ? null : Long.valueOf(history.getToJobId()));
		persist.setLog(history.getLog().getContent());
		persist.setStartTime(history.getStartTime());
		persist.setStatus(history.getStatus() == null ? null : history
				.getStatus().getId());
		persist.setTriggerType(history.getTriggerType() == null ? null
				: history.getTriggerType().getId());
		persist.setIllustrate(history.getIllustrate());
		persist.setOperator(history.getOperator());
		JSONObject json = new JSONObject();
		if (history.getProperties() != null) {
			for (String key : history.getProperties().keySet()) {
				json.put(key, history.getProperties().get(key));
			}
		}
		persist.setProperties(json.toString());
		persist.setTimezone(history.getTimezone());
		persist.setCycle(history.getCycle());
		if (history.getStatisEndTime() != null
				&& !history.getStatisEndTime().equals("")) {
			try {
				persist.setStatisEndTime(DateUtil.string2Date(history
						.getStatisEndTime()));
			} catch (ParseException e) {
				Log.warn("parse str to date failed", e);
			}
		}

		return persist;
	}

	public static FileDescriptor convert(FilePersistence persistence) {
		if (persistence == null) {
			return null;
		}
		FileDescriptor file = new FileDescriptor();
		file.setId(persistence.getId() == null ? null : persistence.getId()
				.toString());
		file.setContent(persistence.getContent());
		file.setFolder(persistence.getType() == FilePersistence.FOLDER ? true
				: false);
		file.setName(persistence.getName());
		file.setOwner(persistence.getOwner());
		file.setParent(persistence.getParent() == null ? null : persistence
				.getParent().toString());
		file.setGmtCreate(persistence.getGmtCreate());
		file.setGmtModified(persistence.getGmtModified());
		return file;
	}

	public static FilePersistence convert(FileDescriptor file) {
		if (file == null) {
			return null;
		}
		FilePersistence persistence = new FilePersistence();
		persistence.setContent(file.getContent());
		persistence.setId(file.getId() == null ? null : Long.valueOf(file
				.getId()));
		persistence.setName(file.getName());
		persistence.setOwner(file.getOwner());
		persistence.setParent(file.getParent() == null ? null : Long
				.valueOf(file.getParent()));
		persistence.setType(file.isFolder() ? FilePersistence.FOLDER
				: FilePersistence.FILE);
		persistence.setGmtCreate(file.getGmtCreate());
		persistence.setGmtModified(file.getGmtModified());
		return persistence;
	}

	public static DebugHistory convert(DebugHistoryPersistence persistence) {
		DebugHistory debug = new DebugHistory();
		debug.setEndTime(persistence.getEndTime());
		debug.setExecuteHost(persistence.getExecuteHost());
		debug.setFileId(persistence.getFileId() == null ? null : persistence
				.getFileId().toString());
		debug.setId(persistence.getId() == null ? null : persistence.getId()
				.toString());
		debug.setStartTime(persistence.getStartTime());
		debug.setStatus(Status.parser(persistence.getStatus()));
		debug.setGmtCreate(persistence.getGmtCreate());
		debug.setGmtModified(persistence.getGmtModified());
		debug.setScript(persistence.getScript());
		debug.setJobRunType(JobRunType.parser(persistence.getRuntype()));
		debug.setLog(persistence.getLog());
		debug.setOwner(persistence.getOwner());
		return debug;
	}

	public static DebugHistoryPersistence convert(DebugHistory debug) {
		DebugHistoryPersistence persist = new DebugHistoryPersistence();
		persist.setEndTime(debug.getEndTime());
		persist.setExecuteHost(debug.getExecuteHost());
		persist.setFileId(debug.getFileId() == null ? null : Long.valueOf(debug
				.getFileId()));
		persist.setId(debug.getId() == null ? null
				: Long.valueOf(debug.getId()));
		persist.setStartTime(debug.getStartTime());
		if(debug.getOwner() != null){
			persist.setOwner(debug.getOwner() );
		}
		persist.setStatus(debug.getStatus() == null ? null : debug.getStatus()
				.toString());
		persist.setGmtCreate(debug.getGmtCreate());
		persist.setGmtModified(debug.getGmtModified());
		persist.setScript(debug.getScript());
		persist.setRuntype(debug.getJobRunType() == null ? null : debug
				.getJobRunType().toString());
		persist.setLog(debug.getLog().getContent());
		persist.setOwner(debug.getOwner() == null ? null : debug.getOwner());
		return persist;
	}

	public static ProfilePersistence convert(Profile p) {
		if (p == null) {
			return null;
		}
		ProfilePersistence pp = new ProfilePersistence();
		if (p.getHadoopConf() != null) {
			JSONObject o = new JSONObject();
			for (String key : p.getHadoopConf().keySet()) {
				o.put(key, p.getHadoopConf().get(key));
			}
			pp.setHadoopConf(o.toString());
		}
		pp.setId(p.getId() == null ? null : Long.valueOf(p.getId()));
		pp.setUid(p.getUid());
		pp.setGmtCreate(p.getGmtCreate());
		pp.setGmtModified(p.getGmtModified());
		return pp;
	}

	public static Profile convert(ProfilePersistence pp) {
		if (pp == null) {
			return null;
		}
		Profile p = new Profile();
		if (pp.getHadoopConf() != null) {
			JSONObject o = JSONObject.fromObject(pp.getHadoopConf());
			for (Object key : o.keySet()) {
				p.getHadoopConf().put(key.toString(),
						o.getString(key.toString()));
			}
		}
		p.setId(pp.getId() == null ? null : pp.getId().toString());
		p.setUid(pp.getUid());
		p.setGmtCreate(pp.getGmtCreate());
		p.setGmtModified(pp.getGmtModified());
		return p;
	}
}
