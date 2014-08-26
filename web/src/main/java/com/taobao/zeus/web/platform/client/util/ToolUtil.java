package com.taobao.zeus.web.platform.client.util;

import java.util.Arrays;
import java.util.List;

public class ToolUtil {

	private static final List<Character> jobIdChars=Arrays.asList('_','0','1','2','3','4','5','6','7','8','9');
	/**
	 * 日志行中提取云梯jobid
	 * @param line
	 * @return
	 */
	public static String extractJobId(String line){
		if(line.contains("job_201")){
			int start=line.indexOf("job_201");
			int end=start;
			for(int i=start+7;i<line.length();i++){
				if(jobIdChars.contains(line.charAt(i))){
					end=i;
				}else{
					break;
				}
			}
			String job=line.substring(start,end+1);
			return job;
		}
		return null;
	}
	
	public static String extractSyncFromId(String content){
	    if(content==null){
	        return null;
	    }
		String[] lines=content.split("\n");
		String jobId=null;
		for(String line:lines){
			String prefix=null;
			if(line.startsWith("#sync[")){
				prefix="#sync[";
			}else if(line.startsWith("--sync[")){
				prefix="--sync[";
			}
			if(prefix!=null && line.trim().endsWith("]")){
				jobId=line.substring(line.indexOf(prefix)+prefix.length(), line.indexOf("->"));
				try {
					Integer.valueOf(jobId);
					return jobId;
				} catch (NumberFormatException e) {
				}
				
			}
		}
		return null;
	}
	
	public static String extractSyncToId(String content){
		if(content==null){
			return null;
		}
		String[] lines=content.split("\n");
		String jobId=null;
		for(String line:lines){
			String prefix=null;
			if(line.startsWith("#sync[")){
				prefix="#sync[";
			}else if(line.startsWith("--sync[")){
				prefix="--sync[";
			}
			if(prefix!=null && line.trim().endsWith("]")){
				jobId=line.substring(line.indexOf("->")+2, line.indexOf("]"));
				try {
					Integer.valueOf(jobId);
					return jobId;
				} catch (NumberFormatException e) {
				}
				
			}
		}
		return null;
	}
}
