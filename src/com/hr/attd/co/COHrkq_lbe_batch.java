package com.hr.attd.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.util.Systemdate;
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
import com.hr.attd.entity.Hrkq_lbe_batch_line;
import com.hr.attd.entity.Hrkq_leave_blance;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hrkq.lbeb")
public class COHrkq_lbe_batch {
	@ACOAction(eventname = "implbebatchlistexcel", Authentication = true, ispublic = false, notes = "导入批量展期明细Excel")
	public String implbebatchlistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		String emplev = CorUtil.hashMap2Str(CSContext.getParms(), "emplev", "需要参数emplev");
		String sccode = CorUtil.hashMap2Str(CSContext.getParms(), "sccode", "需要参数sccode");
		String newvaldate = CorUtil.hashMap2Str(CSContext.getParms(), "newvaldate", "需要参数newvaldate");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile_lbebatch(p, orgid, emplev, sccode, newvaldate);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else {
			return "[]";
		}

	}

	private String parserExcelFile_lbebatch(Shw_physic_file pf, String orgid, String emplev, String sccode, String newvaldate) throws Exception {
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
		return parserExcelSheet_lbebatch(aSheet, orgid, emplev, sccode, newvaldate);
	}

	private String parserExcelSheet_lbebatch(Sheet aSheet, String orgid, String emplev, String sccode, String newvaldate) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		List<CExcelField> efds = initExcelFields_lbebatch();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}
		int lev = Integer.parseInt(emplev);

		Hr_employee emp = new Hr_employee();
		CJPALineData<Hrkq_lbe_batch_line> lbebls = new CJPALineData<Hrkq_lbe_batch_line>(Hrkq_lbe_batch_line.class);
		Shworg emporg = new Shworg();
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (Map<String, String> v : values) {
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("批量展期明细上的工号不能为空");

			emp.clear();
			emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料");
			if (lev == 1) {
				if (emp.lv_num.getAsFloat() < 3) {
					//System.out.println("工号为【" + employee_code + "】的员工不在本次展期的职级范围内！");
					continue;
				}
			} else {
				if (emp.lv_num.getAsFloat() >= 3) {
					//System.out.println("工号为【" + employee_code + "】的员工不在本次展期的职级范围内！");
					continue;
				}
			}
			emporg.clear();
			emporg.findByID(emp.orgid.getValue());
			if (emporg.isEmpty())
				throw new Exception("没找到员工【" + emp.employee_name.getValue() + "】所属的ID为【" + emp.orgid.getValue() + "】的机构");
			if (emporg.idpath.getValue().indexOf(org.idpath.getValue()) == -1) {
				throw new Exception("员工【" + employee_code + "】不属于【" + org.extorgname.getValue() + "】机构");
			}
			// int stype = Integer.valueOf(dictemp.getVbCE("1019", v.get("stype"), false, "员工【" + emp.employee_name.getValue() + "】的来源类型【" + v.get("stype")
			// + "】不存在"));
			// String valdate = v.get("valdate");
			// String newvaldate = v.get("newvaldate");
			// Date newdate = Systemdate.getDateByStr(newvaldate);// 去除时分秒
			// Date vdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(valdate)));// 去除时分秒
			// Date enddate = Systemdate.dateMonthAdd(vdate, 3);// 加一月
			// if (newdate.getTime() > enddate.getTime()) {
			// System.out.println("员工【" + employee_code + "】的展期时间不能大于3个月！");
			// continue;
			// }
			Hrkq_leave_blance app = new Hrkq_leave_blance();
			String sqlstr = "SELECT * FROM hrkq_leave_blance WHERE er_id=" + emp.er_id.getValue()
					+ " AND stype=1 AND sccode='" + sccode + "'";
			app.findBySQL(sqlstr);
			if (app.isEmpty()) {
				//System.out.println("没有找到工号为【" + employee_code + "】的员工的可调休信息！");
				continue;
			}

			Hrkq_lbe_batch_line lbebl = new Hrkq_lbe_batch_line();

			lbebl.er_id.setValue(emp.er_id.getValue());
			lbebl.employee_code.setValue(emp.employee_code.getValue());
			lbebl.employee_name.setValue(emp.employee_name.getValue());
			lbebl.ospid.setValue(emp.ospid.getValue());
			lbebl.ospcode.setValue(emp.ospcode.getValue());
			lbebl.sp_name.setValue(emp.sp_name.getValue());
			lbebl.lv_num.setValue(emp.lv_num.getValue());
			lbebl.hg_name.setValue(emp.hg_name.getValue());
			lbebl.orgid.setValue(emp.orgid.getValue());
			lbebl.orgcode.setValue(emp.orgcode.getValue());
			lbebl.orgname.setValue(emp.orgname.getValue());
			lbebl.idpath.setValue(emp.idpath.getValue());
			lbebl.valdate.setValue(app.valdate.getValue());
			// lbebl.newvaldate.setAsDatetime(Systemdate.dateMonthAdd(app.valdate.getAsDate(), 3));
			lbebl.newvaldate.setValue(newvaldate);
			// String ext_reason = v.get("ext_reason");
			// lbebl.ext_reason.setValue(ext_reason);
			lbebl.stype.setAsInt(1);
			lbebl.sid.setAsInt(0);
			lbebl.alllbtime.setValue(app.alllbtime.getValue());
			lbebl.usedlbtime.setValue(app.usedlbtime.getValue());
			lbebl.lbid.setValue(app.lbid.getValue());
			lbebl.lbname.setValue(app.lbname.getValue());
			// lbebl.remark.setValue(v.get("remark"));
			lbebls.add(lbebl);
		}
		return lbebls.tojson();

	}

	private List<CExcelField> initExcelFields_lbebatch() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", false));
		return efields;
	}

}
