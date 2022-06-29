package com.awarematics.postmedia.types.mediamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import com.awarematics.postmedia.mgeom.MGeometryFactory;

@SuppressWarnings("rawtypes")
public abstract class MGeometry implements Serializable, Comparable, Cloneable {

	private static final long serialVersionUID = 1L;
	public static final int MAX_VALUE = 7732345;
	private static double EARTH_RADIUS = 6378.137;

	public abstract Geometry spatial();

	public abstract long getDuration();

	public abstract MGeometry lattice(long duration);

	public abstract MGeometry atomize(long duration);

	public abstract MGeometry lattice(MDuration duration);

	public abstract Geometry snapshot(long ts);

	public abstract MGeometry slice(long fromTime, long toTime);

	public abstract MGeometry slice(Polygon pol);

	public abstract MGeometry first();

	public abstract MGeometry last();

	public abstract MGeometry at(int n);

	public abstract int numOf();

	public abstract String toGeoString();

	public abstract long[] getTimes();

	public abstract Coordinate[] getCoords();

	public abstract double veolocityAtTimeTime(long instant);

	public abstract double accelerationAtTimeTime(long instant);

	public abstract MDouble timeToDistance();

	public abstract ArrayList<Long> timeAtDistance(double distance);

	public abstract long timeAtCummulativeDistance(double distance);

	public abstract MGeometry snapToGrid(int cellSize);

	public abstract Geometry bbox();

	public abstract MPeriod btime();

	public abstract Period time();

	public abstract Period time(int n);

	public abstract long startTime();

	public abstract long endTime();

	public abstract MDouble area();

	public abstract MDouble direction();

	public abstract MInt count();

	public abstract MDouble velocity();

	public static MBool equal(MGeometry mg1, MGeometry mg2) {
		MGeometryFactory mgeom = new MGeometryFactory();
		MBool mbools = intersects(mg1, mg2);
		MBool mbool2 = meet(mg1, mg2);
		boolean[] bools = new boolean[mbools.numOf()];
		long[] times = mbools.getTimes();
		for (int i = 0; i < times.length; i++) {
			if (times[i] >= mg1.getTimes()[0] && times[i] >= mg2.getTimes()[0]
					&& times[i] <= mg1.getTimes()[mg1.numOf() - 1] && times[i] <= mg2.getTimes()[mg2.numOf() - 1]) {
				if (mg1.snapshot(times[i]).equals(mg2.snapshot(times[i])) && mbool2.bools[i] != true) {
					bools[i] = true;
				}
			} else {
				bools[i] = false;
			}
		}
		return mgeom.createMBool(bools, times);
	}

	public static MBool meet(MGeometry mg1, MGeometry mg2) {
		MGeometryFactory mgeom = new MGeometryFactory();
		MBool mbools = intersects(mg1, mg2);

		boolean[] bools = mbools.getBools();
		boolean[] boolsresult = new boolean[mbools.numOf()];
		long[] times = mbools.getTimes();

		for (int i = 1; i < times.length - 1; i++) {
			if (bools[i] == true && bools[i - 1] == true && bools[i + 1] == true)
				boolsresult[i] = false;
			else
				boolsresult[i] = bools[i];
		}
		boolsresult[0] = bools[0];
		boolsresult[times.length - 1] = bools[times.length - 1];
		return mgeom.createMBool(boolsresult, times);
	}

	public static MBool intersects(MGeometry mg1, MGeometry mg2) {
		ArrayList<Long> timeList = new ArrayList<Long>();
		MGeometryFactory mgeom = new MGeometryFactory();

		if (eventTime(mg1, mg2).getInstant() != null) {
			long[] ms = eventTime(mg1, mg2).getInstant();
			for (int i = 0; i < ms.length; i++) {
				timeList.add(ms[i]);
			}

			long[] tempList = new long[timeList.size()];
			for (int i = 0; i < timeList.size(); i++)
				tempList[i] = timeList.get(i);
			boolean[] bools = new boolean[timeList.size()];
			for (int i = 0; i < tempList.length; i++) {
				if (mg1.startTime() <= tempList[i] && mg1.endTime() >= tempList[i] && mg2.startTime() <= tempList[i]
						&& mg2.endTime() >= tempList[i]) {
					if (mg1.snapshot(tempList[i]).intersects(mg2.snapshot(tempList[i]))) {
						bools[i] = true;
					}
				}
			}
			return mgeom.createMBool(bools, tempList);
		}

		long[] tempList = mg1.getTimes();
		boolean[] bools = new boolean[tempList.length];
		for (int i = 0; i < tempList.length; i++)
			bools[i] = false;

		return mgeom.createMBool(bools, tempList);
	}

	public static MBool disjoint(MGeometry mg1, MGeometry mg2) {

		MGeometryFactory mgeom = new MGeometryFactory();
		MBool mbools = intersects(mg1, mg2);
		boolean[] bools = new boolean[mbools.numOf()];
		long[] times = mbools.getTimes();
		for (int i = 0; i < bools.length; i++) {
			if (mbools.getBools()[i] == true)
				bools[i] = false;
			else
				bools[i] = true;
		}
		return mgeom.createMBool(bools, times);
	}

	public static MInstant eventTime(MGeometry mg1, MGeometry mg2) {
		MGeometryFactory mgeom = new MGeometryFactory();
		long overlappedStartTime = Math.max(mg1.getTimes()[0], mg2.getTimes()[0]);
		long overlappedEndTime = Math.min(mg1.getTimes()[mg1.numOf() - 1], mg2.getTimes()[mg2.numOf() - 1]);
		long[] timeArea = new long[] { overlappedStartTime, overlappedEndTime };

		ArrayList<Long> timeList = getTimeList(mg1, mg2, timeArea);
		LineString line1 = MGeometryToGeometry(mg1, timeArea);
		LineString line2 = MGeometryToGeometry(mg2, timeArea);
		if (mg1 instanceof MPoint) {
			long[] eve = eventTime(line1, line2, (MPoint) mg1, timeList);
			return mgeom.createMInstant(eve);
		} else {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg1.numOf(); i++) {
				if (mg1.getTimes()[i] > timeArea[0] || mg1.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg1.getTimes()[i]);
					coords.add(mg1.getCoords()[i]);
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
			long[] tempList = new long[nowTime.size()];
			for (int i = 0; i < nowTime.size(); i++)
				tempList[i] = nowTime.get(i);
			// long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			long[] eve = eventTime(line1, line2, mp, timeList);
			return mgeom.createMInstant(eve);
		}
	}

	
	public static MInstant eventTime(MGeometry mg1, MGeometry mg2, double distance) {
		MGeometryFactory mgeom = new MGeometryFactory();
		long overlappedStartTime = Math.max(mg1.getTimes()[0], mg2.getTimes()[0]);
		long overlappedEndTime = Math.min(mg1.getTimes()[mg1.numOf() - 1], mg2.getTimes()[mg2.numOf() - 1]);
		long[] timeArea = new long[] { overlappedStartTime, overlappedEndTime };

		ArrayList<Long> timeList = getTimeList(mg1, mg2, timeArea);
		LineString line1 = MGeometryToGeometry(mg1, timeArea);
		LineString line2 = MGeometryToGeometry(mg2, timeArea);
		if (mg1 instanceof MPoint) {
			long[] eve = eventTime(line1, line2, (MPoint) mg1, timeList, distance);
			return mgeom.createMInstant(eve);
		}
		return null; 
	}

	public static MPoint eventPosition(MGeometry mg1, MGeometry mg2, double distance) {
		long overlappedStartTime = Math.max(mg1.getTimes()[0], mg2.getTimes()[0]);
		long overlappedEndTime = Math.min(mg1.getTimes()[mg1.numOf() - 1], mg2.getTimes()[mg2.numOf() - 1]);
		long[] timeArea = new long[] { overlappedStartTime, overlappedEndTime };

		ArrayList<Long> timeList = getTimeList(mg1, mg2, timeArea);
		LineString line1 = MGeometryToGeometry(mg1, timeArea);
		LineString line2 = MGeometryToGeometry(mg2, timeArea);
		if (mg1 instanceof MPoint) {
			MPoint eve = eventPosition(line1, line2, (MPoint) mg1, timeList, distance);
			return eve;
		}
		return null; 
	}

	
	public static void getInstantList(LineString g1, LineString g2, MGeometry mg1, MGeometry mg2, long[] tempList, ArrayList<Long> result) {
		try {
			if (g1.intersects(g2)) {
				Geometry interPoint = g1.intersection(g2);
				Coordinate[] pp = interPoint.getCoordinates();
				for (int k = 0; k < pp.length; k++) {
					for (int ii = 1; ii < tempList.length; ii++) {
						double rightpart = calDistance(g1.getCoordinates()[ii], pp[k]);
						double leftpart = calDistance(g1.getCoordinates()[ii - 1], pp[k]);
						double total = calDistance(g1.getCoordinates()[ii], g1.getCoordinates()[ii - 1]);
						if ((leftpart + rightpart) - total < 0.00000001) {
							long newTime = (long) ((tempList[ii] - tempList[ii - 1]) * leftpart / total + tempList[ii - 1]);
							if (calDistance(mg2.snapshot(newTime).getCoordinate(),
									mg1.snapshot(newTime).getCoordinate()) < 0.001 && newTime != 0)
								result.add(newTime);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public static MString relationship(MGeometry mg1, MGeometry mg2) {
		MGeometryFactory mgeom = new MGeometryFactory();
		MBool mbools = intersects(mg1, mg2);
		MBool mbool2 = meet(mg1, mg2);
		MBool mbool3 = equal(mg1, mg2);
		MBool mbool4 = overlaps(mg1, mg2);
		MBool mbool5 = inside(mg1, mg2);
		MBool mbool6 = contains(mg1, mg2);
		MBool mbool7 = overlaps(mg1, mg2);

		String[] string = new String[mbools.numOf()];
		for (int i = 0; i < mbools.numOf(); i++) {
			if (mbool2.getBools()[i] == true)
				string[i] = "meet";
			else if (mbool3.getBools()[i] == true)
				string[i] = "equal";
			else if (mbool4.getBools()[i] == true)
				string[i] = "overlaps";
			else if (mbool5.getBools()[i] == true)
				string[i] = "inside";
			else if (mbool6.getBools()[i] == true)
				string[i] = "contains";
			else if (mbool7.getBools()[i] == true)
				string[i] = "overlaps";
			else if (mbools.getBools()[i] == true)
				string[i] = "intersect";
			else
				string[i] = "disjoint";
		}
		long[] times = mbools.getTimes();
		MString ms = mgeom.createMString(string, times);
		return ms;
	}

	public static MBool overlaps(MGeometry mg1, MGeometry mg2) {
		MGeometryFactory mgeom = new MGeometryFactory();
		long[] times = null;
		boolean[] bools = null;

		MBool mbools = intersects(mg1, mg2);
		MBool mbool2 = meet(mg1, mg2);
		bools = new boolean[mbools.numOf()];
		times = mbools.getTimes();

		for (int i = 0; i < times.length; i++) {
			if (times[i] >= mg1.getTimes()[0] && times[i] >= mg2.getTimes()[0]
					&& times[i] <= mg1.getTimes()[mg1.numOf() - 1] && times[i] <= mg2.getTimes()[mg2.numOf() - 1]) {
				if (mg1.snapshot(times[i]).overlaps(mg2.snapshot(times[i])) && mbool2.bools[i] != true) {
					bools[i] = true;
				}
			} else {
				bools[i] = false;
			}
		}
		return mgeom.createMBool(bools, times);
	}

	public static MBool inside(MGeometry mg1, MGeometry mg2) {
		MGeometryFactory mgeom = new MGeometryFactory();
		long[] times = null;
		boolean[] bools = null;

		MBool mbools = intersects(mg1, mg2);
		MBool mbool2 = meet(mg1, mg2);
		bools = new boolean[mbools.numOf()];
		times = mbools.getTimes();

		for (int i = 0; i < times.length; i++) {
			if (times[i] >= mg1.getTimes()[0] && times[i] >= mg2.getTimes()[0]
					&& times[i] <= mg1.getTimes()[mg1.numOf() - 1] && times[i] <= mg2.getTimes()[mg2.numOf() - 1]) {
				if (mg1.snapshot(times[i]).within(mg2.snapshot(times[i])) && mbool2.bools[i] != true) {
					bools[i] = true;
				}
			} else {
				bools[i] = false;
			}
		}
		return mgeom.createMBool(bools, times);
	}

	public static MBool contains(MGeometry mg1, MGeometry mg2) {
		MGeometryFactory mgeom = new MGeometryFactory();
		long[] times = null;
		boolean[] bools = null;

		MBool mbools = intersects(mg1, mg2);
		MBool mbool2 = meet(mg1, mg2);
		bools = new boolean[mbools.numOf()];
		times = mbools.getTimes();

		for (int i = 0; i < times.length; i++) {
			if (times[i] >= mg1.getTimes()[0] && times[i] >= mg2.getTimes()[0]
					&& times[i] <= mg1.getTimes()[mg1.numOf() - 1] && times[i] <= mg2.getTimes()[mg2.numOf() - 1]) {
				if (mg1.snapshot(times[i]).contains(mg2.snapshot(times[i])) && mbool2.bools[i] != true) {
					bools[i] = true;
				}
			} else {
				bools[i] = false;
			}
		}
		return mgeom.createMBool(bools, times);
	}

	public static ArrayList<Long> getTimeList(MGeometry mg1, MGeometry mg2, long[] timeArea) {
		ArrayList<Long> nowTime = new ArrayList<Long>();
		if (timeArea[0] > timeArea[1])
			return null;
		for (int i = 0; i < mg1.numOf(); i++) {
			if (mg1.getTimes()[i] >= timeArea[0] && mg1.getTimes()[i] <= timeArea[1]) {
				nowTime.add(mg1.getTimes()[i]);
			}
		}
		for (int i = 0; i < mg2.numOf(); i++) {
			if (mg2.getTimes()[i] >= timeArea[0] && mg2.getTimes()[i] <= timeArea[1]) {
				nowTime.add(mg2.getTimes()[i]);
			}
		}
		return nowTime;
	}

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
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000d) / 10000d;
		s = s * 1000;
		return s;
	}

	public static LineString MGeometryToGeometry(MGeometry mg, long[] timeArea) {
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
		if (timeArea[0] > timeArea[1])
			return null;
		if (mg instanceof MPoint) {
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] >= timeArea[0] || mg.getTimes()[i] <= timeArea[1])
					coords.add(((MPoint) mg).getCoords()[i]);
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < coords.size(); i++) {
				coordinates[i] = coords.get(i);
			}
			GeometryFactory geometryFactory = new GeometryFactory();
			return geometryFactory.createLineString(coordinates);
		}
		if (mg instanceof MVideo) {
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] >= timeArea[0] || mg.getTimes()[i] <= timeArea[1])
					coords.add(((MVideo) mg).getCoords()[i]);
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < coords.size(); i++) {
				coordinates[i] = coords.get(i);
			}
			GeometryFactory geometryFactory = new GeometryFactory();
			return geometryFactory.createLineString(coordinates);
		}
		if (mg instanceof MPhoto) {
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] >= timeArea[0] || mg.getTimes()[i] <= timeArea[1])
					coords.add(((MPhoto) mg).getCoords()[i]);
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < coords.size(); i++) {
				coordinates[i] = coords.get(i);
			}
			GeometryFactory geometryFactory = new GeometryFactory();
			return geometryFactory.createLineString(coordinates);
		}
		if (mg instanceof MPolygon) {
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] >= timeArea[0] || mg.getTimes()[i] <= timeArea[1])
					coords.add(((MPolygon) mg).getListPolygon()[i].getCoordinate());
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < coords.size(); i++) {
				coordinates[i] = coords.get(i);
			}
			GeometryFactory geometryFactory = new GeometryFactory();
			return geometryFactory.createLineString(coordinates);
		}
		if (mg instanceof MLineString) {
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] >= timeArea[0] || mg.getTimes()[i] <= timeArea[1])
					coords.add(((MLineString) mg).getPoints()[i].getCoordinateN(0));
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < coords.size(); i++) {
				coordinates[i] = coords.get(i);
			}
			GeometryFactory geometryFactory = new GeometryFactory();
			return geometryFactory.createLineString(coordinates);
		}
		return null;
	}

	public static long[] eventTime(Geometry geometry1, Geometry geometry2, MPoint mg1, ArrayList<Long> timeList) {
		try {
			if (geometry1.intersects(geometry2) != false) {
				Geometry interPoint = geometry2.intersection(geometry1);
				Coordinate[] pp = interPoint.getCoordinates();

				for (int k = 0; k < pp.length; k++) {
					for (int ii = 1; ii < mg1.numOf(); ii++) {

						if ((calDistance(mg1.getCoords()[ii], pp[k]) + calDistance(mg1.getCoords()[ii - 1], pp[k]))
								- calDistance(mg1.getCoords()[ii], mg1.getCoords()[ii - 1]) < 0.000000001) {
							long newTime = (long) ((mg1.getTimes()[ii] - mg1.getTimes()[ii - 1])
									* calDistance(mg1.getCoords()[ii - 1], pp[k])
									/ calDistance(mg1.getCoords()[ii], mg1.getCoords()[ii - 1])
									+ mg1.getTimes()[ii - 1]);
							if (newTime != 0)
								timeList.add(newTime);
						}
					}
				}
				LinkedHashSet<Long> set = new LinkedHashSet<Long>(timeList.size());
				set.addAll(timeList);
				timeList.clear();
				timeList.addAll(set);

				long[] tempList = new long[timeList.size()];
				for (int i = 0; i < timeList.size(); i++)
					tempList[i] = timeList.get(i);
				Arrays.sort(tempList);
				return tempList;

			} else {

				long[] tempList = new long[timeList.size()];
				for (int i = 0; i < timeList.size(); i++)
					tempList[i] = timeList.get(i);
				Arrays.sort(tempList);
				return tempList;
			}
		} catch (Exception e) {

		}
		return null;
	}
	public static long[] eventTime(Geometry geometry1, Geometry geometry2, MPoint mg1, ArrayList<Long> t, double distance) {
		ArrayList<Long> timeList = new ArrayList<Long>();
		try {
			if (geometry1.intersects(geometry2) != false) {
				Geometry interPoint = geometry2.intersection(geometry1);
				Coordinate[] pp = interPoint.getCoordinates();

				for (int k = 0; k < pp.length; k++) {
					for (int ii = 1; ii < mg1.numOf(); ii++) {

						if ((calDistance(mg1.getCoords()[ii], pp[k]) + calDistance(mg1.getCoords()[ii - 1], pp[k]))
								- calDistance(mg1.getCoords()[ii], mg1.getCoords()[ii - 1]) < distance) {
							long newTime = (long) ((mg1.getTimes()[ii] - mg1.getTimes()[ii - 1])
									* calDistance(mg1.getCoords()[ii - 1], pp[k])
									/ calDistance(mg1.getCoords()[ii], mg1.getCoords()[ii - 1])
									+ mg1.getTimes()[ii - 1]);
							if (newTime != 0)
								timeList.add(newTime);
						}
					}
				}
				LinkedHashSet<Long> set = new LinkedHashSet<Long>(timeList.size());
				set.addAll(timeList);
				timeList.clear();
				timeList.addAll(set);

				long[] tempList = new long[timeList.size()];
				for (int i = 0; i < timeList.size(); i++)
					tempList[i] = timeList.get(i);
				Arrays.sort(tempList);
				return tempList;

			} else {

				long[] tempList = new long[timeList.size()];
				for (int i = 0; i < geometry1.getNumPoints(); i++)
					for (int j = 0; j < geometry2.getNumPoints(); j++)
						if(calDistance(geometry1.getCoordinates()[i], geometry2.getCoordinates()[j]) < distance)
							tempList[i] = timeList.get(i);
							Arrays.sort(tempList);
				return tempList;
			}
		} catch (Exception e) {

		}
		return null;
	}
	
	public static MPoint eventPosition(Geometry geometry1, Geometry geometry2, MPoint mg1, ArrayList<Long> timeList, double distance) {
		try {
			if (geometry1.intersects(geometry2) != false) {
				Geometry interPoint = geometry2.intersection(geometry1);
				Coordinate[] pp = interPoint.getCoordinates();

				for (int k = 0; k < pp.length; k++) {
					for (int ii = 1; ii < mg1.numOf(); ii++) {

						if ((calDistance(mg1.getCoords()[ii], pp[k]) + calDistance(mg1.getCoords()[ii - 1], pp[k]))
								- calDistance(mg1.getCoords()[ii], mg1.getCoords()[ii - 1]) < distance) {
							long newTime = (long) ((mg1.getTimes()[ii] - mg1.getTimes()[ii - 1])
									* calDistance(mg1.getCoords()[ii - 1], pp[k])
									/ calDistance(mg1.getCoords()[ii], mg1.getCoords()[ii - 1])
									+ mg1.getTimes()[ii - 1]);
							if (newTime != 0)
								timeList.add(newTime);
						}
					}
				}
				LinkedHashSet<Long> set = new LinkedHashSet<Long>(timeList.size());
				set.addAll(timeList);
				timeList.clear();
				timeList.addAll(set);

				long[] tempList = new long[timeList.size()];
				for (int i = 0; i < timeList.size(); i++)
					tempList[i] = timeList.get(i);
				Arrays.sort(tempList);
				MGeometryFactory geometryFactory = new MGeometryFactory();
			
				return geometryFactory.createMPoint(pp, tempList);

			} else {

				long[] tempList = new long[timeList.size()];
				Coordinate[] tempcoot = new Coordinate[timeList.size()];
				for (int i = 0; i < geometry1.getNumPoints(); i++)
					for (int j = 0; j < geometry2.getNumPoints(); j++)
						if(calDistance(geometry1.getCoordinates()[i], geometry2.getCoordinates()[j]) < distance)
							tempList[i] = timeList.get(i);
							Arrays.sort(tempList);
							MGeometryFactory geometryFactory = new MGeometryFactory();
							
							return geometryFactory.createMPoint(tempcoot, tempList);
			}
		} catch (Exception e) {

		}
		return null;
	}
}
