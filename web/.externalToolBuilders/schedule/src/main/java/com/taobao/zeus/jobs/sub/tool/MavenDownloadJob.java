package com.taobao.zeus.jobs.sub.tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.JobContext;

public class MavenDownloadJob extends AbstractJob {

	public static class MavenConfig {
		private String group;
		private String artifact;
		private String version;
		private String url;

		public MavenConfig() {
		}

		public MavenConfig(String group, String artifact, String version,
				String url) {
			this.group = group;
			this.artifact = artifact;
			this.version = version;
			this.url = url;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public String getArtifact() {
			return artifact;
		}

		public void setArtifact(String artifact) {
			this.artifact = artifact;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	private List<MavenConfig> files = new ArrayList<MavenConfig>();

	public MavenDownloadJob(JobContext jobContext, List<MavenConfig> files) {
		super(jobContext);
		if (files != null) {
			this.files = files;
		}
	}

	@Override
	public Integer run() {
		Integer exitCode = 0;
		for (MavenConfig mc : files) {
			String downloadUrl = mc.getUrl();
			if (downloadUrl == null || "".equals(downloadUrl)) {
				downloadUrl = "http://mvnrepo.taobao.ali.com:8081/nexus/content/groups/public/"
						+ StringUtils.replaceChars(mc.getGroup(), '.', '/')
						+ "/"
						+ mc.getArtifact()
						+ "/"
						+ mc.getArtifact()
						+ mc.getArtifact() + "-" + mc.getVersion() + ".jar";
			}
			try {
				jobContext.getJobHistory().getLog()
						.appendZeus("开始下载maven配置文件:" + downloadUrl);
				download(downloadUrl, jobContext.getWorkDir() + File.separator
						+ downloadUrl.substring(downloadUrl.lastIndexOf("/")));
				jobContext.getJobHistory().getLog()
						.appendZeus("下载maven配置文件 ：" + downloadUrl + " 成功");
			} catch (Exception e) {
				jobContext.getJobHistory().getLog().appendZeusException(e);
				exitCode = -1;
			}
		}
		return exitCode;
	}

	private void download(String destUrl, String localFilePath)
			throws Exception {
		int size = 0;
		byte[] buf = new byte[1024];
		HttpURLConnection httpUrl = (HttpURLConnection) new URL(destUrl)
				.openConnection();
		httpUrl.connect();
		BufferedInputStream bis = new BufferedInputStream(
				httpUrl.getInputStream());

		FileOutputStream fos = new FileOutputStream(new File(localFilePath));
		while ((size = bis.read(buf)) != -1) {
			fos.write(buf, 0, size);
		}
		fos.close();
		bis.close();
		httpUrl.disconnect();

	}

	@Override
	public void cancel() {
		canceled = true;
	}
}
