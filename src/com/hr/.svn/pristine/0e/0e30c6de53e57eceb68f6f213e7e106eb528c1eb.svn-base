package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_standposition;
import com.hr.inface.entity.View_TxZlEmployee_zp;
import com.hr.perm.entity.Hr_black_add;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_leanexp;
import com.hr.pm.entity.Hrpm_rstmonthex;

@ACO(coname = "web.hr.perm")
public class COHRPerm {

	@ACOAction(eventname = "findOrgpositionByOspid", Authentication = true, notes = "根据机构职位ID获取职类")
	public String findOrgpositionByOspid() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String ospid = CorUtil.hashMap2Str(parms, "ospid", "需要参数ospid");
		Hr_standposition ho = new Hr_standposition();
		String sqlstr = "SELECT sp.* FROM hr_standposition sp,hr_orgposition op WHERE sp.sp_id=op.sp_id AND op.ospid=" + ospid;
		ho.findBySQL(sqlstr, false);
		return ho.tojson();
	}

	@ACOAction(eventname = "getTrantype4byOspids", Authentication = true, notes = "根据机构职位ID获取职类")
	public String getTrantype4byOspids() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String odospid = CorUtil.hashMap2Str(parms, "odospid", "需要参数odospid");
		String newospid = CorUtil.hashMap2Str(parms, "newospid", "需要参数newospid");
		Hr_orgposition odosp = new Hr_orgposition();
		Hr_orgposition newosp = new Hr_orgposition();
		odosp.findByID(odospid, false);
		if (odosp.isEmpty()) {
			throw new Exception("ID为【" + odospid + "】的机构职位不存在");
		}
		newosp.findByID(newospid, false);
		if (newosp.isEmpty()) {
			throw new Exception("ID为【" + newospid + "】的机构职位不存在");
		}
		JSONObject rst = new JSONObject();
		if (odosp.hw_codezl.getValue().equalsIgnoreCase("M") || newosp.hw_codezl.getValue().equalsIgnoreCase("M")) {
			rst.put("rst", "1");
		} else if ((odosp.ishighrisk.getAsIntDefault(0) == 1) || (newosp.ishighrisk.getAsIntDefault(0) == 1)) {
			rst.put("rst", "2");
		} else if (newosp.isdreamposition.getAsIntDefault(0) == 1) {
			rst.put("rst", "3");
		} else
			rst.put("rst", "4");
		return rst.toString();
	}

	@ACOAction(eventname = "findLastBlackAddInfo", Authentication = true, notes = "根据员工ID获取最后加黑")
	public String findLastBlackAddInfo() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "需要参数er_id");
		Hr_black_add ba = new Hr_black_add();
		String sqlstr = "SELECT * FROM hr_black_add WHERE er_id=" + er_id + " AND stat=9"
				+ " ORDER BY createtime DESC LIMIT 0,1";
		return ba.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "printEmployeeCard", Authentication = true, notes = "打印厂牌")
	public String printEmployeeCard() throws Exception {
		JSONObject jo = JSONObject.fromObject(CSContext.getPostdata());
		String erids = jo.getString("erids");
		if ((erids == null) || (erids.isEmpty()))
			throw new Exception("需要打印厂牌的人事ID列表【erids】不能为空");

		String sqlstr = "select * from hr_employee where er_id in(" + erids + ")";
		CJPALineData<Hr_employee> emps = new CJPALineData<Hr_employee>(Hr_employee.class);
		emps.findDataBySQL(sqlstr);

		return null;
	}

	@ACOAction(eventname = "test", Authentication = true, notes = "测试")
	public String test() throws Exception {
		String sqlstr = "select top 1 * from HRMS.dbo.view_TxZlEmployee_zp where pydate>='2017-08-20'";
		View_TxZlEmployee_zp ezp = new View_TxZlEmployee_zp();
		ezp.findBySQL(sqlstr);
		return ezp.tojson();
	}

	@ACOAction(eventname = "imppmmexcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String imppmmexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserPmmExcelFile(p, batchno);
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

	private int parserPmmExcelFile(Shw_physic_file pf, String batchno) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
		// : new XSSFWorkbook(new FileInputStream(fullname));
		
		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserPmmExcelSheet(aSheet, batchno);
	}

	private int parserPmmExcelSheet(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initPmmExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_employee emp = new Hr_employee();
		Hrpm_rstmonthex pm = new Hrpm_rstmonthex();
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
				pm.clear();
				pm.er_id.setValue(emp.er_id.getValue()); // 人事ID
				pm.employee_code.setValue(employee_code); // 工号
				pm.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				pm.pmtype.setValue(dictemp.getVbCE("1289", v.get("pmtype"), true, "工号【" + employee_code + "】职位类别【" + v.get("pmtype") + "】不存在")); // 1 主职 2 兼职
				pm.orgname.setValue(v.get("orgname")); // 机构
				pm.sp_name.setValue(v.get("sp_name")); // 职位
				pm.lv_num.setValue(v.get("lv_num")); // 职级
				pm.pmyear.setValue(v.get("pmyear")); // 年
				pm.pmonth.setValue(v.get("pmonth")); // 月
				pm.qrst.setValue(v.get("qrst")); // 绩效
				pm.createtime.setAsDatetime(new Date()); // 创建时间
				pm.remark.setValue(v.get("remark")); // 备注
				pm.save(con);
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

	private List<CExcelField> initPmmExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("年度", "pmyear", true));
		efields.add(new CExcelField("月份", "pmonth", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("职位类型", "pmtype", true));
		efields.add(new CExcelField("机构", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("绩效", "qrst", true));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}

}
