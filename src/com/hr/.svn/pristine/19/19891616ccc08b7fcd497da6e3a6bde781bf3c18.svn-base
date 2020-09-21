package com.hr.salary.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.salary.ctr.CtrHr_salary_specchgsa_batch;

import java.sql.Types;

@CEntity(caption="批量特殊调薪单",tablename="Hr_salary_specchgsa_batch",controller=CtrHr_salary_specchgsa_batch.class)
public class Hr_salary_specchgsa_batch extends CJPA {
	@CFieldinfo(fieldname ="scsbid",iskey=true,notnull=true,precision=20,scale=0,caption="批量特殊调薪ID",datetype=Types.INTEGER)
	public CField scsbid;  //批量特殊调薪ID
	@CFieldinfo(fieldname ="scsbcode",notnull=true,codeid=131,precision=15,scale=0,caption="批量特殊调薪编码",datetype=Types.VARCHAR)
	public CField scsbcode;  //批量特殊调薪编码
	@CFieldinfo(fieldname ="chgtype",notnull=true,precision=2,scale=0,caption="调薪类型",defvalue="5",datetype=Types.INTEGER)
	public CField chgtype;  //调薪类型
	@CFieldinfo(fieldname ="orgid",notnull=true,precision=10,scale=0,caption="部门ID",datetype=Types.INTEGER)
	public CField orgid;  //部门ID
	@CFieldinfo(fieldname ="orgcode",notnull=true,precision=16,scale=0,caption="部门编码",datetype=Types.VARCHAR)
	public CField orgcode;  //部门编码
	@CFieldinfo(fieldname ="orgname",notnull=true,precision=128,scale=0,caption="部门名称",datetype=Types.VARCHAR)
	public CField orgname;  //部门名称
	@CFieldinfo(fieldname ="orghrlev",notnull=true,precision=1,scale=0,caption="机构人事层级",defvalue="0",datetype=Types.INTEGER)
	public CField orghrlev;  //机构人事层级
	@CFieldinfo(fieldname ="chgreason",precision=512,scale=0,caption="申请原因",datetype=Types.VARCHAR)
	public CField chgreason;  //申请原因
	@CFieldinfo(fieldname ="salary_quotacode",precision=16,scale=0,caption="可用工资额度证明编号",datetype=Types.VARCHAR)
	public CField salary_quotacode;  //可用工资额度证明编号
	@CFieldinfo(fieldname ="salary_quota_stand",precision=10,scale=2,caption="标准工资额度",defvalue="0.00",datetype=Types.DECIMAL)
	public CField salary_quota_stand;  //标准工资额度
	@CFieldinfo(fieldname ="salary_quota_canuse",precision=10,scale=2,caption="可用工资额度",defvalue="0.00",datetype=Types.DECIMAL)
	public CField salary_quota_canuse;  //可用工资额度
	@CFieldinfo(fieldname ="salary_quota_used",precision=10,scale=2,caption="己用工资额度",defvalue="0.00",datetype=Types.DECIMAL)
	public CField salary_quota_used;  //己用工资额度
	@CFieldinfo(fieldname ="salary_quota_blance",precision=10,scale=2,caption="可用工资余额",defvalue="0.00",datetype=Types.DECIMAL)
	public CField salary_quota_blance;  //可用工资余额
	@CFieldinfo(fieldname ="salary_quota_appy",precision=10,scale=2,caption="申请工资额度",defvalue="0.00",datetype=Types.DECIMAL)
	public CField salary_quota_appy;  //申请工资额度
	@CFieldinfo(fieldname ="salary_quota_ovsp",precision=32,scale=0,caption="超额度审批",datetype=Types.VARCHAR)
	public CField salary_quota_ovsp;  //超额度审批
	@CFieldinfo(fieldname ="salarydate",precision=10,scale=0,caption="核薪生效日期",datetype=Types.DATE)
	public CField salarydate;  //核薪生效日期
	@CFieldinfo(fieldname ="remark",precision=512,scale=0,caption="备注",datetype=Types.VARCHAR)
	public CField remark;  //备注
	@CFieldinfo(fieldname ="wfid",precision=20,scale=0,caption="wfid",datetype=Types.INTEGER)
	public CField wfid;  //wfid
	@CFieldinfo(fieldname ="attid",precision=20,scale=0,caption="attid",datetype=Types.INTEGER)
	public CField attid;  //attid
	@CFieldinfo(fieldname ="stat",notnull=true,precision=2,scale=0,caption="表单状态",datetype=Types.INTEGER)
	public CField stat;  //表单状态
	@CFieldinfo(fieldname ="idpath",notnull=true,precision=256,scale=0,caption="idpath",datetype=Types.VARCHAR)
	public CField idpath;  //idpath
	@CFieldinfo(fieldname ="entid",notnull=true,precision=5,scale=0,caption="entid",datetype=Types.INTEGER)
	public CField entid;  //entid
	@CFieldinfo(fieldname ="creator",notnull=true,precision=32,scale=0,caption="制单人",datetype=Types.VARCHAR)
	public CField creator;  //制单人
	@CFieldinfo(fieldname ="createtime",notnull=true,precision=19,scale=0,caption="制单时间",datetype=Types.TIMESTAMP)
	public CField createtime;  //制单时间
	@CFieldinfo(fieldname ="updator",precision=32,scale=0,caption="更新人",datetype=Types.VARCHAR)
	public CField updator;  //更新人
	@CFieldinfo(fieldname ="updatetime",precision=19,scale=0,caption="更新时间",datetype=Types.TIMESTAMP)
	public CField updatetime;  //更新时间
	@CFieldinfo(fieldname ="attribute1",precision=32,scale=0,caption="备用字段1",datetype=Types.VARCHAR)
	public CField attribute1;  //备用字段1
	@CFieldinfo(fieldname ="attribute2",precision=32,scale=0,caption="备用字段2",datetype=Types.VARCHAR)
	public CField attribute2;  //备用字段2
	@CFieldinfo(fieldname ="attribute3",precision=32,scale=0,caption="备用字段3",datetype=Types.VARCHAR)
	public CField attribute3;  //备用字段3
	@CFieldinfo(fieldname ="attribute4",precision=32,scale=0,caption="备用字段4",datetype=Types.VARCHAR)
	public CField attribute4;  //备用字段4
	@CFieldinfo(fieldname ="attribute5",precision=32,scale=0,caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname = "emplev",  caption = "人事层级", datetype = Types.INTEGER)
	public CField emplev; // 人事层级
	public String SqlWhere; //查询附加条件
	public int MaxCount; //查询最大数量

     //自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_salary_specchgsa_batch_line.class, linkFields = { @LinkFieldItem(lfield = "scsbid", mfield = "scsbid") })
	public CJPALineData<Hr_salary_specchgsa_batch_line> hr_salary_specchgsa_batch_lines;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_salary_specchgsa_batch() throws Exception {
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

