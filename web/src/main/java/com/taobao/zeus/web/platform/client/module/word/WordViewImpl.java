package com.taobao.zeus.web.platform.client.module.word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CloseEvent;
import com.sencha.gxt.widget.core.client.event.CloseEvent.CloseHandler;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.app.PlatformPlace.KV;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.filemanager.images.Images;
import com.taobao.zeus.web.platform.client.module.word.toolbar.ToolBarContainer;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

public class WordViewImpl implements WordView {

	private VerticalLayoutContainer container;
	private ToolBarContainer toolBarContainer=new ToolBarContainer();
	private TabPanel tabPanel;

	private Map<String, Word> wordMap = new HashMap<String, Word>();

	private final WordPresenter presenter;
	private final PlatformContext context;

	public WordViewImpl(final PlatformContext context, final WordPresenter presenter) {
		this.presenter = presenter;
		this.context = context;
		tabPanel = new TabPanel();
		tabPanel.setTabScroll(true);
		tabPanel.setCloseContextMenu(true);
		tabPanel.getElement().setMargins(3);

		tabPanel.addCloseHandler(new CloseHandler<Widget>() {
			@Override
			public void onClose(CloseEvent<Widget> event) {
				Widget widget = event.getItem();
				for (String key : wordMap.keySet()) {
					if (wordMap.get(key).asWidget() == widget) {
						wordMap.remove(key);
						// 更新打开的文档状态
						presenter.updateLastOpen();
						if(tabPanel.getActiveWidget()!=null) {
							toolBarContainer.onDocChange((Word)tabPanel.getActiveWidget());
						}else{
							toolBarContainer.onDocChange(null);
						}
						break;
					}
				}
			}
		});

		tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				if (event.getSelectedItem() instanceof Word) {
					Word word=(Word) event.getSelectedItem();
					String token=History.getToken();
					if(token!=null){
						PlatformPlace pp=new PlatformPlace(token);
						Queue<KV> queue=new LinkedList<PlatformPlace.KV>();
						for(KV kv:pp.getSource()){
							if(!kv.key.equals(WordPresenter.TAG)){
								queue.offer(kv);
							}
						}
						queue.offer(new KV(WordPresenter.TAG, word.getFileModel().getId()));
						History.newItem(new PlatformPlace(queue).getToken(), false);
					}
					// 更新打开的文档状态
					presenter.updateLastOpen();
					toolBarContainer.onDocChange(word);
				}
			}
		});
		
		container=new VerticalLayoutContainer();
		container.add(toolBarContainer,new VerticalLayoutData(1, 35, new Margins(0,0,3,0)));
		container.add(tabPanel,new VerticalLayoutData(1, 1d, new Margins(0)));
	}

	@Override
	public boolean contain(String fileId) {
		if (wordMap.get(fileId) != null) {
			return true;
		}
		return false;
	}

	@Override
	public void open(FileModel fm) {
		if (contain(fm.getId())) {
			tabPanel.setActiveWidget(wordMap.get(fm.getId()));
		} else {
			Widget widget = null;
			TabItemConfig config = new TabItemConfig(getDocViewName(fm), true);
			if (fm.isFolder()) {
				throw new RuntimeException("不能打开文件夹");
			} else if (fm.getName().endsWith(".hive")) {
				HiveWord word = new HiveWord(context, presenter, fm);
				config.setIcon(Images.getImageResources().script());
				wordMap.put(fm.getId(), word);
				widget = word.asWidget();
			} else if (fm.getName().endsWith(".sh")) {
				ShellWord word = new ShellWord(context, presenter, fm);
				config.setIcon(Images.getImageResources().script());
				wordMap.put(fm.getId(), word);
				widget = word.asWidget();
			} else {
				TextWord word = new TextWord(context, presenter, fm);
				config.setIcon(Images.getImageResources().page_white());
				wordMap.put(fm.getId(), word);
				widget = word.asWidget();
			}
			if (widget != null) {
				tabPanel.add(widget, config);
				tabPanel.setActiveWidget(widget);
			}
		}
		// 更新打开的文档状态
		this.presenter.updateLastOpen();
	}

	private String getDocViewName(FileModel fm) {
		return "["+fm.getId()+"] "+fm.getName();
	}

	@Override
	public Widget asWidget() {
		return container;
	}

	@Override
	public void updateFileName(FileModel model) {
		if (contain(model.getId())) {
			Word word = wordMap.get(model.getId());
			TabItemConfig config = tabPanel.getConfig(word);
			config.setText(getDocViewName(model));
			tabPanel.update(word, config);
		}
	}

	@Override
	public List<String> getOpenedDocs() {
		List<String> list =  new ArrayList<String>(tabPanel.getWidgetCount()+1);
		for(int i=0;i<tabPanel.getWidgetCount();i++){
			Word word = (Word) tabPanel.getWidget(i);
			list.add(word.getFileModel().getId());
		}
		// 把激活的文档ID加到列表末端
		if(tabPanel.getActiveWidget()!=null){
			Word activeWord = (Word) tabPanel.getActiveWidget();
			list.add(activeWord.getFileModel().getId());
		}
		return list;
	}
}
