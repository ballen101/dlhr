package com.hr.publishedco;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.DictionaryTemp;
import com.hr.attd.entity.Hrkq_holidayapp_cancel;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_transfer;

@ACO(coname = "hr.transfer")
public class COPublicTransfer {

	//	{
	//	    "employee_code": "540039",
	//	    "tranflev": "4",
	//	    "tranftype1": "3",
	//	    "tranftype2": "1",
	//	    "tranftype3": "4",
	//	    "tranfreason": "测试",
	//	    "probation": "0",
	//	    "oldcalsalarytype": "计件",
	//	    "oldchecklev": "5",
	//   	"neworgcode": "00012400",
	//      "newospcode": "OP2001000300",
	//	    "neworgcode": "00012400",
	//	    "newospcode": "OP2001000300",
	//	    "newsp_name": "装配工",
	//	    "newattendtype": "3",
	//	    "newcalsalarytype": "计件",
	//	    "newstru_id": "2",
	//	    "newstru_name": "5天无考核",
	//	    "newchecklev": "5",
	//	    "salary_qcnotnull": "2",
	//	    "istp": "2",
	//	    "tranamt": "0.00",
	//	    "exam_score": "0.00",
	//	    "mupexam_score": "0.00",
	//	    "quota_over": "2",
	//	    "quota_over_rst": "0",
	//	    "isdreamposition": "2",
	//	    "isdreamemploye": "2",
	//	    "tranftype4": "4",
	//	    "isexistsrl": "2",
	//	    "stat": "1",
	//	    "idpath": "1,179,181,144,",
	//	    "entid": "1",
	//	    "creator": "DEV",
	//	    "createtime": "2020-09-15 10:17:58",
	//	    "updator": "DEV",
	//	    "updatetime": "2020-09-15 10:17:58",
	//	    "shw_attachs": [],
	//	    "hr_employee_transfer_rls": []
	//	}

	@ACOAction(eventname = "getTransferJson", Authentication = false, notes = "创建调动单")
	public String getTransferJson() throws Exception {
		Hr_employee_transfer hr_employee_transfer = new Hr_employee_transfer();// 创建销假表单实体
		hr_employee_transfer.findByID("180303");
		return hr_employee_transfer.tojson();
	}
	@ACOAction(eventname = "hr_add_transfer", Authentication = false, notes = "创建调单并自动提交生效")
	public String hr_add_transfer() throws Exception{
		Hr_employee emp = new Hr_employee();
		Hr_orgposition oldosp = new Hr_orgposition();
		Hr_orgposition newosp = new Hr_orgposition();
		Hr_employee_transfer tr = new Hr_employee_transfer();
		CDBConnection con = emp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		tr.fromjson(CSContext.getPostdata());// 从前端JSON获取数据
		String employee_code =tr.employee_code.getValue();
		if ((employee_code == null) || (employee_code.isEmpty())){
			return "N";
		}


		emp.clear();
		emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
		if (emp.isEmpty())
			throw new Exception("工号【" + employee_code + "】不存在人事资料");

		oldosp.clear();
		oldosp.findBySQL("SELECT * FROM hr_orgposition WHERE ospcode='" + tr.odospcode.getValue()  + "'", false);
		if (oldosp.isEmpty())
			throw new Exception("工号【" + employee_code + "】的调动前机构职位编码【" + tr.odospcode.getValue()+ "】不存在");

		newosp.clear();
		newosp.findBySQL("SELECT * FROM hr_orgposition WHERE ospcode='" + tr.newospcode.getValue() + "'", false);
		if (newosp.isEmpty())
			throw new Exception("工号【" + employee_code + "】的调动后机构职位编码【" + tr.newospcode.getValue() + "】不存在");

		tr.clear();
		tr.er_id.setValue(emp.er_id.getValue()); // 人事ID
		tr.employee_code.setValue(emp.employee_code.getValue()); // 工号
		tr.id_number.setValue(emp.id_number.getValue()); // 身份证号
		tr.employee_name.setValue(emp.employee_name.getValue()); // 姓名
		tr.mnemonic_code.setValue(emp.mnemonic_code.getValue()); // 助记码
		tr.email.setValue(emp.email.getValue()); // 邮箱/微信
		tr.empstatid.setValue(emp.empstatid.getValue()); // 人员状态
		tr.telphone.setValue(emp.telphone.getValue()); // 电话
		//tr.tranfcmpdate.setValue(v.get("tranfcmpdate")); // 调动时间
		//	tr.hiredday.setValue(emp.hiredday.getValue()); // 聘用日期
		tr.degree.setValue(emp.degree.getValue()); // 学历
		tr.tranflev.setValue(dictemp.getVbCE("1215",tr.tranflev.getValue(), false, "工号【" + employee_code + "】调动层级【" +tr.tranflev.getValue() + "】不存在")); // 调动层级
		tr.tranftype1.setValue(dictemp.getVbCE("719",tr.tranftype1.getValue(), false, "工号【" + employee_code + "】调动类别【" + tr.tranftype1.getValue() + "】不存在")); // 调动类别
		tr.tranftype2.setValue(dictemp.getVbCE("724",tr.tranftype2.getValue(), false, "工号【" + employee_code + "】调动性质【" +tr.tranftype2.getValue() + "】不存在")); // 调动性质
		tr.tranftype3.setValue(dictemp.getVbCE("729",tr.tranftype3.getValue(), true, "工号【" + employee_code + "】调动范围【" + tr.tranftype3.getValue() + "】不存在")); // 调动范围

		tr.ispromotioned.setValue("1"); // 已转正
		tr.odorgid.setValue(oldosp.orgid.getValue()); // 调动前部门ID
		tr.odorgcode.setValue(oldosp.orgcode.getValue()); // 调动前部门编码
		tr.odorgname.setValue(oldosp.orgname.getValue()); // 调动前部门名称
		tr.odorghrlev.setValue("0"); // 调调动前部门人事层级
		tr.odlv_id.setValue(oldosp.lv_id.getValue()); // 调动前职级ID
		tr.odlv_num.setValue(oldosp.lv_num.getValue()); // 调动前职级
		tr.odhg_id.setValue(oldosp.hg_id.getValue()); // 调动前职等ID
		tr.odhg_code.setValue(oldosp.hg_code.getValue()); // 调动前职等编码
		tr.odhg_name.setValue(oldosp.hg_name.getValue()); // 调动前职等名称
		tr.odospid.setValue(oldosp.ospid.getValue()); // 调动前职位ID
		tr.odospcode.setValue(oldosp.ospcode.getValue()); // 调动前职位编码
		tr.odsp_name.setValue(oldosp.sp_name.getValue()); // 调动前职位名称
		String oldemnature = (oldosp.isoffjob.getAsIntDefault(0) == 1) ? "脱产" : "非脱产";
		tr.oldemnature.setValue(oldemnature);// 调动后职位性质
		tr.oldemnature.setValue(emp.emnature.getValue());// 调动前职位性质
		tr.odattendtype.setValue(emp.atdtype.getValue()); // 调动前出勤类别
		tr.oldcalsalarytype.setValue("0"); // 调动前计薪方式
		tr.oldhwc_namezl.setValue(oldosp.hwc_namezl.getValue()); // 调动前职类
		tr.oldhwc_namezq.setValue(oldosp.hwc_namezq.getValue());// 调动前职群
		tr.oldhwc_namezz.setValue(oldosp.hwc_namezz.getValue());// 调动前职种
		tr.oldposition_salary.setValue("0"); // 调动前职位工资
		tr.oldbase_salary.setValue("0"); // 调动前基本工资
		tr.oldtech_salary.setValue("0"); // 调动前技能工资
		tr.oldachi_salary.setValue("0"); // 调动前技能工资
		tr.oldtech_allowance.setValue("0"); // 调动前技术津贴
		tr.oldavg_salary.setValue("0"); // 调动前平均工资
		tr.neworgid.setValue(newosp.orgid.getValue()); // 调动后部门ID
		tr.neworgcode.setValue(newosp.orgcode.getValue()); // 调动后部门编码
		tr.neworgname.setValue(newosp.orgname.getValue()); // 调动后部门名称
		tr.neworghrlev.setValue("0"); // 调动后部门人事层级*
		tr.newlv_id.setValue(newosp.lv_id.getValue()); // 调动后职级ID
		tr.newlv_num.setValue(newosp.lv_num.getValue()); // 调动后职级
		tr.newhg_id.setValue(newosp.hg_id.getValue()); // 调动后职等ID
		tr.newhg_code.setValue(newosp.hg_code.getValue()); // 调动后职等编码
		tr.newhg_name.setValue(newosp.hg_name.getValue()); // 调动后职等名称
		tr.newospid.setValue(newosp.ospid.getValue()); // 调动后职位ID
		tr.newospcode.setValue(newosp.ospcode.getValue()); // 调动后职位编码
		tr.newsp_name.setValue(newosp.sp_name.getValue()); // 调动后职位名称
		String newemnature = (newosp.isoffjob.getAsIntDefault(0) == 1) ? "脱产" : "非脱产";
		tr.newemnature.setValue(newemnature);// 调动后职位性质
		tr.newattendtype.setValue("0"); // 19.3.21 取消 调动后出勤类别 dictemp.getVbCE("1399", v.get("newattendtype"), true, "工号【" + employee_code + "】调动后出勤类别【" + v.get("newattendtype") + "】不存在")
		tr.newcalsalarytype.setValue("0"); // 调动后计薪方式
		tr.newhwc_namezl.setValue(newosp.hwc_namezl.getValue()); // 调动后职类
		tr.newhwc_namezq.setValue(newosp.hwc_namezq.getValue());// 调动后职群
		tr.newhwc_namezz.setValue(newosp.hwc_namezz.getValue());// 调动后职种
		tr.newposition_salary.setValue("0"); // 调动前职位工资
		tr.newbase_salary.setValue("0"); // 调动后基本工资
		tr.newtech_salary.setValue("0"); // 调动后技能工资
		tr.newachi_salary.setValue("0"); // 调动后技能工资
		tr.newtech_allowance.setValue("0"); // 调动后技术津贴
		tr.newavg_salary.setValue("0"); // 调动后平均工资
		tr.salary_quotacode.setValue("0"); // 可用工资额度证明编号
		tr.salary_quota_stand.setValue("0"); // 标准工资额度
		tr.salary_quota_canuse.setValue("0"); // 可用工资额度
		tr.salary_quota_used.setValue("0"); // 己用工资额度
		tr.salary_quota_blance.setValue("0"); // 可用工资余额
		tr.salary_quota_appy.setValue("0"); // 申请工资额度
		tr.salary_quota_ovsp.setValue("0"); // 超额度审批
		tr.salarydate.setValue(null); // 核薪生效日期
		tr.istp.setValue(dictemp.getVbCE("5", tr.istp.getValue(), true, "工号【" + employee_code + "】是否词汇【" + tr.istp.getValue() + "】不存在")); // 是否特批

		tr.quota_over.setValue("2"); // 是否超编
		tr.quota_over_rst.setValue("2"); // 超编审批结果 1 允许增加编制调动 ps是否自动生成编制调整单 2 超编调动 3 不允许调动
		tr.isdreamposition.setValue("2"); // 是否梦职场调入
		tr.isdreamemploye.setValue("2"); // 是否梦职场储备员工
		tr.tranftype4.setValue(dictemp.getVbCE("1189",tr.tranftype4.getValue(), true, "工号【" + employee_code + "】调动类型【" +tr.tranftype4.getValue() + "】不存在")); // 调动类型
		// 4其他调动
		tr.isexistsrl.setValue("2"); // 关联关系 有关联关系 无关联关系
		tr.rlmgms.setValue("1"); // 管控措施 不需管控 终止调动 申请豁免
		tr.ismangerl.setValue("2"); // 是否构成需要管控的管理关系类别
		tr.isapprlhm.setValue(null); // 是否申请关联关系豁免
		tr.isapprlhmsp.setValue(null); // 关联关系豁免申请是否得到审批
		tr.quotastand.setValue("0"); // 标准配置人数
		tr.quotasj.setValue("0"); // 实际配置人数
		tr.quotacq.setValue("0"); // 超缺编人数(正数表示超编、负数表示缺编)
		tr.isallowdrmin.setValue(null); // 是否同意特批调动至梦职场职位
		tr.idpath.setValue(emp.idpath.getValue()); // idpath
		tr.stat.setAsInt(9);
		tr.attribute3.setValue("BMS接口写入");
		tr.save(con);
		tr.wfcreate(null, con);
		return "Y";
	}
}
