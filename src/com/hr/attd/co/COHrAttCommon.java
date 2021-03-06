package com.hr.attd.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.attd.ctr.CacalKQData;
import com.hr.attd.ctr.CtrHrkq_calc;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.attd.entity.Hrkq_calc;
import com.hr.attd.entity.Hrkq_resign;
import com.hr.attd.entity.Hrkq_resignline;
import com.hr.attd.entity.Hrkq_resigntimeparm;
import com.hr.attd.entity.Hrkq_sched;
import com.hr.attd.entity.Hrkq_sched_line;
import com.hr.attd.entity.Hrkq_swcdlst;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.CalcThread;
import com.hr.util.HRSpecDataFilter;

@ACO(coname = "web.hrkq.cmn")
public class COHrAttCommon {
	@ACOAction(eventname = "calcDateDiffDay", Authentication = true, notes = "获取日期相差天数,最小单位0.5")
	public String calcDateDiffDay() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		Date bgtime = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "bgtime", "需要参数bgtime"));
		Date edtime = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "edtime", "需要参数edtime"));
		if (bgtime.getTime() >= edtime.getTime())
			throw new Exception("开始时间大于截止时间");
		float hours = CacalKQData.calcDateDiffHH(bgtime, edtime);
		float days = (float) hours / 8;// 按每天8小时计算
		if ((days * 10 % 5) != 0)
			throw new Exception("算法错误，最小单位为半天");
		JSONObject rst = new JSONObject();
		rst.put("rst", days);
		return rst.toString();
	}

	@ACOAction(eventname = "calcDateDiffMonth", Authentication = true, notes = "获取日期相差月数")
	public String calcDateDiffMonth() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		Date bgtime = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "bgtime", "需要参数bgtime"));
		Date edtime = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "edtime", "需要参数edtime"));
		if (bgtime.getTime() >= edtime.getTime())
			throw new Exception("开始时间大于截止时间");

		JSONObject rst = new JSONObject();
		rst.put("rst", Systemdate.getBetweenMonth(bgtime, edtime));
		return rst.toString();

	}

	@ACOAction(eventname = "getAttParm", Authentication = true, notes = "获取考勤参数")
	public String getAttParm() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String parmcode = CorUtil.hashMap2Str(parms, "parmcode", "需要参数parmcode");
		JSONObject rst = new JSONObject();
		rst.put("value", HrkqUtil.getParmValueErr(parmcode));
		return rst.toString();
	}

	@ACOAction(eventname = "redocalc", Authentication = true, notes = "考勤重新计算")
	public String redocalc() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String clcid = CorUtil.hashMap2Str(parms, "clcid", "需要参数clcid");
		Hrkq_calc calc = new Hrkq_calc();
		calc.findByID(clcid);
		if (calc.isEmpty())
			throw new Exception("ID为【" + clcid + "】的考勤计算单不存在");
		// CDBConnection con = calc.pool.getCon(this);
		// con.startTrans();
		try {
			// CalcThread ct = new CalcThread(clcid); // 线程运行
			// ct.start();
			// calc.findByID4Update(con, clcid, true);
			// calc.stat.setAsInt(1);// 制单状态
			// // calc.execstat.setAsInt(1);// 待执行
			// calc.save(con);
			// calc.wfcreate(null, con);
			// con.submit();
			(new CacalKQData()).calcKQReq(clcid);
			calc.findByID(clcid);
			return calc.tojson();
		} catch (Exception e) {
			// con.rollback();
			throw e;
		}

	}

	@ACOAction(eventname = "impresigntimeparmexcel", Authentication = true, ispublic = false, notes = "批量导入补签参数")
	public String impresigntimeparmexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile(p, batchno);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("rst", rst);
		jo.put("batchno", batchno);
		return jo.toString();
	}

	private int parserExcelFile(Shw_physic_file pf, String batchno) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		// HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fullname));
		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet(aSheet, batchno);
	}

	private int parserExcelSheet(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_employee emp = new Hr_employee();
		CDBConnection con = emp.pool.getCon(this);
		con.startTrans();
		Hrkq_resigntimeparm rp = new Hrkq_resigntimeparm();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String employee_code = v.get("employee_code");
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				rst++;
				emp.clear();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
				if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】的人事资料不存在");
				rp.clear();
				int resigntimes = Integer.valueOf(v.get("resigntimes"));
				rp.resigntimes.setAsInt(resigntimes);
				rp.note.setValue(v.get("note"));
				rp.assignfieldOnlyValue(emp, new String[] { "er_id", "employee_code", "employee_name",
						"ospid", "ospcode", "sp_name", "lv_num", "orgid", "orgcode", "hg_name",
						"orgname", "idpath", "hwc_namezl", "hiredday" });
				rp.property1.setValue(batchno);
				rp.save(con);
			}
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}
	/*
	 *补签查询用 
	 */
	@ACOAction(eventname = "findkqcalcrst4resign", Authentication = true, notes = "查询考勤计算结果为补签")
	public String findkqcalcrst4resign() throws Exception {
		String[] notnull = {};
		String sqlstr = "SELECT r.bckqid,r.kqdate,e.er_id,e.employee_code,e.employee_name,e.orgname,e.sp_name,e.lv_num,"
				+ " r.frtime,r.frsktime,r.mtslate,r.frst,r.totime,r.lrst,r.tosktime,r.mtslearly,r.trst,r.isabnom,"
				+ " r.scdname,r.sclid,r.bcno,e.idpath"
				+ " FROM hrkq_bckqrst r,hr_employee e"
				+ " WHERE r.er_id=e.er_id and r.kqdate<CURDATE() AND r.kqdate>=e.kqdate_start "; // 添加入职日期以后 17-12-11 moyh 改为考勤开始时间以后
		HashMap<String, String> parms = CSContext.getParms();

		String spcetype = CorUtil.hashMap2Str(parms, "spcetype");
		if ((spcetype != null) && (!spcetype.isEmpty())) {
			int si = Integer.valueOf(spcetype);
			if (si > 0) {
				sqlstr = sqlstr + " and e.employee_code='" + CSContext.getUserName() + "'";
			}
		}

		sqlstr = sqlstr + "  AND NOT EXISTS("
				+ " SELECT * FROM `hrkq_resign` h,  `hrkq_resignline`  l"
				+ " WHERE h.resid =l.resid AND l.`kqdate`=r.`kqdate` AND r.`er_id`=h.`er_id`"
				+ " AND r.sclid=l.`sclid` AND l.isreg=1"
				+ " AND h.`stat`>1 AND h.`stat`<10"
				+ " )";
		return new CReport(sqlstr, " kqdate DESC ", notnull).findReport();
	}

	private List<CExcelField> initExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("签卡限制", "resigntimes", true));
		efields.add(new CExcelField("备注", "note", false));
		return efields;
	}

	@ACOAction(eventname = "impresignbatchexcel", Authentication = true, ispublic = false, notes = "导入批量补签")
	public String impresignbatchexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserrbExcelFile(p, batchno);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("rst", rst);
		jo.put("batchno", batchno);
		return jo.toString();
	}

	private int parserrbExcelFile(Shw_physic_file pf, String batchno) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fullname));
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		HSSFSheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserrbExcelSheet(aSheet, batchno);
	}

	private int parserrbExcelSheet(HSSFSheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initRBExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		String ym = checkisTheSameMonth(values);// 检查是否同一月
		List<String> ecs = getEmpcodeList(values);// 获取所有不重复工号
		Hr_employee emp = new Hr_employee();
		CDBConnection con = emp.pool.getCon(this);
		con.startTrans();
		Hrkq_resign rs = new Hrkq_resign();
		CJPALineData<Hrkq_sched> scheds = new CJPALineData<Hrkq_sched>(Hrkq_sched.class);// 班次缓存
		int rst = 0;
		try {
			for (String ec : ecs) {
				String ec1 = ec;
				if ((ec1 == null) || (ec1.isEmpty()))
					continue;

				emp.clear();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + ec1 + "'");
				if (emp.isEmpty())
					throw new Exception("工号【" + ec1 + "】的人事资料不存在");

				rs.clear();
				rs.er_id.setValue(emp.er_id.getValue()); // 申请人档案ID
				rs.employee_code.setValue(ec1); // 申请人工号
				rs.employee_name.setValue(emp.employee_name.getValue()); // 申请人姓名
				rs.resdate.setValue(ym + "-01"); // 考勤月度
				rs.orgid.setValue(emp.orgid.getValue()); // 部门ID
				rs.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				rs.orgname.setValue(emp.orgname.getValue()); // 部门名称
				rs.ospid.setValue(emp.ospid.getValue()); // 职位ID
				rs.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				rs.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				rs.lv_num.setValue(emp.lv_num.getValue()); // 职级
				rs.res_type.setValue("1"); // 补签类别 1 因公 2因私
				rs.orghrlev.setValue("0"); // 机构人事层级
				rs.emplev.setValue("0"); // 人事层级
				rs.res_times.setValue("0"); // 补签次数
				rs.stat.setValue("1"); // 表单状态
				rs.idpath.setValue(emp.idpath.getValue()); // idpath
				rs.attribute1.setValue(batchno);
				for (Map<String, String> v : values) {
					String ec2 = v.get("employee_code").trim();
					if ((ec2 == null) || (ec2.isEmpty()))
						continue;
					if (ec1.equalsIgnoreCase(ec2)) {
						rst++;
						rs.remark.setValue(v.get("note")); // 备注
						Hrkq_sched sched = getCatchSched(scheds, v.get("scdode").trim());
						if (sched.isEmpty())
							throw new Exception("编码为【" + v.get("scdode").trim() + "】的班制不存在");
						Hrkq_sched_line sl = getslbyso(sched, v.get("bcno").trim());
						if (sl.isEmpty())
							throw new Exception("编码为【" + v.get("scdode").trim() + "】班制的班次号【" + v.get("bcno").trim() + "】不存在");
						Hrkq_resignline rsl = new Hrkq_resignline();
						rsl.kqdate.setValue(v.get("kqdate").trim()); // 考勤日期
						rsl.ltype.setValue("1"); // 1 正班 2 加班 3值班
						rsl.bcno.setValue(sl.bcno.getValue()); // 班次号
						int sxb = ("上班".equalsIgnoreCase(v.get("sxb").trim())) ? 1 : 2;
						rsl.sxb.setAsInt(sxb); // 1 上班 2 下班
						if (sxb == 1)
							rsl.rgtime.setValue(sl.frtime.getValue()); // 打卡时间
						else
							rsl.rgtime.setValue(sl.totime.getValue()); // 打卡时间
						rsl.isreg.setValue("1"); // 是否签卡
						rsl.resreson.setValue(v.get("resreson").trim()); // 补签原因
						rsl.sclid.setValue(sl.sclid.getValue()); // 班次行ID
						rsl.scid.setValue(sl.scid.getValue()); // 班次ID
						rsl.scdname.setValue(sched.scdname.getValue()); // 班次名
						rsl.ri_times.setValue("2"); // 计入补签次数
						rs.hrkq_resignlines.add(rsl);
					}
				}
				rs.save(con);
				rs.wfcreate(null, con);
			}
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private List<CExcelField> initRBExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("考勤日期", "kqdate", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("班制编码", "scdode", true));
		efields.add(new CExcelField("班次号", "bcno", true));
		efields.add(new CExcelField("上下班", "sxb", true));
		efields.add(new CExcelField("计入补签次数", "ri_times", true));
		efields.add(new CExcelField("补签原因", "resreson", false));
		efields.add(new CExcelField("备注", "note", false));
		return efields;
	}

	private Hrkq_sched getCatchSched(CJPALineData<Hrkq_sched> scheds, String scdode) throws Exception {
		Hrkq_sched rst = null;
		for (CJPABase jpa : scheds) {
			Hrkq_sched sched = (Hrkq_sched) jpa;
			if (sched.scdode.getValue().equalsIgnoreCase(scdode))
				rst = sched;
		}
		if (rst == null) {
			rst = new Hrkq_sched();
			rst.findBySQL("select * from hrkq_sched where scdode='" + scdode + "'");
			scheds.add(rst);
		}
		return rst;
	}

	private Hrkq_sched_line getslbyso(Hrkq_sched sc, String bcno) {
		if ((bcno == null) || (bcno.isEmpty()))
			return null;
		for (CJPABase cb : sc.hrkq_sched_lines) {
			Hrkq_sched_line sl = (Hrkq_sched_line) cb;
			if (bcno.equalsIgnoreCase(sl.bcno.getValue()))
				return sl;
		}
		return null;
	}

	private List<String> getEmpcodeList(List<Map<String, String>> values) {
		List<String> rst = new ArrayList<String>();
		for (Map<String, String> map : values) {
			String ec = map.get("employee_code").trim();
			if (rst.indexOf(ec) < 0)
				rst.add(ec);
		}
		return rst;
	}

	private String checkisTheSameMonth(List<Map<String, String>> values) throws Exception {
		if (values.size() == 0)
			return null;
		String ym = Systemdate.getStrDateByFmt(Systemdate.getDateByStr(values.get(0).get("kqdate").trim()), "yyyy-MM");
		for (Map<String, String> map : values) {
			String ym1 = Systemdate.getStrDateByFmt(Systemdate.getDateByStr(map.get("kqdate").trim()), "yyyy-MM");
			if (!ym.equalsIgnoreCase(ym1))
				throw new Exception("只能导入同一个月的补签数据");
		}
		return ym;
	}

	@ACOAction(eventname = "doBackAndMoveKQSWCT", Authentication = true, notes = "将打卡记录同步到历史表中")
	public String doBackAndMoveKQSWCT() throws Exception {
		HrkqUtil.backAndMoveKQSWCT();
		JSONObject rst = new JSONObject();
		rst.put("result", "OK");
		return rst.toString();
	}

}
