package com.awarematics.postmedia.algorithms.similarity;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import com.awarematics.postmedia.types.mediamodel.FoV;
import com.awarematics.postmedia.types.mediamodel.Frame;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class MLCVSwithMBT {
	MVideo mv1;
	MVideo mv2;

	public MLCVSwithMBT() {

	}

	public double measure(MVideo mv1, MVideo mv2, double delta) {
		double[][] c = null;

		c = new double[mv1.numOf() + 1][mv2.numOf() + 1];
		for (int i = 0; i < mv1.numOf(); i++) {
			c[i][0] = 0;
		}
		for (int j = 0; j <= mv2.numOf(); j++) {
			c[0][j] = 0;
		}

	
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
		Polygon polygon = genFoVArea(f1.getCoos().x, f1.getCoos().y, f1.getFov());
		Polygon polygon2 = genFoVArea(f2.getCoos().x, f2.getCoos().y, f2.getFov());
		if (polygon.getEnvelope().intersects(polygon2.getEnvelope())) {
			Geometry pl3 = polygon.intersection(polygon2);
			Geometry pl5 = polygon2.union(polygon);
			cvw = pl3.getArea() / pl5.getArea();
		}
		return cvw;
	}

	private Polygon genFoVArea(double x, double y, FoV fov) {
		int times = 1;
		double x4 = (double) x;
		double y4 = (double) y;

		double x2 = (double) x + fov.getDistance() * 2 / Math.sqrt(3)
				* Math.sin(Math.toRadians(fov.getDirection2d() + (fov.getHorizontalAngle()) / 2)) * times;
		double y2 = (double) y + fov.getDistance() * 2 / Math.sqrt(3)
				* Math.cos(Math.toRadians(fov.getDirection2d() + (fov.getHorizontalAngle()) / 2)) * times;// left
		double x3 = (double) x + fov.getDistance() * 2 / Math.sqrt(3)
				* Math.sin(Math.toRadians(fov.getDirection2d() - (fov.getHorizontalAngle()) / 2)) * times;
		double y3 = (double) y + fov.getDistance() * 2 / Math.sqrt(3)
				* Math.cos(Math.toRadians(fov.getDirection2d() - (fov.getHorizontalAngle()) / 2)) * times;// right
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

	public double similarity(MVideo mg1, MVideo mg2, double omega) {
		double lcss_distance = 0.0;
		double similarity = 0.0;
		lcss_distance = measure(mg1, mg2, omega);
		similarity = lcss_distance / (Math.min(mg1.numOf(), mg2.numOf()));
		return similarity;
	}


}
