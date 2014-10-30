package com.taobao.zeus.broadcast.notify;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.util.Environment;

public class ZKResultNotify extends AbstractJobResultNotify{

	private static String host;

	@Override
	public void send(String historyId,String data) throws Exception {
		if(host==null || "".equals(host.trim())){
			return;
		}
		JobHistory history=jobHistoryManager.findJobHistory(historyId);
		String jobId=history.getJobId();


		String path="/zeus/jobs/"+jobId;

		send(host,path,data);
	}

	public void setHost(String host){
		this.host=host;
	}

	public void send(String host,String path,String data) throws Exception{
		init();
		ZooKeeper zk=null;
		try {
			CountDownLatch latch=new CountDownLatch(1);
			zk=new ZooKeeper(host, 5*1000, new BlankWatcher(latch));

			if(!latch.await(10,TimeUnit.SECONDS)){
				throw new Exception("Connecting to zk host["+host+"] timeout!");
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			if (data != null) {
				if (data.equalsIgnoreCase("false")
						|| data.equalsIgnoreCase("true")) {
					dos.writeBoolean(Boolean.valueOf(data));
				} else {
					dos.writeBytes(data);
				}
			}
			dos.flush();
			dos.flush();

			if (zk.exists(path, false) != null) {
				zk.setData(path, baos.toByteArray(), -1);
			} else {
				zk.create(path, baos.toByteArray(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
		} finally{
			if(zk!=null){
				zk.close();
			}
		}
	}
	public static class BlankWatcher implements Watcher{
		private CountDownLatch latch;

		public BlankWatcher(CountDownLatch latch){
			this.latch=latch;
		}
		@Override
		public void process(WatchedEvent event) {
			if(event.getState()==KeeperState.SyncConnected){
				latch.countDown();
			}
		}
	}

	private static AtomicBoolean initd=new AtomicBoolean(false);
	private static void init() throws Exception{
		if(initd.compareAndSet(false, true)){
			if(host==null || "".equals(host.trim())){
				return ;
			}
			ZooKeeper zk=null;
			try {
				CountDownLatch latch=new CountDownLatch(1);
				zk=new ZooKeeper(host, 5*1000, new BlankWatcher(latch));

				latch.await();

				if (zk.exists("/zeus", false) == null){
					zk.create("/zeus", null, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				}
				if(zk.exists("/zeus/jobs", false)==null){
					zk.create("/zeus/jobs", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
			} finally{
				if(zk!=null){
					zk.close();
				}
			}
		}
	}
}