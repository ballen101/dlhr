package com.hr.canteen.co;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.hr.attd.entity.Hrkq_sched;
import com.hr.canteen.entity.Hr_canteen_cardreader;
import com.hr.canteen.entity.Hr_canteen_cardrelatems;
import com.hr.canteen.entity.Hr_canteen_costrecords;
import com.hr.canteen.entity.Hr_canteen_costrecordscount;
import com.hr.canteen.entity.Hr_canteen_mealclass;
import com.hr.canteen.entity.Hr_canteen_mealsystem;
import com.hr.canteen.entity.Hr_canteen_room;
import com.hr.canteen.entity.Hr_canteen_special;
import com.hr.inface.entity.Hrms_txjocatlist;
import com.hr.insurance.entity.Hr_ins_cancel;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.TimerTaskHRCTCostRecords;
import com.hr.util.TimerTaskHRSwcard;

@ACO(coname = "web.hrct.costrecord")
public class COHr_canteen_costrecords{
	@ACOAction(eventname = "findcostrecordday", Authentication = true, ispublic = true, notes = "获取日消费明细")
	public String findcostrecordday() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpcostbg = CjpaUtil.getParm(jps, "costbg");
		if (jpcostbg == null)
			throw new Exception("需要参数【costbg】");
		String orgcode = jporgcode.getParmvalue();
		String dqbgdate = jpcostbg.getParmvalue();
		
		JSONParm jpcosted = CjpaUtil.getParm(jps, "costed");
		if (jpcosted == null)
			throw new Exception("需要参数【costed】");
		String dqeddate = jpcosted.getParmvalue();
		
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqbgdate)));// 去除时分秒
		Date eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqeddate)));// 去除时分秒
		//Date eddate = Systemdate.dateDayAdd(bgdate, 1);// 加一天
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "costbg","costed","orgcode","employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT * FROM hr_canteen_costrecords cr where cr.idpath like '"+org.idpath.getValue()+"%' and cr.costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate);
		eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
		sqlstr=sqlstr+"' and cr.costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ";
		if(jpempcode!=null){
			String emplcode= jpempcode.getParmvalue();
			sqlstr=sqlstr+" and employee_code='"+emplcode+"'";
		}
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		Hr_canteen_cardrelatems crms=new Hr_canteen_cardrelatems();
		Hr_canteen_mealsystem ms=new Hr_canteen_mealsystem();
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		int countsubnums=0;
		for (int i = 0; i < rts.size(); i++) {
			JSONObject row = rts.getJSONObject(i);
			String ctcr_id = row.getString("ctcr_id");
			String mc_id = row.getString("mc_id");
			String lv_num = row.getString("lv_num");
			int lv=getemplev(lv_num);
			String idpath = row.getString("idpath");
			sqlstr="SELECT * FROM hr_canteen_cardrelatems crms WHERE crms.ctcr_id="+ctcr_id+" AND crms.mc_id="+mc_id;
			crms.clear();
			crms.findBySQL(sqlstr, false);
			if(!crms.isEmpty()){
				/*row.put("ctms_id", crms.ctms_id.getValue());
				row.put("ctms_name", crms.ctms_name.getValue());*/
				row.put("price", crms.price.getValue());
			}else{
				row.put("price", 0);
			}
			String costtime =row.getString("costtime");
			int lmcid = Integer.parseInt(mc_id);
			int sctype=dogetsctype(costtime,row.getString("er_id"));
			//int mnums=dogetmealnums(costtime,row.getString("er_id"),sctype);
			if((((sctype==1)&&((lmcid==26)||(lmcid==27)))||((sctype==2)&&(lmcid==28)))&&(sctype!=0)){
			if(row.containsKey("price")&& (row.getString("price").length()>0)){
				String price=row.getString("price");
				String ct = dictemp.getVbCE("1107", row.getString("mc_name"), false, "餐类类型【" + row.getString("mc_name")
						+ "】不存在");
				Hr_canteen_special spec=new Hr_canteen_special();
				spec.findBySQL("SELECT * FROM hr_canteen_special sp WHERE sp.stat=9 AND sp.usable=1 AND sp.er_id="+row.getString("er_id")+
						"  AND sp.class_type LIKE '%"+ct+"%' "+
						" AND ((sp.applytimetype=2 and sp.appbg_date<='"+row.getString("costtime")+"' AND '"+row.getString("costtime")+"'<=sp.apped_date)"+
						" or (sp.applytimetype=1 and sp.apply_date<='"+row.getString("costtime")+"'))");//餐厅?
				if(!spec.isEmpty()){
					if(spec.class_type.getValue().indexOf(ct)!=-1){
						if(spec.subsidiestype.getAsInt()==2)
							lv=2;
						if(spec.subsidiestype.getAsInt()==1)
							lv=3;
					}
				}
				ms.clear();
				ms.findBySQL("SELECT ms.* FROM hr_canteen_mealsystem ms WHERE ms.mc_id="+mc_id+" AND ms.price="+price+
						" AND ms.emplev="+lv+" AND LOCATE(ms.idpath,'"+idpath+"')=1 and ms.sublimit>="+countsubnums+" ORDER BY ms.idpath DESC", false);
				if(!ms.isEmpty()){
					countsubnums++;
					row.put("ctms_id", ms.ctms_id.getValue());
					row.put("ctms_code", ms.ctms_code.getValue());
					row.put("ctms_name", ms.ctms_name.getValue());
					row.put("price", ms.price.getValue());
					row.put("subsidies", ms.subsidies.getValue());
					row.put("compay", ms.subsidies.getValue());
					row.put("selfcost", ms.cost.getValue());
					row.put("emplev", ms.emplev.getValue());
				}
			}
			}else{
				row.put("subsidies", 0);
				row.put("compay", 0);
				row.put("selfcost", crms.price.getValue());
			}
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}
	}

	@ACOAction(eventname = "findcostrecordmonth", Authentication = true, notes = "获取月消费明细")
	public String findcostrecordmonth() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpcosttime = CjpaUtil.getParm(jps, "costtime");
		if (jpcosttime == null)
			throw new Exception("需要参数【costtime】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpcosttime.getParmvalue();
		//String emplcode="";
		
			
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "costtime","orgcode","employee_code" };// 忽略的查询条件
		String[] notnull = {};
		/*String sqlstr = "SELECT cr.*,ms.price,ms.subsidies ,ms.cost AS selfcost,ms.subsidies AS compay "+
		"FROM `hr_canteen_costrecords` cr left join `hr_canteen_mealsystem` ms "+
				"on ms.mc_id=cr.mc_id AND ms.emplev=(CASE WHEN cr.lv_num<3.0 THEN 1 WHEN cr.lv_num>=3.0 AND cr.lv_num<3.4 THEN 2 "+
                "WHEN cr.lv_num>=4.0 AND cr.lv_num<4.3 THEN 3 WHEN cr.lv_num>5.0 AND cr.lv_num<6.4 THEN 4 "+
                "WHEN cr.lv_num>7.0 AND cr.lv_num<=8.0 THEN 5  END) ";*/
        /*    	String sqlstr = "SELECT cr.*,ms.price,ms.subsidies,ms.cost AS selfcost,ms.subsidies AS compay FROM hr_canteen_costrecords cr,"+
                        "hr_canteen_cardrelatems crms ,hr_canteen_mealsystem ms WHERE   crms.mc_id=cr.mc_id AND crms.ctcr_id=cr.ctcr_id AND ms.ctms_id=crms.ctms_id ";
            	if(jpempcode!=null){
        			String emplcode= jpempcode.getParmvalue();
        			sqlstr=sqlstr+" and employee_code='"+emplcode+"' ";
        		}
		sqlstr=sqlstr+" and cr.idpath like '"+org.idpath.getValue()+"%' and cr.costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+"' and cr.costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);*/	
		String sqlstr="SELECT * FROM hr_canteen_costrecords cr where cr.idpath like '"+org.idpath.getValue()+"%' and cr.costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+"' and cr.costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ";
		if(jpempcode!=null){
			String emplcode= jpempcode.getParmvalue();
			sqlstr=sqlstr+" and employee_code='"+emplcode+"'";
		}
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		Hr_canteen_cardrelatems crms=new Hr_canteen_cardrelatems();
		Hr_canteen_mealsystem ms=new Hr_canteen_mealsystem();
		for (int i = 0; i < rts.size(); i++) {
			JSONObject row = rts.getJSONObject(i);
			String ctcr_id = row.getString("ctcr_id");
			String mc_id = row.getString("mc_id");
			String lv_num = row.getString("lv_num");
			int lv=getemplev(lv_num);
			String idpath = row.getString("idpath");
			sqlstr="SELECT * FROM hr_canteen_cardrelatems crms WHERE crms.ctcr_id="+ctcr_id+" AND crms.mc_id="+mc_id;
			crms.clear();
			crms.findBySQL(sqlstr, false);
			if(!crms.isEmpty()){
				/*row.put("ctms_id", crms.ctms_id.getValue());
				row.put("ctms_name", crms.ctms_name.getValue());*/
				row.put("price", crms.price.getValue());
			}else{
				row.put("price", 0);
			}
			if(row.containsKey("price")&& (row.getString("price").length()>0)){
				String price=row.getString("price");
				ms.clear();
				ms.findBySQL("SELECT ms.* FROM hr_canteen_mealsystem ms WHERE ms.mc_id="+mc_id+" AND ms.price="+price+
						" AND ms.emplev="+lv+" AND LOCATE(ms.idpath,'"+idpath+"')=1 ORDER BY ms.idpath DESC", false);
				if(!ms.isEmpty()){
					row.put("ctms_id", ms.ctms_id.getValue());
					row.put("ctms_code", ms.ctms_code.getValue());
					row.put("ctms_name", ms.ctms_name.getValue());
					row.put("price", ms.price.getValue());
					row.put("subsidies", ms.subsidies.getValue());
					row.put("compay", ms.subsidies.getValue());
					row.put("selfcost", ms.cost.getValue());
					row.put("emplev", ms.emplev.getValue());
				}
			}
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}
	}
	
	@ACOAction(eventname = "findcostrecordmonthcount", Authentication = true, notes = "获取月机构消费统计")
	public String findcostrecordmonthcount() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		//JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpcosttime = CjpaUtil.getParm(jps, "costtime");
		if (jpcosttime == null)
			throw new Exception("需要参数【costtime】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpcosttime.getParmvalue();
		//String emplcode="";
		//if(jpempcode!=null){
		//	emplcode= jpcosttime.getParmvalue();
		//}
			
	
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "costtime","orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT cr.*,ms.price,sum(ms.subsidies) as subsidies,sum(ms.cost) AS selfcost,sum(ms.subsidies) AS compay,COUNT(*) AS costnum "+
		"FROM `hr_canteen_costrecords` cr left join `hr_canteen_mealsystem` ms "+
				"on ms.mc_id=cr.mc_id AND ms.emplev=(CASE WHEN cr.lv_num<3.0 THEN 1 WHEN cr.lv_num>=3.0 AND cr.lv_num<3.4 THEN 2 "+
                "WHEN cr.lv_num>=4.0 AND cr.lv_num<4.3 THEN 3 WHEN cr.lv_num>5.0 AND cr.lv_num<6.4 THEN 4 "+
                "WHEN cr.lv_num>7.0 AND cr.lv_num<=8.0 THEN 5  END) ";
		sqlstr=sqlstr+" where cr.idpath like '"+org.idpath.getValue()+"%' and cr.costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+"' and cr.costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' GROUP BY cr.orgid,cr.mc_id ";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	
	@ACOAction(eventname = "syncCostrecords", Authentication = true, notes = "同步消费记录")
	public String syncCostrecords() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String sbgdate = CorUtil.hashMap2Str(parms, "bgdate", "需要参数bgdate");
		String seddate = CorUtil.hashMap2Str(parms, "eddate", "需要参数eddate");
		String empcode = CorUtil.hashMap2Str(parms, "code");
		Date ftime = Systemdate.getDateByStr(sbgdate);
		Date ttime = Systemdate.dateDayAdd(Systemdate.getDateByStr(seddate), 1);
		TimerTaskHRCTCostRecords.importdata4oldct2hrms(ftime, ttime, empcode);
		JSONObject rst = new JSONObject();
		rst.put("rst", "ok");
		return rst.toString();
	}
	
	@ACOAction(eventname = "findcostrecordlist", Authentication = true, notes = "获取消费记录")
	public String findcostrecordlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		//JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		//JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		//if (jporgcode == null)
		//	throw new Exception("需要参数【orgcode】");
		JSONParm jpxfbg = CjpaUtil.getParm(jps, "xfbg");
		if (jpxfbg == null)
			throw new Exception("需要参数【xfbg】");
		//String orgcode = jporgcode.getParmvalue();
		String xfbgdate = jpxfbg.getParmvalue();
		
		JSONParm jpxfed = CjpaUtil.getParm(jps, "xfed");
		if (jpxfed == null)
			throw new Exception("需要参数【xfed】");
		//String orgcode = jporgcode.getParmvalue();
		String xfeddate = jpxfed.getParmvalue();
		//String emplcode="";
		//if(jpempcode!=null){
		//	emplcode= jpcosttime.getParmvalue();
		//}
			
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(xfbgdate)));// 去除时分秒
		Date eddate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(xfeddate)));// 去除时分秒
		//Date eddate = Systemdate.dateDayAdd(bgdate, 1);// 加一天
		String[] ignParms = { "xfbg","xfed" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT crls.*,IFNULL(e.employee_code,'') AS employee_code,IFNULL(e.employee_name,'') AS employee_name,"+
		"IFNULL(e.orgname,'') AS orgname,IFNULL(e.sp_name,'') AS sp_name,IFNULL(e.lv_num,'') AS lv_num "+
		" FROM (SELECT crs.*,IFNULL(cd.ctcr_name,'') AS ctcr_name,IFNULL(cd.ctr_name,'') AS ctr_name FROM  "+
		" (SELECT * FROM hrms_txjocatlist WHERE xfsj>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND ";
		//int cp=bgdate.compareTo(eddate);
		//if(cp==0){
			eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
			sqlstr=sqlstr+" xfsj<'"+Systemdate.getStrDateyyyy_mm_dd(eddate);
		//}else if(cp<0){
		//	sqlstr=sqlstr+" xfsj<='"+Systemdate.getStrDateyyyy_mm_dd(eddate);
		//}
		sqlstr=sqlstr+"') crs LEFT JOIN hr_canteen_cardreader cd "+
		" ON crs.xfjh=cd.sync_sn) crls LEFT JOIN hr_employee e ON crls.code=e.employee_code";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	
	@ACOAction(eventname = "findcostpaymonth", Authentication = true, notes = "获取月度费用结算表")
	public String findcostpaymonth() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		//JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		if (jpbgtime == null)
			throw new Exception("需要参数【begintime】");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		if (jpedtime == null)
			throw new Exception("需要参数【endtime】");
		String dqbgdate = jpbgtime.getParmvalue();
		String dqeddate = jpedtime.getParmvalue();
		Date bgdate=Systemdate.getDateByStr(dqbgdate);
		Date eddate =Systemdate.getDateByStr(dqeddate);
		String orgcode = jporgcode.getParmvalue();
		//String emplcode="";
		//if(jpempcode!=null){
		//	emplcode= jpcosttime.getParmvalue();
		//}
		String[] ignParms = { "begintime","endtime","orgcode" };// 忽略的查询条件
	
		//Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		//Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		
		String[] notnull = {};
		String sqlstr = "SELECT cr.orgname,cr.cardnumber,cr.employee_code,cr.employee_name,cr.sp_name,cr.lv_num,cr.ctr_name,cr.mc_name,ms.price,"+
		"SUM(CASE WHEN cr.mc_id=25 THEN 1 ELSE 0 END ) AS mc1, SUM(CASE WHEN cr.mc_id=26 THEN 1 ELSE 0 END ) AS mc2,"+ 
"SUM(CASE WHEN cr.mc_id=27 THEN 1 ELSE 0 END ) AS mc3, SUM(CASE WHEN cr.mc_id=28 THEN 1 ELSE 0 END ) AS mc4, "+
"SUM(CASE WHEN cr.mc_id=25 THEN ms.subsidies ELSE 0 END ) AS sub1,SUM(CASE WHEN cr.mc_id=26 THEN ms.subsidies ELSE 0 END ) AS sub2,"+
"SUM(CASE WHEN cr.mc_id=27 THEN ms.subsidies ELSE 0 END ) AS sub3,SUM(CASE WHEN cr.mc_id=28 THEN ms.subsidies ELSE 0 END ) AS sub4,"+
"SUM(ms.subsidies) AS compay,SUM(ms.cost) AS tatolcost,SUM(ms.subsidies) AS subsidies"+
" FROM hr_canteen_costrecords cr,hr_canteen_mealsystem ms "+
" WHERE ms.mc_id=cr.mc_id AND ms.emplev=(CASE WHEN cr.lv_num<3.0 THEN 1 WHEN cr.lv_num>=3.0 AND cr.lv_num<3.4 THEN 2 "+
                "WHEN cr.lv_num>=4.0 AND cr.lv_num<4.3 THEN 3 WHEN cr.lv_num>5.0 AND cr.lv_num<6.4 THEN 4 "+
                "WHEN cr.lv_num>7.0 AND cr.lv_num<=8.0 THEN 5  END) ";
		sqlstr=sqlstr+" and cr.idpath like '"+org.idpath.getValue()+"%' and cr.costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+"' and cr.costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
		sqlstr=sqlstr+" GROUP BY cr.er_id,cr.mc_id,cr.ctr_id";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "syncNewCostrecords", Authentication = true, notes = "转换原始消费记录")
	public String syncNewCostrecords() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String sbgdate = CorUtil.hashMap2Str(parms, "bgdate", "需要参数bgdate");
		String seddate = CorUtil.hashMap2Str(parms, "eddate", "需要参数eddate");
		String empcode = CorUtil.hashMap2Str(parms, "code");
		Date ftime = Systemdate.getDateByStr(sbgdate);
		Date ttime = Systemdate.dateDayAdd(Systemdate.getDateByStr(seddate), 1);
		TimerTaskHRCTCostRecords.doinsertrecords(0L,ftime, ttime, empcode);
		JSONObject rst = new JSONObject();
		rst.put("rst", "ok");
		return rst.toString();
	}
	
	@ACOAction(eventname = "findlocalcostrecordlist", Authentication = true, notes = "获取本地已同步的消费记录")
	public String findlocalcostrecordlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		//JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		//JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		//if (jporgcode == null)
		//	throw new Exception("需要参数【orgcode】");
		JSONParm jpcosttime = CjpaUtil.getParm(jps, "costtime");
		if (jpcosttime == null)
			throw new Exception("需要参数【costtime】");
		//String orgcode = jporgcode.getParmvalue();
		String dqdate = jpcosttime.getParmvalue();
		//String emplcode="";
		//if(jpempcode!=null){
		//	emplcode= jpcosttime.getParmvalue();
		//}
			
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateDayAdd(bgdate, 1);// 加一天
		String[] ignParms = { "costtime" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT emp.employee_code AS cardnumber,emp.er_id,emp.employee_code,emp.employee_name,"+
		"emp.orgid,emp.orgcode,emp.orgname,emp.ospid,emp.ospcode,emp.sp_name,emp.lv_id,emp.lv_num,emp.idpath,"+
		"cr.ctr_id,cr.ctr_code,cr.ctr_name,cr.ctcr_id,cr.ctcr_code,cr.ctcr_name,xfl.xfsj AS costtime,"+
		"mc.mc_id,mc.mc_name,xfl.xfid AS synid "+
        " FROM hrms_txjocatlist xfl,hr_employee emp ,hr_canteen_cardreader cr,hr_canteen_mealclass mc"+
        " WHERE   xfl.rfkh=emp.employee_code AND xfl.xfjh=cr.sync_sn AND"+
        " (CASE WHEN  (HOUR(xfsj)>=20 OR HOUR(xfsj)<=3) THEN HOUR(mealbegin)>=20 "+
        " WHEN (HOUR(xfsj)<20 AND HOUR(xfsj)>3 ) THEN HOUR(xfsj)>=HOUR(mealbegin) AND HOUR(xfsj)<HOUR(mealend) END) "+
        " AND INSTR(emp.idpath,mc.idpath) >0 AND xfsj>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+
        "' AND xfsj<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ORDER BY xfl.xfsj";
		return new CReport(sqlstr, notnull).findReport(ignParms);
	}
	
	@ACOAction(eventname = "countcostlists", Authentication = true, ispublic = true, notes = "就餐统计表")
	public String countcostlists() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		/*JSONParm jpcosttime = CjpaUtil.getParm(jps, "costtime");
		if (jpcosttime == null)
			throw new Exception("需要参数【costtime】");*/
		String orgcode = jporgcode.getParmvalue();
		//String dqdate = jpcosttime.getParmvalue();
		String idpath=CSContext.getUserIdpaths()[0];
		
		
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
			
		/*Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一天
*/		String[] ignParms = { "costtime","orgcode" };// 忽略的查询条件
		String[] notnull = {};
	/*	String sqlstr = "SELECT cr.*,ms.price,ms.subsidies,COUNT(*) AS costnum,(ms.price-ms.subsidies)*(COUNT(*)) AS totalpay "+
		" FROM hr_canteen_costrecords cr,`hr_canteen_mealsystem` ms,hr_canteen_cardrelatems crms WHERE cr.idpath like '"+idpath+"%' and cr.costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+
		"' AND cr.costtime<='"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'  AND crms.mc_id=cr.mc_id AND crms.ctcr_id=cr.ctcr_id AND ms.ctms_id=crms.ctms_id  "+
		"  GROUP BY cr.er_id,cr.mc_id,cr.ctr_id,ms.price ORDER BY cr.er_id ";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);*/
		String sqlstr=" SELECT * FROM (SELECT cr.*,IFNULL(crms.price,0) AS price FROM (SELECT *,COUNT(*) AS costnum FROM hr_canteen_costrecords WHERE "+
		//"  costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' AND "+
		" idpath LIKE '"+org.idpath.getValue()+"%'   ";
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		if ((jpbgtime != null)&&(jpedtime != null)){
			String dqbgdate = jpbgtime.getParmvalue();
			String dqeddate = jpedtime.getParmvalue();
			Date bgdate=Systemdate.getDateByStr(dqbgdate);
			Date eddate =Systemdate.getDateByStr(dqeddate);
			int cp=bgdate.compareTo(eddate);
			if(cp==0){
				eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
				sqlstr=sqlstr+" and costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
			}else if(cp<0){
				sqlstr=sqlstr+" and costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<='"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
			}
			 
		}
		
		if(jpempcode!=null){
			String empcode= jpempcode.getParmvalue();
			sqlstr=sqlstr+" and employee_code='"+empcode+"' ";
		}
		sqlstr=sqlstr+"  GROUP BY er_id,mc_id,ctcr_id) cr LEFT JOIN  hr_canteen_cardrelatems crms ON cr.ctcr_id=crms.ctcr_id AND cr.mc_id=crms.mc_id ) crs WHERE 1=1 GROUP BY er_id,mc_id,ctcr_id,price";
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		for (int i = 0; i < rts.size(); i++) {
			JSONObject row = rts.getJSONObject(i);
			float sub=0;
			float cost=0;
			String lv_num = row.getString("lv_num");
			String mc_id = row.getString("mc_id");
			String cridpath = row.getString("idpath");
			String price = row.getString("price");
			float pri=Float.parseFloat(price);
			int nums=Integer.parseInt(row.getString("costnum"));
			Hr_canteen_mealsystem ms=new Hr_canteen_mealsystem();
			int emplev=getemplev(lv_num);
			ms.findBySQL("SELECT * FROM  hr_canteen_mealsystem ms WHERE ms.price="+price+" AND ms.emplev="+emplev+
					" AND ms.mc_id="+mc_id+" AND LOCATE(ms.idpath,'"+cridpath+"')=1  ORDER BY ms.idpath DESC ");
			if(ms.isEmpty()){
				sub=0;
			}else{
				sub=ms.subsidies.getAsFloat();
			}
			cost=(pri-sub)*nums;
			row.put("subsidies", sub);
			row.put("totalpay",cost);		
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}
	}
	
	@ACOAction(eventname = "costSummary", Authentication = true, notes = "餐费汇总表")
	public String costSummary() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		//JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		/*JSONParm jpcosttime = CjpaUtil.getParm(jps, "costtime");
		if (jpcosttime == null)
			throw new Exception("需要参数【costtime】");*/
		String orgcode = jporgcode.getParmvalue();
		//String dqdate = jpcosttime.getParmvalue();
		
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
			
		//Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		//Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一天
		String[] ignParms = { "costtime","orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr = "SELECT cr.*,ms.zao1,ms.subzao1,ms.wu1,ms.subwu1,ms.wu2,ms.subwu2,ms.wan1,ms.subwan1,ms.wan2,ms.subwan2,"+
		"ms.ye1,ms.subye1,ms.ye2,ms.subye2,ms.ye3,ms.subye3,zao*zao1 AS zaopay1,wu*wu1 AS wupay1,wu*wu2 AS wupay2,wan*wan1 AS wanpay1,"+
		"wan*wan2 AS wanpay2,ye*ye1 AS yepay1,ye*ye2 AS yepay2,ye*ye3 AS yepay3,wu*subwu1 AS wupaysub1,wu*subwu2 AS wupaysub2,"+
		"wan*subwan1 AS wanpaysub1,wan*subwan2 AS wanpaysub2,ye*subye1 AS yepaysub1,ye*subye2 AS yepaysub2,ye*subye3 AS yepaysub3,"+
		"(zao*zao1+wu*wu1+wan*wan1+ye*ye1) AS totalpay1,(wu*subwu1+wan*subwan1+ye*subye1) AS totalpaysub1 FROM "+
		"(SELECT c.* ,SUM(CASE c.mc_id WHEN 25 THEN 1 ELSE 0 END) AS zao,SUM(CASE c.mc_id WHEN 26 THEN 1 ELSE 0 END) AS wu,"+
		"SUM(CASE c.mc_id WHEN 27 THEN 1 ELSE 0 END) AS wan,SUM(CASE c.mc_id WHEN 28 THEN 1 ELSE 0 END) AS ye FROM hr_canteen_costrecords c "+
		" WHERE c.idpath LIKE '"+org.idpath.getValue()+"%' "+
		//"AND c.costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND c.costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'"+
		" GROUP BY c.er_id ) cr,(SELECT m.*,SUM(CASE WHEN attribute1=1 AND mc_id=25 THEN price ELSE 0 END) zao1,"+
		"SUM(CASE WHEN attribute1=1 AND mc_id=26 THEN price ELSE 0 END) wu1,SUM(CASE WHEN attribute1=2 AND mc_id=26 THEN price ELSE 0 END) wu2,"+
		"SUM(CASE WHEN attribute1=1 AND mc_id=27 THEN price ELSE 0 END) wan1,SUM(CASE WHEN attribute1=2 AND mc_id=27 THEN price ELSE 0 END) wan2,"+
		"SUM(CASE WHEN attribute1=1 AND mc_id=28 THEN price ELSE 0 END) ye1,SUM(CASE WHEN attribute1=2 AND mc_id=28 THEN price ELSE 0 END) ye2,"+
		"SUM(CASE WHEN attribute1=3 AND mc_id=28 THEN price ELSE 0 END) ye3,SUM(CASE WHEN attribute1=1 AND mc_id=25 THEN subsidies ELSE 0 END) subzao1,"+
		"SUM(CASE WHEN attribute1=1 AND mc_id=26 THEN subsidies ELSE 0 END) subwu1,SUM(CASE WHEN attribute1=2 AND mc_id=26 THEN subsidies ELSE 0 END) subwu2,"+
		"SUM(CASE WHEN attribute1=1 AND mc_id=27 THEN subsidies ELSE 0 END) subwan1,SUM(CASE WHEN attribute1=2 AND mc_id=27 THEN subsidies ELSE 0 END) subwan2,"+
		"SUM(CASE WHEN attribute1=1 AND mc_id=28 THEN subsidies ELSE 0 END) subye1,SUM(CASE WHEN attribute1=2 AND mc_id=28 THEN subsidies ELSE 0 END) subye2,"+
		"SUM(CASE WHEN attribute1=3 AND mc_id=28 THEN subsidies ELSE 0 END) subye3 FROM `hr_canteen_mealsystem` m GROUP BY m.emplev) ms WHERE "+ 
		"ms.emplev=(CASE WHEN cr.lv_num<3.0 THEN 1 WHEN cr.lv_num>=3.0 AND cr.lv_num<3.4 THEN 2 "+
		"WHEN cr.lv_num>=4.0 AND cr.lv_num<4.3 THEN 3 WHEN cr.lv_num>5.0 AND cr.lv_num<6.4 THEN 4 "+
		"WHEN cr.lv_num>7.0 AND cr.lv_num<=8.0 THEN 5  END) GROUP BY cr.er_id,cr.mc_id ";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "countcanteencost", Authentication = true, notes = "饭堂消费统计")
	public String countcanteencost() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jproomcode = CjpaUtil.getParm(jps, "ctr_code");
		/*if (jproomcode == null)
			throw new Exception("需要参数【ctr_code】");*/
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		if (jpbgtime == null)
			throw new Exception("需要参数【begintime】");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		if (jpedtime == null)
			throw new Exception("需要参数【endtime】");
		String dqbgdate = jpbgtime.getParmvalue();
		String dqeddate = jpedtime.getParmvalue();
		Date bgdate=Systemdate.getDateByStr(dqbgdate);
		Date eddate =Systemdate.getDateByStr(dqeddate);
		eddate =Systemdate.dateDayAdd(eddate, 1);// 加一天
		
		
		String[] ignParms = { "begintime","endtime","ctr_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT  DATE_FORMAT(costtime,'%Y-%m-%d') AS days ,COUNT(*) AS costnum,COUNT(DISTINCT(cr.er_id)) AS empnum,cr.ctr_id,cr.ctr_name,"+
		"SUM(CASE WHEN cr.mc_id=25 THEN 1 ELSE 0 END) AS zaonum,SUM(CASE WHEN cr.mc_id=26 THEN 1 ELSE 0 END) AS wunum,"+
		"SUM(CASE WHEN cr.mc_id=27 THEN 1 ELSE 0 END) AS wannum,SUM(CASE WHEN cr.mc_id=28 THEN 1 ELSE 0 END) AS yenum, "+
		"SUM(CASE WHEN cr.mc_id=25 THEN IFNULL(crms.price,0) ELSE 0 END) AS zaopay,SUM(CASE WHEN cr.mc_id=26 THEN IFNULL(crms.price,0) ELSE 0 END) AS wupay,"+
		"SUM(CASE WHEN cr.mc_id=27 THEN IFNULL(crms.price,0) ELSE 0 END) AS wanpay,SUM(CASE WHEN cr.mc_id=28 THEN IFNULL(crms.price,0) ELSE 0 END) AS yepay ,"+
        "SUM(IFNULL(crms.price,0)) AS totalcost"+
		" FROM (SELECT * FROM hr_canteen_costrecords  WHERE costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"'  AND costtime<'"+
				Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ";
		if (jproomcode != null){
			String roomcode = jproomcode.getParmvalue();
			Hr_canteen_room ctroom=new Hr_canteen_room();
			String sqlstr1 ="select * from hr_canteen_room where ctr_code='"+roomcode+"'";
			ctroom.findBySQL(sqlstr1);
			if(ctroom.isEmpty())
				throw new Exception("编码为【" + roomcode + "】的餐厅不存在");
			sqlstr=sqlstr+" AND ctr_id="+ctroom.ctr_id.getValue();
		}
		sqlstr=sqlstr+" ) cr LEFT JOIN hr_canteen_cardrelatems crms ON cr.ctcr_id=crms.ctcr_id AND cr.mc_id=crms.mc_id AND crms.usable=1  GROUP BY cr.ctr_id,days ";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	/*	JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		for (int i = 0; i < rts.size(); i++) {
			JSONObject row = rts.getJSONObject(i);
			String daydate = row.getString("days");
			String ctr_id = row.getString("ctr_id");
			Date daybgdate = Systemdate.getDateByStr(daydate);// 去除时分秒
			Date dayeddate = Systemdate.dateDayAdd(daybgdate, 1);// 加一天
			sqlstr="SELECT cr.ctr_id,cr.ctr_name,crms.price,SUM(CASE WHEN cr.mc_id=25 THEN crms.price ELSE 0 END) zaopay,"+
			"SUM(CASE WHEN cr.mc_id=26 THEN crms.price ELSE 0 END) wupay,SUM(CASE WHEN cr.mc_id=27 THEN crms.price ELSE 0 END) wanpay,"+
			"SUM(CASE WHEN cr.mc_id=28 THEN crms.price ELSE 0 END) yepay,SUM(crms.price) AS totalcost FROM hr_canteen_costrecords cr,hr_canteen_cardrelatems crms "+
			" WHERE  crms.mc_id=cr.mc_id AND crms.ctcr_id=cr.ctcr_id  "+
			"AND cr.costtime>='"+Systemdate.getStrDateyyyy_mm_dd(daybgdate)+"'  AND cr.costtime<'"+Systemdate.getStrDateyyyy_mm_dd(dayeddate)+
			"' AND cr.ctr_id="+ctr_id;
			JSONObject ctcosts = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
			JSONArray roomcosts = ctcosts.getJSONArray("rows");
			for(int j = 0; j < roomcosts.size(); j++){
				JSONObject roomcost = roomcosts.getJSONObject(j);
				if(roomcost.isEmpty()){
					row.put("zaopay", 0);
					row.put("wupay",0);
					row.put("wanpay", 0);
					row.put("yepay",0);
					row.put("totalcost",0);
				}else{
					row.put("zaopay", roomcost.getString("zaopay"));
					row.put("wupay", roomcost.getString("wupay"));
					row.put("wanpay", roomcost.getString("wanpay"));
					row.put("yepay", roomcost.getString("yepay"));
					row.put("totalcost", roomcost.getString("totalcost"));
				}
			}
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}*/
	}
	
	@ACOAction(eventname = "costSummaryEx", Authentication = true, notes = "餐费汇总表Ex")
	public String costSummaryEx() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		CJPALineData<Hr_canteen_mealsystem> mealsyss = new CJPALineData<Hr_canteen_mealsystem>(Hr_canteen_mealsystem.class);
		mealsyss.findDataBySQL("SELECT * FROM  hr_canteen_mealsystem ms ORDER BY ms.idpath DESC");
		//Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		//Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一天
		String[] ignParms = { "costtime","orgcode","employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT cr.*,COUNT(*) AS nums FROM hr_canteen_costrecords cr WHERE 1=1 ";
		if(jpempcode!=null){
			String empcode= jpempcode.getParmvalue();
			sqlstr=sqlstr+" and employee_code='"+empcode+"' ";
		}
		if(jpbgtime != null){
			String dqbgdate = jpbgtime.getParmvalue();	
			Date bgdate=Systemdate.getDateByStr(dqbgdate);
			Date eddate =Systemdate.getDateByStr(Systemdate.getStrDate());
			if(jpedtime != null){
				String dqeddate = jpedtime.getParmvalue();
				eddate =Systemdate.getDateByStr(dqeddate);
			}
			//int cp=bgdate.compareTo(eddate);
			//if(cp==0){
				eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
				sqlstr=sqlstr+" and costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
			//}else if(cp<0){
			//	sqlstr=sqlstr+" and costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<='"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
			//}
		}
		sqlstr=sqlstr+" AND idpath LIKE '"+org.idpath.getValue()+"%' GROUP BY er_id";
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (int i = 0; i < rts.size(); i++) {
			JSONObject row = rts.getJSONObject(i);
			int zaonum=0;
			int wunum5=0;
			int wunum7=0;
			int wunum=0;
			int wannum5=0;
			int wannum7=0;
			int wannum=0;
			int yenum=0;
			double yeprice=0;
			double yesub=0;
			double yepay=0;
			double wuprice=0;
			double wusub=0;
			double wupay=0;
			double wu5pay=0;
			double wu7pay=0;
			double wanprice=0;
			double wansub=0;
			double wanpay=0;
			double wan5pay=0;
			double wan7pay=0;
			double zaoprice=0;
			double zaopay=0;
			double zaosub=0;
			double totalprice=0;
			double totalsub=0;
			double totalpay=0;
			int limit=0;
			int countsubnums=0;
			String er_id = row.getString("er_id");
			sqlstr=" SELECT cr.* ,IFNULL(crms.price,0) AS price "+
			" FROM (SELECT * FROM `hr_canteen_costrecords` WHERE "+"  idpath LIKE '"+org.idpath.getValue()+"%' AND er_id="+er_id;
			if(jpbgtime != null){
				String dqbgdate = jpbgtime.getParmvalue();
				Date bgdate=Systemdate.getDateByStr(dqbgdate);
				Date eddate =Systemdate.getDateByStr(Systemdate.getStrDate());
				if(jpedtime != null){
					String dqeddate = jpedtime.getParmvalue();
					 eddate =Systemdate.getDateByStr(dqeddate);
				}
				
				//int cp=bgdate.compareTo(eddate);
				//if(cp==0){
					eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
					sqlstr=sqlstr+" and costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
				//}else if(cp<0){
				//	sqlstr=sqlstr+" and costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<='"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
				//}
			}
			sqlstr=sqlstr+" ) cr LEFT JOIN hr_canteen_cardrelatems crms ON cr.ctcr_id=crms.ctcr_id AND cr.mc_id=crms.mc_id ";
			JSONObject empcosts = new CReport(sqlstr, null).findReport2JSON_O(ignParms,false);
			JSONArray emprts = empcosts.getJSONArray("rows");
			for (int j = 0; j < emprts.size(); j++) {
				JSONObject emprt = emprts.getJSONObject(j);
				if(emprt.isEmpty())
					continue;
				int mc_id = Integer.parseInt(emprt.getString("mc_id"));
				String lv_num =emprt.getString("lv_num");
				
				float mprice =Float.parseFloat(emprt.getString("price"));
				double subsidies = 0;
				int sublimit = 0;
				int specsubnum=0;
				String idpath=emprt.getString("idpath");
				//Hr_canteen_mealsystem ms=new Hr_canteen_mealsystem();
				int emplev=getemplev(lv_num);
				String ct = dictemp.getVbCE("1107", emprt.getString("mc_name"), false, "餐类类型【" + emprt.getString("mc_name")
						+ "】不存在");
				Hr_canteen_special spec=new Hr_canteen_special();
				spec.findBySQL("SELECT * FROM hr_canteen_special sp WHERE sp.stat=9 AND sp.usable=1 AND sp.er_id="+emprt.getString("er_id")+
						"  AND sp.class_type LIKE '%"+ct+"%' "+
						" AND ((sp.applytimetype=2 and sp.appbg_date<='"+emprt.getString("costtime")+"' AND '"+emprt.getString("costtime")+"'<=sp.apped_date)"+
						" or (sp.applytimetype=1 and sp.apply_date<='"+emprt.getString("costtime")+"'))");//餐厅?
				if(!spec.isEmpty()){
					if(spec.class_type.getValue().indexOf(ct)!=-1){
						if(spec.subsidiestype.getAsInt()==2)
						   emplev=2;
						if(spec.subsidiestype.getAsInt()==1)
							   emplev=3;
					}
				}
				String costtime =emprt.getString("costtime");
				int sctype=dogetsctype(costtime,emprt.getString("er_id"));
				//int mnums=dogetmealnums(costtime,emprt.getString("er_id"),sctype);
				if((((sctype==1)&&((mc_id==26)||(mc_id==27)))||((sctype==2)&&(mc_id==28)))&&(sctype!=0)){
				  for(int k=0;k<mealsyss.size();k++){
					Hr_canteen_mealsystem mealsys=(Hr_canteen_mealsystem)mealsyss.get(k);
					int idrst=idpath.indexOf(mealsys.idpath.getValue());
					if((mealsys.price.getAsFloat()==mprice)&&(mealsys.emplev.getAsInt()==emplev)&&(mealsys.mc_id.getAsInt()==mc_id)&&(idrst>=0)){
						if(mealsys.sublimit.getAsInt()>=countsubnums){
							subsidies=(double)(mealsys.subsidies.getAsFloat());
							sublimit=mealsys.sublimit.getAsInt();//特殊用餐申请层级补贴餐数与原层级补贴餐数相同？
							countsubnums++;
							specsubnum++;//特殊用餐申请的补贴次数
							//System.out.println("---ms:"+mealsys.price.getValue()+","+mealsys.ctms_name.getValue()+"cr:price"+mprice+","+countsubnums);
							break;
						}
					}
					subsidies = 0;
					 sublimit = 0;
				  }
				}
				double price =Double.parseDouble(emprt.getString("price"));
				/*if(ms.isEmpty()){
					 subsidies = 0;
					 sublimit = 0;
					 continue;
				}else{
					subsidies=ms.subsidies.getAsFloat();
					sublimit=ms.sublimit.getAsInt();
				}*/
			
				if(mc_id==25){
					zaonum++;
					zaoprice=add(zaoprice,price);
					zaopay=add(zaopay,sub(price,subsidies));
					zaosub=add(zaosub,subsidies);
				}else if(mc_id==26){
					if(price==5.8){
						wunum5++;
						wu5pay=add(wu5pay,price);
					}
					if(price==7.8){
						wunum7++;
						wu7pay=add(wu7pay,price);
					}
					wunum++;
					wuprice=add(wuprice,price);
					wusub=add(wusub,subsidies);
					wupay=add(wupay,sub(price,subsidies));
				}else if(mc_id==27){
					if(price==5.8){
						wannum5++;
						wan5pay=add(wan5pay,price);
					}
					if(price==7.8){
						wannum7++;
						wan7pay=add(wan7pay,price);
					}
					wannum++;
					wanprice=add(wanprice,price);
					wansub=add(wansub,subsidies);
					wanpay=add(wanpay,sub(price,subsidies));
				}else if(mc_id==28){
					yenum++;
					yeprice=add(yeprice,price);
					yesub=add(yesub,subsidies);
					yepay=add(yepay,sub(price,subsidies));
				}
				limit=sublimit;
				if((wunum+wannum+yenum)>limit){
					totalprice=add(zaoprice,add(wuprice,add(wanprice,yeprice)));
					totalpay=add(zaoprice,add(wuprice,add(wanprice,yeprice)));
				}else{
					totalprice=add(zaoprice,add(wuprice,add(wanprice,yeprice)));
					totalsub=add(wusub,add(wansub,yesub));
					totalpay=add(zaopay,add(wupay,add(wanpay,yepay)));
				}
			}
			row.put("zaonum", zaonum);
			row.put("wunum5", wunum5);
			row.put("wunum7", wunum7);
			row.put("wunum", wunum);
			row.put("wannum5", wannum5);
			row.put("wannum7", wannum7);
			row.put("wannum", wannum);
			row.put("yenum", yenum);
			row.put("zaoprice", zaoprice);
			row.put("zaopay", zaopay);
			row.put("zaosub", zaosub);
			row.put("wu5pay", wu5pay);
			row.put("wu7pay", wu7pay);
			row.put("wupay", wupay);
			row.put("wuprice", wuprice);
			row.put("wusub", wusub);
			row.put("wan5pay", wan5pay);
			row.put("wan7pay", wan7pay);
			row.put("wanpay", wanpay);
			row.put("wanprice", wanprice);
			row.put("wansub", wansub);
			row.put("yeprice", yeprice);
			row.put("yesub", yesub);
			row.put("yepay", yepay);
			row.put("totalpay", totalpay);
			row.put("totalprice", totalprice);
			row.put("totalsub", totalsub);
			int nums5=wunum5+wannum5;
			int nums7=wunum7+wannum7;
			row.put("wuwannum5",nums5 );
			row.put("wuwannum7", nums7);
			row.put("wuwanprice", add(wuprice,wanprice));
			row.put("wuwansub", add(wusub,wansub));
			row.put("sublimit", limit);
			//System.out.println("wu7:"+wunum7+",wan7:"+wannum7+",total7:"+nums7);
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}
	}
	
	@ACOAction(eventname = "countcostlistsEX", Authentication = true, ispublic = true, notes = "就餐统计表EX")
	public String countcostlistsEX() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		//String idpath=CSContext.getUserIdpaths()[0];
			
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		
		String[] ignParms = { "begintime","endtime","orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr=" SELECT * FROM (SELECT cr.*,IFNULL(crms.price,0) AS price FROM (SELECT *,COUNT(*) AS costnum FROM hr_canteen_costrecords WHERE "+
		" idpath LIKE '"+org.idpath.getValue()+"%'   ";
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		String dqbgdate = jpbgtime.getParmvalue();
		String dqeddate = jpedtime.getParmvalue();
		Date bgdate=Systemdate.getDateByStr(dqbgdate);
		Date eddate =Systemdate.getDateByStr(dqeddate);
		//int cp=bgdate.compareTo(eddate);
		//if(cp==0){
			eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
			sqlstr=sqlstr+" and costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
		//}else if(cp<0){
		//	sqlstr=sqlstr+" and costtime>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<='"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
		//}
		if(jpempcode!=null){
			String empcode= jpempcode.getParmvalue();
			sqlstr=sqlstr+" and employee_code='"+empcode+"' ";
		}
		sqlstr=sqlstr+"  GROUP BY er_id,mc_id,ctcr_id) cr LEFT JOIN  hr_canteen_cardrelatems crms ON cr.ctcr_id=crms.ctcr_id AND cr.mc_id=crms.mc_id ) crs WHERE 1=1 GROUP BY er_id,mc_id,ctcr_id,price";
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		CJPALineData<Hr_canteen_mealsystem> mealsyss = new CJPALineData<Hr_canteen_mealsystem>(Hr_canteen_mealsystem.class);
		mealsyss.findDataBySQL("SELECT * FROM  hr_canteen_mealsystem ms ORDER BY ms.idpath DESC");//将所有餐制查询出来备用
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		for (int i = 0; i < rts.size(); i++) {
			JSONObject row = rts.getJSONObject(i);
			float sub=0;
			float tprice=0;
			float tsub=0;
			float tpay=0;
			int sublimit=0;
			int specsubnum=0; 
			double dpri = 0;  
			double dsub = 0;  
			double dtprice = 0;  
			double dtsub = 0; 
			double dtpay =0; 
			String er_id = row.getString("er_id");
			String lv_num = row.getString("lv_num");
			String mc_id = row.getString("mc_id");
			String price = row.getString("price");
			int emplev=getemplev(lv_num);
			float pri=Float.parseFloat(price);
			//int nums=Integer.parseInt(row.getString("costnum"));
			int countsubnums=0;
			sqlstr=" SELECT cr.* ,IFNULL(crms.price,0) AS price "+
					" FROM (SELECT * FROM `hr_canteen_costrecords` WHERE "+"  idpath LIKE '"+org.idpath.getValue()+"%' AND er_id="+er_id+" AND costtime>='";
			//if(cp==0){
				//eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
				sqlstr=sqlstr+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
			//}else if(cp<0){
				//sqlstr=sqlstr+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<='"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
			//}
			if(jpempcode!=null){
				String empcode= jpempcode.getParmvalue();
				sqlstr=sqlstr+" and employee_code='"+empcode+"' ";
			}
			sqlstr=sqlstr+" and mc_id="+mc_id+" and ctcr_id="+row.getString("ctcr_id")+" ) cr LEFT JOIN hr_canteen_cardrelatems crms ON cr.ctcr_id=crms.ctcr_id AND cr.mc_id=crms.mc_id ";
			JSONObject emprcs = new CReport(sqlstr, null).findReport2JSON_O(ignParms,false);
			JSONArray emprc = emprcs.getJSONArray("rows");
			for (int j = 0; j < emprc.size(); j++) {
				JSONObject emprt = emprc.getJSONObject(j);
				if(emprt.isEmpty())
					continue;
				String idpath=emprt.getString("idpath");
				String ct = dictemp.getVbCE("1107", emprt.getString("mc_name"), false, "餐类类型【" + emprt.getString("mc_name")
						+ "】不存在");
				Hr_canteen_special spec=new Hr_canteen_special();
				spec.findBySQL("SELECT * FROM hr_canteen_special sp WHERE sp.stat=9 AND sp.usable=1 AND sp.er_id="+emprt.getString("er_id")+
						"  AND sp.class_type LIKE '%"+ct+"%' "+
						" AND ((sp.applytimetype=2 and sp.appbg_date<='"+emprt.getString("costtime")+"' AND '"+emprt.getString("costtime")+"'<=sp.apped_date)"+
						" or (sp.applytimetype=1 and sp.apply_date<='"+emprt.getString("costtime")+"'))");//餐厅?
				if(!spec.isEmpty()){
					if(spec.class_type.getValue().indexOf(ct)!=-1){
						if(spec.subsidiestype.getAsInt()==2)
						emplev=2;
						if(spec.subsidiestype.getAsInt()==1)
							emplev=3;
					}
				}
				int lmcid = Integer.parseInt(emprt.getString("mc_id"));
				String costtime =emprt.getString("costtime");
				int sctype=dogetsctype(costtime,emprt.getString("er_id"));
				//int mnums=dogetmealnums(costtime,emprt.getString("er_id"),sctype);
				if((((sctype==1)&&((lmcid==26)||(lmcid==27)))||((sctype==2)&&(lmcid==28)))&&(sctype!=0)){
				  for(int k=0;k<mealsyss.size();k++){
					Hr_canteen_mealsystem mealsys=(Hr_canteen_mealsystem)mealsyss.get(k);
					int idrst=idpath.indexOf(mealsys.idpath.getValue());
					int mcid=Integer.parseInt(mc_id);
					if((mealsys.price.getAsFloat()==pri)&&(mealsys.emplev.getAsInt()==emplev)&&(mealsys.mc_id.getAsInt()==mcid)&&(idrst>=0)){
						if(mealsys.sublimit.getAsInt()>=countsubnums){
							sub=mealsys.subsidies.getAsFloat();
							sublimit=mealsys.sublimit.getAsInt();//特殊用餐申请层级补贴餐数与原层级补贴餐数相同？
							specsubnum++;//特殊用餐申请的补贴次数
							countsubnums++;
							//System.out.println("---ms:"+mealsys.price.getValue()+","+mealsys.ctms_name.getValue()+mealsys.idpath.getValue()+"cr:price"+pri+","+idpath);
							break;
						}
					}
					sub = 0;
					sublimit = 0;
				  }
				}
				tprice=tprice+pri;
				tsub=tsub+sub;
				tpay=tpay+pri-sub;
				BigDecimal btprice = new BigDecimal(String.valueOf(tprice));  
				dtprice = btprice.setScale(1,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
				BigDecimal btsub = new BigDecimal(String.valueOf(tsub));  
				dtsub = btsub.setScale(1,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
				BigDecimal btpay = new BigDecimal(String.valueOf(tpay));  
			    dtpay = btpay.setScale(1,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
			}
			row.put("costnums", specsubnum);
			row.put("subsidies", dtsub);
			row.put("totalpay",dtpay);		
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}
	}
	
	public static int getemplev(String lv_num){
		float lv=Float.parseFloat(lv_num);
		int emplev=0;
		if(lv<3.4){
			emplev=2;
		}else if(lv>=4.0 && lv<4.4){
			emplev=3;
		}else if(lv>5.0 && lv<6.4){
			emplev=4;
		}else if(lv>7.0 && lv<=8.0){
			emplev=5;
		}
		return emplev;
	}
	public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        int r=b2.compareTo(BigDecimal.ZERO);
	    if(r==0){
	    	return b1.doubleValue();
	    }
        return b1.add(b2).setScale(1,   BigDecimal.ROUND_HALF_UP).doubleValue();
    }
	public static double sub(double v1,double v2){
	    BigDecimal b1 = new BigDecimal(Double.toString(v1));
	    BigDecimal b2 = new BigDecimal(Double.toString(v2));
	    int r=b2.compareTo(BigDecimal.ZERO);
	    if(r==0){
	    	return b1.doubleValue();
	    }
	    return b1.subtract(b2).setScale(1,   BigDecimal.ROUND_HALF_UP).doubleValue();
	} 
	
	 public static double mul(double v1, double v2) { 
	        BigDecimal b1 = new BigDecimal(Double.toString(v1)); 
	        BigDecimal b2 = new BigDecimal(Double.toString(v2)); 
	        return  b1.multiply(b2).setScale(1,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
	 }
	 
	 public static double div(double v1, double v2) { 
	        BigDecimal b1 = new BigDecimal(Double.toString(v1)); 
	        BigDecimal b2 = new BigDecimal(Double.toString(v2)); 
	        int r=b2.compareTo(BigDecimal.ZERO);
		    if(r==0){
		    	throw new IllegalArgumentException("除数不能为0");
		    }
	        return b1.divide(b2, 1, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	    } 
	
	@ACOAction(eventname = "dobackupcostrecords", Authentication = true, notes = "将消费记录备份到历史表中")
	public String dobackupcostrecords() throws Exception{
		String tbmonth = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(new Date(), -3), "yyMM");
		String tbname = "hr_canteen_costrecords" + tbmonth;
		String ctsqlstr = "CREATE TABLE IF NOT EXISTS  `" + tbname + "` ("
				+"`core_id` int(20) NOT NULL AUTO_INCREMENT COMMENT '消费打卡记录ID',"
				+" `cardnumber` varchar(64) NOT NULL COMMENT '卡号',"
				+"  `er_id` int(10) DEFAULT NULL COMMENT '档案ID',"
				+"  `employee_code` varchar(16) DEFAULT NULL COMMENT '工号',"
				+"  `employee_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '姓名',"
				+"  `orgid` int(10) NOT NULL COMMENT '部门ID',"
				+" `orgcode` varchar(16) NOT NULL COMMENT '部门编码',"
				+"  `orgname` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '部门名称',"
				+"  `ospid` int(20) DEFAULT NULL COMMENT '职位ID',"
				+"  `ospcode` varchar(16) DEFAULT NULL COMMENT '职位编码',"
				+"  `sp_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '职位名称',"
				+"  `lv_id` int(10) DEFAULT NULL COMMENT '职级ID',"
				+"  `lv_num` decimal(4,1) DEFAULT NULL COMMENT '职级',"
				+"  `ctr_id` int(20) NOT NULL COMMENT '用餐餐厅ID',"
				+"  `ctr_code` varchar(16) NOT NULL COMMENT '用餐餐厅编码',"
				+"  `ctr_name` varchar(64) NOT NULL COMMENT '用餐餐厅名称',"
				+"  `ctcr_id` int(20) NOT NULL COMMENT '卡机ID',"
				+"  `ctcr_code` varchar(16) NOT NULL COMMENT '卡机编号',"
				+"  `ctcr_name` varchar(64) NOT NULL COMMENT '名称',"
				+"  `costtime` datetime DEFAULT NULL COMMENT '刷卡时间',"
				+"  `mc_id` int(20) DEFAULT NULL COMMENT '餐类ID',"
				+"  `mc_name` varchar(64) DEFAULT NULL COMMENT '餐类名称',"
				+"  `cost` decimal(4,2) DEFAULT NULL COMMENT '消费金额',"
				+"  `remark` varchar(512) DEFAULT NULL COMMENT '备注',"
				+"  `entid` int(5) NOT NULL,"
				+"  `creator` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '创建人',"
				+"  `createtime` datetime NOT NULL COMMENT '创建时间',"
				+"  `updator` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '更新人',"
				+"  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',"
				+"  `attribute1` varchar(32) DEFAULT NULL COMMENT '备用字段1',"
				+"  `attribute2` varchar(32) DEFAULT NULL COMMENT '备用字段2',"
				+"  `attribute3` varchar(32) DEFAULT NULL COMMENT '备用字段3',"
				+"  `attribute4` varchar(32) DEFAULT NULL COMMENT '备用字段4',"
				+"  `attribute5` varchar(32) DEFAULT NULL COMMENT '备用字段5',"
				+"  `idpath` varchar(256) NOT NULL COMMENT 'idpath',"
				+"  `inporttime` datetime DEFAULT NULL COMMENT '导入时间',"
				+"  `synid` int(10) DEFAULT NULL COMMENT '关联id',"
				+"  `classtype` int(1) DEFAULT NULL COMMENT '餐类类型',"
				+ "PRIMARY KEY (`core_id`)"
				+") ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='消费打卡记录';";
		Hr_canteen_costrecords crs=new Hr_canteen_costrecords();
		CDBConnection con = crs.pool.getCon(this);
		try{
			con.execsql(ctsqlstr);
			con.execsql("INSERT INTO " + tbname + " (SELECT * FROM hr_canteen_costrecords WHERE costtime<DATE_SUB(CURDATE(),INTERVAL 2 MONTH))");
			con.execsql("DELETE FROM hr_canteen_costrecords WHERE costtime<DATE_SUB(CURDATE(),INTERVAL 2 MONTH)");
		}finally{
			con.close();
		}
		JSONObject rst = new JSONObject();
		rst.put("result", "OK");
		return rst.toString();
	}
	
	@ACOAction(eventname = "dobackuptxlists", Authentication = true, notes = "将原始记录备份到历史表中")
	public String dobackuptxlists() throws Exception{
		HashMap<String, String> parms = CSContext.getParms();
		String ym = CorUtil.hashMap2Str(parms, "ym", "需要参数ym");
		Date bckbfdate = Systemdate.getDateByStr(ym);// 去除时分秒
		String tbmonth = Systemdate.getStrDateByFmt(bckbfdate, "yyMM");
		String tbname = "hrms_txjocatlist" + tbmonth;
		String ctsqlstr = "CREATE TABLE IF NOT EXISTS  `" + tbname + "` ("
				+"`txxf_id` int(20) NOT NULL COMMENT 'ID',"
				+" `rfkh` varchar(64) NOT NULL COMMENT '卡号',"
				+"  `xfjh` varchar(64) NOT NULL COMMENT '消费机号',"
				+"  `xfsj` datetime DEFAULT NULL COMMENT '消费时间',"
				+"  `xfid` varchar(64) DEFAULT NULL COMMENT '关联原记录id',"
				+"  `createTime` datetime NOT NULL COMMENT '创建时间',"
				+" `code` varchar(64) NOT NULL DEFAULT '0' COMMENT '工号',"
				+ "PRIMARY KEY (`txxf_id`)"
				+") ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='原数据库消费记录';";
		Hrms_txjocatlist txls=new Hrms_txjocatlist();
		CDBConnection con = txls.pool.getCon(this);
		try{
			con.execsql(ctsqlstr);
			con.execsql("INSERT INTO " + tbname + " (SELECT * FROM hrms_txjocatlist WHERE xfsj<DATE_ADD('"+Systemdate.getStrDateyyyy_mm_dd(bckbfdate)+"',INTERVAL 1 MONTH))");
			con.execsql("DELETE FROM hrms_txjocatlist WHERE xfsj<DATE_ADD('"+Systemdate.getStrDateyyyy_mm_dd(bckbfdate)+"',INTERVAL 1 MONTH)");
		}finally{
			con.close();
		}
		JSONObject rst = new JSONObject();
		rst.put("result", "OK");
		return rst.toString();
	}
	
	@ACOAction(eventname = "docountguestmeals", Authentication = true, notes = "客餐消费统计")
	public String docountguestmeals() throws Exception{
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jproomcode = CjpaUtil.getParm(jps, "ctr_code");
		//if (jproomcode == null)
			//throw new Exception("需要参数【ctr_code】");
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		if (jpbgtime == null)
			throw new Exception("需要参数【begintime】");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		if (jpedtime == null)
			throw new Exception("需要参数【endtime】");
		String dqbgdate = jpbgtime.getParmvalue();
		String dqeddate = jpedtime.getParmvalue();
		Date bgdate=Systemdate.getDateByStr(dqbgdate);
		Date eddate =Systemdate.getDateByStr(dqeddate);
		Date nowdate=Systemdate.getDateByStr(Systemdate.getStrDate());
		int checkdate=bgdate.compareTo(nowdate);
		int ckdt=bgdate.compareTo(eddate);
		if(checkdate>=0){
			throw new Exception("统计开始日期不能比当前日期大！");
		}
		if(ckdt>0){
			throw new Exception("统计开始日期不能比统计截止日期大！");
		}
		//eddate =Systemdate.dateDayAdd(eddate, 1);// 加一天
		
		String[] ignParms = { "begintime","endtime","ctr_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr=" SELECT ctg.*,ctgl.num,ctgl.guestname,ctgl.guestorg,ctgl.mealstand,ctgl.mc_id,ctgl.mc_name, "+
		"0 AS zaonum,0 AS zaopay,0 AS wunum5,0 AS wupay5,0 AS wunum7,0 AS wupay7,0 AS wannum5,0 AS wanpay5,0 AS wannum7,0 AS wanpay7,0 AS yenum,0 AS yepay "+
		" FROM hr_canteen_guest ctg,hr_canteen_guest_line ctgl WHERE  ctgl.ctg_id=ctg.ctg_id AND ctg.stat=9 AND ctg.usable=1 ";
		if(jproomcode!=null){
			String roomcode = jproomcode.getParmvalue();
			Hr_canteen_room ctroom=new Hr_canteen_room();
			String sqlstr1 ="select * from hr_canteen_room where ctr_code='"+roomcode+"'";
			ctroom.findBySQL(sqlstr1);
			if(ctroom.isEmpty())
				throw new Exception("编码为【" + roomcode + "】的餐厅不存在");
			sqlstr=sqlstr+"AND ctr_id="+ctroom.ctr_id.getValue();
		}
		sqlstr=sqlstr+" AND edday>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"'  AND bgday<='"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' GROUP BY orgid,ctr_id";
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		DecimalFormat decimalFormat=new DecimalFormat(".0");
		for (int i = 0; i < rts.size(); i++) {
			JSONObject row = rts.getJSONObject(i);
			String orgid = row.getString("orgid");
			sqlstr=" SELECT ctg.*,ctgl.num,ctgl.guestname,ctgl.guestorg,ctgl.mealstand,ctgl.mc_id,ctgl.mc_name "+
					" FROM hr_canteen_guest ctg,hr_canteen_guest_line ctgl WHERE  ctgl.ctg_id=ctg.ctg_id AND ctg.stat=9 AND ctg.usable=1 AND ctr_id="+row.getString("ctr_id")+
					" AND edday>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"'  AND bgday<='"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' and orgid="+orgid;
			JSONObject guestrst = new CReport(sqlstr, null).findReport2JSON_O(ignParms,false);
			JSONArray guests = guestrst.getJSONArray("rows");
			int totalnums=0;
			for (int j = 0; j < guests.size(); j++) {
				JSONObject guest = guests.getJSONObject(j);
				if(guest.isEmpty())
					continue;
				Date ft=Systemdate.getDateByStr(Systemdate.getStrDate(),"yyyy-MM-dd");
				Date tt=Systemdate.getDateByStr(Systemdate.getStrDate(),"yyyy-MM-dd");
				Date bgday = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(guest.getString("bgday"))));// 去除时分秒
				Date edday = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(guest.getString("edday"))));// 去除时分秒
				int fdp=bgdate.compareTo(bgday);
				int tdp=eddate.compareTo(edday);
				if(fdp>=0){
					ft=bgdate;
				}else{
					ft=bgday;
				}
				if(tdp>=0){
					tt=edday;
				}else{
					tt=eddate;
				}
				if((guest.has("iseat"))&&("2".equals(guest.getString("iseat")))){
					//System.out.println("非空为2");
					if(guest.has("canceldate")){
						Date ccdate=Systemdate.getDateByStr(guest.getString("canceldate"),"yyyy-MM-dd");
						int ftccd=ft.compareTo(ccdate);
						if(ftccd<0){
							int ttccd=tt.compareTo(ccdate);
							if(ttccd>=0){
								tt=ccdate;
							}
						}else{
							continue;
						}
					}else {
						//System.out.println("日期空");
						continue;
					}
				}
				int days=daysBetween(ft,tt)+1;
				int nums=Integer.parseInt(guest.getString("num"));
				float pri=Float.parseFloat(guest.getString("mealstand"));
				int mcid = Integer.parseInt(guest.getString("mc_id"));
				float pay=pri*nums*days;
				//System.out.println("---nums:"+nums+",days:"+days+",price"+pri+",mcid:"+mcid);
				if(mcid==25){
					int zaonum= Integer.parseInt(row.getString("zaonum"));
					float zaopay=Float.parseFloat(row.getString("zaopay"));
					zaonum=zaonum+nums*days;
					zaopay=zaopay+pay;
					row.put("zaonum", zaonum);
					row.put("zaopay", decimalFormat.format(zaopay));
					//System.out.println("---zaonums:"+zaonum+",zaopay:"+zaopay);
				}
                if(mcid==26){
					if(Math.abs(pri-5.8)<=1e-6){
						int wunum5= Integer.parseInt(row.getString("wunum5"));
						float wupay5=Float.parseFloat(row.getString("wupay5"));
						wunum5=wunum5+nums*days;
						wupay5=wupay5+pay;
						row.put("wunum5", wunum5);
						row.put("wupay5", decimalFormat.format(wupay5));
						//System.out.println("---wunum5:"+wunum5+",wupay5:"+wupay5);
					}else if(Math.abs(pri-7.8)<=1e-6){
						int wunum7= Integer.parseInt(row.getString("wunum7"));
						float wupay7=Float.parseFloat(row.getString("wupay7"));
						wunum7=wunum7+nums*days;
						wupay7=wupay7+pay;
						row.put("wunum7", wunum7);
						row.put("wupay7", decimalFormat.format(wupay7));
						//System.out.println("---wunum7:"+wunum7+",wupay7:"+wupay7);
					}
				}
                if(mcid==27){
                	if(Math.abs(pri-5.8)<=1e-6){
						int wannum5= Integer.parseInt(row.getString("wannum5"));
						float wanpay5=Float.parseFloat(row.getString("wanpay5"));
						wannum5=wannum5+nums*days;
						wanpay5=wanpay5+pay;
						row.put("wannum5", wannum5);
						row.put("wanpay5",  decimalFormat.format(wanpay5));
						//System.out.println("---wannum5:"+wannum5+",wanpay5:"+wanpay5);
					}else if(Math.abs(pri-7.8)<=1e-6){
						int wannum7= Integer.parseInt(row.getString("wannum7"));
						float wanpay7=Float.parseFloat(row.getString("wanpay7"));
						wannum7=wannum7+nums*days;
						wanpay7=wanpay7+pay;
						row.put("wannum7", wannum7);
						row.put("wanpay7", decimalFormat.format(wanpay7));
						//System.out.println("---wannum7:"+wannum7+",wanpay7:"+wanpay7);
					}
				}
                if(mcid==28){
                	int yenum= Integer.parseInt(row.getString("yenum"));
					float yepay=Float.parseFloat(row.getString("yepay"));
					yenum=yenum+nums*days;
					yepay=yepay+pay;
					row.put("yenum", yenum);
					row.put("yepay", decimalFormat.format(yepay));
					//System.out.println("---yenum:"+yenum+",yepay:"+yepay);
				}
                totalnums=totalnums+nums*days;
			}
			row.put("totalnums", totalnums);
			row.put("bgdate", Systemdate.getStrDateyyyy_mm_dd(bgdate));
			row.put("eddate", Systemdate.getStrDateyyyy_mm_dd(eddate));
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}
	}
	
	public static int daysBetween(Date smdate,Date bdate) throws Exception    
    {    
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();      
        long between_days=(time2-time1)/(1000*3600*24);  
        return Integer.parseInt(String.valueOf(between_days));           
    }
	
	public static int dogetsctype(String costtime,String er_id) throws Exception{
		int sctype=0;
		Calendar cale = Calendar.getInstance();
		Date ct = Systemdate.getDateByStr(costtime);
		String yearmonth = Systemdate.getStrDateByFmt(ct, "yyyy-MM");
		cale.setTime(ct);
		int days = cale.get(Calendar.DATE);
		if(days==0){
			return 3;
		}
		int hours=cale.get(Calendar.HOUR_OF_DAY);
		if(hours<3){
			if(days==1){
				Date lastdate=Systemdate.dateDayAdd(ct, -1);
				cale.setTime(lastdate);
			    days = cale.get(Calendar.DATE);
			    yearmonth = Systemdate.getStrDateByFmt(lastdate, "yyyy-MM");
			}else{
				days=days-1;
			}
		}
		String scid="scid"+days;
		
		String sqlstr=" SELECT s.* FROM hrkq_workschmonthlist l,hrkq_sched s "+
		"WHERE l.er_id="+er_id+" AND l."+scid+"=s.scid AND l.yearmonth='"+yearmonth+"'";
		Hrkq_sched sched=new Hrkq_sched();
		sched.findBySQL(sqlstr,false);
		if(!sched.isEmpty()){
			if(!sched.sctype.isEmpty()){
				sctype=sched.sctype.getAsInt();
			}
		}
		return sctype;
	}
	
	public static int dogetmealnums(String costtime,String er_id,int sctype) throws Exception{
		Date ct = Systemdate.getDateByStr(costtime);
		Date firstdate = Systemdate.getFirstAndLastOfMonth(ct).date1;
		String fd = Systemdate.getStrDateyyyy_mm_dd(firstdate);
		String sqlstr="SELECT IFNULL(COUNT(*),0) nums FROM (select * from  hr_canteen_costrecords WHERE costtime>='"+fd+"' AND costtime<'"+costtime+"' AND  er_id="+er_id;	
		if(sctype==1){
			sqlstr=sqlstr+" and (mc_id=26 or mc_id=27) ";
		}else if(sctype==2){
			sqlstr=sqlstr+" and mc_id=28 ";
		}
		sqlstr=sqlstr+" ) ct";
		return Integer.valueOf((new Hr_canteen_costrecords()).pool.openSql2List(sqlstr).get(0).get("nums"));
	}
	
	
	public static int countCostRecords(Date ftime, Date ttime, String empno,HashMap<String, String> empmealnums) throws Exception {
		//System.out.println("==================开始加载数据");
		String sft = Systemdate.getStrDateByFmt(ftime, "yyyy-MM-dd HH:mm:ss");
		String stt = Systemdate.getStrDateByFmt(ttime, "yyyy-MM-dd HH:mm:ss");
		String sqlstr = "SELECT MAX(synid) mx FROM `hr_canteen_costrecordscount` WHERE costtime>='" + sft + "' AND costtime<'" + stt + "' ";
		if (empno != null)
			sqlstr = sqlstr + " and employee_code='" + empno + "' ";
		Hr_canteen_costrecordscount crcs=new Hr_canteen_costrecordscount();
		String[] ignParms = { };
		List<HashMap<String, String>> sws = crcs.pool.openSql2List(sqlstr);
		long mx = ((sws.size() == 0) || (sws.get(0).get("mx") == null)) ? 0 : Long.valueOf(sws.get(0).get("mx"));
		//CJPALineData<Hr_canteen_costrecords> txjls = new CJPALineData<Hr_canteen_costrecords>(Hr_canteen_costrecords.class);
		sqlstr=" SELECT  cr.* ,IFNULL(crms.price,0) AS price "+
				" FROM (SELECT * FROM `hr_canteen_costrecords` WHERE  costtime>='" + sft + "' AND costtime<'" + stt + "' and synid>"+mx;
		if (empno != null)
			sqlstr = sqlstr + " and employee_code='" + empno + "' ";
		sqlstr=sqlstr+" ) cr LEFT JOIN hr_canteen_cardrelatems crms ON cr.ctcr_id=crms.ctcr_id AND cr.classtype=crms.classtype order by synid LIMIT 0,5000 ";
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms,false);
		JSONArray rts = jo.getJSONArray("rows");
		CJPALineData<Hr_canteen_costrecordscount> hrctcrcs = new CJPALineData<Hr_canteen_costrecordscount>(Hr_canteen_costrecordscount.class);
		int rst=rts.size();
		CJPALineData<Hr_canteen_mealsystem> mealsyss = new CJPALineData<Hr_canteen_mealsystem>(Hr_canteen_mealsystem.class);
		mealsyss.findDataBySQL("SELECT * FROM  hr_canteen_mealsystem ms ORDER BY ms.idpath DESC");
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		int countsubnums=0;
		//Hr_canteen_mealsystem ms=new Hr_canteen_mealsystem();
		
		for (int i = 0; i < rst; i++) {
			JSONObject row = rts.getJSONObject(i);
			if(row.isEmpty())
				continue;
			int classtype = Integer.parseInt(row.getString("classtype"));
			String lv_num =row.getString("lv_num");
			int issub=2;
			float mprice =Float.parseFloat(row.getString("price"));
			double subsidies = 0;
			int sublimit = 0;
			String idpath=row.getString("idpath");
			String costtime =row.getString("costtime");
			String empid=row.getString("er_id");
			//Hr_canteen_mealsystem ms=new Hr_canteen_mealsystem();
			int emplev=getemplev(lv_num);
			String ct = dictemp.getVbCE("1107", row.getString("mc_name"), false, "餐类类型【" + row.getString("mc_name")
					+ "】不存在");
			Hr_canteen_special spec=new Hr_canteen_special();
			spec.findBySQL("SELECT * FROM hr_canteen_special sp WHERE sp.stat=9 AND sp.usable=1 AND sp.er_id="+empid+
					"  AND sp.class_type LIKE '%"+ct+"%' "+
					" AND ((sp.applytimetype=2 and sp.appbg_date<='"+costtime+"' AND '"+costtime+"'<=sp.apped_date)"+
					" or (sp.applytimetype=1 and sp.apply_date<='"+costtime+"'))");//餐厅?
			if(!spec.isEmpty()){
				if(spec.class_type.getValue().indexOf(ct)!=-1){
					if(spec.subsidiestype.getAsInt()==2)
					   emplev=2;
					if(spec.subsidiestype.getAsInt()==1)
						   emplev=3;
				}
			}
			//System.out.println("lev:"+emplev);
			
			int sctype=dogetsctype(costtime,empid);
			/*int dmnums=0;
			int ymnums=0;
			Calendar cal = Calendar.getInstance();
			Date costdate=Systemdate.getDateByStr(costtime);
			cal.setTime(costdate);
			int ctday=cal.get(Calendar.DATE);
			if(ctday>16){
				dmnums=dogetmealnums(costtime,row.getString("er_id"),1);
				if(sctype==2){
					ymnums=dogetmealnums(costtime,row.getString("er_id"),2);
				}
			}*/
			if((((sctype<2)&&((classtype==1)||(classtype==2)||(classtype==3))))||((sctype==2)&&(classtype==4))){
				int mns=0;
				 // for(int k=0;k<mealsyss.size();k++){
					for (CJPABase msjpa : mealsyss) {
					Hr_canteen_mealsystem mealsys=(Hr_canteen_mealsystem)msjpa;
					//Hr_canteen_mealsystem mealsys=(Hr_canteen_mealsystem)mealsyss.get(k);
					int idrst=idpath.indexOf(mealsys.idpath.getValue());
					if((mealsys.price.getAsFloat()==mprice)&&(mealsys.emplev.getAsInt()==emplev)&&(mealsys.classtype.getAsInt()==classtype)&&(idrst>=0)){
						/*
						String empnums=empmealnums.get(empid);
						if(empnums==null){
							mns=0;
						}else{
							mns=Integer.parseInt(empnums);
						}
						//System.out.println("subnums:"+mns);*/
						if(mealsys.subsidies.getAsFloatDefault(0)==0){
							break;
						}
						if(empmealnums.containsKey(empid)){
							String mealnums=empmealnums.get(empid);
						    mns=Integer.parseInt(mealnums);
						}
						
						if(mealsys.sublimit.getAsInt()>mns){	
							subsidies=(double)(mealsys.subsidies.getAsFloat());
							sublimit=mealsys.sublimit.getAsInt();//特殊用餐申请层级补贴餐数与原层级补贴餐数相同？
							issub=1;
							mns++;
							empmealnums.put(empid, String.valueOf(mns));
							row.put("attribute1", String.valueOf(mns));
							//System.out.println("---ms:"+mealsys.price.getValue()+","+mealsys.ctms_name.getValue()+"cr:price"+mprice+",subnums:"+mns+",补贴餐数："+mealsys.sublimit.getValue());
							break;
						}
					}
					subsidies = 0;
					 sublimit = 0;
				  }
				
				/*String empnums=empmealnums.get(empid);
				if(empnums==null){
					mns=0;
				}else{
					mns=Integer.parseInt(empnums);
				}
				//System.out.println("subnums:"+mns);
				ms.clear();
				ms.findBySQL("SELECT ms.* FROM hr_canteen_mealsystem ms WHERE ms.classtype="+classtype+" AND ms.price="+mprice+
						" AND ms.emplev="+emplev+" AND LOCATE(ms.idpath,'"+idpath+"')=1 and ms.sublimit>"+mns+" ORDER BY ms.idpath DESC", false);
				if(!ms.isEmpty()){
					subsidies=(double)(ms.subsidies.getAsFloat());
					sublimit=ms.sublimit.getAsInt();//特殊用餐申请层级补贴餐数与原层级补贴餐数相同？
					issub=1;
					mns++;
					empmealnums.put(empid, String.valueOf(mns));
					//System.out.println("---ms:"+ms.price.getValue()+","+ms.ctms_name.getValue()+"cr:price"+mprice+",subnums:"+mns+",补贴餐数："+ms.sublimit.getValue());
				}else{
					subsidies = 0;
					 sublimit = 0;
				}*/
				}
			//System.out.println("-----------------"+issub);
				double price =Double.parseDouble(row.getString("price"));
				if(classtype==1){
					row.put("zaonum", 1);
					row.put("zaopay", price);
					row.put("zaosub", subsidies);
				}
				if(classtype==2){
					row.put("wunum", 1);
					row.put("wupay", price);
					if(price==5.8){
						row.put("wunum5", 1);
						row.put("wupay5", price);
						row.put("wuwannum5", 1);
					}
					if(price==7.8){
						row.put("wunum7", 1);
						row.put("wupay7", price);
						row.put("wuwannum7", 1);
					}
					row.put("wusub", subsidies);
				}
				if(classtype==3){
					row.put("wannum", 1);
					row.put("wanpay", price);
					if(price==5.8){
						row.put("wannum5", 1);
						row.put("wanpay5", price);
						row.put("wuwannum5", 1);
					}
					if(price==7.8){
						row.put("wannum7", 1);
						row.put("wanpay7", price);
						row.put("wuwannum7", 1);
					}
					row.put("wansub", subsidies);
				}
				if(classtype==4){
					row.put("yenum", 1);
					row.put("yepay", price);
					row.put("yesub", subsidies);
				}
				
				row.put("totalpay", price);
				row.put("totalsub", subsidies);
				row.put("issub", issub);
				row.put("inporttime", new Date());
				row.put("sublimit", sublimit);
				Hr_canteen_costrecordscount crc=new Hr_canteen_costrecordscount();
				crc.fromjson(row.toString());
				//crc.inporttime.setAsDatetime(new Date());// 导入时间
				//crc.sublimit.setAsInt(sublimit);
				countsubnums++;
				//System.out.println("=======消费时间【" + crc.costtime.getValue()+",处理条数："+countsubnums);
				hrctcrcs.add(crc);
		}
		if(hrctcrcs.size()>0){
			hrctcrcs.saveBatchSimple();
			//System.out.println("====================计算消费统计记录条数【" + hrctcrcs.size());
		}
		hrctcrcs.clear();
		return rst;
	}
	
	@ACOAction(eventname = "docountrecords", Authentication = true, notes = "将消费记录计算后插入消费统计记录表")
	public String docountrecords() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String sbgdate = CorUtil.hashMap2Str(parms, "bgdate", "需要参数bgdate");
		String seddate = CorUtil.hashMap2Str(parms, "eddate", "需要参数eddate");
		String empcode = CorUtil.hashMap2Str(parms, "empcode");
		Date  bgdate=Systemdate.getDateByStr(sbgdate);
		Date ft=Systemdate.getFirstAndLastOfMonth(bgdate).date1;
		Date tt=Systemdate.dateMonthAdd(ft, 1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(bgdate);
		int bgday=cal.get(Calendar.DATE);
		if((bgdate.compareTo(ft)>0)&&(bgday>1)){
			throw new Exception("开始日期不能大于1号！ ");
		}
		Date	eddate =Systemdate.getDateByStr(seddate);
		if(eddate.compareTo(tt)>=0){
			throw new Exception("截止日期大于开始日期月份最后一天，不能跨月！ ");
		}
		eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
		int bte=bgdate.compareTo(eddate);
		if(bte>=0){
			throw new Exception("截止日期要比开始日期大！ ");
		}
		String sqlstr = null;
		sqlstr = "DELETE FROM hr_canteen_costrecordscount WHERE costtime>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND costtime<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "'  ";
		if(empcode!=null){
			sqlstr = sqlstr + " and employee_code='" + empcode + "' ";
		}
		Hr_canteen_costrecordscount crcs = new Hr_canteen_costrecordscount();
		crcs.pool.execsql(sqlstr);// 删除已经存在的数据
		HashMap<String, String> empmealnums=new HashMap<String, String>(1048576);
		while (true) {
			int countct = countCostRecords( bgdate, eddate, empcode,empmealnums);
			//System.out.println("导入消费记录【" + countct + "】条【" + Systemdate.getStrDate() + "】");
			if (countct == 0)
				break;
		}
		//System.out.println("计算统计消费记录，处理完毕" + Systemdate.getStrDate());
		JSONObject rst = new JSONObject();
		rst.put("rst", "ok");
		return rst.toString();
	}
	
	
	@ACOAction(eventname = "costSummaryEx2", Authentication = true, notes = "餐费汇总表Ex2")
	public String costSummaryEx2() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "begintime","endtime","orgcode","employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT crc.*,COUNT(*) AS nums,SUM(CASE WHEN issub=1 THEN 1 ELSE 0 END) AS subnums,SUM(wuwannum5) AS ww5,SUM(wuwannum7) AS ww7,(SUM(wupay)+SUM(wanpay)) AS wuwanprice,"+
			"(SUM(wusub)+SUM(wansub)) AS wuwansub,SUM(zaonum) AS znum,SUM(zaopay) AS zaoprice,SUM(zaosub) AS zsub,SUM(yenum) AS ynum,SUM(yepay) AS yeprice,SUM(yesub) AS ysub,"+
			"SUM(totalpay) AS totalprice,SUM(totalsub) AS ttsub,(SUM(totalpay)-SUM(totalsub)) AS ttpay "+
			" FROM `hr_canteen_costrecordscount` crc WHERE idpath like '"+org.idpath.getValue()+"%' ";
		if(jpbgtime != null){
			String dqbgdate = jpbgtime.getParmvalue();	
			Date bgdate=Systemdate.getDateByStr(dqbgdate);
			Date eddate =Systemdate.getDateByStr(Systemdate.getStrDate());
			if(jpedtime != null){
				String dqeddate = jpedtime.getParmvalue();
				eddate =Systemdate.getDateByStr(dqeddate);
			}
			//int cp=bgdate.compareTo(eddate);
			//if(cp==0){
				eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
				sqlstr=sqlstr+" and costtime>='"+ Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+ Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ";
		}
		if(jpempcode!=null){
			String empno=jpempcode.getParmvalue();
			sqlstr=sqlstr+" and employee_code='"+empno+"' ";
		}
		sqlstr=sqlstr+"GROUP BY er_id";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "costSummaryEx3", Authentication = true, notes = "费用结算月报")
	public String costSummaryEx3() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "begintime","endtime","orgcode","employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT e.*,crc.ctr_name,crc.cardnumber,COUNT(*) AS nums,SUM(wuwannum5) AS ww5,SUM(wuwannum7) AS ww7,(SUM(wupay)+SUM(wanpay)) AS wuwanprice,"+
			"(SUM(wusub)+SUM(wansub)) AS wuwansub,SUM(wunum) AS twunum,SUM(wannum) AS twannum,SUM(wusub) AS twusub,SUM(wansub) AS twansub,"+
			"SUM(zaonum) AS znum,SUM(zaopay) AS zaoprice,SUM(zaosub) AS zaosub,SUM(yenum) AS ynum,SUM(yepay) AS yeprice,SUM(yesub) AS ysub,"+
			"SUM(totalpay) AS totalprice,SUM(totalsub) AS ttsub,SUM(totalsub) AS ttcompay,(SUM(totalpay)-SUM(totalsub)) AS ttpay "+
			" FROM `hr_canteen_costrecordscount` crc,hr_employee e WHERE crc.idpath like '"+org.idpath.getValue()+"%' ";
		if(jpbgtime != null){
			String dqbgdate = jpbgtime.getParmvalue();	
			Date bgdate=Systemdate.getDateByStr(dqbgdate);
			Date eddate =Systemdate.getDateByStr(Systemdate.getStrDate());
			if(jpedtime != null){
				String dqeddate = jpedtime.getParmvalue();
				eddate =Systemdate.getDateByStr(dqeddate);
			}
			eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
			sqlstr=sqlstr+" and crc.costtime>='"+ Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND crc.costtime<'"+ Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ";
		}
		if(jpempcode!=null){
			String empno=jpempcode.getParmvalue();
			sqlstr=sqlstr+" and crc.employee_code='"+empno+"' ";
		}
		sqlstr=sqlstr+" AND crc.er_id=e.er_id GROUP BY er_id,ctr_id";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "costSummaryEx4", Authentication = true, notes = "就餐统计")
	public String costSummaryEx4() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "begintime");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "endtime");
		
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "begintime","endtime","orgcode","employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT crc.*,COUNT(*) AS costnum,"+
			"SUM(totalpay) AS totalprice,SUM(totalsub) AS ttsub,(SUM(totalpay)-SUM(totalsub)) AS ttpay "+
			" FROM `hr_canteen_costrecordscount` crc WHERE idpath like '"+org.idpath.getValue()+"%' ";
		if(jpbgtime != null){
			String dqbgdate = jpbgtime.getParmvalue();	
			Date bgdate=Systemdate.getDateByStr(dqbgdate);
			Date eddate =Systemdate.getDateByStr(Systemdate.getStrDate());
			if(jpedtime != null){
				String dqeddate = jpedtime.getParmvalue();
				eddate =Systemdate.getDateByStr(dqeddate);
			}
			eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
			sqlstr=sqlstr+" and costtime>='"+ Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+ Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ";
		}
		if(jpempcode!=null){
			String empno=jpempcode.getParmvalue();
			sqlstr=sqlstr+" and employee_code='"+empno+"' ";
		}
		sqlstr=sqlstr+"GROUP BY er_id,mc_id,ctcr_id,totalpay";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "costSummaryEx5", Authentication = true, notes = "消费明细")
	public String costSummaryEx5() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		JSONParm jpempcode = CjpaUtil.getParm(jps, "employee_code");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();
		JSONParm jpbgtime = CjpaUtil.getParm(jps, "costbg");
		JSONParm jpedtime = CjpaUtil.getParm(jps, "costed");
		
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String[] ignParms = { "costbg","costed","orgcode","employee_code" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT cr.*,(cr.totalpay-cr.totalsub) as selfcost,cr.totalsub as ttsub FROM hr_canteen_costrecordscount cr where cr.idpath like '"+org.idpath.getValue()+"%' ";
		if(jpbgtime != null){
			String dqbgdate = jpbgtime.getParmvalue();	
			Date bgdate=Systemdate.getDateByStr(dqbgdate);
			Date eddate =Systemdate.getDateByStr(Systemdate.getStrDate());
			if(jpedtime != null){
				String dqeddate = jpedtime.getParmvalue();
				eddate =Systemdate.getDateByStr(dqeddate);
			}
			eddate = Systemdate.dateDayAdd(eddate, 1);// 加一天
			sqlstr=sqlstr+" and costtime>='"+ Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'"+ Systemdate.getStrDateyyyy_mm_dd(eddate)+"' ";
		}
		if(jpempcode!=null){
			String empno=jpempcode.getParmvalue();
			sqlstr=sqlstr+" and employee_code='"+empno+"' order by costtime asc ";
		}else{
			sqlstr=sqlstr+" order by orgid";
		}
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "dorecounts", Authentication = true, notes = "重新计算超过补贴餐数的消费记录")
	public String dorecounts() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String sbgdate = CorUtil.hashMap2Str(parms, "bgdate", "需要参数bgdate");
		//String seddate = CorUtil.hashMap2Str(parms, "eddate", "需要参数eddate");
		Date  bgdate=Systemdate.getDateByStr(sbgdate);
		Date ft=Systemdate.getFirstAndLastOfMonth(bgdate).date1;
		Date tt=Systemdate.dateMonthAdd(ft, 1);
		 dofinalcountrecords(ft, tt);
		//System.out.println("重新计算消费记录，处理完毕" + Systemdate.getStrDate());
		JSONObject rst = new JSONObject();
		rst.put("rst", "ok");
		return rst.toString();
	}
	
	public void dofinalcountrecords(Date bgdate, Date eddate) throws Exception{
		String sqlstr = "SELECT crc.er_id,crc.classtype,COUNT(*) AS nums FROM `hr_canteen_costrecordscount` crc WHERE"+
	" costtime>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND costtime<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) +
	"'  AND issub=1 AND idpath NOT LIKE '1,203,5445,%' GROUP BY er_id HAVING COUNT(*)>50";
		String[] ignParms = { };
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms,false);
		JSONArray rts = jo.getJSONArray("rows");
		int rst=rts.size();
		List<String> sqls = new ArrayList<String>();
		Hr_canteen_costrecordscount crcs = new Hr_canteen_costrecordscount();
		//CDBConnection con = crcs.pool.getCon(this);
		//con.startTrans();
		//try{
		for (int i = 0; i < rst; i++) {
			JSONObject row = rts.getJSONObject(i);
			if(row.isEmpty())
				continue;
			String empid=row.getString("er_id");
			String classtype=row.getString("classtype");
			int ct=Integer.parseInt(classtype);
			String sqlstr1="UPDATE `hr_canteen_costrecordscount` crc SET crc.issub=2,crc.sublimit=0,crc.totalsub=0 ";
			if(ct==2){
				sqlstr1=sqlstr1+",crc.wusub=0 ";
			}
			if(ct==3){
				sqlstr1=sqlstr1+",crc.wansub=0 ";
			}
			if(ct==4){
				sqlstr1=sqlstr1+",crc.yesub=0 ";
			}		
			sqlstr1=sqlstr1+" WHERE costtime>='" +Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'" +Systemdate.getStrDateyyyy_mm_dd(eddate) +
			"' AND issub=1 AND er_id="+empid+" AND corec_id IN (SELECT corec_id FROM (SELECT * FROM `hr_canteen_costrecordscount`"+
					" WHERE costtime>='" +Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' AND costtime<'" +Systemdate.getStrDateyyyy_mm_dd(eddate) +
			"' AND issub=1 AND er_id="+empid+" ORDER BY synid LIMIT 50,100) tt)";
			//crcs.pool.execsql(sqlstr1);
			////System.out.println("计算超过补贴餐数的消费记录" + Systemdate.getStrDate());
			sqls.add(sqlstr1);
			if ((i + 1) % 10 == 0) {
				crcs.pool.execSqls(sqls);
				sqls.clear();
				//System.out.println("计算超过补贴餐数的消费记录，处理10名员工" + Systemdate.getStrDate());
			}
		}
		crcs.pool.execSqls(sqls);
		//con.submit();
		//System.out.println("计算超过补贴餐数的消费记录，处理完成" + Systemdate.getStrDate());
		//} catch (Exception e) {
		//	con.rollback();
		//	throw e;
		//}
	}

}
