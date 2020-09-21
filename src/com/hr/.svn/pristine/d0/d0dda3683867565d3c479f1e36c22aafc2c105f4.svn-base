package com.hr.access.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.generic.Shworg;
import com.hr.access.entity.Hr_access_emauthority_list;
import com.hr.access.entity.Hr_access_list;
import com.hr.access.entity.Hr_access_orauthority;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.perm.entity.Hr_employee;

//功能与需求不符，此表需将人员所拥有的机构门禁权限、特殊门禁权限显示，包括状态为可用与不可用的。（具体字段详见附表2）
//①新入职人员，入职的机构有机构门禁权限，此表增加此人门禁权限信息，状态为“可用”，备注标注“入职新增”；
//②调动时，调动前所拥有的所有门禁自动变为“不可用”，备注标注“调动关闭”；若调入的机构存在机构门禁，此表增加此人新机构的门禁权限信息，状态为“可用”，备注标注“入职新增”；
//③离职人员，离职后所有用的所拥有的所有门禁自动变为“不可用”，备注标注“离职关闭”；
//④特殊开通门禁时，此表增加此人门禁权限信息，状态为“可用”，备注标注“特殊新增”；
//⑤特殊门禁作废时，此表此人特殊门禁权限信息状态为“不可用”，备注标注“特殊关闭”；
//⑥机构门禁作废时，涉及到该机构门禁人员状态变更为“不可用”，备注标注“机构门禁作废关闭”

public class UtilAccess {

	/**
	 * 1 新发卡/补卡 2 调入
	 * 按机构授权 将机构所有门禁权限授予指定人
	 * 
	 * @throws Exception
	 */
	public static void appendAccessListByOrg(CDBConnection con, int er_id, String orgid, int source, String source_id, String source_num, String remarks) throws Exception {
		Hr_access_emauthority_list as = new Hr_access_emauthority_list();
		Hr_employee emp = new Hr_employee();
		emp.findByID(String.valueOf(er_id));
		if (emp.isEmpty())
			throw new Exception("未找到ID为【" + er_id + "】的员工资料");
		Hr_ykt_card ac = new Hr_ykt_card();
		String sqlstr = "SELECT * FROM hr_ykt_card WHERE er_id =" + er_id + " AND card_stat=1";
		ac.findBySQL(con, sqlstr, false);
		if (ac.isEmpty())
			throw new Exception("工号【" + emp.employee_code.getValue() + "】无可用卡信息");
		sqlstr = "select * from hr_access_orauthority ao where ao.orgid=" + orgid;
		CJPALineData<Hr_access_orauthority> aos = new CJPALineData<Hr_access_orauthority>(Hr_access_orauthority.class);
		aos.findDataBySQL(con, sqlstr, true, false);
		for (CJPABase jpa1 : aos) {
			Hr_access_orauthority ao = (Hr_access_orauthority) jpa1;
			as.clear();
			as.source.setAsInt(source);
			as.source_id.setValue(source_num);
			as.source_num.setValue(source_num);
			as.change_reason.setValue(remarks);
			as.er_id.setValue(emp.er_id.getValue());
			as.employee_code.setValue(emp.employee_code.getValue());
			as.employee_name.setValue(emp.employee_name.getValue());
			as.sex.setValue(emp.sex.getValue());
			as.access_card_number.setValue(ac.card_number.getValue());
			as.access_card_seq.setValue(ac.card_sn.getValue());
			as.hiredday.setValue(emp.hiredday.getValue());
			as.orgid.setValue(emp.orgid.getValue());
			as.orgname.setValue(emp.orgname.getValue());
			as.orgcode.setValue(emp.orgcode.getValue());
			as.extorgname.setValue(emp.orgname.getValue());
			as.hwc_namezl.setValue(emp.hwc_namezl.getValue());
			as.lv_id.setValue(emp.lv_id.getValue());
			as.lv_num.setValue(emp.lv_num.getValue());
			as.access_list_id.setValue(ao.access_list_id.getValue());
			as.access_list_code.setValue(ao.access_list_code.getValue());
			as.access_list_model.setValue(ao.access_list_model.getValue());
			as.deploy_area.setValue(ao.deploy_area.getValue());
			as.access_place.setValue(ao.access_place.getValue());
			as.accrediter.setValue("SYSTEM");
			as.accredit_date.setAsDatetime(new Date());
			as.remarks.setValue(remarks);
			as.access_status.setAsInt(1);
			as.save(con, false);
		}
	}

	/**
	 * 将门禁权限授予指定员工
	 * 
	 * @param con
	 * @param ac
	 *            门禁
	 * @param emp
	 *            员工
	 * @param accstat
	 *            1 授权 2 取消授权
	 * @param source
	 * @param source_id
	 * @param source_num
	 * @param remarks
	 * @throws Exception
	 */
	public static void appendOneAccess(CDBConnection con, Hr_access_list acl, Hr_employee emp, int accstat,
			int source, String source_id, String source_num, String remarks) throws Exception {
		//System.out.println("appendOneAccess!!!!!!!!!!!!!!!!");
		Hr_access_emauthority_list as = new Hr_access_emauthority_list();
		Hr_ykt_card ac = new Hr_ykt_card();
		String sqlstr = "SELECT * FROM hr_ykt_card WHERE er_id =" + emp.er_id.getValue() + " AND card_stat=1";
		ac.findBySQL(con, sqlstr, false);
		if (ac.isEmpty())
			throw new Exception("工号【" + emp.employee_code.getValue() + "】无可用卡信息");
		as.clear();
		as.source.setAsInt(source);
		as.source_id.setValue(source_id);
		as.source_num.setValue(source_num);
		as.change_reason.setValue(remarks);
		as.er_id.setValue(emp.er_id.getValue());
		as.employee_code.setValue(emp.employee_code.getValue());
		as.employee_name.setValue(emp.employee_name.getValue());
		as.sex.setValue(emp.sex.getValue());
		as.access_card_number.setValue(ac.card_number.getValue());
		as.access_card_seq.setValue(ac.card_sn.getValue());
		as.hiredday.setValue(emp.hiredday.getValue());
		as.orgid.setValue(emp.orgid.getValue());
		as.orgname.setValue(emp.orgname.getValue());
		as.orgcode.setValue(emp.orgcode.getValue());
		as.extorgname.setValue(emp.orgname.getValue());
		as.hwc_namezl.setValue(emp.hwc_namezl.getValue());
		as.lv_id.setValue(emp.lv_id.getValue());
		as.lv_num.setValue(emp.lv_num.getValue());
		as.access_list_id.setValue(acl.access_list_id.getValue());
		as.access_list_code.setValue(acl.access_list_code.getValue());
		as.access_list_model.setValue(acl.access_list_model.getValue());
		as.deploy_area.setValue(acl.deploy_area.getValue());
		as.access_place.setValue(acl.deploy_area.getValue());
		as.access_status.setAsInt(accstat);
		as.accrediter.setValue("SYSTEM");
		as.accredit_date.setAsDatetime(new Date());
		as.remarks.setValue(remarks);
		as.save(con, false);
	}

	/**
	 * 批量更新权限表为不可用
	 * 
	 * 清卡、挂失、离职
	 * 
	 * @param con
	 * @param er_id
	 * @throws Exception
	 */
	public static void disableAllAccessByEmpid(CDBConnection con, int er_id, String remarks) throws Exception {
		String sqlstr = "UPDATE hr_access_emauthority_sum SET  access_status = 2, remarks = '" + remarks + "', accredit_date = NOW() WHERE er_id = "
				+ er_id + "   AND access_status = 1 ";
		con.execsql(sqlstr);
	}

	/**
	 * 批量更新权限表为不可用
	 * 
	 * @param con
	 * @param access_list_id
	 *            门
	 * @param orgid
	 *            机构
	 * @param includechild
	 *            是否包含子机构
	 * @param remarks
	 *            备注
	 * @throws Exception
	 */
	public static void disableAllAccessByAccAndOrg(CDBConnection con, String access_list_id, String orgid, boolean includechild, String remarks) throws Exception {
		Shworg org = new Shworg();
		org.findByID(orgid);
		String sqlstr = "UPDATE hr_access_emauthority_sum SET  access_status=2, remarks = '" + remarks + "', accredit_date = NOW() "
				+ "WHERE access_status=1 AND access_list_id=" + access_list_id + " AND EXISTS( "
				+ " SELECT 1 FROM shworg WHERE shworg.orgid=hr_access_emauthority_sum.orgid ";
		if (includechild)
			sqlstr = sqlstr + " AND shworg.idpath LIKE '" + org.idpath.getValue() + "%'";
		else
			sqlstr = sqlstr + " AND shworg.orgid=" + orgid;
		sqlstr = sqlstr + ")";
		con.execsql(sqlstr);
	}
}
