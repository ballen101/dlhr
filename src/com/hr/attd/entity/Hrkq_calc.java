package com.hr.attd.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.attd.ctr.CtrHrkq_calc;
import java.sql.Types;

@CEntity(controller = CtrHrkq_calc.class)
public class Hrkq_calc extends CJPA {
	@CFieldinfo(fieldname = "clcid", iskey = true, notnull = true, precision = 10, scale = 0, caption = "计算ID", datetype = Types.INTEGER)
	public CField clcid; // 计算ID
	@CFieldinfo(fieldname = "clccode", codeid = 83, notnull = true, precision = 20, scale = 0, caption = "计算编码", datetype = Types.VARCHAR)
	public CField clccode; // 计算编码
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 10, scale = 0, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 128, scale = 0, caption = "机构", datetype = Types.VARCHAR)
	public CField orgname; // 机构
	@CFieldinfo(fieldname = "schtype", notnull = true, precision = 1, scale = 0, caption = "调度类型 1立即 2一次 3每天 4每周 5每月", datetype = Types.INTEGER)
	public CField schtype; // 调度类型 1立即 2一次 3每天 4每周 5每月
	@CFieldinfo(fieldname = "scdate", precision = 10, scale = 0, caption = "日期", datetype = Types.DATE)
	public CField scdate; // 日期
	@CFieldinfo(fieldname = "weekday", precision = 1, scale = 0, caption = "周 1234567", datetype = Types.INTEGER)
	public CField weekday; // 周 1234567
	@CFieldinfo(fieldname = "monthday", precision = 2, scale = 0, caption = "日1-31", datetype = Types.INTEGER)
	public CField monthday; // 日1-31
	@CFieldinfo(fieldname = "sctime", precision = 5, scale = 0, caption = "时间", datetype = Types.VARCHAR)
	public CField sctime; // 时间
	@CFieldinfo(fieldname = "kqdatetype", precision = 1, scale = 0, caption = "考勤日期界定方式 1 时间段 2 前X天 3 当前周 4 当前月", datetype = Types.INTEGER)
	public CField kqdatetype; // 考勤日期界定方式 1 时间段 2 前X天 3 当前周 4 当前月
	@CFieldinfo(fieldname = "kqdateperxday", precision = 11, scale = 0, caption = "前X天", datetype = Types.INTEGER)
	public CField kqdateperxday; // 前X天
	@CFieldinfo(fieldname = "kqstartdate", precision = 10, scale = 0, caption = "开始日期", datetype = Types.DATE)
	public CField kqstartdate; // 开始日期
	@CFieldinfo(fieldname = "kqenddate", precision = 10, scale = 0, caption = "截止日期", datetype = Types.DATE)
	public CField kqenddate; // 截止日期
	@CFieldinfo(fieldname = "exectime", precision = 19, scale = 0, caption = "实际执行时间", datetype = Types.TIMESTAMP)
	public CField exectime; // 实际执行时间
	@CFieldinfo(fieldname = "flishtime", precision = 19, scale = 0, caption = "完成时间", datetype = Types.TIMESTAMP)
	public CField flishtime; // 完成时间
	@CFieldinfo(fieldname = "costtime", precision = 12, scale = 2, caption = "时间成本秒", datetype = Types.DECIMAL)
	public CField costtime; // 时间成本秒
	@CFieldinfo(fieldname = "nxtexectime", precision = 19, scale = 0, caption = "下次执行时间", datetype = Types.TIMESTAMP)
	public CField nxtexectime; // 下次执行时间
	@CFieldinfo(fieldname = "scstat", notnull = true, precision = 1, scale = 0, caption = "调度状态 1 正常 2 错误 3 暂停", datetype = Types.INTEGER)
	public CField scstat; // 调度状态 1 正常 2 错误 3 暂停
	@CFieldinfo(fieldname="filstrato",notnull=true,precision=6,scale=2,caption="完成比例",defvalue="0.00",datetype=Types.DECIMAL)
	public CField filstrato;  //完成比例
	@CFieldinfo(fieldname="exstat",precision=1,scale=0,caption="执行状态1 执行中 2 未在执行",defvalue="2",datetype=Types.INTEGER)
	public CField exstat;  //执行状态1 执行中 2 未在执行
	@CFieldinfo(fieldname = "rstmsg", precision = 512, scale = 0, caption = "执行结果", datetype = Types.VARCHAR)
	public CField rstmsg; // 执行结果
	@CFieldinfo(fieldname = "remark", precision = 512, scale = 0, caption = "备注", datetype = Types.VARCHAR)
	public CField remark; // 备注
	@CFieldinfo(fieldname = "stat", notnull = true, precision = 2, scale = 0, caption = "表单状态", datetype = Types.INTEGER)
	public CField stat; // 表单状态
	@CFieldinfo(fieldname = "idpath", notnull = true, precision = 256, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	@CFieldinfo(fieldname = "entid", notnull = true, precision = 5, scale = 0, caption = "entid", datetype = Types.INTEGER)
	public CField entid; // entid
	@CFieldinfo(fieldname = "creator", notnull = true, precision = 32, scale = 0, caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", precision = 19, scale = 0, caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", precision = 32, scale = 0, caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", precision = 19, scale = 0, caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "attribute1", precision = 32, scale = 0, caption = "备用字段1", datetype = Types.VARCHAR)
	public CField attribute1; // 备用字段1
	@CFieldinfo(fieldname = "attribute2", precision = 32, scale = 0, caption = "备用字段2", datetype = Types.VARCHAR)
	public CField attribute2; // 备用字段2
	@CFieldinfo(fieldname = "attribute3", precision = 32, scale = 0, caption = "备用字段3", datetype = Types.VARCHAR)
	public CField attribute3; // 备用字段3
	@CFieldinfo(fieldname = "attribute4", precision = 32, scale = 0, caption = "备用字段4", datetype = Types.VARCHAR)
	public CField attribute4; // 备用字段4
	@CFieldinfo(fieldname = "attribute5", precision = 32, scale = 0, caption = "备用字段5", datetype = Types.VARCHAR)
	public CField attribute5; // 备用字段5
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义
	@CLinkFieldInfo(jpaclass = Hrkq_calcline.class, linkFields = { @LinkFieldItem(lfield = "clcid", mfield = "clcid") })
	public CJPALineData<Hrkq_calcline> hrkq_calclines;

	public Hrkq_calc() throws Exception {
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
