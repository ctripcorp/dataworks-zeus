package com.taobao.zeus.web.platform.client.module.word;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlacePath.DocType;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.filemanager.OpenFileEvent;
import com.taobao.zeus.web.platform.client.module.profile.ProfileModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.StringUtil;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.util.async.PlatformAsyncCallback;
import com.taobao.zeus.web.platform.client.util.filesystem.FileUpdateEvent;
import com.taobao.zeus.web.platform.client.util.filesystem.FileUpdateEvent.FileUpdateHandler;
import com.taobao.zeus.web.platform.client.util.place.PlatformPlaceChangeEvent;
public class WordPresenterImpl implements WordPresenter{

	private final PlatformContext context;
	private WordView wordView;
	public WordPresenterImpl(PlatformContext context){
		this.context=context;
		wordView=new WordViewImpl(context,this);
		context.getPlatformBus().addHandler(FileUpdateEvent.TYPE, new FileUpdateHandler(){
			@Override
			public void onFileUpdate(FileUpdateEvent event) {
				FileModel model=event.getModel();
				wordView.updateFileName(model);
			}
		});
		context.getPlatformBus().registPlaceHandler(this);
		// 打开上传关闭时打开的文档
		RPCS.getProfileService().getProfile(new AbstractAsyncCallback<ProfileModel>() {
			@Override
			public void onSuccess(ProfileModel result) {
				String[] docIds = StringUtil.split(result.getHadoopConf().get("zeus.doc.lastopen"));
				if(docIds!=null) {
					for(String id : docIds){
						open(id, null);
					}
				}
			}
		});
	}
	
	@Override
	public void open(String fileId,final PlatformAsyncCallback<FileModel> callback) {
		context.getFileSystem().getFile(fileId, new PlatformAsyncCallback<FileModel>() {
			@Override
			public void callback(final FileModel t) {
				getWordView().open(t);
				if(callback!=null){
					callback.callback(t);
				}
			}
		});
	}

	@Override
	public void updateLastOpen() {
		RPCS.getProfileService().getProfile(new AbstractAsyncCallback<ProfileModel>() {
			@Override
			public void onSuccess(ProfileModel result) {
				Map<String, String> conf = result.getHadoopConf();
				if(conf!=null){
					List<String> opened = getWordView().getOpenedDocs();
					conf.put("zeus.doc.lastopen",StringUtil.join(opened.toArray(),' '));
					RPCS.getProfileService().updateHadoopConf(conf, new AbstractAsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
						}
					});
				}
			}
		});
	}

	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(getWordView().asWidget());
		context.getPlatformBus().addHandler(OpenFileEvent.TYPE, new OpenFileEvent.OpenFileHandler(){
			@Override
			public void onOpenFile(OpenFileEvent event) {
				FileModel model=event.getModel();
				if(!model.isFolder()){
					open(model.getId(), null);
					if(model.getOwner().equals(context.getUser().getUid())){
						History.newItem(new PlacePath().toApp(App.Document)
								.toDocType(DocType.MyDoc)
								.toDocId(model.getId()).create().getToken(), false);
					}else{
						History.newItem(new PlacePath().toApp(App.Document)
								.toDocType(DocType.SharedDoc)
								.toDocId(model.getId())
								.create().getToken(), false);
					}
				}
			}
		});
		
	}

	public WordView getWordView() {
		return wordView;
	}

	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}

	@Override
	public void handle(PlatformPlaceChangeEvent event) {
		open(event.getNewPlace().getCurrent().value, new PlatformAsyncCallback<FileModel>() {
			@Override
			public void callback(FileModel t) {
				context.getPlatformBus().fireEvent(new OpenFileEvent(t));
			}
		});
	}

	@Override
	public String getHandlerTag() {
		return TAG;
	}

}
