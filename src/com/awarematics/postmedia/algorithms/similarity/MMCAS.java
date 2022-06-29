package com.awarematics.postmedia.algorithms.similarity;
 
import java.util.ArrayList;

import org.locationtech.jts.geom.*; 
import org.locationtech.jts.io.ParseException;

import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.MDouble;
import com.awarematics.postmedia.types.mediamodel.MGeometry;
import com.awarematics.postmedia.types.mediamodel.MPhoto;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import com.awarematics.postmedia.types.mediamodel.MPolygon;
import com.awarematics.postmedia.types.mediamodel.MVideo;

@SuppressWarnings("unused")
public class MMCAS {
	private MPoint t1;
	private MPoint t2;
	public static final long DEFAULT_TIME_LATTICE_UNIT = 1000;
	public static final double DEFAULT_OMEGA = 1;
	public static final double DEFAULT_THETA = 1;
	public static final double DEFAULT_EPSILON = 1;
	public static final int MAX_VALUE = 7732345;
	MDouble mdoubleArrayA;
	private static double[][] c;
	private double epsilon;
	private double theta;
	private double omega;

public MMCAS(MPoint mv1, MPoint mv2, double epsilon,double theta,double omega){
	this.t1 = mv1;
	this.t2 = mv2;
	this.epsilon = epsilon;
	this.theta = theta;
	this.omega = omega;
}
//@Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
public static double caculate(String mpString1, String mpString2, double epsilon, double theta,double omega) throws java.text.ParseException, ParseException{
	MPoint mtr1, mtr2;
	MMCAS mmcas ;

	
	MWKTReader reader = new MWKTReader(); 
	mtr1 = (MPoint)reader.read( mpString1 );
	mtr2 = (MPoint)reader.read( mpString2 );
	
	mmcas = new MMCAS(mtr1, mtr2, epsilon, theta, omega);
	double result = mmcas.measure(mtr1, mtr2);
	double Lsim=mmcas.similarity(result);
	return Lsim;
}
public double measure(MGeometry g1, MGeometry g2,double epsilon,double theta, double omega, long time){
	double result = 0.0;
	//MGeometry mg1 = g1.atomize(time);
	Coordinate[] coords = null;
	Coordinate[] coords2 = null;
	Polygon[] mp1 = null;
	Polygon[] mp2 = null;
	if(g1 instanceof MPoint)
		coords  = ((MPoint)g1).getCoords();
	if(g1 instanceof MPhoto)
	{
		coords  = ((MPhoto)g1).getCoords();
		mp1 =  ((MPhoto)g1).getListPolygon();
	}
	if(g1 instanceof MVideo)
	{
		coords  = ((MVideo)g1).getCoords();
		//mp1 =  ((MVideo)g1).getListPolygon();
	}
	long[] t_value = g1.getTimes();
	
	//MGeometry mg2 = g2.atomize(time);
	if(g2 instanceof MPoint)
		coords2  = ((MPoint)g2).getCoords();
	if(g2 instanceof MPhoto)
	{
		coords2  = ((MPhoto)g2).getCoords();
		mp2 =  ((MPhoto)g2).getListPolygon();
	}
	if(g2 instanceof MVideo)
	{
		coords2  = ((MVideo)g2).getCoords();
		//mp2 =  ((MVideo)g2).getListPolygon();
	}
	ArrayList<Double> minDistances1 = new ArrayList<Double>();
	ArrayList<Double> minDistances2 = new ArrayList<Double>();
	c = new double[g1.numOf()][g2.numOf()];

	for (int i = 0; i <= t1.numOf(); i++) {
		c[i][0] = 0;
	}
	for (int j = 0; j <= t2.numOf(); j++) {
		c[0][j] = 0;
	}
	for (int i = 1; i < t1.numOf(); i++) {
		double min = MAX_VALUE;
		for (int j = 1; j < t2.numOf(); j++) {
			double tp = d(coords[i].x, coords[i].y, coords2[j].x, coords2[j].y);
			double va =0;
			if( (g1 instanceof MPhoto && g2 instanceof MPhoto) || (g1 instanceof MVideo && g2 instanceof MVideo))
				va = dist(mp1[i],mp2[i],theta);
			if (tp <= epsilon && Math.abs(j - i) <= omega && va >= theta) {
				c[i][j] = c[i - 1][j - 1] + 1;
			} else if (c[i - 1][j] <= c[i][j - 1]) {
				c[i][j] = c[i][j - 1];
			} else {
				c[i][j] = c[i - 1][j];
			}
			if (tp <= min) {
				min = tp;
			}
		}
		minDistances1.add(min);
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
			//minDistances1.stream().mapToDouble(i -> i).toArray();
	
	mdoubleArrayA = geometryFactory.createMDouble(tempList, t_value);
	return c[t1.numOf()-1][t2.numOf()-1];
}
public double similarity(double result) {
	double sim = result / (Math.min(t1.numOf(), t2.numOf()));
	return sim;
}

public static double dist(Polygon g1, Polygon g2, double theta2){	
	double number=0;
	try{
		Geometry pl3 = g1.intersection(g2);
		Geometry pl4 = g2.intersection(g1);
		Geometry pl5 = g2.union(g1);
		double ttt = Math.max(pl3.getArea()/pl5.getArea(), pl4.getArea()/pl5.getArea());
			if(ttt>=theta2){
			number = ttt;
			}
		}
		catch( Exception e){}
	//System.out.println(number);
	return (number);
	}	

public static double d(double x, double y, double x2, double y2) {           
	return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
}

public MDouble Result() {
	return mdoubleArrayA;
}
public double measure(MGeometry g1, MGeometry g2) {
	return this.measure(g1, g2, DEFAULT_EPSILON, DEFAULT_THETA, DEFAULT_OMEGA, DEFAULT_TIME_LATTICE_UNIT);
}
}