package com.corsair.server.genco;

import com.corsair.cjpa.CJPAJSON;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shwposition;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;

@ACO(coname = "web.position")
public class COShwPosition {
	@ACOAction(eventname = "findPositions", Authentication = true, notes = "查找岗位")
	public String findPositions() throws Exception {
		String sqlstr = "select * from shwposition where 1=1";
		// CJPALineData<Shwposition> pis = new CJPALineData<Shwposition>(Shwposition.class);
		// pis.findDataBySQL(" ");
		return new CReport(sqlstr, null, null).findReport();
		// return pis.tojson();
	}

	@ACOAction(eventname = "findUsersByPosition", Authentication = true, notes = "查找岗位用户")
	public String findUsersByPosition() throws Exception {
		String positionid = CSContext.getParms().get("positionid");
		if ((positionid == null) || positionid.isEmpty())
			throw new Exception("需要positionid参数");
		if ("undefined".equalsIgnoreCase(positionid))
			return "{}";
		String sqlstr = "select a.* from shwuser a,shwpositionuser b where a.userid=b.userid and b.positionid=" + positionid;
		CJPALineData<Shwuser> users = new CJPALineData<Shwuser>(Shwuser.class).findDataBySQL(sqlstr);
		return users.tojson();
	}

	@ACOAction(eventname = "savePosition", Authentication = true, notes = "保存岗位")
	public String savePosition() throws Exception {
		Shwposition posi = (Shwposition) new Shwposition().fromjson(CSContext.getPostdata());
		return ((CJPAJSON) posi.save()).tojson();
	}

	@ACOAction(eventname = "delPosition", Authentication = true, notes = "删除岗位")
	public String delPosition() throws Exception {
		String positionid = CSContext.getParms().get("positionid");
		if ((positionid == null) || positionid.isEmpty())
			throw new Exception("需要positionid参数");
		Shwposition posi = (Shwposition) new Shwposition();

		CDBConnection con = posi.pool.getCon(this);
		con.startTrans();
		try {
			posi.delete(con, positionid, false);
			con.execsql("delete from shwpositionuser where positionid=" + positionid);
			con.submit();
			return "{\"result\":\"OK\"}";
		} catch (Exception e) {
			con.rollback();
			throw new Exception("删除岗位资料错误:" + e.getMessage());
		} finally {
			con.close();
		}
	}
}
