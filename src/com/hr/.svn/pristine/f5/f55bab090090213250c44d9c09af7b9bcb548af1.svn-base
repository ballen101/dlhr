package com.hr.perm.ctr;

import com.corsair.dbpool.CDBConnection;
import com.hr.base.entity.Hr_standposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_transfer;

/**
 * 调动辅助类
 * 
 * 调动单据 ①调动前为正式状态，调动后根据调动单的填写确认调动后的状态。调动后若有考察期则状态变成考察，若无考察期则调动后为正式状态。
 * ②调动前为考察状态，调动后根据调动单的填写确认调动后的状态。调动后若有考察期则前一次考察期结束，开展新的考察期，人事状态为考察；若无考察期则原考察期结束，调动后为正式状态。
 * ③调动前为试用状态，若调动前与调动后职位一致，调动后状态仍为试用；调动前与调动后职位发生变化，调动后若有考察期则试用期结束，状态变成考察，若无考察期则调动后仍为试用。
 * 
 * 年度组织架构调整 使用机构合并拆分功能时，调动前与调动后人事状态保持一致。 调动后，若填写调动单，则按调动单规则决定调动后人事状态。
 * 调动后，若填写转正单，则按转正单规则决定调动后人事状态。
 * 
 * 
 * @author shangwen
 * 
 */
public class CtrHr_employee_transferUtil {

    /**
     * @param ep
     * @param emp
     * @return 返回是否需要生成考察期列表 1生成 2 不生成
     * @throws Exception
     */
    public static int setEmpState(CDBConnection con, Hr_employee_transfer ep, Hr_employee emp) throws Exception {
	Hr_standposition osop = new Hr_standposition();
	Hr_standposition nsop = new Hr_standposition();
	String sqlstr = "SELECT s.* FROM hr_standposition s,hr_orgposition o WHERE s.sp_id=o.sp_id AND o.ospid=%s";
	osop.findBySQL(con, String.format(sqlstr, ep.odospid.getValue()), false);
	nsop.findBySQL(con, String.format(sqlstr, ep.newospid.getValue()), false);
	if (osop.isEmpty())
	    throw new Exception("调动前标准职位不存在");
	if (nsop.isEmpty())
	    throw new Exception("调动后标准职位不存在");
	boolean isSameoption = (osop.sp_id.getAsInt() == nsop.sp_id.getAsInt());// 相同标准职位

	int rst = 2;
	int attribute1 = ep.attribute1.getAsIntDefault(0);
	if (attribute1 != 1) {
	    int empstatid = emp.empstatid.getAsIntDefault(0);// 2 试用 3考察 4 正式
	    if (empstatid == 4) {
		if (ep.probation.getAsInt() == 0) {// 无考察期 1
		    ep.ispromotioned.setAsInt(1);
		} else {
		    emp.empstatid.setAsInt(3);
		    rst = 1;
		}
	    }
	    if (empstatid == 2) {// 试用
		if (isSameoption) {// 调动前为试用状态，若调动前与调动后职位一致，调动后状态仍为试用?
				   // 若设置了考察期呢？？？？？？？？？？？？？？

		} else {
		    if (ep.probation.getAsInt() == 0) {// 无考察期 1 无考察期则调动后仍为试用
			ep.ispromotioned.setAsInt(1);
		    } else {
			emp.empstatid.setAsInt(3);// 有考察期则试用期结束，状态变成考察
			endPerShiyong(con, ep, emp);// 结束之前试用期
			rst = 1;
		    }
		}
	    }
	    if (empstatid == 3) {
		if (ep.probation.getAsInt() == 0) {// 无考察期 1
		    ep.ispromotioned.setAsInt(1);
		} else {
		    emp.empstatid.setAsInt(3);
		    rst = 1;
		}
		endPerKaocha(con, ep, emp);// 结束之前考察期
	    }
	}
	return rst;
    }

    /**
     * 结束所有试用期
     * 
     * @param emp
     * @throws Exception
     */
    private static void endPerShiyong(CDBConnection con, Hr_employee_transfer ep, Hr_employee emp) throws Exception {
	String sqlstr = "UPDATE hr_entry_try SET trystat=4,promotiondaytrue=IF(promotiondaytrue=NULL,NOW(),promotiondaytrue),"
		+ "delaypromotiondaytrue=IF(delaypromotiondaytrue=NULL,NOW(),delaypromotiondaytrue)," // 修改试用期列表
		+ "attribute1='调动自动转正" + ep.emptranf_id.getValue() + "' WHERE  trystat<>4 AND er_id=" + ep.er_id.getValue();
	con.execsql(sqlstr);
	sqlstr = "UPDATE hr_entry SET ispromotioned=1 ,promotionday=NOW(),attribute1='调动自动转正" + ep.emptranf_id.getValue() + "'  WHERE er_id="
		+ ep.er_id.getValue() + " AND ispromotioned<>1";
	con.execsql(sqlstr);
    }

    /**
     * 结束所有考察期
     * 
     * @param emp
     * @throws Exception
     */
    private static void endPerKaocha(CDBConnection con, Hr_employee_transfer ep, Hr_employee emp) throws Exception {
	String sqlstr = "UPDATE hr_transfer_try SET trystat=4,probationdatetrue=IF(probationdatetrue=NULL,NOW(),probationdatetrue),"
		+ "delaypromotiondaytrue=IF(delaypromotiondaytrue=NULL,NOW(),delaypromotiondaytrue) ," + "attribute1='调动自动转正" + ep.emptranf_id.getValue()
		+ "'  WHERE trystat<>4 AND er_id=" + ep.er_id.getValue();
	con.execsql(sqlstr);
	sqlstr = "UPDATE hr_employee_transfer SET ispromotioned=1,attribute2='调动自动转正" + ep.emptranf_id.getValue() + "' WHERE  ispromotioned<>1 AND er_id="
		+ ep.er_id.getValue() + " AND emptranf_id<>" + ep.emptranf_id.getValue();
	con.execsql(sqlstr);
    }

}
