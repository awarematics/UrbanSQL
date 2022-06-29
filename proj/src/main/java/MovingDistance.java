package com.awarematics.postmedia.algorithms.distance;

import java.util.ArrayList;

//import org.postgresql.pljava.annotation.Function;
//import static org.postgresql.pljava.annotation.Function.Effects.IMMUTABLE;
//import static org.postgresql.pljava.annotation.Function.OnNullInput.RETURNS_NULL;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.distance.DistanceOp;
import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.MDouble;
import com.awarematics.postmedia.types.mediamodel.MGeometry;

public class MovingDistance {
	static double result;
	static MDouble mb = null;

	public static final long DEFAULT_TIME_LATTICE_UNIT = 1000;
	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static MDouble distance(Geometry geo, MGeometry mg) {
		return CalDistance(geo, mg, new EventTimeGenerator());
	}
	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static MDouble distance(Geometry geo, MGeometry mg, long value) {
		return CalDistance(geo, mg, new AtomicTimeGenerator(value));
	}
	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static MDouble distance(MGeometry mg1, MGeometry mg2) {
		return CalDistance(mg1, mg2, new EventTimeGenerator());
	}
	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static MDouble distance(MGeometry mg1, MGeometry mg2, long value) {
		return CalDistance(mg1, mg2, new AtomicTimeGenerator(value));
	}
	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static MDouble distance(MGeometry mg, Geometry geo) {
		return CalDistance(mg, geo, new EventTimeGenerator());
	}
	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static MDouble distance(MGeometry mg, Geometry geo, long value) {
		return CalDistance(mg, geo, new AtomicTimeGenerator(value));
	}

	public static MDouble CalDistance(MGeometry mg1, MGeometry mg2, TimeGenerator timeFunc) {
		ArrayList<Double> distances = new ArrayList<Double>();
		ArrayList<Long> times = new ArrayList<Long>();
		Double temp = 0.0;
		long overlappedStartTime = Math.max(mg1.getTimes()[0], mg2.getTimes()[0]);
		long overlappedEndTime = Math.min(mg1.getTimes()[mg1.numOf() - 1], mg2.getTimes()[mg2.numOf() - 1]);
		if(overlappedStartTime>overlappedEndTime) return null;
		for (long time : timeFunc.genTimes(mg1, mg2)) {
			if (mg1.snapshot(time) == null || mg2.snapshot(time) == null) {
			} else if (mg1.snapshot(time).within(mg2.snapshot(time))) {
				temp = 0.0;
			} else {
				Coordinate[] c = DistanceOp.nearestPoints(mg2.snapshot(time), mg1.snapshot(time));
				temp = d(c[0], mg1.snapshot(time).getCoordinates()[0]);
			}
			distances.add(temp);
			times.add(time);
		}
		double[] value = new double[distances.size()];		
		long[] targets = new long[times.size()];
		for(int i=0;i<distances.size();i++)
			value[i] = distances.get(i);	
		for(int i=0;i<times.size();i++)
			targets[i] = times.get(i);			
		//double[] value = distances.stream().mapToDouble(i -> i).toArray();
		//long[] targets = times.stream().mapToLong(i -> i).toArray();
	/*
	 * delete repeat time;
	 */
		ArrayList<Integer> ditto = new ArrayList<Integer>();
		for (int i = 0; i < targets.length; i++) {
			for (int j = i + 1; j < targets.length; j++) {
				if (targets[i]==(targets[j])) {
					if (value[i] < value[j]) {
						ditto.add(i);						
					} else {
						ditto.add(j);						
					}
				}
			}
		}
		/*
		 * delete repeat time
		 */
		ArrayList<Double> St = new ArrayList<Double>();
		ArrayList<Long> tt = new ArrayList<Long>();
		int tem=0;
		for(int i=0;i<targets.length;i++)
		{
			tem =0;
			for(int j=0;j<ditto.size();j++)
			{
				if(ditto.get(j)==i) tem = 1;
			}
			if(tem == 0){
				St.add(value[i]);
				tt.add(targets[i]);
			}
		}
		double[] value2 = new double[St.size()];		
		long[] targets2 = new long[tt.size()];
		for(int i=0;i<St.size();i++)
			value2[i] = St.get(i);	
		for(int i=0;i<tt.size();i++)
			targets2[i] = tt.get(i);	
		//double[] value2 = St.stream().mapToDouble(i -> i).toArray();
		//long[] targets2 = tt.stream().mapToLong(i -> i).toArray();
		
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MDouble mdouble = geometryFactory.createMDouble(value2, targets2);
		return mdouble;
	}

	public static MDouble CalDistance(MGeometry mg, Geometry geo, TimeGenerator timeFunc) {
		ArrayList<Double> distances = new ArrayList<Double>();
		ArrayList<Long> times = new ArrayList<Long>();
		Double temp = 0.0;
		for (long time : timeFunc.genTimes(mg, geo)) {
			if (mg.snapshot(time) == null) {
			} else if (mg.snapshot(time).within(geo)) {
				temp = 0.0;
			} else {
				Coordinate[] c = DistanceOp.nearestPoints(geo, mg.snapshot(time));
				temp = d(c[0], mg.snapshot(time).getCoordinates()[0]);
			}
			distances.add(temp);
			times.add(time);
		}
		double[] value = new double[distances.size()];		
		long[] targets = new long[times.size()];
		for(int i=0;i<distances.size();i++)
			value[i] = distances.get(i);	
		for(int i=0;i<times.size();i++)
			targets[i] = times.get(i);
		//double[] value = distances.stream().mapToDouble(i -> i).toArray();
		//long[] targets = times.stream().mapToLong(i -> i).toArray();
		
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MDouble mdouble = geometryFactory.createMDouble(value, targets);
		return mdouble;
	}

	public static MDouble CalDistance(Geometry geo, MGeometry mg, TimeGenerator timeFunc) {
		ArrayList<Double> distances = new ArrayList<Double>();
		ArrayList<Long> times = new ArrayList<Long>();
		Double temp = 0.0;
		for (long time : timeFunc.genTimes(geo, mg)) {
			if (mg.snapshot(time) == null) {
			} else if (mg.snapshot(time).within(geo)) {
				temp = 0.0;
			} else {
				Coordinate[] c = DistanceOp.nearestPoints(geo, mg.snapshot(time));
				temp = d(c[0], mg.snapshot(time).getCoordinates()[0]);
			}
			distances.add(temp);
			times.add(time);
		}
		double[] value = new double[distances.size()];		
		long[] targets = new long[times.size()];
		for(int i=0;i<distances.size();i++)
			value[i] = distances.get(i);	
		for(int i=0;i<times.size();i++)
			targets[i] = times.get(i);
		//double[] value = distances.stream().mapToDouble(i -> i).toArray();
		//long[] targets = times.stream().mapToLong(i -> i).toArray();
		
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MDouble mdouble = geometryFactory.createMDouble(value, targets);
		return mdouble;
	}
	private static double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	public static double d(Coordinate p1, Coordinate p2) {
	/*	double x1 = p1.x;
		double y1 = p1.y;
		double x2 = p2.x;
		double y2 = p2.y;
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		*/
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
}

