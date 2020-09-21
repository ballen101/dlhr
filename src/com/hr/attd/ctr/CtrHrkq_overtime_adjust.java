package com.hr.attd.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_onduty;
import com.hr.attd.entity.Hrkq_overtime;
import com.hr.attd.entity.Hrkq_overtime_adjust;
import com.hr.attd.entity.Hrkq_overtime_adjust_line;
import com.hr.attd.entity.Hrkq_overtime_list;

public class CtrHrkq_overtime_adjust extends JPAController {

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_overtime_adjust) jpa).otad_code.getValue());
	}

	
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doAdjustOverTimeList(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doAdjustOverTimeList(jpa, con);
		}
	}

	private void doAdjustOverTimeList(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_overtime_adjust ota = (Hrkq_overtime_adjust) jpa;
		Hrkq_overtime ot = new Hrkq_overtime();
		ot.findByID(ota.ot_id.getValue());
		if (ot.isEmpty()) {
			throw new Exception("找不到id为：" + ot.ot_id.getValue() + "的加班申请单！");
		} else {
			if ((!ot.isadjust.isEmpty()) && (ot.isadjust.getAsInt() == 1)) {
				// throw new Exception("id为："+ot.ot_id.getValue()+"的加班申请单已经调整过一次了，无法再次调整！");//暂时先取消
			}
		}
		for (CJPABase jpa1 : ota.hrkq_overtime_adjust_lines) {
			Hrkq_overtime_adjust_line adl = (Hrkq_overtime_adjust_line) jpa1;
			Hrkq_overtime_list otlst = new Hrkq_overtime_list();
			if (adl.alterbegin_date.isEmpty() || adl.alterend_date.isEmpty())
				continue;
			String sqlstr = "SELECT * FROM `hrkq_overtime_list` WHERE oth_id=" + adl.oth_id.getValue() + "  AND ot_id=" + ota.ot_id.getValue() + "  AND er_id="
					+ ota.er_id.getValue();
			otlst.findBySQL(sqlstr);
			if (otlst.isEmpty()) {
				throw new Exception("员工【" + ota.employee_name.getValue() + "】没有找到id为【" + otlst.otlistid.getValue() + "】的加班时间明细!");
			}
			checkJXRecord(adl, ota, ot);
			otlst.bgtime.setValue(adl.alterbegin_date.getValue()); // 上班时间
			otlst.edtime.setValue(adl.alterend_date.getValue()); // 下班时间
			otlst.save(con);
		}
		ot.isadjust.setAsInt(1);
		ot.save(con);
	}

	private void checkJXRecord(Hrkq_overtime_adjust_line adl, Hrkq_overtime_adjust ota, Hrkq_overtime ot) throws NumberFormatException, Exception {
		Date bgtime = adl.alterbegin_date.getAsDatetime();
		Date edtime = adl.alterend_date.getAsDatetime();
		HrkqUtil.checkAllOverlapDatetime(2, ot.ot_id.getValue(), ota.er_id.getValue(), ota.employee_name.getValue(), bgtime, edtime);

		// String bgtstr1 = Systemdate.getStrDate(bgtime1);
		// String edtstr1 = Systemdate.getStrDate(edtime1);
		// String sqlstr = "SELECT * FROM hrkq_overtime_list "
		// + " WHERE er_id=" + ota.er_id.getValue() + " and oth_id<>" + adl.oth_id.getValue()
		// + " AND (((bgtime<='" + bgtstr1 + "') AND (edtime>='" + bgtstr1 + "'))||((bgtime>='" + bgtstr1 + "')&&(bgtime<='" + edtstr1 + "')))";
		// Hrkq_overtime_list otlst = new Hrkq_overtime_list();
		// otlst.findBySQL(sqlstr);
		// if (!otlst.isEmpty())
		// throw new Exception("员工【" + ota.employee_name.getValue() + "】调整的加班时间与已经完成的加班申请【" + otlst.otlistid.getValue() + "】时间交叉错误!");
	}

}
