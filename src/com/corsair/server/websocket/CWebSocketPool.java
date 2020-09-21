package com.corsair.server.websocket;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.corsair.server.base.ConstsSw;

public class CWebSocketPool {
	private List<CWebSocket> sockets = Collections.synchronizedList(new ArrayList<CWebSocket>());

	public CWebSocket getSocket(String sid, String vport) {
		if ((sid == null) || sid.isEmpty())
			return null;
		if ((vport == null) || vport.isEmpty())
			return null;
		for (CWebSocket socket : sockets) {
			if (sid.equals(socket.getSessionid()) && vport.equals(socket.getVport()))
				return socket;
		}
		return null;
	}

	//不存就创建
	public CWebSocket addSocket(String vport,String sid) throws Exception {
		CWebSocket rst = getSocket(sid, vport);
		if (rst != null)
			return rst;
		Object oc= ConstsSw._allSocketClassName.get(vport);
		if(oc==null)
			throw new Exception("websocket虚拟端口【"+vport+"】未定义");
		Class<CWebSocket> sktcls= ((CWebsocketClas) oc).getSocketclass();
		
		Class<?> paramTypes[] = { String.class, String.class };
		Constructor<?> cst = null;
		try {
			cst = sktcls.getConstructor(paramTypes);
			Object o = cst.newInstance(sid, vport);
			rst = (CWebSocket) (o);
		} catch (Exception e) {
			Object o;
			o = sktcls.newInstance();
			rst = (CWebSocket) (o);
		}
		sockets.add(rst);
		return rst;
	}


	public void sendJSON(String userid, String vport, String jsonstr) {
		if ((userid == null) || userid.isEmpty())
			return;
		if ((vport == null) || vport.isEmpty())
			return;

		List<CWebSocket> usersockets = getSocketByUser(userid);
		for (CWebSocket socket : usersockets) {
			if (vport.equals(socket.getVport())) {
				System.out.println("do send message:" + socket.getVport());
				socket.sendJSON(jsonstr);
			}
		}
	}

	public List<CWebSocket> getSocketByUser(String userid) {
		List<CWebSocket> rst = new ArrayList<CWebSocket>();
		if (userid == null)
			return rst;
		for (CWebSocket socket : sockets) {
			if (userid.equals(socket.getUserid())) {
				rst.add(socket);
			}
		}
		return rst;
	}


	public void removeSocket(String sid, String vport) {
		CWebSocket socket = getSocket(sid, vport);
		if (socket != null)
			sockets.remove(socket);
	}

	public List<CWebSocket> getSockets() {
		return sockets;
	}



}
