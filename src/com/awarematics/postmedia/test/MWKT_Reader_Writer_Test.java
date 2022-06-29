package com.awarematics.postmedia.test;

import java.io.IOException;
import java.text.ParseException;
import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.io.MWKTWriter;
import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import com.awarematics.postmedia.types.mediamodel.MVideo;


public class MWKT_Reader_Writer_Test {

	public static void main(String[] args)
			throws IOException, ParseException, org.locationtech.jts.io.ParseException{

		MGeometryFactory geometryFactory = new MGeometryFactory();
		MWKTReader reader = new MWKTReader(geometryFactory);	  		  
		
	    MPoint mp = (MPoint)reader.read("MPOINT ((12793 1310.2 NaN) 1180338585000");
	    MVideo mv = (MVideo)reader.read("MVIDEO (uri, MPOINT ((0.0 0.0) 1481480632123, (2.0 5.0) 1481480635123, (34.0 333.0) 1481480638000), FRAME ((1 1 1 1 1), (1 1 1 1 2), (1 1 1 1 3)))");
		
		System.out.println(mv.toGeoString());
		System.out.println(mp.toGeoString()); 
	
		
		MWKTWriter writer = new MWKTWriter();
	
		System.out.println(writer.write(mv));
		System.out.println(writer.write(mp));
	
	}

}