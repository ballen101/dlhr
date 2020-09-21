package com.hr.salary.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.salary.entity.Hr_salary_structure;

public class CtrHr_salary_structure extends JPAController{

	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		Hr_salary_structure ss=(Hr_salary_structure)jpa;
		ss.usable.setAsInt(2);
		return null;
	}
	

}
