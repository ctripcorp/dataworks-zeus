package com.taobao.zeus.store.mysql.tool;

import net.sf.json.JSONObject;

import com.taobao.zeus.model.processer.DownloadProcesser;
import com.taobao.zeus.model.processer.HiveProcesser;
import com.taobao.zeus.model.processer.JobProcesser;
import com.taobao.zeus.model.processer.MailProcesser;
import com.taobao.zeus.model.processer.MetaProcesser;
import com.taobao.zeus.model.processer.OutputCheckProcesser;
import com.taobao.zeus.model.processer.OutputCleanProcesser;
import com.taobao.zeus.model.processer.Processer;
import com.taobao.zeus.model.processer.WangWangProcesser;
import com.taobao.zeus.model.processer.ZooKeeperProcesser;

@SuppressWarnings("deprecation")
public class ProcesserUtil {

	public static Processer parse(JSONObject o){
		Processer result=null;
		String id=o.getString("id");
		if("download".equals(id)){
			result= new DownloadProcesser();
		}else if("zookeeper".equalsIgnoreCase(id)){
			result=new ZooKeeperProcesser();
			result.parse(o.getString("config"));
		}else if("mail".equalsIgnoreCase(id)){
			result=new MailProcesser();
			result.parse(o.getString("config"));
		}else if("meta".equalsIgnoreCase(id)){
			result=new MetaProcesser();
			result.parse(o.getString("config"));
		}else if("wangwang".equalsIgnoreCase(id)){
			result=new WangWangProcesser();
			result.parse(o.getString("config"));
		}else if("OutputCheck".equalsIgnoreCase(id)){
			result=new OutputCheckProcesser();
			result.parse(o.getString("config"));
		}else if("OutputClean".equalsIgnoreCase(id)){
			result=new OutputCleanProcesser();
			result.parse(o.getString("config"));
		}else if("JobProcesser".equalsIgnoreCase(id)){
			result=new JobProcesser();
			result.parse(o.getString("config"));
		}else if("hive".equalsIgnoreCase(id)){
			result = new HiveProcesser();
			result.parse(o.getString("config"));
		}
		
		return result;
	}
	
}
