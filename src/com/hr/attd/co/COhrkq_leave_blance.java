package com.hr.attd.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.attd.ctr.CtrHrkq_leave_blance;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.attd.entity.Hrkq_leave_blance;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_leavejob;

@ACO(coname = "web.hrkq.leaveblc")
public class COhrkq_leave_blance {
	@ACOAction(eventname = "createYearLeaveInfo", Authentication = true, notes = "生成年假")
	public String createYearLeaveInfo() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String year = CorUtil.hashMap2Str(parms, "year", "参数year不能为空");
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "参数orgid不能为空");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		String sqlstr = "select distinct * from(SELECT e.* FROM hr_employee e,hr_orgposition op,hr_standposition sp "
				+ " WHERE e.ospid=op.ospid AND op.sp_id=sp.sp_id AND e.empstatid IN(3,4,5) and sp.yhtype=1 "
				+ " AND e.idpath LIKE '" + org.idpath.getValue() + "%') tb where 1=1 ";
		CJPALineData<Hr_employee> emps = new CJPALineData<Hr_employee>(Hr_employee.class);
		CDBConnection con = (new Hr_employee()).pool.getCon(this);
		con.startTrans();
		try {
			emps.findDataBySQL(con, sqlstr, false, false);
			int i = 0;
			for (CJPABase jpa : emps) {
				Hr_employee emp = (Hr_employee) jpa;
				CtrHrkq_leave_blance.getYearLeave_blance(con, emp, year, true);
				System.out.println("进度：" + (++i) + "/" + emps.size());
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
		JSONObject rst = new JSONObject();
		rst.put("result", "OK");
		return rst.toString();
	}

	@ACOAction(eventname = "geterleavelblc", Authentication = true, notes = "获取某人可休假列表")
	public String geterleavelblc() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String er_id = CorUtil.hashMap2Str(parms, "er_id", "参数er_id不能为空");
		boolean isv = Boolean.valueOf(CorUtil.hashMap2Str(parms, "isv"));// 是否可用 没超时 且 没用完
		String sqlstr = "SELECT * FROM hrkq_leave_blance WHERE 1=1 and er_id=" + er_id;
		if (isv)
			sqlstr = sqlstr + " and valdate>'" + Systemdate.getStrDateyyyy_mm_dd() + "' and alllbtime>usedlbtime";
		sqlstr = sqlstr + " order by valdate desc, lbid desc";
		Hrkq_leave_blance lb = new Hrkq_leave_blance();
		return lb.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "impexcel", Authentication = true, ispublic = false, notes = "可调休余额导入Excel")
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
//		Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new FileInputStream(fullname))
//				: new XSSFWorkbook(new FileInputStream(fullname));

		Workbook workbook = WorkbookFactory.create(file);

		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		Sheet aSheet = workbook.getSheetAt(0);// 获得一个sheet

		return parserExcelSheet(aSheet, batchno);
	}

	private int parserExcelSheet(Sheet aSheet, String batchno) throws Exception {
		String idpathwhere = CSContext.getIdpathwhere();
		String entid = CSContext.getCurEntID();
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 1);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 1);
		Hr_employee emp = new Hr_employee();
		CDBConnection con = emp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			int ct = values.size();
			int curidx = 0;
			for (Map<String, String> v : values) {
				Logsw.debug("可调休余额导入【" + (curidx++) + "/" + ct + "】");
				String employee_code = v.get("employee_code");
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				rst++;
				emp.clear();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'" + idpathwhere);
				if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在或权限范围内不存在");

				String sstype = dictemp.getVbCE("1019", v.get("stype"), false, "工号【" + employee_code + "】调休来源【" + v.get("stype") + "】不存在");
				int stype = Integer.valueOf(sstype);
				String sccode = (stype == 1) ? v.get("sccode") : "0";

				String sqlstr = "SELECT * FROM hrkq_leave_blance WHERE stype=" + stype + " and er_id=" + emp.er_id.getValue()
						+ " AND sid =0 and sccode='" + sccode + "'";
				Hrkq_leave_blance lb = new Hrkq_leave_blance();
				// lb.findBySQL(con, sqlstr, false);
				// if (!lb.isEmpty())
				// throw new Exception("工号【" + employee_code + "】【" + v.get("stype") + "】调休信息已经存在");

				float alllbtime = Float.valueOf(v.get("alllbtime"));
				float usedlbtime = Float.valueOf(v.get("usedlbtime"));
				Date valdate = Systemdate.getDateByStr(v.get("valdate"));
				lb.clear();
				lb.lbname.setValue(v.get("stype")); // 标题
				lb.stype.setAsInt(stype); // 源类型 1 年假 2 加班 3 值班 4出差
				lb.sid.setAsInt(0); // 源ID
				lb.sccode.setValue(sccode); // 源编码/年假的年份
				lb.er_id.setValue(emp.er_id.getValue()); // 人事ID
				lb.employee_code.setValue(emp.employee_code.getValue()); // 工号
				lb.employee_name.setValue(emp.employee_name.getValue());
				lb.hg_name.setValue(emp.hg_name.getValue()); // 职等名称
				lb.lv_num.setValue(emp.lv_num.getValue()); // 职级
				lb.orgid.setValue(emp.orgid.getValue()); // 部门ID
				lb.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				lb.orgname.setValue(emp.orgname.getValue()); // 部门名称
				lb.alllbtime.setAsFloat(alllbtime); // 可调休时间小时
				lb.usedlbtime.setAsFloat(usedlbtime); // 已调休时间小时
				lb.valdate.setAsDatetime(valdate); // 有效期
				lb.note.setValue(v.get("note"));
				lb.idpath.setValue(emp.idpath.getValue()); // idpath
				lb.save(con);
			}
			// throw new Exception("不给通过");
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
		efields.add(new CExcelField("来源类型", "stype", true));
		efields.add(new CExcelField("年度", "sccode", true));
		efields.add(new CExcelField("总调休时间(H)", "alllbtime", true));
		efields.add(new CExcelField("已经调休时间(H)", "usedlbtime", true));
		efields.add(new CExcelField("到期时间", "valdate", true));
		efields.add(new CExcelField("备注", "note", false));
		return efields;
	}

}
