package com.taobao.zeus.jobs.sub.conf;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.zeus.util.Environment;

public class ConfUtil {
	
	private static Logger log=LoggerFactory.getLogger(ConfUtil.class);
	
	public static String getHadoopHome(){
		String dir = System.getenv("HADOOP_HOME");
		if(dir==null || "".equals(dir.trim())){
			dir = Environment.getHadoopHome();
		}
		return dir == null ? null : "";
	}
	
	public static String getHiveHome(){
		String dir = System.getenv("HIVE_HOME");
		if(dir==null || "".equals(dir.trim())){
			dir = Environment.getHiveHome();
		}
		return dir == null ? null : "";
	}
	
	public static String getHadoopConfDir(){
		String dir=System.getenv("HADOOP_CONF_DIR");
		//2014-09-19增加配置文件的读取
		if(dir==null || "".equals(dir.trim())){
			dir=Environment.getHadoopConfDir();
//			System.out.println("from environment hadoop_conf_dir:"+dir);
		}
		if(dir==null || "".equals(dir.trim())){
			//hadoop2中，配置的默认地址已经修改
			dir=getHadoopHome()+File.separator+"etc"+File.separator+"hadoop";
			File f=new File(dir);
			/**
			 * 兼容Hadoop1的配置
			 */
			if(!f.exists()){
				dir=getHadoopHome()+File.separator+"conf";
			}
		}
//		System.out.println(dir);
		return dir;
	}
	
	public static String getHiveConfDir(){
		String dir=System.getenv("HIVE_CONF_DIR");
		//2014-09-19增加配置文件的读取
		if(dir==null || "".equals(dir.trim())){
			dir=Environment.getHiveConfDir();
//			System.out.println("from environment hive_conf_dir:"+dir);
		}
		if(dir==null || "".equals(dir.trim())){
			dir=getHiveHome()+File.separator+"conf";
		}
//		System.out.println(dir);
		return dir;
	}
	
	
	public static Configuration getDefaultHiveSite(){
		try {
			File f=new File(getHiveConfDir()+File.separator+"hive-site.xml");
			if(f.exists()){
				Configuration conf=new Configuration(false);
				conf.addResource(f.toURI().toURL());
				return conf;
			}
		} catch (IOException e) {
			log.error("load $HIVE_CONF_DIR/hive-site.xml error",e);
		}
		return null;
	}

	public static Configuration getDefaultCoreSite(){
		try {
			File f=new File(getHadoopConfDir()+File.separator+"core-site.xml");
			if(f.exists()){
				Configuration conf=new Configuration(false);
				conf.addResource(f.toURI().toURL());
				return conf;
			}
		} catch (IOException e) {
			log.error("load $HADOOP_CONF_DIR/core-site.xml error",e);
		}
		return null;
	}
	
	public static Configuration getDefaultHdfsSite(){
		try{
			File f=new File(getHadoopConfDir()+File.separator+"hdfs-site.xml");
			if(f.exists()){
				Configuration conf=new Configuration(false);
				conf.addResource(f.toURI().toURL());
				return conf;
			}
		} catch (IOException e) {
			log.error("load $HADOOP_CONF_DIR/hdfs-site.xml error",e);
		}
		return null;
	}
	
	public static Configuration getDefaultMapredSite(){
		try{
			File f=new File(getHadoopConfDir()+File.separator+"mapred-site.xml");
			if(f.exists()){
				Configuration conf=new Configuration(false);
				conf.addResource(f.toURI().toURL());
				return conf;
			}
		} catch (IOException e) {
			log.error("load $HADOOP_CONF_DIR/mapred-site.xml error",e);
		}
		return null;
	}
	
	public static Configuration getDefaultCoreAndHdfsSite(){
		try{
			File core_site=new File(getHadoopConfDir()+File.separator+"core-site.xml");
			File hdfs_site=new File(getHadoopConfDir()+File.separator+"hdfs-site.xml");
			Configuration conf=new Configuration(false);
			if(core_site.exists()){
				conf.addResource(core_site.toURI().toURL());
				log.info("load core site succcessfully");
			}
			if (hdfs_site.exists()) {
				conf.addResource(hdfs_site.toURI().toURL());
				log.info("load hdfs site succcessfully");
			}
			return conf;
		} catch (IOException e) {
			log.error("load $HADOOP_CONF_DIR/core-site.xml and $HADOOP_CONF_DIR/hdfs-site.xml error",e);
		}
		return null;
	}
	

	public static Configuration getDefaultYarnSite(){
		try{
			File f=new File(getHadoopConfDir()+File.separator+"yarn-site.xml");
			if(f.exists()){
				Configuration conf=new Configuration(false);
				conf.addResource(f.toURI().toURL());
				return conf;
			}
		} catch (IOException e) {
			log.error("load $HADOOP_CONF_DIR/yarn-site.xml",e);
		}
		return null;
	}
	
//	public static Configuration getWorkConf(JobContext jobContext) {
//		String workDir = jobContext.getWorkDir();
//		Configuration conf = new Configuration();
//		File hadoopSite = new File(workDir + File.separator + "hadoop-site.xml");
//		if (hadoopSite.exists()) {
//			conf.addResource(new Path(workDir + File.separator
//					+ "hadoop-site.xml"));
//		}
//		File hiveSite = new File(workDir + File.separator + "hive-site.xml");
//		if (hiveSite.exists()) {
//			conf.addResource(new Path(workDir + File.separator + "hive-site.xml"));
//		}
//		return conf;
//	}
}
