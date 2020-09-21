package com.hr.access.ctr;

import java.util.ArrayList;
import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.server.cjpa.JPAController;
import com.hr.access.entity.Hr_access_emauthority_list;
import com.hr.access.entity.Hr_access_emauthority_sum;

public class CtrHr_access_emauthority_list extends JPAController {
	// 流水表保存时，自动计算余额表中的数值
	@Override
	public void OnSave(CJPABase jpa, CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
		Hr_access_emauthority_list as = (Hr_access_emauthority_list) jpa;
		// 当为离职时(2)时，根据员工定位需要调整的余额数据
		// if (as.source.equals("2")) {
		// String sqlstr = "UPDATE hr_access_emauthority_sum t " +
		// "	SET t.access_status = '2'," +
		// "		t.accrediter    = 'SYSTEM'," +
		// "		t.accredit_date = NOW()," +
		// "		t.remarks = '离职关闭'" +
		// "	WHERE t.employee_code = '" + as.employee_code.getValue() + "'";
		// con.execsql(sqlstr);
		// }
		// else {
		String strsql = "SELECT *" +
				"  FROM hr_access_emauthority_sum t" +
				" WHERE t.er_id =" + as.er_id.getValue()+
				"   and t.access_list_id = " + as.access_list_id.getValue();
		Hr_access_emauthority_sum aes = new Hr_access_emauthority_sum();
		aes.findBySQL4Update(con, strsql, false);
		aes.er_id.setValue(as.er_id.getValue());
		aes.employee_code.setValue(as.employee_code.getValue());
		aes.employee_name.setValue(as.employee_name.getValue());
		aes.sex.setValue(as.sex.getValue());
		aes.access_card_number.setValue(as.access_card_number.getValue());
		aes.access_card_seq.setValue(as.access_card_seq.getValue());
		aes.hiredday.setValue(as.hiredday.getValue());
		aes.orgid.setValue(as.orgid.getValue());
		aes.orgname.setValue(as.orgname.getValue());
		aes.orgcode.setValue(as.orgcode.getValue());
		aes.extorgname.setValue(as.extorgname.getValue());
		aes.hwc_namezl.setValue(as.hwc_namezl.getValue());
		aes.lv_id.setValue(as.lv_id.getValue());
		aes.lv_num.setValue(as.lv_num.getValue());
		aes.access_list_id.setValue(as.access_list_id.getValue());
		aes.access_list_code.setValue(as.access_list_code.getValue());
		aes.access_list_model.setValue(as.access_list_model.getValue());
		aes.deploy_area.setValue(as.deploy_area.getValue());
		aes.access_place.setValue(as.access_place.getValue());
		aes.access_status.setValue(as.access_status.getValue());
		aes.accrediter.setValue(as.accrediter.getValue());
		aes.accredit_date.setAsDatetime(new Date());//是否需要更新？？？？ 理论上需要更新
		aes.remarks.setValue(as.remarks.getValue());
		aes.save(con, false);
		// }
	}

}
