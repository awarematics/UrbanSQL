package com.awarematics.postmedia.algorithms.similarity;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import com.awarematics.postmedia.types.mediamodel.FoV;
import com.awarematics.postmedia.types.mediamodel.Frame;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class MLCVS {

	public static final int UNIT_ANGLE = 5;

	MVideo mv1;
	MVideo mv2;

	// public static final int EPSILON_R = 2;
	public double measure(MVideo mv1, MVideo mv2, double delta) {
		double[][] c = null;

		c = new double[mv1.numOf() + 1][mv2.numOf() + 1];
		for (int i = 0; i < mv1.numOf(); i++) {
			c[i][0] = 0;
		}
		for (int j = 0; j <= mv2.numOf(); j++) {
			c[0][j] = 0;
		}

		//FoV[] fov1 = mv1.getFov();
		//FoV[] fov2 = mv2.getFov();
		for (int i = 1; i <= mv1.numOf(); i++) {
			for (int j = 1; j <= mv2.numOf(); j++) {
				if (Math.abs(j - i) <= delta) {
					c[i][j] = c[i - 1][j - 1] + calcCVW(mv1.getFrame()[i - 1], mv2.getFrame()[j - 1]);
				} else if (c[i - 1][j] <= c[i][j - 1]) {
					c[i][j] = c[i][j - 1];
				} else {
					c[i][j] = c[i - 1][j];
				}
			}
		}
		return c[mv1.numOf()][mv2.numOf()];
	}


	private double calcCVW(Frame f1, Frame f2) {

		double cvw = 0.0;
		Polygon polygon = genFoVArea(f1.getCoos().x, f1.getCoos().y, f1.getFov(), UNIT_ANGLE);
		Polygon polygon2 = genFoVArea(f2.getCoos().x, f2.getCoos().y, f2.getFov(), UNIT_ANGLE);
		if (polygon.getEnvelope().intersects(polygon2.getEnvelope())) {
			Geometry pl3 = polygon.intersection(polygon2);
			Geometry pl5 = polygon2.union(polygon);
			cvw = pl3.getArea() / pl5.getArea();
		}
		return cvw;
	}

	private Polygon genFoVArea(double x, double y, FoV fov, int unitAngle) {
		int times = 1;

		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate[] coor1 = new Coordinate[(int) (fov.getHorizontalAngle()) / unitAngle + 2];
		coor1[0] = new Coordinate(x, y);

		for (int i = 0; i < (int) (fov.getHorizontalAngle()) / unitAngle; i++) {
			double x_temp = x + fov.getDistance()
					* Math.sin(Math.toRadians(fov.getDirection2d() - (fov.getHorizontalAngle()) / 2 + unitAngle * i)) * times;
			double y_temp = y + fov.getDistance()
					* Math.cos(Math.toRadians(fov.getDirection2d() - (fov.getHorizontalAngle()) / 2 + unitAngle * i)) * times;
			coor1[i + 1] = new Coordinate(x_temp, y_temp);
		}
		coor1[(int) (fov.getHorizontalAngle()) / unitAngle + 1] = new Coordinate(x, y);
		LinearRing line = geometryFactory.createLinearRing(coor1);
		Polygon pl1 = geometryFactory.createPolygon(line, null);
		return pl1;

	}

	public double similarity(MVideo mg1, MVideo mg2, double omega) {
		double lcss_distance = 0.0;
		double similarity = 0.0;
		lcss_distance = measure(mg1, mg2, omega);
		similarity = lcss_distance / (Math.min(mg1.numOf(), mg2.numOf()));
		return similarity;
	}


}
