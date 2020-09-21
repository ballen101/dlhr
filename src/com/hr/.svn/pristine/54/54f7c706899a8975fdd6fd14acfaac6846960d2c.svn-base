package com.hr.base.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.base.entity.Hr_grade;

public class CtrHr_grade extends JPAController {
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String id) throws Exception {
		Hr_grade hg = new Hr_grade();
		String sqlstr = "SELECT COUNT(*) ct FROM hr_employee WHERE hg_id=" + id;
		if (Integer.valueOf((hg.pool.openSql2List(sqlstr).get(0).get("ct").toString())) != 0)
			throw new Exception("职等已经在【人事档案】中使用，不允许删除！");
		sqlstr = "SELECT COUNT(*) ct FROM hr_standposition WHERE hg_id=" + id;
		if (Integer.valueOf((hg.pool.openSql2List(sqlstr).get(0).get("ct").toString())) != 0)
			throw new Exception("职等已经在【标准职位】中使用，不允许删除！");

		return null;
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_grade hg = (Hr_grade) jpa;
		String sqlstr = "SELECT count(*) ct FROM hr_grade WHERE hg_name='" + hg.hg_name.getValue() + "'";
		if (!hg.hg_id.isEmpty())
			sqlstr = sqlstr + " and hg_id<>" + hg.hg_id.getValue();
		if (Integer.valueOf((con.openSql2List(sqlstr).get(0).get("ct").toString())) != 0)
			throw new Exception("名称为【" + hg.hg_name.getValue() + "】的职等已经存在！");
	}
}
