package com.taobao.zeus.jobs.sub.conf;

import org.apache.hadoop.conf.Configuration;


public class DailyConf extends Configuration{

	public DailyConf(){
		set("hadoop.job.ugi", "tbauction,cug-dev-other,cug-dev-tbdp,#commodity");
		set("mapred.job.queue.name","cug-dev-other");
		set("proxy.hosts","10.232.101.170:1080");
		set("hadoop.rpc.socket.factory.class.default","com.taobao.cmp.proxy.HadoopProxy");
		set("fs.default.name", "hdfs://hdpnn:9000");
		set("mapred.working.dir", "/group/tbdataapplication/zhoufang"); 
	}
}
