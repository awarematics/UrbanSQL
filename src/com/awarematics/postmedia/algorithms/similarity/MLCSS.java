package com.awarematics.postmedia.algorithms.similarity;
//import java.text.DecimalFormat;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;

import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.types.mediamodel.MDouble;
import com.awarematics.postmedia.types.mediamodel.MGeometry;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import com.awarematics.postmedia.types.mediamodel.MVideo;

//import org.postgresql.pljava.annotation.Function;
//import static org.postgresql.pljava.annotation.Function.Effects.IMMUTABLE;
//import static org.postgresql.pljava.annotation.Function.OnNullInput.RETURNS_NULL;
public class MLCSS implements MSimilarityMeasure  {
	public static final long DEFAULT_TIME_LATTICE_UNIT = 1000;
	public static final double DEFAULT_OMEGA = 1;
	public static final double DEFAULT_EPSILON = 1;
	public static final int MAX_VALUE = 7732345;

	public static final int EPSILON_R = 2;
	@SuppressWarnings("unused")
	private double epsilon;
	@SuppressWarnings("unused")
	private double omega;
	MDouble mdoubleArrayA;

	// @Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static double measure(String mpString1, String mpString2, double epsilon, double omega)
			throws ParseException, java.text.ParseException {
		MPoint mtr1, mtr2;
		MLCSS mlcss;

		MWKTReader reader = new MWKTReader(); 
		mtr1 = (MPoint)reader.read( mpString1 );
		mtr2 = (MPoint)reader.read( mpString2 );

		mlcss = new MLCSS();
		//double result = mlcss.measure(mtr1, mtr2);
		double Lsim = mlcss.similarity(mtr1, mtr2, epsilon, omega);
		System.out.println("Lsim=" + Lsim);
		return Lsim;
	}

	public double measure(MGeometry g1, MGeometry g2, double epsilon, double omega, long time)
	{
		//MGeometry mg1 = g1.atomize(time);
		//MGeometry mg2 = g2.atomize(time);
		return measure( g1, g2, epsilon, omega );
	}

	public double measure(MGeometry g1, MGeometry g2,double epsilon, double delta)
	{
		double[][] c;

		
		Coordinate[] coords1 = null;
		Coordinate[] coords2 = null;
		coords1 = g1.getCoords();
		coords2 = g2.getCoords();
		
		c = new double[g1.numOf()+1][g2.numOf()+1];
		for (int i = 0; i < g1.numOf(); i++) {
			c[i][0] = 0;
		}
		for (int j = 0; j <= g2.numOf(); j++) {
			c[0][j] = 0;
		}
		for (int i = 1; i <= g1.numOf(); i++) {
			for (int j = 1; j <= g2.numOf(); j++) {
				double tp = calcEuclideanDistance(coords1[i-1].x, coords1[i-1].y, coords2[j-1].x, coords2[j-1].y);
				if (tp <= epsilon && Math.abs(j - i) <= delta) {
					c[i][j] = c[i - 1][j - 1] + 1;
				} else if (c[i - 1][j] <= c[i][j - 1]) {
					c[i][j] = c[i][j - 1];
				} else {
					c[i][j] = c[i - 1][j];
				} 
			}  
		}
		return c[g1.numOf()][g2.numOf()];
	}

	public double calculate(MVideo t1, MVideo t2) {

		return 0;
	}

	public double similarity(MGeometry mg1,MGeometry mg2, double epsilon, double omega) {
		double lcss_distance = 0.0;
		double similarity = 0.0;
		lcss_distance = measure(mg1, mg2, epsilon, omega);
		similarity = lcss_distance / ( Math.min( mg1.numOf(), mg2.numOf()) );
		return similarity;
	}

	private static double calcEuclideanDistance(double x, double y, double x2, double y2) {
		return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
	}

	public static double dist(Polygon g1, Polygon g2, double theta2) {
		double number = 0;
		try {
			Geometry pl3 = g1.intersection(g2);
			Geometry pl4 = g2.intersection(g1);
			Geometry pl5 = g2.union(g1);
			double ttt = Math.max(pl3.getArea() / pl5.getArea(), pl4.getArea() / pl5.getArea());// ¡É
			if (ttt >= theta2) {
				number = ttt;
			}
		} catch (Exception e) {
		}
		return (number);
	}

	public MDouble Result() {
		return mdoubleArrayA;
	}
	@Override
	public double measure(MGeometry g1, MGeometry g2) {
		return this.measure(g1, g2, DEFAULT_EPSILON, DEFAULT_OMEGA, DEFAULT_TIME_LATTICE_UNIT);
	}
}