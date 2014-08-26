package com.taobao.zeus.web.platform.client.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gwt.place.shared.Place;

public class PlatformPlace extends Place{
	public static final String SPLIT="/";
	public static final String EQUAL=":";
	
	private List<KV> source=new ArrayList<KV>();
	private Queue<KV> queue=new LinkedList<KV>();
	
	public PlatformPlace(Queue<KV> queue){
		this.queue=new LinkedList<KV>(queue);
		this.source=new ArrayList<KV>(queue);
	}
	
	public PlatformPlace(String token){
		String[] s=token.split(SPLIT);
		for(String ss:s){
			if(ss.indexOf(EQUAL)!=-1){
				String[] a=ss.split(EQUAL);
				queue.offer(new KV(a[0],a[1]));
			}else{
				queue.offer(new KV(ss,null));
			}
		}
		source=new ArrayList<KV>(queue);
	}
	
	public String getToken(){
		String token="";
		for(KV kv:source){
			if(kv.value==null){
				token+=kv.key+SPLIT;
			}else{
				token+=kv.key+EQUAL+kv.value+SPLIT;
			}
		}
		return token;
	}
	
	public boolean next(){
		queue.poll();
		if(queue.isEmpty()){
			return false;
		}
		return true;
	}
	public KV getCurrent(){
		return queue.peek();
	}
	public Iterator<KV> iterator(){
	    return queue.iterator();
	}
	
	public boolean isOriginal(){
		return source.size()==queue.size();
	}
	
	public static class KV{
		public final String key;
		public final String value;
		public KV(String key,String value){
			this.key=key;
			this.value=value;
		}
	}

	public List<KV> getSource() {
		return source;
	}
}
