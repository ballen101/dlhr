package com.hr.inface.ctr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.hr.access.entity.Hr_access_emauthority_sum;
import com.hr.access.entity.Hr_access_list;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.inface.entity.View_ICCO_AssignEmp;

public class TimerTaskHRAccessSum extends TimerTask {
	private static int syncrowno = 5000;

	@Override
	public void run() {
		try {
			String sqlstr = "SELECT t.parmvalue FROM hrkq_parms t where t.parmcode = 'ACCESS_SUM_SYNTIME'";
			Hr_access_emauthority_sum aes1 = new Hr_access_emauthority_sum();
			List<HashMap<String, String>> sws = aes1.pool.openSql2List(sqlstr);
			String mx = ((sws.size() == 0) || (sws.get(0).get("parmvalue") == null)) ? "" : String.valueOf(sws.get(0).get("parmvalue"));
			CJPALineData<Hr_access_emauthority_sum> aes = new CJPALineData<Hr_access_emauthority_sum>(Hr_access_emauthority_sum.class);
			sqlstr = "SELECT * FROM hr_access_emauthority_sum t WHERE t.updatetime>'" + mx + "' order by t.updatetime LIMIT 0," + syncrowno;
			aes.findDataBySQL(sqlstr, true, false);
			CJPALineData<View_ICCO_AssignEmp> iae = new CJPALineData<View_ICCO_AssignEmp>(View_ICCO_AssignEmp.class);
			String lastDate = "";
			for (CJPABase jpa : aes) {
				Hr_access_emauthority_sum ae = (Hr_access_emauthority_sum) jpa;
				View_ICCO_AssignEmp iae1 = new View_ICCO_AssignEmp();
				Hr_ykt_card yc = new Hr_ykt_card();
				String sqlstr2 = "SELECT * FROM hr_ykt_card t WHERE t.employee_code = '" + ae.employee_code.getValue() + "'";
				yc.findBySQL(sqlstr2);
				if (yc.isEmpty())
					throw new Exception("工号【" + ae.employee_code.getValue() + "】不存在");
				Hr_access_list al = new Hr_access_list();
				String sqlstr4 = "SELECT * FROM hr_access_list t WHERE t.access_list_id = " + ae.access_list_id.getValue();
				al.findBySQL(sqlstr4);
				/*
				 * iae1.card_id.setValue(yc.card_id.getValue());
				 * iae1.clock_id.setValue(ae.access_list_code.getValue());//门禁编码
				 * iae1.opDate.setValue(ae.accredit_date.getValue());//授权日期
				 * iae1.join_id.setValue(ae.employee_name.getValue());//员工姓名
				 * iae1.card_sn.setValue(yc.card_sn.getValue());//卡号
				 * iae1.depart_id.setValue(ae.orgcode.getValue());//部门编码
				 * iae.add(iae1);
				 */
				lastDate = ae.updatetime.getValue();
				if (!al.isEmpty()) {
					String sqlstr3 = "INSERT INTO dbo.ICCO_TaskCard (card_id,clock_id,ope_date,join_id,card_sn,depart_id,user_name,code,state,door_val,down_flag) VALUES ('" +
							yc.card_number.getValue() + "','" + al.access_list_code.getValue() + "','" + ae.accredit_date.getValue() + "','" + yc.er_id.getValue() +
							"','" + yc.card_sn.getValue() + "','" + ae.orgcode.getValue() + "','" + ae.creator.getValue() + "','" + yc.employee_code.getValue() +
							"','0','01020304','0')";
					DBPools.poolByName("oldtxmssql").execsql(sqlstr3);
				}
			}
			if (aes.size() > 0) {
				// iae.saveBatchSimple();// 高速存储
				String sqlstr1 = "UPDATE hrkq_parms t SET t.parmvalue = '" + lastDate + "' WHERE t.parmcode='ACCESS_SUM_SYNTIME'";
				aes1.pool.execsql(sqlstr1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
