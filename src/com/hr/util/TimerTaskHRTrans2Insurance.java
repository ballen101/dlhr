package com.hr.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.hr.attd.entity.Hrkqtransfer2insurancetimer;
import com.hr.insurance.entity.Hr_ins_buyinsurance;
import com.hr.insurance.entity.Hr_ins_insurancetype;
import com.hr.perm.entity.Hr_employee;

public class TimerTaskHRTrans2Insurance extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Logsw.debug("调动后自动生成购保单");
		try {
			int synct = dosettrans2insurance();
			if (synct == 0) {
				System.out.println("自动生成调动后购保单【" + Systemdate.getStrDate() + "】" + "本次没有生成数据！");
			} else
				System.out.println("自动生成调动后购保单【" + Systemdate.getStrDate() + "】:" + synct);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static int dosettrans2insurance() throws Exception {
		String sqlstr = "SELECT * FROM hrkqtransfer2insurancetimer WHERE isbuyins=2 AND  CURDATE()>=dobuyinsdate AND CURDATE()<=LAST_DAY(dobuyinsdate)";
		CJPALineData<Hrkqtransfer2insurancetimer> timers = new CJPALineData<Hrkqtransfer2insurancetimer>(Hrkqtransfer2insurancetimer.class);
		timers.findDataBySQL(sqlstr, true, false);
		int rst = timers.size();
		Hr_employee emp = new Hr_employee();
		Hr_ins_buyinsurance bi = new Hr_ins_buyinsurance();
		Hr_ins_insurancetype it = new Hr_ins_insurancetype();
		// CJPALineData<Hr_ins_buyinsurance> bis=new CJPALineData<Hr_ins_buyinsurance>(Hr_ins_buyinsurance.class);
		CDBConnection con = bi.pool.getCon(bi);
		con.startTrans();
		try {
			for (CJPABase jpa : timers) {
				Hrkqtransfer2insurancetimer transins = (Hrkqtransfer2insurancetimer) jpa;
				emp.clear();
				emp.findBySQL("select * from hr_employee where employee_code='" + transins.employee_code.getValue() + "'");
				if (emp.isEmpty())
					continue;
				it.clear();
				it.findBySQL("SELECT * FROM hr_ins_insurancetype WHERE ins_type=1 AND '" + transins.dobuyinsdate.getValue() + "'>=buydate ORDER BY buydate DESC ");
				if (it.isEmpty()) {
					throw new Exception("购保时间为【" + transins.dobuyinsdate.getValue() + "】时无可用的险种");
				}
				bi.clear();
				bi.insurance_number.setValue(emp.employee_code.getValue());
				bi.orgid.setValue(emp.orgid.getValue()); // 部门ID
				bi.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				bi.orgname.setValue(emp.orgname.getValue()); // 部门名称
				bi.buydday.setValue(transins.dobuyinsdate.getValue());
				bi.insit_id.setValue(it.insit_id.getValue());
				bi.insit_code.setValue(it.insit_code.getValue());
				bi.insname.setValue(it.insname.getValue());
				bi.ins_type.setAsInt(1);
				bi.payment.setValue(it.payment.getValue());
				bi.tselfpay.setValue(it.selfpay.getValue());
				bi.tcompay.setValue(it.compay.getValue());
				bi.er_id.setValue(emp.er_id.getValue());
				bi.employee_code.setValue(emp.employee_code.getValue());
				bi.employee_name.setValue(emp.employee_name.getValue());
				bi.ospid.setValue(emp.ospid.getValue());
				bi.ospcode.setValue(emp.ospcode.getValue());
				bi.sp_name.setValue(emp.sp_name.getValue());
				bi.lv_id.setValue(emp.lv_id.getValue());
				bi.lv_num.setValue(emp.lv_num.getValue());
				bi.hiredday.setValue(emp.hiredday.getValue());
				bi.orgid.setValue(emp.orgid.getValue());
				bi.orgcode.setValue(emp.orgcode.getValue());
				bi.orgname.setValue(emp.orgname.getValue());
				bi.degree.setValue(emp.degree.getValue());
				bi.sex.setValue(emp.sex.getValue());
				bi.telphone.setValue(emp.telphone.getValue());
				bi.nativeplace.setValue(emp.nativeplace.getValue());
				bi.registertype.setValue(emp.registertype.getValue());
				bi.pay_type.setValue(emp.pay_way.getValue());
				bi.id_number.setValue(emp.id_number.getValue());
				bi.sign_org.setValue(emp.sign_org.getValue());
				bi.sign_date.setValue(emp.sign_date.getValue());
				bi.expired_date.setValue(emp.expired_date.getValue());
				bi.birthday.setValue(emp.birthday.getValue());
				bi.idpath.setValue(emp.idpath.getValue());
				bi.entid.setValue("1");
				bi.creator.setValue("DEV");
				bi.createtime.setValue(Systemdate.getStrDate());
				int age = getAgeByBirth(emp.birthday.getValue());
				bi.age.setAsInt(age);
				bi.sptype.setValue(emp.emnature.getValue());
				bi.isnew.setAsInt(1);
				bi.reg_type.setAsInt(1);
				bi.buydday.setValue(transins.dobuyinsdate.getValue());
				bi.ins_type.setValue(it.ins_type.getValue());
				bi.selfratio.setValue(it.selfratio.getValue());
				bi.selfpay.setValue(it.selfpay.getValue());
				bi.comratio.setValue(it.comratio.getValue());
				bi.compay.setValue(it.compay.getValue());
				bi.insurancebase.setValue(it.insurancebase.getValue());
				bi.remark.setValue("调动单【" + transins.sourcecode.getValue() + "】自动生成购保");
				// bis.add(bi);
				bi.save(con);
				Hrkqtransfer2insurancetimer changetimer = new Hrkqtransfer2insurancetimer();
				changetimer.findByID(transins.timer_id.getValue());
				if (!changetimer.isEmpty()) {
					changetimer.isbuyins.setAsInt(1);
					changetimer.buyins_id.setValue(bi.buyins_id.getValue());
					changetimer.buyins_code.setValue(bi.buyins_code.getValue());
					changetimer.save(con);
				}

			}
			/*
			 * if (bis.size() > 0)
			 * bis.saveBatchSimple();// 高速存储
			 * bis.clear();
			 */
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private static int getAgeByBirth(String birthday) {
		int age = 0;
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());// 当前时间
			Calendar birth = Calendar.getInstance();
			Date bd = Systemdate.getDateByStr(birthday);
			birth.setTime(bd);
			if (birth.after(now)) {// 如果传入的时间，在当前时间的后面，返回0岁
				age = 0;
			} else {
				age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
				if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
					age += 1;
				}
			}
			return age;
		} catch (Exception e) {// 兼容性更强,异常后返回数据
			return 0;
		}
	}

}
