package com.hr.salary.co;

import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.hr.salary.entity.Hr_salary_specchgsa;

@ACO(coname = "web.hr.salaryscs")
public class COHr_salary_specchgsa {
	@ACOAction(eventname = "findspecchgsaconst", Authentication = true, notes = "获取特殊调薪考核项")
	public String findspecchgsaconst() throws Exception {
		String sqlstr = "select itemname khitem,itemnote khnote from hr_salary_specchgsa_khconst order by pkcid";
		return (new Hr_salary_specchgsa()).pool.opensql2json(sqlstr);
	}

}
