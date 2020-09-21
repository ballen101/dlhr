package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_empptjob_app;

import java.sql.Types;

@CEntity(controller = CtrHr_empptjob_app.class)
public class Hr_empptjob_app extends CJPA {
	@CFieldinfo(fieldname = "ptjaid", iskey = true, notnull = true, caption = "兼职申请ID", datetype = Types.INTEGER)
	public CField ptjaid; // 兼职申请ID
	@CFieldinfo(fieldname = "ptjacode", codeid = 60, notnull = true, caption = "兼职编码", datetype = Types.VARCHAR)
	public CField ptjacode; // 兼职编码
	@CFieldinfo(fieldname = "applaydate", notnull = true, caption = "申请时间", datetype = Types.TIMESTAMP)
	public CField applaydate; // 申请时间
	@CFieldinfo(fieldname = "startdate", notnull = true, caption = "开始时间", datetype = Types.TIMESTAMP)
	public CField startdate; // 开始时间
	@CFieldinfo(fieldname = "enddate", notnull = true, caption = "结束时间", datetype = Types.TIMESTAMP)
	public CField enddate; // 结束时间
	@CFieldinfo(fieldname="breaked",caption="已终止",defvalue="2",datetype=Types.INTEGER)
	public CField breaked;  //已终止
	@CFieldinfo(fieldname = "ptjalev", caption = "兼职层级", datetype = Types.INTEGER)
	public CField ptjalev; // 兼职层级
	@CFieldinfo(fieldname = "ptjatype", caption = "兼职范围", datetype = Types.INTEGER)
	public CField ptjatype; // 兼职范围 1内部调用 2 跨单位 3 跨模块/制造群
	@CFieldinfo(fieldname = "ptreason", caption = "兼职原因", datetype = Types.VARCHAR)
	public CField ptreason; // 兼职原因
	@CFieldinfo(fieldname = "issubsidy", caption = "申请补贴", datetype = Types.INTEGER)
	public CField issubsidy; // 申请补贴
	@CFieldinfo(fieldname = "subsidyarm", caption = "月度补贴金额", datetype = Types.DECIMAL)
	public CField subsidyarm; // 月度补贴金额
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "人事ID", datetype = Types.INTEGER)
	public CField er_id; // 人事ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "id_number", notnull = true, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "degree", caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "hiredday", caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "odorgid", notnull = true, caption = "主职部门ID", datetype = Types.INTEGER)
	public CField odorgid; // 主职部门ID
	@CFieldinfo(fieldname = "odorgcode", notnull = true, caption = "主职部门编码", datetype = Types.VARCHAR)
	public CField odorgcode; // 主职部门编码
	@CFieldinfo(fieldname = "odorgname", notnull = true, caption = "主职部门名称", datetype = Types.VARCHAR)
	public CField odorgname; // 主职部门名称
	@CFieldinfo(fieldname = "odorghrlev", notnull = true, caption = "主职部门人事级别", datetype = Types.INTEGER)
	public CField odorghrlev; // 主职部门人事级别
	@CFieldinfo(fieldname = "odlv_id", caption = "主职职级ID", datetype = Types.INTEGER)
	public CField odlv_id; // 主职职级ID
	@CFieldinfo(fieldname = "odlv_num", caption = "主职职级", datetype = Types.DECIMAL)
	public CField odlv_num; // 主职职级
	@CFieldinfo(fieldname = "odhg_id", caption = "主职职等ID", datetype = Types.INTEGER)
	public CField odhg_id; // 主职职等ID
	@CFieldinfo(fieldname = "odhg_code", caption = "主职职等编码", datetype = Types.VARCHAR)
	public CField odhg_code; // 主职职等编码
	@CFieldinfo(fieldname = "odhg_name", caption = "主职职等名称", datetype = Types.VARCHAR)
	public CField odhg_name; // 主职职等名称
	@CFieldinfo(fieldname = "odospid", caption = "主职职位ID", datetype = Types.INTEGER)
	public CField odospid; // 主职职位ID
	@CFieldinfo(fieldname = "odospcode", caption = "主职职位编码", datetype = Types.VARCHAR)
	public CField odospcode; // 主职职位编码
	@CFieldinfo(fieldname = "odsp_name", caption = "主职职位名称", datetype = Types.VARCHAR)
	public CField odsp_name; // 主职职位名称
	@CFieldinfo(fieldname = "odattendtype", caption = "主职出勤类别", datetype = Types.VARCHAR)
	public CField odattendtype; // 主职出勤类别
	@CFieldinfo(fieldname = "oldhwc_namezl", caption = "主职职类", datetype = Types.VARCHAR)
	public CField oldhwc_namezl; // 主职职类
	@CFieldinfo(fieldname = "neworgid", notnull = true, caption = "兼职部门ID", datetype = Types.INTEGER)
	public CField neworgid; // 兼职部门ID
	@CFieldinfo(fieldname = "neworgcode", notnull = true, caption = "兼职部门编码", datetype = Types.VARCHAR)
	public CField neworgcode; // 兼职部门编码
	@CFieldinfo(fieldname = "neworgname", notnull = true, caption = "兼职部门名称", datetype = Types.VARCHAR)
	public CField neworgname; // 兼职部门名称
	@CFieldinfo(fieldname = "newlv_id", caption = "兼职职级ID", datetype = Types.INTEGER)
	public CField newlv_id; // 兼职职级ID
	@CFieldinfo(fieldname = "newlv_num", caption = "兼职职级", datetype = Types.DECIMAL)
	public CField newlv_num; // 兼职职级
	@CFieldinfo(fieldname = "newhg_id", caption = "兼职职等ID", datetype = Types.INTEGER)
	public CField newhg_id; // 兼职职等ID
	@CFieldinfo(fieldname = "newhg_code", caption = "兼职职等编码", datetype = Types.VARCHAR)
	public CField newhg_code; // 兼职职等编码
	@CFieldinfo(fieldname = "newhg_name", caption = "兼职职等名称", datetype = Types.VARCHAR)
	public CField newhg_name; // 兼职职等名称
	@CFieldinfo(fieldname = "neworghrlev", notnull = true, caption = "兼职部门人事级别", datetype = Types.INTEGER)
	public CField neworghrlev; // 兼职部门人事级别
	@CFieldinfo(fieldname = "newospid", caption = "兼职职位ID", datetype = Types.INTEGER)
	public CField newospid; // 兼职职位ID
	@CFieldinfo(fieldname = "newospcode", caption = "兼职职位编码", datetype = Types.VARCHAR)
	public CField newospcode; // 兼职职位编码
	@CFieldinfo(fieldname = "newsp_name", caption = "兼职职位名称", datetype = Types.VARCHAR)
	public CField newsp_name; // 兼职职位名称
	@CFieldinfo(fieldname = "newattendtype", caption = "兼职出勤类别", datetype = Types.VARCHAR)
	public CField newattendtype; // 兼职出勤类别
	@CFieldinfo(fieldname = "newhwc_namezl", caption = "兼职职类", datetype = Types.VARCHAR)
	public CField newhwc_namezl; // 兼职职类
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", notnull = true, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
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
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	@CFieldinfo(fieldname="substart",precision=19,scale=0,caption="补贴开始月份",datetype=Types.TIMESTAMP)
	public CField substart;  //补贴开始月份
	@CFieldinfo(fieldname="subend",precision=19,scale=0,caption="补贴截止月份",datetype=Types.TIMESTAMP)
	public CField subend;  //补贴截止月份
	@CFieldinfo(fieldname = "ptbbdate1",  caption = "终止日期", datetype = Types.TIMESTAMP)
	public CField ptbbdate1; // 终止日期
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_empptjob_app() throws Exception {
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
