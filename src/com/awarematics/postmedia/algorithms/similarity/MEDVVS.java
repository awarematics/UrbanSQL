package com.awarematics.postmedia.algorithms.similarity;


import org.locationtech.jts.geom.Coordinate;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class MEDVVS {

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
		/*
		 * get vvd result array
		 */

		double[] directions1 = printDirection(mtr1.getCoords());
		double[] directions2 = printDirection(mtr2.getCoords());
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
					if( (pointY1 + pointY2) == 0)	prependicular =0;
					else
						prependicular = (pointY1 * pointY1 + pointY2 * pointY2) / (pointY1 + pointY2);
				
					// get angle distance by vector compute
					angledistance =  Math.sqrt((mtr1.getCoords()[j].x - mtr1.getCoords()[j - 1].x) * (mtr1.getCoords()[j].x - mtr1.getCoords()[j - 1].x) + (mtr1.getCoords()[j].y - mtr1.getCoords()[j - 1].y) * (mtr1.getCoords()[j].y - mtr1.getCoords()[j - 1].y));				
					movingangle = Math.abs(directions1[j-1]-directions2[j-1]);
					
					// get vector distance
					vectordistance = (mtr1.getCoords()[j].x - mtr1.getCoords()[j - 1].x) * (mtr1.getCoords()[i].x - mtr1.getCoords()[i - 1].x) + (mtr1.getCoords()[j].y - mtr1.getCoords()[j - 1].y) * (mtr1.getCoords()[i].y - mtr1.getCoords()[i - 1].y);
					viewangle = Math.abs(mtr1.getFrame()[j].getFov().getDirection2d() - mtr2.getFrame()[i].getFov().getDirection2d());
									
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
		
		return c[mtr1.numOf()][mtr2.numOf()];
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