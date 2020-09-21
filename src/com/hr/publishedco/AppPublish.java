package com.hr.publishedco;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.genco.COCSCommon;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shworguser;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.hr.attd.ctr.CacalKQData;
import com.hr.attd.ctr.HrkqUtil;
import com.hr.attd.entity.Hrkq_business_trip;
import com.hr.attd.entity.Hrkq_holidayapp;
import com.hr.attd.entity.Hrkq_holidaytype;
import com.hr.attd.entity.Hrkq_ondutyline;
import com.hr.attd.entity.Hrkq_overtime_list;
import com.hr.attd.entity.Hrkq_resign;
import com.hr.attd.entity.Hrkq_special_holday;
import com.hr.attd.entity.Hrkq_wkoff;
import com.hr.attd.entity.Hrkq_wkoffsourse;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.HRUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ACO(coname = "hrkq.app")
public class AppPublish {

//	public static void main(String[] args) throws Exception {
//		System.out.println(new CommonResult<>(200, new Hrkq_holidayapp(), "message").toString());
//	}

	@ACOAction(eventname = "holiday_new", Authentication = false, notes = "创建请假表单并自动提交生效")
	public String holiday_new() throws Exception {

		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		Hrkq_holidayapp happ = new Hrkq_holidayapp();// 创建请假表单实体
		// 初始化参数
		String entid = null;
		String userid = null;
		String userids = CorUtil.hashMap2Str(urlparms, "userids");
		String[] ckuserids = ((userids != null) && (userids.length() > 0)) ? userids.split(",") : null;
		Shwuser user = new Shwuser();
		happ.fromjson(CSContext.getPostdata());// 从前端JSON获取数据
		String sqlstr = "SELECT * FROM shwuser WHERE username=?";
		PraperedSql psql = new PraperedSql();
		psql.setSqlstr(sqlstr);
		psql.getParms().add(new PraperedValue(java.sql.Types.VARCHAR, happ.employee_code.getValue().trim()));// UserName.trim().toUpperCase()
		user.findByPSQL(psql);
		if (user.isEmpty())
			// throw new Exception("用户名不存在!");
			return CommonResult.failed("用户不存在").toString();
		if (user.actived.getAsIntDefault(0) != 1)
			// throw new Exception("用户已禁用!");
			return CommonResult.failed("用户已禁用").toString();
		userid = user.getid();
		CJPALineData<Shworguser> ous = new CJPALineData<Shworguser>(Shworguser.class);
		ous.setPool(user.pool);
		String sqlstrfinduser = "select * from shworguser where userid=" + user.userid.getValue();
		ous.findDataBySQL(sqlstrfinduser, true, true);
		for (CJPABase oub : ous) {
			Shworguser ou = (Shworguser) oub;
			if (ou.isdefault.getAsIntDefault(0) == 1) {
				Shworg org = new Shworg();
				org.findByID(ou.orgid.getValue());
				entid = org.entid.getValue();
				break;
			}
		}

		if (happ.haid.getValue().isEmpty() || happ.haid.getValue() != null) {
			if (happ.employee_code.isEmpty()) {
				return CommonResult.failed("工号不能为空").toString();
			}
			if (happ.hdays.isEmpty()) {
				return CommonResult.failed("请假天数不能为空").toString();
			}
			if (happ.timebg.isEmpty()) {
				return CommonResult.failed("请假开始时间不能为空").toString();
			}
			if (happ.timeed.isEmpty()) {
				return CommonResult.failed("请假结束时间不能为空").toString();
			}
			if (happ.timebg.getValue().compareTo(happ.timeed.getValue()) > 0) {
				return CommonResult.failed("结束时间必须大于开始时间").toString();
			}
			if (happ.htid.isEmpty()) {
				return CommonResult.failed("请假类型不能为空").toString();
			}
			if (happ.htreason.isEmpty()) {
				return CommonResult.failed("请输入请假原因").toString();
			}
			if (happ.hrkq_holidayapp_months.isEmpty()) {
				return CommonResult.failed("hrkq_holidayapp_months不能为空").toString();
			}
			// 备注
			if (happ.remark.isEmpty()) {
				happ.remark.setValue("HRMS移动提单请假单信息");
			}

			Hr_employee emp = new Hr_employee();// 创建人事档案实体
			String sqlstr2 = "select * from hr_employee where employee_code='" + happ.employee_code.getValue() + "'";
			emp.findBySQL(sqlstr2);// 查询人事档案
			if (emp.isEmpty())
				return CommonResult.failed("查询不到工号为：" + happ.employee_code.getValue() + "的人事资料").toString();
			// new Exception("ID为【" + happ.er_id.getValue() + "】的人事资料不存在");
			Hrkq_holidaytype ht = new Hrkq_holidaytype();// 创建假期类型实体
			ht.findByID(happ.htid.getValue());
			if (ht.isEmpty())
				return CommonResult.failed("查询不到ID为：" + happ.htid.getValue() + "的假期类型").toString();
			// new Exception("ID为【" + happ.htid.getValue() + "】的假期类型不存在");

			happ.er_id.setValue(emp.er_id.getValue()); // 人事ID
			happ.employee_code.setValue(emp.employee_code.getValue()); // 工号
			happ.employee_name.setValue(emp.employee_name.getValue()); // 姓名
			happ.orgid.setValue(emp.orgid.getValue()); // 部门ID
			happ.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
			happ.orgname.setValue(emp.orgname.getValue()); // 部门
			happ.ospid.setValue(emp.ospid.getValue()); // 职位ID
			happ.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
			happ.sp_name.setValue(emp.sp_name.getValue()); // 职位
			happ.lv_num.setValue(emp.lv_num.getValue()); // 职级
			happ.hdaystrue.setValue(happ.hdays.getValue()); // 实际天数 如果没销假 则==销假天数
			happ.timeedtrue.setValue(happ.timeed.getValue()); // 实际截止时间 如果有销假 按销假时间 否则按申请截止时间 yyyy-MM-dd hh:mm
			happ.htid.setValue(ht.htid.getValue()); // 类型ID
			happ.htname.setValue(ht.htname.getValue()); // 假期类型
			happ.bhtype.setValue(ht.bhtype.getValue()); // 假期类别
			happ.htconfirm.setValue("1"); // 假期确认 //符合规则 违反规则 空
			happ.viodeal.setValue(null); // 违规处理 //事假 旷工 空
			happ.timebk.setValue(null); // 销假时间 yyyy-MM-dd hh:mm
			happ.btconfirm.setValue(null); // 销假确认
			happ.iswfagent.setValue("1"); // 启用流程代理
			happ.stat.setValue("1"); // 表单状态
			happ.idpath.setValue(emp.idpath.getValue()); // idpath
			happ.createtime.setAsDatetime(new Date()); // 创建时间
			happ.updatetime.setAsDatetime(new Date()); // 修改时间

			// 获取机构人事层级
			happ.orghrlev.setValue(HRUtil.getOrgHrLev(emp.orgid.getValue()));
			happ.creator.setValue(emp.employee_code.getValue());// 创建人
			happ.updator.setValue(emp.employee_code.getValue());// 修改人
			happ.entid.setValue("1");// 制单状态

			CDBConnection con = happ.pool.getCon(this); // 获取数据库连接
			con.startTrans(); // 开始事务
			try {
				happ.save(con);// 保存请假表单
				con.submit();// 提交数据库事务
			} catch (Exception e) {
				con.rollback();// 回滚事务
				throw e;
			} finally {
				con.close();
			}
		}
		try {
			return COCSCommon.createWFForMobile("com.hr.attd.entity.Hrkq_holidayapp", happ.haid.getValue(), userid, entid, ckuserids);
		} catch (Exception e) {
			// TODO: handle exception
			return CommonResult.failed(e.getMessage()).toString();
		}

	}

	@ACOAction(eventname = "business_trip_new", Authentication = false, notes = "创建出差单并自动提交生效")
	public String business_trip_new() throws Exception {

		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		Hrkq_business_trip happ_btrip = new Hrkq_business_trip();// 创建出差单实体
		// 初始化参数
		String entid = null;
		String userid = null;
		String userids = CorUtil.hashMap2Str(urlparms, "userids");
		String[] ckuserids = ((userids != null) && (userids.length() > 0)) ? userids.split(",") : null;
		Shwuser user = new Shwuser();
		happ_btrip.fromjson(CSContext.getPostdata());// 从前端JSON获取数据
		String sqlstr = "SELECT * FROM shwuser WHERE username=?";
		PraperedSql psql = new PraperedSql();
		psql.setSqlstr(sqlstr);
		psql.getParms().add(new PraperedValue(java.sql.Types.VARCHAR, happ_btrip.employee_code.getValue().trim()));// UserName.trim().toUpperCase()
		user.findByPSQL(psql);
		if (user.isEmpty())
			// throw new Exception("用户名不存在!");
			return CommonResult.failed("用户不存在").toString();
		if (user.actived.getAsIntDefault(0) != 1)
			// throw new Exception("用户已禁用!");
			return CommonResult.failed("用户已禁用").toString();
		userid = user.getid();
		CJPALineData<Shworguser> ous = new CJPALineData<Shworguser>(Shworguser.class);
		ous.setPool(user.pool);
		String sqlstrfinduser = "select * from shworguser where userid=" + user.userid.getValue();
		ous.findDataBySQL(sqlstrfinduser, true, true);
		for (CJPABase oub : ous) {
			Shworguser ou = (Shworguser) oub;
			if (ou.isdefault.getAsIntDefault(0) == 1) {
				Shworg org = new Shworg();
				org.findByID(ou.orgid.getValue());
				entid = org.entid.getValue();
				break;
			}
		}
		// 初始化参数结束

		if (happ_btrip.bta_id.getValue().isEmpty() || happ_btrip.bta_id.getValue() == null) {
			if (happ_btrip.employee_code.isEmpty()) {
				return CommonResult.failed("工号不能为空").toString();
			}
			if (happ_btrip.tripdays.isEmpty()) {
				return CommonResult.failed("出差天数不能为空").toString();
			}
			if (happ_btrip.begin_date.isEmpty()) {
				return CommonResult.failed("出差开始时间不能为空").toString();
			}
			if (happ_btrip.end_date.isEmpty()) {
				return CommonResult.failed("出差结束时间不能为空").toString();
			}
			if (happ_btrip.begin_date.getValue().compareTo(happ_btrip.end_date.getValue()) > 0) {
				return CommonResult.failed("结束时间必须大于开始时间").toString();
			}
			if (happ_btrip.trip_type.isEmpty()) {
				return CommonResult.failed("出差类型不能为空").toString();
			}
			if (happ_btrip.destination.isEmpty()) {
				return CommonResult.failed("出差地点不能为空").toString();
			}
			if (happ_btrip.tripreason.isEmpty()) {
				return CommonResult.failed("出差事由不能为空").toString();
			}
			if (happ_btrip.emplev.isEmpty()) {
				return CommonResult.failed("人事层级不能为空").toString();
			}
			// 备注
			if (happ_btrip.remark.isEmpty()) {
				happ_btrip.remark.setValue("HRMS移动提单出差单");
			}

			Hr_employee emp = new Hr_employee();// 创建人事档案实体
			String sqlstr2 = "select * from hr_employee where employee_code='" + happ_btrip.employee_code.getValue() + "'";
			emp.findBySQL(sqlstr2);// 查询人事档案
			if (emp.isEmpty())
				// new Exception("ID为【" + happ_btrip.er_id.getValue() + "】的人事资料不存在");
				return CommonResult.failed("查询不到工号为：" + happ_btrip.employee_code.getValue() + "的人事资料").toString();

			if ((happ_btrip.trip_type.getAsIntDefault(0) != 1) && (happ_btrip.trip_type.getAsIntDefault(0) != 2))
				// new Exception("ID为【" + happ_btrip.trip_type + "】的出差类型不存在");
				return CommonResult.failed("查询不到ID为：" + happ_btrip.trip_type.getValue() + "的出差类型").toString();
			happ_btrip.er_id.setValue(emp.er_id.getValue()); // 人事ID
			happ_btrip.employee_code.setValue(emp.employee_code.getValue()); // 工号
			happ_btrip.employee_name.setValue(emp.employee_name.getValue()); // 姓名
			happ_btrip.orgid.setValue(emp.orgid.getValue()); // 部门ID
			happ_btrip.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
			happ_btrip.orgname.setValue(emp.orgname.getValue()); // 部门
			happ_btrip.ospid.setValue(emp.ospid.getValue()); // 职位ID
			happ_btrip.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
			happ_btrip.sp_name.setValue(emp.sp_name.getValue()); // 职位
			happ_btrip.lv_num.setValue(emp.lv_num.getValue()); // 职级
			happ_btrip.lv_id.setValue(emp.lv_id.getValue());// 职级ID

			happ_btrip.iswfagent.setValue("1"); // 启用流程代理
			happ_btrip.stat.setValue("1"); // 表单状态
			happ_btrip.idpath.setValue(emp.idpath.getValue()); // idpath
			// happ_btrip.creator.setValue("inteface"); // 创建人
			happ_btrip.creator.setValue(emp.employee_code.getValue());
			happ_btrip.updator.setValue(emp.employee_code.getValue());// 更新人
			happ_btrip.createtime.setAsDatetime(new Date()); // 创建时间
			happ_btrip.updatetime.setAsDatetime(new Date()); // 修改时间
			// 获取机构人事层级
			int orghrlev = HRUtil.getOrgHrLev(emp.orgid.getValue());
			happ_btrip.orghrlev.setValue(orghrlev);

			happ_btrip.orghrlev.setValue(HRUtil.getOrgHrLev(emp.orgid.getValue()));// 机构人事层级
			happ_btrip.entid.setValue("1");// 制单状态

			CDBConnection con = happ_btrip.pool.getCon(this); // 获取数据库连接
			con.startTrans(); // 开始事务
			try {
				happ_btrip.save(con);// 保存出差表单
				con.submit();// 提交数据库事务
			} catch (Exception e) {
				con.rollback();// 回滚事务
				throw e;
			} finally {
				con.close();
			}
		}
		try {
			return COCSCommon.createWFForMobile("com.hr.attd.entity.Hrkq_business_trip", happ_btrip.bta_id.getValue(), userid, entid, ckuserids);
		} catch (Exception e) {
			// return "{\"jpaid\":"+happ_btrip.getid()+"}";
			// System.out.println(e.getMessage());
			return CommonResult.failed(e.getMessage()).toString();
		}
		// return CommonResult.success("\"jpaid\":" + happ_btrip.getid()).toString();

	}

	@ACOAction(eventname = "wkoff_new", Authentication = false, notes = "创建调休表单并自动提交生效")
	public String wkoff_new() throws Exception {

		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		Hrkq_wkoff happwkoff = new Hrkq_wkoff();// 创建调休表单实体
		happwkoff.fromjson(CSContext.getPostdata());// 从前端JSON获取数据

		// 初始化参数
		String entid = null;
		String userid = null;
		String userids = CorUtil.hashMap2Str(urlparms, "userids");
		String[] ckuserids = ((userids != null) && (userids.length() > 0)) ? userids.split(",") : null;
		Shwuser user = new Shwuser();
		String sqlstr = "SELECT * FROM shwuser WHERE username=?";
		PraperedSql psql = new PraperedSql();
		psql.setSqlstr(sqlstr);
		psql.getParms().add(new PraperedValue(java.sql.Types.VARCHAR, happwkoff.employee_code.getValue().trim()));// UserName.trim().toUpperCase()
		user.findByPSQL(psql);
		if (user.isEmpty())
			// throw new Exception("用户名不存在!");
			return CommonResult.failed("用户不存在").toString();
		if (user.actived.getAsIntDefault(0) != 1)
			// throw new Exception("用户已禁用!");
			return CommonResult.failed("用户已禁用").toString();
		userid = user.getid();
		CJPALineData<Shworguser> ous = new CJPALineData<Shworguser>(Shworguser.class);
		ous.setPool(user.pool);
		String sqlstrfinduser = "select * from shworguser where userid=" + user.userid.getValue();
		ous.findDataBySQL(sqlstrfinduser, true, true);
		for (CJPABase oub : ous) {
			Shworguser ou = (Shworguser) oub;
			if (ou.isdefault.getAsIntDefault(0) == 1) {
				Shworg org = new Shworg();
				org.findByID(ou.orgid.getValue());
				entid = org.entid.getValue();
				break;
			}
		}

		if (happwkoff.woid.getValue().isEmpty() || happwkoff.woid.getValue() == null) {
			if (happwkoff.employee_code.isEmpty()) {
				return CommonResult.failed("工号不能为空").toString();
				// new Exception("调休表单字段【employee_code】不能为空");
			}
			if (happwkoff.begin_date.isEmpty()) {
				return CommonResult.failed("调休开始时间不能为空").toString();
				// new Exception("调休表单字段【begin_date】不能为空");
			}
			if (happwkoff.end_date.isEmpty()) {
				return CommonResult.failed("调休结束时间不能为空").toString();
				// new Exception("调休表单字段【end_date】不能为空");
			}
			if (happwkoff.wodays.isEmpty()) {
				return CommonResult.failed("调休天数不能为空").toString();
				// new Exception("调休表单字段【wodays】不能为空");
			}
			if (happwkoff.reason.isEmpty()) {
				return CommonResult.failed("调休原因不能为空").toString();
				// new Exception("调休表单字段【reason】不能为空");
			}
			if (happwkoff.emplev.isEmpty()) {
				return CommonResult.failed("人事层级不能为空").toString();
			}
			// 备注
			if (happwkoff.remark.isEmpty()) {
				happwkoff.remark.setValue("HRMS移动提单调休单");
			}
//			if (happwkoff.hrkq_wkoffsourses.isEmpty()) {
//				return CommonResult.failed("调休源不能为空").toString();
//			}
			Hr_employee emp = new Hr_employee();// 创建人事档案实体
			String sqlstr2 = "select * from hr_employee where employee_code='" + happwkoff.employee_code.getValue() + "'";
			emp.findBySQL(sqlstr2);// 查询人事档案
			if (emp.isEmpty())
				// new Exception("ID为【" + happwkoff.employee_code.getValue() + "】的人事资料不存在");
				return CommonResult.failed("查询不到工号为：" + happwkoff.employee_code.getValue() + "的人事资料").toString();

			happwkoff.er_id.setValue(emp.er_id.getValue()); // 人事ID
			happwkoff.employee_code.setValue(emp.employee_code.getValue()); // 工号
			happwkoff.employee_name.setValue(emp.employee_name.getValue()); // 姓名
			happwkoff.orgid.setValue(emp.orgid.getValue()); // 部门ID
			happwkoff.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
			happwkoff.orgname.setValue(emp.orgname.getValue()); // 部门
			happwkoff.sp_name.setValue(emp.sp_name.getValue()); // 职位
			happwkoff.lv_num.setValue(emp.lv_num.getValue()); // 职级
			happwkoff.iswfagent.setValue("1"); // 启用流程代理
			happwkoff.stat.setValue("1"); // 表单状态
			happwkoff.idpath.setValue(emp.idpath.getValue()); // idpath
			happwkoff.entid.setValue("1"); // entid
			happwkoff.creator.setValue(emp.employee_code.getValue());// 创建人
			happwkoff.createtime.setAsDatetime(new Date()); // 创建时间
			happwkoff.updator.setValue(emp.employee_code.getValue());// 修改人
			happwkoff.updatetime.setAsDatetime(new Date()); // 修改时间
			// 获取人事机构层级
			if (happwkoff.orghrlev.isEmpty()) {
				happwkoff.orghrlev.setValue(HRUtil.getOrgHrLev(happwkoff.orgid.getValue()));
			}

			// 获取调休源
			String str = "[{\"parmname\":\"er_id\",\"reloper\":\"=\",\"parmvalue\":\"" + emp.er_id.getValue() + "\"},{\"parmname\":\"canuses\",\"reloper\":\"=\",\"parmvalue\":1}]";
			urlparms.put("parms", str);
			CSContext.setParms(urlparms);
			String idpathwhere = " and (( idpath like '" + emp.idpath.getValue() + "%') )";
			String[] notnull = {};

			String sqlstr3 = "SELECT * FROM( "
					+ "SELECT e.hwc_namezl,e.sp_name,b.*, IF(b.valdate>curdate(),2,1) isexpire,IF(b.usedlbtime<b.alllbtime,2,1) usup,IF((b.valdate>curdate()) AND (b.usedlbtime<b.alllbtime),1,2) canuses "
					+ "FROM hrkq_leave_blance b,hr_employee e where e.er_id=b.er_id " + idpathwhere.replace("idpath", "b.idpath") + " ) tb ";
			Hrkq_overtime_list ol = new Hrkq_overtime_list();
			Hrkq_business_trip bt = new Hrkq_business_trip();
			Hrkq_ondutyline dl = new Hrkq_ondutyline();
			Hrkq_special_holday sh = new Hrkq_special_holday();

			String[] ignParms = new String[] {};
			boolean paging = true;
			String idpw = idpathwhere;

			JSONObject rst = new CReport(HRUtil.getReadPool(), sqlstr3, " valdate desc ", notnull).findReport2JSON_O(ignParms, paging, idpw);
			JSONArray rows = rst.getJSONArray("rows");
			for (int i = 0; i < rows.size(); i++) {
				JSONObject row = rows.getJSONObject(i);
				int stype = row.getInt("stype");// 源类型 1 年假 2 加班 3 值班 4出差 5特殊
				int sid = row.getInt("sid");
				if ((stype == 2) && (sid != 0)) {
					ol.clear();
					ol.findByID(String.valueOf(sid));
					if (!ol.isEmpty()) {
						row.put("bgtime", ol.bgtime.getValue());
						row.put("edtime", ol.edtime.getValue());
					}
				}

				if ((stype == 3) && (sid != 0)) {
					dl.clear();
					dl.findByID(String.valueOf(sid));
					if (!dl.isEmpty()) {
						row.put("bgtime", dl.begin_date.getValue());
						row.put("edtime", dl.end_date.getValue());
					}
				}

				if ((stype == 4) && (sid != 0)) {
					bt.clear();
					bt.findByID(String.valueOf(sid));
					if (!bt.isEmpty()) {
						row.put("bgtime", bt.begin_date.getValue());
						row.put("edtime", bt.end_date.getValue());
					}
				}

				if ((stype == 5) && (sid != 0)) {
					sh.clear();
					sqlstr = "SELECT h.* FROM hrkq_special_holday h ,hrkq_special_holdayline l " + " WHERE h.sphid=l.sphid AND l.sphlid=" + sid;
					sh.findBySQL(sqlstr);
					if (!sh.isEmpty()) {
						row.put("bgtime", sh.sphdate.getValue());
					}
				}
			}

			if (rows.size() <= 0) {
				return CommonResult.failed("查询不到调休源").toString();
			}
			// 自动分配调休源
			// System.out.println("wodays:" + happwkoff.wodays.getValue().toString());
			// Hrkq_wkoffsourse hrkq_wkoffsourse = new Hrkq_wkoffsourse(); //调休源实体
			CJPALineData<Hrkq_wkoffsourse> list = new CJPALineData<Hrkq_wkoffsourse>(Hrkq_wkoffsourse.class);
			float sxwodays = Float.parseFloat(happwkoff.wodays.getValue()) * 8; // 调休小时
			for (int i = rows.size() - 1; i >= 0; i--) {
				Hrkq_wkoffsourse w = new Hrkq_wkoffsourse();
				JSONObject row = rows.getJSONObject(i);
				System.out.println(row);
				w.lbid.setValue(row.get("lbid").toString());
				w.lbname.setValue(row.get("lbname").toString());
				w.stype.setValue(row.get("stype").toString());
				w.sccode.setValue("0");
				if (row.get("sccode") != null) {
					w.sccode.setValue(row.get("sccode").toString());
				}
				w.alllbtime.setValue(row.get("alllbtime").toString());
				w.usedlbtime.setValue(row.get("usedlbtime").toString());
				w.valdate.setValue(row.get("valdate").toString());
				if (row.get("note") != null) {
					w.note.setValue(row.get("note").toString());
				}
				System.out.println(w);

				float canxdays = Float.parseFloat(w.alllbtime.getValue()) - Float.parseFloat(w.usedlbtime.getValue());
				if (canxdays > sxwodays) {
					w.wotime.setValue(sxwodays);
					sxwodays = 0;
				} else {
					w.wotime.setValue(canxdays);
					sxwodays = sxwodays - canxdays;
				}
				list.add(w);
				if (sxwodays == 0)
					break;
			}
			if (sxwodays > 0) {
				return CommonResult.failed("可用调休时间不足").toString();
			}

			// 设置调休源
//			System.out.println(list);
			
//			happwkoff.fromjson(happwkoff.tojson());
			System.out.println("____________");
			System.out.print(happwkoff.tojson());
//			CJPALineData<Hrkq_wkoffsourse> wss = happwkoff.hrkq_wkoffsourses;
//			System.out.println("wss:" + wss);
//			// System.out.println("source:" + happwkoff.hrkq_wkoffsourses);
//			for (CJPABase jpa1 : wss) {
//				Hrkq_wkoffsourse ws = (Hrkq_wkoffsourse) jpa1;
//				ws.lbid.setValue(ws.lbid.getValue());
//				ws.lbname.setValue(ws.lbname.getValue());
//				ws.stype.setValue(ws.stype.getValue());
//				ws.sccode.setValue(ws.sccode.getValue());
//				ws.alllbtime.setValue(ws.alllbtime.getValue());
//				ws.usedlbtime.setValue(ws.usedlbtime.getValue());
//				ws.valdate.setValue(ws.valdate.getValue());
//				ws.note.setValue(ws.note.getValue());
//				ws.wotime.setValue(ws.wotime.getValue());
//				System.out.println("ws: " + ws);
//			}

			System.out.println("happwkoff:" + happwkoff);
			System.out.println(happwkoff.hrkq_wkoffsourses);
			CDBConnection con = happwkoff.pool.getCon(happwkoff); // 获取数据库连接
			con.startTrans(); // 开始事务
			try {
				happwkoff.save(con);// 保存调休表单
//				for (CJPABase item : list) {
//					Hrkq_wkoffsourse s = (Hrkq_wkoffsourse) item;
//					s.woid.setValue(happwkoff.woid.getValue());
//					s.save();
//				}
				con.submit();// 提交数据库事务

			} catch (Exception e) {
				con.rollback();// 回滚事务
				throw e;
			} finally {
				con.close();
			}
		}
		// System.out.print(happwkoff.getHrkq_wkoffsourses().tojson());
		try {
			return COCSCommon.createWFForMobile("com.hr.attd.entity.Hrkq_wkoff", happwkoff.woid.getValue(), userid, entid, ckuserids);
		} catch (Exception e) {
			return CommonResult.failed(e.getMessage()).toString();
		}

	}

	@ACOAction(eventname = "resign_new", Authentication = false, notes = "创建补签表单并自动提交生效")
	public String resign_new() throws Exception {

		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		Hrkq_resign happresign = new Hrkq_resign();// 创建补签表单实体
		happresign.fromjson(CSContext.getPostdata());// 从前端JSON获取数据

		// 初始化参数
		String entid = null;
		String userid = null;
		String userids = CorUtil.hashMap2Str(urlparms, "userids");
		String[] ckuserids = ((userids != null) && (userids.length() > 0)) ? userids.split(",") : null;
		Shwuser user = new Shwuser();
		String sqlstr = "SELECT * FROM shwuser WHERE username=?";
		PraperedSql psql = new PraperedSql();
		psql.setSqlstr(sqlstr);
		psql.getParms().add(new PraperedValue(java.sql.Types.VARCHAR, happresign.employee_code.getValue().trim()));// UserName.trim().toUpperCase()
		user.findByPSQL(psql);
		if (user.isEmpty())
			// throw new Exception("用户名不存在!");
			return CommonResult.failed("用户不存在").toString();
		if (user.actived.getAsIntDefault(0) != 1)
			// throw new Exception("用户已禁用!");
			return CommonResult.failed("用户已禁用").toString();
		userid = user.getid();
		CJPALineData<Shworguser> ous = new CJPALineData<Shworguser>(Shworguser.class);
		ous.setPool(user.pool);
		String sqlstrfinduser = "select * from shworguser where userid=" + user.userid.getValue();
		ous.findDataBySQL(sqlstrfinduser, true, true);
		for (CJPABase oub : ous) {
			Shworguser ou = (Shworguser) oub;
			if (ou.isdefault.getAsIntDefault(0) == 1) {
				Shworg org = new Shworg();
				org.findByID(ou.orgid.getValue());
				entid = org.entid.getValue();
				break;
			}
		}

		if (happresign.resid.getValue() == null || happresign.resid.getValue().isEmpty()) {
			if (happresign.employee_code.isEmpty()) {
				return CommonResult.failed("工号不能为空").toString();
			}
			if (happresign.emplev.isEmpty()) {
				return CommonResult.failed("人事层级不能为空").toString();
			}
			if (happresign.hrkq_resignlines.isEmpty()) {
				return CommonResult.failed("补签明细不能为空").toString();
			}
			if (happresign.remark.isEmpty()) {
				happresign.remark.setValue("HRMS移动提单补签单");
			}
			Hr_employee emp = new Hr_employee();// 创建人事档案实体
			String sqlstr2 = "select * from hr_employee where employee_code='" + happresign.employee_code.getValue() + "'";
			emp.findBySQL(sqlstr2);// 查询人事档案
			if (emp.isEmpty())
				new Exception("工号为【" + happresign.employee_code.getValue() + "】的人事资料不存在");
			happresign.er_id.setValue(emp.er_id.getValue()); // 人事ID
			happresign.employee_code.setValue(emp.employee_code.getValue()); // 工号
			happresign.employee_name.setValue(emp.employee_name.getValue()); // 姓名
			happresign.orgid.setValue(emp.orgid.getValue()); // 部门ID
			happresign.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
			happresign.orgname.setValue(emp.orgname.getValue()); // 部门
			happresign.ospid.setValue(emp.ospid.getValue()); // 职位ID
			happresign.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
			happresign.sp_name.setValue(emp.sp_name.getValue()); // 职位
			happresign.lv_num.setValue(emp.lv_num.getValue()); // 职级
			happresign.stat.setValue("1"); // 表单状态
			happresign.idpath.setValue(emp.idpath.getValue()); // idpath
			happresign.entid.setValue("1"); // entid
			happresign.creator.setValue(emp.employee_code.getValue());// 创建人
			happresign.createtime.setAsDatetime(new Date()); // 创建时间
			happresign.updator.setValue(emp.employee_code.getValue());// 修改人
			happresign.updatetime.setAsDatetime(new Date());// 修改时间
			happresign.orghrlev.setValue(HRUtil.getOrgHrLev(emp.orgid.getValue()));
			// happresign.emplev.setValue("0");
			CDBConnection con = happresign.pool.getCon(this); // 获取数据库连接
			con.startTrans(); // 开始事务
			try {
				happresign.save(con);// 保存补签表单
				con.submit();// 提交数据库事务
			} catch (Exception e) {
				con.rollback();// 回滚事务
				throw e;
			} finally {
				con.close();
			}
		}
		try {
			return COCSCommon.createWFForMobile("com.hr.attd.entity.Hrkq_resign", happresign.resid.getValue(), userid, entid, ckuserids);
		} catch (Exception e) {
			return CommonResult.failed(e.getMessage()).toString();
		}

	}

	@ACOAction(eventname = "calcDateDiffMonth", Authentication = false, notes = "获取日期相差月数")
	public String calcDateDiffMonth() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		Date bgtime = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "bgtime", "需要参数bgtime"));
		Date edtime = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "edtime", "需要参数edtime"));
		if (bgtime.getTime() >= edtime.getTime())
			throw new Exception("开始时间大于截止时间");

		JSONObject rst = new JSONObject();
		rst.put("rst", Systemdate.getBetweenMonth(bgtime, edtime));
		// return rst.toString();
		return CommonResult.success(rst.get("rst")).toString();
	}

	@ACOAction(eventname = "calcDateDiffDay", Authentication = false, notes = "获取日期相差天数,最小单位0.5")
	public String calcDateDiffDay() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		Date bgtime = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "bgtime", "需要参数bgtime"));
		Date edtime = Systemdate.getDateByStr(CorUtil.hashMap2Str(parms, "edtime", "需要参数edtime"));
		if (bgtime.getTime() >= edtime.getTime()) {
			return CommonResult.failed("开始时间大于截至时间").toString();
		}
		float hours = CacalKQData.calcDateDiffHH(bgtime, edtime);
		float days = (float) hours / 8;// 按每天8小时计算
		if ((days * 10 % 5) != 0) {
			return CommonResult.failed("算法错误，最小单位为半天").toString();
		}
		return CommonResult.success(days).toString();
	}

	@ACOAction(eventname = "getEmployeeInfo", Authentication = false, notes = "获取人事信息")
	public String getEmployeeInfo() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String emp_code = parms.get("employee_code");
		if ("".equals(emp_code) || emp_code == null) {
			return CommonResult.failed("没有工号").toString();
		}
		Hr_employee emp = new Hr_employee();// 创建人事档案实体
		String sqlstr = "select * from hr_employee where employee_code='" + emp_code + "'";
		emp.findBySQL(sqlstr);// 查询人事档案
		if (emp.isEmpty()) {
			// new Exception("ID为【" + happ_btrip.er_id.getValue() + "】的人事资料不存在");
			return CommonResult.failed("查询不到工号为：" + emp_code + "的人事资料").toString();
		}
		// 过滤信息
		emp.id_number.setValue("");
		emp.birthday.setValue("");
		emp.nativeplace.setValue("");
		emp.major.setValue("");
		emp.registeraddress.setValue("");
		emp.cellphone.setValue("");
		emp.telphone.setValue("");
		emp.sp_name.setValue("");
		emp.hwc_namezq.setValue("");
		emp.hwc_namezz.setValue("");
		emp.sign_org.setValue("");
		emp.urgencycontact.setValue("");
		emp.emnature.setValue("");
		return CommonResult.success(emp).toString();

	}

	@ACOAction(eventname = "findOrgHrlev", Authentication = false, notes = "获取人事机构层级")
	public String findOrgHrlev() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String orgid = parms.get("orgid");
		if ("".equals(orgid) || orgid == null) {
			return CommonResult.failed("没有部门ID").toString();
		}
		int orghrlev = HRUtil.getOrgHrLev(orgid);
		return CommonResult.success(orghrlev).toString();

	}

	@ACOAction(eventname = "getAttParm", Authentication = false, notes = "获取考勤参数")
	public String getAttParm() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String parmcode = CorUtil.hashMap2Str(parms, "parmcode", "需要参数parmcode");
		JSONObject rst = new JSONObject();
		rst.put("value", HrkqUtil.getParmValueErr(parmcode));
		return CommonResult.success(rst.get("value")).toString();
	}

	@ACOAction(eventname = "findleavlblc", Authentication = false, notes = "浏览可休假列表")
	public String getleavlblc() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();

		HashMap<String, String> parms = CSContext.getParms();
		String employee_code = CorUtil.hashMap2Str(parms, "employee_code", "需要employee_code参数");
		// 获取idpathwhere
		Hr_employee emp = new Hr_employee();// 创建人事档案实体
		String sqlstr1 = "select * from hr_employee where employee_code='" + employee_code + "'";
		emp.findBySQL(sqlstr1);// 查询人事档案
		if (emp.isEmpty()) {
			// new Exception("ID为【" + happ_btrip.er_id.getValue() + "】的人事资料不存在");
			return CommonResult.failed("查询不到工号为：" + employee_code + "的人事资料").toString();
		}
		String str = "[{\"parmname\":\"er_id\",\"reloper\":\"=\",\"parmvalue\":\"" + emp.er_id.getValue() + "\"},{\"parmname\":\"canuses\",\"reloper\":\"=\",\"parmvalue\":1}]";
		urlparms.put("parms", str);
		CSContext.setParms(urlparms);
		String idpathwhere = " and (( idpath like '" + emp.idpath.getValue() + "%') )";

		String[] notnull = {};

//		String sqlstr = "SELECT * FROM( "
//				+ "SELECT e.hwc_namezl,e.sp_name,b.*, IF(b.valdate>curdate(),2,1) isexpire,IF(b.usedlbtime<b.alllbtime,2,1) usup,IF((b.valdate>curdate()) AND (b.usedlbtime<b.alllbtime),1,2) canuses "
//				+ "FROM hrkq_leave_blance b,hr_employee e where e.er_id=b.er_id " + CSContext.getIdpathwhere().replace("idpath", "b.idpath") + " ) tb ";
		String sqlstr = "SELECT * FROM( "
				+ "SELECT e.hwc_namezl,e.sp_name,b.*, IF(b.valdate>curdate(),2,1) isexpire,IF(b.usedlbtime<b.alllbtime,2,1) usup,IF((b.valdate>curdate()) AND (b.usedlbtime<b.alllbtime),1,2) canuses "
				+ "FROM hrkq_leave_blance b,hr_employee e where e.er_id=b.er_id " + idpathwhere.replace("idpath", "b.idpath") + " ) tb ";
		Hrkq_overtime_list ol = new Hrkq_overtime_list();
		Hrkq_business_trip bt = new Hrkq_business_trip();
		Hrkq_ondutyline dl = new Hrkq_ondutyline();
		Hrkq_special_holday sh = new Hrkq_special_holday();
		// System.out.println("sqlstr::" + sqlstr);

		String[] ignParms = new String[] {};
		boolean paging = true;
		String idpw = idpathwhere;

		JSONObject rst = new CReport(HRUtil.getReadPool(), sqlstr, " valdate desc ", notnull).findReport2JSON_O(ignParms, paging, idpw);
		JSONArray rows = rst.getJSONArray("rows");
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			int stype = row.getInt("stype");// 源类型 1 年假 2 加班 3 值班 4出差 5特殊
			int sid = row.getInt("sid");
			if ((stype == 2) && (sid != 0)) {
				ol.clear();
				ol.findByID(String.valueOf(sid));
				if (!ol.isEmpty()) {
					row.put("bgtime", ol.bgtime.getValue());
					row.put("edtime", ol.edtime.getValue());
				}
			}

			if ((stype == 3) && (sid != 0)) {
				dl.clear();
				dl.findByID(String.valueOf(sid));
				if (!dl.isEmpty()) {
					row.put("bgtime", dl.begin_date.getValue());
					row.put("edtime", dl.end_date.getValue());
				}
			}

			if ((stype == 4) && (sid != 0)) {
				bt.clear();
				bt.findByID(String.valueOf(sid));
				if (!bt.isEmpty()) {
					row.put("bgtime", bt.begin_date.getValue());
					row.put("edtime", bt.end_date.getValue());
				}
			}

			if ((stype == 5) && (sid != 0)) {
				sh.clear();
				sqlstr = "SELECT h.* FROM hrkq_special_holday h ,hrkq_special_holdayline l " + " WHERE h.sphid=l.sphid AND l.sphlid=" + sid;
				sh.findBySQL(sqlstr);
				if (!sh.isEmpty()) {
					row.put("bgtime", sh.sphdate.getValue());
				}
			}
		}

		String scols = urlparms.get("cols");
		if (scols == null) {
			return CommonResult.success(rst).toString();
		} else {
			(new CReport()).export2excel(rows, scols);
			return null;
		}

	}

	@ACOAction(eventname = "findkqcalcrst4resign", Authentication = false, notes = "查询考勤计算结果为补签")
	public String findkqcalcrst4resign() throws Exception {
		// 接收参数：1.档案Id,er_id：260799 2.考勤月度,resdate：2020-08
		HashMap<String, String> parm = CSContext.getParms();
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();

		// 初始化参数
		if ("".equals(parm.get("er_id")) || parm.get("er_id") == null) {
			return CommonResult.failed("er_id字段不能为空").toString();
		}
		if ("".equals(parm.get("resdate")) || parm.get("resdate") == null) {
			return CommonResult.failed("resdate字段不能为空").toString();
		}
		String er_id = parm.get("er_id");
		String resdate = parm.get("resdate") + "-01";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Calendar calendar = Calendar.getInstance();
		int nowMonth = calendar.get(Calendar.MONTH) + 1;
		calendar.setTime(simpleDateFormat.parse(resdate));
		int d = calendar.get(Calendar.MONTH) + 1;
		calendar.add(Calendar.MONTH, 1);
		String kqdate = simpleDateFormat.format(calendar.getTime());

		// 判断月份
		if (nowMonth - d > 1) {
			return CommonResult.failed("最多只能补签上月的").toString();
		}

		String parms1 = "[{\"parmname\":\"er_id\",\"reloper\":\"=\",\"parmvalue\":\"" + er_id + "\"},{\"parmname\":\"kqdate\",\"reloper\":\">=\",\"parmvalue\":\"" + resdate + "\"},{\"parmname\":\"kqdate\",\"reloper\":\"<\",\"parmvalue\":\"" + kqdate
				+ "\"},{\"parmname\":\"parmname_sqlwhere\",\"reloper\":\"=\",\"parmvalue\":\"(isabnom=1) and (frst=4 or trst=4)\"}]";
		String order = "resdate desc";
		urlparms.put("parms", parms1);
		urlparms.put("order", order);
		CSContext.setParms(urlparms);

		// 获取idpath
		Hr_employee hr_employee = new Hr_employee();// 创建人事存档
		String sql = "select * from hr_employee where er_id='" + er_id + "'";
		hr_employee.findBySQL(sql);
		if (hr_employee.isEmpty()) {
			return CommonResult.failed("查询不到id为：" + er_id + "的人事资料").toString();
		}
		String idpath = hr_employee.idpath.getValue();
		String idpathw = "and (( idpath like '" + idpath + "%') )";

		String[] notnull = {};
		String sqlstr = "SELECT r.bckqid,r.kqdate,e.er_id,e.employee_code,e.employee_name,e.orgname,e.sp_name,e.lv_num,"
				+ " r.frtime,r.frsktime,r.mtslate,r.frst,r.totime,r.lrst,r.tosktime,r.mtslearly,r.trst,r.isabnom,"
				+ " r.scdname,r.sclid,r.bcno,e.idpath"
				+ " FROM hrkq_bckqrst r,hr_employee e"
				+ " WHERE r.er_id=e.er_id and r.kqdate<CURDATE() AND r.kqdate>=e.kqdate_start "; // 添加入职日期以后 17-12-11 moyh 改为考勤开始时间以后
		// HashMap<String, String> parms = CSContext.getParms();
		String spcetype = CorUtil.hashMap2Str(parm, "spcetype");
		if ((spcetype != null) && (!spcetype.isEmpty())) {
			int si = Integer.valueOf(spcetype);
			if (si > 0) {
				sqlstr = sqlstr + " and e.employee_code='" + CSContext.getUserName() + "'";
			}
		}

		sqlstr = sqlstr + "  AND NOT EXISTS("
				+ " SELECT * FROM `hrkq_resign` h,  `hrkq_resignline`  l"
				+ " WHERE h.resid =l.resid AND l.`kqdate`=r.`kqdate` AND r.`er_id`=h.`er_id`"
				+ " AND r.sclid=l.`sclid` AND l.isreg=1"
				+ " AND h.`stat`>1 AND h.`stat`<10"
				+ " )";

		// return new CReport(sqlstr, " kqdate DESC ", notnull).findReport(null,idpathw);
		return CommonResult.success(new CReport(sqlstr, " kqdate DESC ", notnull).findReport(null, idpathw)).toString();
	}
}
