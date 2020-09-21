package com.corsair.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.JPAControllerBase;
import com.corsair.cjpa.util.CPoint;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;

public class CExcelEx {

	public static void expJPASByModel(CJPALineData<CJPABase> jpas, String mdfname, OutputStream os, int tp) throws Exception {
		expJPASByModel(jpas, mdfname, os, tp, null);
	}

	/*
	 * 单页多个JPA item 模板格式
	 * Excel 第一行第一列注解内容
	 * {
	 * modelkey:"A001",模板ID
	 * modeltype:3，类型
	 * pageitem:10，每页数据项数量
	 * "pagecols":5,每页列数
	 * offsetbrow:1,行偏移
	 * offsetbcol:1,列偏移
	 * itemrows:9,每个数据项行数
	 * itemcols:4每个数据项列数
	 * clearemptyitem:true 清空多余的item模板数据
	 * }
	 */

	public static void expJPASByModel(CJPALineData<CJPABase> jpas, String mdfname, OutputStream os, int tp, JSONObject flds) throws Exception {
		if (jpas.size() == 0)
			throw new Exception("无需要打印或导出的数据");
		CJPA jpa = (CJPA) jpas.get(0);
		String fsep = System.getProperty("file.separator");
		String filename = ConstsSw.excelModelPath + jpa.getClass().getSimpleName() + fsep + mdfname;
		expJPASByModelFullFileName(jpas, filename, os, tp, flds);
	}

	public static void expJPASByModelFullFileName(CJPALineData<CJPABase> jpas, String mdFullFileName, OutputStream os, int tp, JSONObject flds) throws Exception {
		CJPA jpa = (CJPA) jpas.get(0);
		ExcelParmsRst epr = null;
		try {
			epr = getExcelParms(mdFullFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((epr == null) || (epr.getJo() == null)) {// 兼容旧的模板
			new CExcel().expJPAByModelFullFileName(jpa, mdFullFileName, os, tp, flds);
		} else {
			JSONObject parms = epr.getJo();
			Workbook workbook = epr.getWorkbook();
			int modeltype = parms.getInt("modeltype");
			if (modeltype == 3) {// 单页多个JPA item 模板
				expJPAByModel3(parms, workbook, jpas, mdFullFileName, os, tp);
			}
		}
	}

	@SuppressWarnings("resource")
	private static ExcelParmsRst getExcelParms(String filename) throws Exception {
		// Workbook workbook = CExcelUtil.isExcel2003(filename) ? new HSSFWorkbook(new FileInputStream(filename))
		// : new XSSFWorkbook(new FileInputStream(filename));

		Workbook workbook = WorkbookFactory.create(new File(filename));

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel模板<" + filename + ">没有sheet");
		else if (sn > 1)
			while (workbook.getNumberOfSheets() > 1) {
				workbook.removeSheetAt(1);
			}
		Sheet sheet = workbook.getSheetAt(0);// 获得一个sheet
		Row frow = sheet.getRow(0);
		if (frow == null)
			throw new Exception("excel模板【" + filename + "】第一个sheet第一行为NULL");
		Cell fcell = frow.getCell(0);
		if (fcell == null)
			throw new Exception("excel模板【" + filename + "】第一个sheet第一行第一列为NULL");
		Comment hssct = fcell.getCellComment();
		if (hssct == null)
			throw new Exception("excel模板【" + filename + "】第一个sheet第一行第一列【注解】为NULL");
		RichTextString hssctstr = hssct.getString();
		if (hssctstr == null)
			throw new Exception("excel模板【" + filename + "】第一个sheet第一行第一列【注解】为NULL");
		String comment = hssctstr.toString();
		if ((comment == null) || (comment.isEmpty()))// 为空 兼容旧格式
			return null;
		try {
			JSONObject parms = JSONObject.fromObject(comment); // 不是合法JSON 兼容旧格式
			return new ExcelParmsRst(parms, workbook);
		} catch (Exception e) {
			return null;
		}
	}

	// 在一个sheet上面导出多个jpa实例
	private static void expJPAByModel3(JSONObject parms, Workbook workbook, CJPALineData<CJPABase> jpas,
			String mdfname, OutputStream os, int tp) throws Exception {
		if (jpas.size() == 0)
			return;
		CJPABase jpa = jpas.get(0);
		String fsep = System.getProperty("file.separator");
		String filename = ConstsSw.excelModelPath + jpa.getClass().getSimpleName() + fsep + mdfname;
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		if (parms.getInt("modeltype") != 3)
			throw new Exception("本方法只支持modeltype为3的模板");
		int pageitem = parms.getInt("pageitem");// 每页行数
		int pagect = ((jpas.size() % pageitem) == 0) ? ((int) Math.floor(jpas.size() / pageitem)) : ((int) (Math.floor(jpas.size() / pageitem) + 1));// 页数
		JPAControllerBase ctr = jpa.getController();
		for (int i = 0; i < pagect; i++) {
			Sheet sheet = workbook.cloneSheet(0);
			if (tp == 1) {
				String epwd = ConstsSw.getSysParm("PRINT_EXCEL_PWD");
				if ((epwd != null) && (!epwd.isEmpty()))
					sheet.protectSheet(epwd);
			}
			workbook.setSheetName(i + 1, "第" + (i + 1) + "页");
			expPage(parms, jpas, workbook, sheet, pageitem, i, dictemp, ctr, mdfname);
			SetSystemParms(sheet, pagect, i);
			clearNotFind(sheet);
		}
		dictemp = null;
		// ///////////////
		workbook.removeSheetAt(0);
		workbook.write(os);
	}

	/**
	 * @param pageparms页码参数
	 * @param jpas写入的数据
	 * @param sheet写入的Sheet
	 * @param pageitem每页数量
	 * @param pagenum页码
	 * @param dictemp词汇缓存
	 * @throws Exception
	 */

	private static void expPage(JSONObject pageparms, CJPALineData<CJPABase> jpas, Workbook workbook, Sheet sheet,
			int pageitem, int pagenum, DictionaryTemp dictemp, JPAControllerBase ctr, String mdfname) throws Exception {
		String modelkey = pageparms.getString("modelkey");// 偏移开始行；
		int offsetbrow = pageparms.getInt("offsetbrow");// 偏移开始行；
		int offsetbcol = pageparms.getInt("offsetbcol");// 偏移开始列；
		int itemrows = pageparms.getInt("itemrows");// 一个数据单元行数
		int itemcols = pageparms.getInt("itemcols");// 一个数据单元列数
		int pagecols = pageparms.getInt("pagecols");// 每行列数
		boolean clearemptyitem = (pageparms.has("clearemptyitem")) ? pageparms.getBoolean("clearemptyitem") : false;// 清理空项目

		for (int i = 0; i < pageitem; i++) {// 写入一个数据项
			int idx = (pagenum * pageitem) + i;// 数据行

			int itemrow = i / pagecols;// 写入的数据块行 不是excel的行

			int bgrow = offsetbrow + (itemrows * itemrow);
			int edrow = bgrow + itemrows;
			int bgcol = offsetbcol + (itemcols * (i % pagecols));
			int edcol = bgcol + itemcols;
			if (idx < jpas.size()) {
				CJPA jpa = (CJPA) jpas.get(idx);
				for (int row = bgrow; row <= edrow; row++) {
					Row aRow = sheet.getRow(row);
					if (aRow == null)
						continue;
					for (int col = bgcol; col <= edcol; col++) {
						Cell aCell = aRow.getCell(col);
						String celltext = getCellValue(aCell);
						if ((celltext == null) || (celltext.isEmpty()))
							continue;
						int pos1 = celltext.indexOf("<");
						int pos2 = celltext.indexOf("/>");
						if ((pos1 < 0) || (pos2 < 0) || (pos1 >= pos2))
							continue;
						for (CField fd : jpa.cFields) {
							String res = "<*" + fd.getFieldname() + "*/>";

							String v = null;
							if (!fd.getDicclass().isEmpty()) {
								v = dictemp.getCaptionByValueCls(fd.getDicclass(), fd.getValue());
							} else {
								if (fd.getDicid() != 0)
									v = dictemp.getCaptionByValue(String.valueOf(fd.getDicid()), fd.getValue());
							}
							if (v == null)
								v = fd.getValue();
							if (ctr != null) {
								String cv = ctr.OnPrintField2Excel(jpa, modelkey, fd.getFieldname(), v, aCell);
								if (cv == null)
									celltext = celltext.replace(res.toLowerCase(), v);
								else
									celltext = celltext.replace(res.toLowerCase(), cv);
							} else
								celltext = celltext.replace(res.toLowerCase(), v);
						}
						aCell.setCellValue(celltext);
					}
				}
				if (ctr != null) {
					CPoint cellfrom = new CPoint(bgcol, bgrow);
					CPoint cellto = new CPoint(edcol, edrow);
					ctr.AfterPrintItem2Excel(jpa, modelkey, workbook, sheet, cellfrom, cellto);
				}
			} else {
				if (clearemptyitem) {
					for (int row = bgrow; row <= edrow; row++) {
						Row aRow = sheet.getRow(row);
						if (aRow == null)
							continue;
						for (int col = bgcol; col <= edcol; col++) {
							Cell aCell = aRow.getCell(col);
							if (aCell != null)
								aCell.setCellValue("");
						}
					}
				}
			}

			// 完成一条数据
		}
	}

	private static void SetSystemParms(Sheet sheet, int pages, int page) throws Exception {
		// for (CellField cfd : cellfields) {
		// String celltext = cfd.celltext;
		// celltext = celltext.replace("<#PageCount#/>".toLowerCase(), String.valueOf(pages));
		// celltext = celltext.replace("<#PageNo#/>".toLowerCase(), String.valueOf(page + 1));
		// celltext = celltext.replace("<#SysdateTime#/>".toLowerCase(), Systemdate.getStrDate());
		// celltext = celltext.replace("<#WeekDay#/>".toLowerCase(), Systemdate.getWeekOfDate(new Date()));
		// celltext = celltext.replace("<#Year#/>".toLowerCase(), Systemdate.getStrDateByFmt(new Date(), "yyyy"));
		// celltext = celltext.replace("<#Moth#/>".toLowerCase(), Systemdate.getStrDateByFmt(new Date(), "MM"));
		// celltext = celltext.replace("<#Day#/>".toLowerCase(), Systemdate.getStrDateByFmt(new Date(), "dd"));
		// celltext = celltext.replace("<#User#/>".toLowerCase(), CSContext.getUserDisplayname());
		// sheet.getRow(cfd.row).getCell(cfd.col).setCellValue(celltext);
		// }
	}

	private static void clearNotFind(Sheet sheet) {
		for (int row = 0; row <= sheet.getLastRowNum(); row++) {
			Row aRow = sheet.getRow(row);
			if (null != aRow) {
				for (int col = 0; col <= aRow.getLastCellNum(); col++) {
					Cell aCell = aRow.getCell(col);
					String celltext = getCellValue(aCell);
					if (celltext == null)
						continue;
					if (celltext.indexOf("/>") >= 0) {
						aCell.setCellValue("");
					}
				}
			}
		}
	}

	private static String getCellValue(Cell aCell) {
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
