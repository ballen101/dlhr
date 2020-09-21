package com.hr.salary.co;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
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
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.attd.co.COreport2;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_standposition;
import com.hr.insurance.entity.Hr_ins_buyins_line;
import com.hr.insurance.entity.Hr_ins_insurancetype;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_empptjob_app;
import com.hr.salary.ctr.CtrSalaryCommon;
import com.hr.salary.entity.Hr_salary_chglg;
import com.hr.salary.entity.Hr_salary_chglgsubmit_line;
import com.hr.salary.entity.Hr_salary_hotsub_line;
import com.hr.salary.entity.Hr_salary_hotsub_qual;
import com.hr.salary.entity.Hr_salary_hotsub_qual_line;
import com.hr.salary.entity.Hr_salary_linestarget_line;
import com.hr.salary.entity.Hr_salary_orgminstandard;
import com.hr.salary.entity.Hr_salary_positionwage;
import com.hr.salary.entity.Hr_salary_postsub_line;
import com.hr.salary.entity.Hr_salary_specchgsa_batch_line;
import com.hr.salary.entity.Hr_salary_structure;
import com.hr.salary.entity.Hr_salary_teamawardsubmit_line;
import com.hr.salary.entity.Hr_salary_techsub_line;
import com.hr.salary.entity.Hr_salary_yearraise_line;
import com.hr.salary.entity.Hr_salary_yearraise_quota;
import com.hr.util.HRUtil;

@ACO(coname = "web.hrsalary.command")
public class COHr_salary {
	@ACOAction(eventname = "imppostwagesexcel", Authentication = true, ispublic = false, notes = "导入职位工资标准")
	public String imppostwagesexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_salary_postwg(p, batchno);
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

	private int parserExcelFile_salary_postwg(Shw_physic_file pf, String batchno) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_salary_postwg(aSheet, batchno);
	}

	private int parserExcelSheet_salary_postwg(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_salary_list();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);

		CJPALineData<Hr_salary_positionwage> newpss = new CJPALineData<Hr_salary_positionwage>(Hr_salary_positionwage.class);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		int rst = 0;
		for (Map<String, String> v : values) {
			String spcode = v.get("ospcode");
			if ((spcode == null) || (spcode.isEmpty()))
				throw new Exception("职位工资标准中的职位编码不能为空");

			Hr_standposition sp = new Hr_standposition();
			sp.findBySQL("SELECT * FROM hr_standposition WHERE usable=1 AND sp_code='" + spcode + "'");
			if (sp.isEmpty())
				throw new Exception("编码为【" + spcode + "】的职位不存在");

			Hr_salary_positionwage sapost = new Hr_salary_positionwage();
			sapost.remark.setValue(v.get("remark")); // 备注
			sapost.ptwg_name.setValue(v.get("ptwg_name")); // 名称
			sapost.ospid.setValue(sp.sp_id.getValue()); // 标准职位id
			sapost.ospcode.setValue(sp.sp_code.getValue()); // 标准职位编码
			sapost.sp_name.setValue(sp.sp_name.getValue()); // 标准职位名称
			sapost.lv_id.setValue(sp.lv_id.getValue()); // 职级id
			sapost.lv_num.setValue(sp.lv_num.getValue()); // 职级
			sapost.hg_id.setValue(sp.hg_id.getValue()); // 职等id
			sapost.hg_name.setValue(sp.hg_name.getValue()); // 职等
			sapost.hwc_idzl.setValue(sp.hwc_idzl.getValue()); // 职类id
			sapost.hwc_namezl.setValue(sp.hwc_namezl.getValue()); // 职类
			sapost.hwc_idzq.setValue(sp.hwc_idzq.getValue()); // 职群id
			sapost.hwc_namezq.setValue(sp.hwc_namezq.getValue()); // 职群
			sapost.hwc_idzz.setValue(sp.hwc_idzz.getValue()); // 职种id
			sapost.hwc_namezz.setValue(sp.hwc_namezz.getValue()); // 职种
			sapost.psl1.setValue(v.get("psl1")); // S1
			sapost.psl2.setValue(v.get("psl2")); // S2
			sapost.psl3.setValue(v.get("psl3")); // S3
			sapost.psl4.setValue(v.get("psl4")); // S4
			sapost.psl5.setValue(v.get("psl5")); // S5
			sapost.usable.setAsInt(1);// 有效
			newpss.add(sapost);
			rst++;
		}
		if (newpss.size() > 0) {
			System.out.println("====================导入职位工资标准条数【" + newpss.size() + "】");
			newpss.saveBatchSimple();// 高速存储
		}
		newpss.clear();
		return rst;
	}

	private List<CExcelField> initExcelFields_salary_list() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("名称", "ptwg_name", true));
		efields.add(new CExcelField("职位编码", "ospcode", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("职类", "hwc_namezl", true));
		efields.add(new CExcelField("职群", "hwc_namezq", true));
		efields.add(new CExcelField("职种", "hwc_namezz", true));
		efields.add(new CExcelField("S1", "psl1", true));
		efields.add(new CExcelField("S2", "psl2", true));
		efields.add(new CExcelField("S3", "psl3", true));
		efields.add(new CExcelField("S4", "psl4", true));
		efields.add(new CExcelField("S5", "psl5", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "getHotSubsPost", Authentication = true, ispublic = true, notes = "获取高温补贴资格职位")
	public String getHotSubsPost() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT hsq.*,hsql.sp_id,hsql.sp_code,hsql.sp_name,hsql.substand FROM hr_salary_hotsub_qual hsq,hr_salary_hotsub_qual_line hsql WHERE hsql.hsq_id=hsq.hsq_id AND hsq.stat=9";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getHotSubsEmps", Authentication = true, ispublic = true, notes = "获取申请高温补贴资格内人事档案")
	public String getHotSubsEmps() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONParm jpemployee_code = CjpaUtil.getParm(jps, "employee_code");
		JSONParm jpyearmonth = CjpaUtil.getParm(jps, "yearmonth");
		if (jpyearmonth == null)
			throw new Exception("需要参数【yearmonth】");
		String ym = jpyearmonth.getParmvalue();
		String yearmonth = ym + "-01";
		Date month = Systemdate.getDateByStr(yearmonth);
		Date nextmonth = Systemdate.dateMonthAdd(month, 1);
		String nm = Systemdate.getStrDateByFmt(nextmonth, "yyyy-MM-dd");
		String[] ignParms = { "orgcode", "yearmonth", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT emp.*,ops.substand FROM (SELECT * FROM `hr_month_employee` WHERE yearmonth='" + ym + "' AND idpath LIKE '" + org.idpath.getValue() + "%' and hiredday<'" + nm + "' ";
		if (jpemployee_code != null) {
			String empcode = jpemployee_code.getParmvalue();
			sqlstr = sqlstr + " and employee_code='" + empcode + "' ";
		}
		sqlstr=sqlstr+") emp,(SELECT op.*,hsqs.substand FROM hr_orgposition op,"+
				"( SELECT hsql.sp_id,hsql.sp_code,hsql.sp_name,hsql.substand,hsq.idpath FROM hr_salary_hotsub_qual hsq,hr_salary_hotsub_qual_line hsql "+
				"WHERE hsql.hsq_id=hsq.hsq_id AND hsq.stat=9  AND LOCATE('"+org.idpath.getValue()+"',hsq.idpath)=1 "+
				" AND hsq.startdate<='"+yearmonth+"' AND DATE_ADD(DATE_FORMAT(hsql.canceldate,'%Y-%m-01'),INTERVAL 1 MONTH)>'"+yearmonth+"'";
		if(org.orgid.getAsInt()==1){
			sqlstr=sqlstr+" and idpath like '"+org.idpath.getValue()+"' ";
		}
		sqlstr = sqlstr + ") hsqs " +
				" WHERE op.sp_id=hsqs.sp_id AND LOCATE(hsqs.idpath,op.idpath)=1) ops WHERE emp.ospid=ops.ospid  ";
		JSONObject empjo = new CReport(sqlstr, notnull).findReport2JSON_O(ignParms);
		JSONArray tememps = empjo.getJSONArray("rows");
		if (tememps.size() > 0) {
			sqlstr = "SELECT emp.*,ops.substand FROM (SELECT * FROM hr_employee WHERE empstatid>=12  AND idpath LIKE '" + org.idpath.getValue() + "%' and hiredday<'" + nm + "' ";
			if (jpemployee_code != null) {
				String empcode = jpemployee_code.getParmvalue();
				sqlstr = sqlstr + " and employee_code='" + empcode + "' ";
			}
			if (org.orgid.getAsInt() == 1) {
				sqlstr = sqlstr + " and idpath like '" + org.idpath.getValue() + "' ";
			}
			sqlstr=sqlstr+") emp,(SELECT op.*,hsqs.substand FROM hr_orgposition op,"+
					"( SELECT hsql.sp_id,hsql.sp_code,hsql.sp_name,hsql.substand,hsq.idpath FROM hr_salary_hotsub_qual hsq,hr_salary_hotsub_qual_line hsql "+
					"WHERE hsql.hsq_id=hsq.hsq_id AND hsq.stat=9  AND LOCATE('"+org.idpath.getValue()+"',hsq.idpath)=1 "+
					" AND hsq.startdate<='"+ym+"' AND DATE_ADD(DATE_FORMAT(hsql.canceldate,'%Y-%m-01'),INTERVAL 1 MONTH)>'"+ym+"'";
			sqlstr=sqlstr+") hsqs "+
					" WHERE op.sp_id=hsqs.sp_id AND LOCATE(hsqs.idpath,op.idpath)=1) ops WHERE emp.ospid=ops.ospid";
			JSONObject ljempjo = new CReport(sqlstr, notnull).findReport2JSON_O(ignParms);
			JSONArray templjemps = ljempjo.getJSONArray("rows");
			tememps.addAll(templjemps);
			JSONObject jo = new JSONObject();
			jo.put("rows", tememps);
			String scols = urlparms.get("cols");
			if (scols == null) {
				return jo.toString();
			} else {
				(new CReport()).export2excel(tememps, scols);
				return null;
			}
		}else{
			sqlstr="SELECT emp.*,ops.substand FROM (SELECT * FROM hr_employee WHERE (empstatid>0 AND empstatid<=10 "+
					" OR (empstatid>=12 AND ljdate>='"+yearmonth+"' AND ljdate<'"+nm+"')) AND idpath LIKE '"+org.idpath.getValue()+"%' and hiredday<'"+nm+"' ";
			if (jpemployee_code != null){
				String empcode=jpemployee_code.getParmvalue();
				sqlstr=sqlstr+" and employee_code='"+empcode+"' ";
			}
			sqlstr=sqlstr+") emp,(SELECT op.*,hsqs.substand FROM hr_orgposition op,"+
					"( SELECT hsql.sp_id,hsql.sp_code,hsql.sp_name,hsql.substand,hsq.idpath FROM hr_salary_hotsub_qual hsq,hr_salary_hotsub_qual_line hsql "+
					"WHERE hsql.hsq_id=hsq.hsq_id AND hsq.stat=9  AND LOCATE('"+org.idpath.getValue()+"',hsq.idpath)=1 "+
					" AND hsq.startdate<='"+yearmonth+"' AND DATE_ADD(DATE_FORMAT(hsql.canceldate,'%Y-%m-01'),INTERVAL 1 MONTH)>'"+yearmonth+"'";
			if(org.orgid.getAsInt()==1){
				sqlstr=sqlstr+" and idpath like '"+org.idpath.getValue()+"' ";
			}
			sqlstr=sqlstr+") hsqs "+
					" WHERE op.sp_id=hsqs.sp_id AND LOCATE(hsqs.idpath,op.idpath)=1) ops WHERE emp.ospid=ops.ospid   ";
			return new CReport(sqlstr, notnull).findReport(ignParms,null);
		}

	}

	@ACOAction(eventname = "getHotSubNotQualEmps", Authentication = true, ispublic = true, notes = "获取高温补贴资格外人事档案")
	public String getHotSubNotQualEmps() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT * FROM hr_employee WHERE empstatid>0 AND empstatid<=10  AND idpath LIKE '" + org.idpath.getValue() +
				"%' AND ospid NOT IN (SELECT op.ospid FROM hr_orgposition op, ( SELECT hsql.* FROM hr_salary_hotsub_qual hsq,hr_salary_hotsub_qual_line hsql " +
				" WHERE hsql.hsq_id=hsq.hsq_id AND hsq.stat=9 AND hsq.usable=1 AND hsql.usable=1 AND hsq.idpath LIKE '" + org.idpath.getValue() + "%' ) hsqs  " +
				" WHERE op.sp_id=hsqs.sp_id AND op.idpath LIKE '" + org.idpath.getValue() + "%') ";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getEmpKQDetial", Authentication = true, ispublic = true, notes = "获取人员应出满勤与实际考勤")
	public String getEmpKQDetial() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		String yearmonth = CorUtil.hashMap2Str(parms, "applydate", "需要参数applydate");
		JSONObject jo = COreport2.getYCMQSJCQ(yearmonth, er_id);
		return jo.toString();
	}

	@ACOAction(eventname = "getTechSubsEmps", Authentication = true, ispublic = true, notes = "获取申请技术补贴人事档案")
	public String getTechSubsEmps() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jpsys = CjpaUtil.getParm(jps, "appsys");
		if (jpsys == null)
			throw new Exception("需要参数【appsys】");
		String appsys = jpsys.getParmvalue();
		String[] ignParms = { "appsys" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT * FROM hr_employee e WHERE e.empstatid>0 AND e.empstatid<=10 AND e.ospid  " +
				"IN (SELECT op.ospid FROM hr_orgposition op ,hr_salary_techsub_post tsp " +
				" WHERE tsp.stat=9 AND  tsp.usable=1  AND op.sp_id=tsp.sp_id AND tsp.appsys=" + appsys + " )";
		if (!HRUtil.hasRoles("71")) {// 薪酬管理员
			sqlstr = sqlstr + " and employee_code='" + CSContext.getUserName() + "'";
		}
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getEmpSalaryList", Authentication = true, ispublic = true, notes = "获取人员工资明细")
	public String getEmpSalaryList() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		// String yearmonth = CorUtil.hashMap2Str(parms, "applydate", "需要参数applydate");
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT * FROM `hr_salary_list` WHERE wagetype=1 AND er_id=" + er_id + " ORDER BY yearmonth DESC LIMIT 0,1";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "impscsbatch_listexcel", Authentication = true, ispublic = false, notes = "导入批量特殊调薪明细Excel")
	public String impscsbatch_listexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_scsblist(p, orgid);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_scsblist(Shw_physic_file pf, String orgid) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_scsblist(aSheet, orgid);
	}

	private String parserExcelSheet_scsblist(Sheet aSheet, String orgid) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_scsblist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}

		Hr_employee emp = new Hr_employee();
		CJPALineData<Hr_salary_specchgsa_batch_line> scsbls = new CJPALineData<Hr_salary_specchgsa_batch_line>(Hr_salary_specchgsa_batch_line.class);
		Shworg emporg = new Shworg();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("明细行上的工号不能为空");

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
			Hr_salary_specchgsa_batch_line scsbl = new Hr_salary_specchgsa_batch_line();

			scsbl.er_id.setValue(emp.er_id.getValue());
			scsbl.employee_code.setValue(emp.employee_code.getValue());
			scsbl.employee_name.setValue(emp.employee_name.getValue());
			scsbl.ospid.setValue(emp.ospid.getValue());
			scsbl.ospcode.setValue(emp.ospcode.getValue());
			scsbl.sp_name.setValue(emp.sp_name.getValue());
			scsbl.lv_id.setValue(emp.lv_id.getValue());
			scsbl.lv_num.setValue(emp.lv_num.getValue());
			scsbl.hiredday.setValue(emp.hiredday.getValue());
			scsbl.orgid.setValue(emp.orgid.getValue());
			scsbl.orgcode.setValue(emp.orgcode.getValue());
			scsbl.orgname.setValue(emp.orgname.getValue());
			scsbl.idpath.setValue(emp.idpath.getValue());
			scsbl.orghrlev.setValue(0);
			Hr_salary_chglg sc = CtrSalaryCommon.getCur_salary_chglg(emp.er_id.getValue());
			DecimalFormat df = new DecimalFormat("0.00");
			scsbl.oldstru_id.setValue(sc.newstru_id.getValue()); // 调薪前工资结构ID
			scsbl.oldstru_name.setValue(sc.newstru_name.getValue()); // 调薪前工资结构名
			scsbl.oldchecklev.setValue(sc.newchecklev.getValue());// 调薪前绩效考核层级
			scsbl.oldattendtype.setValue(sc.newattendtype.getValue()); // 调薪前出勤类别
			scsbl.oldposition_salary.setValue(df.format(sc.newposition_salary.getAsFloat())); // 调薪前职位工资
			scsbl.oldbase_salary.setValue(df.format(sc.newbase_salary.getAsFloat())); // 调薪前基本工资
			scsbl.oldtech_salary.setValue(df.format(sc.newtech_salary.getAsFloat())); // 调薪前技能工资
			scsbl.oldachi_salary.setValue(df.format(sc.newachi_salary.getAsFloat())); // 调薪前绩效工资
			scsbl.oldotwage.setValue(df.format(sc.newotwage.getAsFloat())); // 调薪前固定加班工资
			scsbl.oldtech_allowance.setValue(df.format(sc.newtech_allowance.getAsFloat())); // 调薪前技术津贴

			float newposition_salary = Float.parseFloat(v.get("newposition_salary"));
			float newtech_allowance = 0;
			String nta = v.get("newtech_allowance");
			if ((nta == null) || (nta.isEmpty())) {
				if ((sc.newtech_allowance.isEmpty() || (sc.newtech_allowance.getAsFloat() == 0))) {
					newtech_allowance = 0;
				} else {
					newtech_allowance = sc.newtech_allowance.getAsFloat();
				}

			} else {
				newtech_allowance = Float.parseFloat(nta);
			}
			String newstru = v.get("newstru_name");
			Hr_salary_structure ss = new Hr_salary_structure();
			String stru = "";
			if (sc.newstru_name.isEmpty()) {
				stru = newstru;
			} else {
				stru = sc.newstru_name.getValue();
			}
			String sqlstr = "select * from hr_salary_structure where stat=9 and stru_name='" + stru + "'";
			ss.findBySQL(sqlstr);
			if (ss.isEmpty()) {
				throw new Exception("工号为【" + employee_code + "】的工资结构【" + stru + "】不存在！");
			}
			float chgbase_salary=0;
			float chgtech_salary=0;
			float chgachi_salary=0;
			float chgotwage=0;
			if(ss.strutype.getAsInt()==1){
				float minstand=0;
				String minsqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+emp.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC  ";
				Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
				oms.findBySQL(minsqlstr);
				if(oms.isEmpty()){
					minstand=0;
				}else{
					minstand=oms.minstandard.getAsFloatDefault(0);
				}
				float bw=(newposition_salary*ss.basewage.getAsFloatDefault(0))/100;
				float bow=(newposition_salary*ss.otwage.getAsFloatDefault(0))/100;
				float sw=(newposition_salary*ss.skillwage.getAsFloatDefault(0))/100;
				float pw=(newposition_salary*ss.meritwage.getAsFloatDefault(0))/100;
				if(minstand>bw){
					if((bw+bow)>minstand){
						bow=bw+bow-minstand;
						bw=minstand;
					}else if((bw+bow+sw)>minstand){
						sw=bw+bow+sw-minstand;
						bow=0;
						bw=minstand;
					}else if((bw+bow+sw+pw)>minstand){
						pw=bw+bow+sw+pw-minstand;
						sw=0;
						bow=0;
						bw=minstand;
					}else{
						bw=newposition_salary;
						pw=0;
						sw=0;
						bow=0;
					}
				}
				scsbl.newbase_salary.setValue(df.format(bw)); // 调薪后基本工资
				scsbl.newtech_salary.setValue(df.format(sw)); // 调薪后技能工资
				scsbl.newachi_salary.setValue(df.format(pw)); // 调薪后绩效工资
				scsbl.newotwage.setValue(df.format(bow)); // 调薪后固定加班工资
				chgbase_salary=bw-sc.newbase_salary.getAsFloatDefault(0);
				chgtech_salary=sw-sc.newtech_salary.getAsFloatDefault(0);
				chgachi_salary=pw-sc.newachi_salary.getAsFloatDefault(0);
				chgotwage=bow-sc.newotwage.getAsFloatDefault(0);
			}else{
				scsbl.newbase_salary.setValue(v.get("newbase_salary")); // 调薪后基本工资
				scsbl.newtech_salary.setValue(v.get("newtech_salary")); // 调薪后技能工资
				scsbl.newachi_salary.setValue(v.get("newachi_salary")); // 调薪后绩效工资
				scsbl.newotwage.setValue(v.get("newotwage")); // 调薪后固定加班工资
				String nbs=v.get("newbase_salary");
				if((nbs == null) || (nbs.isEmpty())){
					nbs="0";
				}
				String nts=v.get("newtech_salary");
				if((nts == null) || (nts.isEmpty())){
					nts="0";
				}
				String nas=v.get("newachi_salary");
				if((nas == null) || (nas.isEmpty())){
					nas="0";
				}
				String notw=v.get("newotwage");
				if((notw == null) || (notw.isEmpty())){
					notw="0";
				}
				chgbase_salary=Float.valueOf(nbs)-sc.newbase_salary.getAsFloatDefault(0);
				chgtech_salary=Float.valueOf(nts)-sc.newtech_salary.getAsFloatDefault(0);
				chgachi_salary=Float.valueOf(nas)-sc.newachi_salary.getAsFloatDefault(0);
				chgotwage=Float.valueOf(notw)-sc.newotwage.getAsFloatDefault(0);
			}
			scsbl.newstru_id.setValue(ss.stru_id.getValue()); // 调薪后工资结构ID
			scsbl.newstru_name.setValue(ss.stru_name.getValue()); // 调薪后工资结构名
			scsbl.newchecklev.setValue(ss.checklev.getValue());// 调薪后绩效考核层级
			scsbl.newattendtype.setValue(ss.kqtype.getValue()); // 调薪后出勤类别
			scsbl.newposition_salary.setValue(df.format(newposition_salary)); // 调薪后职位工资
			scsbl.newtech_allowance.setValue(df.format(newtech_allowance)); // 调薪后技术津贴
			float chgposition_salary=newposition_salary-sc.newposition_salary.getAsFloatDefault(0);
			float chgtech_allowance=newtech_allowance-sc.newtech_allowance.getAsFloatDefault(0);
			float pbtsarylev=chgposition_salary+chgtech_allowance;
			float pbtsarylev_chgtech=0;
			if((pbtsarylev==0)||(sc.newposition_salary.getAsFloatDefault(0)==0)){
				pbtsarylev_chgtech=0;
			}else{
				pbtsarylev_chgtech=(pbtsarylev/sc.newposition_salary.getAsFloatDefault(0))*100;
			}
			scsbl.chgposition_salary.setValue(chgposition_salary); // 职位工资调薪幅度
			scsbl.chgbase_salary.setValue(chgbase_salary); // 基本工资调薪幅度
			scsbl.chgtech_salary.setValue(chgtech_salary); // 技能工资调薪幅度
			scsbl.chgachi_salary.setValue(chgachi_salary);// 绩效工资调薪幅度
			scsbl.chgotwage.setValue(chgotwage);// 基本加班工资调薪幅度
			scsbl.chgtech_allowance.setValue(chgtech_allowance);// 技术津贴调薪幅度
			scsbl.pbtsarylev.setValue(df.format(pbtsarylev));// 调薪金额
			scsbl.overf_salary.setValue(0);// 超标金额
			scsbl.overf_salary_chgtech.setValue(0);// 超标比例
			scsbl.pbtsarylev_chgtech.setValue(df.format(pbtsarylev_chgtech));// 调整幅度
			scsbl.remark.setValue(v.get("remark"));
			scsbls.add(scsbl);
		}
		return scsbls.tojson();

	}

	private List<CExcelField> initExcelFields_scsblist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("调薪后工资结构", "newstru_name", true));
		efields.add(new CExcelField("调薪后职位工资", "newposition_salary", true));
		efields.add(new CExcelField("调薪后技术津贴", "newtech_allowance", true));
		efields.add(new CExcelField("调薪后基本工资", "newbase_salary", false));
		efields.add(new CExcelField("调薪后固定加班工资", "newotwage", false));
		efields.add(new CExcelField("调薪后技能工资", "newtech_salary", false));
		efields.add(new CExcelField("调薪后绩效工资", "newachi_salary", false));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "getPostWage", Authentication = true, ispublic = true, notes = "获取指定职位工资标准")
	public String getPostWage() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String ospid = CorUtil.hashMap2Str(parms, "ospid", "需要参数ospid");
		JSONObject result = new JSONObject();
		if (!HRUtil.hasRoles("71")) {// 薪酬管理员
			result.put("accessed", 2);
			return result.toString();
		}
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT pw.* FROM `hr_salary_positionwage` pw,`hr_orgposition` op WHERE pw.stat=9 AND pw.usable=1 AND pw.ospid=op.sp_id AND op.ospid=" + ospid;
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getOrgAvgSalarys", Authentication = true, ispublic = true, notes = "获取机构工资水平")
	public String getOrgAvgSalarys() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("id为【" + orgid + "】的机构不存在");
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT * FROM `hr_salary_parms` WHERE stat=9 AND usable=1 AND idpath LIKE '" + org.idpath.getValue() + "%'";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getLastYearSalaryChglg", Authentication = true, notes = "获取上一年的调薪信息")
	public String getLastYearSalaryChglg() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String er_id = CorUtil.hashMap2Str(urlparms, "er_id", "需要参数er_id");
		String salarydate = CorUtil.hashMap2Str(urlparms, "salarydate", "需要参数salarydate");
		//// 检查权限
		JSONObject jo = new JSONObject();
		jo = getLastYearChgSa(er_id,salarydate, jo);
		return jo.toString();
	}

	private JSONObject getLastYearChgSa(String er_id,String salarydate, JSONObject jo) throws Exception {
		//Date ny = new Date();
		//String nystr = Systemdate.getStrDateByFmt(salarydate, "yyyy-MM");
		String	nystr = salarydate + "-01";
		Date nowyear = Systemdate.getDateByStr(nystr, "yyyy-MM-dd");
		Date lastyear = Systemdate.dateMonthAdd(nowyear, -12);
		Hr_salary_chglg sc = new Hr_salary_chglg();
		String sqlstr = "SELECT SUM(sacrage) AS sacrage,GROUP_CONCAT(DISTINCT DATE_FORMAT(chgdate,'%Y-%m') ORDER BY chgdate) AS chgdates FROM `hr_salary_chglg` WHERE er_id=" + er_id + " AND chgdate>='" + Systemdate.getStrDateyyyy_mm_dd(lastyear) +
				"' AND chgdate<'" + Systemdate.getStrDateyyyy_mm_dd(nowyear);
		String sqlstr1 = sqlstr + "' AND stype=5 GROUP BY er_id";
		JSONArray entrysc = sc.pool.opensql2json_O(sqlstr1);
		if (!entrysc.isEmpty()) {
			JSONObject ent = entrysc.getJSONObject(0);
			jo.put("dateentry", ent.getString("chgdates"));
			jo.put("chgentry", ent.getString("sacrage"));
		} else {
			jo.put("dateentry", "");
			jo.put("chgentry", "");
		}

		String sqlstr2 = sqlstr + "' AND stype=6  GROUP BY er_id";
		JSONArray tfsc = sc.pool.opensql2json_O(sqlstr2);
		if (!tfsc.isEmpty()) {
			JSONObject tf = tfsc.getJSONObject(0);
			jo.put("datetransfer", tf.getString("chgdates"));
			jo.put("chgtransfer", tf.getString("sacrage"));
		} else {
			jo.put("datetransfer", "");
			jo.put("chgtransfer", "");
		}

		String sqlstr3 = sqlstr + "' AND (stype=7 or stype=9) GROUP BY er_id";
		JSONArray spsc = sc.pool.opensql2json_O(sqlstr3);
		if (!spsc.isEmpty()) {
			JSONObject sp = spsc.getJSONObject(0);
			jo.put("datespec", sp.getString("chgdates"));
			jo.put("chgspec", sp.getString("sacrage"));
		} else {
			jo.put("datespec", "");
			jo.put("chgspec", "");
		}
		return jo;
	}

	@ACOAction(eventname = "impyearriase_listexcel", Authentication = true, ispublic = false, notes = "导入年度调薪明细Excel")
	public String impyearriase_listexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		String qutaorgs = CorUtil.hashMap2Str(CSContext.getParms(), "qutaorgs", "需要参数qutaorgs");
		String salarydate = CorUtil.hashMap2Str(CSContext.getParms(), "salarydate", "需要参数salarydate");

		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_yrlist(p, orgid, qutaorgs,salarydate);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_yrlist(Shw_physic_file pf, String orgid, String qutaorgs,String salarydate) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_yrlist(aSheet, orgid, qutaorgs,salarydate);
	}

	private String parserExcelSheet_yrlist(Sheet aSheet, String orgid, String qutaorgs,String salarydate) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_yrlist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}
		String[] orgs = qutaorgs.split(",");
		Shworg qutaorg = new Shworg();
		String where = " and (";
		for (int j = 0; j < orgs.length; j++) {
			qutaorg.clear();
			qutaorg.findByID(orgs[j]);
			if (!qutaorg.isEmpty()) {
				where = where + " (idpath like '" + qutaorg.idpath.getValue() + "%' ) or";
			}
		}
		where = where.substring(0, where.length() - 3);
		where = where + ")";

		Hr_employee emp = new Hr_employee();
		CJPALineData<Hr_salary_yearraise_line> yrls = new CJPALineData<Hr_salary_yearraise_line>(Hr_salary_yearraise_line.class);
		Shworg emporg = new Shworg();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("明细行上的工号不能为空");
			emp.clear();
			String empsqlstr = "SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'" + where;
			emp.findBySQL(empsqlstr);
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料或不属于所选额度机构里");
			emporg.clear();
			emporg.findByID(emp.orgid.getValue());
			if (emporg.isEmpty())
				throw new Exception("没找到员工【" + emp.employee_name.getValue() + "】所属的ID为【" + emp.orgid.getValue() + "】的机构");
			if (emporg.idpath.getValue().indexOf(org.idpath.getValue()) == -1) {
				throw new Exception("员工【" + employee_code + "】不属于此机构");
			}
			Hr_salary_yearraise_line yrl = new Hr_salary_yearraise_line();
			yrl.er_id.setValue(emp.er_id.getValue());
			yrl.employee_code.setValue(emp.employee_code.getValue());
			yrl.employee_name.setValue(emp.employee_name.getValue());
			yrl.ospid.setValue(emp.ospid.getValue());
			yrl.ospcode.setValue(emp.ospcode.getValue());
			yrl.sp_name.setValue(emp.sp_name.getValue());
			yrl.lv_id.setValue(emp.lv_id.getValue());
			yrl.lv_num.setValue(emp.lv_num.getValue());
			yrl.hiredday.setValue(emp.hiredday.getValue());
			yrl.degree.setValue(emp.degree.getValue());
			yrl.orgid.setValue(emp.orgid.getValue());
			yrl.orgcode.setValue(emp.orgcode.getValue());
			yrl.orgname.setValue(emp.orgname.getValue());
			yrl.idpath.setValue(emp.idpath.getValue());
			yrl.orghrlev.setValue(0);
			Hr_salary_chglg sc = CtrSalaryCommon.getCur_salary_chglg(emp.er_id.getValue());
			yrl.oldstru_id.setValue(sc.newstru_id.getValue()); // 调薪前工资结构ID
			yrl.oldstru_name.setValue(sc.newstru_name.getValue()); // 调薪前工资结构名
			yrl.oldchecklev.setValue(sc.newchecklev.getValue());// 调薪前绩效考核层级
			yrl.oldattendtype.setValue(sc.newattendtype.getValue()); // 调薪前出勤类别
			yrl.oldposition_salary.setValue(sc.newposition_salary.getValue()); // 调薪前职位工资
			yrl.oldbase_salary.setValue(sc.newbase_salary.getValue()); // 调薪前基本工资
			yrl.oldtech_salary.setValue(sc.newtech_salary.getValue()); // 调薪前技能工资
			yrl.oldachi_salary.setValue(sc.newachi_salary.getValue()); // 调薪前绩效工资
			yrl.oldotwage.setValue(sc.newotwage.getValue()); // 调薪前固定加班工资
			yrl.oldtech_allowance.setValue(sc.newtech_allowance.getValue()); // 调薪前技术津贴
			String newstru = v.get("newstru_name");
			Hr_salary_structure ss = new Hr_salary_structure();
			String stru = "";
			if (sc.newstru_name.isEmpty()) {
				stru = newstru;
			} else {
				stru = sc.newstru_name.getValue();
			}
			String sqlstr = "select * from hr_salary_structure where stat=9 and stru_name='" + stru + "'";
			ss.findBySQL(sqlstr);
			if (ss.isEmpty()) {
				throw new Exception("工号为【" + employee_code + "】的调薪后工资结构为空！");
			}
			float newposition_salary=Float.parseFloat(v.get("newposition_salary"));
			float chgbase_salary=0;
			float chgtech_salary=0;
			float chgachi_salary=0;
			float chgotwage=0;
			DecimalFormat df=new DecimalFormat(".00");
			if(ss.strutype.getAsInt()==1){
				float minstand=0;
				String minsqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+emp.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC  ";
				Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
				oms.findBySQL(minsqlstr);
				if(oms.isEmpty()){
					minstand=0;
				}else{
					minstand=oms.minstandard.getAsFloatDefault(0);
				}
				float bw=(newposition_salary*ss.basewage.getAsFloatDefault(0))/100;
				float bow=(newposition_salary*ss.otwage.getAsFloatDefault(0))/100;
				float sw=(newposition_salary*ss.skillwage.getAsFloatDefault(0))/100;
				float pw=(newposition_salary*ss.meritwage.getAsFloatDefault(0))/100;
				if(minstand>bw){
					if((bw+bow)>minstand){
						bow=bw+bow-minstand;
						bw=minstand;
					}else if((bw+bow+sw)>minstand){
						sw=bw+bow+sw-minstand;
						bow=0;
						bw=minstand;
					}else if((bw+bow+sw+pw)>minstand){
						pw=bw+bow+sw+pw-minstand;
						sw=0;
						bow=0;
						bw=minstand;
					}else{
						bw=newposition_salary;
						pw=0;
						sw=0;
						bow=0;
					}
				}
				yrl.newbase_salary.setValue(df.format(bw)); // 调薪后基本工资
				yrl.newtech_salary.setValue(df.format(sw)); // 调薪后技能工资
				yrl.newachi_salary.setValue(df.format(pw)); // 调薪后绩效工资
				yrl.newotwage.setValue(df.format(bow)); // 调薪后固定加班工资
				chgbase_salary=bw-sc.newbase_salary.getAsFloatDefault(0);
				chgtech_salary=sw-sc.newtech_salary.getAsFloatDefault(0);
				chgachi_salary=pw-sc.newachi_salary.getAsFloatDefault(0);
				chgotwage=bow-sc.newotwage.getAsFloatDefault(0);
			}else{
				yrl.newbase_salary.setValue(v.get("newbase_salary")); // 调薪后基本工资
				yrl.newtech_salary.setValue(v.get("newtech_salary")); // 调薪后技能工资
				yrl.newachi_salary.setValue(v.get("newachi_salary")); // 调薪后绩效工资
				yrl.newotwage.setValue(v.get("newotwage")); // 调薪后固定加班工资

				String nbs=v.get("newbase_salary");
				if((nbs == null) || (nbs.isEmpty())){
					nbs="0";
				}
				String nts=v.get("newtech_salary");
				if((nts == null) || (nts.isEmpty())){
					nts="0";
				}
				String nas=v.get("newachi_salary");
				if((nas == null) || (nas.isEmpty())){
					nas="0";
				}
				String notw=v.get("newotwage");
				if((notw == null) || (notw.isEmpty())){
					notw="0";
				}
				chgbase_salary=Float.valueOf(nbs)-sc.newbase_salary.getAsFloatDefault(0);
				chgtech_salary=Float.valueOf(nts)-sc.newtech_salary.getAsFloatDefault(0);
				chgachi_salary=Float.valueOf(nas)-sc.newachi_salary.getAsFloatDefault(0);
				chgotwage=Float.valueOf(notw)-sc.newotwage.getAsFloatDefault(0);
			}
			yrl.newstru_id.setValue(ss.stru_id.getValue()); // 调薪后工资结构ID
			yrl.newstru_name.setValue(ss.stru_name.getValue()); // 调薪后工资结构名
			yrl.newchecklev.setValue(ss.checklev.getValue());// 调薪后绩效考核层级
			yrl.newattendtype.setValue(ss.kqtype.getValue()); // 调薪后出勤类别
			yrl.newposition_salary.setValue(newposition_salary); // 调薪后职位工资
			yrl.newtech_allowance.setValue(sc.newtech_allowance.getValue()); // 调薪后技术津贴
			float chgposition_salary=newposition_salary-sc.newposition_salary.getAsFloatDefault(0);
			float chgtech_allowance=0;
			float pbtsarylev=chgposition_salary+chgtech_allowance;
			float pbtsarylev_chgtech=0;
			if((newposition_salary==0)||(sc.newposition_salary.getAsFloatDefault(0)==0)){
				pbtsarylev_chgtech=0;
			}else{
				pbtsarylev_chgtech=(pbtsarylev/(sc.newposition_salary.getAsFloatDefault(0)+sc.newtech_allowance.getAsFloatDefault(0)))*100;
			}
			yrl.chgposition_salary.setValue(chgposition_salary); // 职位工资调薪幅度
			yrl.chgbase_salary.setValue(chgbase_salary); // 基本工资调薪幅度
			yrl.chgtech_salary.setValue(chgtech_salary); // 技能工资调薪幅度
			yrl.chgachi_salary.setValue(chgachi_salary);// 绩效工资调薪幅度
			yrl.chgotwage.setValue(chgotwage);// 基本加班工资调薪幅度
			yrl.chgtech_allowance.setValue(chgtech_allowance);// 技术津贴调薪幅度
			yrl.pbtsarylev.setValue(pbtsarylev);// 调薪金额
			yrl.overf_salary.setValue(0);// 超标金额
			yrl.overf_salary_chgtech.setValue(0);// 超标比例
			yrl.pbtsarylev_chgtech.setValue(df.format(pbtsarylev_chgtech));// 调整幅度
			yrl.remark.setValue(v.get("remark"));
			JSONObject jo = new JSONObject();
			jo = getLastYearChgSa(emp.er_id.getValue(), salarydate,jo);
			yrl.dateentry.setValue(jo.getString("dateentry")); // 上一年入职转正调薪月份
			yrl.chgentry.setValue(jo.getString("chgentry")); // 上一年入职转正调薪
			yrl.datetransfer.setValue(jo.getString("datetransfer")); // 上一年调动转正调薪月份
			yrl.chgtransfer.setValue(jo.getString("chgtransfer")); // 上一年调动转正调薪
			yrl.datespec.setValue(jo.getString("datespec")); // 上一年特殊调薪月份
			yrl.chgspec.setValue(jo.getString("chgspec")); // 上一年特殊调薪
			JSONObject hljo = new JSONObject();
			hljo = LastYearHoliday(emp.er_id.getValue(),salarydate, hljo);
			yrl.lastyqqdays.setValue(hljo.getString("hdays"));// 上一年请假/缺勤天数
			JSONObject expjo = new JSONObject();
			expjo = LastYearEXPoint(emp.er_id.getValue(), salarydate,expjo);
			yrl.lastavgallowance.setValue(expjo.getString("expoint"));// 上一年平均绩效系数
			yrls.add(yrl);
		}
		return yrls.tojson();

	}

	private List<CExcelField> initExcelFields_yrlist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("调薪后工资结构", "newstru_name", true));
		efields.add(new CExcelField("调薪后职位工资", "newposition_salary", true));
		efields.add(new CExcelField("调薪后基本工资", "newbase_salary", false));
		efields.add(new CExcelField("调薪后固定加班工资", "newotwage", false));
		efields.add(new CExcelField("调薪后技能工资", "newtech_salary", false));
		efields.add(new CExcelField("调薪后绩效工资", "newachi_salary", false));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "getSalaryReportSpec", Authentication = true, ispublic = true, notes = "获取特殊调薪报表")
	public String getSalaryReportSpec() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "chgdate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 5, 7);
		String orderstr = " chgdate desc ";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getSalaryReportSpecBatch", Authentication = true, ispublic = true, notes = "获取批量特殊调薪报表")
	public String getSalaryReportSpecBatch() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "chgdate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 5, 9);
		String orderstr = " chgdate desc ";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getSalaryReportEntry", Authentication = true, ispublic = true, notes = "获取入职定薪报表")
	public String getSalaryReportEntry() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "chgdate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 1, 0);
		String orderstr = " chgdate desc ";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getSalaryReportTransf", Authentication = true, ispublic = true, notes = "获取调动核薪报表")
	public String getSalaryReportTransf() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "chgdate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 2, 0);
		String orderstr = " chgdate desc ";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getSalaryReportEntryProb", Authentication = true, ispublic = true, notes = "获取入职转正报表")
	public String getSalaryReportEntryProb() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "chgdate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 3, 5);
		String orderstr = " pbtdate desc ";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getSalaryReportTransfProb", Authentication = true, ispublic = true, notes = "获取调动转正报表")
	public String getSalaryReportTransfProb() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "chgdate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 3, 6);
		String orderstr = " pbtdate desc ";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getSalaryReportYearRaise", Authentication = true, ispublic = true, notes = "获取年度调薪报表")
	public String getSalaryReportYearRaise() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "chgdate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 4, 0);
		String orderstr = " idpath desc";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getSalaryReportTechSub", Authentication = true, ispublic = true, notes = "获取技术津贴统计") ////// 停用
	public String getSalaryReportTechSub() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "chgdate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 10, 0);
		String orderstr = " chgdate desc ";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

	private String getSalarySearchSQL(List<JSONParm> jps, int cglgtype, int probtype) throws Exception {
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporgcode != null) {
			String orgcode = jporgcode.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		Hr_employee emp = new Hr_employee();
		JSONParm empcode = CjpaUtil.getParm(jps, "employee_code");
		if (empcode != null) {
			String emplcode = empcode.getParmvalue();
			emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + emplcode + "'");
			if (emp.isEmpty())
				throw new Exception("工号【" + emplcode + "】不存在人事资料");
		}
		JSONParm jpchgdate = CjpaUtil.getParm(jps, "chgdate");
		JSONParm jpchgbg = CjpaUtil.getParm(jps, "chgbg");
		JSONParm jpchged = CjpaUtil.getParm(jps, "chged");
		String ymrelator = "=";
		boolean isdate = false;
		Date eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd());// 去除时分秒;
		Date bgdate = Systemdate.getFirstAndLastOfMonth(eddate).date1;
		if (jpchgdate != null) {
			isdate = true;
			String dqdate = jpchgdate.getParmvalue();
			ymrelator = jpchgdate.getReloper();
			bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
			eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		}
		if ((jpchgbg != null) && (jpchged != null)) {
			isdate = true;
			String dqbgdate = jpchgbg.getParmvalue();
			bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqbgdate)));// 去除时分秒
			String dqeddate = jpchged.getParmvalue();
			eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqeddate)));// 去除时分秒
			eddate = Systemdate.dateDayAdd(eddate, 1);
		}

		String sqlstr = "";
		if (cglgtype == 1) {
			sqlstr = "SELECT cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath,DATE_FORMAT(cl.chgdate,'%Y-%m') AS chgdate1,'" +
					cglgtype + "' AS chgtype ,emp.hwc_namezl,emp.hwc_namezq,emp.hwc_namezz FROM hr_salary_chglg cl,hr_employee emp  " +
					" WHERE cl.er_id=emp.er_id  AND cl.stype=3 and newcalsalarytype='月薪'";
		}
		if (cglgtype == 2) {
			sqlstr = "SELECT cl.*,emp.employee_code,emp.employee_name,emp.tranfcmpdate,emp.tranftype1,DATE_FORMAT(cl.chgdate,'%Y-%m') AS chgdate1," +
					"emp.odorgname,emp.odsp_name,emp.odlv_num,emp.neworgname,emp.newsp_name,emp.newlv_num,emp.hiredday,emp.idpath,emp.salarydate" +
					" FROM `hr_salary_chglg` cl,`hr_employee_transfer` emp " + " WHERE cl.er_id=emp.er_id and emp.stat=9 AND cl.newcalsalarytype='月薪'  "
					+ "AND cl.scatype=2 AND cl.sid=emp.emptranf_id ";
		}
		if (cglgtype == 3) {
			if (probtype == 5) {
				sqlstr = "SELECT cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath,DATE_FORMAT(cl.chgdate,'%Y-%m') AS chgdate1,'" +
						cglgtype + "' AS chgtype ,emp.pbtdate,emp.pbtsarylev,emp.updatetime FROM hr_salary_chglg cl,hr_entry_prob emp WHERE cl.er_id=emp.er_id  AND cl.scatype=3 and stype=5 AND emp.pbtid=cl.sid ";
			}
			if (probtype == 6) {
				sqlstr = "SELECT cl.*,emp.employee_code,emp.employee_name,emp.neworgname,emp.newsp_name,emp.newlv_num,emp.hiredday,emp.idpath,DATE_FORMAT(cl.chgdate,'%Y-%m') AS chgdate1,'" +
						cglgtype + "' AS chgtype ,emp.pbtdate,emp.tranfcmpdate,emp.pbtsarylev,emp.updatetime FROM hr_salary_chglg cl, hr_transfer_prob emp  WHERE cl.er_id=emp.er_id  AND cl.scatype=3 and stype=6 AND emp.pbtid=cl.sid";
			}
		}
		if (cglgtype == 4) {
			sqlstr = "SELECT cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath,DATE_FORMAT(cl.chgdate,'%Y-%m') AS chgdate1,'" +
					cglgtype + "' AS chgtype  FROM hr_salary_chglg cl,hr_employee emp  WHERE cl.er_id=emp.er_id  AND cl.scatype=4";
		}
		if (cglgtype == 11) {
			sqlstr ="SELECT '年度调薪' chgtype,b.pbtsarylev sacrage,a.yrcode,a.salarydate,b.* " +
			" FROM hr_salary_yearraise a,hr_salary_yearraise_line b ,shwwfprocuser wf "+
			" WHERE  a.yrid=b.yrid AND wf.wfid=a.wfid AND wf.nextpresstime IS NOT NULL AND wf.audittime IS NULL AND wf.userid=62 AND a.stat<>9 ";
		}
		if (cglgtype == 5) {
			if(probtype ==7){
				sqlstr = "SELECT cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath,DATE_FORMAT(cl.chgdate,'%Y-%m') AS chgdate1,'" +
						cglgtype + "' AS chgtype,emp.updatetime  FROM hr_salary_chglg cl,hr_salary_specchgsa emp  WHERE cl.er_id=emp.er_id AND cl.sid=emp.scsid AND cl.scatype=5 and cl.stype=7 ";
			}
			if(probtype ==9){
				sqlstr = " SELECT cl.*,emps.employee_code,emps.employee_name,emps.hiredday,emps.idpath,emps.updatetime,DATE_FORMAT(cl.chgdate,'%Y-%m') AS chgdate1,'"+
						cglgtype+"' AS chgtype  FROM hr_salary_chglg cl,"+
						"(SELECT emp.scsbid,empl.er_id,empl.employee_code,empl.employee_name,empl.orgname,empl.sp_name,empl.lv_num,empl.hiredday,emp.idpath,emp.updatetime "+
						"FROM `hr_salary_specchgsa_batch` emp,`hr_salary_specchgsa_batch_line` empl WHERE empl.scsbid=emp.scsbid ) emps "+
						" WHERE  cl.er_id=emps.er_id AND cl.sid=emps.scsbid AND cl.scatype=5 AND cl.stype=9  ";
			}
		}

		if (cglgtype == 10) {
			sqlstr = "SELECT cl.*,tsc.canceldate FROM (SELECT DATE_FORMAT(cl.chgdate,'%Y-%m') AS months,cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath ,emp.ospid,'2' AS substype" +
					" FROM `hr_salary_chglg` cl,`hr_employee` emp WHERE  cl.er_id=emp.er_id AND cl.useable=1 AND stype=10 ";
		}
		if (isdate) {
			if (ymrelator.equals("=")) {
				sqlstr = sqlstr + " AND cl.chgdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) +
						"' AND cl.chgdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
			}
			if (ymrelator.equals(">=")) {
				sqlstr = sqlstr + " AND cl.chgdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' ";
			}
			if (ymrelator.equals("<=")) {
				sqlstr = sqlstr + " AND cl.chgdate<='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' ";
			}
		}
		/*JSONParm jpupdatetime = CjpaUtil.getParm(jps, "updatetime");
		if ((jpupdatetime != null) && (jpupdatetime != null)) {
			String updatedate = jpupdatetime.getParmvalue();
			Date update = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(updatedate)));// 去除时分秒
			sqlstr = sqlstr + " AND emp.updatetime>='" + Systemdate.getStrDateyyyy_mm_dd(update) + "' ";
		}*/
		if (cglgtype == 10) {
			sqlstr = sqlstr + ") cl LEFT JOIN  `hr_salary_techsub_cancel` tsc ON tsc.stat=9 AND tsc.er_id=cl.er_id ";
		}
		if (empcode != null) {
			sqlstr = sqlstr + " and cl.er_id=" + emp.er_id.getValue();
		}
		if (jporgcode != null) {
			sqlstr = sqlstr + " and emp.idpath LIKE '" + org.idpath.getValue() + "%' ";
		}
		if(cglgtype!=11){
			sqlstr = sqlstr + CSContext.getIdpathwhere();
		}
		
		// sqlstr = sqlstr + " ORDER BY chgdate desc ";
		// sqlstr=sqlstr+" ORDER BY cl.er_id,cl.createtime ";
		return sqlstr;
	}

	@ACOAction(eventname = "gettechsublist", Authentication = true, ispublic = true, notes = "获取技术津贴明细")
	public String gettechsublist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporgcode != null) {
			String orgcode = jporgcode.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		Hr_employee emp = new Hr_employee();
		JSONParm empcode = CjpaUtil.getParm(jps, "employee_code");
		if (empcode != null) {
			String emplcode = empcode.getParmvalue();
			emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + emplcode + "'");
			if (emp.isEmpty())
				throw new Exception("工号【" + emplcode + "】不存在人事资料");
		}
		JSONParm jpchgdate = CjpaUtil.getParm(jps, "chgdate");
		JSONParm jpchgbg = CjpaUtil.getParm(jps, "chgbg");
		JSONParm jpchged = CjpaUtil.getParm(jps, "chged");
		JSONParm jpccdate = CjpaUtil.getParm(jps, "canceldate");
		Date eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd());// 去除时分秒;
		Date bgdate = Systemdate.getFirstAndLastOfMonth(eddate).date1;
		if (jpchgdate != null) {
			String dqdate = jpchgdate.getParmvalue();
			bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
			eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		}
		if ((jpchgbg != null) && (jpchged != null)) {
			String dqbgdate = jpchgbg.getParmvalue();
			bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqbgdate)));// 去除时分秒
			String dqeddate = jpchged.getParmvalue();
			eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqeddate)));// 去除时分秒
			eddate = Systemdate.dateDayAdd(eddate, 1);
		}
		String[] ignParms = { "chgdate", "orgcode", "employee_code", "chgbg", "chged","canceldate" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT pss.*,(CASE WHEN pss.isend=2 THEN 1 ELSE 2 END) AS usable,psc.canceldate  " +
				"FROM (SELECT ps.substype,ps.subsdate,psl.* FROM `hr_salary_techsub` ps,`hr_salary_techsub_line` psl " +
				"WHERE ps.stat=9 AND psl.ts_id=ps.ts_id AND (ps.subsdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND ps.subsdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) +
				"')  ";
		if (jporgcode != null) {
			sqlstr = sqlstr + " and psl.idpath LIKE '" + org.idpath.getValue() + "%' ";
		}
		if(empcode !=null){
			sqlstr = sqlstr + " and psl.employee_code='"+emp.employee_code.getValue()+"'";
		}
		sqlstr = sqlstr + " ) pss LEFT JOIN  `hr_salary_techsub_cancel` psc ON psc.stat=9 AND psc.tsl_id=pss.tsl_id ";
		if(jpccdate!=null){
			String ccdate = jpccdate.getParmvalue();
			Date canceldate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(ccdate)));// 去除时分秒
			String ccrept=jpccdate.getReloper();
			if(ccrept.equals("=")){
				ccrept=">=";
			}
			sqlstr = sqlstr + " and canceldate "+ccrept+" '"+ Systemdate.getStrDateyyyy_mm_dd(canceldate)+"'";
		}
		sqlstr = sqlstr +" ORDER BY pss.subsdate";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "gethotsubquallist", Authentication = true, ispublic = true, notes = "获取高温补贴资格列表")
	public String gethotsubquallist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		/*
		 * JSONParm jpym = CjpaUtil.getParm(jps, "yearmonth");
		 * if (jpym == null)
		 * throw new Exception("需要参数【yearmonth】");
		 * String yearmonth = jpym.getParmvalue();
		 */
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT hss.*,hqc.canceldate FROM (SELECT hsq.*,hsql.sp_id,hsql.sp_name,hsql.hsql_id,hsql.substand,hsql.usable AS lusable " +
				"FROM `hr_salary_hotsub_qual` hsq,`hr_salary_hotsub_qual_line` hsql WHERE hsq.stat=9 AND hsql.hsq_id=hsq.hsq_id ORDER BY startdate) hss " +
				"LEFT JOIN `hr_salary_hotsub_qual_cancel` hqc ON hqc.stat=9 AND hqc.hsql_id=hss.hsql_id ORDER BY hss.startdate";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "gethotsublist", Authentication = true, ispublic = true, notes = "获取高温补贴列表")
	public String gethotsublist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jpymbg = CjpaUtil.getParm(jps, "ymbg");
		if (jpymbg == null)
			throw new Exception("需要参数【ymbg】");
		String ymbg = jpymbg.getParmvalue();

		JSONParm jpymed = CjpaUtil.getParm(jps, "ymed");
		if (jpymed == null)
			throw new Exception("需要参数【ymed】");
		String ymed = jpymed.getParmvalue();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(ymbg)));// 去除时分秒
		Date eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(ymed)));// 去除时分秒
		eddate = Systemdate.dateMonthAdd(eddate, 1);// 加一月
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporgcode != null) {
			String orgcode = jporgcode.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		String[] ignParms = { "ymbg", "ymed", "orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "   SELECT hsl.*,hs.substype,hs.applydate,DATE_FORMAT(hs.applydate,'%Y-%m') AS months " +
				"FROM `hr_salary_hotsub` hs,`hr_salary_hotsub_line` hsl WHERE hs.stat=9 AND hsl.hs_id=hs.hs_id AND hs.usable=1" +
				" AND applydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' and applydate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		if (jporgcode != null) {
			sqlstr = sqlstr + " and hsl.idpath LIKE '" + org.idpath.getValue() + "%' ";
		}
		sqlstr = sqlstr + " ORDER BY hs.applydate";
		return new CReport(HRUtil.getReadPool(), sqlstr, null, notnull).findReport(ignParms);
		//return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getpostsublist", Authentication = true, ispublic = true, notes = "获取岗位补贴统计")
	public String getpostsublist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jpymbg = CjpaUtil.getParm(jps, "ymbg");
		if (jpymbg == null)
			throw new Exception("需要参数【ymbg】");
		String ymbg = jpymbg.getParmvalue();

		JSONParm jpymed = CjpaUtil.getParm(jps, "ymed");
		if (jpymed == null)
			throw new Exception("需要参数【ymed】");
		String ymed = jpymed.getParmvalue();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(ymbg)));// 去除时分秒
		Date eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(ymed)));// 去除时分秒
		eddate = Systemdate.dateMonthAdd(eddate, 1);// 加一月
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporgcode != null) {
			String orgcode = jporgcode.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		String[] ignParms = { "ymbg", "ymed", "orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT pss.*,(CASE WHEN pss.isend=2 THEN 1 ELSE 2 END) AS usable,psc.canceldate FROM (SELECT ps.substype,ps.startdate,ps.enddate,psl.* FROM `hr_salary_postsub` ps,`hr_salary_postsub_line` psl " +
				"WHERE ps.stat=9 AND psl.ps_id=ps.ps_id AND ((ps.startdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND ps.startdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) +
				"') OR (ps.startdate<='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND ps.enddate>'" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "'))  ";
		if (jporgcode != null) {
			sqlstr = sqlstr + " and psl.idpath LIKE '" + org.idpath.getValue() + "%' ";
		}
		sqlstr = sqlstr + " ) pss LEFT JOIN  `hr_salary_postsub_cancel` psc ON psc.stat=9 AND psc.psl_id=pss.psl_id ORDER BY pss.startdate";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	public static double getPercentile(double[] param, double p) {
		if (param == null || p < 0 || p > 1 || param.length <= 0)
			return 0;
		// 转成BigDecimal类型，避免失去精度
		BigDecimal[] datas = new BigDecimal[param.length];
		for (int i = 0; i < param.length; i++) {
			datas[i] = BigDecimal.valueOf(param[i]);
		}
		BigDecimal pt = new BigDecimal(Double.toString(p));
		int len = datas.length;// 数组长度
		Arrays.sort(datas); // 数组排序，从小到大
		BigDecimal px = pt.multiply(new BigDecimal(len - 1));
		int pi = px.setScale(0, BigDecimal.ROUND_DOWN).intValue();
		BigDecimal j = px.subtract(new BigDecimal(pi));
		if (j.compareTo(BigDecimal.ZERO) == 0) {
			return datas[pi].setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			BigDecimal res = datas[pi].multiply(new BigDecimal(1).subtract(j)).add(datas[pi + 1].multiply(j));
			return res.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}

	@ACOAction(eventname = "getPosAvgSalarys", Authentication = true, ispublic = true, notes = "获取职位平均工资水平")
	public String getPosAvgSalarys() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String ospid = CorUtil.hashMap2Str(parms, "ospid", "需要参数ospid");
		JSONObject result = new JSONObject();
		if (!HRUtil.hasRoles("71")) {// 薪酬管理员
			result.put("accessed", 2);
			return result.toString();
		}
		Hr_standposition sp = new Hr_standposition();
		sp.findBySQL("SELECT sp.* FROM `hr_orgposition` op,`hr_standposition` sp WHERE op.ospid=" + ospid + " AND sp.sp_id=op.sp_id");
		if (sp.isEmpty())
			throw new Exception("id为【" + ospid + "】的职位不存在");
		// String[] ignParms = {};// 忽略的查询条件
		// String[] notnull = {};
		String ym = CorUtil.hashMap2Str(parms, "yearmonth", "需要参数yearmonth");
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(ym)));// 去除时分秒
		Date nowdate = Systemdate.getDateByStr(Systemdate.getStrDate());
		Date ftdate = Systemdate.dateMonthAdd(bgdate, -1);
		String ymbg = Systemdate.getStrDateByFmt(ftdate, "yyyy-MM");
		String ymed = Systemdate.getStrDateByFmt(bgdate, "yyyy-MM");
		String sqlstr = "SELECT poswage FROM `hr_salary_list` WHERE ospid IN " +
				"(SELECT os2.ospid FROM `hr_orgposition` os1,hr_orgposition os2 WHERE os1.ospid=" + ospid + " AND os2.sp_id=os1.sp_id) AND wagetype=1 AND " +
				" yearmonth>='" + ymbg + "' AND yearmonth<'" + ymed + "' ORDER BY poswage";
		JSONArray res = sp.pool.opensql2json_O(sqlstr);
		double[] datas = new double[res.size()];
		if (res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				JSONObject row = res.getJSONObject(i);
				Double pw = Double.valueOf(row.getString("poswage"));
				datas[i] = pw;
			}
		}
		double presntwage = getPercentile(datas, 0.5);
		double pw10 = getPercentile(datas, 0.1);
		double pw25 = getPercentile(datas, 0.25);
		double pw75 = getPercentile(datas, 0.75);
		double pw90 = getPercentile(datas, 0.9);
		sqlstr = "SELECT yearmonth,TRUNCATE(AVG(poswage),2) AS avgpos,COUNT(*) AS nums,MAX(poswage) maxpos,MIN(poswage) minpos,IFNULL('" + sp.sp_name.getValue() +
				"','') AS pos FROM `hr_salary_list` WHERE ospid IN (SELECT os2.ospid FROM `hr_orgposition` os1,hr_orgposition os2 WHERE os1.ospid=" + ospid +
				" AND os2.sp_id=os1.sp_id) AND wagetype=1 AND yearmonth>='" + ymbg + "' AND yearmonth<'" + ymed + "'";
		res = sp.pool.opensql2json_O(sqlstr);
		if (res.size() > 0) {
			res.getJSONObject(0).put("pstwg", presntwage);
			res.getJSONObject(0).put("pw10", pw10);
			res.getJSONObject(0).put("pw25", pw25);
			res.getJSONObject(0).put("pw75", pw75);
			res.getJSONObject(0).put("pw90", pw90);
			res.getJSONObject(0).put("lv_num", sp.lv_num.getValue());
			res.getJSONObject(0).put("hg_name", sp.hg_name.getValue());
			res.getJSONObject(0).put("hwc_namezl", sp.hwc_namezl.getValue());
			res.getJSONObject(0).put("hwc_namezq", sp.hwc_namezq.getValue());
			res.getJSONObject(0).put("hwc_namezz", sp.hwc_namezz.getValue());
		}
		JSONObject jo = new JSONObject();
		jo.put("accessed", 1);
		jo.put("rows", res);
		String scols = parms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(res, scols);
			return null;
		}
	}

	@ACOAction(eventname = "getEmpAvgWage", Authentication = true, ispublic = true, notes = "获取非月薪人员前三个满勤月应发合计不含津贴的平均工资")
	public String getEmpAvgWage() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT IFNULL(ROUND(AVG(paynosubs),2),0) AS avgpaysubs FROM (SELECT * FROM " +
				" hr_salary_list WHERE wagetype=2 AND isfullattend=1 AND er_id=" + er_id + " ORDER BY yearmonth DESC LIMIT 0,3) tt";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getHotSubQualPost", Authentication = true, ispublic = true, notes = "获取高温补贴资格申请的职位")
	public String getHotSubQualPost() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String isqual = CorUtil.hashMap2Str(parms, "isqual", "需要参数isqual");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}
		int isq = Integer.parseInt(isqual);
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "";
		if (isq == 1) {
			sqlstr = "SELECT * FROM `hr_standposition` sp WHERE sp.usable=1 AND  " +
					"sp.sp_id IN  (SELECT DISTINCT sp_id FROM `hr_salary_hotsub_qualsp` WHERE stat=9 AND usable=1 AND  idpath LIKE '" + org.idpath.getValue() + "%') ";
		} else if (isq == 2) {
			sqlstr = "SELECT * FROM `hr_standposition` sp WHERE sp.usable=1 AND  sp.sp_id IN " +
					" (SELECT DISTINCT  sp_id FROM `hr_orgposition` op WHERE  op.usable=1 AND op.idpath LIKE '" + org.idpath.getValue() + "%') ";
		}
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "imptechsubs_listexcel", Authentication = true, ispublic = false, notes = "导入技术津贴明细Excel")
	public String imptechsubs_listexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String appsys = CorUtil.hashMap2Str(CSContext.getParms(), "appsys", "需要参数appsys");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_techsublist(p, appsys);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_techsublist(Shw_physic_file pf, String appsys) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_techsublist(aSheet, appsys);
	}

	private String parserExcelSheet_techsublist(Sheet aSheet, String appsys) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_techsublist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		/*
		 * Shworg org = new Shworg();
		 * org.findByID(orgid);
		 * if (org.isEmpty()) {
		 * throw new Exception("没找到ID为【" + orgid + "】的机构");
		 * }
		 */

		Hr_employee emp = new Hr_employee();
		CJPALineData<Hr_salary_techsub_line> tsls = new CJPALineData<Hr_salary_techsub_line>(Hr_salary_techsub_line.class);
		Shworg emporg = new Shworg();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("明细行上的工号不能为空");

			String sqlstr = "SELECT e.* FROM hr_employee e,hr_orgposition op WHERE e.empstatid<=10 AND e.ospid=op.ospid " +
					" AND e.employee_code='" + employee_code +
					"' AND op.sp_id IN (SELECT sp_id FROM `hr_salary_techsub_post` WHERE stat=9 AND usable=1 AND appsys=" + appsys + ")";
			emp.clear();
			emp.findBySQL(sqlstr);
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料或不符合技术津贴标准");
			emporg.clear();
			emporg.findByID(emp.orgid.getValue());
			if (emporg.isEmpty())
				throw new Exception("没找到员工【" + emp.employee_name.getValue() + "】所属的ID为【" + emp.orgid.getValue() + "】的机构");

			Hr_salary_techsub_line scsbl = new Hr_salary_techsub_line();

			scsbl.er_id.setValue(emp.er_id.getValue());
			scsbl.employee_code.setValue(emp.employee_code.getValue());
			scsbl.employee_name.setValue(emp.employee_name.getValue());
			scsbl.ospid.setValue(emp.ospid.getValue());
			scsbl.ospcode.setValue(emp.ospcode.getValue());
			scsbl.sp_name.setValue(emp.sp_name.getValue());
			scsbl.lv_id.setValue(emp.lv_id.getValue());
			scsbl.lv_num.setValue(emp.lv_num.getValue());
			scsbl.hwc_namezq.setValue(emp.hwc_namezq.getValue());
			scsbl.hwc_namezz.setValue(emp.hwc_namezz.getValue());
			scsbl.hiredday.setValue(emp.hiredday.getValue());
			scsbl.orgid.setValue(emp.orgid.getValue());
			scsbl.orgcode.setValue(emp.orgcode.getValue());
			scsbl.orgname.setValue(emp.orgname.getValue());
			scsbl.idpath.setValue(emp.idpath.getValue());
			Hr_salary_chglg sc = CtrSalaryCommon.getCur_salary_chglg(emp.er_id.getValue());
			scsbl.otechsub.setValue(sc.newtech_allowance.getValue()); // 原技术津贴金额
			scsbl.ntechsub.setValue(v.get("ntechsub")); // 申请津贴金额
			float ot=sc.newtech_allowance.getAsFloatDefault(0);
			float nt=Float.parseFloat(v.get("ntechsub"));
			float ttsub=ot+nt;
			scsbl.ttsub.setAsFloat(ttsub);
			scsbl.remark.setValue(v.get("remark"));
			tsls.add(scsbl);
		}
		return tsls.tojson();

	}

	private List<CExcelField> initExcelFields_techsublist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("申请津贴", "ntechsub", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "getparttimesublist", Authentication = true, ispublic = true, notes = "获取兼职津贴明细")
	public String getparttimesublist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jpymbg = CjpaUtil.getParm(jps, "ymbg");
		boolean filterdate = false;
		boolean isym = false;
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDate());// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月

		if (jpymbg != null) {
			filterdate = true;
			isym = true;
			String ymbg = jpymbg.getParmvalue();
			bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(ymbg)));// 去除时分秒
			eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		}
		JSONParm jpsubstart = CjpaUtil.getParm(jps, "substart");
		JSONParm jpsubend = CjpaUtil.getParm(jps, "subend");
		String ssrelator = "=";
		String serelator = "=";
		if (jpsubstart != null) {
			filterdate = true;
			isym = false;
			String ss = jpsubstart.getParmvalue();
			bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(ss)));// 去除时分秒
			ssrelator = jpsubstart.getReloper();
		}

		if (jpsubend != null) {
			filterdate = true;
			isym = false;
			String se = jpsubend.getParmvalue();
			eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(se)));// 去除时分秒
			serelator = jpsubend.getReloper();
		}

		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporgcode != null) {
			String orgcode = jporgcode.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}

		String[] ignParms = { "ymbg", "orgcode", "substart", "subend" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT pts.*,ejb.ptbbdate,(CASE WHEN ((pts.ptbbdate1 IS NOT NULL) OR pts.subsidyarm<=0 OR pts.breaked=1) THEN 2 ELSE 1 END) AS usable " +
				" FROM ( SELECT pta.*  FROM   hr_empptjob_app pta  WHERE pta.subsidyarm<>0 and  pta.stat=9 ";
		if (filterdate) {
			if (isym) {
				sqlstr = sqlstr + " AND ((pta.substart<='" +
						Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' and pta.subend>'" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "') or (pta.substart>='" +
						Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND pta.substart<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "')) ";
			} else {
				if (jpsubstart != null) {
					sqlstr = sqlstr + " and pta.substart" + ssrelator + "'" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' ";
				}
				if (jpsubend != null) {
					sqlstr = sqlstr + " and pta.subend" + serelator + "'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
				}
			}
		}

		if (jporgcode != null) {
			sqlstr = sqlstr + " and pta.idpath LIKE '" + org.idpath.getValue() + "%' ";
		}
		sqlstr = sqlstr + ") pts LEFT JOIN `hr_empptjob_break` ejb ON  ejb.ptjaid=pts.ptjaid AND ejb.stat=9 ORDER BY pts.substart";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getCancelTechSubsEmps", Authentication = true, ispublic = true, notes = "获取要取消技术补贴的人事档案")
	public String getCancelTechSubsEmps() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporg = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporg != null) {
			String orgcode = jporg.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		String[] ignParms = { "orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT ts.ts_code,ts.subsdate,ts.applyreason,ts.appsys,tsl.* " +
				"FROM `hr_salary_techsub` ts,`hr_salary_techsub_line` tsl " +
				"WHERE tsl.ts_id=ts.ts_id AND ts.stat=9 AND tsl.isend=2  ";
		if (jporg != null) {
			sqlstr = sqlstr + " tsl.idpath like '" + org.idpath.getValue() + "%'";
		}
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getCancelPostSubsEmps", Authentication = true, ispublic = true, notes = "获取要取消岗位补贴的人事档案")
	public String getCancelPostSubsEmps() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporg = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporg != null) {
			String orgcode = jporg.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		String[] ignParms = { "orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT ps.startdate,ps.enddate,ps.appreason,psl.* " +
				"FROM `hr_salary_postsub` ps,`hr_salary_postsub_line` psl " +
				"WHERE psl.ps_id=ps.ps_id AND psl.isend=2 and ps.stat=9  ";
		if (jporg != null) {
			sqlstr = sqlstr + " psl.idpath like '" + org.idpath.getValue() + "%'";
		}
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "imppostsubs_listexcel", Authentication = true, ispublic = false, notes = "导入岗位津贴明细Excel")
	public String imppostsubs_listexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		// String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_postsublist(p);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_postsublist(Shw_physic_file pf) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_postsublist(aSheet);
	}

	private String parserExcelSheet_postsublist(Sheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_postsublist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		/*
		 * Shworg org = new Shworg();
		 * org.findByID(orgid);
		 * if (org.isEmpty()) {
		 * throw new Exception("没找到ID为【" + orgid + "】的机构");
		 * }
		 */

		Hr_employee emp = new Hr_employee();
		CJPALineData<Hr_salary_postsub_line> tsls = new CJPALineData<Hr_salary_postsub_line>(Hr_salary_postsub_line.class);
		Shworg emporg = new Shworg();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("明细行上的工号不能为空");

			emp.clear();
			emp.findBySQL("SELECT * FROM `hr_employee` WHERE employee_code='" + employee_code + "'");
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料");
			emporg.clear();
			emporg.findByID(emp.orgid.getValue());
			if (emporg.isEmpty())
				throw new Exception("没找到员工【" + emp.employee_name.getValue() + "】所属的ID为【" + emp.orgid.getValue() + "】的机构");

			Hr_salary_postsub_line scsbl = new Hr_salary_postsub_line();

			scsbl.er_id.setValue(emp.er_id.getValue());
			scsbl.employee_code.setValue(emp.employee_code.getValue());
			scsbl.employee_name.setValue(emp.employee_name.getValue());
			scsbl.ospid.setValue(emp.ospid.getValue());
			scsbl.ospcode.setValue(emp.ospcode.getValue());
			scsbl.sp_name.setValue(emp.sp_name.getValue());
			scsbl.lv_id.setValue(emp.lv_id.getValue());
			scsbl.lv_num.setValue(emp.lv_num.getValue());
			scsbl.hwc_namezq.setValue(emp.hwc_namezq.getValue());
			scsbl.hwc_namezz.setValue(emp.hwc_namezz.getValue());
			scsbl.hiredday.setValue(emp.hiredday.getValue());
			scsbl.orgid.setValue(emp.orgid.getValue());
			scsbl.orgcode.setValue(emp.orgcode.getValue());
			scsbl.orgname.setValue(emp.orgname.getValue());
			scsbl.idpath.setValue(emp.idpath.getValue());
			Hr_salary_chglg sc = CtrSalaryCommon.getCur_salary_chglg(emp.er_id.getValue());
			scsbl.opostsub.setValue(sc.newpostsubs.getValue()); // 原岗位津贴金额
			scsbl.npostsub.setValue(v.get("npostsub")); // 申请津贴金额
			scsbl.remark.setValue(v.get("remark"));
			tsls.add(scsbl);
		}
		return tsls.tojson();

	}

	private List<CExcelField> initExcelFields_postsublist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("申请津贴", "npostsub", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "imphotsubqual_listexcel", Authentication = true, ispublic = false, notes = "导入高温津贴资格明细Excel")
	public String imphotsubqual_listexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		String isqual = CorUtil.hashMap2Str(CSContext.getParms(), "isqual", "需要参数isqual");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_hotsubquallist(p, orgid, isqual);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_hotsubquallist(Shw_physic_file pf, String orgid, String isqual) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_hotsubquallist(aSheet, orgid, isqual);
	}

	private String parserExcelSheet_hotsubquallist(Sheet aSheet, String orgid, String isqual) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_hotsubquallist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}

		CJPALineData<Hr_salary_hotsub_qual_line> tsls = new CJPALineData<Hr_salary_hotsub_qual_line>(Hr_salary_hotsub_qual_line.class);
		Hr_orgposition op = new Hr_orgposition();
		Hr_standposition sp = new Hr_standposition();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String spcode = v.get("sp_code");
			if ((spcode == null) || (spcode.isEmpty()))
				throw new Exception("职位编码不能为空");

			/*
			 * op.clear();
			 * op.findBySQL("SELECT * FROM  hr_orgposition WHERE usable=1 AND sp_code='"+spcode+"' AND idpath LIKE'" + org.idpath.getValue() + "%'");
			 * if (op.isEmpty())
			 * throw new Exception("编码【" + spcode + "】的职位不存在或此机构无此职位！");
			 */
			int isq = Integer.parseInt(isqual);
			String sqlstr = "";
			if (isq == 1) {
				sqlstr = "SELECT * FROM `hr_standposition` sp WHERE sp.usable=1 AND sp.sp_id IN " +
						" (SELECT DISTINCT sp_id FROM `hr_salary_hotsub_qualsp` WHERE stat=9 AND usable=1 AND " +
						" sp_code='" + spcode + "' AND idpath LIKE '" + org.idpath.getValue() + "%') ";
			} else if (isq == 2) {
				sqlstr = "SELECT * FROM hr_standposition sp WHERE sp.usable=1 AND sp_code='" + spcode + "'";
			}
			sp.clear();
			sp.findBySQL(sqlstr);
			if (sp.isEmpty())
				throw new Exception("没找到编码【" + spcode + "】的标准职位或资格内无此职位！");

			Hr_salary_hotsub_qual_line scsbl = new Hr_salary_hotsub_qual_line();
			scsbl.sp_id.setValue(sp.sp_id.getValue());
			scsbl.sp_code.setValue(sp.sp_code.getValue());
			scsbl.sp_name.setValue(sp.sp_name.getValue());
			scsbl.substand.setValue(v.get("substand"));
			scsbl.usable.setValue(1);
			tsls.add(scsbl);
		}
		return tsls.tojson();

	}

	private List<CExcelField> initExcelFields_hotsubquallist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("职位编码", "sp_code", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("津贴标准", "substand", true));
		return efields;
	}

	@ACOAction(eventname = "imphottsubs_listexcel", Authentication = true, ispublic = false, notes = "导入高温津贴明细Excel")
	public String imphottsubs_listexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		// String emplev = CorUtil.hashMap2Str(CSContext.getParms(), "emplev", "需要参数emplev");
		String applydate = CorUtil.hashMap2Str(CSContext.getParms(), "applydate", "需要参数applydate");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_hotsublist(p, orgid, applydate);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_hotsublist(Shw_physic_file pf, String orgid, String applydate) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_hotsublist(aSheet, orgid, applydate);
	}

	private String parserExcelSheet_hotsublist(Sheet aSheet, String orgid, String applydate) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_hotsublist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}

		Hr_employee emp = new Hr_employee();
		CJPALineData<Hr_salary_hotsub_line> tsls = new CJPALineData<Hr_salary_hotsub_line>(Hr_salary_hotsub_line.class);
		// Shworg emporg = new Shworg();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("明细行上的工号不能为空");

			emp.clear();
			emp.findBySQL("SELECT * FROM `hr_employee` WHERE employee_code='" + employee_code + "'  and idpath like '" + org.idpath.getValue() + "%'");
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料或不属于此机构");
			Hr_salary_hotsub_qual_line hsql = new Hr_salary_hotsub_qual_line();
			String sqlstr = "SELECT hsql.* FROM `hr_salary_hotsub_qual` hsq,`hr_salary_hotsub_qual_line` hsql" +
					" WHERE hsq.stat=9 AND hsql.hsq_id=hsq.hsq_id and hsql.usable=1 " +
					" AND hsql.sp_id=(SELECT sp_id FROM `hr_orgposition` WHERE ospid=" + emp.ospid.getValue() +
					") AND LOCATE('" + org.idpath.getValue() + "',hsq.idpath)=1 ";
			if (org.orgid.getAsInt() == 1) {
				sqlstr = sqlstr + " and idpath like '" + org.idpath.getValue() + "' ";
			}
			sqlstr = sqlstr + " ORDER BY hsq.createtime DESC";
			hsql.findBySQL(sqlstr);
			if (hsql.isEmpty()) {
				throw new Exception("工号【" + employee_code + "】员工的职位没有申请高温补贴的资格！");
			}

			Hr_salary_hotsub_line scsbl = new Hr_salary_hotsub_line();

			scsbl.er_id.setValue(emp.er_id.getValue());
			scsbl.employee_code.setValue(emp.employee_code.getValue());
			scsbl.employee_name.setValue(emp.employee_name.getValue());
			scsbl.ospid.setValue(emp.ospid.getValue());
			scsbl.ospcode.setValue(emp.ospcode.getValue());
			scsbl.sp_name.setValue(emp.sp_name.getValue());
			scsbl.lv_id.setValue(emp.lv_id.getValue());
			scsbl.lv_num.setValue(emp.lv_num.getValue());
			scsbl.hiredday.setValue(emp.hiredday.getValue());
			scsbl.orgid.setValue(emp.orgid.getValue());
			scsbl.orgcode.setValue(emp.orgcode.getValue());
			scsbl.orgname.setValue(emp.orgname.getValue());
			scsbl.idpath.setValue(emp.idpath.getValue());
			String substand = v.get("substand");
			if ((substand == null) || (substand.isEmpty())) {
				scsbl.substand.setValue(hsql.substand.getValue());
			} else {
				scsbl.substand.setValue(substand);
			}
			double subs = scsbl.substand.getAsFloatDefault(0);
			// JSONObject jo =COreport2.getYCMQSJCQ(applydate,emp.er_id.getValue());
			double ycmq = Double.parseDouble(v.get("shouldduty"));
			double sjcq = Double.parseDouble(v.get("realduty"));
			double shouldpay = 0;
			if ((ycmq == 0) || (sjcq == 0)) {
				shouldpay = 0;
			} else if (sjcq >= ycmq) {
				shouldpay = subs;
			} else {
				shouldpay = (sjcq / ycmq) * subs;
			}
			BigDecimal b = new BigDecimal(shouldpay);
			String sp = b.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
			scsbl.shouldduty.setValue(v.get("shouldduty"));
			scsbl.realduty.setValue(v.get("realduty"));
			scsbl.shouldpay.setValue(sp);
			scsbl.remark.setValue(v.get("remark"));
			tsls.add(scsbl);
		}
		return tsls.tojson();

	}

	private List<CExcelField> initExcelFields_hotsublist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("津贴标准", "substand", true));
		efields.add(new CExcelField("应出满勤", "shouldduty", true));
		efields.add(new CExcelField("实际出勤", "realduty", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	private void getlvnum(String emplev, float lvnum1, float lvnum2) {
		int lev = Integer.valueOf(emplev);
		if (lev == 1) {
			lvnum1 = 0;
			lvnum2 = (float) 1.2;
		} else if (lev == 2) {
			lvnum1 = (float) 1.2;
			lvnum2 = (float) 2;
		} else if ((lev == 3) || (lev == 4)) {
			lvnum1 = (float) 2;
			lvnum2 = (float) 3;
		} else if (lev == 5) {
			lvnum1 = (float) 3;
			lvnum2 = (float) 4;
		} else if (lev == 6) {
			lvnum1 = (float) 4;
			lvnum2 = (float) 9;
		}

	}

	@ACOAction(eventname = "getCancelHotSubQuals", Authentication = true, ispublic = true, notes = "获取要取消高温补贴资格的职位")
	public String getCancelHotSubQuals() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		/*
		 * JSONParm jporg = CjpaUtil.getParm(jps, "orgcode");
		 * Shworg org = new Shworg();
		 * if (jporg != null){
		 * String orgcode = jporg.getParmvalue();
		 * String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		 * org.findBySQL(sqlstr1);
		 * if (org.isEmpty())
		 * throw new Exception("编码为【" + orgcode + "】的机构不存在");
		 * }
		 */
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT hsq.*,hsql.sp_id,hsql.sp_code,hsql.sp_name,hsql.substand,hsql.hsql_id FROM `hr_salary_hotsub_qual` hsq,`hr_salary_hotsub_qual_line` hsql " +
				"WHERE hsq.stat=9 AND hsql.hsq_id=hsq.hsq_id AND hsql.usable=1 ";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getLastYearEXPoint", Authentication = true, ispublic = true, notes = "获取上一年的绩效系数")
	public String getLastYearEXPoint() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		String salarydate = CorUtil.hashMap2Str(parms, "salarydate", "需要参数salarydate");
		JSONObject jo = new JSONObject();
		jo = LastYearEXPoint(er_id,salarydate, jo);
		return jo.toString();
	}

	private JSONObject LastYearEXPoint(String er_id,String salarydate, JSONObject jo) throws Exception {
		Hr_employee emp = new Hr_employee();
		emp.findByID(er_id);
		if (emp.isEmpty()) {
			throw new Exception("没找到ID为【" + er_id + "】的人事档案");
		}
		Date ny = new Date();
		String nystr = Systemdate.getStrDateByFmt(ny, "yyyy-MM");
		nystr = nystr + "-01";
		Date now = Systemdate.getDateByStr(nystr, "yyyy-MM-dd");
		Date last = Systemdate.dateMonthAdd(now, -12);
		Calendar nday = Calendar.getInstance();
		nday.setTime(now);
		int nyear=nday.get(Calendar.YEAR);
		int nmonth= nday.get(Calendar.MONTH)+1;
		Calendar lday = Calendar.getInstance();
		lday.setTime(last);
		int lastyear = lday.get(Calendar.YEAR);
		int lastmonth= lday.get(Calendar.MONTH)+1;
		String sqlstr = "select IFNULL(ROUND((qrst),2),0)as expoint,pmonth,pmyear FROM `hrpm_rstmonthex` WHERE er_id=" + er_id + " AND pmyear>='" + lastyear + "' and pmyear<= '"+nyear+"'";
		JSONArray holidays = emp.pool.opensql2json_O(sqlstr);
		float expoint=0;
		int count=0;
		for(int i=0;i<holidays.size();i++){
			JSONObject hds = holidays.getJSONObject(i);
			//找到符合的年份月份
			if((hds.getInt("pmyear")==lastyear && hds.getInt("pmonth")>=lastmonth)||hds.getInt("pmyear")==nyear && hds.getInt("pmonth")<nmonth){
				expoint+=hds.getDouble("expoint");
				count++;
			}
		}
		if(expoint>0){
			DecimalFormat df = new DecimalFormat("0.00");
			jo.put("expoint",df.format(expoint/count));
		}else{
			jo.put("expoint", "0");
		}

		return jo;
	}

	@ACOAction(eventname = "getLastYearHolidays", Authentication = true, ispublic = true, notes = "获取上一年的请假天数")
	public String getLastYearHolidays() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		String salarydate = CorUtil.hashMap2Str(parms, "salarydate", "需要参数salarydate");
		JSONObject jo = new JSONObject();
		jo = LastYearHoliday(er_id, salarydate,jo);
		return jo.toString();
	}

	private JSONObject LastYearHoliday(String er_id, String salarydate,JSONObject jo) throws Exception {
		Hr_employee emp = new Hr_employee();
		emp.findByID(er_id);
		if (emp.isEmpty()) {
			throw new Exception("没找到ID为【" + er_id + "】的人事档案");
		}
		Date ct = Systemdate.getDateByStr(salarydate+"-01");
		Date lastyear = Systemdate.dateMonthAdd(ct, -12);// 上一年时间
		Calendar hday = Calendar.getInstance();
		hday.setTime(lastyear);
		String eddate = Systemdate.getStrDateByFmt(ct, "yyyy-MM");
		String bgdate = Systemdate.getStrDateByFmt(lastyear, "yyyy-MM");	
		String sqlstr = "SELECT IFNULL(SUM(ham.lhdaystrue),0) AS hdays FROM `hrkq_holidayapp` ha ,`hrkq_holidayapp_month` ham " +
				"WHERE ha.stat=9 AND ha.htid=1 AND ham.haid=ha.haid AND ha.er_id=" + er_id +
				" AND ham.yearmonth>='" + bgdate + "' AND yearmonth<'" + eddate + "'";
		JSONArray holidays = emp.pool.opensql2json_O(sqlstr);
		if (!holidays.isEmpty()) {
			JSONObject hds = holidays.getJSONObject(0);
			jo.put("hdays", hds.getString("hdays"));
		} else {
			jo.put("hdays", "0");
		}
		return jo;
	}

	@ACOAction(eventname = "imphotsubqualsexcel", Authentication = true, ispublic = false, notes = "批量导入高温津贴资格Excel")
	public String imphotsubqualsexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		// String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_hotsubquals(p);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("rst", rst);
		return jo.toString();
	}

	private int parserExcelFile_hotsubquals(Shw_physic_file pf) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_hotsubquals(aSheet);
	}

	private int parserExcelSheet_hotsubquals(Sheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_hotsubquals();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);

		CJPALineData<Hr_salary_hotsub_qual_line> tsls = new CJPALineData<Hr_salary_hotsub_qual_line>(Hr_salary_hotsub_qual_line.class);
		Hr_orgposition op = new Hr_orgposition();
		Hr_standposition sp = new Hr_standposition();
		Shworg org = new Shworg();
		Hr_salary_hotsub_qual hsq = new Hr_salary_hotsub_qual();
		Hr_salary_hotsub_qual_line hsql = new Hr_salary_hotsub_qual_line();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		CDBConnection con = org.pool.getCon(this);
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String orgcode = v.get("orgcode");
				if ((orgcode == null) || (orgcode.isEmpty()))
					// throw new Exception("机构编码不能为空");
					continue;
				org.clear();
				org.findBySQL("select * from shworg where code= '" + orgcode + "'");
				if (org.isEmpty()) {
					throw new Exception("没找到编码为【" + orgcode + "】的机构");
				}
				hsq.clear();
				hsq.hr_salary_hotsub_qual_lines.clear();
				hsql.clear();
				hsq.orgid.setValue(org.orgid.getValue());
				hsq.orgcode.setValue(org.code.getValue());
				hsq.orgname.setValue(org.extorgname.getValue());
				hsq.idpath.setValue(org.idpath.getValue());
				hsq.substype.setValue(3);
				hsq.usable.setValue(1);
				hsq.startdate.setValue(v.get("startdate"));
				hsq.enddate.setValue(v.get("enddate"));
				hsq.applyreason.setValue(v.get("applyreason"));
				int hrlev = HRUtil.getOrgHrLev(org.orgid.getValue());
				hsq.orghrlev.setValue(hrlev);
				/////////
				String spcode = v.get("sp_code");
				if ((spcode == null) || (spcode.isEmpty()))
					throw new Exception("职位编码不能为空");
				/*
				 * op.clear();
				 * op.findBySQL("SELECT * FROM  hr_orgposition WHERE usable=1 AND sp_code='"+spcode+"' AND idpath LIKE'" + org.idpath.getValue() + "%'");
				 * if (op.isEmpty())
				 * throw new Exception("编码【" + spcode + "】的职位不存在或此机构【"+org.extorgname.getValue()+"】无此职位！");
				 */
				String sqlstr = "SELECT * FROM hr_standposition sp WHERE sp.usable=1 AND sp_code='" + spcode + "'";
				sp.clear();
				sp.findBySQL(sqlstr);
				if (sp.isEmpty())
					throw new Exception("没找到编码【" + spcode + "】的标准职位或资格内无此职位！");
				hsql.sp_id.setValue(sp.sp_id.getValue());
				hsql.sp_code.setValue(sp.sp_code.getValue());
				hsql.sp_name.setValue(sp.sp_name.getValue());
				hsql.substand.setValue(v.get("substand"));
				hsql.usable.setValue(1);
				hsq.hr_salary_hotsub_qual_lines.add(hsql);
				hsq.save(con);
				hsq.wfcreate(null, con);
				System.out.println("已插入条数：" + rst);
				rst++;
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

	private List<CExcelField> initExcelFields_hotsubquals() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("机构编码", "orgcode", true));
		efields.add(new CExcelField("机构", "orgname", true));
		efields.add(new CExcelField("开始月份", "startdate", true));
		efields.add(new CExcelField("结束月份", "enddate", true));
		efields.add(new CExcelField("申请原因", "applyreason", true));
		efields.add(new CExcelField("职位编码", "sp_code", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("津贴标准", "substand", true));
		return efields;
	}

	@ACOAction(eventname = "getYearRaiseQuato", Authentication = true, ispublic = true, notes = "获取年度调薪机构调薪额度")
	public String getYearRaiseQuato() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		Shworg org = new Shworg();
		if (orgid != null) {
			org.findByID(orgid);
			if (org.isEmpty())
				throw new Exception("id为【" + orgid + "】的机构不存在");
		}
		String salarydate = CorUtil.hashMap2Str(parms, "salarydate", "需要参数salarydate");
		Date quatodate = Systemdate.getDateByStr(salarydate);
		// Date quatodate =Systemdate.getFirstAndLastOfMonth(ct).date1;
		String[] ignParms = { "orgid" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT * FROM `hr_salary_yearraise_quota` WHERE salaryquotadate='" + Systemdate.getStrDateyyyy_mm_dd(quatodate) +
				"' AND usable=1 AND orgid="+orgid+" ORDER BY createtime DESC";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	/**
	 * includelv 包含离职的
	 * 
	 * @return
	 * @throws Exception
	 */
	@ACOAction(eventname = "findYearRaiseEmoloyeeList", Authentication = true, ispublic = false, notes = "根据登录权限查询年度调薪员工资料")
	public String findEmoloyeeList() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		String spcetype = parms.get("spcetype");
		List<JSONParm> jps = CJSON.getParms(eparms);

		Hr_employee he = new Hr_employee();
		String where = CjpaUtil.buildFindSqlByJsonParms(he, jps);

		String orgid = parms.get("orgid");
		if ((orgid != null) && (!orgid.isEmpty())) {
			Shworg org = new Shworg();
			org.findByID(orgid, false);
			if (org.isEmpty())
				throw new Exception("没发现ID为【" + orgid + "】的机构");
			where = where + " and idpath like '" + org.idpath.getValue() + "%'";
		}

		String qutaorgs = parms.get("qutaorgs");
		if ((qutaorgs != null) && (!qutaorgs.isEmpty())) {
			where = where + "and ( ";
			String[] orgs = qutaorgs.split(",");
			Shworg qutaorg = new Shworg();
			if (orgs.length > 0) {
				for (int i = 0; i < orgs.length; i++) {
					qutaorg.clear();
					qutaorg.findByID(orgs[i]);
					where = where + "(idpath like '" + qutaorg.idpath.getValue() + "%') or ";
				}
			}
			where = where.substring(0, where.length() - 3);
			where = where + ")";
		}

		if ((spcetype != null) && (Integer.valueOf(spcetype) == 1)) {
			where = where + " and employee_code='" + CSContext.getUserName() + "'";
		}
		String hdays = HrkqUtil.getParmValue("SA_YEARRAISE_HIREDDAY");// 筛选此入职日期在此之前的员工
		String specmonth = HrkqUtil.getParmValue("SA_YEARRAISE_SPEC");// 此月份之后特殊调薪金额为0的人
		String entprobmonth = HrkqUtil.getParmValue("SA_YEARRAISE_ENTPROB");// 此月份之后入职转正金额为0 的人
		String transprobmonth = HrkqUtil.getParmValue("SA_YEARRAISE_TRANSPROB");// 此月份之后调动转正金额为0 的人
		int lowsastand = Integer.valueOf(HrkqUtil.getParmValue("SA_YEARRAISE_LOWSASTANDARD"));// 调薪前工资低于该职位工资标准S5的人
		if(specmonth==null){
			specmonth=Systemdate.getStrDateyyyy_mm_dd();
		}else{
			specmonth = specmonth + "-01";
		}
		if(entprobmonth==null){
			entprobmonth=Systemdate.getStrDateyyyy_mm_dd();
		}else{
			entprobmonth = entprobmonth + "-01";
		}
		if(transprobmonth==null){
			transprobmonth=Systemdate.getStrDateyyyy_mm_dd();
		}else{
			transprobmonth = transprobmonth + "-01";
		}

		String chgdatesql1="SELECT DISTINCT er_id FROM `hr_salary_chglg` WHERE (stype=7 OR stype=9) AND sacrage>0 AND DATE_FORMAT(chgdate,'%Y-%m-01')>'"+specmonth+"' ";
		String chgdatesql2="SELECT DISTINCT er_id FROM `hr_salary_chglg` WHERE stype=5 AND sacrage>0 AND DATE_FORMAT(chgdate,'%Y-%m-01')>'"+entprobmonth+"' ";
		String chgdatesql3="SELECT DISTINCT er_id FROM `hr_salary_chglg` WHERE stype=6 AND sacrage>0 AND DATE_FORMAT(chgdate,'%Y-%m-01')>'"+transprobmonth+"' ";
		where=where+" AND er_id NOT IN ("+chgdatesql1+") AND er_id NOT IN ("+chgdatesql2+") AND er_id NOT IN ("+chgdatesql3+") AND hiredday<'"+hdays+"' ";

		String strincludelv = parms.get("includelv");
		boolean includelv = (strincludelv == null) ? false : Boolean.valueOf(strincludelv);
		String llvdate = parms.get("llvdate");// 最晚离职日期

		String smax = parms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);
		String sqlstr = "select * from hr_employee where usable=1";
		if ((llvdate != null) && (!llvdate.isEmpty())) {
			sqlstr = sqlstr + " and ( empstatid<=10 || ljdate<'" + llvdate + "') ";
		} else {
			if (!includelv)
				sqlstr = sqlstr + " and empstatid<=10 ";
		}
		sqlstr = sqlstr + CSContext.getIdpathwhere() + where + " limit 0," + max;
		if(lowsastand==1){
			String sqlstr1="SELECT emppws.* FROM ( SELECT emps.*,IFNULL(opws.psl5,0) AS psl5 FROM  ("+sqlstr+") emps LEFT JOIN "+
					" (SELECT pw.*,op.ospid AS oospid FROM `hr_salary_positionwage` pw,`hr_orgposition` op WHERE pw.stat=9 AND pw.usable=1 AND pw.ospid=op.sp_id) opws "+
					" ON emps.ospid=opws.oospid ) emppws,hr_salary_chglg cl  WHERE emppws.er_id=cl.er_id AND cl.useable=1 AND (cl.oldposition_salary<emppws.psl5 OR emppws.psl5=0) limit 0," + max;
			return he.pool.opensql2json(sqlstr1);
		}
		return he.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "getHasAdminAccess4Sa", Authentication = true, ispublic = true, notes = "是否有薪资管理员权限")
	public String getHasAdminAccess4Sa() throws Exception {
		JSONObject rst = new JSONObject();
		if (!HRUtil.hasRoles("19")) {// 薪酬管理员
			rst.put("accessed", 2);// 不是薪酬管理员
		} else {
			rst.put("accessed", 1);
		}
		return rst.toString();
	}

	@ACOAction(eventname = "getLinesTargetPrice", Authentication = true, ispublic = true, notes = "获取拉线每月考核的可分配金额")
	public String getLinesTargetPrice() throws Exception {
		/*
		 * HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		 * List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		 */
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "orgid参数不能为空");
		String ym = CorUtil.hashMap2Str(parms, "applydate", "applydate参数不能为空");
		String plcom = CorUtil.hashMap2Str(parms, "plcom", "plcom参数不能为空");
		String gs = CorUtil.hashMap2Str(parms, "gs", "gs参数不能为空");
		String sro = CorUtil.hashMap2Str(parms, "sro", "sro参数不能为空");
		String wd = CorUtil.hashMap2Str(parms, "wd", "wd参数不能为空");

		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("id为【" + orgid + "】的机构不存在");

		ym = ym + "-01";
		float tarcom = Float.parseFloat(plcom);
		float targs = Float.parseFloat(gs);
		float tarsro = Float.parseFloat(sro);
		float tarwd = Float.parseFloat(wd);
		float standard = 0;
		float canpay = 0;
		boolean iscomplete = false;
		String sqlstr = "SELECT ptl.*,pt.startdate,pt.enddate,pt.standardprice " +
				"FROM `hr_salary_projecttarget` pt,`hr_salary_projecttarget_line` ptl WHERE pt.stat=9 AND " +
				"pt.startdate<='" + ym + "' AND pt.enddate>='" + ym + "' AND ptl.pjt_id=pt.pjt_id " +
				" AND projecttype=1 AND LOCATE(ptl.idpath,'" + org.idpath.getValue() + "') ORDER BY ptl.idpath DESC limit 0,1";
		JSONArray temptc = org.pool.opensql2json_O(sqlstr);///// 计划达成率
		if (temptc.size() > 0) {
			JSONObject temptc1 = temptc.getJSONObject(0);
			String tc1targ = temptc1.getString("targets");// 计划达成率目标值
			String tc1weight = temptc1.getString("weight");// 计划达成后权重
			String tc1oper = temptc1.getString("operators");// 计划达成率运算符
			String tc1sprice = temptc1.getString("standardprice");// 分配金额标准
			float target = Float.parseFloat(tc1targ);
			int operator = Integer.valueOf(tc1oper);
			float weight = Float.parseFloat(tc1weight);
			float standardprice = Float.parseFloat(tc1sprice);
			if ((operator == 1) || (operator == 3)) {// 大于等于、大于
				if (target > tarcom) {
					standard = 0;
					canpay = 0;
				} else if ((operator == 3) && (target == tarcom)) {
					standard = 0;
					canpay = 0;
				} else {
					if (tarwd <= 10) {
						standard = 0;
						canpay = 0;
					} else if (tarwd <= 20) {
						iscomplete = true;
						standard = standardprice / 2;
						float comprice = standard * weight / 100;
						canpay = canpay + comprice;
					} else {
						iscomplete = true;
						standard = standardprice;
						float comprice = standard * weight / 100;
						canpay = canpay + comprice;
					}
				}
			}
		} else {
			standard = 0;
			canpay = 0;
		}
		if (iscomplete) {
			sqlstr = "SELECT ptl.*,pt.startdate,pt.enddate,pt.standardprice " +
					"FROM `hr_salary_projecttarget` pt,`hr_salary_projecttarget_line` ptl WHERE pt.stat=9 AND " +
					"pt.startdate<='" + ym + "' AND pt.enddate>='" + ym + "' AND ptl.pjt_id=pt.pjt_id " +
					" AND projecttype=2 AND LOCATE(ptl.idpath,'" + org.idpath.getValue() + "') ORDER BY ptl.idpath DESC limit 0,1";
			JSONArray tempgs = org.pool.opensql2json_O(sqlstr);///// 验货合格率
			if (tempgs.size() > 0) {
				JSONObject tempgs2 = tempgs.getJSONObject(0);
				String gs2targ = tempgs2.getString("targets");// 验货合格率目标值
				String gs2weight = tempgs2.getString("weight");// 验货合格率达成后权重
				String gs2oper = tempgs2.getString("operators");// 验货合格率运算符
				String gs2sprice = tempgs2.getString("standardprice");// 分配金额标准
				float target = Float.parseFloat(gs2targ);
				int operator = Integer.valueOf(gs2oper);
				float weight = Float.parseFloat(gs2weight);
				float standardprice = Float.parseFloat(gs2sprice);
				if ((operator == 1) || (operator == 3)) {// 大于等于、大于
					if (target > targs) {
						float gsprice = 0;
						canpay = canpay + gsprice;
					} else if ((operator == 3) && (target == targs)) {
						float gsprice = 0;
						canpay = canpay + gsprice;
					} else {
						float gsprice = standard * weight / 100;
						canpay = canpay + gsprice;
					}
				}
			}
		}
		if (iscomplete) {
			sqlstr = "SELECT ptl.*,pt.startdate,pt.enddate,pt.standardprice " +
					"FROM `hr_salary_projecttarget` pt,`hr_salary_projecttarget_line` ptl WHERE pt.stat=9 AND " +
					"pt.startdate<='" + ym + "' AND pt.enddate>='" + ym + "' AND ptl.pjt_id=pt.pjt_id " +
					" AND projecttype=3 AND LOCATE(ptl.idpath,'" + org.idpath.getValue() + "') ORDER BY ptl.idpath DESC limit 0,1";
			JSONArray tempsro = org.pool.opensql2json_O(sqlstr);///// 验货合格率
			if (tempsro.size() > 0) {
				JSONObject tempsro3 = tempsro.getJSONObject(0);
				String sro3targ = tempsro3.getString("targets");// 人员流失率目标值
				String sro3weight = tempsro3.getString("weight");// 人员流失率达成后权重
				String sro3oper = tempsro3.getString("operators");// 人员流失率运算符
				String sro3sprice = tempsro3.getString("standardprice");// 分配金额标准
				float target = Float.parseFloat(sro3targ);
				int operator = Integer.valueOf(sro3oper);
				float weight = Float.parseFloat(sro3weight);
				float standardprice = Float.parseFloat(sro3sprice);
				if ((operator == 2) || (operator == 4)) {// 小于等于、小于
					if (tarsro > target) {
						float sroprice = 0;
						canpay = canpay + sroprice;
					} else if ((operator == 4) && (target == tarsro)) {
						float sroprice = 0;
						canpay = canpay + sroprice;
					} else {
						float sroprice = standard * weight / 100;
						canpay = canpay + sroprice;
					}
				}
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("standard", standard);
		jo.put("canpay", canpay);
		return jo.toString();
	}

	@ACOAction(eventname = "getLinesTargets", Authentication = true, ispublic = true, notes = "获取某条拉线某月的考核指标")
	public String getLinesTargets() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "orgid参数不能为空");
		String ym = CorUtil.hashMap2Str(parms, "applydate", "applydate参数不能为空");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("id为【" + orgid + "】的机构不存在");
		ym = ym + "-01";
		String[] ignParms = { "orgid", "applydate" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT lt.applydate,lt.targetcomplete,ltl.* FROM `hr_salary_linestarget` lt,`hr_salary_linestarget_line` ltl " +
				"WHERE lt.stat=9 AND  ltl.slt_id=lt.slt_id AND lt.applydate='" + ym + "' AND ltl.orgid=" + org.orgid.getValue() + " order by createtime desc ";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "findTeamWardEmoloyeeList", Authentication = true, ispublic = false, notes = "根据登录权限查询团队奖拉线员工资料")
	public String findTeamWardEmoloyeeList() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		String spcetype = parms.get("spcetype");
		List<JSONParm> jps = CJSON.getParms(eparms);

		Hr_employee he = new Hr_employee();
		String where = CjpaUtil.buildFindSqlByJsonParms(he, jps);
		String ym = parms.get("applydate");
		if ((ym == null) || (ym.isEmpty())) {
			throw new Exception("需要参数applydate");
		}
		ym = ym + "-01";
		Date bgdate = Systemdate.getDateByStr(ym);// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String orgid = parms.get("orgid");
		if ((orgid == null) || (orgid.isEmpty())) {
			throw new Exception("需要orgid参数");
		}
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("没发现ID为【" + orgid + "】的机构");
		where = where + " and idpath like '" + org.idpath.getValue() + "%'";

		if ((spcetype != null) && (Integer.valueOf(spcetype) == 1)) {
			where = where + " and employee_code='" + CSContext.getUserName() + "'";
		}

		String smax = parms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);
		String sqlstr = "select * from hr_employee where usable=1 ";
		if ((ym != null) && (!ym.isEmpty())) {
			sqlstr = sqlstr + " AND ((empstatid<=10  OR (empstatid>=12 AND ljdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND ljdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "')) ";
		}
		sqlstr = sqlstr + " AND ospid IN (SELECT op.ospid FROM `hr_orgposition` op,`hr_standposition` sp " +
				"WHERE op.sp_id=sp.sp_id AND sp.isteamward=1 AND sp.usable=1 AND op.idpath LIKE '" + org.idpath.getValue() + "%') ";
		sqlstr = sqlstr + CSContext.getIdpathwhere() + where;
		sqlstr = sqlstr + ") or er_id IN (SELECT er_id FROM `hr_empptjob_app` WHERE stat=9 AND startdate<='" + ym + "' AND enddate>='" + ym + "' AND neworgid=" + orgid + ") ";
		sqlstr = sqlstr + " OR er_id IN (SELECT er_id FROM `hr_month_employee` WHERE yearmonth='" + parms.get("applydate") + "' AND idpath " +
				" LIKE '" + org.idpath.getValue() + "%' AND ospid IN (SELECT op.ospid FROM `hr_orgposition` op,`hr_standposition` sp WHERE op.sp_id=sp.sp_id AND sp.isteamward=1 " +
				"AND sp.usable=1 AND op.idpath LIKE '" + org.idpath.getValue() + "%'))";
		sqlstr = sqlstr + " limit 0," + max;
		return he.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "isPartTimeEmp", Authentication = true, ispublic = false, notes = "查询员工是否在某机构兼职")
	public String isPartTimeEmp() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String ym = parms.get("applydate");
		if ((ym == null) || (ym.isEmpty())) {
			throw new Exception("需要参数applydate");
		}
		ym = ym + "-01";
		String orgid = parms.get("orgid");
		if ((orgid == null) || (orgid.isEmpty())) {
			throw new Exception("需要orgid参数");
		}
		String erid = parms.get("er_id");
		if ((erid == null) || (erid.isEmpty())) {
			throw new Exception("需要er_id参数");
		}
		String sqlstr = "SELECT * FROM  `hr_empptjob_app` WHERE stat=9 AND startdate<='" + ym + "' AND enddate>='" + ym + "' AND neworgid=" + orgid + " AND er_id=" + erid;
		Hr_empptjob_app ptj = new Hr_empptjob_app();
		JSONArray temp = ptj.pool.opensql2json_O(sqlstr);
		JSONObject jo = new JSONObject();
		if (temp.size() > 0) {
			jo.put("isparttime", 1);
			sqlstr = "SELECT IFNULL(COUNT(*),0) AS ct FROM  `hr_empptjob_app` WHERE stat=9 AND startdate<='" + ym + "' AND enddate>='" + ym + "' AND er_id=" + erid;
			JSONArray pttemp = ptj.pool.opensql2json_O(sqlstr);
			JSONObject ptnums = pttemp.getJSONObject(0);
			jo.put("ptnums", ptnums.getString("ct"));
		} else {
			jo.put("isparttime", 2);
			jo.put("ptnums", 0);
		}
		return jo.toString();
	}

	@ACOAction(eventname = "getServerLinesCanpay", Authentication = true, ispublic = false, notes = "查询散件拉线的可分配金额")
	public String getServerLinesCanpay() throws Exception {// 根据总装拉线每月考核指标维护的数据计算得出
		HashMap<String, String> parms = CSContext.getParms();
		String ym = parms.get("applydate");
		if ((ym == null) || (ym.isEmpty())) {
			throw new Exception("需要参数applydate");
		}
		ym = ym + "-01";
		String orgid = parms.get("orgid");
		if ((orgid == null) || (orgid.isEmpty())) {
			throw new Exception("需要orgid参数");
		}
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("没发现ID为【" + orgid + "】的机构");
		String lorgids = parms.get("lorgids");
		if ((lorgids == null) || (lorgids.isEmpty())) {
			throw new Exception("需要lorgids参数");
		}
		String sqlstr = "SELECT IFNULL(ROUND(AVG(ltl.canpay)),0) AS avgcanpay FROM `hr_salary_linestarget` lt,`hr_salary_linestarget_line` ltl WHERE lt.stat=9 AND ltl.slt_id=lt.slt_id " +
				"AND lt.applydate='" + ym + "' AND LOCATE('" + org.idpath.getValue() + "',ltl.idpath)=1 AND ltl.orgid IN (" + lorgids + ")";
		Hr_empptjob_app ptj = new Hr_empptjob_app();
		JSONArray temp = ptj.pool.opensql2json_O(sqlstr);
		JSONObject jo = new JSONObject();
		if (temp.size() > 0) {
			jo.put("avgcanpay", temp.getJSONObject(0).getString("avgcanpay"));
		} else {
			jo.put("avgcanpay", 0);
		}
		return jo.toString();
	}

	@ACOAction(eventname = "getTeamWardList", Authentication = true, ispublic = true, notes = "获取团队管理奖明细")
	public String getTeamWardList() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		Shworg org = new Shworg();
		org.findBySQL("select * from shworg where code='" + orgcode + "'");
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONParm jpapplydate = CjpaUtil.getParm(jps, "applydate");
		if (jpapplydate == null)
			throw new Exception("需要参数【applydate】");
		String ym = jpapplydate.getParmvalue();
		String ymreloper = jpapplydate.getReloper();
		ym = ym + "-01";
		String[] ignParms = { "orgcode", "applydate" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT DATE_FORMAT(tw.applydate,'%Y-%m') AS applydate,twl.orgname AS lorgname,twl.servicelinesname," +
				"CONCAT(twl.plancomplete,'%') AS plancomplete,CONCAT(twl.goodsrate,'%') AS goodsrate," +
				"CONCAT(twl.staffrunoff,'%') AS staffrunoff,ROUND(twl.canpay) as canpay,twle.*,CONCAT(ROUND(twle.descriprev,1),'%') AS empdescriprev, " +
				"twle.shouldpay as empshouldpay,twle.realpay as emprealpay " +
				" FROM `hr_salary_teamaward` tw,`hr_salary_teamaward_line` twl,`hr_salary_teamaward_line_emps` twle " +
				" WHERE tw.stat=9 AND twl.tw_id=tw.tw_id AND twle.twl_id=twl.twl_id  AND tw.idpath LIKE '" + org.idpath.getValue() + "%' ";
		sqlstr = sqlstr + " and tw.applydate" + ymreloper + "'" + ym + "' ";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getLinesTargetList", Authentication = true, ispublic = true, notes = "获取拉线考核指标维护明细")
	public String getLinesTargetList() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		Shworg org = new Shworg();
		org.findBySQL("select * from shworg where code='" + orgcode + "'");
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONParm jpapplydate = CjpaUtil.getParm(jps, "applydate");
		if (jpapplydate == null)
			throw new Exception("需要参数【applydate】");
		String ym = jpapplydate.getParmvalue();
		String ymreloper = jpapplydate.getReloper();
		ym = ym + "-01";
		String[] ignParms = { "orgcode", "applydate" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT lt.applydate,ltl.orgid,ltl.orgname,ROUND(targetoutput) AS targetoutput,ROUND(realoutput) AS realoutput," +
				"CONCAT(plancomplete,'%') AS plancomplete,CONCAT(goodsrate,'%') AS goodsrate,CONCAT(staffrunoff,'%') AS staffrunoff,workdays," +
				"ROUND(standard) AS standard,ROUND(canpay) AS canpay  FROM `hr_salary_linestarget` lt,`hr_salary_linestarget_line` ltl " +
				"WHERE lt.stat=9 AND ltl.slt_id=lt.slt_id AND lt.applydate" + ymreloper + "'" + ym + "' AND ltl.idpath LIKE '" + org.idpath.getValue() + "%' ";
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "implinestarget_lineexcel", Authentication = true, ispublic = false, notes = "导入总装拉线每月考核指标维护明细")
	public String implinestarget_lineexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_linestarget_line(p, orgid);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_linestarget_line(Shw_physic_file pf, String orgid) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_linestarget_line(aSheet, orgid);
	}

	private String parserExcelSheet_linestarget_line(Sheet aSheet, String orgid) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_linestarget_line();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}

		CJPALineData<Hr_salary_linestarget_line> ltls = new CJPALineData<Hr_salary_linestarget_line>(Hr_salary_linestarget_line.class);
		Shworg lineorg = new Shworg();

		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String orgname = v.get("orgname");
			if ((orgname == null) || (orgname.isEmpty()))
				throw new Exception("拉线不能为空");

			String sqlstr = "select * from shworg where extorgname='" + orgname + "' ";

			lineorg.clear();
			lineorg.findBySQL(sqlstr);
			if (lineorg.isEmpty())
				throw new Exception("没找到名为【" + orgname + "】的机构！");

			Hr_salary_linestarget_line ltl = new Hr_salary_linestarget_line();
			ltl.orgid.setValue(lineorg.orgid.getValue());
			ltl.orgcode.setValue(lineorg.code.getValue());
			ltl.orgname.setValue(lineorg.extorgname.getValue());
			ltl.idpath.setValue(lineorg.idpath.getValue());
			ltl.targetoutput.setValue(v.get("targetoutput"));
			ltl.realoutput.setValue(v.get("realoutput"));
			float to = Float.parseFloat(v.get("targetoutput"));
			float ro = Float.parseFloat(v.get("realoutput"));
			float pc = (ro / to) * 100;
			ltl.plancomplete.setAsFloat(pc);
			ltl.goodsrate.setValue(v.get("goodsrate"));
			ltl.staffrunoff.setValue(v.get("staffrunoff"));
			ltl.workdays.setValue(v.get("workdays"));
			ltls.add(ltl);
		}
		return ltls.tojson();

	}

	private List<CExcelField> initExcelFields_linestarget_line() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("拉线", "orgname", true));
		efields.add(new CExcelField("目标产量", "targetoutput", true));
		efields.add(new CExcelField("实际产量", "realoutput", true));
		efields.add(new CExcelField("验货合格率", "goodsrate", true));
		efields.add(new CExcelField("人员流失率", "staffrunoff", true));
		efields.add(new CExcelField("开拉天数", "workdays", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "getHadTargetLines", Authentication = true, ispublic = true, notes = "获取某月已维护考核指标的拉线")
	public String getHadTargetLines() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "orgid参数不能为空");
		String ym = CorUtil.hashMap2Str(parms, "applydate", "applydate参数不能为空");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("id为【" + orgid + "】的机构不存在");
		ym = ym + "-01";
		Date month = Systemdate.getDateByStr(ym);
		Date nextmonth = Systemdate.dateMonthAdd(month, 1);
		String[] ignParms = { "orgid", "applydate" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT * FROM `hr_salary_linestarget_line` WHERE sltl_id IN " +
				"(SELECT MAX(sltl_id) FROM `hr_salary_linestarget` lt,`hr_salary_linestarget_line` ltl " +
				"WHERE lt.stat=9 AND  ltl.slt_id=lt.slt_id and ltl.canpay>0 AND lt.applydate='" + ym + "' AND " +
				"ltl.idpath LIKE '" + org.idpath.getValue() + "%' GROUP BY ltl.orgid)";
		JSONObject rst = new CReport(sqlstr, notnull).findReport2JSON_O(ignParms);
		JSONArray temlines = rst.getJSONArray("rows");
		for (int i = 0; i < temlines.size(); i++) {
			JSONObject line = temlines.getJSONObject(i);// 拉线
			String lidpath = line.getString("idpath");
			String sqlstr1 = "SELECT * FROM hr_employee WHERE usable=1  AND " +
					"((empstatid<=10  OR (empstatid>=12 AND ljdate>='" + ym + "' AND ljdate<'" + Systemdate.getStrDateyyyy_mm_dd(nextmonth) + "')) " +
					" AND ospid IN (SELECT op.ospid FROM `hr_orgposition` op,`hr_standposition` sp " +
					" WHERE op.sp_id=sp.sp_id AND sp.isteamward=1 AND sp.usable=1 AND op.idpath LIKE '" + org.idpath.getValue() + "%' ) " +
					" AND idpath LIKE '" + lidpath + "%' )  OR er_id IN (SELECT er_id FROM `hr_empptjob_app` WHERE stat=9 AND startdate<='" +
					ym + "' AND enddate>='" + ym + "' AND neworgid=" + line.getString("orgid") + ")";
			JSONObject linerst = new CReport(sqlstr1, notnull).findReport2JSON_O(ignParms);
			JSONArray lineemps = linerst.getJSONArray("rows");
			for (int j = 0; j < lineemps.size(); j++) {
				JSONObject emp = lineemps.getJSONObject(j);// 拉线人员
				String sqlstr2 = "SELECT COUNT(*) AS ct FROM `hr_empptjob_app` WHERE stat=9 " +
						"AND startdate<='" + ym + "' AND enddate>='" + ym + "'  AND er_id=" + emp.getString("er_id");
				int ptnums = Integer.valueOf(org.pool.openSql2List(sqlstr2).get(0).get("ct"));
				if (ptnums > 0) {
					emp.put("isparttime", 1);
					emp.put("ptnums", ptnums);
				} else {
					emp.put("isparttime", 2);
					emp.put("ptnums", 0);
				}
			}
			if (lineemps.size() > 0) {
				line.put("hr_salary_teamaward_line_empss", lineemps);
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("rows", temlines);
		String scols = parms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(temlines, scols);
			return null;
		}
	}

	@ACOAction(eventname = "impyearraise_quotaexcel", Authentication = true, ispublic = false, notes = "导入年度调薪额度")
	public String impyearraise_quotaexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_salary_yearraise_quota(p, batchno);
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

	private int parserExcelFile_salary_yearraise_quota(Shw_physic_file pf, String batchno) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_salary_yearraise_quota(aSheet, batchno);
	}

	private int parserExcelSheet_salary_yearraise_quota(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_salary_yearraise_quota();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);

		//CJPALineData<Hr_salary_yearraise_quota> yrqs = new CJPALineData<Hr_salary_yearraise_quota>(Hr_salary_yearraise_quota.class);
		//DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		int rst = 0;
		Shworg org=new Shworg();
		CDBConnection con = org.pool.getCon(this);
		con.startTrans();
		try{
			for (Map<String, String> v : values) {
				String orgname = v.get("orgname");
				if ((orgname == null) || (orgname.isEmpty()))
					throw new Exception("额度维护中的机构不能为空");

				org.clear();
				org.findBySQL("select * from shworg where extorgname='"+orgname+"' ");
				if(org.isEmpty())
					throw new Exception("没找到名为【" + orgname + "】的机构！");

				Hr_salary_yearraise_quota yrq = new Hr_salary_yearraise_quota();
				yrq.remark.setValue(v.get("remark")); // 备注
				yrq.yrqname.setValue(v.get("yrqname")); // 名称
				yrq.orgid.setValue(org.orgid.getValue());
				yrq.orgcode.setValue(org.code.getValue());
				yrq.orgname.setValue(org.extorgname.getValue());
				yrq.idpath.setValue(org.idpath.getValue());
				yrq.salary_quota_canuse.setValue(v.get("salary_quota_canuse")); // 可用工资额度
				yrq.salaryquotadate.setValue(v.get("salaryquotadate")); // 额度使用月份
				yrq.usable.setAsInt(1);//有效
				yrq.isused.setAsInt(2);//是否已使用
				yrq.save(con);
				yrq.wfcreate(null, con);
				//yrqs.add(yrq);
				System.out.println("====================导入年度调薪额度维护条数【" + rst + "】");
				rst++;
			}
			/*if (yrqs.size() > 0) {
				System.out.println("====================导入年度调薪额度维护条数【" + yrqs.size() + "】");
				yrqs.save();// 
			}
			yrqs.clear();*/
			con.submit();
			return rst;
		}catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private List<CExcelField> initExcelFields_salary_yearraise_quota() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("名称", "yrqname", true));
		efields.add(new CExcelField("机构", "orgname", true));
		efields.add(new CExcelField("额度使用月份", "salaryquotadate", true));
		efields.add(new CExcelField("可用工资额度", "salary_quota_canuse", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "getOrgMinStandard", Authentication = true, ispublic = true, notes = "获取机构最低工资标准")
	public String getOrgMinStandard() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		/*JSONObject result = new JSONObject();
		if (!HRUtil.hasRoles("71")) {// 薪酬管理员
			result.put("accessed", 2);
			return result.toString();
		}*/
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("id为【" + orgid + "】的机构不存在");
		String[] ignParms = {};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+org.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC LIMIT 0,1 ";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}

	@ACOAction(eventname = "getSubmitSalaryChgs", Authentication = true, ispublic = true, notes = "获取指定机构的待批报异动记录")
	public String getSubmitSalaryChgs() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "orgid参数不能为空");
		//String ym = CorUtil.hashMap2Str(parms, "submitdate", "submitdate参数不能为空");
		String ym = parms.get("submitdate");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("id为【" + orgid + "】的机构不存在");
		String[] ignParms = { "orgid", "submitdate" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT cl.*,e.employee_code,e.employee_name,e.orgcode,e.lv_id,e.hiredday,e.idpath FROM `hr_employee` e ,`hr_salary_chglg` cl WHERE e.idpath LIKE '"+org.idpath.getValue()
		+"%' AND cl.er_id=e.er_id ";
		if ((ym == null) || (ym.isEmpty())) {
			sqlstr=sqlstr+" and cl.useable=1 ";
		}else{
			ym = ym + "-01";
			Date bgdate = Systemdate.getDateByStr(ym);// 去除时分秒
			Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
			sqlstr=sqlstr+" AND cl.chgdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND cl.chgdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' "+
					" AND NOT EXISTS (SELECT * FROM hr_salary_chglg b WHERE cl.er_id=b.er_id AND b.chgdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) +
					"' AND b.chgdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' AND cl.createtime<createtime)";
		}
		if(org.orgid.getAsInt()==1){
			sqlstr=sqlstr+" AND e.idpath NOT LIKE '1,9979,%' AND e.idpath NOT LIKE '1,8761,%' ";
		}
		sqlstr=sqlstr+" and e.pay_way='月薪' ";
		String orderby="stype";
		return new CReport(sqlstr,orderby, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "impchgsubmit_listexcel", Authentication = true, ispublic = false, notes = "导入薪资异动提报明细Excel")
	public String impchgsubmit_listexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String submitdate = CorUtil.hashMap2Str(CSContext.getParms(), "submitdate", "需要参数submitdate");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_chgsubmitlist(p,submitdate);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_chgsubmitlist(Shw_physic_file pf,String submitdate) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_chgsubmitlist(aSheet,submitdate);
	}

	private String parserExcelSheet_chgsubmitlist(Sheet aSheet,String submitdate) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_chgsubmitlist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		/*
		 * Shworg org = new Shworg();
		 * org.findByID(orgid);
		 * if (org.isEmpty()) {
		 * throw new Exception("没找到ID为【" + orgid + "】的机构");
		 * }
		 */

		Hr_employee emp = new Hr_employee();
		CJPALineData<Hr_salary_chglgsubmit_line> tsls = new CJPALineData<Hr_salary_chglgsubmit_line>(Hr_salary_chglgsubmit_line.class);
		Shworg emporg = new Shworg();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("明细行上的工号不能为空");

			emp.clear();
			emp.findBySQL("SELECT * FROM `hr_employee` WHERE employee_code='" + employee_code + "'");
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料");
			emporg.clear();
			emporg.findByID(emp.orgid.getValue());
			if (emporg.isEmpty())
				throw new Exception("没找到员工【" + emp.employee_name.getValue() + "】所属的ID为【" + emp.orgid.getValue() + "】的机构");

			Hr_salary_chglgsubmit_line scsbl = new Hr_salary_chglgsubmit_line();

			scsbl.er_id.setValue(emp.er_id.getValue());
			scsbl.employee_code.setValue(emp.employee_code.getValue());
			scsbl.employee_name.setValue(emp.employee_name.getValue());
			scsbl.ospid.setValue(emp.ospid.getValue());
			scsbl.ospcode.setValue(emp.ospcode.getValue());
			scsbl.sp_name.setValue(emp.sp_name.getValue());
			scsbl.lv_id.setValue(emp.lv_id.getValue());
			scsbl.lv_num.setValue(emp.lv_num.getValue());
			scsbl.hiredday.setValue(emp.hiredday.getValue());
			scsbl.orgid.setValue(emp.orgid.getValue());
			scsbl.orgcode.setValue(emp.orgcode.getValue());
			scsbl.orgname.setValue(emp.orgname.getValue());
			scsbl.idpath.setValue(emp.idpath.getValue());
			Date bgdate = Systemdate.getDateByStr(submitdate);// 去除时分秒
			Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
			Hr_salary_chglg sc = new Hr_salary_chglg(); 
			sc.findBySQL("SELECT * FROM `hr_salary_chglg` WHERE er_id="+emp.er_id.getValue()+
					" AND chgdate>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND chgdate<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ORDER BY createtime DESC");
			if(sc.isEmpty()){
				sc=CtrSalaryCommon.getCur_salary_chglg(emp.er_id.getValue());
			}
			if(sc.orgid.getValue()!=null){
				scsbl.ospid.setValue(sc.ospid.getValue());
				scsbl.sp_name.setValue(sc.sp_name.getValue());
				scsbl.lv_num.setValue(sc.lv_num.getValue());
				scsbl.orgid.setValue(sc.orgid.getValue());
				scsbl.orgname.setValue(sc.orgname.getValue());
			}
			scsbl.sid.setValue(sc.sid.getValue()); // 
			scsbl.scode.setValue(sc.scode.getValue()); // 
			scsbl.scatype.setValue(sc.scatype.getValue()); // 变更类型 导入无类型
			scsbl.stype.setValue(sc.stype.getValue()); // 来源类型 历史数据
			scsbl.oldstru_id.setValue(sc.oldstru_id.getValue()); // 工资结构id
			scsbl.oldstru_name.setValue(sc.oldstru_name.getValue()); // 工资结构
			scsbl.oldchecklev.setValue(sc.oldchecklev.getValue()); // 工资结构id
			scsbl.oldattendtype.setValue(sc.oldattendtype.getValue()); // 工资结构
			scsbl.oldposition_salary.setValue(sc.oldposition_salary.getValue()); // 职位工资
			scsbl.oldbase_salary.setValue(sc.oldbase_salary.getValue()); // 基本工资
			scsbl.oldotwage.setValue(sc.oldotwage.getValue()); // 固定加班工资
			scsbl.oldtech_salary.setValue(sc.oldtech_salary.getValue()); // 技能工资
			scsbl.oldachi_salary.setValue(sc.oldachi_salary.getValue()); // 绩效工资
			scsbl.oldtech_allowance.setValue(sc.oldtech_allowance.getValue()); // 技能津贴
			scsbl.oldparttimesubs.setValue(sc.oldparttimesubs.getValue()); // 兼职津贴
			scsbl.oldpostsubs.setValue(sc.oldpostsubs.getValue()); // 岗位津贴
			scsbl.oldcalsalarytype.setValue(sc.oldcalsalarytype.getValue()); // 计薪方式
			scsbl.newstru_id.setValue(sc.newstru_id.getValue()); // 工资结构id
			scsbl.newstru_name.setValue(sc.newstru_name.getValue()); // 工资结构
			scsbl.newchecklev.setValue(sc.newchecklev.getValue()); // 考核层级
			scsbl.newattendtype.setValue(sc.newattendtype.getValue()); // 出勤类别
			scsbl.newposition_salary.setValue(sc.newposition_salary.getValue()); // 职位工资
			scsbl.newbase_salary.setValue(sc.newbase_salary.getValue()); // 基本工资
			scsbl.newotwage.setValue(sc.newotwage.getValue()); // 固定加班工资
			scsbl.newtech_salary.setValue(sc.newtech_salary.getValue()); // 技能工资
			scsbl.newachi_salary.setValue(sc.newachi_salary.getValue()); // 绩效工资
			scsbl.newtech_allowance.setValue(sc.newtech_allowance.getValue()); // 技能津贴
			scsbl.newparttimesubs.setValue(sc.newparttimesubs.getValue()); // 兼职津贴
			scsbl.newpostsubs.setValue(sc.newpostsubs.getValue()); // 岗位津贴
			scsbl.newcalsalarytype.setValue(sc.newcalsalarytype.getValue()); // 调薪后计薪方式
			scsbl.sacrage.setValue(sc.sacrage.getValue());//调薪幅度
			scsbl.chgdate.setValue(sc.chgdate.getValue());//调薪日期
			scsbl.chgreason.setValue(sc.chgreason.getValue());//调薪原因
			scsbl.useable.setValue(sc.useable.getValue());//有效

			scsbl.remark.setValue(v.get("remark"));
			tsls.add(scsbl);
		}
		return tsls.tojson();

	}

	private List<CExcelField> initExcelFields_chgsubmitlist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "getSubmitSalaryChgsLists", Authentication = true, ispublic = true, notes = "获取薪资异动报批明细")
	public String getSubmitSalaryChgsLists() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpym = CjpaUtil.getParm(jps, "submitdate");
		//String ym = CorUtil.hashMap2Str(parms, "submitdate", "submitdate参数不能为空");


		String[] ignParms = { "orgcode", "submitdate" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT cs.cls_code,DATE_FORMAT(cs.submitdate,'%Y-%m') as submitdate,DATE_FORMAT(csl.chgdate,'%Y-%m') as lchgdate,"+
				"csl.employee_code,csl.employee_name,csl.orgname,csl.sp_name,csl.lv_num,csl.hiredday,csl.stype,csl.sacrage,csl.remark,"+
				"csl.oldstru_name,ROUND(csl.oldposition_salary) as oldposition_salary,ROUND(csl.oldbase_salary) as oldbase_salary,ROUND(csl.oldotwage) as oldotwage,"+
				"ROUND(csl.oldtech_salary) as oldtech_salary,ROUND(csl.oldachi_salary) as oldachi_salary,ROUND(csl.oldtech_allowance) as oldtech_allowance,"+
				"ROUND(csl.oldparttimesubs) as oldparttimesubs,ROUND(csl.oldpostsubs) as oldpostsubs,"+
				"csl.newstru_name,ROUND(csl.newposition_salary) as newposition_salary,ROUND(csl.newbase_salary) as newbase_salary,ROUND(csl.newotwage) as newotwage,"+
				"ROUND(csl.newtech_salary) as newtech_salary,ROUND(csl.newachi_salary) as newachi_salary,ROUND(csl.newtech_allowance) as newtech_allowance,"+
				"ROUND(csl.newparttimesubs) as newparttimesubs,ROUND(csl.newpostsubs) as newpostsubs "+
				" FROM `hr_salary_chglgsubmit` cs,`hr_salary_chglgsubmit_line` csl WHERE cs.stat=9  AND csl.cls_id=cs.cls_id ";
		if (jpym == null) {

		}else{
			String ym = jpym.getParmvalue();
			ym = ym + "-01";
			Date bgdate = Systemdate.getDateByStr(ym);// 去除时分秒
			Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
			sqlstr=sqlstr+" AND cs.submitdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND cs.submitdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		}
		if (jporgcode != null) {
			String orgcode =  jporgcode.getParmvalue();
			Shworg org = new Shworg();
			org.findBySQL("select * from shworg where code='" + orgcode + "'");
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
			sqlstr=sqlstr+" AND csl.idpath LIKE '"+org.idpath.getValue()+"%' ";
		}
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getLinesSubmitSalary", Authentication = true, ispublic = true, notes = "获取单位下拉线的报批数据")
	public String getLinesSubmitSalary() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String ym = CorUtil.hashMap2Str(parms, "applydate", "applydate参数不能为空");
		ym = ym + "-01";
		Date bgdate = Systemdate.getDateByStr(ym);// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		/*JSONObject result = new JSONObject();
		if (!HRUtil.hasRoles("71")) {// 薪酬管理员
			result.put("accessed", 2);
			return result.toString();
		}*/
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("id为【" + orgid + "】的机构不存在");
		String[] ignParms = { "orgid","applydate"};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT IFNULL(SUM(twl.canpay),0) AS tcanpay,IFNULL(SUM(twl.lrealpay),0) AS lrealpay FROM `hr_salary_teamaward` tw,`hr_salary_teamaward_line` twl "+
				"WHERE tw.stat=9 AND tw.applydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND tw.applydate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + 
				"' AND twl.idpath LIKE '"+org.idpath.getValue()+"%' AND twl.tw_id=tw.tw_id ";
		JSONObject linerst = new CReport(sqlstr, notnull).findReport2JSON_O(ignParms).getJSONArray("rows").getJSONObject(0);
		sqlstr="SELECT COUNT(DISTINCT twl.orgid) AS linenums,COUNT(DISTINCT twle.er_id) AS payempnums,IFNULL(SUM(twle.realpay),0) AS tshouldpay,"+
				"IFNULL(ROUND(SUM(twle.realpay)/COUNT(DISTINCT twle.er_id),2),0) AS avgpay FROM `hr_salary_teamaward` tw,`hr_salary_teamaward_line` twl,"+
				"`hr_salary_teamaward_line_emps` twle WHERE tw.stat=9 AND tw.applydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + 
				"' AND tw.applydate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate)+"' AND twl.idpath LIKE '"+org.idpath.getValue()+"%' AND twl.tw_id=tw.tw_id AND twle.twl_id=twl.twl_id";
		JSONObject rst = new CReport(sqlstr, notnull).findReport2JSON_O(ignParms).getJSONArray("rows").getJSONObject(0);
		rst.put("tcanpay", linerst.getString("tcanpay"));
		return rst.toString();
	}

	@ACOAction(eventname = "impteamwardsubmit_listexcel", Authentication = true, ispublic = false, notes = "导入团队奖提报明细Excel")
	public String impteamwardsubmit_listexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		String submitdate = CorUtil.hashMap2Str(CSContext.getParms(), "submitdate", "需要参数submitdate");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_teamwardsubmitlist(p,orgid,submitdate);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_teamwardsubmitlist(Shw_physic_file pf,String orgid,String submitdate) throws Exception {
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
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_teamwardsubmitlist(aSheet,orgid,submitdate);
	}

	private String parserExcelSheet_teamwardsubmitlist(Sheet aSheet,String orgid,String submitdate) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_teamwardsubmitlist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}
		submitdate = submitdate + "-01";
		Date bgdate = Systemdate.getDateByStr(submitdate);// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String[] ignParms = { "orgid","submitdate"};// 忽略的查询条件
		String[] notnull = {};
		CJPALineData<Hr_salary_teamawardsubmit_line> twsls = new CJPALineData<Hr_salary_teamawardsubmit_line>(Hr_salary_teamawardsubmit_line.class);
		Shworg lineorg = new Shworg();
		// DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String orgname = v.get("orgname");
			if ((orgname == null) || (orgname.isEmpty()))
				throw new Exception("明细行上的单位不能为空");
			lineorg.findBySQL("SELECT * FROM `shworg` WHERE extorgname LIKE '%"+orgname+"%' AND idpath LIKE '"+org.idpath.getValue()+"%' ORDER BY idpath");

			Hr_salary_teamawardsubmit_line twsl = new Hr_salary_teamawardsubmit_line();
			twsl.orgid.setValue(lineorg.orgid.getValue());
			twsl.orgcode.setValue(lineorg.code.getValue());
			twsl.orgname.setValue(lineorg.extorgname.getValue());
			twsl.idpath.setValue(lineorg.idpath.getValue());
			String sqlstr="SELECT IFNULL(SUM(twl.canpay),0) AS tcanpay,IFNULL(SUM(twl.lrealpay),0) AS lrealpay FROM `hr_salary_teamaward` tw,`hr_salary_teamaward_line` twl "+
					"WHERE tw.stat=9 AND tw.applydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND tw.applydate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + 
					"' AND twl.idpath LIKE '"+lineorg.idpath.getValue()+"%' AND twl.tw_id=tw.tw_id ";
			JSONArray temlines =new CReport(sqlstr, notnull).findReport2JSON_O(ignParms).getJSONArray("rows");
			if(temlines.size()>0){
				JSONObject linerst = temlines.getJSONObject(0);
				twsl.tcanpay.setValue(linerst.getString("tcanpay")); // 本月可分配总金额
			}else{
				twsl.tcanpay.setValue(0); // 本月可分配总金额
			}
			sqlstr="SELECT COUNT(DISTINCT twl.orgid) AS linenums,COUNT(DISTINCT twle.er_id) AS payempnums,IFNULL(SUM(twle.realpay),0) AS tshouldpay,"+
					"IFNULL(ROUND(SUM(twle.realpay)/COUNT(DISTINCT twle.er_id),2),0) AS avgpay FROM `hr_salary_teamaward` tw,`hr_salary_teamaward_line` twl,"+
					"`hr_salary_teamaward_line_emps` twle WHERE tw.stat=9 AND tw.applydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + 
					"' AND tw.applydate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate)+"' AND twl.idpath LIKE '"+lineorg.idpath.getValue()+"%' AND twl.tw_id=tw.tw_id AND twle.twl_id=twl.twl_id";
			JSONArray temlrst =new CReport(sqlstr, notnull).findReport2JSON_O(ignParms).getJSONArray("rows");
			if(temlrst.size()>0){
				JSONObject rst = temlrst.getJSONObject(0);
				twsl.linenums.setValue(rst.getString("linenums")); // 拉线数
				twsl.payempnums.setValue(rst.getString("payempnums")); // 本月参与分配人数
				twsl.tshouldpay.setValue(rst.getString("tshouldpay")); // 实际分配金额
				twsl.avgpay.setValue(rst.getString("avgpay")); // 人均分配金额
			}else{
				twsl.linenums.setValue(0); // 拉线数
				twsl.payempnums.setValue(0); // 本月参与分配人数
				twsl.tshouldpay.setValue(0); // 实际分配金额
				twsl.avgpay.setValue(0); // 人均分配金额
			}

			twsl.useable.setValue(1);//有效
			twsl.remark.setValue(v.get("remark"));
			twsls.add(twsl);
		}
		return twsls.tojson();

	}

	private List<CExcelField> initExcelFields_teamwardsubmitlist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("单位", "orgname", true));
		efields.add(new CExcelField("年月", "submitdate", false));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "getSubmitTeamWardLists", Authentication = true, ispublic = true, notes = "获取团队奖报批明细")
	public String getSubmitTeamWardLists() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		String orgcode =  jporgcode.getParmvalue();
		if ((orgcode == null) || (orgcode.isEmpty())) {
			throw new Exception("orgcode参数不能为空");
		}
		//String ym = CorUtil.hashMap2Str(parms, "submitdate", "submitdate参数不能为空");
		JSONParm jpym = CjpaUtil.getParm(jps, "submitdate");
		Shworg org = new Shworg();
		org.findBySQL("select * from shworg where code='" + orgcode + "'");
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "orgcode", "submitdate" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT ts.submitdate,ts.ttcanpay,ts.ttshouldpay,tsl.* FROM `hr_salary_teamawardsubmit` ts,`hr_salary_teamawardsubmit_line` tsl "+
				"WHERE ts.stat=9 AND tsl.tws_id=ts.tws_id  AND tsl.idpath LIKE '"+org.idpath.getValue()+"%' ";
		if (jpym == null) {

		}else{
			String ym = jpym.getParmvalue();
			ym = ym + "-01";
			Date bgdate = Systemdate.getDateByStr(ym);// 去除时分秒
			Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
			sqlstr=sqlstr+" AND ts.submitdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND ts.submitdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		}
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	@ACOAction(eventname = "getYearRaiseOrgEmps", Authentication = true, ispublic = true, notes = "获取年度调薪选定单位下的人事档案数据")
	public String getYearRaiseOrgEmps() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		//String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String salarydate = CorUtil.hashMap2Str(parms, "salarydate", "salarydate参数不能为空");
		String qutaorgs = CorUtil.hashMap2Str(parms, "qutaorgs", "需要参数qutaorgs");

		String[] orgs = qutaorgs.split(",");
		Shworg qutaorg = new Shworg();
		String where = " and (";
		for (int j = 0; j < orgs.length; j++) {
			qutaorg.clear();
			qutaorg.findByID(orgs[j]);
			if (!qutaorg.isEmpty()) {
				where = where + " (idpath like '" + qutaorg.idpath.getValue() + "%' ) or";
			}
		}
		where = where.substring(0, where.length() - 3);
		where = where + ")";
		String hdays = HrkqUtil.getParmValue("SA_YEARRAISE_HIREDDAY");// 筛选此入职日期在此之前的员工
		String specmonth = HrkqUtil.getParmValue("SA_YEARRAISE_SPEC");// 此月份之后特殊调薪金额为0的人
		String entprobmonth = HrkqUtil.getParmValue("SA_YEARRAISE_ENTPROB");// 此月份之后入职转正金额为0 的人
		String transprobmonth = HrkqUtil.getParmValue("SA_YEARRAISE_TRANSPROB");// 此月份之后调动转正金额为0 的人
		int lowsastand = Integer.valueOf(HrkqUtil.getParmValue("SA_YEARRAISE_LOWSASTANDARD"));// 调薪前工资低于该职位工资标准S5的人
		String[] ignParms = { "qutaorgs"};// 忽略的查询条件
		String[] notnull = {};
		String empsqlstr = "SELECT * FROM hr_employee WHERE empstatid<10 and pay_way='月薪' AND hiredday<'"+hdays+"' " + where;
		if(specmonth==null){
			specmonth=Systemdate.getStrDateyyyy_mm_dd();
		}else{
			specmonth = specmonth + "-01";
		}
		if(entprobmonth==null){
			entprobmonth=Systemdate.getStrDateyyyy_mm_dd();
		}else{
			entprobmonth = entprobmonth + "-01";
		}
		if(transprobmonth==null){
			transprobmonth=Systemdate.getStrDateyyyy_mm_dd();
		}else{
			transprobmonth = transprobmonth + "-01";
		}

		String chgdatesql1="SELECT DISTINCT er_id FROM `hr_salary_chglg` WHERE (stype=7 OR stype=9) AND sacrage>0 AND DATE_FORMAT(chgdate,'%Y-%m-01')>'"+specmonth+"' ";
		String chgdatesql2="SELECT DISTINCT er_id FROM `hr_salary_chglg` WHERE stype=5 AND sacrage>0 AND DATE_FORMAT(chgdate,'%Y-%m-01')>'"+entprobmonth+"' ";
		String chgdatesql3="SELECT DISTINCT er_id FROM `hr_salary_chglg` WHERE stype=6 AND sacrage>0 AND DATE_FORMAT(chgdate,'%Y-%m-01')>'"+transprobmonth+"' ";
		empsqlstr=empsqlstr+" AND er_id NOT IN ("+chgdatesql1+") AND er_id NOT IN ("+chgdatesql2+") AND er_id NOT IN ("+chgdatesql3+") ";
		JSONObject rst = new CReport(empsqlstr, notnull).findReport2JSON_O(ignParms);
		JSONArray tempemps = rst.getJSONArray("rows");
		JSONArray emprst=new JSONArray();
		for (int i = 0; i < tempemps.size(); i++) {
			JSONObject temp = tempemps.getJSONObject(i);// 拉线
			temp.put("orghrlev", 0);
			Hr_salary_chglg sc = CtrSalaryCommon.getCur_salary_chglg(temp.getString("er_id"));
			if(lowsastand==1){
				Hr_salary_positionwage ptw=new Hr_salary_positionwage();
				ptw.findBySQL("SELECT pw.* FROM `hr_salary_positionwage` pw,`hr_orgposition` op WHERE pw.stat=9 AND pw.usable=1 AND pw.ospid=op.sp_id AND op.ospid="+temp.getString("ospid"));
				if(!ptw.isEmpty()){
					if(ptw.psl5.getAsFloatDefault(0)<sc.newposition_salary.getAsFloatDefault(0)){
						continue;
					}
				}
			}
			temp.put("oldstru_id", sc.newstru_id.getValue());
			temp.put("oldstru_name", sc.newstru_name.getValue());
			temp.put("oldchecklev", sc.newchecklev.getValue());
			temp.put("oldkqtype", sc.newattendtype.getValue());
			temp.put("oldposition_salary", sc.newposition_salary.getValue());// 调薪前职位工资
			temp.put("oldbase_salary", sc.newbase_salary.getValue());// 调薪前基本工资
			temp.put("oldtech_salary", sc.newtech_salary.getValue());// 调薪前技能工资
			temp.put("oldachi_salary", sc.newachi_salary.getValue());// 调薪前绩效工资
			temp.put("oldotwage", sc.newotwage.getValue());// 调薪前固定加班工资
			temp.put("oldtech_allowance", sc.newtech_allowance.getValue());// 调薪前技术津贴

			DecimalFormat df=new DecimalFormat(".00");
			float chgbase_salary=0;
			float chgtech_salary=0;
			float chgachi_salary=0;
			float chgotwage=0;
			Hr_salary_structure ss = new Hr_salary_structure();
			if(!sc.newstru_id.isEmpty()){
				ss.findByID(sc.newstru_id.getValue());
				if (ss.isEmpty()) {
					throw new Exception("工号为【" + temp.getString("employee_code") + "】的调薪后工资结构为空！");
				}

				float newposition_salary=sc.newposition_salary.getAsFloatDefault(0);


				if(ss.strutype.getAsInt()==1){
					float minstand=0;
					String minsqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+temp.getString("idpath")+"',idpath)=1  ORDER BY idpath DESC  ";
					Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
					oms.findBySQL(minsqlstr);
					if(oms.isEmpty()){
						minstand=0;
					}else{
						minstand=oms.minstandard.getAsFloatDefault(0);
					}
					float bw=(newposition_salary*ss.basewage.getAsFloatDefault(0))/100;
					float bow=(newposition_salary*ss.otwage.getAsFloatDefault(0))/100;
					float sw=(newposition_salary*ss.skillwage.getAsFloatDefault(0))/100;
					float pw=(newposition_salary*ss.meritwage.getAsFloatDefault(0))/100;
					if(minstand>bw){
						if((bw+bow)>minstand){
							bow=bw+bow-minstand;
							bw=minstand;
						}else if((bw+bow+sw)>minstand){
							sw=bw+bow+sw-minstand;
							bow=0;
							bw=minstand;
						}else if((bw+bow+sw+pw)>minstand){
							pw=bw+bow+sw+pw-minstand;
							sw=0;
							bow=0;
							bw=minstand;
						}else{
							bw=newposition_salary;
							pw=0;
							sw=0;
							bow=0;
						}
					}
					temp.put("newbase_salary", df.format(bw));// 调薪后基本工资
					temp.put("newtech_salary", df.format(sw)); // 调薪后技能工资
					temp.put("newachi_salary", df.format(pw)); // 调薪后绩效工资
					temp.put("newotwage", df.format(bow)); // 调薪后固定加班工资
					chgbase_salary=bw-sc.newbase_salary.getAsFloatDefault(0);
					chgtech_salary=sw-sc.newtech_salary.getAsFloatDefault(0);
					chgachi_salary=pw-sc.newachi_salary.getAsFloatDefault(0);
					chgotwage=bow-sc.newotwage.getAsFloatDefault(0);
				}else{
					temp.put("newbase_salary", 0);// 调薪后基本工资
					temp.put("newtech_salary", 0); // 调薪后技能工资
					temp.put("newachi_salary", 0); // 调薪后绩效工资
					temp.put("newotwage", 0); // 调薪后固定加班工资

					String nbs=temp.getString("newbase_salary");
					if((nbs == null) || (nbs.isEmpty())){
						nbs="0";
					}
					String nts=temp.getString("newtech_salary");
					if((nts == null) || (nts.isEmpty())){
						nts="0";
					}
					String nas=temp.getString("newachi_salary");
					if((nas == null) || (nas.isEmpty())){
						nas="0";
					}
					String notw=temp.getString("newotwage");
					if((notw == null) || (notw.isEmpty())){
						notw="0";
					}
					chgbase_salary=Float.valueOf(nbs)-sc.newbase_salary.getAsFloatDefault(0);
					chgtech_salary=Float.valueOf(nts)-sc.newtech_salary.getAsFloatDefault(0);
					chgachi_salary=Float.valueOf(nas)-sc.newachi_salary.getAsFloatDefault(0);
					chgotwage=Float.valueOf(notw)-sc.newotwage.getAsFloatDefault(0);
				}
				temp.put("newstru_id", ss.stru_id.getValue()); // 调薪后工资结构ID
				temp.put("newstru_name", ss.stru_name.getValue()); // 调薪后工资结构名
				temp.put("newchecklev", ss.checklev.getValue());// 调薪后绩效考核层级
				temp.put("newkqtype", ss.kqtype.getValue()); // 调薪后出勤类别

			}else{
				temp.put("newstru_id", sc.newstru_id.getValue());
				temp.put("newstru_name", sc.newstru_name.getValue());
				temp.put("newchecklev", sc.newchecklev.getValue());
				temp.put("newkqtype", sc.newattendtype.getValue());
				temp.put("newposition_salary", sc.newposition_salary.getValue());// 调薪前职位工资
				temp.put("newbase_salary", sc.newbase_salary.getValue());// 调薪前基本工资
				temp.put("newtech_salary", sc.newtech_salary.getValue());// 调薪前技能工资
				temp.put("newachi_salary", sc.newachi_salary.getValue());// 调薪前绩效工资
				temp.put("newotwage", sc.newotwage.getValue());// 调薪前固定加班工资
				temp.put("newtech_allowance", sc.newtech_allowance.getValue());// 调薪前技术津贴
			}
			temp.put("newposition_salary", sc.newposition_salary.getValue()); // 调薪后职位工资
			temp.put("newtech_allowance", sc.newtech_allowance.getValue()); // 调薪后技术津贴
			float chgposition_salary=sc.newposition_salary.getAsFloatDefault(0)-sc.newposition_salary.getAsFloatDefault(0);
			float chgtech_allowance=0;
			float pbtsarylev=chgposition_salary+chgtech_allowance;
			float pbtsarylev_chgtech=0;
			if((sc.newposition_salary.getAsFloatDefault(0)==0)||(sc.newposition_salary.getAsFloatDefault(0)==0)){
				pbtsarylev_chgtech=0;
			}else{
				pbtsarylev_chgtech=(pbtsarylev/(sc.newposition_salary.getAsFloatDefault(0)+sc.newtech_allowance.getAsFloatDefault(0)))*100;
			}
			temp.put("chgposition_salary", chgposition_salary); // 职位工资调薪幅度
			temp.put("chgbase_salary", chgbase_salary); // 基本工资调薪幅度
			temp.put("chgtech_salary", chgtech_salary); // 技能工资调薪幅度
			temp.put("chgachi_salary", chgachi_salary);// 绩效工资调薪幅度
			temp.put("chgotwage", chgotwage);// 基本加班工资调薪幅度
			temp.put("chgtech_allowance", chgtech_allowance);// 技术津贴调薪幅度
			temp.put("pbtsarylev", pbtsarylev);// 调薪金额
			temp.put("overf_salary", 0);// 超标金额
			temp.put("overf_salary_chgtech", 0);// 超标比例
			temp.put("pbtsarylev_chgtech", df.format(pbtsarylev_chgtech));// 调整幅度
			temp.put("remark", "按机构生成调薪名单");
			JSONObject jo = new JSONObject();
			jo = getLastYearChgSa(temp.getString("er_id"),salarydate, jo);
			temp.put("dateentry", jo.getString("dateentry")); // 上一年入职转正调薪月份
			temp.put("chgentry", jo.getString("chgentry")); // 上一年入职转正调薪
			temp.put("datetransfer", jo.getString("datetransfer")); // 上一年调动转正调薪月份
			temp.put("chgtransfer", jo.getString("chgtransfer")); // 上一年调动转正调薪
			temp.put("datespec", jo.getString("datespec")); // 上一年特殊调薪月份
			temp.put("chgspec", jo.getString("chgspec")); // 上一年特殊调薪
			JSONObject hljo = new JSONObject();
			hljo = LastYearHoliday(temp.getString("er_id"),salarydate, hljo);
			temp.put("lastyqqdays", hljo.getString("hdays")); // 上一年请假/缺勤天数
			JSONObject expjo = new JSONObject();
			expjo = LastYearEXPoint(temp.getString("er_id"),salarydate, expjo);
			temp.put("lastavgallowance", expjo.getString("expoint"));// 上一年平均绩效系数
			emprst.add(temp);
		}
		JSONObject jo = new JSONObject();
		jo.put("rows", emprst);
		String scols = parms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(emprst, scols);
			return null;
		}
	}

	@ACOAction(eventname = "getCurrentSalaryStruce", Authentication = true, ispublic = true, notes = "获取指定的工资结构")
	public String getCurrentSalaryStruce() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String stru_id = CorUtil.hashMap2Str(parms, "stru_id", "需要参数stru_id");
		String[] ignParms = { "orgid","applydate"};// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "select * from hr_salary_structure where stat=9 and stru_id="+stru_id;
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
	}

	/*
	 * 邵工2020-8-3
	 */
	@ACOAction(eventname = "getSalaryReportYearRaisesh", Authentication = true, ispublic = true, notes = "获取年度调薪报表导出")
	public String getSalaryReportYearRaisesh() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		String[] ignParms = { "salarydate", "orgcode", "employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = getSalarySearchSQL(jps, 11, 0);
		String orderstr = " 1 ";
		return new CReport(sqlstr, orderstr, notnull).findReport(ignParms, null);
	}

}
