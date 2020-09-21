/**
 * 
 */
package com.hr.attd.co;
import java.io.File;
import java.io.FileInputStream;
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
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.UpLoadFileEx;
import com.hr.perm.entity.Hr_employee;
import com.hr.attd.entity.Hrkq_business_trip_batchline;

/**
 * @ClassName:COHrkq_business_trip_batch
 * @Description:TODO
 * @author David
 * @date 2019年12月5日 下午3:11:46
 */
@ACO(coname = "web.hrkq.businesstripbatch")
public class COHrkq_business_trip_batch {
	@ACOAction(eventname = "impexcel", Authentication = true, notes = "从excel导入出差人员信息")
	public String impexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		//String orgid = CorUtil.hashMap2Str(CSContext.getParms(), "orgid", "需要参数orgid");
		String orgid="";
		

		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			String rst = parserExcelFile(p, orgid);
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
			return rst;
		} else
			return "[]";
	}
	
	private String parserExcelFile(Shw_physic_file pf, String orgid) throws Exception {
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
		return parserExcelSheet(aSheet, orgid);
	}
	private String parserExcelSheet(HSSFSheet aSheet, String orgid) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return "[]";
		}
		int empcol = -1;
		int tripreasoncol = -1;
		int triptypecol = -1;
		int destincol = -1;
		int begindatecol = -1;
		int enddatecol = -1;
		int isagentcol = -1;

		HSSFRow aRow = aSheet.getRow(0);
		for (int col = 0; col <= aRow.getLastCellNum(); col++) {
			HSSFCell aCell = aRow.getCell(col);
			String celltext = CExcelUtil.getCellValue(aCell);
			if ((celltext == null) || (celltext.isEmpty()))
				continue;
			if ("工号".equals(celltext.trim())) {
				empcol = col;
			}
			if ("流程代理".equals(celltext.trim())) {
				isagentcol = col;
			}

			if ("出差事由".equals(celltext.trim())) {
				tripreasoncol = col;
			}
			
			if ("出差类型".equals(celltext.trim())) {
				triptypecol = col;
			}
			if ("出差地点".equals(celltext.trim())) {
				destincol = col;
			}
			if ("出差开始时间".equals(celltext.trim())) {
				begindatecol = col;
			}
			if ("出差结束时间".equals(celltext.trim())) {
				enddatecol = col;
			}
		}
		if ((empcol == -1) || (triptypecol == -1) || (destincol == -1)|| (begindatecol == -1)|| (enddatecol == -1)) {
			throw new Exception("没找到【工号】【出差类型】【出差地点】【出差开始时间】或【出差结束时间】列");
		}



		//boolean ctrl = (Integer.valueOf(HrkqUtil.getParmValueErr("TRANSFERBATCH_CTROL")) == 1);
		//boolean hasr37 = Hrkq_business_trip_batch..hasaccrole37();

		CJPALineData<Hrkq_business_trip_batchline> rls = new CJPALineData<Hrkq_business_trip_batchline>(Hrkq_business_trip_batchline.class);
		Hr_employee emp = new Hr_employee();

		for (int row = 1; row <= aSheet.getLastRowNum(); row++) {
			aRow = aSheet.getRow(row);
			if (null != aRow) {
				HSSFCell aCell = aRow.getCell(empcol);

				String employee_code = CExcelUtil.getCellValue(aCell);
				if (employee_code == null)
					continue;

				emp.findBySQL("select * from hr_employee where employee_code='" + employee_code + "'", false);
				if (emp.isEmpty())
					throw new Exception("没有找到工号为【" + employee_code + "】的员工资料");

				aCell = aRow.getCell(tripreasoncol);
				String tripreason = CExcelUtil.getCellValue(aCell);
				if ((tripreason == null) || (tripreason.isEmpty()))
					tripreason = null;
				
				aCell = aRow.getCell(triptypecol);
				String triptype = CExcelUtil.getCellValue(aCell);

				aCell = aRow.getCell(isagentcol);
				String isagent = CExcelUtil.getCellValue(aCell);
				

				aCell = aRow.getCell(destincol);
				String destin = CExcelUtil.getCellValue(aCell);
				
				aCell = aRow.getCell(begindatecol);
				String begindate = CExcelUtil.getCellValue(aCell);
				
				aCell = aRow.getCell(enddatecol);
				String enddate = CExcelUtil.getCellValue(aCell);
				
				
				

				Hrkq_business_trip_batchline etb = new Hrkq_business_trip_batchline();
				etb.er_id.setValue(emp.er_id.getValue()); // 人事ID
				etb.employee_code.setValue(emp.employee_code.getValue()); // 工号
				
				etb.employee_name.setValue(emp.employee_name.getValue()); // 姓名

				etb.orgid.setValue(emp.orgid.getValue()); // 部门ID
				etb.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				etb.orgname.setValue(emp.orgname.getValue()); // 部门名称
				etb.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
				etb.lv_num.setValue(emp.lv_num.getValue()); // 职级

				etb.ospid.setValue(emp.ospid.getValue()); // 职位ID
				etb.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				etb.sp_name.setValue(emp.sp_name.getValue()); // 职位名称


				etb.trip_type.setValue("国内出差".equals(triptype)?1:2); // 出差类型
				etb.destination.setValue(destin); // 出差目的地
				etb.begin_date.setValue(begindate); // 出差开始日期
				etb.end_date.setValue(enddate); // 出差结束日期
				etb.tripreason.setValue(tripreason); // 出差事由
				etb.iswfagent.setValue("是".equals(isagent)?1:2); // 是否代理
				//etb.is_agent.setValue(1); // 是否代理

				rls.add(etb);
			}
		}
		return rls.tojson();
	}

}
