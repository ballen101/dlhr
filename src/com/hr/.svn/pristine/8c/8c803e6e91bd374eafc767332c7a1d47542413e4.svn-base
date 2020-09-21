package com.hr.attd.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_business_trip;
import com.hr.attd.entity.Hrkq_business_trip_batch;
import com.hr.attd.entity.Hrkq_business_trip_batchline;

public class CtrHrkq_business_trip_batch extends JPAController {
	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hrkq_business_trip_batch btbBatch=(Hrkq_business_trip_batch) jpa ;
		
		for(CJPABase jb : btbBatch.hrkq_business_trip_batchlines){
			Hrkq_business_trip_batchline bttbl =(Hrkq_business_trip_batchline) jb;
		float hours=	CacalKQData.calcDateDiffHH(bttbl.begin_date.getAsDatetime(), bttbl.end_date.getAsDatetime());
		
		//float hours = CacalKQData.calcDateDiffHH(bgtime, edtime);
		float days = (float) hours / 8;// 按每天8小时计算
		if ((days * 10 % 5) != 0)
			throw new Exception("算法错误，最小单位为半天");
		bttbl.tripdays.setValue(days);
		}	
		
	}

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		Hrkq_business_trip_batch bt = (Hrkq_business_trip_batch) jpa;
		wf.subject.setValue(bt.btab_code.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
		if (isFilished) {
			createTrip(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished) throws Exception {
		if (isFilished) {
			createTrip(jpa, con);
		}
	}

	private void createTrip(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_business_trip_batch tb = (Hrkq_business_trip_batch) jpa;
		CJPALineData<Hrkq_business_trip_batchline> tbls = tb.hrkq_business_trip_batchlines;
		Hrkq_business_trip t = new Hrkq_business_trip();
		Shworg org = new Shworg();
		for (CJPABase j : tbls) {
			Hrkq_business_trip_batchline l = (Hrkq_business_trip_batchline) j;
			org.findByID(l.orgid.getValue());
			t.clear();
			t.assignfield(j, true);
			
			t.remark.setValue("批量出差单："+ tb.btab_code.getValue()+"生成");
			
			t.isoffday.setValue(2);
			t.idpath.setValue(org.idpath.getValue());//
			t.entid.setValue(1);
			t.creator.setValue(tb.creator.getValue());
			t.save(con);
			t.wfcreate(null, con);
		}
	}

}
