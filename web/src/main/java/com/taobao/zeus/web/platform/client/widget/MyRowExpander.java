/**
 * 
 */
package com.taobao.zeus.web.platform.client.widget;

import com.google.gwt.cell.client.Cell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowExpander;

/**
 * @author gufei.wzy 2012-9-29
 */

public class MyRowExpander<M> extends RowExpander<M> {

	private Grid<M> grid;

	public MyRowExpander(IdentityValueProvider<M> valueProvider, Grid<M> grid,
			Cell<M> contentCell) {
		super(valueProvider, contentCell);
		this.grid = grid;
	}

	public void toggleExpand(int index) {
		XElement row = XElement.as(this.grid.getView().getRow(index));
		if (row != null) {
			if (isExpanded(row))
				collapseRow(row);
			else
				expandRow(row);
		}
	}

}
