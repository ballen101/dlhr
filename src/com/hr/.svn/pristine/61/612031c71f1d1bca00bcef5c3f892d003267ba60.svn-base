package com.hr.inface.entity;

/**
 * 招募系统接口实体
 * 
 * @author shangwen
 * 
 */
import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.server.cjpa.CJPA;
import java.sql.Types;

@CEntity(dbpool = "oldtxmssql", tablename = "HRMS.dbo.view_TxZlEmployee_zp")
public class View_TxZlEmployee_zp extends CJPA {
	@CFieldinfo(fieldname = "id", notnull = true, precision = 10, scale = 0, caption = "id", datetype = Types.INTEGER)
	public CField id; // 员工id
	@CFieldinfo(fieldname = "dept", precision = 24, scale = 0, caption = "dept", datetype = Types.VARCHAR)
	public CField dept; // orgcode组织
	@CFieldinfo(fieldname = "code", notnull = true, precision = 12, scale = 0, caption = "code", datetype = Types.CHAR)
	public CField code; // 工号employee_code
	@CFieldinfo(fieldname = "name", precision = 100, scale = 0, caption = "name", datetype = Types.VARCHAR)
	public CField name; // 姓名employee_name
	@CFieldinfo(fieldname = "sex", notnull = true, precision = 10, scale = 0, caption = "sex", datetype = Types.INTEGER)
	public CField sex; // CASE WHEN z.sex=0 THEN 2 ELSE 1 END AS sex,--性别
	@CFieldinfo(fieldname = "borndate", notnull = true, precision = 23, scale = 3, caption = "borndate", datetype = Types.TIMESTAMP)
	public CField borndate; // ,--出生日期birthday
	@CFieldinfo(fieldname = "g_sg", notnull = true, precision = 10, scale = 0, caption = "g_sg", datetype = Types.VARCHAR)
	public CField g_sg; // --身高g_sg
	@CFieldinfo(fieldname = "hrcode", precision = 50, scale = 0, caption = "hrcode", datetype = Types.NVARCHAR)
	public CField hrcode; // 民族nation
	@CFieldinfo(fieldname = "hunyin", notnull = true, precision = 10, scale = 0, caption = "hunyin", datetype = Types.INTEGER)
	public CField hunyin; // z.hunyin='01' THEN 1 WHEN z.hunyin='02' THEN 2 WHEN z.hunyin='03' THEN 3 ELSE 2 END AS hunyin,--婚姻married
	@CFieldinfo(fieldname = "jxfs", notnull = true, precision = 4, scale = 0, caption = "jxfs", datetype = Types.VARCHAR)
	public CField jxfs; // WHEN z.Jxfs='01' THEN '计时' WHEN z.Jxfs='02' THEN '月薪' WHEN z.Jxfs='03' THEN '计件' WHEN z.Jxfs='04' THEN '日薪' ELSE '计时' END AS jxfs
						// ,--计薪方式pay_way
	@CFieldinfo(fieldname = "ifdaka", notnull = true, precision = 10, scale = 0, caption = "ifdaka", datetype = Types.INTEGER)
	public CField ifdaka; // WHEN z.ifDaKa=1 THEN 2 ELSE 1 END AS ifdaka ,--免考勤打卡noclock
	@CFieldinfo(fieldname = "g_ygjg", notnull = true, precision = 1, scale = 0, caption = "g_ygjg", datetype = Types.VARCHAR)
	public CField g_ygjg; // z.g_ygjg='本市户籍' THEN '1' WHEN z.g_ygjg='外市农村户籍' THEN '3' z.g_ygjg='外市城镇户籍' THEN '4' ELSE '3' END AS g_ygjg ,--户籍类型registertype
	@CFieldinfo(fieldname = "jiguan", notnull = true, precision = 100, scale = 0, caption = "jiguan", datetype = Types.VARCHAR)
	public CField jiguan; // 籍贯nativeplace
	@CFieldinfo(fieldname = "g_sfzf", notnull = true, precision = 50, scale = 0, caption = "g_sfzf", datetype = Types.VARCHAR)
	public CField g_sfzf; // 身份证发证机构sign_org
	@CFieldinfo(fieldname = "g_jtzz", notnull = true, precision = 80, scale = 0, caption = "g_jtzz", datetype = Types.VARCHAR)
	public CField g_jtzz; // 家庭住址address,身份证地址registeraddress
	@CFieldinfo(fieldname = "G_Dqzz", notnull = true, precision = 100, scale = 0, caption = "G_Dqzz", datetype = Types.VARCHAR)
	public CField G_Dqzz; // G_Dqzz 当前住址address
	@CFieldinfo(fieldname = "G_rzfs", notnull = true, precision = 30, scale = 0, caption = "G_rzfs", datetype = Types.VARCHAR)
	public CField G_rzfs; // G_rzfs人员来源
	@CFieldinfo(fieldname = "G_jblb", notnull = true, precision = 30, scale = 0, caption = "G_jblb", datetype = Types.VARCHAR)
	public CField G_jblb; // G_jblb加班类别
	@CFieldinfo(fieldname = "sfz", notnull = true, precision = 18, scale = 0, caption = "sfz", datetype = Types.CHAR)
	public CField sfz; // 身份证号码id_number
	@CFieldinfo(fieldname = "g_qfrq", precision = 23, scale = 3, caption = "g_qfrq", datetype = Types.TIMESTAMP)
	public CField g_qfrq; // 签发日期sign_date
	@CFieldinfo(fieldname = "g_sxq", precision = 23, scale = 3, caption = "g_sxq", datetype = Types.TIMESTAMP)
	public CField g_sxq; // 签发到期expired_date
	@CFieldinfo(fieldname = "zhiji", precision = 6, scale = 0, caption = "zhiji", datetype = Types.CHAR)
	public CField zhiji; // --职级
	@CFieldinfo(fieldname = "xueli", notnull = true, precision = 10, scale = 0, caption = "xueli", datetype = Types.INTEGER)
	public CField xueli; // --学历degree
	@CFieldinfo(fieldname = "g_zy", notnull = true, precision = 50, scale = 0, caption = "g_zy", datetype = Types.VARCHAR)
	public CField g_zy; // 专业major
	@CFieldinfo(fieldname = "G_cqlb", precision = 60, scale = 0, caption = "G_cqlb", datetype = Types.VARCHAR)
	public CField G_cqlb; // 出勤类别atdtype
	@CFieldinfo(fieldname = "isoffjob", notnull = true, precision = 10, scale = 0, caption = "isoffjob", datetype = Types.INTEGER)
	public CField isoffjob; // z.cy=1 THEN 1 ELSE 0 END AS isoffjob,--脱产非脱产
	@CFieldinfo(fieldname = "typeName", precision = 6, scale = 0, caption = "typeName", datetype = Types.VARCHAR)
	public CField typeName; // when z.cy=1 then '脱产' when z.cy=0 then '非脱产' end typeName,--脱产
	@CFieldinfo(fieldname = "g_lxdh", precision = 50, scale = 0, caption = "g_lxdh", datetype = Types.VARCHAR)
	public CField g_lxdh; // 联系电话telphone
	@CFieldinfo(fieldname = "g_jjllr", notnull = true, precision = 30, scale = 0, caption = "g_jjllr", datetype = Types.VARCHAR)
	public CField g_jjllr; // 紧急联系人urgencycontact
	@CFieldinfo(fieldname = "g_jjlldh", notnull = true, precision = 30, scale = 0, caption = "g_jjlldh", datetype = Types.VARCHAR)
	public CField g_jjlldh; // 紧急联系电话cellphone
	@CFieldinfo(fieldname = "ospcode", precision = 255, scale = 0, caption = "ospcode", datetype = Types.NVARCHAR)
	public CField ospcode; // --职务编码
	@CFieldinfo(fieldname = "zhiwu", precision = 255, scale = 0, caption = "zhiwu", datetype = Types.NVARCHAR)
	public CField zhiwu; // --同鑫职务编码
	@CFieldinfo(fieldname = "yuZhiWu", precision = 50, scale = 0, caption = "yuZhiWu", datetype = Types.CHAR)
	public CField yuZhiWu; // -职务名称
	@CFieldinfo(fieldname = "pydate", notnull = true, precision = 23, scale = 3, caption = "pydate", datetype = Types.TIMESTAMP)
	public CField pydate; // 入职时间hiredday
	@CFieldinfo(fieldname = "state", notnull = true, precision = 3, scale = 0, caption = "state", datetype = Types.TINYINT)
	public CField state; // 员工在职状态
	@CFieldinfo(fieldname = "roomBed", notnull = true, precision = 16, scale = 0, caption = "roomBed", datetype = Types.VARCHAR)
	public CField roomBed; // 住宿床位号dorm_bed
	@CFieldinfo(fieldname = "g_pqjg", notnull = true, precision = 10, scale = 0, caption = "g_pqjg", datetype = Types.INTEGER)
	public CField g_pqjg; // z.g_pqjg='新宝' then 1 when z.g_pqjg='锐旗(新宝自招)' then 2 when z.g_pqjg='同烜(新宝自招)' then 3 when z.g_pqjg='同烜输送' then 4 when
							// z.g_pqjg='特思德(新宝自招)' then 5 when z.g_pqjg='志高公司' then 6
							// when z.g_pqjg='电机派遣' then 7 else 1 end as g_pqjg ,--派遣机构dispunit
	@CFieldinfo(fieldname = "G_ZPRY", precision = 10, scale = 0, caption = "G_ZPRY", datetype = Types.VARCHAR)
	public CField G_ZPRY; // 招聘员工号
	@CFieldinfo(fieldname = "G_ZPRYXM", precision = 20, scale = 0, caption = "G_ZPRYXM", datetype = Types.VARCHAR)
	public CField G_ZPRYXM; // 招聘员姓名
	@CFieldinfo(fieldname = "G_lsgjg", notnull = true, precision = 20, scale = 0, caption = "G_lsgjg", datetype = Types.VARCHAR)
	public CField G_lsgjg; // 输送机构transorg
	@CFieldinfo(fieldname = "G_lwhz", notnull = true, precision = 23, scale = 3, caption = "G_lwhz", datetype = Types.TIMESTAMP)
	public CField G_lwhz; // 输送期限transextime
	@CFieldinfo(fieldname = "jdate", precision = 23, scale = 3, caption = "jdate", datetype = Types.TIMESTAMP)
	public CField jdate; // 最后操作时间
	public String SqlWhere; // 查询附加条件
	public int MaxCount; // 查询最大数量

	// 自关联数据定义

	public View_TxZlEmployee_zp() throws Exception {
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
