package com.hr.inface.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(dbpool = "oldtxmssql", tablename = "View_Hrms_TXJocatConfig")
public class View_Hrms_TXJocatConfig extends CJPA {
	@CFieldinfo(fieldname="id",iskey=true,notnull=true,caption="id",datetype=Types.INTEGER)
	public CField id;  //id
	@CFieldinfo(fieldname="jh",notnull=true,caption="消费机编号",datetype=Types.INTEGER)
	public CField jh;  //消费机编号
	@CFieldinfo(fieldname="lx",notnull=true,caption="名称",datetype=Types.VARCHAR)
	public CField lx;  //名称
	@CFieldinfo(fieldname="ip",notnull=true,caption="消费机ip",datetype=Types.VARCHAR)
	public CField ip;  //消费机ip
	@CFieldinfo(fieldname="bz",notnull=true,caption="消费机地址",datetype=Types.VARCHAR)
	public CField bz;  //消费机地址
	@CFieldinfo(fieldname="zt",notnull=true,caption="状态 1可用，0不可用",datetype=Types.INTEGER)
	public CField zt;  //状态 1可用，0不可用
	@CFieldinfo(fieldname="sd1",notnull=true,caption="早餐",datetype=Types.VARCHAR)
	public CField sd1;  //早餐
	@CFieldinfo(fieldname="sd2",notnull=true,caption="早餐",datetype=Types.VARCHAR)
	public CField sd2;  //早餐
	@CFieldinfo(fieldname="sd3",notnull=true,caption="中餐",datetype=Types.VARCHAR)
	public CField sd3;  //中餐
	@CFieldinfo(fieldname="sd4",notnull=true,caption="中餐",datetype=Types.VARCHAR)
	public CField sd4;  //中餐
	@CFieldinfo(fieldname="sd5",notnull=true,caption="晚餐",datetype=Types.VARCHAR)
	public CField sd5;  //晚餐
	@CFieldinfo(fieldname="sd6",notnull=true,caption="晚餐",datetype=Types.VARCHAR)
	public CField sd6;  //晚餐
	@CFieldinfo(fieldname="sd7",notnull=true,caption="夜宵",datetype=Types.VARCHAR)
	public CField sd7;  //夜宵
	@CFieldinfo(fieldname="sd8",notnull=true,caption="夜宵",datetype=Types.VARCHAR)
	public CField sd8;  //夜宵
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public View_Hrms_TXJocatConfig() throws Exception {
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
