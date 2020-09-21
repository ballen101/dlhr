package com.hr.salary.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.salary.entity.Hr_salary_linestarget;
import com.hr.salary.entity.Hr_salary_linestarget_line;
import com.hr.util.HRUtil;

public class CtrHr_salary_linestarget extends JPAController{

	@Override
	public void BeforeWFStart(CJPA jpa, String wftempid, CDBConnection con)
			throws Exception {
		// TODO Auto-generated method stub
		if(!checklinestargets(jpa,con)){
			throw new Exception("拉线可分配金额为空！请先核算可分配金额!");
		}
		checkdate(jpa,con);
	}
	
	private boolean checklinestargets(CJPA jpa, CDBConnection con) throws Exception{
		Hr_salary_linestarget lt=(Hr_salary_linestarget)jpa;
		CJPALineData<Hr_salary_linestarget_line> ltls=new CJPALineData<Hr_salary_linestarget_line>(Hr_salary_linestarget_line.class);
		String sqlstr="select * from hr_salary_linestarget_line where slt_id="+lt.slt_id.getValue();
		ltls.findDataBySQL(con, sqlstr, false, false);
		if(!ltls.isEmpty()){
			for(CJPABase ltl : ltls){
				Hr_salary_linestarget_line sltl=(Hr_salary_linestarget_line)ltl;
				if((sltl.standard.isEmpty())||(sltl.canpay.isEmpty())){
					return false;
				}
			}
		}
		
		
		return true;
	}

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
		Hr_salary_linestarget lt=(Hr_salary_linestarget)jpa;
		CJPALineData<Hr_salary_linestarget_line> ltls=lt.hr_salary_linestarget_lines;
		for(int i=0;i<ltls.size();i++){
			Hr_salary_linestarget_line ltl=(Hr_salary_linestarget_line)ltls.get(i);
			String sqlstr="SELECT COUNT(*) AS ct FROM `hr_salary_linestarget` lt,`hr_salary_linestarget_line` ltl"+
			" WHERE lt.stat=9 AND ltl.slt_id=lt.slt_id AND lt.applydate='"+lt.applydate.getValue()+"-01"+
			"' AND ltl.orgid="+ltl.orgid.getValue();
			int nums=Integer.valueOf(con.pool.openSql2List(sqlstr).get(0).get("ct"));
			if(nums>0){
				throw new Exception("在【"+lt.applydate.getValue()+"】月份已维护拉线【"+ltl.orgname.getValue()+"】的考核指标，请先作废后再重新录入。");
			}
		}
		
	}
	
	private void checkdate(CJPA jpa,  CDBConnection con) throws Exception{
		Hr_salary_linestarget lt=(Hr_salary_linestarget)jpa;
		CtrSalaryCommon.checkLineTargetValidDate(lt.applydate.getAsDate());
	}

}
