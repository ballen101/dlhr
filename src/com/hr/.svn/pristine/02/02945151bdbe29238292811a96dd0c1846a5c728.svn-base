package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="工资水平统计设置",tablename="Hr_salary_parms")
public class Hr_salary_parms extends CJPA {
	@CFieldinfo(fieldname ="sap_id",iskey=true,notnull=true,precision=20,scale=0,caption="工资水平统计设置ID",datetype=Types.INTEGER)
	public CField sap_id;  //工资水平统计设置ID
	@CFieldinfo(fieldname ="sap_code",notnull=true,codeid=133,precision=16,scale=0,caption="工资水平统计设置编码",datetype=Types.VARCHAR)
	public CField sap_code;  //工资水平统计设置编码
	@CFieldinfo(fieldname ="yearmonth",precision=7,scale=0,caption="年月",datetype=Types.VARCHAR)
	public CField yearmonth;  //年月
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="salary_avg",precision=10,scale=2,caption="平均工资",datetype=Types.DECIMAL)
	public CField salary_avg;  //平均工资
	@CFieldinfo(fieldname ="salary_avghour",precision=10,scale=2,caption="平均小时工资",datetype=Types.DECIMAL)
	public CField salary_avghour;  //平均小时工资
	@CFieldinfo(fieldname ="salary_max",precision=10,scale=2,caption="最高工资",datetype=Types.DECIMAL)
	public CField salary_max;  //最高工资
	@CFieldinfo(fieldname ="salary_min",precision=10,scale=2,caption="最低工资",datetype=Types.DECIMAL)
	public CField salary_min;  //最低工资
	@CFieldinfo(fieldname ="per10",precision=10,scale=2,caption="10分位值",datetype=Types.DECIMAL)
	public CField per10;  //10分位值
	@CFieldinfo(fieldname ="per25",precision=10,scale=2,caption="25分位值",datetype=Types.DECIMAL)
	public CField per25;  //25分位值
	@CFieldinfo(fieldname ="per50",precision=10,scale=2,caption="50分位值",datetype=Types.DECIMAL)
	public CField per50;  //50分位值
	@CFieldinfo(fieldname ="per75",precision=10,scale=2,caption="75分位值",datetype=Types.DECIMAL)
	public CField per75;  //75分位值
	@CFieldinfo(fieldname ="per90",precision=10,scale=2,caption="90分位值",datetype=Types.DECIMAL)
	public CField per90;  //90分位值
	@CFieldinfo(fieldname ="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname ="stat",notnull=true,precision=2,scale=0,caption="流程状态",datetype=Types.INTEGER)
	public CField stat;  //流程状态
	@CFieldinfo(fieldname ="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname ="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname ="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname ="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname ="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname ="property1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField property1;  //备用字段1
	@CFieldinfo(fieldname ="property2",precision=64,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField property2;  //备用字段2
	@CFieldinfo(fieldname ="property3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField property3;  //备用字段3
	@CFieldinfo(fieldname ="property4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField property4;  //备用字段4
	@CFieldinfo(fieldname ="property5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField property5;  //备用字段5
	@CFieldinfo(fieldname ="usable",notnull=true,precision=1,scale=0,caption="有效",defvalue="1",datetype=Types.INTEGER)
	public CField usable;  //有效
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hr_salary_parms() throws Exception {
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
