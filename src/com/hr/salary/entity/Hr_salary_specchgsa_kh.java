package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="入职转正表单考核")
public class Hr_salary_specchgsa_kh extends CJPA {
	@CFieldinfo(fieldname="pbtkhid",iskey=true,notnull=true,precision=20,scale=0,caption="考核ID",datetype=Types.INTEGER)
	public CField pbtkhid;  //考核ID
	@CFieldinfo(fieldname="scsid",notnull=true,precision=20,scale=0,caption="特殊调薪ID",datetype=Types.INTEGER)
	public CField scsid;  //特殊调薪ID
	@CFieldinfo(fieldname="khitem",notnull=true,precision=64,scale=0,caption="考核项目",datetype=Types.VARCHAR)
	public CField khitem;  //考核项目
	@CFieldinfo(fieldname="khnote",notnull=true,precision=64,scale=0,caption="考核说明",datetype=Types.VARCHAR)
	public CField khnote;  //考核说明
	@CFieldinfo(fieldname="khjudge",precision=64,scale=0,caption="考核评价",datetype=Types.VARCHAR)
	public CField khjudge;  //考核评价
	@CFieldinfo(fieldname="khvalue",precision=2,scale=0,dicid=749,caption="考核结果  优   □良   □合格   □不合格",datetype=Types.INTEGER)
	public CField khvalue;  //考核结果  优   □良   □合格   □不合格
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_specchgsa_kh() throws Exception {
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

