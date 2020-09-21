package com.hr.perm.ctr;

import java.util.Date;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.perm.entity.Hr_employee_contract;
import com.hr.perm.entity.Hr_employee_contract_stop;

public class CtrHr_employee_contract_stop extends JPAController {

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			stopContract(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con,Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			stopContract(jpa, con);
		}
	}

	private void stopContract(CJPA jpa, CDBConnection con) throws Exception {
		Hr_employee_contract_stop hrconstop = (Hr_employee_contract_stop) jpa;
		if (hrconstop.isEmpty())
			throw new Exception("没找到相关联的终止合同表单!");
		if (!hrconstop.con_id.isEmpty()) {
			Hr_employee_contract hrecon = new Hr_employee_contract();
			hrecon.findByID(hrconstop.con_id.getValue());
			if (hrecon.isEmpty()) {
				throw new Exception("没找到关联ID为"
						+ hrconstop.con_id.getValue() + "的合同资料!");
			}
			if ((hrecon.stat.getAsInt() == 9)
					&& (hrecon.contractstat.getAsInt() == 1)) {
				hrecon.contractstat.setAsInt(2);
				if ((hrecon.deadline_type.getAsInt() == 1)
						&& (!hrecon.begin_date.isEmpty())) {
					hrecon.end_date.setAsDatetime(new Date());
				}
				hrecon.save(con);

			} else {
				throw new Exception("表单关联的合同资料未审核或合同非有效状态!");
			}
		}
		hrconstop.contractstat.setAsInt(2);
		if ((hrconstop.deadline_type.getAsInt() == 1)
				&& (!hrconstop.begin_date.isEmpty())) {
			hrconstop.end_date.setAsDatetime(new Date());
		}
		hrconstop.save(con);
	}

	// 终止某人合同
	public static void stopContract(CDBConnection con, String er_id, String reson) throws Exception {
		/*String sqlstr = "SELECT * FROM hr_employee_contract WHERE er_id=" + er_id + " AND stat=9 AND contractstat=1";
		Hr_employee_contract hrecon = new Hr_employee_contract();
		hrecon.findBySQL4Update(con, sqlstr, false);
		if (!hrecon.isEmpty()) {
			hrecon.contractstat.setAsInt(2);
			String remark = (hrecon.remark.isEmpty()) ? "" : hrecon.remark.getValue() + " ";
			hrecon.remark.setValue(remark + reson);
			hrecon.save(con);
		}*/
		String sqlstr = "UPDATE hr_employee_contract SET contractstat=2,remark=CONCAT(remark,'"+reson+"') WHERE stat=9 AND (contractstat=1 or contractstat=6) AND contract_type<>2 AND contract_type<>3 AND er_id=" + er_id ;
		con.execsql(sqlstr);
	}
}
