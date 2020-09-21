package com.corsair.server.genco;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.cjpa.CJPAJSON;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.CJPAWorkFlow;
import com.corsair.server.cjpa.CJPAWorkFlow2;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.corsair.server.wordflow.Shwwfprocuser;
import com.corsair.server.wordflow.Shwwftemp;
import com.corsair.server.wordflow.Shwwftemplinkline;
import com.corsair.server.wordflow.Shwwftempproc;

@ACO(coname = "web.wf")
public class COShwWf {

	@ACOAction(eventname = "getWftemp", Authentication = true, ispublic = true)
	public String getWftemp() throws Exception {
		String wftempid = CSContext.getParms().get("wftempid");
		if ((wftempid == null) || wftempid.isEmpty())
			throw new Exception("参数wftempid必须！");
		return ((CJPAJSON) new Shwwftemp().findByID(wftempid)).tojson();
	}

	@ACOAction(eventname = "getWftemps", Authentication = true, ispublic = true)
	public String getWftemps() throws Exception {
		String fdrid = CSContext.getParms().get("fdrid");
		if ((fdrid == null) || fdrid.isEmpty())
			throw new Exception("参数fdrid必须！");
		String sqlstr = "select * from shwwftemp a where a.fdrid=" + fdrid + " and EntID=" + CSContext.getCurEntID();
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getWffds", Authentication = true, ispublic = true)
	public String getWffds() throws Exception {
		String sqlstr = "select * from shwfdr a where a.fdtype=2 and superid<>0 and actived=1";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getwftemtypetree", Authentication = true, ispublic = true)
	public String getwftemtypetree() throws Exception {
		String sqlstr = "select * from shwwftemtype where 1=1 ";
		return DBPools.defaultPool().opensql2jsontree(sqlstr, "wftpid", "superid", false);
	}

	@ACOAction(eventname = "saveWfTemp", Authentication = true, notes = "保存流程模板")
	public String saveWfTemp() throws Exception {
		JsonNode rootNode = new ObjectMapper().readTree(CSContext.getPostdata());
		boolean isnew = rootNode.path("isnew").asBoolean();
		String jsondata = rootNode.path("jsondata").toString();
		Shwwftemp wft = (Shwwftemp) new Shwwftemp();
		if (isnew)
			wft.setJpaStat(CJPAStat.RSINSERT);
		else
			wft.setJpaStat(CJPAStat.RSUPDATED);
		wft.fromjson(jsondata);
		// wft.toxmlfile("c:\\" + wft.getClass().getSimpleName() + ".xml");
		wft.save();
		return wft.tojson();
	}

	@ACOAction(eventname = "copyWfTemp", Authentication = true, notes = "复制模板")
	public String copyWfTemp() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String wftempid = CorUtil.hashMap2Str(parms, "wftempid", "需要参数【wftempid】");
		String new_wftempname = CorUtil.hashMap2Str(parms, "new_wftempname", "需要参数【new_wftempname】");
		Shwwftemp wft = new Shwwftemp();

		CDBConnection con = wft.pool.getCon(this);
		con.startTrans();
		try {

			wft.findByID(wftempid);
			CJPALineData<Shwwftempproc> proces = wft.shwwftempprocs;
			CJPALineData<Shwwftemplinkline> lines = wft.shwwftemplinklines;
			for (CJPABase jpa : lines) {
				Shwwftemplinkline line = (Shwwftemplinkline) jpa;
				Shwwftempproc fproc = (Shwwftempproc) proces.getJPAByID(line.fromproctempid.getValue());
				Shwwftempproc tproc = (Shwwftempproc) proces.getJPAByID(line.toproctempid.getValue());
				TwoObject o2 = new TwoObject(fproc, tproc);
				line._userdata = o2;
			}
			// System.out.println("1111:"+wft.tojson());
			wft.clearAllId();
			// System.out.println("2222:"+wft.tojson());
			wft.checkOrCreateIDCode(con, true);
			// System.out.println("3333:"+wft.tojson());
			for (CJPABase jpa : lines) {
				Shwwftemplinkline line = (Shwwftemplinkline) jpa;
				TwoObject o2 = (TwoObject) line._userdata;
				Shwwftempproc fproc = (Shwwftempproc) o2.obj1;
				Shwwftempproc tproc = (Shwwftempproc) o2.obj2;
				line.fromproctempid.setValue(fproc.proctempid.getValue());
				line.toproctempid.setValue(tproc.proctempid.getValue());
			}
			wft.wftempname.setValue(new_wftempname);
			wft.save(con);
			con.submit();
			return wft.tojson();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * @return 获取当前登录用户 活动流程数量
	 * @throws Exception
	 */
	@ACOAction(eventname = "wfcount", Authentication = true, ispublic = true)
	public String getWFcount() throws Exception {
		String sqlstr = "	SELECT COUNT(*) AS ct"
				+ "	FROM (SELECT DISTINCT a.*"
				+ "		FROM shwwf a, shwwfproc b, shwwfprocuser c, (SELECT wfu.`userid`, wfu.`wftempid`"
				+ "			FROM `shwuser` u, `shwuser_wf_agent` wfu"
				+ "			WHERE u.`goout` = 1"
				+ "				AND u.`userid` = wfu.`userid`"
				+ "				AND wfu.`auserid` = " + CSContext.getUserID()
				+ "			) ag"
				+ "		WHERE a.wfid = b.wfid"
				+ "			AND b.procid = c.procid"
				+ "			AND a.stat = 1"
				+ "			AND c.stat IN(1,3)"
				+ "			AND b.stat = 2"
				+ "			AND a.`wftemid` = ag.wftempid"
				+ "			AND c.`userid` = ag.userid"
				+ "		UNION"
				+ "		SELECT DISTINCT a.*"
				+ "		FROM shwwf a, shwwfproc b, shwwfprocuser c"
				+ "		WHERE a.wfid = b.wfid"
				+ "			AND b.procid = c.procid"
				+ "			AND a.stat = 1"
				+ "			AND c.stat IN(1,3)"
				+ "			AND b.stat = 2"
				+ "			AND c.userid = " + CSContext.getUserID()
				+ "		) tb";

		String rst = DBPools.defaultPool().opensql2json(sqlstr);
		return rst;
	}

	// private HashMap<String, String> newBuildInParm(String id, String value) {
	// HashMap<String, String> pm = new HashMap<String, String>();
	// pm.put("id", id);
	// pm.put("value", value);
	// return pm;
	// }

	@ACOAction(eventname = "delWFTem", Authentication = true, notes = "删除流程模板")
	public String delWFTem() throws Exception {
		String wftempid = CorUtil.hashMap2Str(CSContext.parPostDataParms(), "wftempid", "需要参数wftempid");
		Shwwftemp wft = (Shwwftemp) new Shwwftemp();
		wft.findByID(wftempid);
		if (wft.isEmpty())
			throw new Exception("没有发现ID为【" + wftempid + "】的流程模板");
		wft.delete();
		return "{\"result\":\"ok\"}";
	}

	// @ACOAction(eventname = "getWFBuildInParms", Authentication = true)
	// public String getWFBuildInParms() throws Exception {
	// List<HashMap<String, String>> pms = new ArrayList<HashMap<String, String>>();
	// pms.add(newBuildInParm("<#sysdatetime#/>", "系统时间"));
	// pms.add(newBuildInParm("<#proctitle#/>", "节点标题"));
	// return CJSON.List2JSON(pms);
	// }

	@ACOAction(eventname = "getWfCurProcedure", Authentication = true, ispublic = true)
	public String getWfCurProcedure() throws Exception {
		String wfid = CorUtil.hashMap2Str(CSContext.getParms(), "wfid", "需要参数wfid");
		Shwwfproc proc = getWfCurProcedure(wfid);
		if (proc == null)
			return "{}";
		else
			return proc.tojson();
	}

	/**
	 * @param wfid
	 * @return 获取活动流程
	 * @throws Exception
	 */
	public static Shwwfproc getWfCurProcedure(String wfid) throws Exception {
		String sqlstr = "SELECT procid FROM `shwwfproc` WHERE wfid=" + wfid + " AND stat=2 AND isbegin<>1 AND isend<>1";
		List<HashMap<String, String>> cpros = DBPools.defaultPool().openSql2List(sqlstr);
		if (cpros.size() <= 0)
			return null;
		String procid = cpros.get(0).get("procid").toString();
		return (Shwwfproc) new Shwwfproc().findByID(procid);
	}

	/**
	 * @return 返回当前登录用户是否在流程活动节点里面 同时返回流程对象
	 * @throws Exception
	 */
	@ACOAction(eventname = "findWfInfo", Authentication = true, ispublic = true)
	public String findWfInfo() throws Exception {
		String wfid = CorUtil.hashMap2Str(CSContext.getParms(), "wfid", "需要参数wfid");
		String userid = CSContext.getParms().get("userid");
		if ((userid == null) || userid.isEmpty())
			userid = CSContext.getUserID();
		Shwwf wf = new Shwwf();
		wf.findByID(wfid);
		// 设置代理名；
		// 判断当前用户是否参与活动节点
		Shwwfproc proc = null;
		if (wf.stat.getAsIntDefault(0) != 2) {
			for (CJPABase jprc : wf.shwwfprocs) {
				Shwwfproc proc1 = (Shwwfproc) jprc;
				if ("2".equals(proc1.stat.getValue())) {
					proc = proc1;
					break;
				}
			}
		}
		Shwwfprocuser user = null;
		if (proc != null)
			user = CJPAWorkFlow2.checkProc(wf, proc, userid);
		JSONObject rst = new JSONObject();
		int userInActiveNode = (user != null) ? 1 : 2;
		rst.put("userInActiveNode", userInActiveNode);
		rst.put("wfinfo", wf.toJsonObj());
		return rst.toString();
	}

	/**
	 * @return 流程管理界面获取流程
	 * @throws Exception
	 */
	@ACOAction(eventname = "getManageWFList", Authentication = true, ispublic = true)
	public String getManageWFList() throws Exception {
		String poolname = CorUtil.hashMap2Str(CSContext.getParms(), "poolname", "需要参数poolname");

		String sqlstr = "SELECT * FROM shwwf WHERE stat=1 ORDER BY wfname, wfid DESC";
		HashMap<String, CJPA> jpas = new HashMap<String, CJPA>();
		CDBPool pool = DBPools.poolByName(poolname);
		CDBConnection con = pool.getCon(this);
		try {
			JSONArray rst = con.opensql2json_o(sqlstr);
			for (int i = 0; i < rst.size(); i++) {
				JSONObject j = rst.getJSONObject(i);
				CJPA jpa = getjpabyname(jpas, j.getString("clas"));
				if (jpa == null)
					throw new Exception("根据指定的类【" + j.getString("clas") + "】创建实体错误");
				jpa.findByID(con, j.getString("ojcectid"), false);
				if (jpa.isEmpty())
					j.put("isexistobj", 2);
				else
					j.put("isexistobj", 1);
			}
			return rst.toString();
		} finally {
			con.close();
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@ACOAction(eventname = "getWFLog", Authentication = true, ispublic = true, notes = "获取流程审批日志")
	public String getWFLog() throws Exception {
		String wfid = CorUtil.hashMap2Str(CSContext.getParms(), "wfid", "需要参数wfid");
		String sqlstr = "select * from shwwfproclog where wfid=" + wfid + " ORDER BY actiontime";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	private CJPA getjpabyname(HashMap<String, CJPA> jpas, String jpaclass) throws Exception {
		CJPA jpa = jpas.get(jpaclass);
		if (jpa == null)
			jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		else
			jpa.clear();
		return jpa;
	}

}
