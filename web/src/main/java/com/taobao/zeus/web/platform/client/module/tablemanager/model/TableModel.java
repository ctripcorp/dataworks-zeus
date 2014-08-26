package com.taobao.zeus.web.platform.client.module.tablemanager.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TableModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String dbName;
	private Date createDate;
	private String name;
	private String owner;
	private String path;
	private String comment;
	/** 字段分隔符 */
	private String fieldDelim;
	/** 行分隔符 */
	private String lineDelim;
	/** 序列化类名 */
	private String serDeClass;
	/** 输入格式，类名 */
	private String inputFormat;
	/** 分区名列表 */
	@Deprecated
	private List<String> partitions;
	/** 字段列表 */
	private List<TableColumnModel> cols;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<TableColumnModel> getCols() {
		return cols;
	}

	public void setCols(List<TableColumnModel> cols) {
		this.cols = cols;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		if (comment == null)
			this.comment = "";
		this.comment = comment;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public List<String> getPartitions() {
		return partitions;
	}

	public void setPartitions(List<String> partitions) {
		this.partitions = partitions;
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

	public String getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(String inputFormat) {
		this.inputFormat = inputFormat;
	}
}
