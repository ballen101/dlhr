package com.hr.recruit.co;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shwarea;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelEx;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_linked;
import com.hr.perm.entity.Hr_employee_transfer;
import com.hr.recruit.ctr.HrRecruitUtil;
import com.hr.recruit.entity.Hr_recruit_form;
import com.hr.recruit.entity.Hr_recruit_formex;
import com.hr.recruit.entity.Hr_recruit_quachk;
import com.hr.util.HRUtil;

@ACO(coname = "web.hr.recruit")
public class COHr_recruit_form {
	public static String sctfields = "h.recruit_id, h.recruit_code, h.er_id, h.employee_code, h.entrytype,"
			+ "h.entrysourcr, h.entrydate,h.entryaddr, h.probation, h.promotionday,"
			+ "h.quota_over, h.quota_over_rst, h.remark, h.wfid, h.stat,h.orghrlev,h.rectname,h.rectcode,"
			+ "h.idpath, h.entid, h.creator, h.createtime, h.updator,"
			+ "h.updatetime, h.attribute1, h.attribute2, h.attribute3,"
			+ "h.attribute4, h.attribute5, e.id_number, e.card_number,e.bwday,"
			+ "h.quachk,h.quachknote,h.iview,h.iviewnote,h.forunit,h.chkok,h.chkigmsg,"
			+ "h.forunitnote,h.cercheck,h.cerchecknote,h.wtexam,h.wtexamnote,"
			+ "h.transorg,h.transorgnote,h.formchk,h.formchknote,h.train,h.trainnote,h.transextime,"
			+ "h.transextimenote,h.tetype,h.tetypenote,h.meexam,h.meexamnote,h.dispunit,"
			+ "h.dispunitnote,h.ovtype,h.ovtypenote,h.rcenl,h.rcenlnote,h.dispeextime,h.dispeextimenote,"
			+ "h.salarydate,h.recruit_quachk_id,e.juridical,h.reportfor,h.reportfornote,"
			+ "e.employee_name, e.mnemonic_code, e.english_name, e.avatar_id1,e.avatar_id2,e.eovertype,"
			+ "e.birthday, e.sex, e.hiredday, e.degree, e.married,e.nativeid, e.nativeplace,"
			+ "e.address, e.nation, e.email, e.empstatid, e.modify, e.usedname,"
			+ "e.pldcp, e.major, e.registertype, e.registeraddress, e.health,"
			+ "e.medicalhistory, e.bloodtype, e.height, e.importway, e.importor,"
			+ "e.cellphone, e.urgencycontact, e.telphone, e.nationality,"
			+ "e.introducer, e.guarantor,e.sign_org,e.sign_date,e.expired_date,"
			+ "e.urmail,e.emnature,e.hwc_namezl,e.sscurty_enddate,e.needdrom,e.atdtype,"
			+ "e.skill, e.skillfullanguage, e.speciality, e.welfare, e.talentstype,"
			+ "e.orgid, e.orgcode, e.orgname, e.lv_id, e.lv_num, e.hg_id,"
			+ "e.hg_code, e.hg_name, h.ospid, e.ospcode, e.sp_name, e.usable,"
			+ "e.sscurty_addr, e.sscurty_startdate, e.shoesize, e.pants_code,"
			+ "e.iskey,e.hwc_namezq,e.hwc_namezz,e.bwyear,"
			+ "e.coat_code, e.dorm_bed, e.pay_way, e.schedtype, e.note, e.attid,e.noclock,"
			+ "e.kqdate_start, TIMESTAMPDIFF(YEAR, e.birthday, CURDATE()) age ";

	@ACOAction(eventname = "findquachkByIDCard", Authentication = true, ispublic = false, notes = "查找最新资质对比")
	public String findquachkByIDCard() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String id_number = CorUtil.hashMap2Str(parms, "id_number", "id_number参数不能为空");
		String sqlstr = "SELECT * FROM hr_recruit_quachk t  WHERE t.stat=9 and t.id_number = '" + id_number
				+ "' ORDER BY t.createtime DESC LIMIT 1";
		Hr_recruit_quachk rq = new Hr_recruit_quachk();
		JSONArray rst = rq.pool.opensql2json_O(sqlstr);
		Hr_employee emp = new Hr_employee();
		for (int i = 0; i < rst.size(); i++) {
			JSONObject jo = rst.getJSONObject(i);
			String id = jo.getString("id_number");
			String address = (jo.has("address")) ? jo.getString("address") : null;
			if ((id != null) && (!id.isEmpty())) {
				if ((address == null) || (address.isEmpty())) {
					sqlstr = "SELECT * FROM hr_employee WHERE id_number='" + id + "' ORDER BY createtime DESC";
					emp.clear();
					emp.findBySQL(sqlstr);
					if (!emp.isEmpty()) {
						jo.put("registeraddress", emp.registeraddress.getValue());
					}
				} else
					jo.put("registeraddress", address);

				String stx = id.substring(0, 2);
				sqlstr = "SELECT * FROM shwarea WHERE assistcode=" + stx;
				Shwarea area = new Shwarea();
				area.findBySQL(sqlstr);
				if (!area.isEmpty()) {
					jo.put("areaid", area.areaid.getValue());
					jo.put("areaname", area.areaname.getValue());
				}
			}
		}
		if (rst.size() > 0)
			return rst.getJSONObject(0).toString();
		else
			return "{}";
	}

	@ACOAction(eventname = "save", Authentication = true, notes = "保存招募单据")
	public String save() throws Exception {
		Hr_employee_linked emp = new Hr_employee_linked();
		Hr_recruit_form enty = new Hr_recruit_form();
		String psd = CSContext.getPostdata();
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
		emp.empstatid.setAsInt(0);// 招募待入职
		emp.hiredday.setValue(oj.getString("entrydate"));
		if (emp.degreetype.isEmpty())
			emp.degreetype.setValue(5);

		String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM `hr_balcklist` where id_number='" + emp.id_number.getValue() + "'";
		if (Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct")) != 0)
			throw new Exception("身份证号码【" + emp.id_number.getValue() + "】在黑名单");

		enty.fromjson(psd);

		String er_id = enty.er_id.getValue();
		sqlstr = "SELECT COUNT(*) ct FROM `hr_employee` WHERE (empstatid<>12 and empstatid<>0)  AND id_number='" + emp.id_number.getValue() + "'";
		if ((er_id != null) && (!er_id.isEmpty()))
			sqlstr = sqlstr + " and er_id<>" + er_id;
		if (Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct")) != 0)
			throw new Exception("身份证号码【" + emp.id_number.getValue() + "】存在在职/黑名单信息，请核实入职信息");

		sqlstr = "SELECT COUNT(*) ct FROM hr_employee e, hr_recruit_form  f WHERE e.er_id=f.er_id "
				+ "AND  e.empstatid=0 AND f.chkok=1 AND e.id_number='" + emp.id_number.getValue() + "'";
		if ((er_id != null) && (!er_id.isEmpty()))
			sqlstr = sqlstr + " and e.er_id<>" + er_id;
		if (Integer.valueOf(emp.pool.openSql2List(sqlstr).get(0).get("ct")) != 0)
			throw new Exception("身份证号码【" + emp.id_number.getValue() + "】存在待入职信息，请核实入职信息");

		if (!emp.employee_code.isEmpty() && (emp.employee_code.getValue().length() != 6)) {
			throw new Exception("工号必须为6位");
		}

		CDBConnection con = emp.pool.getCon(this);
		con.startTrans();
		try {
			if (emp.er_id.isEmpty())
				emp.employee_code.setValue(HrRecruitUtil.getNewEmpCode(con));
			else {
				Hr_employee te = new Hr_employee();
				te.findByID(emp.er_id.getValue());
				if (te.empstatid.getAsIntDefault(-1) != 0) {
					throw new Exception("已分配人员不可再次编辑");
				}
			}

			Hr_orgposition osp = new Hr_orgposition();
			osp.findByID(emp.ospid.getValue(), false);
			if (osp.isEmpty())
				throw new Exception("ID为【" + emp.ospid.getValue() + "】的机构职位不存在");
			emp.idpath.setValue(osp.idpath.getValue());
			emp.note.setValue(enty.remark.getValue());
			emp.save(con);
			enty.er_id.setValue(emp.er_id.getValue());
			enty.employee_code.setValue(emp.employee_code.getValue());
			enty.er_id.setValue(emp.er_id.getValue());
			enty.orgid.setValue(emp.orgid.getValue());
			enty.lv_num.setAsFloat(emp.lv_num.getAsFloat());
			enty.save(con, false);
			String rst = findbyid(con, enty.recruit_id.getValue());
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

	@ACOAction(eventname = "find", Authentication = true, notes = "查询招募单据")
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

			CJPALineData<Hr_recruit_formex> jpas = new CJPALineData<Hr_recruit_formex>(Hr_recruit_formex.class);
			Hr_recruit_form enty = new Hr_recruit_form();

			List<JSONParm> jps = CJSON.getParms(parms);
			String where = CjpaUtil.buildFindSqlByJsonParms(new Hr_recruit_formex(), jps);
			if (enty.cfield("idpath") != null)
				where = where + CSContext.getIdpathwhere();
			if ((sqlwhere != null) && (sqlwhere.length() > 0))
				where = where + " and " + sqlwhere + " ";
			if (enty.cfieldbycfieldname("stat") != null) {
				String sqlstat = "";
				if (Obj2Bl(edts.get("isedit")))
					sqlstat = sqlstat + " (stat=1) or";
				if (Obj2Bl(edts.get("issubmit"))) {
					sqlstat = sqlstat + " (stat>1 and stat<9) or";
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

			sqltr = "select * from (SELECT " + sctfields + " FROM hr_recruit_form h,hr_employee e WHERE h.er_id=e.er_id) tb where 1=1 " + where;

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
					return new CReport(sqltr, null).findReport();
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
		String sqlstr = "SELECT " + sctfields + " FROM hr_recruit_form h,hr_employee e WHERE h.er_id=e.er_id AND h.recruit_id=" + entry_id;
		Hr_recruit_form enty = new Hr_recruit_form();
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
		return o.toString();
	}

	@ACOAction(eventname = "del", Authentication = true, notes = "删除入职单据")
	public String del() throws Exception {
		String id = CSContext.getParms().get("id");
		if ((id == null) || id.isEmpty())
			throw new Exception("需要id参数");
		Hr_recruit_form enty = new Hr_recruit_form();
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
		String where = CjpaUtil.buildFindSqlByJsonParms(new Hr_recruit_formex(), jps);
		String idpw = CSContext.getIdpathwhere().replace("idpath", "h.idpath");
		String smax = parms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);
		String sqlstr = "SELECT " + sctfields + " FROM hr_recruit_form h,hr_employee e WHERE h.er_id=e.er_id " + idpw + where + " limit 0," + max;
		return he.pool.opensql2json(sqlstr);
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
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 1);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 1);
		Hr_employee emp = new Hr_employee();
		Hr_orgposition osp = new Hr_orgposition();
		Hr_recruit_form enty = new Hr_recruit_form();
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

	@ACOAction(eventname = "dodelduplicates", Authentication = true, notes = "删除重复的调动单")
	public String dodelduplicates() throws Exception {
		String sqlstr = " SELECT DISTINCT er_id FROM  hr_employee_transfer  GROUP BY  er_id,tranfcmpdate,tranftype2,tranftype1," +
				" tranftype3,odorgid,odlv_num,odhg_id,odospid,odattendtype,oldcalsalarytype,neworgid,newlv_num,newhg_id,newospid," +
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
						" SELECT tr.emptranf_id FROM hr_employee_transfer tr,  ( SELECT  MAX(emptranf_id) AS emptranf_id," +
						" er_id,tranfcmpdate,tranftype2,tranftype1,tranftype3,odorgid,odlv_num,odhg_id,odospid,odattendtype," +
						"oldcalsalarytype,neworgid,newlv_num,newhg_id,newospid,newattendtype,newhwc_namezl FROM hr_employee_transfer " +
						" GROUP BY  er_id,tranfcmpdate,tranftype2,tranftype1,tranftype3,odorgid,odlv_num,odhg_id,odospid,odattendtype," +
						"oldcalsalarytype, neworgid,newlv_num,newhg_id,newospid,newattendtype,newhwc_namezl HAVING COUNT(*) > 1) tb " +
						" WHERE tr.er_id=" + er_id + "  AND tr.er_id=tb.er_id AND tr.tranfcmpdate=tb.tranfcmpdate AND tr.tranftype2=tb.tranftype2 AND " +
						" tr.tranftype1=tb.tranftype1 AND tr.tranftype3=tb.tranftype3 AND tr.odorgid=tb.odorgid AND tr.odlv_num=tb.odlv_num AND" +
						" tr.odhg_id=tb.odhg_id AND tr.odospid=tb.odospid AND tr.odattendtype=tb.odattendtype AND tr.oldcalsalarytype=tb.oldcalsalarytype AND " +
						" tr.neworgid=tb.neworgid AND tr.newlv_num=tb.newlv_num AND tr.newhg_id=tb.newhg_id AND tr.newospid=tb.newospid AND" +
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

	@ACOAction(eventname = "find_rec_orgs", Authentication = true, notes = "查询招募可用机构")
	public String find_rec_orgs() throws Exception {
		String sqlstr = "SELECT * FROM shworg WHERE usable=1 AND attribute2=1 ORDER BY extorgname";
		String[] ignParms = {};
		return new CReport(sqlstr, null).findReport(ignParms, "");// 不采用IDPATH
	}

	@ACOAction(eventname = "printcard", Authentication = true, notes = "打印厂牌")
	public void printcard() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String modfilename = CorUtil.hashMap2Str(parms, "modfilename", "需要参数modfilename");
		int tp = Integer.valueOf(CorUtil.hashMap2Str(parms, "tp", "需要参数tp"));
		// 1 print 2 export
		String jpaids = CorUtil.hashMap2Str(parms, "jpaids", "需要参数jpaids");

		String fields = CorUtil.hashMap2Str(parms, "fields");
		JSONObject flds = ((fields != null) && (!fields.isEmpty())) ? JSONObject.fromObject(fields) : null;

		HttpServletResponse rsp = CSContext.getResponse();
		rsp.setContentType("application/x-xls");
		rsp.setHeader("content-disposition", "attachment; filename=" + new String(modfilename.getBytes("GBK"), "ISO_8859_1"));
		OutputStream os = rsp.getOutputStream();
		try {
			CJPALineData<CJPABase> jpas = new CJPALineData<CJPABase>(Hr_employee.class);
			String sqlstr = "SELECT * FROM hr_employee e WHERE EXISTS( " +
					"SELECT 1 FROM hr_recruit_form r WHERE r.er_id=e.er_id AND r.recruit_id IN (" + jpaids + ") " +
					")";
			jpas.findDataBySQL(sqlstr);
			String fsep = System.getProperty("file.separator");
			String filename = ConstsSw.excelModelPath + Hr_recruit_form.class.getSimpleName() + fsep + modfilename;
			CExcelEx.expJPASByModelFullFileName(jpas, filename, os, tp, flds);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			os.flush();
			os.close();
		}
	}

	// @ACOAction(eventname = "get_transport", Authentication = true, notes = "获取输送机构")
	// public String get_transport() throws Exception {
	// HashMap<String, String> parms = CSContext.getParms();
	// String id = CorUtil.hashMap2Str(parms, "id", "需要参数id");
	// Hr_recruit_transport rt = new Hr_recruit_transport();
	// rt.findByID(id);
	// if (rt.isEmpty())
	// throw new Exception("ID为【" + id + "】的输送机构不存在");
	// return rt.toString();
	// }

}
