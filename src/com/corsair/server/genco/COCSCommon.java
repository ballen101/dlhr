package com.corsair.server.genco;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPAJSON;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.csession.CSession;
import com.corsair.server.exception.CWFException;
import com.corsair.server.generic.Shw_attach;
import com.corsair.server.generic.Shw_attach_line;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shwjpaattr;
import com.corsair.server.generic.Shwjpaexportsche;
import com.corsair.server.generic.Shwnotice;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwsystemparms;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.listener.CorsairSessionListener;
import com.corsair.server.mail.CMail;
import com.corsair.server.mail.CMailInfo;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.retention.COAcitonItem;
import com.corsair.server.util.CExcel;
import com.corsair.server.util.CExcelEx;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.GetNewSEQID;
import com.corsair.server.util.GetNewSystemCode;
import com.corsair.server.util.ListHashMap;
import com.corsair.server.util.MatrixUtil;
import com.corsair.server.util.PictureCheckCode;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.util.TerminalType.TermType;
import com.corsair.server.websocket.CWebSocket;
import com.corsair.server.wordflow.FindWfTemp;
import com.corsair.server.wordflow.Shwwfproc;

/**
 * 通用CO
 * 
 * @author Administrator
 *
 */
@ACO(coname = "web.common")
public class COCSCommon {
	@ACOAction(eventname = "getcorsairxk", Authentication = false, notes = "许可", ispublic = true)
	public String getcorsairxk() {
		// fc: ty=1;
		String ty = CSContext.getParms().get("ty");
		String rst = "true";
		if ((ty == null) || ty.isEmpty()) {
			rst = "true";
		}
		int t = Integer.valueOf(ty);
		if (t == 1) {
			rst = "true";
		}
		return "{\"rst\":\"" + rst + "\"}";
	}

	@ACOAction(eventname = "poolsinfo", Authentication = false, notes = "", ispublic = true)
	public String poolsinfo() throws Exception {
		JSONArray rst = new JSONArray();
		for (CDBPool p : DBPools.getDbpools()) {
			JSONObject o = new JSONObject();
			o.put("name", p.pprm.name);
			o.put("schema", p.pprm.schema);
			o.put("default", p.pprm.isdefault);
			o.put("sessions", p.sessions.size());
			JSONArray pi = p.getinfo();
			o.put("poolinfo", pi);
			rst.add(o);
		}
		return rst.toString();
	}

	@ACOAction(eventname = "websocketinfo", Authentication = false, notes = "", ispublic = true)
	public String websocketinfo() throws Exception {
		JSONArray rst = new JSONArray();
		List<CWebSocket> sockets = CSContext.wbsktpool.getSockets();
		for (CWebSocket socket : sockets) {
			String session = socket.getSessionid();
			String userid = socket.getUserid();
			String lasttime = String.valueOf(socket.getLasttime());
			JSONObject jo = new JSONObject();
			jo.put("session", session);
			jo.put("userid", userid);
			jo.put("lasttime", lasttime);
			jo.put("vport", socket.getVport());
			rst.add(jo);
		}
		return rst.toString();
	}

	@ACOAction(eventname = "openCLientSQL", Authentication = true, notes = "", ispublic = true)
	public String openCLientSQL() throws Exception {
		boolean isadmin = false;
		Object o = CSession.getvalue(CSContext.getSession().getId(), "usertype");// CSContext.getSession().getAttribute("usertype");
		if ((o != null) && (o.toString().length() > 0) && (Integer.valueOf(o.toString()) == 1)) {
			isadmin = true;
		}
		if ((!isadmin) && (CSContext.getTermType() != TermType.delphiclt)) {
			throw new Exception("openCLientSQL只给客户端程序使用");
		}
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String poolname = CorUtil.hashMap2Str(parms, "poolname");
		String sqlstr = CorUtil.hashMap2Str(parms, "sqlstr", "需要参数sqlstr");
		CDBPool pool;
		if (poolname != null)
			pool = DBPools.defaultPool();
		else
			pool = DBPools.poolByName(poolname);
		if (pool == null)
			throw new Exception("未能获取适合的数据库连接池");
		return pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getnotices", Authentication = true, notes = "获取系统通知", ispublic = true)
	public String getNotices() throws Exception {
		CJPALineData<Shwnotice> nots = new CJPALineData<Shwnotice>(Shwnotice.class);
		String sqlstr = "select * from shwnotice where entid=" + CSContext.getCurEntID() + " and stat=9 order by create_time desc";
		nots.findDataBySQL(sqlstr, true, false);
		return nots.tojson();
	}

	@ACOAction(eventname = "getshw_attachbyid", Authentication = true, notes = "获取附件", ispublic = true)
	public String getShw_attach() throws Exception {
		String attid = CorUtil.hashMap2Str(CSContext.getParms(), "id", "需要参数id");
		Shw_attach att = new Shw_attach();
		att.findByID(attid);
		return att.tojson();
	}

	@ACOAction(eventname = "upLoadFile", Authentication = true, notes = "上传附件", ispublic = true)
	public String upLoadFile() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		boolean imgthb = (parms.get("attimgthb") == null) ? false : Boolean.valueOf(parms.get("attimgthb").toString());// 是否生成图片缩略图
		String filetitle = (parms.get("filetitle") == null) ? null : parms.get("filetitle").toString();
		CJPALineData<Shw_physic_file> fs = UpLoadFileEx.doupload(imgthb, filetitle);
		return fs.tojson();
	}

	@ACOAction(eventname = "upLoadFile64", Authentication = true, notes = "上传附件", ispublic = true)
	public String upLoadFile64() throws Exception {
		JSONObject opd = CSContext.parPostData2JSONObject();
		String extfname = CorUtil.getJSONValue(opd, "extfname", "需要参数extfname");
		String fdata = CorUtil.getJSONValue(opd, "fdata", "需要参数fdata");
		String fname = CorUtil.getJSONValue(opd, "fname");
		Shw_physic_file rst = UpLoadFileEx.douploadBase64File(fdata, fname, extfname);
		return rst.tojson();
	}

	@ACOAction(eventname = "upLoadFileProress", Authentication = true, notes = "文件上传进度", ispublic = true)
	public String upLoadFileProress() {
		String p = CSContext.getParm("uploadfile");
		if (p == null)
			p = "0";
		return "{\"p\":\"" + p + "\"}";
	}

	/**
	 * ptype 文件夹
	 * 
	 * @throws Exception
	 */
	@ACOAction(eventname = "downattfile", Authentication = false, notes = "下载附件", ispublic = true)
	public void downattfile() throws Exception {
		HttpServletResponse rsp = CSContext.getResponse();
		Shw_physic_file pf = new Shw_physic_file();
		String pfid = CSContext.getParms().get("pfid");
		if ((pfid == null) || pfid.isEmpty())
			throw new Exception("需要附件ID参数");

		int ptype = (CSContext.getParms().get("ptype") == null) ? 0 : Integer.valueOf(CSContext.getParms().get("ptype").toString());// 下载对应类型的扩展文件
		pf.findByID(pfid);

		if (pf.isEmpty())
			throw new Exception("没有找到物理文件记录！");
		if (ptype != 0) {
			String sqlstr = "SELECT * FROM shw_physic_file f WHERE f.ppfid=" + pf.pfid.getValue() + " AND f.ptype=" + ptype;
			Shw_physic_file epf = new Shw_physic_file();
			epf.findBySQL(sqlstr);
			if (!epf.isEmpty())
				pf = epf;
		}

		String fullname = UpLoadFileEx.getPhysicalFileName(pf);
		if (!(new File(fullname)).exists())
			throw new Exception("文件" + fullname + "不存在!");

		rsp.setContentType(getContentType(fullname));
		rsp.setHeader("content-disposition", "attachment; filename=" + new String(pf.displayfname.getValue().getBytes("GB2312"), "ISO_8859_1"));
		OutputStream os = rsp.getOutputStream();
		FileInputStream fis = new FileInputStream(fullname);
		try {
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = fis.read(b)) > 0) {
				os.write(b, 0, i);
			}
			fis.close();
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			fis.close();
			os.flush();
			os.close();
		}
	}

	private static String getContentType(String pathToFile) {
		Path path = Paths.get(pathToFile);
		try {
			return Files.probeContentType(path);
		} catch (IOException e) {
			return "application/text";
		}
	}

	@ACOAction(eventname = "delattfile", Authentication = true, notes = "删除附件", ispublic = true)
	public String delattfile() throws Exception {
		// HttpServletResponse rsp = CSContext.getResponse();
		Shw_physic_file pf = new Shw_physic_file();
		String pfid = CSContext.getParms().get("pfid");
		if ((pfid == null) || pfid.isEmpty())
			throw new Exception("需要附件ID参数");
		pf.findByID(pfid);
		UpLoadFileEx.delAttFile(pf.pfid.getValue());
		String sqlstr = "DELETE from shw_attach_line WHERE pfid=" + pf.pfid.getValue();
		DBPools.defaultPool().execsql(sqlstr);
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "getmyworkwflist", Authentication = true, notes = "获取工作列表", ispublic = true)
	public String myworkwflist() throws Exception {
		String wftype = CSContext.getParms().get("wftype");
		if ((wftype == null) || wftype.isEmpty())
			throw new Exception("需要wftype参数");
		String sqlstr = null;
		String userid = CSContext.getUserID();
		int wf = Integer.valueOf(wftype);
		if (wf == 1) {// 当前流程
			sqlstr = "	SELECT tp.wftempname,tb.*"
					+ "	FROM (SELECT DISTINCT a.*"
					+ "		FROM shwwf a, shwwfproc b, shwwfprocuser c, (SELECT wfu.userid, wfu.wftempid"
					+ "			FROM shwuser u, shwuser_wf_agent wfu"
					+ "			WHERE u.goout = 1"
					+ "				AND u.userid = wfu.userid"
					+ "				AND wfu.auserid =  " + userid
					+ "			) ag"
					+ "		WHERE a.wfid = b.wfid"
					+ "			AND b.procid = c.procid"
					+ "			AND a.stat = 1"
					+ "			AND c.stat IN(1,3)"
					+ "			AND b.stat = 2"
					+ "			AND a.wftemid = ag.wftempid"
					+ "			AND c.userid = ag.userid"
					+ "		UNION"
					+ "		SELECT DISTINCT a.*"
					+ "		FROM shwwf a, shwwfproc b, shwwfprocuser c"
					+ "		WHERE a.wfid = b.wfid"
					+ "			AND b.procid = c.procid"
					+ "			AND a.stat = 1"
					+ "			AND c.stat IN(1,3)"
					+ "			AND b.stat = 2"
					+ "			AND c.userid =  " + userid
					+ "		) tb,shwwftemp tp"
					+ "	WHERE tb.wftemid=tp.wftempid"
					+ "	ORDER BY tb.createtime DESC";

		} else if (wf == 2) {// 我启动的流程
			sqlstr = " select DISTINCT * from( SELECT  tp.wftempname,a.*"
					+ " FROM shwwf a, shwwftemp tp"
					+ " WHERE a.stat = 1 AND a.wftemid=tp.wftempid AND a.submituserid = " + userid
					+ ") tb ORDER BY createtime DESC";
		} else if (wf == 3) {// 未到到的流程
			sqlstr = "select distinct * from( SELECT tp.wftempname,a.*"
					+ " FROM shwwf a, shwwfproc b, shwwfprocuser c, shwwftemp tp  "// shwuser d,//180105
					+ " WHERE a.wfid = b.wfid AND b.procid = c.procid AND a.wftemid=tp.wftempid "
					+ " AND b.stat = 1 AND c.userid =  " + userid
					+ ") tb order by createtime desc";
		} else if (wf == 4) {// 已完成的流程
			sqlstr = "select distinct * from( SELECT tp.wftempname,a.*"
					+ " FROM shwwf a, shwwfproc b, shwwfprocuser c, shwwftemp tp  "// shwuser d,//180105
					+ " WHERE a.wfid = b.wfid AND b.procid = c.procid AND a.wftemid=tp.wftempid "
					+ " AND a.stat = 3 AND c.userid =  " + userid
					+ ") tb order by createtime desc";
		} else if (wf == 5) {// 通知我的流程
			sqlstr = "select distinct * from( SELECT tp.wftempname,a.*"
					+ " FROM shwwf a, shwwfproc b, shwwfprocuser c, shwwftemp tp  "// shwuser d, //180105
					+ " WHERE a.wfid = b.wfid AND b.procid = c.procid AND a.wftemid=tp.wftempid "
					+ " AND c.jointype=3 AND c.userid =  " + userid
					+ ")tb order by createtime desc";
		} // AND a.creator = d.username

		JSONArray rts = DBPools.defaultPool().opensql2json_O(sqlstr);
		if (wf != 4) {
			for (int i = 0; i < rts.size() - 1; i++) {
				JSONObject jo = rts.getJSONObject(i);
				sqlstr = "SELECT p.procname,p.arivetime,u.displayname,u.userid FROM shwwfproc p,shwwfprocuser u"
						+ " WHERE p.procid=u.procid AND  p.stat=2 AND u.stat IN(1,3) AND p.wfid=" + jo.getString("wfid");
				String names = "";
				String uids = "";
				List<HashMap<String, String>> us = DBPools.defaultPool().openSql2List(sqlstr);
				for (int j = 0; j < us.size(); j++) {
					HashMap<String, String> u = us.get(j);
					names = names + u.get("displayname") + ",";
					uids = uids + u.get("userid") + ",";
				}
				if (!names.isEmpty())
					names = names.substring(0, names.length() - 1);
				if (!uids.isEmpty())
					uids = uids.substring(0, uids.length() - 1);
				jo.put("curusers", names);
				jo.put("curuserids", uids);
				if (us.size() > 0) {
					jo.put("procname", us.get(0).get("procname"));
					jo.put("arivetime", us.get(0).get("arivetime"));
				}

			}

		}
		rts = buldTreeWf(rts);
		return rts.toString();
	}

	private JSONArray buldTreeWf(JSONArray wfs) {
		JSONArray rst = new JSONArray();
		long mxwfid = getMaxwfID(wfs);
		for (int i = 0; i < wfs.size(); i++) {
			JSONObject wf = wfs.getJSONObject(i);
			if (!hasTemp(rst, wf)) {
				JSONObject tem = new JSONObject();
				tem.put("wftempname", wf.getString("wftempname"));
				tem.put("wftemid", wf.getString("wftemid"));
				tem.put("wfid", ++mxwfid);
				tem.put("iswftemp", "1");
				JSONArray rstwfs = new JSONArray();
				for (int j = 0; j < wfs.size(); j++) {
					JSONObject rstwf = wfs.getJSONObject(j);
					if (rstwf.getString("wftemid").equalsIgnoreCase(wf.getString("wftemid"))) {
						rstwf.put("wftempname", rstwf.getString("subject"));
						rstwfs.add(rstwf);
					}
				}
				tem.put("children", rstwfs);
				rst.add(tem);
			}
		}
		return rst;
	}

	private boolean hasTemp(JSONArray tems, JSONObject wf) {
		for (int i = 0; i < tems.size(); i++) {
			JSONObject tem = tems.getJSONObject(i);
			if (wf.getString("wftemid").equalsIgnoreCase(tem.getString("wftemid"))) {
				return true;
			}
		}
		return false;
	}

	private long getMaxwfID(JSONArray wfs) {
		long wfid = 0;
		for (int i = 0; i < wfs.size(); i++) {
			JSONObject wf = wfs.getJSONObject(i);
			if (wf.getLong("wfid") > wfid)
				wfid = wf.getLong("wfid");
		}
		return wfid;
	}

	@ACOAction(eventname = "getserverdate", Authentication = true, notes = "获取系统时间", ispublic = true)
	public String serverdate() {
		return "{\"date\":\"" + Systemdate.getStrDate() + "\"}";
	}

	@ACOAction(eventname = "getnewid", Authentication = true, notes = "获取ID", ispublic = true)
	public String getnewid() throws Exception {
		String tbname = CSContext.getParms().get("tbname");
		if ((tbname == null) || tbname.isEmpty())
			throw new Exception("需要tbname参数");
		String newid = null;
		try {
			newid = GetNewSEQID.dogetnewid1(tbname, 1);
		} catch (Exception e) {
			Logsw.error(e);
		}
		return "{\"newid\":\"" + newid + "\"}";
	}

	@ACOAction(eventname = "getJPAInfo", Authentication = false, notes = "获取JPA信息", ispublic = true)
	public String getJPAInfo() throws Exception {
		String jpaclass = CSContext.getParms().get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		return jpa.toJPAInfoJson();
	}

	// 是职能部门用户
	private static boolean userIsInside() throws Exception {
		CJPALineData<Shworg> os = CSContext.getUserOrgs();
		for (CJPABase o : os) {
			int ot = ((Shworg) o).orgtype.getAsIntDefault(0);
			if (ot == 1) {
				return true;
			}
		}
		return false;
	}

	private static boolean Obj2Bl(Object o) {
		if (o == null)
			return false;
		return Boolean.valueOf(o.toString());
	}

	@ACOAction(eventname = "find", Authentication = true, notes = "公用查询", ispublic = true)
	public String find() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		return do_find(urlparms);
	}

	public static String do_find(HashMap<String, String> urlparms) throws Exception {
		String jpaclass = CorUtil.hashMap2Str(urlparms, "jpaclass", "需要参数jpaclass");
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");

		String disidpath = urlparms.get("disidpath");
		boolean disi = (disidpath != null) ? Boolean.valueOf(disidpath) : false;
		disi = disi && (ConstsSw.getSysParmIntDefault("ALLCLIENTCHGIDPATH", 2) == 1);// 前端要求屏蔽idpath 且 服务端设置允许前端修改
		// System.out.println("dfidpath:" + dfidpath);
		// System.out.println("dfi:" + dfi);
		String sqlwhere = urlparms.get("sqlwhere");
		String selflines = urlparms.get("selfline");
		boolean selfline = (selflines != null) ? Boolean.valueOf(selflines) : true;

		// String chgidpath = urlparms.get("chgidpath");
		// boolean chgi = (chgidpath != null) ? Boolean.valueOf(chgidpath) :
		// false;

		if ("list".equalsIgnoreCase(type) || "tree".equalsIgnoreCase(type)) {
			selfline = false;
			String parms = urlparms.get("parms");
			String edittps = CorUtil.hashMap2Str(urlparms, "edittps", "需要参数edittps");
			String activeprocname = urlparms.get("activeprocname");

			HashMap<String, String> edts = CJSON.Json2HashMap(edittps);

			String smax = urlparms.get("max");
			String order = urlparms.get("order");

			String spage = urlparms.get("page");
			String srows = urlparms.get("rows");
			boolean needpage = false;// 是否需要分页
			int page = 0, rows = 0;
			if (spage == null) {
				if (smax == null) {
					needpage = false;
				} else {
					needpage = true;
					page = 1;
					rows = Integer.valueOf(smax);
				}
			} else {
				needpage = true;
				page = Integer.valueOf(spage);
				rows = (srows == null) ? 300 : Integer.valueOf(srows);
			}

			CJPALineData<CJPABase> jpas = CjpaUtil.newJPALinedatas(jpaclass);
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);

			List<JSONParm> jps = CJSON.getParms(parms);
			String where = CjpaUtil.buildFindSqlByJsonParms(jpa, jps);
			// if (dfi && chgi && (userIsInside()) && (jpa.cfield("idpath") !=
			// null)) {
			// where = where + " and idpath like '1,%'";
			// } else if (dfi && )
			if ((jpa.cfield("idpath") != null) && (!disi))
				where = where + CSContext.getIdpathwhere();
			if ((sqlwhere != null) && (sqlwhere.length() > 0))
				where = where + " and " + sqlwhere + " ";

			if (jpa.getPublicControllerBase() != null) {
				String w = ((JPAController) jpa.getPublicControllerBase()).OnCCoFindBuildWhere(jpa, urlparms);
				if (w != null)
					where = where + " " + w;
			}

			if (jpa.getController() != null) {
				String w = ((JPAController) jpa.getController()).OnCCoFindBuildWhere(jpa, urlparms);
				if (w != null)
					where = where + " " + w;
			}

			// edittps:{"isedit":true,"issubmit":true,"isview":true,"isupdate":false,"isfind":true}
			if (jpa.cfieldbycfieldname("stat") != null) {
				String sqlstat = "";
				if (Obj2Bl(edts.get("isedit")))
					sqlstat = sqlstat + " (stat=1) or";
				if (Obj2Bl(edts.get("issubmit"))) {
					sqlstat = sqlstat + " (stat>1 and stat<9) or";
					// 去掉 在流程中、当前节点为数据保护节点 且 当前 登录用户不在 当前节点
				}
				if (Obj2Bl(edts.get("isview"))) // 查询所有单据 包含作废的
					sqlstat = sqlstat + " ( 1=1 ) or";
				if (Obj2Bl(edts.get("isupdate")) || Obj2Bl(edts.get("isfind")))
					sqlstat = sqlstat + " (stat=9) or";
				if (sqlstat.length() > 0) {
					sqlstat = sqlstat.substring(1, sqlstat.length() - 2);
					where = where + " and (" + sqlstat + ")";
				}
			}
			if ((activeprocname != null) && (!activeprocname.isEmpty())) {
				String idfd = jpa.getIDField().getFieldname();
				String ew = "SELECT " + idfd + " FROM " + jpa.tablename + " t,shwwf wf,shwwfproc wfp"
						+ " WHERE t.stat>1 AND t.stat<9 AND t.wfid=wf.wfid AND wf.wfid=wfp.wfid "
						+ "  AND wfp.stat=2 AND wfp.procname='" + activeprocname + "'";
				ew = " and " + idfd + " in (" + ew + ")";
				where = where + ew;
			}
			String sqltr = null;

			String textfield = urlparms.get("textfield");
			String pidfd = null;
			if ("tree".equalsIgnoreCase(type))
				pidfd = CorUtil.hashMap2Str(urlparms, "parentid", "需要参数parentid");

			if (("tree".equalsIgnoreCase(type)) && (textfield != null) && (textfield.length() > 0)) {
				String idfd = jpa.getIDField().getFieldname();
				sqltr = "select " + idfd + " as id," + textfield +
						" as text," + idfd + "," + textfield + "," + pidfd
						+ ",a.* from " + jpa.tablename + " a where 1=1 " + where;
			} else
				sqltr = "select * from " + jpa.tablename + " where 1=1 " + where;
			if ((order != null) && (!order.isEmpty())) {
				sqltr = sqltr + " order by " + order;
			} else
				sqltr = sqltr + " order by " + jpa.getIDFieldName() + " desc ";
			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps, edts, disi, selfline);
				if (rst != null)
					return rst;
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps, edts, disi, selfline);
				if (rst != null)
					return rst;
			}
			if ("list".equalsIgnoreCase(type)) {
				String scols = urlparms.get("cols");
				if (scols != null) {
					String[] ignParms = new String[] {};
					new CReport(sqltr, null).export2excel(ignParms, scols);
					return null;
				} else {
					if (!needpage) {
						JSONArray js = jpa.pool.opensql2json_O(sqltr);
						if (jpa.getController() != null) {
							String rst = ((JPAController) jpa.getController()).AfterCOFindList((Class<CJPA>) jpa.getClass(), js, 0, 0);
							if (rst != null)
								return rst;
						}
						return js.toString();
					} else {
						JSONObject jo = jpa.pool.opensql2json_O(sqltr, page, rows);
						if (jpa.getController() != null) {
							String rst = ((JPAController) jpa.getController()).AfterCOFindList((Class<CJPA>) jpa.getClass(), jo, page, rows);
							if (rst != null)
								return rst;
						}
						return jo.toString();
					}
				}
			}
			if ("tree".equalsIgnoreCase(type)) {
				return jpa.pool.opensql2jsontree(sqltr, jpa.getIDField().getFieldname(), pidfd, false);
			}
		}
		// //by id
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
				if (rst != null)
					return rst;
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
				if (rst != null)
					return rst;
			}

			CField idfd = jpa.getIDField();
			if (idfd == null) {
				throw new Exception("根据ID查询JPA<" + jpa.getClass().getSimpleName() + ">数据没发现ID字段");
			}
			String sqlfdname = CJPASqlUtil.getSqlField(jpa.pool.getDbtype(), idfd.getFieldname());
			String sqlvalue = CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), idfd.getFieldtype(), id);
			String sqlstr = "select * from " + jpa.tablename + " where " + sqlfdname + "=" + sqlvalue;
			// if ((chgi) && (userIsInside())) {
			// sqlstr = sqlstr + " and idpath like '1,%'";
			// } else if (dfi && (jpa.cfield("idpath") != null))
			// sqlstr = sqlstr + CSContext.getIdpathwhere();
			jpa.findBySQL(sqlstr, selfline);
			if (jpa.isEmpty())
				return "{}";
			else
				return jpa.tojson();
		}
		return "";
	}

	@ACOAction(eventname = "save", Authentication = true, notes = "公用保存", ispublic = true)
	public String save() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();
		Logsw.dblog("save:"+CSContext.getPostdata());
		String jpaclass = pparms.get("jpaclass").trim();
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("保存数据，需要jpaclass参数");
		String jpadata = pparms.get("jpadata");
		Logsw.dblog("save提交的数据："+jpadata.toString());
		if ((jpadata == null) || jpadata.isEmpty())
			throw new Exception("保存数据，需要jpadata参数");

		CDBConnection con = DBPools.defaultPool().getCon(jpaclass + " COCSCommon.save");
		con.startTrans();
		try {
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		
			jpa.fromjson(jpadata);
			// System.out.println("JPA JSON:" + jpa.tojson());
			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoSave(con, jpa);
				if (rst != null) {
					con.submit();
					return rst;
				}
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).OnCCoSave(con, jpa);
				if (rst != null) {
					con.submit();
					return rst;
				}
			}
			String lines = pparms.get("lines");
			boolean selfLink = ((lines == null) || lines.isEmpty()) ? true : Boolean.valueOf(lines);
			jpa.save(con, selfLink);// 事物处理的保存 需要调用 applayChange方法
			jpa.applayChange();

			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).AfterCCoSave(con, jpa);
				if (rst != null) {
					con.submit();
					return rst;
				}
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).AfterCCoSave(con, jpa);
				if (rst != null) {
					con.submit();
					return rst;
				}
			}
			con.submit();
			return ((CJPAJSON) jpa).tojson();
		} catch (Exception e) {
			con.rollback();
			System.out.println("通用保存错误:" + jpaclass);
			throw e;
		}finally{
			con.close();
		}
	}

	@SuppressWarnings("unchecked")
	@ACOAction(eventname = "copy", Authentication = true, notes = "复制单据", ispublic = true)
	public String copy() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();

		String jpaclass = pparms.get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");

		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass.trim());

		if (jpa.getPublicControllerBase() != null) {
			String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoCopy((Class<CJPA>) jpa.getClass(), pparms);
			if (rst != null)
				return rst;
		}
		if (jpa.getController() != null) {
			String rst = ((JPAController) jpa.getController()).OnCCoCopy((Class<CJPA>) jpa.getClass(), pparms);
			if (rst != null)
				return rst;
		}

		String id = pparms.get("id");
		if ((id == null) || id.isEmpty())
			throw new Exception("需要id参数");

		String[] cfs = null;
		String clearfields = pparms.get("clearfields");
		if ((clearfields != null) && !clearfields.isEmpty()) {
			cfs = clearfields.trim().split(",");
		}

		jpa.findByID(id.trim());
		jpa.clearAllId();
		if (jpa.cfieldbycfieldname("stat") != null) {
			jpa.cfieldbycfieldname("stat").setAsInt(1);
		}
		if (cfs != null)
			for (String fd : cfs) {
				CField cfd = jpa.cfieldbycfieldname(fd);
				if (cfd != null) {
					cfd.setValue(null);
				} else {
					new Exception("【" + jpaclass + "】没发现字段【" + fd + "】");
				}
			}
		jpa.setJpaStat(CJPAStat.RSINSERT);
		return ((CJPAJSON) jpa.save()).tojson();
	}

	@ACOAction(eventname = "delete", Authentication = true, notes = "公用删除", ispublic = true)
	public String delete() throws Exception {
		String jpaclass = CSContext.getParms().get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");

		String id = CSContext.getParms().get("id");
		if ((id == null) || id.isEmpty())
			throw new Exception("需要id参数");

		CDBConnection con = DBPools.defaultPool().getCon(jpaclass + " COCSCommon.delete");
		con.startTrans();
		try {
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoDel(con, (Class<CJPA>) jpa.getClass(), id);
				if (rst != null) {
					con.submit();
					return rst;
				}
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).OnCCoDel(con, (Class<CJPA>) jpa.getClass(), id);
				if (rst != null) {
					con.submit();
					return rst;
				}
			}

			jpa.findByID(id, false);
			CField stat = jpa.cfield("stat");
			if ((stat != null) && (stat.getAsIntDefault(0) > 1)) {
				throw new Exception("非制单状态表单不允许删除");
			}

			jpa.setid(id);
			jpa.delete(con, true);
			// System.out.println("do delete!!!!!!!!!!!!!!!!!!!!!!!");
			con.submit();
			return "{\"result\":\"OK\"}";
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	@ACOAction(eventname = "dovoid", Authentication = true, notes = "公用作废", ispublic = true)
	public String dovoid() throws Exception {
		String jpaclass = CSContext.getParms().get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");

		String id = CSContext.getParms().get("id");
		if ((id == null) || id.isEmpty())
			throw new Exception("需要id参数");

		CDBConnection con = DBPools.defaultPool().getCon(jpaclass + " COCSCommon.delete");
		con.startTrans();
		try {
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
			jpa.findByID4Update(con, id, true);
			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoVoid(con, jpa);
				if (rst != null) {
					con.submit();
					return rst;
				}
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).OnCCoVoid(con, jpa);
				if (rst != null) {
					con.submit();
					return rst;
				}
			}
			CField statfd = jpa.cfield("stat");
			if (statfd != null) {
				statfd.setAsInt(12);
			}
			jpa.save(con);
			con.submit();
			return jpa.tojson();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	@ACOAction(eventname = "findWfTemps", Authentication = true, notes = "获取流程模板", ispublic = true)
	public String findWfTemps() throws Exception {
		String jpaclass = CSContext.getParms().get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");

		String id = CSContext.getParms().get("id");
		if ((id == null) || id.isEmpty())
			throw new Exception("需要id参数");
		return FindWfTemp.findwftemp(jpaclass, id);
	}

	@SuppressWarnings("unchecked")
	@ACOAction(eventname = "createWF", Authentication = true, notes = "启动流程", ispublic = true)
	public String createWF() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();
		Logsw.dblog("createWF:"+CSContext.getPostdata());
		String jpaclass = pparms.get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");

		String wftempid = pparms.get("wftempid");
	
		String jpaid = pparms.get("jpaid");
		if ((jpaid == null) || jpaid.isEmpty())
			throw new Exception("需要jpaid参数");
		//2020-09-08 没有流程模板时自行添加
		if ((wftempid == null) || wftempid.isEmpty()) {
			wftempid=FindWfTemp.findwftempid(jpaclass,jpaid);
		}
		String userids = CorUtil.hashMap2Str(pparms, "userids");
		String[] ckuserids = ((userids != null) && (userids.length() > 0)) ? userids.split(",") : null;

		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
	
		jpa.findByID(jpaid);

		if (jpa.getPublicControllerBase() != null) {
			String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoCreateWF(jpa, wftempid, jpaid);
			if (rst != null)
				return rst;
		}

		if (jpa.getController() != null) {
			String rst = ((JPAController) jpa.getController()).OnCCoCreateWF(jpa, wftempid, jpaid);
			if (rst != null)
				return rst;
		}
		// //////////
		// jpa.setWflistener(wflistener)
		try {
			jpa.wfcreate(wftempid, ckuserids);
			return jpa.tojson();
		} catch (CWFException e) {
			Logsw.dblog("流程出错："+e.getErrcode());
			if (e.getErrcode() == CWFException.ERR_NEED_SELECT_USER) {
				Object userobj = e.getUserobj();
				if (userobj != null) {
					Shwwfproc proc = (Shwwfproc) userobj;
					JSONObject o = new JSONObject();
					o.put("type", "procuser");
					o.put("procid", e.getInfocode());
					o.put("users", proc.shwwfprocusers.tojson());
					return o.toString();
				} else
					throw e;
			} else
				throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 移动端创建工作流
	 * @param jpaclass 表单类
	 * @param jpaid  表单id
	 * @param userid 用户id
	 * @param entid 组织id
	 * @return
	 * @throws Exception
	 */
	public static String createWFForMobile(String jpaclass,String jpaid,String userid,String entid,String[] ckuserids) throws Exception {
		//HashMap<String, String> pparms = CSContext.parPostDataParms();
		String wftempid = null;
		//2020-09-08 没有流程模板时自行添加
		if ((wftempid == null) || wftempid.isEmpty()) {
			wftempid=FindWfTemp.findwftempid(jpaclass,jpaid);
		}
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
	
		jpa.findByID(jpaid);

		if (jpa.getPublicControllerBase() != null) {
			String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoCreateWF(jpa, wftempid, jpaid);
			if (rst != null)
				return rst;
		}

		if (jpa.getController() != null) {
			String rst = ((JPAController) jpa.getController()).OnCCoCreateWF(jpa, wftempid, jpaid);
			if (rst != null)
				return rst;
		}
		// //////////
		// jpa.setWflistener(wflistener)
		try {
			jpa.wfcreateForMobile(wftempid,userid,entid, ckuserids);
			return jpa.tojson();
		} catch (CWFException e) {
			//弹出流程节点，多选
			if (e.getErrcode() == CWFException.ERR_NEED_SELECT_USER) {
				Object userobj = e.getUserobj();
				if (userobj != null) {
					Shwwfproc proc = (Shwwfproc) userobj;
					JSONObject o = new JSONObject();
					o.put("jpaid", jpaid);
					o.put("users", proc.shwwfprocusers.tojson());
					return o.toString();
				} else
					throw e;
			} else
				throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@ACOAction(eventname = "unsubmitWF", Authentication = true, notes = "反审核", ispublic = true)
	public String unsubmitWF() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();
		String jpaclass = CorUtil.hashMap2Str(pparms, "jpaclass", "需要参数jpaclass");
		String jpaid = CorUtil.hashMap2Str(pparms, "jpaid", "需要参数jpaid");
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		CDBConnection con = jpa.pool.getCon(this);
		con.startTrans();// 开始事务
		try {
			jpa.findByID4Update(con, jpaid, true); // 锁定业务单据
			CField StatField = jpa.cfield("stat");
			if (StatField == null)
				throw new Exception("对象没有找到stat字段，不允许反审核!");
			if (StatField.getAsIntDefault(0) != 9)
				throw new Exception("只有完成状态的对象，才允许反审核");

			if (jpa.getPublicControllerBase() != null) {
				String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoUnSubmitWF(con, jpa);
				if (rst != null)
					return rst;
			}
			if (jpa.getController() != null) {
				String rst = ((JPAController) jpa.getController()).OnCCoUnSubmitWF(con, jpa);
				if (rst != null)
					return rst;
			}
			StatField.setAsInt(1);
			jpa.save(con);
			con.submit();
			return jpa.tojson();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	@SuppressWarnings("unchecked")
	@ACOAction(eventname = "submitWF", Authentication = true, notes = "提交流程", ispublic = true)
	public String submitWF() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();

		String jpaclass = CorUtil.hashMap2Str(pparms, "jpaclass", "需要参数jpaclass");
		String jpaid = CorUtil.hashMap2Str(pparms, "jpaid", "需要参数jpaid");
		String aoption = CorUtil.hashMap2Str(pparms, "aoption", "需要参数aoption");
		String procid = CorUtil.hashMap2Str(pparms, "procid", "需要参数procid");
		String userids = CorUtil.hashMap2Str(pparms, "userids");
		String[] ckuserids = ((userids != null) && (userids.length() > 0)) ? userids.split(",") : null;

		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		jpa.findByID(jpaid);

		if (jpa.getPublicControllerBase() != null) {
			String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoSubmitWF(jpa, procid, aoption);
			if (rst != null)
				return rst;
		}

		if (jpa.getController() != null) {
			String rst = ((JPAController) jpa.getController()).OnCCoSubmitWF(jpa, procid, aoption);
			if (rst != null)
				return rst;
		}

		try {
			jpa.wfsubmit(procid, aoption, ckuserids);
			return jpa.tojson();
		} catch (CWFException e) {
			if ((e.getErrcode() == CWFException.ERR_NEED_SELECT_USER) || (e.getErrcode() == CWFException.ERR_NEED_SELECT_ALLUSER)) {
				Object userobj = e.getUserobj();
				if (userobj != null) {
					Shwwfproc proc = (Shwwfproc) userobj;
					JSONObject o = new JSONObject();
					o.put("type", "procuser");
					o.put("users", proc.shwwfprocusers.tojson());
					return o.toString();
				} else
					throw e;
			} else
				throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@ACOAction(eventname = "rejectWF", Authentication = true, notes = "驳回流程", ispublic = true)
	public String rejectWF() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();
		String jpaclass = pparms.get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");

		String jpaid = pparms.get("jpaid");
		if ((jpaid == null) || jpaid.isEmpty())
			throw new Exception("需要jpaid参数");

		String aoption = pparms.get("aoption");
		if ((aoption == null) || aoption.isEmpty())
			throw new Exception("需要aoption参数");

		String fprocid = pparms.get("fprocid");
		if ((fprocid == null) || fprocid.isEmpty())
			throw new Exception("需要fprocid参数");

		String tprocid = pparms.get("tprocid");
		if ((tprocid == null) || tprocid.isEmpty())
			throw new Exception("需要tprocid参数");
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		jpa.findByID(jpaid);

		if (jpa.getPublicControllerBase() != null) {
			String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoRejectWF(jpa, fprocid, tprocid, aoption);
			if (rst != null)
				return rst;
		}

		if (jpa.getController() != null) {
			String rst = ((JPAController) jpa.getController()).OnCCoRejectWF(jpa, fprocid, tprocid, aoption);
			if (rst != null)
				return rst;
		}
		jpa.wfreject(fprocid, tprocid, aoption);
		return jpa.tojson();
	}

	@ACOAction(eventname = "breakWF", Authentication = true, notes = "中断流程", ispublic = true)
	public String breakWF() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();
		String jpaclass = pparms.get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");

		String jpaid = pparms.get("jpaid");
		if ((jpaid == null) || jpaid.isEmpty())
			throw new Exception("需要jpaid参数");
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		jpa.findByID(jpaid);

		if (jpa.getPublicControllerBase() != null) {
			String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoBreakWF(jpa);
			if (rst != null)
				return rst;
		}

		if (jpa.getController() != null) {
			String rst = ((JPAController) jpa.getController()).OnCCoBreakWF(jpa);
			if (rst != null)
				return rst;
		}
		return jpa.wfbreak().tojson();
	}

	@ACOAction(eventname = "transferWF", Authentication = true, notes = "转办流程", ispublic = true)
	public String transferWF() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();
		String jpaclass = pparms.get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");

		String jpaid = pparms.get("jpaid");
		if ((jpaid == null) || jpaid.isEmpty())
			throw new Exception("需要jpaid参数");

		String aoption = pparms.get("aoption");
		if ((aoption == null) || aoption.isEmpty())
			throw new Exception("需要aoption参数");

		String tuserid = pparms.get("tuserid");
		if ((tuserid == null) || tuserid.isEmpty())
			throw new Exception("需要tuserid参数");
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		jpa.findByID(jpaid);

		if (jpa.getPublicControllerBase() != null) {
			String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoTransferWF(jpa, tuserid, aoption);
			if (rst != null)
				return rst;
		}

		if (jpa.getController() != null) {
			String rst = ((JPAController) jpa.getController()).OnCCoTransferWF(jpa, tuserid, aoption);
			if (rst != null)
				return rst;
		}
		return jpa.wftransfer(tuserid, aoption).tojson();
	}

	@ACOAction(eventname = "findModels", Authentication = true, notes = "查找Excel模板", ispublic = true)
	public String findModels() throws Exception {
		String jpaclass = CSContext.getParms().get("jpaclass");
		if ((jpaclass == null) || jpaclass.isEmpty())
			throw new Exception("需要jpaclass参数");
		ArrayList<String[]> filelist = new ArrayList<String[]>();
		jpaclass = jpaclass.substring(jpaclass.lastIndexOf(".") + 1);
		String path = ConstsSw.excelModelPath + jpaclass;
		CExcel.getFilesInfo(path, filelist, false);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartArray();
		for (String[] item : filelist) {
			jg.writeStartObject();
			jg.writeStringField("fname", item[0]);
			jg.writeStringField("size", item[1]);
			jg.writeStringField("lastupdate", item[2]);
			jg.writeEndObject();
		}
		jg.writeEndArray();
		jg.close();
		return baos.toString("utf-8");
	}

	@ACOAction(eventname = "dowloadExcelByModel", Authentication = true, notes = "导出Excel", ispublic = true)
	public void dowloadExcelByModel() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String jpaclass = CorUtil.hashMap2Str(parms, "jpaclass", "需要参数jpaclass");
		String modfilename = CorUtil.hashMap2Str(parms, "modfilename", "需要参数modfilename");
		int tp = Integer.valueOf(CorUtil.hashMap2Str(parms, "tp", "需要参数tp"));
		// 1 print 2 export
		String jpaids = CorUtil.hashMap2Str(parms, "jpaids", "需要参数jpaids");

		String fields = CorUtil.hashMap2Str(parms, "fields");
		JSONObject flds = ((fields != null) && (!fields.isEmpty())) ? JSONObject.fromObject(fields) : null;

		HttpServletResponse rsp = CSContext.getResponse();
		rsp.setContentType("application/x-xls");
		rsp.setHeader("content-disposition", "attachment; filename=" + new String(modfilename.getBytes("GBK"), "ISO_8859_1"));
		OutputStream os = rsp.getOutputStream();
		try {
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
			Class<?> CJPAcd = Class.forName(jpaclass);
			CJPALineData<CJPABase> jpas = new CJPALineData<CJPABase>(CJPAcd);
			String sqlstr = "select * from " + jpa.tablename + " where " + jpa.getIDFieldName() + " in (" + jpaids + ")";
			jpas.findDataBySQL(sqlstr);
			CExcelEx.expJPASByModel(jpas, modfilename, os, tp, flds);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			os.flush();
			os.close();
		}
	}

	// 导出 导入模板
	@ACOAction(eventname = "exportExcelModel", Authentication = true, notes = "导出导入模板", ispublic = true)
	public void exportExcelModel() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String jpaclass = CorUtil.hashMap2Str(parms, "jpaclass", "需要参数jpaclass");
		int tp = Integer.valueOf(CorUtil.hashMap2Str(parms, "tp", "需要参数tp"));
		if (tp == 1) {
			String fname = "导出导入模板.xls";
			CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
			String fsep = System.getProperty("file.separator");
			String fullname = ConstsSw.excelModelPath + jpa.getClass().getSimpleName() + fsep + "excelmodel" + fsep + fname;
			if (!(new File(fullname)).exists())
				throw new Exception("文件【" + fullname + "不存在!");
			HttpServletResponse rsp = CSContext.getResponse();
			rsp.setContentType(getContentType(fullname));
			rsp.setHeader("content-disposition", "attachment; filename=" + new String(fname.getBytes("GB2312"), "ISO_8859_1"));
			OutputStream os = rsp.getOutputStream();
			FileInputStream fis = new FileInputStream(fullname);
			try {
				byte[] b = new byte[1024];
				int i = 0;
				while ((i = fis.read(b)) > 0) {
					os.write(b, 0, i);
				}
				fis.close();
				os.flush();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				fis.close();
				os.flush();
				os.close();
			}
		}
	}

	@ACOAction(eventname = "getColist", Authentication = false, notes = "导出Co列表", ispublic = true)
	public String getColist() throws Exception {
		HashMap<String, Object> cos = ConstsSw._allCoClassName;
		List<HashMap<String, String>> jcos = new ArrayList<HashMap<String, String>>();
		Iterator<Entry<String, Object>> mapite = cos.entrySet().iterator();
		int idx = 1;
		while (mapite.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry co = (Map.Entry) mapite.next();
			String cokey = co.getKey().toString();
			COAcitonItem coitem = (COAcitonItem) co.getValue();
			HashMap<String, String> jco = new HashMap<String, String>();
			jco.put("index", String.valueOf(idx++));
			jco.put("name", cokey);
			jco.put("method", coitem.getClassname() + "." + coitem.getMethodname());
			jco.put("auth", String.valueOf(coitem.getCce().Authentication()));
			jco.put("note", coitem.getCce().notes());
			jcos.add(jco);
		}
		return CJSON.List2JSON(jcos);
	}

	@ACOAction(eventname = "getCosByMenu", Authentication = false, notes = "获取菜单的Cos", ispublic = true)
	public String getCosByMenu() throws Exception {
		String menuid = CSContext.getParms().get("menuid");
		if ((menuid == null) || menuid.isEmpty())
			throw new Exception("需要menuid参数");
		List<HashMap<String, String>> mcos = DBPools.defaultPool().openSql2List("select coname from shwmenuco where menuid=" + menuid);
		return CJSON.List2JSON(mcos);
	}

	@ACOAction(eventname = "getCoTreeMenu", Authentication = false, notes = "导出菜单Co树", ispublic = true)
	public String getCoTreeMenu() throws Exception {
		String menuid = CSContext.getParms().get("menuid");
		if ((menuid == null) || menuid.isEmpty())
			throw new Exception("需要menuid参数");

		List<HashMap<String, String>> mcos = DBPools.defaultPool().openSql2List("select * from shwmenuco where menuid=" + menuid);

		List<HashMap<String, String>> jcos = createCoList();
		List<HashMap<String, String>> jcohs = new ArrayList<HashMap<String, String>>();
		for (HashMap<String, String> jco : jcos) {
			jco.put("checked", String.valueOf(isexitsco(mcos, jco.get("name").toString())));
			if (!hasKey(jcohs, "key1", jco.get("key1").toString())) {
				if (!"web.common".equalsIgnoreCase(jco.get("key1").toString())) {
					// System.out.println(jco.get("key1").toString());
					jcohs.add(jco);
				}
			}
		}
		return wirteJsonTree(jcohs, jcos);
	}

	private boolean isexitsco(List<HashMap<String, String>> mcos, String coname) {
		if ((coname == null) || (coname.isEmpty()))
			return false;
		for (HashMap<String, String> mco : mcos) {
			if (mco.get("coname").toString().equalsIgnoreCase(coname))
				return true;
		}
		return false;
	}

	private List<HashMap<String, String>> createCoList() {
		HashMap<String, Object> cos = ConstsSw._allCoClassName;
		ListHashMap jcos = new ListHashMap();
		Iterator<Entry<String, Object>> mapite = cos.entrySet().iterator();
		while (mapite.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry co = (Map.Entry) mapite.next();
			String cokey = co.getKey().toString();// web.maintenance.findhis
			int idx = cokey.lastIndexOf(".");
			String key1 = cokey.substring(0, idx);
			String key2 = cokey.substring(idx + 1);
			COAcitonItem coitem = (COAcitonItem) co.getValue();
			if (!coitem.getCce().ispublic()) {
				HashMap<String, String> jco = new HashMap<String, String>();
				jco.put("name", cokey);
				jco.put("key1", key1);
				jco.put("key2", key2);
				jco.put("method", coitem.getClassname() + "." + coitem.getMethodname());
				jco.put("auth", String.valueOf(coitem.getCce().Authentication()));
				jco.put("note", coitem.getCce().notes());
				jcos.add(jco);
			}
		}
		jcos.Sort("key1", "asc");
		return jcos;
	}

	private String wirteJsonTree(List<HashMap<String, String>> jcohs, List<HashMap<String, String>> jcos) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartArray();
		for (HashMap<String, String> jcoh : jcohs) {
			jg.writeStartObject();
			// putjgfields(jg, jcoh);
			jg.writeStringField("text", jcoh.get("key1"));
			jg.writeStringField("state", "closed");
			jg.writeArrayFieldStart("children");
			for (HashMap<String, String> jco : jcos) {
				if (jco.get("key1").toString().equals(jcoh.get("key1").toString())) {
					jg.writeStartObject();
					putjgfields(jg, jco);
					jg.writeStringField("text", jco.get("key2"));
					jg.writeEndObject();
				}
			}
			jg.writeEndArray();
			jg.writeEndObject();
		}
		jg.writeEndArray();
		jg.close();
		return baos.toString("utf-8");
	}

	private void putjgfields(JsonGenerator jg, HashMap<String, String> jco) throws Exception {
		Iterator<Entry<String, String>> iter = jco.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			if ((!"key1".equals(key)) && (!"key2".equals(key))) {
				if (key.equalsIgnoreCase("checked"))
					jg.writeBooleanField(entry.getKey(), Boolean.valueOf(entry.getValue()));
				else
					jg.writeStringField(entry.getKey(), entry.getValue());
			}
		}
	}

	private boolean hasKey(List<HashMap<String, String>> list, String key, String value) {
		for (HashMap<String, String> item : list) {
			if (item.containsKey(key))
				if (item.get(key).toString().equalsIgnoreCase(value))
					return true;
		}
		return false;
	}

	@ACOAction(eventname = "getssparms", notes = "获取系统参数", Authentication = true)
	public String getctparms() throws Exception {
		String parmname = CorUtil.hashMap2Str(CSContext.getParms(), "parmname");
		String sqlstr = "select * from shwsystemparms where useable=1 and edtable=1 ";
		if ((parmname != null) && (!parmname.isEmpty()))
			sqlstr = sqlstr + " and parmname='" + parmname + "' ";
		sqlstr = sqlstr + " order by sspid";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getSystemInfo", ispublic = true, notes = "公开获取系统参数", Authentication = false)
	public String getSystemInfo() throws Exception {
		String parmname = CorUtil.hashMap2Str(CSContext.getParms(), "parmname");
		String sqlstr = "select * from shwsystemparms where useable=1 ";
		if ((parmname != null) && (!parmname.isEmpty()))
			sqlstr = sqlstr + " and parmname='" + parmname + "' ";
		sqlstr = sqlstr + " order by sspid";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "savessparms", notes = "保存系统参数", Authentication = true)
	public String savectparms() throws Exception {
		Shwsystemparms sp = new Shwsystemparms();
		sp.fromjson(CSContext.getPostdata());
		return ((CJPAJSON) sp.save()).tojson();
	}

	// http://127.0.0.1:8080/csm/web/common/getcheckcode.co
	@ACOAction(eventname = "getcheckcode", notes = "获取验证码", Authentication = false)
	public String getcheckcode() throws Exception {
		new PictureCheckCode().resCheckCode();
		return null;
	}

	@ACOAction(eventname = "getDBDatetime", notes = "获取数据库时间", Authentication = false)
	public String getDBDatetime() throws Exception {
		CDBPool pool = DBPools.defaultPool();
		DBType dtp = pool.getDbtype();
		String sqlstr = null;
		switch (dtp) {
		case mysql:
			sqlstr = "select now() dt";
			break;
		case oracle:
			sqlstr = "SELECT SYSDATE dt FROM dual";
			break;
		case sqlserver:
			sqlstr = "SELECT SYSDATE dt FROM dual";
			break;
		default:
			throw new Exception("暂时不支持的数据库类型【" + dtp.toString() + "】");
		}
		Date dt = Systemdate.getDateByStr(pool.openSql2List(sqlstr).get(0).get("dt"));
		JSONObject jo = new JSONObject();
		jo.put("rst", Systemdate.getStrDate(dt));
		return jo.toString();
	}

	@ACOAction(eventname = "sendDemoMail", notes = "发送测试邮件", Authentication = true)
	public String sendDemoMail() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String mailto = CorUtil.hashMap2Str(parms, "mailto", "需要参数mailto");
		String mailacc = CorUtil.hashMap2Str(parms, "mailacc");
		String subject = CorUtil.hashMap2Str(parms, "subject");
		String content = CorUtil.hashMap2Str(parms, "content", "需要参数content");
		CMailInfo mi = new CMailInfo();
		String[] ms = mailto.split(";");
		for (String m : ms) {
			mi.addToMail(m);
		}
		if ((mailacc != null) && (!mailacc.isEmpty())) {
			ms = mailacc.split(";");
			for (String m : ms) {
				mi.addCCMail(m);
			}
		}
		mi.setContent(content);
		if ((subject == null) || (subject.isEmpty())) {
			mi.setSubject("空标题");
		} else
			mi.setSubject(subject);
		CMail.sendMail(mi);
		JSONObject jo = new JSONObject();
		jo.put("rst", "OK");
		return jo.toString();
	}

	@ACOAction(eventname = "heartbeat", notes = "客户端心跳保持", Authentication = false)
	public String heartbeat() throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("rst", "OK");
		return jo.toString();
	}

	@ACOAction(eventname = "getnewcode", notes = "获取新编码", Authentication = true, ispublic = true)
	public String getnewcode() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String jpaclass = CorUtil.hashMap2Str(parms, "jpaclass");
		String codeid = CorUtil.hashMap2Str(parms, "codeid", "需要参数codeid");
		CJPA jpa = ((jpaclass == null) || (jpaclass.isEmpty())) ? null : (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		String code = (new GetNewSystemCode()).dogetnewsyscode(jpa, codeid);
		JSONObject jo = new JSONObject();
		jo.put("code", code);
		return jo.toString();
	}

	@ACOAction(eventname = "getDataURLImg", notes = "获取toDataURL数据生成图片", Authentication = true, ispublic = true)
	public void getDataURLImg() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String data = CorUtil.hashMap2Str(parms, "data", "需要参数data");
		String img64 = data.substring("data:image/jpeg;base64,".length());
		byte[] k = Base64.decodeBase64(img64);
		//System.out.println("k.length:" + k.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(k);
		//System.out.println("bais.available():" + bais.available());
		BufferedImage image = ImageIO.read(bais);
		//System.out.println("bi1:" + image);
		HttpServletResponse rsp = CSContext.getResponse();
		OutputStream os = rsp.getOutputStream();
		rsp.setContentType("image/jpeg");
		String fileName = UUID.randomUUID().toString() + ".jpg";
		rsp.setHeader("content-disposition", "attachment; filename=" + new String(fileName.getBytes("GB2312"), "ISO_8859_1"));
		try {
			image.flush();
			ImageIO.write(image, "jpg", os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			os.flush();
			os.close();
		}
	}

	public static byte[] decodeBase64(String input) throws Exception {
		Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
		Method mainMethod = clazz.getMethod("decode", String.class);
		mainMethod.setAccessible(true);
		Object retObj = mainMethod.invoke(null, input);
		return (byte[]) retObj;
	}

	@ACOAction(eventname = "getsessioncount", notes = "获取在线数量", Authentication = false, ispublic = true)
	public String getsessioncount() {
		JSONObject rst = new JSONObject();
		rst.put("sct", CorsairSessionListener.sessioncount);
		return rst.toString();
	}

	@ACOAction(eventname = "updatephysic_file", notes = "重新更新文件名", Authentication = true, ispublic = false)
	public String updatephysic_file() throws Exception {
		CJPALineData<Shw_physic_file> pfs = new CJPALineData<Shw_physic_file>(Shw_physic_file.class);
		pfs.findDataBySQL("select * from shw_physic_file where 1=1 ");
		for (CJPABase jpa : pfs) {
			Shw_physic_file pf = (Shw_physic_file) jpa;
			String fs = System.getProperty("file.separator");
			String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			String extfname = UpLoadFileEx.getExtention(fullname);
			File f = new File(fullname);
			if ((f.exists()) && (f.isFile())) {
				String nfname = "system_" + CorUtil.getShotuuid() + extfname;
				String nfullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + nfname;
				if (f.renameTo(new File(nfullname))) {
					pf.pfname.setValue(nfname);
					pf.save();
				}
			} else
				Logsw.debug("文件【" + fullname + "】不存在");
		}
		JSONObject rst = new JSONObject();
		rst.put("rst", "OK");
		return rst.toString();
	}

	/**
	 * str 需要转二维码的字符串
	 * extstr 显示到二维码下面的扩展字符串
	 * logopfid logo图片
	 * 
	 * @throws Exception
	 */
	@ACOAction(eventname = "getQrcodeImg", notes = "字符串转二维码", Authentication = false, ispublic = true)
	public void getQrcodeImg() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String str = CorUtil.hashMap2Str(parms, "str", "需要参数str");
		String extstr = CorUtil.hashMap2Str(parms, "extstr");
		String logopfid = CorUtil.hashMap2Str(parms, "logopfid");
		
		int w = (CorUtil.hashMap2Str(parms, "w") == null) ? 800 : Integer.valueOf(CorUtil.hashMap2Str(parms, "w"));
		int h = (CorUtil.hashMap2Str(parms, "h") == null) ? 800 : Integer.valueOf(CorUtil.hashMap2Str(parms, "h"));
		String lgfname = ((logopfid == null) || (logopfid.isEmpty())) ? null : UpLoadFileEx.getPhysicalFileName(logopfid);
		HttpServletResponse rsp = CSContext.getResponse();
		rsp.setContentType("image/jpeg");
		rsp.setDateHeader("expries", -1);
		rsp.setHeader("Cache-Control", "no-cache");
		rsp.setHeader("Pragma", "no-cache");
		OutputStream os = rsp.getOutputStream();
		System.out.println("lgfname:" + lgfname);
		MatrixUtil mu = new MatrixUtil().setText(str).setTextext(extstr).setQcwidth(w).setQcheight(h).setLogofilename(lgfname);
		mu.toQrcodeStream(os, "jpg");
		os.flush();
		os.close();
	}

	@ACOAction(eventname = "getJPAAttrsByCls", notes = "通过CLS获取扩展参数", Authentication = false, ispublic = true)
	public String getJPAAttrsByCls() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String jpaclassname = CorUtil.hashMap2Str(parms, "jpaclassname", "需要参数jpaclassname");
		String sqlstr = "select * from shwjpaattr where jpaclassname=?";
		Shwjpaattr ja = new Shwjpaattr();
		String[] vs = { jpaclassname };
		PraperedSql psql = new PraperedSql(sqlstr, vs);
		ja.findByPSQL(psql);
		if (ja.isEmpty())
			return CJSON.getErrJson("类【" + jpaclassname + "】不存在扩展属性配置信息");
		return ja.toString();
	}

	@ACOAction(eventname = "getJPAExportSchm", notes = "获取列表导出方案", Authentication = false, ispublic = true)
	public String getJPAExportSchm() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String jpaclass = CorUtil.hashMap2Str(parms, "jpaclass", "需要参数jpaclass");
		String userid = CSContext.getUserIDNoErr();
		String sqlstr = "SELECT * FROM shwjpaexportsche WHERE jpaclassname='" + jpaclass + "'"
				+ " AND (ispublic=1 ";
		if ((userid != null) && (!userid.isEmpty()))
			sqlstr = sqlstr + " or ownuserid=" + userid;
		sqlstr = sqlstr + " ) ORDER BY ispublic DESC,updatetime DESC";
		JSONArray rows = DBPools.defaultPool().opensql2json_O(sqlstr);
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			row.put("isown", 2);
			if (row.has("ownuserid")) {
				if (userid.equalsIgnoreCase(row.getString("ownuserid"))) {
					row.put("isown", 1);
				}
			}
		}
		return rows.toString();
	}

	@ACOAction(eventname = "saveJPAExportSchm", notes = "保存列表导出方案", Authentication = true, ispublic = true)
	public String saveJPAExportSchm() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String jpaclass = CorUtil.hashMap2Str(parms, "jpaclass", "需要参数jpaclass");
		String jpaname = CorUtil.hashMap2Str(parms, "jpaname", "需要参数jpaname");
		String fields = CorUtil.hashMap2Str(parms, "fields", "需要参数fields");
		String jesname = CorUtil.hashMap2Str(parms, "jesname", "需要参数jesname");
		int ispublic = Integer.valueOf(CorUtil.hashMap2Str(parms, "ispublic", "需要参数ispublic"));
		String userid = CSContext.getUserIDNoErr();
		Shwuser user = new Shwuser();
		user.findByID(userid, false);
		Shwjpaexportsche exschm = new Shwjpaexportsche();
		exschm.jesname.setValue(jesname); // 方案名称
		exschm.ispublic.setValue(ispublic); // 是否公有方案
		exschm.jpaclassname.setValue(jpaclass); // Class
		exschm.jpaname.setValue(jpaname); // JPA名称
		exschm.ownuserid.setValue(userid); // 用户ID
		exschm.ownusername.setValue(user.username.getValue()); // 用户名
		exschm.owndisplayname.setValue(user.displayname.getValue()); // 用户姓名
		exschm.expfields.setValue(fields); // 导出的字段 字符串数组
		exschm.save();
		return exschm.toString();
	}

	@ACOAction(eventname = "editJPAExportSchm", notes = "变更列表导出方案", Authentication = true, ispublic = true)
	public String editJPAExportSchm() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String jesid = CorUtil.hashMap2Str(parms, "jesid", "需要参数jesid");
		int edittype = Integer.valueOf(CorUtil.hashMap2Str(parms, "edittype", "需要参数edittype"));// 2 公开方案 3 私藏方案 4 删除方案
		Shwjpaexportsche exschm = new Shwjpaexportsche();
		exschm.findByID(jesid);
		if (exschm.isEmpty())
			throw new Exception("不存在ID为【" + jesid + "】的方案");
		if (!exschm.ownuserid.getValue().equalsIgnoreCase(CSContext.getUserIDNoErr())) {
			throw new Exception("不是您的方案不允许修改");
		}
		if (edittype == 2) {
			exschm.ispublic.setValue(1);
			exschm.save();
		} else if (edittype == 3) {
			exschm.ispublic.setValue(2);
			exschm.save();
		} else if (edittype == 4) {
			exschm.delete();
		}
		return CSContext.getJSON_OK();
	}

}
