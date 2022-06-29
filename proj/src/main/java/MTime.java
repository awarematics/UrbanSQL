package com.awarematics.postmedia.types.mediamodel;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;


public class MTime extends MGeometry {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/*
	public static long btime(MGeometry mg){	
		return mg.getTimes()[0];
	}
	*/
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
	public MGeometry atomize(long duration) {
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
	public int numOf() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long[] getTimes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toGeoString() {
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
