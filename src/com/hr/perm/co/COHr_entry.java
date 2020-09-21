package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFRow;
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
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_orgposition;
import com.hr.insurance.entity.Hr_ins_buyinsurance;
import com.hr.perm.ctr.CtrHrEmployeeUtil;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_linked;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.perm.entity.Hr_entry;
import com.hr.perm.entity.Hr_entryex;
import com.hr.salary.co.Cosacommon;
import com.hr.util.HRUtil;
import com.hr.util.TimerTaskHRZM;

@ACO(coname = "web.hr.entry")
public class COHr_entry {
	private static String sctfields = "h.entry_id, h.entry_code, h.er_id, h.employee_code, h.entrytype,"
			+ "h.entrysourcr, h.entrydate, h.probation, h.promotionday,"
			+ "h.quota_over, h.quota_over_rst, h.remark, h.wfid, h.stat,h.orghrlev,h.rectname,h.rectcode,"
			+ "h.idpath, h.entid, h.creator, h.createtime, h.updator,"
			+ "h.updatetime, h.attribute1, h.attribute2, h.attribute3,"
			+ "h.attribute4, h.attribute5, e.id_number, e.card_number,e.bwday,"
			+ "h.quachk,h.quachknote,h.iview,h.iviewnote,h.forunit,h.chkok,h.chkigmsg,"
			+ "h.forunitnote,h.cercheck,h.cerchecknote,h.wtexam,h.wtexamnote,"
			+ "h.transorg,h.transorgnote,h.formchk,h.formchknote,h.train,h.trainnote,h.transextime,"
			+ "h.transextimenote,h.tetype,h.tetypenote,h.meexam,h.meexamnote,h.dispunit,"
			+ "h.dispunitnote,h.ovtype,h.ovtypenote,h.rcenl,h.rcenlnote,h.dispeextime,h.dispeextimenote,"
			+ "h.salarydate,"
			+ "e.idtype,e.employee_name, e.mnemonic_code, e.english_name, e.avatar_id1,e.avatar_id2,e.eovertype,"
			+ "e.birthday, e.sex, e.hiredday, e.degree, e.married,e.nativeid, e.nativeplace,"
			+ "e.address, e.nation, e.email, e.empstatid, e.modify, e.usedname,"
			+ "e.pldcp, e.major, e.registertype, e.registeraddress, e.health,"
			+ "e.medicalhistory, e.bloodtype, e.height, e.importway, e.importor,"
			+ "e.cellphone, e.urgencycontact, e.telphone, e.nationality,"
			+ "e.introducer, e.guarantor,e.sign_org,e.sign_date,e.expired_date,"
			+ "e.urmail,e.emnature,e.hwc_namezl,e.sscurty_enddate,e.needdrom,e.atdtype,"
			+ "e.skill, e.skillfullanguage, e.speciality, e.welfare, e.talentstype,e.usable,"
			+ "e.sscurty_addr, e.sscurty_startdate, e.shoesize, e.pants_code,"
			+ "e.iskey,e.hwc_namezq,e.hwc_namezz,e.bwyear,e.juridical,e.advisername,e.advisercode,e.adviserphone,"
			+ "e.coat_code, e.dorm_bed, e.pay_way, e.schedtype, e.note, e.attid,e.noclock,e.kqdate_start,"
			+ "h.stru_id,h.stru_name,h.position_salary,h.base_salary,h.tech_salary,h.achi_salary,"
			+ "h.otwage,h.tech_allowance,h.postsubs,e.degreetype,e.degreecheck,"
			+ ""
			+ "osp.orgid, osp.orgcode, osp.orgname, osp.lv_id, osp.lv_num, osp.hg_id,"
			+ "osp.hg_code, osp.hg_name, osp.ospid, osp.ospcode, osp.sp_name"
			+ "";

	@ACOAction(eventname = "save", Authentication = true, notes = "保存入职单据")
	public String save() throws Exception {
		// System.out.println("entry_save1111111111111111111111111111111111111111111111111111111111111");
		Hr_employee_linked emp = new Hr_employee_linked();
		Hr_entry enty = new Hr_entry();
		String psd = CSContext.getPostdata();
		Logsw.dblog("保存职员的JSON"+psd);
		JSONObject oj = JSONObject.fromObject(psd);
		Object oerid = oj.get("er_id");
		String[] disFlds = null;
		if ((oerid != null) && (!oerid.toString().isEmpty())) {
			emp.findByID(oerid.toString());
			if (emp.isEmpty()) {
				throw new Exception("没有找到ID为【" + oerid.toString() + "】的员工档案 ");
			}
			disFlds = new String[] { "idpath", "creator", "createtime", "updator", "updatetime" };
		} else {
			disFlds = new String[] {};
		}
		String[] flds = emp.getFieldNames(disFlds);
		emp.fromjson(psd, flds);
		emp.empstatid.setAsInt(6);// 待入职
		// checkIDNumber(emp);// 检查是否有重复身份证号码 允许重复身份证号码 检查在职的
		// 检查岗位年龄
		// System.out.println("employe json:" + emp.tojson());

		// if (oj.has("entry_id") && (oj.getString("entry_id") != null))
		// s enty.findByID(oj.getString("entry_id"));

		// String sqlstr = "SELECT * FROM hr_employee_leved WHERE employee_code='" +
		// emp.employee_code.getValue() + "'";
		// Hr_employee_leved el = new Hr_employee_leved();
		// el.findBySQL(sqlstr, false);
		// if (!el.isEmpty())
		// throw new Exception("该工号已在离职列表中，不允许入职");

		String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM `hr_balcklist` where id_number='" + emp.id_number.getValue()
				+ "'";
		if (Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct")) != 0)
			throw new Exception("身份证号码【" + emp.id_number.getValue() + "】在黑名单");

		enty.fromjson(psd);
		emp.note.setValue(enty.remark.getValue());
		CDBConnection con = emp.pool.getCon(this);
		con.startTrans();
		try {
			emp.save(con);
			enty.er_id.setValue(emp.er_id.getValue());
			enty.employee_code.setValue(emp.employee_code.getValue());
			enty.er_id.setValue(emp.er_id.getValue());
			enty.orgid.setValue(emp.orgid.getValue());
			enty.lv_num.setAsFloat(emp.lv_num.getAsFloat());
			enty.save(con, false);
			Cosacommon.save_salary_chgblill(con, enty, oj);
			String rst = findbyid(con, enty.entry_id.getValue());
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private void checkIDNumber(Hr_employee_linked emp) throws Exception {
		if (!emp.er_id.isEmpty())
			return;// 已经保存的资料无需判断
		String idnumber = emp.id_number.getValue();
		String sqlstr = "select count(*) ct from hr_employee where id_number='" + idnumber + "'";
		if (Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct")) != 0)
			throw new Exception("身份证号码【" + idnumber + "】重复!");
	}

	@ACOAction(eventname = "find", Authentication = true, notes = "查询入职单据")
	public String find() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String sqlwhere = urlparms.get("sqlwhere");
		if ("list".equalsIgnoreCase(type) || "tree".equalsIgnoreCase(type)) {
			String parms = urlparms.get("parms");
			String edittps = CorUtil.hashMap2Str(urlparms, "edittps", "需要参数edittps");
			String activeprocname = urlparms.get("activeprocname");

			HashMap<String, String> edts = CJSON.Json2HashMap(edittps);

			String smax = urlparms.get("max");
			int max = (smax == null) ? 300 : Integer.valueOf(smax);
			String order = urlparms.get("order");

			String spage = urlparms.get("page");
			String srows = urlparms.get("rows");

			int page = (spage == null) ? 1 : Integer.valueOf(spage);
			int rows = (srows == null) ? 300 : Integer.valueOf(srows);

			CJPALineData<Hr_entryex> jpas = new CJPALineData<Hr_entryex>(Hr_entryex.class);
			Hr_entry enty = new Hr_entry();

			List<JSONParm> jps = CJSON.getParms(parms);
			String where = CjpaUtil.buildFindSqlByJsonParms(new Hr_entryex(), jps);
			// if (dfi && chgi && (userIsInside()) && (jpa.cfield("idpath") !=
			// null)) {
			// where = where + " and idpath like '1,%'";
			// } else if (dfi && )
			if (enty.cfield("idpath") != null)
				where = where + CSContext.getIdpathwhere();
			if ((sqlwhere != null) && (sqlwhere.length() > 0))
				where = where + " and " + sqlwhere + " ";

			// edittps:{"isedit":true,"issubmit":true,"isview":true,"isupdate":false,"isfind":true}
			if (enty.cfieldbycfieldname("stat") != null) {
				String sqlstat = "";
				if (Obj2Bl(edts.get("isedit")))
					sqlstat = sqlstat + " (stat=1) or";
				if (Obj2Bl(edts.get("issubmit"))) {
					sqlstat = sqlstat + " (stat>1 and stat<9) or";
					// 去掉 在流程中、当前节点为数据保护节点 且 当前 登录用户不在 当前节点
				}
				if (Obj2Bl(edts.get("isview")))
					sqlstat = sqlstat + " (stat<=9) or";
				if (Obj2Bl(edts.get("isupdate")) || Obj2Bl(edts.get("isfind")))
					sqlstat = sqlstat + " (stat=9) or";
				if (sqlstat.length() > 0) {
					sqlstat = sqlstat.substring(1, sqlstat.length() - 2);
					where = where + " and (" + sqlstat + ")";
				}
			}
			if ((activeprocname != null) && (!activeprocname.isEmpty())) {
				String idfd = enty.getIDField().getFieldname();
				String ew = "SELECT " + idfd + " FROM " + enty.tablename + " t,shwwf wf,shwwfproc wfp"
						+ " WHERE t.stat>1 AND t.stat<9 AND t.wfid=wf.wfid AND wf.wfid=wfp.wfid "
						+ "  AND wfp.stat=2 AND wfp.procname='" + activeprocname + "'";
				ew = " and " + idfd + " in (" + ew + ")";
				where = where + ew;
			}
			String sqltr = null;

			String textfield = urlparms.get("textfield");

			sqltr = "select * from (SELECT " + sctfields
					+ " FROM hr_entry h,hr_employee e,hr_orgposition osp WHERE h.er_id=e.er_id AND h.ospid = osp.ospid) tb where 1=1 " + where;

			if ((order != null) && (!order.isEmpty())) {
				sqltr = sqltr + " order by " + order;
			} else
				sqltr = sqltr + " order by " + enty.getIDFieldName() + " desc ";

			if ("list".equalsIgnoreCase(type)) {
				String scols = urlparms.get("cols");
				if (scols != null) {
					String[] ignParms = new String[] {};
					new CReport(sqltr, null).export2excel(ignParms, scols);
					return null;
				} else {
					if ((spage == null) || (srows == null)) {

						return DBPools.defaultPool().opensql2json(sqltr, 1, max);

						// jpas.findDataBySQL(sqltr, false, false, 1, max);
						// return jpas.tojson();
					} else {
						return DBPools.defaultPool().opensql2json(sqltr, page, max);
						// jpas.findDataBySQL(sqltr, false, false, page, rows);
						// return jpas.tojsonpage();
					}
				}
			}
		}
		// //by id
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			CDBConnection con = DBPools.defaultPool().getCon(this);
			try {
				return findbyid(con, id);
			} finally {
				con.close();
			}
		}
		return null;
	}

	private boolean Obj2Bl(Object o) {
		if (o == null)
			return false;
		return Boolean.valueOf(o.toString());
	}

	private String findbyid(CDBConnection con, String entry_id) throws Exception {
		String sqlstr = "SELECT " + sctfields + " FROM hr_entry h,hr_employee e,hr_orgposition osp WHERE h.er_id=e.er_id and h.ospid=osp.ospid AND h.entry_id=" + entry_id;
		Hr_entry enty = new Hr_entry();
		JSONArray os = con.opensql2json_o(sqlstr);
		if (os.size() == 0)
			throw new Exception("没有找到ID为【" + entry_id + "】的入职表单");
		JSONObject o = os.getJSONObject(0);
		String er_id = o.getString("er_id");

		Hr_employee_linked el = new Hr_employee_linked();
		el.findByID(er_id, true);

		JSONObject elo = el.toJsonObj();

		o.put("hr_employee_works", elo.getJSONArray("hr_employee_works"));
		o.put("hr_employee_rewards", elo.getJSONArray("hr_employee_rewards"));
		// o.put("hr_employee_relations", elo.getJSONArray("hr_employee_relations"));
		o.put("hr_employee_phexas", elo.getJSONArray("hr_employee_phexas"));
		o.put("hr_employee_leanexps", elo.getJSONArray("hr_employee_leanexps"));
		o.put("hr_employee_familys", elo.getJSONArray("hr_employee_familys"));
		o.put("hr_employee_cretls", elo.getJSONArray("hr_employee_cretls"));
		o.put("hr_employee_trainexps", elo.getJSONArray("hr_employee_trainexps"));
		o.put("shw_attachs", elo.getJSONArray("shw_attachs"));

		// o.put("hr_employee_works", enty.pool.opensql2json_O("select * from
		// hr_employee_work where er_id=" + er_id));
		// o.put("hr_employee_rewards", enty.pool.opensql2json_O("select * from
		// hr_employee_reward where er_id=" + er_id));
		// o.put("hr_employee_relations", enty.pool.opensql2json_O("select * from
		// hr_employee_relation where er_id=" + er_id));
		// o.put("hr_employee_phexas", enty.pool.opensql2json_O("select * from
		// hr_employee_phexa where er_id=" + er_id));
		// o.put("hr_employee_leanexps", enty.pool.opensql2json_O("select * from
		// hr_employee_leanexp where er_id=" + er_id));
		// o.put("hr_employee_familys", enty.pool.opensql2json_O("select * from
		// hr_employee_family where er_id=" + er_id));
		// o.put("hr_employee_cretls", enty.pool.opensql2json_O("select * from
		// hr_employee_cretl where er_id=" + er_id));
		// o.put("hr_employee_trainexps", enty.pool.opensql2json_O("select * from
		// hr_employee_trainexp where er_id=" + er_id));
		return o.toString();
	}

	@ACOAction(eventname = "del", Authentication = true, notes = "删除入职单据")
	public String del() throws Exception {
		String id = CSContext.getParms().get("id");
		if ((id == null) || id.isEmpty())
			throw new Exception("需要id参数");
		Hr_entry enty = new Hr_entry();

		CDBConnection con = enty.pool.getCon(this);
		con.startTrans();
		try {
			enty.findByID(id);
			if (enty.stat.getAsInt() > 1)
				throw new Exception("非制单状态，不允许删除");
			Hr_employee_linked e = new Hr_employee_linked();
			e.delete(con, enty.er_id.getValue(), true);
			enty.delete(con, id, true);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "findEntryLis", Authentication = true, ispublic = false, notes = "根据登录权限查询入职资料")
	public String findEntryLis() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(eparms);
		Hr_employee he = new Hr_employee();
		JSONParm p = CjpaUtil.getParm(jps, "employee_code");
		if ((p != null) && (p.getParmname().indexOf(".") == -1))
			p.setParmname("e." + p.getParmname());
		String where = CjpaUtil.buildFindSqlByJsonParms(new Hr_entryex(), jps);
		String idpw = CSContext.getIdpathwhere().replace("idpath", "h.idpath");
		String smax = parms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);
		String sqlstr = "SELECT " + sctfields + " FROM hr_entry h,hr_employee e WHERE h.er_id=e.er_id " + idpw + where
				+ " limit 0," + max;
		return he.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findEntryEmpLis", Authentication = true, ispublic = true, notes = "根据登录权限查询入职人员资料")
	public String findEntryEmpLis() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String[] ignParms = { "employee_code" };// 忽略的查询条件
		String[] notnull = {};

		String idpw = CSContext.getIdpathwhere().replace("idpath", "h.idpath");
		String sqlstr = "SELECT * FROM Hr_entry_try h WHERE 1=1 ";
		if (!HRUtil.hasRoles("71")) {// 薪酬管理员
			sqlstr = sqlstr + " and h.employee_code='" + CSContext.getUserName() + "' ";
		}
		JSONParm p = CjpaUtil.getParm(jps, "employee_code");
		if (p != null) {
			String employee_code = p.getParmvalue();
			if ((employee_code != null) && (!employee_code.isEmpty()))
				sqlstr = sqlstr + " and h.employee_code='" + employee_code + "' ";
		}
		sqlstr = sqlstr + idpw;// + " limit 0," + max;
		return new CReport(sqlstr, notnull).findReport(ignParms, null);
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
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 1);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 1);
		Hr_employee emp = new Hr_employee();
		Hr_orgposition osp = new Hr_orgposition();
		Hr_entry enty = new Hr_entry();
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
				if (!emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】的人事资料已经存在");
				String ospcode = v.get("ospcode");
				if ((ospcode == null) || (ospcode.isEmpty()))
					throw new Exception("【" + v.get("employee_name") + "】机构职位编码不能为空");
				osp.clear();
				osp.findBySQL("SELECT * FROM hr_orgposition WHERE ospcode='" + ospcode + "'", false);
				if (osp.isEmpty())
					throw new Exception("工号【" + employee_code + "】的机构职位编码【" + ospcode + "】不存在");

				String id_number = v.get("id_number");
				if ((id_number == null) || (id_number.isEmpty()))
					throw new Exception("【" + v.get("employee_name") + "】身份证不能为空");

				String sprobation = v.get("probation");
				if ((sprobation == null) || (sprobation.isEmpty()))
					throw new Exception("【" + v.get("employee_name") + "】试用期不能为空");
				int probation = Integer.valueOf(sprobation);
				if (probation < 0)
					throw new Exception("试用期不能小于0");

				emp.clear();
				emp.card_number.setValue(v.get("card_number"));
				emp.employee_code.setValue(v.get("employee_code"));
				emp.employee_name.setValue(v.get("employee_name"));
				emp.sex.setValue(dictemp.getValueByCation("81", v.get("sex")));
				emp.degree.setValue(dictemp.getValueByCation("84", v.get("degree")));
				emp.major.setValue(v.get("major"));
				emp.birthday.setValue(v.get("birthday"));
				emp.id_number.setValue(v.get("id_number"));
				emp.registertype.setValue(dictemp.getValueByCation("702", v.get("registertype")));
				emp.registeraddress.setValue(v.get("registeraddress"));
				emp.sign_org.setValue(v.get("sign_org"));
				emp.sign_date.setValue(v.get("sign_date"));
				emp.expired_date.setValue(v.get("expired_date"));
				emp.hiredday.setValue(v.get("hiredday"));
				emp.telphone.setValue(v.get("telphone"));
				emp.urgencycontact.setValue(v.get("urgencycontact"));
				emp.cellphone.setValue(v.get("cellphone"));
				emp.dorm_bed.setValue(v.get("dorm_bed"));

				emp.pldcp.setValue(dictemp.getValueByCation("495", v.get("pldcp")));//
				emp.nation.setValue(dictemp.getValueByCation("797", v.get("nation")));//
				emp.nationality.setValue(v.get("nationality"));
				emp.married.setValue(dictemp.getValueByCation("714", v.get("married")));//
				emp.nativeplace.setValue(v.get("nativeplace"));
				emp.health.setValue(v.get("health"));
				emp.medicalhistory.setValue(v.get("medicalhistory"));
				emp.bloodtype.setValue(dictemp.getValueByCation("697", v.get("bloodtype")));//
				emp.entrysourcr.setValue(dictemp.getValueByCation("741", v.get("entrysourcr")));//
				emp.pay_way.setValue(v.get("pay_way"));
				emp.atdtype.setValue(v.get("atdtype"));//
				emp.rectcode.setValue(v.get("rectcode"));
				emp.rectname.setValue(v.get("rectname"));
				emp.address.setValue(v.get("address"));
				emp.advisercode.setValue(v.get("advisercode"));
				emp.advisername.setValue(v.get("advisername"));
				emp.adviserphone.setValue(v.get("adviserphone"));
				emp.juridical.setValue(v.get("juridical"));
				emp.note.setValue(v.get("note"));
				emp.dispunit.setValue(dictemp.getValueByCation("1322", v.get("dispunit")));
				emp.dispeextime.setValue(v.get("dispeextime"));
				emp.transorg.setValue(v.get("transorg"));
				emp.transextime.setValue(v.get("transextime"));

				emp.orgid.setValue(osp.orgid.getValue()); // 部门ID
				emp.orgcode.setValue(osp.orgcode.getValue()); // 部门编码
				emp.orgname.setValue(osp.orgname.getValue()); // 部门名称
				emp.hwc_namezl.setValue(osp.hwc_namezl.getValue()); // 职类
				emp.lv_id.setValue(osp.lv_id.getValue()); // 职级ID
				emp.lv_num.setValue(osp.lv_num.getValue()); // 职级
				emp.hg_id.setValue(osp.hg_id.getValue()); // 职等ID
				emp.hg_code.setValue(osp.hg_code.getValue()); // 职等编码
				emp.hg_name.setValue(osp.hg_name.getValue()); // 职等名称
				emp.ospid.setValue(osp.ospid.getValue()); // 职位ID
				emp.ospcode.setValue(osp.ospcode.getValue()); // 职位编码
				emp.sp_name.setValue(osp.sp_name.getValue()); // 职位名称
				emp.iskey.setValue(osp.iskey.getValue()); // 关键岗位
				emp.hwc_namezq.setValue(osp.hwc_namezq.getValue()); // 职群
				emp.hwc_namezz.setValue(osp.hwc_namezz.getValue()); // 职种
				emp.usable.setAsInt(1); // 有效
				emp.idpath.setValue(osp.idpath.getValue());
				emp.empstatid.setAsInt(6);// 待入职
				emp.kqdate_start.setAsDatetime(emp.hiredday.getAsDatetime());
				emp.save(con, false);
				enty.clear();
				enty.er_id.setValue(emp.er_id.getValue());
				enty.employee_code.setValue(emp.employee_code.getValue());
				enty.er_id.setValue(emp.er_id.getValue());
				enty.orgid.setValue(emp.orgid.getValue());
				enty.lv_num.setAsFloat(emp.lv_num.getAsFloat());
				enty.entrytype.setAsInt(1);
				enty.entrysourcr.setAsInt(1);
				enty.entrydate.setAsDatetime(emp.hiredday.getAsDatetime());
				enty.probation.setAsInt(probation);
				enty.promotionday.setAsDatetime(Systemdate.dateMonthAdd(emp.hiredday.getAsDatetime(), probation));
				if (probation == 0)
					enty.ispromotioned.setAsInt(1);
				else
					enty.ispromotioned.setAsInt(2);
				enty.quota_over.setAsInt(2);
				enty.quota_over_rst.setAsInt(2);// 如果超编 允许超编入职
				enty.orghrlev.setAsInt(HRUtil.getOrgHrLev(emp.orgid.getValue()));
				enty.quachk.setAsInt(1);
				enty.cercheck.setAsInt(1);
				enty.wtexam.setAsInt(1);
				enty.formchk.setAsInt(1);
				enty.idpath.setValue(osp.idpath.getValue());
				enty.attribute1.setValue(batchno);
				enty.dispunit.setValue(dictemp.getValueByCation("1322", v.get("dispunit")));
				enty.dispeextime.setValue(v.get("dispeextime"));
				enty.transorg.setValue(v.get("transorg"));
				enty.transextime.setValue(v.get("transextime"));
				enty.save(con, false);
				enty.wfcreate(null, con);
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
		efields.add(new CExcelField("卡号", "card_number", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("性别", "sex", true));
		efields.add(new CExcelField("学历", "degree", true));
		efields.add(new CExcelField("专业", "major", true));
		efields.add(new CExcelField("出生日期", "birthday", true));
		efields.add(new CExcelField("身份证/护照号码", "id_number", true));
		efields.add(new CExcelField("户籍类型", "registertype", true));
		efields.add(new CExcelField("身份证地址", "registeraddress", true));
		efields.add(new CExcelField("发证机关", "sign_org", true));
		efields.add(new CExcelField("签发日期", "sign_date", true));
		efields.add(new CExcelField("签发到期", "expired_date", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("机构职位编码", "ospcode", true));
		efields.add(new CExcelField("联系电话", "telphone", true));
		efields.add(new CExcelField("紧急联系人", "urgencycontact", true));
		efields.add(new CExcelField("紧急联系人电话", "cellphone", true));
		efields.add(new CExcelField("宿舍床位", "dorm_bed", true));
		efields.add(new CExcelField("试用期", "probation", true));

		efields.add(new CExcelField("政治面貌", "pldcp", true));
		efields.add(new CExcelField("民族", "nation", true));
		efields.add(new CExcelField("国籍", "nationality", true));
		efields.add(new CExcelField("婚姻状况", "married", true));
		efields.add(new CExcelField("籍贯", "nativeplace", true));
		efields.add(new CExcelField("健康情况", "health", true));
		efields.add(new CExcelField("过往病史", "medicalhistory", true));
		efields.add(new CExcelField("血型", "bloodtype", true));
		efields.add(new CExcelField("人员来源", "entrysourcr", true));
		efields.add(new CExcelField("计薪方式", "pay_way", true));
		efields.add(new CExcelField("出勤类别", "atdtype", true));
		efields.add(new CExcelField("招聘人工号", "rectcode", true));
		efields.add(new CExcelField("招聘人姓名", "rectname", true));
		efields.add(new CExcelField("现住址", "address", true));
		efields.add(new CExcelField("兴趣爱好", "speciality", true));
		efields.add(new CExcelField("派遣机构", "dispunit", true));
		efields.add(new CExcelField("派遣期限", "dispeextime", true));
		efields.add(new CExcelField("输送机构", "transorg", true));
		efields.add(new CExcelField("输送期限", "transextime", true));
		efields.add(new CExcelField("指导老师工号", "advisercode", true));
		efields.add(new CExcelField("指导老师姓名", "advisername", true));
		efields.add(new CExcelField("指导老师电话", "adviserphone", true));
		efields.add(new CExcelField("法人单位", "juridical", true));
		efields.add(new CExcelField("备注", "note", true));
		return efields;
	}

	@ACOAction(eventname = "inport4zm", Authentication = true, notes = "从招募导入数据")
	public String inport4zm() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String bgdatestr = CorUtil.hashMap2Str(parms, "bgdate");
		String eddatestr = CorUtil.hashMap2Str(parms, "eddate");
		String empcode = CorUtil.hashMap2Str(parms, "empcode");
		Date bgdate = null, eddate = null;
		if ((bgdatestr != null) && (eddatestr != null)) {
			String strdtbg = Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(bgdatestr));
			String strdted = Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(eddatestr));
			bgdate = Systemdate.getDateByStr(strdtbg);
			eddate = Systemdate.dateDayAdd(Systemdate.getDateByStr(strdted), 1);
			if (eddate.getTime() < bgdate.getTime())
				throw new Exception("截止日期必须大于开始日期");
		} else {
			if (empcode == null)
				throw new Exception("日期和工号不能同时为空");
		}
		if (empcode != null)
			empcode = empcode.trim();
		String ct = TimerTaskHRZM.importDayData(bgdate, eddate, empcode);
		JSONObject jo = new JSONObject();
		jo.put("rst", ct);
		return jo.toString();
	}

	@ACOAction(eventname = "dodelduplicates", Authentication = true, notes = "删除重复的调动单")
	public String dodelduplicates() throws Exception {
		String sqlstr = " SELECT DISTINCT er_id FROM  hr_employee_transfer  GROUP BY  er_id,tranfcmpdate,tranftype2,tranftype1,"
				+
				" tranftype3,odorgid,odlv_num,odhg_id,odospid,odattendtype,oldcalsalarytype,neworgid,newlv_num,newhg_id,newospid,"
				+
				"newattendtype,newhwc_namezl HAVING COUNT(*) > 1";
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O();
		JSONArray rts = jo.getJSONArray("rows");
		Hr_employee_transfer tran = new Hr_employee_transfer();
		CDBConnection con = tran.pool.getCon(this);
		con.startTrans();
		List<String> sqls = new ArrayList<String>();
		System.out.println("删除重复调动记录，处理开始" + Systemdate.getStrDate());
		try {
			for (int i = 0; i < rts.size(); i++) {
				JSONObject row = rts.getJSONObject(i);

				String er_id = row.getString("er_id");
				String sqlstr1 = "DELETE FROM hr_employee_transfer WHERE emptranf_id IN  (SELECT * FROM (" +
						" SELECT tr.emptranf_id FROM hr_employee_transfer tr,  ( SELECT  MAX(emptranf_id) AS emptranf_id,"
						+
						" er_id,tranfcmpdate,tranftype2,tranftype1,tranftype3,odorgid,odlv_num,odhg_id,odospid,odattendtype,"
						+
						"oldcalsalarytype,neworgid,newlv_num,newhg_id,newospid,newattendtype,newhwc_namezl FROM hr_employee_transfer "
						+
						" GROUP BY  er_id,tranfcmpdate,tranftype2,tranftype1,tranftype3,odorgid,odlv_num,odhg_id,odospid,odattendtype,"
						+
						"oldcalsalarytype, neworgid,newlv_num,newhg_id,newospid,newattendtype,newhwc_namezl HAVING COUNT(*) > 1) tb "
						+
						" WHERE tr.er_id=" + er_id
						+ "  AND tr.er_id=tb.er_id AND tr.tranfcmpdate=tb.tranfcmpdate AND tr.tranftype2=tb.tranftype2 AND "
						+
						" tr.tranftype1=tb.tranftype1 AND tr.tranftype3=tb.tranftype3 AND tr.odorgid=tb.odorgid AND tr.odlv_num=tb.odlv_num AND"
						+
						" tr.odhg_id=tb.odhg_id AND tr.odospid=tb.odospid AND tr.odattendtype=tb.odattendtype AND tr.oldcalsalarytype=tb.oldcalsalarytype AND "
						+
						" tr.neworgid=tb.neworgid AND tr.newlv_num=tb.newlv_num AND tr.newhg_id=tb.newhg_id AND tr.newospid=tb.newospid AND"
						+
						" tr.newattendtype=tb.newattendtype AND tr.newhwc_namezl=tb.newhwc_namezl  AND tr.emptranf_id<>tb.emptranf_id) temp )";
				sqls.add(sqlstr1);
				if ((i + 1) % 50 == 0) {
					con.execSqls(sqls);
					sqls.clear();
					System.out.println("删除重复调动记录，处理50条" + Systemdate.getStrDate());
				}
			}
			con.execSqls(sqls);
			con.submit();
			System.out.println("删除重复调动记录，处理完毕" + Systemdate.getStrDate());
			return "{\"result\":\"OK\"}";
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}
	
	@ACOAction(eventname = "changeEnttyPostion", Authentication = true, ispublic = true, notes = "入职单已完成状态下修改职位信息")
	public String changeEnttyPostion() throws Exception {
		if (!HRUtil.hasRoles("19")){// 薪酬管理员
			throw new Exception("当前登录用户无使用此功能的权限");
		}
		HashMap<String, String> parms = CSContext.getParms();
		String ospid = CorUtil.hashMap2Str(parms, "ospid", "ospid参数不能为空");
		String entcode = CorUtil.hashMap2Str(parms, "entcode", "entcode参数不能为空");
		Hr_orgposition op=new Hr_orgposition();
		Hr_entry ent=new Hr_entry();
		op.findByID(ospid);
		if(op.isEmpty()){
			throw new Exception("id为【" + ospid + "】的机构职位不存在");
		}
		ent.findBySQL("select * from hr_entry where  entry_code='"+entcode+"'");
		if(ent.isEmpty()){
			throw new Exception("编码为【" + entcode + "】的入职单不存在");
		}
		String emn="非脱产";
		if(op.isoffjob.getAsInt()==1){
			emn="脱产";
		}
		int hrlev =HRUtil.getOrgHrLev(op.orgid.getValue());
		
		String sqlstr ="UPDATE `hr_entry` SET orgid="+op.orgid.getValue()+",lv_num="+op.lv_num.getValue()+",ospid="+op.ospid.getValue()+
				",idpath='"+op.idpath.getValue()+"',orghrlev="+hrlev+" WHERE entry_code='"+entcode+"'";
	    ent.pool.execsql(sqlstr);
	    String sqlstr1 ="UPDATE `hr_entry_try` SET orgid="+op.orgid.getValue()+",orgcode='"+op.orgcode.getValue()+"',orgname='"+op.orgname.getValue()+
				"',lv_num="+op.lv_num.getValue()+",hg_name='"+op.hg_name.getValue()+"',ospid="+op.ospid.getValue()+",ospcode='"+op.ospcode.getValue()+"',sp_name='"+op.sp_name.getValue()+
				"',hwc_namezl='"+op.hwc_namezl.getValue()+"',idpath='"+op.idpath.getValue()+"' WHERE entry_code='"+entcode+"'";
	    ent.pool.execsql(sqlstr1);
	    String sqlstr2 ="UPDATE `hr_employee` SET orgid="+op.orgid.getValue()+",orgcode='"+op.orgcode.getValue()+"',orgname='"+op.orgname.getValue()+
				"',lv_id="+op.lv_id.getValue()+",lv_num="+op.lv_num.getValue()+",hg_id="+op.hg_id.getValue()+",hg_code='"+op.hg_code.getValue()+
				"',hg_name='"+op.hg_name.getValue()+"',ospid="+op.ospid.getValue()+",ospcode='"+op.ospcode.getValue()+"',sp_name='"+op.sp_name.getValue()+
				"',iskey="+op.iskey.getValue()+",hwc_namezq='"+op.hwc_namezq.getValue()+"',hwc_namezz='"+op.hwc_namezz.getValue()+"',hwc_namezl='"+op.hwc_namezl.getValue()+
				"',idpath='"+op.idpath.getValue()+"',emnature='"+emn+"' WHERE er_id="+ent.er_id.getValue();
	    ent.pool.execsql(sqlstr2);
	    JSONObject jo = new JSONObject();
		jo.put("rst", "已修改职位信息，请重新查询单据");
		return jo.toString();
	}
}
