package com.corsair.dbpool.util;

import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据集描述对象
 * 
 * @author Administrator
 *
 */
public class CResultSetMetaDataItem {
	/**
	 * 
	 */
	private boolean AutoIncrement = false;
	private String ColumnClassName = "";
	private int ColumnDisplaySize;
	private String ColumnLabel;
	private String ColumnName;
	private int ColumnType;
	private String ColumnTypeName;
	private int Precision;
	private int Scale;
	private String SchemaName;
	private String TableName;
	private boolean CaseSensitive;
	private boolean Currency;
	private boolean DefinitelyWritable;
	private int Nullable;
	private boolean ReadOnly;
	private boolean Searchable;
	private boolean Signed;
	private boolean Writable;

	public CResultSetMetaDataItem(ResultSetMetaData rsmd, int col) {
		try {
			AutoIncrement = rsmd.isAutoIncrement(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ColumnClassName = rsmd.getColumnClassName(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ColumnDisplaySize = rsmd.getColumnDisplaySize(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ColumnLabel = rsmd.getColumnLabel(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ColumnName = rsmd.getColumnName(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ColumnType = rsmd.getColumnType(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ColumnTypeName = rsmd.getColumnTypeName(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Precision = rsmd.getPrecision(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Scale = rsmd.getScale(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			SchemaName = rsmd.getSchemaName(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			TableName = rsmd.getTableName(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			CaseSensitive = rsmd.isCaseSensitive(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Currency = rsmd.isCurrency(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			DefinitelyWritable = rsmd.isDefinitelyWritable(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Nullable = rsmd.isNullable(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ReadOnly = rsmd.isReadOnly(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Searchable = rsmd.isSearchable(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Signed = rsmd.isSigned(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Writable = rsmd.isWritable(col);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getColumnClassName() {
		return ColumnClassName;
	}

	public int getColumnDisplaySize() {
		return ColumnDisplaySize;
	}

	public String getColumnLabel() {
		return ColumnLabel;
	}

	public String getColumnName() {
		return ColumnName;
	}

	public int getColumnType() {
		return ColumnType;
	}

	public String getColumnTypeName() {
		return ColumnTypeName;
	}

	public int getPrecision() {
		return Precision;
	}

	public int getScale() {
		return Scale;
	}

	public String getSchemaName() {
		return SchemaName;
	}

	public String getTableName() {
		return TableName;
	}

	public boolean isCaseSensitive() {
		return CaseSensitive;
	}

	public boolean isCurrency() {
		return Currency;
	}

	public boolean isDefinitelyWritable() {
		return DefinitelyWritable;
	}

	public boolean isReadOnly() {
		return ReadOnly;
	}

	public boolean isSearchable() {
		return Searchable;
	}

	public boolean isSigned() {
		return Signed;
	}

	public boolean isWritable() {
		return Writable;
	}

	public boolean isAutoIncrement() {
		return AutoIncrement;
	}

	public int getNullable() {
		return Nullable;
	}

	public String toString() {
		try {
			List<String> ClassStrs = new ArrayList<String>();
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true); // 设置些属性是可以访问的
				Object object = field.get(this);
				if (object != null) {
					String fdname = field.getName();
					Object val = field.get(this);// 得到此属性的值
					ClassStrs.add(fdname + ":" + val);
				}
			}
			return getliststr(ClassStrs);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String getliststr(List<String> ClassStrs) {
		String rst = "";
		for (String s : ClassStrs) {
			rst = rst + s + "\r\n";
		}
		return rst;
	}

}
