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
import com.hr.access.entity.Hr_access_list;
import com.hr.asset.entity.Hr_asset_type;

@ACO(coname = "web.hr.Access")
public class COHr_access_list extends JPAController {

	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String typeid) throws Exception {

		if (jpaclass.isAssignableFrom(Hr_access_list.class)) {
			String sqlstr = "SELECT IFNULL(COUNT(*),0) oc FROM hr_access_emauthority_list t WHERE t.access_list_id = " + typeid;
			if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("oc")) != 0)
				throw new Exception("门禁已经被授权，不允许删除");
		}
		return null;
	}

	// Excel导入
	@ACOAction(eventname = "ImpListExcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String ImpListExcel() throws Exception {
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

		Workbook workbook = WorkbookFactory.create(file);
		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
		// : new XSSFWorkbook(new FileInputStream(fullname));

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
		Hr_access_list at = new Hr_access_list();
		CDBConnection con = at.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		Shworg org = new Shworg();
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String sqlstr = "SELECT * FROM shworg WHERE `code`='" + v.get("orgcode") + "'";
				org.findBySQL(sqlstr);
				if (org.isEmpty())
					throw new Exception("编码为【" + v.get("orgcode") + "】的机构不存在");
				rst++;
				at.clear();
				at.access_list_code.setValue(v.get("access_list_code"));
				at.access_list_model.setValue(v.get("access_list_model"));
				at.access_list_name.setValue(v.get("access_list_name"));
				at.orgid.setValue(org.orgid.getValue());
				at.orgcode.setValue(org.code.getValue());
				at.extorgname.setValue(org.extorgname.getValue());
				at.idpath.setValue(org.idpath.getValue());
				at.deploy_area.setValue(v.get("deploy_area"));
				at.remarks.setValue(v.get("remarks"));
				at.stat.setValue("9");
				at.save(con);
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
		efields.add(new CExcelField("门禁编码", "access_list_code", false));
		efields.add(new CExcelField("门禁型号", "access_list_model", false));
		efields.add(new CExcelField("门禁名称", "access_list_name", true));
		efields.add(new CExcelField("机构编码", "orgcode", false));
		efields.add(new CExcelField("机构全称", "extorgname", false));
		efields.add(new CExcelField("配置区域", "deploy_area", false));
		efields.add(new CExcelField("备注", "remarks", false));
		return efields;
	}

}
