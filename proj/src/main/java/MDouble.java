package com.awarematics.postmedia.types.mediamodel;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/*
 *  MDouble (x y, x y, x y)  
 */
public class MDouble extends MGeometry  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double[] value;
	long[] t;

	public MDouble(MDouble mp) {
		this.value = mp.value;
		this.t = mp.t;
	}

	public MDouble() {
		// TODO Auto-generated constructor stub
	}

	public MDouble(double[] value, long[] times) {
		this.value = value;
		this.t = times;
	}

	public ArrayList<Timestamp> NormalizeByTime(double x)  {
		return null;
	}

	
	@Override
	public String toGeoString() {
		String mpointString = "MDOUBLE (";
		for (int i = 0; i < numOf(); i++) {
			if (i == 0) {
				mpointString = mpointString + value[i] + " " + t[i];
			} else {
				mpointString = mpointString + ", " + value[i] + " " + t[i];
			}
		}
		mpointString = mpointString + ")";
		return mpointString;
	}



	public double[] getValue() {
		return value;
	}

	public void setValue(double[] value) {
		this.value = value;
	}

	public long[] getT() {
		return t;
	}

	public void setT(long[] t) {
		this.t = t;
	}

	@Override
	public int numOf() {
		return value.length;
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
	public MGeometry atomize(long x2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MGeometry lattice(long duration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGeometry lattice(MDuration duration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Object arg0) {
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
		return t;
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

