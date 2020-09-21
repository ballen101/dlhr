package com.hr.insurance.co;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.Login;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.insurance.entity.Hr_ins_buyins;
import com.hr.insurance.entity.Hr_ins_buyins_line;
import com.hr.insurance.entity.Hr_ins_buyinsurance;
import com.hr.insurance.entity.Hr_ins_cancel;
import com.hr.insurance.entity.Hr_ins_insurancetype;
import com.hr.insurance.entity.Hr_ins_insurancetype_line;
import com.hr.insurance.entity.Hr_ins_prebuyins;
import com.hr.perm.entity.Hr_empconbatch_line;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_contract;
import com.hr.perm.entity.Hr_leavejob;
import com.hr.util.HRUtil;

@ACO(coname = "web.hrins.insurance")
public class COHr_ins_insurance {
	@ACOAction(eventname = "findbuyinsmonth", Authentication = true, notes = "获取月参保明细")
	public String findbuyinsmonth() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		// JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpbuyday = CjpaUtil.getParm(jps, "buydday");
		if (jpbuyday == null)
			throw new Exception("需要参数【buydday】");
		String orgcode = jporgcode.getParmvalue();
		String reloper = jporgcode.getReloper();
		String dqdate = jpbuyday.getParmvalue();
		// String emplcode="";
		// if(jpempcode!=null){
		// emplcode= jpcosttime.getParmvalue();
		// }

		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月

		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");

		// String sqlstr = "SELECT * FROM hr_ins_buyinsurance buy WHERE buy.stat=9 ";

		String[] notnull = {};
		// String sqlstr =
		// "SELECT buy.insbuy_code,buy.buydday as buyday,buy.payment,buy.tselfpay,buy.tcompay,buy.remark as tremark,buy.stat,buy.ins_type AS instype,buy.insname,buyl.* "+
		// "FROM hr_ins_buyins buy,hr_ins_buyins_line buyl WHERE buy.stat=9 AND buyl.insbuy_id=buy.insbuy_id ";
		String sqlstr = "SELECT * FROM hr_ins_buyinsurance buy WHERE buy.stat=9  AND buy.idpath ";
		if (reloper.equals("<>")) {
			sqlstr = sqlstr + " not ";
		}
		sqlstr = sqlstr + " LIKE '" + org.idpath.getValue() + "%' and buy.buydday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' and buy.buydday<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "'";
		String[] ignParms = { "buydday", "orgcode" };// 忽略的查询条件
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		int rst = rts.size();
		for (int i = 0; i < rst; i++) {
			JSONObject row = rts.getJSONObject(i);
			if (row.isEmpty())
				continue;
			Hr_employee emp = new Hr_employee();
			emp.findByID(row.getString("er_id"));
			if (emp.isEmpty())
				continue;
			// if(emp.empstatid.getAsInt()>11){
			/*
			 * Hr_leavejob lj=new Hr_leavejob();
			 * lj.findBySQL("SELECT * FROM hr_leavejob WHERE stat=9 AND er_id="+row.getString("er_id")+" ORDER BY ljdate DESC");
			 * if(!lj.isEmpty()){
			 * row.put("ljdate", lj.ljdate.getValue());
			 * }else{
			 * row.put("ljdate", "");
			 * }
			 */
			row.put("ljdate", emp.ljdate.getValue());
			Date firstdate = Systemdate.getFirstAndLastOfMonth(Systemdate.getDateByStr(Systemdate.getStrDate())).date1;
			String fd = Systemdate.getStrDateyyyy_mm_dd(firstdate);
			Hr_ins_cancel cc = new Hr_ins_cancel();
			cc.findBySQL("SELECT * FROM hr_ins_cancel WHERE stat=9 AND er_id=" + row.getString("er_id") + "  ORDER BY canceldate DESC");
			if (!cc.isEmpty()) {
				String buyid = row.getString("buyins_id");
				// System.out.println(buyid+"---"+cc.insbuy_id.getValue());
				if (cc.insbuy_id.getValue().equals(buyid)) {
					row.put("canceldate", cc.canceldate.getValue());
				} else {
					row.put("canceldate", "");
				}
			} else {
				row.put("canceldate", "");
			}
			// }
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}

	}

	@ACOAction(eventname = "findcancelinsmonth", Authentication = true, notes = "获取月退保明细")
	public String findcancelinsmonth() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		// JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpcancelday = CjpaUtil.getParm(jps, "canceldate");
		if (jpcancelday == null)
			throw new Exception("需要参数【canceldate】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpcancelday.getParmvalue();
		String reloper = jporgcode.getReloper();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");

		String[] notnull = {};
		// String sqlstr =
		// " SELECT cc.*,bi.payment,bi.tselfpay,bi.tcompay FROM hr_ins_cancel cc,hr_ins_buyins bi WHERE cc.stat=9 and bi.insbuy_id=cc.insbuy_id ";
		String sqlstr = " SELECT cc.*,bi.buyins_code,bi.payment,bi.tselfpay,bi.tcompay FROM hr_ins_cancel cc,hr_ins_buyinsurance bi WHERE cc.stat=9 AND bi.buyins_id=cc.insbuy_id  ";
		sqlstr = sqlstr + " and cc.idpath ";
		if (reloper.equals("<>")) {
			sqlstr = sqlstr + " not ";
		}
		sqlstr = sqlstr + " like '%" + org.idpath.getValue() + "%' and cc.canceldate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' and cc.canceldate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "'";
		String[] ignParms = { "canceldate", "orgcode" };// 忽略的查询条件
		return new CReport(sqlstr, notnull).findReport(ignParms);
	}

	@ACOAction(eventname = "findtbljemp", Authentication = true, ispublic = true, notes = "查询退保的离职人员")
	public String findtbljemp() throws Exception {
		/*
		 * String sqlstr =
		 * "SELECT emp.pay_way,emp.empstatid,bi.insbuy_code,bi.buydday as buyday,bi.ins_type as instype,bi.insit_id,bi.insname,bi.insurance_number,bil.*,bi.idpath "
		 * +
		 * " FROM hr_employee emp, hr_ins_buyins bi,hr_ins_buyins_line bil "+
		 * "WHERE bi.stat=9 AND bil.insbuy_id=bi.insbuy_id AND emp.er_id=bil.er_id  AND emp.empstatid>=12  ";
		 */
		String sqlstr = "SELECT emp.pay_way,emp.empstatid,bi.* FROM hr_employee emp, hr_ins_buyinsurance bi WHERE bi.stat=9  AND emp.er_id=bi.er_id  AND emp.empstatid>=12 ";
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String parms = urlparms.get("parms");
		List<JSONParm> jps = CJSON.getParms(parms);
		Hr_employee ep = new Hr_employee();
		String[] ignParms = { "employee_code", "orgname" };// 忽略的查询条件
		JSONParm orgparm = CorUtil.getJSONParm(jps, "orgname");
		if (orgparm != null)
			sqlstr = sqlstr + " and emp.orgname like '%" + orgparm.getParmvalue() + "%' ";

		JSONParm empcode = CorUtil.getJSONParm(jps, "employee_code");
		if (empcode != null)
			sqlstr = sqlstr + " AND emp.employee_code='" + empcode.getParmvalue() + "'";
		String where = CSearchForm.getCommonSearchWhere(urlparms, ep);
		if ((where != null) && (!where.isEmpty()))
			sqlstr = sqlstr + where;
		sqlstr = sqlstr + " order by bi.buydday desc";
		JSONObject rst = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		return rst.toString();
	}

	@ACOAction(eventname = "getljemployee", Authentication = true, ispublic = false, notes = "查询退保人员的离职单")
	public String getljemployee() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		Hr_leavejob hrlj = new Hr_leavejob();
		String sqlstr = " SELECT * FROM hr_leavejob lj WHERE lj.stat=9 AND lj.er_id="
				+ er_id + "  ORDER BY ljid DESC LIMIT 1";

		return hrlj.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getccempbuyins", Authentication = true, ispublic = false, notes = "查询退保人员的购保单单")
	public String getccempbuyins() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		Hr_ins_buyinsurance bi = new Hr_ins_buyinsurance();
		String sqlstr = " SELECT * FROM hr_ins_buyinsurance WHERE stat=9 AND er_id="
				+ er_id + "  ORDER BY buydday DESC LIMIT 1";
		return bi.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findinsmonthlist", Authentication = true, notes = "获取月购保统计")
	public String findinsmonthlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		// JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpbuyday = CjpaUtil.getParm(jps, "buydday");
		if (jpbuyday == null)
			throw new Exception("需要参数【buydday】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpbuyday.getParmvalue();
		// String emplcode="";
		// if(jpempcode!=null){
		// emplcode= jpcosttime.getParmvalue();
		// }

		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "buydday", "orgcode" };// 忽略的查询条件
		String[] notnull = {};
		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		dws.add(0, org.toJsonObj());
		dofindinsdetail(dws, bgdate, eddate);
		/*
		 * String sqlstr = "SELECT bi.buydday AS buyday,bil.*,SUM(bil.selfpay) AS tsp,SUM(bil.compay) AS tcp,SUM(bil.lpayment) AS tpm,COUNT(*) AS tnum "+
		 * " FROM hr_ins_buyins bi,hr_ins_buyins_line bil WHERE bi.stat=9 AND bil.insbuy_id=bi.insbuy_id  ";
		 */
		/*
		 * String sqlstr = "SELECT bi.*,SUM(bi.tselfpay) AS tsp,SUM(bi.tcompay) AS tcp,SUM(bi.payment) AS tpm,COUNT(*) AS tnum  FROM hr_ins_buyinsurance bi WHERE bi.stat=9 ";
		 * sqlstr = sqlstr + " and bi.idpath like '%" + org.idpath.getValue() + "%' and bi.buydday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
		 * + "' and bi.buydday<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' GROUP BY bi.orgid";
		 * return new CReport(sqlstr, notnull).findReport(ignParms);
		 */

		String scols = urlparms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	@ACOAction(eventname = "PremiumCalculator", Authentication = true, notes = "保费计算器（计算已购保险在险种标准变更时的差额）")
	public String PremiumCalculator() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpbuyday = CjpaUtil.getParm(jps, "buydday");
		if (jpbuyday == null)
			throw new Exception("需要参数【buydday】");
		JSONParm jpoitname = CjpaUtil.getParm(jps, "oitname");
		if (jpoitname == null)
			throw new Exception("需要参数【oitname】");
		JSONParm jpnitname = CjpaUtil.getParm(jps, "nitname");
		if (jpnitname == null)
			throw new Exception("需要参数【nitname】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpbuyday.getParmvalue();
		String oldname = jpoitname.getParmvalue();
		String newname = jpnitname.getParmvalue();

		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒

		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");

		Hr_ins_insurancetype instype1 = new Hr_ins_insurancetype();
		String sqlstr2 = " SELECT * FROM hr_ins_insurancetype WHERE insname='" + oldname + "'";
		instype1.findBySQL(sqlstr2);
		if (instype1.isEmpty())
			throw new Exception("名称为【" + oldname + "】的险种不存在");

		Hr_ins_insurancetype instype2 = new Hr_ins_insurancetype();
		String sqlstr3 = " SELECT * FROM hr_ins_insurancetype WHERE insname='" + newname + "'";
		instype2.findBySQL(sqlstr3);
		if (instype2.isEmpty())
			throw new Exception("名称为【" + newname + "】的险种不存在");

		String[] notnull = {};
		/*
		 * String sqlstr = " SELECT bi.insbuy_code,bi.buydday AS buyday,bi.insit_id AS oitid,"+
		 * "bi.insname AS oitname,bi.insurancebase as obase,bil.*,"+
		 * " it.insit_id AS nitid,it.insname AS nitname,it.insurancebase AS nbase,it.payment AS npayment,it.selfpay AS nsp,it.compay AS ncp, "+
		 * "(IFNULL(it.selfpay,0)-IFNULL(bil.selfpay,0)) AS spdiff,(IFNULL(it.compay,0)-IFNULL(bil.compay,0)) AS cpdiff,(IFNULL(it.payment,0)-IFNULL(bil.lpayment,0)) AS paydiff "
		 * +
		 * " FROM hr_ins_buyins bi,hr_ins_buyins_line bil,hr_ins_insurancetype it "+
		 */
		String sqlstr = " SELECT bi.buydday AS buyday,bi.insit_id AS oitid,bi.insname AS oitname,bi.insurancebase AS obase,bi.*, it.insit_id AS nitid,it.insname AS nitname," +
				" it.insurancebase AS nbase,it.payment AS npayment,it.selfpay AS nsp,it.compay AS ncp, (IFNULL(it.selfpay,0)-IFNULL(bi.tselfpay,0)) AS spdiff," +
				"(IFNULL(it.compay,0)-IFNULL(bi.tcompay,0)) AS cpdiff, (IFNULL(it.payment,0)-IFNULL(bi.payment,0)) AS paydiff   FROM hr_ins_buyinsurance bi,hr_ins_insurancetype it " +
				" WHERE bi.stat=9  AND bi.idpath LIKE '%" + org.idpath.getValue() + "%' " +
				" AND bi.insit_id=" + instype1.insit_id.getAsInt() + "  AND it.insit_id=" + instype2.insit_id.getAsInt() +
				" AND bi.buydday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "'";

		String[] ignParms = { "buydday", "orgcode", "oitname", "nitname" };// 忽略的查询条件
		return new CReport(sqlstr, notnull).findReport(ignParms);
	}

	@ACOAction(eventname = "impinscancellistexcel", Authentication = true, ispublic = false, notes = "导入退保登记Excel")
	public String impinscancellistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_inscancel(p, batchno);
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

	private int parserExcelFile_inscancel(Shw_physic_file pf, String batchno) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

//		Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//				: new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_inscancel(aSheet, batchno);
	}

	private int parserExcelSheet_inscancel(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_inscancel();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);

		// Hr_ins_buyins insbuy=new Hr_ins_buyins();

		CJPALineData<Hr_ins_cancel> newccbiss = new CJPALineData<Hr_ins_cancel>(Hr_ins_cancel.class);

		// CDBConnection con = emp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		// con.startTrans();
		int rst = 0;
		// try {
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("退保单上的工号不能为空");

			Hr_employee emp = new Hr_employee();
			emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料");
			/*
			 * if(emp.empstatid.getAsInt()!=12)
			 * throw new Exception("工号【" + employee_code + "】的员工不是离职状态");
			 */

			Date nowdate = Systemdate.getDateByStr(v.get("canceldate"));// 获取当前日期
			Date bgdate = Systemdate.getFirstAndLastOfMonth(nowdate).date1;// 获取本月第一天日期
			Date eddate = Systemdate.getFirstAndLastOfMonth(nowdate).date2;// 获取本月最后一天日期
			Hr_ins_cancel oldcc = new Hr_ins_cancel();
			oldcc.findBySQL(" SELECT * FROM `hr_ins_cancel` WHERE er_id=" + emp.er_id.getValue() + " AND canceldate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "'  AND canceldate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "'");
			if (!oldcc.isEmpty()) {
				// throw new Exception("工号【" + employee_code + "】本月已有退保单！");
				continue;
			}
			Hr_leavejob lj = new Hr_leavejob();
			lj.findBySQL("SELECT * FROM hr_leavejob WHERE stat=9 AND employee_code='" + employee_code + "'");
			/*
			 * if (lj.isEmpty())
			 * throw new Exception("工号【" + employee_code + "】不存在离职记录");
			 */

			Hr_ins_buyinsurance insbuy = new Hr_ins_buyinsurance();
			// insbuy.findBySQL("SELECT * FROM hr_ins_buyins bi,hr_ins_buyins_line bil WHERE bi.stat=9 AND bil.insbuy_id=bi.insbuy_id AND bil.employee_code='"
			// + employee_code+"'" );
			String instype = dictemp.getVbCE("1220", v.get("ins_type"), false, "员工【" + emp.employee_name.getValue() + "】保险类型【" + v.get("ins_type")
					+ "】不存在");
			// System.out.println("---------------"+instype);
			insbuy.findBySQL("SELECT * FROM hr_ins_buyinsurance bi WHERE bi.stat=9 and ins_type=" + instype + "  AND bi.employee_code='" + employee_code + "' order by buydday desc");
			if (insbuy.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在购保信息！");
			// continue;

			Hr_ins_cancel inscc = new Hr_ins_cancel();
			inscc.remark.setValue(v.get("remark")); // 备注
			inscc.er_id.setValue(emp.er_id.getValue()); // 人事档案id
			inscc.employee_code.setValue(emp.employee_code.getValue()); // 申请人工号
			inscc.employee_name.setValue(emp.employee_name.getValue()); // 姓名
			inscc.orgid.setValue(emp.orgid.getValue()); // 部门
			inscc.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
			inscc.orgname.setValue(emp.orgname.getValue()); // 部门名称
			inscc.ospid.setValue(emp.ospid.getValue()); // 职位id
			inscc.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
			inscc.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
			inscc.lv_id.setValue(emp.lv_id.getValue()); // 职级id
			inscc.lv_num.setValue(emp.lv_num.getValue()); // 职级
			inscc.degree.setValue(emp.degree.getValue()); // 学历
			inscc.sex.setValue(emp.sex.getValue()); // 性别
			inscc.birthday.setValue(emp.birthday.getValue()); // 出生日期
			inscc.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
			inscc.telphone.setValue(emp.telphone.getValue()); // 联系电话
			inscc.id_number.setValue(emp.id_number.getValue()); // 身份证号
			inscc.sign_org.setValue(emp.sign_org.getValue()); // 发证机构
			inscc.sign_date.setValue(emp.sign_date.getValue()); // 有效日期
			inscc.expired_date.setValue(emp.expired_date.getValue()); // 截止日期
			inscc.nativeplace.setValue(emp.nativeplace.getValue()); // 籍贯
			inscc.registertype.setValue(emp.registertype.getValue()); // 户籍类型

			/*
			 * CJPALineData<Hr_ins_buyins_line> bils=insbuy.hr_ins_buyins_lines;
			 * Hr_ins_buyins_line bil=new Hr_ins_buyins_line();
			 * for (CJPABase jpa2 : bils) {
			 * Hr_ins_buyins_line temp=(Hr_ins_buyins_line)jpa2;
			 * if(employee_code.equals(temp.employee_code.getValue())){
			 * bil=temp;
			 * }
			 * }
			 */
			inscc.age.setValue(insbuy.age.getValue()); // 年龄
			inscc.pay_type.setValue(emp.pay_way.getValue()); // 记薪方式
			inscc.insurance_number.setValue(insbuy.insurance_number.getValue()); // 参保号
			inscc.buydday.setValue(insbuy.buydday.getValue()); // 参保日期
			inscc.ins_type.setValue(insbuy.ins_type.getValue()); // 保险类型
			inscc.reg_type.setValue(insbuy.reg_type.getValue()); // 参保性质
			inscc.insbuy_id.setValue(insbuy.buyins_id.getValue()); // 购保单id
			inscc.insbuy_code.setValue(insbuy.buyins_code.getValue()); // 购保单编码
			inscc.insit_id.setValue(insbuy.insit_id.getValue()); // 险种id
			inscc.insname.setValue(insbuy.insname.getValue()); // 险种名称

			if (!lj.isEmpty()) {
				inscc.ljdate.setValue(lj.ljdate.getValue()); // 离职日期
				inscc.ljtype2.setValue(lj.ljtype2.getValue()); // 离职类型
				inscc.ljreason.setValue(lj.ljreason.getValue()); // 离职原因
			}

			inscc.canceldate.setValue(v.get("canceldate")); // 退保日期
			inscc.cancelreason.setValue(v.get("cancelreason")); // 退保原因
			inscc.stat.setAsInt(9);// 保存直接完成
			// inscc.save(con);
			newccbiss.add(inscc);
			rst++;
		}
		if (newccbiss.size() > 0) {
			System.out.println("====================导入购保单条数【" + newccbiss.size() + "】");
			// System.out.println("+++++++++++++++++++++++++++"+listOfString.toString());
			newccbiss.saveBatchSimple();// 高速存储
		}
		newccbiss.clear();
		// con.submit();
		return rst;
		/*
		 * } catch (Exception e) {
		 * con.rollback();
		 * throw e;
		 * }
		 */
	}

	private List<CExcelField> initExcelFields_inscancel() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("参保号", "insurance_number", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("性别", "sex", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("联系电话", "telphone", true));
		efields.add(new CExcelField("参保日期", "reginsdate", true));
		efields.add(new CExcelField("保险类型", "ins_type", true));
		efields.add(new CExcelField("离职日期", "ljdate", true));
		efields.add(new CExcelField("离职类型", "ljtype2", true));
		efields.add(new CExcelField("离职原因", "ljreason", true));
		efields.add(new CExcelField("退保日期", "canceldate", true));
		efields.add(new CExcelField("退保原因", "cancelreason", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "findinscancelmonthlist", Authentication = true, notes = "月退保统计")
	public String findinscancelmonthlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		// JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpccday = CjpaUtil.getParm(jps, "canceldate");
		if (jpccday == null)
			throw new Exception("需要参数【canceldate】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpccday.getParmvalue();
		// String emplcode="";
		// if(jpempcode!=null){
		// emplcode= jpcosttime.getParmvalue();
		// }
		String reloper = jporgcode.getReloper();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "canceldate", "orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT cc.orgid,cc.orgname,'" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' as canceldate, COUNT(*) AS tnum,SUM(bi.payment) AS tpayment,SUM(bi.tselfpay) AS tselfpay,SUM(bi.tcompay) AS tcompay  " +
				" FROM  hr_ins_cancel cc,hr_ins_buyinsurance bi WHERE cc.stat=9 AND bi.buyins_id=cc.insbuy_id and cc.idpath ";
		if (reloper.equals("<>")) {
			sqlstr = sqlstr + " not ";
		}
		sqlstr = sqlstr + "  like '%" + org.idpath.getValue() + "%' and cc.canceldate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' and cc.canceldate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' GROUP BY cc.orgid,cc.orgname";
		return new CReport(sqlstr, notnull).findReport(ignParms);
	}

	@ACOAction(eventname = "impbuyinslistexcel", Authentication = true, ispublic = false, notes = "导入购保登记明细Excel")
	public String impbuyinslistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		String insitid = CorUtil.hashMap2Str(CSContext.getParms(), "insit_id", "需要参数insit_id");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_buyins(p, orgid, insitid);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_buyins(Shw_physic_file pf, String orgid, String insitid) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

//		Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//				: new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_buyins(aSheet, orgid, insitid);
	}

	private String parserExcelSheet_buyins(Sheet aSheet, String orgid, String insitid) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_buyins();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}
		Hr_ins_insurancetype instype = new Hr_ins_insurancetype();
		instype.findByID(insitid);
		if (instype.isEmpty()) {
			throw new Exception("没找到ID为【" + insitid + "】的保险资料");
		}
		Hr_employee emp = new Hr_employee();
		CJPALineData<Hr_ins_buyins_line> bils = new CJPALineData<Hr_ins_buyins_line>(Hr_ins_buyins_line.class);
		Shworg emporg = new Shworg();
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("购保单上的工号不能为空");

			emp.clear();
			emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料");
			emporg.clear();
			emporg.findByID(emp.orgid.getValue());
			if (emporg.isEmpty())
				throw new Exception("没找到员工【" + emp.employee_name.getValue() + "】所属的ID为【" + emp.orgid.getValue() + "】的机构");
			if (emporg.idpath.getValue().indexOf(org.idpath.getValue()) == -1) {
				throw new Exception("员工【" + employee_code + "】不属于此机构");
			}
			Hr_ins_buyins_line buyinsline = new Hr_ins_buyins_line();

			buyinsline.er_id.setValue(emp.er_id.getValue());
			buyinsline.employee_code.setValue(emp.employee_code.getValue());
			buyinsline.employee_name.setValue(emp.employee_name.getValue());
			buyinsline.ospid.setValue(emp.ospid.getValue());
			buyinsline.ospcode.setValue(emp.ospcode.getValue());
			buyinsline.sp_name.setValue(emp.sp_name.getValue());
			buyinsline.lv_id.setValue(emp.lv_id.getValue());
			buyinsline.lv_num.setValue(emp.lv_num.getValue());
			buyinsline.hiredday.setValue(emp.hiredday.getValue());
			buyinsline.orgid.setValue(emp.orgid.getValue());
			buyinsline.orgcode.setValue(emp.orgcode.getValue());
			buyinsline.orgname.setValue(emp.orgname.getValue());
			buyinsline.degree.setValue(emp.degree.getValue());
			buyinsline.sex.setValue(emp.sex.getValue());
			buyinsline.telphone.setValue(emp.telphone.getValue());
			buyinsline.nativeplace.setValue(emp.nativeplace.getValue());
			buyinsline.registertype.setValue(emp.registertype.getValue());
			buyinsline.pay_type.setValue(emp.pay_way.getValue());
			buyinsline.id_number.setValue(emp.id_number.getValue());
			buyinsline.sign_org.setValue(emp.sign_org.getValue());
			buyinsline.sign_date.setValue(emp.sign_date.getValue());
			buyinsline.expired_date.setValue(emp.expired_date.getValue());
			buyinsline.birthday.setValue(emp.birthday.getValue());
			int age = getAgeByBirth(emp.birthday.getValue());
			buyinsline.age.setAsInt(age);
			buyinsline.sptype.setValue(emp.emnature.getValue());
			int isnew = Integer.valueOf(dictemp.getVbCE("5", v.get("isnew"), false, "员工【" + emp.employee_name.getValue() + "】的是否新增【" + v.get("isnew")
					+ "】不存在"));
			int regtype = Integer.valueOf(dictemp.getVbCE("1234", v.get("reg_type"), false, "员工【" + emp.employee_name.getValue() + "】参保性质【" + v.get("reg_type")
					+ "】不存在"));
			buyinsline.isnew.setAsInt(isnew);
			buyinsline.reg_type.setAsInt(regtype);
			/*
			 * String nowtime ="";
			 * Calendar hday = Calendar.getInstance();
			 * Date hd=Systemdate.getDateByStr(emp.hiredday.getValue());
			 * hday.setTime(hd);
			 * int inday=hday.get(Calendar.DATE);
			 * if(inday<21){
			 * nowtime = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM-dd");
			 * }else{
			 * Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateByFmt(new Date(), "yyyy-MM-dd"));// 去除时分秒
			 * Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
			 * nowtime =Systemdate.getStrDateyyyy_mm_dd(eddate);
			 * }
			 */
			buyinsline.buydday.setValue(v.get("buydday"));
			buyinsline.ins_type.setValue(instype.ins_type.getValue());
			buyinsline.selfratio.setValue(instype.selfratio.getValue());
			buyinsline.selfpay.setValue(instype.selfpay.getValue());
			buyinsline.comratio.setValue(instype.comratio.getValue());
			buyinsline.compay.setValue(instype.compay.getValue());
			buyinsline.insurance_number.setValue(v.get("insurance_number"));
			buyinsline.insurancebase.setValue(instype.insurancebase.getValue());
			float sp = 0;
			float cp = 0;
			if (!("".equals(instype.selfpay.getValue()) || (instype.selfpay.getValue() == null))) {
				sp = Float.parseFloat(instype.selfpay.getValue());
			}
			if (!("".equals(instype.compay.getValue()) || (instype.compay.getValue() == null))) {
				cp = Float.parseFloat(instype.compay.getValue());
			}
			float lpm = sp + cp;
			buyinsline.lpayment.setAsFloat(lpm);
			buyinsline.remark.setValue(v.get("remark"));
			bils.add(buyinsline);
		}
		return bils.tojson();

	}

	private List<CExcelField> initExcelFields_buyins() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("参保日期", "buydday", true));
		efields.add(new CExcelField("参保号", "insurance_number", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("性别", "sex", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("联系电话", "telphone", true));
		efields.add(new CExcelField("是否新增", "isnew", true));
		efields.add(new CExcelField("参保性质", "reg_type", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	public static int getAgeByBirth(String birthday) {
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

	@ACOAction(eventname = "impinstypelineexcel", Authentication = true, ispublic = false, notes = "导入险种设置明细Excel")
	public String impinstypelineexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_instype(p);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_instype(Shw_physic_file pf) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

//		Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//				: new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_instype(aSheet);
	}

	private String parserExcelSheet_instype(Sheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_instype();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		CJPALineData<Hr_ins_insurancetype_line> itls = new CJPALineData<Hr_ins_insurancetype_line>(Hr_ins_insurancetype_line.class);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		DecimalFormat dec = new DecimalFormat("0.00");
		for (Map<String, String> v : values) {
			String lins = v.get("linsurance");
			if ((lins == null) || (lins.isEmpty()))
				throw new Exception("险种设置明细的保险名称不能为空");

			String pb = v.get("paybase");
			if ((pb == null) || (pb.isEmpty()))
				throw new Exception("险种设置明细的缴费基数不能为空");

			String lcr = v.get("lcomratio");
			if ((lcr == null) || (lcr.isEmpty()))
				throw new Exception("险种设置明细的单位缴费占比不能为空");

			String lsr = v.get("lselfratio");
			if ((lsr == null) || (lsr.isEmpty()))
				throw new Exception("险种设置明细的个人缴费占比不能为空");

			float cro = Float.parseFloat(lcr);
			float sro = Float.parseFloat(lsr);
			float paybase = Float.parseFloat(pb);
			float cp = 0;
			float sp = 0;
			cp = (paybase * cro) / 100;
			sp = (paybase * sro) / 100;
			float lpm = cp + sp;

			Hr_ins_insurancetype_line itline = new Hr_ins_insurancetype_line();
			itline.paybase.setValue(dec.format(paybase));
			itline.lpayment.setValue(dec.format(lpm));
			itline.linsurance.setValue(lins);
			itline.lselfratio.setValue(dec.format(sro));
			itline.lselfpay.setValue(dec.format(sp));
			itline.lcomratio.setValue(dec.format(cro));
			itline.lcompay.setValue(dec.format(cp));
			itline.remark.setValue(v.get("remark"));
			itls.add(itline);
		}
		return itls.tojson();

	}

	private List<CExcelField> initExcelFields_instype() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("保险名称", "linsurance", true));
		efields.add(new CExcelField("缴费基数", "paybase", true));
		efields.add(new CExcelField("单位缴费占比", "lcomratio", true));
		efields.add(new CExcelField("单位缴费金额", "lcompay", true));
		efields.add(new CExcelField("个人缴费占比", "lselfratio", true));
		efields.add(new CExcelField("个人缴费金额", "lselfpay", true));
		efields.add(new CExcelField("缴纳总金额", "lpayment", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "findemployeeinslist", Authentication = true, notes = "员工社保汇总查询")
	public String findemployeeinslist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		// JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		Date bgdate = new Date();
		Date eddate = new Date();
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpmondate = CjpaUtil.getParm(jps, "monthdate");
		if (jpmondate == null)
			throw new Exception("需要参数【monthdate】");
		String dqdate = jpmondate.getParmvalue();
		bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月

		JSONParm jpbeginday = CjpaUtil.getParm(jps, "beginday");
		if (jpbeginday != null) {
			String beginday = jpbeginday.getParmvalue();
			bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(beginday)));
		}
		JSONParm jpendday = CjpaUtil.getParm(jps, "endday");
		if (jpendday != null) {
			String endday = jpendday.getParmvalue();
			eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(endday)));
		}

		String orgcode = jporgcode.getParmvalue();
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "canceldate", "orgcode" };// 忽略的查询条件
		String[] notnull = {};

		String sqlstr = " SELECT emp.*,bi.er_id AS biler_id,bi.buydday AS buydate,bi.buyins_code,bi.insurance_number,bi.insurancebase,bi.tselfpay AS lselfpay," +
				"bi.tcompay AS lcompay,bi.payment AS lpayment,bi.sptype,bi.reg_type,bi.ins_type,bi.insname, " +
				"cc.ljdate AS ljdate2,ljtype2,ljreason,canceldate,cancelreason " +
				" FROM (SELECT * FROM hr_employee WHERE  empstatid=4 AND idpath LIKE '" + org.idpath.getValue() + "%' )   emp " +
				" LEFT JOIN (SELECT * FROM hr_ins_buyinsurance  WHERE stat=9  AND buydday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) +
				"' AND buydday<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "') bi ON (emp.er_id=bi.er_id ) " +
				" LEFT JOIN (SELECT er_id,ljdate ,ljtype2,ljreason,canceldate,cancelreason FROM hr_ins_cancel WHERE stat=9) cc ON (emp.er_id=cc.er_id) ";
		return new CReport(sqlstr, notnull).findReport(ignParms);
	}

	@ACOAction(eventname = "findemployeeinslistex", Authentication = true, notes = "员工社保汇总查询EX")
	public String findemployeeinslistex() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		String reloper = jporgcode.getReloper();
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");

		JSONParm jpmondate = CjpaUtil.getParm(jps, "monthdate");
		if (jpmondate == null)
			throw new Exception("需要参数【monthdate】");
		String dqdate = jpmondate.getParmvalue();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String bds = Systemdate.getStrDateyyyy_mm_dd(bgdate);
		String eds = Systemdate.getStrDateyyyy_mm_dd(eddate);

		String[] ignParms = { "canceldate", "orgcode", "monthdate", "beginday", "endday", "instype" };// 忽略的查询条件
		String sqlstr = "SELECT * FROM hr_employee WHERE empstatid=4 AND idpath";
		if (reloper.equals("<>")) {
			sqlstr = sqlstr + " not ";
		}
		sqlstr = sqlstr + " LIKE '" + org.idpath.getValue() + "%'";
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		Hr_ins_buyinsurance ibs = new Hr_ins_buyinsurance();
		Hr_ins_cancel ibsc = new Hr_ins_cancel();
		for (int i = 0; i < rts.size(); i++) {
			JSONObject row = rts.getJSONObject(i);
			String er_id = row.getString("er_id");
			sqlstr = "SELECT * FROM hr_ins_buyinsurance WHERE er_id=" + er_id + " and stat=9 AND buydday >='" + bds + "' AND buydday<='" + eds + "'";
			ibs.clear();
			ibs.findBySQL(sqlstr, false);
			if (!ibs.isEmpty()) {
				row.put("buydate", ibs.buydday.getValue());
				row.put("buyins_code", ibs.buyins_code.getValue());
				row.put("insurance_number", ibs.insurance_number.getValue());
				row.put("insurancebase", ibs.insurancebase.getValue());
				row.put("lselfpay", ibs.tselfpay.getValue());
				row.put("lcompay", ibs.tcompay.getValue());
				row.put("lpayment", ibs.payment.getValue());
				row.put("sptype", ibs.sptype.getValue());
				row.put("reg_type", ibs.reg_type.getValue());
				row.put("ins_type", ibs.ins_type.getValue());
				row.put("insname", ibs.insname.getValue());
			}
			sqlstr = "select * from hr_ins_cancel where stat=9 and er_id=" + er_id;
			ibsc.clear();
			ibsc.findBySQL(sqlstr, false);
			if (!ibsc.isEmpty()) {
				row.put("ljdate2", ibsc.ljdate.getValue());
				row.put("ljtype2", ibsc.ljtype2.getValue());
				row.put("ljreason", ibsc.ljreason.getValue());
				row.put("canceldate", ibsc.canceldate.getValue());
				row.put("cancelreason", ibsc.cancelreason.getValue());
			}
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}
	}

	@ACOAction(eventname = "dosumbitbuyinslist", Authentication = true, notes = "一键提交购保单")
	public String dosumbitbuyinslist() throws Exception {
		// CJPALineData<Hr_ins_buyins> bis= new CJPALineData<Hr_ins_buyins>(Hr_ins_buyins.class);
		Date maxdate = Systemdate.getFirstAndLastOfMonth(new Date()).date2;// 当月最后一天的日期
		CJPALineData<Hr_ins_buyinsurance> bis = new CJPALineData<Hr_ins_buyinsurance>(Hr_ins_buyinsurance.class);
		// String sqlstr = "select * from hr_ins_buyins bi where bi.stat=1 ";
		String sqlstr = "select * from hr_ins_buyinsurance bi where bi.stat=1 AND buydday<='" + Systemdate.getStrDateyyyy_mm_dd(maxdate) + "' ";
		sqlstr = sqlstr + " order by bi.buyins_id desc";
		bis.findDataBySQL(sqlstr, true, false);
		// System.out.println("------------------"+bis.size());
		// Hr_ins_buyins tempbi=new Hr_ins_buyins();
		Hr_ins_buyinsurance tempbi = new Hr_ins_buyinsurance();
		CDBConnection con = tempbi.pool.getCon(this);
		con.startTrans();
		try {
			for (CJPABase jpa : bis) {
				Hr_ins_buyinsurance bi = (Hr_ins_buyinsurance) jpa;
				tempbi.clear();
				tempbi.findByID(bi.buyins_id.getValue());
				/*
				 * CJPALineData<Hr_ins_buyins_line> bils=tempbi.hr_ins_buyins_lines;
				 * if(bils.size()==0){
				 * continue;
				 * }
				 */
				// System.out.println("+++++++++++++++++++++++++++"+bils.size());
				tempbi.wfcreate(null, con);
			}
			con.submit();
			JSONObject rst = new JSONObject();
			rst.put("rst", "ok");
			return rst.toString();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	@ACOAction(eventname = "dosumbitcancelinslist", Authentication = true, notes = "一键提交退保单")
	public String dosumbitcancelinslist() throws Exception {
		CJPALineData<Hr_ins_cancel> ccs = new CJPALineData<Hr_ins_cancel>(Hr_ins_cancel.class);
		String sqlstr = "select * from hr_ins_cancel cc where cc.stat=1  ";
		sqlstr = sqlstr + " order by cc.inscc_id desc";
		ccs.findDataBySQL(sqlstr, true, false);
		// System.out.println("------------------"+ccs.size());
		// int counts=0;
		Hr_ins_cancel tempcc = new Hr_ins_cancel();
		CDBConnection con = tempcc.pool.getCon(this);
		con.startTrans();
		try {
			for (CJPABase jpa : ccs) {
				Hr_ins_cancel cc = (Hr_ins_cancel) jpa;
				tempcc.clear();
				tempcc.findByID(cc.inscc_id.getValue());
				if (tempcc.insbuy_id.isEmpty()) {
					continue;
				}

				tempcc.wfcreate(null, con);
				// counts++;
			}
			con.submit();
			// System.out.println("++++++++++++++++"+counts);
			JSONObject rst = new JSONObject();
			rst.put("rst", "ok");
			return rst.toString();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	@ACOAction(eventname = "impinsbuylistexcel", Authentication = true, ispublic = false, notes = "批量导入购保登记Excel")
	public String impinsbuylistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelSheet_buyinslist(p, batchno);
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

	private int parserExcelSheet_buyinslist(Shw_physic_file pf, String batchno) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		Workbook workbook = WorkbookFactory.create(file);
		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
		// : new XSSFWorkbook(new FileInputStream(fullname));
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_buyinslist(aSheet, batchno);
	}

	private int parserExcelSheet_buyinslist(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_buyinslist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);

		Hr_ins_insurancetype instype = new Hr_ins_insurancetype();
		Hr_employee emp = new Hr_employee();
		Hr_ins_buyinsurance buyins = new Hr_ins_buyinsurance();
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		Shwuser admin = Login.getAdminUser();
		String loginuser = CSContext.getUserName();
		CDBConnection con = emp.pool.getCon(this);
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String employee_code = v.get("employee_code");
				if ((employee_code == null) || (employee_code.isEmpty()))
					throw new Exception("购保单上的工号不能为空");

				emp.clear();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
				if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在人事资料");
				rst++;
				/*
				 * emporg.clear();
				 * emporg.findByID(emp.orgid.getValue());
				 * if (emporg.isEmpty())
				 * throw new Exception("没找到员工【"+emp.employee_name.getValue()+"】所属的ID为【" + emp.orgid.getValue() + "】的机构");
				 * if (emporg.idpath.getValue().indexOf(org.idpath.getValue()) == -1) {
				 * throw new Exception("员工【" + employee_code + "】不属于此机构");
				 * }
				 */
				String insitcode = v.get("insit_code");
				if ((insitcode == null) || (insitcode.isEmpty()))
					throw new Exception("购保单上的保险编号不能为空");
				instype.clear();
				instype.findBySQL("SELECT * FROM hr_ins_insurancetype WHERE stat=9 and  insit_code='" + insitcode + "'");
				if (instype.isEmpty()) {
					throw new Exception("没找到编号为【" + insitcode + "】的保险资料");
				}

				buyins.clear();

				buyins.er_id.setValue(emp.er_id.getValue());
				buyins.employee_code.setValue(emp.employee_code.getValue());
				buyins.employee_name.setValue(emp.employee_name.getValue());
				buyins.ospid.setValue(emp.ospid.getValue());
				buyins.ospcode.setValue(emp.ospcode.getValue());
				buyins.sp_name.setValue(emp.sp_name.getValue());
				buyins.lv_id.setValue(emp.lv_id.getValue());
				buyins.lv_num.setValue(emp.lv_num.getValue());
				buyins.hiredday.setValue(emp.hiredday.getValue());
				buyins.orgid.setValue(emp.orgid.getValue());
				buyins.orgcode.setValue(emp.orgcode.getValue());
				buyins.orgname.setValue(emp.orgname.getValue());
				buyins.idpath.setValue(emp.idpath.getValue());
				buyins.degree.setValue(emp.degree.getValue());
				buyins.sex.setValue(emp.sex.getValue());
				buyins.telphone.setValue(emp.telphone.getValue());
				buyins.nativeplace.setValue(emp.nativeplace.getValue());
				buyins.registertype.setValue(emp.registertype.getValue());
				buyins.registeraddress.setValue(emp.registeraddress.getValue());
				buyins.torgname.setValue(emp.transorg.getValue());
				buyins.sorgname.setValue(emp.dispunit.getValue());
				buyins.pay_type.setValue(emp.pay_way.getValue());
				buyins.id_number.setValue(emp.id_number.getValue());
				buyins.sign_org.setValue(emp.sign_org.getValue());
				buyins.sign_date.setValue(emp.sign_date.getValue());
				buyins.expired_date.setValue(emp.expired_date.getValue());

				buyins.birthday.setValue(emp.birthday.getValue());
				int age = getAgeByBirth(emp.birthday.getValue());
				buyins.age.setAsInt(age);
				buyins.sptype.setValue(emp.emnature.getValue());
				int isnew = Integer.valueOf(dictemp.getVbCE("5", v.get("isnew"), false, "员工【" + emp.employee_name.getValue() + "】的是否新增【" + v.get("isnew")
						+ "】不存在"));
				int regtype = Integer.valueOf(dictemp.getVbCE("1234", v.get("reg_type"), false, "员工【" + emp.employee_name.getValue() + "】参保性质【" + v.get("reg_type")
						+ "】不存在"));
				buyins.isnew.setAsInt(isnew);
				buyins.reg_type.setAsInt(regtype);
				buyins.buydday.setValue(v.get("buydday"));
				buyins.attribute1.setValue(v.get("attribute1"));
				buyins.insit_id.setValue(instype.insit_id.getValue());
				buyins.insit_code.setValue(instype.insit_code.getValue());
				buyins.insname.setValue(instype.insname.getValue());
				buyins.ins_type.setValue(instype.ins_type.getValue());
				buyins.selfratio.setValue(instype.selfratio.getValue());
				buyins.selfpay.setValue(instype.selfpay.getValue());
				buyins.tselfpay.setValue(instype.selfpay.getValue());
				buyins.comratio.setValue(instype.comratio.getValue());
				buyins.compay.setValue(instype.compay.getValue());
				buyins.tcompay.setValue(instype.compay.getValue());
				buyins.insurance_number.setValue(v.get("insurance_number"));
				buyins.insurancebase.setValue(instype.insurancebase.getValue());
				buyins.payment.setValue(instype.payment.getValue());
				buyins.remark.setValue(v.get("remark"));
				buyins.creator.setValue(loginuser);
				buyins.createtime.setValue(Systemdate.getStrDate());
				buyins.save(con);
				buyins.wfcreate(null, con, admin.userid.getValue(), "1", null);// 当前Session无登录验证，用任意管理员账号 提交表单（无流程）
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

	private List<CExcelField> initExcelFields_buyinslist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("缴费年月", "buydday", true));
		efields.add(new CExcelField("购保日期", "attribute1", true));
		efields.add(new CExcelField("参保号", "insurance_number", true));
		efields.add(new CExcelField("保险编号", "insit_code", true));
		efields.add(new CExcelField("保险名称", "insname", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("性别", "sex", true));
		efields.add(new CExcelField("户籍类型", "registertype", true));
		efields.add(new CExcelField("户籍住址", "registeraddress", true));
		efields.add(new CExcelField("输送机构", "torgname", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("联系电话", "telphone", true));
		efields.add(new CExcelField("是否新增", "isnew", true));
		efields.add(new CExcelField("参保性质", "reg_type", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "dotransferbuyinslist", Authentication = true, notes = "将主从表购保单数据导到单表购保单")
	public String dotransferbuyinslist() throws Exception {
		CJPALineData<Hr_ins_buyins> bis = new CJPALineData<Hr_ins_buyins>(Hr_ins_buyins.class);
		CJPALineData<Hr_ins_buyinsurance> newbis = new CJPALineData<Hr_ins_buyinsurance>(Hr_ins_buyinsurance.class);
		String sqlstr = "select * from hr_ins_buyins bi where 1=1  ";
		bis.findDataBySQL(sqlstr, true, false);
		Hr_ins_buyins tempbi = new Hr_ins_buyins();
		// CDBConnection con = tempbi.pool.getCon(this);
		// con.startTrans();
		for (CJPABase jpa : bis) {
			Hr_ins_buyins bi = (Hr_ins_buyins) jpa;
			tempbi.clear();
			tempbi.findByID(bi.insbuy_id.getValue());
			CJPALineData<Hr_ins_buyins_line> bils = tempbi.hr_ins_buyins_lines;
			for (CJPABase bijpa : bils) {
				Hr_ins_buyins_line tempbil = (Hr_ins_buyins_line) bijpa;
				Hr_employee emp = new Hr_employee();
				emp.findByID(tempbil.er_id.getValue(), false);
				Hr_ins_buyinsurance buyins = new Hr_ins_buyinsurance();
				buyins.er_id.setValue(emp.er_id.getValue());
				buyins.employee_code.setValue(emp.employee_code.getValue());
				buyins.employee_name.setValue(emp.employee_name.getValue());
				buyins.ospid.setValue(emp.ospid.getValue());
				buyins.ospcode.setValue(emp.ospcode.getValue());
				buyins.sp_name.setValue(emp.sp_name.getValue());
				buyins.lv_id.setValue(emp.lv_id.getValue());
				buyins.lv_num.setValue(emp.lv_num.getValue());
				buyins.hiredday.setValue(emp.hiredday.getValue());
				buyins.orgid.setValue(emp.orgid.getValue());
				buyins.orgcode.setValue(emp.orgcode.getValue());
				buyins.orgname.setValue(emp.orgname.getValue());
				buyins.idpath.setValue(emp.idpath.getValue());
				buyins.degree.setValue(emp.degree.getValue());
				buyins.sex.setValue(emp.sex.getValue());
				buyins.telphone.setValue(emp.telphone.getValue());
				buyins.nativeplace.setValue(emp.nativeplace.getValue());
				buyins.registertype.setValue(emp.registertype.getValue());
				buyins.pay_type.setValue(emp.pay_way.getValue());
				buyins.id_number.setValue(emp.id_number.getValue());
				buyins.sign_org.setValue(emp.sign_org.getValue());
				buyins.sign_date.setValue(emp.sign_date.getValue());
				buyins.expired_date.setValue(emp.expired_date.getValue());
				buyins.birthday.setValue(emp.birthday.getValue());
				buyins.age.setValue(tempbil.age.getValue());
				buyins.sptype.setValue(tempbil.sptype.getValue());

				buyins.isnew.setValue(tempbil.isnew.getValue());
				buyins.reg_type.setValue(tempbil.reg_type.getValue());
				buyins.buydday.setValue(bi.buydday.getValue());
				buyins.insit_id.setValue(bi.insit_id.getValue());
				buyins.insit_code.setValue(bi.insit_code.getValue());
				buyins.insname.setValue(bi.insname.getValue());
				buyins.ins_type.setValue(bi.ins_type.getValue());
				buyins.selfratio.setValue(bi.selfratio.getValue());
				buyins.selfpay.setValue(bi.selfpay.getValue());
				buyins.tselfpay.setValue(bi.selfpay.getValue());
				buyins.comratio.setValue(bi.comratio.getValue());
				buyins.compay.setValue(bi.compay.getValue());
				buyins.tcompay.setValue(bi.compay.getValue());
				buyins.insurance_number.setValue(bi.insurance_number.getValue());
				buyins.insurancebase.setValue(bi.insurancebase.getValue());
				buyins.payment.setValue(tempbil.lpayment.getValue());
				buyins.remark.setValue(bi.remark.getValue());
				newbis.add(buyins);
			}
		}
		if (newbis.size() > 0) {
			System.out.println("====================导入购保单条数【" + newbis.size() + "】");
			// System.out.println("+++++++++++++++++++++++++++"+listOfString.toString());
			newbis.saveBatchSimple();// 高速存储
		}
		newbis.clear();
		return "{\"result\":\"OK\"}";

	}

	@ACOAction(eventname = "findprebuyinslist", Authentication = true, notes = "获取当月预购保名单")
	public String findprebuyinslist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		// JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		String reloper = jporgcode.getReloper();
		Date nowdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd());// 获取当前日期
		Date bgdate = Systemdate.getFirstAndLastOfMonth(nowdate).date1;// 获取本月第一天日期
		Date eddate = Systemdate.getFirstAndLastOfMonth(nowdate).date2;// 获取本月最后一天日期

		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");

		String[] notnull = {};
		String sqlstr = " SELECT pbi.* FROM hr_ins_prebuyins pbi,hr_employee e WHERE pbi.isbuyins=2 AND pbi.dobuyinsdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND pbi.dobuyinsdate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' and pbi.idpath ";
		if (reloper.equals("<>")) {
			sqlstr = sqlstr + " not ";
		}
		sqlstr = sqlstr + " like '" + org.idpath.getValue() +
				"%' and pbi.er_id=e.er_id AND e.empstatid<12 order by pbi.dobuyinsdate ";
		String[] ignParms = { "orgcode" };// 忽略的查询条件
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	private void dofindinsdetail(JSONArray datas, Date bgdate, Date eddate) throws Exception {
		for (int i = 0; i < datas.size(); i++) {
			// boolean includechld = (i != 0);
			boolean includechld = true;
			JSONObject dw = datas.getJSONObject(i);

			String sqlstr = "SELECT bi.buydday,SUM(bi.tselfpay) AS tsp,SUM(bi.tcompay) AS tcp,SUM(bi.payment) AS tpm,COUNT(*) AS tnum " +
					" FROM hr_ins_buyinsurance bi WHERE bi.stat=9 ";
			sqlstr = sqlstr + " and bi.buydday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
					+ "' and bi.buydday<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
			if (includechld) {
				sqlstr = sqlstr + " and bi.idpath like '%" + dw.getString("idpath") + "%'";
			} else {
				sqlstr = sqlstr + " and bi.orgid=" + dw.getString("orgid");
			}
			sqlstr = sqlstr + " GROUP BY bi.buydday";
			List<HashMap<String, String>> rsts = DBPools.defaultPool().openSql2List(sqlstr);
			dw.put("tsp", rsts.get(0).get("tsp"));
			dw.put("tcp", rsts.get(0).get("tcp"));
			dw.put("tpm", rsts.get(0).get("tpm"));
			dw.put("tnum", rsts.get(0).get("tnum"));
			dw.put("buydday", rsts.get(0).get("buydday"));
		}
	}

	@ACOAction(eventname = "findnotprebuyinslist", Authentication = true, notes = "获取当月未生成预购保名单")
	public String findnotprebuyinslist() throws Exception {
		/*
		 * HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		 * List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		 * JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		 * // JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		 * if (jporgcode == null)
		 * throw new Exception("需要参数【orgcode】");
		 * String orgcode = jporgcode.getParmvalue();
		 */

		Date nowdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd());// 获取当前日期
		Date bgdate = Systemdate.getFirstAndLastOfMonth(nowdate).date1;// 获取本月第一天日期
		Date eddate = Systemdate.getFirstAndLastOfMonth(nowdate).date2;// 获取本月最后一天日期

		/*
		 * String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		 * Shworg org = new Shworg();
		 * org.findBySQL(sqlstr1);
		 * if (org.isEmpty())
		 * throw new Exception("编码为【" + orgcode + "】的机构不存在");
		 */

		String[] notnull = {};
		String sqlstr = " SELECT * FROM `hr_employee` WHERE hiredday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND hiredday<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' AND empstatid<10 " +
				" AND (lv_num<=6.3 OR registertype=1 OR registertype=2) AND er_id NOT IN " +
				"(SELECT er_id FROM hr_ins_buyinsurance WHERE stat=9  AND '" + Systemdate.getStrDateyyyy_mm_dd(eddate) +
				"'>=buydday AND '" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "'<=buydday) AND er_id NOT IN " +
				"(SELECT er_id FROM hr_ins_prebuyins pbi WHERE isbuyins=2 AND pbi.dobuyinsdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) +
				"' AND pbi.dobuyinsdate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "')  ORDER BY hiredday ";
		String[] ignParms = { "orgcode" };// 忽略的查询条件
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	// @ACOAction(eventname = "dosetprebuyinslist", Authentication = true, notes = "未预购保名单插入预购保单")
	private void dosetprebuyinslist(Date bgdate, Date eddate) throws Exception {
		// Date nowdate= Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd());//获取当前日期
		// Date bgdate = Systemdate.getFirstAndLastOfMonth(nowdate).date1;// 获取本月第一天日期
		// Date eddate = Systemdate.getFirstAndLastOfMonth(nowdate).date2;// 获取本月最后一天日期
		CJPALineData<Hr_employee> emps = new CJPALineData<Hr_employee>(Hr_employee.class);
		String sqlstr = " SELECT * FROM `hr_employee` WHERE hiredday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND hiredday<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' AND empstatid<10 " +
				" AND (lv_num<=6.3 OR registertype=1 OR registertype=2) AND er_id NOT IN " +
				"(SELECT er_id FROM hr_ins_buyinsurance WHERE stat=9  AND '" + Systemdate.getStrDateyyyy_mm_dd(eddate) +
				"'>=buydday AND '" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "'<=buydday) AND er_id NOT IN " +
				"(SELECT er_id FROM hr_ins_prebuyins pbi WHERE isbuyins=2 AND pbi.dobuyinsdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) +
				"' AND pbi.dobuyinsdate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "')  ORDER BY hiredday ";
		emps.findDataBySQL(sqlstr, true, false);
		Hr_employee tempemp = new Hr_employee();
		CDBConnection con = tempemp.pool.getCon(this);
		con.startTrans();
		try {
			for (CJPABase jpa : emps) {
				Hr_employee emp = (Hr_employee) jpa;
				Hr_ins_prebuyins pbi = new Hr_ins_prebuyins();
				pbi.er_id.setValue(emp.er_id.getValue());
				pbi.employee_code.setValue(emp.employee_code.getValue());
				pbi.employee_name.setValue(emp.employee_name.getValue());
				pbi.ospid.setValue(emp.ospid.getValue());
				pbi.ospcode.setValue(emp.ospcode.getValue());
				pbi.sp_name.setValue(emp.sp_name.getValue());
				pbi.lv_id.setValue(emp.lv_id.getValue());
				pbi.lv_num.setValue(emp.lv_num.getValue());
				pbi.hiredday.setValue(emp.hiredday.getValue());
				pbi.orgid.setValue(emp.orgid.getValue());
				pbi.orgcode.setValue(emp.orgcode.getValue());
				pbi.orgname.setValue(emp.orgname.getValue());
				pbi.sex.setValue(emp.sex.getValue());
				pbi.birthday.setValue(emp.birthday.getValue());
				pbi.registertype.setValue(emp.registertype.getValue());
				pbi.idpath.setValue(emp.idpath.getValue());
				pbi.degree.setValue(emp.degree.getValue());
				pbi.nativeplace.setValue(emp.nativeplace.getValue());
				pbi.id_number.setValue(emp.id_number.getValue());
				pbi.registeraddress.setValue(emp.registeraddress.getValue());
				pbi.telphone.setValue(emp.telphone.getValue());
				pbi.transorg.setValue(emp.transorg.getValue());
				pbi.dispunit.setValue(emp.dispunit.getValue());
				int age = getAgeByBirth(emp.birthday.getValue());
				pbi.age.setAsInt(age);
				pbi.tranfcmpdate.setValue(emp.hiredday.getValue());
				pbi.isbuyins.setAsInt(2);
				Calendar hday = Calendar.getInstance();
				Date hd = Systemdate.getDateByStr(emp.hiredday.getValue());
				hday.setTime(hd);
				String nowtime = "";
				// Date ndate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd());
				int inday = hday.get(Calendar.DATE);
				if (inday <= 25) {
					Date firstdate = Systemdate.getFirstAndLastOfMonth(hd).date1;
					nowtime = Systemdate.getStrDateyyyy_mm_dd(firstdate);
				} else {
					Date lastdaydate = Systemdate.getFirstAndLastOfMonth(hd).date2;// 取最后一天的日期
					Date nextdaydate = Systemdate.dateDayAdd(lastdaydate, 1);// 加一天获取下月一号的日期
					nowtime = Systemdate.getStrDateyyyy_mm_dd(nextdaydate);
				}
				pbi.dobuyinsdate.setValue(nowtime);
				pbi.save(con);
			}
			con.submit();
			// JSONObject rst = new JSONObject();
			// rst.put("rst", "ok");
			// return rst.toString();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

}
