package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_emptransferbatch;

import java.sql.Types;

@CEntity(controller = CtrHr_emptransferbatch.class)
public class Hr_emptransferbatch extends CJPA {
	@CFieldinfo(fieldname = "emptranfb_id", iskey = true, notnull = true, caption = "调动单ID", datetype = Types.INTEGER)
	public CField emptranfb_id; // 调动单ID
	@CFieldinfo(fieldname = "emptranfbcode", codeid = 57, notnull = true, caption = "调动编码", datetype = Types.VARCHAR)
	public CField emptranfbcode; // 调动编码
	@CFieldinfo(fieldname = "probation", caption = "考察期", datetype = Types.INTEGER)
	public CField probation; // 考察期
	@CFieldinfo(fieldname = "tranfcmpdate", caption = "调动生效时间", datetype = Types.TIMESTAMP)
	public CField tranfcmpdate; // 调动生效时间
	@CFieldinfo(fieldname = "tranfreason", caption = "调动原因", datetype = Types.VARCHAR)
	public CField tranfreason; // 调动原因
	@CFieldinfo(fieldname = "tranftype3", caption = "调动范围", datetype = Types.INTEGER)
	public CField tranftype3; // 调动范围 1内部调用 2 跨单位 3 跨模块/制造群
	@CFieldinfo(fieldname = "olorgid", caption = "调出单位ID", datetype = Types.INTEGER)
	public CField olorgid; // 调出单位ID
	@CFieldinfo(fieldname = "oldorgcode", caption = "调出单位编码", datetype = Types.VARCHAR)
	public CField oldorgcode; // 调出单位编码
	@CFieldinfo(fieldname = "oldorgname", caption = "调出单位名称", datetype = Types.VARCHAR)
	public CField oldorgname; // 调出单位名称
	@CFieldinfo(fieldname = "neworgid", notnull = true, caption = "调动后部门ID", datetype = Types.INTEGER)
	public CField neworgid; // 调动后部门ID
	@CFieldinfo(fieldname = "neworgcode", notnull = true, caption = "调动后部门编码", datetype = Types.VARCHAR)
	public CField neworgcode; // 调动后部门编码
	@CFieldinfo(fieldname = "neworgname", notnull = true, caption = "调动后部门名称", datetype = Types.VARCHAR)
	public CField neworgname; // 调动后部门名称
	@CFieldinfo(fieldname = "newlv_id", caption = "调动后职级ID", datetype = Types.INTEGER)
	public CField newlv_id; // 调动后职级ID
	@CFieldinfo(fieldname = "newlv_num", caption = "调动后职级", datetype = Types.DECIMAL)
	public CField newlv_num; // 调动后职级
	@CFieldinfo(fieldname = "newhg_id", caption = "调动后职等ID", datetype = Types.INTEGER)
	public CField newhg_id; // 调动后职等ID
	@CFieldinfo(fieldname = "newhg_code", caption = "调动后职等编码", datetype = Types.VARCHAR)
	public CField newhg_code; // 调动后职等编码
	@CFieldinfo(fieldname = "newhg_name", caption = "调动后职等名称", datetype = Types.VARCHAR)
	public CField newhg_name; // 调动后职等名称
	@CFieldinfo(fieldname = "newospid", caption = "调动后职位ID", datetype = Types.INTEGER)
	public CField newospid; // 调动后职位ID
	@CFieldinfo(fieldname = "newospcode", caption = "调动后职位编码", datetype = Types.VARCHAR)
	public CField newospcode; // 调动后职位编码
	@CFieldinfo(fieldname = "newsp_name", caption = "调动后职位名称", datetype = Types.VARCHAR)
	public CField newsp_name; // 调动后职位名称
	@CFieldinfo(fieldname = "newattendtype", caption = "调动后出勤类别", datetype = Types.INTEGER)
	public CField newattendtype; // 调动后出勤类别
	@CFieldinfo(fieldname = "newcalsalarytype", caption = "调动后计薪方式", datetype = Types.VARCHAR)
	public CField newcalsalarytype; // 调动后计薪方式
	@CFieldinfo(fieldname="tranamt",caption="调拨金额",datetype=Types.DECIMAL)
	public CField tranamt;  //调拨金额
	@CFieldinfo(fieldname = "newhwc_namezl", caption = "调动后职类", datetype = Types.VARCHAR)
	public CField newhwc_namezl; // 调动后职类
	@CFieldinfo(fieldname = "newzwxz", caption = "调动后职位性质", datetype = Types.VARCHAR)
	public CField newzwxz; // 调动后职位性质
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "quota_over", caption = "是否超编", datetype = Types.INTEGER)
	public CField quota_over; // 是否超编
	@CFieldinfo(fieldname = "quota_over_rst", caption = "超编审批结果", datetype = Types.INTEGER)
	public CField quota_over_rst; // 超编审批结果 1 允许增加编制调动 ps是否自动生成编制调整单 2 超编调动 3
									// 不允许调动
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
	@CFieldinfo(fieldname = "creator", notnull = true, caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "createtime", notnull = true, caption = "制单时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 制单时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1 调动人数
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
	@CLinkFieldInfo(jpaclass = Hr_emptransferbatch_line.class, linkFields = { @LinkFieldItem(lfield = "emptranfb_id", mfield = "emptranfb_id") })
	public CJPALineData<Hr_emptransferbatch_line> hr_emptransferbatch_lines;
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_emptransferbatch() throws Exception {
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
