package com.corsair.server.genco;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.ctrl.CtrShworg;
import com.corsair.server.ctrl.CtrlDoc;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shwfdr;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwposition;
import com.corsair.server.generic.Shwrole;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.generic.Shwuser_wf_agent;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CTreeUtil;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DesSw;
import com.corsair.server.util.UpLoadFileEx;

@ACO(coname = "web.user")
public class COShwUser {

	@ACOAction(eventname = "saveOrg", Authentication = true, notes = "保存机构")
	public String saveOrg() throws Exception {
		Shworg org = new Shworg();
		CDBConnection con = org.pool.getCon(this);
		con.startTrans();
		try {
			org.fromjson(CSContext.getPostdata());
			if (org.orgid.isEmpty()) {
				org.orgid.setValue(org.newid(con, 1));
				Shworg porg = new Shworg();
				porg.findByID(org.superid.getValue());
				if (porg.isEmpty())
					org.idpath.setValue(org.orgid.getValue() + ",");
				else
					org.idpath.setValue(porg.idpath.getValue() + org.orgid.getValue() + ",");
				org.setJpaStat(CJPAStat.RSINSERT);
			} else {
				org.setJpaStat(CJPAStat.RSUPDATED);
			}
			org.save();
			org.extorgname.setValue(getOrgNamepath(con, org.idpath.getValue()));
			org.orglevel.setAsInt(CTreeUtil.getLevel(org.idpath.getValue()));
			org.save(con);
			int usable = org.usable.getAsIntDefault(0);
			if (usable == 1) {
				// 设置所有上级机构可用
				setALLPOrgUsable(con, org, 1);
			}
			if (usable == 2) {
				// 设置所有下机机构不可用
				setALLChildOrgUsable(con, org, 2);
			}
			con.submit();
			return org.tojson();
		} catch (Exception e) {
			con.rollback();
			// TODO Auto-generated catch block
			throw e;
		}finally{
			con.close();
		}
	}

	private void setALLPOrgUsable(CDBConnection con, Shworg org, int usable) throws Exception {
		String idpath = org.idpath.getValue();
		String ids = idpath.substring(0, idpath.length() - 1);
		String sqlstr = "update shworg set usable=" + usable + ",updatetime=NOW() where orgid in(" + ids + ")";
		con.execsql(sqlstr);
	}

	/**
	 * 设置所有子机构是否可用
	 * 
	 * @param con
	 * @param org
	 * @param usable
	 * @throws Exception
	 */
	public static void setALLChildOrgUsable(CDBConnection con, Shworg org, int usable) throws Exception {
		String sqlstr = "update shworg set usable=" + usable + ",updatetime=NOW() where idpath like '" + org.idpath.getValue() + "%'";
		con.execsql(sqlstr);
	}

	@ACOAction(eventname = "delOrg", Authentication = true, notes = "删除机构")
	public String delOrg() throws Exception {
		String orgid = CSContext.getParms().get("id");
		if ((orgid == null) || orgid.isEmpty())
			throw new Exception("需要orgid参数");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");

		if (Integer.valueOf(org.pool.openSql2List("select count(*) ct from shworg where superid=" + orgid).get(0).get("ct")) > 0) {
			throw new Exception("【" + org.orgname.getValue() + "】包含子机构，不允许删除！");
		}
		if (Integer.valueOf(org.pool.openSql2List("select count(*) ct from shworguser where orgid=" + orgid).get(0).get("ct")) > 0) {
			throw new Exception("【" + org.orgname.getValue() + "】包含用户，不允许删除！");
		}
		// 检查所有单据

		CDBConnection con = org.pool.getCon(this);
		con.startTrans();
		try {
			ArrayList<String> sqls = new ArrayList<String>();
			sqls.add("DELETE FROM shwfdracl WHERE acltype IN(1,2) AND ownerid=" + orgid);
			sqls.add("DELETE FROM shworg_find WHERE orgid=" + orgid + " OR forgid=" + orgid);
			sqls.add("DELETE FROM shworguser WHERE orgid=" + orgid);
			org.pool.execSqls(con, sqls);
			org.delete(con, orgid, true);
			con.submit();
			return "{\"result\":\"OK\"}";
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}

	}

	@ACOAction(eventname = "delUser", Authentication = true, notes = "删除用户")
	public String delUser() throws Exception {
		String userid = CSContext.getParms().get("userid");
		if ((userid == null) || userid.isEmpty())
			throw new Exception("需要orgid参数");
		Shwuser user = (Shwuser) (new Shwuser().findByID(userid));
		ArrayList<String> sqls = new ArrayList<String>();
		sqls.add("delete from shworguser where userid=" + user.userid.getValue());// 删除用户机构
		sqls.add("delete from shwroleuser where userid=" + user.userid.getValue());// 删除用户角色
		sqls.add("delete from shwpositionuser where userid=" + user.userid.getValue());// 删除用户岗位
		sqls.add("delete from shwuserparms where userid=" + user.userid.getValue());// 删除用户参数
		sqls.add("delete from shwmenushortcut where userid=" + user.userid.getValue());// 删除快捷菜单
		sqls.add("delete from shwuser_wf_agent where userid=" + user.userid.getValue());// 删除出差代理
		// 删除个人文件夹 文件
		// 删除个人文件夹 文件权限
		sqls.add("delete from shwuser where userid=" + user.userid.getValue());// 删除用户
		user.pool.getCon(this).execSqls(sqls);
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "saveUser", Authentication = true, notes = "保存用户")
	public String saveUser() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(CSContext.getPostdata());
		boolean isnew = rootNode.path("isnew").asBoolean();
		JsonNode n_orgs = rootNode.path("orgs");
		JsonNode n_roles = rootNode.path("roles");
		JsonNode n_positions = rootNode.path("positions");
		String jsondata = rootNode.path("jsondata").toString();

		Shwuser user = (Shwuser) new Shwuser().fromjson(jsondata);
		CDBConnection con = user.pool.getCon(this);
		con.startTrans();
		try {
			user.save(con);
			if (isnew) {
				saveUserParms(con, user);// 保存用户参数
			}
			saveOrgs(con, n_orgs, user);
			saveRoles(con, n_roles, user);
			saveOptions(con, n_positions, user);
			con.submit();
			return user.tojson();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private void saveOrgs(CDBConnection con, JsonNode n_orgs, Shwuser user) throws Exception {
		con.execsql("delete from shworguser where userid=" + user.userid.getValue());
		for (int i = 0; i < n_orgs.size(); i++) {
			JsonNode orgn = n_orgs.get(i);
			String orgid = orgn.path("orgid").asText();
			String isdefault = orgn.path("isdefault").asText();
			String inheritrole = orgn.path("inheritrole").asText();
			con.execsql("insert into shworguser(orgid,isdefault,inheritrole,userid) values(" + orgid + "," + isdefault + "," + inheritrole + ","
					+ user.userid.getValue() + ")");
		}
	}

	private void saveRoles(CDBConnection con, JsonNode n_roles, Shwuser user) throws Exception {
		con.execsql("delete from shwroleuser where userid=" + user.userid.getValue());
		for (int i = 0; i < n_roles.size(); i++) {
			JsonNode roln = n_roles.get(i);
			String roleid = roln.path("roleid").asText();
			con.execsql("insert into shwroleuser (roleid,userid) values(" + roleid + "," + user.userid.getValue() + ")");
		}
	}

	private void saveOptions(CDBConnection con, JsonNode n_positions, Shwuser user) throws Exception {
		con.execsql("delete from shwpositionuser where userid=" + user.userid.getValue());
		for (int i = 0; i < n_positions.size(); i++) {
			JsonNode psin = n_positions.get(i);
			String positionid = psin.path("positionid").asText();
			con.execsql("insert into shwpositionuser (positionid,userid) values(" + positionid + "," + user.userid.getValue() + ")");
		}
	}

	private void saveUserParms(CDBConnection con, Shwuser user) throws Exception {
		con.execsql("delete from shwuserparms where userid=" + user.userid.getValue());
		con.execsql("insert into shwuserparms select a.cstparmid," + user.userid.getValue() + ",a.defaultvalue from shwuserparmsconst a");
		// 创建个人文件夹
		Shwfdr fdr = new Shwfdr();
		fdr.checkOrCreateIDCode(con, true);
		fdr.fdrname.setValue("(" + user.username.getValue() + ")的文件夹");
		fdr.stat.setAsInt(1);
		fdr.creator.setValue("SYSTEM");
		fdr.createtime.setAsDatetime(new Date());
		fdr.fidpath.setValue(fdr.fdrid.getValue() + ",");
		fdr.actived.setAsInt(1);
		fdr.fdtype.setAsInt(2);
		fdr.superid.setAsInt(0);
		fdr.entid.setValue(CSContext.getCurEntID());
		fdr.sysbuildin.setAsInt(1);
		fdr.save(con);
		// 创建个人文件夹权限
		CtrlDoc.newacl(con, fdr.fdrid.getValue(), user.userid.getValue(), user.username.getValue(), 3, CtrlDoc.SACL.ADMIN,
				Systemdate.getDateByStr("2011-01-01"),
				Systemdate.getDateByStr("9999-12-31"));
	}

	@ACOAction(eventname = "userListByOrg", Authentication = true, notes = "获取机构下的用户")
	public String userListByOrg() throws Exception {
		String orgid = CSContext.getParms().get("orgid");
		if ((orgid == null) || orgid.isEmpty())
			throw new Exception("需要orgid参数");

		String sqlstr = "SELECT a.* FROM shwuser a,shworguser b " + "where a.userid=b.userid  and b.orgid=" + orgid;
		CJPALineData<Shwuser> wfs = new CJPALineData<Shwuser>(Shwuser.class);
		wfs.findDataBySQL(sqlstr, true, false);
		return wfs.tojson();
	}

	@ACOAction(eventname = "getOptionsByUserID", ispublic = true, Authentication = true, notes = "查找用户的岗位")
	public String getOptionsByUserID() throws Exception {
		String userid = CSContext.getParms().get("userid");
		String sqlstr = null;
		if ((userid == null) || userid.isEmpty())
			sqlstr = "select a.* from shwposition a ";
		else
			sqlstr = "select a.* from shwposition a ,shwpositionuser b where a.positionid = b.positionid and b.userid = " + userid;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getOrgsByUserID", notes = "查找用户的机构")
	public String getOrgsByUserID() throws Exception {
		String userid = CSContext.getParms().get("userid");
		if ((userid == null) || userid.isEmpty())
			throw new Exception("需要userid参数");
		String sqlstr = "select a.*,b.orgname,b.extorgname from shworguser a,shworg b where a.orgid=b.orgid and a.userid=" + userid;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getRolesByUserID", Authentication = true, notes = "查找用户的角色")
	public String getRolesByUserID() throws Exception {
		String userid = CSContext.getParms().get("userid");
		String sqlstr = null;
		if ((userid == null) || userid.isEmpty())
			sqlstr = "SELECT b.* FROM shwrole b ";
		else
			sqlstr = "SELECT a.userid,b.* FROM shwroleuser a,shwrole b  where a.roleid=b.roleid  and a.userid=" + userid;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getOrgsByLged", Authentication = true, ispublic = true, notes = "查找当前登录用户权限下的所有机构")
	public String getOrgsByLged() throws Exception {
		HashMap<String, String> urlparms = CSContext.getParms();
		String type = urlparms.get("type");
		String isall = urlparms.get("isall");
		boolean all = Boolean.valueOf((isall == null) ? "false" : isall);

		type = (type == null) ? "list" : type;
		String sqlstr = "SELECT a.orgid id,a.* FROM shworg a where 1=1 ";
		if (!CSContext.isAdminNoErr())
			sqlstr = sqlstr + CSContext.getIdpathwhere();
		if (!all)
			sqlstr = sqlstr + " and usable=1 ";
		String parms = urlparms.get("parms");
		List<JSONParm> jps = CJSON.getParms(parms);
		if (("list".equalsIgnoreCase(type))) {
			if (jps.size() == 0) {
				throw new Exception("需要查询参数");
			}
		}
		String where = CjpaUtil.buildFindSqlByJsonParms(new Shworg(), jps);
		if (where != null)
			sqlstr = sqlstr + where;
		String orgid = urlparms.get("orgid");
		if ((orgid != null) && (!orgid.isEmpty())) {
			Shworg org = new Shworg();
			org.findByID(orgid, false);
			if (org.isEmpty())
				throw new Exception("没发现ID为【" + orgid + "】的机构");
			sqlstr = sqlstr + " and idpath like '" + org.idpath.getValue() + "%'";
		}
		if ("list".equalsIgnoreCase(type)) {
			return DBPools.defaultPool().opensql2json(sqlstr);
		}
		if ("gridtree".equalsIgnoreCase(type)) {
			return getOrgGridTree(urlparms, true, all);
		}
		if ("tree".equalsIgnoreCase(type)) {
			sqlstr = "SELECT a.orgid id,a.orgname text,a.superid,a.code,a.idpath FROM shworg a where a.entid=" + CSContext.getCurEntID();
			if (!CSContext.isAdminNoErr())
				sqlstr = sqlstr + CSContext.getIdpathwhere();
			if (!all)
				sqlstr = sqlstr + " and usable=1 ";
			return DBPools.defaultPool().opensql2jsontree(sqlstr, "id", "superid", false);
		}
		if ("orgusrtree".equalsIgnoreCase(type)) {
			return getorgusrtree(true, all);// 获取带机构 带用户的
		}
		return "[]";
	}

	private boolean hasChild(String orgid) throws Exception {
		String sqlstr = "SELECT count(*) ct FROM shworg WHERE superid = " + orgid;
		return Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct")) > 0;
	}

	@ACOAction(eventname = "getorgs", Authentication = true, ispublic = true, notes = "查找当前登录用户组织下的所有机构")
	public String orglistjson() throws Exception {
		HashMap<String, String> urlparms = CSContext.getParms();
		String type = urlparms.get("type");
		String isall = urlparms.get("isall");
		boolean all = Boolean.valueOf((isall == null) ? "false" : isall);
		//System.out.println("all:" + all);
		String sqlstr = "SELECT a.orgid id,a.* FROM shworg a where a.entid=" + CSContext.getCurEntID();
		if (!all)
			sqlstr = sqlstr + " and usable=1 ";
		if ("list".equalsIgnoreCase(type)) {
			return DBPools.defaultPool().opensql2json(sqlstr);
		}
		if ("gridtree".equalsIgnoreCase(type)) {
			return getOrgGridTree(urlparms, false, all);
		}
		if ("tree".equalsIgnoreCase(type)) {
			sqlstr = "SELECT a.orgid id,a.orgname text,a.superid,a.code,a.idpath FROM shworg a where a.entid=" + CSContext.getCurEntID();
			if (!all)
				sqlstr = sqlstr + " and usable=1 ";
			return DBPools.defaultPool().opensql2jsontree(sqlstr, "id", "superid", false);
		}
		if ("orgusrtree".equalsIgnoreCase(type)) {
			return getorgusrtree(false, true);
		}
		return "[]";
	}

	private String getOrgGridTree(HashMap<String, String> urlparms, boolean accctrl, boolean all) throws Exception {
		String sqlstr = null;
		String id = urlparms.get("id");
		if (id == null) {
			if (accctrl && (!CSContext.isAdminNoErr())) {
				String orgid = urlparms.get("orgid");
				if ((orgid != null) && (!orgid.isEmpty())) {
					sqlstr = "select orgid from shworg where orgid=" + orgid;
				} else {
					String idpaths = CSContext.getUserParmValue("idpaths");
					String[] idps = idpaths.split("#");
					String wh = "";
					for (String idp : idps) {
						wh = wh + "'" + idp + "',";
					}
					if (!wh.isEmpty())
						wh = wh.substring(0, wh.length() - 1);
					sqlstr = "select orgid from shworg where idpath in(" + wh + ")";
				}
			} else {
				sqlstr = "select orgid from shworg where superid=0";
			}

			if (!all)
				sqlstr = sqlstr + " and usable=1 ";

			List<HashMap<String, String>> listorgids = DBPools.defaultPool().openSql2List(sqlstr);
			String orgids = "";
			for (HashMap<String, String> hmorgid : listorgids) {
				orgids = orgids + hmorgid.get("orgid") + ",";
			}
			if (!orgids.isEmpty())
				orgids = orgids.substring(0, orgids.length() - 1);
			sqlstr = "SELECT * FROM shworg WHERE orgid IN (" + orgids + ")";
			if (!all)
				sqlstr = sqlstr + " and usable=1 ";
			JSONArray orgs = DBPools.defaultPool().opensql2json_O(sqlstr);
			for (int i = 0; i < orgs.size(); i++) {
				JSONObject org = orgs.getJSONObject(i);
				sqlstr = "SELECT * FROM shworg WHERE superid =" + org.getString("orgid");
				if (!all)
					sqlstr = sqlstr + " and usable=1 ";
				JSONArray childorgs = DBPools.defaultPool().opensql2json_O(sqlstr);
				if (childorgs.size() > 0) {
					for (int j = 0; j < childorgs.size(); j++) {
						JSONObject corg = childorgs.getJSONObject(j);
						corg.put("state", hasChild(corg.getString("orgid")) ? "closed" : "open");
					}
					org.put("children", childorgs);
				}
				org.put("state", "open");
			}
			return orgs.toString();
		} else {
			sqlstr = "SELECT * FROM shworg WHERE superid = " + id;
			if (!all)
				sqlstr = sqlstr + " and usable=1 ";
			JSONArray orgs = DBPools.defaultPool().opensql2json_O(sqlstr);
			for (int i = 0; i < orgs.size(); i++) {
				JSONObject org = orgs.getJSONObject(i);
				org.put("state", hasChild(org.getString("orgid")) ? "closed" : "open");
			}
			return orgs.toString();
		}
	}

	private String getorgusrtree(boolean idpath, boolean all) throws Exception {
		CDBConnection con = DBPools.defaultPool().getCon(null);
		try {
			String sqlstr = "select a.orgid id,a.orgname text,a.superid,'' email,'' mobil,'org' tp FROM shworg a where a.entid="
					+ CSContext.getCurEntID() + CSContext.getIdpathwhere();
			if (!all)
				sqlstr = sqlstr + " and usable=1 ";
			JSONArray js = con.opensql2jsontree_o(sqlstr, "id", "superid", false);
			for (int i = 0; i < js.size(); i++) {
				JSONObject j = js.getJSONObject(i);
				putNodeUsers(con, j);
			}
			return js.toString();
		} finally {
			con.close();
		}
	}

	private void putNodeUsers(CDBConnection con, JSONObject oj) throws Exception {
		if (!"org".equalsIgnoreCase(oj.getString("tp"))) {
			return;
		}
		String sqlstr = "SELECT u.userid id,u.username text,ou.orgid superid,u.email,u.mobil,'usr' tp "
				+ " FROM shwuser u,shworguser ou WHERE u.userid=ou.userid AND ou.orgid=" + oj.getString("id");
		JSONArray ujs = con.opensql2json_o(sqlstr);
		if (oj.has("children")) {
			JSONArray chjs = oj.getJSONArray("children");
			for (int i = 0; i < chjs.size(); i++) {
				JSONObject ojt = chjs.getJSONObject(i);
				putNodeUsers(con, ojt);
			}
			if ((ujs != null) && (ujs.size() > 0))
				chjs.addAll(ujs);
		} else {
			JSONArray chjs = new JSONArray();
			chjs.addAll(ujs);
			if ((ujs != null) && (ujs.size() > 0))
				oj.put("children", chjs);
		}
	}

	@ACOAction(eventname = "getfindorgs", Authentication = true, notes = "查找机构的查询机构")
	public String getfindorgs() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要设置orgid参数");
		String sqlstr = "select * from shworg_find where orgid=" + orgid;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getuserparms", Authentication = true, ispublic = true, notes = "获取用户参数")
	public String userparms() throws Exception {
		String sqlstr = "SELECT b.userid, b.cstparmid, b.parmvalue, a.parmname, a.defaultvalue, a.language1  language, " + "a.notelanguage1 notelanguage "
				+ " FROM shwuserparmsconst a, shwuserparms b" + " WHERE a.cstparmid = b.cstparmid AND b.userid = " + CSContext.getUserID();
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "loadUserShortCut", Authentication = true, ispublic = true, notes = "登陆用户获取快捷方式")
	public String loadUserShortCut() throws Exception {
		String sqlstr = " SELECT m.menuid id,m.menuname text,2 tp,m.ico"
				+ " FROM shwmenu m, shwmenushortcut s "
				+ " WHERE m.menuid=s.id AND s.tp=2 and s.userid=" + CSContext.getUserID();
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "adduserShortCut", Authentication = true, ispublic = true, notes = "登陆用户添加快捷方式")
	public String adduserShortCut() throws Exception {
		HashMap<String, String> ppram = CSContext.parPostDataParms();
		String userid = CSContext.getUserID();
		String id = CorUtil.hashMap2Str(ppram, "id", "需要设置id参数");
		String tp = CorUtil.hashMap2Str(ppram, "tp", "需要设置tp参数");
		ArrayList<String> sqls = new ArrayList<String>();
		sqls.add("DELETE FROM shwmenushortcut  WHERE userid = " + userid + " AND id = " + id + " and tp=" + tp);
		sqls.add("INSERT INTO shwmenushortcut (userid, id,tp) VALUES(" + userid + ", " + id + "," + tp + ")");
		DBPools.defaultPool().execSqls(sqls);
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "deluserShortCut", Authentication = true, ispublic = true, notes = "删除用户添加快捷方式")
	public String deluserShortCut() throws Exception {
		HashMap<String, String> ppram = CSContext.parPostDataParms();
		String userid = CSContext.getUserID();
		String id = CorUtil.hashMap2Str(ppram, "id", "需要设置id参数");
		String tp = CorUtil.hashMap2Str(ppram, "tp", "需要设置tp参数");
		ArrayList<String> sqls = new ArrayList<String>();
		sqls.add("DELETE FROM shwmenushortcut  WHERE userid = " + userid + " AND id = " + id + " and tp=" + tp);
		DBPools.defaultPool().execSqls(sqls);
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "getuserOutWorkinf", Authentication = true, ispublic = true, notes = "当前登陆用户出差信息")
	public String getuserOutWorkinf() throws Exception {
		String userid = CSContext.getUserID();
		String sqlstr = "SELECT u.userid,u.goout,u.gooutstarttime,u.gooutendtime "
				+ " FROM shwuser u WHERE u.userid= " + userid;
		return DBPools.defaultPool().openrowsql2json(sqlstr);
	}

	@ACOAction(eventname = "getwfagents", Authentication = true, ispublic = true, notes = "查询流程代理")
	public String getwfagents() throws Exception {
		String userid = CSContext.getUserID();
		String sqlstr = "SELECT tbwf.wftempid,tbwf.wftempname, wfa.wfagentid,wfa.auserid,wfa.ausername,wfa.adisplayname"
				+ " FROM (SELECT * "
				+ "  FROM shwwftemp wf "
				+ "  WHERE wf.stat=1 and wf.wftempid IN( "
				+ "  select pu.wftempid from shwwftempprocuser pu where pu.isposition=2 and pu.userid=" + userid +
				" union " +
				" select pu.wftempid from shwwftempprocuser pu,shwpositionuser spu " +
				" where pu.userid=spu.positionid and pu.`isposition`=1 and spu.userid=" + userid + ")) tbwf "
				+ " LEFT JOIN ("
				+ "   SELECT * FROM shwuser_wf_agent WHERE userid=" + userid
				+ "   ) wfa ON tbwf.wftempid=wfa.wftempid ORDER BY  tbwf.wftempid";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "savewfagents", Authentication = true, ispublic = true, notes = "流程代理")
	public String savewfagents() throws Exception {
		JSONObject data = JSONObject.fromObject(CSContext.getPostdata());
		CDBConnection con = DBPools.defaultPool().getCon(this);
		con.startTrans();
		try {
			String userid = CSContext.getUserID();
			String sqlstr = "delete from shwuser_wf_agent where userid=" + userid;
			con.execsql(sqlstr);
			Shwuser user = new Shwuser();
			user.findByID4Update(con, userid, false);
			user.goout.setAsBoolean(data.getBoolean("goout"));
			user.gooutstarttime.setValue(data.getString("gooutstarttime"));
			user.gooutendtime.setValue(data.getString("gooutendtime"));
			user.save(con);

			JSONArray jas = data.getJSONArray("agent");
			for (int i = 0; i < jas.size(); i++) {
				JSONObject ja = jas.getJSONObject(i);
				String auserid = ja.getString("auserid");
				String wftempid = ja.getString("wftempid");
				if (userid.equalsIgnoreCase(auserid)) {
					throw new Exception("不能代理给自己");
				}
				if ((auserid != null) && (auserid.length() > 0) && (wftempid != null) && (wftempid.length() > 0)) {
					Shwuser_wf_agent wfa = new Shwuser_wf_agent();
					wfa.auserid.setValue(auserid);
					wfa.wftempid.setValue(wftempid);
					wfa.wftempname.setValue(ja.getString("wftempname"));
					wfa.userid.setValue(userid);
					wfa.ausername.setValue(ja.getString("ausername"));
					wfa.adisplayname.setValue(ja.getString("adisplayname"));
					wfa.save(con);
				}
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "findOrgUserByLogined", Authentication = true, ispublic = true, notes = "根据登录用户权限查找用户")
	public String findOrgUserByLogined() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(eparms);
		Shwuser user = new Shwuser();
		String where = CjpaUtil.buildFindSqlByJsonParms(user, jps);

		String sqlstr = " SELECT o.orgid,o.orgname,u.userid,u.username,o.idpath,u.displayname,o.extorgname "
				+ " FROM shwuser u, shworguser uo, shworg o"
				+ " WHERE uo.orgid=o.orgid AND u.userid=uo.userid" + CSContext.getIdpathwhere() + where;

		return new CReport(sqlstr, null).findReport();

		// return user.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findOrgUser", Authentication = true, ispublic = true, notes = "根据输入用户名查找用户")
	public String findOrgUser() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String parms = urlparms.get("parms");
		List<JSONParm> jps = CJSON.getParms(parms);
		JSONParm username_p = CorUtil.getJSONParm(jps, "username");
		JSONParm displayname_p = CorUtil.getJSONParm(jps, "displayname");
		// if (username == null)
		// throw new Exception("输入用户名或姓名查询");
		String[] ignParms = { "username", "displayname" };// 忽略的查询条件
		Shwuser user = new Shwuser();
		String where = CSearchForm.getCommonSearchWhere(urlparms, user);
		String username = (username_p == null) ? null : username_p.getParmvalue();
		String displayname = (displayname_p == null) ? null : displayname_p.getParmvalue();
		if (((username == null) || username.isEmpty()) && ((displayname == null) || displayname.isEmpty()))
			throw new Exception("登录名或姓名不能全为空");
		if ((username != null) && (!username.isEmpty()))
			where = where + " and u.username='" + username + "'";
		if ((displayname != null) && (!displayname.isEmpty()))
			where = where + " and u.displayname like '%" + displayname + "%' ";
		// where = where + " and (u.username like '%" + username.getParmvalue() + "%' or u.displayname like '%" + username.getParmvalue() + "%')";
		String sqlstr = " SELECT o.orgid,o.orgname,u.userid,u.username,o.idpath,u.displayname,o.extorgname "
				+ " FROM shwuser u, shworguser uo, shworg o"
				+ " WHERE uo.orgid=o.orgid AND u.userid=uo.userid" + where;
		JSONObject rst = new CReport(sqlstr, null).findReport2JSON_O(ignParms, false, "");
		return rst.toString();
	}

	@ACOAction(eventname = "resetExtOrgName", Authentication = true, ispublic = false, notes = "根据机构ID获取机构全称")
	public String resetExtOrgName() throws Exception {
		String sqlstr = "select * from shworg where 1=1 ";
		CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class, sqlstr);
		for (CJPABase jpa : orgs) {
			Shworg org = (Shworg) jpa;
			org.extorgname.setValue(getOrgNamepath(org.idpath.getValue()));
			org.save();
		}
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "reBuildAllOrgIDPath", Authentication = true, notes = "修复所有机构IDPath")
	public String reBuildAllOrgIDPath() throws Exception {
		String sqlstr = "select * from shworg where 1=1 ";
		Shworg jpa = new Shworg();
		JSONArray jpas = jpa.pool.opensql2jsontree_o(sqlstr, "orgid", "superid", false);
		CTreeUtil.reSetIDPathLev(jpas, jpa, "orgid", "idpath", "orglevel");
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "getExtOrgName", Authentication = true, ispublic = true, notes = "根据机构ID获取机构全称")
	public String getExtOrgName() throws Exception {
		HashMap<String, String> ppram = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(ppram, "orgid", "需要设置orgid参数");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("机构【" + orgid + "】不存在");
		JSONObject rst = org.toJsonObj();
		rst.put("extorgname", getOrgNamepath(org.idpath.getValue()));
		return rst.toString();
	}

	public static String getOrgNamepath(CDBConnection con, String idpath) throws Exception {
		// String idpath = org.idpath.getValue();
		String[] ids = idpath.split(",");
		idpath = idpath.substring(0, idpath.length() - 1);
		String sqlstr = "select * from shworg where orgid in (" + idpath + ")";
		List<HashMap<String, String>> orgs = con.openSql2List(sqlstr);
		String exporgname = "";
		for (String id : ids) {
			String orgname = getOrgNameByID(con, orgs, id);
			// System.out.println("orgid:" + id + " " + orgname);
			if (exporgname.isEmpty())
				exporgname = orgname;
			else
				exporgname = exporgname + "-" + orgname;
		}
		return exporgname;
	}

	public static String getOrgNamepath(String idpath) throws Exception {
		CDBConnection con = DBPools.defaultPool().getCon("COShwUser");
		try {
			return getOrgNamepath(con, idpath);
		} finally {
			con.close();
		}
	}

	private static String getOrgNameByID(CDBConnection con, List<HashMap<String, String>> orgs, String id) {
		if ((id == null) || (id.isEmpty()))
			return "";
		for (HashMap<String, String> org : orgs) {
			Object o = org.get("orgid");
			if (o != null) {
				if (o.toString().equals(id)) {
					return org.get("orgname").toString();
				}
			}
		}
		return "";
	}

	@ACOAction(eventname = "putOrg2Org", Authentication = true, notes = "putOrg2Org")
	public String putOrg2Org() throws Exception {
		HashMap<String, String> ppram = CSContext.parPostDataParms();
		int tp = Integer.valueOf(CorUtil.hashMap2Str(ppram, "tp", "需要设置tp参数"));
		String sorgid = CorUtil.hashMap2Str(ppram, "sorgid", "需要设置sorgid参数");
		String dorgid = CorUtil.hashMap2Str(ppram, "dorgid", "需要设置dorgid参数");
		if (tp == 2) {
			CtrShworg.setOrg2Org(sorgid, dorgid);
		}
		if (tp == 1) {
			CtrShworg.putOrgData2Org(sorgid, dorgid);
		}

		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "updateOrgName", Authentication = true, notes = "updateOrgName")
	public String updateOrgName() throws Exception {
		HashMap<String, String> ppram = CSContext.parPostDataParms();
		String orgid = CorUtil.hashMap2Str(ppram, "orgid", "需要设置orgid参数");
		String orgname = CorUtil.hashMap2Str(ppram, "orgname", "需要设置orgname参数");
		CtrShworg.chgOrgName(orgid, orgname);
		Shworg org = new Shworg();
		org.findByID(orgid);
		return org.tojson();
	}

	@ACOAction(eventname = "importuserinfo", Authentication = true, ispublic = false, notes = "用户资料Excel导入")
	public String importuserinfo() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		HashMap<String, String> ppram = CSContext.getParms();
		int tp = Integer.valueOf(CorUtil.hashMap2Str(ppram, "tp", "需要设置tp参数"));// 1 用户资料 2 角色 3岗位
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile(p, batchno, tp);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("rst", rst);
		jo.put("batchno", batchno);
		return jo.toString();
	}

	private int parserExcelFile(Shw_physic_file pf, String batchno, int tp) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fullname));
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		HSSFSheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		if (tp == 1) {
			return parserExcelUserInfoSheet(aSheet, batchno);
		} else if (tp == 2) {
			return parserExcelUserRolesSheet(aSheet, batchno);
		} else if (tp == 3) {
			return parserExcelUserOptionsSheet(aSheet, batchno);
		} else
			throw new Exception("参数tp错误，只允许为1,2,3");
	}

	private int parserExcelUserInfoSheet(HSSFSheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initUserInfoExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shwuser user = new Shwuser();
		Shworg org = new Shworg();
		CDBConnection con = user.pool.getCon(this);
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String username = v.get("username");
				if ((username == null) || (username.isEmpty()))
					continue;

				String orgcode = v.get("orgcode");
				if ((orgcode == null) || (orgcode.isEmpty()))
					throw new Exception("用户【" + v.get("displayname") + "】所属机构不能为空");

				org.clear();
				String sqlstr = "select * from shworg where code='" + orgcode + "'";
				org.findBySQL(sqlstr, false);
				if (org.isEmpty())
					throw new Exception("编码为【" + orgcode + "】的机构不存在");
				checkOrgRight(con, org.orgid.getValue(), org.extorgname.getValue());
				rst++;
				user.clear();
				sqlstr = "select * from shwuser where username='" + username + "'";
				user.findBySQL(con, sqlstr, false);
				boolean isnew = user.isEmpty();
				user.actived.setAsInt(1);
				user.usertype.setAsInt(2);
				user.username.setValue(username);
				user.displayname.setValue(v.get("displayname"));
				user.email.setValue(v.get("email"));
				user.mobil.setValue(v.get("mobil"));
				user.telh.setValue(v.get("telh"));
				user.idcardno.setValue(v.get("idcardno"));
				user.note.setValue("导入批号:" + batchno);
				String pwd = v.get("pwd");
				if (pwd == null)
					pwd = "";
				pwd = pwd.trim();
				if (pwd.isEmpty())
					user.userpass.setValue(null);
				else {
					user.userpass.setValue(DesSw.EncryStrHex(pwd, ConstsSw._userkey));
				}
				user.save(con);
				if (isnew)
					saveUserParms(con, user);// 保存用户参数
				// 机构
				String defaultorg = v.get("defaultorg");
				defaultorg = (defaultorg == null) ? "否" : defaultorg.trim();
				int isdefault = (defaultorg.equalsIgnoreCase("是")) ? 1 : 2;
				con.execsql("delete from shworguser where userid=" + user.userid.getValue() + " and orgid=" + org.orgid.getValue());
				con.execsql("insert into shworguser(orgid,isdefault,inheritrole,userid) values(" + org.orgid.getValue() + ","
						+ isdefault + ",1," + user.userid.getValue() + ")");
			}
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	private List<CExcelField> initUserInfoExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("登录名", "username", true));
		efields.add(new CExcelField("姓名", "displayname", true));
		efields.add(new CExcelField("密码", "pwd", false));
		efields.add(new CExcelField("机构编码", "orgcode", true));
		efields.add(new CExcelField("是否默认机构", "defaultorg", true));
		efields.add(new CExcelField("邮箱", "email", false));
		efields.add(new CExcelField("手机", "mobil", false));
		efields.add(new CExcelField("电话", "telh", false));
		efields.add(new CExcelField("身份证", "idcardno", false));
		return efields;
	}

	private int parserExcelUserRolesSheet(HSSFSheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initUserRoleExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shwuser user = new Shwuser();
		CJPALineData<Shwrole> roles = new CJPALineData<Shwrole>(Shwrole.class);
		String sqlstr = "select * from shwrole where 1=1 ";
		roles.findDataBySQL(sqlstr);
		CDBConnection con = user.pool.getCon(this);
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String username = v.get("username");
				if ((username == null) || (username.isEmpty()))
					continue;
				user.clear();
				sqlstr = "select * from shwuser where username='" + username + "'";
				user.findBySQL(sqlstr, false);
				if (user.isEmpty())
					throw new Exception("登录名为【" + username + "】的系统用户不存在");
				checkUserRight(con, user.userid.getValue(), user.username.getValue());
				String rolename = v.get("rolename");
				if ((rolename == null) || (rolename.isEmpty()))
					throw new Exception("登录名为【" + username + "】的角色名不允许为空");
				Shwrole role = getRolebyName(roles, rolename);
				String roleid = role.roleid.getValue();
				con.execsql("delete from shwroleuser where userid=" + user.userid.getValue() + " and roleid=" + roleid);
				con.execsql("insert into shwroleuser (roleid,userid) values(" + roleid + "," + user.userid.getValue() + ")");
				rst++;
			}
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	private Shwrole getRolebyName(CJPALineData<Shwrole> roles, String rolename) throws Exception {
		for (CJPABase jpa : roles) {
			Shwrole role = (Shwrole) jpa;
			if (rolename.equalsIgnoreCase(role.rolename.getValue()))
				return role;
		}
		throw new Exception("角色【" + rolename + "】不存在");
	}

	private List<CExcelField> initUserRoleExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("登录名", "username", true));
		efields.add(new CExcelField("系统角色", "rolename", true));
		return efields;
	}

	private int parserExcelUserOptionsSheet(HSSFSheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initUserOptionExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shwuser user = new Shwuser();
		CJPALineData<Shwposition> ops = new CJPALineData<Shwposition>(Shwposition.class);
		String sqlstr = "select * from shwposition";
		ops.findDataBySQL(sqlstr);
		CDBConnection con = user.pool.getCon(this);
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String username = v.get("username");
				if ((username == null) || (username.isEmpty()))
					continue;
				user.clear();
				sqlstr = "select * from shwuser where username='" + username + "'";
				user.findBySQL(sqlstr, false);
				if (user.isEmpty())
					throw new Exception("登录名为【" + username + "】的系统用户不存在");
				checkUserRight(con, user.userid.getValue(), user.username.getValue());
				String positiondesc = v.get("positiondesc");
				if ((positiondesc == null) || (positiondesc.isEmpty()))
					throw new Exception("登录名为【" + username + "】的岗位名不允许为空");
				Shwposition op = getOpitonbyName(ops, positiondesc);
				String positionid = op.positionid.getValue();
				con.execsql("delete from shwpositionuser where userid=" + user.userid.getValue() + " and positionid=" + positionid);
				con.execsql("insert into shwpositionuser (positionid,userid) values(" + positionid + "," + user.userid.getValue() + ")");
				rst++;
			}
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	private Shwposition getOpitonbyName(CJPALineData<Shwposition> ops, String positiondesc) throws Exception {
		for (CJPABase jpa : ops) {
			Shwposition op = (Shwposition) jpa;
			if (positiondesc.equalsIgnoreCase(op.positiondesc.getValue()))
				return op;
		}
		throw new Exception("岗位【" + positiondesc + "】不存在");
	}

	private List<CExcelField> initUserOptionExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("登录名", "username", true));
		efields.add(new CExcelField("系统岗位", "positiondesc", true));
		return efields;
	}

	// 检查当前登录用户是否有处理该用户权限
	private void checkOrgRight(CDBConnection con, String orgid, String orgname) throws Exception {
		String sqlstr = "SELECT count(*) ct FROM shworg WHERE orgid=" + orgid + CSContext.getIdpathwhere();
		if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) == 0)
			throw new Exception("处理机构【" + orgname + "】权限");
	}

	// 检查当前登录用户是否有处理该用户权限
	private void checkUserRight(CDBConnection con, String userid, String username) throws Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM shworg,shworguser"
				+ " WHERE  shworg.orgid=shworguser.orgid AND shworguser.userid=" + userid + CSContext.getIdpathwhere();
		if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) == 0)
			throw new Exception("处理用户【" + username + "】权限");
	}

}
