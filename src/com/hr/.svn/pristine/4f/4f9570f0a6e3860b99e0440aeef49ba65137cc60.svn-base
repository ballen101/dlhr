package com.hr.salary.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.salary.entity.Hr_salary_hotsub_qual;
import com.hr.salary.entity.Hr_salary_hotsub_qual_cancel;
import com.hr.salary.entity.Hr_salary_hotsub_qual_line;
import com.hr.util.HRUtil;

public class CtrHr_salary_hotsub_qual extends JPAController{
	public static void scanOutTimeHotSubQual() throws Exception {
		Date ct =Systemdate.getDateByStr(Systemdate.getStrDate());
		Date firstdate = Systemdate.getFirstAndLastOfMonth(ct).date1;
		String fd = Systemdate.getStrDateyyyy_mm_dd(firstdate);
		String sqlstr = "SELECT * FROM `hr_salary_hotsub_qual` WHERE stat=9 AND  enddate< '"+fd+"'";
		CJPALineData<Hr_salary_hotsub_qual> hsqs=new CJPALineData<Hr_salary_hotsub_qual>(Hr_salary_hotsub_qual.class);
		hsqs.findDataBySQL(sqlstr, true, false);
		CDBConnection con = DBPools.defaultPool().getCon("scanOutTimeHotSubQual");
		con.startTrans();
		try {
			for (CJPABase jpa : hsqs) {
				Hr_salary_hotsub_qual hsq=(Hr_salary_hotsub_qual)jpa;
				CJPALineData<Hr_salary_hotsub_qual_line> hsqls=new CJPALineData<Hr_salary_hotsub_qual_line>(Hr_salary_hotsub_qual_line.class);
				hsqls.findDataBySQL(con, "select * from hr_salary_hotsub_qual_line where hsq_id="+hsq.hsq_id.getValue(), false, false);
				if(!hsqls.isEmpty()){
					for(CJPABase qljpa : hsqls){
						Hr_salary_hotsub_qual_line hsql=(Hr_salary_hotsub_qual_line)qljpa;
						Hr_salary_hotsub_qual_cancel hsqc=new Hr_salary_hotsub_qual_cancel();
						hsqc.canceldate.setValue(fd);
						hsqc.cancelreason.setValue("高温补贴资格过期自动终止");
						hsqc.orgid.setValue(hsq.orgid.getValue());
						hsqc.orgcode.setValue(hsq.orgcode.getValue());
						hsqc.orgname.setValue(hsq.orgname.getValue());
						hsqc.idpath.setValue(hsq.idpath.getValue());
						hsqc.sp_id.setValue(hsql.sp_id.getValue());
						hsqc.sp_code.setValue(hsql.sp_code.getValue());
						hsqc.sp_name.setValue(hsql.sp_name.getValue());
						hsqc.substype.setValue(hsq.substype.getValue());
						hsqc.substand.setValue(hsql.substand.getValue());
						hsqc.applyreason.setValue(hsq.applyreason.getValue());
						hsqc.startdate.setValue(hsq.startdate.getValue());
						hsqc.enddate.setValue(hsq.enddate.getValue());
						hsqc.hsql_id.setValue(hsql.hsql_id.getValue());
						hsqc.entid.setValue(hsq.entid.getValue());
						hsqc.creator.setValue("DEV");
						hsqc.createtime.setValue(Systemdate.getStrDate());
						hsqc.save(con);
						String[] ign=new String[]{};
						hsqc.wfcreate(null, con, "763", "1", ign);
					}
				}
				hsq.usable.setValue(2);
				//hsq.stat.setValue(12);
				hsq.save(con);
			}
			con.submit();
		}catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}
	
	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		// TODO Auto-generated method stub
		if (!HRUtil.hasRoles("19")) {// 薪酬管理员
			throw new Exception("当前登录用户没有权限使用该功能！");
		}
		return null;
	}

}
