package com.hr.base.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;

public class CtrHr_relative extends JPAController {
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		throw new Exception("不允许删除！");
	}
}
