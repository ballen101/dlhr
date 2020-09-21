package com.hr.inface.entity;

import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

/**相片接口表
 * @author shangwen
 *         
 */
@CEntity(dbpool = "oldtxmssql", tablename = "HRMSTest.dbo.view_TxZlEmployee")
public class View_TxZlEmployee extends CJPA {
	@CFieldinfo(fieldname = "id", notnull = true, caption = "员工id", datetype = Types.INTEGER)
	public CField id; // 员工id
	@CFieldinfo(fieldname = "dept", caption = "组织", datetype = Types.VARCHAR)
	public CField dept; // 组织
	@CFieldinfo(fieldname = "code", notnull = true, caption = "工号 ", datetype = Types.CHAR)
	public CField code; // 工号 
	@CFieldinfo(fieldname = "name", caption = "姓名 ", datetype = Types.VARCHAR)
	public CField name; // 姓名 
	@CFieldinfo(fieldname = "sex", notnull = true, caption = "性别 ", datetype = Types.BIT)
	public CField sex; // 性别 0 男 1女
	@CFieldinfo(fieldname = "borndate", notnull = true, caption = "出生日期", datetype = Types.TIMESTAMP)
	public CField borndate; // 出生日期
	@CFieldinfo(fieldname = "nation", caption = "nation", datetype = Types.CHAR)
	public CField nation; // nation
	@CFieldinfo(fieldname = "hunyin", caption = "hunyin", datetype = Types.CHAR)
	public CField hunyin; // hunyin
	@CFieldinfo(fieldname = "g_sg", notnull = true, caption = "身高", datetype = Types.VARCHAR)
	public CField g_sg; // 身高
	@CFieldinfo(fieldname = "g_sfzf", notnull = true, caption = "身份证发证机构", datetype = Types.VARCHAR)
	public CField g_sfzf; // 身份证发证机构
	@CFieldinfo(fieldname = "g_ygjg", notnull = true, caption = "户籍", datetype = Types.VARCHAR)
	public CField g_ygjg; // 户籍
	@CFieldinfo(fieldname = "g_jtzz", notnull = true, caption = "家庭住址", datetype = Types.VARCHAR)
	public CField g_jtzz; // 家庭住址
	@CFieldinfo(fieldname = "g_lxdh", caption = "联系电话 ", datetype = Types.VARCHAR)
	public CField g_lxdh; // 联系电话 
	@CFieldinfo(fieldname = "cy", notnull = true, caption = "cy", datetype = Types.BIT)
	public CField cy; // cy
	@CFieldinfo(fieldname = "sfz", notnull = true, caption = "身份证号码", datetype = Types.CHAR)
	public CField sfz; // 身份证号码
	@CFieldinfo(fieldname = "sfz6", caption = "提取身份证后六位", datetype = Types.VARCHAR)
	public CField sfz6; // 提取身份证后六位
	@CFieldinfo(fieldname = "zhiji", caption = "职级", datetype = Types.CHAR)
	public CField zhiji; // 职级
	@CFieldinfo(fieldname = "xueli", caption = "学历", datetype = Types.CHAR)
	public CField xueli; // 学历
	@CFieldinfo(fieldname = "g_zy", notnull = true, caption = "专业", datetype = Types.VARCHAR)
	public CField g_zy; // 专业
	@CFieldinfo(fieldname = "g_jjllr", notnull = true, caption = "紧急联系人", datetype = Types.VARCHAR)
	public CField g_jjllr; // 紧急联系人
	@CFieldinfo(fieldname = "g_jjlldh", notnull = true, caption = "紧急联系电话", datetype = Types.VARCHAR)
	public CField g_jjlldh; // 紧急联系电话
	@CFieldinfo(fieldname = "g_dqzz", notnull = true, caption = "职务名称编码", datetype = Types.VARCHAR)
	public CField g_dqzz; // 职务名称编码
	@CFieldinfo(fieldname = "zhiwu", caption = "职务名称", datetype = Types.CHAR)
	public CField zhiwu; // 职务名称
	@CFieldinfo(fieldname = "yuZhiWu", caption = "入职时间", datetype = Types.CHAR)
	public CField yuZhiWu; // 入职时间
	@CFieldinfo(fieldname = "pydate", notnull = true, caption = "入职时间 ", datetype = Types.TIMESTAMP)
	public CField pydate; // 入职时间 
	@CFieldinfo(fieldname = "state", notnull = true, caption = "员工在职状态 ", datetype = Types.TINYINT)
	public CField state; // 员工在职状态 
	@CFieldinfo(fieldname = "lzdate", notnull = true, caption = "离职日期", datetype = Types.VARCHAR)
	public CField lzdate; // 离职日期
	@CFieldinfo(fieldname = "lzmemo", notnull = true, caption = "离职原因", datetype = Types.VARCHAR)
	public CField lzmemo; // 离职原因
	@CFieldinfo(fieldname = "roomBed", notnull = true, caption = "HT17053044", datetype = Types.VARCHAR)
	public CField roomBed; // HT17053044
	@CFieldinfo(fieldname = "G_pqjg", notnull = true, caption = "招聘方式", datetype = Types.VARCHAR)
	public CField G_pqjg; // 招聘方式
	@CFieldinfo(fieldname = "G_lsgjg", notnull = true, caption = "招聘单位", datetype = Types.VARCHAR)
	public CField G_lsgjg; // 招聘单位
	@CFieldinfo(fieldname = "typeName", caption = "脱产", datetype = Types.VARCHAR)
	public CField typeName; // 脱产
	@CFieldinfo(fieldname = "jdate", caption = "最后操作时间", datetype = Types.TIMESTAMP)
	public CField jdate; // 最后操作时间
	@CFieldinfo(fieldname = "photo", caption = "相片", datetype = Types.LONGVARBINARY)
	public CField photo; // 相片
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public View_TxZlEmployee() throws Exception {
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
