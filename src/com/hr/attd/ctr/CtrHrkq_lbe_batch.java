package com.hr.attd.ctr;

import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_lbe_batch;
import com.hr.attd.entity.Hrkq_lbe_batch_line;
import com.hr.attd.entity.Hrkq_leave_blance;

public class CtrHrkq_lbe_batch extends JPAController{

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_lbe_batch)jpa).lbebcode.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			dobatchextend(jpa,con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if(isFilished){
			dobatchextend(jpa,con);
		}
	}
	
	private void dobatchextend(CJPA jpa, CDBConnection con) throws Exception{
		Hrkq_lbe_batch lbeb=(Hrkq_lbe_batch)jpa;
		CJPALineData<Hrkq_lbe_batch_line> lbebls=lbeb.hrkq_lbe_batch_lines;
		for(int i=0;i<lbebls.size();i++){
			Hrkq_lbe_batch_line line=(Hrkq_lbe_batch_line)lbebls.get(i);
			Hrkq_leave_blance lb = new Hrkq_leave_blance();
			lb.findByID(line.lbid.getValue());
			if (lb.isEmpty())
				throw new Exception("ID为【" + line.lbid.getValue() + "】的可调休信息不存在");
			if (lb.extended.getAsIntDefault(0) == 1)
				throw new Exception("ID为【" + line.lbid.getValue() + "】的可调休信息已经展期");
			if (lb.valdate.getAsDatetime().getTime() > line.newvaldate.getAsDatetime().getTime())
				throw new Exception("展期日期必须大于到期日期");
			lb.valdate.setAsDatetime(line.newvaldate.getAsDatetime());
			lb.extended.setAsInt(1);
			lb.save(con);
		}
		
	}

}
