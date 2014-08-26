package com.taobao.zeus.web.platform.client.module.tablemanager.component;

import java.io.Serializable;

public class Tuple<X, Y> implements Serializable {

	private static final long serialVersionUID = 1L;
	private X x;
	private Y y;

	public Tuple() {
		x = null;
		y = null;
	};

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}

	@Override
	public String toString() {
		return "[" + x.toString() + "," + y.toString() + "]";
	}
}
