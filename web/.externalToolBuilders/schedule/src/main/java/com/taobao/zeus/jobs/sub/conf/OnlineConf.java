package com.taobao.zeus.jobs.sub.conf;

import org.apache.hadoop.conf.Configuration;

public class OnlineConf extends Configuration{
	public OnlineConf(){
		set("hadoop.job.ugi", "tbresys,cug-tbauction,cug-tbdp,cug-tbptd-dev,cug-verticalmarket,cug-tbdataapplication,cug-taobao-sns,cug-mercury,#resys123");
		set("mapred.job.queue.name","cug-tbdataapplication");
		set("proxy.hosts","172.24.160.65:1080");
		set("hadoop.rpc.socket.factory.class.default","com.taobao.cmp.proxy.HadoopProxy");
		set("fs.default.name", "hdfs://hdpnn:9000");
		set("mapred.job.tracker","hdpjt:9001");
		set("mapred.working.dir", "/group/tbdataapplication/tbresys"); 
	}
}
