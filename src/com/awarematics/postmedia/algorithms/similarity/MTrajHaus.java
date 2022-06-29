package com.awarematics.postmedia.algorithms.similarity;

import java.util.ArrayList;
import java.util.Collections;

import org.locationtech.jts.io.ParseException;

import com.awarematics.postmedia.types.mediamodel.MGeometry;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class MTrajHaus {
	
	private static double[][] dist_matrix;	
	@SuppressWarnings("unused")
	private MVideo mtr1;
	@SuppressWarnings("unused")
	private MVideo mtr2;  
	public void M_Trajhausdorff (MVideo mtr1, MVideo mtr2){
		this.mtr1 = mtr1;
		this.mtr2 = mtr2;
		//dist_matrix = new double[mtr1.size()][mtr2.size()];
	}
	//@Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static double caculate(String ss1,String ss2 ) throws java.text.ParseException, ParseException
	{
		MVideo mtr1, mtr2;
		MTrajHaus mtra ;

		mtr1 = new MVideo();
		mtr2 = new MVideo();
		dist_matrix = new double[mtr1.numOf()][mtr2.numOf()];
		mtra = new MTrajHaus();
		double result = mtra.calculate(mtr1, mtr2);		
		return result;
//		return 0;
	}
	
	public double calculate(MVideo mtr1,MVideo mtr2 ){
		double result = 0.0;
		double parallel= 0.0;
		double prependicular = 0.0;
		double angle = 0.0;
		double dangle = 0.0;
		
		dist_matrix = new double[mtr2.numOf()][mtr1.numOf()];
		ArrayList<Double> minDistances1 = new ArrayList<Double>();
		ArrayList<Double> minDistances2 = new ArrayList<Double>();
		for(int i = 1; i< dist_matrix.length; i++){
			for(int j = 1; j < dist_matrix[0].length; j++){	
				if(i==0||j==0){dist_matrix[i][j]=0;}
				else{
			
				// get parallel distance
				double pointX1 = Math.abs(mtr1.getCoords()[j].x-mtr2.getCoords()[i].x);
				double pointX2 = Math.abs(mtr1.getCoords()[j-1].x-mtr2.getCoords()[i-1].x);
				parallel = Math.min(pointX1, pointX2);
				// get prependicular distance 
				double pointY1 =Math.abs(mtr1.getCoords()[j].y-mtr2.getCoords()[i].y);
				double pointY2 = Math.abs(mtr1.getCoords()[j-1].y-mtr2.getCoords()[i-1].y);
				prependicular = (pointY1*pointY1+pointY2*pointY2)/(pointY1+pointY2);
				// get angle distance by vector compute
			    dangle = (pointX1*pointX2+pointY1*pointY2)/(Math.sqrt(pointX1*pointX1+pointY1*pointY1)*Math.sqrt(pointX2*pointX2+pointY2*pointY2));
			    double sum = Math.acos(dangle);
			    angle = sum/Math.PI*180;
			    if(angle>=90){
			    	//dangle = s;
			    	dist_matrix[i][j] = Math.abs(dangle)+prependicular+parallel;
			    }
			    else 
			    	dist_matrix[i][j] = Math.abs(dangle*Math.sin(angle))+prependicular+parallel;
				}
			}
		}
		for(int i = 1; i< dist_matrix.length; i++){
			double min = Double.POSITIVE_INFINITY;
			for(int j = 1; j < dist_matrix[0].length; j++){
				if(dist_matrix[i][j] <= min){
					min = dist_matrix[i][j];
				}
			}
			minDistances1.add(min);
		}
		for(int i = 1; i< dist_matrix[0].length; i++){
			double min = Double.POSITIVE_INFINITY;
			for(int j = 0; j < dist_matrix.length; j++){
				if(dist_matrix[j][i] <= min){
					min = dist_matrix[j][i];
				}
			}
			minDistances2.add(min);
		}
		Collections.sort(minDistances1);
		Collections.sort(minDistances2);
		
		double value1 =  minDistances1.get(minDistances1.size()-1);
		double value2 =  minDistances2.get(minDistances2.size()-1);
		result = Math.max(value1, value2);
		return result;
	}
	
	public double calculate(MGeometry mtr1,MGeometry mtr2 ){
		double result = 0.0;
		double parallel= 0.0;
		double prependicular = 0.0;
		double angle = 0.0;
		double dangle = 0.0;
		
		dist_matrix = new double[mtr2.numOf()][mtr1.numOf()];
		ArrayList<Double> minDistances1 = new ArrayList<Double>();
		ArrayList<Double> minDistances2 = new ArrayList<Double>();
		for(int i = 1; i< dist_matrix.length; i++){
			for(int j = 1; j < dist_matrix[0].length; j++){	
				if(i==0||j==0){dist_matrix[i][j]=0;}
				else{
			
				// get parallel distance
				double pointX1 = Math.abs(mtr1.getCoords()[j].x-mtr2.getCoords()[i].x);
				double pointX2 = Math.abs(mtr1.getCoords()[j-1].x-mtr2.getCoords()[i-1].x);
				parallel = Math.min(pointX1, pointX2);
				// get prependicular distance 
				double pointY1 =Math.abs(mtr1.getCoords()[j].y-mtr2.getCoords()[i].y);
				double pointY2 = Math.abs(mtr1.getCoords()[j-1].y-mtr2.getCoords()[i-1].y);
				prependicular = (pointY1*pointY1+pointY2*pointY2)/(pointY1+pointY2);
				// get angle distance by vector compute
			    dangle = (pointX1*pointX2+pointY1*pointY2)/(Math.sqrt(pointX1*pointX1+pointY1*pointY1)*Math.sqrt(pointX2*pointX2+pointY2*pointY2));
			    double sum = Math.acos(dangle);
			    angle = sum/Math.PI*180;
			    if(angle>=90){
			    	//dangle = s;
			    	dist_matrix[i][j] = Math.abs(dangle)+prependicular+parallel;
			    }
			    else 
			    	dist_matrix[i][j] = Math.abs(dangle*Math.sin(angle))+prependicular+parallel;
				}
			}
		}
		for(int i = 1; i< dist_matrix.length; i++){
			double min = Double.POSITIVE_INFINITY;
			for(int j = 1; j < dist_matrix[0].length; j++){
				if(dist_matrix[i][j] <= min){
					min = dist_matrix[i][j];
				}
			}
			minDistances1.add(min);
		}
		for(int i = 1; i< dist_matrix[0].length; i++){
			double min = Double.POSITIVE_INFINITY;
			for(int j = 0; j < dist_matrix.length; j++){
				if(dist_matrix[j][i] <= min){
					min = dist_matrix[j][i];
				}
			}
			minDistances2.add(min);
		}
		Collections.sort(minDistances1);
		Collections.sort(minDistances2);
		
		double value1 =  minDistances1.get(minDistances1.size()-1);
		double value2 =  minDistances2.get(minDistances2.size()-1);
		result = Math.max(value1, value2);
		return result;
	}
}