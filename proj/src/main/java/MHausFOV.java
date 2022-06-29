package com.awarematics.postmedia.algorithms.similarity;

//import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.locationtech.jts.geom.Coordinate;
//import org.postgresql.pljava.annotation.Function;
//import static org.postgresql.pljava.annotation.Function.Effects.IMMUTABLE;
//import static org.postgresql.pljava.annotation.Function.OnNullInput.RETURNS_NULL;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.MDouble;
import com.awarematics.postmedia.types.mediamodel.MGeometry;
import com.awarematics.postmedia.types.mediamodel.MPoint;

public class MHausFOV implements MSimilarityMeasure  {
	public static final long DEFAULT_TIME_LATTICE_UNIT = 1000;
	public static final double DEFAULT_THEAT = 1;
	public static final int MAX_VALUE = 7732345;
	MDouble mdoubleArrayA;
	private static double[][] dist_matrix;

	public void M_HausdFOV(MPoint mtr1, MPoint mtr2, double theta) {
		dist_matrix = new double[mtr2.numOf()][mtr1.numOf()];
	}

	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static double measure(String mpString1, String mpString2, double theta) throws java.text.ParseException, ParseException
			 {
		MPoint mtr1, mtr2;
		MHausFOV mhaus;

		MWKTReader reader = new MWKTReader(); 
		mtr1 = (MPoint)reader.read( mpString1 );
		mtr2 = (MPoint)reader.read( mpString2 );
		
		dist_matrix = new double[mtr2.numOf()][mtr1.numOf()];
		mhaus = new MHausFOV();
		double result = mhaus.measure(mtr1, mtr2);
		return result;
	}

	public double measure(MGeometry g1, MGeometry g2, double theta, long time) {
		double result = 0.0;
		double parallel = 0.0;
		double prependicular = 0.0;
		double angle = 0.0;
		double dangle = 0.0;
		//MGeometry mg1 = g1.atomize(time);
		//MGeometry mg2 = g2.atomize(time);
		Coordinate[] coords = null;
		Coordinate[] coords2 = null;
		coords = g1.spatial().getCoordinates();
		coords2 = g2.spatial().getCoordinates();
		long[] t_value = g1.getTimes();
		
		
		
		//if(g2 instanceof MVideo)
		//	coords2  = ((MVideo)mg2).getCoords();
		//long[] t1_value = mg2.getTimes();
		ArrayList<Double> minDistances1 = new ArrayList<Double>();
		ArrayList<Double> minDistances2 = new ArrayList<Double>();
		dist_matrix = new double[g1.numOf()][g2.numOf()];

		for (int i = 1; i < g1.numOf(); i++) {
			for (int j = 1; j <g2.numOf(); j++) {
				
				double pointX1 = Math.abs(coords[j].x - coords2[j-1].x);
				double pointX2 = Math.abs(coords[j-1].x - coords2[i-1].x);
				double pointY1 = Math.abs(coords[j].y - coords2[i].y);
				double pointY2 = Math.abs(coords[j-1].y - coords2[i-1].y);

				parallel = Math.min(pointX1, pointX2);
				prependicular = (pointY1 * pointY1 + pointY2 * pointY2) / (pointY1 + pointY2);
				double s = (pointX1 * pointX2 + pointY1 * pointY2) / (Math.sqrt(pointX1 * pointX1 + pointY1 * pointY1)
						* Math.sqrt(pointX2 * pointX2 + pointY2 * pointY2));
				double sum = Math.acos(s);
				angle = sum / Math.PI * 180;
				if (angle >= 90) {
					dangle = Math.sqrt((coords[i].x - coords[i-1].x) * (coords[i].x - coords[i-1].x)
							+ (coords[i].y - coords[i-1].y) * (coords[i].y - coords[i-1].y));
					dist_matrix[i-1][j-1] = (Math.abs(dangle) + prependicular + parallel);
				} else
					dist_matrix[i-1][j-1] = (Math.abs(angle) + prependicular + parallel);
			}
		}
		for (int i = 0; i < g2.numOf()-1; i++) {
			double min = MAX_VALUE;
			for (int j = 0; j < g1.numOf()-1; j++) {
				if (dist_matrix[i][j] <= min) {
					min = dist_matrix[i][j];
				}
			}
			minDistances1.add(min);
		}
		for (int i = 0; i < g1.numOf()-1; i++) {
			double min = MAX_VALUE;
			for (int j = 1; j < g2.numOf()-1; j++) {
				if (dist_matrix[j][i] <= min) {
					min = dist_matrix[j][i];
				}
			}
			minDistances2.add(min);
		}     
		
  
		
		MGeometryFactory geometryFactory = new MGeometryFactory();
		//double[] tempList = minDistances1.stream().mapToDouble(i -> i).toArray();
		/*
		 * update for java 1.7
		 */
		double[] tempList = new double[minDistances1.size()];
		for(int i=0; i<tempList.length; i++)
		{
			tempList[i] = minDistances1.get(i);
		}
		
		
		mdoubleArrayA = geometryFactory.createMDouble(tempList, t_value);

		Collections.sort(minDistances1);
		Collections.sort(minDistances2);
		double value1 = minDistances1.get(minDistances1.size() - 1);
		double value2 = minDistances2.get(minDistances2.size() - 1);
		result = Math.max(value1, value2);
		return result;
	}
  
	@SuppressWarnings("unused")
	private double distance(Polygon polygon, Polygon polygon2, double theta) {
		double number = 0;
		try {
			Geometry pl3 = polygon.intersection(polygon2);
			Geometry pl4 = polygon2.intersection(polygon);
			Geometry pl5 = polygon2.union(polygon);
			double ttt = Math.max(pl3.getArea() / pl5.getArea(), pl4.getArea() / pl5.getArea());
			if (ttt >= 0.3) {
				number = 1;
			} else
				number = ttt;
		} catch (Exception e) {
		}
		return (number);
	}

	public MDouble Result() {
		return mdoubleArrayA;
	}

	@Override
	public double measure(MGeometry g1, MGeometry g2) {
		return this.measure(g1, g2,DEFAULT_THEAT, DEFAULT_TIME_LATTICE_UNIT);
	}
}