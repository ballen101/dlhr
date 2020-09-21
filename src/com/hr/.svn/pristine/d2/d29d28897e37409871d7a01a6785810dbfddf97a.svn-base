package com.hr.canteen.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.canteen.ctr.CtrHr_canteen_guest;

import java.sql.Types;

@CEntity(caption="客餐申请",controller =CtrHr_canteen_guest.class)
public class Hr_canteen_guest extends CJPA {
	@CFieldinfo(fieldname="ctg_id",iskey=true,notnull=true,caption="客餐申请ID",datetype=Types.INTEGER)
	public CField ctg_id;  //客餐申请ID
	@CFieldinfo(fieldname="ctg_code",codeid=102,notnull=true,caption="客餐申请编码",datetype=Types.VARCHAR)
	public CField ctg_code;  //客餐申请编码
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
	@CFieldinfo(fieldname="iseat",caption="是否用餐",datetype=Types.INTEGER)
	public CField iseat;  //是否用餐
	@CFieldinfo(fieldname="reasons",caption="未用餐原因说明",datetype=Types.VARCHAR)
	public CField reasons;  //未用餐原因说明
	@CFieldinfo(fieldname="guesttype",caption="客户类型",datetype=Types.VARCHAR)
	public CField guesttype;  //客户类型
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
	@CFieldinfo(fieldname="ctr_id",caption="餐厅ID",datetype=Types.INTEGER)
	public CField ctr_id;  //餐厅ID
	@CFieldinfo(fieldname="ctr_code",caption="餐厅编码",datetype=Types.VARCHAR)
	public CField ctr_code;  //餐厅编码
	@CFieldinfo(fieldname="ctr_name",caption="餐厅名称",datetype=Types.VARCHAR)
	public CField ctr_name;  //餐厅名称
	@CFieldinfo(fieldname="beginmeal",caption="用餐时段开始",datetype=Types.VARCHAR)
	public CField beginmeal;  //用餐开始时间
	@CFieldinfo(fieldname="endmeal",caption="用餐时段结束",datetype=Types.VARCHAR)
	public CField endmeal;  //用餐结束时间
	@CFieldinfo(fieldname="bgday",caption="开始日期",datetype=Types.TIMESTAMP)
	public CField bgday;  //开始日期
	@CFieldinfo(fieldname="edday",caption="结束日期",datetype=Types.TIMESTAMP)
	public CField edday;  //结束日期
	@CFieldinfo(fieldname="persontype",precision=2,scale=0,caption="就餐人员",datetype=Types.INTEGER)
	public CField persontype;  //就餐人员
	@CFieldinfo(fieldname="usable",precision=1,scale=0,caption="是否可用",datetype=Types.INTEGER)
	public CField usable;  //是否可用
	@CFieldinfo(fieldname="canceldate",caption="中止日期",datetype=Types.TIMESTAMP)
	public CField canceldate;  //入职日期
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_canteen_guest_line.class, linkFields = { @LinkFieldItem(lfield = "ctg_id", mfield = "ctg_id") })
	public CJPALineData<Hr_canteen_guest_line> hr_canteen_guest_lines;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_canteen_guest() throws Exception {
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
