package com.awarematics.postmedia.types.mediamodel;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceComparator;
import org.locationtech.jts.geom.CoordinateSequenceFilter;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryComponentFilter;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.GeometryFilter;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;

import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.mgeom.MGeometryFactory;

@SuppressWarnings("unused")
public class MPoint extends MGeometry {

	private static final long serialVersionUID = 1L;
	Coordinate[] coords;
	long[] times;

	public MPoint(Coordinate[] coords, long[] times) {
		this.coords = coords.clone();
		this.times = times.clone();
	}

	public MPoint clone() {
		return new MPoint(coords, times);
	}

	@Override
	public Geometry snapshot(long instant) {
		int searchedPosition = 0;
		GeometryFactory factory = null;
		if (times == null) {
			return null;
		}
		if ((times.length > 0) && ((instant < times[0]) || (instant > times[times.length - 1]))) {
			return null;
		}
		factory = new GeometryFactory();
		searchedPosition = Arrays.binarySearch(times, instant);
		if (searchedPosition >= 0) {
			Coordinate coord = this.coords[searchedPosition];
			return factory.createPoint(coord);
		}
		int startPosition = (searchedPosition * -1) - 2;
		int endPosition = (searchedPosition * -1) - 1;
		long startTime = times[startPosition];
		long endTime = times[endPosition];

		// Assure endTime is not equal startTime
		double dx = (instant - startTime) * (coords[endPosition].x - coords[startPosition].x) / (endTime - startTime);
		double dy = (instant - startTime) * (coords[endPosition].y - coords[startPosition].y) / (endTime - startTime);
		Coordinate newCoord = new Coordinate(coords[startPosition].x + dx, coords[startPosition].y + dy);
		return factory.createPoint(newCoord);
	}

	@Override
	public MGeometry slice(long fromTime, long toTime) {
		// do it defensive programming
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
			return factory.createMPoint(coords, times);
		}
		int num = 0;
		for (int i = 0; i < times.length; i++) {
			if (times[i] <= overlappedEndTime && times[i] >= overlappedStartTime) {
				num++;
			}
		}
		if (num == 0)
			return null;

		Coordinate[] coordSli = new Coordinate[num];
		long[] timeSli = new long[num];
		int value = 0;
		for (int i = 0; i < times.length; i++) {
			if (times[i] <= overlappedEndTime && times[i] >= overlappedStartTime) {
				coordSli[value] = coords[i];
				timeSli[value] = times[i];
				value++;
			}
		}
		return factory.createMPoint(coordSli, timeSli);
	}

	@Override
	public MGeometry lattice(long duration) {
		MGeometry pt = atomize(duration);
		Coordinate[] coordsLat = new Coordinate[2];
		long[] timesLat = new long[2];
		coordsLat[0] = ((MPoint) pt).getCoords()[0];
		coordsLat[1] = ((MPoint) pt).getCoords()[pt.numOf() - 1];
		timesLat[0] = pt.getTimes()[0];
		timesLat[1] = pt.getTimes()[pt.numOf() - 1];
		MGeometryFactory geometryFactory = new MGeometryFactory();
		return geometryFactory.createMPoint(coordsLat, timesLat);
	}

	@Override
	public MGeometry atomize(long duration) {
		long str_start = times[0];
		long str_end = times[numOf() - 1];
		long now = 0;
		ArrayList<Coordinate> coordsNor = new ArrayList<Coordinate>();
		ArrayList<Long> timesNor = new ArrayList<Long>();
		for (int i = 0; i < numOf(); i++) {
			for (long j = now; j < str_end; j += duration) {
				if (now <= str_end && now >= str_start) {
					coordsNor.add(snapshot(now).getCoordinate());
					timesNor.add(now);
				}
				if (now == 0) {
					now = times[i];
				} else if (now > str_end) {
					break;
				} else {
					now = now + duration;
				}		
			}
		}
		// notice: the method is not support in java-version 8 version 9 is OK
		long[] tempList = new long[timesNor.size()];
		for(int i=0;i<timesNor.size();i++)
			tempList[i] = timesNor.get(i);	
		Coordinate[] coords = new Coordinate[coordsNor.size()];
		for (int i = 0; i < coordsNor.size(); i++)
			coords[i] = coordsNor.get(i);
		MGeometryFactory geometryFactory = new MGeometryFactory();
		return geometryFactory.createMPoint(coords, tempList);
	}

	@Override
	public long getDuration() {
		long start = times[0];
		long end = times[times.length - 1];
		long interval = end - start;
		return interval;
	}

	@Override
	public String toGeoString() {
		String mpointString = "MPOINT (";
		for (int i = 0; i < numOf(); i++) {
			if (i == 0) {
				if( String.valueOf(coords[i].z) == "NaN")
					mpointString = mpointString + "(" + coords[i].x + " " + coords[i].y + ")" + " " + times[i];
				else
					mpointString = mpointString + "(" + coords[i].x + " " + coords[i].y + " " + coords[i].z + ")" + " " + times[i];
			} else {
				if(String.valueOf(coords[i].z) == "NaN")
					mpointString = mpointString + ", (" + coords[i].x + " " + coords[i].y + ")" + " " + times[i];
				else
					mpointString = mpointString + ", (" + coords[i].x + " " + coords[i].y + " " + coords[i].z + ")" + " " + times[i];
			}
		}
		mpointString = mpointString + ")";
		return mpointString;
	}

	@Override
	public int numOf() {
		return coords.length;
	}

	@Override
	public MGeometry lattice(MDuration duration) {
		return null;
	}

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

	@SuppressWarnings("deprecation")
	@Override
	public Geometry spatial() {
		GeometryFactory geo = new GeometryFactory();
		if(numOf()>1)
			return geo.createLineString(coords);
		else
			return geo.createMultiPoint(coords);
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public MGeometry first() {
		Coordinate[] coord = new Coordinate[1];
		long[] time = new long[1];
		coord[0] = coords[0];
		time[0] = times[0];
		return new MPoint(coord, time);
	}

	@Override
	public MGeometry last() {
		Coordinate[] coord = new Coordinate[1];
		long[] time = new long[1];
		coord[0] = coords[coords.length - 1];
		time[0] = times[coords.length - 1];
		return new MPoint(coord, time);
	}

	@Override
	public MGeometry at(int n) {
		Coordinate[] coord = new Coordinate[1];
		long[] time = new long[1];
		coord[0] = coords[n - 1];
		time[0] = times[n - 1];
		return new MPoint(coord, time);
	}

	@Override
	public double veolocityAtTimeTime(long instant) {
		double  veolocity = 0;
		int searchedPosition = 0;
		if (times == null) {
			return 0;
		}
		if ((times.length > 0) && ((instant < times[0]) || (instant > times[times.length - 1]))) {
			return 0;
		}
		searchedPosition = Arrays.binarySearch(times, instant);
		if (searchedPosition >= 0) {
			Coordinate coord = this.coords[searchedPosition];
			if(searchedPosition==0)
				return 0;
			else{
				veolocity = calDistance(coord, this.coords[searchedPosition-1])/ (instant-this.times[searchedPosition-1]);
				return veolocity;
			}
		}
		int startPosition = (searchedPosition * -1) - 2;
		int endPosition = (searchedPosition * -1) - 1;
		long startTime = times[startPosition];
		long endTime = times[endPosition];
		// Assure endTime is not equal startTime
		double dx = (instant - startTime) * (coords[endPosition].x - coords[startPosition].x) / (endTime - startTime);
		double dy = (instant - startTime) * (coords[endPosition].y - coords[startPosition].y) / (endTime - startTime);
		Coordinate newCoord = new Coordinate(coords[startPosition].x + dx, coords[startPosition].y + dy);
		veolocity = calDistance(newCoord, coords[startPosition])/ (instant-times[startPosition]);
		return veolocity;
	}

	@Override
	public double accelerationAtTimeTime(long instant) {
		double  veolocityt = 0;
		double  veolocity0 = 0;
		double  acceleration =0;
		int searchedPosition = 0;
		if (times == null) {
			return 0;
		}

		if ((times.length > 0) && ((instant < times[0]) || (instant > times[times.length - 1]))) {
			return 0;
		}
		searchedPosition = Arrays.binarySearch(times, instant);
		if (searchedPosition >= 0) {
			Coordinate coord = this.coords[searchedPosition];
			if(searchedPosition==0)
				return 0;
			else{
				veolocityt = calDistance(coord, this.coords[searchedPosition-1])/ (instant-this.times[searchedPosition-1]);
				veolocity0 = veolocityAtTimeTime(this.times[searchedPosition-1]);
				acceleration = 2*(veolocityt - veolocity0 * (instant-this.times[searchedPosition-1]))/(Math.pow((instant-this.times[searchedPosition-1]), 2));
				return acceleration*1000;
			}
		}

		int startPosition = (searchedPosition * -1) - 2;
		int endPosition = (searchedPosition * -1) - 1;
		long startTime = times[startPosition];
		long endTime = times[endPosition];

		// Assure endTime is not equal startTime
		double dx = (instant - startTime) * (coords[endPosition].x - coords[startPosition].x) / (endTime - startTime);
		double dy = (instant - startTime) * (coords[endPosition].y - coords[startPosition].y) / (endTime - startTime);
		Coordinate newCoord = new Coordinate(coords[startPosition].x + dx, coords[startPosition].y + dy);
		veolocityt = calDistance(newCoord, coords[startPosition])/ (instant-times[startPosition]);
		veolocity0 = veolocityAtTimeTime(times[startPosition]);
		acceleration = 2*(veolocityt - veolocity0 * (instant-times[startPosition]))/(Math.pow((instant-times[startPosition]), 2));
		return acceleration*1000;
	}

	@Override
	public long timeAtCummulativeDistance(double distance) {
		double[] value = new double[numOf()];
		long time = -1;
		value[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			value[i] = value[i - 1] + calDistance(coords[i], coords[i - 1]);
		}
		//System.out.println(distance);
		for (int i = 1; i < numOf(); i++) {
			if (distance >= value[i - 1] && distance < value[i]) {
				//System.out.println(value[i - 1]);
				double leftpart = distance - value[i - 1];
				double total = value[i] - value[i - 1];
				time = (long) ((times[i] - times[i - 1]) * leftpart / total + times[i - 1]);
			}
		}
		return time;
	}

	@Override
	public MGeometry snapToGrid(int cellSize) {

		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		Coordinate[] coordGrid = new Coordinate[numOf()];
		long[] timesGrid = times;
		for(int i=0;i< numOf();i++)
		{
			
				coordGrid[i] = new Coordinate();
				BigDecimal bgx = new BigDecimal(coords[i].x);
				coordGrid[i].x = bgx.setScale(cellSize, BigDecimal.ROUND_HALF_UP).doubleValue();
				BigDecimal bgy = new BigDecimal( coords[i].y);
				coordGrid[i].y = bgy.setScale(cellSize, BigDecimal.ROUND_HALF_UP).doubleValue();
		}	
		return mgeometryFactory.createMPoint(coordGrid, timesGrid);
	}

	@Override
	public Geometry bbox() {
		Geometry gg = spatial();
		return gg.getEnvelope();
	}

	@Override
	public MPeriod btime() {
		MPeriod mp = new MPeriod();
		mp.period = new Period[numOf()];
		for (int i = 1; i < numOf(); i++) {
			mp.period[i] = new Period();
			mp.period[i].from = (first().getTimes()[0]);
			mp.period[i].to = (at(i+1).getTimes()[0]);
		}
		return mp;
	}

	@Override
	public Period time() {
		Period[] mp = new Period[numOf()];
		for (int i = 1; i < numOf(); i++) {
			mp[i] = new Period();
			mp[i].from = (first().getTimes()[0]);
			mp[i].to = (at(i+1).getTimes()[0]);
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
			mp[i].to = at(i+1).getTimes()[0];
		}
		return mp[n];
	}

	@Override
	public long startTime() {
		return getTimes()[0];
	}

	@Override
	public long endTime() {
		return getTimes()[numOf() - 1];
	}
	 private static double EARTH_RADIUS = 6378.137;  
     
     private static double rad(double d) {  
         return d * Math.PI / 180.0;  
     }  

	public static double calDistance(Coordinate p1, Coordinate p2) {
		
		double x1 = p1.x;
		double y1 = p1.y;
		double x2 = p2.x;
		double y2 = p2.y;	
		 double radLat1 = rad(x1);  
         double radLat2 = rad(x2);  
         double a = radLat1 - radLat2;  
         double b = rad(y1) - rad(y2);  
         double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)  
                 + Math.cos(radLat1) * Math.cos(radLat2)  
                 * Math.pow(Math.sin(b / 2), 2)));  
         s = s * EARTH_RADIUS;  
         s = Math.round(s * 10000d) / 10000d;  
         s = s*1000;  
         return s;  
	}

	@Override
	public MDouble timeToDistance() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] mdistance = new double[numOf()];
		mdistance[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			mdistance[i] =  calDistance(coords[i], coords[0]);
		}	
		return mgeometryFactory.createMDouble(mdistance,times);
	}

	@Override
	public ArrayList<Long> timeAtDistance(double distance) {
		double[] mdistance = new double[numOf()];
		 ArrayList<Long> time = new  ArrayList<Long>();
		mdistance[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			mdistance[i] = calDistance(coords[i], coords[0]);
		}
		for (int i = 1; i < numOf(); i++) {
			if ((distance >= mdistance[i - 1] && distance < mdistance[i]) || (distance < mdistance[i - 1] && distance >= mdistance[i])) {
				double leftpart = distance - mdistance[i - 1];
				double total = mdistance[i] - mdistance[i - 1];
				time.add((long) ((times[i] - times[i - 1]) * leftpart / total + times[i - 1]));
			}
		}
		return time;
	}

	@Override
	public MDouble area() {
		return null;
	}

	@Override
	public MDouble direction() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] direction = new double[numOf()];
		direction = printDirection(coords);
		return mgeometryFactory.createMDouble(direction, times);
	}
	public static double[] printDirection(Coordinate[] coordsxy) {
		double[] k = new double[coordsxy.length];
		double start_x = coordsxy[0].x;
		double start_y = coordsxy[0].y;
		
		double[] result = new double[coordsxy.length];
		/*
		 * k[0]  start point maybe is a stop point for several seconds
		 */
		int num =0;
		for(int i=1;i<coordsxy.length;i++){

			if((coordsxy[i].x!=start_x||coordsxy[i].y!=start_y)&& num==0)
			{
				k[0] =  Math.asin((coordsxy[i].y-start_y)/Math.sqrt(((coordsxy[i].x -start_x) * (coordsxy[i].x -start_x)) + (coordsxy[i].y - start_y) * (coordsxy[i].y - start_y))); num=1;
				result[0] = Double.valueOf((k[0]* 180 / Math.PI));
			}
		}
		for (int i = 1; i < coordsxy.length; i++) {
			if( Math.sqrt(((coordsxy[i].x -coordsxy[i-1].x) * (coordsxy[i].x - coordsxy[i-1].x)) + (coordsxy[i].y - coordsxy[i-1].y) * (coordsxy[i].y- coordsxy[i-1].y))!=0){
				k[i] = Math.asin((coordsxy[i].y- coordsxy[i-1].y)/ Math.sqrt(((coordsxy[i].x -coordsxy[i-1].x) * (coordsxy[i].x - coordsxy[i-1].x)) + (coordsxy[i].y - coordsxy[i-1].y) * (coordsxy[i].y - coordsxy[i-1].y)));
			}
			else
			{
				k[i]=k[i-1];
			}
			result[i] = Double.valueOf((k[i]* 180 / Math.PI));
		}
		return result;
	}
	@Override
	public MInt count() {
		return null;
	}

	@Override
	public MDouble velocity() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] veolocity  = new double[numOf()];
		veolocity[0] = 0;
		for(int i =1; i< numOf();i++)
		{
			veolocity[i] = calDistance(coords[i],coords[i-1])/ (times[i]-times[i-1]);
		}	
		return mgeometryFactory.createMDouble(veolocity, times);
	}

	@Override
	public MGeometry slice(Polygon mpol) {
		MGeometryFactory factory = null;
		GeometryFactory geo = new GeometryFactory();
		if(this.spatial().intersects(mpol))
		{
			factory = new MGeometryFactory();
			ArrayList<Coordinate> cooarr = new ArrayList<Coordinate>();
			ArrayList<Long> timearr = new ArrayList<Long>();
			for(int i=0;i< times.length;i++)
			{
				if(geo.createPoint(coords[i]).intersects(mpol))
				{
					cooarr.add(coords[i]);
					timearr.add(times[i]);
				}
			}
			Coordinate[] coo = new Coordinate[cooarr.size()];
			long[] timecoo = new long[cooarr.size()];
			for(int i=0;i< cooarr.size();i++)
			{
				coo[i] = cooarr.get(i);
				timecoo[i] = timearr.get(i);
			}
			return factory.createMPoint(coo, timecoo);
		}
		return null;
	}

}
