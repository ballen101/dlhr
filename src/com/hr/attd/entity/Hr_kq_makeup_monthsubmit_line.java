package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(tablename="Hr_kq_makeup_monthsubmit_line")
public class Hr_kq_makeup_monthsubmit_line extends CJPA {
	@CFieldinfo(fieldname ="mmkql_id",iskey=true,notnull=true,precision=11,scale=0,caption="mmkql_id",datetype=Types.INTEGER)
	public CField mmkql_id;  //mmkql_id
	@CFieldinfo(fieldname ="mmkq_id",notnull=true,precision=11,scale=0,caption="mmkq_id",datetype=Types.INTEGER)
	public CField mmkq_id;  //mmkq_id
	@CFieldinfo(fieldname ="er_id",precision=10,scale=0,caption="员工档案ID",datetype=Types.INTEGER)
	public CField er_id;  //员工档案ID
	@CFieldinfo(fieldname ="employee_code",precision=16,scale=0,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname ="employee_name",precision=64,scale=0,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname ="orgid",precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="idpath",precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname ="ospid",precision=20,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname ="ospcode",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname ="sp_name",precision=128,scale=0,caption="标准职位名称",defvalue="0",datetype=Types.VARCHAR)
	public CField sp_name;  //标准职位名称
	@CFieldinfo(fieldname ="lv_id",precision=10,scale=0,caption="职级ID",defvalue="0",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname ="lv_num",precision=4,scale=1,caption="职级",defvalue="0",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname ="bcqts",precision=5,scale=1,caption="补出勤天数",defvalue="0",datetype=Types.DECIMAL)
	public CField bcqts;  //补出勤天数
	@CFieldinfo(fieldname ="khcqts",precision=11,scale=1,caption="扣回出勤天数",defvalue="0",datetype=Types.DECIMAL)
	public CField khcqts;  //扣回出勤天数
	@CFieldinfo(fieldname ="bpsjb",precision=11,scale=1,caption="补平时加班（H）",defvalue="0",datetype=Types.DECIMAL)
	public CField bpsjb;  //补平时加班（H）
	@CFieldinfo(fieldname ="bxxsjjb",precision=11,scale=1,caption="补休息时间加班（H）",defvalue="0",datetype=Types.DECIMAL)
	public CField bxxsjjb;  //补休息时间加班（H）
	@CFieldinfo(fieldname ="yxj",precision=11,scale=1,caption="有薪假",defvalue="0",datetype=Types.DECIMAL)
	public CField yxj;  //有薪假
	@CFieldinfo(fieldname ="yxcj",precision=11,scale=1,caption="有薪产假",defvalue="0",datetype=Types.DECIMAL)
	public CField yxcj;  //有薪产假
	@CFieldinfo(fieldname ="bj",precision=11,scale=1,caption="病假",defvalue="0",datetype=Types.DECIMAL)
	public CField bj;  //病假
	@CFieldinfo(fieldname ="fdjrjb",precision=11,scale=1,caption="法定假日加班（H）",defvalue="0",datetype=Types.DECIMAL)
	public CField fdjrjb;  //法定假日加班（H）
	@CFieldinfo(fieldname ="bztcs",precision=11,scale=0,caption="补早迟次数",defvalue="0",datetype=Types.INTEGER)
	public CField bztcs;  //补早迟次数
	@CFieldinfo(fieldname ="bhkcdcqcs",precision=11,scale=0,caption="补回扣除的超签次数",defvalue="0",datetype=Types.INTEGER)
	public CField bhkcdcqcs;  //补回扣除的超签次数
	@CFieldinfo(fieldname ="bfkcdkgts",precision=11,scale=1,caption="补发扣除的旷工天数",defvalue="0",datetype=Types.DECIMAL)
	public CField bfkcdkgts;  //补发扣除的旷工天数
	@CFieldinfo(fieldname ="bfjbf",precision=11,scale=2,caption="补发加班费（元）",defvalue="0",datetype=Types.DECIMAL)
	public CField bfjbf;  //补发加班费（元）
	@CFieldinfo(fieldname ="kccqts",precision=11,scale=1,caption="扣除出勤天数",defvalue="0",datetype=Types.DECIMAL)
	public CField kccqts;  //扣除出勤天数
	@CFieldinfo(fieldname ="kccqcs",precision=11,scale=0,caption="扣除超签次数",defvalue="0",datetype=Types.INTEGER)
	public CField kccqcs;  //扣除超签次数
	@CFieldinfo(fieldname ="kcpsjbss",precision=11,scale=1,caption="扣除平时加班时数（H）",defvalue="0",datetype=Types.DECIMAL)
	public CField kcpsjbss;  //扣除平时加班时数（H）
	@CFieldinfo(fieldname ="kcxxsjjb",precision=11,scale=1,caption="扣除休息时间加班（H）",defvalue="0",datetype=Types.DECIMAL)
	public CField kcxxsjjb;  //扣除休息时间加班（H）
	@CFieldinfo(fieldname ="bffwfl",precision=11,scale=2,caption="补发法务福利（元）",defvalue="0",datetype=Types.DECIMAL)
	public CField bffwfl;  //补发法务福利（元）
	@CFieldinfo(fieldname ="fdyxjts",precision=11,scale=1,caption="法定有薪假天数",defvalue="0",datetype=Types.DECIMAL)
	public CField fdyxjts;  //法定有薪假天数
	@CFieldinfo(fieldname ="remark",precision=255,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_kq_makeup_monthsubmit_line() throws Exception {
	}

	@Override
	public boolean InitObject() {//类初始化调用的方法
		super.InitObject();
		return true;
	} 

	@Override
	public boolean FinalObject() { //类释放前调用的方法
		super.FinalObject(); 
		return true; 
	} 
} 

