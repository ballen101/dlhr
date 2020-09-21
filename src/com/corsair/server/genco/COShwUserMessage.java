package com.corsair.server.genco;

import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;

@ACO(coname = "web.usermsg")
public class COShwUserMessage {

	@ACOAction(eventname = "findUserMSGs", Authentication = true, ispublic = true, notes = "获取登录用户消息")
	public String findUserMSGs() throws Exception {
		HashMap<String, String> ppram = CSContext.getParms();
		String userid = CSContext.getUserID();
		// 1：所有信息 2：已读信息 3：未读信息
		int tp = Integer.valueOf(CorUtil.hashMap2Str(ppram, "tp", "需要设置tp参数"));
		// tp为3时候 获取 该ID以后的信息
		String mmid = CorUtil.hashMap2Str(ppram, "mmid");
		int lt = (mmid == null) ? 0 : Integer.valueOf(mmid);
		String where = " a.touserid=" + userid + " ";
		if (tp == 1) {

		} else if (tp == 2) {
			where = where + " and a.isreaded=1 ";
		} else if (tp == 3) {
			where = where + " and a.isreaded=2 ";
			if (lt != 0)
				where = where + " and mid>" + lt + " ";
		}
		String sqlstr = "select a.* from shwusermessage a where " + where + " order by a.mid desc";
		// System.out.println(sqlstr);
		return DBPools.defaultPool().opensql2json(sqlstr, false);
	}

	@ACOAction(eventname = "setReadStat", Authentication = true, ispublic = true, notes = "获取登录用户消息")
	public String setReadStat() throws Exception {
		JSONArray arrs = JSONArray.fromObject(CSContext.getPostdata());
		// 1 已读 2 未读
		int tp = Integer.valueOf(CorUtil.hashMap2Str(CSContext.getParms(), "tp", "需要设置tp参数"));
		if ((tp != 1) && (tp != 2))
			throw new Exception("tp只能为1或2");
		String mids = "";
		for (int i = 0; i < arrs.size(); i++) {
			JSONObject jo = arrs.getJSONObject(i);
			String mid = jo.get("mid").toString();
			mids = mids + mid + ",";
		}
		if (mids.length() == 0)
			throw new Exception("需要设置mids参数");
		mids = "(" + mids.substring(0, mids.length() - 1) + ")";
		String sqlstr = "update shwusermessage set isreaded=" + tp + " where mid in" + mids;
		DBPools.defaultPool().execsql(sqlstr, true);
		return "{\"result\":\"OK\"}";
	}
}
