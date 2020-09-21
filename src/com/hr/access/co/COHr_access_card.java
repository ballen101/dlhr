package com.hr.access.co;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.access.entity.Hr_access_card;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.Access")
public class COHr_access_card extends JPAController{

	
	@Override
	public String OnCCoDel(CDBConnection con, Class<CJPA> jpaclass, String typeid) throws Exception {
		if(jpaclass.isAssignableFrom(Hr_access_card.class)){
			Hr_access_card ac = new Hr_access_card();
			if(!ac.stat.getValue().equals("1")){
				throw new Exception("门禁卡信息不允许删除");
			}
			return null;
		}
		return null;
	}
	//Excel导入
	@ACOAction(eventname = "ImpCardExcel", Authentication = true, ispublic = false, notes = "导入Excel")
	public String ImpCardExcel() throws Exception {
		String  userid=CSContext.getUserID();
		
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
		Hr_employee ee = new Hr_employee();
		Hr_access_card ac = new Hr_access_card();
		CDBConnection con = ac.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String employee_code = v.get("employee_code");
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				rst++;
				ee.clear();
				ee.findBySQL("SELECT * FROM hr_employee t WHERE usable=1 and t.employee_code = '"+employee_code+"'");
				if(ee.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在");
				ac.clear();
				ac.access_card_seq.setValue(v.get("access_card_seq"));
				ac.access_card_number.setValue(v.get("access_card_number"));
				ac.employee_code.setValue(v.get("employee_code"));
				ac.employee_name.setValue(ee.employee_name.getValue());
				ac.sex.setValue(ee.sex.getValue()); 
				ac.hiredday.setValue(ee.hiredday.getValue()); 
				ac.orgid.setValue(ee.orgid.getValue()); 
				ac.orgname.setValue(ee.orgname.getValue()); 
				ac.orgcode.setValue(ee.orgcode.getValue()); 
				ac.extorgname.setValue(ee.orgname.getValue()); 
				ac.hwc_namezl.setValue(ee.hwc_namezl.getValue()); 
				ac.lv_id.setValue(ee.lv_id.getValue()); 
				ac.lv_num.setValue(ee.lv_num.getValue()); 				
				ac.publish_date.setValue(v.get("publish_date"));
				ac.remarks.setValue(v.get("remarks"));
				ac.stat.setValue("9");
				ac.save(con);
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
		efields.add(new CExcelField("卡序列号", "access_card_seq", true));
		efields.add(new CExcelField("卡号", "access_card_number", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("发卡时间", "publish_date", true));
		efields.add(new CExcelField("备注", "remarks", false));
		return efields;
	}

}
