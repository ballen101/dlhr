package com.hr.salary.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.salary.entity.Hr_salary_postsub;
import com.hr.salary.entity.Hr_salary_postsub_cancel;
import com.hr.salary.entity.Hr_salary_postsub_line;

public class CtrHr_salary_postsub extends JPAController{

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			Hr_salary_postsub ps=(Hr_salary_postsub)jpa;
			CtrSalaryCommon.newSalaryChangeLog(con,ps,true);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			Hr_salary_postsub ps=(Hr_salary_postsub)jpa;
			CtrSalaryCommon.newSalaryChangeLog(con,ps,true);
		}
	}
	
	public static void scanOutTimePostSub() throws Exception {
		Date ct =Systemdate.getDateByStr(Systemdate.getStrDate());
		Date firstdate = Systemdate.getFirstAndLastOfMonth(ct).date1;
		String fd = Systemdate.getStrDateyyyy_mm_dd(firstdate);
		String sqlstr = "SELECT * FROM `hr_salary_postsub` WHERE stat=9 AND usable=1 and enddate < '"+fd+"'";
		CJPALineData<Hr_salary_postsub> pss=new CJPALineData<Hr_salary_postsub>(Hr_salary_postsub.class);
		pss.findDataBySQL(sqlstr, true, false);
		CDBConnection con = DBPools.defaultPool().getCon("scanOutTimePostSub");
		con.startTrans();
		try {
			for (CJPABase jpa : pss) {
				Hr_salary_postsub ps=(Hr_salary_postsub)jpa;
				CJPALineData<Hr_salary_postsub_line> psls=new CJPALineData<Hr_salary_postsub_line>(Hr_salary_postsub_line.class);
				String sqlstr1="SELECT * FROM `hr_salary_postsub_line` WHERE isend=2 AND ps_id="+ps.ps_id.getValue();
				psls.findDataBySQL(con, sqlstr1, false, false);
				for(CJPABase psljpa : psls){
					Hr_salary_postsub_line psl=(Hr_salary_postsub_line)psljpa;
					Hr_salary_postsub_cancel psc=new Hr_salary_postsub_cancel();
					psc.canceldate.setValue(fd);
					psc.cancelreason.setValue("岗位津贴过期自动终止");
					psc.er_id.setValue(psl.er_id.getValue());
					psc.employee_code.setValue(psl.employee_code.getValue());
					psc.employee_name.setValue(psl.employee_name.getValue());
					psc.orgid.setValue(psl.orgid.getValue());
					psc.orgcode.setValue(psl.orgcode.getValue());
					psc.orgname.setValue(psl.orgname.getValue());
					psc.idpath.setValue(psl.idpath.getValue());
					psc.ospid.setValue(psl.ospid.getValue());
					psc.ospcode.setValue(psl.ospcode.getValue());
					psc.sp_name.setValue(psl.sp_name.getValue());
					psc.lv_id.setValue(psl.lv_id.getValue());
					psc.lv_num.setValue(psl.lv_num.getValue());
					psc.hwc_namezq.setValue(psl.hwc_namezq.getValue());
					psc.hwc_namezz.setValue(psl.hwc_namezz.getValue());
					psc.hiredday.setValue(psl.hiredday.getValue());
					psc.opostsub.setValue(psl.opostsub.getValue());
					psc.npostsub.setValue(psl.npostsub.getValue());
					psc.appreason.setValue(ps.appreason.getValue());
					psc.startdate.setValue(ps.startdate.getValue());
					psc.enddate.setValue(ps.enddate.getValue());
					psc.psl_id.setValue(psl.psl_id.getValue());
					psc.entid.setValue(ps.entid.getValue());
					psc.creator.setValue("DEV");
					psc.createtime.setValue(Systemdate.getStrDate());
					psc.save(con);
					String[] ign=new String[]{};
					psc.wfcreate(null, con, "763", "1", ign);
				}
				//CtrSalaryCommon.newSalaryChangeLog(con, ps, false);
				ps.usable.setValue(2);
				ps.save(con);
			}
			con.submit();
		}catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}
	

}
