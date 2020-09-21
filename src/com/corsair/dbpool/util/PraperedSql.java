package com.corsair.dbpool.util;

import java.sql.Types;
import java.util.ArrayList;

/**
 * 参数化SQL对象
 * 
 * @author Administrator
 *
 */
public class PraperedSql {
	private String sqlstr;
	private ArrayList<PraperedValue> parms = new ArrayList<PraperedValue>();

	public PraperedSql() {

	}

	public PraperedSql(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public PraperedSql(String sqlstr, Object[] values) {
		this.sqlstr = sqlstr;
		addParm(values);
	}

	public PraperedSql(String sqlstr, int[] values) {
		this.sqlstr = sqlstr;
		addParm(values);
	}

	public void addParm(PraperedValue pv) {
		parms.add(pv);
	}

	/**
	 * @param fieldtype
	 * Types...... 数据库字段类型
	 * @param value
	 */
	public void addParm(int fieldtype, Object value) {
		parms.add(new PraperedValue(fieldtype, value));
	}

	/**
	 * 当字符串处理
	 * 
	 * @param value
	 */
	public void addParm(Object value) {
		parms.add(new PraperedValue(Types.VARCHAR, value));
	}

	/**
	 * 当字符串处理
	 * 
	 * @param values
	 * {"",""}
	 */
	public void addParm(Object[] values) {
		for (Object value : values) {
			parms.add(new PraperedValue(Types.VARCHAR, value));
		}
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public ArrayList<PraperedValue> getParms() {
		return parms;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public void setParms(ArrayList<PraperedValue> parms) {
		this.parms = parms;
	}

}
