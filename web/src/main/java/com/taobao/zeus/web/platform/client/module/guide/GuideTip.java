package com.taobao.zeus.web.platform.client.module.guide;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.widget.core.client.tips.ToolTip;

public class GuideTip extends ToolTip {

	public GuideTip(Widget target) {
		super(target);
		setClosable(true);
		getToolTipConfig().setDismissDelay(0);
	}

	public void initTarget(final Widget target) {
		this.target = target;

	}

	public void setTitleHtml(String html) {
		titleHtml = html;
	}

	public void setTitleText(String text) {
		titleHtml = SafeHtmlUtils.htmlEscape(text);
	}

	public void setBodyHtml(String html) {
		bodyHtml = html;
	}

	public void setBodyText(String text) {
		bodyHtml = SafeHtmlUtils.htmlEscape(text);
	}

	public void setAnchor(Side side) {
		toolTipConfig.setAnchor(side);
	}

	private int offsetX = 0;
	private int offsetY = 0;

	public void setOffset(int x, int y) {
		offsetX = x;
		offsetY = y;
	}

	@Override
	public void showAt(int x, int y) {
		super.showAt(x + offsetX, y + offsetY);
	}
}
