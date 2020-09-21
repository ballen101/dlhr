package com.hr.perm.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.perm.ctr.CtrHr_black_del;

import java.sql.Types;

@CEntity(controller = CtrHr_black_del.class)
public class Hr_black_del extends CJPA {
	@CFieldinfo(fieldname = "bdid", iskey = true, notnull = true, caption = "解封ID", datetype = Types.INTEGER)
	public CField bdid; // 解封ID
	@CFieldinfo(fieldname = "bdcode", codeid = 67, notnull = true, caption = "解封编码", datetype = Types.VARCHAR)
	public CField bdcode; // 解封编码
	@CFieldinfo(fieldname = "delappdate", notnull = true, caption = "解封申请日期", datetype = Types.TIMESTAMP)
	public CField delappdate; // 解封申请日期
	@CFieldinfo(fieldname = "deldate", notnull = true, caption = "解封生效日期", datetype = Types.TIMESTAMP)
	public CField deldate; // 解封生效日期
	@CFieldinfo(fieldname = "blackreason", caption = "加封原因", datetype = Types.VARCHAR)
	public CField blackreason; // 加封原因
	@CFieldinfo(fieldname = "batype", caption = "加封类型", datetype = Types.VARCHAR)
	public CField batype; // 加封类型
	@CFieldinfo(fieldname = "badate", caption = "加封日期", datetype = Types.DATE)
	public CField badate; // 加封日期
	@CFieldinfo(fieldname = "bacode", caption = "加封编码", datetype = Types.VARCHAR)
	public CField bacode; // 加封编码
	@CFieldinfo(fieldname = "blackdellreason", caption = "解封原因", datetype = Types.VARCHAR)
	public CField blackdellreason; // 解封原因
	@CFieldinfo(fieldname = "deltimes", caption = "解封次数", datetype = Types.INTEGER)
	public CField deltimes; // 解封次数
	@CFieldinfo(fieldname = "deltype", caption = "解封类型", datetype = Types.INTEGER)
	public CField deltype; // 解封类型
	@CFieldinfo(fieldname = "deltype1", caption = "解封类别", datetype = Types.INTEGER)
	public CField deltype1; // 解封类别
	@CFieldinfo(fieldname = "delfileid", caption = "解封支持附件ID", datetype = Types.INTEGER)
	public CField delfileid; // 解封支持附件ID
	@CFieldinfo(fieldname = "delfilename", caption = "解封支持附件", datetype = Types.VARCHAR)
	public CField delfilename; // 解封支持附件
	@CFieldinfo(fieldname = "billtype", caption = "表单方式1 单个 2 批量", defvalue = "1", datetype = Types.INTEGER)
	public CField billtype; // 表单方式1 单个 2 批量
	@CFieldinfo(fieldname = "ljid", caption = "离职ID", datetype = Types.INTEGER)
	public CField ljid; // 离职ID
	@CFieldinfo(fieldname = "ljcode", caption = "离职编码", datetype = Types.VARCHAR)
	public CField ljcode; // 离职编码
	@CFieldinfo(fieldname = "ljreason", caption = "离职原因", datetype = Types.VARCHAR)
	public CField ljreason; // 离职原因
	@CFieldinfo(fieldname = "orgid", caption = "部门ID", datetype = Types.INTEGER)
	public CField orgid; // 部门ID
	@CFieldinfo(fieldname = "orgcode", caption = "部门编码", datetype = Types.VARCHAR)
	public CField orgcode; // 部门编码
	@CFieldinfo(fieldname = "orgname", caption = "部门名称", datetype = Types.VARCHAR)
	public CField orgname; // 部门名称
	@CFieldinfo(fieldname = "orghrlev", caption = "人事机构层级", datetype = Types.INTEGER)
	public CField orghrlev; // 人事机构层级
	@CFieldinfo(fieldname = "empflev", caption = "员工层级", datetype = Types.INTEGER)
	public CField empflev; // 员工层级
	@CFieldinfo(fieldname = "neworgid", caption = "再入职部门ID", datetype = Types.INTEGER)
	public CField neworgid; // 再入职部门ID
	@CFieldinfo(fieldname = "neworgcode", caption = "再入职部门code", datetype = Types.VARCHAR)
	public CField neworgcode; // 再入职部门code
	@CFieldinfo(fieldname = "neworgname", caption = "再入职部门", datetype = Types.VARCHAR)
	public CField neworgname; // 再入职部门
	@CFieldinfo(fieldname = "newospid", caption = "再入职职位ID", datetype = Types.INTEGER)
	public CField newospid; // 再入职职位ID
	@CFieldinfo(fieldname = "newospcode", caption = "再入职职位Code", datetype = Types.VARCHAR)
	public CField newospcode; // 再入职职位Code
	@CFieldinfo(fieldname = "newsp_name", caption = "再入职职位", datetype = Types.VARCHAR)
	public CField newsp_name; // 再入职职位
	@CFieldinfo(fieldname = "newlv_num", caption = "再入职职级", datetype = Types.DECIMAL)
	public CField newlv_num; // 再入职职级
	@CFieldinfo(fieldname="neworghrlev",precision=1,scale=0,caption="在入职机构人事层级",datetype=Types.INTEGER)
	public CField neworghrlev;  //在入职机构人事层级
	@CFieldinfo(fieldname = "er_id", notnull = true, caption = "档案ID", datetype = Types.INTEGER)
	public CField er_id; // 档案ID
	@CFieldinfo(fieldname = "er_code", caption = "档案编码", datetype = Types.VARCHAR)
	public CField er_code; // 档案编码
	@CFieldinfo(fieldname = "employee_code", notnull = true, caption = "工号", datetype = Types.VARCHAR)
	public CField employee_code; // 工号
	@CFieldinfo(fieldname = "sex", notnull = true, caption = "性别", datetype = Types.INTEGER)
	public CField sex; // 性别
	@CFieldinfo(fieldname = "id_number", notnull = true, caption = "身份证号", datetype = Types.VARCHAR)
	public CField id_number; // 身份证号
	@CFieldinfo(fieldname = "employee_name", notnull = true, caption = "姓名", datetype = Types.VARCHAR)
	public CField employee_name; // 姓名
	@CFieldinfo(fieldname = "degree", caption = "学历", datetype = Types.INTEGER)
	public CField degree; // 学历
	@CFieldinfo(fieldname = "hiredday", caption = "入职日期", datetype = Types.TIMESTAMP)
	public CField hiredday; // 入职日期
	@CFieldinfo(fieldname = "ljdate", caption = "离职日期", datetype = Types.TIMESTAMP)
	public CField ljdate; // 离职日期
	@CFieldinfo(fieldname = "ljtype1", caption = "离职方式 自离 辞职 等", datetype = Types.INTEGER)
	public CField ljtype1; // 离职方式 自离 辞职 等
	@CFieldinfo(fieldname = "lv_id", caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hg_id", caption = "职等ID", datetype = Types.INTEGER)
	public CField hg_id; // 职等ID
	@CFieldinfo(fieldname = "hg_code", caption = "职等编码", datetype = Types.VARCHAR)
	public CField hg_code; // 职等编码
	@CFieldinfo(fieldname = "hg_name", caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "ospid", caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "sp_name", caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "wfid", caption = "wfid", datetype = Types.INTEGER)
	public CField wfid; // wfid
	@CFieldinfo(fieldname = "attid", caption = "attid", datetype = Types.INTEGER)
	public CField attid; // attid
	@CFieldinfo(fieldname = "stat", caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", caption = "制单人", datetype = Types.VARCHAR)
	public CField creator; // 制单人
	@CFieldinfo(fieldname = "createtime", caption = "制单时间", datetype = Types.TIMESTAMP)
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
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_black_del() throws Exception {
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
