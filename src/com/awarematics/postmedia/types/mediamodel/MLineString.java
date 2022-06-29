package com.awarematics.postmedia.types.mediamodel;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

public class MLineString extends MGeometry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LineString[] points;
	long[] times;

	public MLineString(MLineString mp) {
		points = mp.points;
		times = mp.times;
	}
	public MLineString() {
		// TODO Auto-generated constructor stub
	}
	public MLineString(LineString[] points, long[] value) {
		this.points = points;
		this.times = value;
	}

	@Override
	public String toGeoString() {
		String mpointString = "MLINESTRING (";
		for (int i = 0; i < numOf(); i++) {
			if (i == 0) {
				mpointString = mpointString + "(" + points[i] + ") " + times[i];
			} else {
				mpointString = mpointString + ", (" + points[i] + ") " + times[i];
			}
		}
		mpointString = mpointString + ")";
		return mpointString;
	}

	@Override
	public int numOf() {
		// TODO Auto-generated method stub
		return times.length;
	}

	public LineString[] getPoints() {
		return points;
	}

	public void setPoints(LineString[] points) {
		this.points = points;
	}

	public long[] getTimes() {
		return times;
	}

	public void setTimes(long[] times) {
		this.times = times;
	}

	@Override
	public Geometry snapshot(long ts) {
		PrecisionModel precisionModel = new PrecisionModel(1000);
		GeometryFactory geometryFactorys = new GeometryFactory(precisionModel, 0);
		long current_time = ts;

		for (int i = 0; i < numOf(); i++) {
			long t = times[i];
			if (t == current_time) {

				Coordinate[] coo = new Coordinate[points[i].getCoordinates().length];
				for (int p = 0; p < coo.length; p++) {
					double x = points[i].getCoordinates()[p].x;
					double y = points[i].getCoordinates()[p].y;
					coo[p] = new Coordinate();
					coo[p].x = x;
					coo[p].y = y;
				}
				LineString pol = geometryFactorys.createLineString(coo);
				return pol;
			}
		}
		int temp = 0; long start = 0; long end = 0;
		for (int i = numOf()-1; i >=0; i--) {
			long t = times[i];
			if (t < current_time) {
				start = t;
			}	
			if (t > current_time && temp != 1) {
				end = t;
				double d;
				double dis;
				if(1 !=0){
					Coordinate[] coo = new Coordinate[points[i].getCoordinates().length];
					for (int p = 0; p < coo.length; p++) {
						d = d (points[i].getCoordinates()[p].x,points[i-1].getCoordinates()[p].x, points[i].getCoordinates()[p].y, points[i-1].getCoordinates()[p].y);
						dis = (current_time - start) * d / (end - start);
						double cy = (dis * (points[i].getCoordinates()[p].y- points[i-1].getCoordinates()[p].y)) / d + points[i-1].getCoordinates()[p].y;
						double cx = (dis * (points[i].getCoordinates()[p].x - points[i-1].getCoordinates()[p].x)) / d + points[i-1].getCoordinates()[p].x;
						coo[p] = new Coordinate();
						coo[p].x = cx;
						coo[p].y = cy;
					}
					LineString pol = geometryFactorys.createLineString(coo);
					temp = 1;
					return pol;
				}
			}
		}
		return null;
	}
	private double d(double d, double e, double f, double g) {
		return Math.sqrt((d-e)*(d-e)+(f-g)*(f-g));
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

