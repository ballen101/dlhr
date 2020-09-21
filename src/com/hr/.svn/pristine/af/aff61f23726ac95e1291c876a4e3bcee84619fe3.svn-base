package com.hr.perm.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.perm.entity.Hr_empptjob_app;
import com.hr.perm.entity.Hr_empptjob_info;
import com.hr.salary.ctr.CtrSalaryCommon;
import com.hr.salary.entity.Hr_salary_parttimesub;

public class CtrHr_empptjob_app extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_empptjob_app) jpa).ptjacode.getValue());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4) {
			addNewPtjInfo(jpa, con);
			// addNewPartTimeSubs(jpa,con);
		}
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			addNewPtjInfo(jpa, con);
			// addNewPartTimeSubs(jpa,con);
		}
	}

	private void addNewPtjInfo(CJPA jpa, CDBConnection con) throws Exception {
		Hr_empptjob_app ja = (Hr_empptjob_app) jpa;
		CtrSalaryCommon.newSalaryChangeLog(con, ja, true);

		Hr_empptjob_info ji = new Hr_empptjob_info();
		ji.er_id.setValue(ja.er_id.getValue()); // 人事ID
		ji.odospid.setValue(ja.odospid.getValue()); // 主职位ID
		ji.newospid.setValue(ja.newospid.getValue()); // 兼职职位ID
		ji.startdate.setValue(ja.startdate.getValue()); // 开始时间
		ji.enddate.setValue(ja.enddate.getValue()); // 结束时间
		ji.ptjalev.setValue(ja.ptjalev.getValue()); // 兼职层级
		ji.ptjatype.setValue(ja.ptjatype.getValue()); // 兼职范围 1内部调用 2 跨单位 3
														// 跨模块/制造群
		ji.issubsidy.setValue(ja.issubsidy.getValue()); // 申请补贴
		ji.subsidyarm.setValue(ja.subsidyarm.getValue()); // 月度补贴金额
		ji.sourceid.setValue(ja.ptjaid.getValue()); // 申请单ID
		ji.ptjstat.setAsInt(1); // 兼职状态 兼职中 完成
		ji.idpath.setValue(ja.idpath.getValue()); // idpath
		ji.save(con);
	}

	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_empptjob_app ja = (Hr_empptjob_app) jpa;
		if (ja.applaydate.isEmpty())
			ja.applaydate.setAsDatetime(new Date());
		if (ja.idpath.isEmpty()) {
			Shworg org = new Shworg();
			org.findByID(ja.odorgid.getValue(), false);
			if (org.isEmpty())
				throw new Exception("没有找到ID为【" + ja.odorgid.getValue() + "】的机构");
			ja.idpath.setValue(org.idpath.getValue());
		}
	}

	
	public static void scanOutTimeApp() throws Exception {
		String sqlstr = "SELECT * FROM `hr_empptjob_app` WHERE `breaked`=2 AND enddate < CURDATE()";
		CJPALineData<Hr_empptjob_app> jas = new CJPALineData<Hr_empptjob_app>(Hr_empptjob_app.class);
		jas.findDataBySQL(sqlstr, true, false);
		CDBConnection con = DBPools.defaultPool().getCon("scanOutTimeApp");
		con.startTrans();
		try {
			for (CJPABase jpa : jas) {
				Hr_empptjob_app ja = (Hr_empptjob_app) jpa;
				CtrSalaryCommon.newSalaryChangeLog(con, ja, false);
				ja.breaked.setValue(1);
				ja.ptbbdate1.setValue(Systemdate.getStrDate());
				ja.save(con);
				Hr_empptjob_info ji = new Hr_empptjob_info();
				String sqlstr1 = "select * from hr_empptjob_info where sourceid=" + ja.ptjaid.getValue();
				ji.findBySQL(sqlstr1, false);
				if (!ji.isEmpty()) {
					ji.enddate.setAsDatetime(ja.ptbbdate1.getAsDatetime());
					ji.save(con);
				}
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private void addNewPartTimeSubs(CJPA jpa, CDBConnection con) throws Exception {
		Hr_empptjob_app ja = (Hr_empptjob_app) jpa;
		Hr_salary_parttimesub pts = new Hr_salary_parttimesub();
		pts.er_id.setValue(ja.er_id.getValue()); // 人事档案id
		pts.employee_code.setValue(ja.employee_code.getValue()); // 申请人工号
		pts.employee_name.setValue(ja.employee_name.getValue()); // 姓名
		pts.orgid.setValue(ja.odorgid.getValue()); // 部门
		pts.orgcode.setValue(ja.odorgcode.getValue()); // 部门编码
		pts.orgname.setValue(ja.odorgname.getValue()); // 部门名称
		pts.ospid.setValue(ja.odospid.getValue()); // 职位id
		pts.ospcode.setValue(ja.odospcode.getValue()); // 职位编码
		pts.sp_name.setValue(ja.odsp_name.getValue()); // 职位名称
		pts.lv_id.setValue(ja.odlv_id.getValue()); // 职级id
		pts.lv_num.setValue(ja.odlv_num.getValue()); // 职级
		pts.hiredday.setValue(ja.hiredday.getValue()); // 入职日期
		pts.ptsubs.setValue(ja.subsidyarm.getValue()); // 开始时间
		pts.startdate.setValue(ja.startdate.getValue()); // 开始时间
		pts.enddate.setValue(ja.enddate.getValue()); // 结束时间
		pts.substart.setValue(ja.substart.getValue()); // 补贴开始月份
		pts.subend.setValue(ja.subend.getValue()); // 补贴结束月份
		pts.idpath.setValue(ja.idpath.getValue()); // idpath
		pts.save(con);
		pts.wfcreate(null, con);
	}
}
