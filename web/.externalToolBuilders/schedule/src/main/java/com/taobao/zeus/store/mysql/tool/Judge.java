package com.taobao.zeus.store.mysql.tool;

import java.util.Date;
/**
 * 判断是否需要刷新数据
 * @author zhoufang
 *
 */
public class Judge {
	public Date lastModified=new Date(0);
	public Integer maxId=-1;
	public Integer count=0;
	public Date stamp=new Date(0);
}
