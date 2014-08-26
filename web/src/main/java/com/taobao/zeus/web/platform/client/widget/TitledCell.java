package com.taobao.zeus.web.platform.client.widget;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TitledCell extends AbstractCell<String> {
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		value = value == null ? "" : value;
		sb.appendHtmlConstant("<span title=\"" + value + "\">")
				.appendHtmlConstant(value).appendHtmlConstant("</span>");
	}
}