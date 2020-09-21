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

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.base.entity.Hr_employeestat;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_emptransferbatch_line;

@ACO(coname = "web.hr.emtranbatch")
public class COHr_emptransferbatch {
	@ACOAction(eventname = "impexcel", Authentication = true, notes = "从excel导入编制发布信息")
	public String impexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String sorgid = CorUtil.hashMap2Str(CSContext.getParms(), "sorgid", "需要参数sorgid");
		String dorgid = CorUtil.hashMap2Str(CSContext.getParms(), "dorgid", "需要参数dorgid");

		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile(p, sorgid, dorgid);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}

	@ACOAction(eventname = "findOrgPostionsbatch", Authentication = true, ispublic = false, notes = "查询机构职位")
	public String findOrgPostionsbatch() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(eparms);
		if (jps.size() == 0)
			throw new Exception("需要查询参数");
		if (!JSONParm.hasParmName(jps, "orgid"))
			throw new Exception("需要参数orgid");
		if (!JSONParm.hasParmName(jps, "odospid"))
			throw new Exception("需要参数odospid");
		JSONParm opm = JSONParm.getParmByName(jps, "odospid");
		String odospid = opm.getParmvalue();
		jps.remove(opm);

		JSONParm oorg = JSONParm.getParmByName(jps, "orgid");
		String orgid = oorg.getParmvalue();
		jps.remove(oorg);

		Hr_orgposition odho = new Hr_orgposition();
		odho.findByID(odospid, false);
		if (odho.isEmpty())
			throw new Exception("ID为【" + odospid + "】的机构职位不存在");

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");

		Hr_orgposition ho = new Hr_orgposition();
		String where = CjpaUtil.buildFindSqlByJsonParms(ho, jps);
		boolean has37 = COHr_employee_transfer.hasaccrole37();
		String sqlstr = "select * from hr_orgposition where usable=1 and idpath like '" + org.idpath.getValue() + "%'";
		if (!has37) {
			sqlstr = sqlstr + " and sp_id=" + odho.sp_id.getValue() + " and lv_num=" + odho.lv_num.getValue();
		}
		sqlstr = sqlstr + where;
		return new CReport(sqlstr, new String[] {}).findReport(new String[] { "orgid", "odospid" }, null);
	}

	private String parserExcelFile(Shw_physic_file pf, String sorgid, String dorgid) throws Exception {
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
		return parserExcelSheet(aSheet, sorgid, dorgid);
	}

	// 序号 工号 姓名 调动前 调动后 备注
	// 序号 工号 姓名 调动后机构职位编码 备注

	private String parserExcelSheet(HSSFSheet aSheet, String sorgid, String dorgid) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		int empcol = -1;
		int memocol = -1;
		int opcol = -1;
		HSSFRow aRow = aSheet.getRow(0);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			HSSFCell aCell = aRow.getCell(col);
			String celltext = CExcelUtil.getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			if ("工号".equals(celltext.trim())) {
				empcol = col;
			}
			if ("调动后机构职位编码".equals(celltext.trim())) {
				opcol = col;
			}
			if ("备注".equals(celltext.trim())) {
				memocol = col;
			}
		}
		if ((empcol == -1) || (opcol == -1) || (memocol == -1)) {
			throw new Exception("没找到【工号】【调动后机构职位编码】或【备注】列");
		}

		Shworg sorg = new Shworg();
		sorg.findByID(sorgid);
		if (sorg.isEmpty()) {
			throw new Exception("没找到ID为【" + sorgid + "】的机构");
		}

		boolean ctrl = (Integer.valueOf(HrkqUtil.getParmValueErr("TRANSFERBATCH_CTROL")) == 1);
		boolean hasr37 = COHr_employee_transfer.hasaccrole37();

		CJPALineData<Hr_emptransferbatch_line> rls = new CJPALineData<Hr_emptransferbatch_line>(Hr_emptransferbatch_line.class);
		Hr_employee emp = new Hr_employee();
		Hr_employeestat es = new Hr_employeestat();
		Hr_orgposition oop = new Hr_orgposition();
		Hr_orgposition oldhop = new Hr_orgposition();
		Hr_orgposition hop = new Hr_orgposition();
		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {
				HSSFCell aCell = aRow.getCell(empcol);

				String employee_code = CExcelUtil.getCellValue(aCell);
				if (employee_code == null)
					continue;

				emp.findBySQL("select * from hr_employee where employee_code='" + employee_code + "'", false);
				if (emp.isEmpty())
					throw new Exception("没有找到工号为【" + employee_code + "】的员工资料");
				/*
				 * if (ctrl) {
				 * if (emp.orgid.getAsInt() != sorg.orgid.getAsInt()) {
				 * throw new Exception("调出员工【" + employee_code + "】不属于选定【调出机构】");
				 * }
				 * }
				 */

				int empstatid = emp.empstatid.getAsInt();
				es.clear();
				es.findBySQL("select * from hr_employeestat where statvalue=" + empstatid);
				if (es.allowtransfer.getAsIntDefault(0) != 1) {
					throw new Exception("工号为【" + employee_code + "】的员工的人事状态为【" + es.language1.getValue() + "】不能调动");
				}

				oop.findBySQL("select * from hr_orgposition where ospid=" + emp.ospid.getValue());

				aCell = aRow.getCell(opcol);
				String ospcode = CExcelUtil.getCellValue(aCell);
				if ((ospcode == null) || (ospcode.trim().isEmpty()))
					throw new Exception("工号为【" + employee_code + "】的员工【调动后机构职位编码】不能为空");
				hop.clear();
				hop.findBySQL("select * from hr_orgposition where ospcode='" + ospcode + "'", false);
				if (hop.isEmpty())
					throw new Exception("编码为【" + ospcode + "】的机构职位不存在");

				// if (ctrl && (!hasr37)) {
				// if (hop.orgid.getAsInt() != Integer.valueOf(dorgid)) {
				// throw new Exception("编码为【" + ospcode + "】的机构职位不属于调入机构");
				// }
				// }

				aCell = aRow.getCell(memocol);
				String remark = CExcelUtil.getCellValue(aCell);
				if ((remark == null) || (remark.isEmpty()))
					remark = null;

				Hr_emptransferbatch_line etb = new Hr_emptransferbatch_line();
				etb.er_id.setValue(emp.er_id.getValue()); // 人事ID
				etb.employee_code.setValue(emp.employee_code.getValue()); // 工号
				etb.id_number.setValue(emp.id_number.getValue()); // 身份证号
				etb.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				etb.mnemonic_code.setValue(emp.mnemonic_code.getValue()); // 助记码
				etb.email.setValue(emp.email.getValue()); // 邮箱/微信
				etb.empstatid.setValue(emp.empstatid.getValue()); // 人员状态
				etb.telphone.setValue(emp.telphone.getValue()); // 电话
				etb.hiredday.setValue(emp.hiredday.getValue()); // 聘用日期
				etb.degree.setValue(emp.degree.getValue()); // 学历
				etb.probation.setValue("0"); // 考察期
				etb.odorgid.setValue(emp.orgid.getValue()); // 调动前部门ID
				etb.odorgcode.setValue(emp.orgcode.getValue()); // 调动前部门编码
				etb.odorgname.setValue(emp.orgname.getValue()); // 调动前部门名称
				etb.odlv_id.setValue(emp.lv_id.getValue()); // 调动前职级ID
				etb.odlv_num.setValue(emp.lv_num.getValue()); // 调动前职级
				etb.odhg_id.setValue(emp.hg_id.getValue()); // 调动前职等ID
				etb.odhg_code.setValue(emp.hg_code.getValue()); // 调动前职等编码
				etb.odhg_name.setValue(emp.hg_name.getValue()); // 调动前职等名称
				etb.odospid.setValue(emp.ospid.getValue()); // 调动前职位ID
				etb.odospcode.setValue(emp.ospcode.getValue()); // 调动前职位编码
				etb.odsp_name.setValue(emp.sp_name.getValue()); // 调动前职位名称
				etb.oldhwc_namezl.setValue(oop.hwc_namezl.getValue()); // 调动前职类
				etb.oldzwxz.setValue("非脱产");

				etb.newzwxz.setValue("非脱产"); // 调动后职位性质
				etb.newhwc_namezl.setValue(hop.hwc_namezl.getValue()); // 调动后职类
				etb.newcalsalarytype.setValue(null); // 调动后计薪方式
				etb.newattendtype.setValue(null); // 调动后出勤类别
				etb.newsp_name.setValue(hop.sp_name.getValue()); // 调动后职位名称
				etb.newospcode.setValue(hop.sp_code.getValue()); // 调动后职位编码
				etb.newospid.setValue(hop.ospid.getValue()); // 调动后职位ID
				etb.newhg_code.setValue(hop.hg_code.getValue()); // 调动后职等编码
				etb.newhg_id.setValue(hop.hg_id.getValue()); // 调动后职等ID
				etb.newlv_num.setValue(hop.lv_num.getValue()); // 调动后职级
				etb.newlv_id.setValue(hop.lv_id.getValue()); // 调动后职级ID
				etb.neworgname.setValue(hop.orgname.getValue()); // 调动后部门名称
				etb.neworgcode.setValue(hop.orgcode.getValue()); // 调动后部门编码
				etb.neworgid.setValue(hop.orgid.getValue()); // 调动后部门ID
				etb.newhg_name.setValue(hop.hg_name.getValue()); // 调动后职等名称

				etb.remark.setValue(remark); // 备注
				if (ctrl && (!hasr37)) {
					oldhop.clear();
					oldhop.findByID(etb.odospid.getValue(), false);
					if (oldhop.isEmpty())
						throw new Exception("员工【" + emp.employee_name.getValue() + "】调动前机构职位ID【" + etb.odospid.getValue() + "】不存在");
					if (oldhop.sp_id.getAsIntDefault(0) != hop.sp_id.getAsIntDefault(-1)) {
						throw new Exception("员工【" + emp.employee_name.getValue() + "】批量调动前后不是同一标准职位");
					}
					if (etb.odlv_num.getAsFloat() != etb.newlv_num.getAsFloat()) {
						throw new Exception("员工【" + emp.employee_name.getValue() + "】批量调动前后职级不一致");
					}
				}

				rls.add(etb);
			}
		}
		return rls.tojson();
	}

}
