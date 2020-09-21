package com.corsair.cjpa;

/**
 * 兼容DELPHI的JPA（过时）
 * 
 * @author Administrator
 *
 */
public abstract class CJPAForDelphi extends CJPASave {

	public CJPAForDelphi() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	public CJPAForDelphi(String sqlstr) throws Exception {
		super(sqlstr);
	}

	public String d_save() throws Exception {
		save(true);
		return toxml(); // 保存成功后返回自己
	}

	public String d_findByID() throws Exception {
		CField idfd = this.getIDField();
		if (idfd == null) {
			throw new Exception("根据ID查询JPA<" + this.getClass().getSimpleName() + ">数据没发现ID字段");
		}
		findByID(idfd.getValue(), true);
		return toxml();
	}

	public String d_findBySQL() throws Exception {
		String sqlstr = "select * from " + this.tablename + " where 1=1 " + SqlWhere;
		findBySQL(sqlstr, true);
		return this.toxml();
	}

}
