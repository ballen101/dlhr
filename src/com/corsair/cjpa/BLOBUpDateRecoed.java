package com.corsair.cjpa;

/**
 * BLOB字段对象
 * 
 * @author Administrator
 *
 */
public class BLOBUpDateRecoed {
	public enum TDataBlockType {
		dbk_BLOB, dbk_CLOB
	}

	/**
	 * 表明
	 */
	public String AtableName;
	/**
	 * 字段名
	 */
	public String AFieldName;
	/**
	 * 查询语句
	 */
	public String AQuerySqlStr;
	/**
	 * 编码后的值
	 */
	public String ABlogStr;
	/**
	 * 字段类型 BLOB  还是 CLOB
	 */
	TDataBlockType ADataBlockType;
}
