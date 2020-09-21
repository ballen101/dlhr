package com.corsair.server.genco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPAJSON;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.exception.CException;
import com.corsair.server.generic.Shwmenu;
import com.corsair.server.generic.Shwmenuco;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CTreeUtil;
import com.corsair.server.util.CorUtil;

/**
 * 菜单CO
 * 
 * @author Administrator
 *
 */
@ACO(coname = "web.menu")
public class COMenu extends JPAController {

	// @ACOAction(eventname = "getModTree", Authentication = true, notes =
	// "通过角色获取模块树")
	// public String getModTree() throws Exception {
	// String sqlstr = null;
	// String roleid = CSContext.getParms().get("roleid");
	// if ((roleid == null) || roleid.isEmpty())
	// throw new Exception("需要roleid参数");
	// if ("undefined".equalsIgnoreCase(roleid))
	// roleid = "-1";
	// sqlstr =
	// "select a.modid id,a.modname text,b.roleid,a.* from shwmod a left join shwrolemod b on "
	// + " a.modid=b.modid and b.roleid=" + roleid
	// + " where a.state=1 and a.modid<>53 order by a.sortindex";
	// return DBPools.defaultPool().opensql2jsontree(sqlstr, "id", "modpid",
	// false);
	// }

	@ACOAction(eventname = "getAllMenuTree", Authentication = true, notes = "获取整个菜单树")
	public String getAllMenuTree() throws Exception {
		String sqlstr = "SELECT a.*  FROM shwmenu a where a.state=1 ORDER BY a.sortindex";
		return DBPools.defaultPool().opensql2jsontree(sqlstr, "menuid", "menupid", false);
	}

	@ACOAction(eventname = "getRoleMenus", Authentication = true, notes = "获取某个角色对应的菜单ID列表")
	public String getRoleMenus() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String roleid = CorUtil.hashMap2Str(parms, "roleid", "需要设置roleid参数");
		String sqlstr = "SELECT a.menuid FROM shwrolemenu a WHERE a.roleid=" + roleid;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	/*
	 * @ACOAction(eventname = "getModMenuTree", Authentication = true, notes = "根据角色获取模块菜单树") public String getModMenuTree() throws Exception { String roleid =
	 * CSContext.getParms().get("roleid"); if ((roleid == null) || roleid.isEmpty()) throw new Exception("需要roleid参数"); if
	 * ("undefined".equalsIgnoreCase(roleid)) roleid = "-1";// -1不用权限控制 String type = CSContext.getParms().get("type"); String fs =
	 * ("all".equalsIgnoreCase(type)) ? "" : " and a.flatform in(2,3) "; String modid = CSContext.getParms().get("modid"); if ((modid == null) ||
	 * modid.isEmpty()) throw new Exception("需要modid参数"); String sqlstr =
	 * "select a.menuid id,a.menuname text,a.*,b.roleid from shwmenu a left join shwrolemenu b on a.menuid=b.menuid " + "and b.roleid=" + roleid +
	 * " where a.modid=" + modid + fs + " order by a.sortindex"; return DBPools.defaultPool().opensql2jsontree(sqlstr, "id", "menupid", false); }
	 * @ACOAction(eventname = "getMenusMod1s", Authentication = true, ispublic = true, notes = "获取模块的一级菜单") public String getMenusMod1s() throws Exception {
	 * String modid = CSContext.getParms().get("modid"); if ((modid == null) || modid.isEmpty()) throw new Exception("需要modid参数"); String userid =
	 * CSContext.getUserID(); Shwuser user = new Shwuser(); user.findByID(userid, false); if (user.isEmpty()) throw new Exception("获取当前登录用户错误!"); String sqlstr
	 * = null; if ("1".equals(user.usertype.getValue())) { sqlstr =
	 * "select a.* from shwmenu a where a.state=1 and a.menupid<>0  and a.modid in(SELECT distinct  a.modid  FROM shwmod a " +
	 * " where a.state=1 and a.modpid<>0 and a.flatform in(2,3) and a.modpid=" + modid + ") ORDER BY a.modid,a.sortindex"; } else { sqlstr =
	 * "SELECT distinct a.*  FROM shwmenu a,shwrolemenu b ,shwroleuser c ,shwrole d " +
	 * " where a.menuid=b.menuid and b.roleid=c.roleid and b.roleid=d.roleid  and d.entid=" + CSContext.getCurEntID() +
	 * " and a.state=1 and a.menupid<>0  and c.userid=" + userid + " and a.modid in (SELECT distinct  a.modid  FROM shwmod a " +
	 * " where a.state=1 and a.modpid<>0 and a.flatform in(2,3) and  a.modpid=" + modid + ")  ORDER BY a.modid,a.sortindex"; } return
	 * DBPools.defaultPool().opensql2json(sqlstr); }
	 * @ACOAction(eventname = "getmods", Authentication = true, ispublic = true, notes = "登录用户获取模块") public String getmods() throws Exception { String userid =
	 * CSContext.getUserID(); Shwuser user = new Shwuser(); user.findByID(userid, false); if (user.isEmpty()) throw new Exception("获取当前登录用户错误!"); String sqlstr
	 * = null; if ("1".equals(user.usertype.getValue())) { sqlstr = "SELECT distinct  a.*  FROM shwmod a where a.state=1 and modpid<>0 ORDER BY a.sortindex" ; }
	 * else { sqlstr = "SELECT distinct  a.* " + " FROM shwmod a,shwrolemod b,shwroleuser c,shwrole d  " +
	 * " where a.state=1 and a.modid=b.modid and b.roleid=c.roleid and a.modpid<>0  and d.roleid=c.roleid and d.entid=" + CSContext.getCurEntID() +
	 * " and c.userid=" + userid + " ORDER BY a.sortindex"; } CJPALineData<Shwmod> mods = new CJPALineData<Shwmod>(Shwmod.class); mods.findDataBySQL(sqlstr,
	 * true, true); return mods.tojson(); }
	 */

	@ACOAction(eventname = "getIndexMenuTree", Authentication = true, ispublic = true, notes = "登录用户获取首页菜单NEW")
	public String getIndexMenuTree() throws Exception {
		String userid = CSContext.getUserID();
		Shwuser user = new Shwuser();
		user.findByID(userid, false);
		if (user.isEmpty())
			throw new Exception("获取当前登录用户错误!");
		String sqlstr = null;
		if ("1".equals(user.usertype.getValue())) {
			sqlstr = "SELECT * FROM ( SELECT DISTINCT * FROM shwmenu WHERE state=1 )tb ORDER BY sortindex";
		} else {
			sqlstr = "SELECT * FROM (SELECT distinct m.* FROM shwmenu m,shwrolemenu sm,shwroleuser ru "
					+ "WHERE m.menuid=sm.menuid AND sm.roleid=ru.roleid AND m.state=1 AND ru.userid="
					+ userid + " ) tb where 1=1 order by sortindex";
		}
		return DBPools.defaultPool().opensql2jsontree(sqlstr, "menuid", "menupid", false);
	}

	@ACOAction(eventname = "getmenutree", Authentication = true, notes = "登录用户根据模块获取菜单test")
	public String getmenutree() throws Exception {
		String sqlstr = "select distinct * from (SELECT * FROM shwmenu ORDER BY sortindex) where 1=1";
		return DBPools.defaultPool().opensql2jsontree(sqlstr, "menuid", "menupid", false);
	}

	/*
	 * @ACOAction(eventname = "getmenus", Authentication = true, notes = "登录用户根据模块获取菜单") public String getmenus() throws Exception { String modid =
	 * CSContext.getParms().get("modid"); if ((modid == null) || modid.isEmpty()) throw new CException("参数modid必须！"); String userid = CSContext.getUserID();
	 * Shwuser user = new Shwuser(); user.findByID(userid); if (user.isEmpty()) throw new CException("获取当前登录用户错误!"); String sqlstr = null; if
	 * ("1".equals(user.usertype.getValue())) { sqlstr = "SELECT distinct a.*  FROM shwmenu a where  a.modid=" + modid + " ORDER BY a.sortindex"; } else {
	 * sqlstr = "SELECT distinct a.*  FROM shwmenu a,shwrolemenu b ,shwroleuser c ,shwrole d " +
	 * " where a.menuid=b.menuid and b.roleid=c.roleid and b.roleid=d.roleid  and d.entid=" + CSContext.getCurEntID() +
	 * " and a.state=1 and a.menupid<>0 and c.userid=" + userid + " and a.modid=" + modid + " ORDER BY a.sortindex  "; } CJPALineData<Shwmenu> menus = new
	 * CJPALineData<Shwmenu>(Shwmenu.class); menus.findDataBySQL(sqlstr, true, true); return menus.tojson(); }
	 * @ACOAction(eventname = "saveMod", Authentication = true, notes = "保存模块") public String saveMod() throws Exception { Shwmod mod = new Shwmod();
	 * mod.fromjson(CSContext.getPostdata()); return ((CJPAJSON) mod.save()).tojson(); }
	 * @ACOAction(eventname = "delMod", Authentication = true, notes = "删除模块") public String delMod() throws Exception { String modid =
	 * CSContext.getParms().get("modid"); if ((modid == null) || modid.isEmpty()) throw new Exception("参数modid必须！"); if
	 * (Integer.valueOf(DBPools.defaultPool().openSql2List( "select count(*) ct from shwmod where modpid=" + modid).get(0).get("ct")) > 0) { throw new
	 * Exception("有子节点，不允许删除！"); } new Shwmod().delete(modid, false); // ////////模块所属菜单也需要删除 return "{\"result\":\"OK\"}"; }
	 */

	@ACOAction(eventname = "saveMenuCo", Authentication = true, notes = "保存菜单CO")
	public String saveMenuCo() throws Exception {
		CDBConnection con = DBPools.defaultPool().getCon(this);
		con.startTrans();
		try {
			HashMap<String, String> pdata = CSContext.parPostDataParms();
			String menuid = CorUtil.hashMap2Str(pdata, "menuid", "需要设置menuid参数");
			if (pdata.get("cos") != null) {
				List<HashMap<String, String>> conames = CJSON.parArrJson(pdata.get("cos").toString());
				updateMenuCos(con, menuid, conames, "name");
			}
			con.submit();
			return "{\"result\":\"OK\"}";
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	private void updateMenuCos(CDBConnection con, String menuid, List<HashMap<String, String>> conames, String conamefd) throws Exception {
		Shwmenuco mco = new Shwmenuco();
		con.execsql("delete from shwmenuco where menuid=" + menuid);
		for (HashMap<String, String> coname : conames) {
			String sn = coname.get(conamefd);
			mco.clear();
			mco.menuid.setValue(menuid);
			mco.coname.setValue(sn.trim());
			mco.save(con);
		}
	}

	// @ACOAction(eventname = "delMenu", Authentication = true, notes = "删除菜单")
	// public String delMenu() throws Exception {
	// String menuid = CSContext.getParms().get("menuid");
	// if ((menuid == null) || menuid.isEmpty())
	// throw new Exception("参数menuid必须！");
	// ArrayList<String> sqls = new ArrayList<String>();
	// sqls.add("delete from shwmenu where menupid=" + menuid);
	// sqls.add("delete from shwmenu where menuid=" + menuid);
	// sqls.add("delete from shwmenuco where menuid=" + menuid);
	// DBPools.defaultPool().execSqls(sqls);
	// // new Shwmenu().delete(menuid, false);
	// return "{\"result\":\"OK\"}";
	// }

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String menuid) throws Exception {
		ArrayList<String> sqls = new ArrayList<String>();
		sqls.add("delete from shwmenu where menupid=" + menuid);
		sqls.add("delete from shwmenu where menuid=" + menuid);
		sqls.add("delete from shwmenuco where menuid=" + menuid);
		sqls.add("delete from shwmenushortcut where id=" + menuid);
		sqls.add("delete from shwrolemenu where menuid=" + menuid);
		con.execSqls(sqls);
		// throw new Exception("测试错误");
		return null;
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		Shwmenu menu = (Shwmenu) jpa;
		menu.save();
		if (menu.menuidpath.isEmpty()) {
			midpath = "";
			getidpath(menu);
			menu.menuidpath.setValue(midpath);
			menu.save(con);
		}
		if (menu.state.getAsInt() != 1) {// 如果 上级菜单停用了 需要停用所有子菜单
			String sqlstr = "update shwmenu set state=2 where menuidpath like '" + menu.menuidpath.getValue() + "%'";
			con.execsql(sqlstr);
		}
		return menu.tojson();
	}

	@ACOAction(eventname = "getRepairIdpath", Authentication = true, notes = "修复菜单IDPath")
	public String getRepairIdpath() throws Exception {
		String menuid = CorUtil.hashMap2Str(CSContext.getParms(), "menuid", "需要设置menuid参数");
		Shwmenu menu = new Shwmenu();
		menu.findByID(menuid);
		if (menu.isEmpty()) {
			throw new Exception("ID为【" + menuid + "】的菜单不存在");
		}
		midpath = "";
		getidpath(menu);
		return "{\"menuidpath\":\"" + midpath + "\"}";
	}

	private String midpath;

	@ACOAction(eventname = "reBuildAllMenuIDPath", Authentication = true, notes = "修复所有菜单IDPath")
	public String reBuildAllMenuIDPath() throws Exception {
		String sqlstr = "select * from shwmenu where 1=1 ";
		Shwmenu m = new Shwmenu();
		JSONArray ms = m.pool.opensql2jsontree_o(sqlstr, "menuid", "menupid", false);
		CTreeUtil.reSetIDPathLev(ms, m, "menuid", "menuidpath", "level");
		return "{\"result\":\"OK\"}";
	}

	private void getidpath(Shwmenu menu) throws Exception {
		midpath = menu.menuid.getValue() + "," + midpath;
		String sqlstr = "select * from shwmenu where menuid=" + menu.menupid.getValue();
		Shwmenu pmenu = new Shwmenu();
		pmenu.findBySQL(sqlstr);
		if (!pmenu.isEmpty()) {
			getidpath(pmenu);
		}
	}

	@ACOAction(eventname = "getMenuByid", Authentication = true, notes = "根据ID获取菜单信息")
	public String getMenuByid() throws Exception {
		String menuid = CSContext.getParms().get("menuid");
		if ((menuid == null) || menuid.isEmpty())
			throw new Exception("参数menuid必须！");
		Shwmenu menu = new Shwmenu();
		return ((CJPAJSON) menu.findByID(menuid)).tojson();
	}

	/*
	 * @ACOAction(eventname = "getModByid", Authentication = true, ispublic = true, notes = "根据ID获取模块信息") public String getModByid() throws Exception { String
	 * modid = CSContext.getParms().get("modid"); if ((modid == null) || modid.isEmpty()) throw new Exception("参数modid必须！"); Shwmod mod = new Shwmod(); return
	 * ((CJPAJSON) mod.findByID(modid)).tojson(); }
	 */

	@ACOAction(eventname = "saveMenuCos", Authentication = true, notes = "保存菜单CO权限(粘贴)")
	public String saveMenuCos() throws Exception {
		HashMap<String, String> postparms = CSContext.parPostDataParms();
		String mueuid = CorUtil.hashMap2Str(postparms, "mueuid", "需要参数mueuid");
		String conames = CorUtil.hashMap2Str(postparms, "conames", "需要参数conames");
		List<HashMap<String, String>> cos = CJSON.parArrJson(conames);
		CDBConnection con = DBPools.defaultPool().getCon(this);
		con.startTrans();
		try {
			updateMenuCos(con, mueuid, cos, "coname");
			con.submit();
			return "{\"result\":\"OK\"}";
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	@ACOAction(eventname = "moveMenu", Authentication = true, notes = "移动菜单")
	public String moveMenu() throws Exception {
		HashMap<String, String> postparms = CSContext.parPostDataParms();
		String menuid = CorUtil.hashMap2Str(postparms, "menuid", "需要参数menuid");
		String menuid2 = CorUtil.hashMap2Str(postparms, "menuid2", "需要参数menuid2");
		Shwmenu menu = new Shwmenu();
		menu.findByID(menuid);
		if (menu.isEmpty()) {
			throw new Exception("ID为【" + menuid + "】的菜单不存在");
		}
		Shwmenu menu2 = new Shwmenu();
		menu2.findByID(menuid2);
		if (menu2.isEmpty()) {
			throw new Exception("ID为【" + menuid2 + "】的菜单不存在");
		}
		CDBConnection con = menu.pool.getCon(this);
		con.startTrans();
		try {
			menu.menupid.setValue(menu2.menuid.getValue());
			menu.save(con);
			String sqlstr = "select * from shwmenu where 1=1";
			Shwmenu m = new Shwmenu();
			JSONArray ms = m.pool.opensql2jsontree_o(sqlstr, "menuid", "menupid", false);
			CTreeUtil.reSetIDPathLev(con, ms, m, "menuid", "menuidpath", "level");
			menu.findByID(con, menuid);
			sqlstr = "SELECT * FROM shwmenu WHERE menuidpath LIKE '" + menu.menuidpath.getValue() + "%'";
			JSONArray js = con.opensql2jsontree_o(sqlstr, "menuid", "menupid", false);
			//System.out.println("js:" + js.toString());
			if (js.size() == 0)
				throw new Exception("出错了 ^v^");
			con.submit();
			return js.toString();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}

	}
}
