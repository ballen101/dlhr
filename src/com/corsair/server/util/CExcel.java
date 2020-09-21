package com.corsair.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.JPAControllerBase;
import com.corsair.cjpa.util.CPoint;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.wordflow.Shwwfproclog;

public class CExcel {
	private Workbook workbook = null;
	private int PageRowNums = -1;// 每页行数
	private String LineClassName = null;// 行数据类名
	private int LineBeginRow = -1;// 行数据起始行数
	private int SumRow = -1;// 合计行

	public enum ExcelModelType {
		emtJPA, emtSql
	};

	private List<HashMap<String, String>> emtSqlDatas;
	private ExcelModelType excelModelType = ExcelModelType.emtJPA;
	private List<CellField> cellfields = new ArrayList<CellField>();

	public class CellField {
		public int row;
		public int col;
		List<String> jfields = new ArrayList<String>();
		public String celltext;

		public CellField(int row, int col, String celltext) {
			this.row = row;
			this.col = col;
			this.celltext = celltext;
		}
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

	// <*Tinv_stock_out_other_line.item_short_name*/><*Tinv_stock_out_other_line.spec*/>

	public void getCellFields(List<String> jfields, String celltext) {
		String temstr = celltext;
		while (true) {
			int pos1 = temstr.indexOf("<");
			int pos2 = temstr.indexOf("/>");
			if ((pos1 < 0) || (pos2 < 0) || (pos2 <= pos1))
				break;
			String fdname = temstr.substring(pos1 + 1, pos2);
			String fstr = fdname.substring(0, 1);
			String lstr = fdname.substring(fdname.length() - 1);
			if (("*".equals(fstr)) && ("*".equals(lstr))) {
				fdname = fdname.substring(1, fdname.length() - 1);
				boolean added = false;
				for (String fd : jfields) {
					if (fdname.equalsIgnoreCase(fd)) {
						added = true;
						break;
					}
				}
				if (!added)
					jfields.add(fdname);
			}
			temstr = temstr.substring(pos2 + 2);
		}

		// celltext
	}

	private void AnalysisExcelModel(String mdfile) throws Exception {
		// workbook = new HSSFWorkbook(new FileInputStream(mdfile));
		File file = new File(mdfile);
		if (!file.exists())
			throw new Exception("文件" + mdfile + "不存在!");
		workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel模板<" + mdfile + ">没有sheet");
		else if (sn > 1)
			while (workbook.getNumberOfSheets() > 1) {
				workbook.removeSheetAt(1);
			}
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		Row frow = aSheet.getRow(0);
		if (frow != null) {
			Cell fcell = frow.getCell(0);
			if (fcell != null) {
				Comment hssct = fcell.getCellComment();
				if (hssct != null) {
					RichTextString hssctstr = hssct.getString();
					if (hssctstr != null) {
						String ct = hssctstr.toString();
						if (ct != null) {
							if ((ct.toUpperCase().equalsIgnoreCase("DATASET")) || (ct.toUpperCase().equalsIgnoreCase("SQL"))) {
								excelModelType = ExcelModelType.emtSql;
							}
						}
					}
				}
			}
		}

		PageRowNums = 0;
		cellfields.clear();
		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			Row aRow = aSheet.getRow(row);
			boolean lineadded = false;
			if (null != aRow) {
				for (int col = 0; col <= aRow.getLastCellNum(); col++) {
					Cell aCell = aRow.getCell(col);
					String celltext = getCellValue(aCell);
					if ((celltext == null) || (celltext.isEmpty()))
						continue;
					int pos1 = celltext.indexOf("<");
					int pos2 = celltext.indexOf("/>");
					if ((pos1 < 0) || (pos2 < 0) || (pos1 >= pos2))
						continue;
					String pamsname = celltext.substring(pos1 + 1, pos2);
					String fstr = pamsname.substring(0, 1);
					String lstr = pamsname.substring(pamsname.length() - 1);
					if (pamsname.indexOf(".") == -1) {// 是JPA字段或系统字段等 反正是非明细行
						CellField cfd = new CellField(row, col, celltext);
						getCellFields(cfd.jfields, celltext);
						cellfields.add(cfd);
					}

					if (("*".equals(fstr)) && ("*".equals(lstr)) && (pamsname.indexOf(".") > 0)) {// 是明细行
						if (!lineadded) {
							lineadded = true;
							PageRowNums++;
						}
						if (LineBeginRow == -1)
							LineBeginRow = row;
						if (LineClassName == null) {
							// System.out.println("pamsname:" + pamsname);// *tinv_stock_out_other_line.item_code*
							String pn = pamsname.replace("*", "");
							if (pn.indexOf(".") > 2) {
								if ("T".equalsIgnoreCase(pn.substring(0, 1)))
									LineClassName = pn.substring(1, pn.indexOf("."));
								else
									LineClassName = pn.substring(0, pn.indexOf("."));
							}
							// System.out.println("LineClassName:" + LineClassName);
						}
					}
					if (celltext.indexOf("<%") > -1)
						SumRow = row;
				}
			}
		}
		// System.out.println("PageRowNums:" + PageRowNums +
		// " LineBeginRow:" + LineBeginRow + " LineClassName:" +
		// LineClassName + " SumRow:"
		// + SumRow);
	}

	private int getPageNums(CJPA jpa, String mdfname) throws Exception {
		if (excelModelType == ExcelModelType.emtSql) {
			JPAControllerBase ctr = jpa.getController();
			if (ctr == null) {
				throw new Exception("【" + mdfname + "】数据源设置为数据库，必须实现JAP Control onPrint方法，以加载数据");
			}
			emtSqlDatas = ctr.OnPrintDBData2Excel(jpa, mdfname);
			if (emtSqlDatas == null) {
				throw new Exception("【" + mdfname + "】数据源设置为数据库，必须实现JAP Control onPrint方法，以加载数据");
			}
			if (emtSqlDatas.size() == 0)
				return 1;
			if ((emtSqlDatas.size() % PageRowNums) == 0)
				return (int) Math.floor(emtSqlDatas.size() / PageRowNums);
			else
				return (int) (Math.floor(emtSqlDatas.size() / PageRowNums) + 1);
		} else {
			CJPALineData<CJPABase> jpas = jpa.lineDataByCFieldName(LineClassName + "s");
			if (jpas == null)
				return 1;
			if ((jpas.size() % PageRowNums) == 0)
				return (int) Math.floor(jpas.size() / PageRowNums);
			else
				return (int) (Math.floor(jpas.size() / PageRowNums) + 1);
		}
	}

	private String getFieldDisplay(CField fd, JSONObject flds) {
		String v = fd.getValue();
		if ((v == null) || (v.isEmpty()))
			return v;
		if (flds == null)
			return v;
		if (flds.has("cols")) {
			JSONArray lfds = flds.getJSONArray("cols");
			for (int i = 0; i < lfds.size(); i++) {
				JSONObject jfd = lfds.getJSONObject(i);
				if (jfd.has("field") && jfd.has("formatparms")) {
					if (fd.getFieldname().equalsIgnoreCase(jfd.getString("field"))) {
						return getFormatValue(jfd, fd.getValue());
					}
				}
			}
		}
		return v;
	}

	private String getFieldDisplay(CField fd, JSONObject flds, String LineClassName) {
		String v = fd.getValue();
		if ((v == null) || (v.isEmpty()))
			return v;
		if (flds == null)
			return v;
		if (flds.has("linecols")) {
			JSONObject lcs = flds.getJSONObject("linecols");
			if (lcs.has(LineClassName + "s")) {
				JSONArray lfds = lcs.getJSONArray(LineClassName + "s");
				for (int i = 0; i < lfds.size(); i++) {
					JSONObject jfd = lfds.getJSONObject(i);
					if (jfd.has("field") && jfd.has("formatparms")) {
						if (fd.getFieldname().equalsIgnoreCase(jfd.getString("field"))) {
							return getFormatValue(jfd, fd.getValue());
						}
					}
				}
			}
		}
		return v;
	}

	private void expPage(CJPA jpa, Sheet sheet, int pagenum, DictionaryTemp dictemp, JSONObject flds) throws Exception {
		// 写主表信息
		for (CellField cfd : cellfields) {
			String celltext = cfd.celltext;
			String newcelltext = celltext;
			for (String fdname : cfd.jfields) {
				CField fd = jpa.cfieldNoCase(fdname);
				if (fd == null)
					continue;
				int pos1 = celltext.indexOf("<");
				int pos2 = celltext.indexOf("/>");
				if ((pos1 < 0) || (pos2 < 0) || (pos1 >= pos2))
					continue;
				String v = null;
				if (!fd.getDicclass().isEmpty()) {
					v = dictemp.getCaptionByValueCls(fd.getDicclass(), fd.getValue());
				} else if (fd.getDicid() != 0) {
					v = dictemp.getCaptionByValue(String.valueOf(fd.getDicid()), fd.getValue());
				} else {
					v = getFieldDisplay(fd, flds);
				}

				if (v == null)
					v = fd.getValue();
				newcelltext = celltext.replace("<*" + fdname + "*/>", v);
			}
			// sheet.getRow(cfd.row).getCell(cfd.col).setCellValue(newcelltext);
			cfd.celltext = newcelltext;
		}
		// 写从表信息
		int ExeclRow = LineBeginRow;

		if (excelModelType == ExcelModelType.emtSql) {
			for (int row = pagenum * PageRowNums; row < (pagenum + 1) * PageRowNums; row++) {
				if (row >= emtSqlDatas.size())
					break;
				HashMap<String, String> drow = emtSqlDatas.get(row);
				Row aRow = sheet.getRow(ExeclRow);
				if (aRow == null)
					break;
				for (int col = 0; col <= aRow.getLastCellNum(); col++) {
					Cell aCell = aRow.getCell(col);
					String celltext = getCellValue(aCell);
					if ((celltext == null) || (celltext.isEmpty()))
						continue;

					Iterator<Entry<String, String>> iter = drow.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						String res = "<*." + key.toString() + "*/>";
						if (val != null)
							celltext = celltext.replace(res.toLowerCase(), val.toString());
					}
					aCell.setCellValue(celltext);
				}
				ExeclRow++;
			}
		} else {
			for (int row = pagenum * PageRowNums; row < (pagenum + 1) * PageRowNums; row++) {

				CJPALineData<CJPABase> jpas = jpa.lineDataByCFieldNameNoCase(LineClassName + "s");
				if (jpas == null)
					jpas = jpa.lineDataByCFieldNameNoCase(LineClassName);
				if (jpas == null)
					throw new Exception("<" + jpa.getClass().getName() + "." + LineClassName + ">未发现");
				if (row >= jpas.size())
					break;
				CJPA ljpa = (CJPA) jpas.get(row);
				Row aRow = sheet.getRow(ExeclRow);
				if (aRow == null)
					break;
				for (int col = 0; col <= aRow.getLastCellNum(); col++) {
					Cell aCell = aRow.getCell(col);
					String celltext = getCellValue(aCell);
					if ((celltext == null) || (celltext.isEmpty()))
						continue;
					for (CField fd : ljpa.cFields) {
						String res = "<*T" + ljpa.getClass().getSimpleName() + "." + fd.getFieldname() + "*/>";

						String v = null;
						if (!fd.getDicclass().isEmpty()) {
							v = dictemp.getCaptionByValueCls(fd.getDicclass(), fd.getValue());
						} else if (fd.getDicid() != 0) {
							v = dictemp.getCaptionByValue(String.valueOf(fd.getDicid()), fd.getValue());
						} else {
							v = getFieldDisplay(fd, flds, LineClassName);
						}
						if (v == null)
							v = fd.getValue();
						celltext = celltext.replace(res.toLowerCase(), v);
						res = "<*" + ljpa.getClass().getSimpleName() + "." + fd.getFieldname() + "*/>";
						celltext = celltext.replace(res.toLowerCase(), v);
					}
					aCell.setCellValue(celltext);
				}
				ExeclRow++;
			}
		}
	}

	private void writeSumRow(CJPA jpa, Sheet sheet) throws Exception {
		if (SumRow == -1)
			return;
		CJPALineData<CJPABase> jpas = jpa.lineDataByCFieldNameNoCase(LineClassName + "s");
		if (jpas == null)
			throw new Exception("<" + jpa.getClass().getName() + "." + LineClassName + "s>未发现");
		if (jpas.size() == 0)
			return;
		CJPA ljpa = (CJPA) jpas.get(0);

		Row aRow = sheet.getRow(SumRow);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			Cell aCell = aRow.getCell(col);
			String celltext = getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			for (CField fd : ljpa.cFields) {
				String res = "<%" + ljpa.getClass().getSimpleName() + "." + fd.getCfieldname() + "%/>";
				celltext = celltext.replace(res.toLowerCase(), String.valueOf(jpas.getSumValue(fd.getCfieldname())));
			}
			aCell.setCellValue(celltext);
		}
	}

	private void SetSystemParms(CJPA jpa, Sheet sheet, int pages, int page) throws Exception {
		for (CellField cfd : cellfields) {
			String celltext = cfd.celltext;
			celltext = celltext.replace("<#PageCount#/>".toLowerCase(), String.valueOf(pages));
			celltext = celltext.replace("<#PageNo#/>".toLowerCase(), String.valueOf(page + 1));
			celltext = celltext.replace("<#SysdateTime#/>".toLowerCase(), Systemdate.getStrDate());
			celltext = celltext.replace("<#WeekDay#/>".toLowerCase(), Systemdate.getWeekOfDate(new Date()));
			celltext = celltext.replace("<#Year#/>".toLowerCase(), Systemdate.getStrDateByFmt(new Date(), "yyyy"));
			celltext = celltext.replace("<#Moth#/>".toLowerCase(), Systemdate.getStrDateByFmt(new Date(), "MM"));
			celltext = celltext.replace("<#Day#/>".toLowerCase(), Systemdate.getStrDateByFmt(new Date(), "dd"));
			celltext = celltext.replace("<#User#/>".toLowerCase(), CSContext.getUserDisplayname());
			celltext = celltext.replace("<#WorkflowInfo#/>".toLowerCase(), WFPROCLOGSTR);
			sheet.getRow(cfd.row).getCell(cfd.col).setCellValue(celltext);
		}
	}

	private String getWfInfo(CJPA jpa, DictionaryTemp dictemp) throws Exception {
		CField fd = jpa.cfield("wfid");
		if ((fd == null) || fd.isEmpty())
			return "";
		CJPALineData<Shwwfproclog> wflogs = new CJPALineData<Shwwfproclog>(Shwwfproclog.class);
		String sqlstr = "SELECT * FROM shwwfproclog WHERE wfid=" + fd.getValue() + " ORDER BY wflid";
		wflogs.findDataBySQL(sqlstr);
		if (wflogs.isEmpty())
			return "";
		int[] spl = { 12, 18, 10, 25, 25 };
		String rst = getLengStr("处理用户", spl[0], 2) + getLengStr("意见", spl[1], 2)
				+ getLengStr("处理类型", spl[2], 2) + getLengStr("到达时间", spl[3], 2)
				+ getLengStr("到达时间", spl[4], 2);
		for (CJPABase j : wflogs) {
			Shwwfproclog wflog = (Shwwfproclog) j;

			String v = dictemp.getCaptionByValue("262", wflog.actiontype.getValue());

			String r = getLengStr(wflog.displayname.getValue(), spl[0], 2)
					+ getLengStr(wflog.opinion.getValue(), spl[1], 2)
					+ getLengStr(v, spl[2], 2)
					+ getLengStr(wflog.arivetime.getValue(), spl[3], 2)
					+ getLengStr(wflog.actiontime.getValue(), spl[4], 2);
			rst = rst + "\r\n" + r;
		}
		return rst;
	}

	/**
	 * @param value
	 *            原始字符串
	 * @param len
	 *            长度字符（中文算2）
	 * @param filltp
	 *            1：前面补空格 2：后面补空格 3：前后补空格
	 * @return 返回指定长度字符串
	 */
	private static String getLengStr(String value, int len, int filltp) {
		if (value == null)
			return getzdstr(len, " ");
		int tlen = StrLen(value);
		if (tlen == len)
			return value;
		if (tlen > len)
			return getSubString(value, len);
		if (tlen < len) {
			int lspnum = 0;
			int rspnum = 0;
			if (filltp == 1) {
				lspnum = len - tlen;
			} else if (filltp == 2) {
				rspnum = len - tlen;
			} else if (filltp == 3) {
				lspnum = (len - tlen) / 2;
				rspnum = len - tlen - lspnum;
			}
			return getzdstr(lspnum, " ") + value + getzdstr(rspnum, " ");
		}
		return "";
	}

	private static String getzdstr(int len, String sp) {
		String rst = "";
		for (int i = 0; i < len; i++) {
			rst = rst + sp;
		}
		return rst;
	}

	/**
	 * @param value
	 * @return 获取字符串真实长度（中文算2）
	 */
	private static int StrLen(String value) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}

	/**
	 * <b>截取指定字节长度的字符串，不能返回半个汉字</b>
	 * 例如：
	 * 如果网页最多能显示17个汉字，那么 length 则为 34
	 * StringTool.getSubString(str, 34);
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String getSubString(String str, int length) {
		int count = 0;
		int offset = 0;
		char[] c = str.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] > 256) {
				offset = 2;
				count += 2;
			} else {
				offset = 1;
				count++;
			}
			if (count == length) {
				return str.substring(0, i + 1);
			}
			if ((count == length + 1 && offset == 2)) {
				return str.substring(0, i);
			}
		}
		return "";
	}

	private void clearNotFind(Sheet sheet) {
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

	/*
	 * Excel 第一行第一列注解内容
	 * {
	 * modeltype:3，类型
	 * pageitem:10，每页数据项数量
	 * offsetbrow:1,行偏移
	 * offsetbcol:1,列偏移
	 * itemrows:9,每个数据项行数
	 * itemcols:4每个数据项列数
	 * }
	 */

	private JSONObject AnalysisExcelModel4JPAs(String mdfile) throws Exception {
		File file = new File(mdfile);
		if (!file.exists())
			throw new Exception("文件" + mdfile + "不存在!");
		workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel模板<" + mdfile + ">没有sheet");
		else if (sn > 1)
			while (workbook.getNumberOfSheets() > 1) {
				workbook.removeSheetAt(1);
			}
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		Row frow = aSheet.getRow(0);
		if (frow == null)
			throw new Exception("excel模板【" + mdfile + "】第一个sheet第一行为NULL");
		Cell fcell = frow.getCell(0);
		if (fcell == null)
			throw new Exception("excel模板【" + mdfile + "】第一个sheet第一行第一列为NULL");
		Comment hssct = fcell.getCellComment();
		if (hssct == null)
			throw new Exception("excel模板【" + mdfile + "】第一个sheet第一行第一列【注解】为NULL");
		RichTextString hssctstr = hssct.getString();
		if (hssctstr == null)
			throw new Exception("excel模板【" + mdfile + "】第一个sheet第一行第一列【注解】为NULL");
		String comment = hssctstr.toString();
		if (comment == null)
			throw new Exception("excel模板【" + mdfile + "】第一个sheet第一行第一列【注解】为NULL");

		JSONObject parms = JSONObject.fromObject(comment);
		return parms;
	}

	public void expJPAByModel(CJPA jpa, String mdfname, OutputStream os, int tp) throws Exception {
		expJPAByModel(jpa, mdfname, os, tp, null);
	}

	private String WFPROCLOGSTR = "";

	public void expJPAByModel(CJPA jpa, String mdfname, OutputStream os, int tp, JSONObject flds) throws Exception {
		String fsep = System.getProperty("file.separator");
		String filename = ConstsSw.excelModelPath + jpa.getClass().getSimpleName() + fsep + mdfname;
		expJPAByModelFullFileName(jpa, filename, os, tp, flds);
	}

	public void expJPAByModelFullFileName(CJPA jpa, String filename, OutputStream os, int tp, JSONObject flds) throws Exception {
		AnalysisExcelModel(filename);
		int pages = getPageNums(jpa, filename);
		// System.out.println("pages:" + pages);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		WFPROCLOGSTR = getWfInfo(jpa, dictemp);
		for (int i = 0; i < pages; i++) {
			Sheet aSheet = workbook.cloneSheet(0);
			if (tp == 1) {
				String epwd = ConstsSw.getSysParm("PRINT_EXCEL_PWD");
				if ((epwd != null) && (!epwd.isEmpty()))
					aSheet.protectSheet(epwd);
			}
			workbook.setSheetName(i + 1, "第" + (i + 1) + "页");
			expPage(jpa, aSheet, i, dictemp, flds);
			if (excelModelType == ExcelModelType.emtSql) {
			} else {
				writeSumRow(jpa, aSheet);
			}
			SetSystemParms(jpa, aSheet, pages, i);
			clearNotFind(aSheet);
		}
		WFPROCLOGSTR = "";
		dictemp = null;
		// ///////////////
		workbook.removeSheetAt(0);
		workbook.write(os);
	}

	private static int getidxbycolex(JSONArray afds, String fdname) {
		if (fdname == null)
			return -1;
		for (int i = 0; i < afds.size(); i++) {
			JSONObject jcol = afds.getJSONObject(i);
			if (jcol.has("field")) {
				if (fdname.equalsIgnoreCase(jcol.getString("field")))
					return jcol.getInt("c");
			}
		}
		return -1;
	}

	private static void initColsXY(JSONArray colss) {
		for (int tr = 0; tr < colss.size(); tr++) {
			JSONArray jcols = colss.getJSONArray(tr);
			for (int i = 0; i < jcols.size(); i++) {
				JSONObject jcol = jcols.getJSONObject(i);
				jcol.put("r", tr);
				jcol.put("c", i);
			}
		}
	}

	private static void updaterowc(JSONArray jcols, int start, int colspan) {
		for (int i = 0; i < jcols.size(); i++) {
			JSONObject jcol = jcols.getJSONObject(i);
			int c = jcol.getInt("c");
			// System.out.println("c:" + c);
			if (c > start) {
				// System.out.println("nc:" + (c + colspan - 1));
				jcol.put("c", c + colspan - 1);
			}
		}

	}

	private static void updatearowc(JSONArray colss, int sr, int oldx, int rowspan, int colspan) {
		for (int i = sr; i < sr + rowspan; i++) {
			if (i >= colss.size())
				break;

			JSONArray jcols = colss.getJSONArray(i);
			if (sr == i) {
				updaterowc(jcols, oldx, colspan);
			} else {
				// System.out.println("row:" + i + " oldx:" + oldx);
				updaterowc(jcols, oldx - 1, colspan + 1);
			}
		}
	}

	private static void caclcOffsetXY(JSONArray colss) {
		initColsXY(colss);
		for (int tr = 0; tr < colss.size(); tr++) {
			JSONArray jcols = colss.getJSONArray(tr);
			for (int i = 0; i < jcols.size(); i++) {
				JSONObject jcol = jcols.getJSONObject(i);
				jcol.put("r", tr);
				int rowspan = (jcol.has("rowspan")) ? Integer.valueOf(jcol.getString("rowspan")) : 1;
				int colspan = (jcol.has("colspan")) ? Integer.valueOf(jcol.getString("colspan")) : 1;
				int oldx = jcol.getInt("c");
				// 设置当行 后续c
				updatearowc(colss, tr, oldx, rowspan, colspan);
				// 设置后续所有行 大于C的值；
				// jcol.put("c", jcol.getInt("c") + colspan - 1);
			}
		}
	}

	private static JSONArray getAllFields(JSONArray colss) {
		JSONArray rst = new JSONArray();
		for (int tr = 0; tr < colss.size(); tr++) {
			JSONArray jcols = colss.getJSONArray(tr);
			for (int i = 0; i < jcols.size(); i++) {
				JSONObject jcol = jcols.getJSONObject(i);
				if (jcol.containsKey("field")) {
					rst.add(jcol);
				}
			}
		}
		return rst;
	}

	// cols:[{},{}]
	// cols:[[{},{}],[{},{}]]
	public static void expByJSCols(JSONArray jdatas, JSONArray cols, OutputStream os) throws Exception {
		// HSSFWorkbook workbook = new HSSFWorkbook();// 创建一个Excel文件
		SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

		int maxl = 65535;
		int dct = jdatas.size();
		int page = 0;
		if ((dct % maxl) == 0)
			page = dct / maxl;
		else
			page = (dct / maxl) + 1;
		if (cols.size() == 0)
			throw new Exception("导出Excel没有列属性");
		JSONArray colss = new JSONArray();
		if (cols.get(0) instanceof JSONArray) {
			colss = cols;
		} else {
			colss.add(cols);
		}
		// System.out.println("字段:" + colss.toString());
		caclcOffsetXY(colss);// 定位表头
		// System.out.println("定位表头后字段:" + colss.toString());
		JSONArray afds = getAllFields(colss);// 所有字段
		// System.out.println("导出数据的字段d:" + afds.toString());
		for (int pg = 0; pg < page; pg++) {
			Sheet sheet = workbook.createSheet();// 创建一个Excel的Sheet
			for (int tr = 0; tr < colss.size(); tr++) {
				JSONArray jcols = colss.getJSONArray(tr);
				Row rowt = sheet.createRow(tr);
				// int c = 0;
				for (int i = 0; i < jcols.size(); i++) {
					JSONObject jcol = jcols.getJSONObject(i);
					int c = jcol.getInt("c");
					int rowspan = (jcol.has("rowspan")) ? Integer.valueOf(jcol.getString("rowspan")) : 1;
					int colspan = (jcol.has("colspan")) ? Integer.valueOf(jcol.getString("colspan")) : 1;
					sheet.addMergedRegion(new CellRangeAddress(tr, tr + rowspan - 1, c, c + colspan - 1));
					Cell cell = rowt.createCell(c);
					cell.setCellValue(jcol.getString("title"));

					int w = (jcol.has("width")) ? jcol.getInt("width") : 64;
					sheet.setColumnWidth(i, w * 20);
					// c = c + colspan;
				}
			}

			int r = colss.size();
			for (int i = pg * maxl; i < (pg + 1) * maxl; i++) {
				if (i >= dct)
					break;
				JSONObject data = jdatas.getJSONObject(i);
				Row row = sheet.createRow(r);
				r++;

				Iterator iterator = data.keys();
				while (iterator.hasNext()) {
					String fdname = (String) iterator.next();
					int x = getidxbycolex(afds, fdname);
					JSONObject jo = getOByCol(afds, fdname);
					if (x != -1) {
						Cell cell = row.createCell(x);
						cell.setCellValue(getFormatValue(jo, data.getString(fdname)));
						// cell.setCellStyle(cellStyle);
					}
					// String value = data.getString(fdname);\
					// int x = getidxbycolex(afds, fdname);
					// if (x != -1) {
					// HSSFCell cell = row.createCell(x);
					// cell.setCellValue(value);
					// }
				}
			}
		}
		workbook.write(os);
	}

	// "formatparms":{"valueField":"dictvalue","textField":"language1",multiple:true,"jsondata":[{"language1":"是","dictvalue":"1"},{"language1":"否","dictvalue":"2"}]}}
	private static String getFormatValue(JSONObject jo, String value) {
		if (value == null)
			return null;
		if (jo.has("formatparms")) {
			JSONObject jp = jo.getJSONObject("formatparms");
			if (jp.has("valueField") && jp.has("textField") && jp.has("jsondata")) {
				boolean multiple = (jp.has("multiple")) ? jp.getBoolean("multiple") : false;
				String vf = jp.getString("valueField");
				String tf = jp.getString("textField");
				// System.out.println("multiple:" + multiple + " " + vf);
				JSONArray dts = jp.getJSONArray("jsondata");
				if (!multiple) {
					for (int i = 0; i < dts.size(); i++) {
						JSONObject dt = dts.getJSONObject(i);
						if (value.equalsIgnoreCase(dt.getString(vf))) {
							return dt.getString(tf);
						}
					}
				} else {
					// System.out.println(value);
					String[] vs = value.split(",");
					String rst = "";
					for (String v : vs) {
						for (int i = 0; i < dts.size(); i++) {
							JSONObject dt = dts.getJSONObject(i);
							if (v.equalsIgnoreCase(dt.getString(vf))) {
								rst = rst + dt.getString(tf) + ",";
							}
						}
					}
					if (!rst.isEmpty()) {
						rst = rst.substring(0, rst.length() - 1);
						return rst;
					}
				}
				return value;
			} else if (jp.has("fttype")) {
				if ("date".equalsIgnoreCase(jp.getString("fttype"))) {
					if (!value.isEmpty()) {
						return Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(value));
					} else
						return value;
				} else
					return value;
			} else
				return value;
		} else
			return value;
	}

	public static void expLargeByCols(String sqlstr, JSONArray cols, OutputStream os) throws Exception {
		expLargeByCols(DBPools.defaultPool(), sqlstr, null, cols, os);
	}

	public static void expLargeByCols(CDBPool pool, String sqlstr, String orderby, JSONArray cols, OutputStream os) throws Exception {
		int pagesize = 5000;
		String sqlct = "select ifnull(count(*),0) ct from(" + sqlstr + ") tb1";
		int sumct = Integer.valueOf(pool.openSql2List(sqlct).get(0).get("ct"));
		int pages = ((sumct % pagesize) == 0) ? sumct / pagesize : sumct / pagesize + 1;
		// System.out.println("pages:" + pages);
		SXSSFWorkbook wb = new SXSSFWorkbook(1000);
		// keep 100 rows in memory,exceeding rows will be flushed to disk
		// wb.setCompressTempFiles(true); // temp files will be gzipped
		JSONArray colss = new JSONArray();// 字段
		if (cols.get(0) instanceof JSONArray) {
			colss = cols;
		} else {
			colss.add(cols);
		}
		caclcOffsetXY(colss);// 定位表头
		// System.out.println("定位表头后字段:" + colss.toString());
		JSONArray afds = getAllFields(colss);// 所有字段

		Sheet sh = wb.createSheet();
		// /写入标题行
		// Row row0 = sh.createRow(0);
		CellStyle cellStyleTitle = wb.createCellStyle();
		cellStyleTitle.setFillPattern(CellStyle.SOLID_FOREGROUND); // 填充单元格
		cellStyleTitle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		cellStyleTitle.setAlignment(CellStyle.ALIGN_CENTER);// //居中显示
		cellStyleTitle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
		cellStyleTitle.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
		cellStyleTitle.setBorderTop(CellStyle.BORDER_THIN);// 上边框
		cellStyleTitle.setBorderRight(CellStyle.BORDER_THIN);// 右边框
		Font font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);// 粗体显示
		cellStyleTitle.setFont(font);

		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);// 上边框
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);// 右边框

		for (int tr = 0; tr < colss.size(); tr++) {
			JSONArray jcols = colss.getJSONArray(tr);
			Row rowt = sh.createRow(tr);
			// int c = 0;
			for (int i = 0; i < jcols.size(); i++) {
				JSONObject jcol = jcols.getJSONObject(i);
				int c = jcol.getInt("c");
				int rowspan = (jcol.has("rowspan")) ? Integer.valueOf(jcol.getString("rowspan")) : 1;
				int colspan = (jcol.has("colspan")) ? Integer.valueOf(jcol.getString("colspan")) : 1;
				sh.addMergedRegion(new CellRangeAddress(tr, tr + rowspan - 1, c, c + colspan - 1));
				Cell cell = rowt.createCell(c);
				cell.setCellValue(jcol.getString("title"));
				cell.setCellStyle(cellStyleTitle);
				int w = 60;
				if (jcol.has("width")) {
					String o = jcol.getString("width");
					w = (o == null) ? 60 : Integer.valueOf(o.toString());
				}

				sh.setColumnWidth(i, w * 50);
				// c = c + colspan;
			}
		}
		// int c = 0;
		// for (int i = 0; i < cols.size(); i++) {
		// JSONObject col = cols.getJSONObject(i);
		// Cell cell = row0.createCell(c);
		// cell.setCellStyle(cellStyleTitle);
		// cell.setCellValue(col.getString("title"));
		// Object o = col.get("width");
		// int w = (o == null) ? 20 : Integer.valueOf(o.toString());
		// sh.setColumnWidth(c, w * 50);
		// c++;
		// }
		// 写标题行完成
		for (int i = 0; i < pages; i++) {
			String sqlpage = "select * from (" + sqlstr + ") tb1 "
					+ (((orderby == null) || (orderby.isEmpty())) ? "" : " order by " + orderby)
					+ " limit " + i * pagesize + "," + pagesize;
			List<HashMap<String, String>> datas = pool.openSql2List(sqlpage);
			for (int j = 0; j < datas.size(); j++) {
				HashMap<String, String> data = datas.get(j);
				int r = i * pagesize + j + colss.size(); //
				Row row = sh.createRow(r);

				Iterator<Entry<String, String>> iter = data.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = iter.next();
					String fdname = entry.getKey();

					int x = getidxbycolex(afds, fdname);
					JSONObject jo = getOByCol(afds, fdname);
					if (x != -1) {
						Cell cell = row.createCell(x);
						cell.setCellValue(getFormatValue(jo, entry.getValue()));
						cell.setCellStyle(cellStyle);
					}

					// int x = getidxbycol(cols, fdname);
					// JSONObject jo = getOByCol(cols, fdname);
					// if (x != -1) {
					// Cell cell = row.createCell(x);
					// cell.setCellValue(getFormatValue(jo, entry.getValue()));
					// cell.setCellStyle(cellStyle);
					// }
				} // while
			}
		}
		wb.write(os);
	}

	// col:{"field":"whcode","title":"仓库编码","width":64}
	private static void expByCols(List<HashMap<String, String>> datas, JSONArray cols, OutputStream os) throws IOException {
		// Workbook workbook = new Workbook();// 创建一个Excel文件
		XSSFWorkbook workbook = new XSSFWorkbook();

		SXSSFWorkbook wk = new SXSSFWorkbook();
		int maxl = 65535;
		int dct = datas.size();
		int page = 0;
		if ((dct % maxl) == 0)
			page = dct / maxl;
		else
			page = (dct / maxl) + 1;
		for (int pg = 0; pg < page; pg++) {
			Sheet sheet = workbook.createSheet();// 创建一个Excel的Sheet
			Row row0 = sheet.createRow(0);
			int c = 0;
			CellStyle cellStyleTitle = workbook.createCellStyle();
			cellStyleTitle.setFillPattern(CellStyle.SOLID_FOREGROUND); // 填充单元格
			cellStyleTitle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
			cellStyleTitle.setAlignment(CellStyle.ALIGN_CENTER);// //居中显示
			cellStyleTitle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
			cellStyleTitle.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
			cellStyleTitle.setBorderTop(CellStyle.BORDER_THIN);// 上边框
			cellStyleTitle.setBorderRight(CellStyle.BORDER_THIN);// 右边框
			Font font = workbook.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);// 粗体显示
			cellStyleTitle.setFont(font);

			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);// 上边框
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);// 右边框
			for (int i = 0; i < cols.size(); i++) {
				JSONObject col = cols.getJSONObject(i);
				Cell cell = row0.createCell(c);
				cell.setCellStyle(cellStyleTitle);
				cell.setCellValue(col.getString("title"));
				Object o = col.get("width");
				int w = (o == null) ? 20 : Integer.valueOf(o.toString());
				sheet.setColumnWidth(c, w * 50);
				c++;
			}

			int r = 1;
			for (int i = pg * maxl; i < (pg + 1) * maxl; i++) {
				if (i >= dct)
					break;
				HashMap<String, String> data = datas.get(i);
				Row row = sheet.createRow(r);
				r++;
				Iterator<Entry<String, String>> iter = data.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = iter.next();
					String fdname = entry.getKey();
					int x = getidxbycol(cols, fdname);
					JSONObject jo = getOByCol(cols, fdname);
					if (x != -1) {
						Cell cell = row.createCell(x);
						cell.setCellValue(getFormatValue(jo, entry.getValue()));
						cell.setCellStyle(cellStyle);
					}
				}
			}
		}
		workbook.write(os);
	}

	private static JSONObject getOByCol(JSONArray cols, String fdname) {
		int c = 0;
		if (fdname == null)
			return null;
		for (int i = 0; i < cols.size(); i++) {
			JSONObject col = cols.getJSONObject(i);
			if (fdname.equalsIgnoreCase(col.getString("field"))) {
				return col;
			}
			c++;
		}
		return null;
	}

	private static int getidxbycol(JSONArray cols, String fdname) {
		int c = 0;
		if (fdname == null)
			return -1;
		for (int i = 0; i < cols.size(); i++) {
			JSONObject col = cols.getJSONObject(i);
			if (fdname.equalsIgnoreCase(col.getString("field"))) {
				return c;
			}
			c++;
		}
		return -1;
	}

	public static void getFilesInfo(String strPath, ArrayList<String[]> filelist, boolean IncludechildPath) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();

		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if (IncludechildPath)
					getFilesInfo(files[i].getAbsolutePath(), filelist, true);
			} else {
				String FileInft[] = new String[3];
				String strFileName = files[i].getAbsolutePath();
				String strSize = String.valueOf(files[i].length());
				Date dt = new Date(files[i].lastModified());
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期格式
				String lastUpdate = df.format(dt);

				strFileName = strFileName.substring(strPath.length());

				FileInft[0] = strFileName.substring(1);
				FileInft[1] = strSize;
				FileInft[2] = lastUpdate;

				filelist.add(FileInft);
			}
		}
	}
}
