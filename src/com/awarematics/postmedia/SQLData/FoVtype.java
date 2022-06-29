package com.awarematics.postmedia.SQLData;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;


public class FoVtype implements SQLData {
	   private 	double horizontalAngle;
	   private	double verticalAngle;
	   private	double distance;
	   private	double direction2d;
	   private	double direction3d;
	   private String m_typeName;

	   public String getSQLTypeName() {
	      return m_typeName;
	   }

	   public void readSQL(SQLInput stream, String typeName) throws SQLException {
		   horizontalAngle = stream.readDouble();
		   verticalAngle = stream.readDouble();
		   distance = stream.readDouble();
		   direction2d = stream.readDouble();
		   direction3d = stream.readDouble();
		   m_typeName = typeName;
	   }

	   public void writeSQL(SQLOutput stream) throws SQLException {
	      stream.writeDouble(horizontalAngle);
	      stream.writeDouble(verticalAngle);
	      stream.writeDouble(distance);
	      stream.writeDouble(direction2d);
	      stream.writeDouble(direction3d);
	   }
}
