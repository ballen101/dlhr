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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
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
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.attd.ctr.CtrHrkq_overtime;
import com.hr.attd.entity.Hrkq_overtime;
import com.hr.attd.entity.Hrkq_overtime_hour;
import com.hr.attd.entity.Hrkq_overtime_line;
import com.hr.attd.entity.Hrkq_overtime_qual;
import com.hr.attd.entity.Hrkq_overtime_qual_line;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hrkq.overtime")
public class COHrkq_overtime {
	@ACOAction(eventname = "findOverTimeHours", Authentication = true, ispublic = false, notes = "查询加班时间段")
	public String findOverTimeHours() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String otl_id = CorUtil.hashMap2Str(parms, "otl_id", "需要参数otl_id");
		Hrkq_overtime_hour oth = new Hrkq_overtime_hour();
		String sqlstr = "SELECT * FROM hrkq_overtime_hour WHERE otl_id= " + otl_id;
		return oth.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getOverTimeTotal", Authentication = true, ispublic = false, notes = "查询加班汇总信息")
	public String getOverTimeTotal() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String[] notnull = {};
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要选择筛选条件");
		}
		List<JSONParm> jps = CJSON.getParms(ps);

		String orgcode = CorUtil.getJSONParmValue(jps, "orgcode1", "需要参数【机构编码】");
		String yearmonth = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【统计年月】");

		Date begin_date = Systemdate.getDateByStr(yearmonth + "-01");
		Date end_date = Systemdate.dateMonthAdd(begin_date, 1);
		String bdate = Systemdate.getStrDateyyyy_mm_dd(begin_date);
		String edate = Systemdate.getStrDateyyyy_mm_dd(end_date);

		Shworg org = new Shworg();
		String sqlstr1 = "select * from shworg where usable=1 and code = '" + orgcode + "'";
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("名称为【" + orgcode + "】的机构不存在");

		String sqlstr = "select '" + yearmonth + "' yearmonth,tb.* from (select em.orgid, em.orgcode, if(em.hwc_namezl='OO',2,1) isoffjob,"
				+ " em.orgname, em.er_id, em.employee_code, em.employee_name, 	"
				+ "	  em.lv_id, em.lv_num, em.ospid, em.sp_name, otl.dealtype, em.idpath,	"  // otl.bgtime, otl.edtime,
				+ "	    sum(otl.othours) othours, SUM(	"
				+ "	    CASE	"
				+ "	      WHEN ot.over_type = 1 and otltype IN(1,2) 	"
				+ "	      THEN otl.othours 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) othour1, SUM(	"
				+ "	    CASE	"
				+ "	      WHEN ot.over_type = 2  and otltype IN(1,2)	"
				+ "	      THEN otl.othours 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) othour2, SUM(	"
				+ "	    CASE	"
				+ "	      WHEN ot.over_type = 3 and otltype IN(1,2)	"
				+ "	      THEN otl.othours 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) othour3, SUM(	"
				+ "	    CASE	"
				+ "	      WHEN otltype IN(3,4,5)	"
				+ "	      THEN otl.othours 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) othour4, SUM(	"
				+ "	    CASE	"
				+ "	      WHEN otl.frst=2	"
				+ "	      THEN 1 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) cdnums, SUM(	"
				+ "	    CASE	"
				+ "	      WHEN otl.trst=3	"
				+ "	      THEN 1 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) ztnums,(	"
				+ "	    CASE	"
				+ "	      WHEN ot.over_type = 1 	"
				+ "	      THEN ot.otrate 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) otrate1, (	"
				+ "	    CASE	"
				+ "	      WHEN ot.over_type = 2 	"
				+ "	      THEN ot.otrate 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) otrate2, (	"
				+ "	    CASE	"
				+ "	      WHEN ot.over_type = 3 	"
				+ "	      THEN ot.otrate 	"
				+ "	      ELSE 0 	"
				+ "	    END	"
				+ "	  ) otrate3 	"
				+ "	FROM hr_employee em,hrkq_overtime_list otl LEFT JOIN hrkq_overtime ot ON  ot.ot_id = otl.ot_id  "
				+ "	where  otl.er_id = em.er_id 	"
				+ "	    AND em.idpath like '" + org.idpath.getValue() + "%'";

		sqlstr = sqlstr + " and otl.edtime>='" + bdate + "'";
		sqlstr = sqlstr + " and otl.edtime<='" + edate + "'";
		sqlstr = sqlstr + "	group by otl.er_id,otl.dealtype) tb where 1=1 	";
		String[] ignParms = { "yearmonth", "orgcode1" };// 忽略的查询条件

		JSONObject rst = new CReport(sqlstr, notnull).findReport2JSON_O(ignParms);
		JSONArray rows = rst.getJSONArray("rows");
		getOverTimeTotalExinfo(rows, bdate, edate);
		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(rows, scols);
			return null;
		}
	}

	private void getOverTimeTotalExinfo(JSONArray rows, String bdate, String edate) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			String er_id = row.getString("er_id");
			if (row.has("dealtype") && (row.getInt("dealtype") == 2)) {
				String sqlstr = "SELECT IFNULL(permonlimit,0) permonlimit FROM hrkq_overtime_qual q,`hrkq_overtime_qual_line`l "
						+ " WHERE q.`oq_id`=l.oq_id AND q.`stat`=9 AND l.`breaked`=2 AND l.er_id=" + er_id
						+ " AND q.`begin_date`<'" + edate + "' AND q.`end_date`>'" + bdate + "' "
						+ " ORDER BY q.`apply_date` DESC LIMIT 1";
				List<HashMap<String, String>> mps = DBPools.defaultPool().openSql2List(sqlstr);
				double permonlimit = 0;
				if (mps.size() > 0) {
					permonlimit = Double.valueOf(mps.get(0).get("permonlimit").toString());
				}
				row.put("permonlimit", permonlimit);
				double othours = row.getDouble("othours");
				double ccothours = othours - permonlimit;
				if (ccothours < 0)
					ccothours = 0;
				row.put("ccothours", ccothours);
				double vvothours = othours - ccothours;
				row.put("vvothours", vvothours);
			}
		}
	}

	@ACOAction(eventname = "checkOverTime", Authentication = true, ispublic = false, notes = "检查加班时间合法性")
	public String checkOverTime() throws Exception {
		Hrkq_overtime ot = new Hrkq_overtime();
		ot.fromjson(CSContext.getPostdata());
		if (ot.stat.getAsIntDefault(0) >= 2) {
			return "{\"rst\":\"OK\"}";
		}
		if ((ot.isoffjob.getAsIntDefault(0) == 2) || (ot.dealtype.getAsIntDefault(0) == 1))// 非脱产或调休不控制
			return "{\"rst\":\"OK\"}";
		for (CJPABase jt : ot.hrkq_overtime_lines) {
			Hrkq_overtime_line otl = (Hrkq_overtime_line) jt;
			CtrHrkq_overtime.check(ot, otl);
		}
		return "{\"rst\":\"OK\"}";
	}

	@ACOAction(eventname = "qualimpexcel", Authentication = true, ispublic = false, notes = "加班资格从表Excel")
	public String qualimpexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String lv_num = CorUtil.hashMap2Str(parms, "emplev", "需要参数emplev");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		String rst = null;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile(p, orgid, lv_num);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
		return rst;
	}

	private String parserExcelFile(Shw_physic_file pf, String orgid, String lv_num) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
		// : new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet(aSheet, orgid, lv_num);
	}

	private String parserExcelSheet(Sheet aSheet, String orgid, String lv_num) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "";
		}
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");

		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);

		JSONArray rst = new JSONArray();
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			// System.out.println("employee_code:" + employee_code);
			if ((employee_code == null) || (employee_code.isEmpty()))
				continue;
			Hr_employee emp = new Hr_employee();
			String sqlstr = "SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "' and idpath like '" + org.idpath.getValue() + "%'";
			int lvn = Integer.valueOf(lv_num);
			if (lvn == 1) {
				sqlstr = sqlstr + " and lv_num>=3 and lv_num<4 ";
			}
			if (lvn == 2) {
				sqlstr = sqlstr + " and lv_num>=4 ";
			}
			emp.findBySQL(sqlstr);
			if (emp.isEmpty())
				throw new Exception("限定机构及职级条件下，工号【" + employee_code + "】的人事资料不存在");

			JSONObject jo = emp.toJsonObj();
			jo.put("remark", v.get("remark"));
			rst.add(jo);
		}
		return rst.toString();
	}

	private List<CExcelField> initExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "overtimeimpexcel", Authentication = true, ispublic = false, notes = "加班从表孙表导入Excel")
	public String overtimeimpexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String lv_num = CorUtil.hashMap2Str(parms, "emplev", "需要参数emplev");
		String isoffjob = CorUtil.hashMap2Str(parms, "isoffjob", "需要参数isoffjob");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		String rst = null;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFileOverTime(p, orgid, lv_num, isoffjob);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
		return rst;
	}

	private String parserExcelFileOverTime(Shw_physic_file pf, String orgid, String lv_num, String isoffjob) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
		// : new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheetOvertime(aSheet, orgid, lv_num, isoffjob);
	}

	private String parserExcelSheetOvertime(Sheet aSheet, String orgid, String lv_num, String isoffjob) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "";
		}
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");

		List<CExcelField> efds = initExcelFieldsOvertime();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 1);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 1);

		JSONArray rst = new JSONArray();
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			// System.out.println("employee_code:" + employee_code);
			if ((employee_code == null) || (employee_code.isEmpty()))
				continue;
			Hr_employee emp = new Hr_employee();
			String sqlstr = "SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "' and idpath like '" + org.idpath.getValue() + "%'";
			int lvn = Integer.valueOf(lv_num);
			if (lvn == 1) {
				sqlstr = sqlstr + " and lv_num>=3 and lv_num<4 and hg_name not like 'M%'";
			}
			if (lvn == 2) {
				sqlstr = sqlstr + " and lv_num>=4";
			}

			int ifo = Integer.valueOf(isoffjob);
			if (ifo == 1)
				sqlstr = sqlstr + " and emnature='脱产'";
			if (ifo == 2)
				sqlstr = sqlstr + " and emnature='非脱产'";

			emp.findBySQL(sqlstr);
			if (emp.isEmpty())
				throw new Exception("限定机构、职级及职位性质条件下，工号【" + employee_code + "】的人事资料不存在");

			Date bd = Systemdate.getDateByStr(v.get("begin_date"));
			Date ed = Systemdate.getDateByStr(v.get("end_date"));
			if (ed.getTime() <= bd.getTime())
				throw new Exception("工号为【" + employee_code + "】的加班时段，截止时间小于开始时间");
			JSONObject jo = emp.toJsonObj();
			jo.put("begin_date", Systemdate.getStrDateByFmt(bd, "yyyy-MM-dd HH:mm"));
			jo.put("end_date", Systemdate.getStrDateByFmt(ed, "yyyy-MM-dd HH:mm"));
			jo.put("needchedksb", ("是".equalsIgnoreCase(v.get("needchedksb"))) ? 1 : 2);
			jo.put("needchedkxb", ("是".equalsIgnoreCase(v.get("needchedkxb"))) ? 1 : 2);
			rst.add(jo);
		}
		return rst.toString();
	}

	private List<CExcelField> initExcelFieldsOvertime() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("开始时间", "begin_date", true));
		efields.add(new CExcelField("结束时间", "end_date", true));
		efields.add(new CExcelField("上班打卡", "needchedksb", true));
		efields.add(new CExcelField("下班打卡", "needchedkxb", true));
		return efields;
	}

	@ACOAction(eventname = "find", Authentication = true, ispublic = true, notes = "加班申请替换通用查询")
	public String find() throws Exception {
		String sqlstr = "select * from hrkq_overtime where 1=1 ";
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] ignParms = { "employee_code" };// 忽略的查询条件
		Hrkq_overtime ho = new Hrkq_overtime();
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			// sqlstr = sqlstr + " and ot_id=" + id;
			ho.findByID(id);
			return ho.tojson();
		} else {
			String parms = urlparms.get("parms");
			List<JSONParm> jps = CJSON.getParms(parms);
			JSONParm ecp = CorUtil.getJSONParm(jps, "employee_code");
			if (ecp != null)
				sqlstr = sqlstr + "AND EXISTS(SELECT 1 FROM hrkq_overtime_line WHERE hrkq_overtime.ot_id=hrkq_overtime_line.ot_id "
						+ "AND hrkq_overtime_line.employee_code='" + ecp.getParmvalue() + "')";
			String where = CSearchForm.getCommonSearchWhere(urlparms, ho);
			if ((where != null) && (!where.isEmpty()))
				sqlstr = sqlstr + where;
			//sqlstr = sqlstr + " order by ot_id desc";
			return new CReport(sqlstr," createtime desc ", null).findReport(ignParms);
		}

	}

	@ACOAction(eventname = "findqual", Authentication = true, ispublic = true, notes = "加班资格替换通用查询")
	public String findqual() throws Exception {
		String sqlstr = "select * from hrkq_overtime_qual where 1=1 ";
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] ignParms = { "employee_code" };// 忽略的查询条件
		Hrkq_overtime_qual ho = new Hrkq_overtime_qual();
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			// sqlstr = sqlstr + " and ot_id=" + id;
			ho.findByID(id);
			return ho.tojson();
		} else {
			String parms = urlparms.get("parms");
			List<JSONParm> jps = CJSON.getParms(parms);
			JSONParm ecp = CorUtil.getJSONParm(jps, "employee_code");
			if (ecp != null)
				sqlstr = sqlstr + "AND EXISTS(SELECT 1 FROM hrkq_overtime_qual_line WHERE hrkq_overtime_qual.oq_id=hrkq_overtime_qual_line.oq_id "
						+ "AND hrkq_overtime_qual_line.employee_code='" + ecp.getParmvalue() + "')";
			String where = CSearchForm.getCommonSearchWhere(urlparms, ho);
			if ((where != null) && (!where.isEmpty()))
				sqlstr = sqlstr + where;
			//sqlstr = sqlstr + " order by createtime desc";
			return new CReport(sqlstr," createtime desc ", null).findReport(ignParms);
		}

	}

	@ACOAction(eventname = "qualbatchimpexcel", Authentication = true, ispublic = false, notes = "加班资格批量Excel")
	public String qualbatchimpexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelQBFile(p, batchno);
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

	private int parserExcelQBFile(Shw_physic_file pf, String batchno) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
		// : new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserQBExcelSheet(aSheet, batchno);
	}

	private int parserQBExcelSheet(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}

		String lguserid = CSContext.getUserID();
		String lgusername = CSContext.getUserName();
		String lguserdsname = CSContext.getUserDisplayname();
		String widpath = CSContext.getIdpathwhere();
		Date nowdate = new Date();

		List<CExcelField> efds = initQBExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hrkq_overtime_qual oq = new Hrkq_overtime_qual();
		Hrkq_overtime_qual_line oql = new Hrkq_overtime_qual_line();
		Hr_employee emp = new Hr_employee();
		CDBConnection con = emp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String employee_code = v.get("employee_code");
				// System.out.println("employee_code:" + employee_code);
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				rst++;
				String sqlstr = "SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'" + widpath;
				emp.clear();
				emp.findBySQL(sqlstr);
				if (emp.isEmpty())
					throw new Exception("权限范围内，工号【" + employee_code + "】的人事资料不存在");
				oq.clear();
				oq.hrkq_overtime_qual_lines.clear();
				oql.clear();
				oq.er_id.setValue(lguserid); // 申请人档案ID
				oq.employee_code.setValue(lgusername); // 申请人工号
				oq.employee_name.setValue(lguserdsname); // 申请人姓名
				oq.orgid.setValue(emp.orgid.getValue()); // 部门ID
				oq.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				oq.orgname.setValue(emp.orgname.getValue()); // 部门名称
				oq.apply_date.setAsDatetime(nowdate); // 申请日期
				oq.over_type.setValue(getover_type(dictemp, employee_code, v.get("over_type"))); // 申请加班类型
				oq.begin_date.setValue(v.get("begin_date")); // 申请加班开始时间
				oq.end_date.setValue(v.get("end_date")); // 申请加班结束时间
				oq.dealtype.setValue("2"); // 加班处理
				// oq.employee_type.setValue(value); // 加班人员类型
				oq.permonlimit.setValue(v.get("permonlimit")); // 月度加班上限时数
				oq.appreason.setValue(v.get("appreason")); // 申请原因
				oq.remark.setValue(v.get("remark")); // 备注
				oq.stat.setValue("1"); // 表单状态
				oq.idpath.setValue(emp.idpath.getValue()); // idpath
				oq.entid.setValue("1"); // entid
				oq.creator.setValue(lguserdsname); // 创建人
				oq.createtime.setAsDatetime(nowdate); // 创建时间
				oq.emplev.setValue("0"); // 人事层级
				oq.orghrlev.setValue("0"); // 机构人事层级
				// //////
				oql.breaked.setValue("2"); // 已终止
				oql.er_id.setValue(emp.er_id.getValue()); // 员工档案ID
				oql.employee_code.setValue(emp.employee_code.getValue()); // 工号
				oql.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				oql.orgid.setValue(emp.orgid.getValue()); // 部门ID
				oql.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				oql.orgname.setValue(emp.orgname.getValue()); // 部门名称
				oql.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
				oql.lv_num.setValue(emp.lv_num.getValue()); // 职级
				oql.ospid.setValue(emp.ospid.getValue()); // 职位ID
				oql.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				oql.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				oql.orghrlev.setValue("0"); // 机构人事层级
				oql.emplev.setValue("0"); // 人事层级
				oql.remark.setValue("批量导入" + batchno); // 备注
				oq.hrkq_overtime_qual_lines.add(oql);
				oq.save(con);
				oq.wfcreate(null, con);
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

	private String getover_type(DictionaryTemp dictemp, String employee_code, String over_type) throws Exception {
		String[] ots = over_type.split(",");
		String rst = "";
		for (String ot : ots) {
			if ((ot != null) && (!ot.isEmpty())) {
				String otid = dictemp.getVbCE("923", ot, false, "工号【" + employee_code + "】加班类型【" + ot + "】不存在");
				rst = rst + otid + ",";
			}
		}
		if (rst.isEmpty())
			throw new Exception("工号【" + employee_code + "】加班类型不能为空");
		else
			rst = rst.substring(0, rst.length() - 1);
		return rst;
	}

	private List<CExcelField> initQBExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("月上限(H)", "permonlimit", true));
		efields.add(new CExcelField("加班处理", "dealtype", true));
		efields.add(new CExcelField("加班类型", "over_type", true));
		efields.add(new CExcelField("开始日期", "begin_date", true));
		efields.add(new CExcelField("截止日期", "end_date", true));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}

}
