package com.hr.base.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_standposition;
import com.hr.perm.entity.Hr_employee;

public class CtrHr_orgposition extends JPAController {
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_employee WHERE ospid=" + id;
		Hr_employee ep = new Hr_employee();
		int ct = Integer.valueOf(ep.pool.openSql2List(sqlstr).get(0).get("ct"));
		if (ct != 0) {
			throw new Exception("机构职位已经在使用中，不允许删除");
		}

		sqlstr = "SELECT COUNT(*) ct FROM hr_orgposition WHERE pid=" + id;
		Hr_orgposition op = new Hr_orgposition();
		ct = Integer.valueOf(op.pool.openSql2List(sqlstr).get(0).get("ct"));
		if (ct != 0) {
			throw new Exception("机构职位有下级职位，不允许删除");
		}

		return null;
	}

	
}
