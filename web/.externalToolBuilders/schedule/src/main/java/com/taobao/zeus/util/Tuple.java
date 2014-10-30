package com.taobao.zeus.util;

import java.io.Serializable;

public class Tuple<X, Y> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final X x;
	private final Y y;

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
