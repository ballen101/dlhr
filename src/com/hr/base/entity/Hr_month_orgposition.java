package com.hr.base.entity;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(caption = "机构职位月结(编制)")
public class Hr_month_orgposition extends CJPA {
	@CFieldinfo(fieldname = "mid", iskey = true, autoinc = true, notnull = true, precision = 20, scale = 0, caption = "ID", datetype = Types.INTEGER)
	public CField mid; // ID
	@CFieldinfo(fieldname = "yearmonth", notnull = true, precision = 7, scale = 0, caption = "年月", datetype = Types.VARCHAR)
	public CField yearmonth; // 年月
	@CFieldinfo(fieldname = "ospid", notnull = true, precision = 20, scale = 0, caption = "职位ID", datetype = Types.INTEGER)
	public CField ospid; // 职位ID
	@CFieldinfo(fieldname = "ospcode", notnull = true, precision = 16, scale = 0, caption = "职位编码", datetype = Types.VARCHAR)
	public CField ospcode; // 职位编码
	@CFieldinfo(fieldname = "pid", notnull = true, precision = 20, scale = 0, caption = "上级职位ID", datetype = Types.INTEGER)
	public CField pid; // 上级职位ID
	@CFieldinfo(fieldname = "pname", precision = 128, scale = 0, caption = "上级职位名称", datetype = Types.VARCHAR)
	public CField pname; // 上级职位名称
	@CFieldinfo(fieldname = "orgid", notnull = true, precision = 20, scale = 0, caption = "机构ID", datetype = Types.INTEGER)
	public CField orgid; // 机构ID
	@CFieldinfo(fieldname = "orgname", notnull = true, precision = 64, scale = 0, caption = "机构名称", datetype = Types.VARCHAR)
	public CField orgname; // 机构名称
	@CFieldinfo(fieldname = "sp_id", notnull = true, precision = 10, scale = 0, caption = "标准ID", datetype = Types.INTEGER)
	public CField sp_id; // 标准ID
	@CFieldinfo(fieldname = "sp_name", precision = 128, scale = 0, caption = "标准职位名称", datetype = Types.VARCHAR)
	public CField sp_name; // 标准职位名称
	@CFieldinfo(fieldname = "gtitle", precision = 32, scale = 0, caption = "职衔", datetype = Types.VARCHAR)
	public CField gtitle; // 职衔
	@CFieldinfo(fieldname = "lv_num", notnull = true, precision = 4, scale = 1, caption = "职级", datetype = Types.DECIMAL)
	public CField lv_num; // 职级
	@CFieldinfo(fieldname = "hg_name", precision = 128, scale = 0, caption = "职等", datetype = Types.VARCHAR)
	public CField hg_name; // 职等
	@CFieldinfo(fieldname = "hwc_namezl", precision = 128, scale = 0, caption = "职类", datetype = Types.VARCHAR)
	public CField hwc_namezl; // 职类
	@CFieldinfo(fieldname = "hwc_namezq", precision = 128, scale = 0, caption = "职群", datetype = Types.VARCHAR)
	public CField hwc_namezq; // 职群
	@CFieldinfo(fieldname = "hwc_namezz", precision = 128, scale = 0, caption = "职种", datetype = Types.VARCHAR)
	public CField hwc_namezz; // 职种
	@CFieldinfo(fieldname = "quota", precision = 10, scale = 0, caption = "编制数量", defvalue = "0", datetype = Types.INTEGER)
	public CField quota; // 编制数量
	@CFieldinfo(fieldname = "isadvtech", precision = 1, scale = 0, caption = "高级技术专业人才", defvalue = "2", datetype = Types.INTEGER)
	public CField isadvtech; // 高级技术专业人才
	@CFieldinfo(fieldname = "isoffjob", precision = 1, scale = 0, caption = "脱产", defvalue = "1", datetype = Types.INTEGER)
	public CField isoffjob; // 脱产
	@CFieldinfo(fieldname = "issensitive", precision = 1, scale = 0, caption = "敏感岗位", defvalue = "2", datetype = Types.INTEGER)
	public CField issensitive; // 敏感岗位
	@CFieldinfo(fieldname = "iskey", precision = 1, scale = 0, caption = "关键岗位", defvalue = "2", datetype = Types.INTEGER)
	public CField iskey; // 关键岗位
	@CFieldinfo(fieldname = "ishighrisk", precision = 1, scale = 0, caption = "高危岗位", defvalue = "2", datetype = Types.INTEGER)
	public CField ishighrisk; // 高危岗位
	@CFieldinfo(fieldname = "isneedadtoutwork", precision = 1, scale = 0, caption = "离职审计", defvalue = "2", datetype = Types.INTEGER)
	public CField isneedadtoutwork; // 离职审计
	@CFieldinfo(fieldname = "isdreamposition", precision = 1, scale = 0, caption = "梦想职位", defvalue = "2", datetype = Types.INTEGER)
	public CField isdreamposition; // 梦想职位
	@CFieldinfo(fieldname = "idpath", precision = 250, scale = 0, caption = "idpath", datetype = Types.VARCHAR)
	public CField idpath; // idpath
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public Hr_month_orgposition() throws Exception {
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
