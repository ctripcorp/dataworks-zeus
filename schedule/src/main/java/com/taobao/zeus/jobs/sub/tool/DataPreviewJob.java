package com.taobao.zeus.jobs.sub.tool;

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
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.LineRecordReader;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileRecordReader;
import org.apache.hadoop.util.ReflectionUtils;

import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.conf.ConfUtil;

/**
 * 云梯数据预览Job
 * 
 * @author zhoufang
 * 
 */
public class DataPreviewJob extends AbstractJob {

	private static final int MAX_RECORD_TO_READ = 100;
	public static final char DEFAULT_FIELD_DELIM = '\001';

	public DataPreviewJob(JobContext jobContext) {
		super(jobContext);
	}

	@Override
	public Integer run() throws Exception {
		String pathString = jobContext.getProperties().getProperty(
				"preview.hdfs.path");
		String inputFormatString = jobContext.getProperties().getProperty(
				"preview.hdfs.inputFormat");

		@SuppressWarnings("unused")
		boolean isCompressed = jobContext.getProperties()
				.getProperty("preview.hdfs.isCompressed").equals("true");
		String ugi = jobContext.getProperties().getProperty(
				"preview.hadoop.job.ugi");
		Path path = new Path(pathString);
		Configuration conf = (Configuration) jobContext.getData("hadoop.conf");
		if (conf == null) {
			conf = ConfUtil.getDefaultCoreSite();
		}
		if (ugi != null) {
			conf.set("hadoop.job.ugi", ugi);
		}
		JobConf confQ = new JobConf(conf);
		FileSystem fs = FileSystem.get(confQ);
/*		if (inputFormatString.equals("org.apache.hadoop.hive.ql.io.RCFileInputFormat")) {
			log("暂时不支持RCFile访问 ");
			throw new Exception("暂时不支持RCFile访问");
		}*/

		@SuppressWarnings("unchecked")
		InputFormat<Writable, Writable> inputFormat = (InputFormat<Writable, Writable>) ReflectionUtils
				.newInstance(Class.forName(inputFormatString), conf);
		RecordReader<Writable, Writable> reader;

		FileStatus[] files = fs.listStatus(path);
		if (files == null) {
			log("无法访问" + pathString + "\n路径不存在或没有访问权限！ ");
			throw new Exception("无法访问" + pathString + "\n路径不存在或没有访问权限！ ");
		}

		for (FileStatus f : files) {
			// 忽略目录
			if (f.isDir()) {
				continue;
			}

			@SuppressWarnings("deprecation")
			InputSplit split = new FileSplit(f.getPath(), 0, f.getLen(),
					new JobConf(conf));
			//FIXME
			reader = inputFormat.getRecordReader(split, confQ, Reporter.NULL);
			Writable key = null;
			Text textValue = new Text();
			int count = 0;

			if (((RecordReader) reader) instanceof LineRecordReader
					|| (RecordReader) reader instanceof SequenceFileRecordReader) {

				// sequnceFile的key是BytesWritable，lineRecordReader的key为LongWritable
				if ((RecordReader) reader instanceof SequenceFileRecordReader) {
					SequenceFileRecordReader sReader = (SequenceFileRecordReader) reader;
					key = (Writable) sReader.getKeyClass().newInstance();
				} else {
					key = new LongWritable();
				}
				while (reader.next(key, textValue)
						&& count < MAX_RECORD_TO_READ) {
					String line = new String(textValue.getBytes(), 0,
							textValue.getLength(), "UTF-8");
					log("[output]" + line);
					count++;
				}

			} else if ((RecordReader) reader instanceof RCFileRecordReader) {

				// RCFile读出来是数组，单独处理
				key = new LongWritable();
				BytesRefArrayWritable value = new BytesRefArrayWritable();
				while (reader.next(key, value) && count < MAX_RECORD_TO_READ) {
					StringBuffer sb = new StringBuffer();
					textValue.clear();
					for (int i = 0; i < value.size(); i++) {
						BytesRefWritable v = value.get(i);
						textValue.set(v.getData(), v.getStart(), v.getLength());
						sb.append(textValue.toString());
						if (i < value.size() - 1) {
							// do not put the TAB for the last column
							sb.append(DEFAULT_FIELD_DELIM);
						}
					}
					log(sb.insert(0, "[output]").toString());
					count++;
				}
			}

			// 够MAX_RECORD_TO_READ条就跳出
			if (count >= MAX_RECORD_TO_READ) {
				break;
			}
		}
		return 0;
	}

	@Override
	public void cancel() {
		canceled = true;
	}

}
