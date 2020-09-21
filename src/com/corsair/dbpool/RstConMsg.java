package com.corsair.dbpool;

/**
 * 记录错误
 * 
 * @author Administrator
 *
 */
public class RstConMsg {
	/**
	 * 
	 */
	private CDBConnection con;
	private String errmsg;

	public RstConMsg(CDBConnection con) {
		this.con = con;
		this.errmsg = null;
	}

	public RstConMsg(String errmsg) {
		this.con = null;
		this.errmsg = errmsg;
	}

	public CDBConnection getCon() {
		return con;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setCon(CDBConnection con) {
		this.con = con;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

}
