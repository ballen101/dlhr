package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.perm.ctr.CtrHr_emploanbatch;

import java.sql.Types;

@CEntity(controller = CtrHr_emploanbatch.class)
public class Hr_emploanbatch extends CJPA {
	@CFieldinfo(fieldname = "loanid", iskey = true, notnull = true, caption = "借调单ID", datetype = Types.INTEGER)
	public CField loanid; // 借调单ID
	@CFieldinfo(fieldname = "loancode", codeid = 59, notnull = true, caption = "借调编码", datetype = Types.VARCHAR)
	public CField loancode; // 借调编码
	@CFieldinfo(fieldname = "applaydate", notnull = true, caption = "申请时间", datetype = Types.TIMESTAMP)
	public CField applaydate; // 申请时间
	@CFieldinfo(fieldname = "loandate", notnull = true, caption = "开始时间", datetype = Types.TIMESTAMP)
	public CField loandate; // 开始时间
	@CFieldinfo(fieldname = "returndate", notnull = true, caption = "归还时间", datetype = Types.TIMESTAMP)
	public CField returndate; // 归还时间
	@CFieldinfo(fieldname = "odorgid", caption = "借调前部门ID", datetype = Types.INTEGER)
	public CField odorgid; // 借调前部门ID
	@CFieldinfo(fieldname = "odorgcode", caption = "借调前部门编码", datetype = Types.VARCHAR)
	public CField odorgcode; // 借调前部门编码
	@CFieldinfo(fieldname = "odorgname", caption = "借调前部门名称", datetype = Types.VARCHAR)
	public CField odorgname; // 借调前部门名称
	@CFieldinfo(fieldname = "neworgid", caption = "借调后部门ID", datetype = Types.INTEGER)
	public CField neworgid; // 借调后部门ID
	@CFieldinfo(fieldname = "neworgcode", caption = "借调后部门编码", datetype = Types.VARCHAR)
	public CField neworgcode; // 借调后部门编码
	@CFieldinfo(fieldname = "neworgname", caption = "借调后部门名称", datetype = Types.VARCHAR)
	public CField neworgname; // 借调后部门名称
	@CFieldinfo(fieldname = "loanreason", caption = "借调原因", datetype = Types.VARCHAR)
	public CField loanreason; // 借调原因
	@CFieldinfo(fieldname = "loantype", caption = "调动范围", datetype = Types.INTEGER)
	public CField loantype; // 调动范围 1内部调用 2 跨单位 3 跨模块/制造群
	@CFieldinfo(fieldname = "empnum", caption = "人数", datetype = Types.INTEGER)
	public CField empnum; // 人数
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, caption = "流程状态", datetype = Types.INTEGER)
	public CField stat; // 流程状态
	@CFieldinfo(fieldname = "idpath", notnull = true, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hr_emploanbatch_line.class, linkFields = { @LinkFieldItem(lfield = "loanid", mfield = "loanid") })
	public CJPALineData<Hr_emploanbatch_line> hr_emploanbatch_lines;

	public Hr_emploanbatch() throws Exception {
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
