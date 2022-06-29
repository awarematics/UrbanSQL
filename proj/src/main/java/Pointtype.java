package com.awarematics.postmedia.SQLData;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;


public class Pointtype implements SQLData {
	   private double m_x;
	   private double m_y;
	   private String m_typeName;

	   public String getSQLTypeName() {
	      return m_typeName;
	   }

	   public void readSQL(SQLInput stream, String typeName) throws SQLException {
	      m_x = stream.readDouble();
	      m_y = stream.readDouble();
	      m_typeName = typeName;
	   }

	   public void writeSQL(SQLOutput stream) throws SQLException {
	      stream.writeDouble(m_x);
	      stream.writeDouble(m_y);
	   }
}
