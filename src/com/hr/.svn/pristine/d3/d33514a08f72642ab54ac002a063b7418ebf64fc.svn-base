package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_empptjob_app;

@ACO(coname = "web.hr.empptja")
public class COHr_empptjob_app {

	@ACOAction(eventname = "impexcel", Authentication = true, ispublic = false, notes = "入职导入Excel")
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

		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fullname));
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		HSSFSheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		return parserExcelSheet(aSheet, batchno);
	}

	private int parserExcelSheet(HSSFSheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_employee emp = new Hr_employee();
		Hr_orgposition osp = new Hr_orgposition();
		Hr_empptjob_app ptja = new Hr_empptjob_app();
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
					throw new Exception("工号【" + employee_code + "】的人事资料不存在");
				ptja.clear();

				Date startdate = Systemdate.getDateByStr(v.get("startdate"));
				Date enddate = Systemdate.getDateByStr(v.get("enddate"));
				if (startdate.getTime() >= enddate.getTime())
					throw new Exception("工号【" + employee_code + "】兼职申请起止日期错误");

				String sd = Systemdate.getStrDateByFmt(startdate, "yyyy-MM-dd");
				String ed = Systemdate.getStrDateByFmt(enddate, "yyyy-MM-dd");

				osp.findBySQL("select * from hr_orgposition where ospcode='" + v.get("newospcode") + "'");
				if (osp.isEmpty())
					throw new Exception("工号【" + employee_code + "】兼职职位【" + v.get("newospcode") + "】不存在");
				if (osp.usable.getAsIntDefault(0) == 2)
					throw new Exception("工号【" + employee_code + "】兼职职位【" + v.get("newospcode") + "】不可用");

				if (emp.lv_num.getAsFloat() > osp.lv_num.getAsFloat()) {
					// System.out.println(emp.lv_num.getAsFloat() + ":" + osp.lv_num.getAsFloat());
					throw new Exception("工号【" + employee_code + "】兼职职级必须低于主职职级");
				}

				ptja.applaydate.setAsDatetime(new Date()); // 申请时间
				ptja.startdate.setValue(sd); // 开始时间
				ptja.enddate.setValue(ed); // 结束时间
				ptja.breaked.setValue("2"); // 已终止
				ptja.ptjalev.setValue(dictemp.getVbCE("734", v.get("ptjalev"), false, "工号【" + employee_code + "】的兼职层级【" + v.get("ptjalev") + "】不存在")); // 兼职层级
				ptja.ptjatype.setValue(dictemp.getVbCE("729", v.get("ptjatype"), false, "工号【" + employee_code + "】的兼职范围【" + v.get("ptjatype") + "】不存在")); // 兼职范围
				ptja.ptreason.setValue(v.get("ptreason")); // 兼职原因
				ptja.issubsidy.setValue(dictemp.getVbCE("5", v.get("issubsidy"), false, "工号【" + employee_code + "】是否申请补贴【" + v.get("issubsidy") + "】不存在")); // 申请补贴
				ptja.subsidyarm.setValue(v.get("subsidyarm")); // 月度补贴金额
				ptja.er_id.setValue(emp.er_id.getValue()); // 人事ID
				ptja.employee_code.setValue(emp.employee_code.getValue()); // 工号
				ptja.id_number.setValue(emp.id_number.getValue()); // 身份证号
				ptja.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				ptja.degree.setValue(emp.degree.getValue()); // 学历
				ptja.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				ptja.odorgid.setValue(emp.orgid.getValue()); // 主职部门ID
				ptja.odorgcode.setValue(emp.orgcode.getValue()); // 主职部门编码
				ptja.odorgname.setValue(emp.orgname.getValue()); // 主职部门名称
				ptja.odorghrlev.setValue("0"); // 主职部门人事级别 流程用到的 这里可以不处理
				ptja.odlv_id.setValue(emp.lv_id.getValue()); // 主职职级ID
				ptja.odlv_num.setValue(emp.lv_num.getValue()); // 主职职级
				ptja.odhg_id.setValue(emp.hg_id.getValue()); // 主职职等ID
				ptja.odhg_code.setValue(emp.hg_code.getValue()); // 主职职等编码
				ptja.odhg_name.setValue(emp.hg_name.getValue()); // 主职职等名称
				ptja.odospid.setValue(emp.ospid.getValue()); // 主职职位ID
				ptja.odospcode.setValue(emp.ospcode.getValue()); // 主职职位编码
				ptja.odsp_name.setValue(emp.sp_name.getValue()); // 主职职位名称
				ptja.odattendtype.setValue(v.get("odattendtype")); // 主职出勤类别
				ptja.oldhwc_namezl.setValue(emp.hwc_namezl.getValue()); // 主职职类
				ptja.neworgid.setValue(osp.orgid.getValue()); // 兼职部门ID
				ptja.neworgcode.setValue(osp.orgcode.getValue()); // 兼职部门编码
				ptja.neworgname.setValue(osp.orgname.getValue()); // 兼职部门名称
				ptja.newlv_id.setValue(osp.lv_id.getValue()); // 兼职职级ID
				ptja.newlv_num.setValue(osp.lv_num.getValue()); // 兼职职级
				ptja.newhg_id.setValue(osp.hg_id.getValue()); // 兼职职等ID
				ptja.newhg_code.setValue(osp.hg_code.getValue()); // 兼职职等编码
				ptja.newhg_name.setValue(osp.hg_name.getValue()); // 兼职职等名称
				ptja.neworghrlev.setValue("0"); // 兼职部门人事级别
				ptja.newospid.setValue(osp.ospid.getValue()); // 兼职职位ID
				ptja.newospcode.setValue(osp.ospcode.getValue()); // 兼职职位编码
				ptja.newsp_name.setValue(osp.sp_name.getValue()); // 兼职职位名称
				ptja.newattendtype.setValue(v.get("newhwc_namezl")); // 兼职出勤类别
				ptja.newhwc_namezl.setValue(osp.hwc_namezl.getValue()); // 兼职职类
				ptja.remark.setValue(v.get("remark")); // 备注
				ptja.save(con);
				ptja.wfcreate(null, con);
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
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("兼职层级", "ptjalev", true));
		efields.add(new CExcelField("兼职范围", "ptjatype", true));
		efields.add(new CExcelField("开始时间", "startdate", true));
		efields.add(new CExcelField("结束时间", "enddate", true));
		efields.add(new CExcelField("申请补贴", "issubsidy", true));
		efields.add(new CExcelField("月度补贴金额", "subsidyarm", true));
		efields.add(new CExcelField("兼职机构职位编码", "newospcode", true));
		efields.add(new CExcelField("主职出勤类别", "odattendtype", false));
		efields.add(new CExcelField("兼职出勤类别", "newattendtype", false));
		efields.add(new CExcelField("兼职原因", "ptreason", false));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}

}
