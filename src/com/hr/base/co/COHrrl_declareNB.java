package com.hr.base.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hrrl_declare;
import com.hr.base.entity.Hrrl_standlib;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.rldecnb")
public class COHrrl_declareNB {
	@ACOAction(eventname = "impexcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String impexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile(p, batchno);
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

	private int parserExcelFile(Shw_physic_file pf, String batchno) throws Exception {
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
		return parserExcelSheet(aSheet, batchno);
	}

	private int parserExcelSheet(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_employee emp = new Hr_employee();
		Hr_employee empr = new Hr_employee();
		Hrrl_standlib sl = new Hrrl_standlib();
		Hrrl_declare decl = new Hrrl_declare();
		CDBConnection con = emp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				emp.clear();
				empr.clear();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + v.get("employee_code") + "'");
				if (emp.isEmpty())
					throw new Exception("工号【" + v.get("employee_code") + "】不存在人事资料");
				empr.clear();
				empr.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + v.get("remployee_code") + "'");
				if (empr.isEmpty())
					throw new Exception("工号【" + v.get("remployee_code") + "】不存在人事资料");
				sl.clear();
				sl.findBySQL("select * from hrrl_standlib where lscode='" + v.get("lscode") + "'");
				if (sl.isEmpty())
					throw new Exception("关联关系【" + v.get("lscode") + "】不存在");
				rst++;
				decl.clear();
				decl.ldtype.setValue("1"); // 申报类型 1内部员工 2客户 3供应商
				decl.dcldate.setValue(v.get("dcldate")); // 申报日期
				decl.slid.setValue(sl.slid.getValue()); // 关联关系ID
				decl.rlname.setValue(sl.rlname.getValue()); // 关联关系
				decl.rllabel_b.setValue(sl.rllabel_a.getValue()); // 联关系称谓
				decl.rllabel_a.setValue(sl.rllabel_b.getValue()); // 联关系自称
				decl.rltype1.setValue(sl.rltype1.getValue()); // 联关系类型
				decl.rltype2.setValue(sl.rltype2.getValue()); // 联关系类别
				decl.hrvlevel.setValue(sl.hrvlevel.getValue()); // 联关系分级
				decl.rlext.setValue(sl.rlext.getValue()); // 联关系说明
				decl.er_id.setValue(emp.er_id.getValue()); // 档案ID
				decl.employee_code.setValue(emp.employee_code.getValue()); // 工号
				decl.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				decl.sex.setValue(emp.sex.getValue()); // 性别
				decl.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				decl.ljdate.setValue(emp.ljdate.getValue()); // 离职日期
				decl.orgid.setValue(emp.orgid.getValue()); // 部门ID
				decl.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				decl.orgname.setValue(emp.orgname.getValue()); // 部门名称
				decl.ospid.setValue(emp.ospid.getValue()); // 职位ID
				decl.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				decl.sp_name.setValue(emp.sp_name.getValue()); // 职位
				decl.hwc_namezl.setValue(emp.hwc_namezl.getValue()); // 职类
				decl.hg_name.setValue(emp.hg_name.getValue()); // 职等
				decl.lv_num.setValue(emp.lv_num.getValue()); // 职级
				decl.orghrlev.setValue("0"); // 机构人事层级
				decl.emplev.setValue("0"); // 人事层级
				decl.rer_id.setValue(empr.er_id.getValue()); // 关联档案ID
				decl.remployee_code.setValue(empr.employee_code.getValue()); // 关联工号
				decl.remployee_name.setValue(empr.employee_name.getValue()); // 关联姓名/推荐人姓名
				decl.rsex.setValue(empr.sex.getValue()); // 关联性别
				decl.rhiredday.setValue(empr.hiredday.getValue()); // 关联入职日期
				decl.rljdate.setValue(empr.ljdate.getValue()); // 关联离职日期
				decl.rorgid.setValue(empr.orgid.getValue()); // 关联部门ID
				decl.rorgcode.setValue(empr.orgcode.getValue()); // 关联部门编码
				decl.rorgname.setValue(empr.orgname.getValue()); // 关联部门名称/推荐人工作单位
				decl.rospid.setValue(empr.ospid.getValue()); // 关联职位ID
				decl.rospcode.setValue(empr.ospcode.getValue()); // 关联职位编码
				decl.rsp_name.setValue(empr.sp_name.getValue()); // 关联职位/推荐人职务
				decl.rhwc_namezl.setValue(empr.hwc_namezl.getValue()); // 关联职类
				decl.rhg_name.setValue(empr.hg_name.getValue()); // 关联职等
				decl.rlv_num.setValue(empr.lv_num.getValue()); // 关联职级
				decl.rlmanagetype.setValue(dictemp.getVbCE("1146", v.get("rlmanagetype"), true, "管理关系类别【" + v.get("rlmanagetype") + "】不存在")); // 管理关系类别
				decl.rctrms.setValue(dictemp.getVbCE("1141", v.get("rctrms"), true, "管控措施【" + v.get("rctrms") + "】不存在")); // 管控措施 1 不管控、2调动处理 3申请豁免 4、其他
				decl.rccode.setValue(v.get("rccode")); // 管控单编码
				decl.useable.setValue(dictemp.getVbCE("5", v.get("useable"), true, "是否有效【" + v.get("useable") + "】不存在")); // 有效
				decl.disusetime.setValue(v.get("disusetime")); // 失效时间
				decl.remark.setValue(v.get("remark")); // 备注
				decl.idpath.setValue(emp.idpath.getValue());
				decl.save(con);
				decl.wfcreate(null, con);
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

	private List<CExcelField> initExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("申报日期", "dcldate", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("关联工号", "remployee_code", true));
		efields.add(new CExcelField("关联关系编码", "lscode", true));
		efields.add(new CExcelField("管理关系类别", "rlmanagetype", true));
		efields.add(new CExcelField("管控措施", "rctrms", true));
		efields.add(new CExcelField("管控单编码", "rccode", false));
		efields.add(new CExcelField("有效", "usable", true));
		efields.add(new CExcelField("失效时间", "disusetime", false));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}
}
