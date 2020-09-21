package com.hr.base.ctr;

import java.util.ArrayList;
import java.util.List;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_standposition;
import com.hr.perm.entity.Hr_employee;

public class CtrHr_standposition extends JPAController {
	public static String getNewCode(Hr_standposition sdp) {
		int codelen = 4;
		String ncode = "000000";
		try {
			String zzcode = sdp.hw_codezz.getValue();
			String sqlstr = "select max(sp_code) sp_code  from hr_standposition where sp_code like '" + zzcode + "%'";
			String oldcode = sdp.pool.openSql2List(sqlstr).get(0).get("sp_code");
			int curid = 0;
			if (oldcode == null) {
				curid = 1;
			} else {
				curid = Integer.valueOf(oldcode.substring(oldcode.length() - codelen)) + 1;
			}
			String ts = ncode + String.valueOf(curid);
			return zzcode + ts.substring(ts.length() - codelen);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_standposition sp = (Hr_standposition) jpa;
		Hr_orgposition op = new Hr_orgposition();
		Hr_employee ep = new Hr_employee();

		String[] opfds = { "sp_code", "sp_name", "gtitle", "lv_id", "lv_num", "hg_id", "hg_code", "hg_name", "hwc_idzl", "hw_codezl", "hwc_namezl",
				"hwc_idzq", "hw_codezq", "hwc_namezq", "hwc_idzz", "hw_codezz", "hwc_namezz", "isadvtech", "isoffjob", "issensitive",
				"iskey", "ishighrisk", "isneedadtoutwork", "isdreamposition", "maxage", "minage", "mindegree", "usable" };
		String[] epfds = { "hwc_namezl", "lv_id", "lv_num", "hg_id", "hg_code",
				"hg_name", "sp_name", "iskey", "hwc_namezq", "hwc_namezz" };
		if (!sp.sp_id.isEmpty()) {
			List<String> sqls = new ArrayList<String>();
			String sqlstr = "UPDATE hr_orgposition SET ";
			sqlstr = sqlstr + CJPASqlUtil.getFieldSQLUpdate(op, opfds, sp);
			sqlstr = sqlstr + " WHERE sp_id=" + sp.sp_id.getValue();
			sqls.add(sqlstr);

			String emnature = (sp.isoffjob.getAsIntDefault(0) == 1) ? "脱产" : "非脱产";

			sqlstr = "UPDATE hr_employee SET ";
			sqlstr = sqlstr + " emnature='" + emnature + "',";
			sqlstr = sqlstr + CJPASqlUtil.getFieldSQLUpdate(ep, epfds, sp);
			sqlstr = sqlstr + " WHERE ospid IN (SELECT ospid FROM `hr_orgposition` WHERE sp_id=" + sp.sp_id.getValue() + ")";
			sqls.add(sqlstr);

			con.execSqls(sqls);
		}
		// String sqlstr = "select count(*) ct from hr_standposition where hwc_idzl=" + sp.hwc_idzl.getValue() + " and sp_name='" + sp.sp_name.getValue() +
		// "'";
		// if (!sp.sp_id.isEmpty()) {
		// sqlstr = sqlstr + " and sp_id<>" + sp.sp_id.getValue();
		// }
		// if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) != 0) {
		// throw new Exception("同一职类下已经存在职位【" + sp.sp_name.getValue() + "】");
		// }
	}

	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_orgposition WHERE sp_id=" + id;
		Hr_orgposition op = new Hr_orgposition();
		int ct = Integer.valueOf(op.pool.openSql2List(sqlstr).get(0).get("ct"));
		if (ct != 0) {
			throw new Exception("标准职位已经在使用中，不允许删除");
		}
		return null;
	}
}
