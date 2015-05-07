package com.taobao.zeus.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunShell {

	private static Logger log = LoggerFactory.getLogger(RunShell.class);
	private List<String> cmd;
	private ProcessBuilder builder;
	private Process process;
	private int exitCode = -999;

	public RunShell(String command) {
		cmd = new ArrayList<String>();
		cmd.add("sh");
		cmd.add("-c");
		cmd.add(command);

	}

	public int run() {
		builder = new ProcessBuilder(cmd);
		try {
			process = builder.start();
			exitCode = process.waitFor();
		} catch (IOException e) {
			log.error("error", e);
		} catch (InterruptedException e) {
			log.error("error", e);
		}
		return exitCode;
	}

	public String getResult() throws IOException {
		if (exitCode == 0) {
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			StringBuilder result = new StringBuilder();
			while ((line = input.readLine()) != null) {
				result.append(line);
			}
			return result.toString().trim();
		} else {
			BufferedReader err_input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			StringBuilder err_result = new StringBuilder();
			while ((line = err_input.readLine()) != null) {
				err_result.append(line);
			}
			log.error(err_result.toString());
		}
		return null;
	}
}
