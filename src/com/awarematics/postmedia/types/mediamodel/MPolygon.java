package com.awarematics.postmedia.types.mediamodel;

import java.util.ArrayList;
import java.util.Arrays;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import com.awarematics.postmedia.mgeom.MGeometryFactory;


public class MPolygon extends MGeometry {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//POLYGON (20 10, 30 0, 40 10, 30 20, 20 10)
	 Polygon[] listPolygon;
	 long[] times;

	public MPolygon(MPolygon mp) {
		listPolygon = mp.listPolygon;
		times = mp.times;
	}

	public MPolygon() {
		// TODO Auto-generated constructor stub
	}
	
	public MPolygon(Polygon[] points, long[] value) {
		this.listPolygon = points;
		this.times = value;
	}


	@Override
	public long getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MGeometry first() {
		Polygon[] pol = new Polygon[1];
		long[] time = new long[1];
		pol[0] = listPolygon[0];
		time[0] = times[0];
		return new MPolygon(pol, time);
	}

	@Override
	public MGeometry last() {
		Polygon[] pol = new Polygon[1];
		long[] time = new long[1];
		pol[0] = listPolygon[numOf()-1];
		time[0] = times[numOf()-1];
		return new MPolygon(pol, time);
	}

	@Override
	public MGeometry at( int n) {
		Polygon[] pol = new Polygon[1];
		long[] time = new long[1];
		pol[0] = listPolygon[n-1];
		time[0] = times[n-1];
		return new MPolygon(pol, time);
	}

	@Override
	public int numOf() {
		// TODO Auto-generated method stub
		return listPolygon.length;
	}
	@Override
	  public String toGeoString()
	    {
		  String mpointString = "MPOLYGON (";
			for (int i = 0; i < numOf(); i++) {
				if (i == 0) {
					mpointString = mpointString +"("+ listPolygon[i] + ") " + times[i];
				} else {
					mpointString = mpointString + ", (" + listPolygon[i] + ") " + times[i];
				}
			}
			mpointString = mpointString + ")";
			return mpointString;
	    }

	@Override
	public MGeometry lattice( MDuration duration) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Geometry spatial() {
		GeometryFactory geo = new GeometryFactory();
		return geo.createMultiPolygon(listPolygon);
	}


	@Override
	public Geometry snapshot(long instant) {
	//	PrecisionModel precisionModel = new PrecisionModel(1000);
		GeometryFactory geometryFactorys = new GeometryFactory();
		long current_time = instant;
		for (int i = 0; i < numOf(); i++) {
			long t = times[i];
			if (t == current_time) {
				Coordinate[] coo = new Coordinate[ listPolygon[i].getCoordinates().length];
				for(int p=0;p<coo.length;p++)
				{
					double x = listPolygon[i].getCoordinates()[p].x;
					double y = listPolygon[i].getCoordinates()[p].y;
					coo[p] = new Coordinate();
					coo[p].x = x;
					coo[p].y = y;
				}
				Polygon pol = geometryFactorys.createPolygon(coo);
				return pol;
			}
		}
		int temp = 0; long start = 0; long end = 0;
		for (int i = numOf()-1; i >=0; i--) {
			long t = times[i];
			if (t <= current_time) {
				start = t;
			}
			if (t > current_time && temp != 1) {
				end = t;
				double d;
				double dis;
				if (i !=0 ) {
					Coordinate[] coo = new Coordinate[listPolygon[i].getCoordinates().length];
					for (int p = 0; p < coo.length; p++) {
					//	System.out.println("p\t\t"+p);
						d = d (listPolygon[i].getCoordinates()[p].x,listPolygon[i-1].getCoordinates()[p].x, listPolygon[i].getCoordinates()[p].y, listPolygon[i-1].getCoordinates()[p].y);
						dis = (current_time - start) * d / (end - start);
						double cy = (dis * (listPolygon[i].getCoordinates()[p].y- listPolygon[i-1].getCoordinates()[p].y)) / d + listPolygon[i-1].getCoordinates()[p].y;
						double cx = (dis * (listPolygon[i].getCoordinates()[p].x - listPolygon[i-1].getCoordinates()[p].x)) / d + listPolygon[i-1].getCoordinates()[p].x;
						coo[p] = new Coordinate();
						coo[p].x = cx;
						coo[p].y = cy;
						//System.out.println(coo[p].x);
					}
					Polygon[] polygons = new Polygon[1];
					polygons[0] = geometryFactorys.createPolygon(coo);
					temp = 1;
					return polygons[0];
				}
			}
		}
		return null;
	}
	private double d(double d, double e, double f, double g) {
		return Math.sqrt((d-e)*(d-e)+(f-g)*(f-g));
	}
	
	@Override
	public MGeometry atomize(long duration) {
		long str_start = times[0];
		long str_end = times[numOf() - 1];
		long now = 0;
		ArrayList<Polygon> polNor = new ArrayList<Polygon>();
		ArrayList<Long> timesNor = new ArrayList<Long>();
		for (int i = 0; i < numOf(); i++) {
			for (long j = now; j < str_end; j += duration) {
				if (now == 0) {
					now = times[i] - times[i] % duration;
				} else if (now >= str_end) {
					break;
				} else {
					now = now + duration;
				}
				if (now < str_end && now > str_start) {
					polNor.add((Polygon)snapshot(now));
					timesNor.add(now);
				}
			}

		}
		// notice: the method is not support in java-version 8 version 9 is OK
		long[] tempList = new long[timesNor.size()];
		for(int i=0;i<timesNor.size();i++)
			tempList[i] = timesNor.get(i);	
//long[] tempList = timesNor.stream().mapToLong(i -> i).toArray();
		Polygon[] polygon = new Polygon[polNor.size()];
		for (int i = 0; i < polNor.size(); i++)
			polygon[i] = polNor.get(i);
		MGeometryFactory geometryFactory = new MGeometryFactory();
		return geometryFactory.createMPolygon(polygon, tempList);
	}


	@Override
	public MGeometry lattice(long duration){
		MGeometry pt = atomize(duration);
		Polygon[] pol = new Polygon[2];
		long[] timesLat = new long[2];
		pol[0] = ((MPolygon) pt).getListPolygon()[0];
		pol[1] = ((MPolygon) pt).getListPolygon()[pt.numOf() - 1];
		timesLat[0] = pt.getTimes()[0];
		timesLat[1] = pt.getTimes()[pt.numOf() - 1];
		MGeometryFactory geometryFactory = new MGeometryFactory();
		return geometryFactory.createMPolygon(pol, timesLat);
	}


	public Polygon[] getListPolygon() {
		return listPolygon;
	}

	public void setListPolygon(Polygon[] listPolygon) {
		this.listPolygon = listPolygon;
	}

	public long[] getTimes() {
		return times;
	}

	public void setTimes(long[] times) {
		this.times = times;
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
			return factory.createMPolygon(listPolygon, times);
		}
		int num = 0;
		for (int i = 0; i < times.length; i++) {
			if (times[i] <= overlappedEndTime && times[i] >= overlappedStartTime) {
				num++;
			}
		}
		if (num == 0)
			return null;

		Polygon[] pol = new Polygon[num];
		long[] timeSli = new long[num];
		int value = 0;

		for (int i = 0; i < times.length; i++) {
			if (times[i] <= overlappedEndTime && times[i] >= overlappedStartTime) {
				pol[value] = listPolygon[i];
				timeSli[value] = times[i];
				value++;
			}
		}
		return factory.createMPolygon(pol, timeSli);
	}

	@Override
	public Coordinate[] getCoords() {
		// TODO Auto-generated method stub
		return null;
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
			Coordinate coord = this.listPolygon[searchedPosition].getCoordinate();
			if(searchedPosition==0)
				return 0;
			else{
				veolocity = calDistance(coord, this.listPolygon[searchedPosition-1].getCoordinate())/ (instant-this.times[searchedPosition-1]);
				return veolocity;
			}
		}

		int startPosition = (searchedPosition * -1) - 2;
		int endPosition = (searchedPosition * -1) - 1;
		long startTime = times[startPosition];
		long endTime = times[endPosition];

		// Assure endTime is not equal startTime
		double dx = (instant - startTime) * (listPolygon[endPosition].getCoordinate().x - listPolygon[startPosition].getCoordinate().x) / (endTime - startTime);
		double dy = (instant - startTime) * (listPolygon[endPosition].getCoordinate().y - listPolygon[startPosition].getCoordinate().y) / (endTime - startTime);
		Coordinate newCoord = new Coordinate(listPolygon[startPosition].getCoordinate().x + dx, listPolygon[startPosition].getCoordinate().y + dy);
		veolocity = calDistance(newCoord, listPolygon[startPosition].getCoordinate())/ (instant-times[startPosition]);
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
			Coordinate coord = this.listPolygon[searchedPosition].getCoordinate();
			if(searchedPosition==0)
				return 0;
			else{
				veolocityt =  calDistance(coord, this.listPolygon[searchedPosition-1].getCoordinate())/ (instant-this.times[searchedPosition-1]);
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
		double dx = (instant - startTime) * (listPolygon[endPosition].getCoordinate().x - listPolygon[startPosition].getCoordinate().x) / (endTime - startTime);
		double dy = (instant - startTime) * (listPolygon[endPosition].getCoordinate().y - listPolygon[startPosition].getCoordinate().y) / (endTime - startTime);
		Coordinate newCoord = new Coordinate(listPolygon[startPosition].getCoordinate().x + dx, listPolygon[startPosition].getCoordinate().y + dy);
		veolocityt = calDistance(newCoord, listPolygon[startPosition].getCoordinate())/ (instant-times[startPosition]);
		veolocity0 = veolocityAtTimeTime(times[startPosition]);
		acceleration = 2*(veolocityt - veolocity0 * (instant-times[startPosition]))/(Math.pow((instant-times[startPosition]), 2));
		return acceleration*1000;
	}

	@Override
	public long timeAtCummulativeDistance(double distance) {
		double[] value = new double[numOf()];
		long time = 0;
		value[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			value[i] = value[i - 1] + calDistance(listPolygon[i].getCoordinate(), listPolygon[i-1].getCoordinate());
		}
		System.out.println(distance);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Geometry bbox() {
		Geometry gg = spatial();
		return gg.getEnvelope();
	}

	@Override
	public MPeriod btime() {
		//System.out.println("gg");
		MPeriod mp = new MPeriod();
		mp.period = new Period[numOf()];
		for(int i=1;i<numOf();i++)
		{
			mp.period[i] = new Period();
			mp.period[i].from=( first().getTimes()[0]);
			mp.period[i].to=(at(i).getTimes()[0]);
		}	
		return mp;
	}

	@Override
	public Period time() {
		Period[] mp = new Period[numOf()];
		for(int i=1;i<numOf();i++)
		{
			mp[i] = new Period();
			mp[i].from=( first().getTimes()[0]);
			mp[i].to=(at(i).getTimes()[0]); 
		}		
		return mp[numOf()-1];
	}
	@Override
	public Period time(int n) {
		Period[] mp = new Period[numOf()];
		for(int i=1;i<numOf();i++)
		{
			//mp.period[i] = new Period();
			mp[i] = new Period();
			mp[i].from =  first().getTimes()[0];
			mp[i].to = at(i).getTimes()[0];
		}		
		return mp[n];
	}
	@Override
	public long startTime() {
		return getTimes()[0];
	}
	@Override
	public long endTime() {
		
		return getTimes()[numOf()-1];
	}

	@Override
	public MDouble timeToDistance() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] mdistance = new double[numOf()];
		mdistance[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			mdistance[i] =  calDistance(listPolygon[i].getCoordinate(),listPolygon[0].getCoordinate());
		}	
		return mgeometryFactory.createMDouble(mdistance,times);
	}

	@Override
	public ArrayList<Long> timeAtDistance(double distance) {
		double[] mdistance = new double[numOf()];
		 ArrayList<Long> time = new  ArrayList<Long>();
		mdistance[0] = 0;
		for (int i = 1; i < numOf(); i++) {
			mdistance[i] = calDistance(listPolygon[i].getCoordinate(),listPolygon[0].getCoordinate());
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
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] area = new double[numOf()];
		for(int i =0;i<numOf();i++){
			area[i] = listPolygon[i].getArea();
		}
		return mgeometryFactory.createMDouble(area, times);
	}

	@Override
	public MDouble direction() {
		return null;
	}

	@Override
	public MInt count() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public MDouble velocity() {
		MGeometryFactory mgeometryFactory = new MGeometryFactory();
		double[] veolocity  = new double[numOf()];
		veolocity[0] = 0;
		for(int i =1; i< numOf();i++)
		{
			veolocity[i] = calDistance(listPolygon[i].getCoordinate(),listPolygon[i-1].getCoordinate())/ (times[i]-times[i-1]);
		}
		
		return mgeometryFactory.createMDouble(veolocity, times);
	}

	@Override
	public MGeometry slice(Polygon pol) {
		if(this.spatial().intersects(pol))
		{				
			ArrayList<Polygon> listPolygonNor = new ArrayList<Polygon>();
			ArrayList<Long>  creationTimeNor = new ArrayList<Long>();
			for(int i=0;i< times.length;i++)
			{
				if((listPolygon[i]).intersects(pol))
				{
					listPolygonNor.add(listPolygon[i]);
					creationTimeNor.add(times[i]);
				}
			}
			long[] creationTime = new long[creationTimeNor.size()];
			
			for(int i=0;i<creationTimeNor.size();i++){
				creationTime[i] = creationTimeNor.get(i);
			}	
			Polygon[] listPolygon = new Polygon[creationTimeNor.size()];
			for (int i = 0; i < creationTimeNor.size(); i++){
				listPolygon[i] = listPolygonNor.get(i);
			}
			MGeometryFactory geometryFactory = new MGeometryFactory();
			return geometryFactory.createMPolygon(listPolygon, creationTime);
		}
		return null;
	}

}

