/**
 * Created by Administrator on 2018/12/8.
 */

//查询表单薪资变动明细
function showsalarydetail(tp, id,callback) {
    var url = _serUrl + "/web/hr/sa/get_salary_chgblill.co?tp=" + tp + "&id=" + id;
    $ajaxjsonget(url, function (jsdata) {
        console.log(JSON.stringify(jsdata));
        if (parseInt(jsdata.accessed) == 2) {//没有权限
            $("#salary_div").hide();
            return;
        }
        $("#salary_div").show();
        var chgbill = jsdata.chgbill;
        selected_salary_structure = jsdata.newstruc;
        var mdata = mainline.getMainData();
        mdata.scatype = chgbill.scatype;
        mdata.stype = chgbill.stype;
        mdata.oldstru_id = chgbill.oldstru_id;
        mdata.oldstru_name = chgbill.oldstru_name;
        mdata.oldchecklev = chgbill.oldchecklev;
        mdata.oldattendtype = chgbill.oldattendtype;
        mdata.oldcalsalarytype = chgbill.oldcalsalarytype;
        mdata.oldposition_salary = chgbill.oldposition_salary; // 调薪前职位工资
        mdata.oldbase_salary = chgbill.oldbase_salary; // 调薪前基本工资
        mdata.oldtech_salary = chgbill.oldtech_salary; // 调薪前技能工资
        mdata.oldachi_salary = chgbill.oldachi_salary; // 调薪前绩效工资
        mdata.oldotwage = chgbill.oldotwage; // 调薪前固定加班工资
        mdata.oldtech_allowance = chgbill.oldtech_allowance; // 调薪前技术津贴
        mdata.oldparttimesubs = chgbill.oldparttimesubs; // 调薪前兼职津贴
        mdata.oldpostsubs = chgbill.oldpostsubs; // 调薪前岗位津贴
        mdata.oldavg_salary = chgbill.oldavg_salary; // 调薪前平均工资
        mdata.newstru_id = chgbill.newstru_id; // 调薪后工资结构ID
        mdata.newstru_name = chgbill.newstru_name; // 调薪后工资结构名
        mdata.newchecklev = chgbill.newchecklev; // 调薪后绩效考核层级
        mdata.newattendtype = chgbill.newattendtype; // 调薪后出勤类别
        mdata.newcalsalarytype = chgbill.newcalsalarytype; // 调薪后计薪方式
        mdata.newposition_salary = chgbill.newposition_salary; // 调薪后职位工资
        mdata.newbase_salary = chgbill.newbase_salary; // 调薪后基本工资
        mdata.newtech_salary = chgbill.newtech_salary; // 调薪后技能工资
        mdata.newachi_salary = chgbill.newachi_salary; // 调薪后绩效工资
        mdata.newotwage = chgbill.newotwage; // 调薪后固定加班工资
        mdata.newtech_allowance = chgbill.newtech_allowance; // 调薪后技术津贴
        mdata.newparttimesubs = chgbill.newparttimesubs; // 调薪后兼职津贴
        mdata.newpostsubs = chgbill.newpostsubs; // 调薪后岗位津贴
        mdata.sacrage = chgbill.sacrage; // 调薪幅度
        mdata.chgdate = chgbill.chgdate; // 调薪日期
        mdata.chgreason = chgbill.chgreason; // 调薪原因
        mainline.setJson2Inputs();
        if (callback)
            callback(jsdata);
    }, function (err) {
        alert(JSON.stringify(err));
    });
}

function getOldSaralyInfo(er_id, callback) {
    var url = _serUrl + "/web/hr/sa/getCur_salary_chglg.co?er_id=" + er_id;
    $ajaxjsonget(url, function (jsdata) {
        if (parseInt(jsdata.accessed) == 2) {//没有权限
            $("#salary_div").hide();
            return;
        }
        $("#salary_div").show();
        mainline.setisloadingdata(true);
        mainline.setFieldValue('oldstru_id', jsdata.newstru_id);
        mainline.setFieldValue('oldstru_name', jsdata.newstru_name);
        mainline.setFieldValue('oldchecklev', jsdata.newchecklev);
        mainline.setFieldValue('oldattendtype', jsdata.newattendtype);
        mainline.setFieldValue('oldposition_salary',(isNaN(jsdata.newposition_salary))?0: parseFloat(jsdata.newposition_salary).toFixed(2));
        mainline.setFieldValue('oldbase_salary',(isNaN(jsdata.newbase_salary))?0:  parseFloat(jsdata.newbase_salary).toFixed(2));
        mainline.setFieldValue('oldotwage',(isNaN(jsdata.newotwage))?0:  parseFloat(jsdata.newotwage).toFixed(2));
        mainline.setFieldValue('oldtech_salary',(isNaN(jsdata.newtech_salary))?0:  parseFloat(jsdata.newtech_salary).toFixed(2));
        mainline.setFieldValue('oldachi_salary',(isNaN(jsdata.newachi_salary))?0:  parseFloat(jsdata.newachi_salary).toFixed(2));
        mainline.setFieldValue('oldtech_allowance',(isNaN(jsdata.newtech_allowance))?0: parseFloat(jsdata.newtech_allowance).toFixed(2));
		mainline.setFieldValue('oldparttimesubs',(isNaN(jsdata.newparttimesubs))?0: parseFloat(jsdata.newparttimesubs).toFixed(2));
		mainline.setFieldValue('oldpostsubs',(isNaN(jsdata.newpostsubs))?0:  parseFloat(jsdata.newpostsubs).toFixed(2));

        mainline.setFieldValue('newstru_id', jsdata.newstru_id);
        mainline.setFieldValue('newstru_name', jsdata.newstru_name);
        mainline.setFieldValue('newchecklev', jsdata.newchecklev);
        mainline.setFieldValue('newattendtype', jsdata.newattendtype);
        mainline.setFieldValue('newposition_salary', parseFloat(jsdata.newposition_salary).toFixed(2));
        mainline.setFieldValue('newbase_salary', parseFloat(jsdata.newbase_salary).toFixed(2));
        mainline.setFieldValue('newotwage', parseFloat(jsdata.newotwage).toFixed(2));
        mainline.setFieldValue('newtech_salary', parseFloat(jsdata.newtech_salary).toFixed(2));
        mainline.setFieldValue('newachi_salary', parseFloat(jsdata.newachi_salary).toFixed(2));
        mainline.setFieldValue('newtech_allowance',(isNaN(jsdata.newtech_allowance))?0: parseFloat(jsdata.newtech_allowance).toFixed(2));
		mainline.setFieldValue('newparttimesubs', (isNaN(jsdata.newparttimesubs))?0: parseFloat(jsdata.newparttimesubs).toFixed(2));
		mainline.setFieldValue('newpostsubs',(isNaN(jsdata.newpostsubs))?0:  parseFloat(jsdata.newpostsubs).toFixed(2));
        mainline.setisloadingdata(false);
        if (callback)
            callback(jsdata);
    }, function (err) {
        alert(JSON.stringify(err));
    });
}

function $RndNum(n) {
    var rnd = "";
    for (var i = 0; i < n; i++)
        rnd += Math.floor(Math.random() * 10);
    return rnd;
}