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

public class Insert_BDD_MVideo {
	static double[] coordinate1_x;
	static double[] coordinate1_y;
	static long[] timeArray;

	public static void main(String args[]) throws IOException,
			NumberFormatException, ParseException {
		ArrayList<String> result =  BddDataToPostgre("D://val/", "mpoint");
	
		System.out.println(result.size());
		for (int i = 0; i < result.size(); i++) {
			Connection c = null;
		      Statement stmt = null;
		      try {
		         Class.forName("org.postgresql.Driver");
		         c = DriverManager
		            .getConnection("jdbc:postgresql://202.31.147.183:5432/PostGeoMedia_BerlinMOD_BDD",
		            "postgres", "mcalab3408");
		         //System.out.println("connect 成功！");
		         stmt = c.createStatement();
		         String sql = result.get(i);
		         //System.out.println( sql);
		         stmt.executeUpdate(sql);
		         stmt.close();
		         c.close();
		         
		      } catch (Exception e) {
		         e.printStackTrace();
		         System.err.println(e.getClass().getName()+": "+e.getMessage());
		         System.exit(0);
		      }
		    // System.out.println("insert 成功！");
		}
	}

	// @Function
	public static ArrayList<String> BddDataToPostgre(String uris, String type) {
		ArrayList<String> bddString = new ArrayList<String>();
		int k=0;
		for (int numof = 1; numof <= 10000; numof++) {
			long[] timeArray;
			String timestring = "";
			String pointstring = "{\"";
			String horizontalAngle = "";
			String verticalAngle = "";
			String direction2d = "";
			String direction3d = "";
			String distance = "";
			String uri = "";
			try {
				k = k+1;
				File file = new File(uris + "/1 (" + numof + ").json");
				String content = FileUtils.readFileToString(file, "UTF-8");
				JSONObject jsonObject = new JSONObject(content);
				JSONArray getJsonArray = jsonObject.getJSONArray("locations"); // locations
				
				int num = getJsonArray.length();
				coordinate1_x = new double[num];
				coordinate1_y = new double[num];
				Coordinate[] coordinate1 = new Coordinate[num];
				timeArray = new long[num];			
				for (int j = 0; j < num; j++) {				
					String[] array = getJsonArray.get(j).toString().split(":");
								
					String result_x = array[1].split(",")[0];
					String result_y = array[6].split(",")[0].replace("}", "");
					String time = array[5].split(",")[0];
					coordinate1_x[j] = Double.valueOf(result_x);
					coordinate1_y[j] = Double.valueOf(result_y);
					
					timestring = timestring +","+ time;
					//getTimes
					timeArray[j] = Long.parseLong(time);
					//getPoints					
					coordinate1[j] = new Coordinate();
					coordinate1[j].y = coordinate1_x[j];
					coordinate1[j].x = coordinate1_y[j];
					
					pointstring = pointstring + "(" + coordinate1_x[j] + "," + coordinate1_y[j] +")\",\"";
					
					horizontalAngle = horizontalAngle + "," + 120;
					verticalAngle = verticalAngle + "," + 120;				
					direction3d = direction3d + "," + 0;
					distance = distance + "," + 100;
					uri = uri + "," + "http://u-gis.net/mvideo/" + k + ".mp4";
				}
				double[] dir2d = printDirection(coordinate1_x, coordinate1_y);
				for (int j = 0; j < num; j++) {		
					direction2d = direction2d + "," + dir2d[j];
				}
				
				
				MGeometryFactory geometryFactory = new MGeometryFactory();				
				MPoint mp = geometryFactory.createMPoint(coordinate1,timeArray);
				pointstring = pointstring.substring(0, pointstring.length()-3);
				pointstring = pointstring+"\"}";
				
				horizontalAngle = "{"+horizontalAngle.substring(1, horizontalAngle.length()) + "}";
				verticalAngle = "{"+verticalAngle.substring(1, verticalAngle.length()) + "}";
				direction3d = "{"+direction3d.substring(1, direction3d.length()) + "}";
				distance = "{"+distance.substring(1, distance.length()) + "}";
				uri = "{"+uri.substring(1, uri.length()) + "}";
				direction2d = "{"+direction2d.substring(1, direction2d.length()) + "}";
				timestring = "{"+timestring.substring(1, timestring.length()) + "}";
				
				//getTimeRange
				String int8range = mp.time().toGeoString();
				int8range = int8range.replaceAll("\\(", "[");
				int8range = int8range.replaceAll("\\)", "]");
				int8range = int8range.replaceAll(" ", ",");
				
				//getMBR
				String mbr = mp.bbox().toText();
				String sqlstring = "insert into mvideo_186327 (mpid, segid, mbr, timerange, datetimes, geo, horizontalAngle, verticalAngle, direction2d, direction3d, distance, uri) "
						+ 		"values("+ k +",1,'" + mbr + "'::geometry, '" + int8range + "'::int8range, '" + timestring + "', '"+  pointstring + "',"
								+ "'"+horizontalAngle +"'," + "'"+verticalAngle +"'," + "'"+direction2d +"'," + "'"+direction3d +"',"
								+ "'"+distance +"'," + "'"+uri +"'" 
								+")";
				bddString.add(sqlstring);
				//System.out.println(sqlstring);
				if(k%1000==0)
				{
					System.out.println(k);
				}
			} catch (Exception e) {
				continue;
			}
		}
		return bddString;
	}
	public static double[] printDirection(double[] x, double[] y) {
		double[] k = new double[x.length];
		double start_x = x[0];
		double start_y = y[0];
		
		double[] result = new double[x.length];
		/*
		 * k[0]  start point maybe is a stop point for several seconds
		 */
		int num =0;
		for(int i=1;i<x.length;i++){

			if((x[i]!=start_x||y[i]!=start_y)&& num==0)
			{
				k[0] =  Math.asin((y[i] -start_y)/Math.sqrt(((x[i] -start_x) * (x[i] -start_x)) + (y[i] - start_y) * (y[i] - start_y))); num=1;
				result[0] = Double.valueOf((k[0]* 180 / Math.PI));
			}
		}
		for (int i = 1; i < x.length; i++) {
			if( Math.sqrt(((x[i] -x[i-1]) * (x[i] - x[i-1])) + (y[i] - y[i-1]) * (y[i] - y[i-1]))!=0){
				k[i] = Math.asin((y[i] - y[i-1])/ Math.sqrt(((x[i] -x[i-1]) * (x[i] - x[i-1])) + (y[i] - y[i-1]) * (y[i] - y[i-1])));
			}
			else
			{
				k[i]=k[i-1];
			}
			//DecimalFormat df = new DecimalFormat("0.000000000");df.format
			result[i] = Double.valueOf((k[i]* 180 / Math.PI));
		}
		return result;
	}
}