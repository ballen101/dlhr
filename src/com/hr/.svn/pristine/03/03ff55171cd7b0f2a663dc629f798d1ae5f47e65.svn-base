package com.hr.base.co;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Types;
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

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.base.entity.Hr_grade;
import com.hr.base.entity.Hr_orgposition;
import com.hr.base.entity.Hr_orgpositionkpi;
import com.hr.base.entity.Hr_standposition;
import com.hr.base.entity.Hr_wclass;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_leavejob;

@ACO(coname = "web.hr.orgposition")
public class COHr_orgposition {

	@ACOAction(eventname = "findStationOption", Authentication = true, notes = "查询标准职位")
	public String findStationOption() throws Exception {
		String sqlstr = "SELECT * FROM hr_standposition WHERE 1=1 ";
		CJPALineData<Shworg> orgs = CSContext.getUserOrgs();
		String sw = "(";
		for (CJPABase jpa : orgs) {
			Shworg org = (Shworg) jpa;
			sw = sw + "(aidpath =  LEFT('" + org.idpath.getValue() + "',LENGTH(aidpath))) or ";
		}
		if (!sw.isEmpty())
			sw = sw.substring(0, sw.length() - 3);
		sw = sw + ")";
		sqlstr = sqlstr + " and " + sw;
		String[] ignParms = {};
		return new CReport(sqlstr, null).findReport(ignParms);
	}

	@ACOAction(eventname = "findop", Authentication = true, notes = "查询机构职位")
	public String findop() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String sqlstr = "select * from hr_orgposition where orgid=" + orgid;
		Hr_orgposition hsp = new Hr_orgposition();
		return hsp.pool.opensql2json(sqlstr);
		// return hsp.pool.opensql2jsontree(sqlstr, "ospid", "pid", false);
	}

	@ACOAction(eventname = "delop", Authentication = true, notes = "删除机构职位")
	public String delop() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String ospid = CorUtil.hashMap2Str(parms, "ospid", "需要参数ospid");
		// 判断是否有下属职位
		// 判断是否有职员试用该职位
		Hr_orgposition hsp = new Hr_orgposition();
		cheOSPCanDel(hsp, ospid);
		hsp.delete(ospid, true);
		JSONObject rst = new JSONObject();
		rst.put("result", "OK");
		return rst.toString();
	}

	private void cheOSPCanDel(Hr_orgposition hsp, String ospid) throws Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_orgposition WHERE pid=" + ospid;
		int ct = Integer.valueOf(hsp.pool.openSql2List(sqlstr).get(0).get("ct").toString());
		if (ct != 0) {
			throw new Exception("有下级职位，不允许删除");
		}

		sqlstr = "SELECT COUNT(*) ct FROM hr_employee WHERE ospid=" + ospid;
		ct = Integer.valueOf(hsp.pool.openSql2List(sqlstr).get(0).get("ct").toString());
		if (ct != 0) {
			throw new Exception("该职位包含员工，不允许删除");
		}
	}

	@ACOAction(eventname = "findSuperOrgPosiotions", Authentication = true, notes = "查询前置机构岗位")
	public String findSuperOrgPosiotions() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String plev = CorUtil.hashMap2Str(parms, "lev");
		plev = (plev == null) ? "2" : plev;

		int pl = Integer.valueOf(plev);
		if (pl <= 0) {
			throw new Exception("参数【lev】必须大于0");
		}

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("没有找到ID为【" + orgid + "】的机构");
		String orgidpath = org.idpath.getValue();
		String wids = getpidstr(orgidpath, pl);
		if (wids.isEmpty())
			throw new Exception("生成查询条件错误");
		String sqlstr = "SELECT ho.`orgid`,ho.`orgcode`,ho.`orgname`,ho.`ospid`,ho.`ospcode`,ho.`sp_name` FROM `hr_orgposition` ho "
				+ "WHERE ho.`usable`=1 AND orgid IN (" + wids + ")";
		Hr_orgposition ho = new Hr_orgposition();
		String eparms = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(eparms);
		String where = CjpaUtil.buildFindSqlByJsonParms(ho, jps);
		sqlstr = sqlstr + where;

		return ho.pool.opensql2json(sqlstr);
	}

	private String getpidstr(String ids, int len) {
		int pl = len;
		String[] ps = ids.split(",");
		int l = ps.length;
		String rst = "";
		for (int i = 0; i < pl; i++) {
			int idx = l - i - 1;
			if (idx < 0)
				break;
			String ts = ps[idx];
			rst = rst + ts + ",";
		}
		if (!rst.isEmpty())
			rst = rst.substring(0, rst.length() - 1);
		return rst;
	}

	@ACOAction(eventname = "findorgOptionKPIs", Authentication = true, notes = "查询机构岗位KPI")
	public String findorgOptionKPIs() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String ospid = CorUtil.hashMap2Str(parms, "ospid", "需要参数ospid");
		Hr_orgpositionkpi kpi = new Hr_orgpositionkpi();
		String sqlstr = "select * from hr_orgpositionkpi where ospid=" + ospid;
		return kpi.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "saveOrgOption", Authentication = true, notes = "保存机构岗位")
	public String saveOrgOption() throws Exception {
		JSONObject rootNode = JSONObject.fromObject(CSContext.getPostdata());
		boolean isnew = rootNode.getBoolean("isnew");
		String jsondata = rootNode.getString("jsondata");// rootNode.path("jsondata").toString();
		Hr_orgposition ho = new Hr_orgposition();
		ho.fromjson(jsondata);

		Shworg org = new Shworg();
		org.findByID(ho.orgid.getValue());
		if (org.isEmpty())
			throw new Exception("ID为【" + ho.orgid.getValue() + "】的机构不存在");
		ho.idpath.setValue(org.idpath.getValue());

		if (ho.pid.isEmpty())
			ho.pid.setAsInt(0);
		ho.quota.setIgnoreSave(true);// 此处不允许更新编制
		if (isnew) {
			ho.setJpaStat(CJPAStat.RSINSERT);
		} else {
			ho.setJpaStat(CJPAStat.RSUPDATED);
		}
		ho.save();
		return ho.tojson();
	}

	@ACOAction(eventname = "findGradeList", Authentication = true, notes = "保存机构岗位")
	public String findGradeList() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(eparms);
		Hr_grade gd = new Hr_grade();
		String where = CjpaUtil.buildFindSqlByJsonParms(gd, jps);
		String hwc_id = CorUtil.hashMap2Str(parms, "hwc_id", "需要参数hwc_id");
		String sqlstr = "SELECT * FROM hr_wclass WHERE hwc_id=" + hwc_id + " and type_id=1";
		Hr_wclass wc = new Hr_wclass();
		wc.findBySQL(sqlstr);
		if (wc.isEmpty())
			throw new Exception("没有找到ID为【" + hwc_id + "】的职类");
		String si = "(" + wc.hwc_id.getValue() + "," + wc.pid.getValue() + ")";
		sqlstr = "SELECT * FROM hr_grade WHERE hwc_id IN " + si + " AND usable=1";
		return gd.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "setDefaultOptions", Authentication = true, notes = "设置机构默认职位")
	public String setDefaultOptions() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		if (org.usable.getAsIntDefault(0) != 1)
			throw new Exception("ID为【" + orgid + "】的机构不可用");
		DictionaryTemp dt = new DictionaryTemp();
		String sqlstr = "SELECT * FROM  hr_standposition WHERE  sp_id IN(SELECT sp_id FROM hr_standposition_orgtp WHERE usable=1 AND orgtype= "
				+ org.orgtype.getValue() + " ) ";
		CJPALineData<Hr_standposition> pos = new CJPALineData<Hr_standposition>(Hr_standposition.class);
		pos.findDataBySQL(sqlstr, true, false);
		if (pos.size() <= 0)
			throw new Exception("机构类型【" + dt.getCaptionByValue("75", org.orgtype.getValue()) + "】未设置对应的标准职位");
		Hr_orgposition op = new Hr_orgposition();
		int rst = 0;
		CDBConnection con = op.pool.getCon(this);
		try {
			for (CJPABase jpa : pos) {
				Hr_standposition po = (Hr_standposition) jpa;
				op.clear();
				sqlstr = "SELECT * FROM hr_orgposition WHERE orgid=" + orgid + " AND sp_id=" + po.sp_id.getValue();
				op.findBySQL(sqlstr, false);
				if (op.isEmpty()) {
					rst++;
					op.assignfield(po, true);
					op.pid.setAsInt(0);
					op.createtime.setAsDatetime(new Date());
					op.creator.setValue("SYSTEM");
					op.updatetime.setValue(null);
					op.updator.setValue(null);
					op.orgid.setValue(org.orgid.getValue());
					op.orgcode.setValue(org.code.getValue());
					op.orgname.setValue(org.extorgname.getValue());
					op.idpath.setValue(org.idpath.getValue());
					op.save(con);
				}
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
		JSONObject jo = new JSONObject();
		jo.put("updatenum", rst);
		return jo.toString();
	}

	@ACOAction(eventname = "impexcel", Authentication = true, ispublic = false, notes = "入职导入Excel")
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
		// Workbook workbook = CExcelUtil.isExcel2003(fullname) ? new HSSFWorkbook(new
		// FileInputStream(fullname))
		// : new XSSFWorkbook(new FileInputStream(fullname));

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

		Hr_standposition sp = new Hr_standposition();
		Shworg org = new Shworg();
		Hr_orgposition osp = new Hr_orgposition();
		CDBConnection con = osp.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			int ct = values.size();
			int curidx = 0;
			for (Map<String, String> v : values) {
				String orgcode = (v.get("orgcode") == null) ? null : v.get("orgcode").toString();
				String sp_code = (v.get("sp_code") == null) ? null : v.get("sp_code").toString();
				if ((orgcode == null) || (orgcode.isEmpty()) || (sp_code == null) || (sp_code.isEmpty()))
					continue;

				String sqlstr = "SELECT * FROM shworg WHERE code='" + orgcode + "' " + idpathwhere;// AND usable=1
																									// //17-10-31
																									// 甲方要求去掉可用机构限制
				org.findBySQL(sqlstr);
				if (org.isEmpty())
					throw new Exception("权限范围内无可用编码为【" + orgcode + "】的机构");
				sqlstr = "SELECT * FROM hr_standposition WHERE sp_code='" + sp_code + "' AND usable=1";
				sp.findBySQL(sqlstr);
				if (sp.isEmpty())
					throw new Exception("无可用编码为【" + sp_code + "】的标准职位");

				sqlstr = "SELECT * FROM hr_orgposition WHERE orgid=" + org.orgid.getValue() + " AND sp_id="
						+ sp.sp_id.getValue();
				osp.findBySQL(sqlstr, false);
				if (!osp.isEmpty())
					continue;
				checkis100(v);
				rst++;
				osp.clear();
				osp.pid.setAsInt(0); // 上级职位ID
				osp.pname.setAsInt(0); // 上级职位名称
				osp.orgid.setValue(org.orgid.getValue()); // 机构ID
				osp.orgname.setValue(org.extorgname.getValue()); // 机构名称
				osp.orgcode.setValue(org.code.getValue()); // 机构编码
				osp.idpath.setValue(org.idpath.getValue());
				osp.sp_id.setValue(sp.sp_id.getValue()); // 标准ID
				osp.sp_code.setValue(sp.sp_code.getValue()); // 标准职位编码
				osp.sp_name.setValue(sp.sp_name.getValue()); // 标准职位名称
				osp.gtitle.setValue(sp.gtitle.getValue()); // 职衔
				osp.sp_exp.setValue(sp.sp_exp.getValue()); // 职位说明
				osp.lv_id.setValue(sp.lv_id.getValue()); // 职级ID
				osp.lv_num.setValue(sp.lv_num.getValue()); // 职级
				osp.hg_id.setValue(sp.hg_id.getValue()); // 职等ID
				osp.hg_code.setValue(sp.hg_code.getValue()); // 职等编码
				osp.hg_name.setValue(sp.hg_name.getValue()); // 职等名称
				osp.hwc_idzl.setValue(sp.hwc_idzl.getValue()); // 职类ID
				osp.hw_codezl.setValue(sp.hw_codezl.getValue()); // 职类代码
				osp.hwc_namezl.setValue(sp.hwc_namezl.getValue()); // 职类名称
				osp.hwc_idzq.setValue(sp.hwc_idzq.getValue()); // 职群ID
				osp.hw_codezq.setValue(sp.hw_codezq.getValue()); // 职类代码
				osp.hwc_namezq.setValue(sp.hwc_namezq.getValue()); // 职群名称
				osp.hwc_idzz.setValue(sp.hwc_idzz.getValue()); // 职种ID
				osp.hw_codezz.setValue(sp.hw_codezz.getValue()); // 职种代码
				osp.hwc_namezz.setValue(sp.hwc_namezz.getValue()); // 职种名称
				osp.quota.setValue("0"); // 编制数量
				osp.usable.setValue("1"); // 有效状态
				osp.isadvtech.setValue(dictemp.getValueByCation("5", v.get("isadvtech"), "2")); // 高级技术专业人才
				osp.isoffjob.setValue(dictemp.getValueByCation("5", v.get("isoffjob"), "2")); // 脱产
				osp.issensitive.setValue(dictemp.getValueByCation("5", v.get("issensitive"), "2")); // 敏感岗位 离职 调岗需要审计
				osp.iskey.setValue(dictemp.getValueByCation("5", v.get("iskey"), "2")); // 关键岗位
				osp.ishighrisk.setValue(dictemp.getValueByCation("5", v.get("ishighrisk"), "2")); // 高危岗位
				osp.isneedadtoutwork.setValue(dictemp.getValueByCation("5", v.get("isneedadtoutwork"), "2")); // 需要离职审计
				osp.isdreamposition.setValue(dictemp.getValueByCation("5", v.get("isdreamposition"), "2")); // 是否梦想职位
				osp.maxage.setValue("0"); // 最大年龄
				osp.minage.setValue("0"); // 最小年龄
				osp.keyresp1.setValue(getvvalue(v, "keyresp1")); // 关键职责1
				osp.keyrespper1.setValue(getvvalue(v, "keyrespper1")); // 比重1%
				osp.keyresp2.setValue(getvvalue(v, "keyresp2")); // 关键职责2
				osp.keyrespper2.setValue(getvvalue(v, "keyrespper2")); // 比重2%
				osp.keyresp3.setValue(getvvalue(v, "keyresp3")); // 关键职责3
				osp.keyrespper3.setValue(getvvalue(v, "keyrespper3")); // 比重3%
				osp.keyresp4.setValue(getvvalue(v, "keyresp4")); // 关键职责4
				osp.keyrespper4.setValue(getvvalue(v, "keyrespper4")); // 比重4%
				osp.keyresp5.setValue(getvvalue(v, "keyresp5")); // 关键职责5
				osp.keyrespper5.setValue(getvvalue(v, "keyrespper5")); // 比重5%
				osp.keyresp6.setValue(getvvalue(v, "keyresp6")); // 关键职责6
				osp.keyrespper6.setValue(getvvalue(v, "keyrespper6")); // 比重6%
				osp.keyresp7.setValue(getvvalue(v, "keyresp7")); // 关键职责7
				osp.keyrespper7.setValue(getvvalue(v, "keyrespper7")); // 比重7%
				osp.keyresp8.setValue(getvvalue(v, "keyresp8")); // 关键职责8
				osp.keyrespper8.setValue(getvvalue(v, "keyrespper8")); // 比重8%
				osp.remark.setValue(getvvalue(v, "remark")); // 备注
				osp.save(con);
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

	private String getvvalue(Map<String, String> v, String key) {
		Object oj = v.get(key);
		return (oj == null) ? null : oj.toString();
	}

	private void checkis100(Map<String, String> v) throws Exception {
		float sumsp = 0;
		for (int i = 1; i <= 8; i++) {
			String sresp = v.get("keyrespper" + i);
			try {
				float resp = (sresp == null) || (sresp.isEmpty()) ? 0 : Float.valueOf(sresp);
				sumsp += resp;
			} catch (Exception e) {
				v.put("keyrespper" + i, "0");
				e.printStackTrace();
			}
		}
		if (sumsp != 100)
			throw new Exception("所有【关键职责 绩效】之和必须为100，当前为：" + sumsp);
	}

	private List<CExcelField> initExcelFields() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("机构编码", "orgcode", false));
		efields.add(new CExcelField("标准职位编码", "sp_code", true));
		efields.add(new CExcelField("职位名称", "sp_name", true));
		efields.add(new CExcelField("职位说明", "sp_exp", false));
		efields.add(new CExcelField("脱产", "isoffjob", true));
		efields.add(new CExcelField("科技岗位", "isadvtech", true));
		efields.add(new CExcelField("敏感岗位", "issensitive", true));
		efields.add(new CExcelField("关键岗位", "iskey", true));
		efields.add(new CExcelField("高危岗位", "ishighrisk", true));
		efields.add(new CExcelField("关键职责1", "keyresp1", true));
		efields.add(new CExcelField("比重1(%)", "keyrespper1", true));
		efields.add(new CExcelField("关键职责2", "keyresp2", true));
		efields.add(new CExcelField("比重2(%)", "keyrespper2", true));
		efields.add(new CExcelField("关键职责3", "keyresp3", true));
		efields.add(new CExcelField("比重3(%)", "keyrespper3", true));
		efields.add(new CExcelField("关键职责4", "keyresp4", false));
		efields.add(new CExcelField("比重4(%)", "keyrespper4", false));
		efields.add(new CExcelField("关键职责5", "keyresp5", false));
		efields.add(new CExcelField("比重5(%)", "keyrespper5", false));
		efields.add(new CExcelField("关键职责6", "keyresp6", false));
		efields.add(new CExcelField("比重6(%)", "keyrespper6", false));
		efields.add(new CExcelField("关键职责7", "keyresp7", false));
		efields.add(new CExcelField("比重7(%)", "keyrespper7", false));
		efields.add(new CExcelField("关键职责8", "keyresp8", false));
		efields.add(new CExcelField("比重8(%)", "keyrespper8", false));
		efields.add(new CExcelField("备注", "remark", false));
		return efields;
	}

}
