package com.taobao.zeus.web.platform.server.rpc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.taobao.zeus.model.DebugHistory;
import com.taobao.zeus.model.FileDescriptor;
import com.taobao.zeus.model.JobDescriptor.JobRunType;
import com.taobao.zeus.socket.protocol.Protocol.ExecuteKind;
import com.taobao.zeus.socket.worker.ClientWorker;
import com.taobao.zeus.store.DebugHistoryManager;
import com.taobao.zeus.store.FileManager;
import com.taobao.zeus.store.Super;
import com.taobao.zeus.web.LoginUser;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryModel;
import com.taobao.zeus.web.platform.client.util.GwtException;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugService;

public class JobDebugRpcImpl implements JobDebugService {
	@Autowired
	private DebugHistoryManager debugHistoryManager;
	@Autowired
	private FileManager fileManager;
	@Autowired
	private ClientWorker worker;

	private static Logger log = LoggerFactory.getLogger(JobDebugRpcImpl.class);

	@Override
	public String debug(String fileId, String mode, String script)
			throws GwtException {

		String uid = LoginUser.getUser().getUid();
		FileDescriptor fd = fileManager.getFile(fileId);
		if (!fd.getOwner().equals(uid)) {
			return "您无权操作";
			//throw new RuntimeException("您无权操作");
		}

		DebugHistory history = new DebugHistory();
		history.setFileId(fileId);
		history.setOwner(uid);
		history.setJobRunType(JobRunType.parser(mode));
		history.setScript(script);
		debugHistoryManager.addDebugHistory(history);

		String debugId = history.getId();

		try {
			worker.executeJobFromWeb(ExecuteKind.DebugKind, history.getId());
		} catch (Exception e) {
			throw new GwtException(e.getMessage());
		}

		return debugId;
	}

	@Override
	public String getLog(String debugId) {
		return debugHistoryManager.findDebugHistory(debugId).getLog()
				.getContent();
	}

	public String getStatus(String debugId) {
		return debugHistoryManager.findDebugHistory(debugId).getStatus()
				.getId();
	}

	@Override
	public PagingLoadResult<DebugHistoryModel> getDebugHistory(
			PagingLoadConfig config, String fileId) {
		int total = debugHistoryManager.pagingTotal(fileId);
		List<DebugHistory> historyList = debugHistoryManager.pagingList(fileId,
				config.getOffset(), config.getLimit());
		List<DebugHistoryModel> modelList = convert(historyList);

		return new PagingLoadResultBean<DebugHistoryModel>(modelList, total,
				config.getOffset());
	}

	private List<DebugHistoryModel> convert(List<DebugHistory> list) {
		List<DebugHistoryModel> ret = new ArrayList<DebugHistoryModel>();
		for (DebugHistory his : list)
			ret.add(convert(his));
		return ret;
	}

	private DebugHistoryModel convert(DebugHistory his) {
		DebugHistoryModel ret = new DebugHistoryModel();
		ret.setEndTime(his.getEndTime());
		ret.setExecuteHost(his.getExecuteHost());
		ret.setFileId(his.getFileId());
		ret.setGmtCreate(his.getGmtCreate());
		ret.setGmtModified(his.getGmtModified());
		ret.setId(his.getId());
		if (his.getJobRunType() != null)
			ret.setJobRunType(DebugHistoryModel.JobRunType.parser(his
					.getJobRunType().toString()));
		ret.setLog(his.getLog().getContent());
		ret.setScript(his.getScript());
		ret.setStartTime(his.getStartTime());
		if (his.getStatus() != null)
			ret.setStatus(DebugHistoryModel.Status.parser(his.getStatus()
					.getId()));
		return ret;
	}

	@Override
	public void cancelDebug(String debugId) throws GwtException {
		String uid = LoginUser.getUser().getUid();
		DebugHistory his = debugHistoryManager.findDebugHistory(debugId);
		FileDescriptor fd = fileManager.getFile(his.getFileId());
		if (!fd.getOwner().equals(uid) && !Super.getSupers().contains(uid)) {
			throw new RuntimeException("您无权操作\nuid=" + uid + " fileOwner="
					+ fd.getOwner());
		}
		try {
			worker.cancelJobFromWeb(ExecuteKind.DebugKind, debugId, LoginUser
					.getUser().getUid());
		} catch (Exception e) {
			log.error("cancelDebug error", e);
			throw new GwtException(e.getMessage());
		}
	}

	@Override
	public DebugHistoryModel getHistoryModel(String debugId) {
		DebugHistory his = debugHistoryManager.findDebugHistory(debugId);
		return convert(his);
	}

}
