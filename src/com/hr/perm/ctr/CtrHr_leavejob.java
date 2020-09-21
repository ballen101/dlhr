package com.hr.perm.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.access.co.COHr_access_emauthority_list;
import com.hr.access.ctr.UtilAccess;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.base.entity.Hr_employeestat;
import com.hr.card.co.COHr_ykt_card_loss;
import com.hr.insurance.entity.Hr_ins_buyins;
import com.hr.insurance.entity.Hr_ins_buyins_line;
import com.hr.insurance.entity.Hr_ins_buyinsurance;
import com.hr.insurance.entity.Hr_ins_cancel;
import com.hr.perm.entity.Hr_black_add;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_entry_prob;
import com.hr.perm.entity.Hr_leavejob;
import com.hr.util.HRJPAEventListener;
import com.hr.util.HRUtil;

public class CtrHr_leavejob extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_leavejob) jpa).ljcode.getValue());
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4) {
			doLeave(jpa, con);
			// docancelinsurance(jpa, con);
			docancelmealapply(jpa, con);
		}
	}

	// 不管是否有流程都会触发
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		dochekrole(jpa, con);
		if (isFilished) {
			doLeave(jpa, con);
			// docancelinsurance(jpa, con);
			docancelmealapply(jpa, con);
		}
	}

	private void dochekrole(CJPA jpa, CDBConnection con) throws Exception {
		Hr_leavejob lj = (Hr_leavejob) jpa;
		if (lj.ljappdate.getAsDate().getTime() > lj.ljdate.getAsDate().getTime())
			throw new Exception("申请日期不能大于离职日期");

		if (HRUtil.hasRoles("2"))// 招募中心人员 和 管理员
			return;

		int ljtype1 = lj.ljtype1.getAsIntDefault(0);
		if (ljtype1 == 8)
			throw new Exception("只有角色为【招募中心人员】的登录用户允许提交【离职类别】为【招募中心自离】的表单");
	}

	private void doLeave(CJPA jpa, CDBConnection con) throws Exception {
		Hr_leavejob lj = (Hr_leavejob) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID4Update(con, lj.er_id.getValue(), false);
		if (emp.isEmpty()) {
			throw new Exception("ID为【" + lj.er_id.getValue() + "】的人事档案不存在");
		}

		Hr_employeestat es = new Hr_employeestat();
		es.findBySQL("select * from hr_employeestat where statvalue=" + emp.empstatid.getValue());

		if (lj.ljtype.getAsInt() == 1) {
			if (es.allowsxlv.getAsInt() != 1) {
				throw new Exception("状态为【" + es.language1.getValue() + "】的离职表单不允许做实习离职操作！");
			}
		} else if (lj.ljtype.getAsInt() == 2) {
			// System.out.println(es.tojson());
			if (es.allowlv.getAsInt() != 1) {
				throw new Exception("状态为【" + es.language1.getValue() + "】的离职表单不允许做离职操作！");
			}
		} else
			throw new Exception("离职表单【离职类型】错误");

		checklvdate(lj);

		if (lj.isblacklist.getAsInt() == 1) {
			addblackinfo(lj, emp, con);
			emp.empstatid.setAsInt(13);
		} else
			emp.empstatid.setAsInt(12);
		emp.ljdate.setValue(lj.ljdate.getValue());
		if (lj.kqdate_end.isEmpty())
			emp.kqdate_end.setAsDatetime(lj.ljdate.getAsDatetime());
		else
			emp.kqdate_end.setAsDatetime(lj.kqdate_end.getAsDatetime());
		emp.save(con);

		CtrHr_employee_contract_stop.stopContract(con, emp.er_id.getValue(), "离职终止合同");

		if (lj.ljtype1.getAsIntDefault(0) == 1)// 自离
			COHr_ykt_card_loss.doLeaveLoss(con, emp.er_id.getValue(), "离职自动挂失");
		else
			UtilAccess.disableAllAccessByEmpid(con, emp.er_id.getAsInt(), "离职禁用权限");
		// COHr_access_emauthority_list.leaveAccess(con, emp.employee_code.getValue());

		Shwuser user = new Shwuser();
		String sqlstr = "SELECT * FROM shwuser WHERE username='" + emp.employee_code.getValue() + "' for update";
		user.findBySQL4Update(con, sqlstr, false);
		if (!user.isEmpty()) {
			user.actived.setAsInt(2);
			user.save(con, false);
		}

		// 卡自动挂失

		// 失效关联关系
		sqlstr = "update hrrl_declare set useable=2,disusetime=now(),disuseremark='离职' where useable=1 and (rer_id="
				+ emp.er_id.getValue()
				+ " or er_id=" + emp.er_id.getValue() + ")";
		con.execsql(sqlstr);
		// 删除通讯录
		sqlstr = "DELETE FROM hr_interfaceempaddr WHERE employee_code='" + emp.employee_code.getValue() + "'";
		con.execsql(sqlstr);

	}

	private void checklvdate(Hr_leavejob lj) throws Exception {
		// 检查登录用户角色
		if (HRUtil.hasRoles("59"))// 单据维护员 和 管理员
			return;
		int days = Integer.valueOf(HrkqUtil.getParmValue("HRALLOW_LEIVE_DAYS"));// 允许提交当前时间前后X天的离职表单
		if (days == 0)
			return;
		Date now = new Date();
		Date ljdate = lj.ljdate.getAsDate();
		Date pd = Systemdate.dateDayAdd(ljdate, days * -1);
		Date nd = Systemdate.dateDayAdd(ljdate, days + 1);
		if ((now.getTime() < pd.getTime()) || (now.getTime() > nd.getTime())) {
			throw new Exception("只允许提交前后【" + days + "】天的离职表单");
		}
	}

	public static void main(String[] args) throws Exception {
		Date ljdate = Systemdate.getDateYYYYMMDD(new Date());
		Date pd = Systemdate.dateDayAdd(ljdate, 2);
		System.out.println(Systemdate.getStrDate(pd));
	}

	private void addblackinfo(Hr_leavejob lj, Hr_employee emp, CDBConnection con) throws Exception {
		Hr_black_add ba = new Hr_black_add();
		ba.addappdate.setAsDatetime(new Date());// 加封申请日期
		ba.adddate.setAsDatetime(new Date());// 加封生效日期
		ba.blackreason.setValue("");// 加入黑名单原因
		ba.allwf.setValue("2");// 是否启动流程，用来控制需要加入黑名单离职表单审批通过后自动加黑
		ba.billtype.setAsInt(1);// 单个离职
		ba.ljid.setValue(lj.ljid.getValue());// 离职ID
		ba.ljcode.setValue(lj.ljcode.getValue()); // 离职编码
		ba.ljreason.setValue(lj.ljreason.getValue()); // 离职原因
		ba.orgid.setValue(lj.orgid.getValue()); // 部门ID
		ba.orgcode.setValue(lj.orgcode.getValue());// 部门编码
		ba.orgname.setValue(lj.orgname.getValue());// 部门名称
		ba.er_id.setValue(lj.er_id.getValue()); // 档案ID
		ba.er_code.setValue(emp.er_code.getValue());// 档案编码
		ba.employee_code.setValue(emp.employee_code.getValue()); // 工号
		ba.sex.setValue(emp.sex.getValue()); // 性别
		ba.id_number.setValue(emp.id_number.getValue()); // 身份证号
		ba.employee_name.setValue(emp.employee_name.getValue()); // 姓名
		ba.degree.setValue(emp.degree.getValue()); // 学历
		ba.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
		ba.ljdate.setValue(lj.ljdate.getValue()); // 离职日期
		ba.ljtype1.setValue(lj.ljtype1.getValue()); // 离职方式 自离 辞职 等
		ba.addtype.setValue(lj.addtype.getValue());
		ba.addtype1.setValue(lj.addtype1.getValue());
		ba.blackreason.setValue(lj.blackreason.getValue());
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
		ba.remark.setValue("离职表单【" + lj.ljcode.getValue() + "】自动生成加黑记录"); // 备注
		ba.creator.setValue("SYSTEM");// 制单人
		ba.createtime.setAsDatetime(new Date()); // 制单时间
		ba.save(con);
		ba.wfcreate(null, con);
	}

	// 离职生成退保单
	private void docancelinsurance(CJPA jpa, CDBConnection con) throws Exception {
		Hr_leavejob lj = (Hr_leavejob) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID4Update(con, lj.er_id.getValue(), false);
		if (emp.isEmpty()) {
			throw new Exception("ID为【" + lj.er_id.getValue() + "】的人事档案不存在");
		}
		Hr_ins_cancel cc = new Hr_ins_cancel();
		Hr_ins_buyinsurance bi = new Hr_ins_buyinsurance();
		String sqlstr = "SELECT * FROM hr_ins_buyinsurance bi " +
				" WHERE  bi.stat=9 AND bi.er_id=" + emp.er_id.getValue();
		sqlstr = sqlstr + " ORDER BY bi.buydday DESC";
		bi.findBySQL(sqlstr);
		if (bi.isEmpty()) {
			//System.out.println("工号为【" + lj.employee_code.getValue() + "】的无有效的购保单");
			return;
		}

		cc.insurance_number.setValue(bi.insurance_number.getValue());
		cc.er_id.setValue(emp.er_id.getValue());
		cc.employee_code.setValue(emp.employee_code.getValue());
		cc.employee_name.setValue(emp.employee_name.getValue());
		cc.orgid.setValue(emp.orgid.getValue()); // 部门ID
		cc.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
		cc.orgname.setValue(emp.orgname.getValue()); // 部门名称
		cc.ospid.setValue(emp.ospid.getValue());
		cc.ospcode.setValue(emp.ospcode.getValue());
		cc.sp_name.setValue(emp.sp_name.getValue());
		cc.lv_id.setValue(emp.lv_id.getValue());
		cc.lv_num.setValue(emp.lv_num.getValue());
		cc.hiredday.setValue(emp.hiredday.getValue());
		cc.degree.setValue(emp.degree.getValue());
		cc.sex.setValue(emp.sex.getValue());
		cc.telphone.setValue(emp.telphone.getValue());
		cc.nativeplace.setValue(emp.nativeplace.getValue());
		cc.registertype.setValue(emp.registertype.getValue());
		cc.pay_type.setValue(emp.pay_way.getValue());
		cc.id_number.setValue(emp.id_number.getValue());
		cc.sign_org.setValue(emp.sign_org.getValue());
		cc.sign_date.setValue(emp.sign_date.getValue());
		cc.expired_date.setValue(emp.expired_date.getValue());
		cc.birthday.setValue(emp.birthday.getValue());
		cc.age.setValue(bi.age.getValue());
		cc.pay_type.setValue(emp.pay_way.getValue());
		cc.idpath.setValue(bi.idpath.getValue());

		cc.buydday.setValue(bi.buydday.getValue());
		cc.insit_id.setValue(bi.insit_id.getValue());
		cc.insname.setValue(bi.insname.getValue());
		cc.insbuy_id.setValue(bi.buyins_id.getValue());
		cc.insbuy_code.setValue(bi.buyins_code.getValue());
		cc.ins_type.setAsInt(bi.ins_type.getAsInt());
		cc.reg_type.setAsInt(bi.reg_type.getAsInt());

		cc.ljdate.setValue(lj.ljdate.getValue());
		cc.ljtype2.setValue(lj.ljtype2.getValue());
		cc.ljreason.setValue(lj.ljreason.getValue());

		cc.canceldate.setValue(Systemdate.getStrDateyyyy_mm_dd(new Date()));
		cc.cancelreason.setValue("离职");
		cc.remark.setValue("离职单【" + lj.ljcode.getValue() + "】自动生成退保");
		cc.save(con);
	}

	// 离职作废客餐申请与特殊用餐申请
	private void docancelmealapply(CJPA jpa, CDBConnection con) throws Exception {
		Hr_leavejob lj = (Hr_leavejob) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID4Update(con, lj.er_id.getValue(), false);
		if (emp.isEmpty()) {
			throw new Exception("ID为【" + lj.er_id.getValue() + "】的人事档案不存在");
		}
		String sqlstr = "UPDATE hr_canteen_guest SET usable=2,stat=12 WHERE stat=9 AND (usable=1 OR usable IS NULL)  AND er_id="
				+ lj.er_id.getValue();
		con.execsql(sqlstr);
		sqlstr = " UPDATE hr_canteen_special SET usable=2,stat=12 WHERE stat=9 AND (usable=1 OR usable IS NULL)  AND er_id="
				+ lj.er_id.getValue();
		con.execsql(sqlstr);
	}
}
