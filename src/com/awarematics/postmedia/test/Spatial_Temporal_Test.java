package com.awarematics.postmedia.test;

import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;

import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.FoV;
import com.awarematics.postmedia.types.mediamodel.Frame;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class Spatial_Temporal_Test {

	public static void main(String[] args) throws IOException, ParseException, java.text.ParseException
	{		
		
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MWKTReader reader = new MWKTReader(geometryFactory);	  		  
		
		MPoint mp = (MPoint)reader.read("MPOINT ((0 0) 1481480632123, (2 200) 1481480638000, (5 0) 1481480639123, (13 13) 1481480641123, (30 33) 1481480642556, (70 70) 1481480643223, (102 63) 1481480644123, (103 33) 1481480645556)");
		MPoint mp2 = (MPoint)reader.read("MPOINT ((0 2) 1481480633123, (2 0) 1481480638000, (5 0) 1481480639123, (13 13) 1481480641123, (30 33) 1481480642556, (63 50) 1481480643523, (102 70) 1481480644123, (103 333) 1481480645556)");
		
		MVideo mv = (MVideo)reader.read("MVIDEO (('localhost:///tmp/drone/test1.jpg', MPOINT ((0.0 0.0) 1481480632123, (2.0 200.0) 1481480638000, (5.0 0.0) 1481480639123, (13.0 13.0) 1481480641123, (30.0 33.0) 1481480642556, (70.0 70.0) 1481480643223, (102.0 63.0) 1481480644123, (103.0 33.0) 1481480645556), "
				+ "FRAME ((0 0 0 0 0), (0 0 0 0 0), (0 0 0 0 0), (0 0 0 0 0), (0 0 0 0 0), (0 0 0 0 0), (0 0 0 0 0), (0 0 0 0 0))");
		
		String[] points = {"(40.7424697420008,-73.9917157777343)","(40.7424498768903,-73.9916716051046)","(40.7424249826379,-73.9916175418291)","(40.742397531905,-73.9915364888254)","(40.7423820253841,-73.9914577827547)","(40.7423419598869,-73.9913754724655)","(40.7423110306642,-73.9912962634805)","(40.7422762038566,-73.9912199881617)"};
		Coordinate[] coos = new Coordinate[points.length];
		for(int i = 0; i < points.length; i++)
		{
			coos[i] = new Coordinate();
			String pointtext = points[i].substring(1, points[i].length()-1);
			coos[i].x = Double.parseDouble(pointtext.split(",")[0]);
			coos[i].y = Double.parseDouble(pointtext.split(",")[1]);
		
		}	
		MPoint mps = geometryFactory.createMPoint(coos, mp.getTimes());
		
		double[] dir2d = {0, 0, 0, 0, 0, 0, 0, 0};
		double[] dir3d = {0, 0, 0, 0, 0, 0, 0, 0};
		double[] dis = {0, 0, 0, 0, 0, 0, 0, 0};
		double[] hangle = {0, 0, 0, 0, 0, 0, 0, 0};
		double[] vangle = {0, 0, 0, 0, 0, 0, 0, 0};

		Frame[] frame = new Frame[dis.length];
		for(int i = 0; i < points.length; i++)
		{
			FoV fovs = new FoV();
			fovs.setDirection2d(dir2d[i]);
			fovs.setDirection3d(dir3d[i]);
			fovs.setDistance(dis[i]);
			fovs.setHorizontalAngle(hangle[i]);
			fovs.setVerticalAngle(vangle[i]);
			frame[i] = new Frame();
			frame[i].setFov(fovs);
			System.out.println(frame[i].getFov().toGeoString());
		}
		
		MVideo mvs = geometryFactory.createMVideo("urti", mps, null, null, frame[0].getFov(), frame);
		System.out.println(m_spatial(points));
		}
	public static boolean m_tintersects(long[] timetext, String period)
			throws ParseException{
		String periodtext = period.substring(1, period.length()-1);
		double fromtime = Double.parseDouble(periodtext.split(",")[0]);
		double totime = Double.parseDouble(periodtext.split(",")[1]);
		
		if(timetext[0] > totime || timetext[timetext.length-1] < fromtime )
			return false;
		
		return true;
	}
	
	public static String m_spatial(String[] geotext)
			throws ParseException{
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate[] coos = new Coordinate[geotext.length];
		for(int i = 0; i < geotext.length; i++)
		{
			coos[i] = new Coordinate();
			String pointtext = geotext[i].substring(1, geotext[i].length()-1);
			coos[i].x = Double.parseDouble(pointtext.split(",")[0]);
			coos[i].y = Double.parseDouble(pointtext.split(",")[1]);	
		}	
		LineString mps = geometryFactory.createLineString(coos);
		return mps.toText();
	}
	
}