package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Types;
import java.util.ArrayList;
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

import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
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
import com.hr.perm.entity.Hr_employee_leanexp;

@ACO(coname = "web.hr.employee.leanexp")
public class COHr_employee_leanexp {

	@ACOAction(eventname = "getleanexplist", Authentication = true, ispublic = true, notes = "获取培训列表信息")
	public String getrewardlist() throws Exception {
		String sqlstr = "SELECT e.employee_code,e.employee_name,e.orgname,e.sp_name,e.hg_name,e.lv_num,e.idpath,l.* "
				+ " FROM  hr_employee e,hr_employee_leanexp l "
				+ " WHERE e.er_id=l.er_id ";
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			sqlstr = sqlstr + " and l.emple_id=" + id;
		} else
			sqlstr = sqlstr + " order by emple_id desc";
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
		Hr_employee_leanexp er = new Hr_employee_leanexp();
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
				er.begintime.setValue(v.get("begintime")); // 入学时间
				er.endtime.setValue(v.get("endtime")); // 毕业时间
				er.schoolname.setValue(v.get("schoolname"));// 学校名称
				er.major.setValue(v.get("major")); // 院系专业
				er.degree.setValue(dictemp.getVbCE("84", v.get("degree"), true, "工号【" + employee_code + "】学历【" + v.get("degree") + "】不存在")); // 学历
				er.certnum.setValue(v.get("certnum"));// 学位证编号
				er.gcernum.setValue(v.get("gcernum")); // 毕业证编号
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
		efields.add(new CExcelField("入学时间", "begintime", true));
		efields.add(new CExcelField("毕业时间", "endtime", true));
		efields.add(new CExcelField("学校名称", "schoolname", true));
		efields.add(new CExcelField("院系专业", "major", false));
		efields.add(new CExcelField("学历", "degree", false));
		efields.add(new CExcelField("学位证编号", "certnum", false));
		efields.add(new CExcelField("毕业证编号", "gcernum", false));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}
}
