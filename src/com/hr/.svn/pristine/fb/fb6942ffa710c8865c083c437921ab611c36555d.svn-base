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
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.asset.entity.Hr_asset_item;
import com.hr.asset.entity.Hr_asset_type;

@ACO(coname = "web.hr.Asset")
public class COHr_asset_item extends JPAController{

		//Excel导入
		@ACOAction(eventname = "ImpItemExcel", Authentication = true, ispublic = false, notes = "导入Excel")
		public String ImpItemExcel() throws Exception {
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

//			Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//					: new XSSFWorkbook(new FileInputStream(fullname));
			
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
			Hr_asset_item at = new Hr_asset_item();
			Hr_asset_type atp = new Hr_asset_type();
			CDBConnection con = at.pool.getCon(this);
			DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
			con.startTrans();
			int rst = 0;
			try {
				for (Map<String, String> v : values) {
					String asset_type_code = v.get("asset_type_code");
					if ((asset_type_code == null) || (asset_type_code.isEmpty()))
						continue;
					rst++;
					atp.clear();
					atp.findBySQL("SELECT * FROM hr_asset_type t WHERE t.effective_flag = 1 AND t.asset_type_code='"+asset_type_code+"'");
					if(atp.isEmpty())
						throw new Exception("类型【" + asset_type_code + "】不存在");
					at.clear();
					at.asset_type_id.setValue(atp.asset_type_id.getValue());
					at.asset_type_code.setValue(v.get("asset_type_code"));
					at.asset_type_name.setValue(atp.asset_type_name.getValue());
					at.asset_item_code.setValue(v.get("asset_item_code"));
					at.asset_item_name.setValue(v.get("asset_item_name"));
					at.uom.setValue(dictemp.getVbCE("9", v.get("uom"), true, "是否词汇【" + v.get("isemcat") + "】不存在"));
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
			efields.add(new CExcelField("类型编码", "asset_type_code", true));
			efields.add(new CExcelField("类型名称", "asset_type_name", false));
			efields.add(new CExcelField("资产物料编码", "asset_item_code", false));
			efields.add(new CExcelField("资产物料名称", "asset_item_name", true));
			efields.add(new CExcelField("单位", "uom", false));
			efields.add(new CExcelField("备注", "remarks", false));
			return efields;
		}

}
