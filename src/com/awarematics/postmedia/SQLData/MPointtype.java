package com.awarematics.postmedia.SQLData;

import java.sql.Array;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;


public class MPointtype implements SQLData {
	   private 	Array coords;
	   private	Array times;
	   private String m_typeName;

	   public String getSQLTypeName() {
	      return m_typeName;
	   }

	   public void readSQL(SQLInput stream, String typeName) throws SQLException {
		   coords = stream.readArray();
		   times = stream.readArray();
		   m_typeName = typeName;
	   }

	   public void writeSQL(SQLOutput stream) throws SQLException {
	      stream.writeArray(coords);
	      stream.writeArray(times);
	   }
}
