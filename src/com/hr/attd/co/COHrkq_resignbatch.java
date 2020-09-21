package com.hr.attd.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

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
import com.corsair.server.util.UpLoadFileEx;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hrkq.resignbatch")
public class COHrkq_resignbatch {

	@ACOAction(eventname = "imprgbexcel", Authentication = true, ispublic = false, notes = "批量补签明细行导入")
	public String imprgbexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserresignbatchExcelFileex(p, orgid);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}

	private String parserresignbatchExcelFileex(Shw_physic_file pf, String orgid) throws Exception {
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
		return parserresignbatchExcelSheetex(aSheet, orgid);
	}

	private String parserresignbatchExcelSheetex(Sheet aSheet, String orgid) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "";
		}
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");

		List<CExcelField> efds = initresignbatchExcelFieldsex();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 1);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 1);

		JSONArray rst = new JSONArray();
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				continue;
			Hr_employee emp = new Hr_employee();
			String sqlstr = "SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "' and idpath like '" + org.idpath.getValue() + "%'";
			emp.findBySQL(sqlstr);
			if (emp.isEmpty())
				throw new Exception("限定机构，工号【" + employee_code + "】的人事资料不存在");
			JSONObject jo = new JSONObject();
			jo.put("er_id", emp.er_id.getValue());
			jo.put("employee_code", emp.employee_code.getValue());
			jo.put("employee_name", emp.employee_name.getValue());
			jo.put("orgid", emp.orgid.getValue());
			jo.put("orgcode", emp.orgcode.getValue());
			jo.put("orgname", emp.orgname.getValue());
			jo.put("ospid", emp.ospid.getValue());
			jo.put("ospcode", emp.ospcode.getValue());
			jo.put("sp_name", emp.sp_name.getValue());
			jo.put("lv_num", emp.lv_num.getValue());
			jo.put("kqdate", v.get("kqdate"));
			jo.put("bcno", v.get("bcno"));
			jo.put("rgtime", v.get("rgtime"));
			rst.add(jo);
		}
		return rst.toString();
	}

	private List<CExcelField> initresignbatchExcelFieldsex() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("考勤日期", "kqdate", true));
		efields.add(new CExcelField("班次号", "bcno", true));
		efields.add(new CExcelField("班次时间", "rgtime", true));
		return efields;
	}
}
