package com.awarematics.postmedia.algorithms.distance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import com.awarematics.postmedia.types.mediamodel.MGeometry;
import com.awarematics.postmedia.types.mediamodel.MLineString;
import com.awarematics.postmedia.types.mediamodel.MPhoto;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import com.awarematics.postmedia.types.mediamodel.MPolygon;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class EventTimeGenerator implements TimeGenerator {

	public long[] genTimes(MGeometry mg1, MGeometry mg2) {
		long overlappedStartTime = Math.max(mg1.getTimes()[0], mg2.getTimes()[0]);
		long overlappedEndTime = Math.min(mg1.getTimes()[mg1.numOf() - 1], mg2.getTimes()[mg2.numOf() - 1]);
		long[] timeArea = new long[] { overlappedStartTime, overlappedEndTime };

		ArrayList<Long> timeList = getTimeList(mg1, mg2, timeArea);
		LineString line1 = MGeometryToGeometry(mg1, timeArea);
		LineString line2 = MGeometryToGeometry(mg2, timeArea);
		if (mg1 instanceof MPoint)
			return eventTime(line1, line2, (MPoint) mg1, timeList);
		if (mg1 instanceof MPhoto) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg1.numOf(); i++) {
				if (mg1.getTimes()[i] > timeArea[0] || mg1.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg1.getTimes()[i]);
					coords.add(((MPhoto) mg1).getCoords()[i]);
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
		long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, line2, mp, timeList);
		}
		if (mg1 instanceof MVideo) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg1.numOf(); i++) {
				if (mg1.getTimes()[i] > timeArea[0] || mg1.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg1.getTimes()[i]);
					coords.add(((MVideo) mg1).getCoords()[i]);
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, line2, mp, timeList);
		}
		if (mg1 instanceof MPolygon) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg1.numOf(); i++) {
				if (mg1.getTimes()[i] > timeArea[0] || mg1.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg1.getTimes()[i]);
					coords.add(((MPolygon) mg1).getListPolygon()[i].getCoordinate());
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, line2, mp, timeList);
		}
		if (mg1 instanceof MLineString) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg1.numOf(); i++) {
				if (mg1.getTimes()[i] > timeArea[0] || mg1.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg1.getTimes()[i]);
					coords.add(((MLineString) mg1).getPoints()[i].getCoordinateN(0));
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, line2, mp, timeList);
		}
		return null;
	}

	public long[] genTimes(MGeometry mg, Geometry geo) {
		long overlappedStartTime = mg.getTimes()[0];
		long overlappedEndTime = mg.getTimes()[mg.numOf() - 1];
		long[] timeArea = new long[] { overlappedStartTime, overlappedEndTime };
		ArrayList<Long> timeList = getTimeList(mg, timeArea);
		LineString line1 = MGeometryToGeometry(mg, timeArea);

		if (mg instanceof MPoint)
			return eventTime(line1, geo, (MPoint) mg, timeList);
		if (mg instanceof MPhoto || mg instanceof MVideo) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] > timeArea[0] || mg.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg.getTimes()[i]);
					coords.add(mg.spatial().getCoordinates()[i]);
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, geo, mp, timeList);
		}
		if (mg instanceof MPolygon) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] > timeArea[0] || mg.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg.getTimes()[i]);
					coords.add(((MPolygon) mg).getListPolygon()[i].getCoordinate());
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, geo, mp, timeList);
		}
		if (mg instanceof MLineString) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] > timeArea[0] || mg.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg.getTimes()[i]);
					coords.add(((MLineString) mg).getPoints()[i].getCoordinateN(0));
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, geo, mp, timeList);
		}
		return null;
	}

	public long[] genTimes(Geometry geo, MGeometry mg) {
		long overlappedStartTime = mg.getTimes()[0];
		long overlappedEndTime = mg.getTimes()[mg.numOf() - 1];
		long[] timeArea = new long[] { overlappedStartTime, overlappedEndTime };
		ArrayList<Long> timeList = getTimeList(mg, timeArea);
		LineString line1 = MGeometryToGeometry(mg, timeArea);
		if (mg instanceof MPoint)
			return eventTime(line1, geo, (MPoint) mg, timeList);
		if (mg instanceof MPhoto || mg instanceof MVideo) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] > timeArea[0] || mg.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg.getTimes()[i]);
					coords.add(((MPhoto) mg).getCoords()[i]);
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}
long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, geo, mp, timeList);
		}
		if (mg instanceof MPolygon) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] > timeArea[0] || mg.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg.getTimes()[i]);
					coords.add(((MPolygon) mg).getListPolygon()[i].getCoordinate());
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}

long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, geo, mp, timeList);
		}
		if (mg instanceof MLineString) {
			ArrayList<Long> nowTime = new ArrayList<Long>();
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
			for (int i = 0; i < mg.numOf(); i++) {
				if (mg.getTimes()[i] > timeArea[0] || mg.getTimes()[i] <= timeArea[1]) {
					nowTime.add(mg.getTimes()[i]);
					coords.add(((MLineString) mg).getPoints()[i].getCoordinateN(0));
				}
			}
			Coordinate[] coordinates = new Coordinate[coords.size()];
			for (int i = 0; i < nowTime.size(); i++) {
				coordinates[i] = coords.get(i);
			}

long[] tempList = new long[nowTime.size()];
		for(int i=0;i<nowTime.size();i++)
			tempList[i] = nowTime.get(i);	
			//long[] tempList = nowTime.stream().mapToLong(i -> i).toArray();
			MPoint mp = new MPoint(coordinates, tempList);
			return eventTime(line1, geo, mp, timeList);
		}
		return null;
	}

	private long[] eventTime(Geometry geometry1, Geometry geometry2, MPoint mg1, ArrayList<Long> timeList) {
		try {if (geometry1.intersects(geometry2)!=false) {
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
							timeList.add(newTime);
						}
					}
				}
				LinkedHashSet<Long> set = new LinkedHashSet<Long>(timeList.size());
				set.addAll(timeList);
				timeList.clear();
				timeList.addAll(set);

long[] tempList = new long[timeList.size()];
		for(int i=0;i<timeList.size();i++)
			tempList[i] = timeList.get(i);	
				//long[] tempList = timeList.stream().filter(i -> i != null).mapToLong(i -> i).toArray();
				Arrays.sort(tempList);
				return tempList;
			 
		} else {
		
			long[] tempList = new long[timeList.size()];
			for(int i=0;i<timeList.size();i++)
				tempList[i] = timeList.get(i);	
			//long[] tempList = timeList.stream().filter(i -> i != null).mapToLong(i -> i).toArray();
			Arrays.sort(tempList);
			return tempList;
		}}catch (Exception e) {

		}
		return null;
	}

	public LineString MGeometryToGeometry(MGeometry mg, long[] timeArea) {
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

	public static double calDistance(Coordinate p1, Coordinate p2) {
		double x1 = p1.x;
		double y1 = p1.y;
		double x2 = p2.x;
		double y2 = p2.y;
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	private ArrayList<Long> getTimeList(MGeometry mg, long[] timeArea) {
		ArrayList<Long> nowTime = new ArrayList<Long>();
		if (timeArea[0] > timeArea[1])
			return null;
		for (int i = 0; i < mg.numOf(); i++) {
			if (mg.getTimes()[i] >= timeArea[0] || mg.getTimes()[i] <= timeArea[1])
				nowTime.add(mg.getTimes()[i]);
		}
		return nowTime;
	}

	public ArrayList<Long> getTimeList(MGeometry mg1, MGeometry mg2, long[] timeArea) {
		ArrayList<Long> nowTime = new ArrayList<Long>();
		if (timeArea[0] > timeArea[1])
			return null;
		for (int i = 0; i < mg1.numOf(); i++) {
			if (mg1.getTimes()[i] >= timeArea[0] || mg1.getTimes()[i] <= timeArea[1])
				nowTime.add(mg1.getTimes()[i]);
		}
		for (int i = 0; i < mg2.numOf(); i++) {
			if (mg2.getTimes()[i] >= timeArea[0] || mg2.getTimes()[i] <= timeArea[1])
				nowTime.add(mg2.getTimes()[i]);
		}
		return nowTime;
	}

}


