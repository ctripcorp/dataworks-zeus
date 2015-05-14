package com.taobao.zeus.schedule.mvc.event;

import com.taobao.zeus.mvc.AppEvent;
import com.taobao.zeus.mvc.EventType;

public class JobMaintenanceEvent extends AppEvent {
	
	private final String id;
	public JobMaintenanceEvent(EventType type,String id){
		super(type);
		this.id=id;
	}
	public String getId() {
		return id;
	}

}
