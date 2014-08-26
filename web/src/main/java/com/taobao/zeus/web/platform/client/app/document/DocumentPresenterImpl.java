package com.taobao.zeus.web.platform.client.app.document;

import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.module.filemanager.FileManagerPresenter;
import com.taobao.zeus.web.platform.client.module.filemanager.FileManagerPresenterImpl;
import com.taobao.zeus.web.platform.client.module.tablemanager.TableManagerPresenter;
import com.taobao.zeus.web.platform.client.module.tablemanager.TableManagerPresenterImpl;
import com.taobao.zeus.web.platform.client.module.word.WordPresenter;
import com.taobao.zeus.web.platform.client.module.word.WordPresenterImpl;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

public class DocumentPresenterImpl implements DocumentPresenter {

	private DocumentView documentView;
	private FileManagerPresenter fileManagerPresenter;
	private WordPresenter wordPresenter;
	private TableManagerPresenter tableManagerPresenter;

	private final PlatformContext context;

	public DocumentPresenterImpl(PlatformContext context) {
		this.context = context;
	}

	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(getDocumentView().asWidget());
	}

	@Override
	public FileManagerPresenter getFileManagerPresenter() {
		if (fileManagerPresenter == null) {
			fileManagerPresenter = new FileManagerPresenterImpl(context);
		}
		return fileManagerPresenter;
	}

	@Override
	public WordPresenter getWordPresenter() {
		if (wordPresenter == null) {
			wordPresenter = new WordPresenterImpl(context);
		}
		return wordPresenter;
	}

	@Override
	public TableManagerPresenter getTableManagerPresenter() {
		if (tableManagerPresenter == null) {
			tableManagerPresenter = new TableManagerPresenterImpl(context);
		}
		return tableManagerPresenter;
	}

	public DocumentView getDocumentView() {
		if (documentView == null) {
			documentView = new DocumentViewImpl(context, this);
		}
		return documentView;
	}

	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}

}
