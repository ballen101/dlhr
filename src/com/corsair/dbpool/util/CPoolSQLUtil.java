package com.corsair.dbpool.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * 工具类
 * 
 * @author Administrator
 *
 */
public class CPoolSQLUtil {
	/**
	 * 
	 */
	public static int[] numFDType = { Types.BIT, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT, Types.FLOAT, Types.REAL, Types.DOUBLE,
			Types.NUMERIC, Types.DECIMAL, Types.NULL, Types.BOOLEAN, Types.BINARY, Types.ROWID, Types.NCHAR };
	public static int[] strFDType = { Types.CHAR, Types.VARCHAR, Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.VARBINARY, Types.LONGVARBINARY, Types.NVARCHAR,
			Types.LONGNVARCHAR, Types.SQLXML };
	public static int[] dateFDType = { Types.DATE, Types.TIME, Types.TIMESTAMP };
	public static int[] bloFDType = { Types.BLOB, Types.CLOB, Types.NCLOB };

	public static boolean eInArray(int e, int[] arr) {
		for (int i : arr) {
			if (i == e) {
				return true;
			}
		}
		return false;
	}

	public static void setSqlPValue(PreparedStatement pstmt, int index, PraperedValue pv) throws SQLException {
		if (eInArray(pv.getFieldtype(), dateFDType)) {
			java.sql.Timestamp dt = (pv.getValue() == null) ? null : new java.sql.Timestamp(((Date) pv.getValue()).getTime());
			pstmt.setTimestamp(index, dt);
		} else {
			pstmt.setObject(index, pv.getValue());
		}
	}
}
