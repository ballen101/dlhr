package com.hr.insurance.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="预生成购保单明细表")
public class Hr_ins_prebuyins extends CJPA {
	@CFieldinfo(fieldname="pbi_id",iskey=true,notnull=true,precision=20,scale=0,caption="ID",datetype=Types.INTEGER)
	public CField pbi_id;  //ID
	@CFieldinfo(fieldname="er_id",precision=10,scale=0,caption="档案ID",datetype=Types.INTEGER)
	public CField er_id;  //档案ID
	@CFieldinfo(fieldname="employee_code",precision=16,scale=0,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",precision=64,scale=0,caption="员工姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //员工姓名
	@CFieldinfo(fieldname="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="ospid",precision=20,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",precision=128,scale=0,caption="职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //职位名称
	@CFieldinfo(fieldname="lv_id",precision=10,scale=0,caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",precision=4,scale=1,caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="sex",precision=1,scale=0,caption="性别",datetype=Types.INTEGER)
	public CField sex;  //性别
	@CFieldinfo(fieldname="hiredday",precision=19,scale=0,caption="入职日期",datetype=Types.TIMESTAMP)
	public CField hiredday;  //入职日期
	@CFieldinfo(fieldname="birthday",notnull=true,precision=19,scale=0,caption="出生日期",datetype=Types.TIMESTAMP)
	public CField birthday;  //出生日期
	@CFieldinfo(fieldname="tranfcmpdate",precision=19,scale=0,caption="调动生效日期",datetype=Types.TIMESTAMP)
	public CField tranfcmpdate;  //调动生效日期
	@CFieldinfo(fieldname="dobuyinsdate",precision=19,scale=0,caption="购保日期",datetype=Types.TIMESTAMP)
	public CField dobuyinsdate;  //购保日期
	@CFieldinfo(fieldname="isbuyins",precision=2,scale=0,caption="是否已购保",defvalue="2",datetype=Types.INTEGER)
	public CField isbuyins;  //是否已购保
	@CFieldinfo(fieldname="sourceid",precision=10,scale=0,caption="来源单ID",datetype=Types.INTEGER)
	public CField sourceid;  //来源单ID
	@CFieldinfo(fieldname="sourcecode",precision=16,scale=0,caption="来源单编码",datetype=Types.VARCHAR)
	public CField sourcecode;  //来源单编码
	@CFieldinfo(fieldname="buyins_id",precision=10,scale=0,caption="保险购买ID",datetype=Types.INTEGER)
	public CField buyins_id;  //保险购买ID
	@CFieldinfo(fieldname="buyins_code",precision=16,scale=0,caption="保险购买编码",datetype=Types.VARCHAR)
	public CField buyins_code;  //保险购买编码
	@CFieldinfo(fieldname="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname = "registertype", caption = "户籍类型", datetype = Types.INTEGER)
	public CField registertype; // 户籍类型
	@CFieldinfo(fieldname="idpath",notnull=true,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="prebuytype",precision=2,scale=0,caption="预生成类型",datetype=Types.INTEGER)
	public CField prebuytype;  //预生成类型
	@CFieldinfo(fieldname="degree",precision=2,scale=0,caption="学历",datetype=Types.INTEGER)
	public CField degree;  //学历
	@CFieldinfo(fieldname="nativeplace",precision=128,scale=0,caption="籍贯",datetype=Types.VARCHAR)
	public CField nativeplace;  //籍贯
	@CFieldinfo(fieldname="id_number",precision=20,scale=0,caption="身份证号",datetype=Types.VARCHAR)
	public CField id_number;  //身份证号
	@CFieldinfo(fieldname="registeraddress",precision=256,scale=0,caption="户籍住址",datetype=Types.VARCHAR)
	public CField registeraddress;  //户籍住址
	@CFieldinfo(fieldname="telphone",precision=64,scale=0,caption="联系电话",datetype=Types.VARCHAR)
	public CField telphone;  //联系电话
	@CFieldinfo(fieldname="transorg",precision=64,scale=0,caption="输送机构",datetype=Types.VARCHAR)
	public CField transorg;  //输送机构
	@CFieldinfo(fieldname="dispunit",precision=64,scale=0,caption="派遣机构",datetype=Types.VARCHAR)
	public CField dispunit;  //派遣机构
	@CFieldinfo(fieldname="age",precision=16,scale=0,caption="年龄",datetype=Types.VARCHAR)
	public CField age;  //年龄
	@CFieldinfo(fieldname="creator",notnull=true,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_ins_prebuyins() throws Exception {
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
