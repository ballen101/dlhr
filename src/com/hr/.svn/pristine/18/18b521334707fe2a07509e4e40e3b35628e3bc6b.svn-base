package com.hr.asset.co;

import java.util.Date;
import java.text.SimpleDateFormat;
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
import com.hr.base.entity.Hr_grade;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_orgpositionkpi;
import com.hr.base.entity.Hr_standposition;
import com.hr.base.entity.Hr_wclass;
import com.hr.asset.entity.Hr_asset_item;
import com.hr.asset.entity.Hr_asset_register;
import com.hr.asset.entity.Hr_asset_statement;
import com.hr.asset.entity.Hr_asset_sum;
import com.hr.asset.entity.Hr_asset_type;
import com.hr.canteen.entity.Hr_canteen_room;

@ACO(coname = "web.hr.Asset")
public class COHr_asset_register extends JPAController {
	// 流程完成时数据插入流水表
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doUpdateAssetList(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doUpdateAssetList(jpa, con);
		}
	}

	private void doUpdateAssetList(CJPA jpa, CDBConnection con) throws Exception {
		Hr_asset_register ar = (Hr_asset_register) jpa;
		Hr_asset_statement as = new Hr_asset_statement();
		Date df = new Date();
		SimpleDateFormat nowdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		as.clear();
		as.source.setValue("asset_register");
		as.source_id.setValue(ar.asset_register_id.getValue());
		as.source_num.setValue(ar.asset_register_num.getValue());
		as.asset_item_id.setValue(ar.asset_item_id.getValue());
		as.asset_item_code.setValue(ar.asset_item_code.getValue());
		as.asset_item_name.setValue(ar.asset_item_name.getValue());
		as.asset_type_id.setValue(ar.asset_type_id.getValue());
		as.asset_type_code.setValue(ar.asset_type_code.getValue());
		as.asset_type_name.setValue(ar.asset_type_name.getValue());
		as.brand.setValue(ar.brand.getValue());
		as.model.setValue(ar.model.getValue());
		as.original_value.setValue(ar.original_value.getValue());
		as.net_value.setValue(ar.net_value.getValue());
		as.uom.setValue(ar.uom.getValue());
		as.service_life.setValue(ar.service_life.getValue());
		as.acquired_date.setValue(ar.acquired_date.getValue());
		as.deploy_area.setValue(ar.deploy_area.getValue());
		as.deploy_restaurant_id.setValue(ar.deploy_restaurant_id.getValue());
		as.deploy_restaurant.setValue(ar.deploy_restaurant.getValue());
		as.keep_own.setValue(ar.keep_own.getValue());
		as.adjust_qty.setValue(ar.deploy_qty.getValue());
		as.adjust_date.setAsDatetime(new Date());
		as.save(con);
	}

	// Excel导入
	@ACOAction(eventname = "ImpRegExcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String ImpRegExcel() throws Exception {
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
		Hr_asset_register ar = new Hr_asset_register();
		Hr_asset_item at = new Hr_asset_item();
		Hr_canteen_room cr = new Hr_canteen_room();
		CDBConnection con = ar.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String asset_item_code = v.get("asset_item_code");
				String deploy_restaurant = v.get("deploy_restaurant");
				if ((asset_item_code == null) || (asset_item_code.isEmpty()))
					continue;
				rst++;
				at.clear();
				at.findBySQL("SELECT * FROM hr_asset_item t WHERE t.asset_item_code ='" + asset_item_code + "'");
				if (at.isEmpty())
					throw new Exception("类型【" + asset_item_code + "】不存在");
				cr.clear();
				cr.findBySQL("SELECT * FROM hr_canteen_room t WHERE t.ctr_name= '" + deploy_restaurant + "'");
				if (cr.isEmpty())
					throw new Exception("餐厅【" + deploy_restaurant + "】不存在");
				ar.clear();
				ar.asset_type_id.setValue(at.asset_type_id.getValue());
				ar.asset_type_code.setValue(at.asset_type_code.getValue());
				ar.asset_type_name.setValue(at.asset_type_name.getValue());
				ar.asset_item_id.setValue(at.asset_item_id.getValue());
				ar.asset_item_code.setValue(v.get("asset_item_code"));
				ar.asset_item_name.setValue(at.asset_item_name.getValue());
				ar.brand.setValue(v.get("brand"));
				ar.model.setValue(v.get("model"));
				ar.original_value.setValue(v.get("original_value"));
				ar.net_value.setValue(v.get("net_value"));
				ar.service_life.setValue(v.get("service_life"));
				ar.acquired_date.setValue(v.get("acquired_date"));
				ar.deploy_qty.setValue(v.get("deploy_qty"));
				ar.deploy_area.setValue(v.get("deploy_area"));
				ar.deploy_restaurant_id.setValue(cr.ctr_id.getValue());
				ar.deploy_restaurant.setValue(v.get("deploy_restaurant"));
				ar.keep_own.setValue(v.get("keep_own"));
				ar.uom.setValue(dictemp.getVbCE("9", v.get("uom"), true, "是否词汇【" + v.get("isemcat") + "】不存在"));
				ar.remark.setValue(v.get("remark"));
				ar.save(con);
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
		efields.add(new CExcelField("类型名称", "asset_type_name", false));
		efields.add(new CExcelField("资产物料编码", "asset_item_code", true));
		efields.add(new CExcelField("资产物料名称", "asset_item_name", false));
		efields.add(new CExcelField("品牌", "brand", false));
		efields.add(new CExcelField("型号", "model", false));
		efields.add(new CExcelField("原值", "original_value", false));
		efields.add(new CExcelField("净值", "net_value", false));
		efields.add(new CExcelField("报废年限", "service_life", false));
		efields.add(new CExcelField("购置日期", "acquired_date", false));
		efields.add(new CExcelField("配置数量", "deploy_qty", true));
		efields.add(new CExcelField("配置区域", "deploy_area", true));
		efields.add(new CExcelField("配置餐厅", "deploy_restaurant", true));
		efields.add(new CExcelField("保管人", "keep_own", false));
		efields.add(new CExcelField("单位", "uom", false));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}
}
