package com.awarematics.postmedia.algorithms.distance;

import org.locationtech.jts.geom.Geometry;

import com.awarematics.postmedia.types.mediamodel.MGeometry;
public interface TimeGenerator {
	
		long[] genTimes( MGeometry mg1, MGeometry mg2 );
		long[] genTimes( MGeometry mg, Geometry geo );
		long[] genTimes( Geometry geo, MGeometry mg );   

}

