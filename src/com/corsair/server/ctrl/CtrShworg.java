package com.corsair.server.ctrl;

import java.lang.reflect.Constructor;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.genco.COShwUser;
import com.corsair.server.generic.Shwfdracl;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shworg_acthis;
import com.corsair.server.generic.Shworg_find;
import com.corsair.server.generic.Shworguser;
import com.corsair.server.util.CTreeUtil;

/**
 * 机构控制器
 * 
 * @author Administrator
 *
 */
public class CtrShworg {
	public static void chgOrgName(String orgid, String newname) throws Exception {
		Shworg org = new Shworg();
		CDBConnection con = org.pool.getCon(CTreeUtil.class);
		con.startTrans();
		try {
			org.findByID(con, orgid, false);
			if (org.isEmpty())
				throw new Exception("ID为【" + orgid + "】的机构不存在");
			org.orgname.setValue(newname);
			org.save(con);
			String sqlstr = "select * from shworg where idpath like '" + org.idpath.getValue() + "%'";
			CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class);
			orgs.findDataBySQL(con, sqlstr, false, true);
			for (CJPABase jpa : orgs) {
				Shworg torg = (Shworg) jpa;
				torg.extorgname.setValue(COShwUser.getOrgNamepath(con, torg.idpath.getValue()));
				torg.save(con);
				OnChangeOrgInfoListener oic = getOrgChgListener();
				if (oic != null)
					oic.OnOrgChg(con, torg.toJsonObj(), null);
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	// ///////////////////////////////////////////////////
	// ///////////////////////////////////////////////////
	// 将源机构下面信息挪动到目标机构下 原机构不动
	public static void putOrgData2Org(String sorgid, String dorgid) throws Exception {
		Shworg sorg = new Shworg();
		Shworg dorg = new Shworg();
		CDBConnection con = sorg.pool.getCon(CTreeUtil.class);
		con.startTrans();
		try {
			sorg.findByID(con, sorgid);
			if (sorg.isEmpty()) {
				throw new Exception("ID为【" + sorgid + "】的机构不存在");
			}
			dorg.findByID(con, dorgid);
			if (dorg.isEmpty()) {
				throw new Exception("ID为【" + dorgid + "】的机构不存在");
			}
			JSONObject dorg_s = dorg.toJsonObj();
			putOrgData2Org(con, sorg, dorg_s);

			Shworg_acthis soh = new Shworg_acthis();
			soh.orgid.setValue(sorg.orgid.getValue());
			soh.acttype.setAsInt(3);
			soh.acttime.setAsDatetime(new Date());
			soh.actcommit.setValue("合并到机构" + dorg.orgid.getValue() + "," + dorg.code.getValue() + "," + dorg.orgname.getValue());
			soh.save(con);
			soh.clear();
			soh.orgid.setValue(dorg.orgid.getValue());
			soh.acttype.setAsInt(2);
			soh.acttime.setAsDatetime(new Date());
			soh.actcommit.setValue("合并机构" + sorg.orgid.getValue() + "," + sorg.code.getValue() + "," + sorg.orgname.getValue());
			soh.save(con);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	private static void putOrgData2Org(CDBConnection con, Shworg sorg, JSONObject dorg_s) throws Exception {
		String sidp = sorg.idpath.getValue();
		String didp = dorg_s.getString("idpath");
		if (didp.length() > sidp.length())// 只有目标长度大于源长度才有可能
			if (didp.substring(0, sidp.length()).equals(sidp)) {
				throw new Exception("目标机构【" + dorg_s.getString("orgname") + "】不能是源机构【" + sorg.orgname.getValue() + "】及其下属机构");
			}
		upData2NewOrg(con, sorg, dorg_s);
	}

	private static void upData2NewOrg(CDBConnection con, Shworg oldorg, JSONObject dorg_s) throws Exception {
		// 文件权限
		String sqlstr = "SELECT * FROM shwfdracl WHERE acltype IN(1,2) AND ownerid=" + oldorg.orgid.getValue();
		CJPALineData<Shwfdracl> acls = new CJPALineData<Shwfdracl>(Shwfdracl.class);
		acls.findDataBySQL(con, sqlstr, true, false, -1, 0, true);
		for (CJPABase jpa : acls) {
			Shwfdracl oldacl = (Shwfdracl) jpa;
			sqlstr = "SELECT count(*) ct FROM shwfdracl WHERE acltype IN(1,2) AND ownerid=" + dorg_s.getString("orgid")
					+ " and objid=" + oldacl.objid.getValue();
			if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) == 0) {
				oldacl.ownerid.setValue(dorg_s.getString("orgid"));
				oldacl.idpath.setValue(dorg_s.getString("idpath"));
				oldacl.ownername.setValue(dorg_s.getString("orgname"));
				oldacl.save(con);
			}
		}
		// 兼管机构
		sqlstr = "SELECT * FROM shworg_find WHERE forgid=" + oldorg.orgid.getValue();
		CJPALineData<Shworg_find> ofs = new CJPALineData<Shworg_find>(Shworg_find.class);
		ofs.findDataBySQL(con, sqlstr, true, false, -1, 0, true);
		for (CJPABase jpa : ofs) {
			Shworg_find oldof = (Shworg_find) jpa;
			sqlstr = "select count(*) ct from shworg_find where forgid=" + dorg_s.getString("orgid") + " and orgid=" + oldof.orgid.getValue();
			if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) == 0) {
				oldof.forgid.setValue(dorg_s.getString("orgid"));
				oldof.forgname.setValue(dorg_s.getString("extorgname"));
				oldof.fcode.setValue(dorg_s.getString("code"));
				oldof.fidpath.setValue(dorg_s.getString("idpath"));
				oldof.save(con);
			}
		}
		sqlstr = "UPDATE shworg_find SET orgid=" + dorg_s.getString("orgid") + " WHERE orgid=" + oldorg.orgid.getValue();
		con.execsql(sqlstr);
		// 用户机构
		sqlstr = "SELECT * FROM shworguser WHERE orgid=" + oldorg.orgid.getValue();
		CJPALineData<Shworguser> ous = new CJPALineData<Shworguser>(Shworguser.class);
		ous.findDataBySQL(con, sqlstr, true, false, -1, 0, true);
		for (CJPABase jpa : ous) {
			Shworguser oldou = (Shworguser) jpa;
			sqlstr = "SELECT count(*) ct FROM shworguser WHERE orgid=" + dorg_s.getString("orgid") + " and userid=" + oldou.userid.getValue();
			if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) == 0) {
				sqlstr = "UPDATE shworguser SET orgid=" + dorg_s.getString("orgid") + " WHERE orgid=" + oldorg.orgid.getValue() + " and userid="
						+ oldou.userid.getValue();
				con.execsql(sqlstr);// 修改主关键字只能用SQL语句
			}
		}

		OnChangeOrgInfoListener oic = getOrgChgListener();
		if (oic != null)
			oic.OnOrgData2Org(con, oldorg, dorg_s);

		// 不能放前面去
		sqlstr = "SELECT * FROM shworg WHERE superid =" + oldorg.orgid.getValue();
		CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class);
		orgs.findDataBySQL(con, sqlstr, true, false, -1, 0, true);
		for (CJPABase jpa : orgs) {
			Shworg org = (Shworg) jpa;
			org.superid.setValue(dorg_s.getString("orgid"));
			org.idpath.setValue(dorg_s.getString("idpath") + org.orgid.getValue() + ",");
			org.save(con);
			setOrgParentAsOrg(con, org, dorg_s);
		}

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////

	// ///将源机构整个挪到目标机构下（改变原机构上级机构ID）
	public static void setOrg2Org(String sorgid, String dorgid) throws Exception {
		Shworg sorg = new Shworg();
		Shworg dorg = new Shworg();
		CDBConnection con = sorg.pool.getCon(CTreeUtil.class);
		con.startTrans();
		try {
			sorg.findByID(con, sorgid);
			if (sorg.isEmpty()) {
				throw new Exception("ID为【" + sorgid + "】的机构不存在");
			}
			dorg.findByID(con, dorgid);
			if (dorg.isEmpty()) {
				throw new Exception("ID为【" + dorgid + "】的机构不存在");
			}
			JSONObject dorg_s = dorg.toJsonObj();
			// dorg_s.put("extorgname", COShwUser.getOrgNamepath(dorg.idpath.getValue()));
			setOrgParentAsOrg(con, sorg, dorg_s);
			Shworg_acthis soh = new Shworg_acthis();
			soh.orgid.setValue(sorg.orgid.getValue());
			soh.acttype.setAsInt(4);
			soh.acttime.setAsDatetime(new Date());
			soh.actcommit.setValue("调整上级机构为" + dorg.orgid.getValue() + "," + dorg.code.getValue() + "," + dorg.orgname.getValue());
			soh.save(con);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}finally{
			con.close();
		}
	}

	private static void setOrgParentAsOrg(CDBConnection con, Shworg sorg, JSONObject dorg_s) throws Exception {
		String sidp = sorg.idpath.getValue();
		String didp = dorg_s.getString("idpath");
		if (didp.length() > sidp.length())// 只有目标长度大于源长度才有可能
			if (didp.substring(0, sidp.length()).equals(sidp)) {
				throw new Exception("目标机构【" + dorg_s.getString("orgname") + "】不能是源机构【" + sorg.orgname.getValue() + "】及其下属机构");
			}
		String sqlstr = "select * from shworg where idpath like '" + sorg.idpath.getValue() + "%'";
		JSONArray ms = con.opensql2jsontree_o(sqlstr, "orgid", "superid", false);
		if (ms.size() != 1)
			throw new Exception("IDPATH为【" + sorg.idpath.getValue() + "】的机构重复或不存在");
		JSONObject jorg = ms.getJSONObject(0);
		jorg.put("idpath", dorg_s.getString("idpath") + jorg.getString("orgid") + ",");
		jorg.put("superid", dorg_s.getString("orgid"));
		updateOrgInfo(con, jorg, dorg_s);
		sqlstr = "update shworg set superid=" + dorg_s.getString("orgid") + ", idpath='" + jorg.getString("idpath") + "' where orgid="
				+ jorg.getString("orgid");
		con.execsql(sqlstr);
		CTreeUtil.reSetIDPathLev(con, new OnChangeIDPathLevListener() {

			@Override
			public void OnChange(CDBConnection con, JSONObject jorg, JSONObject jporg) throws Exception {
				// TODO Auto-generated method stub
				updateOrgInfo(con, jorg, jporg);
			}

		}, jorg, sorg, "orgid", "idpath", null);
	}

	public static OnChangeOrgInfoListener getOrgChgListener() throws Exception {
		if (ConstsSw._onchgorginfolestener == null) {
			Object o = ConstsSw.getAppParm("OrgInfoChangeListener");
			if (o == null)
				return null;
			else {
				String className = o.toString().trim();
				Class<?> CJPAcd = Class.forName(className);
				if (!OnChangeOrgInfoListener.class.isAssignableFrom(CJPAcd)) {
					throw new Exception(className + "必须从 com.corsair.server.ctrl.OnChangeOrgInfoListener继承");
				}
				Class<?> paramTypes[] = {};
				Constructor<?> cst = CJPAcd.getConstructor(paramTypes);
				ConstsSw._onchgorginfolestener = (OnChangeOrgInfoListener) cst.newInstance();
			}
		}
		return ConstsSw._onchgorginfolestener;
	}

	public static void updateOrgInfo(CDBConnection con, JSONObject jorg, JSONObject jporg) throws Exception {
		String orgid = jorg.getString("orgid");
		String idpath = jorg.getString("idpath");
		jorg.put("extorgname", jporg.getString("extorgname") + "-" + jorg.getString("orgname"));
		String sqlstr = "UPDATE shwfdracl SET idpath='" + idpath + "', ownername='" + jorg.getString("orgname")
				+ "'  WHERE ownerid=" + orgid
				+ " AND acltype IN(1,2)";
		con.execsql(sqlstr);
		sqlstr = "UPDATE shworg_find SET fidpath='" + idpath + "' WHERE forgid=" + orgid;
		con.equals(sqlstr);
		OnChangeOrgInfoListener oic = getOrgChgListener();
		if (oic != null)
			oic.OnOrgChg(con, jorg, jporg);
	}

}
