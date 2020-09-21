package com.hr.attd.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_overtime_qual;
import com.hr.attd.entity.Hrkq_overtime_qual_line;

public class CtrHrkq_overtime_qual extends JPAController {
	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_overtime_qual) jpa).oq_code.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			// doUpdateOther(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			// doUpdateOther(jpa, con);
		}
	}

	private void doUpdateOther(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_overtime_qual oq = (Hrkq_overtime_qual) jpa;
		CJPALineData<Hrkq_overtime_qual_line> oqls = oq.hrkq_overtime_qual_lines;
		for (CJPABase j : oqls) {
			Hrkq_overtime_qual_line oql = (Hrkq_overtime_qual_line) j;
			String sqlstr = "UPDATE hrkq_overtime_qual_line SET breaked=1,remark= CONCAT(remark,'" + oq.oq_code.getValue() + "审批通过') "
					+ " WHERE breaked=2 AND hrkq_overtime_qual_line.er_id=" + oql.er_id.getValue() + " and oql_id<>" + oql.oql_id.getValue()
					+ " AND EXISTS( "
					+ " SELECT 1 FROM hrkq_overtime_qual WHERE hrkq_overtime_qual_line.oq_id=hrkq_overtime_qual.oq_id AND hrkq_overtime_qual.stat=9)";
			con.execsql(sqlstr);
		}

	}

}
