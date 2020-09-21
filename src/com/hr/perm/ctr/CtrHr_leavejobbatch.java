package com.hr.perm.ctr;

import java.sql.Types;
import java.util.Date;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_employeestat;
import com.hr.perm.entity.Hr_black_add;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_leavejob;
import com.hr.perm.entity.Hr_leavejobbatch;
import com.hr.perm.entity.Hr_leavejobbatchline;

public class CtrHr_leavejobbatch extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_leavejobbatch) jpa).ljbcode.getValue());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			doLeaveex(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			doLeaveex(jpa, con);
	}

	private void doLeaveex(CJPA jpa, CDBConnection con) throws Exception {
		Hr_leavejobbatch lvb = (Hr_leavejobbatch) jpa;
		CJPALineData<Hr_leavejobbatchline> ls = lvb.hr_leavejobbatchlines;
		Hr_employee emp = new Hr_employee();
		Hr_leavejob lj = new Hr_leavejob();
		for (CJPABase j : ls) {
			Hr_leavejobbatchline l = (Hr_leavejobbatchline) j;
			emp.findByID4Update(con, l.er_id.getValue(), false);
			if (emp.isEmpty()) {
				throw new Exception("ID为【" + l.er_id.getValue() + "】的人事档案不存在");
			}
			// 自动生成单个离职单并提交
			lj.clear();
			lj.ljtype.setValue(lvb.ljbtype.getValue()); // 离职类型 1 实习生 2 员工
			lj.orgid.setValue(emp.orgid.getValue()); // 部门ID
			lj.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
			lj.orgname.setValue(emp.orgname.getValue()); // 部门名称
			lj.er_id.setValue(emp.er_id.getValue()); // 档案ID
			lj.er_code.setValue(emp.er_code.getValue()); // 档案编码
			lj.employee_code.setValue(emp.employee_code.getValue()); // 工号
			lj.sex.setValue(emp.sex.getValue()); // 性别
			lj.id_number.setValue(emp.id_number.getValue()); // 身份证号
			lj.employee_name.setValue(emp.employee_name.getValue()); // 姓名
			lj.degree.setValue(emp.degree.getValue()); // 学历
			lj.birthday.setValue(emp.birthday.getValue()); // 出生日期
			lj.registeraddress.setValue(emp.registeraddress.getValue()); // 户籍住址
			lj.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
			lj.ljappdate.setValue(l.ljappdate.getValue()); // 申请离职日期
			lj.ljdate.setValue(l.ljdate.getValue()); // 离职日期
			if (l.kqdate_end.isEmpty())
				lj.kqdate_end.setAsDatetime(l.ljdate.getAsDatetime());// 考勤截止日期
			else
				lj.kqdate_end.setAsDatetime(l.kqdate_end.getAsDatetime());
			lj.worktime.setValue(l.worktime.getValue()); // 司龄
			lj.ljtype1.setValue(l.ljtype1.getValue()); // 离职方式 自离 辞职 等
			lj.ljtype2.setValue(l.ljtype2.getValue()); // 离职类型 自离 辞职 等
			lj.ljreason.setValue(l.ljreason.getValue()); // 离职原因
			lj.iscpst.setValue(l.iscpst.getValue()); // 是否补偿
			lj.cpstarm.setValue(l.cpstarm.getValue()); // 补偿金额
			lj.iscpt.setValue(l.iscpt.getValue()); // 是否投诉
			lj.isabrt.setValue(l.isabrt.getValue()); // 是否仲裁
			lj.islawsuit.setValue(l.islawsuit.getValue()); // 是否诉讼
			lj.casenum.setValue(l.casenum.getValue()); // 案号
			lj.isblacklist.setValue(l.isblacklist.getValue()); // 是否加入黑名单
			lj.blackreason.setValue(l.blackreason.getValue()); // 加入黑名单原因
			lj.iscanced.setAsInt(2); // 已撤销
			lj.pempstatid.setValue(emp.empstatid.getValue()); // 离职前状态
			lj.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
			lj.lv_num.setValue(emp.lv_num.getValue()); // 职级
			lj.hg_id.setValue(emp.hg_id.getValue()); // 职等ID
			lj.hg_code.setValue(emp.hg_code.getValue()); // 职等编码
			lj.hg_name.setValue(emp.hg_name.getValue()); // 职等名称
			lj.ospid.setValue(emp.ospid.getValue()); // 职位ID
			lj.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
			lj.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
			lj.hwc_namezl.setValue(emp.hwc_namezl.getValue());// 职类
			lj.remark.setValue("批量离职【" + lvb.ljbcode.getValue() + "】生成"); // 备注
			lj.stat.setAsInt(1); // 表单状态
			lj.idpath.setValue(emp.idpath.getValue()); // idpath
			lj.entid.setAsInt(1); // entid
			lj.creator.setValue("SYSTEM"); // 制单人
			lj.createtime.setAsDatetime(new Date()); // 制单时间
			lj.save(con);
			lj.wfcreate(null, con);
		}
	}

	private void doLeaveold(CJPA jpa, CDBConnection con) throws Exception {
		Hr_leavejobbatch lvb = (Hr_leavejobbatch) jpa;
		CJPALineData<Hr_leavejobbatchline> ls = lvb.hr_leavejobbatchlines;
		Hr_employee emp = new Hr_employee();
		for (CJPABase j : ls) {
			Hr_leavejobbatchline l = (Hr_leavejobbatchline) j;
			emp.findByID4Update(con, l.er_id.getValue(), false);
			if (emp.isEmpty()) {
				throw new Exception("ID为【" + l.er_id.getValue() + "】的人事档案不存在");
			}

			Hr_employeestat es = new Hr_employeestat();
			es.findBySQL("select * from hr_employeestat where statvalue=" + emp.empstatid.getValue());

			if (lvb.ljbtype.getAsInt() == 1) {
				if (es.allowsxlv.getAsInt() != 1) {
					throw new Exception("状态为【" + es.language1.getValue() + "】的离职表单不允许做实习离职操作！");
				}
			} else if (lvb.ljbtype.getAsInt() == 2) {
				// System.out.println(es.tojson());
				if (es.allowlv.getAsInt() != 1) {
					throw new Exception("状态为【" + es.language1.getValue() + "】的离职表单不允许做离职操作！");
				}
			} else
				throw new Exception("离职表单【离职类型】错误");

			if (l.isblacklist.getAsInt() == 1) {
				addblackinfo(lvb, l, emp, con);
				emp.empstatid.setAsInt(13);
			} else
				emp.empstatid.setAsInt(12);
			emp.save(con);
			CtrHr_employee_contract_stop.stopContract(con, emp.er_id.getValue(), "离职终止合同");
			// ell.clear();
			// ell.assignfield(emp, true);
			// ell.save(con);
			// emp.delete(con, false);
		}

	}

	private void addblackinfo(Hr_leavejobbatch lvb, Hr_leavejobbatchline l, Hr_employee emp, CDBConnection con) throws Exception {
		Hr_black_add ba = new Hr_black_add();
		ba.addappdate.setAsDatetime(new Date());// 加封申请日期
		ba.adddate.setAsDatetime(new Date());// 加封生效日期
		ba.blackreason.setValue("");// 加入黑名单原因
		ba.allwf.setValue("2");// 是否启动流程，用来控制需要加入黑名单离职表单审批通过后自动加黑
		ba.billtype.setAsInt(2);// 批量离职
		ba.ljid.setValue(l.ljblid.getValue());// 离职行ID
		ba.ljcode.setValue(lvb.ljbcode.getValue()); // 离职编码
		ba.ljreason.setValue(l.ljreason.getValue()); // 离职原因
		ba.orgid.setValue(lvb.orgid.getValue()); // 部门ID
		ba.orgcode.setValue(lvb.orgcode.getValue());// 部门编码
		ba.orgname.setValue(lvb.orgname.getValue());// 部门名称
		ba.er_id.setValue(l.er_id.getValue()); // 档案ID
		ba.er_code.setValue(emp.er_code.getValue());// 档案编码
		ba.employee_code.setValue(emp.employee_code.getValue()); // 工号
		ba.sex.setValue(emp.sex.getValue()); // 性别
		ba.id_number.setValue(emp.id_number.getValue()); // 身份证号
		ba.employee_name.setValue(emp.employee_name.getValue()); // 姓名
		ba.degree.setValue(emp.degree.getValue()); // 学历
		ba.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
		ba.ljdate.setValue(l.ljdate.getValue()); // 离职日期
		ba.ljtype1.setValue(l.ljtype1.getValue()); // 离职方式 自离 辞职 等
		ba.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
		ba.lv_num.setValue(emp.lv_num.getValue()); // 职级
		ba.hg_id.setValue(emp.hg_id.getValue()); // 职等ID
		ba.hg_code.setValue(emp.hg_code.getValue()); // 职等编码
		ba.hg_name.setValue(emp.hg_name.getValue()); // 职等名称
		ba.ospid.setValue(emp.ospid.getValue()); // 职位ID
		ba.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
		ba.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
		ba.lvlev.setAsInt(0); // 离职层级
		ba.orghrlev.setAsInt(0); // 人事机构层级
		ba.remark.setValue("批量离职表单【" + lvb.ljbcode.getValue() + "】自动生成加黑记录"); // 备注
		ba.creator.setValue("SYSTEM");// 制单人
		ba.createtime.setAsDatetime(new Date()); // 制单时间
		ba.save(con);
		ba.wfcreate(null, con);
	}
}
