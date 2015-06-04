package com.taobao.zeus.web.platform.client;

import javax.servlet.http.HttpServletRequest;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.impl.DOMImpl;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.taobao.zeus.web.platform.client.app.document.DocumentApp;
import com.taobao.zeus.web.platform.client.app.home.HomeApp;
import com.taobao.zeus.web.platform.client.app.report.ReportApp;
import com.taobao.zeus.web.platform.client.app.schedule.ScheduleApp;
import com.taobao.zeus.web.platform.client.app.user.UserApp;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.guide.GuideTip;
import com.taobao.zeus.web.platform.client.util.GWTEnvironment;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.StartEvent;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.util.template.TemplateResources;
import com.taobao.zeus.web.platform.client.widget.Platform;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PlatformEntry implements EntryPoint {
	private FramedPanel vp;
	static{
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				com.google.gwt.user.client.Window.alert(e.getMessage());
				e.printStackTrace();
			}
		});
		
	}
	private VerticalPanel mainPanel = new VerticalPanel(); 
	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel(); 
	private HorizontalPanel addPane2 = new HorizontalPanel(); 
	private TextBox newSymbolTextBox = new TextBox(); 
	private Button addStockButton = new Button("Add"); 
	private Label lastUpdatedLabel = new Label();
	@Override

	public void onModuleLoad() {
//			RPCS.getUserService().checkUserSession(new AsyncCallback<String>() {
//				@Override
//				public void onSuccess(String result) {
//					System.out.println(result);
//					if(null==result || result.equals("null")){
//						com.google.gwt.user.client.Window.Location.assign("/zeus-web/login.do");
//					}else{
						new PlatformEntry().init();
//					}
//				}
//				
//				@Override
//				public void onFailure(Throwable caught) {
//					System.out.println(caught);
//					//com.google.gwt.user.client.Window.Location.reload();
//				}
//			});
		    
		
	    
	}

	public void init(){
		RPCS.getUserService().getUser(new AsyncCallback<ZUser>() {
			@Override
			public void onSuccess(ZUser result) {
				Platform s=new Platform(result);
				final HomeApp home=new HomeApp(s.getPlatformContext());
				final UserApp user=new UserApp(s.getPlatformContext(),result);
				s.addApp(home);
				s.addApp(new DocumentApp(s.getPlatformContext()));
				s.addApp(new ScheduleApp(s.getPlatformContext()));
				s.addApp(new ReportApp(s.getPlatformContext()));
				s.addApp(user);
				
				RootPanel.get().add(s);
				
				s.getPlatformContext().getPlatformBus().fireEvent(new StartEvent());
				
				String id=GWTEnvironment.getNoticeTemplateId();
				RPCS.getFileManagerService().getFile(id, new AbstractAsyncCallback<FileModel>() {
					@Override
					public void onSuccess(FileModel result) {
						if(result.getContent()!=null && result.getContent().startsWith("<!--OK-->")){
							process(result.getContent());
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						TemplateResources templates=com.google.gwt.core.shared.GWT.create(TemplateResources.class);
						process(templates.notice().getText());
					}
					private void process(String content){
						GuideTip tip=new GuideTip(home.getShortcut());
						String[] lines=content.split("\n");
						for(String line:lines){
							if(line.startsWith("<!--width=")){
								tip.setWidth(Integer.valueOf(line.substring(10,line.indexOf("-->"))));
							}else if(line.startsWith("<!--height=")){
								tip.setHeight(Integer.valueOf(line.substring(11,line.indexOf("-->"))));
							}
						}
						
						tip.setClosable(true);
						tip.getToolTipConfig().setDismissDelay(0);
						tip.setBodyHtml(content);
						tip.setOffset(100, 0);
						tip.show();
					}
				});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				com.google.gwt.user.client.Window.Location.assign("/zeus-web/login.do");
			}
		});
		// 防ark认证过期，一分钟发送一次rpc请求
		new Timer(){
			@Override
			public void run() {
				RPCS.getUserService().getUser(new AsyncCallback<ZUser>() {
					@Override
					public void onFailure(Throwable caught) {}
					@Override
					public void onSuccess(ZUser result) {}
				});
			}
			
		}.scheduleRepeating(60*1000);
	}
	private Widget loginWindow(){
		if (vp == null) {
			vp = new FramedPanel();
		}
		
		final TextField name = new TextField();
		final PasswordField passsword = new PasswordField();
		final TextButton bt=new TextButton("Login");
		 SelectHandler sh = new SelectHandler() {
		        @Override
		        public void onSelect(SelectEvent event) {
		        	Info.display("Name:"+ name.getValue(),"Password:"+passsword.getValue());
		        	vp.hide();
		        }
		      };
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		vp.setWidth(350);
		vp.add(p);
		
		name.setAllowBlank(false);
		p.add(new FieldLabel(name, "Name"), new VerticalLayoutData(
				1, -1));
		
		passsword.setAllowBlank(false);
		p.add(new FieldLabel(passsword, "Password"), new VerticalLayoutData(1,
				-1));
		
		p.add(bt);
		vp.setButtonAlign(BoxLayoutPack.CENTER);
		vp.setHeadingText("zeus");
		bt.addSelectHandler(sh);
		
		return vp;
	}
}
