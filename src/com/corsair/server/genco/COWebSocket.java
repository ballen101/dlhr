package com.corsair.server.genco;

import java.util.HashMap;

import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.csession.CSession;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.websocket.COnSendJSON;
import com.corsair.server.websocket.CWebSocket;

@ACO(coname = "web.socket")
public class COWebSocket {
	private String sendstr = null;
	private boolean issended = false;

	private final int timeout = 5;// 5分钟
	private final int timestop = 5;// 5秒

	@ACOAction(eventname = "connect", Authentication = false, notes = "长连接", ispublic = true)
	public String connect() throws Exception {
		if (!ConstsSw.getAppParmBoolean("websocket")) {
			throw new Exception("产品配置不支持websocket");
		}
		issended = false;
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String vport = CorUtil.hashMap2Str(urlparms, "vport", "需要参数vport");
		String sid = CSContext.getSession().getId();
		String userid = CSession.getvalue(sid, "userid");
		CWebSocket socket = CSContext.wbsktpool.addSocket(vport, sid);
		COnSendJSON onsend = new COnSendJSON() {
			@Override
			public void onsend(String jsonstr) {
				// TODO Auto-generated method stub
				sendstr = jsonstr;
				issended = true;
			}
		};
		socket.setOnsend(onsend);
		socket.setUserid(userid);
		socket.OnConnected();

		int i = 0;
		while (true) {
			Thread.sleep(timestop * 1000);
			if (issended) {
				break;
			}
			// 超时处理
			i++;
			if ((i * timestop) > (timeout * 60)) {
				socket.OnClose(2);
				return null;
			}
		}
		socket.OnClose(1);
		CSContext.wbsktpool.removeSocket(sid, vport);
		return sendstr;
	}
}
