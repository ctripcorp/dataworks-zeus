package com.taobao.zeus.store;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.jobs.sub.conf.ConfUtil;

/**
 * @author gufei.wzy 2012-9-17
 */
public class CliTableManager implements TableManager {

	private static Logger log = LoggerFactory.getLogger(CliTableManager.class);
	private HiveMetaStoreClient client;
	public static String DEFAULT_DB = "default";

	public CliTableManager() throws Exception {
		HiveConf conf=new HiveConf();
		File f=new File(ConfUtil.getHiveConfDir()+File.separator+"hive-site.xml");
        log.debug("hive conf file:"+f.toString());
		if(f.exists()){
			conf.addResource(f.toURI().toURL());
		}
		client=new HiveMetaStoreClient(conf);
	}

	public CliTableManager(Configuration conf) throws Exception {
		client = new HiveMetaStoreClient(new HiveConf(conf, this.getClass()));
	}

	/**
	 * 获取一个自定义hive配置的manager实例，吞掉了异常
	 * 
	 * @param conf
	 * @return null if an exception was caught.
	 */
	public static TableManager getInstance(Configuration conf) {
		try {
			return new CliTableManager(conf);
		} catch (Exception e) {
			log.error("获取tableManager实例失败", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.zeus.store.TableManager#getAllTables()
	 */
	@Override
	public List<String> getAllTables(String dbName) throws ZeusException {
		try {
			return client.getAllTables(dbName);
		} catch (Exception e) {
			throw new ZeusException("获取所有表信息失败", e);
		}
	}

	@Override
	public List<Table> getPagingTables(String dbName, String pattern, int offset, int limit)
			throws ZeusException {
		if (offset < 0 || limit < 0) {
			throw new ZeusException("获取分页表信息失败,参数不正确");
		}
		if (pattern == null) {
			pattern = "";
		}
		List<Table> tables = new ArrayList<Table>();
		try {
			List<String> tbs = null;
			if (dbName == null) {
				tbs = client.getTables(DEFAULT_DB,
						getPatternFromQuery(pattern));
			}else {
				tbs = client.getTables(dbName,
						getPatternFromQuery(pattern));
			}
			limit = offset + limit > tbs.size() ? tbs.size() - offset : limit;
			for (String t : tbs.subList(offset, offset + limit)) {
				tables.add(client.getTable(dbName, t));
			}
		} catch (Exception e) {
			log.error("获取分页表信息失败", e);
			throw new ZeusException("获取所有表信息失败", e);
		}
		return tables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.zeus.store.TableManager#getTable(java.lang.String)
	 */
	@Override
	public Table getTable(String dbName, String tableName) {
		try {
			if(dbName == null) return client.getTable(DEFAULT_DB, tableName);
			else return client.getTable(dbName, tableName);
		} catch (Exception e) {
			log.warn("找不到该表:" + dbName+":" + tableName, e);
		}

		return null;
	}

	@Override
	public List<Partition> getPartitions(String dbName, String tableName, Integer limit)
			throws ZeusException {
		List<Partition> l = null;
		try {
			if (dbName == null) {
				l = client.listPartitions(DEFAULT_DB, tableName, (short) -1);
			}else{
				l = client.listPartitions(dbName, tableName, (short) -1);
			}
			if (limit != null && limit > 0) {
				limit = limit > l.size() ? l.size() : limit;
				l = l.subList(l.size() - limit, l.size());
			}
			Collections.reverse(l);
		} catch (NoSuchObjectException e) {
			log.error("找不到该表:" + tableName, e);
			throw new ZeusException("没找到这张表！", e);
		} catch (Exception e) {
			log.error("取所有分区失败:" + tableName, e);
			throw new ZeusException("获取所有分区失败！", e);
		}
		return l;
	}

	@Override
	public Integer getTotalNumber(String dbName, String pattern) throws ZeusException {
		if (pattern == null) {
			throw new ZeusException("参数不正确");
		}
		try {
			int size = 0;
			if (dbName == null) {
				size = client.getTables(DEFAULT_DB, getPatternFromQuery(pattern)).size();
			}else {
				size = client.getTables(dbName, getPatternFromQuery(pattern)).size();
			}
			return size;
		} catch (Exception e) {
			throw new ZeusException("获取表数量信息失败", e);
		}
	}

	@Override
	public boolean dropPartition(String tableName, List<String> partVals,
			boolean deleteData) {
		try {
			return client.dropPartition(DEFAULT_DB, tableName, partVals,
					deleteData);
		} catch (NoSuchObjectException e) {
			log.error("表" + tableName + "不存在", e);
		} catch (Exception e) {
			log.error("drop partition failed. table[" + tableName
					+ "] part_vals=" + partVals, e);
		}
		return false;
	}

	/**
	 * <ul>
	 * <li>把检索表的query处理为HiveMetaStoreClient.getTables(String, String
	 * pattern)使用的格式
	 * <li>Query用空格隔开，或逻辑
	 * <p>
	 * 如 <code>gufei*test click</code>
	 * </ul>
	 * 
	 * @see HiveMetaStoreClient#getTables(String, String)
	 * @param query
	 * @return
	 */
	private String getPatternFromQuery(String query) {
		String[] subs = StringUtils.split(query, ' ');
		boolean first = true;
		StringBuffer pattern = new StringBuffer();
		for (String sub : subs) {
			sub = sub.trim() + "*";
			if (!first) {
				pattern.append('|');
			}
			pattern.append(sub);
			first = false;
		}
		return pattern.toString();
	}

	public HiveMetaStoreClient getClient() {
		return client;
	}
	
	

	/**
	 * 指定HiveMetaStoreClient
	 * 
	 * @param client
	 */
	public void setClient(HiveMetaStoreClient client) {
		this.client = client;
	}

	@Override
	public List<String> getAllDataBases() throws ZeusException {
		try {
			return client.getAllDatabases();
		} catch (Exception e) {
			throw new ZeusException("获取所有数据库名称失败", e);
		}
	}
	


}
