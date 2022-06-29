package com.awarematics.postmedia.operation;

import java.sql.Timestamp;
import org.postgresql.pljava.annotation.Function;

public class IO {

	@Function
		public static String M_Astext(String mgstring)  {
		String text = "";
		mgstring = mgstring.replaceAll("\\\\", "");
		mgstring = mgstring.replaceAll("\'", "");
		if (mgstring.contains("mvideo")) {
			mgstring = mgstring.replace("mvideo ", "");
			String[] videos = mgstring.split("\\};\\{");
			String uris = videos[0].replace("{", "");
			String[] points = videos[1].replace("{", "").split("\\\",\\\"");
			String[] angles = videos[2].replace("{", "").split(",");
			String[] distances = videos[3].replace("{", "").split(",");
			String[] directions = videos[4].replace("{", "").split(",");
			String[] times = videos[5].replace("}", "").split("\\\",\\\"");
			String prefix_mpoint = "MPOINT (";
			for (int i = 0; i < points.length; i++) {
				String pointx = points[i].split(",")[0].replace("(", "").replace("\\{", "");
				String pointy = points[i].split(",")[1].replace(")", "");
				String pointz = points[i].split(",")[2].replace(")", "");
				if (i == 0)
					text = text + "(" + pointx.replaceAll("\\\"", "") + " " + pointy.replaceAll("\\\"", "") + " " + pointz.replaceAll("\\\"", "") + ") "
							+ LongToString(times[i]);
				else
					text = text + ", (" + pointx.replaceAll("\\\"", "") + " " + pointy.replaceAll("\\\"", "") + " " + pointz.replaceAll("\\\"", "")+ ") "
							+ LongToString(times[i]);

			}
			String mpointstring = prefix_mpoint + text + ")";
			text = "";
			String prefix = "MVIDEO (";
			String prefix_frame = "FRAME (";
			for (int i = 0; i < points.length; i++) {
				if (i == 0) {
					text = text + "("  + angles[i] + " NAN " + directions[i] + " NAN " + distances[i]  + ")";
				} else
					text = text + ", (" + angles[i] + " NAN " + directions[i] + " NAN " + distances[i]  + ")";

			}
			String framestring = prefix_frame + text + ")";
			// return text;
			text = prefix + uris + ", " + mpointstring + ", " + framestring +")";

		} else if (mgstring.contains("mpoint")) {
			mgstring = mgstring.replace("mpoint ", "");
			String[] points = mgstring.split("\\};\\{");
			String[] point = points[0].replace("{", "").split("\\\",\\\"");
			String[] times = points[1].replace("}", "").split("\\\",\\\"");
			String prefix = "MPOINT (";
			for (int i = 0; i < point.length; i++) {
				String pointx = point[i].split(",")[0].replace("(", "").replace("\\{", "");
				String pointy = point[i].split(",")[1].replace(")", "");
				String pointz = point[i].split(",")[2].replace(")", "");
				if (i == 0)
					text = text + "(" + pointx.replaceAll("\\\"", "") + " " + pointy.replaceAll("\\\"", "") + " " + pointz.replaceAll("\\\"", "") + ") "
							+ LongToString(times[i]);
				else
					text = text + ", (" + pointx.replaceAll("\\\"", "") + " " + pointy.replaceAll("\\\"", "") + " " + pointz.replaceAll("\\\"", "")+ ") "
							+ LongToString(times[i]);

			}
			text = prefix + text + ")";
		} else if (mgstring.contains("mbool")) {
			mgstring = mgstring.replace("mbool ", "");
			String[] points = mgstring.split("\\};\\{");
			String[] bool = points[0].replace("{", "").split(",");
			String[] times = points[1].replace("}", "").split("\\\",\\\"");
			String prefix = "MBOOL (";
			for (int i = 0; i < bool.length; i++) {
				if (i == 0)
					text = text + bool[i] + LongToString(times[i]);
				else
					text = text + ", " + bool[i] + LongToString(times[i]);

			}
			text = prefix + text + ")";
		} 
		return text;
}
		public static long LongToString(String time) {
			
			time = time.replaceAll("\\\"", "");
			String[] value = time.split("\\.");
			Timestamp ts=new Timestamp(0);
			ts=Timestamp.valueOf(value[0]);
			long result =  ts.getTime(); //+ Long.parseLong(value[1]);
			return result;
		}
}

