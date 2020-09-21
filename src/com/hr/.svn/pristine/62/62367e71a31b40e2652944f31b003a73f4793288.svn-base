package com.hr.attd.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.attd.entity.Hrkq_resign;
import com.hr.attd.entity.Hrkq_resignbatch;
import com.hr.attd.entity.Hrkq_resignbatchline;
import com.hr.attd.entity.Hrkq_resignline;

public class CtrHrkq_resignbatch extends JPAController {
	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hrkq_resignbatch rb = (Hrkq_resignbatch) jpa;
		CJPALineData<Hrkq_resignbatchline> rbls = rb.hrkq_resignbatchlines;
		for (CJPABase ljpa : rbls) {
			Hrkq_resignbatchline rbl = (Hrkq_resignbatchline) ljpa;
			String dtstr = Systemdate.getStrDateyyyy_mm_dd(rbl.kqdate.getAsDate()) + " " + rbl.rgtime.getValue();
			Systemdate.getDateByStr(dtstr);// 保存前检查日期对不对
		}
	}

	@Override
	public void BeforeWFStartSave(CJPA jpa, Shwwf wf, CDBConnection con, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		wf.subject.setValue(((Hrkq_resignbatch) jpa).resbcode.getValue());
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			createResign(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			createResign(jpa, con);
		}
	}

	private void createResign(CJPA jpa, CDBConnection con) throws Exception {
		Hrkq_resignbatch rb = (Hrkq_resignbatch) jpa;
		CJPALineData<Hrkq_resignbatchline> rbls = rb.hrkq_resignbatchlines;
		Hrkq_resign r = new Hrkq_resign();
		Hrkq_resignline rl = new Hrkq_resignline();
		for (CJPABase j : rbls) {
			Hrkq_resignbatchline rbl = (Hrkq_resignbatchline) j;
			r.clear();
			r.hrkq_resignlines.clear();
			r.er_id.setValue(rbl.er_id.getValue()); // 申请人档案ID
			r.employee_code.setValue(rbl.employee_code.getValue()); // 申请人工号
			r.employee_name.setValue(rbl.employee_name.getValue()); // 申请人姓名
			r.resdate.setValue(Systemdate.getStrDateByFmt(rbl.kqdate.getAsDatetime(), "yyyy-MM-01")); // 考勤月度
			r.orgid.setValue(rbl.orgid.getValue()); // 部门ID
			r.orgcode.setValue(rbl.orgcode.getValue()); // 部门编码
			r.orgname.setValue(rbl.orgname.getValue()); // 部门名称
			r.ospid.setValue(rbl.ospid.getValue()); // 职位ID
			r.ospcode.setValue(rbl.ospcode.getValue()); // 职位编码
			r.sp_name.setValue(rbl.sp_name.getValue()); // 职位名称
			r.lv_num.setValue(rbl.lv_num.getValue()); // 职级
			r.res_type.setValue("1"); // 补签类别 1 因公 2因私
			r.orghrlev.setValue("0"); // 机构人事层级
			r.emplev.setValue("0"); // 人事层级
			r.res_times.setValue("0"); // 补签次数
			r.remark.setValue(rb.remark.getValue()); // 备注
			r.stat.setValue("1"); // 表单状态
			r.idpath.setValue(rb.idpath.getValue()); // idpath
			r.entid.setValue("1"); // entid
			r.creator.setValue("system"); // 创建人
			r.createtime.setAsDatetime(new Date()); // 创建时间

			rl.kqdate.setAsDatetime(rbl.kqdate.getAsDatetime()); // 考勤日期
			rl.ltype.setValue("1"); // 1 正班 2 加班 3值班
			rl.bcno.setValue(rbl.bcno.getValue()); // 班次号
			rl.rgtime.setValue(rbl.rgtime.getValue()); // 打卡时间
			rl.isreg.setValue("1"); // 是否签卡
			rl.resreson.setValue(rb.resreson.getValue()); // 补签原因
			rl.sclid.setValue("0"); // 班次行ID
			rl.scid.setValue("0"); // 班次ID
			rl.scdname.setValue(rbl.scdname.getValue()); // 班次名
			rl.sxb.setValue(rbl.sxb.getValue()); // 1 上班 2 下班
			r.hrkq_resignlines.add(rl);
			r.save(con);
			r.wfcreate(null, con);
		}
	}
}
