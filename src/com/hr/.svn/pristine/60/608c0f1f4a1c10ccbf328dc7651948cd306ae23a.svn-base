package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_employeestat;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_leavejobbatchline;

@ACO(coname = "web.hr.leavejobbatch")
public class COHr_leavejobbatch {
	@ACOAction(eventname = "impexcel", Authentication = true, notes = "从excel导入离职信息")
	public String impexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		String ljbtype = CorUtil.hashMap2Str(CSContext.getParms(), "ljbtype", "需要参数ljbtype");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile(p, orgid, ljbtype);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}

	private String parserExcelFile(Shw_physic_file pf, String orgid, String ljbtype) throws Exception {
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
		return parserExcelSheet(aSheet, orgid, ljbtype);
	}

	// 工号 申请离职日期 离职日期 离职方式 离职原因 是否补偿 补偿金额 是否投诉 是否仲裁 是否诉讼 案号 加入黑名单 入黑原因
	private String parserExcelSheet(Sheet aSheet, String orgid, String ljbtype) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);

		Hr_employee emp = new Hr_employee();
		int lt = Integer.valueOf(ljbtype);
		String sdfdname = (lt == 2) ? "allowlv" : "allowsxlv";
		String sqlstr = "SELECT statvalue FROM hr_employeestat WHERE " + sdfdname + "=1";
		List<HashMap<String, String>> allowlvs = emp.pool.openSql2List(sqlstr);
		List<Integer> als = new ArrayList<Integer>();
		for (HashMap<String, String> allowlv : allowlvs) {
			als.add(Integer.valueOf(allowlv.get("statvalue")));
		}
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		CJPALineData<Hr_leavejobbatchline> ls = new CJPALineData<Hr_leavejobbatchline>(Hr_leavejobbatchline.class);

		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				continue;

			Shworg org = new Shworg();
			org.findByID(orgid);
			if (org.isEmpty())
				throw new Exception("ID为【" + orgid + "】的机构不存在!");
			sqlstr = "select * from hr_employee where employee_code='" + employee_code + "'";
			sqlstr = sqlstr + " and orgid in (select orgid from shworg where idpath like '" + org.idpath.getValue() + "%')";
			emp.findBySQL(sqlstr, false);
			if (emp.isEmpty())
				throw new Exception("没有找到工号为【" + employee_code + "】的员工资料");
			int empstatid = emp.empstatid.getAsInt();
			if (!isinarr(empstatid, als)) {
				Hr_employeestat es = new Hr_employeestat();
				es.findByID(String.valueOf(empstatid));
				throw new Exception("员工【" + employee_code + "】人事状态为【" + es.language1.getValue() + "】不允许该项操作");
			}

			Hr_leavejobbatchline l = new Hr_leavejobbatchline();
			l.er_id.setValue(emp.er_id.getValue());
			l.employee_code.setValue(emp.employee_code.getValue());
			l.id_number.setValue(emp.id_number.getValue());
			l.employee_name.setValue(emp.employee_name.getValue());
			l.hiredday.setValue(emp.hiredday.getValue());
			l.registeraddress.setValue(emp.registeraddress.getValue());
			l.sex.setValue(emp.sex.getValue());
			l.birthday.setValue(emp.birthday.getValue());
			l.degree.setValue(emp.degree.getValue());
			l.lv_id.setValue(emp.lv_id.getValue());
			l.lv_num.setValue(emp.lv_num.getValue());
			l.hg_id.setValue(emp.hg_id.getValue());
			l.hg_code.setValue(emp.hg_code.getValue());
			l.hg_name.setValue(emp.hg_name.getValue());
			l.ospid.setValue(emp.ospid.getValue());
			l.ospcode.setValue(emp.ospcode.getValue());
			l.sp_name.setValue(emp.sp_name.getValue());
			l.pempstatid.setValue(emp.empstatid.getValue());
			l.ljappdate.setValue(v.get("ljappdate"));
			l.ljdate.setValue(v.get("ljdate"));
			l.ljtype1.setValue(dictemp.getVbCE("782", v.get("ljtype1"), false, "工号【" + employee_code + "】离职类别【" + v.get("ljtype1") + "】不存在")); // 离职类别
			l.ljtype2.setValue(dictemp.getVbCE("1045", v.get("ljtype2"), false, "工号【" + employee_code + "】离职类型【" + v.get("ljtype2") + "】不存在")); // 离职类型
			l.ljreason.setValue(dictemp.getVbCE("1049", v.get("ljreason"), false, "工号【" + employee_code + "】离职原因【" + v.get("ljreason") + "】不存在")); // 离职原因
			l.iscpst.setValue(dictemp.getValueByCation("5", v.get("iscpst"), "2")); // 是否补偿
			l.cpstarm.setValue(v.get("cpstarm")); // 补偿金额
			l.iscpt.setValue(dictemp.getValueByCation("5", v.get("iscpt"), "2")); // 是否投诉
			l.isabrt.setValue(dictemp.getValueByCation("5", v.get("isabrt"), "2")); // 是否仲裁
			l.islawsuit.setValue(dictemp.getValueByCation("5", v.get("islawsuit"), "2")); // 是否诉讼
			l.isblacklist.setValue(dictemp.getValueByCation("5", v.get("isblacklist"), "2")); // 是否加入黑名单
			boolean addb = (l.isblacklist.getAsInt() == 1);
			// System.out.println("工号【" + employee_code + "】加封类型【" + v.get("addtype") + "】");
			// System.out.println("工号【" + employee_code + "】加封类别【" + v.get("addtype1") + "】");
			l.addtype.setValue(dictemp.getVbCE("1071", v.get("addtype"), !addb, "工号【" + employee_code + "】加封类型【" + v.get("addtype") + "】不存在")); // 加封类型
			l.addtype1.setValue(dictemp.getVbCE("1074", v.get("addtype1"), !addb, "工号【" + employee_code + "】加封类别【" + v.get("addtype1") + "】不存在")); // 加封类别
			l.blackreason.setValue(v.get("blackreason")); // 加入黑名单原因
			l.iscanced.setAsInt(2);
			ls.add(l);
		}
		return ls.tojson();
	}

	private List<CExcelField> initExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("申请离职日期", "ljappdate", true));
		efields.add(new CExcelField("离职日期", "ljdate", true));
		efields.add(new CExcelField("离职类型", "ljtype2", true));
		efields.add(new CExcelField("离职类别", "ljtype1", true));
		efields.add(new CExcelField("离职原因", "ljreason", true));
		efields.add(new CExcelField("是否补偿", "iscpst", true));
		efields.add(new CExcelField("补偿金额", "cpstarm", true));
		efields.add(new CExcelField("是否投诉", "iscpt", true));
		efields.add(new CExcelField("是否仲裁", "isabrt", true));
		efields.add(new CExcelField("是否诉讼", "islawsuit", true));
		efields.add(new CExcelField("加入黑名单", "isblacklist", true));
		efields.add(new CExcelField("加封类型", "addtype", true));
		efields.add(new CExcelField("加封类别", "addtype1", true));
		efields.add(new CExcelField("加封原因", "blackreason", true));
		return efields;
	}

	private boolean isinarr(int empstatid, List<Integer> als) {
		for (int al : als) {
			if (empstatid == al)
				return true;
		}
		return false;
	}

}
