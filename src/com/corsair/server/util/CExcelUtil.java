package com.corsair.server.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

import com.corsair.dbpool.util.Systemdate;

public class CExcelUtil {

	public static int colByValue(HSSFRow aRow, String value, Boolean throwerr) throws Exception {
		int col = colByValue(aRow, value);
		if (throwerr) {
			if (col != -1)
				return col;
			else
				throw new Exception("Excel没有发现字段【" + value + "】");
		} else
			return col;
	}

	public static int colByValue(HSSFRow aRow, String value) {
		if ((value == null) || (value.isEmpty()))
			return -1;
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			HSSFCell aCell = aRow.getCell(col);
			String celltext = getCellValue(aCell);
			if (value.equals(celltext))
				return col;
		}
		return -1;
	}

	public static String getCellValue(Cell aCell) {
		if (aCell != null) {
			int cellType = aCell.getCellType();
			switch (cellType) {
			case HSSFCell.CELL_TYPE_NUMERIC:// Numeric
				if (HSSFDateUtil.isCellDateFormatted(aCell)) {// 如果是date类型则
					Date dt = HSSFDateUtil.getJavaDate(aCell.getNumericCellValue()); // ，获取该cell的date值
					return Systemdate.getStrDateByFmt(dt, "yyyy-MM-dd HH:mm:ss");
				} else { // 纯数字
					String rst = null;
					double doubleVal = aCell.getNumericCellValue();
					long longVal = Math.round(aCell.getNumericCellValue());
					if (Double.parseDouble(longVal + ".0") == doubleVal)
						rst = String.valueOf(longVal);
					else
						rst = String.valueOf(doubleVal);
					if (rst.indexOf("e") >= 0) {
						BigDecimal db = new BigDecimal(rst);
						rst = db.toPlainString();
					}
					return rst;
				}
			case HSSFCell.CELL_TYPE_STRING:// String
				return aCell.getStringCellValue();
			case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
				return String.valueOf(aCell.getBooleanCellValue());
			case HSSFCell.CELL_TYPE_FORMULA: // 公式
				return aCell.getCellFormula();
			case HSSFCell.CELL_TYPE_BLANK: // 空值
				return null;
			case HSSFCell.CELL_TYPE_ERROR: // 故障
				return null;
			default:
				return null;
			}
		} else
			return null;
	}

	public static List<CExcelField> parserExcelSheetFields(Sheet aSheet, List<CExcelField> efields) throws Exception {
		return parserExcelSheetFields(aSheet, efields, 0);
	}

	public static List<CExcelField> parserExcelSheetFields(Sheet aSheet, List<CExcelField> efields, int titlerowidx) throws Exception {
		Row aRow = aSheet.getRow(titlerowidx);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			Cell aCell = aRow.getCell(col);
			String celltext = getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			for (CExcelField efd : efields) {
				if (celltext.equalsIgnoreCase(efd.getCaption())) {
					efd.setCol(col);
					break;
				}
			}
		}
		String ermsg = "";
		for (CExcelField efd : efields) {
			if ((efd.getCol() == -1) && (efd.isNotnull())) {// 不允许为空且未发现Cation
				ermsg = ermsg + "【" + efd.getCaption() + "】";
			}
		}
		if (!ermsg.isEmpty()) {
			ermsg = "Excel标题栏" + ermsg + "不存在";
			throw new Exception(ermsg);
		}
		return efields;
	}

	public static Map<String, String> getExcelRowValues(Row aRow, List<CExcelField> flds) {
		HashMap<String, String> values = new HashMap<String, String>();
		if (aRow == null)
			return values;
		for (CExcelField fld : flds) {
			if (fld.getCol() != -1) {
				Cell aCell = aRow.getCell(fld.getCol());
				values.put(fld.getFdname(), getCellValue(aCell));
			}
		}
		return values;
	}

	public static List<Map<String, String>> getExcelValues(Sheet aSheet, List<CExcelField> flds, int titlerowidx) {
		List<Map<String, String>> rst = new ArrayList<Map<String, String>>();
		for (int row = titlerowidx + 1; row <= aSheet.getLastRowNum(); row++) {
			Row aRow = aSheet.getRow(row);
			rst.add(getExcelRowValues(aRow, flds));
		}
		return rst;
	}

	public static boolean isExcel2003Old(String fname) {
		return fname.matches("^.+\\.(?i)(xls)$") || fname.matches("^.+\\.(?i)(xlt)$") || fname.matches("^.+\\.(?i)(xla)$");
	}

}
