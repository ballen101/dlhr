package com.hr.inface.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity()
public class Hrms_txjocatlist extends CJPA {
	@CFieldinfo(fieldname="txxf_id",iskey=true,notnull=true,caption="ID",datetype=Types.INTEGER)
	public CField txxf_id;  //ID
	@CFieldinfo(fieldname="rfkh",notnull=true,caption="卡号",datetype=Types.VARCHAR)
	public CField rfkh;  //卡号
	@CFieldinfo(fieldname="xfjh",notnull=true,caption="消费机号",datetype=Types.VARCHAR)
	public CField xfjh;  //消费机号
	@CFieldinfo(fieldname="xfsj",caption="消费时间",datetype=Types.TIMESTAMP)
	public CField xfsj;  //消费时间
	@CFieldinfo(fieldname="xfid",caption="关联原记录id",datetype=Types.VARCHAR)
	public CField xfid;  //关联原记录id
	@CFieldinfo(fieldname="createTime",notnull=true,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createTime;  //创建时间
	@CFieldinfo(fieldname="code",notnull=true,caption="工号",datetype=Types.VARCHAR)
	public CField code;  //工号
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hrms_txjocatlist() throws Exception {
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
