package com.taobao.zeus.web.platform.client.app.schedule;

import com.taobao.zeus.web.platform.client.module.jobdisplay.JobDisplayPresenter;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobManagerPresenter;
import com.taobao.zeus.web.platform.client.util.Presenter;

public interface SchedulePresenter extends Presenter{

	JobManagerPresenter getJobManagerPresenter();
	
	JobDisplayPresenter getJobDisplayPresenter();
}
