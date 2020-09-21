package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_quota_adjustline;

@ACO(coname = "web.hr.quotaadjust")
public class COHr_quota_adjust {

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

		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fullname));
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		HSSFSheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet(aSheet);
	}

	// 序号 机构编码 机构名称 职位编码 职位名称 调整类型 调整数量
	private String parserExcelSheet(HSSFSheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		int ospcodecol = -1;
		int adjtpcol = -1;
		int adjqtcol = -1;
		HSSFRow aRow = aSheet.getRow(0);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			HSSFCell aCell = aRow.getCell(col);
			String celltext = getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			if ("职位编码".equals(celltext.trim())) {
				ospcodecol = col;
			}
			if ("调整类型".equals(celltext.trim())) {
				adjtpcol = col;
			}
			if ("调整数量".equals(celltext.trim())) {
				adjqtcol = col;
			}
		}
		if ((ospcodecol == -1) || (adjtpcol == -1) || (adjqtcol == -1)) {
			throw new Exception("没找到【职位编码】或【调整类型】或【调整数量】列");
		}

		CJPALineData<Hr_quota_adjustline> rls = new CJPALineData<Hr_quota_adjustline>(Hr_quota_adjustline.class);
		Hr_orgposition hop = new Hr_orgposition();

		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {

				HSSFCell aCell = aRow.getCell(ospcodecol);
				String ospcode = getCellValue(aCell);
				if ((ospcode == null) || (ospcode.isEmpty()))
					continue;
				if (ospcode.indexOf("e") >= 0) {
					BigDecimal db = new BigDecimal(ospcode);
					ospcode = db.toPlainString();
				}

				aCell = aRow.getCell(adjtpcol);
				String adjtp = getCellValue(aCell);
				if ((adjtp == null) || (adjtp.isEmpty()))
					new Exception("调整类型错误");

				aCell = aRow.getCell(adjqtcol);
				String adjqt = getCellValue(aCell);
				if ((adjqt == null) || (adjqt.isEmpty()))
					adjqt = "0";

				hop.findBySQL("select * from hr_orgposition where ospcode = '" + ospcode + "'");
				if (!hop.isEmpty()) {
					if (hop.usable.getAsInt() == 1) {
						Hr_quota_adjustline qadjl = new Hr_quota_adjustline();
						qadjl.orgid.setValue(hop.orgid.getValue());
						qadjl.orgname.setValue(hop.orgname.getValue());
						qadjl.orgcode.setValue(hop.orgcode.getValue());
						qadjl.ospid.setValue(hop.ospid.getValue());
						qadjl.ospcode.setValue(hop.ospcode.getValue());
						qadjl.sp_name.setValue(hop.sp_name.getValue());
						qadjl.oldquota.setValue(hop.quota.getValue());
						if (adjtp.equals("增加"))
							qadjl.adjtype.setAsInt(1);
						else if (adjtp.equals("减少"))
							qadjl.adjtype.setAsInt(2);
						else
							throw new Exception("调整类型【" + adjtp + "】错误");
						qadjl.adjquota.setValue(adjqt);
						rls.add(qadjl);
					} else {
						throw new Exception("职位编码【" + ospcode + "】设置为不可用！");
					}
				} else {
					throw new Exception("职位编码【" + ospcode + "】不存在！");
				}
			}
		}
		return rls.tojson();
	}

	private String getCellValue(HSSFCell aCell) {
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
