package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CkItem;
import com.corsair.server.cjpa.CJPA;
import com.hr.perm.entity.Hr_employee;

import java.sql.Types;

@CEntity()
public class Hr_employeestat extends CJPA {
	@CFieldinfo(fieldname = "empstatid", iskey = true, notnull = true, caption = "ID", datetype = Types.INTEGER,
			dcfds={@CkItem(entity=Hr_employee.class,fdname="empstatid")})
	public CField empstatid; // ID
	@CFieldinfo(fieldname = "description", notnull = true, caption = "状态描述", datetype = Types.VARCHAR)
	public CField description; // 状态描述
	@CFieldinfo(fieldname = "statvalue", notnull = true, caption = "状态值", datetype = Types.INTEGER)
	public CField statvalue; // 状态值
	@CFieldinfo(fieldname = "stattype", notnull = true, caption = "状态类型", datetype = Types.INTEGER)
	public CField stattype; // 状态类型
	@CFieldinfo(fieldname = "isquota", notnull = true, caption = "是否占编制", datetype = Types.INTEGER)
	public CField isquota; // 是否占编制
	@CFieldinfo(fieldname = "isstaffefficiency", notnull = true, caption = "是否占人事效率", datetype = Types.INTEGER)
	public CField isstaffefficiency; // 是否占人事效率
	@CFieldinfo(fieldname = "issxenprob", notnull = true, caption = "允许实习入职转正", datetype = Types.INTEGER)
	public CField issxenprob; // 允许实习入职转正
	@CFieldinfo(fieldname = "isptemprob", notnull = true, caption = "允许普通入职转正", datetype = Types.INTEGER)
	public CField isptemprob; // 允许普通入职转正
	@CFieldinfo(fieldname = "isjxprob", notnull = true, caption = "允许考察转正", datetype = Types.INTEGER)
	public CField isjxprob; // 允许考察转正
	@CFieldinfo(fieldname = "allowtransfer", notnull = true, caption = "允许调动", datetype = Types.INTEGER)
	public CField allowtransfer; // 允许调动
	@CFieldinfo(fieldname = "transferstat", caption = "调动后的状态", datetype = Types.INTEGER)
	public CField transferstat; // 调动后的状态
	@CFieldinfo(fieldname = "allowsxlv", notnull = true, caption = "允许实习离职", datetype = Types.INTEGER)
	public CField allowsxlv; // 允许实习离职
	@CFieldinfo(fieldname = "allowlv", notnull = true, caption = "允许离职", datetype = Types.INTEGER)
	public CField allowlv; // 允许离职
	@CFieldinfo(fieldname = "allowsxfp", notnull = true, caption = "允许实习分配", datetype = Types.INTEGER)
	public CField allowsxfp; // 允许实习分配
	@CFieldinfo(fieldname = "needctrct", notnull = true, caption = "需要签订劳动合同", datetype = Types.INTEGER)
	public CField needctrct; // 需要签订劳动合同
	@CFieldinfo(fieldname = "neetkq", notnull = true, caption = "需要考勤", datetype = Types.INTEGER)
	public CField neetkq; // 需要考勤
	@CFieldinfo(fieldname = "usable", caption = "可用", datetype = Types.INTEGER)
	public CField usable; // 可用
	@CFieldinfo(fieldname = "creator", caption = "创建人", datetype = Types.VARCHAR)
	public CField creator; // 创建人
	@CFieldinfo(fieldname = "createtime", caption = "创建时间", datetype = Types.TIMESTAMP)
	public CField createtime; // 创建时间
	@CFieldinfo(fieldname = "updator", caption = "更新人", datetype = Types.VARCHAR)
	public CField updator; // 更新人
	@CFieldinfo(fieldname = "updatetime", caption = "更新时间", datetype = Types.TIMESTAMP)
	public CField updatetime; // 更新时间
	@CFieldinfo(fieldname = "note", caption = "备注", datetype = Types.VARCHAR)
	public CField note; // 备注
	@CFieldinfo(fieldname = "language1", caption = "状态中文名", datetype = Types.VARCHAR)
	public CField language1; // 状态中文名
	@CFieldinfo(fieldname = "language2", caption = "状态英文名", datetype = Types.VARCHAR)
	public CField language2; // 状态英文名
	@CFieldinfo(fieldname = "language3", caption = "状态繁体名", datetype = Types.VARCHAR)
	public CField language3; // 状态繁体名
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

	public Hr_employeestat() throws Exception {
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
