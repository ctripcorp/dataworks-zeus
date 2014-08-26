package com.taobao.zeus.web.platform.client.module.jobmanager;

import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.util.place.PlaceHandler;

public interface JobManagerPresenter extends Presenter, PlaceHandler{

	public static final String TAG = "jobmanager";

    public void onSelect(String providerKey);
}
