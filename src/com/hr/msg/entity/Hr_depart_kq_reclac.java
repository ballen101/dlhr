package com.hr.msg.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;
@CEntity()
public class Hr_depart_kq_reclac extends CJPA{
//	部门id
	@CFieldinfo(fieldname = "id", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField id;
//	部门名称
	@CFieldinfo(fieldname = "org_name", caption = "部门名称", datetype = Types.VARCHAR)
	public CField org_name;
//	部门编号
	@CFieldinfo(fieldname="org_code",caption="部门编号",datetype=Types.VARCHAR)
	public CField org_code;
//	ipath
	@CFieldinfo(fieldname="idpath",caption="idpath",notnull = true,datetype = Types.VARCHAR)
	public CField idpath;
//	状态
	@CFieldinfo(fieldname="status",caption="状态",datetype=Types.INTEGER)
	public CField status;
//	更改时间
	@CFieldinfo(fieldname="reclac_date",caption="更改时间",datetype=Types.TIMESTAMP)
	public CField reclac_date;
	public Hr_depart_kq_reclac() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean InitObject() {// 类初始化调用的方法
		super.InitObject();
		return true;
	}

	@Override
	public boolean FinalObject() { // 类释放前调用的方法
		super.FinalObject();
		return true;
	}
	
}
