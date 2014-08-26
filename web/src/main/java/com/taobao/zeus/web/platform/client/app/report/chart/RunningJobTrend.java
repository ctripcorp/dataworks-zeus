package com.taobao.zeus.web.platform.client.app.report.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.Legend.VerticalAlign;
import org.moxieapps.gwt.highcharts.client.plotOptions.ColumnPlotOptions;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
/**
 * 运行任务的趋势图
 * 包括每天 成功运行的job，失败的job
 * @author zhoufang
 *
 */
public class RunningJobTrend implements IsWidget{

	private DateField start;
	private DateField end;
	private TextButton submit=new TextButton("查询",new SelectHandler() {
		@Override
		public void onSelect(SelectEvent event) {
			if(start.getValue()==null){
				start.markInvalid("请正确填写");
				return;
			}
			if(end.getValue()==null || end.getValue().after(new Date())){
				end.markInvalid("请正确填写");
				return;
			}
			if(start.getValue().after(end.getValue())){
				start.markInvalid("开始日期必须小于截止日期");
				return;
			}
			if(end.getValue().getTime()-start.getValue().getTime()>15*24*60*60*1000L){
				start.markInvalid("开启截止区间太大，请设置在2周以内");
				return;
			}
			Date endDate=new Date(end.getValue().getTime());
			endDate.setHours(23);
			endDate.setMinutes(59);
			
			RPCS.getReportService().runningJobs(start.getValue(), endDate, new AbstractAsyncCallback<List<Map<String,String>>>() {
				public void onSuccess(List<Map<String, String>> result) {
					String[] categories=new String[result.size()];
					Number[] successNum=new Number[result.size()];
					Number[] failNum=new Number[result.size()];
					for(int i=0;i<result.size();i++){
						Map<String, String> map=result.get(i);
						categories[i]=map.get("date");
						successNum[i]=map.get("success")==null?0:Integer.valueOf(map.get("success"));
						failNum[i]=map.get("fail")==null?0:Integer.valueOf(map.get("fail"));
					}
					
					final Chart chart=new Chart();
					chart.setType(Series.Type.COLUMN);
					chart.setChartTitleText("运行任务趋势图");
					chart.setColumnPlotOptions(new ColumnPlotOptions()
						.setPointPadding(0.2).setBorderWidth(0))
					.setLegend(new Legend()
						.setLayout(Legend.Layout.VERTICAL)
						.setAlign(Legend.Align.LEFT)
						.setVerticalAlign(VerticalAlign.TOP)
						.setX(100)
						.setY(70)
						.setFloating(true)
						.setBackgroundColor("#FFFFFF")
						.setShadow(true))
					.setToolTip(new ToolTip()
						.setFormatter(new ToolTipFormatter() {
							@Override
							public String format(ToolTipData toolTipData) {
								return toolTipData.getYAsLong()+"";
							}
						}));
					
					chart.getXAxis().setCategories(categories);
					chart.getYAxis().setAxisTitleText("数量");
					chart.addSeries(chart.createSeries().setName("成功的任务")
							.setPoints(successNum));
					chart.addSeries(chart.createSeries().setName("失败的任务")
							.setPoints(failNum));
					
					for(int i=0;i<container.getWidgetCount();i++){
						if(container.getWidget(i) instanceof Chart){
							container.remove(container.getWidget(i));
							break;
						}
					}
					container.add(chart,new VerticalLayoutData(1, -1));
				}
			});
			
		}
	});
	private VerticalLayoutContainer container=new VerticalLayoutContainer();
	
	public RunningJobTrend(){
		start=new DateField();
		start.setEditable(false);
		start.setValue(new Date(new Date().getTime()-7*24*60*60*1000L));
		end=new DateField();
		end.setEditable(false);
		end.setValue(new Date());
		HorizontalLayoutContainer form=new HorizontalLayoutContainer();
		form.add(new FieldLabel(start, "起始日期"),new HorizontalLayoutData(0.3,1));
		form.add(new FieldLabel(end,"截止日期"),new HorizontalLayoutData(0.3, 1));
		form.add(submit,new HorizontalLayoutData(-1,-1));
		
		container.add(form,new VerticalLayoutData(1, 30));
		
		container.addAttachHandler(new Handler() {
			public void onAttachOrDetach(AttachEvent event) {
				submit.fireEvent(new SelectEvent());
			}
		});
	}

	@Override
	public Widget asWidget() {
		return container;
	}
}
