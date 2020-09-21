package com.corsair.server.cjpa;

/**
 * 流程通知的扩展类，除了通知用户ID，还包括扩展信息
 * 
 * @author shangwen
 * 
 */
public class CNotifyUEInfo {
	private String userid;// 实际用户ID
	private String wfprocuserid; // 流程节点用户ID

	public CNotifyUEInfo(String userid) throws Exception {
		if (userid == null)
			throw new Exception("CNotifyUEInfo'userid can't null");
		this.userid = userid;
	}

	public CNotifyUEInfo(String userid, String wfprocuserid) throws Exception {
		if (userid == null)
			throw new Exception("CNotifyUEInfo'userid can't null");
		this.userid = userid;
		this.wfprocuserid = wfprocuserid;
	}

	public String getUserid() {
		return userid;
	}

	public String getWfprocuserid() {
		return wfprocuserid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setWfprocuserid(String wfprocuserid) {
		this.wfprocuserid = wfprocuserid;
	}

}
