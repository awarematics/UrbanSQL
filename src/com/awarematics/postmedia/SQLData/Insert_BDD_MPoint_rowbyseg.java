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

public class Insert_BDD_MPoint_rowbyseg {
	static double[] coordinate1_x;
	static double[] coordinate1_y;
	static long[] timeArray;

	public static void main(String args[]) throws IOException,
			NumberFormatException, ParseException {
		ArrayList<String> result =  BddDataToPostgre("D://val/", "mpoint");
	
		System.out.println(result.size());
		//9920
		
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
			/*Connection c = null;
		      Statement stmt = null;
		      try {
		         Class.forName("org.postgresql.Driver");
		         c = DriverManager
		            .getConnection("jdbc:postgresql://202.31.147.183:5432/PostGeoMedia_BerlinMOD_BDD",
		            "postgres", "mcalab3408");
		        // System.out.println("connect 成功！");
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
		    // System.out.println("insert 成功！");*/
		}
		
	}
/*
 * idea     1. mpoint array    if (points>400) split  new points array[]
 */
	// @Function
	public static ArrayList<String> BddDataToPostgre(String uris, String type) {
		ArrayList<String> bddString = new ArrayList<String>();
		int k=0;

		for (int numof = 1; numof <= 10; numof++) {
			long[] timeArray;
			String timestring = "";
			String pointstring = "{\"";
			try {
				k = k+1;
				File file = new File(uris + "/1 (" + numof + ").json");
				String content = FileUtils.readFileToString(file, "UTF-8");
				JSONObject jsonObject = new JSONObject(content);
				JSONArray getJsonArray = jsonObject.getJSONArray("locations"); // locations
				MPoint mp = null;
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
					
					
					//getTimes
					timeArray[j] = Long.parseLong(time);
					//getPoints					
					coordinate1[j] = new Coordinate();
					coordinate1[j].y = coordinate1_x[j];
					coordinate1[j].x = coordinate1_y[j];				
					//System.out.println(sql);				
				}
				
				MGeometryFactory geometryFactory = new MGeometryFactory();
				mp = geometryFactory.createMPoint(coordinate1, timeArray);
				MPoint mp_ato = (MPoint) mp.atomize(100);
			
	
				ArrayList<MPoint> arrmp = new ArrayList<MPoint>();

				int countmp = mp_ato.numOf()/100;
				if( mp_ato.numOf() %100!=0) countmp = countmp+1;
				if(mp_ato.numOf()> 0)
				{
					
					for (int j = 0; j < countmp; j++) {	
						if(j != countmp-1)
						{
							arrmp.add((MPoint) mp_ato.slice(mp_ato.getTimes()[j*100], mp_ato.getTimes()[(j+1)*100]));
						}
						else
						{
							arrmp.add((MPoint) mp_ato.slice(mp_ato.getTimes()[j*100], mp_ato.getTimes()[mp_ato.numOf()-1]));
						}
						for(int i =0;i<arrmp.get(j).numOf();i++)
						{

							timestring = timestring +","+ arrmp.get(j).getTimes()[i];				
							pointstring = pointstring + "(" + arrmp.get(j).getCoords()[i].y + "," + arrmp.get(j).getCoords()[i].x +")\",\"";
				
						}
					
						pointstring = pointstring.substring(0, pointstring.length()-3);
						pointstring = pointstring+"\"}";
						timestring = "{"+timestring.substring(1, timestring.length()) + "}";
						//System.out.println(timestring);	
						//System.out.println(pointstring);	
						String sqlstring = "insert into mpoint_240134 (mpid, segid, datetimes, geo) values("+ k +"," + (j+1) +",'" +  timestring + "', '"+  pointstring + "')";
						bddString.add(sqlstring);
						timestring = "";
						pointstring = "{\"";
					}

				}
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
}