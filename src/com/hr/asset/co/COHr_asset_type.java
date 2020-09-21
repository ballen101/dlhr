package com.hr.asset.co;

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
import com.hr.base.entity.Hr_grade;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_orgpositionkpi;
import com.hr.base.entity.Hr_standposition;
import com.hr.base.entity.Hr_wclass;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_family;
import com.hr.asset.entity.Hr_asset_type;

@ACO(coname = "web.hr.Asset")
public class COHr_asset_type extends JPAController {
	//查询资产类型表提供弹出框使用
	@ACOAction(eventname = "findAssettypelist", Authentication = true, ispublic = false, notes = "资产类型查询")
	public String findAssettypelist() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(eparms);
		if (jps.size() == 0)
			throw new Exception("需要查询参数");
		Hr_asset_type he = new Hr_asset_type();
		String sqlstr = "SELECT * FROM hr_asset_type t WHERE effective_flag = 'Y' ";
		return he.pool.opensql2json(sqlstr);
	}

	// 已经被使用的类型不允许删除
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String typeid) throws Exception {

		if(jpaclass.isAssignableFrom(Hr_asset_type.class)){
			String sqlstr = "SELECT ifnull(COUNT(*),0) ct FROM hr_asset_item t WHERE t.asset_type_id = "+ typeid;
			if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) != 0)
				throw new Exception("类型已经被使用不允许删除");
		}
		return null;
	}

	//Excel导入
	@ACOAction(eventname = "ImpTypeExcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String ImpTypeExcel() throws Exception {
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
//		: new XSSFWorkbook(new FileInputStream(fullname));
		
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
		Hr_asset_type at = new Hr_asset_type();
		CDBConnection con = at.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				rst++;
				at.clear();
				at.asset_type_code.setValue(v.get("asset_type_code"));
				at.asset_type_name.setValue(v.get("asset_type_name"));
				at.effective_flag.setValue(dictemp.getVbCE("5", v.get("effective_flag"), true, "是否词汇【" + v.get("isemcat") + "】不存在")); 
				at.effective_date.setValue(v.get("effective_date"));
				at.invalidation_date.setValue(v.get("invalidation_date"));
				at.remarks.setValue(v.get("remarks"));
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
		efields.add(new CExcelField("类型编码", "asset_type_code", false));
		efields.add(new CExcelField("类型名称", "asset_type_name", true));
		efields.add(new CExcelField("生效标识", "effective_flag", true));
		efields.add(new CExcelField("生效日期", "effective_date", false));
		efields.add(new CExcelField("失效日期", "invalidation_date", false));
		efields.add(new CExcelField("备注", "remarks", false));
		return efields;
	}
}