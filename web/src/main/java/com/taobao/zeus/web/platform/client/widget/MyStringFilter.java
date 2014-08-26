package com.taobao.zeus.web.platform.client.widget;

import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;

/**
 * <ul>
 * <li>改进了默认的StringFilter，提供了更好的扩展性
 * <li>修正了分页情况下改变filter的值，页码不归1的bug
 * </ul>
 * 
 * @author gufei.wzy 2012-10-11
 */
public class MyStringFilter<M> extends StringFilter<M> {
	protected PagingLoader<FilterPagingLoadConfig, PagingLoadResult<M>> loader;

	/**
	 * @param valueProvider
	 * @param loader
	 *            grid的loader
	 */
	public MyStringFilter(ValueProvider<? super M, String> valueProvider,
			PagingLoader<FilterPagingLoadConfig, PagingLoadResult<M>> loader) {
		super(valueProvider);
		this.loader = loader;

	}

	/**
	 * 获取filter的textField
	 * 
	 * @return
	 */
	public TextField getField() {
		return field;
	}

	/**
	 * 设置filter的值，并引起update事件
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		field.clear();
		field.setValue(value);
		loader.setOffset(0);
		fireUpdate();
	}

	@Override
	protected void onFieldKeyUp(Event event) {
		loader.setOffset(0);
		super.onFieldKeyUp(event);
	}
}