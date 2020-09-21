package com.hr.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.util.CReport;
import com.hr.attd.entity.Hrkq_swcdlst;
import com.hr.canteen.entity.Hr_canteen_cardreader;
import com.hr.canteen.entity.Hr_canteen_costrecords;
import com.hr.canteen.entity.Hr_canteen_mealclass;
import com.hr.canteen.entity.Hr_canteen_mealsystem;
import com.hr.inface.entity.Hrms_txjocatlist;
import com.hr.inface.entity.TXkqView_Hrms;
import com.hr.inface.entity.View_Hrms_TXJocatList;
import com.hr.perm.entity.Hr_employee;

public class TimerTaskHRCTCostRecords extends TimerTask {
	private static String autosynkqbgdate = "2017-01-01";
	private static int syncrowno = 2000;
	private static int pageonetime = 20;// 每次触发导入

	@Override
	public void run() {
		Logsw.debug("同步饭堂消费记录");
		try {
			for (int i = 0; i < pageonetime; i++) {
				int synct = importdata4oldcostrecords();//
				if (synct == 0) {
					System.out.println("导入消费记录提前结束【" + Systemdate.getStrDate() + "】" + i + "/" + pageonetime);
					break;
				} else
					System.out.println("导入消费记录【" + Systemdate.getStrDate() + "】" + i + "/" + pageonetime);
			}
			//Date ftime =Systemdate.getDateByStr(autosynkqbgdate);
			Date ttime=Systemdate.getDateByStr(Systemdate.getStrDate());
			Date enddate = Systemdate.dateDayAdd(ttime,1);
			Date ftime = Systemdate.dateDayAdd(ttime, -7);// 减七天
			String sqlstr = "select max(synid) mx from hr_canteen_costrecords";
			Hr_canteen_costrecords ctcr = new Hr_canteen_costrecords();
			List<HashMap<String, String>> crs = ctcr.pool.openSql2List(sqlstr);
			Long mx = ((crs.size() == 0) || (crs.get(0).get("mx") == null)) ? 0 : Long.valueOf(crs.get(0).get("mx"));
			doinsertrecords(mx,ftime, enddate, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int importdata4oldcostrecords() throws Exception {
		String sqlstr = "select max(CAST(xfid AS SIGNED)) mx from hrms_txjocatlist";
		Hr_canteen_costrecords ctcr = new Hr_canteen_costrecords();
		List<HashMap<String, String>> crs = ctcr.pool.openSql2List(sqlstr);
		Date nowdate = Systemdate.getDateByStr(Systemdate.getStrDate());// 去除时分秒
		Date enddate = Systemdate.dateDayAdd(Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(nowdate)), 1);
		Date fromdate = Systemdate.dateMonthAdd(nowdate, -1);// 减一月
		String fromtime=Systemdate.getStrDateyyyy_mm_dd(fromdate);
		long mx = ((crs.size() == 0) || (crs.get(0).get("mx") == null)) ? 0 : Long.valueOf(crs.get(0).get("mx"));		
		CJPALineData<View_Hrms_TXJocatList> txjls = new CJPALineData<View_Hrms_TXJocatList>(View_Hrms_TXJocatList.class);
		 sqlstr = "SELECT TOP " + syncrowno + " * from View_Hrms_TXJocatList where code is not null and id>" + mx// =======================
				+ " and  xfsj>='" + fromtime + "' and xfsj<'"+Systemdate.getStrDateyyyy_mm_dd(enddate)+"' ";
		sqlstr = sqlstr + " order by id";// =========================
		txjls.findDataBySQL(sqlstr, true, false);	 
			 
		CJPALineData<Hrms_txjocatlist> hrctcrs = new CJPALineData<Hrms_txjocatlist>(Hrms_txjocatlist.class);
		int rst = txjls.size();
		for (CJPABase jpa : txjls) {
			View_Hrms_TXJocatList txjl= (View_Hrms_TXJocatList)jpa;
			Hrms_txjocatlist cr = new Hrms_txjocatlist();
			//cr.rfkh.setValue(txjl.rfkh.getValue());// 卡号
			cr.xfsj.setAsDatetime(txjl.xfsj.getAsDatetime());// 消费时间
			//cr.xfjh.setValue(txjl.xfjh.getValue()); // 消费机号
			cr.xfid.setValue(txjl.id.getValue()); // 关联原消费id
			//cr.code.setValue(txjl.code.getValue()); // 工号
			cr.createTime.setAsDatetime(new Date());// 导入时间
			if(txjl.code.isEmpty()){
				cr.code.setValue("0"); // 工号
			}else{
				cr.code.setValue(txjl.code.getValue()); // 工号
			}
			if(txjl.xfjh.isEmpty()){
				cr.xfjh.setValue("0"); // 消费机号
			}else{
				cr.xfjh.setValue(txjl.xfjh.getValue()); // 消费机号
			}
			if(txjl.rfkh.isEmpty()){
				cr.rfkh.setValue("0");// 卡号
			}else{
				cr.rfkh.setValue(txjl.rfkh.getValue());// 卡号
			}
			hrctcrs.add(cr);
		}
		if (hrctcrs.size() > 0)
			hrctcrs.saveBatchSimple();// 高速存储
		hrctcrs.clear();
		//doinsertrecords(Systemdate.getDateByStr(mx), enddate, null);
		return rst;
	}

	public static void importdata4oldct(Date ftime, Date ttime, String empno) throws Exception {
		String sft = Systemdate.getStrDateByFmt(ftime, "yyyy-MM-dd HH:mm:ss");
		String stt = Systemdate.getStrDateByFmt(ttime, "yyyy-MM-dd HH:mm:ss");
		String sqlstr = null;
		sqlstr = "DELETE FROM hr_canteen_costrecords WHERE costtime>='" + sft + "' AND costtime<'" + stt + "'  ";
		if (empno != null)
			sqlstr = sqlstr + " and employee_code='" + empno + "' ";
		Hr_canteen_costrecords records = new Hr_canteen_costrecords();
		records.pool.execsql(sqlstr);// 删除已经存在的数据

		CJPALineData<Hr_canteen_cardreader> cdrs = new CJPALineData<Hr_canteen_cardreader>(Hr_canteen_cardreader.class);// 卡机资料缓存
		cdrs.findDataBySQL("select * from hr_canteen_cardreader where stat=9", false, false);
		CJPALineData<Hr_canteen_mealclass> ctmcs = new CJPALineData<Hr_canteen_mealclass>(Hr_canteen_mealclass.class);// 餐类缓存
		ctmcs.findDataBySQL("SELECT * FROM Hr_canteen_mealclass  WHERE stat=9", false, false);

		while (true) {
			int synct = getTempdataPage(cdrs, ctmcs, ftime, ttime, empno);
			System.out.println("导入消费记录【" + synct + "】条【" + Systemdate.getStrDate() + "】");
			if (synct == 0)
				break;
		}
		System.out.println("同步消费记录，处理完毕" + Systemdate.getStrDate());
	}

	private static int getTempdataPage(CJPALineData<Hr_canteen_cardreader> cdrs, CJPALineData<Hr_canteen_mealclass> ctmcs,
			Date ftime, Date ttime, String empno) throws Exception {
		String sft = Systemdate.getStrDateByFmt(ftime, "yyyy-MM-dd HH:mm:ss");
		String stt = Systemdate.getStrDateByFmt(ttime, "yyyy-MM-dd HH:mm:ss");
		String sqlstr = "select max(synid) mx from hr_canteen_costrecords where costtime>='" + sft + "' AND costtime<'" + stt + "' ";
		if (empno != null)
			sqlstr = sqlstr + " and employee_code='" + empno + "' ";
		Hr_canteen_costrecords records = new Hr_canteen_costrecords();
		List<HashMap<String, String>> sws = records.pool.openSql2List(sqlstr);
		long mx = ((sws.size() == 0) || (sws.get(0).get("mx") == null)) ? 0 : Long.valueOf(sws.get(0).get("mx"));
		CJPALineData<View_Hrms_TXJocatList> txjls = new CJPALineData<View_Hrms_TXJocatList>(View_Hrms_TXJocatList.class);
		sqlstr = "SELECT TOP " + syncrowno + " * from View_Hrms_TXJocatList where id>" + mx// =======================
				+ " and xfsj>='" + sft + "' and xfsj<'" + stt + "'";
		if (empno != null)
			sqlstr = sqlstr + " and rfkh='" + empno + "'";
		sqlstr = sqlstr + " order by id";
		txjls.findDataBySQL(sqlstr, true, false);
		CJPALineData<Hr_canteen_costrecords> hrctcrs = new CJPALineData<Hr_canteen_costrecords>(Hr_canteen_costrecords.class);
		int rst = txjls.size();
		int empnum = 0;
		int classnum = 0;
		Hr_employee emp = new Hr_employee();

		for (CJPABase jpa : txjls) {
			View_Hrms_TXJocatList txjl = (View_Hrms_TXJocatList) jpa;
			emp.clear();
			emp.findBySQL("select * from hr_employee where employee_code='" + txjl.rfkh.getValue().trim() + "'");
			if (emp.isEmpty()) {
				// throw new Exception("找不到卡号为【"+txjl.rfkh.getValue()+"】的人事档案资料");
				empnum++;
				continue;
			}

			Hr_canteen_costrecords cr = new Hr_canteen_costrecords();
			cr.synid.setValue(txjl.id.getValue());
			cr.costtime.setAsDatetime(txjl.xfsj.getAsDatetime());// 消费时间
			cr.cardnumber.setValue(txjl.rfkh.getValue()); // 卡号
			cr.inporttime.setAsDatetime(new Date());// 导入时间
			cr.er_id.setAsInt(emp.er_id.getAsInt()); // 档案id
			cr.employee_code.setValue(emp.employee_code.getValue()); // 工号
			cr.employee_name.setValue(emp.employee_name.getValue()); // 员工姓名
			cr.orgid.setValue(emp.orgid.getValue()); // 机构id
			cr.orgcode.setValue(emp.orgcode.getValue()); // 机构编码
			cr.orgname.setValue(emp.orgname.getValue()); // 机构名称
			cr.ospid.setAsInt(emp.ospid.getAsInt()); // 职位id
			cr.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
			cr.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
			cr.lv_id.setAsInt(emp.lv_id.getAsInt()); // 职级id
			cr.lv_num.setValue(emp.lv_num.getValue()); // 职级
			cr.idpath.setValue(emp.idpath.getValue()); //

			Hr_canteen_cardreader cardreader = getcdr(cdrs, txjl.xfjh.getValue().trim());
			if ((cardreader == null) || cardreader.isEmpty())
				throw new Exception("找不到机号为【" + txjl.xfjh.getValue() + "】的卡机资料");
			cr.ctr_id.setAsInt(cardreader.ctr_id.getAsInt()); // 餐厅id
			cr.ctr_code.setValue(cardreader.ctr_code.getValue()); // 餐厅编码
			cr.ctr_name.setValue(cardreader.ctr_name.getValue()); // 餐厅名
			cr.ctcr_id.setAsInt(cardreader.ctcr_id.getAsInt()); // 卡机id
			cr.ctcr_code.setValue(cardreader.ctcr_code.getValue()); // 卡机编码
			cr.ctcr_name.setValue(cardreader.ctcr_name.getValue()); // 卡机名

			Hr_canteen_mealclass ctmc = getctmc(ctmcs, txjl.xfsj.getValue().trim());
			if ((ctmc == null) || (ctmc.isEmpty())) {
				// throw new Exception("找不到时间为【"+txjl.xfsj.getValue()+"】对应的餐类");
				classnum++;
				// listOfString.add(txjl.xfsj.getValue()+",");
				continue;
			}
			cr.mc_id.setAsInt(ctmc.mc_id.getAsInt());
			cr.mc_name.setValue(ctmc.mc_name.getValue());

			/*
			 * String orgids=emp.idpath.getValue();
			 * orgids=orgids.substring(0,orgids.length() - 1);
			 * int lev=0;
			 * float lv=Float.parseFloat(emp.lv_num.getValue());
			 * if(lv<3){
			 * lev=1;
			 * }else if((lv>=3)&&(lv<3.4)){
			 * lev=2;
			 * }else if((lv>=4)&&(lv<4.4)){
			 * lev=3;
			 * }else if((lv>5)&&(lv<6.4)){
			 * lev=4;
			 * }else if((lv>7)&&(lv<=8)){
			 * lev=5;
			 * }
			 */

			// ctms.clear();
			// ctms.findBySQL("select * from hr_canteen_mealsystem where  stat=9 and orgid in ("+orgids+") and mc_id="+ctmc.mc_id.getValue()+" AND emplev="+lev+" ORDER BY orgid DESC");
			// if (ctms.isEmpty())
			// throw new Exception("找不到机构【"+emp.orgname.getValue()+"】的餐类【"+ctmc.mc_name.getValue()+"】对应的餐制");
			// cr.cost.setAsFloat(ctms.cost.getAsFloat()); // 消费金额
			hrctcrs.add(cr);
		}
		if (hrctcrs.size() > 0) {
			System.out.println("====================导入消费记录条数【" + hrctcrs.size() + "】缺少人事档案数【" + empnum + "】;餐类无法匹配数【" + classnum + "】");
			// System.out.println("+++++++++++++++++++++++++++"+listOfString.toString());
			hrctcrs.saveBatchSimple();// 高速存储
		}
		hrctcrs.clear();
		return rst;
	}

	private static Hr_canteen_cardreader getcdr(CJPALineData<Hr_canteen_cardreader> cdrs, String sync_sn) {
		for (CJPABase jpa : cdrs) {
			Hr_canteen_cardreader cdr = (Hr_canteen_cardreader) jpa;
			if (cdr.sync_sn.getValue().equalsIgnoreCase(sync_sn))
				return cdr;
		}
		return null;
	}

	private static Hr_canteen_mealclass getctmc(CJPALineData<Hr_canteen_mealclass> ctmcs, String xfsj) {
		Date txxftime = Systemdate.getDateByStr(xfsj);
		String xfHHmm = Systemdate.getStrDateByFmt(txxftime, "HH:mm");
		int xfHour = Integer.valueOf(Systemdate.getStrDateByFmt(txxftime, "HH"));
		for (CJPABase jpa : ctmcs) {
			Hr_canteen_mealclass ctmc = (Hr_canteen_mealclass) jpa;
			if ((xfHour >= 20) || (xfHour <= 3)) {
				if (ctmc.mealbegin.getValue().compareTo("20:00") >= 0) {
					return ctmc;
				}
			} else {
				if ((ctmc.mealbegin.getValue().compareTo(xfHHmm) <= 0) && (ctmc.mealend.getValue().compareTo(xfHHmm) >= 0)) {
					return ctmc;
				}
			}
		}
		return null;
	}

	// ///////////////////////////////////////////////////////////////
	public static void importdata4oldct2hrms(Date ftime, Date ttime, String empno) throws Exception {
		String sft = Systemdate.getStrDateByFmt(ftime, "yyyy-MM-dd HH:mm:ss");
		String stt = Systemdate.getStrDateByFmt(ttime, "yyyy-MM-dd HH:mm:ss");
		String sqlstr = null;
		sqlstr = "DELETE FROM hrms_txjocatlist WHERE xfsj>='" + sft + "' AND xfsj<'" + stt + "'  ";
		if ((empno != null)&&(!("".equals(empno))))
			sqlstr = sqlstr + " and code='" + empno + "' ";
		Hrms_txjocatlist records = new Hrms_txjocatlist();
		records.pool.execsql(sqlstr);// 删除已经存在的数据

		while (true) {
			int synct = getTempdataPages(ftime, ttime, empno);
			System.out.println("导入消费记录【" + synct + "】条【" + Systemdate.getStrDate() + "】");
			if (synct == 0)
				break;
		}
		System.out.println("同步消费记录，处理完毕" + Systemdate.getStrDate());
		doinsertrecords(0L,ftime, ttime, empno);
	}

	private static int getTempdataPages(Date ftime, Date ttime, String empno) throws Exception {
		Date bgdt = new Date();
		System.out.println("==================开始加载数据");
		String sft = Systemdate.getStrDateByFmt(ftime, "yyyy-MM-dd HH:mm:ss");
		String stt = Systemdate.getStrDateByFmt(ttime, "yyyy-MM-dd HH:mm:ss");
		String sqlstr = "select max(CAST(xfid AS SIGNED)) mx from hrms_txjocatlist where xfsj>='" + sft + "' AND xfsj<'" + stt + "' ";
		if ((empno != null)&&(!("".equals(empno))))
			sqlstr = sqlstr + " and code='" + empno + "' ";
		Hrms_txjocatlist records = new Hrms_txjocatlist();
		List<HashMap<String, String>> sws = records.pool.openSql2List(sqlstr);
		//String mx=((sws.size() == 0) || (sws.get(0).get("mx") == null)) ? sft : String.valueOf(sws.get(0).get("mx"));
		long mx = ((sws.size() == 0) || (sws.get(0).get("mx") == null)) ? 0 : Long.valueOf(sws.get(0).get("mx"));
		CJPALineData<View_Hrms_TXJocatList> txjls = new CJPALineData<View_Hrms_TXJocatList>(View_Hrms_TXJocatList.class);
		 sqlstr = "SELECT TOP " + syncrowno + " * from View_Hrms_TXJocatList where code is not null and id>" + mx// =======================
				+ " and xfsj>='" + sft + "' and xfsj<'" + stt + "'";
		if ((empno != null)&&(!("".equals(empno))))
			sqlstr = sqlstr + " and code='" + empno + "'";
		sqlstr = sqlstr + " order by id";
		txjls.findDataBySQL(sqlstr, true, false);
		CJPALineData<Hrms_txjocatlist> hrctcrs = new CJPALineData<Hrms_txjocatlist>(Hrms_txjocatlist.class);
		int rst = txjls.size();
		System.out.println("==================开始生成实体列表" + ((new Date()).getTime() - bgdt.getTime()));
		bgdt = new Date();
		for (CJPABase jpa : txjls) {
			View_Hrms_TXJocatList txjl = (View_Hrms_TXJocatList) jpa;
			Hrms_txjocatlist cr = new Hrms_txjocatlist();
			//cr.rfkh.setValue(txjl.rfkh.getValue());// 卡号
			cr.xfsj.setAsDatetime(txjl.xfsj.getAsDatetime());// 消费时间
			//cr.xfjh.setValue(txjl.xfjh.getValue()); // 消费机号
			cr.xfid.setValue(txjl.id.getValue()); // 关联原消费id
			//cr.code.setValue(txjl.code.getValue()); // 工号
			cr.createTime.setAsDatetime(new Date());// 导入时间
			if(txjl.code.isEmpty()){
				cr.code.setValue("0"); // 工号
			}else{
				cr.code.setValue(txjl.code.getValue()); // 工号
			}
			if(txjl.xfjh.isEmpty()){
				cr.xfjh.setValue("0"); // 消费机号
			}else{
				cr.xfjh.setValue(txjl.xfjh.getValue()); // 消费机号
			}
			if(txjl.rfkh.isEmpty()){
				cr.rfkh.setValue("0");// 卡号
			}else{
				cr.rfkh.setValue(txjl.rfkh.getValue());// 卡号
			}
			hrctcrs.add(cr);
		}
		System.out.println("==================完成生成实体列表" + ((new Date()).getTime() - bgdt.getTime()));
		bgdt = new Date();
		if (hrctcrs.size() > 0) {
			System.out.println("====================开始保存记录条数【" + hrctcrs.size() + "】" + ((new Date()).getTime() - bgdt.getTime()));
			bgdt = new Date();
			// System.out.println("+++++++++++++++++++++++++++"+listOfString.toString());
			hrctcrs.saveBatchSimple();// 高速存储
			System.out.println("==================完成保存消费记录" + ((new Date()).getTime() - bgdt.getTime()));
			bgdt = new Date();
		}
		hrctcrs.clear();
		return rst;
	}

	/*
	 * public static void dotransrecords(Date ftime, Date ttime, String empno) throws Exception {
	 * String sft = Systemdate.getStrDateByFmt(ftime, "yyyy-MM-dd HH:mm:ss");
	 * String stt = Systemdate.getStrDateByFmt(ttime, "yyyy-MM-dd HH:mm:ss");
	 * String sqlstr = null;
	 * sqlstr = "DELETE FROM hr_canteen_costrecords WHERE costtime>='" + sft + "' AND costtime<'" + stt + "'  ";
	 * if (empno != null)
	 * sqlstr = sqlstr + " and employee_code='" + empno + "' ";
	 * Hr_canteen_costrecords records = new Hr_canteen_costrecords();
	 * records.pool.execsql(sqlstr);// 删除已经存在的数据
	 * CJPALineData<Hr_canteen_cardreader> cdrs = new CJPALineData<Hr_canteen_cardreader>(Hr_canteen_cardreader.class);// 卡机资料缓存
	 * cdrs.findDataBySQL("select * from hr_canteen_cardreader where stat=9", false, false);
	 * CJPALineData<Hr_canteen_mealclass> ctmcs = new CJPALineData<Hr_canteen_mealclass>(Hr_canteen_mealclass.class);// 餐类缓存
	 * ctmcs.findDataBySQL("SELECT * FROM Hr_canteen_mealclass  WHERE stat=9", false, false);
	 * while (true) {
	 * int synct = dosetnewrecords(cdrs, ctmcs, ftime, ttime, empno);
	 * System.out.println("导入消费记录【" + synct + "】条【" + Systemdate.getStrDate() + "】");
	 * if (synct == 0)
	 * break;
	 * }
	 * System.out.println("消费记录转换，处理完毕" + Systemdate.getStrDate());
	 * }
	 * private static int dosetnewrecords(CJPALineData<Hr_canteen_cardreader> cdrs, CJPALineData<Hr_canteen_mealclass> ctmcs,
	 * Date ftime, Date ttime, String empno) throws Exception {
	 * String sft = Systemdate.getStrDateByFmt(ftime, "yyyy-MM-dd HH:mm:ss");
	 * String stt = Systemdate.getStrDateByFmt(ttime, "yyyy-MM-dd HH:mm:ss");
	 * String sqlstr = "select max(synid) mx from hr_canteen_costrecords where costtime>='" + sft + "' AND costtime<'" + stt + "' ";
	 * if (empno != null)
	 * sqlstr = sqlstr + " and employee_code='" + empno + "' ";
	 * Hr_canteen_costrecords records = new Hr_canteen_costrecords();
	 * List<HashMap<String, String>> sws = records.pool.openSql2List(sqlstr);
	 * long mx = ((sws.size() == 0) || (sws.get(0).get("mx") == null)) ? 0 : Long.valueOf(sws.get(0).get("mx"));
	 * CJPALineData<Hrms_txjocatlist> txjls = new CJPALineData<Hrms_txjocatlist>(Hrms_txjocatlist.class);
	 * sqlstr = "SELECT  * from hrms_txjocatlist where xfid>" + mx// =======================
	 * + " and xfsj>='" + sft + "' and xfsj<'" + stt + "'";
	 * if (empno != null)
	 * sqlstr = sqlstr + " and rfkh='" + empno + "'";
	 * sqlstr = sqlstr + " order by xfid limit "+ syncrowno;
	 * txjls.findDataBySQL(sqlstr, true, false);
	 * CJPALineData<Hr_canteen_costrecords> hrctcrs = new CJPALineData<Hr_canteen_costrecords>(Hr_canteen_costrecords.class);
	 * int rst = txjls.size();
	 * int empnum = 0;
	 * int classnum = 0;
	 * Hr_employee emp = new Hr_employee();
	 * for (CJPABase jpa : txjls) {
	 * Hrms_txjocatlist txjl = (Hrms_txjocatlist) jpa;
	 * emp.clear();
	 * emp.findBySQL("select * from hr_employee where employee_code='" + txjl.rfkh.getValue().trim() + "'");
	 * if (emp.isEmpty()) {
	 * // throw new Exception("找不到卡号为【"+txjl.rfkh.getValue()+"】的人事档案资料");
	 * empnum++;
	 * continue;
	 * }
	 * Hr_canteen_costrecords cr = new Hr_canteen_costrecords();
	 * cr.synid.setValue(txjl.xfid.getValue());
	 * cr.costtime.setAsDatetime(txjl.xfsj.getAsDatetime());// 消费时间
	 * cr.cardnumber.setValue(txjl.rfkh.getValue()); // 卡号
	 * cr.inporttime.setAsDatetime(new Date());// 导入时间
	 * cr.er_id.setAsInt(emp.er_id.getAsInt()); // 档案id
	 * cr.employee_code.setValue(emp.employee_code.getValue()); // 工号
	 * cr.employee_name.setValue(emp.employee_name.getValue()); // 员工姓名
	 * cr.orgid.setValue(emp.orgid.getValue()); // 机构id
	 * cr.orgcode.setValue(emp.orgcode.getValue()); // 机构编码
	 * cr.orgname.setValue(emp.orgname.getValue()); // 机构名称
	 * cr.ospid.setAsInt(emp.ospid.getAsInt()); // 职位id
	 * cr.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
	 * cr.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
	 * cr.lv_id.setAsInt(emp.lv_id.getAsInt()); // 职级id
	 * cr.lv_num.setValue(emp.lv_num.getValue()); // 职级
	 * cr.idpath.setValue(emp.idpath.getValue()); //
	 * Hr_canteen_cardreader cardreader = getcdr(cdrs, txjl.xfjh.getValue().trim());
	 * if ((cardreader == null) || cardreader.isEmpty())
	 * throw new Exception("找不到机号为【" + txjl.xfjh.getValue() + "】的卡机资料");
	 * cr.ctr_id.setAsInt(cardreader.ctr_id.getAsInt()); // 餐厅id
	 * cr.ctr_code.setValue(cardreader.ctr_code.getValue()); // 餐厅编码
	 * cr.ctr_name.setValue(cardreader.ctr_name.getValue()); // 餐厅名
	 * cr.ctcr_id.setAsInt(cardreader.ctcr_id.getAsInt()); // 卡机id
	 * cr.ctcr_code.setValue(cardreader.ctcr_code.getValue()); // 卡机编码
	 * cr.ctcr_name.setValue(cardreader.ctcr_name.getValue()); // 卡机名
	 * Hr_canteen_mealclass ctmc = getctmc(ctmcs, txjl.xfsj.getValue().trim());
	 * if ((ctmc==null)||(ctmc.isEmpty())) {
	 * // throw new Exception("找不到时间为【"+txjl.xfsj.getValue()+"】对应的餐类");
	 * classnum++;
	 * continue;
	 * }
	 * cr.mc_id.setAsInt(ctmc.mc_id.getAsInt());
	 * cr.mc_name.setValue(ctmc.mc_name.getValue());
	 * hrctcrs.add(cr);
	 * }
	 * if (hrctcrs.size() > 0) {
	 * System.out.println("====================导入消费记录条数【" + hrctcrs.size() + "】缺少人事档案数【" + empnum + "】;餐类无法匹配数【" + classnum + "】");
	 * hrctcrs.saveBatchSimple();// 高速存储
	 * }
	 * hrctcrs.clear();
	 * return rst;
	 * }
	 */

	public static void doinsertrecords(Long mx,Date ftime, Date ttime, String empno) throws Exception {
		String sft = Systemdate.getStrDateByFmt(ftime, "yyyy-MM-dd HH:mm:ss");
		String stt = Systemdate.getStrDateByFmt(ttime, "yyyy-MM-dd HH:mm:ss");
		String sqlstr = null;
		sqlstr = "DELETE FROM hr_canteen_costrecords WHERE costtime>='" + sft + "' AND costtime<'" + stt + "'  ";
		if ((empno != null)&&(!("".equals(empno))))
			sqlstr = sqlstr + " and employee_code='" + empno + "' ";
		sqlstr=sqlstr+" and synid>"+mx;
		Hr_canteen_costrecords records = new Hr_canteen_costrecords();
		records.pool.execsql(sqlstr);// 删除已经存在的数据
	
		String sqlstr2 = "INSERT INTO hr_canteen_costrecords(`cardnumber`,`er_id`,`employee_code`,`employee_name`,`orgid`,`orgcode`,`orgname`," +
				"`ospid`,`ospcode`,`sp_name`,`lv_id`,`lv_num`,`ctr_id`,`ctr_code`,`ctr_name`,`ctcr_id`,`ctcr_code`,`ctcr_name`,`costtime`," +
				"`idpath`,`synid`,`inporttime`,`entid`,`creator`,`createtime`) " +
				" SELECT xfl.rfkh AS  cardnumber,emp.er_id,emp.employee_code,emp.employee_name,emp.orgid,emp.orgcode,emp.orgname," +
				"emp.ospid,emp.ospcode,emp.sp_name,emp.lv_id,emp.lv_num,cr.ctr_id,cr.ctr_code,cr.ctr_name,cr.ctcr_id,cr.ctcr_code,cr.ctcr_name," +
				"xfl.xfsj AS costtime,emp.idpath,xfl.xfid AS synid ,NOW() AS inporttime ,1 AS entid,'DEV' AS creator,NOW() AS createtime" +
				" FROM hrms_txjocatlist xfl,hr_employee emp ,hr_canteen_cardreader cr " +
				" WHERE xfl.code=emp.employee_code AND xfl.xfjh=cr.sync_sn AND ";
		if (empno != null)
			sqlstr2=sqlstr2+"xfl.code='"+empno+"' and";
		
		sqlstr2=sqlstr2+"  xfsj>='" + sft + "' AND xfsj<'" + stt + "' and xfl.xfid>"+mx+" ORDER BY xfl.xfid ";

		records.pool.execsql(sqlstr2);// 消费记录数据直接插入记录表
		
		String sqlstr3="UPDATE hr_canteen_costrecords cr,`hr_canteen_mealclass` mc SET cr.mc_id=mc.mc_id,cr.mc_name=mc.mc_name,cr.classtype=mc.classtype "+
		"WHERE cr.costtime>='" + sft + "' AND cr.costtime<'" + stt + "' ";
		if ((empno != null)&&(!("".equals(empno))))
			sqlstr3 = sqlstr3 + " and cr.employee_code='" + empno + "' ";
		sqlstr3=sqlstr3+" AND mc.mc_id=(SELECT mc_id FROM `hr_canteen_mealclass` mc WHERE stat=9 "+
		"AND usable=1 AND INSTR(cr.idpath,mc.idpath) >0 AND ((CASE WHEN HOUR(cr.costtime)<3 OR HOUR(cr.costtime)>=20 THEN classtype=4 END) OR "+
		"(HOUR(cr.costtime)>=HOUR(mealbegin) AND (CASE WHEN HOUR(cr.costtime)=HOUR(mealend) THEN MINUTE(cr.costtime)<=MINUTE(mealend) "+
		"ELSE HOUR(cr.costtime)<HOUR(mealend) END ))) ORDER BY idpath DESC LIMIT 0,1)";
		records.pool.execsql(sqlstr3);// 更新记录表，更新餐类信息
		
		String sqlstr4="DELETE FROM `hr_canteen_costrecords` WHERE costtime>='" + sft + "' AND costtime<'" + stt + "' AND mc_id IS NULL ";
		if ((empno != null)&&(!("".equals(empno))))
			sqlstr4 = sqlstr4 + " and employee_code='" + empno + "' ";
		records.pool.execsql(sqlstr4);// 删除无法匹配餐类的记录，在餐类时间外消费的、对应机构没有维护餐类的
		System.out.println("消费记录转换，处理完毕" + Systemdate.getStrDate());
	}

}
