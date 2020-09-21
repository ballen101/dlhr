package com.hr.base.ctr;

import java.util.ArrayList;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.base.entity.Hr_wclass;

public class CtrHr_wclass extends JPAController {
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		String sqlstr = "SELECT count(*) ct FROM hr_wclass WHERE pid=" + id;
		Hr_wclass wc = new Hr_wclass();
		if (Integer.valueOf((wc.pool.openSql2List(sqlstr).get(0).get("ct").toString())) != 0)
			throw new Exception("有下级分类不允许删除！");
		sqlstr = "SELECT COUNT(*) ct FROM hr_standposition WHERE (hwc_idzl=" + id + " OR hwc_idzq=" + id + " OR hwc_idzz=" + id + ")";
		if (Integer.valueOf((wc.pool.openSql2List(sqlstr).get(0).get("ct").toString())) != 0)
			throw new Exception("已经在标准职位库中使用，不允许删除！");
		return null;
	}

	@Override
	public void OnSave(CJPABase jpa, CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
		Hr_wclass wc = (Hr_wclass) jpa;
//		String sqlstr = "SELECT count(*) ct FROM hr_wclass WHERE hwc_name='" + wc.hwc_name.getValue() + "' AND type_id=" + wc.type_id.getValue();
//		if (!wc.hwc_id.isEmpty()) {
//			sqlstr = sqlstr + "	AND hwc_id<>" + wc.hwc_id.getValue();
//		}
//		int type_id = wc.type_id.getAsInt();
//		String em = null;
//		if (type_id == 1)
//			em = "职类";
//		else if (type_id == 2)
//			em = "职群";
//		else if (type_id == 3)
//			em = "职种";
//		else
//			throw new Exception("【type_id】错误");
//		if (Integer.valueOf((wc.pool.openSql2List(sqlstr).get(0).get("ct").toString())) != 0)
//			throw new Exception("同名【" + em + "】【" + wc.hwc_name.getValue() + "】,不允许保存！！");
	}
}
