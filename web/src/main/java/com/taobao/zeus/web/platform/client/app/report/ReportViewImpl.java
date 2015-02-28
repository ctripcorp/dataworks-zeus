package com.taobao.zeus.web.platform.client.app.report;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.Legend.VerticalAlign;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.plotOptions.ColumnPlotOptions;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.taobao.zeus.web.platform.client.app.report.chart.OwnerJobTrend;
import com.taobao.zeus.web.platform.client.app.report.chart.RunningJobTrend;

public class ReportViewImpl implements ReportView{

	private ReportPresenter presenter;
	private TabPanel panel=new TabPanel();
	private BorderLayoutContainer allContainer=new BorderLayoutContainer();
	private VBoxLayoutContainer allBtnContainer=new VBoxLayoutContainer();
	private CardLayoutContainer allChartContainer=new CardLayoutContainer();
	public ReportViewImpl(ReportPresenter presenter){
		this.presenter=presenter;
		
		
		allBtnContainer.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		BoxLayoutData vBoxData = new BoxLayoutData(new Margins(5, 5, 5, 5));
		ToggleGroup tg=new ToggleGroup();
		ToggleButton runningTrend=new ToggleButton("每日失败任务趋势");
		runningTrend.setAllowDepress(false);
		runningTrend.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			private RunningJobTrend trend=new RunningJobTrend();
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					if(allChartContainer.getWidgetIndex(trend)==-1){
						allChartContainer.add(trend);
					}
					allChartContainer.setActiveWidget(trend);
				}
			}
		});
		ToggleButton ownerTrend=new ToggleButton("负责人失败任务趋势");
		ownerTrend.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			private OwnerJobTrend trend=new OwnerJobTrend();
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					if(allChartContainer.getWidgetIndex(trend)==-1){
						allChartContainer.add(trend);
					}
					allChartContainer.setActiveWidget(trend);
				}
			}
		});
		ownerTrend.setAllowDepress(false);
		tg.add(runningTrend);
		tg.add(ownerTrend);
		allBtnContainer.add(runningTrend,vBoxData);
		allBtnContainer.add(ownerTrend,vBoxData);
		allContainer.setWestWidget(allBtnContainer,new BorderLayoutData(200));
		allContainer.setCenterWidget(allChartContainer);
		panel.add(allContainer, new TabItemConfig("统计报表", false));
		
	}
	@Override
	public Widget asWidget() {
		return panel;
	}

}
