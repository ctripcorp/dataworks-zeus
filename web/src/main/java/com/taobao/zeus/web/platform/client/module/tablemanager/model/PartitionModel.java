/**
 * 
 */
package com.taobao.zeus.web.platform.client.module.tablemanager.model;

import java.io.Serializable;
import java.util.List;

/**
 * 分区
 * 
 * @author gufei.wzy 2012-10-17
 */

public class PartitionModel implements Serializable {
	private static final long serialVersionUID = -1L;
	/**
	 * 完整路径 如：/group/tbdataapplication/gufei.wzy/hive_learn2/pt= 20121074/type=1
	 */
	private String path;
	/** 分区大小 */
	private String size;
	/** 分区名（路径） 如：pt=20121074/type=1 */
	private String name;
	/** 字段分隔符 */
	private String fieldDelim;
	/** 行分隔符 */
	private String lineDelim;
	/** 序列化类名 */
	private String serDeClass;
	/** 字段列表 */
	private List<TableColumnModel> cols;
	/** 输入格式，类名 */
	private String inputFormat;
	/** 是否压缩 */
	private boolean isCompressed;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFieldDelim() {
		return fieldDelim;
	}

	public void setFieldDelim(String fieldDelim) {
		this.fieldDelim = fieldDelim;
	}

	public String getLineDelim() {
		return lineDelim;
	}

	public void setLineDelim(String lineDelim) {
		this.lineDelim = lineDelim;
	}

	public String getSerDeClass() {
		return serDeClass;
	}

	public void setSerDeClass(String serDeClass) {
		this.serDeClass = serDeClass;
	}

	public List<TableColumnModel> getCols() {
		return cols;
	}

	public void setCols(List<TableColumnModel> cols) {
		this.cols = cols;
	}

	public String getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(String inputFormat) {
		this.inputFormat = inputFormat;
	}

	public boolean isCompressed() {
		return isCompressed;
	}

	public void setCompressed(boolean isCompressed) {
		this.isCompressed = isCompressed;
	}
}