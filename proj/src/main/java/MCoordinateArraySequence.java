package com.awarematics.postmedia.types.ev.impl;

import org.locationtech.jts.geom.Coordinate;

public class MCoordinateArraySequence {
	
	protected Coordinate[] coords;
	protected long[] times;
	public Coordinate[] getCoords() {
		return coords;
	}
	public void setCoords(Coordinate[] coords) {
		this.coords = coords;
	}
	public long[] getTimes() {
		return times;
	}
	public void setTimes(long[] times) {
		this.times = times;
	}
}

