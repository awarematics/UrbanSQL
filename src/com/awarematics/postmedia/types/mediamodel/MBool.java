package com.awarematics.postmedia.types.mediamodel;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

public class MBool extends MGeometry {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean[] bools;
	long[] times;
	

	public MBool(MBool mb) {
		this.bools = mb.bools;
		this.times = mb.times;
	}


	public MBool(boolean[] bools, long[] times) {
		this.bools = bools;
		this.times = times;
	}



	public String toGeoString() {
		String mboolString = "MBOOL (";
		for (int i = 0; i < numOf(); i++) {
			if (i == 0){
				mboolString = mboolString + bools[i] +  " " + times[i];
			}
			else{
				mboolString = mboolString + ", " + bools[i] +  " " + times[i];
			}
		}
		mboolString = mboolString + ")";
		return mboolString;
	}


	@Override
	public int numOf() {
		return bools.length;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGeometry first() {
		boolean[] bool = new boolean[1];
		long[] time = new long[1];
		bool[0] = bools[0];
		time[0] = times[0];
		return new MBool(bool,time);
	}

	@Override
	public MGeometry last() {
		boolean[] bool = new boolean[1];
		long[] time = new long[1];
		bool[0] = bools[bools.length-1];
		time[0] = times[bools.length-1];
		return new MBool(bool,time);
	}

	@Override
	public MGeometry at(int n) {
		boolean[] bool = new boolean[1];
		long[] time = new long[1];
		bool[0] = bools[n-1];
		time[0] = times[n-1];
		return new MBool(bool,time);
	}



	@Override
	public Coordinate[] getCoords() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean[] getBools() {
		return bools;
	}


	public void setBools(boolean[] bools) {
		this.bools = bools;
	}


	public void setTimes(long[] times) {
		this.times = times;
	}

	public long[] getTimes() {
		return times;
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

