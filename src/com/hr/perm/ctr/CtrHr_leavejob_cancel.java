package com.hr.perm.ctr;

import java.sql.Types;
import java.util.Date;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_black_del;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_leavejob;
import com.hr.perm.entity.Hr_leavejob_cancel;

public class CtrHr_leavejob_cancel extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		Hr_leavejob_cancel et = (Hr_leavejob_cancel) jpa;
		String sbjet = et.ljccode.getValue() + "-" + et.employee_name.getValue();
		wf.subject.setValue(sbjet);
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			doCancel(jpa, con);
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished)
			doCancel(jpa, con);
	}

	private void doCancel(CJPA jpa, CDBConnection con) throws Exception {
		Hr_leavejob_cancel lvc = (Hr_leavejob_cancel) jpa;
		// Hr_employee_leved hel = new Hr_employee_leved();
		// hel.findBySQL("select * from hr_employee_leved where er_id=" + lvc.er_id.getValue());
		// if (hel.isEmpty()) {
		// throw new Exception("ID为【" + lvc.er_id.getValue() + "】的人事档案不存在");
		// }

		Hr_employee emp = new Hr_employee();
		emp.findByID4Update(con, lvc.er_id.getValue(), false);
		if (emp.isEmpty()) {
			throw new Exception("ID为【" + lvc.er_id.getValue() + "】的人事档案不存在");
		}

		// 检查是否有在职状态的相同身份证人在
		Hr_employee empt = CtrHrEmployeeUtil.getIDNumberIsFrmal(emp.id_number.getValue());
		if (!empt.isEmpty())
			throw new Exception("已经存在该身份证的在职职工，不能取消入职");

		Hr_leavejob lv = new Hr_leavejob();
		lv.findByID4Update(con, lvc.ljid.getValue(), false);
		if (lv.isEmpty())
			throw new Exception("ID为【" + lvc.ljid.getValue() + "】的离职表单不存在");
		if (lv.iscanced.getAsInt() == 1)
			throw new Exception("离职表单已经撤销，不允许重复撤销！");

		if (lv.isblacklist.getAsInt() == 1)
			delBlackList(lvc, lv, con);
		emp.empstatid.setValue(lv.pempstatid.getValue());
		emp.ljdate.setValue(null);
		emp.kqdate_end.setValue(null);
		lv.iscanced.setAsInt(1);
		emp.save(con);
		lv.save(con);
	}

	private void delBlackList(Hr_leavejob_cancel lvc, Hr_leavejob lv, CDBConnection con) throws Exception {
		// CtrHrEmployeeUtil.removeBlackList(con, lvc.id_number.getValue());
		Hr_black_del bd = new Hr_black_del();
		bd.delappdate.setAsDatetime(new Date()); //
		bd.deldate.setAsDatetime(new Date()); // 加封生效日期
		bd.blackreason.setValue(lv.blackreason.getValue()); // 加封原因
		bd.blackdellreason.setValue("取消离职"); // 解封原因
		bd.billtype.setAsInt(1);// 单个离职
		bd.ljid.setValue(lv.ljid.getValue()); // 离职ID
		bd.ljcode.setValue(lv.ljcode.getValue()); // 离职编码
		bd.ljreason.setValue(lv.ljreason.getValue()); // 离职原因
		bd.orgid.setValue(lv.orgid.getValue()); // 部门ID
		bd.orgcode.setValue(lv.orgcode.getValue()); // 部门编码
		bd.orgname.setValue(lv.orgname.getValue()); // 部门名称
		bd.orghrlev.setValue("0"); // 机构人事层级
		bd.empflev.setValue("0"); // 员工层级
		bd.er_id.setValue(lv.er_id.getValue()); // 档案ID
		bd.er_code.setValue(lv.er_code.getValue()); // 档案编码
		bd.employee_code.setValue(lv.employee_code.getValue()); // 工号
		bd.sex.setValue(lv.sex.getValue()); // 性别
		bd.id_number.setValue(lv.id_number.getValue()); // 身份证号
		bd.employee_name.setValue(lv.employee_name.getValue()); // 姓名
		bd.degree.setValue(lv.degree.getValue()); // 学历
		bd.hiredday.setValue(lv.hiredday.getValue()); // 入职日期
		bd.ljdate.setValue(lv.ljdate.getValue()); // 离职日期
		bd.ljtype1.setValue(lv.ljtype1.getValue()); // 离职方式 自离 辞职 等
		bd.lv_id.setValue(lv.lv_id.getValue()); // 职级ID
		bd.lv_num.setValue(lv.lv_num.getValue()); // 职级
		bd.hg_id.setValue(lv.hg_id.getValue()); // 职等ID
		bd.hg_code.setValue(lv.hg_code.getValue()); // 职等编码
		bd.hg_name.setValue(lv.hg_name.getValue()); // 职等名称
		bd.ospid.setValue(lv.ospid.getValue()); // 职位ID
		bd.ospcode.setValue(lv.ospcode.getValue()); // 职位编码
		bd.sp_name.setValue(lv.sp_name.getValue()); // 职位名称
		bd.save(con);
		bd.wfcreate(null, con);
	}
}
