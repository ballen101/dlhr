package com.hr.access.co;

import java.util.Date;
import java.util.ArrayList;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.retention.ACO;
import com.hr.access.entity.Hr_access_emauthority_list;
import com.hr.access.entity.Hr_access_emauthority_sum;
import com.hr.access.entity.Hr_access_orauthority;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.Access")
public class COHr_access_emauthority_list {

	/**
	 * @param con
	 * @param employee_code
	 * @param source_code取值范围1
	 *            ：1入职发卡；2：离职；3：调动；4；特殊授权；5 机构授权； 6 新卡授权
	 * 
	 *            source:按 1 发卡或补卡；7 清卡 8： 挂失 ;2：离职；3：调动；4；特殊授权；5 机构授权； 6 新卡授权,
	 * 
	 *            1：启用，2：禁用
	 * @param source_num取对应的单据号
	 * @param change_reason
	 * @throws Exception
	 */
	// public static void doUpdateAccessList1(CDBConnection con, String employee_code, int source,
	// String source_num, String change_reason) throws Exception {
	// Hr_access_emauthority_list as = new Hr_access_emauthority_list();
	// // 根据工号取值员工信息
	// Hr_employee emp = new Hr_employee();
	// emp.findBySQL(con, "select * from hr_employee where employee_code='" + employee_code + "'", false);
	// if (emp.isEmpty())
	// throw new Exception("未找到工号为【" + employee_code + "】的员工资料");
	// // 根据工号取值门禁卡信息
	// Hr_ykt_card ac = new Hr_ykt_card();
	// ac.findBySQL("select * from hr_ykt_card ac where ac.employee_code='" + employee_code + "'", false);
	// CJPALineData<Hr_access_orauthority> aos = new CJPALineData<Hr_access_orauthority>(Hr_access_orauthority.class);
	// aos.findDataBySQL("select * from hr_access_orauthority ao where ao.orgid=" + emp.orgid.getValue(), true, false);
	// for (CJPABase jpa1 : aos) {
	// Hr_access_orauthority ao = (Hr_access_orauthority) jpa1;
	// as.clear();
	// as.source.setAsInt(source);
	// as.source_id.setValue(source_num);
	// as.source_num.setValue(source_num);
	// as.change_reason.setValue(change_reason);
	// as.employee_code.setValue(emp.employee_code.getValue());
	// as.employee_name.setValue(emp.employee_name.getValue());
	// as.sex.setValue(emp.sex.getValue());
	// as.access_card_number.setValue(ac.card_number.getValue());
	// as.access_card_seq.setValue(ac.card_sn.getValue());
	// as.hiredday.setValue(emp.hiredday.getValue());
	// as.orgid.setValue(emp.orgid.getValue());
	// as.orgname.setValue(emp.orgname.getValue());
	// as.orgcode.setValue(emp.orgcode.getValue());
	// as.extorgname.setValue(emp.orgname.getValue());
	// as.hwc_namezl.setValue(emp.hwc_namezl.getValue());
	// as.lv_id.setValue(emp.lv_id.getValue());
	// as.lv_num.setValue(emp.lv_num.getValue());
	// as.access_list_id.setValue(ao.access_list_id.getValue());
	// as.access_list_code.setValue(ao.access_list_code.getValue());
	// as.access_list_model.setValue(ao.access_list_model.getValue());
	// as.deploy_area.setValue(ao.deploy_area.getValue());
	// as.access_place.setValue(ao.access_place.getValue());
	// as.accrediter.setValue("SYSTEM");
	// as.accredit_date.setAsDatetime(new Date());
	// as.remarks.setValue(change_reason);
	// if (source == 7) {// 清卡
	// as.access_status.setAsInt(2);
	// } else if (source == 1) {// 发卡或补卡
	// as.access_status.setAsInt(1);
	// } else if (source == 8) {// 挂失
	// as.access_status.setAsInt(1);
	// } else
	// throw new Exception("来源类型错误【" + source + "】");
	//
	// as.save(con, false);
	// }
	// }

	// 人员调到自动写入门禁登记流水表
	/**
	 * @param con
	 * @param employee_code
	 * @param source_code对应传入
	 *            “3”
	 * @param source_num取对应的单据号
	 * @param change_reason
	 * @param orgfrom_id调出部门
	 * @param orgto_id调入部门
	 * @throws Exception
	 */
	// public static void doUpdateAccessList2(CDBConnection con, String employee_code, String source_code,
	// String source_num, String change_reason, String orgfrom_id, String orgto_id) throws Exception {
	// Hr_access_emauthority_list as = new Hr_access_emauthority_list();
	// // 根据工号取值员工信息
	// Hr_employee emp = new Hr_employee();
	// emp.findBySQL("select * from hr_employee where employee_code='" + employee_code + "'", false);
	// // 根据工号取值门禁卡信息
	// Hr_ykt_card ac = new Hr_ykt_card();
	// ac.findBySQL("select * from hr_ykt_card ac where ac.employee_code='" + employee_code + "'", false);
	// CJPALineData<Hr_access_orauthority> aos = new CJPALineData<Hr_access_orauthority>(Hr_access_orauthority.class);
	// aos.findDataBySQL("select * from hr_access_orauthority ao where ao.orgid=" + orgfrom_id, true, false);
	// for (CJPABase jpa1 : aos) {
	// Hr_access_orauthority ao = (Hr_access_orauthority) jpa1;
	// as.clear();
	// as.source.setValue(source_code);
	// as.source_id.setValue(source_num);
	// as.source_num.setValue(source_num);
	// as.change_reason.setValue(change_reason);
	// as.employee_code.setValue(emp.employee_code.getValue());
	// as.employee_name.setValue(emp.employee_name.getValue());
	// as.sex.setValue(emp.sex.getValue());
	// as.access_card_number.setValue(ac.card_number.getValue());
	// as.access_card_seq.setValue(ac.card_sn.getValue());
	// as.hiredday.setValue(emp.hiredday.getValue());
	// as.orgid.setValue(emp.orgid.getValue());
	// as.orgname.setValue(emp.orgname.getValue());
	// as.orgcode.setValue(emp.orgcode.getValue());
	// as.extorgname.setValue(emp.orgname.getValue());
	// as.hwc_namezl.setValue(emp.hwc_namezl.getValue());
	// as.lv_id.setValue(emp.lv_id.getValue());
	// as.lv_num.setValue(emp.lv_num.getValue());
	// as.access_list_id.setValue(ao.access_list_id.getValue());
	// as.access_list_code.setValue(ao.access_list_code.getValue());
	// as.access_list_model.setValue(ao.access_list_model.getValue());
	// as.deploy_area.setValue(ao.deploy_area.getValue());
	// as.access_place.setValue(ao.access_place.getValue());
	// as.access_status.setValue("2");
	// as.accrediter.setValue("SYSTEM");
	// as.accredit_date.setAsDatetime(new Date());
	// as.remarks.setValue("调动关闭");
	// as.save(con);
	// }
	//
	// CJPALineData<Hr_access_orauthority> ao = new CJPALineData<Hr_access_orauthority>(Hr_access_orauthority.class);
	// ao.findDataBySQL("select * from hr_access_orauthority ao where ao.orgid=" + orgto_id, true, false);
	// for (CJPABase jpa2 : ao) {
	// Hr_access_orauthority ao1 = (Hr_access_orauthority) jpa2;
	// as.clear();
	// as.source.setValue(source_code);
	// as.source_id.setValue(source_num);
	// as.source_num.setValue(source_num);
	// as.change_reason.setValue(change_reason);
	// as.employee_code.setValue(emp.employee_code.getValue());
	// as.employee_name.setValue(emp.employee_name.getValue());
	// as.sex.setValue(emp.sex.getValue());
	// as.access_card_number.setValue(ac.card_number.getValue());
	// as.access_card_seq.setValue(ac.card_sn.getValue());
	// as.hiredday.setValue(emp.hiredday.getValue());
	// as.orgid.setValue(emp.orgid.getValue());
	// as.orgname.setValue(emp.orgname.getValue());
	// as.orgcode.setValue(emp.orgcode.getValue());
	// as.extorgname.setValue(emp.orgname.getValue());
	// as.hwc_namezl.setValue(emp.hwc_namezl.getValue());
	// as.lv_id.setValue(emp.lv_id.getValue());
	// as.lv_num.setValue(emp.lv_num.getValue());
	// as.access_list_id.setValue(ao1.access_list_id.getValue());
	// as.access_list_code.setValue(ao1.access_list_code.getValue());
	// as.access_list_model.setValue(ao1.access_list_model.getValue());
	// as.deploy_area.setValue(ao1.deploy_area.getValue());
	// as.access_place.setValue(ao1.access_place.getValue());
	// as.access_status.setValue("1");
	// as.accrediter.setValue("SYSTEM");
	// as.accredit_date.setAsDatetime(new Date());
	// as.remarks.setValue("入职新增");
	// as.save(con);
	// }
	// }

}
