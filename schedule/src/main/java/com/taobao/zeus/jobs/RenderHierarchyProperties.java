package com.taobao.zeus.jobs;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.zeus.store.HierarchyProperties;
import com.taobao.zeus.util.ZeusDateTool;

public class RenderHierarchyProperties extends HierarchyProperties{

	private static Logger log=LoggerFactory.getLogger(RenderHierarchyProperties.class);
	private HierarchyProperties properties;
	
	static{
		try {
			Velocity.init();
		} catch (Exception e) {
			log.error("Velocity init fail", e);
		}
	}
	public RenderHierarchyProperties(HierarchyProperties properties) {
		super(new HashMap<String, String>());
		this.properties=properties;
		
	}
	static Pattern pt = Pattern.compile("\\$\\{zdt.*?\\}");
	public static String render(String template){
		if(template==null){
			return null;
		}
		Matcher matcher=pt.matcher(template);
		while(matcher.find()){
			 String m= template.substring(matcher.start(),matcher.end());
			 StringWriter sw=new StringWriter();
			 try {
				VelocityContext context=new VelocityContext();
				context.put("zdt", new ZeusDateTool(new Date()));
				Velocity.evaluate(context, sw, "", m);
				if(m.equals(sw.toString())){
					//渲染后和原数据一样，则直接跳出，如果不跳出会导致死循环
					log.error("render fail with target:"+m);
					break;
				}
			} catch (Exception e) {
				log.error("zdt render error",e);
				break;//防止死循环
			}
			 template=template.replace(m, sw.toString());
			 matcher=pt.matcher(template);
		}
		//${yesterday}变量替换
		template=template.replace("${yesterday}",new ZeusDateTool(new Date()).addDay(-1).format("yyyyMMdd"));
		return template;
	}
	
	public static String render(String template, String dateStr){
		if(template==null){
			return null;
		}
		Matcher matcher=pt.matcher(template);
		while(matcher.find()){
			 String m= template.substring(matcher.start(),matcher.end());
			 StringWriter sw=new StringWriter();
			 try {
				VelocityContext context=new VelocityContext();
				context.put("zdt", new ZeusDateTool(ZeusDateTool.StringToDate(dateStr, "yyyyMMddHHmmss")));
				Velocity.evaluate(context, sw, "", m);
				if(m.equals(sw.toString())){
					//渲染后和原数据一样，则直接跳出，如果不跳出会导致死循环
					log.error("render fail with target:"+m);
					break;
				}
			} catch (Exception e) {
				log.error("zdt render error",e);
				break;//防止死循环
			}
			 template=template.replace(m, sw.toString());
			 matcher=pt.matcher(template);
		}
		//${yesterday}变量替换
		template=template.replace("${yesterday}",new ZeusDateTool(ZeusDateTool.StringToDate(dateStr, "yyyyMMddHHmmss")).addDay(-1).format("yyyyMMdd"));
		return template;
	}
	
	public static void main(String[] args) throws Exception{
		/*VelocityContext context=new VelocityContext();
		context.put("zdt", new ZeusDateTool(new Date()));
		String s="abc${zdt.addDay(-1).format(\"yyyyMMdd\")} ${zdt.addDay(1).format(\"yyyyMMdd\")} ${yesterday}";
		Pattern pt = Pattern.compile("\\$\\{zdt.*\\}");
		Matcher matcher=pt.matcher(s);
		while(matcher.find()){
			 String m= s.substring(matcher.start(),matcher.end());
			 System.out.println(m);
			 StringWriter sw=new StringWriter();
			 Velocity.evaluate(context, sw, "", m);
			 System.out.println(sw.toString());
			 s=s.replace(m, sw.toString());
		}
		s=s.replace("${yesterday}",new ZeusDateTool(new Date()).addDay(-1).format("yyyyMMdd"));
		System.out.println("result:"+s);*/
		String s="abc${zdt.addDay(-2).format(\"yyyy-MM-dd HH:mm:ss\")} ${zdt.addDay(-2).format(\"yyyyMMddHHmmss\")} ${yesterday}";
		s=render(s,"20140924112200");
		//render(s);
		System.out.println("render result:"+s);
	}
	@Override
	public Set<String> getPropertyKeys(){
		Set<String> result=new HashSet<String>();
		
		for(String s:properties.getPropertyKeys()){
			String render=render(s);
			if(render!=null){
				result.add(render);
			}
		}
		return properties.getPropertyKeys();
	}
	
	@Override
	public String getProperty(String key){
		return render(properties.getProperty(key));
	}
	@Override
	public String getProperty(String key,String defaultValue){
		return render(properties.getProperty(key, defaultValue));
	}
	@Override
	public String getLocalProperty(String key){
		return render(properties.getLocalProperty(key));
	}
	@Override
	public String getLocalProperty(String key,String defaultValue){
		return render(properties.getLocalProperty(key));
	}
	@Override
	public List<String> getHierarchyProperty(String key){
		List<String> list= properties.getHierarchyProperty(key);
		List<String> result=new ArrayList<String>();
		for(String s:list){
			result.add(render(s));
		}
		return result;
	}
	@Override
	public HierarchyProperties getParent() {
		return new RenderHierarchyProperties(properties.getParent());
	}
	@Override
	public Map<String, String> getLocalProperties() {
		Map<String, String> map= properties.getLocalProperties();
		Map<String, String> result=new HashMap<String, String>();
		for(String key:map.keySet()){
			result.put(key, render(map.get(key)));
		}
		return result;
	}
	@Override
	public Map<String, String> getAllProperties(){
		Map<String, String> map= properties.getAllProperties();
		Map<String, String> result=new HashMap<String, String>();
		for(String key:map.keySet()){
			result.put(key, render(map.get(key)));
		}
		return result;
	}
	@Override
	public Map<String, String> getAllProperties(String dateStr){
		Map<String, String> map= properties.getAllProperties();
		Map<String, String> result=new HashMap<String, String>();
		for(String key:map.keySet()){
			result.put(key, render(map.get(key),dateStr));
		}
		return result;
	}
	
	@Override
	public void setProperty(String key,String value){
		properties.setProperty(key, value);
	}
}
