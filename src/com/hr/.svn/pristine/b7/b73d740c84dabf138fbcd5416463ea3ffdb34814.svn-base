package com.hr.recruit.co;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.perm.entity.Hr_employee;
import com.hr.recruit.entity.Hr_recruit_score;
import com.hr.recruit.entity.Hr_recruit_transport;

import net.sf.json.JSONObject;

@ACO(coname = "web.hr.rcu.score")
public class COHr_recruit_score {
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
		String idpathwhere = CSContext.getIdpathwhere();
		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_recruit_score sc = new Hr_recruit_score();
		Hr_employee emp = new Hr_employee();
		CDBConnection con = sc.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		Shworg org = new Shworg();
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String employee_code = v.get("employee_code");
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				rst++;
				emp.clear();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'" + idpathwhere);
				if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在或权限范围内不存在");

				sc.clear();
				sc.er_id.setValue(emp.er_id.getValue()); // 人事ID
				sc.employee_code.setValue(emp.employee_code.getValue()); // 工号
				sc.employee_name.setValue(emp.employee_name.getValue());// 姓名
				sc.id_number.setValue(emp.id_number.getValue()); // 身份证号
				sc.orgid.setValue(emp.orgid.getValue()); // 部门ID
				sc.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				sc.orgname.setValue(emp.orgname.getValue()); // 部门名称
				sc.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
				sc.lv_num.setValue(emp.lv_num.getValue()); // 职级
				sc.ospid.setValue(emp.ospid.getValue()); // 职位ID
				sc.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				sc.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				sc.twktitle.setValue(v.get("twktitle")); // 培训课题
				sc.twktime.setValue(v.get("twktime")); // 培训日期
				sc.twkscore.setValue(v.get("twkscore")); // 培训成绩
				sc.writime.setValue(v.get("writime")); // 笔试日期
				sc.wriscore.setValue(v.get("wriscore")); // 笔试成绩
				sc.idpath.setValue(emp.idpath.getValue()); // idpath
				sc.save(con);
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
		efields.add(new CExcelField("培训课题", "twktitle", false));
		efields.add(new CExcelField("培训日期", "twktime", false));
		efields.add(new CExcelField("培训成绩", "twkscore", false));
		efields.add(new CExcelField("笔试日期", "writime", false));
		efields.add(new CExcelField("笔试成绩", "wriscore", false));
		return efields;
	}
}
