package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hr_credentials extends CJPA {
	@CFieldinfo(fieldname="cert_id",iskey=true,notnull=true,caption="证件ID",datetype=Types.INTEGER)
	public CField cert_id;  //证件ID
	@CFieldinfo(fieldname="er_id",notnull=true,caption="员工档案ID",datetype=Types.INTEGER)
	public CField er_id;  //员工档案ID
	@CFieldinfo(fieldname="er_code",notnull=true,caption="员工档案编码",datetype=Types.VARCHAR)
	public CField er_code;  //员工档案编码
	@CFieldinfo(fieldname="employee_name",notnull=true,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname="picture_id",caption="图片ID",datetype=Types.INTEGER)
	public CField picture_id;  //图片ID
	@CFieldinfo(fieldname="cert_type",caption="证件类型",datetype=Types.INTEGER)
	public CField cert_type;  //证件类型
	@CFieldinfo(fieldname="cert_name",notnull=true,caption="证件名称",datetype=Types.VARCHAR)
	public CField cert_name;  //证件名称
	@CFieldinfo(fieldname="sign_date",caption="签发日期",datetype=Types.TIMESTAMP)
	public CField sign_date;  //签发日期
	@CFieldinfo(fieldname="sign_org",notnull=true,caption="签发单位",datetype=Types.VARCHAR)
	public CField sign_org;  //签发单位
	@CFieldinfo(fieldname="expired_date",caption="到期日期",datetype=Types.TIMESTAMP)
	public CField expired_date;  //到期日期
	@CFieldinfo(fieldname="is_remind",caption="是否预警",datetype=Types.INTEGER)
	public CField is_remind;  //是否预警
	@CFieldinfo(fieldname="creator",caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",caption="创建时间",datetype=Types.TIMESTAMP)
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
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_credentials() throws Exception {
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
