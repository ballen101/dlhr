package com.hr.perm.ctr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.access.co.COHr_access_emauthority_list;
import com.hr.attd.ctr.CtrHrkq_workschmonth;
import com.hr.attd.entity.Hrkqtransfer2insurancetimer;
import com.hr.base.entity.Hr_employeestat;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.co.COHr_employee_contract;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_contract;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.perm.entity.Hr_empptjob_break;
import com.hr.perm.entity.Hr_emptransferbatch;
import com.hr.perm.entity.Hr_emptransferbatch_line;
import com.hr.perm.entity.Hr_quotaoc;
import com.hr.perm.entity.Hr_quotaoc_chglog;
import com.hr.perm.entity.Hr_systemparms;
import com.hr.util.HRQuotaUsedInfo;
import com.hr.util.HTQuotaUtil;

public class CtrHr_emptransferbatch extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		wf.subject.setValue(((Hr_emptransferbatch) jpa).emptranfbcode.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		checkquota(jpa, con);
		if (isFilished)
			setEmploestat(jpa, con);
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean arg4) throws Exception {
		// TODO Auto-generated method stub
		if (arg4)
			setEmploestat(jpa, con);
	}

	private void setEmploestat(CJPA jpa, CDBConnection con) throws Exception {
		// 根据评审结果 修改人事资料
		Hr_emptransferbatch ep = (Hr_emptransferbatch) jpa;
		// if (ep.quota_over.getAsInt() == 1) {// 超编
		// if (ep.quota_over_rst.isEmpty())
		// throw new Exception("超编的调动需要评审超编处理方式");
		// int quota_over_rst = ep.quota_over_rst.getAsInt(); // 1 允许增加编制调动 2
		// // 超编调动 3不允许调动
		// if (quota_over_rst == 1) {
		// Hr_orgposition op = new Hr_orgposition();
		// Hr_emptransferbatch_line l0 = (Hr_emptransferbatch_line) ep.hr_emptransferbatch_lines.get(0);
		// op.findByID(l0.newospid.getValue());
		//
		// HRQuotaUsedInfo qi = HTQuotaUtil.getQuotaClassInfo(op.orgid.getValue(), op.hwc_idzl.getValue());// 获取当前机构可用职类编制
		// int needq = ep.hr_emptransferbatch_lines.size() - (qi.getSumq() - qi.getUsedq());
		// if (needq > 0) {
		// doaddquota(con, op.orgid.getValue(), op, needq, ep);// 增加编制
		// // throw new Exception("总编制【" + qi.getSumq() + "】，已经使用编制【" + qi.getUsedq() + "】，还需要增加编制【" + needq + "】，请先增加编制!");
		// }
		// dotranfer(ep, con);// 调动
		// } else if (quota_over_rst == 2) {
		// dotranfer(ep, con);// 调动
		// } else if (quota_over_rst == 3) {
		// // 不处理资料
		// }
		// } else
		dotranfer(ep, con);// 调动
	}

	private void doaddquota(CDBConnection con, String orgid, Hr_orgposition op, int quota, Hr_emptransferbatch ep) throws Exception {
		// 机构向上找到 类型为6的机构，增加编制；如果没找到增加到本机构
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		String idpath = org.idpath.getValue();
		if (!idpath.isEmpty())
			idpath = idpath.substring(0, idpath.length() - 1);
		String sqlstr = "SELECT orgid,orgtype FROM shworg WHERE orgid IN(" + idpath + ")";
		List<HashMap<String, String>> idtps = org.pool.openSql2List(sqlstr);
		String[] ids = idpath.split(",");
		String dworgid = null;
		for (int i = ids.length - 1; i >= 0; i--) {
			String id = ids[i];
			for (HashMap<String, String> idtp : idtps) {
				if (Integer.valueOf(idtp.get("orgtype")) == 6) {
					dworgid = idtp.get("orgid");
					break;
				}
			}
			if (dworgid != null)
				break;
		}
		dworgid = (dworgid == null) ? orgid : dworgid;
		org.clear();
		org.findByID(dworgid, false);

		Hr_quotaoc_chglog qocc = new Hr_quotaoc_chglog();
		Hr_quotaoc qoc = new Hr_quotaoc();

		// 编制
		sqlstr = "select * from hr_quotaoc where orgid=" + dworgid + " and classid=" + op.hwc_idzl.getValue();
		qoc.findBySQL4Update(con, sqlstr, false);
		int oldquota = qoc.quota.getAsIntDefault(0);
		int newquota = oldquota + quota;
		qoc.orgid.setValue(dworgid);
		qoc.classid.setValue(op.hwc_idzl.getValue());
		qoc.quota.setAsInt(newquota);
		qoc.usable.setAsInt(1);
		qoc.save(con, false);
		// log
		qocc.clear();
		qocc.hwc_idzl.setValue(op.hwc_idzl.getValue());
		qocc.hw_codezl.setValue(op.hw_codezl.getValue());
		qocc.hwc_namezl.setValue(op.hwc_namezl.getValue());
		qocc.orgid.setValue(org.orgid.getValue());
		qocc.orgcode.setValue(org.code.getValue());
		qocc.orgname.setValue(org.extorgname.getValue());
		qocc.sourceid.setValue(ep.emptranfb_id.getValue());
		qocc.sourcecode.setValue(ep.emptranfbcode.getValue());
		qocc.sourcelineid.setValue("0");
		qocc.oldquota.setAsInt(oldquota);
		qocc.newquota.setAsInt(newquota);
		qocc.idpath.setValue(org.idpath.getValue());
		qocc.sourcetype.setAsInt(3);
		qocc.save(con, false);

	}

	private void dotranfer(Hr_emptransferbatch ep, CDBConnection con) throws Exception {
		Hr_employee emp = new Hr_employee();
		Hr_employeestat estat = new Hr_employeestat();
		Shworg org = new Shworg();
		CJPALineData<Hr_emptransferbatch_line> epls = ep.hr_emptransferbatch_lines;
		for (int i = 0; i < epls.size(); i++) {
			Hr_emptransferbatch_line epl = (Hr_emptransferbatch_line) epls.get(i);
			if (epl.rlmgms.getAsIntDefault(0) == 2) {// 终止调动
				continue;
			}
			emp.findByID4Update(con, epl.er_id.getValue(), false);
			if (emp.isEmpty()) {
				throw new Exception("ID为【" + epl.er_id.getValue() + "】的人事档案不存在");
			}
			estat.findBySQL("select * from hr_employeestat where statvalue=" + emp.empstatid.getValue());

			if (estat.allowtransfer.getAsInt() != 1) {
				throw new Exception("状态为【" + estat.language1.getValue() + "】的人事档案不允许调动");
			}

			org.clear();
			org.findByID(epl.neworgid.getValue());
			if (org.isEmpty())
				throw new Exception("没有找到ID为【" + epl.neworgid.getValue() + "】的机构");

			Hr_orgposition op = new Hr_orgposition();
			op.findByID(epl.newospid.getValue());
			if (op.isEmpty())
				throw new Exception("没有找到ID为【" + ep.newospid.getValue() + "】的机构职位");

			// emp.empstatid.setValue(estat.statvalue.getValue()); 批量调动不修改人事状态
			emp.orgid.setValue(org.orgid.getValue()); // 部门ID
			emp.orgcode.setValue(org.code.getValue()); // 部门编码
			emp.orgname.setValue(org.extorgname.getValue()); // 部门名称
			emp.lv_id.setValue(epl.newlv_id.getValue()); // 职级ID
			emp.lv_num.setValue(epl.newlv_num.getValue()); // 职级
			emp.hg_id.setValue(epl.newhg_id.getValue()); // 职等ID
			emp.hg_code.setValue(epl.newhg_code.getValue()); // 职等编码
			emp.hg_name.setValue(epl.newhg_name.getValue()); // 职等名称

			emp.ospid.setValue(op.ospid.getValue()); // 职位ID
			emp.ospcode.setValue(op.ospcode.getValue()); // 职位编码
			emp.idpath.setValue(org.idpath.getValue());
			emp.sp_name.setValue(op.sp_name.getValue());
			emp.hwc_namezq.setValue(op.hwc_namezq.getValue());
			emp.hwc_namezz.setValue(op.hwc_namezz.getValue());
			emp.hwc_namezl.setValue(op.hwc_namezl.getValue());
			emp.iskey.setValue(op.iskey.getValue());
			if (op.isoffjob.getAsIntDefault(0) == 1) {
				emp.emnature.setValue("脱产");
			} else {
				emp.emnature.setValue("非脱产");
			}

			emp.uorgid.setValue(null);
			emp.uorgcode.setValue(null);
			emp.uorgname.setValue(null);
			emp.uidpath.setValue(null);

			emp.save(con);
			CtrHr_employee_transfer.updateExtInfo(con, emp);// 更新扩展信息
			doresigncontract(org, emp, con);// 到新部门后更新原合同
			// CtrHrkq_workschmonth.putEmDdtWeekPB(con, emp, ep.tranfcmpdate.getAsDatetime());// 生成默认排班
			// COHr_access_emauthority_list.doUpdateAccessList(con, emp.employee_code.getValue(), "3",
			// epl.emptranfbl_id.getValue(), "员工批量调动", epl.odorgid.getValue(), epl.neworgid.getValue());
			setinsrancetimer(ep, emp, con);// 插入调度列表等待自动生成购保单
		}

	}

	private void checkquota(CJPA jpa, CDBConnection con) throws Exception {
		Hr_emptransferbatch ep = (Hr_emptransferbatch) jpa;
		// 调动单提交时候 检查编制
		//
		// CJPALineData<Hr_emptransferbatch_line> ls = ep.hr_emptransferbatch_lines;
		// Hr_orgposition op = new Hr_orgposition();
		// for (CJPABase jb : ls) {
		// Hr_emptransferbatch_line l = (Hr_emptransferbatch_line) jb;
		// op.findByID(l.newospid.getValue());
		// if (op.isEmpty())
		// throw new Exception("ID为【" + l.newospid.getValue() + "】的机构职位不存在");
		//
		// String sqlstr = "SELECT * FROM hr_systemparms WHERE usable=1 AND parmcode='BATCH_QUATA_CLASS' AND parmvalue='" + op.hwc_idzl.getValue() + "'";
		// Hr_systemparms hs = new Hr_systemparms(sqlstr);
		// if (hs.isEmpty())
		// throw new Exception("员工对应的职类标记为【机构职位编制控制】不允许批量调动");
		// }
		//
		// if (HTQuotaUtil.checkOrgClassQuota(ep.neworgid.getValue(), op.hwc_idzl.getValue(), ep.hr_emptransferbatch_lines.size()))
		// ep.quota_over.setAsInt(2);
		// else
		// ep.quota_over.setAsInt(1);
		// ep.save(con);
		ep.quota_over.setAsInt(2);
		ep.save(con);
	}

	private void doresigncontract(Shworg org, Hr_employee emp, CDBConnection con) throws Exception {// 调动后更新原合同
		Hr_employee_contract hrecon = new Hr_employee_contract();
		hrecon.findBySQL("SELECT * FROM `hr_employee_contract` WHERE stat=9 AND contractstat=1 AND contract_type=1 AND er_id=" + emp.er_id.getValue());
		if (hrecon.isEmpty())
			return;// 要求取消限制 17-12-27
		// throw new Exception("没有找到员工【" + emp.employee_name.getValue() + "】的有效合同资料");

		hrecon.orgid.setValue(org.orgid.getValue()); // 部门ID
		hrecon.orgcode.setValue(org.code.getValue()); // 部门编码
		hrecon.orgname.setValue(org.extorgname.getValue()); // 部门名称
		hrecon.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
		hrecon.lv_num.setValue(emp.lv_num.getValue()); // 职级
		hrecon.ospid.setValue(emp.ospid.getValue()); // 职位ID
		hrecon.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
		hrecon.idpath.setValue(org.idpath.getValue());
		hrecon.sp_name.setValue(emp.sp_name.getValue());
		hrecon.remark.setValue("批量调动单自动生成合同！" + Systemdate.getStrDate()); // 备注
		hrecon.save(con);
	}

	private void setinsrancetimer(Hr_emptransferbatch trans, Hr_employee emp, CDBConnection con) throws Exception {// 插入待调度列表，等待调度生成购保单
		Hrkqtransfer2insurancetimer instimer = new Hrkqtransfer2insurancetimer();
		instimer.er_id.setValue(emp.er_id.getValue());
		instimer.employee_code.setValue(emp.employee_code.getValue());
		instimer.employee_name.setValue(emp.employee_name.getValue());
		instimer.tranfcmpdate.setValue(trans.tranfcmpdate.getValue());
		String tranmount = Systemdate.getStrDateByFmt(instimer.tranfcmpdate.getAsDatetime(), "yyyy-MM");
		tranmount = tranmount + "-01";
		Date buydate = Systemdate.dateMonthAdd(Systemdate.getDateByyyyy_mm_dd(tranmount), 1);
		instimer.dobuyinsdate.setAsDatetime(buydate);
		instimer.isbuyins.setAsInt(2);
		instimer.sourceid.setValue(trans.emptranfb_id.getValue());
		instimer.sourcecode.setValue(trans.emptranfbcode.getValue());
		instimer.save(con);
	}

	/**
	 * 保存前
	 * 
	 * @param jpa里面有值
	 *            ，还没检测数据完整性，没生成ID CODE 设置默认值
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_emptransferbatch ep = (Hr_emptransferbatch) jpa;
			ep.olorgid.getValue();//转出部门
		ep.neworgid.getValue();//转入部门
		String sqlstr = "select * from shworg where orgid='" + ep.olorgid.getValue() + "'";
		String sqlstr2 = "select * from shworg where orgid='" + ep.neworgid.getValue() + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr);
		String oldidpath=org.idpath.getValue();
		Shworg org2 = new Shworg();
		org2.findBySQL(sqlstr2);
		String newidpath=org2.idpath.getValue();
		String [] oldidpaths= oldidpath.split(",");
		String [] newidpaths= newidpath.split(",");
		if(oldidpaths.length==2 || newidpaths.length==2){
			throw new Exception("调出机构或调入机构不允许选择整个制造群");
		}
		boolean flag=true;
		//调动范围 1跨部门2 跨单位 3 跨模块/制造群 4部门内部
		Logsw.dblog(oldidpaths[1]+"*******"+newidpaths[1]+"*******"+ep.tranftype3.getValue());
		if(oldidpaths.length>=2 && newidpaths.length>=2){
			if(!oldidpaths[1].equals(newidpaths[1])){
				if(!ep.tranftype3.getValue().equals("3")){
					throw new Exception("调动范围只能选择跨模块/制造群");
				}else{
					flag=false;
				}
			}
		}
		if(oldidpaths.length>=3 && newidpaths.length>=3 && flag){
			Logsw.dblog(oldidpaths[2]+"*******"+newidpaths[2]+"*******"+ep.tranftype3.getValue());
			if(oldidpaths[1].equals(newidpaths[1])&& !oldidpaths[2].equals(newidpaths[2]) ){
				if(!ep.tranftype3.getValue().equals("2")){
					throw new Exception("调动范围只能选择跨单位");
				}else{
					flag=false;
				}
			}
		}
		if(oldidpaths.length>=3 && newidpaths.length>=3 && flag){
			if(oldidpaths[1].equals(newidpaths[1])&& oldidpaths[2].equals(newidpaths[2])){
				if(ep.tranftype3.getValue().equals("2") ||ep.tranftype3.getValue().equals("3")){
					throw new Exception("调动范围只能选择跨部门/部门内部");
				}
			}
		}
		ep.attribute1.setAsInt(ep.hr_emptransferbatch_lines.getJpaSize());
	}

}
