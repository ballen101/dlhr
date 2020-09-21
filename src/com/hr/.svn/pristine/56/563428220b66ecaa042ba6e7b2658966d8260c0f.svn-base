package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.attd.ctr.CtrHrkq_lbe_batch;

import java.sql.Types;

@CEntity(controller =CtrHrkq_lbe_batch.class)
public class Hrkq_lbe_batch extends CJPA {
	@CFieldinfo(fieldname="lbebid",iskey=true,notnull=true,precision=10,scale=0,caption="ID",datetype=Types.INTEGER)
	public CField lbebid;  //ID
	@CFieldinfo(fieldname="lbebcode",precision=20,scale=0,codeid=119,caption="批量展期编码",datetype=Types.VARCHAR)
	public CField lbebcode;  //批量展期编码
	@CFieldinfo(fieldname="newvaldate",precision=19,scale=0,caption="展期有效期",datetype=Types.TIMESTAMP)
	public CField newvaldate;  //展期有效期
	@CFieldinfo(fieldname="ext_reason",precision=512,scale=0,caption="展期原因",datetype=Types.VARCHAR)
	public CField ext_reason;  //展期原因
	@CFieldinfo(fieldname="ext_nums",precision=10,scale=0,caption="展期人数",datetype=Types.INTEGER)
	public CField ext_nums;  //展期人数
	@CFieldinfo(fieldname="lbid",precision=10,scale=0,caption="调休流水",datetype=Types.INTEGER)
	public CField lbid;  //调休流水
	@CFieldinfo(fieldname="lbname",precision=64,scale=0,caption="标题",datetype=Types.VARCHAR)
	public CField lbname;  //标题
	@CFieldinfo(fieldname="stype",precision=2,scale=0,caption="源类型 1 年假 2 加班 3 值班 4出差 5特殊",datetype=Types.INTEGER)
	public CField stype;  //源类型 1 年假 2 加班 3 值班 4出差 5特殊
	@CFieldinfo(fieldname="sid",precision=10,scale=0,caption="源ID",datetype=Types.INTEGER)
	public CField sid;  //源ID
	@CFieldinfo(fieldname="sccode",precision=20,scale=0,caption="源编码/年假的年份",datetype=Types.VARCHAR)
	public CField sccode;  //源编码/年假的年份
	@CFieldinfo(fieldname="orgid",precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname="orgcode",precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname="orgname",precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname="valdate",precision=19,scale=0,caption="原有效期",datetype=Types.TIMESTAMP)
	public CField valdate;  //原有效期
	@CFieldinfo(fieldname="emplev",precision=2,scale=0,caption="人事层级",datetype=Types.INTEGER)
	public CField emplev;  //人事层级
	@CFieldinfo(fieldname="remark",precision=128,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname="wfid",precision=20,scale=0,caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname="attid",precision=20,scale=0,caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname="stat",notnull=true,precision=2,scale=0,caption="表单状态",datetype=Types.INTEGER)
	public CField stat;  //表单状态
	@CFieldinfo(fieldname="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname="entid",notnull=true,precision=5,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname="creator",notnull=true,precision=32,scale=0,caption="创建人",datetype=Types.VARCHAR)
	public CField creator;  //创建人
	@CFieldinfo(fieldname="createtime",notnull=true,precision=19,scale=0,caption="创建时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //创建时间
	@CFieldinfo(fieldname="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname="attribute1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname="attribute2",precision=32,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname="attribute3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname="attribute4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname="attribute5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname = "orghrlev", caption = "机构人事层级", datetype = Types.INTEGER)
	public CField orghrlev; // 机构人事层级
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hrkq_lbe_batch_line.class, linkFields = { @LinkFieldItem(lfield = "lbebid", mfield = "lbebid") })
	public CJPALineData<Hrkq_lbe_batch_line> hrkq_lbe_batch_lines;


	public Hrkq_lbe_batch() throws Exception {
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

