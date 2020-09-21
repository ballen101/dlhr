package com.hr.attd.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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

import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
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
import com.hr.attd.entity.Hrkq_leave_blance;
import com.hr.attd.entity.Hrkq_sched;
import com.hr.attd.entity.Hrkq_workschmonthline;
import com.hr.attd.entity.Hrkq_workschmonthlist;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hrkq.wscm")
public class COHrkq_workschmonthlist {

	@ACOAction(eventname = "findwscm", Authentication = true, notes = "查询某人某月排班")
	public String findsched() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		String yearmonth = CorUtil.hashMap2Str(parms, "yearmonth");

		String sqlstr = "select * from hrkq_workschmonthlist where er_id=" + er_id;
		if ((yearmonth != null) && (!yearmonth.isEmpty()))
			sqlstr = sqlstr + " and yearmonth='" + yearmonth + "'";
		Hrkq_workschmonthlist wscl = new Hrkq_workschmonthlist();
		return wscl.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findschedline", Authentication = true, notes = "查询某人某月某日排班明细")
	public String findschedline() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		String resdate = CorUtil.hashMap2Str(parms, "resdate", "需要参数resdate");
		Date dt = Systemdate.getDateByStr(resdate);
		String yearmonth = Systemdate.getStrDateByFmt(dt, "yyyy-MM");
		String sqlstr = "select * from hrkq_workschmonthlist where er_id=" + er_id;
		sqlstr = sqlstr + " and yearmonth='" + yearmonth + "'";
		Hrkq_workschmonthlist wscl = new Hrkq_workschmonthlist();
		wscl.findBySQL(sqlstr);
		if (wscl.isEmpty())
			throw new Exception("没有发现该员工选定月份排班记录");
		int d = Integer.valueOf(Systemdate.getStrDateByFmt(dt, "dd"));
		// System.out.println("d:" + d);
		String scid = wscl.cfield("scid" + d).getValue();
		// System.out.println("scid" + scid);
		if ((scid == null) || (scid.isEmpty()))
			throw new Exception("没有发现该员工选定日期排班记录");
		Hrkq_sched sc = new Hrkq_sched();
		sc.findByID(scid);
		if (sc.isEmpty())
			throw new Exception("没有发现该员工选定日期排班记录");
		return sc.tojson();
	}

	@ACOAction(eventname = "impexcel", Authentication = true, ispublic = false, notes = "导入排班信息")
	public String impexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		CJPALineData<Hrkq_workschmonthline> rst = new CJPALineData<Hrkq_workschmonthline>(Hrkq_workschmonthline.class);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile(p, batchno);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
		return rst.tojson();
	}

	private CJPALineData<Hrkq_workschmonthline> parserExcelFile(Shw_physic_file pf, String batchno) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}
		@SuppressWarnings("resource")
//		Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//				: new XSSFWorkbook(new FileInputStream(fullname));
		
		Workbook workbook = WorkbookFactory.create(file);
		
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet

		return parserExcelSheet(aSheet, batchno);
	}

	private CJPALineData<Hrkq_workschmonthline> parserExcelSheet(Sheet aSheet, String batchno) throws Exception {
		CJPALineData<Hrkq_workschmonthline> rst = new CJPALineData<Hrkq_workschmonthline>(Hrkq_workschmonthline.class);
		if (aSheet.getLastRowNum() == 0) {
			return rst;
		}
		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_employee emp = new Hr_employee();
		Shworg org = new Shworg();
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存

		for (Map<String, String> v : values) {
			Hrkq_workschmonthline wml = new Hrkq_workschmonthline();
			String tcode = v.get("tcode");
			String sstype = dictemp.getVbCE("919", v.get("ttype"), false, "类型【" + v.get("ttype") + "】不存在");
			int stype = Integer.valueOf(sstype);
			wml.ttype.setAsInt(stype);
			if (stype == 2) {// 机构
				org.clear();
				String sqlstr = "select * from shworg where code='" + tcode + "'";
				org.findBySQL(sqlstr, false);
				if (org.isEmpty())
					throw new Exception("编号为【" + tcode + "】的机构不存在");
				wml.tid.setValue(org.orgid.getValue());
				wml.tcode.setValue(org.code.getValue());
				wml.tname.setValue(org.extorgname.getValue());
			} else if (stype == 3) {// 个人
				emp.clear();
				String sqlstr = "select * from hr_employee where employee_code='" + tcode + "'";
				emp.findBySQL(sqlstr, false);
				if (emp.isEmpty())
					throw new Exception("工号为【" + tcode + "】的人事资料不存在");
				wml.tid.setValue(emp.er_id.getValue());
				wml.tcode.setValue(emp.employee_code.getValue());
				wml.tname.setValue(emp.employee_name.getValue());
			} else {
				throw new Exception("类型【" + v.get("ttype") + "】不存在");
			}
			rst.add(wml);
		}
		// throw new Exception("不给通过");
		return rst;
	}

	private List<CExcelField> initExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("编码", "tcode", true));
		efields.add(new CExcelField("类型", "ttype", true));
		return efields;
	}

}
