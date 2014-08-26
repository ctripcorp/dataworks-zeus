package com.taobao.zeus.web.platform.client.app.document;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

public class DocumentViewImpl implements DocumentView {

	private final DocumentPresenter presenter;
	private final PlatformContext ctx;

	public DocumentViewImpl(PlatformContext ctx, DocumentPresenter presenter) {
		this.presenter = presenter;
		this.ctx = ctx;
	}

	private Widget widget;

	@Override
	public Widget asWidget() {
		if (widget == null) {
			final BorderLayoutContainer rootContainer = new BorderLayoutContainer();
			BorderLayoutData westLayout = new BorderLayoutData();
			westLayout.setMargins(new Margins(3));
			westLayout.setSplit(true);
			westLayout.setSize(200);
			westLayout.setCollapsible(true);
			westLayout.setCollapseMini(true);
			final ContentPanel fileManagerPanel = new ContentPanel();
			fileManagerPanel.setHeaderVisible(false);
			fileManagerPanel.setCollapsible(true);
			fileManagerPanel.setHeadingText("文件管理");
			// TODO 动画效果
			fileManagerPanel.setAnimCollapse(true);
			fileManagerPanel.setAnimationDuration(1000);
			final BorderLayoutContainer centerContainer = new BorderLayoutContainer();
			SimpleContainer wordContainer = new SimpleContainer();
			final ContentPanel tabContainer = new ContentPanel() {
//				@Override
//				protected void onAfterFirstAttach() {
//					super.onAfterFirstAttach();
//					centerContainer.hide(LayoutRegion.EAST);
//				}
			};
			tabContainer.setHeaderVisible(false);
			BorderLayoutData tabLayout = new BorderLayoutData();
			tabLayout.setSplit(true);
			tabLayout.setSize(300);
			tabLayout.setCollapsible(true);
			tabLayout.setCollapseMini(true);
			tabLayout.setMinSize(100);
			tabLayout.setMargins(new Margins(3, 3, 3, 5));
			centerContainer.setEastWidget(tabContainer, tabLayout);
			centerContainer.setCenterWidget(wordContainer, new MarginData(3));
			rootContainer.setWestWidget(fileManagerPanel, westLayout);
			rootContainer.setCenterWidget(centerContainer, new MarginData(3));
			presenter.getFileManagerPresenter().go(fileManagerPanel);
			presenter.getWordPresenter().go(wordContainer);
			presenter.getTableManagerPresenter().go(tabContainer);
			widget = rootContainer;

//			presenter
//					.getPlatformContext()
//					.getPlatformBus()
//					.addHandler(WordActiveEvent.TYPE,
//							new WordActiveEvent.WordActiveHandler() {
//								public void onActive(WordActiveEvent event) {
//									if (event.getWord() instanceof HiveWord) {
//										centerContainer.show(LayoutRegion.EAST);
//									} else {
//										centerContainer.hide(LayoutRegion.EAST);
//									}
//								}
//							});
		}
		return widget;
	}
}
