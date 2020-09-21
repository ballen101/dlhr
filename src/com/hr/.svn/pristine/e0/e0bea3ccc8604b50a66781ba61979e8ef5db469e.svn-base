package com.hr.attd.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.attd.entity.Hrkq_holidaytype;

public class CtrHrkq_holidaytype extends JPAController {
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		Hrkq_holidaytype ht = new Hrkq_holidaytype();// 值为空，因为删除无需加载数据
		String sqlstr = "SELECT COUNT(*) ct FROM hrkq_holidayapp WHERE htid=" + id;
		if (Integer.valueOf((ht.pool.openSql2List(sqlstr).get(0).get("ct").toString())) != 0)
			throw new Exception("职等已经在请假申请中使用，不允许删除！");

		ht.findByID(id);
		if (ht.isEmpty())
			throw new Exception("没有找到ID为【" + id + "】的假期类型");

		if (ht.isbuildin.getAsIntDefault(0) == 1)
			throw new Exception("系统内置的假期类型不允许删除");

		return null;
	}
}
