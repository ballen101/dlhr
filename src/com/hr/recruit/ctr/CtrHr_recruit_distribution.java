package com.hr.recruit.ctr;

import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.recruit.entity.Hr_recruit_distribution;
import com.hr.recruit.entity.Hr_recruit_form;
import com.hr.util.HRUtil;

public class CtrHr_recruit_distribution extends JPAController {

    /**
     * 保存前
     * 
     * @param jpa里面有值
     *            ，还没检测数据完整性，没生成ID CODE 设置默认值
     * @param con
     * @param selfLink
     * @throws Exception
     */
    public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
	Hr_recruit_distribution rd = (Hr_recruit_distribution) jpa;
	Hr_recruit_form rf = new Hr_recruit_form();
	String sqlstr = "select * from hr_recruit_form where er_id=" + rd.er_id.getValue();
	rf.findBySQL(sqlstr);
	if (rf.isEmpty())
	    throw new Exception("人事ID为【" + rf.er_id.getValue() + "】的招募登记表单不存在");
	if ((rf.quachk.getAsIntDefault(0) != 1) || (rf.chkok.getAsIntDefault(0) != 1))
	    throw new Exception("验证合格、资格比对合格才允许分配");
    }

    @Override
    public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished) throws Exception {
	// TODO Auto-generated method stub
	checkdate(jpa);
	if (isFilished) {
	    doUpdateEmployee(jpa, con);
	}
    }

    private void checkdate(CJPA jpa) throws Exception {
	if (!HRUtil.hasRoles("2")) {
	    Hr_recruit_distribution rd = (Hr_recruit_distribution) jpa;
	    if (Systemdate.getDateYYYYMMDD(rd.hiredday.getAsDatetime()).getTime() > (Systemdate.getDateYYYYMMDD(new Date()).getTime()))
		throw new Exception("入职日期之后才能分配");
	}
    }

    @Override
    public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished) throws Exception {
	// TODO Auto-generated method stub
	if (isFilished) {
	    doUpdateEmployee(jpa, con);
	}
    }

    // 修改人事表单机构属性
    public void doUpdateEmployee(CJPA jpa, CDBConnection con) throws Exception {
	Hr_recruit_distribution rd = (Hr_recruit_distribution) jpa;
	Hr_recruit_form rf = new Hr_recruit_form();
	String sqlstr = "SELECT t.* FROM hr_recruit_form t WHERE t.er_id = " + rd.er_id.getValue();
	rf.findBySQL(sqlstr);
	sqlstr = "SELECT t.* FROM hr_employee t WHERE t.er_id = " + rd.er_id.getValue();
	Hr_employee he = new Hr_employee();

	Hr_orgposition op = new Hr_orgposition();
	op.findByID(rd.nospid.getValue());
	if (op.isEmpty())
	    throw new Exception("ID为【" + rd.nospid.getValue() + "】的机构职位不存在");

	he.findBySQL4Update(con, sqlstr, false);// 锁定更新记录
	he.orgid.setValue(rd.norgid.getValue());
	Shworg org = new Shworg();
	org.findByID(rd.norgid.getValue());
	if (org.isEmpty())
		throw new Exception("id为【" + rd.norgid.getValue() + "】的机构不存在");
	he.idpath.setValue(org.idpath.getValue());
	he.orgcode.setValue(rd.norgcode.getValue());
	he.orgname.setValue(rd.norgname.getValue());
	he.hwc_namezl.setValue(rd.nhwc_namezl.getValue());
	he.lv_id.setValue(rd.nlv_id.getValue());
	he.lv_num.setValue(rd.nlv_num.getValue());
	he.hg_id.setValue(rd.nhg_id.getValue());
	he.hg_code.setValue(rd.nhg_code.getValue());
	he.hg_name.setValue(rd.nhg_name.getValue());
	he.ospid.setValue(rd.nospid.getValue());
	he.ospcode.setValue(rd.nospcode.getValue());
	he.sp_name.setValue(rd.nsp_name.getValue());
	he.hwc_namezq.setValue(op.hwc_namezq.getValue());
	he.hwc_namezz.setValue(op.hwc_namezz.getValue());
	String emnature = (op.isoffjob.getAsIntDefault(0) == 1) ? "脱产" : "非脱产";
	he.emnature.setValue(emnature);
	// if (rf.probation.getAsIntDefault(1) == 0)
	// he.empstatid.setValue(2);
	// else
	he.empstatid.setValue(4);
	he.save(con, false);
    }

}
