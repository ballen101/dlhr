package com.hr.canteen.co;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
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
import com.hr.attd.entity.Hrkq_workschmonthlist;
import com.hr.canteen.entity.Hr_canteen_cardreader;
import com.hr.canteen.entity.Hr_canteen_cardrelatems;
import com.hr.canteen.entity.Hr_canteen_guest;
import com.hr.canteen.entity.Hr_canteen_mealcharge;
import com.hr.canteen.entity.Hr_canteen_mealclass;
import com.hr.canteen.entity.Hr_canteen_mealsystem;
import com.hr.canteen.entity.Hr_canteen_room;
import com.hr.canteen.entity.Hr_canteen_special;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_contract;

@ACO(coname = "web.hrct.canteen")
public class COHr_canteen {
	@ACOAction(eventname = "impcardreaderlistexcel", Authentication = true, ispublic = false, notes = "批量导入饭堂卡机Excel")
	public String impcardreaderlistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_cardlist(p, batchno);
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

	private int parserExcelFile_cardlist(Shw_physic_file pf, String batchno) throws Exception {
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
		return parserExcelSheet_cardlist(aSheet, batchno);
	}

	private int parserExcelSheet_cardlist(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_cardlist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_canteen_cardreader cr = new Hr_canteen_cardreader();
		Hr_canteen_cardreader ctcard = new Hr_canteen_cardreader();
		Hr_canteen_room room = new Hr_canteen_room();
		CDBConnection con = room.pool.getCon(this);

		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String roomname = v.get("ctr_name");
				if ((roomname == null) || (roomname.isEmpty()))
					continue;
				rst++;
				room.clear();
				room.findBySQL("SELECT * FROM hr_canteen_room WHERE ctr_name='" + roomname + "'");
				if (room.isEmpty())
					throw new Exception("名称为【" + roomname + "】的餐厅资料不存在");

				String ctsyncode = v.get("sync_sn");// 原系统餐厅编号，用作关联
				if ((ctsyncode == null) || (ctsyncode.isEmpty()))
					throw new Exception("在餐厅【" + roomname + "】配置的原卡机编号不能为空");
				String sqlstr1 = "SELECT * FROM `hr_canteen_cardreader` WHERE sync_sn='" + ctsyncode + "'  and stat=9 AND ctr_name='" + roomname + "'";
				cr.clear();
				cr.findBySQL(sqlstr1);
				if (!cr.isEmpty())
					throw new Exception("编号为【" + ctsyncode + "】的卡机在餐厅【" + roomname + "】已配置");

				ctcard.clear();
				ctcard.ctr_id.setValue(room.ctr_id.getValue()); // 餐厅ID
				ctcard.ctr_code.setValue(room.ctr_code.getValue()); // 餐厅编码
				ctcard.ctr_name.setValue(room.ctr_name.getValue()); // 餐厅名称
				ctcard.area.setValue(room.address.getValue()); // 区域
				ctcard.ctcr_name.setValue(v.get("ctcr_name")); // 卡机名称
				ctcard.brand.setValue(v.get("brand")); // 品牌
				ctcard.model.setValue(v.get("model")); // 型号
				ctcard.buydate.setValue(v.get("buydate")); // 购置日期
				ctcard.numbers.setValue(v.get("numbers")); // 购置数量
				ctcard.sync_sn.setValue(v.get("sync_sn")); // 原卡机编号
				ctcard.remark.setValue(v.get("remark")); // 备注

				ctcard.save(con);
				ctcard.wfcreate(null, con);
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

	private List<CExcelField> initExcelFields_cardlist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("原卡机编号", "sync_sn", true));
		efields.add(new CExcelField("名称", "ctcr_name", true));
		efields.add(new CExcelField("品牌", "brand", true));
		efields.add(new CExcelField("型号", "model", true));
		efields.add(new CExcelField("配置餐厅", "ctr_name", true));
		efields.add(new CExcelField("购置日期", "buydate", true));
		efields.add(new CExcelField("配置区域", "area", true));
		efields.add(new CExcelField("配置数量", "numbers", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "impctroomlistexcel", Authentication = true, ispublic = false, notes = "批量导入饭堂餐厅Excel(初始化导入原始餐厅)")
	public String impctroomlistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_ctroom(p, batchno);
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

	private int parserExcelFile_ctroom(Shw_physic_file pf, String batchno) throws Exception {
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
		return parserExcelSheet_ctroom(aSheet, batchno);
	}

	private int parserExcelSheet_ctroom(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_ctroomlist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_canteen_room rooms = new Hr_canteen_room();
		Hr_canteen_room temproom = new Hr_canteen_room();
		CDBConnection con = temproom.pool.getCon(this);
		DictionaryTemp dictemp = new DictionaryTemp();// 数据字典缓存
		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String roomsyncode = v.get("sync_sn");
				if ((roomsyncode == null) || (roomsyncode.isEmpty()))
					throw new Exception("原餐厅编号不能为空");
				rst++;
				temproom.clear();
				temproom.findBySQL("SELECT * FROM hr_canteen_room WHERE stat=9 and sync_sn='" + roomsyncode + "'");
				if (!temproom.isEmpty())
					throw new Exception("编号为【" + roomsyncode + "】的餐厅已存在");
				int roomtype = Integer.valueOf(dictemp.getVbCE("1090", v.get("cttype"), false, "编号【" + roomsyncode + "】餐厅性质【" + v.get("cttype")
						+ "】不存在"));
				String qualdata = v.get("qualification");
				String[] qualdatas = qualdata.split(",");
				String qualvalues = "";
				for (int i = 0; i < qualdatas.length; i++) {
					String qual = dictemp.getVbCE("1169", qualdatas[i], false, "编号【" + roomsyncode + "】公司资质【" + qualdatas[i]
							+ "】不存在");
					qualvalues = qualvalues + qual + ",";
				}
				qualvalues = qualvalues.substring(0, qualvalues.length() - 1);
				int license = Integer.valueOf(dictemp.getVbCE("916", v.get("hygienelicense"), false, "编号【" + roomsyncode + "】有无卫生许可【" + v.get("hygienelicense")
						+ "】不存在，只能填【有】或【无】"));
				String sqlstr1 = "SELECT * FROM `hr_canteen_room` WHERE sync_sn='" + roomsyncode + "'  and stat=9 ";
				temproom.clear();
				temproom.findBySQL(sqlstr1);
				if (!temproom.isEmpty())
					throw new Exception("编号为【" + roomsyncode + "】的餐厅已存在");

				rooms.clear();
				rooms.remark.setValue(v.get("remark")); // 备注
				rooms.ctr_name.setValue(v.get("ctr_name")); // 餐厅名称
				rooms.address.setValue(v.get("address")); // 地点
				rooms.supplier.setValue(v.get("supplier")); // 供应商
				rooms.contact.setValue(v.get("contact")); // 联系人
				rooms.cellphone.setValue(v.get("cellphone")); // 固定电话
				rooms.cttype.setAsInt(roomtype); // 餐厅性质
				rooms.qualification.setValue(qualvalues.toString()); // 公司资质
				rooms.legalperson.setValue(v.get("legalperson")); // 法人
				rooms.hygienelicense.setAsInt(license); // 有无卫生许可
				rooms.licensebg.setValue(v.get("licensebg")); // 签发日期
				rooms.licenseed.setValue(v.get("licenseed")); // 到期日期
				rooms.mobile.setValue(v.get("mobile")); // 手机
				rooms.sync_sn.setValue(v.get("sync_sn")); // 原餐厅编号

				rooms.mbt1.setValue(v.get("mbt1")); // 早餐开始时间
				rooms.met1.setValue(v.get("met1")); // 早餐结束时间
				rooms.mbt2.setValue(v.get("mbt2")); // 中餐开始时间
				rooms.met2.setValue(v.get("met2")); // 中餐结束时间
				rooms.mbt3.setValue(v.get("mbt3")); // 晚餐开始时间
				rooms.met3.setValue(v.get("met3")); // 晚餐结束时间
				rooms.mbt4.setValue(v.get("mbt4")); // 宵夜开始时间
				rooms.met4.setValue(v.get("met4")); // 宵夜结束时间
				rooms.save(con);
				rooms.wfcreate(null, con);
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

	private List<CExcelField> initExcelFields_ctroomlist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("原餐厅编号", "sync_sn", true));
		efields.add(new CExcelField("餐厅名称", "ctr_name", true));
		efields.add(new CExcelField("地点", "address", true));
		efields.add(new CExcelField("餐厅性质", "cttype", true));
		efields.add(new CExcelField("供应商", "supplier", true));
		efields.add(new CExcelField("法人", "legalperson", true));
		efields.add(new CExcelField("联系人", "contact", true));
		efields.add(new CExcelField("固定电话", "cellphone", true));
		efields.add(new CExcelField("手机", "mobile", true));
		efields.add(new CExcelField("公司资质", "qualification", true));
		efields.add(new CExcelField("有无卫生许可", "hygienelicense", true));
		efields.add(new CExcelField("签发日期", "licensebg", true));
		efields.add(new CExcelField("到期日期", "licenseed", true));
		efields.add(new CExcelField("早餐开始时间", "mbt1", true));
		efields.add(new CExcelField("早餐结束时间", "met1", true));
		efields.add(new CExcelField("中餐开始时间", "mbt2", true));
		efields.add(new CExcelField("中餐结束时间", "met2", true));
		efields.add(new CExcelField("晚餐开始时间", "mbt3", true));
		efields.add(new CExcelField("晚餐结束时间", "met3", true));
		efields.add(new CExcelField("宵夜开始时间", "mbt4", true));
		efields.add(new CExcelField("宵夜结束时间", "met4", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "impctspecillistexcel", Authentication = true, ispublic = false, notes = "批量导入特殊用餐申请Excel")
	public String impctguestlistexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_ctspecil(p, batchno);
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

	private int parserExcelFile_ctspecil(Shw_physic_file pf, String batchno) throws Exception {
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
		return parserExcelSheet_ctspecil(aSheet, batchno);
	}

	private int parserExcelSheet_ctspecil(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_ctspecil();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_canteen_special ctsp = new Hr_canteen_special();
		Hr_employee emp = new Hr_employee();
		Hr_canteen_room room = new Hr_canteen_room();

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
				if (emp.isEmpty())
					throw new Exception("工号【" + employee_code + "】不存在人事资料");

				String ctroomname = v.get("ctr_name");
				if ((ctroomname == null) || (ctroomname.isEmpty()))
					continue;
				room.clear();
				room.findBySQL("SELECT * FROM hr_canteen_room WHERE ctr_name='" + ctroomname + "'");
				if (room.isEmpty())
					throw new Exception("餐厅【" + ctroomname + "】不存在");

				String standardtype = v.get("standard_type");
				String[] standardtypes = standardtype.split(",");
				standardtype = "";
				for (int i = 0; i < standardtypes.length; i++) {
					String standard = dictemp.getVbCE("1099", standardtypes[i], false, "餐标类型【" + standardtypes[i]
							+ "】不存在");
					standardtype = standardtype + standard + ",";
				}
				standardtype = standardtype.substring(0, standardtype.length() - 1);

				String classtype = v.get("class_type");
				String[] classtypes = classtype.split(",");
				classtype = "";
				for (int i = 0; i < classtypes.length; i++) {
					String ct = dictemp.getVbCE("1107", classtypes[i], false, "餐类类型【" + classtypes[i]
							+ "】不存在");
					classtype = classtype + ct + ",";
				}
				classtype = classtype.substring(0, classtype.length() - 1);

				int subsidiestype = Integer.valueOf(dictemp.getVbCE("1104", v.get("subsidiestype"), false, "申请补贴类型【" + v.get("subsidiestype")
						+ "】不存在"));
				int applytimetype = Integer.valueOf(dictemp.getVbCE("1101", v.get("applytimetype"), false, "申请期限类型【" + v.get("applytimetype")
						+ "】不存在"));

				ctsp.clear();
				ctsp.remark.setValue(v.get("remark")); // 备注
				ctsp.er_id.setValue(emp.er_id.getValue()); // 人事档案id
				ctsp.employee_code.setValue(emp.employee_code.getValue()); // 申请人工号
				ctsp.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				ctsp.orgid.setValue(emp.orgid.getValue()); // 部门
				ctsp.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				ctsp.orgname.setValue(emp.orgname.getValue()); // 部门名称
				ctsp.ospid.setValue(emp.ospid.getValue()); // 职位id
				ctsp.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				ctsp.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				ctsp.lv_id.setValue(emp.lv_id.getValue()); // 职级id
				ctsp.lv_num.setValue(emp.lv_num.getValue()); // 职级
				ctsp.apply_date.setValue(v.get("apply_date")); // 申请日期
				ctsp.sex.setValue(emp.sex.getValue()); // 性别
				ctsp.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				ctsp.telphone.setValue(emp.telphone.getValue()); // 联系电话
				ctsp.applyreason.setValue(v.get("applyreason")); // 申请原因
				ctsp.standard_type.setValue(standardtype.toString()); // 申请餐标
				ctsp.class_type.setValue(classtype.toString()); // 申请餐类
				ctsp.ctr_id.setValue(room.ctr_id.getValue()); // 用餐餐厅id
				ctsp.ctr_code.setValue(room.ctr_code.getValue()); // 餐厅编码
				ctsp.ctr_name.setValue(room.ctr_name.getValue()); // 餐厅名称
				ctsp.applytimetype.setAsInt(applytimetype); // 申请期限类型
				ctsp.appbg_date.setValue(v.get("appbg_date")); // 开始时间
				ctsp.apped_date.setValue(v.get("apped_date")); // 结束时间
				ctsp.subsidiestype.setAsInt(subsidiestype); // 申请补贴类型
				ctsp.usable.setAsInt(1); // 是否可用
				ctsp.save(con);

			}
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}
	}

	private List<CExcelField> initExcelFields_ctspecil() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("申请人工号", "employee_code", true));
		efields.add(new CExcelField("姓名", "employee_name", true));
		efields.add(new CExcelField("部门", "orgname", true));
		efields.add(new CExcelField("职位", "sp_name", true));
		efields.add(new CExcelField("职级", "lv_num", true));
		efields.add(new CExcelField("申请日期", "apply_date", true));
		efields.add(new CExcelField("性别", "sex", true));
		efields.add(new CExcelField("入职日期", "hiredday", true));
		efields.add(new CExcelField("联系电话", "telphone", true));
		efields.add(new CExcelField("申请原因", "applyreason", true));
		efields.add(new CExcelField("申请餐标", "standard_type", true));
		efields.add(new CExcelField("申请餐类", "class_type", true));
		efields.add(new CExcelField("申请补贴", "subsidiestype", true));
		efields.add(new CExcelField("用餐餐厅", "ctr_name", true));
		efields.add(new CExcelField("申请期限", "applytimetype", true));
		efields.add(new CExcelField("开始日期", "appbg_date", true));
		efields.add(new CExcelField("结束日期", "apped_date", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

	@ACOAction(eventname = "findcardreaderlist", Authentication = true, ispublic = true, notes = "卡机资料通用查询CO")
	public String findcardreaderlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] notnull = {};
		String[] ignParms = { "" };// 忽略的查询条件
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			String sqlstr = "SELECT * FROM hr_canteen_cardreader cr WHERE 1=1 and cr.ctcr_id=" + id;// +" or idpath like '1,%'";
			return (new Hr_canteen_cardreader()).pool.openrowsql2json(sqlstr);
		} else {
			String sqlstr = "SELECT * FROM hr_canteen_cardreader cr WHERE 1=1 ";
			String orderby = " createtime desc ";
			return new CReport(sqlstr, orderby, notnull).findReport(ignParms, null);
		}
	}

	@ACOAction(eventname = "findctroomlist", Authentication = true, ispublic = true, notes = "餐厅通用查询CO")
	public String findctroomlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] notnull = {};
		String[] ignParms = { "" };// 忽略的查询条件
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			// String sqlstr = "SELECT * FROM hr_canteen_room ctr WHERE 1=1 and ctr.ctr_id=" + id;
			// return (new Hr_canteen_room()).pool.openrowsql2json(sqlstr);
			Hr_canteen_room cr = new Hr_canteen_room();
			cr.findByID(id);
			return cr.toString();
		} else {
			String sqlstr = "SELECT * FROM hr_canteen_room ctr WHERE 1=1 ";
			String orderby = " createtime desc ";
			return new CReport(sqlstr, orderby, notnull).findReport(ignParms, null);
		}
	}

	@ACOAction(eventname = "findmealclasslist", Authentication = true, ispublic = true, notes = "餐类通用查询CO")
	public String findmealclasslist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] notnull = {};
		String[] ignParms = { "" };// 忽略的查询条件
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			String sqlstr = "SELECT * FROM hr_canteen_mealclass mc WHERE 1=1 and mc.mc_id=" + id;
			return (new Hr_canteen_mealclass()).pool.openrowsql2json(sqlstr);
		} else {
			String sqlstr = "SELECT * FROM hr_canteen_mealclass mc WHERE 1=1 ";
			String orderby = " createtime desc ";
			return new CReport(sqlstr, orderby, notnull).findReport(ignParms, null);
		}
	}

	@ACOAction(eventname = "findmealchargelist", Authentication = true, ispublic = true, notes = "餐标通用查询CO")
	public String findmealchargelist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] notnull = {};
		String[] ignParms = { "" };// 忽略的查询条件
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			String sqlstr = "SELECT * FROM hr_canteen_mealcharge mc WHERE 1=1 and mc.ctmc_id=" + id;
			return (new Hr_canteen_mealcharge()).pool.openrowsql2json(sqlstr);
		} else {
			String sqlstr = "SELECT * FROM hr_canteen_mealcharge mc WHERE 1=1 ";
			String orderby = " createtime desc ";
			return new CReport(sqlstr, orderby, notnull).findReport(ignParms, null);
		}
	}

	@ACOAction(eventname = "findmealsystemlist", Authentication = true, ispublic = true, notes = "餐制通用查询CO")
	public String findmealsystemlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] notnull = {};
		String[] ignParms = { "" };// 忽略的查询条件
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			String sqlstr = "SELECT * FROM hr_canteen_mealsystem ms WHERE 1=1 and ms.ctms_id=" + id;
			return (new Hr_canteen_mealsystem()).pool.openrowsql2json(sqlstr);
		} else {
			String sqlstr = "SELECT * FROM hr_canteen_mealsystem ms WHERE 1=1 ";
			String orderby = " createtime desc ";
			return new CReport(sqlstr, orderby, notnull).findReport(ignParms, null);
		}
	}

	@ACOAction(eventname = "findguestlist", Authentication = true, ispublic = true, notes = "客餐申请通用查询CO")
	public String findguestlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] notnull = {};
		String[] ignParms = { "" };// 忽略的查询条件
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			String sqlstr = "SELECT * FROM hr_canteen_guest ms WHERE 1=1 and ms.ctg_id=" + id;
			return (new Hr_canteen_guest()).pool.openrowsql2json(sqlstr);
		} else {
			String sqlstr = "SELECT * FROM hr_canteen_guest ms WHERE 1=1 ";
			String orderby = " createtime desc ";
			return new CReport(sqlstr, orderby, notnull).findReport(ignParms, null);
		}
	}

	@ACOAction(eventname = "findspeciallist", Authentication = true, ispublic = true, notes = "特殊用餐申请通用查询CO")
	public String findspeciallist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] notnull = {};
		String[] ignParms = { "" };// 忽略的查询条件
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			String sqlstr = "SELECT * FROM hr_canteen_special ms WHERE 1=1 and ms.ctsp_id=" + id + " order by createtime desc";
			return (new Hr_canteen_special()).pool.openrowsql2json(sqlstr);
		} else {
			String sqlstr = "SELECT * FROM hr_canteen_special ms WHERE 1=1 ";
			String orderby = " createtime desc ";
			return new CReport(sqlstr, orderby, notnull).findReport(ignParms, null);
		}
	}

	@ACOAction(eventname = "impcardrelatemealsystemexcel", Authentication = true, ispublic = false, notes = "批量导入卡机餐制关联Excel")
	public String impcardrelatemealsystemexcel() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String batchno = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 批次号
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		int rst = 0;
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			rst = parserExcelFile_cardrelatelist(p, batchno);
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

	private int parserExcelFile_cardrelatelist(Shw_physic_file pf, String batchno) throws Exception {
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
		return parserExcelSheet_cardrelatelist(aSheet, batchno);
	}

	private int parserExcelSheet_cardrelatelist(Sheet aSheet, String batchno) throws Exception {
		if (aSheet.getLastRowNum() == 0) {
			return 0;
		}
		List<CExcelField> efds = initExcelFields_cardrelatelist();
		efds = CExcelUtil.parserExcelSheetFields(aSheet, efds, 0);// 解析title 并检查必须存在的列
		List<Map<String, String>> values = CExcelUtil.getExcelValues(aSheet, efds, 0);
		Hr_canteen_cardreader cr = new Hr_canteen_cardreader();
		Hr_canteen_cardrelatems cardrelms = new Hr_canteen_cardrelatems();
		Hr_canteen_mealclass mc = new Hr_canteen_mealclass();
		// Hr_canteen_mealsystem ms=new Hr_canteen_mealsystem();
		CDBConnection con = mc.pool.getCon(this);

		con.startTrans();
		int rst = 0;
		try {
			for (Map<String, String> v : values) {
				String cardname = v.get("ctcr_name");
				if ((cardname == null) || (cardname.isEmpty()))
					continue;
				rst++;
				cr.clear();
				cr.findBySQL("SELECT * FROM hr_canteen_cardreader WHERE stat=9 and ctcr_name='" + cardname + "'");
				if (cr.isEmpty())
					throw new Exception("名称为【" + cardname + "】的卡机资料不存在");

				String mcname = v.get("mc_name");//
				if ((mcname == null) || (mcname.isEmpty()))
					throw new Exception("餐类名称【" + mcname + "】不能为空");
				String sqlstr = "SELECT * FROM hr_canteen_mealclass WHERE stat=9  AND mc_name='" + mcname + "'";
				mc.clear();
				mc.findBySQL(sqlstr);
				if (mc.isEmpty())
					throw new Exception("名为【" + mcname + "】的餐类资料不存在！");
				cardrelms.findBySQL("select * from hr_canteen_cardrelatems where ctcr_id=" + cr.ctr_id.getValue() + " and classtype=" + mc.classtype.getValue());
				if (!cardrelms.isEmpty()) {
					throw new Exception("名称为【" + cardname + "】的卡机已存在【" + mcname + "】餐类的关联信息！");
				}
				String price = v.get("price");//
				if ((price == null) || (price.isEmpty()))
					throw new Exception("名称为【" + cardname + "】的卡机的餐类【" + mcname + "】的餐标不能为空");
				/*
				 * String msname = v.get("ctms_name");//
				 * String sub = v.get("subsidies");//
				 * if ((msname == null) || (msname.isEmpty()))
				 * throw new Exception("餐制名称【"+msname+"】不能为空");
				 * if ((price == null) || (price.isEmpty()))
				 * throw new Exception("餐制【"+msname+"】的餐标不能为空");
				 * if ((sub == null) || (sub.isEmpty()))
				 * throw new Exception("餐制【"+msname+"】的补贴金额不能为空");
				 * sqlstr = "SELECT * FROM hr_canteen_mealsystem WHERE stat=9  AND ctms_name='" + msname + "' and mc_id="+mc.mc_id.getValue()+
				 * " and price="+price+" and subsidies="+sub;
				 * ms.clear();
				 * ms.findBySQL(sqlstr);
				 * if (ms.isEmpty())
				 * throw new Exception("名为【"+msname+"】，餐标为【"+price+"】，补贴金额为【"+sub+"】的餐制资料不存在！");
				 */

				cardrelms.clear();
				cardrelms.ctcr_id.setValue(cr.ctcr_id.getValue()); // 卡机ID
				cardrelms.ctcr_code.setValue(cr.ctcr_code.getValue()); // 卡机编码
				cardrelms.ctcr_name.setValue(cr.ctcr_name.getValue()); // 卡机名称
				cardrelms.ctr_id.setValue(cr.ctr_id.getValue()); // 餐厅ID
				cardrelms.ctr_code.setValue(cr.ctr_code.getValue()); // 餐厅编码
				cardrelms.ctr_name.setValue(cr.ctr_name.getValue()); // 餐厅名称

				cardrelms.mc_id.setValue(mc.mc_id.getValue()); // 餐类ID
				cardrelms.mc_name.setValue(mc.mc_name.getValue()); // 餐类名称
				cardrelms.mealbegin.setValue(mc.mealbegin.getValue()); // 用餐开始时间
				cardrelms.mealend.setValue(mc.mealend.getValue()); // 用餐结束时间
				cardrelms.classtype.setValue(mc.classtype.getValue()); // 餐类类型

				// cardrelms.ctms_id.setValue(ms.ctms_id.getValue()); // 餐制ID
				// cardrelms.ctms_code.setValue(ms.ctms_code.getValue()); // 餐制编码
				// cardrelms.ctms_name.setValue(ms.ctms_name.getValue()); // 餐制名称
				cardrelms.price.setValue(price); // 餐标
				// cardrelms.subsidies.setValue(ms.subsidies.getValue()); // 补贴金额
				// cardrelms.emplev.setValue(ms.emplev.getValue()); // 人事层级
				cardrelms.remark.setValue(v.get("remark")); // 备注

				cardrelms.save(con);
				// cardrelms.wfcreate(null, con);
			}
			con.submit();
			return rst;
		} catch (Exception e) {
			con.rollback();
			throw e;
		}

	}

	private List<CExcelField> initExcelFields_cardrelatelist() {
		List<CExcelField> efields = new ArrayList<CExcelField>();
		efields.add(new CExcelField("卡机名称", "ctcr_name", true));
		efields.add(new CExcelField("餐类名称", "mc_name", true));
		// efields.add(new CExcelField("餐制名称", "ctms_name", true));
		efields.add(new CExcelField("餐标", "price", true));
		// efields.add(new CExcelField("补贴金额", "subsidies", true));
		efields.add(new CExcelField("备注", "remark", true));
		return efields;
	}

}
