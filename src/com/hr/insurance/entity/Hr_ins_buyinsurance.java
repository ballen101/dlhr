package com.hr.insurance.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.insurance.ctr.CtrHr_ins_buyinsurance;

import java.sql.Types;

@CEntity(controller =CtrHr_ins_buyinsurance.class)
public class Hr_ins_buyinsurance extends CJPA {
	@CFieldinfo(fieldname="buyins_id",iskey=true,notnull=true,caption="保险购买ID",datetype=Types.INTEGER)
	public CField buyins_id;  //保险购买ID
	@CFieldinfo(fieldname="buyins_code",notnull=true,codeid=104,caption="保险购买编码",datetype=Types.VARCHAR)
	public CField buyins_code;  //保险购买编码
	@CFieldinfo(fieldname="buydday",caption="购买年月",datetype=Types.TIMESTAMP)
	public CField buydday;  //购买年月
	@CFieldinfo(fieldname="payment",caption="缴费总金额",datetype=Types.DECIMAL)
	public CField payment;  //缴费总金额
	@CFieldinfo(fieldname="tselfpay",caption="个人承担金额",datetype=Types.DECIMAL)
	public CField tselfpay;  //个人承担金额
	@CFieldinfo(fieldname="tcompay",caption="公司承担金额",datetype=Types.DECIMAL)
	public CField tcompay;  //公司承担金额
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,caption="表单状态",datetype=Types.INTEGER)
	public CField stat;  //表单状态
	@CFieldinfo(fieldname="idpath",notnull=true,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",notnull=true,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",notnull=true,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname="ins_type",caption="险种类型",datetype=Types.INTEGER)
	public CField ins_type;  //险种类型
	@CFieldinfo(fieldname="insit_id",caption="险种ID",datetype=Types.INTEGER)
	public CField insit_id;  //险种ID
	@CFieldinfo(fieldname="insit_code",caption="险种编码",datetype=Types.VARCHAR)
	public CField insit_code;  //险种编码
	@CFieldinfo(fieldname="insname",caption="险种名称",datetype=Types.VARCHAR)
	public CField insname;  //险种名称
	@CFieldinfo(fieldname="insurancebase",caption="缴费基数",datetype=Types.DECIMAL)
	public CField insurancebase;  //缴费基数
	@CFieldinfo(fieldname="selfratio",caption="个人缴费系数%",datetype=Types.DECIMAL)
	public CField selfratio;  //个人缴费系数%
	@CFieldinfo(fieldname="selfpay",caption="个人缴费金额",datetype=Types.DECIMAL)
	public CField selfpay;  //个人缴费金额
	@CFieldinfo(fieldname="comratio",caption="公司缴费系数%",datetype=Types.DECIMAL)
	public CField comratio;  //公司缴费系数%
	@CFieldinfo(fieldname="compay",caption="公司缴费金额",datetype=Types.DECIMAL)
	public CField compay;  //公司缴费金额
	@CFieldinfo(fieldname="insurance_number",caption="参保号",datetype=Types.VARCHAR)
	public CField insurance_number;  //参保号
	@CFieldinfo(fieldname="er_id",caption="档案ID",datetype=Types.INTEGER)
	public CField er_id;  //档案ID
	@CFieldinfo(fieldname="employee_code",caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",caption="员工姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //员工姓名
	@CFieldinfo(fieldname="orgid",notnull=true,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="ospid",caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",caption="职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //职位名称
	@CFieldinfo(fieldname="lv_id",caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="degree",caption="学历",datetype=Types.INTEGER)
	public CField degree;  //学历
	@CFieldinfo(fieldname="sex",caption="性别",datetype=Types.INTEGER)
	public CField sex;  //性别
	@CFieldinfo(fieldname="hiredday",caption="入职日期",datetype=Types.TIMESTAMP)
	public CField hiredday;  //入职日期
	@CFieldinfo(fieldname="telphone",caption="联系电话",datetype=Types.VARCHAR)
	public CField telphone;  //联系电话
	@CFieldinfo(fieldname="nativeplace",caption="籍贯",datetype=Types.VARCHAR)
	public CField nativeplace;  //籍贯
	@CFieldinfo(fieldname="registertype",caption="户籍类型",datetype=Types.INTEGER)
	public CField registertype;  //户籍类型
	@CFieldinfo(fieldname="id_number",caption="身份证号",datetype=Types.VARCHAR)
	public CField id_number;  //身份证号
	@CFieldinfo(fieldname="sign_org",caption="发证机关",datetype=Types.VARCHAR)
	public CField sign_org;  //发证机关
	@CFieldinfo(fieldname="sign_date",caption="签发日期",datetype=Types.TIMESTAMP)
	public CField sign_date;  //签发日期
	@CFieldinfo(fieldname="expired_date",caption="到期日期",datetype=Types.TIMESTAMP)
	public CField expired_date;  //到期日期
	@CFieldinfo(fieldname="birthday",notnull=true,caption="出生日期",datetype=Types.TIMESTAMP)
	public CField birthday;  //出生日期
	@CFieldinfo(fieldname="isnew",caption="是否新增",datetype=Types.INTEGER)
	public CField isnew;  //是否新增
	@CFieldinfo(fieldname="pay_type",caption="记薪方式",datetype=Types.VARCHAR)
	public CField pay_type;  //记薪方式
	@CFieldinfo(fieldname="torgid",caption="输送机构ID",datetype=Types.INTEGER)
	public CField torgid;  //输送机构ID
	@CFieldinfo(fieldname="torgcode",caption="输送机构编码",datetype=Types.VARCHAR)
	public CField torgcode;  //输送机构编码
	@CFieldinfo(fieldname="torgname",caption="输送机构名称",datetype=Types.VARCHAR)
	public CField torgname;  //输送机构名称
	@CFieldinfo(fieldname="sorgid",caption="派遣机构ID",datetype=Types.INTEGER)
	public CField sorgid;  //派遣机构ID
	@CFieldinfo(fieldname="sorgcode",caption="派遣机构编码",datetype=Types.VARCHAR)
	public CField sorgcode;  //派遣机构编码
	@CFieldinfo(fieldname="sorgname",caption="派遣机构名称",datetype=Types.VARCHAR)
	public CField sorgname;  //派遣机构名称
	@CFieldinfo(fieldname="sptype",caption="职位性质",datetype=Types.VARCHAR)
	public CField sptype;  //职位性质
	@CFieldinfo(fieldname="reg_type",caption="参保性质",datetype=Types.INTEGER)
	public CField reg_type;  //参保性质
	@CFieldinfo(fieldname="age",caption="年龄",datetype=Types.VARCHAR)
	public CField age;  //年龄
	@CFieldinfo(fieldname="insbuy_id",caption="保险购买ID",datetype=Types.INTEGER)
	public CField insbuy_id;  //保险购买ID
	@CFieldinfo(fieldname="insbuyl_id",caption="insbuyl_id",datetype=Types.INTEGER)
	public CField insbuyl_id;  //insbuyl_id
	@CFieldinfo(fieldname="registeraddress",precision=256,scale=0,caption="户籍住址",datetype=Types.VARCHAR)
	public CField registeraddress;  //户籍住址
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_ins_buyinsurance() throws Exception {
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

