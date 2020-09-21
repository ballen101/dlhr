package com.hr.canteen.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.canteen.ctr.CtrHr_canteen_special;

import java.sql.Types;

@CEntity(caption="特殊用餐申请",controller =CtrHr_canteen_special.class)
public class Hr_canteen_special extends CJPA {
	@CFieldinfo(fieldname="ctsp_id",iskey=true,notnull=true,caption="特殊用餐申请ID",datetype=Types.INTEGER)
	public CField ctsp_id;  //特殊用餐申请ID
	@CFieldinfo(fieldname="ctsp_code",codeid=101,notnull=true,caption="特殊用餐申请编码",datetype=Types.VARCHAR)
	public CField ctsp_code;  //特殊用餐申请编码
	@CFieldinfo(fieldname="er_id",caption="申请人档案ID",datetype=Types.INTEGER)
	public CField er_id;  //申请人档案ID
	@CFieldinfo(fieldname="employee_code",caption="申请人工号",datetype=Types.VARCHAR)
	public CField employee_code;  //申请人工号
	@CFieldinfo(fieldname="employee_name",caption="申请人姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //申请人姓名
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
	@CFieldinfo(fieldname="apply_date",caption="申请日期",datetype=Types.TIMESTAMP)
	public CField apply_date;  //申请日期
	@CFieldinfo(fieldname="sex",caption="性别",datetype=Types.INTEGER)
	public CField sex;  //性别
	@CFieldinfo(fieldname="hiredday",caption="入职日期",datetype=Types.TIMESTAMP)
	public CField hiredday;  //入职日期
	@CFieldinfo(fieldname="telphone",caption="联系电话",datetype=Types.VARCHAR)
	public CField telphone;  //联系电话
	@CFieldinfo(fieldname="applyreason",caption="申请原因",datetype=Types.VARCHAR)
	public CField applyreason;  //申请原因
	@CFieldinfo(fieldname="standard_type",caption="餐标类型。1经理级",datetype=Types.VARCHAR)
	public CField standard_type;  //餐标类型。1科长级；2经理级
	@CFieldinfo(fieldname="class_type",caption="申请餐类类型",datetype=Types.VARCHAR)
	public CField class_type;  //申请餐类类型
	@CFieldinfo(fieldname="ctr_id",caption="餐厅ID",datetype=Types.INTEGER)
	public CField ctr_id;  //餐厅ID
	@CFieldinfo(fieldname="ctr_code",caption="餐厅编码",datetype=Types.VARCHAR)
	public CField ctr_code;  //餐厅编码
	@CFieldinfo(fieldname="ctr_name",caption="餐厅名称",datetype=Types.VARCHAR)
	public CField ctr_name;  //餐厅名称
	@CFieldinfo(fieldname="applytimetype",caption="申请期限类型。1长期；2短期",datetype=Types.INTEGER)
	public CField applytimetype;  //申请期限类型。1长期；2短期
	@CFieldinfo(fieldname="appbg_date",caption="申请期限开始时间",datetype=Types.TIMESTAMP)
	public CField appbg_date;  //申请期限开始时间
	@CFieldinfo(fieldname="apped_date",caption="申请期限结束时间",datetype=Types.TIMESTAMP)
	public CField apped_date;  //申请期限结束时间
	@CFieldinfo(fieldname="subsidiestype",caption="补贴类型；1原职级标准；2经理级标准",datetype=Types.INTEGER)
	public CField subsidiestype;  //补贴类型；1原职级标准；2经理级标准
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
	@CFieldinfo(fieldname="emplev",caption="人事层级",datetype=Types.INTEGER)
	public CField emplev;  //人事层级
	@CFieldinfo(fieldname="orghrlev",caption="机构人事层级",datetype=Types.INTEGER)
	public CField orghrlev;  //机构人事层级
	@CFieldinfo(fieldname="usable",precision=1,scale=0,caption="是否可用",datetype=Types.INTEGER)
	public CField usable;  //是否可用
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_canteen_special() throws Exception {
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

