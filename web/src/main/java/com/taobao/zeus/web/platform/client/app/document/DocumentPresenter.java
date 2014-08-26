package com.taobao.zeus.web.platform.client.app.document;

import com.taobao.zeus.web.platform.client.module.filemanager.FileManagerPresenter;
import com.taobao.zeus.web.platform.client.module.tablemanager.TableManagerPresenter;
import com.taobao.zeus.web.platform.client.module.word.WordPresenter;
import com.taobao.zeus.web.platform.client.util.Presenter;

public interface DocumentPresenter extends Presenter{

	
	FileManagerPresenter getFileManagerPresenter();
	
	WordPresenter getWordPresenter();

	TableManagerPresenter getTableManagerPresenter();
}
