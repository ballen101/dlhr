package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption="批量展期明细")
public class Hrkq_lbe_batch_line extends CJPA {
	@CFieldinfo(fieldname="lbeblid",iskey=true,notnull=true,precision=10,scale=0,caption="明细行ID",datetype=Types.INTEGER)
	public CField lbeblid;  //明细行ID
	@CFieldinfo(fieldname="lbebid",notnull=true,precision=10,scale=0,caption="批量展期ID",datetype=Types.INTEGER)
	public CField lbebid;  //批量展期ID
	@CFieldinfo(fieldname="newvaldate",notnull=true,precision=19,scale=0,caption="展期有效期",datetype=Types.TIMESTAMP)
	public CField newvaldate;  //展期有效期
	@CFieldinfo(fieldname="ext_reason",precision=512,scale=0,caption="展期原因",datetype=Types.VARCHAR)
	public CField ext_reason;  //展期原因
	@CFieldinfo(fieldname="ext_days",precision=4,scale=1,caption="展期天数",datetype=Types.DECIMAL)
	public CField ext_days;  //展期天数
	@CFieldinfo(fieldname="lbid",precision=10,scale=0,caption="调休流水",datetype=Types.INTEGER)
	public CField lbid;  //调休流水
	@CFieldinfo(fieldname="lbname",precision=64,scale=0,caption="标题",datetype=Types.VARCHAR)
	public CField lbname;  //标题
	@CFieldinfo(fieldname="stype",notnull=true,precision=2,scale=0,caption="源类型 1 年假 2 加班 3 值班 4出差 5特殊",datetype=Types.INTEGER)
	public CField stype;  //源类型 1 年假 2 加班 3 值班 4出差 5特殊
	@CFieldinfo(fieldname="sid",precision=10,scale=0,caption="源ID",datetype=Types.INTEGER)
	public CField sid;  //源ID
	@CFieldinfo(fieldname="sccode",precision=20,scale=0,caption="源编码/年假的年份",datetype=Types.VARCHAR)
	public CField sccode;  //源编码/年假的年份
	@CFieldinfo(fieldname="er_id",notnull=true,precision=20,scale=0,caption="人事ID",datetype=Types.INTEGER)
	public CField er_id;  //人事ID
	@CFieldinfo(fieldname="employee_code",notnull=true,precision=16,scale=0,caption="工号",datetype=Types.VARCHAR)
	public CField employee_code;  //工号
	@CFieldinfo(fieldname="employee_name",precision=64,scale=0,caption="姓名",datetype=Types.VARCHAR)
	public CField employee_name;  //姓名
	@CFieldinfo(fieldname="hg_name",precision=128,scale=0,caption="职等名称",datetype=Types.VARCHAR)
	public CField hg_name;  //职等名称
	@CFieldinfo(fieldname="lv_num",precision=4,scale=1,caption="职级",datetype=Types.DECIMAL)
	public CField lv_num;  //职级
	@CFieldinfo(fieldname="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="ospid",precision=10,scale=0,caption="职位ID",datetype=Types.INTEGER)
	public CField ospid;  //职位ID
	@CFieldinfo(fieldname="ospcode",precision=16,scale=0,caption="职位编码",datetype=Types.VARCHAR)
	public CField ospcode;  //职位编码
	@CFieldinfo(fieldname="sp_name",precision=128,scale=0,caption="职位名称",datetype=Types.VARCHAR)
	public CField sp_name;  //职位名称
	@CFieldinfo(fieldname="alllbtime",notnull=true,precision=4,scale=1,caption="可调休时间小时",datetype=Types.DECIMAL)
	public CField alllbtime;  //可调休时间小时
	@CFieldinfo(fieldname="usedlbtime",notnull=true,precision=4,scale=1,caption="已调休时间小时",datetype=Types.DECIMAL)
	public CField usedlbtime;  //已调休时间小时
	@CFieldinfo(fieldname="valdate",notnull=true,precision=19,scale=0,caption="原有效期",datetype=Types.TIMESTAMP)
	public CField valdate;  //原有效期
	@CFieldinfo(fieldname="emplev",precision=2,scale=0,caption="人事层级",datetype=Types.INTEGER)
	public CField emplev;  //人事层级
	@CFieldinfo(fieldname="remark",precision=128,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义


	public Hrkq_lbe_batch_line() throws Exception {
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

