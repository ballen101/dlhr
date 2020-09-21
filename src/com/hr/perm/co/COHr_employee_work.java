package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_work;

@ACO(coname = "web.hr.employee.work")
public class COHr_employee_work {
	@ACOAction(eventname = "getworklist", Authentication = true, ispublic = true, notes = "获取列表信息")
	public String getrewardlist() throws Exception {
		String sqlstr = "SELECT e.employee_code,e.employee_name,e.orgname,e.sp_name,e.hg_name,e.lv_num,e.idpath,l.* "
				+ " FROM  hr_employee e,hr_employee_work l "
				+ " WHERE e.er_id=l.er_id ";
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			sqlstr = sqlstr + " and l.empl_id=" + id;
		} else
			sqlstr = sqlstr + " order by empl_id desc";
		return new CReport(sqlstr, null).findReport();
	}

	@ACOAction(eventname = "impexcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String impexcel() throws Exception {
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

//		Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//				: new XSSFWorkbook(new FileInputStream(fullname));
		
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
		Hr_employee_work er = new Hr_employee_work();
		CDBConnection con = emp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
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
					throw new Exception("工号【" + employee_code + "】不存在人事资料");

				er.clear();
				er.er_id.setValue(emp.er_id.getValue());
				er.company.setValue(v.get("company")); // 工作单位
				er.start_date.setValue(v.get("start_date")); // 起始日期
				er.end_date.setValue(v.get("end_date")); // 结束日期
				er.func.setValue(v.get("func")); // 职务
				er.work_type.setValue(null); // 工种
				er.jobexp.setValue(v.get("jobexp")); // 工作内容
				er.endsarary.setValue(v.get("endsarary")); // 离职薪资
				er.position_desc.setValue(null); // 任职岗位
				er.witness.setValue(v.get("witness")); // 证明人
				er.witness_tel.setValue(v.get("witness_tel")); // 联系电话
				er.is_group_experience.setValue("2"); // 是否集团经历
				er.is_vocation_experience.setValue("2"); // 是否行业经历
				er.remark.setValue(v.get("remark")); // 备注
				er.save(con);
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

	private List<CExcelField> initExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("工作单位", "company", true));
		efields.add(new CExcelField("起始日期", "start_date", true));
		efields.add(new CExcelField("结束日期", "end_date", true));
		efields.add(new CExcelField("职务", "func", true));
		efields.add(new CExcelField("主要工作", "jobexp", true));
		efields.add(new CExcelField("离职薪资", "endsarary", true));
		efields.add(new CExcelField("证明人", "witness", true));
		efields.add(new CExcelField("联系电话", "witness_tel", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}
}
