package com.hr.base.entity;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.base.ctr.CtrHr_orgposition;

@CEntity(controller = CtrHr_orgposition.class)
public class Hr_orgposition extends CJPA {
	@CFieldinfo(fieldname = "ospid", iskey = true, notnull = true, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", codeid = 50, notnull = true, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "pid", notnull = true, caption = "上级职位ID", datetype = Types.INTEGER)
	public CField pid; // 上级职位ID
	@CFieldinfo(fieldname = "pname", caption = "上级职位名称", datetype = Types.VARCHAR)
	public CField pname; // 上级职位名称
	@CFieldinfo(fieldname = "orgid", notnull = true, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", notnull = true, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "orgcode", caption = "机构编码", datetype = Types.VARCHAR)
	public CField orgcode; // 机构编码
	@CFieldinfo(fieldname = "sp_id", notnull = true, caption = "标准ID", datetype = Types.INTEGER)
	public CField sp_id; // 标准ID
	@CFieldinfo(fieldname = "sp_code", notnull = true, caption = "标准职位编码", datetype = Types.VARCHAR)
	public CField sp_code; // 标准职位编码
	@CFieldinfo(fieldname = "sp_name", caption = "标准职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 标准职位名称
	@CFieldinfo(fieldname = "gtitle", caption = "职衔", datetype = Types.VARCHAR)
	public CField gtitle; // 职衔
	@CFieldinfo(fieldname = "sp_exp", caption = "职位说明", datetype = Types.VARCHAR)
	public CField sp_exp; // 职位说明
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
	@CFieldinfo(fieldname = "quota", caption = "编制数量", datetype = Types.INTEGER)
	public CField quota; // 编制数量
	@CFieldinfo(fieldname = "usable", caption = "有效状态", datetype = Types.INTEGER)
	public CField usable; // 有效状态
	@CFieldinfo(fieldname = "isadvtech", caption = "高级技术专业人才", datetype = Types.INTEGER)
	public CField isadvtech; // 高级技术专业人才
	@CFieldinfo(fieldname = "isoffjob", caption = "脱产", datetype = Types.INTEGER)
	public CField isoffjob; // 脱产
	@CFieldinfo(fieldname = "issensitive", caption = "敏感岗位 离职 调岗需要审计", datetype = Types.INTEGER)
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
	@CFieldinfo(fieldname = "profnal", caption = "专业要求", datetype = Types.VARCHAR)
	public CField profnal; // 专业要求
	@CFieldinfo(fieldname = "langreq", caption = "语言能力", datetype = Types.VARCHAR)
	public CField langreq; // 语言能力
	@CFieldinfo(fieldname = "offsoft", caption = "办公软件要求", datetype = Types.VARCHAR)
	public CField offsoft; // 办公软件要求
	@CFieldinfo(fieldname = "proknoge", caption = "专业要求", datetype = Types.VARCHAR)
	public CField proknoge; // 专业要求
	@CFieldinfo(fieldname = "skillreq", caption = "技能要求", datetype = Types.VARCHAR)
	public CField skillreq; // 技能要求
	@CFieldinfo(fieldname = "qualification", caption = "资格证书", datetype = Types.VARCHAR)
	public CField qualification; // 资格证书
	@CFieldinfo(fieldname = "postreq", caption = "经验要求", datetype = Types.VARCHAR)
	public CField postreq; // 经验要求
	@CFieldinfo(fieldname = "workpost", caption = "工作经验", datetype = Types.VARCHAR)
	public CField workpost; // 工作经验
	@CFieldinfo(fieldname = "indtryexper", caption = "行业经验", datetype = Types.VARCHAR)
	public CField indtryexper; // 行业经验
	@CFieldinfo(fieldname = "companyexper", caption = "本企业经验", datetype = Types.VARCHAR)
	public CField companyexper; // 本企业经验
	@CFieldinfo(fieldname = "coreqreq", caption = "核心能力", datetype = Types.VARCHAR)
	public CField coreqreq; // 核心能力
	@CFieldinfo(fieldname = "otherreq", caption = "其它要求", datetype = Types.VARCHAR)
	public CField otherreq; // 其它要求
	@CFieldinfo(fieldname = "keyresp1", caption = "关键职责1", datetype = Types.VARCHAR)
	public CField keyresp1; // 关键职责1
	@CFieldinfo(fieldname = "keyrespper1", notnull = false, caption = "比重1%", datetype = Types.DECIMAL)
	public CField keyrespper1; // 比重1%
	@CFieldinfo(fieldname = "keyresp2", caption = "关键职责2", datetype = Types.VARCHAR)
	public CField keyresp2; // 关键职责2
	@CFieldinfo(fieldname = "keyrespper2", notnull = false, caption = "比重2%", datetype = Types.DECIMAL)
	public CField keyrespper2; // 比重2%
	@CFieldinfo(fieldname = "keyresp3", caption = "关键职责3", datetype = Types.VARCHAR)
	public CField keyresp3; // 关键职责3
	@CFieldinfo(fieldname = "keyrespper3", notnull = false, caption = "比重3%", datetype = Types.DECIMAL)
	public CField keyrespper3; // 比重3%
	@CFieldinfo(fieldname = "keyresp4", caption = "关键职责4", datetype = Types.VARCHAR)
	public CField keyresp4; // 关键职责4
	@CFieldinfo(fieldname = "keyrespper4", notnull = false, caption = "比重4%", datetype = Types.DECIMAL)
	public CField keyrespper4; // 比重4%
	@CFieldinfo(fieldname = "keyresp5", caption = "关键职责5", datetype = Types.VARCHAR)
	public CField keyresp5; // 关键职责5
	@CFieldinfo(fieldname = "keyrespper5", notnull = false, caption = "比重5%", datetype = Types.DECIMAL)
	public CField keyrespper5; // 比重5%
	@CFieldinfo(fieldname = "keyresp6", caption = "关键职责6", datetype = Types.VARCHAR)
	public CField keyresp6; // 关键职责6
	@CFieldinfo(fieldname = "keyrespper6", notnull = false, caption = "比重6%", datetype = Types.DECIMAL)
	public CField keyrespper6; // 比重6%
	@CFieldinfo(fieldname = "keyresp7", caption = "关键职责7", datetype = Types.VARCHAR)
	public CField keyresp7; // 关键职责7
	@CFieldinfo(fieldname = "keyrespper7", notnull = false, caption = "比重7%", datetype = Types.DECIMAL)
	public CField keyrespper7; // 比重7%
	@CFieldinfo(fieldname = "keyresp8", caption = "关键职责8", datetype = Types.VARCHAR)
	public CField keyresp8; // 关键职责8
	@CFieldinfo(fieldname = "keyrespper8", notnull = false, caption = "比重8%", datetype = Types.DECIMAL)
	public CField keyrespper8; // 比重8%
	@CFieldinfo(fieldname="pay_way",precision=32,scale=0,caption="计薪方式",datetype=Types.VARCHAR)
	public CField pay_way;  //计薪方式
	@CFieldinfo(fieldname="atdtype",precision=32,scale=0,caption="出勤类别",datetype=Types.VARCHAR)
	public CField atdtype;  //出勤类别
	@CFieldinfo(fieldname="eovertype",precision=2,scale=0,caption="加班类别",datetype=Types.INTEGER)
	public CField eovertype;  //加班类别
	@CFieldinfo(fieldname = "remark", caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
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
	@CFieldinfo(fieldname = "idpath", caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname="istechsub",precision=1,scale=0,caption="是否技术津贴",defvalue="2",datetype=Types.INTEGER)
	public CField istechsub;  //是否技术津贴
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	@CLinkFieldInfo(jpaclass = Hr_orgpositionkpi.class, linkFields = { @LinkFieldItem(lfield = "ospid", mfield = "ospid") })
	public CJPALineData<Hr_orgpositionkpi> hr_orgpositionkpis;

	public Hr_orgposition() throws Exception {
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
