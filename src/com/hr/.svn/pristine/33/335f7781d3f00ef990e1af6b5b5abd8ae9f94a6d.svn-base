package com.hr.salary.ctr;

import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.salary.entity.Hr_salary_hotsub;
import com.hr.salary.entity.Hr_salary_hotsub_line;

public class CtrHr_salary_hotsub extends JPAController{
	@Override
	public void BeforeWFStart(CJPA jpa, String wftempid, CDBConnection con)
			throws Exception {
		// TODO Auto-generated method stub
		checkdate(jpa, con);
		checkemps(jpa,con);
	}
	
	private void checkdate(CJPA jpa,  CDBConnection con) throws Exception{
		Hr_salary_hotsub hs=(Hr_salary_hotsub)jpa;
		CtrSalaryCommon.checkHotSubValidDate(hs.applydate.getAsDate());
	}
	
	private void checkemps(CJPA jpa,  CDBConnection con) throws Exception{
		Hr_salary_hotsub hs=(Hr_salary_hotsub)jpa;
		CJPALineData<Hr_salary_hotsub_line> hsls=hs.hr_salary_hotsub_lines;
		for(int i=0;i<hsls.size();i++){
			Hr_salary_hotsub_line hsl=(Hr_salary_hotsub_line)hsls.get(i);
			String sqlstr="SELECT COUNT(*) AS ct FROM `hr_salary_hotsub` hs,`hr_salary_hotsub_line` hsl  "+
			" WHERE hs.applydate='"+hs.applydate.getValue()+"' AND hsl.hs_id=hs.hs_id AND hsl.er_id="+hsl.er_id.getValue()+" AND hs.stat>1";
			int ct=Integer.valueOf(con.opensql2json_o(sqlstr).getJSONObject(0).getString("ct"));
			if(ct>0){
				throw new Exception("在【"+hs.applydate.getValue()+"】月份里员工【"+hsl.employee_code.getValue()+"】已提交高温补贴申请，不能再次提交");
			}
		}
	}

}
