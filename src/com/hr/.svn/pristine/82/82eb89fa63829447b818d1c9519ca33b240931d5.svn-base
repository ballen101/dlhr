package com.hr.base.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hrrl_standlib;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.rlsl")
public class COHrrl_standlib {
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
		Hrrl_standlib rlsl = new Hrrl_standlib();
		CDBConnection con = emp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				rst++;
				rlsl.clear();
				rlsl.rlname.setValue(v.get("rlname"));
				rlsl.rllabel_b.setValue(v.get("rllabel_b"));
				rlsl.rllabel_a.setValue(v.get("rllabel_a"));
				rlsl.rltype1.setValue(dictemp.getVbCE("1117", v.get("rltype1"), true, "关联关系类型【" + v.get("rltype1") + "】不存在")); //
				rlsl.rltype2.setValue(dictemp.getVbCE("1120", v.get("rltype2"), true, "关联关系类别【" + v.get("rltype2") + "】不存在")); //
				rlsl.hrvlevel.setValue(dictemp.getVbCE("1133", v.get("hrvlevel"), true, "关联关系级别【" + v.get("hrvlevel") + "】不存在")); //
				rlsl.rlext.setValue(v.get("rlext"));
				rlsl.remark.setValue(v.get("remark"));
				rlsl.save(con);
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
		efields.add(new CExcelField("关联关系", "rlname", true));
		efields.add(new CExcelField("关联人称谓", "rllabel_b", true));
		efields.add(new CExcelField("关联人自称", "rllabel_a", true));
		efields.add(new CExcelField("关联关系类型", "rltype1", true));
		efields.add(new CExcelField("关联关系类别", "rltype2", true));
		efields.add(new CExcelField("关联关系级别", "hrvlevel", true));
		efields.add(new CExcelField("说明", "rlext", false));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}
}
