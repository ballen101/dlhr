package com.hr.card.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.Context;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_leanexp;
import com.hr.util.HRUtil;

@ACO(coname = "web.hr.Card.rpt")
public class CORpt {

	@ACOAction(eventname = "getcardpublish", Authentication = true, notes = "卡信息列表")
	public String getcardpublish() throws Exception {
		String sqlstr = "SELECT ycp.card_publish_no," +
				"       ycp.orgcode," +
				"       ycp.orgname," +
				"       ycp.employee_code," +
				"       ycp.employee_name," +
				"       ycp.sp_name," +
				"       ycp.lv_num," +
				"       ycp.finger_mark_no," +
				"       ycp.card_type," +
				"       ycp.card_number," +
				"       ycp.card_sn," +
				"       CASE ycp.card_type" +
				"         WHEN '1' THEN" +
				"          ycp.publish_date" +
				"         ELSE" +
				"          NULL" +
				"       END AS publish_date," +
				"       ycl.loss_date," +
				"       CASE ycp.card_type" +
				"         WHEN '2' THEN" +
				"          ycp.publish_date" +
				"         ELSE" +
				"          NULL" +
				"       END AS repair_date," +
				"       NULL repair_amount," +
				"       ycp.effective_date," +
				"       ycp.disable_date," +
				"       he.ljdate," +
				"       ycc.clean_date," +
				"       yc.card_stat," +
				"       ycp.stat," +
				"       ycp.creator," +
				"       ycp.createtime," +
				"       ycp.updator," +
				"       ycp.updatetime," +
				"       ycp.remark" +
				"  FROM hr_ykt_card_publish ycp" +
				" INNER JOIN hr_employee he" +
				"    ON ycp.employee_code = he.employee_code" +
				"  LEFT JOIN hr_ykt_card_clean ycc" +
				"    ON ycp.card_number = ycc.card_number" +
				"   AND ycp.employee_code = ycc.employee_code" +
				"   AND ycc.stat = '9'" +
				"  LEFT JOIN hr_ykt_card_loss ycl" +
				"    ON ycp.employee_code = ycl.employee_code" +
				"   AND ycp.card_number = ycl.card_number" +
				"   AND ycl.stat = '9'" +
				" INNER JOIN hr_ykt_card yc" +
				"    ON ycp.employee_code = yc.employee_code" +
				"   AND ycp.card_number = yc.card_number" +
				" WHERE ycp.stat = '9'";
		return new CReport(sqlstr, null).findReport();
	}

	@ACOAction(eventname = "getcardlist", Authentication = true, notes = "卡档案查询")
	public String getcardlist() throws Exception {
		String sqlstr = "select * from hr_ykt_card";
		return new CReport(sqlstr, null).findReport();
	}

	@ACOAction(eventname = "getcardinfroex", Authentication = true, notes = "卡档案信息分析报表Ex")
	public String getcardinfroex() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpdqdate = CjpaUtil.getParm(jps, "month");
		if (jporgcode == null)
			throw new Exception("机构不能为空");
		String orgcode = jporgcode.getParmvalue();

		if (jpdqdate == null)
			throw new Exception("月度不能为空");
		String ym = jpdqdate.getParmvalue();
		String sqlstr = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		dws.add(0, org.toJsonObj());
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			dw.put("get_month", ym);
			boolean includechld = true;// (i != 0);
			getMonthEmopCt(dw, ym, includechld);// 总人数
			getCard_publishInfo(dw, ym, includechld);// 发卡信息
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private void getCard_publishInfo(JSONObject dw, String ym, boolean includechld) throws Exception {
		String sqlstr = "SELECT IFNULL(SUM(IF(card_type=1,1,0)),0) publish_count,"
				+ "   IFNULL(SUM(IF(card_type=2,1,0)),0) repair_count,"
				+ "   IFNULL(SUM(IF(card_type=2,amount,0)),0) repair_amount"
				+ " FROM hr_ykt_card_publish "
				+ " WHERE DATE_FORMAT(publish_date,'%Y-%m') ='" + ym + "'";
		if (includechld)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		HashMap<String, String> row = DBPools.defaultPool().openSql2List(sqlstr).get(0);
		dw.put("publish_count", row.get("publish_count"));
		dw.put("repair_count", row.get("repair_count"));
		dw.put("repair_amount", row.get("repair_amount"));

		sqlstr = "SELECT IFNULL(COUNT(*),0) lose_count FROM hr_ykt_card_loss WHERE DATE_FORMAT(loss_date,'%Y-%m') ='" + ym + "'";
		if (includechld)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		dw.put("lose_count", DBPools.defaultPool().openSql2List(sqlstr).get(0).get("lose_count").toString());

		sqlstr = "SELECT IFNULL(COUNT(*),0) clean_count FROM hr_ykt_card_clean WHERE DATE_FORMAT(clean_date,'%Y-%m') ='" + ym + "'";
		if (includechld)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		dw.put("clean_count", DBPools.defaultPool().openSql2List(sqlstr).get(0).get("clean_count").toString());
	}

	private void getMonthEmopCt(JSONObject dw, String ym, boolean includechld) throws NumberFormatException, Exception {
		String sqlstr = "SELECT IFNULL(COUNT(*),0)ct FROM hr_month_employee WHERE hr_month_employee.yearmonth='" + ym + "'";
		if (includechld)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		dw.put("em_count", Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct").toString()));
	}

	@ACOAction(eventname = "getcardinfro", Authentication = true, notes = "卡档案信息分析报表【停用】")
	public String getcardinfro() throws Exception {
		// HashMap<String,String> parms= CSContext.get_pjdataparms();
		// parms.get("a").toString();
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpdqdate = CjpaUtil.getParm(jps, "month");
		String orgcode = (jporgcode == null) ? null : jporgcode.getParmvalue();
		if (jpdqdate == null)
			throw new Exception("月度不能为空");
		String dqdate = jpdqdate.getParmvalue();
		String sqlstr = "SELECT he.orgcode,he.idpath," +
				"       he.orgname," +
				"       COUNT(he.orgname) em_count," +
				"       MONTH(NOW()) get_month," +
				"       (SELECT COUNT(1)" +
				"          FROM hr_ykt_card_publish ycp" +
				"         WHERE MONTH(ycp.publish_date) = MONTH(IFNULL('" + dqdate + "', NOW()))" +
				"           AND ycp.orgcode = he.orgcode" +
				"           AND ycp.card_type = 1" +
				"           AND ycp.stat = '9') publish_count," +
				"       (SELECT COUNT(1)" +
				"          FROM hr_ykt_card_loss ycl" +
				"         WHERE MONTH(ycl.loss_date) = MONTH(IFNULL('" + dqdate + "', NOW()))" +
				"           AND ycl.orgcode = he.orgcode" +
				"           AND ycl.stat = '9') lose_count," +
				"       (SELECT COUNT(1)" +
				"          FROM hr_ykt_card_clean ycc" +
				"         WHERE MONTH(ycc.clean_date) = MONTH(IFNULL('" + dqdate + "', NOW()))" +
				"           AND ycc.orgcode = he.orgcode" +
				"           AND ycc.stat = '9') clean_count," +
				"       (SELECT COUNT(1)" +
				"          FROM hr_ykt_card_publish ycp1" +
				"         WHERE MONTH(ycp1.publish_date) = MONTH(IFNULL('" + dqdate + "', NOW()))" +
				"           AND ycp1.orgcode = he.orgcode" +
				"           AND ycp1.card_type = 2" +
				"           AND ycp1.stat = '9') repair_count," +
				"       NULL repair_amount" +
				"  FROM hr_employee he";
		sqlstr = (orgcode == null) ? sqlstr + " GROUP BY he.orgcode" : sqlstr + " WHERE he.orgcode = '" + orgcode + "' GROUP BY he.orgcode";
		return new CReport(sqlstr, null).findReport();
	}

	@ACOAction(eventname = "impcardexcel", Authentication = true, ispublic = false, notes = "导入卡档案Excel")
	public String impcardexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserCardExcelFile(p, batchno);
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

	private int parserCardExcelFile(Shw_physic_file pf, String batchno) throws Exception {
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
		return parserCardExcelSheet(aSheet, batchno);
	}

	private int parserCardExcelSheet(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initCardExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_employee emp = new Hr_employee();
		Hr_ykt_card cd = new Hr_ykt_card();
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
				/*if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在人事资料");*/

				cd.clear();
				cd.card_sn.setValue(v.get("card_sn")); // 卡序列号
				cd.card_number.setValue(v.get("card_number")); // 卡号
				cd.finger_mark_no.setValue(v.get("finger_mark_no")); // 指纹登记号
				cd.card_stat.setValue(dictemp.getVbCE("1263", v.get("card_stat"), true, "工号【" + employee_code + "】卡状态【" + v.get("card_stat") + "】不存在")); // 卡状态
				cd.er_id.setValue(emp.er_id.getValue()); // 人事ID
				cd.er_code.setValue(emp.er_code.getValue()); // 档案编码
				cd.employee_code.setValue(emp.employee_code.getValue()); // 工号
				cd.employee_name.setValue(emp.employee_name.getValue());
				cd.orgid.setValue(emp.orgid.getValue()); // 部门ID
				cd.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				cd.orgname.setValue(emp.orgname.getValue()); // 部门名称
				cd.sp_name.setValue(emp.sp_name.getValue()); // 职位
				cd.hwc_namezl.setValue(emp.hwc_namezl.getValue()); // 职类
				cd.hwc_namezq.setValue(emp.hwc_namezq.getValue()); // 职群
				cd.hwc_namezz.setValue(emp.hwc_namezz.getValue()); // 职种
				cd.hg_name.setValue(emp.hg_name.getValue()); // 职等
				cd.lv_num.setValue(emp.lv_num.getValue()); // 职级
				cd.remark.setValue(v.get("remark")); // 备注
				cd.idpath.setValue(emp.idpath.getValue()); // idpath
				cd.createtime.setAsDatetime(new Date()); // 创建时间
				cd.save(con);
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

	private List<CExcelField> initCardExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("卡序列号", "card_sn", false));
		efields.add(new CExcelField("卡号", "card_number", true));
		efields.add(new CExcelField("指纹登记号", "finger_mark_no", false));
		efields.add(new CExcelField("卡状态", "card_stat", false));
		efields.add(new CExcelField("工号", "employee_code", false));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}

}