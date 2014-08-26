package com.taobao.zeus.web.platform.client.module.jobdisplay;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlacePath.JobType;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.TreeKeyProviderTool;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeSelectEvent;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.util.place.PlatformPlaceChangeEvent;

public class JobDisplayPresenterImpl implements JobDisplayPresenter{

	private JobDisplayView jobDisplayView;
	private PlatformContext context;
	public JobDisplayPresenterImpl(PlatformContext context){
		jobDisplayView=new JobDisplayViewImpl(this,context);
		this.context=context;
		this.context.getPlatformBus().addHandler(TreeNodeSelectEvent.type, new TreeNodeSelectEvent.TreeNodeSelectHandler() {
			public void onSelect(TreeNodeSelectEvent event) {
				String providerKey=event.getProviderKey();
				open(providerKey);
			}
		});
		context.getPlatformBus().registPlaceHandler(this);
	}


    private void open(final String providerKey) {
        if(TreeKeyProviderTool.parseIsJob(providerKey)){
            RPCS.getJobService().getUpstreamJob(TreeKeyProviderTool.parseId(providerKey), new AbstractAsyncCallback<JobModel>() {
                public void onSuccess(JobModel result) {
                    jobDisplayView.display(result);
                }
            });
        }else{
            RPCS.getGroupService().getUpstreamGroup(TreeKeyProviderTool.parseId(providerKey), new AbstractAsyncCallback<GroupModel>() {
                public void onSuccess(GroupModel result) {
                    jobDisplayView.display(result);
                }
            });
        }
    }
    
	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(jobDisplayView.asWidget());
	}
	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}
    @Override
    public void handle(PlatformPlaceChangeEvent event) {
        String id = event.getNewPlace().getCurrent().value;
        open(id);
    }
    @Override
    public String getHandlerTag() {
        return TAG;
    }

}
