package com.hr.salary.co;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
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
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.salary.entity.Hr_salary_chglg;
import com.hr.salary.entity.Hr_salary_list;
import com.hr.salary.entity.Hr_salary_orgminstandard;
import com.hr.salary.entity.Hr_salary_structure;
import com.hr.util.HRUtil;

@ACO(coname = "web.hrsalary.list")
public class COHr_salary_list {
	@ACOAction(eventname = "impsalarylistexcel", Authentication = true, ispublic = false, notes = "导入薪资明细")
	public String impsalarylistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_salary_list(p, batchno);
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

	private int parserExcelFile_salary_list(Shw_physic_file pf, String batchno) throws Exception {
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
		return parserExcelSheet_salary_list(aSheet, batchno);
	}

	private int parserExcelSheet_salary_list(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_salary_list();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		
		CJPALineData<Hr_salary_list> newsllists = new CJPALineData<Hr_salary_list>(Hr_salary_list.class);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		int rst = 0;
			for (Map<String, String> v : values) {
				int m=rst%2000;
				if(m==0){
					if (newsllists.size() > 0) {
						System.out.println("====================导入工资明细条数【" + newsllists.size() + "】");
						newsllists.saveBatchSimple();// 高速存储
					}
					newsllists.clear();
				}
				String employee_code = v.get("employee_code");
				if ((employee_code == null) || (employee_code.isEmpty()))
					throw new Exception("薪资明细上的工号不能为空");
				int wgtype = Integer.valueOf(dictemp.getVbCE("1447", v.get("wagetype"), false, "员工【" + employee_code + "】薪资类型【" + v.get("wagetype")
						+ "】不存在"));//薪资类型
				if(wgtype==2){
					throw new Exception("工号为【"+employee_code+"】的薪资类型为非月薪人员，不正确！");
				}
				Hr_employee emp = new Hr_employee();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
				if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在人事资料");
				
				Hr_orgposition sp=new Hr_orgposition();
				sp.findByID(emp.ospid.getValue());
				if(sp.isEmpty())
					throw new Exception("工号【" + employee_code + "】的ID为【"+emp.ospid.getValue()+"】的职位不存在");
				
				Hr_salary_list sall = new Hr_salary_list();
				sall.remark.setValue(v.get("remark")); // 备注
				sall.yearmonth.setValue(v.get("yearmonth")); // 年月
				sall.er_id.setValue(emp.er_id.getValue()); // 人事档案id
				sall.employee_code.setValue(emp.employee_code.getValue()); // 申请人工号
				sall.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				sall.orgid.setValue(emp.orgid.getValue()); // 部门
				sall.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				sall.orgname.setValue(emp.orgname.getValue()); // 部门名称
				sall.idpath.setValue(emp.idpath.getValue()); // idpath
				sall.ospid.setValue(emp.ospid.getValue()); // 职位id
				sall.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				sall.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				sall.lv_id.setValue(emp.lv_id.getValue()); // 职级id
				sall.lv_num.setValue(emp.lv_num.getValue()); // 职级
				sall.hg_id.setValue(emp.hg_id.getValue()); // 职等id
				sall.hg_name.setValue(emp.hg_name.getValue()); // 职等
				sall.hwc_idzl.setValue(sp.hwc_idzl.getValue()); // 职类id
				sall.hwc_namezl.setValue(sp.hwc_namezl.getValue()); // 职类
				sall.hwc_idzq.setValue(sp.hwc_idzq.getValue()); // 职群id
				sall.hwc_namezq.setValue(sp.hwc_namezq.getValue()); // 职群
				sall.hwc_idzz.setValue(sp.hwc_idzz.getValue()); // 职种id
				sall.hwc_namezz.setValue(sp.hwc_namezz.getValue()); // 职种
				sall.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				String imporgname = v.get("orgname");
				String impspname = v.get("sp_name");
				if (((imporgname != null))&&((impspname != null) )){
					//if(!imporgname.equals(emp.orgname.getValue())){
						Shworg imporg=new Shworg();
						imporg.findBySQL("select * from shworg where extorgname='"+imporgname+"'");
						if(!imporg.isEmpty()){
							sall.orgid.setValue(imporg.orgid.getValue()); // 部门
							sall.orgcode.setValue(imporg.code.getValue()); // 部门编码
							sall.orgname.setValue(imporg.extorgname.getValue()); // 部门名称
							sall.idpath.setValue(imporg.idpath.getValue()); // idpath
						}
					//}
						Hr_orgposition impsp=new Hr_orgposition();
						impsp.findBySQL("SELECT * FROM `hr_orgposition` WHERE sp_name='"+impspname+"' AND idpath ='"+imporg.idpath.getValue()+"'");
						if(!impsp.isEmpty()){
							sall.ospid.setValue(impsp.ospid.getValue()); // 职位id
							sall.ospcode.setValue(impsp.ospcode.getValue()); // 职位编码
							sall.sp_name.setValue(impsp.sp_name.getValue()); // 职位名称
							sall.lv_id.setValue(impsp.lv_id.getValue()); // 职级id
							sall.lv_num.setValue(impsp.lv_num.getValue()); // 职级
							sall.hg_id.setValue(impsp.hg_id.getValue()); // 职等id
							sall.hg_name.setValue(impsp.hg_name.getValue()); // 职等
							sall.hwc_idzl.setValue(impsp.hwc_idzl.getValue()); // 职类id
							sall.hwc_namezl.setValue(impsp.hwc_namezl.getValue()); // 职类
							sall.hwc_idzq.setValue(impsp.hwc_idzq.getValue()); // 职群id
							sall.hwc_namezq.setValue(impsp.hwc_namezq.getValue()); // 职群
							sall.hwc_idzz.setValue(impsp.hwc_idzz.getValue()); // 职种id
							sall.hwc_namezz.setValue(impsp.hwc_namezz.getValue()); // 职种
						}
				}
				
					String struname=v.get("stru_name");
					Hr_salary_structure  ss=new Hr_salary_structure();
					ss.findBySQL("select * from hr_salary_structure where stat=9 and stru_name='"+struname+"'");
					if(ss.isEmpty()){
						throw new Exception("工号为【"+employee_code+"】的工资结构【"+struname+"】不存在！");
					}
					sall.stru_id.setValue(ss.stru_id.getValue()); // 工资结构id
					sall.stru_name.setValue(ss.stru_name.getValue()); // 工资结构
					sall.poswage.setValue(v.get("poswage")); // 职位工资
					float poswage=Float.parseFloat(v.get("poswage"));
					if(ss.strutype.getAsInt()==1){
						float minstand=0;
						String sqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+emp.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC  ";
						Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
						oms.findBySQL(sqlstr);
						if(oms.isEmpty()){
							minstand=0;
						}else{
							minstand=oms.minstandard.getAsFloatDefault(0);
						}
						float bw=(poswage*ss.basewage.getAsFloatDefault(0))/100;
						float bow=(poswage*ss.otwage.getAsFloatDefault(0))/100;
						float sw=(poswage*ss.skillwage.getAsFloatDefault(0))/100;
						float pw=(poswage*ss.meritwage.getAsFloatDefault(0))/100;
						if(minstand>bw){
							if((bw+bow)>minstand){
								bow=bw+bow-minstand;
								bw=minstand;
							}else if((bw+bow+sw)>minstand){
								sw=bw+bow+sw-minstand;
								bow=0;
								bw=minstand;
							}else if((bw+bow+sw+pw)>minstand){
								pw=bw+bow+sw+pw-minstand;
								sw=0;
								bow=0;
								bw=minstand;
							}else{
								bw=poswage;
								pw=0;
								sw=0;
								bow=0;
							}
						}
						DecimalFormat df=new DecimalFormat(".00");
						sall.basewage.setValue(df.format(bw)); // 基本工资
						sall.baseotwage.setValue(df.format(bow)); // 固定加班工资
						sall.skillwage.setValue(df.format(sw)); // 技能工资
						sall.perforwage.setValue(df.format(pw)); // 绩效工资
					}else{
						sall.basewage.setValue(v.get("basewage")); // 基本工资
						sall.baseotwage.setValue(v.get("baseotwage")); // 固定加班工资
						sall.skillwage.setValue(v.get("skillwage")); // 技能工资
						sall.perforwage.setValue(v.get("perforwage")); // 绩效工资
					}
					sall.skillsubs.setValue(v.get("skillsubs")); // 技能津贴
					sall.parttimesubs.setValue(v.get("parttimesubs")); // 兼职津贴
					sall.postsubs.setValue(v.get("postsubs")); // 岗位津贴
				sall.wagetype.setValue(wgtype);//薪资类型
				sall.usable.setAsInt(1);//有效
				newsllists.add(sall);
				rst++;
			}
			if (newsllists.size() > 0) {
				System.out.println("====================导入月薪人员工资明细条数【" + newsllists.size() + "】");
				newsllists.saveBatchSimple();// 高速存储
			}
			newsllists.clear();
			return rst;
	}

	private List<CExcelField> initExcelFields_salary_list() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("年月", "yearmonth", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("工资结构", "stru_name", true));
		efields.add(new CExcelField("薪资类型", "wagetype", true));
		efields.add(new CExcelField("职位工资", "poswage", true));
		efields.add(new CExcelField("基本工资", "basewage", true));
		efields.add(new CExcelField("固定加班工资", "baseotwage", true));
		efields.add(new CExcelField("技能工资", "skillwage", true));
		efields.add(new CExcelField("绩效工资", "perforwage", true));
		efields.add(new CExcelField("技术津贴", "skillsubs", true));
		efields.add(new CExcelField("兼职津贴", "parttimesubs", true));
		efields.add(new CExcelField("岗位津贴", "postsubs", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}
	
	@ACOAction(eventname = "impsalarynotmonthlistexcel", Authentication = true, ispublic = false, notes = "导入非月薪人员薪资明细")
	public String impsalarynotmonthlistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_salarynotmonth_list(p, batchno);
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

	private int parserExcelFile_salarynotmonth_list(Shw_physic_file pf, String batchno) throws Exception {
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
		return parserExcelSheet_salarynoymonth_list(aSheet, batchno);
	}

	private int parserExcelSheet_salarynoymonth_list(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_salarynotmonth_list();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		
		CJPALineData<Hr_salary_list> newsllists = new CJPALineData<Hr_salary_list>(Hr_salary_list.class);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		int rst = 0;
		for (Map<String, String> v : values) {
			int m=rst%2000;
			if(m==0){
				if (newsllists.size() > 0) {
					System.out.println("====================导入工资明细条数【" + newsllists.size() + "】");
					newsllists.saveBatchSimple();// 高速存储
				}
				newsllists.clear();
			}
			String employee_code = v.get("employee_code");
			if ((employee_code == null) || (employee_code.isEmpty()))
				throw new Exception("薪资明细上的工号不能为空");
			int wgtype = Integer.valueOf(dictemp.getVbCE("1447", v.get("wagetype"), false, "员工【" + employee_code + "】薪资类型【" + v.get("wagetype")
					+ "】不存在"));//薪资类型
			if(wgtype==1){
				throw new Exception("工号为【"+employee_code+"】的薪资类型为月薪人员，不正确！");
			}
			Hr_employee emp = new Hr_employee();
			emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
			if (emp.isEmpty())
				throw new Exception("工号【" + employee_code + "】不存在人事资料");
			
			Hr_orgposition sp=new Hr_orgposition();
			sp.findByID(emp.ospid.getValue());
			if(sp.isEmpty())
				throw new Exception("工号【" + employee_code + "】的ID为【"+emp.ospid.getValue()+"】的职位不存在");
			
			Hr_salary_list sall = new Hr_salary_list();
			sall.remark.setValue(v.get("remark")); // 备注
			sall.yearmonth.setValue(v.get("yearmonth")); // 年月
			sall.er_id.setValue(emp.er_id.getValue()); // 人事档案id
			sall.employee_code.setValue(emp.employee_code.getValue()); // 申请人工号
			sall.employee_name.setValue(emp.employee_name.getValue()); // 姓名
			sall.orgid.setValue(emp.orgid.getValue()); // 部门
			sall.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
			sall.orgname.setValue(emp.orgname.getValue()); // 部门名称
			sall.ospid.setValue(emp.ospid.getValue()); // 职位id
			sall.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
			sall.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
			sall.lv_id.setValue(emp.lv_id.getValue()); // 职级id
			sall.idpath.setValue(emp.idpath.getValue()); // idpath
			sall.lv_num.setValue(emp.lv_num.getValue()); // 职级
			sall.hg_id.setValue(emp.hg_id.getValue()); // 职等id
			sall.hg_name.setValue(emp.hg_name.getValue()); // 职等
			sall.hwc_idzl.setValue(sp.hwc_idzl.getValue()); // 职类id
			sall.hwc_namezl.setValue(sp.hwc_namezl.getValue()); // 职类
			sall.hwc_idzq.setValue(sp.hwc_idzq.getValue()); // 职群id
			sall.hwc_namezq.setValue(sp.hwc_namezq.getValue()); // 职群
			sall.hwc_idzz.setValue(sp.hwc_idzz.getValue()); // 职种id
			sall.hwc_namezz.setValue(sp.hwc_namezz.getValue()); // 职种
			sall.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
			
			String imporgname = v.get("orgname");
			String impspname = v.get("sp_name");
			if (((imporgname != null) )&&((impspname != null) )){
				//if(!imporgname.equals(emp.orgname.getValue())){
					Shworg imporg=new Shworg();
					imporg.findBySQL("select * from shworg where extorgname='"+imporgname+"'");
					if(!imporg.isEmpty()){
						sall.orgid.setValue(imporg.orgid.getValue()); // 部门
						sall.orgcode.setValue(imporg.code.getValue()); // 部门编码
						sall.orgname.setValue(imporg.extorgname.getValue()); // 部门名称
						sall.idpath.setValue(imporg.idpath.getValue()); // idpath
					}
				//}
					Hr_orgposition impsp=new Hr_orgposition();
					impsp.findBySQL("SELECT * FROM `hr_orgposition` WHERE sp_name='"+impspname+"' AND idpath ='"+imporg.idpath.getValue()+"'");
					if(!impsp.isEmpty()){
						sall.ospid.setValue(impsp.ospid.getValue()); // 职位id
						sall.ospcode.setValue(impsp.ospcode.getValue()); // 职位编码
						sall.sp_name.setValue(impsp.sp_name.getValue()); // 职位名称
						sall.lv_id.setValue(impsp.lv_id.getValue()); // 职级id
						sall.lv_num.setValue(impsp.lv_num.getValue()); // 职级
						sall.hg_id.setValue(impsp.hg_id.getValue()); // 职等id
						sall.hg_name.setValue(impsp.hg_name.getValue()); // 职等
						sall.hwc_idzl.setValue(impsp.hwc_idzl.getValue()); // 职类id
						sall.hwc_namezl.setValue(impsp.hwc_namezl.getValue()); // 职类
						sall.hwc_idzq.setValue(impsp.hwc_idzq.getValue()); // 职群id
						sall.hwc_namezq.setValue(impsp.hwc_namezq.getValue()); // 职群
						sall.hwc_idzz.setValue(impsp.hwc_idzz.getValue()); // 职种id
						sall.hwc_namezz.setValue(impsp.hwc_namezz.getValue()); // 职种
					}
			}
			String fullattend = dictemp.getVbCE("5", v.get("isfullattend"), false, "员工【" + emp.employee_name.getValue() + "】是否满勤【" + v.get("isfullattend")
					+ "】不存在");
			sall.isfullattend.setValue(fullattend); // 是否满勤
			sall.baseattend.setValue(v.get("baseattend")); // 基本出勤
			sall.normalot.setValue(v.get("normalot")); // 平时加班工资
			sall.restot.setValue(v.get("restot")); // 休息加班工资
			sall.tworkhours.setValue(v.get("tworkhours")); // 总工时
			sall.paynosubs.setValue(v.get("paynosubs")); // 无津贴应发
			sall.paywsubs.setValue(v.get("paywsubs")); // 有津贴应发
			sall.wagetype.setValue(wgtype);//薪资类型
			sall.usable.setAsInt(1);//有效
			newsllists.add(sall);
			rst++;
		}
		if (newsllists.size() > 0) {
			System.out.println("====================导入工资明细条数【" + newsllists.size() + "】");
			newsllists.saveBatchSimple();// 高速存储
		}
		newsllists.clear();
		return rst;
	}

	private List<CExcelField> initExcelFields_salarynotmonth_list() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("年月", "yearmonth", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("薪资类型", "wagetype", true));
		efields.add(new CExcelField("是否满勤", "isfullattend", true));
		efields.add(new CExcelField("基本出勤", "baseattend", true));
		efields.add(new CExcelField("平时加班工时", "normalot", true));
		efields.add(new CExcelField("休息加班工时", "restot", true));
		efields.add(new CExcelField("总工时", "tworkhours", true));
		efields.add(new CExcelField("不含津贴应发", "paynosubs", true));
		efields.add(new CExcelField("含津贴应发", "paywsubs", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}
	
	@ACOAction(eventname = "findsalarylists", Authentication = true, ispublic = true, notes = "获取月薪人员薪资明细")
	public String findsalarylists() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jpym = CjpaUtil.getParm(jps, "yearmonth");
		if (jpym == null)
			throw new Exception("需要参数【yearmonth】");
		String yearmonth = jpym.getParmvalue();
		String ymrelator=jpym.getReloper();
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporgcode != null){
			String orgcode = jporgcode.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		boolean hasaccessed=true;
		if (!HRUtil.hasRoles("71")) {// 薪酬管理角色
			hasaccessed= false; // 不是薪酬管理角色
		}
		String[] ignParms = { "wgtype","yearmonth","orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT * FROM hr_salary_list sl WHERE wagetype=1 AND yearmonth";
		if(ymrelator.equals("<=")){
			sqlstr=sqlstr+"<=";
		}
		if(ymrelator.equals(">=")){
			sqlstr=sqlstr+">=";
		}
		if(ymrelator.equals("=")){
			sqlstr=sqlstr+"=";
		}
		sqlstr=sqlstr+"'"+yearmonth+"' "+ CSContext.getIdpathwhere();
		if (jporgcode != null){
			sqlstr=sqlstr+" and sl.idpath LIKE '"+org.idpath.getValue()+"%' ";
		}
		if(!hasaccessed){
			sqlstr=sqlstr+" and employee_code='"+CSContext.getUserName()+"' ";
		}
		sqlstr=sqlstr+" ORDER BY orgid";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "findsalarylistsnotmonth", Authentication = true, ispublic = true, notes = "获取非月薪人员薪资明细")
	public String findsalarylistsnotmonth() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jpym = CjpaUtil.getParm(jps, "yearmonth");
		if (jpym == null)
			throw new Exception("需要参数【yearmonth】");
		String yearmonth = jpym.getParmvalue();
		String ymrelator=jpym.getReloper();
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporgcode != null){
			String orgcode = jporgcode.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		boolean hasaccessed=true;
		if (!HRUtil.hasRoles("71")) {// 薪酬管理角色
			hasaccessed= false; // 不是薪酬管理角色
		}
		String[] ignParms = { "wgtype","yearmonth","orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr="SELECT * FROM hr_salary_list sl WHERE wagetype=2 AND yearmonth";
		if(ymrelator.equals("<=")){
			sqlstr=sqlstr+"<=";
		}
		if(ymrelator.equals(">=")){
			sqlstr=sqlstr+">=";
		}
		if(ymrelator.equals("=")){
			sqlstr=sqlstr+"=";
		}
		sqlstr=sqlstr+"'"+yearmonth+"' "+ CSContext.getIdpathwhere();
		if (jporgcode != null){
			sqlstr=sqlstr+" and sl.idpath LIKE '"+org.idpath.getValue()+"%' ";
		}
		if(!hasaccessed){
			sqlstr=sqlstr+" and employee_code='"+CSContext.getUserName()+"' ";
		}
		sqlstr=sqlstr+" ORDER BY orgid";
		return new CReport(sqlstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "findsalarychglgs", Authentication = true, ispublic = true, notes = "获取薪资变更记录")
	public String findsalarychglgs() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		Date bgdate=null;
		String bgymrelator=null;
		Date eddate=null;
		String edymrelator=null;
		for(JSONParm param :jps){
			String parmname = param.getParmname();
			int idx = parmname.lastIndexOf('.');
			String fn = (idx == -1) ? parmname : parmname.substring(idx + 1);
			if ("chgdate".equalsIgnoreCase(fn)){
				String yearmonth = param.getParmvalue();
				String ymrelator=param.getReloper();
				
				//Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
				if(ymrelator.equals("<")||ymrelator.equals("<=")){
					//if(ymrelator.equals("<")){
					edymrelator=ymrelator;
						eddate=Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(yearmonth)));// 去除时分秒
				//	}
					
				}
				else if(ymrelator.equals(">")||ymrelator.equals(">=")){
				//	if(ymrelator.equals(">")){
					bgymrelator=ymrelator;
					bgdate=Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(yearmonth)));// 去除时分秒
				//	}
					
				}
				else{
					
					bgymrelator=ymrelator;
					bgdate=Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(yearmonth)));// 去除时分秒
				}
			}
		}

		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		Shworg org = new Shworg();
		if (jporgcode != null){
			String orgcode = jporgcode.getParmvalue();
			String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
		}
		boolean hasaccessed=true;
		if (!HRUtil.hasRoles("71")) {// 薪酬管理角色
			hasaccessed= false; // 不是薪酬管理角色
		}
		String[] ignParms = { "chgdate","orgcode" };// 忽略的查询条件
		String[] notnull = {};
		String sqlstr=null;
		if(bgdate==null && eddate==null){
			 sqlstr="SELECT cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath  "+
						" FROM `hr_salary_chglg` cl,`hr_employee` emp WHERE cl.er_id=emp.er_id AND   emp.pay_way='月薪'"+ CSContext.getIdpathwhere();
		}
		else if(bgdate!=null && eddate!=null){
			 sqlstr="SELECT cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath  "+
						" FROM `hr_salary_chglg` cl,`hr_employee` emp WHERE cl.er_id=emp.er_id AND   emp.pay_way='月薪' AND cl.chgdate"+bgymrelator+"'"+
						Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' and cl.chgdate"+edymrelator+"'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' "+ CSContext.getIdpathwhere();
		}else if(bgdate!=null && eddate==null){
			 sqlstr="SELECT cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath  "+
						" FROM `hr_salary_chglg` cl,`hr_employee` emp WHERE cl.er_id=emp.er_id AND   emp.pay_way='月薪' AND cl.chgdate"+bgymrelator+"'"+
						Systemdate.getStrDateyyyy_mm_dd(bgdate)+"'" + CSContext.getIdpathwhere();
		}else if(bgdate==null && eddate!=null){
			 sqlstr="SELECT cl.*,emp.employee_code,emp.employee_name,emp.hiredday,emp.idpath  "+
						" FROM `hr_salary_chglg` cl,`hr_employee` emp WHERE cl.er_id=emp.er_id AND   emp.pay_way='月薪' AND  cl.chgdate"+edymrelator+"'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"' "+ CSContext.getIdpathwhere();
		}
		
		if (jporgcode != null){
			sqlstr=sqlstr+" and emp.idpath LIKE '"+org.idpath.getValue()+"%' ";
		}
		if(!hasaccessed){
			sqlstr=sqlstr+" and emp.employee_code='"+CSContext.getUserName()+"' ";
		}
		sqlstr=sqlstr+" ORDER BY emp.orgid,emp.er_id";
		String orderstr="chgdate desc";
		return new CReport(sqlstr,orderstr, notnull).findReport(ignParms,null);
	}
	
	@ACOAction(eventname = "impsalarychglgexcel", Authentication = true, ispublic = false, notes = "导入薪资变动记录")
	public String impsalarychglgexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		/*JSONObject result = new JSONObject();
		if (!HRUtil.hasRoles("19")) {// 薪酬管理员
			result.put("accessed", 2);
			//return result.toString();
			throw new Exception("当前登录用户没有权限使用该功能！");
		}*/
		HashMap<String, String> parms = CSContext.getParms();
		String chgdate = CorUtil.hashMap2Str(parms, "chgdate", "需要参数chgdate");
		String imptype = CorUtil.hashMap2Str(parms, "imptype");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_salary_chglg(p, batchno,chgdate,imptype);
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

	private int parserExcelFile_salary_chglg(Shw_physic_file pf, String batchno,String chgdate,String imptype) throws Exception {
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
		return parserExcelSheet_salary_chglg(aSheet, batchno,chgdate,imptype);
	}

	private int parserExcelSheet_salary_chglg(Sheet aSheet, String batchno,String chgdate,String imptype) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_salary_chglg();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		CJPALineData<Hr_salary_chglg> newchglgs = new CJPALineData<Hr_salary_chglg>(Hr_salary_chglg.class);
		int rst = 0;
		Date cd = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(chgdate)));// 去除时分秒
		Date bgdate=Systemdate.getFirstAndLastOfMonth(cd).date1;
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		int ipt= imptype != null? Integer.parseInt(imptype):1; //导入类型。 1、按工资结构核算；2、历史数据，无工资结构
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		Shworg org = new Shworg();
		CDBConnection con = org.pool.getCon(this);
		con.startTrans();
		try{
			for (Map<String, String> v : values) {
				int m=rst%2000;
				if(m==0){
					if (newchglgs.size() > 0) {
						System.out.println("====================导入工资明细条数【" + newchglgs.size() + "】");
						if(ipt==1){
							newchglgs.save(con);// 高速存储
						}else if(ipt==2){
							newchglgs.saveBathcSiple(con);
						}
					}
					newchglgs.clear();
				}
				String employee_code = v.get("employee_code");
				if ((employee_code == null) || (employee_code.isEmpty()))
					throw new Exception("薪资明细上的工号不能为空");
				Hr_employee emp = new Hr_employee();
				emp.findBySQL("SELECT * FROM hr_employee WHERE employee_code='" + employee_code + "'");
				if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在人事资料");
				
				Hr_salary_chglg cgl = new Hr_salary_chglg();
				Hr_salary_chglg oldcgl = new Hr_salary_chglg();
				cgl.remark.setValue(v.get("remark")); // 备注
				cgl.er_id.setValue(emp.er_id.getValue()); // 人事档案id
				cgl.orgid.setValue(emp.orgid.getValue()); // 机构id
				cgl.orgname.setValue(emp.orgname.getValue()); // 机构
				cgl.lv_num.setValue(emp.lv_num.getValue()); // 职级
				cgl.ospid.setValue(emp.ospid.getValue()); // 职位id
				cgl.sp_name.setValue(emp.sp_name.getValue()); // 职位
				cgl.newcalsalarytype.setValue(emp.pay_way.getValue()); // 调薪后计薪方式
				String chgreason="导入薪资变动记录";
				cgl.sid.setValue(0); // 
				cgl.scode.setValue(0); // 
				int stype = Integer.valueOf(dictemp.getVbCE("1482", v.get("stype"), false, "员工【" + emp.employee_name.getValue() + "】的异动类型【" + v.get("stype")
						+ "】不存在"));
				int scatype=0;
				if(stype==1){
					scatype=0;
				}
				if(stype==3){
					scatype=1;
					chgreason=chgreason+"-入职定薪";
				}
				if(stype==4){
					scatype=2;
					chgreason=chgreason+"-调动核薪";
				}
				if((stype==5)||(stype==6)){
					scatype=3;
					chgreason=chgreason+"-入职转正/调动转正";
				}
				if((stype==7)||(stype==9)){
					scatype=5;
					chgreason=chgreason+"-特殊调薪";
				}
				if(stype==12){
					scatype=4;
					chgreason=chgreason+"-年度调薪";
				}
				if(stype==8){
					chgreason=chgreason+"-兼职津贴";
				}
				if(stype==10){
					chgreason=chgreason+"-技术津贴";
				}
				if(stype==11){
					chgreason=chgreason+"-岗位津贴";
				}
				cgl.scatype.setAsInt(scatype); // 变更类型 导入无类型
				cgl.stype.setAsInt(stype); // 来源类型 历史数据
				cgl.useable.setAsInt(1);//有效
				cgl.chgdate.setValue(Systemdate.getStrDateyyyy_mm_dd(bgdate));//调薪日期
				cgl.chgreason.setValue(chgreason);//调薪原因
				BigDecimal poswage=new BigDecimal(v.get("poswage"));// 职位工资
				if(ipt==1){
					String struname=v.get("stru_name");
					Hr_salary_structure  ss=new Hr_salary_structure();
					ss.findBySQL("select * from hr_salary_structure where stat=9 and stru_name='"+struname+"'");
					if(ss.isEmpty()){
						throw new Exception("工号为【"+employee_code+"】的工资结构【"+struname+"】不存在！");
					}
					cgl.newstru_id.setValue(ss.stru_id.getValue()); // 工资结构id
					cgl.newstru_name.setValue(ss.stru_name.getValue()); // 工资结构
					cgl.newchecklev.setValue(ss.checklev.getValue()); // 工资结构id
					cgl.newattendtype.setValue(ss.kqtype.getValue()); // 工资结构
					cgl.newposition_salary.setValue(v.get("poswage")); // 职位工资
					if(ss.strutype.getAsInt()==1){
						BigDecimal minstand=BigDecimal.ZERO;
						String minsqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+emp.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC  ";
						Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
						oms.findBySQL(minsqlstr);
						if(oms.isEmpty()){
							minstand=BigDecimal.ZERO;
						}else{
							minstand=new BigDecimal(oms.minstandard.getValue());
						}
						BigDecimal hd=new BigDecimal("100");
						BigDecimal bw=poswage.multiply(new BigDecimal(ss.basewage.getValue())).divide(hd);
						BigDecimal bow=poswage.multiply(new BigDecimal(ss.otwage.getValue())).divide(hd);
						BigDecimal sw=poswage.multiply(new BigDecimal(ss.skillwage.getValue())).divide(hd);
						BigDecimal pw=poswage.multiply(new BigDecimal(ss.meritwage.getValue())).divide(hd);
						if(minstand.compareTo(bw)==1){
							if((bw.add(bow)).compareTo(minstand)==1){
								bow=bw.add(bow).subtract(minstand);
								bw=minstand;
							}else if((bw.add(bow).add(sw)).compareTo(minstand)==1){
								sw=bw.add(bow).add(sw).subtract(minstand);
								bow=BigDecimal.ZERO;
								bw=minstand;
							}else if((bw.add(bow).add(sw).add(pw)).compareTo(minstand)==1){
								pw=bw.add(bow).add(sw).add(pw).subtract(minstand);
								sw=BigDecimal.ZERO;
								bow=BigDecimal.ZERO;
								bw=minstand;
							}else{
								bw=poswage;
								pw=BigDecimal.ZERO;
								sw=BigDecimal.ZERO;
								bow=BigDecimal.ZERO;
							}
						}
						//DecimalFormat df=new DecimalFormat(".00");
						cgl.newbase_salary.setValue(bw.setScale(2,   BigDecimal.ROUND_HALF_UP).toString()); // 基本工资
						cgl.newotwage.setValue(bow.setScale(2,   BigDecimal.ROUND_HALF_UP).toString()); // 固定加班工资
						cgl.newtech_salary.setValue(sw.setScale(2,   BigDecimal.ROUND_HALF_UP).toString()); // 技能工资
						cgl.newachi_salary.setValue(pw.setScale(2,   BigDecimal.ROUND_HALF_UP).toString()); // 绩效工资
					}else{
						cgl.newbase_salary.setValue(v.get("basewage")); // 基本工资
						cgl.newotwage.setValue(v.get("baseotwage")); // 固定加班工资
						cgl.newtech_salary.setValue(v.get("skillwage")); // 技能工资
						cgl.newachi_salary.setValue(v.get("perforwage")); // 绩效工资
					}
				}else if(ipt==2){
					cgl.orgid.setValue("0"); // 机构id
					cgl.orgname.setValue(v.get("orgname")); // 机构
					cgl.lv_num.setValue(v.get("lv_num")); // 职级
					cgl.ospid.setValue("0"); // 职位id
					cgl.sp_name.setValue(v.get("sp_name")); // 职位
					
					cgl.oldstru_id.setValue("0"); // 工资结构id
					cgl.oldstru_name.setValue(v.get("oldstru_name")); // 工资结构
					cgl.oldchecklev.setValue("0"); // 绩效层级
					cgl.oldattendtype.setValue("0"); // 出勤类别
					cgl.oldposition_salary.setValue(v.get("oldposwage")); // 职位工资
					cgl.oldbase_salary.setValue(v.get("oldbasewage")); // 基本工资
					cgl.oldotwage.setValue(v.get("oldbaseotwage")); // 固定加班工资
					cgl.oldtech_salary.setValue(v.get("oldskillwage")); // 技能工资
					cgl.oldachi_salary.setValue(v.get("oldperforwage")); // 绩效工资
					cgl.oldtech_allowance.setValue(v.get("oldskillsubs")); // 技能津贴
					cgl.oldparttimesubs.setValue(v.get("oldparttimesubs")); // 兼职津贴
					cgl.oldpostsubs.setValue(v.get("oldpostsubs")); // 岗位津贴
					
					cgl.newstru_id.setValue("0"); // 工资结构id
					cgl.newstru_name.setValue(v.get("stru_name")); // 工资结构
					cgl.newchecklev.setValue("0"); // 工资结构id
					cgl.newattendtype.setValue("0"); // 工资结构
					cgl.newposition_salary.setValue(v.get("poswage")); // 职位工资
					cgl.newbase_salary.setValue(v.get("basewage")); // 基本工资
					cgl.newotwage.setValue(v.get("baseotwage")); // 固定加班工资
					cgl.newtech_salary.setValue(v.get("skillwage")); // 技能工资
					cgl.newachi_salary.setValue(v.get("perforwage")); // 绩效工资
					cgl.useable.setAsInt(2);//有效
					cgl.chgdate.setValue(v.get("chgdate")); // 生效月份
					
					String oldstru=v.get("oldstru_name");
					String newstru=v.get("stru_name");
					Hr_salary_structure  tss=new Hr_salary_structure();
					tss.findBySQL("select * from hr_salary_structure where stat=9 and stru_name='"+newstru+"'");
					if(!tss.isEmpty()){
						cgl.newstru_id.setValue(tss.stru_id.getValue()); // 工资结构id
						cgl.newstru_name.setValue(v.get("stru_name")); // 工资结构
						cgl.newchecklev.setValue(tss.checklev.getValue()); // 工资结构id
						cgl.newattendtype.setValue(tss.kqtype.getValue()); // 工资结构
					}
					tss.clear();
					tss.findBySQL("select * from hr_salary_structure where stat=9 and stru_name='"+oldstru+"'");
					if(!tss.isEmpty()){
						cgl.oldstru_id.setValue(tss.stru_id.getValue()); // 工资结构id
						cgl.oldstru_name.setValue(v.get("oldstru_name")); // 工资结构
						cgl.oldchecklev.setValue(tss.checklev.getValue()); // 工资结构id
						cgl.oldattendtype.setValue(tss.kqtype.getValue()); // 工资结构
					}
				}
				cgl.newtech_allowance.setValue(v.get("skillsubs")); // 技能津贴
				cgl.newparttimesubs.setValue(v.get("parttimesubs")); // 兼职津贴
				cgl.newpostsubs.setValue(v.get("postsubs")); // 岗位津贴
				String skillsubstr=(v.get("skillsubs")==null)?"0":v.get("skillsubs");
				String ptsubstr=(v.get("parttimesubs")==null)?"0":v.get("parttimesubs");
				String postsubstr=(v.get("postsubs")==null)?"0":v.get("postsubs");
				String opw=(v.get("oldposwage")==null)?"0":v.get("oldposwage");
				String oss=(v.get("oldskillsubs")==null)?"0":v.get("oldskillsubs");
				String opts=(v.get("oldparttimesubs")==null)?"0":v.get("oldparttimesubs");
				String ops=(v.get("oldpostsubs")==null)?"0":v.get("oldpostsubs");
				BigDecimal skillsubs=new BigDecimal(skillsubstr);
				BigDecimal parttimesubs=new BigDecimal(ptsubstr);
				BigDecimal postsubs=new BigDecimal(postsubstr);
				BigDecimal tt=poswage.add(skillsubs).add(parttimesubs).add(postsubs);
				BigDecimal oldposwage=new BigDecimal(opw);
				BigDecimal oldskillsubs=new BigDecimal(oss);
				BigDecimal oldparttimesubs=new BigDecimal(opts);
				BigDecimal oldpostsubs=new BigDecimal(ops);
				
				if(ipt==1){
					oldcgl.findBySQL("SELECT * FROM `hr_salary_chglg` WHERE er_id="+emp.er_id.getValue()+
							" and useable=1  ORDER BY createtime DESC");
					if(!oldcgl.isEmpty()){
						cgl.oldstru_id.setValue(oldcgl.newstru_id.getValue()); // 工资结构id
						cgl.oldstru_name.setValue(oldcgl.newstru_name.getValue()); // 工资结构
						cgl.oldchecklev.setValue(oldcgl.newchecklev.getValue()); // 工资结构id
						cgl.oldattendtype.setValue(oldcgl.newattendtype.getValue()); // 工资结构
						cgl.oldposition_salary.setValue(oldcgl.newposition_salary.getValue()); // 职位工资
						cgl.oldbase_salary.setValue(oldcgl.newbase_salary.getValue()); // 基本工资
						cgl.oldotwage.setValue(oldcgl.newotwage.getValue()); // 固定加班工资
						cgl.oldtech_salary.setValue(oldcgl.newtech_salary.getValue()); // 技能工资
						cgl.oldachi_salary.setValue(oldcgl.newachi_salary.getValue()); // 绩效工资
						cgl.oldtech_allowance.setValue(oldcgl.newtech_allowance.getValue()); // 技能津贴
						cgl.oldparttimesubs.setValue(oldcgl.newparttimesubs.getValue()); // 兼职津贴
						cgl.oldpostsubs.setValue(oldcgl.newpostsubs.getValue()); // 岗位津贴
						cgl.oldcalsalarytype.setValue(oldcgl.newcalsalarytype.getValue()); // 计薪方式
					    oldposwage=new BigDecimal(oldcgl.newposition_salary.getValue());
						oldskillsubs=new BigDecimal(oldcgl.newtech_allowance.getValue());
						oldparttimesubs=new BigDecimal(oldcgl.newparttimesubs.getValue());
						oldpostsubs=new BigDecimal(oldcgl.newpostsubs.getValue());
						//cgl.chgdate.setValue(oldcgl.chgdate.getValue());//调薪日期
					}
				}
				BigDecimal sc=tt.subtract(oldposwage).subtract(oldskillsubs).subtract(oldpostsubs).subtract(oldparttimesubs);
				sc=sc.setScale(2,   BigDecimal.ROUND_HALF_UP);
				cgl.sacrage.setValue(sc.toString());//调薪幅度
				newchglgs.add(cgl);
				rst++;
			}
			if (newchglgs.size() > 0) {
				System.out.println("====================导入工资变动记录条数【" + newchglgs.size() + "】");
				if(ipt==1){
					newchglgs.save(con);// 高速存储
				}else if(ipt==2){
					newchglgs.saveBathcSiple(con);
				}
			}
			newchglgs.clear();
			con.submit();
			return rst;
		}catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private List<CExcelField> initExcelFields_salary_chglg() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		//efields.add(new CExcelField("年月", "yearmonth", true));
		efields.add(new CExcelField("工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("异动类型", "stype", true));
		efields.add(new CExcelField("生效月份", "chgdate", true));
		efields.add(new CExcelField("工资结构", "stru_name", true));
		efields.add(new CExcelField("职位工资", "poswage", true));
		efields.add(new CExcelField("基本工资", "basewage", true));
		efields.add(new CExcelField("固定加班工资", "baseotwage", true));
		efields.add(new CExcelField("技能工资", "skillwage", true));
		efields.add(new CExcelField("绩效工资", "perforwage", true));
		efields.add(new CExcelField("技术津贴", "skillsubs", true));
		efields.add(new CExcelField("兼职津贴", "parttimesubs", true));
		efields.add(new CExcelField("岗位津贴", "postsubs", true));
		efields.add(new CExcelField("调薪前工资结构", "oldstru_name", false));
		efields.add(new CExcelField("调薪前职位工资", "oldposwage", false));
		efields.add(new CExcelField("调薪前基本工资", "oldbasewage", false));
		efields.add(new CExcelField("调薪前固定加班工资", "oldbaseotwage", false));
		efields.add(new CExcelField("调薪前技能工资", "oldskillwage", false));
		efields.add(new CExcelField("调薪前绩效工资", "oldperforwage", false));
		efields.add(new CExcelField("调薪前技术津贴", "oldskillsubs", false));
		efields.add(new CExcelField("调薪前兼职津贴", "oldparttimesubs", false));
		efields.add(new CExcelField("调薪前岗位津贴", "oldpostsubs", false));
		return efields;
	}
	
	@ACOAction(eventname = "setSalaryList", Authentication = true, ispublic = true, notes = "生成某月工资明细")
	public String setSalaryList() throws Exception {
		/*HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jpym = CjpaUtil.getParm(jps, "chgdate");
		if (jpym == null)
			throw new Exception("需要参数【chgdate】");
		String yearmonth = jpym.getParmvalue();*/
		HashMap<String, String> parms = CSContext.getParms();
		String yearmonth = CorUtil.hashMap2Str(parms, "chgdate", "需要参数chgdate");
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(yearmonth)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		Date bfdate = Systemdate.dateMonthAdd(bgdate, -1);// 减一月
		String ymbg=Systemdate.getStrDateByFmt(bgdate, "yyyy-MM");
		String ymed=Systemdate.getStrDateByFmt(eddate, "yyyy-MM");
		String ymbf=Systemdate.getStrDateByFmt(bfdate, "yyyy-MM");
		Shworg org = new Shworg();
		//String[] ignParms = { "chgdate","orgcode" };// 忽略的查询条件
		//String[] notnull = {};
		String sqlstr="SELECT cl.* FROM hr_salary_chglg cl,hr_employee e WHERE cl.useable=1 AND cl.newposition_salary>0 "+
			"AND cl.er_id=e.er_id AND e.pay_way='月薪' AND e.empstatid<10 "+
			" AND cl.er_id NOT IN (SELECT * FROM (SELECT er_id FROM hr_salary_list WHERE wagetype=1 AND yearmonth>='"+
		ymbg+"' AND yearmonth<'"+ymed+"'  )tt) ";
		CJPALineData<Hr_salary_list> sllists = new CJPALineData<Hr_salary_list>(Hr_salary_list.class);
		CJPALineData<Hr_salary_chglg> chglgs = new CJPALineData<Hr_salary_chglg>(Hr_salary_chglg.class);
		chglgs.findDataBySQL(sqlstr, true, false);
		CDBConnection con = org.pool.getCon(this);
		con.startTrans();
		int addnums=0;
		try {
			for (CJPABase jpa : chglgs) {
				Hr_salary_chglg cl=(Hr_salary_chglg)jpa;
				int m=addnums%2000;
				if(m==0){
					if (sllists.size() > 0) {
						System.out.println("====================生成工资明细条数【" + sllists.size() + "】");
						sllists.saveBatchSimple();// 高速存储
					}
					sllists.clear();
				}
				Hr_employee emp = new Hr_employee();
				emp.findByID(cl.er_id.getValue());
				if (emp.isEmpty())
					throw new Exception("id为【" + cl.er_id.getValue() + "】不存在人事资料");
				
				Hr_orgposition sp=new Hr_orgposition();
				sp.findByID(emp.ospid.getValue());
				if(sp.isEmpty())
					throw new Exception("员工id为【" + cl.er_id.getValue() + "】的ID为【"+emp.ospid.getValue()+"】的职位不存在");
				
				Hr_salary_list sall = new Hr_salary_list();
				sall.remark.setValue("异动生成薪资明细"); // 备注
				sall.yearmonth.setValue(yearmonth); // 年月
				sall.er_id.setValue(emp.er_id.getValue()); // 人事档案id
				sall.employee_code.setValue(emp.employee_code.getValue()); // 申请人工号
				sall.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				sall.orgid.setValue(emp.orgid.getValue()); // 部门
				sall.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				sall.orgname.setValue(emp.orgname.getValue()); // 部门名称
				sall.idpath.setValue(emp.idpath.getValue()); // idpath
				sall.ospid.setValue(emp.ospid.getValue()); // 职位id
				sall.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				sall.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				sall.lv_id.setValue(emp.lv_id.getValue()); // 职级id
				sall.lv_num.setValue(emp.lv_num.getValue()); // 职级
				sall.hg_id.setValue(emp.hg_id.getValue()); // 职等id
				sall.hg_name.setValue(emp.hg_name.getValue()); // 职等
				sall.hwc_idzl.setValue(sp.hwc_idzl.getValue()); // 职类id
				sall.hwc_namezl.setValue(sp.hwc_namezl.getValue()); // 职类
				sall.hwc_idzq.setValue(sp.hwc_idzq.getValue()); // 职群id
				sall.hwc_namezq.setValue(sp.hwc_namezq.getValue()); // 职群
				sall.hwc_idzz.setValue(sp.hwc_idzz.getValue()); // 职种id
				sall.hwc_namezz.setValue(sp.hwc_namezz.getValue()); // 职种
				sall.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				sall.stru_id.setValue(cl.newstru_id.getValue()); // 工资结构id
				sall.stru_name.setValue(cl.newstru_name.getValue()); // 工资结构
				sall.poswage.setValue(cl.newposition_salary.getValue()); // 职位工资
				sall.basewage.setValue(cl.newbase_salary.getValue()); // 基本工资
				sall.baseotwage.setValue(cl.newotwage.getValue()); // 固定加班工资
				sall.skillwage.setValue(cl.newtech_salary.getValue()); // 技能工资
				sall.perforwage.setValue(cl.newachi_salary.getValue()); // 绩效工资
				sall.skillsubs.setValue(cl.newtech_allowance.getValue()); // 技能津贴
				sall.parttimesubs.setValue(cl.newparttimesubs.getValue()); // 兼职津贴
				sall.postsubs.setValue(cl.newpostsubs.getValue()); // 岗位津贴
				sall.wagetype.setAsInt(1);//薪资类型
				sall.usable.setAsInt(1);//有效
				sllists.add(sall);
				addnums++;
			}
			if (sllists.size() > 0) {
				System.out.println("====================生成薪资明细记录条数【" + sllists.size() + "】");
				sllists.saveBatchSimple();// 高速存储sllists
			}
			sllists.clear();
			String sqlstr1="SELECT * FROM (SELECT sl.* FROM hr_salary_list sl,hr_employee e WHERE  sl.wagetype=1 AND sl.yearmonth>='"+ymbf+"' AND sl.yearmonth<'"+ymbg+
				"' AND sl.er_id=e.er_id AND e.empstatid<10 AND e.pay_way='月薪') sl WHERE er_id NOT IN (SELECT DISTINCT er_id FROM  hr_salary_list WHERE wagetype=1 AND  yearmonth>='"+ymbg+
				"' AND yearmonth<'"+ymed+"')  ";
			sllists.findDataBySQL(sqlstr1, true, false);
			for(CJPABase jpa : sllists){
				Hr_salary_list sl =(Hr_salary_list)jpa;
				sl.clearAllId();
				sl.yearmonth.setValue(yearmonth);
				sl.createtime.setValue(Systemdate.getStrDate());
				sl.updatetime.setValue(Systemdate.getStrDate());
				sl.save(con);
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
		JSONObject rst = new JSONObject();
		rst.put("rst", "ok");
		return rst.toString();
	}
	
	@ACOAction(eventname = "findSalaryEmoloyeeList", Authentication = true, ispublic = false, notes = "根据登录薪酬权限查询员工资料")
	public String findSalaryEmoloyeeList() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		String spcetype = parms.get("spcetype");
		List<JSONParm> jps = CJSON.getParms(eparms);

		Hr_employee he = new Hr_employee();
		String where = CjpaUtil.buildFindSqlByJsonParms(he, jps);
		
		String orgid = parms.get("orgid");
		if ((orgid != null) && (!orgid.isEmpty())) {
			Shworg org = new Shworg();
			org.findByID(orgid, false);
			if (org.isEmpty())
				throw new Exception("没发现ID为【" + orgid + "】的机构");
			where = where + " and idpath like '" + org.idpath.getValue() + "%'";
		}
		
		if (!HRUtil.hasRoles("71")) {// 薪酬管理员
			where = where + " and employee_code='" + CSContext.getUserName() + "'";
		}

		String strincludelv = parms.get("includelv");
		boolean includelv = (strincludelv == null) ? false : Boolean.valueOf(strincludelv);
		//String llvdate = parms.get("llvdate");// 最晚离职日期

		String smax = parms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);
		String sqlstr = "select * from hr_employee where usable=1";
		if (!includelv)
			sqlstr = sqlstr + " and empstatid<=10 ";
		sqlstr = sqlstr + CSContext.getIdpathwhere() + where + " limit 0," + max;
		return he.pool.opensql2json(sqlstr);
	}
	
	@ACOAction(eventname = "findsalarylist", Authentication = true, ispublic = true, notes = "替换通用查询")
	public String findsalarylist() throws Exception {
	     HashMap<String, String> urlparms = CSContext.get_pjdataparms();
	     String jpaclass = CorUtil.hashMap2Str(urlparms, "jpaclass", "需要参数jpaclass");
		    String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		    
		    String disidpath = (String)urlparms.get("disidpath");
		    boolean disi = disidpath != null ? Boolean.valueOf(disidpath).booleanValue() : false;
		    disi = (disi) && (ConstsSw.getSysParmIntDefault("ALLCLIENTCHGIDPATH", 2) == 1);
		    

		    String sqlwhere = (String)urlparms.get("sqlwhere");
		    String selflines = (String)urlparms.get("selfline");
		    boolean selfline = selflines != null ? Boolean.valueOf(selflines).booleanValue() : true;
		    if (("list".equalsIgnoreCase(type)) || ("tree".equalsIgnoreCase(type)))
		    {
		      selfline = false;
		      String parms = (String)urlparms.get("parms");
		      String edittps = CorUtil.hashMap2Str(urlparms, "edittps", "需要参数edittps");
		      String activeprocname = (String)urlparms.get("activeprocname");
		      
		      HashMap<String, String> edts = CJSON.Json2HashMap(edittps);
		      
		      String smax = (String)urlparms.get("max");
		      String order = (String)urlparms.get("order");
		      
		      String spage = (String)urlparms.get("page");
		      String srows = (String)urlparms.get("rows");
		      boolean needpage = false;
		      int page = 0;int rows = 0;
		      if (spage == null)
		      {
		        if (smax == null)
		        {
		          needpage = false;
		        }
		        else
		        {
		          needpage = true;
		          page = 1;
		          rows = Integer.valueOf(smax).intValue();
		        }
		      }
		      else
		      {
		        needpage = true;
		        page = Integer.valueOf(spage).intValue();
		        rows = srows == null ? 300 : Integer.valueOf(srows).intValue();
		      }
		      CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		      
		      List<JSONParm> jps = CJSON.getParms(parms);
		      String where = CjpaUtil.buildFindSqlByJsonParms(jpa, jps);
		      if ((jpa.cfield("idpath") != null) && (!disi)) {
		        where = where + CSContext.getIdpathwhere();
		      }
		      if ((sqlwhere != null) && (sqlwhere.length() > 0)) {
		        where = where + " and " + sqlwhere + " ";
		      }
		      if (jpa.getPublicControllerBase() != null)
		      {
		        String w = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindBuildWhere(jpa, urlparms);
		        if (w != null) {
		          where = where + " " + w;
		        }
		      }
		      if (jpa.getController() != null)
		      {
		        String w = ((JPAController)jpa.getController()).OnCCoFindBuildWhere(jpa, urlparms);
		        if (w != null) {
		          where = where + " " + w;
		        }
		      }
		      if (jpa.cfieldbycfieldname("stat") != null)
		      {
		        String sqlstat = "";
		        if (Obj2Bl(edts.get("isedit"))) {
		          sqlstat = sqlstat + " (stat=1) or";
		        }
		        if (Obj2Bl(edts.get("issubmit"))) {
		          sqlstat = sqlstat + " (stat>1 and stat<9) or";
		        }
		        if (Obj2Bl(edts.get("isview"))) {
		          sqlstat = sqlstat + " ( 1=1 ) or";
		        }
		        if ((Obj2Bl(edts.get("isupdate"))) || (Obj2Bl(edts.get("isfind")))) {
		          sqlstat = sqlstat + " (stat=9) or";
		        }
		        if (sqlstat.length() > 0)
		        {
		          sqlstat = sqlstat.substring(1, sqlstat.length() - 2);
		          where = where + " and (" + sqlstat + ")";
		        }
		      }
		      if ((activeprocname != null) && (!activeprocname.isEmpty()))
		      {
		        String idfd = jpa.getIDField().getFieldname();
		        String ew = "SELECT " + idfd + " FROM " + jpa.tablename + " t,shwwf wf,shwwfproc wfp" + 
		          " WHERE t.stat>1 AND t.stat<9 AND t.wfid=wf.wfid AND wf.wfid=wfp.wfid " + 
		          "  AND wfp.stat=2 AND wfp.procname='" + activeprocname + "'";
		        ew = " and " + idfd + " in (" + ew + ")";
		        where = where + ew;
		      }
		      String sqltr = null;
		      
		      String textfield = (String)urlparms.get("textfield");
		      String pidfd = null;
		      if ("tree".equalsIgnoreCase(type)) {
		        pidfd = CorUtil.hashMap2Str(urlparms, "parentid", "需要参数parentid");
		      }
		      if (("tree".equalsIgnoreCase(type)) && (textfield != null) && (textfield.length() > 0))
		      {
		        String idfd = jpa.getIDField().getFieldname();
		        sqltr = "select " + idfd + " as id," + textfield + 
		          " as text," + idfd + "," + textfield + "," + pidfd + 
		          ",a.* from " + jpa.tablename + " a where 1=1 " + where;
		      }
		      else
		      {
		        sqltr = "select * from " + jpa.tablename + " where 1=1 " + where;
		      }
		      
		      if (!HRUtil.hasRoles("71")) {// 薪酬管理员
		    	  sqltr = sqltr + " and employee_code='" + CSContext.getUserName() + "'";
				}
		      
		      if ((order != null) && (!order.isEmpty())) {
		        sqltr = sqltr + " order by " + order;
		      } else {
		        sqltr = sqltr + " order by " + jpa.getIDFieldName() + " desc ";
		      }
		      if (jpa.getPublicControllerBase() != null)
		      {
		        String rst = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps, edts, disi, selfline);
		        if (rst != null) {
		          return rst;
		        }
		      }
		      if (jpa.getController() != null)
		      {
		        String rst = ((JPAController)jpa.getController()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps, edts, disi, selfline);
		        if (rst != null) {
		          return rst;
		        }
		      }
		      if ("list".equalsIgnoreCase(type))
		      {
		        String scols = (String)urlparms.get("cols");
		        if (scols != null)
		        {
		          String[] ignParms = new String[0];
		          new CReport(sqltr, null).export2excel(ignParms, scols);
		          return null;
		        }
		        if (!needpage)
		        {
		          JSONArray js = jpa.pool.opensql2json_O(sqltr);
		          if (jpa.getController() != null)
		          {
		            String rst = ((JPAController)jpa.getController()).AfterCOFindList((Class<CJPA>) jpa.getClass(), js, 0, 0);
		            if (rst != null) {
		              return rst;
		            }
		          }
		          return js.toString();
		        }
		        JSONObject jo = jpa.pool.opensql2json_O(sqltr, page, rows);
		        if (jpa.getController() != null)
		        {
		          String rst = ((JPAController)jpa.getController()).AfterCOFindList((Class<CJPA>) jpa.getClass(), jo, page, rows);
		          if (rst != null) {
		            return rst;
		          }
		        }
		        return jo.toString();
		      }
		      if ("tree".equalsIgnoreCase(type)) {
		        return jpa.pool.opensql2jsontree(sqltr, jpa.getIDField().getFieldname(), pidfd, false);
		      }
		    }
		    if ("byid".equalsIgnoreCase(type))
		    {
		      String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
		      CJPA jpa = (CJPA)CjpaUtil.newJPAObjcet(jpaclass);
		      if (jpa.getPublicControllerBase() != null)
		      {
		        String rst = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
		        if (rst != null) {
		          return rst;
		        }
		      }
		      if (jpa.getController() != null)
		      {
		        String rst = ((JPAController)jpa.getController()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
		        if (rst != null) {
		          return rst;
		        }
		      }
		      CField idfd = jpa.getIDField();
		      if (idfd == null) {
		        throw new Exception("根据ID查询JPA<" + jpa.getClass().getSimpleName() + ">数据没发现ID字段");
		      }
		      String sqlfdname = CJPASqlUtil.getSqlField(jpa.pool.getDbtype(), idfd.getFieldname());
		      String sqlvalue = CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), idfd.getFieldtype(), id);
		      String sqlstr = "select * from " + jpa.tablename + " where " + sqlfdname + "=" + sqlvalue;
		      
		      jpa.findBySQL(sqlstr, selfline);
		      if (jpa.isEmpty()) {
		        return "{}";
		      }
		      return jpa.tojson();
		    }
		    return "";  
		}
	  private static boolean Obj2Bl(Object o) {
			if (o == null)
				return false;
			return Boolean.valueOf(o.toString());
		}	
	  
	  @ACOAction(eventname = "findtechsublist", Authentication = true, ispublic = true, notes = "替换技术津贴通用查询")
		public String findtechsublist() throws Exception {
		     HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		     String jpaclass = CorUtil.hashMap2Str(urlparms, "jpaclass", "需要参数jpaclass");
			    String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
			    
			    String disidpath = (String)urlparms.get("disidpath");
			    boolean disi = disidpath != null ? Boolean.valueOf(disidpath).booleanValue() : false;
			    disi = (disi) && (ConstsSw.getSysParmIntDefault("ALLCLIENTCHGIDPATH", 2) == 1);
			    

			    String sqlwhere = (String)urlparms.get("sqlwhere");
			    String selflines = (String)urlparms.get("selfline");
			    boolean selfline = selflines != null ? Boolean.valueOf(selflines).booleanValue() : true;
			    if (("list".equalsIgnoreCase(type)) || ("tree".equalsIgnoreCase(type)))
			    {
			      selfline = false;
			      String parms = (String)urlparms.get("parms");
			      String edittps = CorUtil.hashMap2Str(urlparms, "edittps", "需要参数edittps");
			      String activeprocname = (String)urlparms.get("activeprocname");
			      
			      HashMap<String, String> edts = CJSON.Json2HashMap(edittps);
			      
			      String smax = (String)urlparms.get("max");
			      String order = (String)urlparms.get("order");
			      
			      String spage = (String)urlparms.get("page");
			      String srows = (String)urlparms.get("rows");
			      boolean needpage = false;
			      int page = 0;int rows = 0;
			      if (spage == null)
			      {
			        if (smax == null)
			        {
			          needpage = false;
			        }
			        else
			        {
			          needpage = true;
			          page = 1;
			          rows = Integer.valueOf(smax).intValue();
			        }
			      }
			      else
			      {
			        needpage = true;
			        page = Integer.valueOf(spage).intValue();
			        rows = srows == null ? 300 : Integer.valueOf(srows).intValue();
			      }
			      CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
			      
			      List<JSONParm> jps = CJSON.getParms(parms);
			      String where = CjpaUtil.buildFindSqlByJsonParms(jpa, jps);
			      if ((jpa.cfield("idpath") != null) && (!disi)) {
			        where = where + CSContext.getIdpathwhere();
			      }
			      if ((sqlwhere != null) && (sqlwhere.length() > 0)) {
			        where = where + " and " + sqlwhere + " ";
			      }
			      if (jpa.getPublicControllerBase() != null)
			      {
			        String w = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindBuildWhere(jpa, urlparms);
			        if (w != null) {
			          where = where + " " + w;
			        }
			      }
			      if (jpa.getController() != null)
			      {
			        String w = ((JPAController)jpa.getController()).OnCCoFindBuildWhere(jpa, urlparms);
			        if (w != null) {
			          where = where + " " + w;
			        }
			      }
			      if (jpa.cfieldbycfieldname("stat") != null)
			      {
			        String sqlstat = "";
			        if (Obj2Bl(edts.get("isedit"))) {
			          sqlstat = sqlstat + " (stat=1) or";
			        }
			        if (Obj2Bl(edts.get("issubmit"))) {
			          sqlstat = sqlstat + " (stat>1 and stat<9) or";
			        }
			        if (Obj2Bl(edts.get("isview"))) {
			          sqlstat = sqlstat + " ( 1=1 ) or";
			        }
			        if ((Obj2Bl(edts.get("isupdate"))) || (Obj2Bl(edts.get("isfind")))) {
			          sqlstat = sqlstat + " (stat=9) or";
			        }
			        if (sqlstat.length() > 0)
			        {
			          sqlstat = sqlstat.substring(1, sqlstat.length() - 2);
			          where = where + " and (" + sqlstat + ")";
			        }
			      }
			      if ((activeprocname != null) && (!activeprocname.isEmpty()))
			      {
			        String idfd = jpa.getIDField().getFieldname();
			        String ew = "SELECT " + idfd + " FROM " + jpa.tablename + " t,shwwf wf,shwwfproc wfp" + 
			          " WHERE t.stat>1 AND t.stat<9 AND t.wfid=wf.wfid AND wf.wfid=wfp.wfid " + 
			          "  AND wfp.stat=2 AND wfp.procname='" + activeprocname + "'";
			        ew = " and " + idfd + " in (" + ew + ")";
			        where = where + ew;
			      }
			      String sqltr = null;
			      
			      String textfield = (String)urlparms.get("textfield");
			      String pidfd = null;
			      if ("tree".equalsIgnoreCase(type)) {
			        pidfd = CorUtil.hashMap2Str(urlparms, "parentid", "需要参数parentid");
			      }
			      if (("tree".equalsIgnoreCase(type)) && (textfield != null) && (textfield.length() > 0))
			      {
			        String idfd = jpa.getIDField().getFieldname();
			        sqltr = "select " + idfd + " as id," + textfield + 
			          " as text," + idfd + "," + textfield + "," + pidfd + 
			          ",a.* from " + jpa.tablename + " a where 1=1 " + where;
			      }
			      else
			      {
			        sqltr = "select * from " + jpa.tablename + " where 1=1 " + where;
			      }
			      
			      if (!HRUtil.hasRoles("71")) {// 薪酬管理员
			    	  sqltr = sqltr + " AND EXISTS (SELECT * FROM `hr_salary_techsub_line` tsl WHERE tsl.`ts_id`=ts_id AND employee_code='" + CSContext.getUserName() + "')";
					}
			      
			      if ((order != null) && (!order.isEmpty())) {
			        sqltr = sqltr + " order by " + order;
			      } else {
			        sqltr = sqltr + " order by " + jpa.getIDFieldName() + " desc ";
			      }
			      if (jpa.getPublicControllerBase() != null)
			      {
			        String rst = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps, edts, disi, selfline);
			        if (rst != null) {
			          return rst;
			        }
			      }
			      if (jpa.getController() != null)
			      {
			        String rst = ((JPAController)jpa.getController()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps, edts, disi, selfline);
			        if (rst != null) {
			          return rst;
			        }
			      }
			      if ("list".equalsIgnoreCase(type))
			      {
			        String scols = (String)urlparms.get("cols");
			        if (scols != null)
			        {
			          String[] ignParms = new String[0];
			          new CReport(sqltr, null).export2excel(ignParms, scols);
			          return null;
			        }
			        if (!needpage)
			        {
			          JSONArray js = jpa.pool.opensql2json_O(sqltr);
			          if (jpa.getController() != null)
			          {
			            String rst = ((JPAController)jpa.getController()).AfterCOFindList((Class<CJPA>) jpa.getClass(), js, 0, 0);
			            if (rst != null) {
			              return rst;
			            }
			          }
			          return js.toString();
			        }
			        JSONObject jo = jpa.pool.opensql2json_O(sqltr, page, rows);
			        if (jpa.getController() != null)
			        {
			          String rst = ((JPAController)jpa.getController()).AfterCOFindList((Class<CJPA>) jpa.getClass(), jo, page, rows);
			          if (rst != null) {
			            return rst;
			          }
			        }
			        return jo.toString();
			      }
			      if ("tree".equalsIgnoreCase(type)) {
			        return jpa.pool.opensql2jsontree(sqltr, jpa.getIDField().getFieldname(), pidfd, false);
			      }
			    }
			    if ("byid".equalsIgnoreCase(type))
			    {
			      String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			      CJPA jpa = (CJPA)CjpaUtil.newJPAObjcet(jpaclass);
			      if (jpa.getPublicControllerBase() != null)
			      {
			        String rst = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
			        if (rst != null) {
			          return rst;
			        }
			      }
			      if (jpa.getController() != null)
			      {
			        String rst = ((JPAController)jpa.getController()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
			        if (rst != null) {
			          return rst;
			        }
			      }
			      CField idfd = jpa.getIDField();
			      if (idfd == null) {
			        throw new Exception("根据ID查询JPA<" + jpa.getClass().getSimpleName() + ">数据没发现ID字段");
			      }
			      String sqlfdname = CJPASqlUtil.getSqlField(jpa.pool.getDbtype(), idfd.getFieldname());
			      String sqlvalue = CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), idfd.getFieldtype(), id);
			      String sqlstr = "select * from " + jpa.tablename + " where " + sqlfdname + "=" + sqlvalue;
			      
			      jpa.findBySQL(sqlstr, selfline);
			      if (jpa.isEmpty()) {
			        return "{}";
			      }
			      return jpa.tojson();
			    }
			    return "";  
			}
	  
	  @ACOAction(eventname = "findpostsublist", Authentication = true, ispublic = true, notes = "替换岗位津贴通用查询")
			public String findpostsublist() throws Exception {
			     HashMap<String, String> urlparms = CSContext.get_pjdataparms();
			     String jpaclass = CorUtil.hashMap2Str(urlparms, "jpaclass", "需要参数jpaclass");
				    String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
				    
				    String disidpath = (String)urlparms.get("disidpath");
				    boolean disi = disidpath != null ? Boolean.valueOf(disidpath).booleanValue() : false;
				    disi = (disi) && (ConstsSw.getSysParmIntDefault("ALLCLIENTCHGIDPATH", 2) == 1);
				    

				    String sqlwhere = (String)urlparms.get("sqlwhere");
				    String selflines = (String)urlparms.get("selfline");
				    boolean selfline = selflines != null ? Boolean.valueOf(selflines).booleanValue() : true;
				    if (("list".equalsIgnoreCase(type)) || ("tree".equalsIgnoreCase(type)))
				    {
				      selfline = false;
				      String parms = (String)urlparms.get("parms");
				      String edittps = CorUtil.hashMap2Str(urlparms, "edittps", "需要参数edittps");
				      String activeprocname = (String)urlparms.get("activeprocname");
				      
				      HashMap<String, String> edts = CJSON.Json2HashMap(edittps);
				      
				      String smax = (String)urlparms.get("max");
				      String order = (String)urlparms.get("order");
				      
				      String spage = (String)urlparms.get("page");
				      String srows = (String)urlparms.get("rows");
				      boolean needpage = false;
				      int page = 0;int rows = 0;
				      if (spage == null)
				      {
				        if (smax == null)
				        {
				          needpage = false;
				        }
				        else
				        {
				          needpage = true;
				          page = 1;
				          rows = Integer.valueOf(smax).intValue();
				        }
				      }
				      else
				      {
				        needpage = true;
				        page = Integer.valueOf(spage).intValue();
				        rows = srows == null ? 300 : Integer.valueOf(srows).intValue();
				      }
				      CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
				      
				      List<JSONParm> jps = CJSON.getParms(parms);
				      String where = CjpaUtil.buildFindSqlByJsonParms(jpa, jps);
				      if ((jpa.cfield("idpath") != null) && (!disi)) {
				        where = where + CSContext.getIdpathwhere();
				      }
				      if ((sqlwhere != null) && (sqlwhere.length() > 0)) {
				        where = where + " and " + sqlwhere + " ";
				      }
				      if (jpa.getPublicControllerBase() != null)
				      {
				        String w = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindBuildWhere(jpa, urlparms);
				        if (w != null) {
				          where = where + " " + w;
				        }
				      }
				      if (jpa.getController() != null)
				      {
				        String w = ((JPAController)jpa.getController()).OnCCoFindBuildWhere(jpa, urlparms);
				        if (w != null) {
				          where = where + " " + w;
				        }
				      }
				      if (jpa.cfieldbycfieldname("stat") != null)
				      {
				        String sqlstat = "";
				        if (Obj2Bl(edts.get("isedit"))) {
				          sqlstat = sqlstat + " (stat=1) or";
				        }
				        if (Obj2Bl(edts.get("issubmit"))) {
				          sqlstat = sqlstat + " (stat>1 and stat<9) or";
				        }
				        if (Obj2Bl(edts.get("isview"))) {
				          sqlstat = sqlstat + " ( 1=1 ) or";
				        }
				        if ((Obj2Bl(edts.get("isupdate"))) || (Obj2Bl(edts.get("isfind")))) {
				          sqlstat = sqlstat + " (stat=9) or";
				        }
				        if (sqlstat.length() > 0)
				        {
				          sqlstat = sqlstat.substring(1, sqlstat.length() - 2);
				          where = where + " and (" + sqlstat + ")";
				        }
				      }
				      if ((activeprocname != null) && (!activeprocname.isEmpty()))
				      {
				        String idfd = jpa.getIDField().getFieldname();
				        String ew = "SELECT " + idfd + " FROM " + jpa.tablename + " t,shwwf wf,shwwfproc wfp" + 
				          " WHERE t.stat>1 AND t.stat<9 AND t.wfid=wf.wfid AND wf.wfid=wfp.wfid " + 
				          "  AND wfp.stat=2 AND wfp.procname='" + activeprocname + "'";
				        ew = " and " + idfd + " in (" + ew + ")";
				        where = where + ew;
				      }
				      String sqltr = null;
				      
				      String textfield = (String)urlparms.get("textfield");
				      String pidfd = null;
				      if ("tree".equalsIgnoreCase(type)) {
				        pidfd = CorUtil.hashMap2Str(urlparms, "parentid", "需要参数parentid");
				      }
				      if (("tree".equalsIgnoreCase(type)) && (textfield != null) && (textfield.length() > 0))
				      {
				        String idfd = jpa.getIDField().getFieldname();
				        sqltr = "select " + idfd + " as id," + textfield + 
				          " as text," + idfd + "," + textfield + "," + pidfd + 
				          ",a.* from " + jpa.tablename + " a where 1=1 " + where;
				      }
				      else
				      {
				        sqltr = "select * from " + jpa.tablename + " where 1=1 " + where;
				      }
				      
				      if (!HRUtil.hasRoles("71")) {// 薪酬管理员
				    	  sqltr = sqltr + " AND EXISTS (SELECT * FROM `hr_salary_postsub_line` psl WHERE psl.`ps_id`=ps_id AND employee_code='" + CSContext.getUserName() + "')";
						}
				      
				      if ((order != null) && (!order.isEmpty())) {
				        sqltr = sqltr + " order by " + order;
				      } else {
				        sqltr = sqltr + " order by " + jpa.getIDFieldName() + " desc ";
				      }
				      if (jpa.getPublicControllerBase() != null)
				      {
				        String rst = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps, edts, disi, selfline);
				        if (rst != null) {
				          return rst;
				        }
				      }
				      if (jpa.getController() != null)
				      {
				        String rst = ((JPAController)jpa.getController()).OnCCoFindList((Class<CJPA>) jpa.getClass(), jps, edts, disi, selfline);
				        if (rst != null) {
				          return rst;
				        }
				      }
				      if ("list".equalsIgnoreCase(type))
				      {
				        String scols = (String)urlparms.get("cols");
				        if (scols != null)
				        {
				          String[] ignParms = new String[0];
				          new CReport(sqltr, null).export2excel(ignParms, scols);
				          return null;
				        }
				        if (!needpage)
				        {
				          JSONArray js = jpa.pool.opensql2json_O(sqltr);
				          if (jpa.getController() != null)
				          {
				            String rst = ((JPAController)jpa.getController()).AfterCOFindList((Class<CJPA>) jpa.getClass(), js, 0, 0);
				            if (rst != null) {
				              return rst;
				            }
				          }
				          return js.toString();
				        }
				        JSONObject jo = jpa.pool.opensql2json_O(sqltr, page, rows);
				        if (jpa.getController() != null)
				        {
				          String rst = ((JPAController)jpa.getController()).AfterCOFindList((Class<CJPA>) jpa.getClass(), jo, page, rows);
				          if (rst != null) {
				            return rst;
				          }
				        }
				        return jo.toString();
				      }
				      if ("tree".equalsIgnoreCase(type)) {
				        return jpa.pool.opensql2jsontree(sqltr, jpa.getIDField().getFieldname(), pidfd, false);
				      }
				    }
				    if ("byid".equalsIgnoreCase(type))
				    {
				      String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
				      CJPA jpa = (CJPA)CjpaUtil.newJPAObjcet(jpaclass);
				      if (jpa.getPublicControllerBase() != null)
				      {
				        String rst = ((JPAController)jpa.getPublicControllerBase()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
				        if (rst != null) {
				          return rst;
				        }
				      }
				      if (jpa.getController() != null)
				      {
				        String rst = ((JPAController)jpa.getController()).OnCCoFindByID((Class<CJPA>) jpa.getClass(), id);
				        if (rst != null) {
				          return rst;
				        }
				      }
				      CField idfd = jpa.getIDField();
				      if (idfd == null) {
				        throw new Exception("根据ID查询JPA<" + jpa.getClass().getSimpleName() + ">数据没发现ID字段");
				      }
				      String sqlfdname = CJPASqlUtil.getSqlField(jpa.pool.getDbtype(), idfd.getFieldname());
				      String sqlvalue = CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), idfd.getFieldtype(), id);
				      String sqlstr = "select * from " + jpa.tablename + " where " + sqlfdname + "=" + sqlvalue;
				      
				      jpa.findBySQL(sqlstr, selfline);
				      if (jpa.isEmpty()) {
				        return "{}";
				      }
				      return jpa.tojson();
				    }
				    return "";  
				}
			

}
