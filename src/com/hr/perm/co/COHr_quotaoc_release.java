package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_wclass;
import com.hr.perm.entity.Hr_quotaoc_releaseline;

@ACO(coname = "web.hr.quotaocrelease")
public class COHr_quotaoc_release {

	@ACOAction(eventname = "findClass4QC", Authentication = true, notes = "查询可编制控制的职类")
	public String findClass4QC() throws Exception {
		String sqlstr = "SELECT * FROM hr_wclass WHERE type_id=1 AND usable=1 AND hwc_id IN("
				+ "SELECT parmvalue FROM hr_systemparms WHERE usable=1 AND parmcode='BATCH_QUATA_CLASS')";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "impexcel", Authentication = true, notes = "从excel导入编制发布信息")
	public String impexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");

		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile(p);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}

	private String parserExcelFile(Shw_physic_file pf) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		// HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fullname));
		Workbook workbook = WorkbookFactory.create(file);
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet(aSheet);
	}

	// 序号 机构编码 机构名称 职类编码 职类名称 编制预算
	private String parserExcelSheet(Sheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		int orgcol = -1;
		int wccol = -1;
		int quotacol = -1;
		Row aRow = aSheet.getRow(0);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			Cell aCell = aRow.getCell(col);
			String celltext = getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			if ("机构编码".equals(celltext.trim())) {
				orgcol = col;
			}
			if ("职类编码".equals(celltext.trim())) {
				wccol = col;
			}
			if ("编制预算".equals(celltext.trim())) {
				quotacol = col;
			}
		}
		if ((orgcol == -1) || (wccol == -1) || (quotacol == -1)) {
			throw new Exception("没找到【机构编码】或【职类编码】或【编制预算】列");
		}

		CJPALineData<Hr_quotaoc_releaseline> rls = new CJPALineData<Hr_quotaoc_releaseline>(Hr_quotaoc_releaseline.class);
		Shworg org = new Shworg();
		Hr_wclass wc = new Hr_wclass();

		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {
				Cell aCell = aRow.getCell(orgcol);
				String orgcode = getCellValue(aCell);
				if ((orgcode == null) || (orgcode.isEmpty()))
					continue;
				if (orgcode.indexOf("e") >= 0) {
					BigDecimal db = new BigDecimal(orgcode);
					orgcode = db.toPlainString();
				}
				org.findBySQL("select * from shworg where code='" + orgcode + "'", false);
				if (org.isEmpty())
					throw new Exception("没有找到编码为【" + orgcode + "】的机构");

				aCell = aRow.getCell(wccol);
				String hw_code = getCellValue(aCell);
				if ((hw_code == null) || (hw_code.isEmpty()))
					hw_code = "0";

				String sqlstr = "select * from hr_wclass where hw_code='" + hw_code + "'"
						+ "AND hwc_id IN(SELECT parmvalue FROM hr_systemparms WHERE usable=1 AND parmcode='BATCH_QUATA_CLASS')";
				wc.findBySQL(sqlstr, false);
				if (wc.isEmpty())
					throw new Exception("没有找到编码为【" + hw_code + "】的职类或者该职类不允许批调");
				if (wc.type_id.getAsInt() != 1)
					throw new Exception("编码【" + hw_code + "】不是职类");

				aCell = aRow.getCell(quotacol);
				String quota = getCellValue(aCell);
				if ((quota == null) || (quota.isEmpty()))
					quota = "0";
				Hr_quotaoc_releaseline qr = new Hr_quotaoc_releaseline();
				qr.hwc_idzl.setValue(wc.hwc_id.getValue());
				qr.hw_codezl.setValue(wc.hw_code.getValue());
				qr.hwc_namezl.setValue(wc.hwc_name.getValue());
				qr.orgid.setValue(org.orgid.getValue());
				qr.orgcode.setValue(org.code.getValue());
				qr.orgname.setValue(org.orgname.getValue());
				qr.quota.setValue(quota);
				rls.add(qr);
			}
		}
		return rls.tojson();
	}

	private String getCellValue(Cell aCell) {
		if (aCell != null) {
			int cellType = aCell.getCellType();
			switch (cellType) {
			case 0:// Numeric
				return String.valueOf(aCell.getNumericCellValue()).toLowerCase();
			case 1:// String
				return aCell.getStringCellValue().toLowerCase();
			default:
				return null;
			}
		} else
			return null;
	}
}
