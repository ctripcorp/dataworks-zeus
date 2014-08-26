package com.taobao.zeus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZeusStringUtil {

	/**
	 * 获得str中倒数第n个c的index
	 * @param str
	 * @param c
	 * @param n
	 * @return
	 */
	public static int nthLastIndexOf(String str, char c, int n) {
		if (str == null) {
			return -1;
		}
		int pos = str.lastIndexOf(c);
		while (n-- > 0 && pos != -1) {
			pos = str.lastIndexOf(c, pos - 1);
		}
		return pos;
	}

	/**
	 * 修改自 {@link com.alibaba.common.lang.StringUtil#split(String, char)}
	 * <br>连续分隔符处理成空字符串
	 * <br>如：
	 * split("aabad",'a')={"","","b","d"}
	 * @param str
	 * @param separatorChar
	 *            分隔符
	 * @return
	 */
	public static String[] split(String str, char separatorChar) {
		if (str == null) {
			return null;
		}
	
		int length = str.length();
	
		if (length == 0) {
			return new String[0];
		}
	
		List<String> list = new ArrayList<String>();
		int i = 0;
		int start = 0;
	
		while (i < length) {
			if (str.charAt(i) == separatorChar) {
				list.add(str.substring(start, i));
	
				start = ++i;
				continue;
			}
			i++;
		}
	
		list.add(str.substring(start, i));
	
		return list.toArray(new String[list.size()]);
	}

	public static void main(String[] args){
		System.out.println(Arrays.asList((split("aabad",'a'))));
	}
}
