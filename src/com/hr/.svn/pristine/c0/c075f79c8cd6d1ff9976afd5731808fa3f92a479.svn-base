package com.hr.canteen.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_canteen_guest_line extends CJPA {
	@CFieldinfo(fieldname="ctgl_id",iskey=true,notnull=true,caption="客餐申请明细ID",datetype=Types.INTEGER)
	public CField ctgl_id;  //客餐申请明细ID
	@CFieldinfo(fieldname="ctg_id",notnull=true,caption="客餐申请ID",datetype=Types.INTEGER)
	public CField ctg_id;  //客餐申请ID
	@CFieldinfo(fieldname="num",caption="用餐人数",datetype=Types.INTEGER)
	public CField num;  //用餐人数
	@CFieldinfo(fieldname="guestname",caption="姓名",datetype=Types.VARCHAR)
	public CField guestname;  //姓名
	@CFieldinfo(fieldname="sex",caption="性别",datetype=Types.INTEGER)
	public CField sex;  //性别
	@CFieldinfo(fieldname="guestorg",caption="单位",datetype=Types.VARCHAR)
	public CField guestorg;  //单位
	@CFieldinfo(fieldname="classtype",caption="餐类",datetype=Types.INTEGER)
	public CField classtype;  //餐类
	@CFieldinfo(fieldname="mealstand",caption="餐标",datetype=Types.DECIMAL)
	public CField mealstand;  //餐标
	@CFieldinfo(fieldname="mealbg_date",caption="用餐开始时间",datetype=Types.VARCHAR)
	public CField mealbg_date;  //用餐开始时间
	@CFieldinfo(fieldname="mealed_date",caption="用餐截止时间",datetype=Types.VARCHAR)
	public CField mealed_date;  //用餐截止时间
	@CFieldinfo(fieldname="ctmc_id",caption="餐标ID",datetype=Types.INTEGER)
	public CField ctmc_id;  //餐标ID
	@CFieldinfo(fieldname="mc_id",caption="餐类ID",datetype=Types.INTEGER)
	public CField mc_id;  //餐类ID
	@CFieldinfo(fieldname="mc_name",caption="餐类名称",datetype=Types.VARCHAR)
	public CField mc_name;  //餐类名称
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_canteen_guest_line() throws Exception {
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
