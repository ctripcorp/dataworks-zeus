package com.taobao.zeus.web.platform.client.app;

import java.util.LinkedList;
import java.util.Queue;

import com.taobao.zeus.web.platform.client.app.PlatformPlace.KV;
import com.taobao.zeus.web.platform.client.app.document.DocumentApp;
import com.taobao.zeus.web.platform.client.app.home.HomeApp;
import com.taobao.zeus.web.platform.client.app.report.ReportApp;
import com.taobao.zeus.web.platform.client.app.schedule.ScheduleApp;
import com.taobao.zeus.web.platform.client.app.user.UserApp;
import com.taobao.zeus.web.platform.client.module.filemanager.FileManagerPresenter;
import com.taobao.zeus.web.platform.client.module.jobdisplay.JobDisplayPresenter;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobManagerPresenter;
import com.taobao.zeus.web.platform.client.module.word.WordPresenter;

public class PlacePath {

	public enum App{
		Home(HomeApp.TAG),
		Document(DocumentApp.TAG),
		Schedule(ScheduleApp.TAG),
		Report(ReportApp.TAG),
		User(UserApp.TAG);
		private final KV kv;
		private App(String value){
			kv=new KV("App",value);
		}
		public KV getKv() {
			return kv;
		}
	}
    public enum DocType{
        MyDoc("mydoc"),
        SharedDoc("shareddoc");
        private final KV kv;
        private DocType(String value){
            kv=new KV(FileManagerPresenter.TAG,value);
        }
    }
    public enum JobType{
        MyJob("myjob"),
        SharedJob("sharedjob");
        private final KV kv;
        private JobType(String value){
            kv=new KV(JobManagerPresenter.TAG,value);
        }
    }
	
	private Queue<KV> queue=new LinkedList<PlatformPlace.KV>();
	
	public PlacePath toApp(App app){
		queue.offer(app.getKv());
		return this;
	}
    
    public PlacePath toDocType(DocType type){
        queue.offer(type.kv);
        return this;
    }
    
    public PlacePath toDocId(String docId){
        queue.offer(new KV(WordPresenter.TAG, docId));
        return this;
    }
    
    public PlacePath toJobType(JobType type){
        queue.offer(type.kv);
        return this;
    }
    
    public PlacePath toDisplayJob(String jobId){
        queue.offer(new KV(JobDisplayPresenter.TAG, "job-"+jobId));
        return this;
    }
    public PlacePath toDisplayGroup(String groupId){
    	queue.offer(new KV(JobDisplayPresenter.TAG,"group-"+groupId));
    	return this;
    }
	
	public PlatformPlace create(){
		return new PlatformPlace(queue);
	}
}
