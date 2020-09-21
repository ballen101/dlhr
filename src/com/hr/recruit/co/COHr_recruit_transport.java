package com.hr.recruit.co;

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
import com.hr.recruit.entity.Hr_recruit_transport;

@ACO(coname = "web.hr.Recruit.Transport")
public class COHr_recruit_transport extends JPAController {
	
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String typeid) throws Exception {
		throw new Exception("输送机构不允许删除");		
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
			Hr_recruit_transport at = new Hr_recruit_transport();
			CDBConnection con = at.pool.getCon(this);
			DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
			Shworg org = new Shworg();
			con.startTrans();
			int rst = 0;
			try {
				for (Map<String, String> v : values) {
					rst++;
					at.clear();
					at.recruit_transport_name.setValue(v.get("recruit_transport_name"));
					at.contacts_name.setValue(v.get("contacts_name"));
					at.cooperate_begin_date.setValue(v.get("cooperate_begin_date"));
					at.cooperate_end_date.setValue(v.get("cooperate_end_date"));
					at.contacts_phone.setValue(v.get("contacts_phone"));
					at.recruit_transport_stat.setValue("1");
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
			efields.add(new CExcelField("输送机构名称", "recruit_transport_name", true));
			efields.add(new CExcelField("联系人", "contacts_name", false));
			efields.add(new CExcelField("合作开始日期", "cooperate_begin_date", false));
			efields.add(new CExcelField("合作结束日期", "cooperate_end_date", false));
			efields.add(new CExcelField("联系电话", "contacts_phone", false));
			efields.add(new CExcelField("状态", "recruit_transport_stat", false));
			efields.add(new CExcelField("备注", "remarks", false));
			return efields;
		}
	
}
