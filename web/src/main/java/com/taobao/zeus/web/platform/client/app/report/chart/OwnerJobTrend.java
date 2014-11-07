package com.taobao.zeus.web.platform.client.app.report.chart;

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
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
/**
 * 每一个负责人的Job统计信息
 * 主要是排出失败任务最多的几个负责人，督促改进
 * @author zhoufang
 *
 */
public class OwnerJobTrend implements IsWidget{
	//FIXME 标记日历组件
	private DateField date;
	private TextButton submit=new TextButton("查询",new SelectHandler(){
		public void onSelect(SelectEvent event) {
			if(!date.validate()){
				return;
			}
			RPCS.getReportService().ownerFailJobs(date.getValue(), new AbstractAsyncCallback<List<Map<String,String>>>() {
				private DateTimeFormat format=DateTimeFormat.getFormat("yyyy年MM月dd日");
				public void onSuccess(final List<Map<String, String>> result) {
					final String[] categories=new String[result.size()];
					Number[] numbers=new Number[result.size()];
					for(int i=0;i<result.size();i++){
						Map<String, String> map=result.get(i);
						categories[i]=map.get("uname")==null?map.get("uid"):map.get("uname");
						numbers[i]=Integer.valueOf(map.get("count"));
					}
					final Chart chart=new Chart();
					chart.setType(Series.Type.COLUMN);
					chart.setChartTitleText(format.format(date.getValue())+"责任人失败任务统计图");
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
							public String format(ToolTipData toolTipData) {
								int index=0;
								for(int i=0;i<categories.length;i++){
									if(categories[i].equals(toolTipData.getXAsString())){
										index=i;
										break;
									}
								}
								String value= toolTipData.getYAsLong()+"个任务<br/>";
								int count=Integer.valueOf(result.get(index).get("count"));
								if(count>0){
									for(int i=0;i<count;i++){
										value+=result.get(index).get("history"+i)+"<br/>";
									}
								}
								index++;
								return value;
							}
						}));
					
					chart.getXAxis().setCategories(categories);
					chart.getYAxis().setAxisTitleText("失败任务数");
					chart.addSeries(chart.createSeries().setName("失败的任务")
							.setPoints(numbers));
					
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
	public OwnerJobTrend(){
		date=new DateField();
		date.setEditable(false);
		date.setAllowBlank(false);
		date.setValue(new Date());
		
		HorizontalLayoutContainer form=new HorizontalLayoutContainer();
		form.add(new FieldLabel(date,"日期"),new HorizontalLayoutData());
		form.add(submit,new HorizontalLayoutData());
		
		container.add(form,new VerticalLayoutData(1,30));
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
