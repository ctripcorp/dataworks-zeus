package com.taobao.zeus.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.RCFileRecordReader;
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;
import org.apache.hadoop.hive.serde2.columnar.BytesRefWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.LineRecordReader;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileRecordReader;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.zeus.jobs.sub.conf.ConfUtil;
import com.taobao.zeus.model.Profile;
import com.taobao.zeus.store.ProfileManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.util.ZeusStringUtil;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableColumnModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.shared.rpc.TableManagerService;

public class PartitionDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager
			.getLogger(PartitionDownloadServlet.class);

	private static final int MAX_RECORD_TO_READ = Integer.MAX_VALUE;
	public static final char DEFAULT_FIELD_DELIM = '\001';
	public static final char DEFAULT_LINE_DELIM = '\n';

	private TableManagerService tableManager;
	private ProfileManager profileManager;

    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        WebApplicationContext webApplicationContext = WebApplicationContextUtils
                .getWebApplicationContext(config.getServletContext());
		profileManager = (ProfileManager) webApplicationContext
				.getBean("profileManager");
		tableManager = (TableManagerService) webApplicationContext
				.getBean("table.rpc");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ZeusUser user = LoginUser.getUser();
		String dbName = req.getParameter("dbname");
        String tableName = req.getParameter("table");
        String pathString = req.getParameter("path");
		resp.setCharacterEncoding("GBK");
		resp.setContentType("text/csv");
		resp.setHeader("Content-disposition", "attachment;filename="
				+ tableName + ".csv");
		PrintWriter w = resp.getWriter();

		TableModel t = tableManager.getTableModel(dbName,tableName);
		String inputFormatString = t.getInputFormat();
		char fieldDelim = t.getFieldDelim()==null? DEFAULT_FIELD_DELIM:t.getFieldDelim().toCharArray()[0];
		char lineDelim = t.getLineDelim() == null ? DEFAULT_LINE_DELIM : t
				.getLineDelim().toCharArray()[0];

		final Configuration conf = ConfUtil.getDefaultCoreAndHdfsSite();
		Profile profile = profileManager.findByUid(user.getUid());
		if (profile != null) {
			String ugi = profile.getHadoopConf().get("hadoop.hadoop.job.ugi");
			if (ugi != null) {
				conf.set("hadoop.job.ugi", ugi);
			}
		}
		JobConf confQ = new JobConf(conf);
		FileSystem fs = FileSystem.get(confQ);

		InputFormat inputFormat;
		try {
			inputFormat = (InputFormat) ReflectionUtils.newInstance(
					Class.forName(inputFormatString), conf);
		} catch (ClassNotFoundException e) {
			log.error("partition download error", e);
			w.write("分区下载失败");
			return;
		}
		RecordReader<Writable, Writable> reader;

		FileStatus[] files = fs.listStatus(new Path(pathString));
		if (files == null) {
			log("无法访问" + pathString + "\n路径不存在或没有访问权限！ ");
			w.write("无法访问" + pathString + "\n路径不存在或没有访问权限！ ");
			return;
		}
		boolean first = true;
		for (TableColumnModel col : t.getCols()) {
			if (first) {
				first = false;
			} else {
				w.write(',');
			}
			w.write(col.getName());
		}
		w.write(lineDelim);
		int count = 0;
		for (final FileStatus f : files) {
			// 忽略目录
			if (f.isDir()) {
				continue;
			}

			@SuppressWarnings("deprecation")
			FileSplit split = new FileSplit(f.getPath(), 0, f.getLen(),
					new JobConf(conf));

			reader = inputFormat.getRecordReader(split, confQ, Reporter.NULL);
			Writable key = null;
			Text textValue = new Text();

			if (((RecordReader) reader) instanceof LineRecordReader
					|| (RecordReader) reader instanceof SequenceFileRecordReader) {

				// sequnceFile的key是BytesWritable，lineRecordReader的key为LongWritable
				if ((RecordReader) reader instanceof SequenceFileRecordReader) {
					SequenceFileRecordReader sReader = (SequenceFileRecordReader) reader;
					try {
						key = (Writable) sReader.getKeyClass().newInstance();
					} catch (Exception e) {
						log("partition download error!", e);
						resp.getWriter().write("分区下载失败");
						return;
					}
				} else {
					key = new LongWritable();
				}
				while (reader.next(key, textValue)
						&& count < MAX_RECORD_TO_READ) {
					String[] ss = ZeusStringUtil.split(textValue.toString(), fieldDelim);
					for(int i=0;i<ss.length;i++){
						ss[i] = csvEncode(ss[i]);
					}
					w.println(StringUtils.join(ss, ','));
					count++;
				}

			} else if ((RecordReader) reader instanceof RCFileRecordReader) {

				// RCFile读出来是数组，单独处理
				key = new LongWritable();
				BytesRefArrayWritable value = new BytesRefArrayWritable();
				while (reader.next(key, value) && count < MAX_RECORD_TO_READ) {
					textValue.clear();
					for (int i = 0; i < value.size(); i++) {
						BytesRefWritable v = value.get(i);
						textValue.set(v.getData(), v.getStart(), v.getLength());
						w.write(csvEncode(textValue.toString()));
						if (i < value.size() - 1) {
							// do not put the fieldDelim for the last column
							w.write(',');
						}
					}
					w.println();
					count++;
				}
			}
			reader.close();

			// 够MAX_RECORD_TO_READ条就跳出
			if (count >= MAX_RECORD_TO_READ) {
				break;
			}
		}
	}

	/**
	 * 文本中的双引号双写，如果文本包含双引号或者逗号，用双引号包围文本
	 * @param s
	 * @return
	 */
	private String csvEncode(String s) {
		s = StringUtils.replace(s, "\"", "\"\"");
		if(!StringUtils.containsNone(s, "\",")){
			s = new StringBuffer("\"").append(s).append('"').toString();
		}
		return s;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}
}
