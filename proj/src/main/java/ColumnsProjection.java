import org.postgresql.pljava.annotation.Function;

import com.awarematics.postmedia.io.MWKTReader;
import com.awarematics.postmedia.mgeom.MGeometryFactory;

import com.awarematics.postmedia.types.mediamodel.Frame;
import org.locationtech.jts.geom.Coordinate;
import com.awarematics.postmedia.types.mediamodel.MVideo;
import com.awarematics.postmedia.types.mediamodel.MPoint;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ColumnsProjection {

	@Function
	public static String MPointPoint(String toWhom) throws ParseException, org.locationtech.jts.io.ParseException {
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MWKTReader reader = new MWKTReader(geometryFactory);
		
		String result = "";		
		MPoint mv = (MPoint) reader.read(toWhom);
		Coordinate[] coos = mv.getCoords();
		for(int i = 0; i < coos.length; i++)
		{
			if (i == 0)
				result = result + "(" + coos[i].x + ", " + coos[i].y + ", " + coos[i].z + ")";
			else
				result = result + ";" + "(" + coos[i].x + ", " + coos[i].y + ", " + coos[i].z + ")";
		}
		
		return result;
	}

	@Function
	public static String MPointTime(String toWhom) {
		toWhom = toWhom.replace("MPOINT (", "");
		String result = "";
		String[] split1 = toWhom.split(",");
		for (int i = 0; i < split1.length; i++) {
			if (i == 0)
				result = result + "'" + LongToString(Long.parseLong(split1[i].split("\\) ")[1].replaceAll("\\)", "")))
						+ "'";
			else
				result = result + ", '" + LongToString(Long.parseLong(split1[i].split("\\) ")[1].replaceAll("\\)", "")))
						+ "'";
		}
		return result;
	}

	@Function
	public static String MDoubleDouble(String toWhom) {

		toWhom = toWhom.replace("MDOUBLE (", "");
		String result = "";
		String[] split1 = toWhom.split(", ");
		for (int i = 0; i < split1.length; i++) {
			String temp = split1[i].split(" ")[0];
			if (i == 0)
				result = result + temp;
			else
				result = result + "; " + temp;
		}
		return result;
	}

	@Function
	public static String MDoubleTime(String toWhom) {
		toWhom = toWhom.replace("MDOUBLE (", "");
		String result = "";
		String[] split1 = toWhom.split(", ");
		for (int i = 0; i < split1.length; i++) {
			if (i == 0)
				result = result + "'{" + LongToString(Long.parseLong(split1[i].split(" ")[1].replaceAll("\\)", "")))
						+ "}'";
			else
				result = result + ", '{" + LongToString(Long.parseLong(split1[i].split(" ")[1].replaceAll("\\)", "")))
						+ "}'";
		}
		return result;
	}
//MVIDEO (uri, MPOINT ((0.0 0.0) 1481480632123, (2.0 5.0) 1481480635123)
	@Function
	public static String MVideoPoint(String toWhom) throws ParseException, org.locationtech.jts.io.ParseException {
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MWKTReader reader = new MWKTReader(geometryFactory);
		
		String result = "";		
		MVideo mv = (MVideo) reader.read(toWhom);
		Coordinate[] coos = mv.getCoords();
		for(int i = 0; i < coos.length; i++)
		{
			if (i == 0)
				result = result + "(" + coos[i].x + ", " + coos[i].y + ", " + coos[i].z + ")";
			else
				result = result + ";" + "(" + coos[i].x + ", " + coos[i].y + ", " + coos[i].z + ")";
		}
		
		return result;
	}

	@Function
	public static String MVideoTime(String toWhom) throws ParseException, org.locationtech.jts.io.ParseException {
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MWKTReader reader = new MWKTReader(geometryFactory);
		
		String result = "";		
		MVideo mv = (MVideo) reader.read(toWhom);
		long[] times = mv.getCreationTime();
		for (int i = 0; i < times.length; i++) {
			if (i == 0)
				result = result + "'{" + LongToString(times[i]) + "}'";
			else
				result = result + "," + "'{" + LongToString(times[i]) + "}'";
		}
		return result;
	}
	@Function
	public static String MVideoUri(String toWhom) throws ParseException, org.locationtech.jts.io.ParseException {
		MGeometryFactory geometryFactory = new MGeometryFactory();
		MWKTReader reader = new MWKTReader(geometryFactory);
		
		MVideo mv = (MVideo) reader.read(toWhom);
		String uris = mv.getUri();
		return uris;
	}

	@Function
		public static String MVideoDistance(String toWhom) throws ParseException, org.locationtech.jts.io.ParseException {
			MGeometryFactory geometryFactory = new MGeometryFactory();
			MWKTReader reader = new MWKTReader(geometryFactory);
			
			String result = "";		
			MVideo mv = (MVideo) reader.read(toWhom);
			Frame[] frames = mv.getFrame();
			for (int i = 0; i < frames.length; i++) {
				double temp = frames[i].getFov().getDistance();
				if (i == 0)
					result = result + temp;
				else
					result = result + ";" + temp;
			}
			return result;
		}

		@Function
		public static String MVideoDirection(String toWhom) throws ParseException, org.locationtech.jts.io.ParseException {
			MGeometryFactory geometryFactory = new MGeometryFactory();
			MWKTReader reader = new MWKTReader(geometryFactory);
			
			String result = "";		
			MVideo mv = (MVideo) reader.read(toWhom);
			Frame[] frames = mv.getFrame();
			for (int i = 0; i < frames.length; i++) {
				double temp = frames[i].getFov().getDirection2d();
				if (i == 0)
					result = result + temp;
				else
					result = result + ";" + temp;
			}
			return result;
		}

		@Function
		public static String MVideoAngle(String toWhom) throws ParseException, org.locationtech.jts.io.ParseException {
			MGeometryFactory geometryFactory = new MGeometryFactory();
			MWKTReader reader = new MWKTReader(geometryFactory);
			
			String result = "";		
			MVideo mv = (MVideo) reader.read(toWhom);
			Frame[] frames = mv.getFrame();
			for (int i = 0; i < frames.length; i++) {
				double temp = frames[i].getFov().getHorizontalAngle();
				if (i == 0)
					result = result + temp;
				else
					result = result + ";" + temp;
			}
			return result;
		}

	@Function
	public static String MBoolBool(String toWhom) {
		toWhom = toWhom.replace("MBOOL ", "");
		toWhom = toWhom.replaceAll("\\(|\\)", "");
		String result = "";		
		String[] split1 = toWhom.split(", ");
		for (int i = 0; i < split1.length; i++) {
			String temp = split1[i].split(" ")[0];
			if (i == 0)
				result = result  + temp ;
			else
				result = result + ";"  + temp ;
		}
		return result;
	}

	@Function
	public static String MBoolTime(String toWhom) {
		toWhom = toWhom.replace("MBOOL (", "");
		String result = "";
		String[] split1 = toWhom.split(", ");
		for (int i = 0; i < split1.length; i++) {
			if (i == 0)
				result = result + "'{" + LongToString(Long.parseLong(split1[i].split(" ")[1].replaceAll("\\)", "")))
						+ "}'";
			else
				result = result + ", '{" + LongToString(Long.parseLong(split1[i].split(" ")[1].replaceAll("\\)", "")))
						+ "}'";
		}
		return result;
	}

	@Function
	public static String MInstantTime(String toWhom) {
		toWhom = toWhom.replace("MINSTANT (", "");
		String result = "";
		String[] split1 = toWhom.split(", ");
		for (int i = 0; i < split1.length; i++) {
			if (i == 0)
				result = result + "'{" + LongToString(Long.parseLong(split1[i].replaceAll("\\)", "")))
						+ "}'";
			else
				result = result + ", '{" + LongToString(Long.parseLong(split1[i].replaceAll("\\)", "")))
						+ "}'";
		}
		return result;
	}
@Function
	public static String MStringTime(String toWhom) {
		toWhom = toWhom.replace("MSTRING (", "");
		String result = "";
		String[] split1 = toWhom.split(", ");
		for (int i = 0; i < split1.length; i++) {
			if (i == 0)
				result = result + "'{" + LongToString(Long.parseLong(split1[i].split(" ")[1].replaceAll("\\)", "")))
						+ "}'";
			else
				result = result + ", '{" + LongToString(Long.parseLong(split1[i].split(" ")[1].replaceAll("\\)", "")))
						+ "}'";
		}
		return result;
	}
@Function
	public static String MStringString(String toWhom) {

		toWhom = toWhom.replace("MSTRING (", "");
		String result = "";
		String[] split1 = toWhom.split(", ");
		for (int i = 0; i < split1.length; i++) {
			String temp = split1[i].split(" ")[0];
			if (i == 0)
				result = result + temp;
			else
				result = result + "; " + temp;
		}
		return result;
	}

	@Function
	public static String LongToString(long duration) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String timeText = format.format(duration); // long to string
		return timeText;
	}
	
}
