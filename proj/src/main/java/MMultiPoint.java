package com.awarematics.postmedia.types.mediamodel;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

public class MMultiPoint extends MGeometry {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MPoint[] mpoints;
	// example : MMUltiPoint (((x y) t, (x y) t, ...) , ((x y) t, ... ), ...)
	MMultiPoint mmu;

	public MMultiPoint(MMultiPoint mmu) {
		mpoints = mmu.mpoints;
	}

	public MMultiPoint() {
		// TODO Auto-generated constructor stub
	}

	public MMultiPoint(MPoint[] mpoints) {
		this.mpoints = mpoints;
	}


	public String toGeoString() {
		String mmuString = "MMULTIPOINT (";
		for (int i = 0; i < mpoints.length; i++) {
			if (i == 0) {
				String tempMmu = mpoints[i].toGeoString().replaceAll("MPOINT ", "");
				mmuString = mmuString + tempMmu;
			} else {
				String tempMmu = mpoints[i].toGeoString().replaceAll("MPOINT ", "");
				mmuString = mmuString + ", " + tempMmu;
			}
		}
		mmuString = mmuString + ")";
		return mmuString;
	}

	@Override
	public int numOf() {
		// TODO Auto-generated method stub
		return 0;
	}


	public MPoint[] getMpoints() {
		return mpoints;
	}

	public void setMpoints(MPoint[] mpoints) {
		this.mpoints = mpoints;
	}

	public MMultiPoint getMmu() {
		return mmu;
	}

	public void setMmu(MMultiPoint mmu) {
		this.mmu = mmu;
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
		return null;
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

