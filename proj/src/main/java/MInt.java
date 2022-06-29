package com.awarematics.postmedia.types.mediamodel;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import com.awarematics.postmedia.mgeom.MGeometryFactory;

public class MInt extends MGeometry {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// "MInt (10 1, 15 2, 20 3, 17 4)"
	int[] count;
	long[] times;

	public MInt(MInt mi) {
		this.count = mi.count;
		this.times = mi.times;
	}

	public MInt() {
		// TODO Auto-generated constructor stub
	}

	public MInt(int[] count, long[] times) {
		this.count = count;
		this.times = times;
	}


	public String toGeoString() {
		String mpointString = "MINT (";
		for (int i = 0; i < numOf(); i++) {
			if (i == 0) {
				mpointString = mpointString + count[i] + " " + times[i];
			} else {
				mpointString = mpointString + ", " + count[i] + " " + times[i];
			}
		}
		mpointString = mpointString + ")";
		return mpointString;
	}

	
	@Override
	public int numOf() {

		return count.length;
	}


	@Override
	public Geometry snapshot(long ts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Geometry spatial() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MGeometry lattice(long duration){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGeometry atomize(long x2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGeometry lattice(MDuration duration) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MGeometry slice(long fromTime, long toTime) {
		MGeometryFactory factory = null;
		if (times == null)
			return null;

		if ((toTime < times[0]) || (fromTime > times[times.length - 1]))
			return null;

		if (fromTime > toTime)
			return null;

		long overlappedStartTime = Math.max(times[0], fromTime);
		long overlappedEndTime = Math.min(times[times.length - 1], toTime);
		factory = new MGeometryFactory();
		if (times.length == 1) {
			return factory.createMInt(count, times);
		}
		int num = 0;
		for (int i = 0; i < times.length; i++) {
			if (times[i] <= overlappedEndTime && times[i] >= overlappedStartTime) {
				num++;
			}
		}
		if (num == 0)
			return null;

		int[] coordSli = new int[num];
		long[] timeSli = new long[num];
		int value = 0;

		for (int i = 0; i < times.length; i++) {
			if (times[i] <= overlappedEndTime && times[i] >= overlappedStartTime) {
				coordSli[value] = count[i];
				timeSli[value] = times[i];
				value++;
			}
		}
		return factory.createMInt(coordSli, timeSli);
	}


	@Override
	public MGeometry first() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGeometry last() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGeometry at(int n) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long[] getTimes() {
		// TODO Auto-generated method stub
		return times;
	}

	@Override
	public Coordinate[] getCoords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double veolocityAtTimeTime(long instant) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double accelerationAtTimeTime(long instant) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long timeAtCummulativeDistance(double time) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MGeometry snapToGrid(int cellSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Geometry bbox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MPeriod btime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Period time() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Period time(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long startTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long endTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MDouble timeToDistance() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ArrayList<Long> timeAtDistance(double distance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MDouble area() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MDouble direction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MInt count() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MDouble velocity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGeometry slice(Polygon pol) {
		// TODO Auto-generated method stub
		return null;
	}


}

