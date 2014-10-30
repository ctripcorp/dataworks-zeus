package com.taobao.zeus.socket.master.reqresp;

import com.taobao.zeus.schedule.mvc.event.Events;
import com.taobao.zeus.schedule.mvc.event.JobMaintenanceEvent;
import com.taobao.zeus.socket.SocketLog;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.socket.protocol.Protocol.Status;
import com.taobao.zeus.socket.protocol.Protocol.WebOperate;
import com.taobao.zeus.socket.protocol.Protocol.WebRequest;
import com.taobao.zeus.socket.protocol.Protocol.WebResponse;

public class MasterBeUpdate {
	public WebResponse beWebUpdate(MasterContext context,WebRequest req) {
		
		context.getDispatcher().forwardEvent(new JobMaintenanceEvent(Events.UpdateJob,req.getId()));
		WebResponse resp=WebResponse.newBuilder().setRid(req.getRid()).setOperate(WebOperate.UpdateJob)
			.setStatus(Status.OK).build();
		SocketLog.info("send web update response,rid="+req.getRid()+",jobId="+req.getId());
		return resp;
	}
}
