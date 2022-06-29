package com.awarematics.postmedia.algorithms.similarity;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import com.awarematics.postmedia.types.mediamodel.FoV;
import com.awarematics.postmedia.types.mediamodel.Frame;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class MEDLCV {

	private static double[][] dist_matrix;

	public double calculate(MVideo mtr1, MVideo mtr2) {
	
		double[][] c = null;

		c = new double[mtr1.numOf() + 1][mtr2.numOf() + 1];
		for (int i = 0; i < mtr1.numOf(); i++) {
			c[i][0] = 0;
		}
		for (int j = 0; j <= mtr2.numOf(); j++) {
			c[0][j] = 0;
		}


		dist_matrix = new double[mtr2.numOf()][mtr1.numOf()];
		double[] directions1 = printDirection(mtr1.getCoords());
		double[] directions2 = printDirection(mtr2.getCoords());
	
		for (int i = 0; i < dist_matrix.length; i++) {
			for (int j = 0; j < dist_matrix[0].length; j++) {
				if (i == 0 || j == 0) {
					dist_matrix[i][j] = 0;
				} else {
					dist_matrix[i][j] = 1-calcCVW(mtr1.getFrame()[i], mtr2.getFrame()[j]);
				}
			}
		}
		
		
		if(mtr1.numOf() ==0) return mtr2.numOf();
		if(mtr2.numOf() ==0) return mtr1.numOf();
		
		c = new double[mtr1.numOf()+1][mtr2.numOf()+1];
		for (int i = 0; i < mtr1.numOf(); i++) {
			c[i][0] = 0;
		}
		for (int j = 0; j <= mtr2.numOf(); j++) {
			c[0][j] = 0;
		}
		double movingdir = 30;
		double disratio = 0.001;
		double viewdir = 60;
		
		for (int i = 1; i <= mtr1.numOf(); i++) {
			for (int j = 1; j <= mtr2.numOf(); j++) {
				if ( Math.abs(directions1[i-1]- directions2[j-1])<= movingdir && Math.abs(distanceratio(mtr1.getCoords(),i-1)-distanceratio(mtr2.getCoords(),j-1)) <= disratio && Math.abs(mtr1.getFrame()[i-1].getFov().getDirection2d()-mtr2.getFrame()[j-1].getFov().getDirection2d())<= viewdir) {
					c[i][j] = c[i - 1][j - 1];
				} else  {
					c[i][j] = Math.min(Math.min(c[i - 1][j], c[i][j - 1]), c[i - 1][j - 1]) + dist_matrix[i-1][j-1];
				}
			}
		}
		
		return c[mtr1.numOf()][mtr2.numOf()]/Math.max(mtr1.numOf(), mtr2.numOf());
	}
	
	/*
	 * compute distance ratio
	 */
	public double distanceratio(Coordinate[] coords, int length) {
		double result =0;
		double total =0;
		for(int i=0;i<coords.length-1;i++)
		{
			total = total+ Math.sqrt((coords[i].x - coords[i+1].x) * (coords[i].x -coords[i+1].x) + (coords[i].y - coords[i+1].y) *  (coords[i].y - coords[i+1].y));
		}
		if(length == coords.length-1) return 0;
		else
			result = Math.sqrt((coords[length].x - coords[length+1].x) * (coords[length].x -coords[length+1].x) + (coords[length].y - coords[length+1].y) *  (coords[length].y - coords[length+1].y))/total;
		return result;
	}
	public double calcCVW(Frame f1, Frame f2) {
		double cvw = 0.0;
		Polygon polygon = genFoVArea(f1.getCoos().x, f1.getCoos().y, f1.getFov(), 5);
		Polygon polygon2 = genFoVArea(f2.getCoos().x, f2.getCoos().y, f2.getFov(), 5);
		if (polygon.getEnvelope().intersects(polygon2.getEnvelope())) {
			Geometry pl3 = polygon.intersection(polygon2);
			Geometry pl5 = polygon2.union(polygon);
			cvw = pl3.getArea() / pl5.getArea();
		}
		return cvw;
	}

	public Polygon genFoVArea(double x, double y, FoV fov, int unitAngle) {
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
	public double[] printDirection(Coordinate[] coordsxy) {
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
}