package com.awarematics.postmedia.operation;

import java.text.ParseException;
import java.util.Arrays;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;

import com.awarematics.postmedia.algorithms.distance.MovingDistance;
import com.awarematics.postmedia.algorithms.similarity.MHausdorff;
import com.awarematics.postmedia.algorithms.similarity.MLCSS;
import com.awarematics.postmedia.algorithms.similarity.MLCVS;
import com.awarematics.postmedia.algorithms.similarity.MTrajHaus;
import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.FoV;
import com.awarematics.postmedia.types.mediamodel.Frame;
import com.awarematics.postmedia.types.mediamodel.MBool;
import com.awarematics.postmedia.types.mediamodel.MDouble;
import com.awarematics.postmedia.types.mediamodel.MGeometry;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import com.awarematics.postmedia.types.mediamodel.MVideo;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.postgresql.pljava.annotation.Function;

import static org.postgresql.pljava.annotation.Function.Effects.IMMUTABLE;
import static org.postgresql.pljava.annotation.Function.OnNullInput.RETURNS_NULL;


//--m_astext(mgeometry)
public class SpatialTemporalOP {
	@Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static String m_astext(String[] points, long[] timetext, String uri, double[] hangle, double[] vangle, double[] dir2d, double[] dir3d, double[] dis)
			throws ParseException, org.locationtech.jts.io.ParseException {
		
		MGeometryFactory geometryFactory = new MGeometryFactory();
		Frame[] frame = new Frame[points.length];
		Coordinate[] coos = new Coordinate[points.length];
		for(int i = 0; i < points.length; i++)
		{
			coos[i] = new Coordinate();
			String pointtext = points[i].substring(1, points[i].length()-1);
			coos[i].x = Double.parseDouble(pointtext.split(",")[0]);
			coos[i].y = Double.parseDouble(pointtext.split(",")[1]);
			FoV fovs = new FoV();
			fovs.setDirection2d(dir2d[i]);
			fovs.setDirection3d(dir3d[i]);
			fovs.setDistance(dis[i]);
			fovs.setHorizontalAngle(hangle[i]);
			fovs.setVerticalAngle(vangle[i]);
			frame[i] = new Frame();
			frame[i].setFov(fovs);
		}	
		MPoint mps = geometryFactory.createMPoint(coos, timetext);
		MVideo mvs = geometryFactory.createMVideo(uri, mps, null, null, frame[0].getFov(), frame);		
		return mvs.toGeoString();
	}
	 
	@Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static String m_astext(String[] points, long[] timetext)
			throws ParseException, org.locationtech.jts.io.ParseException {
		MGeometryFactory geometryFactory = new MGeometryFactory();
		Coordinate[] coos = new Coordinate[points.length];
		for(int i = 0; i < points.length; i++)
		{
			coos[i] = new Coordinate();
			String pointtext = points[i].substring(1, points[i].length()-1);
			coos[i].x = Double.parseDouble(pointtext.split(",")[0]);
			coos[i].y = Double.parseDouble(pointtext.split(",")[1]);	
		}	
		MPoint mps = geometryFactory.createMPoint(coos, timetext);
		return mps.toGeoString();
	}
	
	@Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static boolean m_tintersects(long[] timetext, String period)
			throws ParseException{
		String periodtext = period.substring(1, period.length()-1);
		double fromtime = Double.parseDouble(periodtext.split(",")[0]);
		double totime = Double.parseDouble(periodtext.split(",")[1]);
		
		if(timetext[0] > totime || timetext[timetext.length-1] < fromtime )
			return false;
		
		return true;
	}
	
	@Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
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
	
	@Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static String m_sintersects(String geotext, String trajtext)
			throws ParseException{
		GeometryFactory geometryFactory = new GeometryFactory();
		WKTReader reader = new WKTReader(geometryFactory);	  		
		Geometry geom = (Geometry)reader.read(geotext);
		Geometry traj = (Geometry)reader.read(trajtext);
		return geom.intersects(traj);
	}
	
	@Function(onNullInput=RETURNS_NULL, effects=IMMUTABLE)
	public static String m_mindistance(String geotext, String trajtext, double distance)
			throws ParseException{
		GeometryFactory geometryFactory = new GeometryFactory();
		WKTReader reader = new WKTReader(geometryFactory);	  		
		Geometry geom = (Geometry)reader.read(geotext);
		Geometry traj = (Geometry)reader.read(trajtext);
		return geom.intersects(traj);
	}
}






















