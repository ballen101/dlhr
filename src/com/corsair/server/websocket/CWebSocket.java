package com.corsair.server.websocket;

public abstract class CWebSocket {
	private String sessionid;
	private String userid;
	private long lasttime;
	private String vport;
	private COnSendJSON onsend;

	public abstract void OnConnected();

	public abstract void OnSend(String data);

	public abstract void OnClose(int code);//1 正常关闭 2超时关闭

	public CWebSocket(String sid, String vport) {
		this.sessionid = sid;
		this.vport = vport;
	}

	public void sendJSON(String jsonstr) {
		if (onsend != null)
			onsend.onsend(jsonstr);
		OnSend(jsonstr);
	}

	public String getSessionid() {
		return sessionid;
	}

	public String getUserid() {
		return userid;
	}

	public long getLasttime() {
		return lasttime;
	}

	public String getVport() {
		return vport;
	}

	public COnSendJSON getOnsend() {
		return onsend;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setLasttime(long lasttime) {
		this.lasttime = lasttime;
	}

	public void setVport(String vport) {
		this.vport = vport;
	}

	public void setOnsend(COnSendJSON onsend) {
		this.onsend = onsend;
	}

}
