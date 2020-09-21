package com.hr.salary.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.salary.entity.Hr_salary_teamaward;
import com.hr.salary.entity.Hr_salary_teamaward_line;
import com.hr.salary.entity.Hr_salary_teamaward_line_emps;
import com.hr.util.HRUtil;

public class CtrHr_salary_teamaward extends JPAController{
	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		if (!HRUtil.hasRoles("19")) {// 薪酬管理员
			throw new Exception("当前登录用户没有权限使用该功能！");
		}
		return null;
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink)
			throws Exception {
		// TODO Auto-generated method stub
		Hr_salary_teamaward tw=(Hr_salary_teamaward)jpa;
		CJPALineData<Hr_salary_teamaward_line> twls=tw.hr_salary_teamaward_lines;
		float linettpay=0;
		for(int i=0;i<twls.size();i++){
			Hr_salary_teamaward_line twl=(Hr_salary_teamaward_line)twls.get(i);
			float lcp=twl.canpay.getAsFloatDefault(0);
			CJPALineData<Hr_salary_teamaward_line_emps> twles=twl.hr_salary_teamaward_line_empss;
			float empttpay=0;
			for(int j=0;j<twles.size();j++){
				Hr_salary_teamaward_line_emps twle=(Hr_salary_teamaward_line_emps)twles.get(j);
				float edp=twle.descriprev.getAsFloatDefault(0);
				float esp=lcp*edp/100;
				float erp=lcp*edp/100;
				int ispt=Integer.parseInt(twle.isparttime.getValue());
				if(ispt==1){//兼职
					int ptnums=Integer.parseInt(twle.ptnums.getValue());
					ptnums=ptnums+1;
					erp=erp/ptnums;
				}
				twle.realpay.setAsFloat(erp);
				twle.shouldpay.setAsFloat(esp);
				empttpay=empttpay+erp;
			}
			twl.lrealpay.setAsFloat(empttpay);
			linettpay=linettpay+empttpay;
		}
		tw.ttpay.setAsFloat(linettpay);
	}
	
	private void checkdate(CJPA jpa,  CDBConnection con) throws Exception{
		Hr_salary_teamaward tw=(Hr_salary_teamaward)jpa;
		CtrSalaryCommon.checkTeamWardValidDate(tw.applydate.getAsDate());
	}

	@Override
	public void BeforeWFStart(CJPA jpa, String wftempid, CDBConnection con)
			throws Exception {
		// TODO Auto-generated method stub
		checkdate(jpa,con);
	}

	
}
