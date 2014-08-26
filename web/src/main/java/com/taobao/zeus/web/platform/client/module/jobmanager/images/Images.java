package com.taobao.zeus.web.platform.client.module.jobmanager.images;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class Images{

	public interface Bundle extends ClientBundle{
		ImageResource job();
	    ImageResource leaf_group();
	    ImageResource folder_group();
	}
	
	private static Bundle bundle=GWT.create(Bundle.class);
	
	public static Bundle getResources(){
		return bundle;
	}
}
