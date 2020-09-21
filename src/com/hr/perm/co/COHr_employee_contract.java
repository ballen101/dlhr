package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.perm.entity.Hr_contract_version;
import com.hr.perm.entity.Hr_empconbatch_line;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_contract;

@ACO(coname = "web.hr.employeecontract")
public class COHr_employee_contract {
	@ACOAction(eventname = "countContracts", Authentication = true, ispublic = false, notes = "获取员工已签订合同数")
	public String countContracts() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		Hr_employee_contract hrecon = new Hr_employee_contract();
		String sqlstr = "SELECT * FROM hr_employee_contract WHERE er_id="
				+ er_id + "  AND contractstat IN (1,3,6) AND stat=9";

		return hrecon.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findExpireContracts", Authentication = true, ispublic = false, notes = "查询即将到期的合同")
	public String findExpireContracts() throws Exception {
		String sqlstr = "SELECT con.*,emp.empstatid FROM hr_employee_contract con,hr_employee emp " +
				" WHERE con.is_remind=1 AND con.stat=9 AND con.contractstat=1 AND con.deadline_type=1 " +
				" AND DATE_ADD(CURDATE(), INTERVAL 2 MONTH) >= con.end_date AND CURDATE()<=con.end_date  AND con.er_id=emp.er_id AND emp.empstatid<11 ";
		// Hr_employee_contract hrecon = new Hr_employee_contract();
		String[] notnull = {};
		String orderby = " end_date asc ";
		return new CReport(sqlstr, orderby, notnull).findReport();
	}

	public void setExpireContracts(String conid) throws Exception {// 将过期的合同设置为失效
		Hr_employee_contract hrecon = new Hr_employee_contract();
		hrecon.findByID(conid);
		if (hrecon.isEmpty()) {
			throw new Exception("无法找到id为：" + conid + "的合同资料！");
		}
		if (hrecon.deadline_type.getAsInt() == 1) {
			Date now = new Date();
			if (hrecon.end_date.getAsDatetime().before(now)) {
				hrecon.contractstat.setAsInt(3);
				hrecon.save();
			}
		}
	}

	@ACOAction(eventname = "conimpexcel", Authentication = true, notes = "从excel导入签订合同信息")
	public String conimpexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile(p, orgid);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}

	private String parserExcelFile(Shw_physic_file pf, String orgid) throws Exception {
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
		return parserExcelSheet(aSheet, orgid);
	}

	// 序号 合同编号 合同名称 工号 姓名 机构 职位 职级 签订次数 入职日期 合同类型 备注

	private String parserExcelSheet(Sheet aSheet, String orgid) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		int empcol = -1;
		int memocol = -1;
		int codecol = -1;
		int namecol = -1;
		int typecol = -1;
		int sdcol = -1;
		int edcol = -1;
		Row aRow = aSheet.getRow(0);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			Cell aCell = aRow.getCell(col);
			String celltext = CExcelUtil.getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			if ("工号".equals(celltext.trim())) {
				empcol = col;
			}
			if ("备注".equals(celltext.trim())) {
				memocol = col;
			}
			if ("合同编号".equals(celltext.trim())) {
				codecol = col;
			}
			if ("合同名称".equals(celltext.trim())) {
				namecol = col;
			}
			if ("合同类型".equals(celltext.trim())) {
				typecol = col;
			}
			if ("签订日期".equals(celltext.trim())) {
				sdcol = col;
			}
			if ("截止日期".equals(celltext.trim())) {
				edcol = col;
			}
		}
		if ((empcol == -1) || (memocol == -1) || (codecol == -1) || (namecol == -1) || (typecol == -1) || (sdcol == -1) || (edcol == -1)) {
			throw new Exception("没找到【工号】或【备注】或【合同编号】或【合同名称】或【合同类型】列");
		}

		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty()) {
			throw new Exception("没找到ID为【" + orgid + "】的机构");
		}
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		CJPALineData<Hr_empconbatch_line> rls = new CJPALineData<Hr_empconbatch_line>(Hr_empconbatch_line.class);
		Hr_employee emp = new Hr_employee();
		Shworg orge = new Shworg();
		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {
				Cell aCell = aRow.getCell(empcol);
				String employee_code = CExcelUtil.getCellValue(aCell);
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;

				BigDecimal ec = new BigDecimal(employee_code);
				DecimalFormat df = new DecimalFormat("000000");
				employee_code = df.format(ec);

				emp.findBySQL("select * from hr_employee where employee_code='" + employee_code + "'",
						false);
				if (emp.isEmpty())
					throw new Exception("没有找到工号为【" + employee_code + "】的员工资料");

				orge.clear();
				orge.findByID(emp.orgid.getValue());
				if (orge.isEmpty())
					throw new Exception("没找到ID为【" + emp.orgid.getValue() + "】的机构");

				if (orge.idpath.getValue().indexOf(org.idpath.getValue()) == -1) {
					throw new Exception("员工【" + employee_code + "】不属于此签约机构");
				}

				int empstatid = emp.empstatid.getAsInt();
				if ((empstatid == 6) || (empstatid == 11) || (empstatid == 12) || (empstatid == 13))
					throw new Exception("工号为【" + employee_code + "】的员工不是在职状态不能签订合同");

				aCell = aRow.getCell(memocol);
				String remark = CExcelUtil.getCellValue(aCell);
				if ((remark == null) || (remark.isEmpty()))
					remark = null;

				aCell = aRow.getCell(codecol);
				String concode = CExcelUtil.getCellValue(aCell);
				if ((concode == null) || (concode.isEmpty()))
					concode = null;

				aCell = aRow.getCell(namecol);
				String conname = CExcelUtil.getCellValue(aCell);
				if ((conname == null) || (conname.isEmpty()))
					conname = null;

				aCell = aRow.getCell(sdcol);
				String sdate = CExcelUtil.getCellValue(aCell);
				if ((sdate == null) || (sdate.isEmpty()))
					sdate = null;

				aCell = aRow.getCell(edcol);
				String edate = CExcelUtil.getCellValue(aCell);
				if ((edate == null) || (edate.isEmpty()))
					edate = null;

				int number = countSignNumber(emp.er_id.getValue()) + 1;

				Hr_empconbatch_line ecb = new Hr_empconbatch_line();

				ecb.er_id.setValue(emp.er_id.getValue()); // 人事ID
				ecb.employee_code.setValue(emp.employee_code.getValue()); // 工号
				ecb.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				ecb.hiredday.setValue(emp.hiredday.getValue()); // 聘用日期
				ecb.contract_type.setValue(dictemp.getValueByCation("758", CExcelUtil.getCellValue(aRow.getCell(typecol)))); // 合同类型
				ecb.orgid.setValue(emp.orgid.getValue()); // 部门ID
				ecb.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				ecb.orgname.setValue(emp.orgname.getValue()); // 部门名称
				ecb.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
				ecb.lv_num.setValue(emp.lv_num.getValue()); // 职级
				ecb.ospid.setValue(emp.ospid.getValue()); // 职位ID
				ecb.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				ecb.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				ecb.remark.setValue(remark); // 备注
				ecb.sign_number.setAsInt(number); // 签订次数
				ecb.con_number.setValue(concode); // 合同编号
				ecb.contract_name.setValue(conname); // 合同名称
				ecb.sign_date.setValue(sdate); // 签订日期
				ecb.end_date.setValue(edate); // 截止日期
				rls.add(ecb);
			}
		}
		return rls.tojson();
	}

	public static int countSignNumber(String erid) throws Exception {
		Hr_employee_contract hrecon = new Hr_employee_contract();
		String sqlstr = "SELECT count(*) ct FROM hr_employee_contract WHERE er_id="
				+ erid + "  AND contractstat IN (1,3,6) AND stat=9";
		return Integer.valueOf(hrecon.pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	@ACOAction(eventname = "impcontractlistexcel", Authentication = true, ispublic = false, notes = "批量导入合同Excel(初始化导入原始合同)")
	public String impcontractlistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_con(p, batchno);
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

	private int parserExcelFile_con(Shw_physic_file pf, String batchno) throws Exception {
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
//		Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//				: new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_con(aSheet, batchno);
	}

	private int parserExcelSheet_con(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_con();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_employee emp = new Hr_employee();
		Hr_employee_contract hrecon = new Hr_employee_contract();
		Hr_employee_contract contract = new Hr_employee_contract();
		CDBConnection con = emp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String employee_code = v.get("employee_code");
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				rst++;
				emp.clear();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
				if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在人事资料");
				int contype = Integer.valueOf(dictemp.getVbCE("758", v.get("contract_type"), false, "工号【" + employee_code + "】合同类型【" + v.get("contract_type")
						+ "】不存在"));
				int constat = Integer.valueOf(dictemp.getVbCE("764", v.get("contractstat"), false, "工号【" + employee_code + "】合同状态【" + v.get("contractstat")
						+ "】不存在"));
				String sqlstr1 = "";
				if (constat == 1) {
					sqlstr1 = "SELECT * FROM `hr_employee_contract` WHERE contractstat=" + constat + " AND contract_type=" + contype
							+ "  and stat=9 AND employee_code='" + employee_code + "'";
					contract.clear();
					contract.findBySQL(sqlstr1);
					if (!contract.isEmpty())
						throw new Exception("工号【" + employee_code + "】有已签订的或审批中的合同");
				}

				hrecon.clear();
				hrecon.er_id.setValue(emp.er_id.getValue());
				hrecon.employee_code.setValue(emp.employee_code.getValue()); // 工号
				hrecon.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				hrecon.hiredday.setValue(emp.hiredday.getValue()); // 聘用日期
				hrecon.contract_type.setValue(dictemp.getVbCE("758", v.get("contract_type"), false, "工号【" + employee_code + "】合同类型【" + v.get("contract_type")
						+ "】不存在")); // 合同类型
				hrecon.orgid.setValue(emp.orgid.getValue()); // 部门ID
				hrecon.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				hrecon.orgname.setValue(emp.orgname.getValue()); // 部门名称
				hrecon.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
				hrecon.lv_num.setValue(emp.lv_num.getValue()); // 职级
				hrecon.ospid.setValue(emp.ospid.getValue()); // 职位ID
				hrecon.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				hrecon.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				hrecon.is_remind.setAsInt(1); // 是否预警
				hrecon.ispermanent.setAsInt(1); // 是否正式员工
				hrecon.remark.setValue(v.get("remark")); // 备注
				hrecon.sign_number.setValue(v.get("sign_number")); // 签订次数
				hrecon.con_number.setValue(v.get("con_number")); // 合同编号
				hrecon.contract_name.setValue(v.get("contract_name")); // 合同名称
				hrecon.signyears.setValue(v.get("signyears")); // 签订年限
				hrecon.contractstat.setValue(dictemp.getVbCE("764", v.get("contractstat"), false, "工号【" + employee_code + "】合同状态【" + v.get("contractstat")
						+ "】不存在")); // 合同状态
				hrecon.deadline_type.setValue(dictemp.getVbCE("771", v.get("deadline_type"), false, "工号【" + employee_code + "】期限类型【" + v.get("deadline_type")
						+ "】不存在")); // 期限类型
				hrecon.sign_date.setValue(v.get("sign_date")); // 签订日期
				hrecon.idpath.setValue(emp.idpath.getValue());
				int dlt = Integer.valueOf(dictemp.getValueByCation("771", v.get("deadline_type")));
				if (dlt == 1) {
					String eddate = v.get("end_date");
					if ((eddate == null) || (eddate.isEmpty())) {
						throw new Exception("工号【" + employee_code + "】的编号为" + v.get("con_number") + "的合同期限类型为【有固定期限】时合同截止时间不能为空！");

					} else {
						hrecon.end_date.setValue(v.get("end_date")); // 截止日期
					}
				}
				hrecon.save(con);
				hrecon.wfcreate(null, con);
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

	private List<CExcelField> initExcelFields_con() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("合同编号", "con_number", true));
		efields.add(new CExcelField("合同类型", "contract_type", true));
		efields.add(new CExcelField("合同状态", "contractstat", true));
		efields.add(new CExcelField("合同名称", "contract_name", true));
		efields.add(new CExcelField("签订年限", "signyears", true));
		efields.add(new CExcelField("签订日期", "sign_date", true));
		efields.add(new CExcelField("期限类型", "deadline_type", true));
		efields.add(new CExcelField("截止日期", "end_date", true));
		efields.add(new CExcelField("签订次数", "sign_number", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "impcontractversionexcel", Authentication = true, ispublic = false, notes = "批量导入合同版本")
	public String impcontractversionexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_ver(p, batchno);
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

	private int parserExcelFile_ver(Shw_physic_file pf, String batchno) throws Exception {
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
//		Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//				: new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet_ver(aSheet, batchno);
	}

	private int parserExcelSheet_ver(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_ver();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_contract_version conver = new Hr_contract_version();

		CDBConnection con = conver.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String ver = v.get("version");
				if ((ver == null) || (ver.isEmpty()))
					continue;
				rst++;

				conver.clear();
				conver.version_describe.setValue(v.get("version_describe"));// 版本描述
				conver.version.setValue(v.get("version")); // 版本号
				conver.contract_type.setAsInt(1); // 合同类型
				conver.use_date.setValue(v.get("use_date")); // 生效日期
				conver.usable.setValue(dictemp.getVbCE("5", v.get("usable"), false, "版本号【" + ver + "】的合同版本可用状态不存在")); // 是否可用
				conver.invaliddate.setValue(v.get("invaliddate")); // 失效日期
				conver.remark.setValue(v.get("remark")); // 备注

				conver.save(con);
				conver.wfcreate(null, con);
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

	private List<CExcelField> initExcelFields_ver() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("版本描述", "version_describe", true));
		efields.add(new CExcelField("版本号", "version", true));
		efields.add(new CExcelField("生效日期", "use_date", true));
		efields.add(new CExcelField("失效日期", "invaliddate", true));
		efields.add(new CExcelField("是否可用", "usable", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

}
