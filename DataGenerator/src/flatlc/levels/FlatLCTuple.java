package flatlc.levels;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Extended version of the superclass that can be used by standard
 * objects.
 * 
 * @author Timotheus Preisinger
 * @author endresma
 * 
 */
public class FlatLCTuple extends FlatLevelCombination implements Serializable {
	//ResultSetMetaData meta;

	public FlatLCTuple(FlatLevelCombination flc) {
		super(flc);
	}

	private int getColumnIndex(String column) {
		if (column == null) return -1;
		
		if (column.startsWith("myCol")) {
			return Integer.parseInt(column.substring(5));
		} else if (column.startsWith("id")) {
			//try {
				return 0; //return meta.getColumnCount();
			//} catch (SQLException e) {
			//}
		}
		return -1;
	}


	public boolean getBoolean(int column) {
		if (value[column] instanceof Boolean)
			return ((Boolean) value[column]).booleanValue();
		return false;
	}

	public boolean getBoolean(String column) {
		return getBoolean(getColumnIndex(column));
	}


	public byte getByte(int column) {
		if (column >= value.length) return new Integer(getSerial()).byteValue();
		if (value[column] instanceof Number)
			return ((Number) value[column]).byteValue();
		return 0;
	}


	public byte getByte(String column) {
		return getByte(getColumnIndex(column));
	}


	public Date getDate(int column) {
		if (value[column] instanceof Date)
			return (Date) value[column];
		return null;
	}


	public Date getDate(String column) {
		return getDate(getColumnIndex(column));
	}


	public double getDouble(int column) {
		if (column >= value.length) return new Integer(getSerial()).doubleValue();
		if (value[column] instanceof Number)
			return ((Number) value[column]).doubleValue();
		return 0;
	}


	public double getDouble(String column) {
		return getDouble(getColumnIndex(column));
	}


	public float getFloat(int column) {
		if (column >= value.length) return new Integer(getSerial()).floatValue();
		if (value[column] instanceof Number)
			return ((Number) value[column]).floatValue();
		return 0;
	}


	public float getFloat(String column) {
		return getFloat(getColumnIndex(column));
	}


	public int getInt(int column) {
		if (column >= value.length) return getSerial();
		if (value[column] instanceof Number)
			return ((Number) value[column]).intValue();
		return 0;
	}


	public int getInt(String column) {
		return getInt(getColumnIndex(column));
	}


	public long getLong(int column) {
		if (column >= value.length) return getSerial();
		if (value[column] instanceof Number)
			return ((Number) value[column]).longValue();
		return 0;
	}


	public long getLong(String column) {
		return getLong(getColumnIndex(column));
	}


	public ResultSetMetaData getMetaData() {
		return null;
	}


	public Object getObject(int column) {
		if (column >= value.length) return new Integer(getSerial()).byteValue();
		return value[column];
	}


	public Object getObject(String column) {
		return value[getColumnIndex(column)];
	}


	public short getShort(int column) {
		if (column >= value.length) return new Integer(getSerial()).shortValue();
		if (value[column] instanceof Number)
			return ((Number) value[column]).shortValue();
		return 0;
	}


	public short getShort(String column) {
		return getShort(getColumnIndex(column));
	}


	public String getString(int column) {
		if (column >= value.length) return Integer.toString(getSerial());
		if (value[column] == null)
			return null;
		return value[column].toString();
	}


	public String getString(String column) {
		int col = getColumnIndex(column);
		if (value[col] == null)
			return null;
		return value[col].toString();
	}


	public Time getTime(int column) {
		if (value[column] instanceof Time)
			return (Time) value[column];
		return null;
	}


	public Time getTime(String column) {
		return getTime(getColumnIndex(column));
	}


	public Timestamp getTimestamp(int column) {
		if (value[column] instanceof Time)
			return (Timestamp) value[column];
		return null;
	}


	public Timestamp getTimestamp(String column) {
		return getTimestamp(getColumnIndex(column));
	}


	public boolean isNull(int column) {
		if (column >= value.length) return false;
		return value[column] == null;
	}


	public boolean isNull(String column) {
		return value[getColumnIndex(column)] == null;
	}

}
