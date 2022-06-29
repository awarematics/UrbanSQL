package com.awarematics.postmedia.mgeom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;

import com.awarematics.postmedia.types.mediamodel.FoV;
import com.awarematics.postmedia.types.mediamodel.Frame;
import com.awarematics.postmedia.types.mediamodel.MBool;
import com.awarematics.postmedia.types.mediamodel.MDouble;
import com.awarematics.postmedia.types.mediamodel.MInstant;
import com.awarematics.postmedia.types.mediamodel.MInt;
import com.awarematics.postmedia.types.mediamodel.MLineString;
import com.awarematics.postmedia.types.mediamodel.MMultiPoint;
import com.awarematics.postmedia.types.mediamodel.MPhoto;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import com.awarematics.postmedia.types.mediamodel.MPolygon;
import com.awarematics.postmedia.types.mediamodel.MString;
import com.awarematics.postmedia.types.mediamodel.MVideo;

public class MGeometryFactory {
	private PrecisionModel precisionModel;

	private CoordinateSequenceFactory coordinateSequenceFactory;

	public MGeometryFactory(PrecisionModel precisionModel, int SRID,
			CoordinateSequenceFactory coordinateSequenceFactory) {
		this.precisionModel = precisionModel;
		this.coordinateSequenceFactory = coordinateSequenceFactory;
		this.SRID = SRID;
	}

	public MGeometryFactory(CoordinateSequenceFactory coordinateSequenceFactory) {
		this(new PrecisionModel(), 0, coordinateSequenceFactory);
	}

	public MGeometryFactory(PrecisionModel precisionModel) {
		this(precisionModel, 0, getDefaultCoordinateSequenceFactory());
	}

	public MGeometryFactory(PrecisionModel precisionModel, int SRID) {
		this(precisionModel, SRID, getDefaultCoordinateSequenceFactory());
	}

	public MGeometryFactory() {
		this(new PrecisionModel(), 0);
	}

	private static CoordinateSequenceFactory getDefaultCoordinateSequenceFactory() {
		return CoordinateArraySequenceFactory.instance();
	}

	public PrecisionModel getPrecisionModel() {
		return precisionModel;
	}

	public MPoint createMPoint(Coordinate[] coords, long[] times) {

		MPoint mm = new MPoint(coords, times);
		return createMPoint(mm);
	}

	public MPoint createMPoint(MPoint mp ) {
		return mp.clone();
	}


	public MDouble createMDouble(double[] value, long[] times)  {
		MDouble md = new MDouble(value, times);
		return createMDouble(md);
	}

	public MDouble createMDouble(MDouble md) {
		return new MDouble(md);
	}

	public MBool createMBool(boolean[] bools, long[] times)  {
		MBool mb = new MBool(bools, times);
		return createMBool(mb);
	}

	public MBool createMBool(MBool mb)  {
		return new MBool(mb);
	}

	public MInt createMInt(int[] count, long[] times)  {
		MInt mi = new MInt(count, times);
		return createMInt(mi);
	}

	public MInt createMInt(MInt mi)  {
		return new MInt(mi);
	}

	public MString createMString(String[] string, long[] times){
		MString ms = new MString(string, times);
		return createMString(ms);
	}

	public MString createMString(MString ms) {
		return new MString(ms);
	}

	public MPhoto createMPhoto(String[] uri, double[] width, double[] height, double[] viewAngle,double[] verticalAngle,double[] distance,double[] direction,
			double[] direction3d,double[] altitude,String[] annotationJson,	String[] exifJson,Coordinate[] coords,long[] creationTime, Polygon[] listPolygon, FoV[] fov) {
		MPhoto mphoto = new MPhoto(uri, width, height,viewAngle,verticalAngle, distance, direction,direction3d, altitude,annotationJson,exifJson,coords,creationTime, listPolygon, fov);
		return createMPhoto(mphoto);
	}

	public MPhoto createMPhoto(MPhoto mphoto) {
		return new MPhoto(mphoto);
	}

	public MVideo createMVideo(MVideo mv) {
		return new MVideo(mv);
	}

	public MMultiPoint createMMultiPoint(MPoint[] mpoints) {
		MMultiPoint mmu = new MMultiPoint(mpoints);
		return createMMultiPoint(mmu);
	}

	public MMultiPoint createMMultiPoint(MMultiPoint mmu) {
		return new MMultiPoint(mmu);
	}

	public MLineString createMLineString(LineString[] points, long[] value){
		MLineString mline = new MLineString(points,value);
		return createMLineString(mline);
	}

	public MLineString createMLineString(MLineString mline) {
		return new MLineString(mline);
	}

	public int getSRID() {
		return SRID;
	}

	private int SRID;

	public CoordinateSequenceFactory getCoordinateSequenceFactory() {
		return coordinateSequenceFactory;
	}

	public MPolygon createMPolygon(Polygon[] points, long[] value)  {
		MPolygon mpol = new MPolygon(points,value);
		return createMPolygon(mpol);
	}

	private MPolygon createMPolygon(MPolygon mpol) {
		return new MPolygon(mpol);
	}

	public MVideo createMVideo(String uri, MPoint mt, String[] annotationJson, long[] creationTime, FoV fov, Frame[] frame) {
		MVideo mv = new MVideo(uri, mt, annotationJson, creationTime, fov, frame);
		return createMVideo(mv);
	}

	public MInstant createMInstant(long[] times) {
		MInstant minstant = new MInstant(times);
		return createMInstant(minstant);
	}

	private MInstant createMInstant(MInstant minstant) {
		return new MInstant(minstant);
	}

}

