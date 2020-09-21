package com.hr.base.ctr;

import java.util.Date;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hrrl_declare;

public class CtrHrrl_declare extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			checkOldDeclare(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc arg3, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			checkOldDeclare(jpa, con);
		}
	}

	// 不为空的返回值，将作为查询结果返回给前台
	@Override
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		Hrrl_declare dec = (Hrrl_declare) jpa;
		dec.useable.setAsInt(2);
		dec.disusetime.setAsDatetime(new Date());
		return null;
	}

	private void checkOldDeclare(CJPA jpa, CDBConnection con) throws Exception {
		Hrrl_declare dec = (Hrrl_declare) jpa;
		if (dec.ldtype.getAsIntDefault(0) == 1) {
			if (!dec.ldid.isEmpty() && !dec.er_id.isEmpty() && !dec.rer_id.isEmpty()) {
				String sqlstr = "UPDATE hrrl_declare SET useable=2,disusetime=NOW() WHERE ldtype=1 AND er_id=" + dec.er_id.getValue()
						+ " AND rer_id=" + dec.rer_id.getValue() + " AND useable=1 and stat=9 and ldid<>" + dec.ldid.getValue();
				con.execsql(sqlstr);
			}
		}
	}

}
