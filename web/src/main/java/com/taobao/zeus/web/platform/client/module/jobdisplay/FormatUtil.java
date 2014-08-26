package com.taobao.zeus.web.platform.client.module.jobdisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;


public class FormatUtil {
	/**
	 * 转换成编辑框中的字符串
	 * @return
	 */
	public static String convertPropertiesToEditString(Map<String, String> properties){
		StringBuffer sb=new StringBuffer();
		for(String key:properties.keySet()){
			sb.append(key+"="+properties.get(key)+"\n");
		}
		return sb.toString();
	}
	
	public static String convertResourcesToEditString(List<Map<String, String>> resources){
		String s="";
		for(Map<String, String> map:resources){
			if(map.isEmpty()){
				continue;
			}
			for(String key:map.keySet()){
				s+=key+":"+map.get(key)+",";
			}
			if(s.endsWith(",")){
				s=s.substring(0, s.length()-1);
			}
			s+="\n";
		}
		return s;
	}
	/**
	 * 转换成HTML代码
	 * @param allProperties
	 * @param localProperties
	 * @return
	 */
	public static String convertPropertiesToText(Map<String, String> allProperties,Map<String, String> localProperties){
		StringBuffer sb=new StringBuffer();
		for(String key:allProperties.keySet()){
			if(localProperties.containsKey(key)){
				sb.append("<div>"+key+"="+allProperties.get(key)+"</div>");
			}else{
				sb.append("<div style='background-color:red'>"+key+"="+allProperties.get(key)+"</div>");
			}
			
		}
		return sb.toString();
	}
	/**
	 * 转换成HTML代码
	 * @param allResources
	 * @param localResources
	 * @return
	 */
	public static final String converResourcesToText(List<Map<String, String>> allResources,List<Map<String, String>> localResources){
		StringBuffer sb=new StringBuffer();
		for(Map<String, String> map:allResources){
			if(map.isEmpty()){
				continue;
			}
			if(localResources.contains(map)){
				sb.append("<div>");
				for(String key:map.keySet()){
					sb.append(key+":"+map.get(key)+",");
				}
				sb.append("</div>");
			}else{
				sb.append("<div style='background-color:red'>");
				for(String key:map.keySet()){
					sb.append(key+":"+map.get(key)+",");
				}
				sb.append("</div>");
			}
		}
		return sb.toString();
	}
	
	public static Map<String, String> parseProperties(String text){
		Map<String, String> conf=new HashMap<String, String>();
		if(text!=null){
			String[] rows=text.split("\n");
			for(String row:rows){
				row=row.trim();
				if(row.equals("")){
					continue;
				}
				int index=row.indexOf("=");
				if(index!=-1){
					conf.put(row.substring(0,index), row.substring(index+1));
				}
				
			}
		}
		return conf;
	}
	public static List<Map<String, String>> parseResources(String text){
		List<Map<String, String>> res=new ArrayList<Map<String,String>>();
		if(text!=null){
			String[] lines=text.split("\n");
			for(String line:lines){
				Map<String, String> map=new HashMap<String, String>();
				line=line.trim();
				if(line.equals("")){
					continue;
				}
				String[] vv=line.split(",");
				for(String v:vv){
					String key=v.split(":")[0];
					String value=v.substring(key.length()+1);
					map.put(key, value);
				}
				if(!map.isEmpty()){
					res.add(map);
				}
			}
		}
		return res;
	}
	
	public static Validator<String> propValidator=new Validator<String>() {
		@Override
		public List<EditorError> validate(Editor<String> editor, String value) {
			if(value==null){
				return null;
			}
			String[] lines=value.split("\n");
			for(String line:lines){
				line=line.trim();
				if(line.equals("")){
					break;
				}
				if(!line.contains("=")){
					EditorError ee=new DefaultEditorError(editor, "格式不正确", null);
					return Arrays.asList(ee);
				}
			}
			return null;
		}
	};
	public static Validator<String> resourceValidator=new Validator<String>() {
		@Override
		public List<EditorError> validate(Editor<String> editor, String value) {
			if(value==null){
				return null;
			}
			String[] lines=value.split("\n");
			for(String line:lines){
				line=line.trim();
				if("".equals(line)){
					continue;
				}
				String[] vv=line.split(",");
				if(vv.length>=2){
					String[] name=vv[0].split(":");
					String[] uri=vv[1].split(":");
					if(!"name".equals(name[0]) || !"uri".equals(uri[0])
							|| name.length<2 || uri.length<2){
						EditorError ee=new DefaultEditorError(editor, "格式不正确", null);
						return Arrays.asList(ee);
					}
				}else{
					EditorError ee=new DefaultEditorError(editor, "格式不正确", null);
					return Arrays.asList(ee);
				}
				
			}
			return null;
		}
	};
}
