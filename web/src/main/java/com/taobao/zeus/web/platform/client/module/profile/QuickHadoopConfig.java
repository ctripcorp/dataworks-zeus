package com.taobao.zeus.web.platform.client.module.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.taobao.zeus.web.platform.client.module.jobdisplay.FormatUtil;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

/**
 * 快速配置Hadoop的配置
 * @author zhoufang
 *
 */
public class QuickHadoopConfig extends Window{

	private TextArea area=new TextArea();
	public QuickHadoopConfig(){
		setHeadingText("定制hadoop配置项");
		setModal(true);
		setHeight(500);
		setWidth(800);
		add(area);
		area.setEmptyText("使用key=value 的格式，所有的配置项都需要加前缀 hadoop.");
		area.addValidator(new Validator<String>() {
			public List<EditorError> validate(Editor<String> editor, String value) {
				List<EditorError> result=new ArrayList<EditorError>();
				String[] lines=value.split("\n");
				for(String line:lines){
					line=line.trim();
					if(line.equals("")){
						break;
					}
					if(!line.contains("=")){
						EditorError ee=new DefaultEditorError(editor, "格式不正确", value);
						result.add(ee);
					}
				}
				return result;
			}
		});
		addButton(new TextButton("保存", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if(area.validate()){
					Map<String, String> conf=FormatUtil.parseProperties(area.getValue());
					RPCS.getProfileService().updateHadoopConf(conf, new AbstractAsyncCallback<Void>() {
						public void onSuccess(Void result) {
							QuickHadoopConfig.this.hide();
						}
					});
				}
			}
		}));
		RPCS.getProfileService().getProfile(new AbstractAsyncCallback<ProfileModel>() {
			public void onSuccess(ProfileModel result) {
				String value="";
				if(result.getHadoopConf()!=null){
					for(String key:result.getHadoopConf().keySet()){
						value+=key+"="+result.getHadoopConf().get(key)+"\n";
					}
				}
				area.setValue(value);
			}
		});
	}
}
