package com.hr.attd.ctr;

import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_overtime_adjust;
import com.hr.attd.entity.Hrkq_overtime_qual_break;
import com.hr.attd.entity.Hrkq_overtime_qual_line;

public class CtrHrkq_overtime_qual_break extends JPAController {
	
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_overtime_qual_break) jpa).oqb_code.getValue());
	}
	
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			dobread(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			dobread(jpa, con);
		}
	}

	private void dobread(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_overtime_qual_break qb = (Hrkq_overtime_qual_break) jpa;
		Hrkq_overtime_qual_line ql = new Hrkq_overtime_qual_line();
		ql.findByID(qb.oql_id.getValue(), false);
		if (ql.isEmpty())
			throw new Exception("ID为【" + qb.oql_id.getValue() + "】的资格申请单行不存在");
		ql.breaked.setAsInt(1);
		ql.save(con);
	}
}
