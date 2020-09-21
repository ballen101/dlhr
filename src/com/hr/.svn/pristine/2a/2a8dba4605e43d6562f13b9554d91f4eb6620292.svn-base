package com.hr.util.hrmail;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "邮件发送日志", tablename = "Hr_emailsend_log")
public class Hr_emailsend_log extends CJPA {
	@CFieldinfo(fieldname = "emid", iskey = true, notnull = true, precision = 16, scale = 0, caption = "自己的ID", datetype = Types.INTEGER)
	public CField emid; // 自己的ID
	@CFieldinfo(fieldname = "emsdid", precision = 16, scale = 0, caption = "别人的ID", datetype = Types.INTEGER)
	public CField emsdid; // 别人的ID
	@CFieldinfo(fieldname = "aynemid", codeid = 117, notnull = true, precision = 16, scale = 0, caption = "同步ID", datetype = Types.VARCHAR)
	public CField aynemid; // 同步ID
	@CFieldinfo(fieldname = "employee_code", notnull = true, precision = 16, scale = 0, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "employee_name", notnull = true, precision = 64, scale = 0, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "address", notnull = true, precision = 64, scale = 0, caption = "收件人", datetype = Types.VARCHAR)
	public CField address; // 收件人
	@CFieldinfo(fieldname = "extid", precision = 64, scale = 0, caption = "扩展ID", datetype = Types.VARCHAR)
	public CField extid; // 扩展ID
	@CFieldinfo(fieldname = "address_bcc", precision = 64, scale = 0, caption = "抄送", datetype = Types.VARCHAR)
	public CField address_bcc; // 抄送
	@CFieldinfo(fieldname = "address_cc", precision = 64, scale = 0, caption = "秘送", datetype = Types.VARCHAR)
	public CField address_cc; // 秘送
	@CFieldinfo(fieldname = "approvaldate", precision = 64, scale = 0, caption = "回写审批时间", datetype = Types.VARCHAR)
	public CField approvaldate; // 回写审批时间
	@CFieldinfo(fieldname = "approvalman", precision = 64, scale = 0, caption = "回写审批人", datetype = Types.VARCHAR)
	public CField approvalman; // 回写审批人
	@CFieldinfo(fieldname = "mailbody", precision = 1024, scale = 0, caption = "邮件内容", datetype = Types.VARCHAR)
	public CField mailbody; // 邮件内容
	@CFieldinfo(fieldname = "mailsubject", notnull = true, precision = 128, scale = 0, caption = "邮件标题", datetype = Types.VARCHAR)
	public CField mailsubject; // 邮件标题
	@CFieldinfo(fieldname = "mailtype", notnull = true, precision = 3, scale = 0, caption = "邮件类型", datetype = Types.VARCHAR)
	public CField mailtype; // 邮件类型
	@CFieldinfo(fieldname = "wfurl", notnull = true, precision = 512, scale = 0, caption = "审批URL", datetype = Types.VARCHAR)
	public CField wfurl; // 审批URL
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", notnull = true, precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "removed", precision = 1, scale = 0, caption = "是否已经从同步数据删除", defvalue = "2", datetype = Types.INTEGER)
	public CField removed; // 是否已经从同步数据删除
	@CFieldinfo(fieldname = "pushsuccess", notnull = true, precision = 1, scale = 0, caption = "推送状态", defvalue = "2", datetype = Types.INTEGER)
	public CField pushsuccess; // 推送状态 1 新建未推送 2 新建推送完成 3 更新失败 4 更新完成
	@CFieldinfo(fieldname = "repuhstimes", notnull = true, precision = 3, scale = 0, caption = "从新推送次数", defvalue = "0", datetype = Types.INTEGER)
	public CField repuhstimes; // 从新推送次数
	@CFieldinfo(fieldname = "attr1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attr1; // 备用字段1
	@CFieldinfo(fieldname = "attr2", precision = 32, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attr2; // 备用字段2
	@CFieldinfo(fieldname = "attr3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attr3; // 备用字段3
	@CFieldinfo(fieldname = "attr4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attr4; // 备用字段4
	@CFieldinfo(fieldname = "attr5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attr5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_emailsend_log() throws Exception {
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
