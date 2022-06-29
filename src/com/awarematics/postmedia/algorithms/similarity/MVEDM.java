package com.awarematics.postmedia.algorithms.similarity;

import java.util.ArrayList;
import java.util.Collections;

import org.locationtech.jts.geom.Coordinate;

import com.awarematics.postmedia.types.mediamodel.MVideo;

public class MVEDM {

	private static double[][] dist_matrix;

	public double calculate(MVideo mtr1, MVideo mtr2) {
		double result = 0.0;
		

		dist_matrix = new double[mtr2.numOf()][mtr1.numOf()];
		ArrayList<Double> minDistances1 = new ArrayList<Double>();
		ArrayList<Double> minDistances2 = new ArrayList<Double>();
		double[] directions1 = printDirection(mtr1.getCoords());
		double[] directions2 = printDirection(mtr2.getCoords());
		//System.out.println(dist_matrix.length);
		for (int i = 1; i < dist_matrix.length; i++) {
			for (int j = 1; j < dist_matrix[0].length; j++) {
				double parallel = 0.0;
				double prependicular = 0.0;
				double movingangle = 0.0;
				double viewangle = 0.0;
				double angledistance = 0.0;
				double vectordistance = 0.0;
				if (i == 0 || j == 0) {
					dist_matrix[i][j] = 0;
				} else {

					// get parallel distance
					double pointX1 = Math.abs(mtr1.getCoords()[j].x - mtr2.getCoords()[i].x);
					double pointX2 = Math.abs(mtr1.getCoords()[j - 1].x - mtr2.getCoords()[i - 1].x);
					parallel = Math.min(pointX1, pointX2);
					
					// get prependicular distance
					double pointY1 = Math.abs(mtr1.getCoords()[j].y - mtr2.getCoords()[i].y);
					double pointY2 = Math.abs(mtr1.getCoords()[j - 1].y - mtr2.getCoords()[i - 1].y);
					prependicular = (pointY1 * pointY1 + pointY2 * pointY2) / (pointY1 + pointY2);
				
					// get angle distance by vector compute
					angledistance =  Math.sqrt((mtr1.getCoords()[j].x - mtr1.getCoords()[j - 1].x) * (mtr1.getCoords()[j].x - mtr1.getCoords()[j - 1].x) + (mtr1.getCoords()[j].y - mtr1.getCoords()[j - 1].y) * (mtr1.getCoords()[j].y - mtr1.getCoords()[j - 1].y));				
					movingangle = Math.abs(directions1[j-1]-directions2[j-1]);
					//System.out.println(directions[i]);
					//System.out.println(movingangle+"\tparallel");
					// get vector distance
					vectordistance = Math.sqrt((mtr1.getCoords()[j].x - mtr1.getCoords()[j - 1].x) * (mtr1.getCoords()[j].x - mtr1.getCoords()[j - 1].x) + (mtr1.getCoords()[j].y - mtr1.getCoords()[j - 1].y) * (mtr1.getCoords()[j].y - mtr1.getCoords()[j - 1].y));
					viewangle = Math.abs(mtr1.getFrame()[j].getFov().getDirection2d() - mtr2.getFrame()[i].getFov().getDirection2d());
					
					System.out.println(movingangle+"\t"+viewangle);
					if (movingangle >= 90 && viewangle >= 90) {
						// dangle = s;
						dist_matrix[i][j] = Math.abs(angledistance) + prependicular + parallel + vectordistance;
					} else if (movingangle <= 90 && viewangle <= 90) {
						dist_matrix[i][j] = Math.abs(angledistance * Math.sin(movingangle)) + prependicular + parallel + vectordistance * Math.sin(viewangle);
					} else if (movingangle <= 90 && viewangle >= 90) {
						dist_matrix[i][j] = Math.abs(angledistance * Math.sin(movingangle)) + prependicular + parallel + vectordistance;
					} else if (movingangle >= 90 && viewangle <= 90) {
						dist_matrix[i][j] = angledistance + prependicular + parallel + vectordistance * Math.sin(viewangle);
					}
				}
			}
		}
		for (int i = 1; i < dist_matrix.length; i++) {
			double min = Double.POSITIVE_INFINITY;
			for (int j = 1; j < dist_matrix[0].length; j++) {
				if (dist_matrix[i][j] <= min) {
					min = dist_matrix[i][j];
				}
			}
			minDistances1.add(min);
		}
		for (int i = 1; i < dist_matrix[0].length; i++) {
			double min = Double.POSITIVE_INFINITY;
			for (int j = 0; j < dist_matrix.length; j++) {
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