package com.hr.base.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_attach;
import com.hr.base.ctr.CtrHr_standposition;

@CEntity(controller = CtrHr_standposition.class)
public class Hr_standposition extends CJPA {
	@CFieldinfo(fieldname = "sp_id", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER)
	public CField sp_id; // ID
	@CFieldinfo(fieldname = "sp_code", codeid = 48, notnull = true, caption = "职位编码", datetype = Types.VARCHAR)
	public CField sp_code; // 职位编码
	@CFieldinfo(fieldname = "sp_name", caption = "职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 职位名称
	@CFieldinfo(fieldname = "sp_exp", caption = "职位说明", datetype = Types.VARCHAR)
	public CField sp_exp; // 职位说明
	@CFieldinfo(fieldname = "orgid", precision = 10, scale = 0, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgcode", precision = 32, scale = 0, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgcode; // 机构名称
	@CFieldinfo(fieldname = "orgname", precision = 255, scale = 0, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "lv_id", notnull = true, caption = "职级ID", datetype = Types.INTEGER)
	public CField lv_id; // 职级ID
	@CFieldinfo(fieldname = "lv_num", notnull = true, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hg_id", notnull = true, caption = "职等ID", datetype = Types.INTEGER)
	public CField hg_id; // 职等ID
	@CFieldinfo(fieldname = "hg_code", notnull = true, caption = "职等编码", datetype = Types.VARCHAR)
	public CField hg_code; // 职等编码
	@CFieldinfo(fieldname = "hg_name", caption = "职等名称", datetype = Types.VARCHAR)
	public CField hg_name; // 职等名称
	@CFieldinfo(fieldname = "hwc_idzl", notnull = true, caption = "职类ID", datetype = Types.INTEGER)
	public CField hwc_idzl; // 职类ID
	@CFieldinfo(fieldname = "hw_codezl", notnull = true, caption = "职类代码", datetype = Types.VARCHAR)
	public CField hw_codezl; // 职类代码
	@CFieldinfo(fieldname = "hwc_namezl", caption = "职类名称", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类名称
	@CFieldinfo(fieldname = "hwc_idzq", notnull = true, caption = "职群ID", datetype = Types.INTEGER)
	public CField hwc_idzq; // 职群ID
	@CFieldinfo(fieldname = "hw_codezq", notnull = true, caption = "职类代码", datetype = Types.VARCHAR)
	public CField hw_codezq; // 职类代码
	@CFieldinfo(fieldname = "hwc_namezq", caption = "职群名称", datetype = Types.VARCHAR)
	public CField hwc_namezq; // 职群名称
	@CFieldinfo(fieldname = "hwc_idzz", notnull = true, caption = "职种ID", datetype = Types.INTEGER)
	public CField hwc_idzz; // 职种ID
	@CFieldinfo(fieldname = "hw_codezz", notnull = true, caption = "职种代码", datetype = Types.VARCHAR)
	public CField hw_codezz; // 职种代码
	@CFieldinfo(fieldname = "hwc_namezz", caption = "职种名称", datetype = Types.VARCHAR)
	public CField hwc_namezz; // 职种名称
	@CFieldinfo(fieldname = "gtitle", caption = "职衔", datetype = Types.VARCHAR)
	public CField gtitle; // 职衔
	@CFieldinfo(fieldname = "usable", caption = "有效状态", datetype = Types.INTEGER)
	public CField usable; // 有效状态
	@CFieldinfo(fieldname = "isadvtech", caption = "高级技术专业人才", datetype = Types.INTEGER)
	public CField isadvtech; // 高级技术专业人才
	@CFieldinfo(fieldname = "isoffjob", caption = "脱产", datetype = Types.INTEGER)
	public CField isoffjob; // 脱产
	@CFieldinfo(fieldname = "issensitive", caption = "敏感岗位", datetype = Types.INTEGER)
	public CField issensitive; // 敏感岗位 离职 调岗需要审计
	@CFieldinfo(fieldname = "iskey", caption = "关键岗位", datetype = Types.INTEGER)
	public CField iskey; // 关键岗位
	@CFieldinfo(fieldname = "ishighrisk", caption = "高危岗位", datetype = Types.INTEGER)
	public CField ishighrisk; // 高危岗位
	@CFieldinfo(fieldname = "isneedadtoutwork", caption = "需要离职审计", datetype = Types.INTEGER)
	public CField isneedadtoutwork; // 需要离职审计
	@CFieldinfo(fieldname = "isdreamposition", caption = "是否梦想职位", datetype = Types.INTEGER)
	public CField isdreamposition; // 是否梦想职位
	@CFieldinfo(fieldname = "maxage", caption = "最大年龄", datetype = Types.INTEGER)
	public CField maxage; // 最大年龄
	@CFieldinfo(fieldname = "minage", caption = "最小年龄", datetype = Types.INTEGER)
	public CField minage; // 最小年龄
	@CFieldinfo(fieldname = "mindegree", caption = "最低学历", datetype = Types.INTEGER)
	public CField mindegree; // 最低学历
	@CFieldinfo(fieldname = "yhtype", caption = "年假处理方式 1年假 2 调休", defvalue = "1", datetype = Types.INTEGER)
	public CField yhtype; // 年假处理方式 1年假 2 调休
	@CFieldinfo(fieldname = "alloworgwsch", precision = 1, scale = 0, caption = "参与机构排班", defvalue = "1", datetype = Types.INTEGER)
	public CField alloworgwsch; // 参与机构排班
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "attid", caption = "附件", datetype = Types.INTEGER)
	public CField attid; // 附件
	@CFieldinfo(fieldname = "aidpath", precision = 255, scale = 0, caption = "aidpath", datetype = Types.VARCHAR)
	public CField aidpath; // aidpath
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
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
	@CFieldinfo(fieldname="attribute5",caption="备用字段5",datetype=Types.VARCHAR)
	public CField attribute5;  //备用字段5
	@CFieldinfo(fieldname="istechsub",precision=1,scale=0,caption="是否技术津贴",defvalue="2",datetype=Types.INTEGER)
	public CField istechsub;  //是否技术津贴
	@CFieldinfo(fieldname="isteamward",precision=1,scale=0,caption="是否团队管理奖",defvalue="2",datetype=Types.INTEGER)
	public CField isteamward;  //是否技术津贴
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Shw_attach.class, linkFields = { @LinkFieldItem(lfield = "attid", mfield = "attid") })
	public CJPALineData<Shw_attach> shw_attachs;

	public Hr_standposition() throws Exception {
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
