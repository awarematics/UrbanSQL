package com.awarematics.postmedia.SQLData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import org.postgresql.pljava.annotation.Function;



import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;

import com.awarematics.postmedia.mgeom.MGeometryFactory;
import com.awarematics.postmedia.types.mediamodel.MPoint;

public class Insert_BDD_Cars {
	static double[] coordinate1_x;
	static double[] coordinate1_y;
	static long[] timeArray;

	public static void main(String args[]) throws IOException,
			NumberFormatException, ParseException {
		ArrayList<String> result =  BddDataToPostgre("D://val/", "mpoint");
		
		System.out.println(result.size());
		
			Connection c = null;
		      Statement stmt = null;
		      try {
		         Class.forName("org.postgresql.Driver");
		         c = DriverManager
		            .getConnection("jdbc:postgresql://202.31.147.183:5432/PostGeoMedia_BerlinMOD_BDD",
		            "postgres", "mcalab3408");
		        // System.out.println("connect 成功！");
		         stmt = c.createStatement();
		         String sql="";
		         for (int i = 0; i < result.size(); i++) {
		         sql = sql + result.get(i);
		         }
		         //System.out.println( sql);
		         stmt.executeUpdate(sql);
		         stmt.close();
		         c.close();
		         
		      } catch (Exception e) {
		         e.printStackTrace();
		         System.err.println(e.getClass().getName()+": "+e.getMessage());
		         System.exit(0);
		    
		    // System.out.println("insert 成功！");
		}
	}

	// @Function
	public static ArrayList<String> BddDataToPostgre(String uris, String type) {
		ArrayList<String> bddString = new ArrayList<String>();
		for (int numof = 1; numof <= 9920; numof++) {
				String sqlstring = "insert into bdd100k_seg values(" + numof + ", 'car"+ numof + "', 'car', 'bdd');\r\n";
				bddString.add(sqlstring);				
		}
		return bddString;
	}
}