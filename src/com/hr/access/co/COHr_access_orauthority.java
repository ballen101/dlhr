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
import com.corsair.server.util.CReport;
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.access.ctr.UtilAccess;
import com.hr.access.entity.Hr_access_card;
import com.hr.access.entity.Hr_access_emauthority_list;
import com.hr.access.entity.Hr_access_list;
import com.hr.access.entity.Hr_access_orauthority;
import com.hr.access.entity.Hr_access_special;
import com.hr.asset.entity.Hr_asset_item;
import com.hr.asset.entity.Hr_asset_type;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.HRUtil;

@ACO(coname = "web.hr.Access")
public class COHr_access_orauthority extends JPAController {

	// 流程完成时数据插入流水表
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf,
			boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doxx(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf,
			Shwwfproc proc, Shwwfproc nxtproc, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doxx(jpa, con);
		}
	}

	private void doxx(CJPA jpa, CDBConnection con) throws Exception {
		Hr_access_orauthority ao = (Hr_access_orauthority) jpa;
		Shworg org = new Shworg();
		org.findByID(ao.orgid.getValue());
		String sqlstr = "SELECT * FROM hr_employee t WHERE t.empstatid<10 and t.idpath like '" + org.idpath.getValue() + "%'";
		CJPALineData<Hr_employee> es = new CJPALineData<Hr_employee>(
				Hr_employee.class);
		es.findDataBySQL(sqlstr);
		Hr_access_list acl = new Hr_access_list();
		acl.findByID(ao.access_list_id.getValue());
		if (acl.isEmpty())
			throw new Exception("id为【" + ao.access_list_id.getValue() + "】的门禁不存在");
		for (CJPABase j : es) {
			Hr_employee e = (Hr_employee) j;
			UtilAccess.appendOneAccess(con, acl, e, 1, 5, ao.access_orauthority_id.getValue(), ao.access_orauthority_id.getValue(), "机构门禁授权");
			// doInsertAccessList(con, e.employee_code.getValue(),
			// "5",
			// ao.access_orauthority_id.getValue(), "机构授权",
			// e.orgid.getAsIntDefault(0), jpa);
		}

	}

	@Override
	/*
	 * 作废功能 作废时将表单中的门禁状态改为关闭，同时同步至门禁流水表
	 */
	public String OnCCoVoid(CDBConnection con, CJPA jpa) throws Exception {
		if (!HRUtil.hasRoles("35")) {
			throw new Exception("非门禁管理员，不允许作废");
		}
		Hr_access_orauthority as = (Hr_access_orauthority) jpa;
		UtilAccess.disableAllAccessByAccAndOrg(con, as.access_list_id.getValue(), as.orgid.getValue(), true, "机构门禁作废");

		// String sqlstr = "UPDATE hr_access_emauthority_sum t"
		// + " SET t.access_status = '2',"
		// + " t.accrediter = 'SYSTEM',"
		// + " t.remarks = '',"
		// + " t.accredit_date = NOW() "
		// + " WHERE t.access_list_code = '"
		// + as.access_list_code.getValue() + "'" + " AND t.orgcode = '"
		// + as.orgcode.getValue() + "'";
		// con.execsql(sqlstr);
		return null;
	}

	// Excel导入
	@ACOAction(eventname = "ImpOrauthorityExcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String ImpOrauthorityExcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase()
				.replaceAll("-", "");// 批次号
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

	private int parserExcelFile(Shw_physic_file pf, String batchno)
			throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs
				+ pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs
					+ pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(
		// new FileInputStream(fullname)) : new XSSFWorkbook(
		// new FileInputStream(fullname));

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
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title
																	// 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet,
				efds, 0);
		Shworg org = new Shworg();
		Hr_access_list al = new Hr_access_list();
		Hr_access_orauthority ao = new Hr_access_orauthority();
		CDBConnection con = ao.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String orgcode = v.get("orgcode");
				String access_list_code = v.get("access_list_code");
				String access_place = v.get("access_place");
				if ((orgcode == null) || (orgcode.isEmpty())
						|| (access_list_code == null)
						|| (access_list_code.isEmpty())
						|| (access_place == null) || (access_place.isEmpty()))
					continue;
				rst++;
				org.clear();
				org.findBySQL("select * from shworg where code='" + orgcode
						+ "'");
				if (org.isEmpty())
					throw new Exception("机构【" + orgcode + "】不存在");
				al.clear();
				al.findBySQL("SELECT * FROM hr_access_list t WHERE t.stat = 9 AND t.access_list_code = '"
						+ access_list_code + "'");
				if (al.isEmpty())
					throw new Exception("门禁【" + access_list_code + "】不存在");
				ao.clear();
				ao.orgid.setValue(org.orgid.getValue());
				ao.orgname.setValue(org.orgname.getValue());
				ao.orgcode.setValue(orgcode);
				ao.extorgname.setValue(org.extorgname.getValue());
				ao.access_list_id.setValue(al.access_list_id.getValue());
				ao.access_list_code.setValue(v.get("access_list_code"));
				ao.access_list_model.setValue(al.access_list_model.getValue());
				ao.access_list_name.setValue(al.access_list_name.getValue());
				ao.deploy_area.setValue(al.deploy_area.getValue());
				ao.access_place.setValue(access_place);
				ao.stat.setValue("9");
				ao.remarks.setValue(v.get("remarks"));
				ao.save(con);
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
		efields.add(new CExcelField("机构名称", "orgname", false));
		efields.add(new CExcelField("机构编码", "orgcode", true));
		efields.add(new CExcelField("机构全称", "extorgname", false));
		efields.add(new CExcelField("门禁编码", "access_list_code", true));
		efields.add(new CExcelField("门禁型号", "access_list_model", false));
		efields.add(new CExcelField("门禁名称", "access_list_name", false));
		efields.add(new CExcelField("门禁位置", "access_place", true));
		efields.add(new CExcelField("备注", "remarks", false));
		return efields;
	}

	// 查询可用门禁
	@ACOAction(eventname = "findUA", Authentication = true, ispublic = false, notes = "可用门禁查询")
	public String findUA() throws Exception {
		Hr_access_list al = new Hr_access_list();
		String sqlstr = "SELECT * FROM hr_access_list t WHERE t.access_stat = 1";
		return new CReport(sqlstr, null).findReport();
	}

}
