package com.taobao.zeus.store;

import java.util.List;

import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;

import com.taobao.zeus.client.ZeusException;

/**
 * hive表管理
 * 
 * @author gufei.wzy 2012-9-17
 */
public interface TableManager {
	
	List<String> getAllDataBases() throws ZeusException;

	/**
	 * 取得所有的表名
	 * 
	 * @return
	 * @throws ZeusException
	 */
	List<String> getAllTables(String dbName) throws ZeusException;

	/**
	 * 获取一个表
	 * @param dataBaseName
	 * @param tableName
	 * @return
	 */
	Table getTable(String dbName, String tableName);

	/**
	 * 获取一个表的所有的分区
	 * @param dbName
	 * @param tableName
	 * @param limit 返回的条数,按id倒序。return all partitions if limit=null.
	 * @return
	 * @throws ZeusException
	 */
	List<Partition> getPartitions(String dbName, String tableName, Integer limit)
			throws ZeusException;

	/**
	 * 获取符合pattern的表
	 * @param dbName
	 * @param pattern
	 *            通配符组成的串 如 gufei，生成的查询类似 ... like "%gufei%";
	 * @param offset
	 *            起始
	 * @param limit
	 *            返回的条数
	 * @return 符合pattern的表的列表
	 * @throws ZeusException
	 */
	List<Table> getPagingTables(String dbName, String pattern, int offset, int limit)
			throws ZeusException;

	/**
	 * 获取表名符合pattern的表数量
	 * @param dbName
	 * @param pattern
	 * @return
	 * @throws ZeusException
	 */
	Integer getTotalNumber(String dbName, String pattern) throws ZeusException;

	/**
	 * 删除分区
	 * @param tableName 表名
	 * @param partVals 分区字段值
	 * @param deleteData 是否删除数据
	 * @return
	 */
	boolean dropPartition(String tableName, List<String> partVals,
			boolean deleteData);

}
