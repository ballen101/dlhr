package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_quota_releaseline;

@ACO(coname = "web.hr.quotarelease")
public class COHr_quota_release {

	@ACOAction(eventname = "findOrgOptionByLoginUser", Authentication = true, notes = "获取权限范围内机构岗位非批量")
	public String findOrgOptionByLoginUser() throws Exception {
		HashMap<String, String> urlparms = CSContext.getParms();
		String parms = urlparms.get("parms");
		String sqlstr = "select osp.* from hr_orgposition osp,shworg o where o.orgid=osp.orgid and osp.usable=1 and o.usable=1 "
				+ " AND osp.hwc_idzl NOT IN(SELECT parmvalue FROM hr_systemparms WHERE usable=1 AND parmcode='BATCH_QUATA_CLASS') ";
		String order = " ORDER BY osp.orgid ASC ";

		String smax = urlparms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);

		List<JSONParm> jps = CJSON.getParms(parms);
		Hr_orgposition hsp = new Hr_orgposition();
		String where = CjpaUtil.buildFindSqlByJsonParms(hsp, jps);
		where = where.replace("`orgname`", "o.orgname");
		sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "o.idpath") + where + order + " limit 0," + max;

		return hsp.pool.opensql2json(sqlstr);
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

	// 序号 机构编码 机构名称 职位编码 职位名称 编制预算
	private String parserExcelSheet(Sheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		int ospcodecol = -1;
		int quotacol = -1;
		Row aRow = aSheet.getRow(0);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			Cell aCell = aRow.getCell(col);
			String celltext = getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			if ("职位编码".equals(celltext.trim())) {
				ospcodecol = col;
			}
			if ("编制预算".equals(celltext.trim())) {
				quotacol = col;
			}
		}
		if ((ospcodecol == -1) || (quotacol == -1)) {
			throw new Exception("没找到【职位编码】或【编制预算】列");
		}

		CJPALineData<Hr_quota_releaseline> rls = new CJPALineData<Hr_quota_releaseline>(Hr_quota_releaseline.class);
		Hr_orgposition hop = new Hr_orgposition();

		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {

				Cell aCell = aRow.getCell(ospcodecol);
				String ospcode = getCellValue(aCell);
				if ((ospcode == null) || (ospcode.isEmpty()))
					continue;
				if (ospcode.indexOf("e") >= 0) {
					BigDecimal db = new BigDecimal(ospcode);
					ospcode = db.toPlainString();
				}

				aCell = aRow.getCell(quotacol);
				String quota = getCellValue(aCell);
				if ((quota == null) || (quota.isEmpty()))
					quota = "0";
				String sqlstr = "select * from hr_orgposition where ospcode = '" + ospcode + "' "
						+ " and hwc_idzl NOT IN(SELECT parmvalue FROM hr_systemparms WHERE usable=1 AND parmcode='BATCH_QUATA_CLASS')";
				hop.findBySQL(sqlstr);
				if (!hop.isEmpty()) {
					if (hop.usable.getAsInt() == 1) {
						Hr_quota_releaseline hqr = new Hr_quota_releaseline();
						hqr.orgid.setValue(hop.orgid.getValue());
						hqr.orgname.setValue(hop.orgname.getValue());
						hqr.orgcode.setValue(hop.orgcode.getValue());
						hqr.ospid.setValue(hop.ospid.getValue());
						hqr.ospcode.setValue(hop.ospcode.getValue());
						hqr.sp_name.setValue(hop.sp_name.getValue());
						hqr.quota.setValue(quota);
						rls.add(hqr);
					} else {
						throw new Exception("职位编码【" + ospcode + "】设置为不可用！");
					}
				} else {
					throw new Exception("职位编码【" + ospcode + "】不存在或者对应职类不允许设置职位编制！");
				}
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
