package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_standposition_orgtp extends CJPA {
	@CFieldinfo(fieldname="spot_id",iskey=true,notnull=true,caption="ID",datetype=Types.INTEGER)
	public CField spot_id;  //ID
	@CFieldinfo(fieldname="orgtype",notnull=true,caption="机构类型 按词汇表",datetype=Types.INTEGER)
	public CField orgtype;  //机构类型 按词汇表
	@CFieldinfo(fieldname="sp_id",notnull=true,caption="职位ID",datetype=Types.INTEGER)
	public CField sp_id;  //职位ID
	@CFieldinfo(fieldname="sp_code",notnull=true,caption="职位编码",datetype=Types.VARCHAR)
	public CField sp_code;  //职位编码
	@CFieldinfo(fieldname="sp_name",notnull=true,caption="职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //职位名称
	@CFieldinfo(fieldname="usable",notnull=true,caption="可用",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //可用
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


	public Hr_standposition_orgtp() throws Exception {
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

