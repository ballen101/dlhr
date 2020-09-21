package com.hr.util;

import java.util.Date;
import java.util.TimerTask;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.hr.perm.entity.Hr_employee_contract;

public class TimerTaskHRContract extends TimerTask{
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Logsw.debug("将过期的合同状态修改为过期");
		try{
			//CDBPool pool = new Hr_employee_contract().pool;
			Hr_employee_contract contract =new Hr_employee_contract();
			String nschtime = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM-dd HH") + ":00:00";
			String sqlstr ="UPDATE hr_employee_contract SET contractstat=3 WHERE contractstat=1 AND  deadline_type=1 AND stat=9 AND end_date<='"+nschtime + "'";
			//CDBConnection con=contract.pool.getCon(this);
			//if (con == null)
			//	return;
			//con.startTrans();
			contract.pool.execsql(sqlstr);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
