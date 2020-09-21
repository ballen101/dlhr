package com.hr.base.co;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.genco.COShwUser;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shworg_acthis;
import com.corsair.server.generic.Shworg_find;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.GetNewSEQID;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.salary.ctr.CtrSalaryCommon;
import com.hr.salary.entity.Hr_salary_chgbill;
import com.hr.salary.entity.Hr_salary_chglg;

/**
 * 组织架构拆分、合并 检测是否有正在走流程的单据；
 * 
 * 对于试用期、考察期的员工，拆分合并后，可能无法转正？
 * 
 * 实习生拆分合并后可能也会有问题。
 * 
 * @author shangwen
 * 
 */
@ACO(coname = "web.hr.org")
public class COOrgSplitMerge {
	@ACOAction(eventname = "getcorgandemps", Authentication = true, notes = "获取某个机构的子机构及员工资料")
	public String getcorgandemps() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		CJPALineData<Shworg> orgs = new CJPALineData<Shworg>(Shworg.class);
		String sqlstr = "select * from shworg where superid=" + orgid + " and usable=1";
		orgs.findDataBySQL(sqlstr, true, false);
		CJPALineData<Hr_employee> emps = new CJPALineData<Hr_employee>(Hr_employee.class);
		sqlstr = "select * from hr_employee where orgid=" + orgid + " and empstatid not in(0,12,13)";
		emps.findDataBySQL(sqlstr, true, false);
		JSONArray rst = new JSONArray();
		for (CJPABase jpa : orgs) {
			Shworg org = (Shworg) jpa;
			JSONObject o = new JSONObject();
			o.put("otype", 1);
			o.put("id", org.orgid.getValue());
			o.put("caption", org.orgname.getValue());
			rst.add(o);
		}
		for (CJPABase jpa : emps) {
			Hr_employee emp = (Hr_employee) jpa;
			JSONObject o = new JSONObject();
			o.put("otype", 2);
			o.put("id", emp.er_id.getValue());
			o.put("caption", emp.employee_name.getValue());
			rst.add(o);
		}
		return rst.toString();
	}

	@ACOAction(eventname = "apporgschg", Authentication = true, notes = "应用机构调整")
	public String apporgschg() throws Exception {
		JSONObject parms = JSONObject.fromObject(CSContext.getPostdata());
		String dorgid = parms.getJSONObject("destorg").getString("orgid");
		String sorgid = parms.getJSONObject("sourseorg").getString("orgid");
		JSONArray rows = parms.getJSONArray("chgs");
		// checkInWf(sorgid);// 检测源机构或人员所在机构是否有流程中的表单
		Shworg dorg = new Shworg();
		dorg.findByID(dorgid);
		if (dorg.isEmpty())
			throw new Exception("ID为【" + dorgid + "】的目标机构不存在");
		JSONObject rst = new JSONObject();
		Shworg_acthis soh = new Shworg_acthis();
		CDBConnection con = DBPools.defaultPool().getCon(this.getClass());
		con.startTrans();
		try {
			for (int i = 0; i < rows.size(); i++) {
				JSONObject row = rows.getJSONObject(i);
				if (row.getInt("otype") == 1) {// 机构
					Shworg sorg = new Shworg();
					sorg.findByID(row.getString("id"), false);
					if (sorg.isEmpty())
						throw new Exception("ID为【" + row.getString("id") + "】的源标机构不存在");
					createOrgsOPtions(con, sorg, dorg);// 将源机构及其子机构 创建到目标机构下面
					// 包括机构职位
					createTrancerEmpoyees(con, sorg);// 将源机构及其子机构 人员 调动到新机构下面
					COShwUser.setALLChildOrgUsable(con, sorg, 2);
					soh.clear();
					soh.orgid.setValue(sorg.orgid.getValue());
					soh.acttype.setAsInt(3);
					soh.acttime.setAsDatetime(new Date());
					soh.actcommit.setValue("合并到机构" + dorg.orgid.getValue() + "," + dorg.code.getValue() + "," + dorg.orgname.getValue());
					soh.save(con);
					soh.clear();
					soh.orgid.setValue(dorg.orgid.getValue());
					soh.acttype.setAsInt(2);
					soh.acttime.setAsDatetime(new Date());
					soh.actcommit.setValue("合并机构" + sorg.orgid.getValue() + "," + sorg.code.getValue() + "," + sorg.orgname.getValue());
					soh.save(con);
				}
				if (row.getInt("otype") == 2) {// 调动人
					Hr_employee emp = new Hr_employee();
					emp.findByID(con, row.getString("id"));
					if (emp.isEmpty())
						throw new Exception("ID为【" + row.getString("id") + "】的人事资料不存在");
					createTrancerEmpoyee(con, emp, dorg);
				}
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}

		rst.put("rst", "ok");
		return rst.toString();
	}

	/**
	 * 将源机构及其子机构 人员 调动到新机构下面
	 * 
	 * @param con
	 * @param sorg
	 * @throws Exception
	 */
	private void createTrancerEmpoyees(CDBConnection con, Shworg sorg) throws Exception {
		CJPALineData<Hr_employee> emps = new CJPALineData<Hr_employee>(Hr_employee.class);
		String sqlstr = "select * from hr_employee where empstatid not in(11,12,13) and idpath like '" + sorg.idpath.getValue() + "%'";
		emps.findDataBySQL(con, sqlstr, false, false);
		Shworg dorg = new Shworg();
		for (CJPABase jpa : emps) {
			Hr_employee emp = (Hr_employee) jpa;
			sqlstr = "SELECT * FROM shworg WHERE attribute5=" + emp.orgid.getValue();
			dorg.clear();
			dorg.findBySQL(con, sqlstr, false);
			if (dorg.isEmpty()) {
				throw new Exception("组织架构调整，计划调入机构所对应调出机构ID【" + emp.orgid.getValue() + "】不存在");
			}
			createTrancerEmpoyee(con, emp, dorg);
		}
	}

	/**
	 * 将员工调入目标机构，根据标准职位 找到随便一个机构职位调入，如果没有则根据源机构职位创建机构职位
	 * 
	 * @param con
	 * @param emp
	 * @param dorg
	 * @throws Exception
	 */
	private void createTrancerEmpoyee(CDBConnection con, Hr_employee emp, Shworg dorg) throws Exception {
		Hr_orgposition sosp = new Hr_orgposition();
		sosp.findByID(con, emp.ospid.getValue());
		if (sosp.isEmpty())
			throw new Exception("调动人事资料，当前机构职位【" + emp.ospid.getValue() + "】不存在");
		Hr_orgposition dosp = getOrCreateOSP(con, sosp, dorg);
		Hr_employee_transfer et = new Hr_employee_transfer();
		Hr_salary_chglg sc = CtrSalaryCommon.getCur_salary_chglg(emp.er_id.getValue());

		et.er_id.setValue(emp.er_id.getValue()); // 人事ID
		et.employee_code.setValue(emp.employee_code.getValue()); // 工号
		et.id_number.setValue(emp.id_number.getValue()); // 身份证号
		et.employee_name.setValue(emp.employee_name.getValue()); // 姓名
		et.mnemonic_code.setValue(emp.mnemonic_code.getValue()); // 助记码
		et.email.setValue(emp.email.getValue()); // 邮箱/微信
		et.empstatid.setValue(emp.empstatid.getValue()); // 人员状态
		et.telphone.setValue(emp.telphone.getValue()); // 电话
		et.tranfcmpdate.setAsDatetime(new Date()); // 调动生效时间
		et.hiredday.setValue(emp.hiredday.getValue()); // 聘用日期
		et.degree.setValue(emp.degree.getValue()); // 学历
		et.tranflev.setValue("0"); // 调动层级
		et.tranftype1.setValue("3"); // 调动类型 1晋升调动 2降职调动 3同职种平级调动 4跨职种平级调动
		et.tranftype2.setValue("1"); // 调动性质 1公司安排 2个人申请 3梦职场调动 4内部招聘
		et.tranftype3.setValue("0"); // 调动范围 1内部调用 2 跨单位 3 跨模块/制造群
		et.tranfreason.setValue("组织架构调整调动"); // 调动原因
		et.probation.setValue("0"); // 考察期 1
		et.probationdate.setValue(null); // 考察到期日期
		et.ispromotioned.setValue("1"); // 已转正
		et.odorgid.setValue(emp.orgid.getValue()); // 调动前部门ID
		et.odorgcode.setValue(emp.orgcode.getValue()); // 调动前部门编码
		et.odorgname.setValue(emp.orgname.getValue()); // 调动前部门名称
		et.odorghrlev.setValue("0"); // 调调动前部门人事层级
		et.odlv_id.setValue(emp.lv_id.getValue()); // 调动前职级ID
		et.odlv_num.setValue(emp.lv_num.getValue()); // 调动前职级
		et.odhg_id.setValue(emp.hg_id.getValue()); // 调动前职等ID
		et.odhg_code.setValue(emp.hg_code.getValue()); // 调动前职等编码
		et.odhg_name.setValue(emp.hg_name.getValue()); // 调动前职等名称
		et.odospid.setValue(sosp.ospid.getValue()); // 调动前职位ID
		et.odospcode.setValue(sosp.ospcode.getValue()); // 调动前职位编码
		et.odsp_name.setValue(sosp.sp_name.getValue()); // 调动前职位名称
		et.odattendtype.setValue(emp.atdtype.getValue()); // 调动前出勤类别
		et.oldcalsalarytype.setValue(emp.pay_way.getValue()); // 调动前计薪方式
		et.oldhwc_namezl.setValue(sosp.hwc_namezl.getValue()); // 调动前职类
		et.oldhwc_namezq.setValue(sosp.hwc_namezq.getValue()); // 调动前职群
		et.oldhwc_namezz.setValue(sosp.hwc_namezz.getValue()); // 调动前职种
		et.oldemnature.setValue(emp.emnature.getValue()); // 调动前职位性质

		et.neworgid.setValue(dosp.orgid.getValue()); // 调动后部门ID
		et.neworgcode.setValue(dosp.orgcode.getValue()); // 调动后部门编码
		et.neworgname.setValue(dosp.orgname.getValue()); // 调动后部门名称
		et.neworghrlev.setValue("0"); // 调动后部门人事层级
		et.newlv_id.setValue(dosp.lv_id.getValue()); // 调动后职级ID
		et.newlv_num.setValue(dosp.lv_num.getValue()); // 调动后职级
		et.newhg_id.setValue(dosp.hg_id.getValue()); // 调动后职等ID
		et.newhg_code.setValue(dosp.hg_code.getValue()); // 调动后职等编码
		et.newhg_name.setValue(dosp.hg_name.getValue()); // 调动后职等名称
		et.newospid.setValue(dosp.ospid.getValue()); // 调动后职位ID
		et.newospcode.setValue(dosp.ospcode.getValue()); // 调动后职位编码
		et.newsp_name.setValue(dosp.sp_name.getValue()); // 调动后职位名称
		et.newattendtype.setValue(emp.atdtype.getValue()); // 调动后出勤类别
		et.newcalsalarytype.setValue(emp.pay_way.getValue()); // 调动后计薪方式
		et.newhwc_namezl.setValue(dosp.hwc_namezl.getValue()); // 调动后职类
		et.newhwc_namezq.setValue(dosp.hwc_namezq.getValue()); // 调动后职群
		et.newhwc_namezz.setValue(dosp.hwc_namezz.getValue()); // 调动后职种
		et.newemnature.setValue(emp.emnature.getValue()); // 调动后职位性质
		if (!sc.isEmpty()) {
			et.oldstru_id.setValue(sc.newstru_id.getValue()); // 调动前工资结构id
			et.oldstru_name.setValue(sc.newstru_name.getValue()); // 调动前工资结构
			et.oldchecklev.setValue(sc.newchecklev.getValue()); // 调动前考核层级
			et.odattendtype.setValue(sc.newattendtype.getValue()); // 调动前出勤类别
			et.oldposition_salary.setValue(sc.newposition_salary.getValue()); // 调动前职位工资
			et.oldbase_salary.setValue(sc.newbase_salary.getValue()); // 调动前基本工资
			et.oldotwage.setValue(sc.newotwage.getValue()); // 调动前固定加班工资
			et.oldtech_salary.setValue(sc.newtech_salary.getValue()); // 调动前技能工资
			et.oldachi_salary.setValue(sc.newachi_salary.getValue()); // 调动前绩效工资
			et.oldtech_allowance.setValue(sc.newtech_allowance.getValue()); // 调动前技能津贴

			et.newstru_id.setValue(sc.newstru_id.getValue()); // 调动后工资结构id
			et.newstru_name.setValue(sc.newstru_name.getValue()); // 调动后工资结构
			et.newchecklev.setValue(sc.newchecklev.getValue()); // 调动后考核层级
			et.newattendtype.setValue(sc.newattendtype.getValue()); // 调动后出勤类别
			et.newposition_salary.setValue(sc.newposition_salary.getValue()); // 调动后职位工资
			et.newbase_salary.setValue(sc.newbase_salary.getValue()); // 调动后基本工资
			et.newotwage.setValue(sc.newotwage.getValue()); // 调动后固定加班工资
			et.newtech_salary.setValue(sc.newtech_salary.getValue()); // 调动后技能工资
			et.newachi_salary.setValue(sc.newachi_salary.getValue()); // 调动后绩效工资
			et.newtech_allowance.setValue(sc.newtech_allowance.getValue()); // 调动后技能津贴
		} else {
			et.oldposition_salary.setValue("0"); // 调动前职位工资
			et.oldbase_salary.setValue("0"); // 调动前基本工资
			et.oldtech_salary.setValue("0"); // 调动前技能工资
			et.oldachi_salary.setValue("0"); // 调动前技能工资
			et.oldtech_allowance.setValue("0"); // 调动前技术津贴
			et.oldavg_salary.setValue("0"); // 调动前平均工资
			et.newposition_salary.setValue("0"); // 调动前职位工资
			et.newbase_salary.setValue("0"); // 调动后基本工资
			et.newtech_salary.setValue("0"); // 调动后技能工资
			et.newachi_salary.setValue("0"); // 调动后技能工资
			et.newtech_allowance.setValue("0"); // 调动后技术津贴
			et.newavg_salary.setValue("0"); // 调动后平均工资
		}

		et.salary_quotacode.setValue("0"); // 可用工资额度证明编号
		et.salary_quota_canuse.setValue("0"); // 可用工资额度
		et.salary_quota_used.setValue("0"); // 己用工资额度
		et.salary_quota_blance.setValue("0"); // 可用工资余额
		et.istp.setValue("1"); // 是否特批
		et.tranamt.setValue("0"); // 调拨金额
		et.exam_title.setValue(null); // 考试课题
		et.exam_time.setValue(null); // 考试时间
		et.exam_score.setValue(null); // 考试分数
		et.mupexam_time.setValue(null); // 补考时间
		et.mupexam_score.setValue(null); // 补考分数
		et.exam_note.setValue(null); // 考试备注
		et.remark.setValue(null); // 备注
		et.quota_over.setValue("2"); // 是否超编
		et.quota_over_rst.setValue("2"); // 超编审批结果 1 允许增加编制调动 ps是否自动生成编制调整单 2
		// 超编调动 3
		et.isdreamposition.setValue("2"); // 是否梦职场调入
		et.isdreamemploye.setValue("2"); // 是否梦职场储备员工
		et.attribute1.setAsInt(1);// 1 表示组织架构调整产生的调动表单
		et.save(con);
		doNewsalary_chgbill(con, et, sc);////
		et.wfcreate(null, con);
	}

	private void doNewsalary_chgbill(CDBConnection con, Hr_employee_transfer et, Hr_salary_chglg sc) throws Exception {
		String stype = "4";
		String sid = et.emptranf_id.getValue();
		String sqlstr = "SELECT * FROM hr_salary_chgbill WHERE scatype=" + 2 + " AND stype=" + stype + " AND sid=" + sid;
		Hr_salary_chgbill cb = new Hr_salary_chgbill();
		cb.findBySQL(con, sqlstr, false);
		cb.er_id.setValue(et.er_id.getValue()); // 人事ID
		cb.scatype.setValue(2); // // 1 入职定薪、2调动核薪、3 转正调薪、4 年度调薪、5 特殊调薪 6调动转正 7 兼职8津贴
		cb.stype.setValue(stype); // 来源类别 1 历史数据、2 实习登记、3 入职表单、4 调动表单、5 入职转正、6 调动转正、7个人特殊调薪 8兼职、9批量特殊调薪、10技术津贴、11岗位津贴、12年度调薪....
		cb.sid.setValue(sid); // 来源ID
		cb.scode.setValue(et.emptranfcode.getValue()); // 来源单号
		cb.oldstru_id.setValue(sc.newstru_id.getValue()); // 调薪前工资结构ID
		cb.oldstru_name.setValue(sc.newstru_name.getValue()); // 调薪前工资结构名
		cb.oldchecklev.setValue(sc.newchecklev.getValue()); // 调薪前绩效考核层级
		cb.oldattendtype.setValue(sc.newattendtype.getValue()); // 调薪前出勤类别
		cb.oldcalsalarytype.setValue(sc.newcalsalarytype.getValue()); // 调薪前计薪方式
		cb.oldposition_salary.setValue(sc.newposition_salary.getValue()); // 调薪前职位工资
		cb.oldbase_salary.setValue(sc.newbase_salary.getValue()); // 调薪前基本工资
		cb.oldtech_salary.setValue(sc.newtech_salary.getValue()); // 调薪前技能工资
		cb.oldachi_salary.setValue(sc.newachi_salary.getValue()); // 调薪前绩效工资
		cb.oldotwage.setValue(sc.newotwage.getValue()); // 调薪前固定加班工资
		cb.oldtech_allowance.setValue(sc.newtech_allowance.getValue()); // 调薪前技术津贴
		cb.oldparttimesubs.setValue(sc.newparttimesubs.getValue()); // 调薪前兼职津贴
		cb.oldpostsubs.setValue(sc.newpostsubs.getValue()); // 调薪前岗位津贴
		cb.oldovt_salary.setValue(sc.newotwage.getValue()); // 调薪前加班工资
		cb.oldavg_salary.setValue(0); // 调薪前平均工资

		cb.newstru_id.setValue(sc.newstru_id.getValue()); // 调薪后工资结构ID
		cb.newstru_name.setValue(sc.newstru_name.getValue()); // 调薪后工资结构名
		cb.newchecklev.setValue(sc.newchecklev.getValue()); // 调薪后绩效考核层级
		cb.newattendtype.setValue(sc.newattendtype.getValue()); // 调薪后出勤类别
		cb.newcalsalarytype.setValue(sc.newcalsalarytype.getValue()); // 调薪后计薪方式
		cb.newposition_salary.setValue(sc.newposition_salary.getValue()); // 调薪后职位工资
		cb.newbase_salary.setValue(sc.newbase_salary.getValue()); // 调薪后基本工资
		cb.newtech_salary.setValue(sc.newtech_salary.getValue()); // 调薪后技能工资
		cb.newachi_salary.setValue(sc.newachi_salary.getValue()); // 调薪后绩效工资
		cb.newotwage.setValue(sc.newotwage.getValue()); // 调薪后固定加班工资
		cb.newtech_allowance.setValue(sc.newtech_allowance.getValue()); // 调薪后技术津贴
		cb.newparttimesubs.setValue(sc.newparttimesubs.getValue()); // 调薪后兼职津贴
		cb.newpostsubs.setValue(sc.newpostsubs.getValue()); // 调薪后岗位津贴
		cb.newovt_salary.setValue(sc.newotwage.getValue()); // 调薪后加班工资
		cb.newavg_salary.setValue(0); // 调薪后平均工资
		cb.sacrage.setValue(sc.sacrage.getValue()); // 调薪幅度

		cb.chgdate.setValue(et.createtime.getValue()); // 调薪日期
		cb.chgreason.setValue("组织架构调整"); // 调薪原因
		cb.remark.setValue(null); // 备注
		cb.save(con);
	}

	/**
	 * 根据标准职位 从目标机构获取机构职位，如果没有则新建机构职位
	 * 
	 * @param con
	 * @param sosp
	 * @param dorg
	 * @return
	 * @throws Exception
	 */
	private Hr_orgposition getOrCreateOSP(CDBConnection con, Hr_orgposition sosp, Shworg dorg) throws Exception {
		String sqlstr = "SELECT * FROM hr_orgposition WHERE sp_id=" + sosp.sp_id.getValue() + " AND orgid=" + dorg.orgid.getValue();
		Hr_orgposition dosp = new Hr_orgposition();
		dosp.findBySQL(con, sqlstr, false);
		if (!dosp.isEmpty())
			return dosp;
		else
			return createOSPByOSP(con, sosp, dorg);
	}

	/**
	 * 将源机构及其子机构 创建到目标机构下面；包括机构职位
	 * 
	 * @param con
	 * @param sorg
	 * @param dorg
	 * @throws Exception
	 */
	private void createOrgsOPtions(CDBConnection con, Shworg sorg, Shworg dorg) throws Exception {
		String idpath = sorg.idpath.getValue();
		String sqlstr = "select * from shworg where idpath like '" + idpath + "%'";
		JSONArray js = con.opensql2jsontree_o(sqlstr, "orgid", "superid", false);
		JSONObject j = js.getJSONObject(0);
		createOrgsOPtion(con, j, dorg.orgid.getValue(), dorg.idpath.getValue(), dorg.extorgname.getValue());
	}

	private void createOrgsOPtion(CDBConnection con, JSONObject j, String pid, String pidpath, String extorgname) throws Exception {
		Shworg oldorg = new Shworg();
		Shworg neworg = new Shworg();
		oldorg.fromjson(j.toString());
		neworg.fromjson(j.toString());
		oldorg.code.setValue(oldorg.code.getValue() + "_old");
		oldorg.attribute2.setValue("架构调整修改编码");
		oldorg.save(con, false);

		String odorgid = oldorg.orgid.getValue();
		neworg.orgid.setValue(GetNewSEQID.doCreateNewID(con, "shworg", 1));
		neworg.superid.setValue(pid);
		neworg.idpath.setValue(pidpath + neworg.orgid.getValue() + ",");
		neworg.attribute5.setValue(odorgid);
		neworg.extorgname.setValue(extorgname + "-" + neworg.orgname.getValue());
		neworg.creator.setValue("SYSTEM");
		neworg.createtime.setAsDatetime(new Date());
		neworg.updator.setValue(null);
		neworg.updatetime.setValue(null);
		neworg.setJpaStat(CJPAStat.RSINSERT);
		neworg.shworg_finds.clear();// 跨机构权限没办法处理，因为新旧ID可能都会发生变化
		neworg.save(con);
		createOSPBySOrg(con, odorgid, neworg);// 复制机构职位
		if (j.has("children")) {
			JSONArray cjs = j.getJSONArray("children");
			for (int i = 0; i < cjs.size(); i++) {
				JSONObject cj = cjs.getJSONObject(i);
				createOrgsOPtion(con, cj, neworg.orgid.getValue(), neworg.idpath.getValue(), neworg.extorgname.getValue());
			}
		}
	}

	/**
	 * 复制源机构下 机构职位到新机构下
	 * 
	 * @param con
	 * @param sorgid
	 * @param dorg
	 * @throws Exception
	 */
	private void createOSPBySOrg(CDBConnection con, String sorgid, Shworg dorg) throws Exception {
		String sqlstr = "SELECT * FROM hr_orgposition WHERE orgid =" + sorgid;
		CJPALineData<Hr_orgposition> osps = new CJPALineData<Hr_orgposition>(Hr_orgposition.class);
		osps.findDataBySQL(con, sqlstr, true, false);
		for (CJPABase jpa : osps) {
			Hr_orgposition osp = (Hr_orgposition) jpa;
			createOSPByOSP(con, osp, dorg);
		}
	}

	/**
	 * 根据机构职位 在目标机构下创建机构职位(不检查)
	 * 
	 * @param con
	 * @param sosp
	 * @param dorg
	 * @return
	 * @throws Exception
	 */
	private Hr_orgposition createOSPByOSP(CDBConnection con, Hr_orgposition sosp, Shworg dorg) throws Exception {
		Hr_orgposition rst = new Hr_orgposition();
		rst.findByID(con, sosp.ospid.getValue());
		rst.orgid.setValue(dorg.orgid.getValue());
		rst.orgcode.setValue(dorg.code.getValue());
		rst.orgname.setValue(dorg.extorgname.getValue());
		rst.idpath.setValue(dorg.idpath.getValue());
		rst.attribute2.setValue(sosp.ospid.getValue());// 存储旧的ospid
		rst.ospid.setValue(null);
		rst.ospcode.setValue(null);
		rst.hr_orgpositionkpis.setFieldValues("opkpiid", null);
		rst.hr_orgpositionkpis.setFieldValues("ospid", null);
		rst.setAllJpaStat(CJPAStat.RSINSERT);
		rst.save(con);
		sosp.usable.setValue(2);
		sosp.save(con);
		return rst;
	}

	private void checkInWf(String sorgid) throws Exception {
		Shworg org = new Shworg();
		org.findByID(sorgid);
		if (org.isEmpty())
			throw new Exception("ID为【" + sorgid + "】的源机构不存在");
		String idpath = org.idpath.getValue();
		String sqlstr = null;
		CDBPool pool = DBPools.defaultPool();
		sqlstr = "SELECT COUNT(*) ct FROM hr_employee_transfer WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "调动表单");
		sqlstr = "SELECT COUNT(*) ct FROM hr_black_add WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "加黑表单");
		sqlstr = "SELECT COUNT(*) ct FROM hr_black_del WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "解封黑名单");
		sqlstr = "SELECT COUNT(*) ct FROM hr_empconbatch WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "批量签订合同");
		sqlstr = "SELECT COUNT(*) ct FROM hr_emploanbatch WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "人事借调批量");
		sqlstr = "SELECT COUNT(*) ct FROM hr_empptjob_app WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "兼职登记单");
		sqlstr = "SELECT COUNT(*) ct FROM hr_empptjob_break WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "兼职终止单");
		sqlstr = "SELECT COUNT(*) ct FROM hr_emptransferbatch WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "批量调动");
		sqlstr = "SELECT COUNT(*) ct FROM hr_entry WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "入职表单");
		sqlstr = "SELECT COUNT(*) ct FROM hr_entry_prob WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "入职转正");
		sqlstr = "SELECT COUNT(*) ct FROM hr_leavejob WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "离职表单");
		sqlstr = "SELECT COUNT(*) ct FROM hr_leavejob_cancel WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "离职取消表单");
		sqlstr = "SELECT COUNT(*) ct FROM hr_leavejobbatch WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "批量离职");
		sqlstr = "SELECT COUNT(*) ct FROM hr_quota_adjust WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "编制调整");
		sqlstr = "SELECT COUNT(*) ct FROM hr_quota_release WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "编制发布");
		sqlstr = "SELECT COUNT(*) ct FROM hr_quotaoc_release WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "职类编制发布");
		sqlstr = "SELECT COUNT(*) ct FROM hr_train_reg WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "实习生登记");
		sqlstr = "SELECT COUNT(*) ct FROM hrkq_business_trip WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "出差表单");
		sqlstr = "SELECT COUNT(*) ct FROM hrkq_holidayapp WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "请假表单");
		sqlstr = "SELECT COUNT(*) ct FROM hrkq_holidayapp_cancel WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "销假表单");
		sqlstr = "SELECT COUNT(*) ct FROM hrkq_onduty WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "值班表单");
		sqlstr = "SELECT COUNT(*) ct FROM hrkq_overtime WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "加班表单");
		sqlstr = "SELECT COUNT(*) ct FROM hrkq_overtime_adjust WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "加班调整表单");
		sqlstr = "SELECT COUNT(*) ct FROM hrkq_overtime_qual WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "加班资格申请表单");
		sqlstr = "SELECT COUNT(*) ct FROM hrkq_wkoff WHERE stat=2 AND idpath LIKE '%" + idpath + "%'";
		opencheksql(pool, sqlstr, "调休表单");
	}

	private void opencheksql(CDBPool pool, String sqlstr, String tbmsg) throws NumberFormatException, Exception {
		if (Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct")) > 0) {
			throw new Exception("源机构存在未审批完成的【" + tbmsg + "】");
		}
	}

}
