package com.taobao.zeus.jobs.sub;

import static com.taobao.zeus.util.RunningJobKeys.RUN_CLASSPATH;
import static com.taobao.zeus.util.RunningJobKeys.RUN_INITIAL_MEMORY_SIZE;
import static com.taobao.zeus.util.RunningJobKeys.RUN_JAVA_MAIN_ARGS;
import static com.taobao.zeus.util.RunningJobKeys.RUN_JAVA_MAIN_CLASS;
import static com.taobao.zeus.util.RunningJobKeys.RUN_JVM_PARAMS;
import static com.taobao.zeus.util.RunningJobKeys.RUN_MAX_MEMORY_SIZE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.ProcessJob;
/**
 * 代表一个执行java命令行的Job
 * @author zhoufang
 *
 */
public class JavaJob extends ProcessJob{

	public static final String DEFAULT_INITIAL_MEMORY_SIZE = "64M";
	public static final String DEFAULT_MAX_MEMORY_SIZE = "256M";

	public static String JAVA_COMMAND = "java";
	
	public JavaJob(JobContext jobContext) {
		super(jobContext);
		
		String value="./";
		if(getProperties().getProperty(RUN_CLASSPATH)!=null){
			value=value+File.pathSeparator+getProperties().getProperty(RUN_CLASSPATH);
		}
		value+=File.pathSeparator+"./*";
		getProperties().setProperty(RUN_CLASSPATH, value);
	}
	
	
	protected String getClassPaths() {
		return getProperty(RUN_CLASSPATH, "");
	}

	protected String getMainArguments() {
		String value= getProperty(RUN_JAVA_MAIN_ARGS,"");
		return value;
	}

	protected String getJavaClass() {
		return getProperties().getProperty(RUN_JAVA_MAIN_CLASS);
	}

	protected String getMaxMemorySize() {
		String value= getProperties().getProperty(RUN_MAX_MEMORY_SIZE);
		if(value==null){
			value=DEFAULT_MAX_MEMORY_SIZE;
		}
		return value;
	}

	/**
	 * Xms 参数
	 * @return
	 */
	protected String getInitialMemorySize() {
		return getProperty(RUN_INITIAL_MEMORY_SIZE, DEFAULT_INITIAL_MEMORY_SIZE);
	}

	/**
	 * JVM参数
	 * @return
	 */
	protected String getJVMArguments() {
		return getProperty(RUN_JVM_PARAMS,"");
	}
	/**
	 * 查找已知class的路径
	 * @param containedClass
	 * @return
	 */
	public static String getSourcePathFromClass(Class<?> containedClass) {
		String path = containedClass.getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		File file = new File(path);
		if (!file.isDirectory() && file.getName().endsWith(".class")) {
			String name = containedClass.getName();
			StringTokenizer tokenizer = new StringTokenizer(name, ".");
			while (tokenizer.hasMoreTokens()) {
				tokenizer.nextElement();
				file = file.getParentFile();
			}
			return file.getPath();
		} else {
			return new File(path).getPath();
		}
	}


	@Override
	public List<String> getCommandList() {
		List<String> commands=new ArrayList<String>();
		String command = JAVA_COMMAND + " ";
		command += getJVMArguments() + " ";
		command += "-Xms" + getInitialMemorySize() + " ";
		command += "-Xmx" + getMaxMemorySize() + " ";
		if(getClassPaths()!=null && !getClassPaths().trim().equals("")){
			command += "-cp " + getClassPaths()+ " ";
		}
		command += getJavaClass() + " ";
		command += getMainArguments();
		
		
		commands.add(command);
		
		return commands;
	}

}
