package com.taobao.zeus.web.platform.client.app.schedule;

import com.taobao.zeus.web.platform.client.theme.ResourcesTool;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class ScheduleShortcut extends Shortcut{

	public ScheduleShortcut() {
		super("schedule","调度中心");
		setIcon(ResourcesTool.iconResources.schedule());
		
	}
}
