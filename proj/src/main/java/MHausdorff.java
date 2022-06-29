package com.awarematics.postmedia.algorithms.similarity;

import java.util.ArrayList;
import java.util.Collections;

import org.locationtech.jts.geom.Coordinate;
//import org.postgresql.pljava.annotation.Function;
//import static org.postgresql.pljava.annotation.Function.Effects.IMMUTABLE;
//import static org.postgresql.pljava.annotation.Function.OnNullInput.RETURNS_NULL;
import org.locationtech.jts.io.ParseException;
import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.MDouble;
import com.awarematics.postmedia.types.mediamodel.MGeometry;
import com.awarematics.postmedia.types.mediamodel.MPhoto;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class MHausdorff implements MSimilarityMeasure {	
	public static final long DEFAULT_TIME_LATTICE_UNIT = 1000;
	public static final int MAX_VALUE = 7732345;
	MDouble mdoubleArrayA;
	public long timeLattice = DEFAULT_TIME_LATTICE_UNIT;

	private static double[][] dist_matrix;
	@SuppressWarnings("unused")
	private MPoint mtr1;
	@SuppressWarnings("unused")
	private MPoint mtr2;
	@SuppressWarnings("unused")
	private MPhoto m1;
	@SuppressWarnings("unused")
	private MPhoto m2;
	@SuppressWarnings("unused")
	private MVideo mv;
	@SuppressWarnings("unused")
	private MVideo mv2;

	public void M_hausdorff(MPoint mtr1, MPoint mtr2) {
		this.mtr1 = mtr1;
		this.mtr2 = mtr2;
		dist_matrix = new double[mtr2.numOf()][mtr1.numOf()];
	}

	public void M_hausdorff(MPhoto m1, MPhoto m2) {
		this.m1 = m1;
		this.m2 = m2;
		dist_matrix = new double[m2.numOf()][m1.numOf()];
	}
	public void M_hausdorff(MVideo mv, MVideo mv2) {
		this.mv = mv;
		this.mv2 = mv2;
		dist_matrix = new double[mv2.numOf()][mv.numOf()];
	}

	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static double measure(String mpString1, String mpString2) throws java.text.ParseException, ParseException {		
		MHausdorff mhaus;
		MWKTReader reader = new MWKTReader(); 
		MGeometry mtr1;
		MGeometry mtr2;
		mtr1 = (MGeometry) reader.read( mpString1 );
		mtr2 = (MGeometry) reader.read( mpString2 );
		dist_matrix = new double[mtr2.numOf()][mtr1.numOf()];
		mhaus = new MHausdorff();
		double result = mhaus.measure(mtr1, mtr2);
		return result;
	}

	private double measure(MGeometry g1, MGeometry g2, long time) {
		double result = 0.0;
		//MGeometry mg1 = g1.atomize(time);
		Coordinate[] coords = null;
		Coordinate[] coords2 = null;
		coords = g1.spatial().getCoordinates();
		coords2 = g2.spatial().getCoordinates();
		long[] t_value = g1.getTimes();
		//MGeometry mg2 = g2.atomize(time);
		
		ArrayList<Double> minDistances1 = new ArrayList<Double>();
		ArrayList<Double> minDistances2 = new ArrayList<Double>();
		dist_matrix = new double[g1.numOf()][g2.numOf()];


		for (int i = 0; i <g1.numOf(); i++) {
			for (int j = 0; j < g2.numOf(); j++) {
				dist_matrix[i][j] = (calcEuclideanDistance(coords[i].x,coords2[j].x,coords[i].y,coords2[j].y));
			}
		}
		for (int i = 0; i < g1.numOf(); i++) {
			double min = Double.POSITIVE_INFINITY;
			for (int j = 0; j < g2.numOf(); j++) {
				if (dist_matrix[i][j] <= min) {
					min = dist_matrix[i][j];
				}
			}
			minDistances1.add(min);
		}
		MGeometryFactory geometryFactory = new MGeometryFactory();
		double[] tempList = new double[minDistances1.size()];
		//double[] tempList = minDistances1.stream().mapToDouble(i -> i).toArray();
		for(int i =0;i< minDistances1.size();i++)
			tempList[i] = minDistances1.get(i);
		mdoubleArrayA = geometryFactory.createMDouble(tempList, t_value);
		for (int i = 0; i <g2.numOf(); i++) {
			double min = Double.POSITIVE_INFINITY;
			for (int j = 0; j <g1.numOf(); j++) {
				if (dist_matrix[j][i] <= min) {
					min = dist_matrix[j][i];
				}
			}
			minDistances2.add(min);
		}
		Collections.sort(minDistances1);
		Collections.sort(minDistances2);

		double value1 = minDistances1.get(minDistances1.size() - 1);
		double value2 = minDistances2.get(minDistances2.size() - 1);
		result = Math.max(value1, value2);
		return result;
	}

	private static double calcEuclideanDistance(double x1, double x2, double y1, double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	public MDouble Result() {
		return mdoubleArrayA;
	}
	@Override
	public double measure(MGeometry g1, MGeometry g2) {
		return this.measure(g1, g2, DEFAULT_TIME_LATTICE_UNIT);
	}
}