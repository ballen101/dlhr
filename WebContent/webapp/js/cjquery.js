/**
 * Created by Administrator on 2014-09-28
 * just for corsair server.
 */
//数据字典

function getBrowserInfo() {
    var ua = navigator.userAgent.toLowerCase(),
        rMsie = /(msie\s|trident.*rv:)([\w.]+)/,
        rFirefox = /(firefox)\/([\w.]+)/,
        rOpera = /(opera).+version\/([\w.]+)/,
        rChrome = /(chrome)\/([\w.]+)/,
        rSafari = /version\/([\w.]+).*(safari)/;
    var match = rMsie.exec(ua);
    if (match != null) {
        return {browser: "IE", version: match[2] || "0"};
    }
    var match = rFirefox.exec(ua);
    if (match != null) {
        return {browser: match[1] || "", version: match[2] || "0"};
    }
    var match = rOpera.exec(ua);
    if (match != null) {
        return {browser: match[1] || "", version: match[2] || "0"};
    }
    var match = rChrome.exec(ua);
    if (match != null) {
        return {browser: match[1] || "", version: match[2] || "0"};
    }
    var match = rSafari.exec(ua);
    if (match != null) {
        return {browser: match[2] || "", version: match[1] || "0"};
    }
    if (match != null) {
        return {browser: "", version: "0"};
    }
}

var browinfo = getBrowserInfo();

//日期精度
var $Precision = {
    piNone: 0, piYear: 1, piMonth: 2, piDay: 3, piHour: 4, piMinute: 5, piSecond: 6
};
//汇总类型
var $Aggregation = {
    agNone: 0, agConst: 1, agSUM: 2, agCOUNT: 3, agAVG: 4, agMAX: 5, agMin: 6
};

//导出Excel列的数据类型
var $ECType = {
    ectUnknown: 0, ectNumber: 1
};

//$.parser.auto = false;
var pc;
var $OnCorsairReady;
var comUrls;
//var islie8 = $.isLessIe8();
$(document).ready(function () {
    try {
        if ($urlparms && $urlparms.title) {
            document.title = $urlparms.title;
        }
    } catch (e) {
        //
    }
    var vfrmOptions = window.frmOptions;
    if (vfrmOptions && vfrmOptions.extattrfilter) {
        init_jpaattr({filter: vfrmOptions.extattrfilter});
    }

    if (comUrls) {
        do_initdics({
            comUrls: comUrls, onOK: function (delay) {
                doinitdealy(delay);
            }
        });
    } else {
        doinitdealy(false);
    }
});

function doinitdealy(delay) {
    if (delay)
        setTimeout(doinit, 500);
    else
        doinit();
}


//部分dic经常会变化的，可能需要及时刷新
function do_initdics(comOptions) {
    var comUrls = comOptions.comUrls;
    if (iswiect(comUrls) == 0)
        if (comOptions.onOK) comOptions.onOK(true);
    for (var i = 0; i < comUrls.length; i++) {
        var comurl = comUrls[i];
        if (comurl.iswie) {
            var url = _serUrl + comurl.url;
            $ajaxjsonget(url, function (jsondata, comurl) {
                var edtor = {type: comurl.type, options: {}};
                if (comurl.type == "combobox") {
                    edtor.options.valueField = comurl.valueField;
                    edtor.options.textField = comurl.textField;
                }
                if (comurl.type == "combotree") {
                    $C.tree.setTree1OpendOtherClosed(jsondata);//第一级开着 第二级后全关上
                }
                var strvalname = "comUrl_" + comurl.index;
                window[strvalname] = comurl;
                comurl.jsondata = jsondata;
                edtor.options.data = comurl.jsondata;
                comurl.editor = edtor;

                comurl.formator = function (value, row) {
                    var curl = window[strvalname];
                    if (value == "get_com_data") {
                        return curl.jsondata;
                    }
                    if (value == "get_com_url") {
                        return curl;
                    }
                    // alert(comurl.index + ":" + value + ":" + JSON.stringify(jsondata));
                    if (curl.type == "combobox") {
                        for (var i = 0; i < curl.jsondata.length; i++) {
                            if (value == curl.jsondata[i][curl.valueField])
                                return curl.jsondata[i][curl.textField];
                        }
                    }
                    if (curl.type == "combotree") {
                        var txt = $getTreeTextById(curl.jsondata, value);
                        if (txt == undefined) txt = value;
                        return txt;
                    }
                    return value;
                };
                comurl.succ = true;
                if (checkAllSuccess()) {
                    if (comOptions.onOK) comOptions.onOK(false);
                }
            }, function (err) {
                $.messager.alert('错误', '初始化Grid Combobox错误:' + err.errmsg, 'error');
            }, true, comurl);
        }
    }

    function checkAllSuccess() {
        for (var i = 0; i < comUrls.length; i++) {
            if ((comUrls[i].iswie) && (!comUrls[i].succ))
                return false;
        }
        return true;
    }

    function iswiect(comUrls) {
        var rst = 0;
        for (var i = 0; i < comUrls.length; i++) {
            if (comUrls[i].iswie) rst++;
        }
        return rst;
    }
}

var _intfunctions = [];
//注册页面初始化函数
function $RegInitfunc(func) {
    _intfunctions.push(func);
}

var inited = false;
function doinit() {
    inited = true;
    if (pc) clearTimeout(pc);
    var rst = execinitfunctions();
    if ($OnCorsairReady) {
        if (!$OnCorsairReady())
            pc = setTimeout(closes, 1000);
    } else
        pc = setTimeout(closes, 1000);
}

//执行注册的初始化函数  所有函数都返回true，则执行函数返回true 否则返回false
function execinitfunctions() {
    var rst = true;
    for (var i = 0; i < _intfunctions.length; i++) {
        rst = rst && _intfunctions[i]();
    }
    return rst;//
}


function closes() {
    $("#loading").fadeOut("normal", function () {
        $(this).remove();
    });
}


$.c_parseOptions = function (obj) {
    var s = $.trim(obj.attr("cjoptions"));
    return $.c_parseOptionsStr(s);
};

$.c_parseOptionsStr = function (s) {
    try {
        if (s.substring(0, 1) != "{") {
            s = "{" + s + "}";
        }
        return (new Function("return " + s))();
    } catch (err) {
        alert(err + ":" + s) // 可执行
    }
};

$.fn.c_initDictionary = function () {
    $(this).find("input[cjoptions]").each(function (index, el) {
        var co = $.c_parseOptions($(this));
        if (co.dicgpid != undefined) {
            var obj = $(this);
            $ajaxjsonget(_serUrl + "/web/dict/getdictvalues.co?dicid=" + co.dicgpid,
                function (jsondata) {
                    obj.combobox({
                        data: jsondata,
                        valueField: 'dictvalue',
                        textField: 'language1'
                    });
                }, function () {
                    obj.combobox({
                        data: {},
                        valueField: 'dictvalue',
                        textField: 'language1'
                    });
                    $.messager.alert("错误", "获取数据字典错误!", "error");
                });
        }
    });
};

$.fn.c_clearData = function () {
    $(this).find("input[cjoptions]").each(function (index, el) {
        $(this).textbox('setValue', "");
    });
};

function getElEditType(el) {
    var tp = $(el).attr("type");
    if (tp == "checkbox")
        return 6;
    var cls = $(el).attr("class");
    if (cls.indexOf("easyui-datetimebox") >= 0) {
        return 1;
    } else if (cls.indexOf("easyui-textbox") >= 0) {
        return 5;
    }
}

$.fn.c_fromJsonData = function (jsondata) {
    $(this).c_clearData();
    $(this).find("input[cjoptions]").each(function (index, el) {
        var co = $.c_parseOptions($(this));
        var et = getElEditType(el);
        var v = jsondata[co.fdname];
        switch (et) {
            case 1:
            {
                if ((v == undefined) || (v == null) || (v == "NULL")) {
                    $(this).datetimebox('setValue', "");
                } else {
                    var dt = (new Date()).fromStr(v);
                    $(this).datetimebox('setValue', dt.toUIDatestr());
                }
                break;
            }
            case 6:
            {
                if ((v == undefined) || (v == null) || (v == "NULL")) {
                    $(this).attr("checked", false);
                } else {
                    $(this).attr("checked", (parseInt(v) == 1));
                }
                break;
            }
            default:
            {
                if ((v == undefined) || (v == null) || (v == "NULL"))
                    $(this).textbox('setValue', "");
                else {
                    if (co.dicgpid) {
                        $(this).combobox('select', v);
                    } else
                        $(this).textbox('setValue', v);
                }
                break;
            }
        }
    });
};

$.fn.c_setJsonDefaultValue = function (jsondata, isnew) {
    var ctimes = ["createtime", "create_time"];
    var utimes = ["updatetime", "update_time"];
    var ctors = ["creator"];
    var utors = ["updator"];
    var ent = "entid";
    var std = "stat";
    var usb = "usable";
    var vsb = "isvisible";
    $(this).find("input[cjoptions]").each(function (index, el) {
        var fdn = $.c_parseOptions($(this)).fdname;
        if (isnew) {
            if (ctimes.indexOf(fdn) >= 0) jsondata[fdn] = (new Date()).format("yyyy-MM-dd hh:mm:ss");
            if (ctors.indexOf(fdn) >= 0) jsondata[fdn] = $C.UserInfo.username();
            if (fdn == ent) jsondata[fdn] = $C.UserInfo.entid();
            if (fdn == std) jsondata[fdn] = 1;
            if (fdn == usb) jsondata[fdn] = 1;
            if (fdn == vsb) jsondata[fdn] = 1;
        }
        else {
            if (utimes.indexOf(fdn) >= 0) jsondata[fdn] = (new Date()).format("yyyy-MM-dd hh:mm:ss");
            if (utors.indexOf(fdn) >= 0) jsondata[fdn] = $C.UserInfo.username();
        }
    });
};

$.fn.c_toJsonData = function (jsondata, isNew) {//不能新建   父亲窗口可能传递隐含值
    $(this).find("input[cjoptions]").each(function (index, el) {
        var et = getElEditType(el);
        if (et == 6) {
            jsondata[$.c_parseOptions($(this)).fdname] = ( $(this).attr("checked")) ? 1 : 2;
        } else {
            jsondata[$.c_parseOptions($(this)).fdname] = $(this).textbox("getValue");
        }
    });
    if (!isNew)
        $(this).c_setJsonDefaultValue(jsondata, false);
    return jsondata;
};

/*
 wdinfo:
 isNew: false,
 url: _serUrl + "/web/common/editorg.co",
 jsonData: node,
 onOK: infoWindowOK,
 onCancel: infoWindowCancel,
 otherData: {}
 */
$.fn.c_popInfoWindow = function (wdinfo) {
    if ($(this).attr('class').indexOf("easyui-window") < 0)
        throw new Error("非easyui-window窗口，不允许弹出!");
    var iframs = $(this).find("iframe")[0];
    var ifw = undefined;
    if (iframs == undefined)
        ifw = window;
    else
        ifw = iframs.contentWindow;
    ifw.c$wdinfo = wdinfo;
    if (ifw._showInitFormData) {
        ifw.c$wdinfo.jsonData = ifw._showInitFormData(wdinfo);
    }

    $(this).window({
        onOpen: ifw.onShowWindow
    });
    $(this).window('open');
};

function _showInitFormData(wdinfo) {
    var oform = $("#" + wdinfo.formID);
    if (oform[0] == undefined) {
        throw new Error(wdinfo.formID + "对象不存在!");
        return;
    }
    oform.c_clearData();
    var jsondata = wdinfo.jsonData;
    if (wdinfo.isNew) {
        oform.c_setJsonDefaultValue(jsondata, true);
    } else {
        jsondata = wdinfo.jsonData;
    }
    oform.c_fromJsonData(jsondata);
    return jsondata;
};

/*var wdinfoex = {
 isNew: false,
 jsonData: data,
 onOK: procok,
 afterGetData:procgetdata(jsondata),
 onCancel: proccancel,
 onShow: onshow,
 otherData: {}
 }*/
//弹出窗体从json获取数据 并显示出来
$.fn.c_popInfo = function (wdinfoex) {
    var iwobj = $(this);
    var jsondata = wdinfoex.jsonData;
    if ($(this).attr('class').indexOf("easyui-window") < 0)
        throw new Error("非easyui-window窗口，不允许弹出!");
    $(this).find(".easyui-tabs").each(function (index, el) {
        $(this).tabs("select", 0);
    });
    clearInputData();
    if (wdinfoex.isNew)
        $(this).c_setJsonDefaultValue(jsondata, true);
    setTimeout(setdata, 500);
    function setdata() {
        setData2Input();
        setData2Grid();
    }

    //设置按钮事件
    var bts = $(this).find("a[cjoptions]").each(function (index, el) {
        var co = $.c_parseOptions($(this));
        if (co.caction == "act_ok") {
            el.onclick = function () {
                acceptAllGrid();
                //从界面获取数据
                var jdata = iwobj.c_toJsonData(jsondata, wdinfoex.isNew);
                //获取列表数据
                //需要重新load grid 数据吗？  可能不需要！

                if (wdinfoex.afterGetData) {
                    wdinfoex.afterGetData(jsondata);
                }
                if ((wdinfoex.onOK) && (wdinfoex.onOK(jdata)))
                    iwobj.window('close');
            };
        }
        if (co.caction == "act_cancel") {
            el.onclick = function () {
                if (wdinfoex.onCancel) {
                    var errmsg = wdinfoex.onCancel();
                    if (errmsg)alert(errmsg);
                }
                iwobj.window('close');
            }
        }
    });
    $(this).window({onOpen: wdinfoex.onShow});
    $(this).window('open');

    function clearInputData() {
        iwobj.find("input[cjoptions]").each(function (index, el) {
            var tp = $(this).attr("type");
            if (tp == "checkbox")
                $(this).attr("checked", false);
            else
                $(this).textbox('setValue', "");
        });
    }

    function setData2Input() {
        iwobj.find("input[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            //alert(3);
            var et = getElEditType(el);
            //alert(4);
            //alert(et);
            var v = jsondata[co.fdname];
            switch (et) {
                case 1:
                {
                    if ((v == undefined) || (v == null) || (v == "NULL")) {
                        $(this).datetimebox('setValue', "");
                    } else {
                        var dt = (new Date()).fromStr(v);
                        $(this).datetimebox('setValue', dt.toUIDatestr());
                    }
                    break;
                }
                case 6:
                {
                    if ((v == undefined) || (v == null) || (v == "NULL")) {
                        $(this).attr("checked", false);
                    } else {
                        $(this).attr("checked", (parseInt(v) == 1));
                    }
                    break;
                }
                default:
                {
                    if ((v == undefined) || (v == null) || (v == "NULL"))
                        $(this).textbox('setValue', "");
                    else {
                        if (co.dicgpid) {
                            $(this).combobox('select', v);
                        } else
                            $(this).textbox('setValue', v);
                    }
                    break;
                }
            }
        });
    }

    function setData2Grid() {
        iwobj.find(".easyui-datagrid[id]").each(function (index, el) {
            var id = $(el).attr("id");
            var tbdata = jsondata[id];
            if ($(this).datagrid) {
                if (tbdata != undefined) {
                    $(this).datagrid({data: tbdata});
                }
            }
        });
    }

    function acceptAllGrid() {
        iwobj.find(".easyui-datagrid[id]").each(function (index, el) {
            var id = $(el).attr("id");
            var tbdata = jsondata[id];
            if ((tbdata) && ($(this).datagrid)) {
                $(this).datagrid("acceptChanges");
            }
        });
    }
};


$.fn.c_popselectList = function (geturl, onItemClick, alladd) {
    var iwobj = $(this);
    if ($(this).attr('class').indexOf("easyui-window") < 0)
        throw new Error("非easyui-window窗口，不允许弹出!");
    var ulo = iwobj.find("ul");
    if (alladd) {
        iwobj.window({tools: "#pw_list_select_toolbar"});
        $("#pw_list_select_toolbar_btnew").click(function () {
            $.messager.prompt('提示信息', '请输入模板名称:', function (r) {
                if (r) {
                    if (r.indexOf(".") == -1)
                        r = r + ".frx";
                    else
                        r.substr(r.indexOf(".") + 1) + ".frx";
                    var fi = {isnew: true, fname: r};
                    iwobj.window("close");
                    if (onItemClick)
                        onItemClick(fi);
                }
            });
        });
    }
    iwobj.window("open");
    $ajaxjsonget(geturl, function (jsdata) {
        var its = "";
        ulo.html("");
        for (var i = 0; i < jsdata.length; i++) {
            var fi = jsdata[i];
            var item = $(" <li><span>" + fi.fname + "</span></li>").appendTo(ulo);
            item.click(fi, function (event) {
                if (onItemClick)
                    onItemClick(event.data);
                iwobj.window("close");
            });
        }
    });

};


////////////////////////////////////new///////////////////////////
function $showModalDialog(poptions) {
    function getUnixTimestamp() {
        var date = new Date();
        var humanDate = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds(), date.getMilliseconds()));
        return humanDate.getTime() / 1000 - 8 * 60 * 60;
    }

    var bgObjid = "cwinbg" + getUnixTimestamp();//div
    var wdid = "cwin" + getUnixTimestamp();//窗体ID
    this.getWindowID = function () {
        return wdid;
    };

    var bgObj = $("<div></div>");
    bgObj.attr("id", bgObjid);

    var wdObj = $("<div></div>");
    wdObj.attr("id", wdid);
    wdObj.addClass("easyui-window");
    wdObj.attr("title", poptions.title);
    wdObj.attr("data-options", poptions.woptions);
    wdObj.attr("style", poptions.style);

    var layoutobj = $("<div class='easyui-layout' data-options='fit:true,border:false' style='padding: 0px;'></div>");
    var layoutcenter = $("<div data-options=\"region:'center',border:false\" style=\"padding:0px;margin: 0px auto;overflow: hidden\">");
    var ifrmObj = $("<iframe frameborder=0 style='width: 100%;height: 100%' scrolling='no'>不支持的iframe的浏览器</iframe>");
    //ifrmObj.attr("src", poptions.url);
    ifrmObj.appendTo(layoutcenter);

    var layoutft = $("<div data-options=\"region:'south',border:false\" style=\"text-align:right;padding: 5px;background-color: #EBF2FF;height: auto\">");
    var btokObj = $("<a class='easyui-linkbutton'>确定</a>");
    btokObj.attr("data-options", "iconCls:'icon-ok'");
    btokObj.attr("style", "width:80px");
    btokObj.appendTo(layoutft);
    var btcancelObj = $("<a class='easyui-linkbutton'>取消</a>");
    btcancelObj.attr("data-options", "iconCls:'icon-cancel'");
    btcancelObj.attr("style", "width:80px");
    btcancelObj.appendTo(layoutft);
    layoutcenter.appendTo(layoutobj);
    layoutft.appendTo(layoutobj);
    layoutobj.appendTo(wdObj);

    wdObj.appendTo(bgObj);
    bgObj.appendTo("body");
    $.parser.parse(bgObj);
    var onOKCallBack = undefined, onCancelCallBack = undefined;

    this.doOkClick = doclick;
    function doclick() {
        var iwd = ifrmObj[0].contentWindow;
        if (iwd.OnOKModalDialog) {
            var rst = iwd.OnOKModalDialog();
            if (rst != undefined) {
                if (onOKCallBack) {
                    if (onOKCallBack(rst)) wdObj.window("close");
                } else
                    wdObj.window("close");
            }
        } else
            wdObj.window("close");
    }

    this.doCancelClick = doCancel;
    function doCancel() {
        if (onCancelCallBack) onCancelCallBack();
        wdObj.window("close");
    }

    btokObj.click(doclick);
    btcancelObj.click(doCancel);

    this.show = function (parms, onOK, onCancel) {
        onOKCallBack = onOK;
        onCancelCallBack = onCancel;
        ifrmObj.attr("src", "");
        ifrmObj.attr("src", poptions.url + "?parms=" + encodeURIComponent(JSON.stringify(parms))) + "&".concat(Math.random());
        wdObj.window("open");
        //var iwd = ifrmObj[0].contentWindow;
        //if (iwd) {
        //    iwd.sender = this;
        //    if (iwd.OnShowModalDialog)
        //        iwd.OnShowModalDialog(parms);
        // }
    };
    this.close = function () {
        wdObj.window("close");
    };
}

var curthname = undefined;

setEaysuiTheme();

function setEaysuiTheme() {
    var twd = window.top;
    var cth = twd.curthname;
    if (!cth) {
        cth = "default";
        twd.curthname = cth;
    }


    $("link[rel='stylesheet']").each(function () {
        var herf = $(this).attr("href");
        var ts = "easyui/themes/";
        var eidx = herf.indexOf("/easyui.css");
        var bidx = herf.indexOf(ts);
        if ((bidx > 0) && (eidx > 0)) {
            bidx = bidx + ts.length;
            var nherf = herf.substr(0, bidx) + cth + "/easyui.css";
            $(this).attr("href", nherf);
        }
    });

    $("iframe").each(function () {
        var ifr = $(this)[0];
        if (ifr) {
            var wd = ifr.contentWindow;
            if (wd && (wd.setEaysuiTheme)) {
                wd.setEaysuiTheme(cth);
            }
        }
    });
}

function $setFindTextByRow(rec, inputo, findeditable) {
    //alert(JSON.stringify(rec));
    if (findeditable == undefined)
        findeditable = false;
    if (rec.cjoptions) {
        var clsname = rec.cjoptions.easyui_class;
        if (!clsname)alert(JSON.stringify(rec.cjoptions) + "没有设置easyui_class");
        var funcname = getEasuiFuncNameByCls(clsname);
        inputo[funcname](rec.cjoptions);
    } else {
        if (rec.findtype) {
            if (rec.findtype == "date") {
                inputo.datebox({
                    formatter: $dateformattostrrYYYY_MM_DD,
                    editable: findeditable,
                    parser: $date4str
                });
            } else if (rec.findtype == "datetime") {
                inputo.datetimebox({
                    formatter: $dateformattostr,
                    editable: findeditable,
                    parser: $date4str
                });
            } else
                inputo.textbox({});
        } else {
            if (rec.formatter) {
                var comurl = rec.formatter("get_com_url");
                if (comurl.type == "combobox") {
                    inputo.combobox({
                        valueField: comurl.valueField,
                        textField: comurl.textField,
                        editable: findeditable,
                        data: eval('comUrl_' + comurl.index).jsondata
                    });
                } else {
                    if (rec.formatter == $fieldDateFormatorYYYY_MM_DD) {
                        inputo.datebox({
                            formatter: $dateformattostrrYYYY_MM_DD,
                            editable: findeditable,
                            parser: $date4str
                        });
                    } else if (rec.formatter == $fieldDateFormatorYYYY_MM) {
                        inputo.datebox({
                            formatter: $dateformattostrrYYYY_MM,
                            editable: findeditable,
                            parser: $date4str
                        });
                        $parserDatebox2YearMonth(inputo);
                    } else if (rec.formatter == $fieldDateFormator) {
                        inputo.datetimebox({
                            formatter: $dateformattostr,
                            editable: findeditable,
                            parser: $date4str
                        });
                    } else if (rec.formatter == $fieldDateFormatorYYYY_MM_DD_HH_MM) {
                        inputo.datetimebox({
                            formatter: $dateformattostrrYYYY_MM_DD_HH_MM,
                            editable: findeditable,
                            showSeconds: false,
                            parser: $date4str
                        });
                    } else
                        inputo.textbox({});
                }
            } else {
                inputo.textbox({});
            }
        }
    }
}

var $$demoptions = {
    filter: "",
    JPAClass: ""
};
function init_jpaattr(options) {
    var $tdht = "<td cjoptions=\"fdname:'{{fdname}}'\">{{uscaption}}</td>"
        + "<td><input cjoptions=\"{{options}}\" {{iptattrs}}/></td>";

    var filter = options.filter;
    if (!filter) {
        alert("方法【init_jpaattr】参数【filter】不能为空");
        return;
    }
    var JPAClass = options.JPAClass;
    if (!JPAClass) {//试着从窗体配置获取参数
        if (frmOptions) {
            JPAClass = frmOptions.JPAClass;
        } else {
            alert("方法【init_jpaattr】参数【JPAClass】不能为空");
            return;
        }
    }
    initui();
    function initui() {
        var url = _serUrl + "/web/common/getJPAAttrsByCls.co?jpaclassname=" + JPAClass;
        $ajaxjsonget(url, function (jsdata) {
            var colnum = jsdata.colct;
            colnum = parseInt(colnum);
            if (isNaN(colnum))colnum = 2;

            var rows = jsdata.shwjpaattrlines;
            var ht = "";
            var cct = 0;
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                if (parseInt(row.usable) == 1) {
                    if (!$isEmpty(row.datasourse)) {
                        var ds = $.c_parseOptionsStr(row.datasourse);
                        var cu = {iswie: true};
                        cu.index = ds.ix;
                        cu.type = ds.tp;
                        cu.url = ds.url;
                        cu.valueField = ds.vf;
                        cu.textField = ds.tf;
                        if (comUrls && (comUrls instanceof Array)) {
                            comUrls.push(cu);
                        }
                    }
                    var rht = $tdht;
                    if (cct == 0)
                        rht = "<tr>" + rht;
                    if (cct + 1 >= colnum) {
                        rht = rht + "</tr>";
                        cct == 0;
                    } else
                        cct++;
                    rht = rht.replace(new RegExp("{{fdname}}", "g"), row.fdname);
                    if (!row.fdcaption)row.fdcaption = "";
                    rht = rht.replace(new RegExp("{{uscaption}}", "g"), row.uscaption);
                    var options = row.inputoption;
                    if ($isEmpty(options)) {
                        options = "easyui_class:'easyui-textbox',fdname:'" + row.fdname + "'";
                    }
                    rht = rht.replace(new RegExp("{{options}}", "g"), options);
                    var iptattrs = "style='height:20px;width: 120px'";
                    rht = rht.replace(new RegExp("{{iptattrs}}", "g"), iptattrs);
                    ht += rht;
                }
            }
            if (!$isEmpty(ht)) {
                if (right(ht, 5) != "</tr>")
                    ht = ht + "</tr>";
                ht = "<table>" + ht + "</table>";
                //console.error(ht);
                $(filter).html(ht);
            } else {
                $(filter).html("");
            }
        }, function (err) {
            alert(err.errmsg);
        }, false);
    }

    function right(mainStr, lngLen) {
        if (mainStr.length - lngLen >= 0 && mainStr.length >= 0 && mainStr.length - lngLen <= mainStr.length) {
            return mainStr.substring(mainStr.length - lngLen, mainStr.length)
        }
        else {
            return null
        }
    }
}

