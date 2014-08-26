package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlacePath.JobType;
import com.taobao.zeus.web.platform.client.app.PlatformPlace.KV;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeChangeEvent;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeSelectEvent;
import com.taobao.zeus.web.platform.client.util.Callback;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.place.PlatformPlaceChangeEvent;

public class JobManagerPresenterImpl implements JobManagerPresenter {
	private PlatformContext context;
	private JobManagerView jobManagerView;
	
	public JobManagerPresenterImpl(PlatformContext context){
		this.context=context;
		context.getPlatformBus().addHandler(TreeNodeSelectEvent.type, new TreeNodeSelectEvent.TreeNodeSelectHandler() {
			public void onSelect(TreeNodeSelectEvent event) {
				//my tree
				final String providerKey = event.getProviderKey();
		        
		        updateHistory(TreeKeyProviderTool.parseId(providerKey),
		                getJobManagerView().isMyTreeActive() ? JobType.MyJob
		                        : JobType.SharedJob,
		                TreeKeyProviderTool.parseIsJob(providerKey));
		        
                doSelect(providerKey);
			}
		});
		context.getPlatformBus().addHandler(TreeNodeChangeEvent.TYPE, new TreeNodeChangeEvent.TreeNodeChangeHandler() {
			public void onJobUpdate(JobModel job,TreeNodeChangeEvent event) {
				List<GroupJobTreeModel> list=jobManagerView.getMyTreePanel().getTree().getStore().getAll();
				for(GroupJobTreeModel model:list){
					if(model.isJob() && model.getId().equals(job.getId())){
						model.setName(job.getName());
						break;
					}
				}
				list=jobManagerView.getAllTreePanel().getTree().getStore().getAll();
				for(GroupJobTreeModel model:list){
					if(model.isJob() && model.getId().equals(job.getId())){
						model.setName(job.getName());
						break;
					}
				}
			}
			public void onGroupUpdate(GroupModel group,TreeNodeChangeEvent event) {
				List<GroupJobTreeModel> list=jobManagerView.getMyTreePanel().getTree().getStore().getAll();
				for(GroupJobTreeModel model:list){
					if(model.isGroup() && model.getId().equals(group.getId())){
						model.setName(group.getName());
						break;
					}
				}
				list=jobManagerView.getAllTreePanel().getTree().getStore().getAll();
				for(GroupJobTreeModel model:list){
					if(model.isGroup() && model.getId().equals(group.getId())){
						model.setName(group.getName());
						break;
					}
				}
			}
			public void onChange(final TreeNodeChangeEvent event) {
				Callback callback=null;
				if(event.getNeedSelectProviderKey()!=null){
					callback=new Callback() {
						@Override
						public void callback() {
							GroupJobTreeModel model=jobManagerView.getMyTreePanel().getTree().getStore().findModelWithKey(event.getNeedSelectProviderKey());
							if(model!=null){
								jobManagerView.getMyTreePanel().getTree().setExpanded(model, true);
								jobManagerView.getMyTreePanel().getTree().getSelectionModel().select(model, true);
								JobManagerPresenterImpl.this.context.getPlatformBus().fireEvent(new TreeNodeSelectEvent(event.getNeedSelectProviderKey()));
								
							}
						}
					};
				}
				jobManagerView.getMyTreePanel().refresh(callback);
				jobManagerView.getAllTreePanel().refresh();
			}
		});
		context.getPlatformBus().registPlaceHandler(this);
	}
	
	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(getJobManagerView().asWidget());
	}
	public JobManagerView getJobManagerView() {
		if(jobManagerView==null){
			jobManagerView=new JobManagerViewImpl(this);
		}
		return jobManagerView;
	}
	@Override
	public void onSelect(String providerKey) {
		context.getPlatformBus().fireEvent(new TreeNodeSelectEvent(providerKey));
	}

	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}


    private void doSelect(final String providerKey) {
        GroupJobTreeModel myNeedSelect=jobManagerView.getMyTreePanel().getTree().getStore().findModelWithKey(providerKey);
        if(myNeedSelect!=null){
            jobManagerView.getMyTreePanel().getTree().getSelectionModel().select(myNeedSelect, true);
            jobManagerView.getMyTreePanel().getTree().scrollIntoView(myNeedSelect);
        }else{
            jobManagerView.getMyTreePanel().getTree().getSelectionModel().deselectAll();
        }
        //all tree
        GroupJobTreeModel allNeedSelect=jobManagerView.getAllTreePanel().getTree().getStore().findModelWithKey(providerKey);
        if(allNeedSelect!=null){
            jobManagerView.getAllTreePanel().getTree().getSelectionModel().select(allNeedSelect, true);
            jobManagerView.getAllTreePanel().getTree().scrollIntoView(allNeedSelect);
        }else{
            jobManagerView.getAllTreePanel().getTree().getSelectionModel().deselectAll();
        }

    }

    private void updateHistory(final String id,
            JobType type, boolean isJob) {
        if(isJob){
            History.newItem(new PlacePath().toApp(App.Schedule)
                    .toJobType(type)
                    .toDisplayJob(id).create().getToken(), false);
        }else{

            History.newItem(new PlacePath().toApp(App.Schedule)
                    .toJobType(type)
                    .toDisplayGroup(id).create().getToken(), false);
        }
        
    }
    
    @Override
    public void handle(PlatformPlaceChangeEvent event) {
        Iterator<KV> iterator = event.getNewPlace().iterator();
        final String value = iterator.next().value;
        String key=null;
        if(iterator.hasNext()){
            key=iterator.next().value;
        }
        if(value.equalsIgnoreCase(PlacePath.JobType.MyJob.toString())){
            getJobManagerView().activeMyTreePanel();
        }else{
            getJobManagerView().activeAllTreePanel();            
        }
        if(key!=null){
            doSelect(key);
        }
    }

    @Override
    public String getHandlerTag() {
        return TAG;
    }
}
