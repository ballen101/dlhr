package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.perm.entity.Hr_emploanbatch_line;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.employeeloanbatch")
public class COHr_emploanbatch {
	@ACOAction(eventname = "impexcel", Authentication = true, notes = "从excel导入借调明细信息")
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

	// 序号 人事编号 姓名 借调前部门 借调后部门编码 借调后部门 备注

	private String parserExcelSheet(HSSFSheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		int empcol = -1;
		int orgcol = -1;
		int memocol = -1;
		HSSFRow aRow = aSheet.getRow(0);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			HSSFCell aCell = aRow.getCell(col);
			String celltext = getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			if ("工号".equals(celltext.trim())) {
				empcol = col;
			}
			if ("借调后部门编码".equals(celltext.trim())) {
				orgcol = col;
			}
			if ("备注".equals(celltext.trim())) {
				memocol = col;
			}
		}

		if ((empcol == -1) || (orgcol == -1) || (memocol == -1)) {
			throw new Exception("没找到【工号】或【借调后部门编码】或【备注】列");
		}

		CJPALineData<Hr_emploanbatch_line> rlls = new CJPALineData<Hr_emploanbatch_line>(Hr_emploanbatch_line.class);
		Hr_employee emp = new Hr_employee();
		Shworg org = new Shworg();

		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {
				HSSFCell aCell = aRow.getCell(empcol);
				String employee_code = CExcelUtil.getCellValue(aCell);
				emp.findBySQL("select * from hr_employee where employee_code='" + employee_code + "'", false);
				if (emp.isEmpty())
					throw new Exception("没有找到工号为【" + employee_code + "】的员工资料");
				int empstatid = emp.empstatid.getAsInt();
				if ((empstatid == 6) || (empstatid == 11) || (empstatid == 12))
					throw new Exception("工号为【" + employee_code + "】的员工不是在职状态不能借调");

				aCell = aRow.getCell(orgcol);
				String orgcode = getCellValue(aCell);
				if ((orgcode == null) || (orgcode.isEmpty()))
					continue;
				org.findBySQL("select * from shworg where code='" + orgcode +
						"'", false);
				if (org.isEmpty())
					throw new Exception("没有找到编码为【" + orgcode + "】的机构");
//				if (org.stat.getAsInt() != 1) {
//					throw new Exception("编码为【" + orgcode + "】的机构不可用");
//				}

				aCell = aRow.getCell(memocol);
				String remark = getCellValue(aCell);
				if ((remark == null) || (remark.isEmpty()))
					remark = null;

				Hr_emploanbatch_line elbl = new Hr_emploanbatch_line();
				elbl.er_id.setValue(emp.er_id.getValue()); // 人事ID
				elbl.employee_code.setValue(emp.employee_code.getValue()); // 工号
				elbl.id_number.setValue(emp.id_number.getValue()); // 身份证号
				elbl.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				elbl.mnemonic_code.setValue(emp.mnemonic_code.getValue()); // 助记码
				elbl.email.setValue(emp.email.getValue()); // 邮箱/微信
				elbl.empstatid.setValue(emp.empstatid.getValue()); // 人员状态
				elbl.telphone.setValue(emp.telphone.getValue()); // 电话
				elbl.hiredday.setValue(emp.hiredday.getValue()); // 聘用日期
				elbl.degree.setValue(emp.degree.getValue()); // 学历
				elbl.probation.setValue("0"); // 考察期
				elbl.odorgid.setValue(emp.orgid.getValue()); // 调动前部门ID
				elbl.odorgcode.setValue(emp.orgcode.getValue()); // 调动前部门编码
				elbl.odorgname.setValue(emp.orgname.getValue()); // 调动前部门名称
				elbl.odlv_id.setValue(emp.lv_id.getValue()); // 调动前职级ID
				elbl.odlv_num.setValue(emp.lv_num.getValue()); // 调动前职级
				elbl.odhg_id.setValue(emp.hg_id.getValue()); // 调动前职等ID
				elbl.odhg_code.setValue(emp.hg_code.getValue()); // 调动前职等编码
				elbl.odhg_name.setValue(emp.hg_name.getValue()); // 调动前职等名称
				elbl.odospid.setValue(emp.ospid.getValue()); // 调动前职位ID
				elbl.odospcode.setValue(emp.ospcode.getValue()); // 调动前职位编码
				elbl.odsp_name.setValue(emp.sp_name.getValue()); // 调动前职位名称
				elbl.oldhwc_namezl.setValue(null); // 调动前职类
				// elbl.neworgid.setValue(org.orgid.getValue()); // 调动后部门ID
				// elbl.neworgcode.setValue(org.code.getValue()); // 调动后部门编码
				// elbl.neworgname.setValue(org.orgname.getValue()); // 调动后部门名称
				// elbl.note.setValue(remark); // 备注
				rlls.add(elbl);
			}
		}
		return rlls.tojson();
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
