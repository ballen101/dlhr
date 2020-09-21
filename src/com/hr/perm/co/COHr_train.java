package com.hr.perm.co;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.base.entity.Hr_employeestat;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_train_reg;
import com.hr.perm.entity.Hr_traindisp_batchline;
import com.hr.perm.entity.Hr_trainentry_batchline;
import com.hr.perm.entity.Hr_traintran_batchline;
import com.hr.salary.ctr.CtrSalaryCommon;
import com.hr.salary.entity.Hr_salary_chglg;
import com.hr.salary.entity.Hr_salary_orgminstandard;
import com.hr.salary.entity.Hr_salary_structure;

@ACO(coname = "web.hr.train")
public class COHr_train {
	@ACOAction(eventname = "entry_batch_impexcel", Authentication = true, notes = "从excel导入毕业生入职信息")
	public String entry_batch_impexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");

		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile(p, 1);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}

	@ACOAction(eventname = "traintran_batch_impexcel", Authentication = true, notes = "从excel导入毕业生转正信息")
	public String traintran_batch_impexcel() throws Exception {

		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");

		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile(p, 2);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}

	@ACOAction(eventname = "traindisp_batch_impexcel", Authentication = true, notes = "从excel导入毕业生分配信息")
	public String traindisp_batch_impexcel() throws Exception {

		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");

		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile(p, 3);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}

	private String parserExcelFile(Shw_physic_file pf, int tp) throws Exception {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (!file.exists())
				throw new Exception("文件" + fullname + "不存在!");
		}

		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fullname));
		int sn = workbook.getNumberOfSheets();
		if (sn <= 0)
			throw new Exception("excel<" + fullname + ">没有sheet");
		HSSFSheet aSheet = workbook.getSheetAt(0);// 获得一个sheet
		if (tp == 1)// 批入
			return parserExcelSheet_entry_batch(aSheet);
		else if (tp == 2)// 批转
			return parserExcelSheet_traintran_batch(aSheet);
		else if (tp == 3)// 批分
			return parserExcelSheet_traindisp_batch(aSheet);
		else
			throw new Exception("参数错误");
	}

	// 工号 姓名 入职日期 试用到期 见习到期 机构编码 机构名称 职位编码 职位名称 考试课题 考试时间 考试分数
	private String parserExcelSheet_entry_batch(HSSFSheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		HSSFRow aRow = aSheet.getRow(0);
		int empcol = CExcelUtil.colByValue(aRow, "工号", true);
		int rzcol = CExcelUtil.colByValue(aRow, "入职日期", true);
		int sycol = CExcelUtil.colByValue(aRow, "试用到期", true);
		int jxcol = CExcelUtil.colByValue(aRow, "见习到期", true);
		int ospcol = CExcelUtil.colByValue(aRow, "职位编码", true);
		int ktcol = CExcelUtil.colByValue(aRow, "考试课题", true);
		int kscol = CExcelUtil.colByValue(aRow, "考试时间", true);
		int fxcol = CExcelUtil.colByValue(aRow, "考试分数", true);

		CJPALineData<Hr_trainentry_batchline> rls = new CJPALineData<Hr_trainentry_batchline>(Hr_trainentry_batchline.class);
		Hr_employee emp = new Hr_employee();
		Hr_orgposition ho = new Hr_orgposition();
		Hr_train_reg tr = new Hr_train_reg();
		Hr_employeestat es = new Hr_employeestat();
		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {
				HSSFCell aCell = aRow.getCell(empcol);
				String employee_code = CExcelUtil.getCellValue(aCell);
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				emp.clear();
				emp.findBySQL("select * from hr_employee where employee_code='" + employee_code + "'", false);
				if (emp.isEmpty())
					throw new Exception("没有找到工号为【" + employee_code + "】的员工资料");
				es.clear();
				es.findBySQL("select * from hr_employeestat where statvalue=" + emp.empstatid.getValue());
				if (es.statvalue.getAsInt() != 1) {
					throw new Exception("工号为【" + employee_code + "】的员工状态为【" + es.language1.getValue() + "】不能做【实习生入职操作】");
				}
				aCell = aRow.getCell(rzcol);
				String hiredday = CExcelUtil.getCellValue(aCell);
				if ((hiredday == null) || (hiredday.trim().isEmpty()))
					hiredday = null;

				aCell = aRow.getCell(sycol);
				String enddatetry = CExcelUtil.getCellValue(aCell);
				if ((enddatetry == null) || (enddatetry.trim().isEmpty()))
					enddatetry = null;

				aCell = aRow.getCell(jxcol);
				String jxdatetry = CExcelUtil.getCellValue(aCell);
				if ((jxdatetry == null) || (jxdatetry.trim().isEmpty()))
					jxdatetry = null;

				aCell = aRow.getCell(ospcol);
				String ospcode = CExcelUtil.getCellValue(aCell);
				if ((ospcode == null) || (ospcode.trim().isEmpty()))
					ospcode = null;

				aCell = aRow.getCell(ktcol);
				String exam_title = CExcelUtil.getCellValue(aCell);
				if ((exam_title == null) || (exam_title.trim().isEmpty()))
					exam_title = null;

				aCell = aRow.getCell(kscol);
				String exam_time = CExcelUtil.getCellValue(aCell);
				if ((exam_time == null) || (exam_time.trim().isEmpty()))
					exam_time = null;

				aCell = aRow.getCell(fxcol);
				String exam_score = CExcelUtil.getCellValue(aCell);
				if ((exam_score == null) || (exam_score.trim().isEmpty()))
					exam_score = null;

				tr.clear();
				String sqlstr = "SELECT * FROM hr_train_reg WHERE stat=9 AND er_id=" + emp.er_id.getValue() + " ORDER BY createtime DESC";
				tr.findBySQL(sqlstr, false);
				if (tr.isEmpty())
					throw new Exception("没有找到人事资料【" + employee_code + "】的实习登记表单");

				getOP(ho, ospcode, emp.ospid.getValue());
				boolean nullho = ho.isEmpty();
				Hr_trainentry_batchline etb = new Hr_trainentry_batchline();

				etb.er_id.setValue(emp.er_id.getValue()); // 人事档案ID
				etb.employee_code.setValue(emp.employee_code.getValue()); // 工号
				etb.sex.setValue(emp.sex.getValue()); // 性别
				etb.id_number.setValue(emp.id_number.getValue()); // 身份证号
				etb.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				etb.degree.setValue(emp.degree.getValue()); // 学历
				if (hiredday == null)
					etb.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				else
					etb.hiredday.setValue(hiredday); // 入职日期
				if (enddatetry == null)
					etb.enddatetry.setValue(tr.enddatetry.getValue()); // 试用到期日期
				else
					etb.enddatetry.setValue(enddatetry); // 试用到期日期
				if (jxdatetry == null)
					etb.jxdatetry.setValue(tr.jxdatetry.getValue()); // 见习到期日期
				else
					etb.jxdatetry.setValue(jxdatetry); // 见习到期日期

				if (nullho) {
					etb.orgid.setValue(emp.orgid.getValue()); // 部门ID
					etb.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
					etb.orgname.setValue(emp.orgname.getValue()); // 部门名称
					etb.ospid.setValue(emp.ospid.getValue()); // 职位ID
					etb.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
					etb.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
					etb.hg_name.setValue(emp.hg_name.getValue()); // 职等
					etb.lv_num.setValue(emp.lv_num.getValue()); // 职级
				} else {
					etb.orgid.setValue(ho.orgid.getValue()); // 部门ID
					etb.orgcode.setValue(ho.orgcode.getValue()); // 部门编码
					etb.orgname.setValue(ho.orgname.getValue()); // 部门名称
					etb.ospid.setValue(ho.ospid.getValue()); // 职位ID
					etb.ospcode.setValue(ho.ospcode.getValue()); // 职位编码
					etb.sp_name.setValue(ho.sp_name.getValue()); // 职位名称
					etb.hg_name.setValue(ho.hg_name.getValue()); // 职等
					etb.lv_num.setValue(ho.lv_num.getValue()); // 职级
				}
				
				etb.newstru_id.setValue(tr.newstru_id.getValue()); // 工资结构id
				etb.newstru_name.setValue(tr.newstru_name.getValue()); // 工资结构
				etb.newchecklev.setValue(tr.newchecklev.getValue()); // 考核层级
				etb.newattendtype.setValue(tr.newattendtype.getValue()); // 出勤类别
				etb.newposition_salary.setValue(tr.newposition_salary.getValue()); // 职位工资
				etb.newbase_salary.setValue(tr.newbase_salary.getValue()); // 基本工资
				etb.newotwage.setValue(tr.newotwage.getValue()); // 固定加班工资
				etb.newtech_salary.setValue(tr.newtech_salary.getValue()); // 技能工资
				etb.newachi_salary.setValue(tr.newachi_salary.getValue()); // 绩效工资
				etb.newtech_allowance.setValue(tr.newtech_allowance.getValue()); // 技能津贴
				etb.newpostsubs.setValue(tr.newpostsubs.getValue()); // 岗位津贴
				etb.norgid.setValue(tr.norgid.getValue()); // 拟用人部门ID
				etb.norgname.setValue(tr.norgname.getValue()); // 拟用人部门名称
				etb.nospid.setValue(tr.nospid.getValue()); // 拟职位ID
				etb.nsp_name.setValue(tr.nsp_name.getValue()); // 拟职位名称
				if (exam_title != null)
					etb.exam_title.setValue(exam_title); // 考试课题
				if (exam_time != null)
					etb.exam_time.setValue(exam_time); // 考试时间
				if (exam_score != null)
					etb.exam_score.setValue(exam_score); // 考试分数
				rls.add(etb);
			}
		}
		return rls.tojson();
	}

	// 工号 见习到期 职位编码 考试课题 考试时间 考试分数 转正前薪资 转正后薪资 试用评价
	private String parserExcelSheet_traintran_batch(HSSFSheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		HSSFRow aRow = aSheet.getRow(0);
		int empcol = CExcelUtil.colByValue(aRow, "工号", true);
		int jxcol = CExcelUtil.colByValue(aRow, "见习到期", true);
		int ospcol = CExcelUtil.colByValue(aRow, "职位编码", true);
		int ktcol = CExcelUtil.colByValue(aRow, "考试课题", true);
		int kscol = CExcelUtil.colByValue(aRow, "考试时间", true);
		int fxcol = CExcelUtil.colByValue(aRow, "考试分数", true);
		int xz1col = CExcelUtil.colByValue(aRow, "转正前薪资", true);
		//int xz2col = CExcelUtil.colByValue(aRow, "转正后薪资", true);
		int pjcol = CExcelUtil.colByValue(aRow, "试用评价", true);
		int strucol = CExcelUtil.colByValue(aRow, "转正后工资结构", true);
		int npcol = CExcelUtil.colByValue(aRow, "转正后职位工资", true);

		CJPALineData<Hr_traintran_batchline> rls = new CJPALineData<Hr_traintran_batchline>(Hr_traintran_batchline.class);
		Hr_employee emp = new Hr_employee();
		Hr_orgposition ho = new Hr_orgposition();
		Hr_train_reg tr = new Hr_train_reg();
		Hr_employeestat es = new Hr_employeestat();
		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {
				HSSFCell aCell = aRow.getCell(empcol);
				String employee_code = CExcelUtil.getCellValue(aCell);
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				emp.clear();
				emp.findBySQL("select * from hr_employee where employee_code='" + employee_code + "'", false);
				if (emp.isEmpty())
					throw new Exception("没有找到工号为【" + employee_code + "】的员工资料");
				es.clear();
				es.findBySQL("select * from hr_employeestat where statvalue=" + emp.empstatid.getValue());
				if (es.statvalue.getAsInt() != 7) {// 实习试用
					throw new Exception("工号为【" + employee_code + "】的员工状态为【" + es.language1.getValue() + "】不能做【实习生转正操作】");
				}

				aCell = aRow.getCell(jxcol);
				String jxdatetry = CExcelUtil.getCellValue(aCell);
				if ((jxdatetry == null) || (jxdatetry.trim().isEmpty()))
					jxdatetry = null;

				aCell = aRow.getCell(ospcol);
				String ospcode = CExcelUtil.getCellValue(aCell);
				if ((ospcode == null) || (ospcode.trim().isEmpty()))
					ospcode = null;

				aCell = aRow.getCell(ktcol);
				String exam_title = CExcelUtil.getCellValue(aCell);
				if ((exam_title == null) || (exam_title.trim().isEmpty()))
					exam_title = null;

				aCell = aRow.getCell(kscol);
				String exam_time = CExcelUtil.getCellValue(aCell);
				if ((exam_time == null) || (exam_time.trim().isEmpty()))
					exam_time = null;

				aCell = aRow.getCell(fxcol);
				String exam_score = CExcelUtil.getCellValue(aCell);
				if ((exam_score == null) || (exam_score.trim().isEmpty()))
					exam_score = null;

				aCell = aRow.getCell(xz1col);
				String psalary = CExcelUtil.getCellValue(aCell);
				if ((psalary == null) || (psalary.trim().isEmpty()))
					psalary = null;

				/*aCell = aRow.getCell(xz2col);
				String nsalary = CExcelUtil.getCellValue(aCell);
				if ((nsalary == null) || (nsalary.trim().isEmpty()))
					nsalary = null;*/

				aCell = aRow.getCell(pjcol);
				String sypj = CExcelUtil.getCellValue(aCell);
				if ((sypj == null) || (sypj.trim().isEmpty()))
					sypj = null;
				
				aCell = aRow.getCell(strucol);
				String stru = CExcelUtil.getCellValue(aCell);
				if ((stru == null) || (stru.trim().isEmpty()))
					stru = null;
				
				aCell = aRow.getCell(npcol);
				String np = CExcelUtil.getCellValue(aCell);
				if ((np == null) || (np.trim().isEmpty()))
					np = null;

				tr.clear();
				String sqlstr = "SELECT * FROM hr_train_reg WHERE stat=9 AND er_id=" + emp.er_id.getValue() + " ORDER BY createtime DESC";
				tr.findBySQL(sqlstr, false);
				if (tr.isEmpty())
					throw new Exception("没有找到人事资料【" + employee_code + "】的实习登记表单");

				getOP(ho, ospcode, emp.ospid.getValue());
				boolean nullho = ho.isEmpty();
				Hr_traintran_batchline etb = new Hr_traintran_batchline();
				etb.er_id.setValue(emp.er_id.getValue()); // 人事档案ID
				etb.employee_code.setValue(emp.employee_code.getValue()); // 工号
				etb.sex.setValue(emp.sex.getValue()); // 性别
				etb.id_number.setValue(emp.id_number.getValue()); // 身份证号
				etb.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				etb.degree.setValue(emp.degree.getValue()); // 学历

				etb.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				etb.enddatetry.setValue(tr.enddatetry.getValue()); // 试用到期日期
				etb.jxdatetry.setValue(tr.jxdatetry.getValue()); // 见习到期日期

				if (nullho) {
					etb.orgid.setValue(emp.orgid.getValue()); // 部门ID
					etb.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
					etb.orgname.setValue(emp.orgname.getValue()); // 部门名称
					etb.ospid.setValue(emp.ospid.getValue()); // 职位ID
					etb.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
					etb.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
					etb.hg_name.setValue(emp.hg_name.getValue()); // 职等
					etb.lv_num.setValue(emp.lv_num.getValue()); // 职级
				} else {
					etb.orgid.setValue(ho.orgid.getValue()); // 部门ID
					etb.orgcode.setValue(ho.orgcode.getValue()); // 部门编码
					etb.orgname.setValue(ho.orgname.getValue()); // 部门名称
					etb.ospid.setValue(ho.ospid.getValue()); // 职位ID
					etb.ospcode.setValue(ho.ospcode.getValue()); // 职位编码
					etb.sp_name.setValue(ho.sp_name.getValue()); // 职位名称
					etb.hg_name.setValue(ho.hg_name.getValue()); // 职等
					etb.lv_num.setValue(ho.lv_num.getValue()); // 职级
				}
				Hr_salary_chglg sc = CtrSalaryCommon.getCur_salary_chglg(emp.er_id.getValue());
				etb.oldstru_id.setValue(sc.newstru_id.getValue()); // 工资结构id
				etb.oldstru_name.setValue(sc.newstru_name.getValue()); // 工资结构
				etb.oldchecklev.setValue(sc.newchecklev.getValue()); // 工资结构id
				etb.oldattendtype.setValue(sc.newattendtype.getValue()); // 工资结构
				etb.oldposition_salary.setValue(sc.newposition_salary.getValue()); // 职位工资
				etb.oldbase_salary.setValue(sc.newbase_salary.getValue()); // 基本工资
				etb.oldotwage.setValue(sc.newotwage.getValue()); // 固定加班工资
				etb.oldtech_salary.setValue(sc.newtech_salary.getValue()); // 技能工资
				etb.oldachi_salary.setValue(sc.newachi_salary.getValue()); // 绩效工资
				etb.oldtech_allowance.setValue(sc.newtech_allowance.getValue()); // 技能津贴
				etb.oldpostsubs.setValue(sc.newpostsubs.getValue()); // 岗位津贴
				etb.newstru_id.setValue(sc.newstru_id.getValue()); // 工资结构id
				etb.newstru_name.setValue(sc.newstru_name.getValue()); // 工资结构
				etb.newchecklev.setValue(sc.newchecklev.getValue()); // 考核层级
				etb.newattendtype.setValue(sc.newattendtype.getValue()); // 出勤类别
				etb.newposition_salary.setValue(sc.newposition_salary.getValue()); // 职位工资
				etb.newbase_salary.setValue(sc.newbase_salary.getValue()); // 基本工资
				etb.newotwage.setValue(sc.newotwage.getValue()); // 固定加班工资
				etb.newtech_salary.setValue(sc.newtech_salary.getValue()); // 技能工资
				etb.newachi_salary.setValue(sc.newachi_salary.getValue()); // 绩效工资
				etb.newtech_allowance.setValue(sc.newtech_allowance.getValue()); // 技能津贴
				etb.newpostsubs.setValue(sc.newpostsubs.getValue()); // 岗位津贴
				DecimalFormat df = new DecimalFormat("0.00");
				Hr_salary_structure ss = new Hr_salary_structure();
				float newposition_salary=sc.newposition_salary.getAsFloat();
				if(np !=null){
					newposition_salary= Float.parseFloat(np);
				}
				if(stru !=null){
					String sqlstr1 = "select * from hr_salary_structure where stat=9 and stru_name='" + stru + "'";
					ss.findBySQL(sqlstr1);
					if (ss.isEmpty()) {
						throw new Exception("工号为【" + employee_code + "】的工资结构【" + stru + "】不存在！");
					}
					etb.newstru_id.setValue(ss.stru_id.getValue()); // 工资结构id
					etb.newstru_name.setValue(ss.stru_name.getValue()); // 工资结构
					etb.newchecklev.setValue(ss.checklev.getValue()); // 考核层级
					etb.newattendtype.setValue(ss.kqtype.getValue()); // 出勤类别
					if(ss.strutype.getAsInt()==1){
						float minstand=0;
						String minsqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+emp.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC  ";
						Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
						oms.findBySQL(minsqlstr);
						if(oms.isEmpty()){
							minstand=0;
						}else{
							minstand=oms.minstandard.getAsFloatDefault(0);
						}
						float bw=(newposition_salary*ss.basewage.getAsFloatDefault(0))/100;
						float bow=(newposition_salary*ss.otwage.getAsFloatDefault(0))/100;
						float sw=(newposition_salary*ss.skillwage.getAsFloatDefault(0))/100;
						float pw=(newposition_salary*ss.meritwage.getAsFloatDefault(0))/100;
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
								bw=newposition_salary;
								pw=0;
								sw=0;
								bow=0;
							}
						}
						etb.newposition_salary.setValue(newposition_salary); // 职位工资
						etb.newbase_salary.setValue(df.format(bw)); // 调薪后基本工资
						etb.newtech_salary.setValue(df.format(sw)); // 调薪后技能工资
						etb.newachi_salary.setValue(df.format(pw)); // 调薪后绩效工资
						etb.newotwage.setValue(df.format(bow)); // 调薪后固定加班工资
					}
				}
			
				etb.norgid.setValue(tr.norgid.getValue()); // 拟用人部门ID
				etb.norgname.setValue(tr.norgname.getValue()); // 拟用人部门名称
				etb.nospid.setValue(tr.nospid.getValue()); // 拟职位ID
				etb.nsp_name.setValue(tr.nsp_name.getValue()); // 拟职位名称
				if (exam_title != null)
					etb.exam_title.setValue(exam_title); // 考试课题
				if (exam_time != null)
					etb.exam_time.setValue(exam_time); // 考试时间
				if (exam_score != null)
					etb.exam_score.setValue(exam_score); // 考试分数
				if (psalary != null)
					etb.psalary.setValue(psalary); // 转正前薪资
				/*if (nsalary != null)
					etb.nsalary.setValue(nsalary); // 转正后薪资*/
				if (sypj != null)
					etb.sypj.setValue(sypj); // 试用期评价
				rls.add(etb);
			}
		}
		return rls.tojson();
	}

	// 序号 工号 姓名 机构名称 分配职位编码 分配职位名称
	private String parserExcelSheet_traindisp_batch(HSSFSheet aSheet) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		HSSFRow aRow = aSheet.getRow(0);
		int empcol = CExcelUtil.colByValue(aRow, "工号", true);
		int ospcol = CExcelUtil.colByValue(aRow, "分配职位编码", true);
		int strucol = CExcelUtil.colByValue(aRow, "转正后工资结构", true);
		int npcol = CExcelUtil.colByValue(aRow, "转正后职位工资", true);
		CJPALineData<Hr_traindisp_batchline> rls = new CJPALineData<Hr_traindisp_batchline>(Hr_traindisp_batchline.class);
		Hr_employee emp = new Hr_employee();
		Hr_orgposition ho = new Hr_orgposition();
		Hr_train_reg tr = new Hr_train_reg();
		Hr_employeestat es = new Hr_employeestat();
		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {
				HSSFCell aCell = aRow.getCell(empcol);
				String employee_code = CExcelUtil.getCellValue(aCell);
				if ((employee_code == null) || (employee_code.isEmpty()))
					continue;
				emp.clear();
				emp.findBySQL("select * from hr_employee where employee_code='" + employee_code + "'", false);
				if (emp.isEmpty())
					throw new Exception("没有找到工号为【" + employee_code + "】的员工资料");
				es.clear();
				es.findBySQL("select * from hr_employeestat where statvalue=" + emp.empstatid.getValue());
				if (es.statvalue.getAsInt() != 8) {// 见习期
					throw new Exception("工号为【" + employee_code + "】的员工状态为【" + es.language1.getValue() + "】不能做【实习生分配操作】");
				}

				aCell = aRow.getCell(ospcol);
				String ospcode = CExcelUtil.getCellValue(aCell);
				if ((ospcode == null) || (ospcode.trim().isEmpty()))
					ospcode = null;
				aCell = aRow.getCell(strucol);
				String stru = CExcelUtil.getCellValue(aCell);
				if ((stru == null) || (stru.trim().isEmpty()))
					stru = null;
				
				aCell = aRow.getCell(npcol);
				String np = CExcelUtil.getCellValue(aCell);
				if ((np == null) || (np.trim().isEmpty()))
					np = null;

				tr.clear();
				String sqlstr = "SELECT * FROM hr_train_reg WHERE stat=9 AND er_id=" + emp.er_id.getValue() + " ORDER BY createtime DESC";
				tr.findBySQL(sqlstr, false);
				if (tr.isEmpty())
					throw new Exception("没有找到人事资料【" + employee_code + "】的实习登记表单");

				getOP(ho, ospcode, tr.nospid.getValue());
				if (ho.isEmpty())
					throw new Exception("没找到对应机构角色资料");

				Hr_traindisp_batchline etb = new Hr_traindisp_batchline();
				etb.er_id.setValue(emp.er_id.getValue()); // 人事档案ID
				etb.employee_code.setValue(emp.employee_code.getValue()); // 工号
				etb.sex.setValue(emp.sex.getValue()); // 性别
				etb.id_number.setValue(emp.id_number.getValue()); // 身份证号
				etb.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				etb.degree.setValue(emp.degree.getValue()); // 学历
				etb.jxdatetry.setValue(tr.jxdatetry.getValue()); // 见习到期日期

				etb.orgid.setValue(emp.orgid.getValue()); // 部门ID
				etb.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				etb.orgname.setValue(emp.orgname.getValue()); // 部门名称
				etb.ospid.setValue(emp.ospid.getValue()); // 职位ID
				etb.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				etb.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				etb.hg_name.setValue(emp.hg_name.getValue()); // 职等
				etb.lv_num.setValue(emp.lv_num.getValue()); // 职级
				etb.norgid.setValue(ho.orgid.getValue()); // 拟用人部门ID
				etb.norgname.setValue(ho.orgname.getValue()); // 拟用人部门名称
				etb.nospid.setValue(ho.ospid.getValue()); // 拟职位ID
				etb.nsp_name.setValue(ho.sp_name.getValue()); // 拟职位名称
				etb.nhg_name.setValue(ho.hg_name.getValue()); // 职等
				etb.nlv_num.setValue(ho.lv_num.getValue()); // 职级
				Hr_salary_chglg sc = CtrSalaryCommon.getCur_salary_chglg(emp.er_id.getValue());
				etb.oldstru_id.setValue(sc.newstru_id.getValue()); // 工资结构id
				etb.oldstru_name.setValue(sc.newstru_name.getValue()); // 工资结构
				etb.oldchecklev.setValue(sc.newchecklev.getValue()); // 工资结构id
				etb.oldattendtype.setValue(sc.newattendtype.getValue()); // 工资结构
				etb.oldposition_salary.setValue(sc.newposition_salary.getValue()); // 职位工资
				etb.oldbase_salary.setValue(sc.newbase_salary.getValue()); // 基本工资
				etb.oldotwage.setValue(sc.newotwage.getValue()); // 固定加班工资
				etb.oldtech_salary.setValue(sc.newtech_salary.getValue()); // 技能工资
				etb.oldachi_salary.setValue(sc.newachi_salary.getValue()); // 绩效工资
				etb.oldtech_allowance.setValue(sc.newtech_allowance.getValue()); // 技能津贴
				etb.oldpostsubs.setValue(sc.newpostsubs.getValue()); // 岗位津贴
				etb.newstru_id.setValue(sc.newstru_id.getValue()); // 工资结构id
				etb.newstru_name.setValue(sc.newstru_name.getValue()); // 工资结构
				etb.newchecklev.setValue(sc.newchecklev.getValue()); // 考核层级
				etb.newattendtype.setValue(sc.newattendtype.getValue()); // 出勤类别
				etb.newposition_salary.setValue(sc.newposition_salary.getValue()); // 职位工资
				etb.newbase_salary.setValue(sc.newbase_salary.getValue()); // 基本工资
				etb.newotwage.setValue(sc.newotwage.getValue()); // 固定加班工资
				etb.newtech_salary.setValue(sc.newtech_salary.getValue()); // 技能工资
				etb.newachi_salary.setValue(sc.newachi_salary.getValue()); // 绩效工资
				etb.newtech_allowance.setValue(sc.newtech_allowance.getValue()); // 技能津贴
				etb.newpostsubs.setValue(sc.newpostsubs.getValue()); // 岗位津贴
				DecimalFormat df = new DecimalFormat("0.00");
				Hr_salary_structure ss = new Hr_salary_structure();
				float newposition_salary=sc.newposition_salary.getAsFloat();
				if(np !=null){
					newposition_salary= Float.parseFloat(np);
				}
				if(stru !=null){
					String sqlstr1 = "select * from hr_salary_structure where stat=9 and stru_name='" + stru + "'";
					ss.findBySQL(sqlstr1);
					if (ss.isEmpty()) {
						throw new Exception("工号为【" + employee_code + "】的工资结构【" + stru + "】不存在！");
					}
					etb.newstru_id.setValue(ss.stru_id.getValue()); // 工资结构id
					etb.newstru_name.setValue(ss.stru_name.getValue()); // 工资结构
					etb.newchecklev.setValue(ss.checklev.getValue()); // 考核层级
					etb.newattendtype.setValue(ss.kqtype.getValue()); // 出勤类别
					if(ss.strutype.getAsInt()==1){
						float minstand=0;
						String minsqlstr="SELECT * FROM `hr_salary_orgminstandard` WHERE stat=9 AND usable=1 AND INSTR('"+emp.idpath.getValue()+"',idpath)=1  ORDER BY idpath DESC  ";
						Hr_salary_orgminstandard oms=new Hr_salary_orgminstandard();
						oms.findBySQL(minsqlstr);
						if(oms.isEmpty()){
							minstand=0;
						}else{
							minstand=oms.minstandard.getAsFloatDefault(0);
						}
						float bw=(newposition_salary*ss.basewage.getAsFloatDefault(0))/100;
						float bow=(newposition_salary*ss.otwage.getAsFloatDefault(0))/100;
						float sw=(newposition_salary*ss.skillwage.getAsFloatDefault(0))/100;
						float pw=(newposition_salary*ss.meritwage.getAsFloatDefault(0))/100;
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
								bw=newposition_salary;
								pw=0;
								sw=0;
								bow=0;
							}
						}
						etb.newposition_salary.setValue(newposition_salary); // 职位工资
						etb.newbase_salary.setValue(df.format(bw)); // 调薪后基本工资
						etb.newtech_salary.setValue(df.format(sw)); // 调薪后技能工资
						etb.newachi_salary.setValue(df.format(pw)); // 调薪后绩效工资
						etb.newotwage.setValue(df.format(bow)); // 调薪后固定加班工资
					}
				}
				rls.add(etb);
			}
		}
		return rls.tojson();
	}

	private void getOP(Hr_orgposition ho, String ospcode, String ospid) throws Exception {
		ho.clear();
		if (ospcode != null) {
			String sqlstr = "SELECT * FROM hr_orgposition WHERE usable=1 and ospcode ='" + ospcode + "'";
			ho.findBySQL(sqlstr, false);
			if (ho.isEmpty())
				throw new Exception("机构岗位【" + ospcode + "】不存在或不可用");
		} else {
			if ((ospid == null) || ospid.isEmpty())
				return;
			else
				ho.findByID(ospid, false);
		}
	}

}
