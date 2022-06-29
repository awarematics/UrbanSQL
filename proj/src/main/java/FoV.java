package com.awarematics.postmedia.types.mediamodel;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

public class FoV extends MGeometry {
	/**
	 * horizontalAngle double precision,
	 * verticalAngle double precision,
	 * direction2d double precision, // -value for fixed attatchment for heading. ex:-90-right fixed 
	 * direction3d double precision, 
	 * distance doubleprecision
	 */
	private static final long serialVersionUID = 1L;
	double horizontalAngle;
	double verticalAngle;
	double distance;
	double direction2d;
	double direction3d;

	public FoV(double horizontalAngle, double verticalAngle, double distance, double direction2d, double direction3d) {
		this.horizontalAngle = horizontalAngle;
		this.distance = distance;
		this.verticalAngle = verticalAngle;
		this.direction2d = direction2d;
		this.direction3d = direction3d;
	}

	public FoV() {
		// TODO Auto-generated constructor stub
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getVerticalAngle() {
		return verticalAngle;
	}

	public void setVerticalAngle(double verticalAngle) {
		this.verticalAngle = verticalAngle;
	}

	public double getHorizontalAngle() {
		return horizontalAngle;
	}

	public void setHorizontalAngle(double horizontalAngle) {
		this.horizontalAngle = horizontalAngle;
	}

	public double getDirection2d() {
		return direction2d;
	}

	public void setDirection2d(double direction2d) {
		this.direction2d = direction2d;
	}

	public double getDirection3d() {
		return direction3d;
	}

	public void setDirection3d(double direction3d) {
		this.direction3d = direction3d;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
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
	public MGeometry lattice(long duration) {
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
	public Geometry snapshot(long ts) {
		// TODO Auto-generated method stub
		return null;
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
	public String toGeoString() {
		// TODO Auto-generated method stub
		return "("+ horizontalAngle +" "+ verticalAngle +" "+ distance +" "+ direction2d +" "+ direction3d +")";
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
	public long timeAtCummulativeDistance(double distance) {
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
