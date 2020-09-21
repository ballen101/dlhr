package com.hr.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.TimerTask;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_standposition;
import com.hr.inface.entity.View_TxZlEmployee;
import com.hr.inface.entity.View_TxZlEmployee_zp;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_entry;

/**
 * 
 * 招募对接
 * 
 * @author shangwen
 * 
 */
public class TimerTaskHRZM extends TimerTask {
	private static String[] fdnames = { "sp_id", "sp_code", "sp_name",
			"sp_exp", "lv_id", "lv_num", "hg_id", "hg_code", "hg_name", "hwc_idzl", "hw_codezl",
			"hwc_namezl", "hwc_idzq", "hw_codezq", "hwc_namezq", "hwc_idzz", "hw_codezz", "hwc_namezz", "gtitle",
			"usable", "isadvtech", "isoffjob", "issensitive", "iskey", "ishighrisk", "isneedadtoutwork",
			"isdreamposition", "maxage", "minage", "mindegree" };

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// importDayData(new Date());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @throws Exception
	 */
	/***
	 * 一次性同步指定时间段内入职的人事资料 或者工号，
	 * 
	 * @param bgdate
	 * @param eddate
	 * @param empcode
	 * @return
	 * @throws Exception
	 */
	public static String importDayData(Date bgdate, Date eddate, String empcode) throws Exception {
		String sqlstr = "select * from HRMS.dbo.view_TxZlEmployee_zp where 1=1 ";
		if ((empcode == null) || empcode.isEmpty()) {
			if ((bgdate == null) || (eddate == null))
				throw new Exception("日期和工号不能同时为空");
			sqlstr = sqlstr + "and pydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "'"
					+ " and pydate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "'";
		} else
			sqlstr = sqlstr + " and code=" + empcode;
		CJPALineData<View_TxZlEmployee_zp> txes = new CJPALineData<View_TxZlEmployee_zp>(View_TxZlEmployee_zp.class);
		Shworg org = new Shworg();
		Hr_standposition sp = new Hr_standposition();
		Hr_entry enty = new Hr_entry();
		Hr_employee emp = new Hr_employee();
		Hr_orgposition osp = new Hr_orgposition();
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		txes.findDataBySQL(sqlstr);
		CDBConnection con = emp.pool.getCon("importDayData");
		con.startTrans();
		int drct = 0, lgct = 0;
		try {
			for (CJPABase jpa : txes) {
				View_TxZlEmployee_zp txe = (View_TxZlEmployee_zp) jpa;
				String employee_code = txe.code.getValue().trim();
				sqlstr = "select * from hr_employee where employee_code='" + employee_code + "'";
				emp.findBySQL(sqlstr);
				boolean isupdate_emp = false;
				if (!emp.isEmpty()) {// 已经存在人事资料，略过
					lgct++;
					// continue; //18-05-01
					isupdate_emp = true;
				} else
					drct++;
				// emp.clear();
				// 根据机构编码扩展属性获取机构
				sqlstr = "SELECT * FROM shworg WHERE attribute1='" + txe.dept.getValue().trim() + "'";
				org.findBySQL(sqlstr, false);
				if (org.isEmpty())
					throwerr("招募系统工号【" + employee_code + "】同步错误，招募系统机构编码【" + txe.dept.getValue().trim() + "】在HRMS系统无对应机构");
				String zhiwu = (txe.zhiwu.isEmpty()) ? "" : txe.zhiwu.getValue().trim();
				String yuzhiwu = (txe.yuZhiWu.isEmpty()) ? "" : txe.yuZhiWu.getValue().trim();
				if ((zhiwu.isEmpty() || (yuzhiwu.isEmpty()))) {
					throwerr("招募系统工号【" + employee_code + "】同步错误，招募系统职位编码或职位名称为空");
				}

				sqlstr = "SELECT count(*) ct FROM hr_standposition WHERE attribute1 LIKE '%" + zhiwu + "#" + yuzhiwu + "%'";
				if (Integer.valueOf(sp.pool.openSql2List(sqlstr).get(0).get("ct").toString()) > 1) {
					throwerr("招募系统工号【" + employee_code + "】同步错误，招募系统职位编码【" + zhiwu + "】在HRMS系统发现多个标准职位，不满足同步要求");
				}
				sqlstr = "SELECT * FROM hr_standposition WHERE attribute1 LIKE '%" + zhiwu + "#" + yuzhiwu + "%'";
				sp.clear();
				sp.findBySQL(sqlstr, false);
				if (sp.isEmpty())
					throwerr("招募系统工号【" + employee_code + "】同步错误，招募系统职位编码【" + zhiwu + "】在HRMS系统无对应的标准职位，不满足同步要求");
				sqlstr = "SELECT * FROM hr_orgposition WHERE orgid=" + org.orgid.getValue() + " AND sp_id=" + sp.sp_id.getValue();
				osp.findBySQL(sqlstr);
				if (osp.isEmpty()) {// 创建机构职位
					createosp(osp, sp, org, con);
				}
				if (osp.isEmpty()) {// 创建机构职位
					throw new Exception("创建了还是为空?为啥");
				}
				saveentry(txe, enty, emp, osp, dictemp, con, isupdate_emp);
			}
			// if (true)
			// throw new Exception("测试");
			con.submit();
			return "共计【" + txes.size() + "】人,更新【" + lgct + "】人，新同步【" + drct + "】人";
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 同步某工号的相片
	 * 
	 * @param employee_code
	 * @return
	 * @throws Exception
	 */
	public static boolean syncempphoto(String employee_code) throws Exception {
		String sqlstr = "select  * from  [HRMSTest].[dbo].[view_TxZlEmployee] where code='" + employee_code + "'";
		View_TxZlEmployee txe = new View_TxZlEmployee();
		txe.findBySQL(sqlstr);
		if (txe.isEmpty())
			throw new Exception(" 工号【" + employee_code + "】在接口【view_TxZlEmployee】中不存在");
		return savepic(txe);
	}

	/**
	 * 保存所有图片
	 * 
	 * @param txes
	 * @throws Exception
	 */
	private static void savepics(CJPALineData<View_TxZlEmployee> txes) throws Exception {
		for (CJPABase jpa : txes) {
			try {
				View_TxZlEmployee txe = (View_TxZlEmployee) jpa;
				savepic(txe);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 导入一个人的相片
	 * 
	 * @param txemp
	 * @return
	 * @throws Exception
	 */
	private static boolean savepic(View_TxZlEmployee txemp) throws Exception {
		Hr_employee emp = new Hr_employee();
		String employee_code = txemp.code.getValue().trim();
		emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
		if (emp.isEmpty())
			throw new Exception("工号【" + employee_code + "】不存在人事资料");
		if (emp.avatar_id1.isEmpty()) {

			String photo = txemp.photo.getValue();
			if (photo != null) {
				String fname = employee_code + ".jpg";
				Shw_physic_file pf = UpLoadFileEx.witrefileFromFieldValue(photo, fname);
				if (pf != null) {
					emp.avatar_id1.setValue(pf.pfid.getValue());
					emp.save(false);
					return true;
				} else {
					Logsw.debug("保存图片后物理文件为空" + employee_code);
					return false;
				}
			} else {
				Logsw.debug("接口图片为空" + employee_code);
				return false;
			}
		} else {
			Logsw.debug("已经有图片了" + employee_code);
			return false;
		}
	}

	private static String gettrimv(String v) {
		return (v == null) ? null : v.trim();
	}

	private static String gettrimvdef(String v, String df) {
		return ((v == null) || (v.trim().isEmpty())) ? df : v.trim();
	}

	private static int getinterfaceSex(String sex) {
		try {
			String s = (sex == null) ? "2" : sex.trim();
			if (s == null)
				return 0;
			else if (Integer.valueOf(s) == 0)
				return 2;
			else if (Integer.valueOf(s) == 1)
				return 1;
			else
				return 0;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 学历对照map 前面是源 后面是 HRMS系统的
	private static int[][] Xueli_Degree_Map = {
			{ 1, 9 }, // 小学
			{ 2, 8 }, // 初中
			{ 3, 5 }, // 高中
			{ 4, 6 }, // 中专
			{ 5, 4 }, // 大专
			{ 6, 3 }, // 本科
			{ 7, 2 }, // 硕士
			{ 8, 1 }, // 博士
			{ 9, 7 }, // 职高
			{ 10, 2 } // 研究生
	};

	private static int getinterfaceDegree(String xl) {
		try {
			String s = gettrimv(xl);
			if (s == null)
				return 10;
			int x = Integer.valueOf(s);
			for (int i = 0; i < Xueli_Degree_Map.length; i++) {
				if (Xueli_Degree_Map[i][0] == x)
					return Xueli_Degree_Map[i][1];
			}
			return 10;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 10;
		}
	}

	private static void saveentry(View_TxZlEmployee_zp txe, Hr_entry enty, Hr_employee emp,
			Hr_orgposition osp, DictionaryTemp dictemp, CDBConnection con, boolean isupdate_emp) throws Exception {
		emp.orgid.setValue(osp.orgid.getValue());
		emp.orgcode.setValue(osp.orgcode.getValue());
		emp.orgname.setValue(osp.orgname.getValue());
		emp.idpath.setValue(osp.idpath.getValue());
		// emp.property2.setValue(txe.card_sn.getValue());
		emp.employee_code.setValue(gettrimv(txe.code.getValue()));
		emp.employee_name.setValue(gettrimv(txe.name.getValue()));
		emp.sex.setValue(gettrimv(txe.sex.getValue()));// getinterfaceSex(txe.sex.getValue())
		// System.out.println("sex:" + txe.sex.getValue() + "-" + (getinterfaceSex(txe.sex.getValue())));
		emp.birthday.setAsDatetime(txe.borndate.getAsDatetime());
		emp.height.setValue(gettrimv(txe.g_sg.getValue()));
		emp.nation.setValue(gettrimv(txe.hrcode.getValue()));
		emp.married.setValue(gettrimv(txe.hunyin.getValue()));
		emp.pay_way.setValue(gettrimv(txe.jxfs.getValue()));
		emp.noclock.setValue(gettrimv(txe.ifdaka.getValue()));
		emp.nativeplace.setValue(gettrimv(txe.jiguan.getValue()));
		emp.sign_org.setValue(gettrimvdef(txe.g_sfzf.getValue(), "招募没填"));
		emp.registeraddress.setValue(gettrimv(txe.g_jtzz.getValue()));// 户籍地址
		emp.address.setValue(gettrimv(txe.G_Dqzz.getValue()));// 家庭地址 都是家庭地址
		emp.telphone.setValue(gettrimvdef(txe.g_lxdh.getValue(), "招募没填"));
		emp.id_number.setValue(gettrimv(txe.sfz.getValue()));
		emp.sign_date.setValue(gettrimvdef(txe.g_qfrq.getValue(), "1899-12-31"));
		emp.expired_date.setValue(gettrimvdef(txe.g_sxq.getValue(), "1899-12-31"));
		emp.degree.setValue(gettrimv(txe.xueli.getValue()));// getinterfaceDegree(txe.xueli.getValue())
		emp.major.setValue(gettrimv(txe.g_zy.getValue()));
		emp.atdtype.setValue(dictemp.getValueByCation("1399", txe.G_cqlb.getValue(), "0"));// G_cqlb 出勤类别 无法对应 先置为0
		emp.urgencycontact.setValue(gettrimv(txe.g_jjllr.getValue()));
		emp.cellphone.setValue(gettrimv(txe.g_jjlldh.getValue()));
		emp.hiredday.setValue(gettrimv(txe.pydate.getValue()));
		emp.kqdate_start.setAsDatetime(emp.hiredday.getAsDatetime());
		emp.dorm_bed.setValue(gettrimv(txe.roomBed.getValue()));
		emp.dispunit.setValue(gettrimv(txe.g_pqjg.getValue()));
		emp.orgid.setValue(osp.orgid.getValue()); // 部门ID
		emp.orgcode.setValue(osp.orgcode.getValue()); // 部门编码
		emp.orgname.setValue(osp.orgname.getValue()); // 部门名称
		emp.hwc_namezl.setValue(osp.hwc_namezl.getValue()); // 职类
		emp.lv_id.setValue(osp.lv_id.getValue()); // 职级ID
		emp.lv_num.setValue(osp.lv_num.getValue()); // 职级
		emp.hg_id.setValue(osp.hg_id.getValue()); // 职等ID
		emp.hg_code.setValue(osp.hg_code.getValue()); // 职等编码
		emp.hg_name.setValue(osp.hg_name.getValue()); // 职等名称
		emp.ospid.setValue(osp.ospid.getValue()); // 职位ID
		emp.ospcode.setValue(osp.ospcode.getValue()); // 职位编码
		emp.sp_name.setValue(osp.sp_name.getValue()); // 职位名称
		emp.iskey.setValue(osp.iskey.getValue()); // 关键岗位
		emp.hwc_namezq.setValue(osp.hwc_namezq.getValue()); // 职群
		emp.hwc_namezz.setValue(osp.hwc_namezz.getValue()); // 职种
		emp.registertype.setValue(gettrimvdef(txe.g_ygjg.getValue(), "0"));// 户籍类型
		emp.rectcode.setValue(gettrimv(txe.G_ZPRY.getValue()));
		emp.rectname.setValue(gettrimv(txe.G_ZPRYXM.getValue()));
		emp.entrysourcr.setValue(dictemp.getValueByCation("741", txe.G_rzfs.getValue(), "0"));// 人员来源 社会招聘/校园招聘/其它
		emp.eovertype.setValue(dictemp.getValueByCation("1394", txe.G_jblb.getValue(), "0"));// 加班类别
		emp.transorg.setValue(gettrimv(txe.G_lsgjg.getValue()));
		emp.transextime.setValue(gettrimv(txe.G_lwhz.getValue()));
		if (osp.isoffjob.getAsIntDefault(0) == 1) {
			emp.emnature.setValue("脱产");
		} else {
			emp.emnature.setValue("非脱产");
		}

		// System.out.println("户籍类型:" + gettrimv(txe.code.getValue()) + "-" + gettrimv(txe.g_ygjg.getValue()));
		emp.usable.setAsInt(1); // 有效

		if (!isupdate_emp) {// 更新人事资料，这个不能更新啦
			emp.empstatid.setAsInt(6);
		}

		emp.save(con);
		// System.out.println("emp:" + emp.tojson());
		if (!isupdate_emp) {// 更新人事资料，入职表单不能修改
			enty.clear();
			enty.er_id.setValue(emp.er_id.getValue());
			enty.employee_code.setValue(emp.employee_code.getValue());
			enty.entrytype.setAsInt(1);
			enty.entrysourcr.setValue(emp.entrysourcr.getValue());// 人员来源 社会招聘/校园招聘/其它
			enty.entrydate.setAsDatetime(emp.hiredday.getAsDatetime());
			enty.probation.setAsInt(1);// 会议决定按一个月算
			Date pd = Systemdate.dateMonthAdd(emp.hiredday.getAsDatetime(), 1);
			enty.promotionday.setAsDatetime(pd);
			enty.ispromotioned.setAsInt(2);
			enty.quota_over.setAsInt(2);
			enty.orgid.setValue(emp.orgid.getValue());
			enty.transorg.setValue(gettrimv(txe.G_lsgjg.getValue()));
			enty.transextime.setValue(gettrimv(txe.G_lwhz.getValue()));
			enty.orghrlev.setAsInt(0);
			enty.quota_over_rst.setAsInt(2);// 默认允许超编入职
			enty.lv_num.setAsFloat(emp.lv_num.getAsFloat());
			enty.save(con);
			enty.wfcreate(null, con);
		}
	}

	private static void createosp(Hr_orgposition osp, Hr_standposition sp, Shworg org, CDBConnection con) throws Exception {
		osp.assignfieldOnlyValue(sp, fdnames);
		osp.orgid.setValue(org.orgid.getValue());
		osp.orgcode.setValue(org.code.getValue());
		osp.orgname.setValue(org.extorgname.getValue());
		osp.idpath.setValue(org.idpath.getValue());
		osp.pid.setAsInt(0);
		osp.save(con);
	}

	private static void throwerr(String err) throws Exception {
		throw new Exception(err);
	}

}
