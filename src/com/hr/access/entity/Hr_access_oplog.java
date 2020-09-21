package com.hr.access.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import com.hr.access.co.COHr_access_card;

import java.sql.Types;

@CEntity()
public class Hr_access_oplog extends CJPA {
	@CFieldinfo(fieldname="access_oplog_id",iskey=true,notnull=true,caption="门禁开门记录ID",datetype=Types.INTEGER)
	public CField access_oplog_id;  //门禁开门记录ID
	@CFieldinfo(fieldname="id",notnull=true,caption="门禁序号",datetype=Types.INTEGER)
	public CField id;  //门禁序号
	@CFieldinfo(fieldname="opTime",caption="操作时间",datetype=Types.VARCHAR)
	public CField opTime;  //操作时间
	@CFieldinfo(fieldname="BeWrite",caption="动作",datetype=Types.VARCHAR)
	public CField BeWrite;  //动作
	@CFieldinfo(fieldname="opUser",caption="操作用户",datetype=Types.VARCHAR)
	public CField opUser;  //操作用户
	@CFieldinfo(fieldname="opComPuter",caption="操作电脑",datetype=Types.VARCHAR)
	public CField opComPuter;  //操作电脑
	@CFieldinfo(fieldname="remarks",caption="备注",datetype=Types.VARCHAR)
	public CField remarks;  //备注
	@CFieldinfo(fieldname="wfid",caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",caption="表单状态",datetype=Types.INTEGER)
	public CField stat;  //表单状态
	@CFieldinfo(fieldname="idpath",caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",caption="attribute1",datetype=Types.VARCHAR)
	public CField attribute1;  //attribute1
	@CFieldinfo(fieldname="attribute2",caption="attribute2",datetype=Types.TIMESTAMP)
	public CField attribute2;  //attribute2
	@CFieldinfo(fieldname="attribute3",caption="attribute3",datetype=Types.VARCHAR)
	public CField attribute3;  //attribute3
	@CFieldinfo(fieldname="attribute4",caption="attribute4",datetype=Types.VARCHAR)
	public CField attribute4;  //attribute4
	@CFieldinfo(fieldname="attribute5",caption="attribute5",datetype=Types.VARCHAR)
	public CField attribute5;  //attribute5
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_access_oplog() throws Exception {
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

