package com.hr.access.co;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.genco.COShwUser;
import com.corsair.server.generic.Shw_attach;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.access.ctr.UtilAccess;
import com.hr.access.entity.Hr_access_emauthority_list;
import com.hr.access.entity.Hr_access_list;
import com.hr.access.entity.Hr_access_orauthority;
import com.hr.access.entity.Hr_access_special;
import com.hr.asset.entity.Hr_asset_item;
import com.hr.asset.entity.Hr_asset_register;
import com.hr.asset.entity.Hr_asset_type;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.HRUtil;
import com.corsair.server.util.CReport;

@ACO(coname = "web.hr.Access")
public class COHr_access_special extends JPAController {

	// 流程完成时数据插入流水表
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doInserAccesslist(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doInserAccesslist(jpa, con);
		}
	}

	@Override
	/*
	 * 作废功能
	 * 作废时将表单中的门禁状态改为关闭，同时同步至门禁流水表
	 */
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		if (!HRUtil.hasRoles("35")) {
			throw new Exception("非门禁管理员，不允许作废");
		}
		Hr_access_special as = (Hr_access_special) jpa;
		Hr_access_list acl = new Hr_access_list();
		acl.findByID(as.access_list_id.getValue());
		if (acl.isEmpty())
			throw new Exception("ID为【" + as.access_list_id.getValue() + "】的门禁不存在");
		Hr_employee emp = new Hr_employee();
		emp.findBySQL("select * from hr_employee where employee_code='" + as.employee_code.getValue() + "'");
		if (emp.isEmpty())
			throw new Exception("工号为【" + as.employee_code.getValue() + "】的人事资料不存在");
		UtilAccess.appendOneAccess(con, acl, emp, 2, 4, as.access_special_id.getValue(), as.access_special_num.getValue(), "特殊授权作废");
		// as.access_status.setAsInt(2);
		// as.save(con);
		// Hr_access_emauthority_list ael = new Hr_access_emauthority_list();
		// ael.clear();
		// ael.source.setValue("4");
		// ael.source_id.setValue(as.access_special_id.getValue());
		// ael.source_num.setValue(as.access_special_num.getValue());
		// ael.change_reason.setValue("特殊授权");
		// ael.employee_code.setValue(as.employee_code.getValue());
		// ael.employee_name.setValue(as.employee_name.getValue());
		// ael.sex.setValue(as.sex.getValue());
		// ael.access_card_number.setValue(as.access_card_number.getValue());
		// ael.access_card_seq.setValue(as.access_card_seq.getValue());
		// ael.hiredday.setValue(as.hiredday.getValue());
		// ael.orgid.setValue(as.orgid.getValue());
		// ael.orgname.setValue(as.orgname.getValue());
		// ael.orgcode.setValue(as.orgcode.getValue());
		// ael.extorgname.setValue(as.extorgname.getValue());
		// ael.hwc_namezl.setValue(as.hwc_namezl.getValue());
		// ael.lv_id.setValue(as.lv_id.getValue());
		// ael.lv_num.setValue(as.lv_num.getValue());
		// ael.access_list_id.setValue(as.access_list_id.getValue());
		// ael.access_list_code.setValue(as.access_list_code.getValue());
		// ael.access_list_model.setValue(as.access_list_model.getValue());
		// ael.access_list_name.setValue(as.access_list_name.getValue());
		// ael.deploy_area.setValue(as.deploy_area.getValue());
		// ael.access_place.setValue(as.access_place.getValue());
		// ael.accrediter.setValue(as.accrediter.getValue());
		// ael.accredit_date.setAsDatetime(new Date());// .setValue(as.begin_date.getValue());
		// ael.remarks.setValue("特殊关闭");
		// ael.access_status.setValue("2");
		// ael.save(con);
		return null;
	}

	// 查询可用门禁
	@ACOAction(eventname = "findUsefulAccess", Authentication = true, ispublic = false, notes = "可用门禁查询")
	public String findUsefulAccess() throws Exception {
		String[] ignParms = {};// 忽略的查询条件
		Hr_access_list al = new Hr_access_list();
		String sqlstr = "SELECT * FROM hr_access_list t WHERE t.access_stat = 1";
		return new CReport(sqlstr, null).findReport(ignParms, null);
	}

	public void doInserAccesslist(CJPA jpa, CDBConnection con) throws Exception {
		//System.out.println("doInserAccesslist..................");
		Hr_access_special as = (Hr_access_special) jpa;
		Hr_access_list acl = new Hr_access_list();
		acl.findByID(as.access_list_id.getValue());
		if (acl.isEmpty())
			throw new Exception("ID为【" + as.access_list_id.getValue() + "】的门禁不存在");
		Hr_employee emp = new Hr_employee();
		emp.findBySQL("select * from hr_employee where employee_code='" + as.employee_code.getValue() + "'");
		if (emp.isEmpty())
			throw new Exception("工号为【" + as.employee_code.getValue() + "】的人事资料不存在");
		UtilAccess.appendOneAccess(con, acl, emp, 1, 3, as.access_special_id.getValue(), as.access_special_num.getValue(), "特殊授权");
	}

	/*
	 * 2017-5-22 zlw
	 * 任务调度使用
	 * 当授权结束日期小于当前日期时，自动轮循数据插入门禁流水表，并同步更新余额表数据 有问题!!!!!!!!!!!!!!!!!
	 */
	public static void doCancelAccesslist1(CDBConnection con) throws Exception {
		Hr_access_emauthority_list al = new Hr_access_emauthority_list();
		String strsql = "SELECT * FROM hr_access_special t WHERE t.end_date <= NOW() and stat=9 ";
		CJPALineData<Hr_access_special> aos = new CJPALineData<Hr_access_special>(Hr_access_special.class);
		aos.findDataBySQL(strsql, true, false);
		for (CJPABase jpa : aos) {
			Hr_access_special as = (Hr_access_special) jpa;
			al.clear();
			al.source.setValue("4");
			al.source_id.setValue(as.access_special_id.getValue());
			al.source_num.setValue(as.access_special_num.getValue());
			al.change_reason.setValue("特殊授权到期系统自动关闭门禁");
			al.employee_code.setValue(as.employee_code.getValue());
			al.employee_name.setValue(as.employee_name.getValue());
			al.sex.setValue(as.sex.getValue());
			al.access_card_number.setValue(as.access_card_number.getValue());
			al.access_card_seq.setValue(as.access_card_seq.getValue());
			al.hiredday.setValue(as.hiredday.getValue());
			al.orgid.setValue(as.orgid.getValue());
			al.orgname.setValue(as.orgname.getValue());
			al.orgcode.setValue(as.orgcode.getValue());
			al.extorgname.setValue(as.extorgname.getValue());
			al.hwc_namezl.setValue(as.hwc_namezl.getValue());
			al.lv_id.setValue(as.lv_id.getValue());
			al.lv_num.setValue(as.lv_num.getValue());
			al.access_list_id.setValue(as.access_list_id.getValue());
			al.access_list_code.setValue(as.access_list_code.getValue());
			al.access_list_model.setValue(as.access_list_model.getValue());
			al.access_list_name.setValue(as.access_list_name.getValue());
			al.deploy_area.setValue(as.deploy_area.getValue());
			al.access_place.setValue(as.access_place.getValue());
			al.accrediter.setValue(as.accrediter.getValue());
			al.accredit_date.setAsDatetime(new Date());// .setValue(as.begin_date.getValue());
			al.remarks.setValue(as.remarks.getValue());
			al.access_status.setValue("2");
			al.save();
		}
	}

	// Excel导入
	@ACOAction(eventname = "ImpSpecialExcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String ImpSpecialExcel() throws Exception {
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

		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
		// : new XSSFWorkbook(new FileInputStream(fullname));
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
		Hr_employee ee = new Hr_employee();
		// Hr_access_card ac = new Hr_access_card();
		Hr_ykt_card ac = new Hr_ykt_card();
		Hr_access_list al = new Hr_access_list();
		Hr_access_special as = new Hr_access_special();
		CDBConnection con = as.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String employee_code = v.get("employee_code");
				String access_card_number = v.get("access_card_number");
				String access_list_code = v.get("access_list_code");
				if ((employee_code == null) || (employee_code.isEmpty()) ||
						(access_card_number == null) || (access_card_number.isEmpty()) ||
						(access_list_code == null) || (access_list_code.isEmpty()))
					continue;
				rst++;
				ee.clear();
				ee.findBySQL("SELECT * FROM hr_employee t WHERE usable=1 and t.employee_code = '" + employee_code + "'");
				if (ee.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在");
				ac.clear();
				ac.findBySQL("SELECT * FROM Hr_ykt_card t WHERE t.card_number = '" + access_card_number + "'");
				if (ac.isEmpty())
					throw new Exception("卡号【" + access_card_number + "】不存在");
				al.clear();
				al.findBySQL("SELECT * FROM hr_access_list t WHERE t.stat = 9 AND t.access_list_code = '" + access_list_code + "'");
				if (al.isEmpty())
					throw new Exception("门禁【" + access_list_code + "】不存在");
				as.clear();
				as.access_special_num.setValue(v.get("access_special_num"));
				as.employee_code.setValue(employee_code);
				as.employee_name.setValue(ee.employee_name.getValue());
				as.sex.setValue(ee.sex.getValue());
				as.hiredday.setValue(ee.hiredday.getValue());
				as.orgid.setValue(ee.orgid.getValue());
				as.orgname.setValue(ee.orgname.getValue());
				as.orgcode.setValue(ee.orgcode.getValue());
				as.extorgname.setValue(ee.orgname.getValue());
				as.hwc_namezl.setValue(ee.hwc_namezl.getValue());
				as.lv_id.setValue(ee.lv_id.getValue());
				as.lv_num.setValue(ee.lv_num.getValue());
				as.access_card_seq.setValue(ac.card_sn.getValue());
				as.access_card_number.setValue(v.get("access_card_number"));
				as.access_list_id.setValue(al.access_list_id.getValue());
				as.access_list_code.setValue(v.get("access_list_code"));
				as.access_list_model.setValue(al.access_list_model.getValue());
				as.access_list_name.setValue(al.access_list_name.getValue());
				as.deploy_area.setValue(al.deploy_area.getValue());
				as.stat.setValue(1);
				as.change_reason.setValue(v.get("change_reason"));
				as.accrediter.setValue(v.get("accrediter"));
				as.begin_date.setValue(v.get("begin_date"));
				as.end_date.setValue(v.get("end_date"));
				as.remarks.setValue(v.get("remarks"));
				as.save(con);
				as.wfcreate(null, con);
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
		efields.add(new CExcelField("特殊申请单号", "access_special_num", false));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("卡号", "access_card_number", true));
		efields.add(new CExcelField("门禁编码", "access_list_code", true));
		efields.add(new CExcelField("申请原因", "change_reason", false));
		efields.add(new CExcelField("授权人", "accrediter", true));
		efields.add(new CExcelField("授权开始时间", "begin_date", true));
		efields.add(new CExcelField("授权结束时间", "end_date", true));
		efields.add(new CExcelField("备注", "remarks", false));
		return efields;
	}

	@ACOAction(eventname = "findCardNo", Authentication = true, ispublic = true, notes = "获取卡号")
	public String findCardNo() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String employeecode = CorUtil.hashMap2Str(parms, "employeecode", "工号参数不能为空");
		Hr_ykt_card yc = new Hr_ykt_card();
		String sqlstr = "SELECT * FROM hr_ykt_card t WHERE t.employee_code = '" + employeecode + "' AND card_stat = 1";
		yc.findBySQL(sqlstr);
		if (yc.isEmpty())
			throw new Exception("没有发现工号为【" + employeecode + "】的卡信息");
		return yc.tojson();
	}

}
