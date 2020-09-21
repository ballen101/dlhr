package com.corsair.server.websocket;

public class CWebsocketClas {
	private Class<CWebSocket> socketclass;
	private String vport;

	public CWebsocketClas(Class<CWebSocket> socketclass, String vport) {
		this.setSocketclass(socketclass);
		this.vport = vport;
	}

	public String getVport() {
		return vport;
	}

	public void setVport(String vport) {
		this.vport = vport;
	}

	public Class<CWebSocket> getSocketclass() {
		return socketclass;
	}

	public void setSocketclass(Class<CWebSocket> socketclass) {
		this.socketclass = socketclass;
	}

}
