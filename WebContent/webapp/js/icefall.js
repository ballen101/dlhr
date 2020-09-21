/**
 * Created by Administrator on 2014-09-16.
 */


easyloader.locale = 'zh_CN';

var _contextPath = undefined;
var _serUrl = getURLStr();
var _notshowajaxprocform = false;//全局禁止显示加载进度框

/*
 *将以下三种格式字符串转成日期
 yyyy-MM-dd hh:mm:ss
 yyyy-MM-dd
 yyy-MM
 */
function $getDate4Str(str) {
    if (!str) return new Date();
    if ((typeof str) == "object")
        return new Date();
    if (str.indexOf(":") < 0) {
        str = str + " 00:00:00";
    }
    var rst = new Date();
    var ymdhms = str.split(" ");
    var ymd = ymdhms[0].split("-");
    var hms = ymdhms[1].split(":");
    if (!ymd[2]) ymd[2] = '01';
    rst.setFullYear(ymd[0], ymd[1] - 1, ymd[2]);
    if (!hms[1]) hms[1] = "00";
    if (!hms[2]) hms[2] = "00";
    rst.setHours(hms[0], hms[1], hms[2]);
    return rst;
}

function getURLStr() {
    var contextPath = document.location.pathname;
    var index = contextPath.substr(1).indexOf("/");
    contextPath = contextPath.substr(0, index + 1);
    _contextPath = contextPath.substr(1, index);
    return document.location.protocol + "//" + location.host + contextPath;
}

if (typeof JSON == 'undefined') {
    $('head').append($("<script type='text/javascript' src='" + _serUrl + "/webapp/js/json2.js'>"));
}


Array.prototype.indexOf = function (e) {
    for (var i = 0, j; j = this[i]; i++) {
        if (j == e) {
            return i;
        }
    }
    return -1;
};

Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    };

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
};

/*
 *将以下三种格式字符串转成日期
 yyyy-MM-dd hh:mm:ss
 yyyy-MM-dd
 yyy-MM
 */
Date.prototype.fromStr = function (str) {
    if (!str) return new Date();
    if ((typeof str) == "object")
        return new Date();
    if (str.indexOf(":") < 0) {
        str = str + " 00:00:00";
    }
    var ymdhms = str.split(" ");
    var ymd = ymdhms[0].split("-");
    var hms = ymdhms[1].split(":");
    if (!ymd[2]) ymd[2] = '01';
    this.setFullYear(ymd[0], ymd[1] - 1, ymd[2]);
    if (!hms[1]) hms[1] = "00";
    if (!hms[2]) hms[2] = "00";
    this.setHours(hms[0], hms[1], hms[2]);
    return this;
};

Date.prototype.toUIDatestr = function () {
    //return this.format("MM/dd/yyyy hh:mm:ss");
    return this.format("yyyy-MM-dd hh:mm:ss");
};

Date.prototype.addMonth = function (month) {
    var y = this.getFullYear();
    var m = this.getMonth();
    m = m + month;
    y = y + Math.floor(m / 12);
    m = m % 12;
    this.setFullYear(y);
    this.setMonth(m);
    return this;
};

Array.prototype.insert = function (index, item) {
    this.splice(index, 0, item);
};

function $getLastDay(year, month) {
    var new_year = year;    //取当前的年份
    var new_month = month++;//取下一个月的第一天，方便计算（最后一天不固定）
    if (month > 12) {          //如果当前大于12月，则年份转到下一年
        new_month -= 12;        //月份减
        new_year++;            //年份增
    }
    var new_date = new Date(new_year, new_month, 1);                //取当年当月中的第一天
    return (new Date(new_date.getTime() - 1000 * 60 * 60 * 24)).getDate();//获取当月最后一天日期
}

function $date4str(s) {
    return new Date().fromStr(s);
}

function $getDateNotKnow(v) {
    if (!v)
        return undefined;
    if (v instanceof Date)
        return v;
    if ((typeof v == 'string'))
        return $date4str(v);
    return v;
}

function $dateformattostr(dt) {
    dt = $getDateNotKnow(dt);
    if ((!dt) || (!(dt instanceof Date))) return undefined;
    return dt.format("yyyy-MM-dd hh:mm:ss");//window.cancel_proc();
}

function $dateformatorYYYY_MM(dt) {
    dt = $getDateNotKnow(dt);
    if ((!dt) || (!(dt instanceof Date))) return undefined;
    var rst = dt.format("yyyy-MM");
    return rst;
}

function $dateformattostrrYYYY_MM_DD(dt) {
    dt = $getDateNotKnow(dt);
    if ((!dt) || (!(dt instanceof Date))) return undefined;
    return dt.format("yyyy-MM-dd");
}

function $dateformattostrrYYYY_MM(dt) {
    dt = $getDateNotKnow(dt);
    if ((!dt) || (!(dt instanceof Date))) return undefined;
    var rst = dt.format("yyyy-MM");
    return rst;
}

function $dateformattostrrYYYY_MM_DD_HH_MM(dt) {
    dt = $getDateNotKnow(dt);
    if ((!dt) || (!(dt instanceof Date))) return undefined;
    var rst = dt.format("yyyy-MM-dd hh:mm");
    return rst;
}

function $fieldDateFormatorYYYY_MM(value, row, index) {
    if ((!value) || (value.length == 0) || ((typeof str) == "object"))
        return value;
    var dt = (new Date()).fromStr(value);
    return $dateformatorYYYY_MM(dt);
}

function $fieldDateFormatorYYYY_MM_DD(value, row, index) {
    if ((!value) || (value.length == 0) || ((typeof str) == "object"))
        return value;
    var dt = (new Date()).fromStr(value);
    return $dateformattostrrYYYY_MM_DD(dt);
}

function $fieldDateFormatorYYYY_MM_DD_HH_MM(value, row, index) {
    if ((!value) || (value.length == 0) || ((typeof str) == "object"))
        return value;
    var dt = (new Date()).fromStr(value);
    return $dateformattostrrYYYY_MM_DD_HH_MM(dt);
}

function $fieldDateFormator(value, row, index) {
    if ((!value) || (value.length == 0) || ((typeof str) == "object"))
        return value;
    var dt = (new Date()).fromStr(value);
    return $dateformattostr(dt);
}

function $moneyFormatorWithY(value, row, index) {
    var v = parseFloat(value);
    if (isNaN(v))
        return "";
    return "￥" + v.toFixed(2);
}

function $moneyFormator(value, row, index) {
    var v = parseFloat(value);
    if (isNaN(v))
        return "";
    return v.toFixed(2);
}

//数组是否存在某个元素
Array.prototype.contains = function (arr) {
    for (var i = 0; i < this.length; i++) {//this指向真正调用这个方法的对象
        if ((this[i] + "") == (arr + "")) {
            return true;
        }
    }
    return false;
};

//根据ComURL，指定值
function $getNewComboxParmsByComUrl(comurl, values) {
    var rst = {
        valueField: comurl.valueField,
        textField: comurl.textField
    };
    var data = [];
    var oddata = comurl.jsondata;
    for (var i = 0; i < oddata.length; i++) {
        var odrow = oddata[i];
        if (values.contains(odrow[comurl.valueField])) {
            var nrow = {};
            nrow[comurl.textField] = odrow[comurl.textField];
            nrow[comurl.valueField] = odrow[comurl.valueField];
            data.push(nrow);
        }
    }
    rst.data = data;
    return rst;
}


function $isEmpty(parm) {
    return ((parm == undefined) || (parm == null) || (parm.length == 0));
}

var $urlparms = $getPageParms();

function $getPageParms() {
    qs = document.location.search.split("+").join(" ");
	if(qs.indexOf("&edittps=")>0){
		qs=qs.substring(0,qs.indexOf("&edittps="));
	}
    var params = {}, tokens,
        re = /[?&]?([^=]+)=([^&]*)/g;
    while (tokens = re.exec(qs)) {
        params[decodeURIComponent(tokens[1])] = decodeURIComponent(tokens[2]);
    }
    return params;
}
function delParam(url, paramKey) {
    var urlParam = url.substr(url.indexOf("?") + 1);
    var beforeUrl = url.substr(0, url.indexOf("?"));
    var nextUrl = "";

    var arr = new Array();
    if (urlParam != "") {
        var urlParamArr = urlParam.split("&");

        for (var i = 0; i < urlParamArr.length; i++) {
            var paramArr = urlParamArr[i].split("=");
            if (paramArr[0] != paramKey) {
                arr.push(urlParamArr[i]);
            }
        }
    }
    if (arr.length > 0) {
        nextUrl = "?" + arr.join("&");
    }
    url = beforeUrl + nextUrl;
    return url;
}


// 将分钟数量转换为小时和分钟字符串
function $toHourMinute(minutes) {
    return (Math.floor(minutes / 60) + ":" + (minutes % 60));
    // 也可以转换为json，以方便外部使用
    // return {hour:Math.floor(minutes/60),minute:(minutes%60)};
}

function $toHourMinuteJSON(minutes) {
    return {hour: Math.floor(minutes / 60), minute: (minutes % 60)};
}

function $toMinute(hm) {
    if (!hm) return 0;
    var idx = hm.indexOf(":");
    if (idx < 0) return 0;
    var ext = parseInt(hm.substr(0, idx)) * 60 + parseInt(hm.substr(idx + 1, hm.length - 1));
    if (!isNaN(ext))
        return ext;
    else return 0;
}
//删除comdic 词汇表 值为 value 的条目
function $removeDicValue(comdic, value) {
    var jsondata = comdic.jsondata;
    var type = comdic.type;
    if (type != 'combobox') {
        alert("只能删除combobox的值");
        return;
    }
    var valueField = comdic.valueField;
    for (var i = 0; i < jsondata.length; i++) {
        if (jsondata[i][valueField] == value) {
            jsondata.splice(i, 1);
            return;
        }
    }
}

function showWaitForm() {
    var allppwids = "<div id='all_ppw_id'></div>";
    var allppwid = window.top.$("#all_ppw_id");
    if (!allppwid[0]) {
        $(allppwids).appendTo(window.top.document.body);
        allppwid = window.top.$("#all_ppw_id");
    }


    var wpws = "<div id='wait_pw_id' class='easyui-window' title=''"
        + "     data-options='modal:true,closed:true,border:false,collapsible:false,minimizable:false,closable:false,maximizable:false,resizable:false'"
        + "     style='width:250px;height:90px;padding: 0px;overflow: hidden'>"
        + "    <div style='width: 100%;height: 100%; border-top:1px solid #95B8E7;vertical-align:middle;text-align: center;padding: 5px;'>"
        + "        <div class='icon-wait'"
        + "             style='position:relative; width: 80%;height: 100%;text-align: center;text-align: center;"
        + "         line-height:80px;margin: 0 auto '>"
        + "            载入数据，稍候......"
        + "        </div>"
        + "    </div>"
        + "    <div></div>"
        + " </div>";
    var pp = window.top.$("#wait_pw_id");
    if (!pp[0]) {
        $(wpws).appendTo(allppwid);
        pp = window.top.$("#wait_pw_id");
        window.top.$.parser.parse(pp.parent());
    }
    pp.window("open");
}

function closeWaitForm() {
    var pp = window.top.$("#wait_pw_id");
    if (pp[0])
        pp.window("close");
}

function onreturnjsondata(data, sucprc, userdata, errprc) {
    var allppwids = "<div id='all_ppw_id'></div>";
    var allppwid = window.top.$("#all_ppw_id");
    if (!allppwid[0]) {
        $(allppwids).appendTo(window.top.document.body);
        allppwid = window.top.$("#all_ppw_id");
    }

    var logins = "<div id='login_window' class='easyui-window' title='登录'"
        + "data-options='modal:true,closed:true,border:false,collapsible:false,minimizable:false'"
        + "style='width:300px;height:250px;padding: 0px'>"
        + "    <iframe scrolling='no' frameborder='0' src='" + _serUrl + "/webapp/common/login.html" + "' style='width: 100%;height: 98%'></iframe>"
        + "</div>";
    if (data.errmsg != undefined) {
        if (data.errmsg.indexOf("未登录用户不允许执行") >= 0) {
            var token = $getPageParms().token;
            //alert(token);
            if (!$isEmpty(token)) {
                var url = _serUrl + "/web/login/loginByToken.co?token=" + token;
                $ajaxjsonget(url, function (jsdata) {
                    //alert(JSON.stringify(jsdata));
                    var nurl = window.location.href;
                    nurl = delParam(nurl, "token");
                    window.location = nurl;
                }, function (err) {
                    $.messager.alert('错误', err, 'error');
                });
            } else {
                $.messager.alert('错误', '您还未登录或登录已经过期，请重新登录!', 'error', function () {
                    if ((window.top._loginURL != undefined) && (window.top._loginURL.length > 0)) {
                        window.top.location = window.top._loginURL;
                    } else {
                        var pp = window.top.$("#login_window");
                        if (!pp[0]) {
                            $(logins).appendTo(allppwid);
                            pp = window.top.$("#login_window");
                            window.top.$.parser.parse(pp.parent());
                        }
                        pp.window("open");
                    }
                });
            }
        } else {
            if (errprc)
                errprc(data);
            else
                $.messager.alert('错误', data.errmsg, 'error');
        }
    } else {
        sucprc(data, userdata);
    }
}

function $ajaxjsonpost(url, data, sucprc, errprc, ayc, userdata, noshowprc) {
    if ((!noshowprc) && (!_notshowajaxprocform))
        showWaitForm();
    if (ayc == undefined)
        ayc = true;
    $.ajax(
        {
            url: url,
            type: 'post',
            dataType: 'json',
            data: data,
            cache: false,
            async: ayc,
            contentType: "application/json;  charset=utf-8",
            success: function (jsondata) {
                if (!noshowprc)
                    closeWaitForm();
                onreturnjsondata(jsondata, sucprc, userdata, errprc);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                closeWaitForm();
                errprc(XMLHttpRequest, textStatus, errorThrown);
            }
        }
    )
}

function $ajaxjsonget(url, sucprc, errprc, ayc, userdata, noshowprc) {
    if ((!noshowprc) && (!_notshowajaxprocform))
        showWaitForm();
    if (ayc == undefined)
        ayc = true;
    $.ajax(
        {
            url: url,
            type: 'get',
            dataType: 'json',
            cache: false,
            async: ayc,
            contentType: "application/json; charset=utf-8",
            success: function (jsondata) {
                if (!noshowprc)
                    closeWaitForm();
                onreturnjsondata(jsondata, sucprc, userdata, errprc);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (!noshowprc)
                    closeWaitForm();
                errprc(XMLHttpRequest, textStatus, errorThrown);
            }
        }
    )
}


function $ajaxhtmlget(url) {
    var rst = undefined;
    $.ajax({
        url: url,
        type: 'get',
        dataType: 'text',
        cache: false,
        async: false,
        contentType: "text/HTML; charset=utf-8",
        success: function (data) {
            rst = data;
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $.messager.alert('错误', '获取模板文件错误!', 'error');
        }
    });
    return rst;
}

function $getHtmlFragment(filter) {
    var s = "<div>" + $ajaxhtmlget("../templet/default/html_gragments.html") + "</div>";
    return $(s).find(filter).html();
}

function $generateUUID() {
    var d = new Date().getTime();
    var uuid = 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return uuid;
}

function getservdate() {
    var dt;
    $ajaxjsonget(_serUrl + "/web/common/getserverdate.co", function (jsondata) {
        dt = jsondata.date;
        return dt;
    }, function () {
        dt = undefined;
        alert("错误");
    }, false);
    return dt;
}

function getnewid(tbname) {
    var dt;
    $ajaxjsonget(_serUrl + "/web/common/getnewid.co?tbname=" + tbname, function (jsondata) {
        dt = jsondata.newid;
        return dt;
    }, function () {
        dt = undefined;
        alert("错误");
    }, false);
    return dt;
}


function $getDBTime() {
    var func = arguments[0];
    if ((typeof func) == "function") {
        $ajaxjsonget(_serUrl + "/web/common/getDBDatetime.co", function (jsondata) {
            var dt = (new Date()).fromStr(jsondata.rst);
            func(dt);
        }, function () {
            dt = undefined;
            alert("错误");
        });
    } else {
        var dt = undefined;
        $ajaxjsonget(_serUrl + "/web/common/getDBDatetime.co", function (jsondata) {
            dt = (new Date()).fromStr(jsondata.rst);
            return dt;
        }, function () {
            dt = undefined;
            alert("错误");
        }, false);
        return dt;
    }
}


/*
 type: tree,grid,list
 data:
 title:
 msg:
 afterLoadData:
 onOK:
 onCancel:
 */
/*function SelectTreeOrList(options) {
 var pdivid = "divid_" + (new Date()).getTime();
 var devid = "select_" + (new Date()).getTime();
 var inputID = "input_" + (new Date()).getTime();
 var btid = "buttons_" + (new Date()).getTime();
 var title = (options.title) ? options.title : "选择";
 var msg = (options.msg) ? options.msg : "选择数据";
 var stype = options.type;
 var sipt = undefined;
 if (stype == "tree") {
 sipt = "<input id='" + inputID + "' class='easyui-combotree' style='width:100%;'/>";
 }
 if (stype == "list") { //需要id field value field
 sipt = "<input id='" + inputID + "' class='easyui-combobox' style='width:100%;'/>";
 }
 if (stype == "grid") {//需要id field value field  columns
 sipt = "<input id='" + inputID + "' class='easyui-combogrid' style='width:100%;'/>";
 }
 var selectOrgUser_str = "<div id='" + pdivid + "'><div id='" + devid + "' class='easyui-dialog' title='" + title + "' style='width:300px;height:150px;padding:10px' data-options=\"modal:true,closed:true,iconCls: 'icon-search',buttons: '#" + btid + "'\">"
 + "<div style='margin: 5px auto'>" + msg + "</div>" + sipt
 + "<div id='" + btid + "'><a  class='easyui-linkbutton' style='width: 80px'>确定</a><a class='easyui-linkbutton' style='width: 80px'>取消</a></div></div></div>";
 this.options = options;
 this.so = function () {
 return so;
 }

 var devobj = $(selectOrgUser_str);
 //$.parser.auto = false;
 devobj.appendTo("body");
 //$.parser.parse("#" + pdivid);
 var so = $("#" + devid)
 so.find(".easyui-linkbutton:eq(0)")[0].onclick = function () {
 var txtobj = $("#" + inputID);
 var data = undefined;
 if (stype == "tree") {
 var v = txtobj.combotree("getValue");
 if ((v == null) || (v == undefined) || (v == '')) {
 $.messager.alert('错误', '没有选择的数据', 'error');
 return;
 }
 data = txtobj.combotree("tree").tree("getSelected");
 }
 if (stype == "list") {
 var v = txtobj.combobox("getValue");
 if ((v == null) || (v == undefined) || (v == '')) {
 $.messager.alert('错误', '没有选择的数据', 'error');
 return;
 }
 var dt = txtobj.combobox("getData");
 for (var i = 0; i < dt.length; i++) {
 if (dt[i][options.valueField] == v) {
 data = dt[i];
 break;
 }
 }
 }
 if (options.onOk) {
 options.onOk(data);
 }
 so.dialog("close");
 };
 so.find(".easyui-linkbutton:eq(1)")[0].onclick = function () {
 if (options.onCancel) {
 options.onCancel();
 }
 so.dialog("close");
 };

 //$.parser.onComplete = initData;
 function initData() {
 var data = options.data;
 if (data) {
 if (stype == "tree") {
 $C.tree.setTree1OpendOtherClosed(data);//输展开第一层
 alert("1111111111111");
 $("#" + inputID).combotree("loadData", data);
 alert("2222222222");
 }
 if (stype == "list") {
 $("#" + inputID).combobox({valueField: options.valueField, textField: options.textField, data: data});
 }
 } else {
 alert("没有数据");
 }
 }

 this.pop = function (NsfrmOptions) {
 if (NsfrmOptions != undefined)
 options = $.extend({}, options, NsfrmOptions);
 initData();
 so.dialog("open");
 }
 }*/

function $accMul(arg1, arg2)//乘法
{
    var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
    try {
        m += s1.split(".")[1].length
    } catch (e) {
    }
    try {
        m += s2.split(".")[1].length
    } catch (e) {
    }
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m)
}

function $accAdd(arg1, arg2) {//加法
    var r1, r2, m;
    try {
        r1 = arg1.toString().split(".")[1].length;
    } catch (e) {
        r1 = 0;
    }
    try {
        r2 = arg2.toString().split(".")[1].length;
    } catch (e) {
        r2 = 0;
    }
    m = Math.pow(10, Math.max(r1, r2));
    return (arg1 * m + arg2 * m) / m;
}

function $accDiv(arg1, arg2) {//除法
    var t1 = 0, t2 = 0, r1, r2;
    try {
        t1 = arg1.toString().split(".")[1].length;
    } catch (e) {
    }
    try {
        t2 = arg2.toString().split(".")[1].length;
    } catch (e) {
    }
    with (Math) {
        r1 = Number(arg1.toString().replace(".", ""));
        r2 = Number(arg2.toString().replace(".", ""));
        return (r1 / r2) * pow(10, t2 - t1);
    }
}

function $accSub(arg1, arg2) {//减法
    return $accAdd(arg1, -arg2);
}


function $getTreeTextById(nodes, value) {
    if ((value == null) || (value == undefined))
        return value;
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (node.id == value)
            return node.text;
        if (node.children) {
            var txt = $getTreeTextById(node.children, value);
            if (txt != undefined) return txt;
        }
    }
    return undefined;
}

var $C = {
    action: {
        New: 1,
        New2: 9,
        Del: 2,
        Edit: 3,
        Submit: 4,
        Print: 5,
        Append: 6,
        Save: 7,
        Cancel: 8,
        Find: 10,
        Detail: 11,
        Reload: 12,
        Export: 13,
        ExportList: 19,
        Exit: 14,
        Upload: 15,
        Download: 16,
        DelAtt: 17,
        Copy: 18,
        ColsFilter: 20,
        Void: 21,
        WFSubmit: 22,
        WFReject: 23,
        WFBreak: 24,
        WFRefer: 25,
        WFUnSubmit: 26,
        ExportChart: 27,
        PrintNew: 28,
        PrintPreview: 29,
        PrintDesign: 30,
        Prev: 31,
        Next: 32
    },
    cos: {
        myworkflows: _serUrl + "/web/common/getmyworkwflist.co",
        commonfind: _serUrl + "/web/common/find.co",
        commonsave: _serUrl + "/web/common/save.co",
        commondelete: _serUrl + "/web/common/delete.co",
        commoncreateWF: _serUrl + "/web/common/createWF.co",
        commonsubmitWF: _serUrl + "/web/common/submitWF.co",
        commonrejectWF: _serUrl + "/web/common/rejectWF.co",
        commonbreakWF: _serUrl + "/web/common/breakWF.co",
        commontransferWF: _serUrl + "/web/common/transferWF.co",
        commondowloadExcelByModel: _serUrl + "/web/common/dowloadExcelByModel.co",
        commonUpLoadFile: _serUrl + "/web/common/upLoadFile.co",
        commonupLoadFileProress: _serUrl + "/web/common/upLoadFileProress.co",
        commonCopy: _serUrl + "/web/common/copy.co",
        mps: {
            importexcel: _serUrl + "/mps/bom/impexcel.co",
            bomlines: _serUrl + "/mps/bom/bomlines.co",
            bomlinesbyid: _serUrl + "/mps/bom/bomlinesbyid.co",
            bomitemr: _serUrl + "/mps/bom/bomitemr.co"
        },
        inv: {
            ledgeritem: _serUrl + "/inv/stockallocate/ledgeritem.co",
            impfromexcel: _serUrl + "/inv/stockin/impfromexcel.co",
            ledgercprice: _serUrl + "/web/inv/ledger/ledgercprice.co"
        },
        css: {
            outletquta: _serUrl + "/css/partapply/outletquta.co",
            submitsw: _serUrl + "/web/install/submitsw.co",
            submitrv: _serUrl + "/web/install/submitrv.co",
            submitdc: _serUrl + "/web/install/submitdc.co",
            submitmatsw: _serUrl + "/web/maintenance/submitmatsw.co",
            submitmatrv: _serUrl + "/web/maintenance/submitmatrv.co",
            submitmatdc: _serUrl + "/web/maintenance/submitmatdc.co",
            submitfixsw: _serUrl + "/web/fix/submitfixsw.co",
            submitfixrv: _serUrl + "/web/fix/submitfixrv.co",
            submitfixdc: _serUrl + "/web/fix/submitfixdc.co"
        },


        findAreaPath: _serUrl + "/web/shwarea/findAreaPath.co",

        getWffds: _serUrl + "/web/wf/getWffds.co",
        getWftemps: _serUrl + "/web/wf/getWftemps.co",
        getWftemp: _serUrl + "/web/wf/getWftemp.co",
        saveWfTemp: _serUrl + "/web/wf/saveWfTemp.co",


        getorgs: _serUrl + "/web/user/getorgs.co",
        delOrg: _serUrl + "/web/user/delOrg.co",
        delUser: _serUrl + "/web/user/delUser.co",
        saveRole: _serUrl + "/web/role/saveRole.co",
        delRole: _serUrl + "/web/role/delRole.co",
        findPositions: _serUrl + "/web/position/findPositions.co",
        findUsersByPosition: _serUrl + "/web/position/findUsersByPosition.co",
        savePosition: _serUrl + "/web/position/savePosition.co",
        delPosition: _serUrl + "/web/position/delPosition.co",
        getModTree: _serUrl + "/web/menu/getModTree.co",
        getModMenuTree: _serUrl + "/web/menu/getModMenuTree.co",
        saveMod: _serUrl + "/web/menu/saveMod.co",
        delMod: _serUrl + "/web/menu/delMod.co",
        saveMenu: _serUrl + "/web/menu/saveMenu.co",
        delMenu: _serUrl + "/web/menu/delMenu.co",
        getDicTree: _serUrl + "/web/dict/getDicTree.co",
        saveDict: _serUrl + "/web/dict/saveDict.co",
        delDict: _serUrl + "/web/dict/delDict.co",


        login: function () {
            return _serUrl + "/web/login/dologin.co";
        },
        loginout: function () {
            return _serUrl + "/web/login/loginout.co";
        },
        notices: function () {
            return _serUrl + "/web/common/getnotices.co";
        },
        getWfCount: function () {
            return _serUrl + "/web/wf/wfcount.co";
        },
        getattach: function () {
            return _serUrl + "/web/common/getshw_attachbyid.co";
        },
        downattfile: function () {
            return _serUrl + "/web/common/downattfile.co";
        },
        delattfile: function () {
            return _serUrl + "/web/common/delattfile.co";
        },
        userparms: function () {
            return _serUrl + "/web/user/getuserparms.co";
        },
        userlistbyorg: function () {
            return _serUrl + "/web/user/userListByOrg.co";
        },
        saveOrg: function () {
            return _serUrl + "/web/user/saveOrg.co";
        },
        saveUser: function () {
            return _serUrl + "/web/user/saveUser.co";
        },
        getmods: function () {
            return _serUrl + "/web/menu/getmods.co";
        },
        getmenusbymod: function () {
            return _serUrl + "/web/menu/getmenus.co";
        },
        getOrgsByUserID: function () {
            return _serUrl + "/web/user/getOrgsByUserID.co";
        },
        getRolesByUserID: function () {
            return _serUrl + "/web/user/getRolesByUserID.co";
        },
        getOptionsByUserID: function () {
            return _serUrl + "/web/user/getOptionsByUserID.co";
        },
        getRoles: function () {
            return _serUrl + "/web/role/getRoles.co";
        },
        getUsersByRoleid: function () {
            return _serUrl + "/web/role/getUsers.co";
        }

    },
    grid: {
        initComFormaters: function (comOptions) {
            var comUrls = comOptions.comUrls;
            if (comUrls.length == 0)
                if (comOptions.onOK) comOptions.onOK();
            for (var i = 0; i < comUrls.length; i++) {
                var comurl = comUrls[i];
                $ajaxjsonget(comurl.url, function (jsondata, comurl) {
                    if (comurl.afterGetData)
                        comurl.afterGetData(jsondata);
                    var edtor = {type: comurl.type, options: {}};
                    if (comurl.type == "combobox") {
                        edtor.options.valueField = comurl.valueField;
                        edtor.options.textField = comurl.textField;
                    }
                    if (comurl.type == "combotree") {
                        $C.tree.setTree1OpendOtherClosed(jsondata);//第一级开着 第二级后全关上
                    }
                    edtor.options.data = jsondata;
                    $C.grid.comEditors[comurl.index] = edtor;
                    comurl.data = jsondata;
                    $C.grid.comDatas[comurl.index] = comurl;
                    $C.grid.comFormaters[comurl.index] = function (value, row) {
                        if (value == "get_com_data") {
                            return jsondata;
                        }
                        if (value == "get_com_url") {
                            return comurl;
                        }
                        // alert(comurl.index + ":" + value + ":" + JSON.stringify(jsondata));
                        if (comurl.type == "combobox") {
                            for (var i = 0; i < jsondata.length; i++) {
                                if (value == jsondata[i][comurl.valueField])
                                    return jsondata[i][comurl.textField];
                            }
                        }
                        if (comurl.type == "combotree") {
                            var txt = $getTreeTextById(jsondata, value);
                            if (txt == undefined) txt = value;
                            return txt;
                        }
                        return value;
                    };
                    comurl.succ = true;
                    if (checkAllSuccess()) {
                        if (comOptions.onOK) comOptions.onOK();
                    }
                }, function (err) {
                    $.messager.alert('错误', '初始化Grid Combobox错误:' + err.errmsg, 'error');
                }, true, comurl);
            }
            function checkAllSuccess() {
                for (var i = 0; i < comUrls.length; i++) {
                    if (!comUrls[i].succ)
                        return false;
                }
                return true;
            }
        },
        comFormaters: [],
        comEditors: [],
        comDatas: [],
        dateFormater: function (value, row) {
            if ((!value) || (value == null) || (value.length = 0))
                return "";
            var date = new Date().fromStr(value);
            return date.format("yyyy-MM-dd hh:mm:ss");
        },
        dateEditor: {type: 'datebox'},
        clearData: function (GridID) {
            var oGrid = undefined;
            if (typeof(GridID) == "object") {
                oGrid = GridID;
            } else {
                if (GridID.charAt(0) != "#")
                    GridID = "#" + GridID;
                oGrid = $(GridID);
            }
            if (oGrid.length <= 0) {
                //alert("页面没ID为:" + GridID + "的对象！");
            }
            if (!oGrid) return false;
            var rows = oGrid.datagrid("getRows");
            for (var i = rows.length - 1; i >= 0; i--) {
                oGrid.datagrid('deleteRow', i);
            }
            return true;
        },
        endEditing: function (GridID) {
            if (GridID.charAt(0) != "#")
                GridID = "#" + GridID;
            var oGrid = $(GridID);
            if (!oGrid) return false;
            var rows = oGrid.datagrid("getRows");
            for (var i = 0; i < rows.length; i++) {
                oGrid.datagrid('endEdit', i);
            }
            return true;
        },
        edit: function (GridID) {
            if (GridID.charAt(0) != "#")
                GridID = "#" + GridID;
            var oGrid = $(GridID);
            if (!oGrid) return false;
            if ($C.grid.endEditing(GridID)) {
                var row = oGrid.datagrid('getSelected');
                if (!row) {
                    $.messager.alert('错误', '没选择的行', 'error');
                    return false;
                }
                var index = oGrid.datagrid('getRowIndex', row);
                oGrid.datagrid('selectRow', index).datagrid('beginEdit', index);
                return true;
            }
        },
        append: function (GridID, RowData, Editing) {
            if (GridID.charAt(0) != "#")
                GridID = "#" + GridID;
            var oGrid = $(GridID);
            if (!oGrid) return false;
            if ($C.grid.endEditing(GridID)) {
                if (!RowData) RowData = {};
                oGrid.datagrid('appendRow', RowData);
                var newIndex = oGrid.datagrid('getRows').length - 1;
                oGrid.datagrid('selectRow', newIndex);
                if (Editing) oGrid.datagrid("beginEdit", newIndex);
                return true;
            }

        },
        remove: function (GridID) {
            if (GridID.charAt(0) != "#")
                GridID = "#" + GridID;
            var oGrid = $(GridID);
            if (!oGrid) return false;
            if ($C.grid.endEditing(GridID)) {
                var row = oGrid.datagrid('getSelected');
                if (!row) {
                    $.messager.alert('错误', '没选择的行', 'error');
                    return false;
                }
                var index = oGrid.datagrid('getRowIndex', row);
                oGrid.datagrid('deleteRow', index);
                return true;
            }
        },
        accept: function (GridID) {
            if (GridID.charAt(0) != "#")
                GridID = "#" + GridID;
            var oGrid = $(GridID);
            if (!oGrid) return false;
            if ($C.grid.endEditing(GridID)) {
                oGrid.datagrid('acceptChanges');
            }
        },
        reject: function (GridID) {
            if (GridID.charAt(0) != "#")
                GridID = "#" + GridID;
            var oGrid = $(GridID);
            if (!oGrid) return false;
            oGrid.datagrid('rejectChanges');
        },
        removeByField: function (filter, fdvalue, fdname) {
            var rows = $(filter).datagrid("getRows");
            var row = undefined;
            for (var i = 0; i < rows.length; i++) {
                row = rows[i];
                if (row[fdname] == fdvalue)
                    break;
            }
            if (!row) return;
            var index = $(filter).datagrid('getRowIndex', row);
            $(filter).datagrid('deleteRow', index);
            return true;
        },
        getRowByField: function (filter, fdvalue, fdname) {
            var rows = $(filter).datagrid("getRows");
            //console.error(JSON.stringify(rows));
            var row = undefined;
            for (var i = 0; i < rows.length; i++) {
                row = rows[i];
                // console.error("row v:" + row[fdname] + " | v:" + fdvalue);
                if (row[fdname] == fdvalue)
                    return row;
            }
            return undefined;
        },
        getRowByFields: function (filter, fdvalues, fdnames) {
            var rows = $(filter).datagrid("getRows");
            if (fdvalues.length != fdnames.length)
                return false;
            if (fdvalues.length == 0)
                return false;
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                var fd = true;
                for (var j = 0; j < fdvalues.length; j++) {
                    if (row[fdnames[j]] != fdvalues[j]) {
                        fd = false;
                        break;
                    }
                }
                if (fd) return row;
            }
            return false;
        }
    },
    UserInfo: {
        userid: function () {
            return localStorage.getItem(_contextPath + ".userid");
        },
        username: function () {
            return localStorage.getItem(_contextPath + ".username");
        },
        displayname: function () {
            return localStorage.getItem(_contextPath + ".displayname");
        },
        entid: function () {
            return localStorage.getItem(_contextPath + ".entid");
        },
        languageid: function () {
            return localStorage.getItem(_contextPath + ".languageid");
        },
        dforgid: function () {
            return localStorage.getItem(_contextPath + ".dforgid");
        },
        dforgidpath: function () {
            return localStorage.getItem(_contextPath + ".dforgidpath");
        },
        usertype: function () {
            return localStorage.getItem(_contextPath + ".usertype");
        },
        removelogininfos: function () {
            localStorage.removeItem(_contextPath + ".userid");
            localStorage.removeItem(_contextPath + ".username");
            localStorage.removeItem(_contextPath + ".displayname");
            localStorage.removeItem(_contextPath + ".entid");
            localStorage.removeItem(_contextPath + ".languageid");
            localStorage.removeItem(_contextPath + ".dforgid");
            localStorage.removeItem(_contextPath + ".dforgidpath");
            localStorage.removeItem(_contextPath + ".usertype");
        }
    },
    tree: {
        setTree1OpendOtherClosed: function (nodes) {//打开第一层
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                if (node.children) {
                    node.state = "opend";
                    $C.tree.setTreeNodesState(node.children, "closed");
                }
            }
            return nodes;
        },
        setTreeNodesState: function (nodes, state) {
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                if (node.children) {
                    node.state = state;
                    $C.tree.setTreeNodesState(node.children, state);
                }
            }
        }
    }
};

function $getfileicobyfileext(ext) {
    var fileexttys = [{
        exts: [".vb", ".vbs", ".reg", ".pl", ".pm", ".cgi", ".as", ".clj", ".cbl", ".cfm", ".d", ".diff", ".dot", ".ejs", ".erl", ".ftl", ".go", ".hs", ".hbs", ".haml", ".erb", ".jade", ".json", ".jq", ".jsx", ".ji", ".tex", ".lisp", ".ls", ".lsl", ".lua", ".lp", ".matlab", ".mel", ".r", ".yaml", ".vb", ".vbs", ".reg", ".pl", ".pm", ".cgi", ".as", ".clj", ".cbl", ".cfm", ".d", ".diff", ".dot", ".ejs", ".erl", ".ftl", ".go", ".hs", ".hbs", ".haml", ".erb", ".jade", ".json", ".jq", ".jsx", ".ji", ".tex", ".lisp", ".ls", ".lsl", ".lua", ".lp", ".matlab", ".mel", ".r", ".yaml"],
        cls: "icon-file-code32"
    },
        {exts: [".folder", ".folder"], cls: "icon-file-folder32"},
        {exts: [".txt", ".md", ".markdown", ".log", ".txt", ".md", ".markdown", ".log"], cls: "icon-file-txt32"},
        {
            exts: [".html", ".htm", ".url", ".tpl", ".lnk", ".haml", ".shtml", ".webloc", ".hta", ".html", ".htm", ".url", ".tpl", ".lnk", ".haml", ".shtml", ".webloc", ".hta"],
            cls: "icon-file-html32"
        },
        {exts: [".css", ".less", ".sass", ".css", ".less", ".sass"], cls: "icon-file-css32"},
        {exts: [".js", ".coffee", ".js", ".coffee"], cls: "icon-file-js32"},
        {
            exts: [".xml", ".config", ".manifest", ".xaml", ".csproj", ".vbproj", ".xml", ".config", ".manifest", ".xaml", ".csproj", ".vbproj"],
            cls: "icon-file-xml32"
        },
        {exts: [".cs", ".asp", ".aspx", ".cs", ".asp", ".aspx"], cls: "icon-file-cs32"},
        {exts: [".java", ".jsp", ".java", ".jsp"], cls: "icon-file-java32"},
        {exts: [".sql", ".sql"], cls: "icon-file-sql32"},
        {exts: [".oexe", ".oexe"], cls: "icon-file-oexe32"},
        {exts: [".php", ".php"], cls: "icon-file-php32"},
        {exts: [".py", ".py"], cls: "icon-file-py32"},
        {exts: [".rb", ".rb"], cls: "icon-file-rb32"},
        {
            exts: [".mm", ".cc", ".cxx", ".cpp", ".c", ".m", ".mm", ".cc", ".cxx", ".cpp", ".c", ".m"],
            cls: "icon-file-cpp32"
        },
        {exts: [".h", ".hpp", ".hh", ".pch", ".h", ".hpp", ".hh", ".pch"], cls: "icon-file-h32"},
        {exts: [".jpg", ".jpeg", ".bmp", ".gif", , ".jpg", ".jpeg", ".bmp", ".gif"], cls: "icon-file-jpg32"},
        {exts: [".asm", ".asm"], cls: "icon-file-asm32"},
        {
            exts: [".Makefile", ".makefile", ".GNUmakefile", ".makefile", ".OCamlMakefile", ".make", ".Makefile", ".makefile", ".GNUmakefile", ".makefile", ".OCamlMakefile", ".make"],
            cls: "icon-file-makefile32"
        },
        {exts: [".sln", ".sln"], cls: "icon-file-sln32"},
        {exts: [".pdf", ".pdf"], cls: "icon-file-pdf32"},
        {exts: [".psd", ".psd"], cls: "icon-file-psd32"},
        {exts: [".flv", ".f4v", ".flv", ".f4v"], cls: "icon-file-flv32"},
        {exts: [".fla", ".fla"], cls: "icon-file-fla32"},
        {exts: [".swf", ".swf"], cls: "icon-file-swf32"},
        {exts: [".air", ".air"], cls: "icon-file-air32"},
        {exts: [".svg", ".svg"], cls: "icon-file-svg32"},
        {exts: [".ai", ".ai"], cls: "icon-file-ai32"},
        {exts: [".chm", ".chm"], cls: "icon-file-chm32"},
        {exts: [".numbers", ".numbers"], cls: "icon-file-numbers32"},
        {exts: [".pages", ".pages"], cls: "icon-file-pages32"},
        {exts: [".ipa", ".ipa"], cls: "icon-file-ipa32"},
        {exts: [".iso", ".dvd", ".iso", ".dvd"], cls: "icon-file-iso32"},
        {exts: [".dmg", ".dmg"], cls: "icon-file-dmg32"},
        {exts: [".key", ".key"], cls: "icon-file-key32"},
        {exts: [".package", ".framework", ".package", ".framework"], cls: "icon-file-package32"},
        {
            exts: [".zip", ".apk", ".tar", ".gzip", ".jar", ".cxr", ".tar", ".gz", ".cab", ".tbz", ".tbz2", ".bz2", ".zip", ".apk", ".tar", ".gzip", ".jar", ".cxr", ".tar", ".gz", ".cab", ".tbz", ".tbz2", ".bz2"],
            cls: "icon-file-zip32"
        },
        {exts: [".rar", ".rar"], cls: "icon-file-rar32"},
        {exts: [".dll", ".dll"], cls: "icon-file-dll32"},
        {exts: [".exe", ".bin", ".exe", ".bin"], cls: "icon-file-exe32"},
        {
            exts: [".ttf", ".otf", ".eot", ".woff", ".tiff", ".ttc", ".ttf", ".otf", ".eot", ".woff", ".tiff", ".ttc"],
            cls: "icon-file-ttf32"
        },
        {
            exts: [".bat", ".cmd", ".sh", ".bash", ".bashrc", ".bat", ".cmd", ".sh", ".bash", ".bashrc"],
            cls: "icon-file-cmd32"
        },
        {
            exts: [".ini", ".conf", ".meta", ".gitignore", ".plist", ".htaccess", ".localized", ".xcscheme", ".storyboard", ".xib", ".strings", ".pbxproj", ".ini", ".conf", ".meta", ".gitignore", ".plist", ".htaccess", ".localized", ".xcscheme", ".storyboard", ".xib", ".strings", ".pbxproj"],
            cls: "icon-file-ini32"
        },
        {
            exts: [".mp3", ".wma", ".mp2", ".mid", ".aac", ".wav", ".m4a", ".mp3", ".wma", ".mp2", ".mid", ".aac", ".wav", ".m4a"],
            cls: "icon-file-mp332"
        },
        {
            exts: [".avi", ".rm", ".rmvb", ".mpg", ".mkv", ".wmv", ".mov", ".mp4", ".avi", ".rm", ".rmvb", ".mpg", ".mkv", ".wmv", ".mov", ".mp4"],
            cls: "icon-file-avi32"
        },
        {exts: [".doc", ".docx", ".wps", ".doc", ".docx", ".wps"], cls: "icon-file-doc32"},
        {exts: [".xls", ".xlsx", ".csv", ".xls", ".xlsx", ".csv"], cls: "icon-file-xls32"},
        {exts: [".ppt", ".pptx", ".ppt", ".pptx"], cls: "icon-file-ppt32"},
        {exts: [".recycle", ".recycle"], cls: "icon-file-recycle_image32"},
        {exts: [".share", ".share"], cls: "icon-file-share_image32"}];

    for (var i = 0; i < fileexttys.length; i++) {
        var exttp = fileexttys[i];
        if ($.inArray(ext, exttp.exts) > -1)
            return exttp.cls;
    }
    return "icon-file-file32";
}

//options
//
function $uploadfile(serco, upfhtml, onerr, onsucc, onstart) {
    $uploadfileEx({
        serco: serco,
        upfhtml: upfhtml,
        onerr: onerr,
        onsucc: onsucc,
        onstart: onstart
    });
}


function $uploadfileEx(options) {
    var serco = (options.serco) ? options.serco : _serUrl + "/web/common/upLoadFile.co?timestamp=" + (new Date()).valueOf();
    var upfhtml = (options.upfhtml) ? options.upfhtml : _serUrl + "/webapp/templet/default/uploadfile.html";
    var onerr = options.onerr;
    var onsucc = options.onsucc;
    var onstart = options.onstart;
    var multifile = (options.multifile == undefined) ? true : options.multifile;
    var acceptfile = options.acceptfile;

    if ($("#common_divs_id").length <= 0) {
        $("<div id='common_divs_id'></div>").appendTo("body");
    }

    if ($("#pw_uploadfile").length <= 0) {
        var athm = "<div id='pw_uploadfile' class='easyui-window' title='上传文件'"
            + " data-options='modal:true,closed:true,iconCls:\"icon-save\"'"
            + " style='width:400px;height:400px;padding:0px;'>"
            + " <iframe frameborder='0' style='width: 100%;height: 95%'></iframe>"
            + " </div>";
        $(athm).appendTo("#common_divs_id");
        $.parser.parse('#common_divs_id');
    }

    var wd = $("#pw_uploadfile iframe");
    var action = encodeURI(serco);
    wd.attr("src", upfhtml);
    var iframe = wd[0];
    if (iframe.attachEvent) {
        iframe.attachEvent("onload", function () {
            setAttPWCallback(iframe, action, onstart);
        });
    } else {
        iframe.onload = function () {
            setAttPWCallback(iframe, action, onstart);
        };
    }
    $("#pw_uploadfile").window("open");

    function setAttPWCallback(iframe, action, onstart) {
        iframe.contentWindow._acceptfile = acceptfile;
        iframe.contentWindow._multifile = multifile;
        iframe.contentWindow._action = action;
        iframe.contentWindow._onstart = onstart;
        iframe.contentWindow._callback = function (rst) {
            var bis = JSON.parse(rst);
            if (bis.errmsg) {
                $("#pw_uploadfile").window("close");
                if (onerr) {
                    onerr(bis.errmsg);
                } else
                    $.messager.alert('错误', bis.errmsg, 'error');
                return;
            } else {
                if (onsucc)onsucc(bis);
            }
            $("#pw_uploadfile").window("close");
        };
    }

}

function $shw_attachinfo(options) {
    var pw_data_options = (options.pw_data_options) ? options.pw_data_options : "data-options='modal:true,closed:true'";
    var pw_style = (options.pw_style) ? options.pw_style : "style='width:800px;height:500px;padding:0px;'>";
    var pw_title = (options.pw_title) ? options.pw_title : "附件";
    var jpaclass = (options.jpaclass) ? options.jpaclass : undefined;
    var allowscan = (options.allowscan != undefined) ? options.allowscan : false;
    options.jpaclass = jpaclass;
    var onerr = (options.onerr) ? options.onerr : undefined;
    var onsucc = (options.onsucc) ? options.onsucc : undefined;
    var onclose = (options.onclose) ? options.onclose : undefined;
    var imgthb = (options.imgthb == undefined) ? false : options.imgthb;
    options.imgthb = imgthb;

    var id = (options.id) ? options.id : id;
    options.id = id;
    var downurl = undefined;
    if (options.downurl) {
        downurl = options.downurl;
        if (downurl.indexOf("?") < 0)
            downurl = downurl + "?timestamp=" + (new Date()).valueOf();
    } else {
        downurl = _serUrl + "/web/common/getshw_attachbyid.co?timestamp=" + (new Date()).valueOf();
    }


    if ($("#common_divs_id").length <= 0) {
        $("<div id='common_divs_id'></div>").appendTo("body");
    }
    if ($("#pw_shw_attach_info").length <= 0) {
        var athm = "<div id='pw_shw_attach_info' class='easyui-window' title='" + pw_title + "'"
            + pw_data_options
            + pw_style
            + "<iframe frameborder='0' style='width: 100%;height: 95%'></iframe>"
            + "</div>";
        $(athm).appendTo("#common_divs_id");
        $.parser.parse('#common_divs_id');
    }
    var wd = $("#pw_shw_attach_info iframe");
    var infhtm = (options.infhtm) ? options.infhtm : _serUrl + "/webapp/templet/default/shw_attach.html?timestamp=" + (new Date()).valueOf();
    var iframe = wd[0];
    if (iframe.attachEvent) {
        iframe.attachEvent("onload", function () {
            setAttPWCallback(iframe, options);
            if (!allowscan)
                $(iframe).contents().find("#btn_scan").css("display", "none");
            // $("#btn_scan", wd.document).css("display", "none");
        });
    } else {
        iframe.onload = function () {
            setAttPWCallback(iframe, options);
            if (!allowscan)
                $(iframe).contents().find("#btn_scan").css("display", "none");
        };
    }
    wd.attr("src", infhtm);

    $("#pw_shw_attach_info").window({
        onClose: function () {
            if (onclose)onclose();
        }
    });
    $("#pw_shw_attach_info").window("open");


    function setAttPWCallback(iframe, options) {
        iframe.contentWindow._options = options;
        iframe.contentWindow._callback = function (rst) {
            //alert(JSON.stringify(rst));
            var bis = JSON.parse(rst);
            if (bis.errmsg) {
                $("#pw_shw_attach_info").window("close");
                if (onerr) {
                    onerr(bis.errmsg);
                } else
                    $.messager.alert('错误', bis.errmsg, 'error');
                return;
            } else {
                if (onsucc)onsucc(bis);
            }
            $("#pw_shw_attach_info").window("close");
        };
    }
}

function $parserDatebox2YearMonth(input) {
    var db = input;
    db.datebox({
        onShowPanel: function () {//显示日趋选择对象后再触发弹出月份层的事件，初始化时没有生成月份层
            span.trigger('click'); //触发click事件弹出月份层
            if (!tds) setTimeout(function () {//延时触发获取月份对象，因为上面的事件触发和对象生成有时间间隔
                tds = p.find('div.calendar-menu-month-inner td');
                tds.click(function (e) {
                    e.stopPropagation(); //禁止冒泡执行easyui给月份绑定的事件
                    var year = /\d{4}/.exec(span.html())[0]//得到年份
                        , month = parseInt($(this).attr('abbr'), 10); //月份，这里不需要+1
                    db.datebox('hidePanel')//隐藏日期对象
                        .datebox('setValue', year + '-' + month); //设置日期的值
                });
            }, 0);
            yearIpt.unbind();//解绑年份输入框中任何事件
        },
        parser: function (s) {
            if (!s) return new Date();
            var arr = s.split('-');
            return new Date(parseInt(arr[0], 10), parseInt(arr[1], 10) - 1, 1);
        },
        formatter: function (d) {
            var m = (d.getMonth() + 1);
            if (m.toString().length == 1)
                m = '0' + m;
            return d.getFullYear() + '-' + m;
            /*getMonth返回的是0开始的，忘记了。。已修正*/
        }
    });
    var p = db.datebox('panel'), //日期选择对象
        tds = false, //日期选择对象中月份
        aToday = p.find('a.datebox-current'),
        yearIpt = p.find('input.calendar-menu-year'),//年份输入框
    //显示月份层的触发控件
        span = aToday.length ? p.find('div.calendar-title span') ://1.3.x版本
            p.find('span.calendar-text'); //1.4.x版本
    p.find('.calendar-menu-year-inner').css("display", "none");
    if (aToday.length) {//1.3.x版本，取消Today按钮的click事件，重新绑定新事件设置日期框为今天，防止弹出日期选择面板
        aToday.unbind('click').click(function () {
            var now = new Date();
            db.datebox('hidePanel').datebox('setValue', now.getFullYear() + '-' + (now.getMonth() + 1));
        });
    }
}


function $getRowByFields(rows, fdvalues, fdnames) {
    //alert($getRowByFields);
    if (fdvalues.length != fdnames.length)
        return false;
    if (fdvalues.length == 0)
        return false;
    for (var i = 0; i < rows.length; i++) {
        var row = rows[i];
        var fd = true;
        //alert(JSON.stringify(row));
        for (var j = 0; j < fdvalues.length; j++) {
            //alert(row[fdnames[j]] + "   " + fdvalues[j]);
            if (row[fdnames[j]] != fdvalues[j]) {
                fd = false;
                break;
            }
        }
        if (fd) return row;
    }
    return false;
}

//var m = [1,2,3,4,4,12,3]; 删除重复元素
function unique(arr) {
    var result = [], hash = {};
    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
        if (!hash[elem]) {
            result.push(elem);
            hash[elem] = true;
        }
    }
    return result;
}


function $InitGridColFields(fields, gridkey) {
    var htmlurl = document.location.href.split("?")[0];
    var index = htmlurl.substr(1).indexOf("//");
    var key = htmlurl.substr(index + 3, htmlurl.length) + gridkey;
    key = key.replace(/\//g, "-");
    var filters = localStorage.getItem(key);
    if (!filters)
        return fields;
    filters = eval(filters);
    return getColFieldsByColFilter(fields, filters);
}

var halignedata = [
    {caption: "居中", value: "center"},
    {caption: "居左", value: "left"},
    {caption: "居右", value: "right"}
];

function $showGridColFilter(grid, defaultfields, fields, gridkey, onOK, onCancel) {

    var haligneditor = {
        type: 'combobox',
        options: {
            valueField: "value",
            textField: "caption",
            data: halignedata
        }
    };
    var haligneformator = function (value, row, index) {
        for (var i = 0; i < halignedata.length; i++) {
            if (value == halignedata[i].value) return halignedata[i].caption;
        }
    };
    var htmlurl = document.location.href.split("?")[0];
    var index = htmlurl.substr(1).indexOf("//");
    var key = htmlurl.substr(index + 3, htmlurl.length) + gridkey;
    key = key.replace(/\//g, "-");
    //读取存储的数据
    var filters = localStorage.getItem(key);
    filters = (filters == undefined) ? createFitlters(fields) : eval(filters);
    var setWindow = $("#gridcolfilterpw_id");
    var setGrid = setWindow.find(".easyui-datagrid");
    var setGridFeids = [
        {field: 'chked', checkbox: true},
        {field: 'sidx', title: '序号', width: 40},
        //{field: 'field', title: '字段', width: 100},
        {field: 'title', title: '标题', width: 100},
        {field: 'halign', title: '标题对齐', width: 100, editor: haligneditor, formatter: haligneformator},
        {field: 'width', title: '宽度', editor: 'numberbox', width: 80}
    ];

    var editingidx = -1;
    setGrid.datagrid({
        columns: [setGridFeids],
        onDblClickRow: function (index, row) {
            setGrid.datagrid("beginEdit", index);
            editingidx = index;
        },
        onCheck: function (index, row) {
            setGrid.datagrid("unselectAll");
            setGrid.datagrid("selectRow", index);
        },
        onClickRow: function (index, row) {
            if (editingidx != -1)
                setGrid.datagrid("endEdit", editingidx);
            editingidx = -1;
            setGrid.datagrid("unselectAll");
            setGrid.datagrid("selectRow", index);
        }
    });
    setGrid.datagrid("loadData", filters);
    for (var i = 0; i < filters.length; i++) {
        if (filters[i].chked) {
            var idx = setGrid.datagrid("getRowIndex", filters[i]);
            setGrid.datagrid("checkRow", idx);
        }
    }

    setWindow.find("a[cjoptions]").each(function (index, el) {
        var co = $.c_parseOptions($(this));
        if (co.caction == "act_ok") {
            el.onclick = function () {
                if (onOK) {
                    $endGridEdit(setGrid);
                    var chks = setGrid.datagrid("getChecked");
                    var filters = setGrid.datagrid("getRows");
                    setFiltersChk(filters, chks);//根据选择框数据设置filters
                    saveColFilterParms(key, JSON.stringify(filters));
                    setCol2Grid(setGrid, fields, filters, grid);
                    onOK(filters);
                    setWindow.window('close');
                }
            };
        }
        if (co.caction == "act_cancel") {
            el.onclick = function () {
                setWindow.window('close');
            }
        }
        if (co.caction == 'act_select') {
            el.onclick = function () {
                var selecteds = setGrid.datagrid('getChecked');
                if (selecteds.length == 0) {
                    setGrid.datagrid("checkAll");
                } else {
                    setGrid.datagrid("uncheckAll");
                }
            }
        }
        if (co.caction == 'act_moveDown') {
            el.onclick = function () {
                var strow = setGrid.datagrid('getSelected');
                if (strow == null)
                    return;
                var datas = setGrid.datagrid("getRows");
                var idx = setGrid.datagrid("getRowIndex", strow);
                if (idx < 0) return;
                if (idx >= (datas.length - 1)) return;
                var rtem = datas[idx + 1];
                strow.sidx = strow.sidx + 1;
                datas[idx + 1] = strow;
                rtem.sidx = rtem.sidx - 1;
                datas[idx] = rtem;
                setGrid.datagrid("loadData", datas);
                setGrid.datagrid("selectRow", idx + 1);
            }
        }
        if (co.caction == 'act_moveUp') {
            el.onclick = function () {
                var strow = setGrid.datagrid('getSelected');
                if (strow == null)
                    return;
                var datas = setGrid.datagrid("getRows");
                var idx = setGrid.datagrid("getRowIndex", strow);
                if (idx <= 0) return;
                var rtem = datas[idx - 1];
                strow.sidx = strow.sidx - 1;
                datas[idx - 1] = strow;
                rtem.sidx = rtem.sidx + 1;
                datas[idx] = rtem;
                setGrid.datagrid("loadData", datas);
                setGrid.datagrid("selectRow", idx - 1);
            }
        }

        if (co.caction == 'act_reset') {
            el.onclick = function () {
                var filters = createFitlters(defaultfields);
                setGrid.datagrid("loadData", filters);
            }
        }

    });
    setWindow.window("open");
}

function createFitlters(fields) {
    var rows = [];
    var idx = 0;
    for (var i = 0; i < fields.length; i++) {
        for (var j = 0; j < fields[i].length; j++) {
            var row = {};
            row.sidx = idx++;
            row.field = fields[i][j].field;
            row.title = fields[i][j].title;
            row.width = fields[i][j].width;
            row.chked = fields[i][j].hidden;
            row.halign = (fields[i][j].halign) ? fields[i][j].halign : "center";
            rows.push(row);
        }
    }
    return rows;
}

function setFiltersChk(filters, chks) {
    for (var i = 0; i < filters.length; i++) {
        var filter = filters[i];
        filter.chked = isChecked(filter.field);
    }
    function isChecked(fdname) {
        for (var i = 0; i < chks.length; i++) {
            if (chks[i].field == fdname) {
                return true;
            }
        }
        return false;
    }
}


function setCol2Grid(setGrid, fields, filters, grid) {
    fields = getColFieldsByColFilter(fields, filters);
    grid.datagrid({columns: fields});
}

function saveColFilterParms(key, value) {
    localStorage.setItem(key, value);
}


function getColFieldsByColFilter(fields, filters) {
    for (var i = 0; i < fields.length; i++) {
        for (var j = 0; j < fields[i].length; j++) {
            var field = fields[i][j];
            var filter = getFilterByFdname(field.field);
            if (filter) {
                field.title = filter.title;
                field.width = filter.width;
                field.hidden = filter.chked;
                field.halign = (filter.halign) ? filter.halign : 'center';
            }
        }
    }
    return fields;

    function getFilterByFdname(fdname) {
        for (var i = 0; i < filters.length; i++) {
            if (fdname == filters[i].field)
                return filters[i];
        }
        return null;
    }
}

function $endGridEdit(oGrid) {
    var rows = oGrid.datagrid("getRows");
    for (var i = 0; i < rows.length; i++) {
        oGrid.datagrid('endEdit', i);
    }
}


function $HeartBeat(parms) {
    var url = (parms && parms.url) ? parms.url : _serUrl + "/web/common/heartbeat.co";
    var delay = (parms && parms.delay) ? parms.delay : 1000 * 60 * 5;//默认5分钟
    var istoped = false;

    function getajaxhb() {
        //alert(getajaxhb);
        $ajaxjsonget(url, function (jsdata) {
            if (!istoped) {
                window.setTimeout(function () {
                    getajaxhb();
                }, delay);
            }
        }, function (msg) {
            return;
        }, true, undefined, true);
    }

    this.start = start;
    function start(dly) {
        istoped = false;
        if (dly != undefined)
            window.setTimeout(function () {
                getajaxhb();
            }, dly);
        else
            getajaxhb();
    }

    this.stop = stop;
    function stop() {
        istoped = true;
    }
}


//console.log(unique(m));

