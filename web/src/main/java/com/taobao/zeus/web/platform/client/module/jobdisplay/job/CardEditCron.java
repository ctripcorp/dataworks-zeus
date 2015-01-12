package com.taobao.zeus.web.platform.client.module.jobdisplay.job;


import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;


public class CardEditCron extends Window  {
	private VerticalLayoutContainer container;
	private CronExpress expModel;
	
	private TextField minute;
	private TextField hour;
	private TextField day;
	private TextField month;
	private TextField week;
	
	private FieldLabel minuteWrapper;
	private FieldLabel hourWrapper;
	private FieldLabel dayWrapper;
	private FieldLabel monthWrapper;
	private FieldLabel weekWrapper;
	
	private SelectHandler handler;
	
	public void setSelectHandler(SelectHandler handler) {
		this.handler = handler;
		
	}

	public CardEditCron(String exp){
		setHeadingText("构造定时表达式");
		setModal(true);
		setHeight(270);
		setWidth(300);
		add(getLayoutContainer());
		expModel = new CronExpress(exp);
		init(expModel);
	}
	
	private void init(CronExpress model){
		minute = new TextField();
		minute.setWidth(150);
		minute.setHeight(36);
		
		hour = new TextField();
		hour.setWidth(150);
		hour.setHeight(36);
		
		day = new TextField();
		day.setWidth(150);
		day.setHeight(36);
		
		month = new TextField();
		month.setWidth(150);
		month.setHeight(36);
		
		week = new TextField();
		week.setWidth(150);
		week.setHeight(36);
		
		if (model.isFull()) {
			minute.setValue(model.getMinute());
			hour.setValue(model.getHour());
			day.setValue(model.getDay());
			month.setValue(model.getMonth());
			week.setValue(model.getWeek());
		}
		
		minuteWrapper = new FieldLabel(minute, "分");
		hourWrapper = new FieldLabel(hour,"时");
		dayWrapper = new FieldLabel(day,"天");
		monthWrapper = new FieldLabel(month,"月");
		weekWrapper = new FieldLabel(week, "周");
		getLayoutContainer().add(minuteWrapper);
		getLayoutContainer().add(hourWrapper);
		getLayoutContainer().add(dayWrapper);
		getLayoutContainer().add(monthWrapper);
		getLayoutContainer().add(weekWrapper);
		addButton(new TextButton("确认",new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				if (handler != null) {
					handler.onSelect(event);
				}
				CardEditCron.this.hide();
			}
		}));
	}
	
	public VerticalLayoutContainer getLayoutContainer(){
		if(container==null){
			container=new VerticalLayoutContainer();
		}
		return container;
	}
	
	public String getCronExpress(){
		expModel.setMinute(minute.getCurrentValue());
		expModel.setHour(hour.getCurrentValue());
		expModel.setDay(day.getCurrentValue());
		expModel.setMonth(month.getCurrentValue());
		expModel.setWeek(week.getCurrentValue());
		return expModel.getCronExpress();
	}
	
	
	public static class CronExpress{
		
		private final String second = "0";
		private String minute = "0";
		private String hour = "3";
		private String day = "*";
		private String month = "*";
		private String week = "?";
		private boolean full;
		
		public CronExpress(){}
		
		public CronExpress(String exp){
				setCronExpress(exp);
		}
		
		public String getMinute() {
			return minute;
		}
		public void setMinute(String minute) {
			this.minute = minute;
		}
		public String getHour() {
			return hour;
		}
		public void setHour(String hour) {
			this.hour = hour;
		}
		public String getDay() {
			return day;
		}
		public void setDay(String day) {
			this.day = day;
		}
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public String getWeek() {
			return week;
		}
		public void setWeek(String week) {
			this.week = week;
		}
		public boolean isFull() {
			return full;
		}
		
		public void setCronExpress(String express){
			full = false;
			if(express == null){
				return;
			}
			String[] parts = express.split(" ");
			if (parts.length != 6) {
				return;
			}
			minute = parts[1];
			hour = parts[2];
			day = parts[3];
			month = parts[4];
			week = parts[5];
			full = true;
		}
		
		public String getCronExpress(){
			return second + " " + minute + " " + hour + " " + day + " " + month + " " + week;
		}
	}
}
