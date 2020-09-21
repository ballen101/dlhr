package com.hr.canteen.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.canteen.ctr.CtrHr_canteen_mealsystem;

import java.sql.Types;

@CEntity(caption="餐制",controller =CtrHr_canteen_mealsystem.class)
public class Hr_canteen_mealsystem extends CJPA {
	@CFieldinfo(fieldname="ctms_id",iskey=true,notnull=true,caption="餐制ID",datetype=Types.INTEGER)
	public CField ctms_id;  //餐制ID
	@CFieldinfo(fieldname="ctms_code",codeid=100,notnull=true,caption="餐制编码",datetype=Types.VARCHAR)
	public CField ctms_code;  //餐制编码
	@CFieldinfo(fieldname="ctms_name",notnull=true,caption="餐制名称",datetype=Types.VARCHAR)
	public CField ctms_name;  //餐制名称
	@CFieldinfo(fieldname="price",caption="餐标",datetype=Types.DECIMAL)
	public CField price;  //餐标
	@CFieldinfo(fieldname="subsidies",caption="补贴金额",datetype=Types.DECIMAL)
	public CField subsidies;  //补贴金额
	@CFieldinfo(fieldname="cost",caption="自费金额",datetype=Types.DECIMAL)
	public CField cost;  //自费金额
	@CFieldinfo(fieldname="mc_id",notnull=true,caption="餐类ID",datetype=Types.INTEGER)
	public CField mc_id;  //餐类ID
	@CFieldinfo(fieldname="mc_name",notnull=true,caption="餐类名称",datetype=Types.VARCHAR)
	public CField mc_name;  //餐类名称
	@CFieldinfo(fieldname="mealbegin",caption="用餐开始时间",datetype=Types.VARCHAR)
	public CField mealbegin;  //用餐开始时间
	@CFieldinfo(fieldname="mealend",caption="用餐结束时间",datetype=Types.VARCHAR)
	public CField mealend;  //用餐结束时间
	@CFieldinfo(fieldname="sublimit",caption="每月补贴餐数上限",datetype=Types.INTEGER)
	public CField sublimit;  //每月补贴餐数上限
	@CFieldinfo(fieldname="orgid",notnull=true,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="lv_id",caption="职级ID",datetype=Types.INTEGER)
	public CField lv_id;  //职级ID
	@CFieldinfo(fieldname="lv_num",caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="emplev",caption="人事层级",datetype=Types.INTEGER)
	public CField emplev;  //人事层级
	@CFieldinfo(fieldname="mealstandard",caption="用餐标准：1经理餐；2职员餐；3员工餐",datetype=Types.INTEGER)
	public CField mealstandard;  //用餐标准：1经理餐；2职员餐；3员工餐
	@CFieldinfo(fieldname="costtype",caption="消费类型：1定额；2随机",datetype=Types.INTEGER)
	public CField costtype;  //消费类型：1定额；2随机
	@CFieldinfo(fieldname="remark",caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
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
	@CFieldinfo(fieldname="usable",precision=1,scale=0,caption="是否可用",datetype=Types.INTEGER)
	public CField usable;  //是否可用
	@CFieldinfo(fieldname ="classtype",precision=1,scale=0,caption="餐类类型",datetype=Types.INTEGER)
	public CField classtype;  //餐类类型
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_canteen_mealsystem() throws Exception {
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
