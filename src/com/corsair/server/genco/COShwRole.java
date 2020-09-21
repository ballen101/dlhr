package com.corsair.server.genco;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shwrole;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;

@ACO(coname = "web.role")
public class COShwRole {
	@ACOAction(eventname = "getRoles", Authentication = true, notes = "登录用户根据岗位获取角色")
	public String getAllroles() throws Exception {
		String sqlstr = "select * from shwrole where entid=" + CSContext.getCurEntID();
		CJPALineData<Shwrole> jpas = new CJPALineData<Shwrole>(Shwrole.class);
		jpas.findDataBySQL(sqlstr, true, false);
		return jpas.tojson();
	}

	@ACOAction(eventname = "getUsers", Authentication = true, notes = "根据角色获取用户")
	public String getUsers() throws Exception {
		String roleid = CSContext.getParms().get("roleid");
		if ((roleid == null) || roleid.isEmpty())
			throw new Exception("需要roleid参数");

		String sqlstr = "select a.* from shwuser a,shwroleuser b where a.userid=b.userid and b.roleid=" + roleid;
		CJPALineData<Shwuser> jpas = new CJPALineData<Shwuser>(Shwuser.class);
		jpas.findDataBySQL(sqlstr, true, false);
		return jpas.tojson();
	}

	@ACOAction(eventname = "saveRole", Authentication = true, notes = "保存角色")
	public String saveRole() throws Exception {

		JSONObject jso = JSONObject.fromObject(CSContext.getPostdata());

		boolean isnew = jso.getBoolean("isnew");
		JSONArray menus = jso.getJSONArray("menus");
		String jsondata = jso.getString("jsondata");
		Shwrole role = (Shwrole) new Shwrole().fromjson(jsondata);
		CDBConnection con = role.pool.getCon(this);
		con.startTrans();
		try {
			role.save(con);
			if (isnew) {
				//
			}
			saveMenus(con, menus, role);
			con.submit();
			return role.tojson();
		} catch (Exception e) {
			con.rollback();
			throw new Exception("保存角色资料错误:" + e.getMessage());
		} finally {
			con.close();
		}
	}

	@ACOAction(eventname = "delRole", Authentication = true, notes = "删除角色")
	public String delRole() throws Exception {
		String roleid = CSContext.getParms().get("roleid");
		if ((roleid == null) || roleid.isEmpty())
			throw new Exception("需要roleid参数");
		Shwrole role = (Shwrole) new Shwrole();
		CDBConnection con = role.pool.getCon(this);
		con.startTrans();
		try {
			role.delete(con, roleid, false);
			con.execsql("delete from shwroleuser where roleid=" + roleid);
			con.submit();
			return "{\"result\":\"OK\"}";
		} catch (Exception e) {
			con.rollback();
			throw new Exception("删除角色资料错误:" + e.getMessage());
		} finally {
			con.close();
		}
	}

	private void saveMenus(CDBConnection con, JSONArray smenus, Shwrole role) throws Exception {
		con.execsql("delete from shwrolemenu where roleid=" + role.roleid.getValue());
		for (int i = 0; i < smenus.size(); i++) {
			JSONObject jo = smenus.getJSONObject(i);
			con.execsql("insert into shwrolemenu(roleid,menuid) values(" + role.roleid.getValue() + ", " + jo.getString("menuid") + ")");
		}
	}
}
