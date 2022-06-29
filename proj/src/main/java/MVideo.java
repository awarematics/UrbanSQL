package com.awarematics.postmedia.types.mediamodel;

import java.util.ArrayList;
import java.util.Arrays;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import com.awarematics.postmedia.mgeom.MGeometryFactory;

public class MVideo extends MGeometry {
	/**
	 * 'MVideo((uri , startTime, mpoint, frames, fov),(...))'
	 */
	private static final long serialVersionUID = 1L;
	String uri;
	MPoint mt;
	String[] annotationJson;
	long[] creationTime;
	FoV fov;
	Frame[] frame;

	public MVideo() {

	}

	public MVideo(String uri, MPoint mt, String[] annotationJson, long[] creationTime, FoV fov, Frame[] frame) {
		this.uri = uri;
		this.annotationJson = annotationJson;
		this.creationTime = creationTime;
		this.fov = fov;
		this.frame = frame;
		this.mt = mt;
	}

	public MVideo(MVideo mv) {
		uri = mv.uri;
		annotationJson = mv.annotationJson;
		creationTime = mv.creationTime;
		fov = mv.fov;
		frame = mv.frame;
		mt = mv.mt;
	}

	@Override
	public Geometry snapshot(long instant) {
		int searchedPosition = 0;
		if (creationTime == null)
			return null;
		if ((creationTime.length > 0) && ((instant < creationTime[0]) || (instant > creationTime[creationTime.length - 1])))
			return null;
		searchedPosition = Arrays.binarySearch(creationTime, instant);
		if (searchedPosition >= 0) {
			Polygon coord = genFoVArea(this.mt.getCoords()[searchedPosition].x, this.mt.getCoords()[searchedPosition].x,
					this.frame[searchedPosition].fov);
			return coord;
		}
		int startPosition = (searchedPosition * -1) - 2;
		int endPosition = (searchedPosition * -1) - 1;
		long startTime = creationTime[startPosition];
		long endTime = creationTime[endPosition];
		// Assure endTime is not equal startTime
		double dx = (instant - startTime) * (mt.getCoords()[endPosition].x - mt.getCoords()[startPosition].x)
				/ (endTime - startTime);
		double dy = (instant - startTime) * (mt.getCoords()[endPosition].y - mt.getCoords()[startPosition].y)
				/ (endTime - startTime);
		Polygon pol = genFoVArea(mt.getCoords()[startPosition].x + dx, mt.getCoords()[startPosition].y + dy,
				this.frame[startPosition].fov);
		return pol;
	}

	@Override
	public MGeometry atomize(long duration) {
		long str_start = creationTime[0];
		long str_end = creationTime[numOf() - 1];
		long now = 0;
		// ArrayList<String> annotationJsonNor = new ArrayList<String>();
		ArrayList<Long> creationTimeNor = new ArrayList<Long>();
		MPoint mtNor = null;
		ArrayList<Frame> frameNor = new ArrayList<Frame>();
		FoV fovNor = null;
		for (int i = 0; i < numOf(); i++)
			for (long j = now; j < str_end; j += duration) {
				if (now == 0) {
					now = creationTime[i] - creationTime[i] % duration;
				} else if (now >= str_end) {
					break;
				} else {
					now = now + duration;
				}
				if (now <= str_end && now >= str_start) {
					creationTimeNor.add(now);
					int num = 0;
					Frame fr = new Frame();
					fr.setCoos(mt.snapshot(now).getCoordinate());
					for (int k = 1; k < numOf(); k++)
						if (now <= creationTime[k]) {
							num = k - 1;
							break;
						}
					fr.setFov(frame[num].getFov());
					fr.setRelativeTime(now);
					frameNor.add(fr);
				}
			}
		mtNor = (MPoint) mt.atomize(duration);
		String uriNor = uri;
		fovNor = fov;
		String[] annotationJsonN = new String[frameNor.size()];
		long[] creationTimeN = new long[frameNor.size()];
		Frame[] frameN = new Frame[frameNor.size()];
		for (int i = 0; i < frameNor.size(); i++)
			frameN[i] = frameNor.get(i);
		MGeometryFactory geometryFactory = new MGeometryFactory();
		return geometryFactory.createMVideo(uriNor, mtNor, annotationJsonN, creationTimeN, fovNor, frameN);
	}

	@Override
	public MGeometry lattice(long duration) {
		MVideo pt = (MVideo) atomize(duration);
		MGeometryFactory geometryFactory = new MGeometryFactory();
		Coordinate[] coords = new Coordinate[2];
		long[] creationTime = new long[2];
		Frame[] frameN = new Frame[2];
		coords[0] = pt.mt.getCoords()[0];
		coords[1] = pt.mt.getCoords()[pt.numOf() - 1];
		creationTime[0] = pt.startTime();
		creationTime[1] = pt.endTime();
		frameN[0] = pt.frame[0];
		frameN[1] = pt.frame[pt.numOf() - 1];
		MPoint mp = geometryFactory.createMPoint(coords, creationTime);
		return geometryFactory.createMVideo(uri, mp, annotationJson, pt.creationTime, fov, frameN);
	}

	@Override
	public int numOf() {
		return mt.numOf();
	}

	@Override
	public String toGeoString() {
		String mphotoString = "MVIDEO (" + uri + ", " + mt.toGeoString() + ", FRAME (";
		for (int i = 0; i < numOf(); i++) {
			 System.out.println( frame[i].fov.toGeoString());
			if (i == 0)
				mphotoString += frame[i].fov.toGeoString();
			else
				mphotoString += ", " + frame[i].fov.toGeoString();
		}
		return mphotoString + "))";
	}

	@SuppressWarnings("deprecation")
	@Override
	public Geometry spatial() {
		GeometryFactory geo = new GeometryFactory();
		if (numOf() > 1)
			return geo.createLineString(mt.getCoords());
		else
			return geo.createMultiPoint(mt.getCoords());
	}

	@Override
	public long getDuration() {
		// TODO Auto-generated method stub
		return 0;
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
		if (creationTime == null)
			return null;
		if ((toTime < creationTime[0]) || (fromTime > creationTime[creationTime.length - 1]))
			return null;
		if (fromTime > toTime)
			return null;
		long overlappedStartTime = Math.max(creationTime[0], fromTime);
		long overlappedEndTime = Math.min(creationTime[creationTime.length - 1], toTime);
		factory = new MGeometryFactory();
		if (creationTime.length == 1) {
			return factory.createMVideo(uri, mt, annotationJson, creationTime, fov, frame);
		}
		int num = 0;
		for (int i = 0; i < creationTime.length; i++)
			if (creationTime[i] <= overlappedEndTime && creationTime[i] >= overlappedStartTime)
				num++;
		if (num == 0)
			return null;
		MPoint mtNor = (MPoint) mt.slice(fromTime, toTime);
		// String[] annotationJsonNor = new String[mtNor.numOf()];
		long[] creationTimeNor = new long[mtNor.numOf()];
		Frame[] frameNor = new Frame[mtNor.numOf()];

		int value = 0;
		for (int i = 0; i < creationTime.length; i++) {
			if (creationTime[i] <= overlappedEndTime && creationTime[i] >= overlappedStartTime) {
				creationTimeNor[value] = creationTime[i];
				frameNor[value] = frame[i];
				value++;
			}
		}
		FoV fovNor = frameNor[0].fov;
		return factory.createMVideo(uri, mtNor, annotationJson, creationTimeNor, fovNor, frameNor);
	}

	@Override
	public MGeometry first() {
		MPoint mtNor = (MPoint) mt.first();
		// String[] annotationJsonNor = new String[1];
		long[] creationTimeNor = new long[1];
		Frame[] frameNor = new Frame[1];
		creationTimeNor[0] = creationTime[0];
		frameNor[0] = frame[0];
		return new MVideo(uri, mtNor, annotationJson, creationTimeNor, fov, frameNor);
	}

	@Override
	public MGeometry last() {
		MPoint mtNor = (MPoint) mt.last();
		// String[] annotationJsonNor = new String[1];
		long[] creationTimeNor = new long[1];
		Frame[] frameNor = new Frame[1];
		creationTimeNor[0] = creationTime[numOf() - 1];
		frameNor[0] = frame[numOf() - 1];
		return new MVideo(uri, mtNor, annotationJson, creationTimeNor, fov, frameNor);
	}

	@Override
	public MGeometry at(int n) {
		MPoint mtNor = (MPoint) mt.at(n);
		// String[] annotationJsonNor = new String[1];
		long[] creationTimeNor = new long[1];
		Frame[] frameNor = new Frame[1];
		creationTimeNor[0] = creationTime[n - 1];
		frameNor[0] = frame[n - 1];
		return new MVideo(uri, mtNor, annotationJson, creationTimeNor, fov, frameNor);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public MPoint getMt() {
		return mt;
	}

	public void setMt(MPoint mt) {
		this.mt = mt;
	}

	public String[] getAnnotationJson() {
		return annotationJson;
	}

	public void setAnnotationJson(String[] annotationJson) {
		this.annotationJson = annotationJson;
	}

	public long[] getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long[] creationTime) {
		this.creationTime = creationTime;
	}

	public FoV getFov() {
		return fov;
	}

	public void setFov(FoV fov) {
		this.fov = fov;
	}

	public Frame[] getFrame() {
		return frame;
	}

	public void setFrame(Frame[] frame) {
		this.frame = frame;
	}

	private Polygon genFoVArea(double x, double y, FoV fov) {
		int times = 1;
		double x4 = (double) x;
		double y4 = (double) y;

		double x2 = (double) x + fov.getDistance() * 2 / Math.sqrt(3)
				* Math.sin(Math.toRadians(fov.direction2d + (fov.horizontalAngle) / 2)) * times;
		double y2 = (double) y + fov.getDistance() * 2 / Math.sqrt(3)
				* Math.cos(Math.toRadians(fov.direction2d + (fov.horizontalAngle) / 2)) * times;// left
		double x3 = (double) x + fov.getDistance() * 2 / Math.sqrt(3)
				* Math.sin(Math.toRadians(fov.direction2d - (fov.horizontalAngle) / 2)) * times;
		double y3 = (double) y + fov.getDistance() * 2 / Math.sqrt(3)
				* Math.cos(Math.toRadians(fov.direction2d - (fov.horizontalAngle) / 2)) * times;// right
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate[] coor1 = new Coordinate[4];
		coor1[0] = new Coordinate(x4, y4);
		coor1[1] = new Coordinate(x2, y2);
		coor1[2] = new Coordinate(x3, y3);
		coor1[3] = new Coordinate(x4, y4);
		LinearRing line = geometryFactory.createLinearRing(coor1);
		Polygon pl1 = geometryFactory.createPolygon(line, null);
		return pl1;
	}

	@Override
	public long[] getTimes() {
		return mt.times;
	}

	@Override
	public double veolocityAtTimeTime(long instant) {
		double veolocity = 0;
		int searchedPosition = 0;
		if (creationTime == null) {
			return 0;
		}
		if ((creationTime.length > 0)
				&& ((instant < creationTime[0]) || (instant > creationTime[creationTime.length - 1]))) {
			return 0;
		}
		searchedPosition = Arrays.binarySearch(creationTime, instant);
		if (searchedPosition >= 0) {
			Coordinate coord = this.mt.coords[searchedPosition];
			if (searchedPosition == 0)
				return 0;
			else {
				veolocity = calDistance(coord, this.mt.coords[searchedPosition - 1])
						/ (instant - this.creationTime[searchedPosition - 1]);
				return veolocity;
			}
		}
		int startPosition = (searchedPosition * -1) - 2;
		int endPosition = (searchedPosition * -1) - 1;
		long startTime = creationTime[startPosition];
		long endTime = creationTime[endPosition];

		// Assure endTime is not equal startTime
		double dx = (instant - startTime) * (mt.coords[endPosition].x - mt.coords[startPosition].x)
				/ (endTime - startTime);
		double dy = (instant - startTime) * (mt.coords[endPosition].y - mt.coords[startPosition].y)
				/ (endTime - startTime);
		Coordinate newCoord = new Coordinate(mt.coords[startPosition].x + dx, mt.coords[startPosition].y + dy);
		veolocity = calDistance(newCoord, mt.coords[startPosition]) / (instant - creationTime[startPosition]);
		return veolocity;
	}

	@Override
	public double accelerationAtTimeTime(long instant) {
		double veolocityt = 0;
		double veolocity0 = 0;
		double acceleration = 0;
		int searchedPosition = 0;
		if (creationTime == null) {
			return 0;
		}
		if ((creationTime.length > 0)
				&& ((instant < creationTime[0]) || (instant > creationTime[creationTime.length - 1]))) {
			return 0;
		}
		searchedPosition = Arrays.binarySearch(creationTime, instant);
		if (searchedPosition >= 0) {
			Coordinate coord = this.mt.coords[searchedPosition];
			if (searchedPosition == 0)
				return 0;
			else {
				veolocityt = calDistance(coord, this.mt.coords[searchedPosition - 1])
						/ (instant - this.creationTime[searchedPosition - 1]);
				veolocity0 = veolocityAtTimeTime(this.creationTime[searchedPosition - 1]);
				acceleration = 2 * (veolocityt - veolocity0 * (instant - this.creationTime[searchedPosition - 1]))
						/ (Math.pow((instant - this.creationTime[searchedPosition - 1]), 2));
				return acceleration * 1000;
			}
		}
		int startPosition = (searchedPosition * -1) - 2;
		int endPosition = (searchedPosition * -1) - 1;
		long startTime = creationTime[startPosition];
		long endTime = creationTime[endPosition];

		// Assure endTime is not equal startTime
		double dx = (instant - startTime) * (mt.coords[endPosition].x - mt.coords[startPosition].x)
				/ (endTime - startTime);
		double dy = (instant - startTime) * (mt.coords[endPosition].y - mt.coords[startPosition].y)
				/ (endTime - startTime);
		Coordinate newCoord = new Coordinate(mt.coords[startPosition].x + dx, mt.coords[startPosition].y + dy);
		veolocityt = calDistance(newCoord, mt.coords[startPosition]) / (instant - creationTime[startPosition]);
		veolocity0 = veolocityAtTimeTime(creationTime[startPosition]);
		acceleration = 2 * (veolocityt - veolocity0 * (instant - creationTime[startPosition]))
				/ (Math.pow((instant - creationTime[startPosition]), 2));
		return acceleration * 1000;
	}

	@Override
	public long timeAtCummulativeDistance(double distance) {
		double[] value = new double[numOf()];
		long time = -1;
		value[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			value[i] = value[i - 1] + calDistance(mt.coords[i], mt.coords[i - 1]);
		}
		for (int i = 1; i < numOf(); i++) {
			if (distance >= value[i - 1] && distance < value[i]) {
				double leftpart = distance - value[i - 1];
				double total = value[i] - value[i - 1];
				time = (long) ((creationTime[i] - creationTime[i - 1]) * leftpart / total + creationTime[i - 1]);
			}
		}
		return time;
	}

	@Override
	public MGeometry snapToGrid(int cellSize) {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		MPoint mtNor = (MPoint) mt.snapToGrid(cellSize);
		return mgeometryFactory.createMVideo(uri, mtNor, annotationJson, creationTime, fov, frame);
	}

	@Override
	public Geometry bbox() {
		Geometry gg = spatial();
		return gg.getEnvelope();
	}

	@Override
	public MPeriod btime() {
		// System.out.println("gg");
		MPeriod mp = new MPeriod();
		mp.period = new Period[numOf()];
		for (int i = 1; i < numOf(); i++) {
			mp.period[i] = new Period();
			mp.period[i].from = (first().getTimes()[0]);
			mp.period[i].to = (at(i + 1).getTimes()[0]);
		}
		return mp;
	}

	@Override
	public Period time() {
		Period[] mp = new Period[numOf()];
		for (int i = 1; i < numOf(); i++) {
			mp[i] = new Period();
			mp[i].from = (first().getTimes()[0]);
			mp[i].to = (at(i + 1).getTimes()[0]);
		}
		return mp[numOf() - 1];
	}

	@Override
	public Period time(int n) {
		Period[] mp = new Period[numOf()];
		for (int i = 1; i < numOf(); i++) {
			// mp.period[i] = new Period();
			mp[i] = new Period();
			mp[i].from = first().getTimes()[0];
			mp[i].to = at(i + 1).getTimes()[0];
		}
		return mp[n];
	}

	@Override
	public long startTime() {
		return getTimes()[0];
	}

	@Override
	public long endTime() {
		// TODO Auto-generated method stub
		return getTimes()[numOf() - 1];
	}

	@Override
	public MDouble timeToDistance() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] mdistance = new double[numOf()];
		mdistance[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			mdistance[i] = calDistance(mt.coords[i], mt.coords[0]);
		}
		return mgeometryFactory.createMDouble(mdistance, creationTime);
	}

	@Override
	public ArrayList<Long> timeAtDistance(double distance) {
		double[] mdistance = new double[numOf()];
		ArrayList<Long> time = new ArrayList<Long>();
		mdistance[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			mdistance[i] = calDistance(mt.coords[i], mt.coords[0]);
		}
		for (int i = 1; i < numOf(); i++) {
			if ((distance >= mdistance[i - 1] && distance < mdistance[i])
					|| (distance < mdistance[i - 1] && distance >= mdistance[i])) {
				double leftpart = distance - mdistance[i - 1];
				double total = mdistance[i] - mdistance[i - 1];
				time.add((long) ((creationTime[i] - creationTime[i - 1]) * leftpart / total + creationTime[i - 1]));
			}
		}
		return time;
	}

	@Override
	public MDouble area() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] area = new double[numOf()];
		for (int i = 0; i < numOf(); i++) {
			area[i] = genFoVArea( mt.coords[i].x, mt.coords[i].y, frame[i].fov).getArea();
		}
		return mgeometryFactory.createMDouble(area, creationTime);
	}

	@Override
	public MDouble direction() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] directions = new double[numOf()];
		for (int i = 0; i < numOf(); i++) {
			directions[i] = frame[i].fov.direction2d;
		}
		return mgeometryFactory.createMDouble(directions, creationTime);
	}

	@Override
	public MInt count() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MDouble velocity() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] veolocity = new double[numOf()];
		veolocity[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			veolocity[i] = calDistance(mt.coords[i], mt.coords[i - 1]) / (creationTime[i] - creationTime[i - 1]);
		}

		return mgeometryFactory.createMDouble(veolocity, creationTime);
	}

	@Override
	public MGeometry slice(Polygon pol) {
		//GeometryFactory factory = new GeometryFactory();
		if (this.spatial().intersects(pol)) {			
			MGeometryFactory factory = new MGeometryFactory();
			factory = new MGeometryFactory();
			if (mt.numOf() == 1) {
				return factory.createMVideo(uri, mt, annotationJson, creationTime, fov, frame);
			}
			Polygon[] listpolygon = new Polygon[mt.numOf()];
			for (int i = 0; i < mt.numOf(); i++)
				listpolygon[i] = (Polygon) this.snapshot(creationTime[i]);
			MPoint mtNor = (MPoint) mt.slice(pol);
		
			FoV fovNor = new FoV();
			ArrayList<Frame> frameNor = new ArrayList<Frame>();
			ArrayList<Long> creationTimeNor = new ArrayList<Long>();			
			for (int i = 0; i < mt.numOf(); i++) {
				if (listpolygon[i].intersects(pol)) {
					creationTimeNor.add(creationTime[i]);
					frameNor.add(frame[i]);
				}
			}
			if(frameNor.size()==0) return null;
			else
			fovNor = frameNor.get(0).fov;
			long[] ttt = new long[creationTimeNor.size()];
			Frame[] fff = new Frame[creationTimeNor.size()];
			for(int i=0;i< creationTimeNor.size();i++)
			{
				ttt[i] = creationTimeNor.get(i);
				fff[i] = frameNor.get(i);
			}
			return factory.createMVideo(uri, mtNor, annotationJson, ttt, fovNor, fff);
		}
		return null;
	}

	@Override
	public Coordinate[] getCoords() {
		// TODO Auto-generated method stub
		return mt.coords;
	}

}
