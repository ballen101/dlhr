var _mods = null;
var _m_menus = [];
var _loginURL = undefined;
var _loadUsesMessageInterval = undefined;
var _allumsg = [];
var _msglt = undefined;
var _systemparmts = undefined;
var initscolltext = undefined;

function $OnCorsairReady() {
    if (checkbrower()) {
        getNoLoginParms();
        if (initscolltext)
            initscolltext();
    } else {
        $("body").html("本系统不支持低于IE8的浏览器！");
    }
}


function checkbrower() {
    if (browinfo.browser == "IE") {
        var v = parseInt(browinfo.version);
        if (v <= 7) {
            alert("本系统不支持低于IE8的浏览器！");
            return false;
        }
        if (v <= 10) {
            alert("亲，请使用360极速、火狐或Chrome浏览器访问本系统，现若使用IE8或其它低版本浏览器，系统页面加载速度较慢。为优化工作效率，请到信息化服务支持网报障升级浏览器，谢谢！");
            return true;
        }
        return true;
    } else
        return true;
}

function getNoLoginParms() {
    $ajaxjsonget(_serUrl + "/web/common/getSystemInfo.co?parmname=ExtLoginUrl", function (jsondata) {
        if (jsondata.length > 0) {
            var pv = jsondata[0].parmvalue;
            if ((pv != null) && (pv.length > 0)) {
                if (pv.substr(0, 1) != "/")
                    pv = "/" + pv;
                _loginURL = _serUrl + pv;
            }
        }
        getSystemInfo();
        initselcts();
        resettabhdclick();
    }, function (XMLHttpRequest, textStatus, errorThrown) {
        alert("服务器开小差了哦！");
    })
}

var indexurl = undefined;
var ReadSystemMessageTime = undefined;
function getSystemInfo() {
    $ajaxjsonget(_serUrl + "/web/common/getSystemInfo.co", function (jsondata) {
        _systemparmts = jsondata;
        for (var i = 0; i < jsondata.length; i++) {
            var pp = jsondata[i];
            var parmname = pp.parmname;
            var parmvalue = pp.parmvalue;
            switch (parmname) {
                case "WebAPPTitle":
                    document.title = parmvalue;
                    break;
                case "WebAPPName":
                    $("#appname").html(parmvalue);
                    break;
                case "CopyRight":
                    $("#copyrightinfo").html(parmvalue);
                    break;
                case "ShowUserInfo":
                    if (parseInt(parmvalue) == 1) {
                        $("#userinfo").css("display", "inline");
                        $("#username").html($C.UserInfo.displayname());
                    } else {
                        $("#userinfo").css("display", "none");
                    }
                    break;
                case "ShowMailInfo":
                    if (parseInt(parmvalue) == 1) {
                        $("#mailinfo").css("display", "inline");
                    } else {
                        $("#mailinfo").css("display", "none");
                    }
                    break;
                case "ShowWorkInfo":
                    if (parseInt(parmvalue) == 1) {
                        $("#workinfo").css("display", "inline");
                        $ajaxjsonget($C.cos.getWfCount(), function (data) {
                            document.getElementById("wfcounts").innerHTML = "(" + data[0].ct + ")";
                        }, function () {
                            alert("获取流程数量错误！");
                        });
                    } else {
                        $("#workinfo").css("display", "none");
                    }
                    break;
                case "HomeUrl":
                    $("#HomeUrl").attr("href", parmvalue);
                    break;
                case "HelpURL":
                    $("#HelpURL").attr("href", parmvalue);
                    break;
                case "ReadSystemMessageTime":
                    ReadSystemMessageTime = parseInt(parmvalue);
                    break;
                case "INDEXPAGE":
                    indexurl = parmvalue;
                    break;
            }
        }
        getMenus();
    }, function (err) {
        alert(err.errmsg)
    })
}

//获取
function getSystemParmValue(parmname) {
    if (_systemparmts == undefined)
        return null;
    for (var i = 0; i < _systemparmts.length; i++) {
        var pp = _systemparmts[i];
        if (parmname == pp.parmname)
            return pp.parmvalue;
    }
    return null;
}

function fetchallusmsg() {
    //获取所有未读消息  并记录最后时间
    var url = _serUrl + "/web/usermsg/findUserMSGs.co?tp=3";
    $ajaxjsonget(url, function (jsdata) {
        _allumsg = jsdata;
        if (jsdata.length > 0)
            _msglt = jsdata[0].mid;
        for (var i = 0; i < jsdata.length; i++) {
            showusmsg(jsdata[i].msgtitle, jsdata[i].msgcontext, jsdata[i].mid);
        }
        if (ReadSystemMessageTime >= 5) {
            _loadUsesMessageInterval = window.setInterval("fetchusmsg()", ReadSystemMessageTime * 1000);
        }
    }, function (err) {
        alert(err.errmsg);
    }, true, null, true);
}

function fetchusmsg() {
    var url = _serUrl + "/web/usermsg/findUserMSGs.co?tp=3";
    if (_msglt)
        url = url + "&mmid=" + _msglt;
    $ajaxjsonget(url, function (jsdata) {
        if (jsdata.length > 0) {
            _allumsg = _allumsg.concat(jsdata);
            _msglt = jsdata[0].mid;
            for (var i = 0; i < jsdata.length; i++) {
                showusmsg(jsdata[i].msgtitle, jsdata[i].msgcontext, jsdata[i].mid);
            }
        }
    }, function (err) {
        alert(err.errmsg);
    }, true, null, true);
}

function doshowuswindow() {
    pw_usermessage.show({}, function () {
        return true;
    });
}

var pw_usermessage = undefined;
function initselcts() {
    var poptions = {
        url: "common/shwusermessage.html",
        title: "用户消息",
        woptions: "modal:true,closed:true,border:false,collapsible:false,minimizable:false,iconCls:'application_home'",
        style: "width:700px;height:500px;padding: 0px",
        chtch: false
    };
    pw_usermessage = new $showModalDialog(poptions);
}


function showusmsg(title, msgcontext, mid) {
    $("#usmeginfonum").html("(" + _allumsg.length + ")")
    var msg = "<div style='font-size: 12px'>"
        + msgcontext + "<br><span class='txtlinked' onclick='alert(" + mid + ")'>(查看详情)</span></div>";
    $.messager.show({
        title: title,
        msg: msg,
        timeout: 0,
        showType: 'slide'
    });
}

function toMywork() {
    var pw = this.top;
    (pw) ? pw.findNodeClickByMenuID(undefined, "7") : findNodeClickByMenuID(undefined, "7");
}

function dologinout() {
    $ajaxjsonget($C.cos.loginout(), function (data) {
        //$C.UserInfo.removelogininfos();
        if ((window.top._loginURL != undefined) && (window.top._loginURL.length > 0)) {
            window.top.location = window.top._loginURL;
        } else
            window.top.location = _serUrl + "/webapp/common/login.html";
    }, function (XMLHttpRequest, textStatus, errorThrown) {
        $.messager.alert('错误', '登录错误!', 'error');
    });
}

function getMenus() {
    var url = _serUrl + "/web/menu/getIndexMenuTree.co";
    $ajaxjsonget(url, function (jsdata) {
        fetchallusmsg();
        if ($isEmpty(indexurl))
            $("#tabiframe0").attr("src", "common/mainpage.html");
        else
            $("#tabiframe0").attr("src", indexurl);
        jsdata = dealTreeData(jsdata);
        createMod_Menu(jsdata);
    }, function (err) {
        $.messager.alert('错误', '获取菜单模块错误:' + err.errmsg, 'error');
    });
}

function createMod_Menu(menus) {
    for (var i = 0; i < menus.length; i++) {
        var menu1 = menus[i];
        var hs = "<ul id='treeid_" + menu1.menuid + "' class='easyui-tree'></ul>";
        var acct = $("#main_menu_id").accordion("add", {
            title: menu1.language1,
            content: hs,
            iconCls: menu1.ico,
            selected: false
        });
        $("#treeid_" + menu1.menuid).tree({
            animate: true,
            lines: false,
            onClick: onClickMenu,
            onContextMenu: onTreeContextMenu,
            formatter: treeMenuFormatter
        });
        if (menu1.children) {
            $("#treeid_" + menu1.menuid).tree("loadData", menu1.children);
            _m_menus.push("#treeid_" + menu1.menuid);//每个一级模块 一棵树
        }
    }
}

//处理树形数据
function dealTreeData(menus) {
    for (var i = 0; i < menus.length; i++) {
        var menu = menus[i];
        menu.id = menu.menuid;
        menu.text = menu.language1;
        if ((menu.ico) && (menu.ico != "NULL") && (menu.ico.length > 0)) {
            menu.iconCls = menu.ico;
        } else {
            if (menu.children)
                menu.menu = "icon-folder";
            else
                menu.iconCls = "icon-star";
        }
        if (menu.clas == "2") {
            menu.text = "---------------";
            menu.iconCls = "icon-blank";
        }
        if (menu.children) {
            menu.state = "closed";
            menu.children = dealTreeData(menu.children);
        }
    }
    return menus;
}


var curSelectNode = undefined;
function onTreeContextMenu(e, node) {
    e.preventDefault();
    $(this).tree('select', node.target);
    $('#popmm').menu('show', {
        left: e.pageX,
        top: e.pageY
    });
    curSelectNode = node;
}

function buildModMenuUi() {
    for (var i in _mods) {
        var mod = _mods[i];
        if (mod.isitem == 2) {
            buildMod(mod);
        }
    }
    //setDraable();
}

function append2faststart() {
    var node = curSelectNode;
    var url = node.weburl;
    var tp = 2;
    if ((url == undefined) || (tp == undefined)) {
        $.messager.alert('错误', '没有设置URL，不允许创建快捷方式!', 'error');
        return;
    }
    var jsdata = {id: node.menuid, tp: tp};
    var url = _serUrl + "/web/user/adduserShortCut.co";
    $ajaxjsonpost(url, JSON.stringify(jsdata), function (rst) {
        var ifm = $("#tabiframe0")[0];
        if (ifm) {
            var src = ifm.src;
            ifm.src = "";
            ifm.src = src;
        }
    }, function (err) {
        alert(err.errmsg)
    });
}

function showpopwin() {
    var node = curSelectNode;
    onClickMenu(node, null, 2);
}

function treeMenuFormatter(node) {
    if (node.clas != 1) {
        return "<span style='color: #B4B4B4'>" + node.text + "</span>";
    } else
        return node.text;
}


function findNodeClickByMenuID(w, menuid) {
    var node = undefined;
    var menuids = (w) ? w._m_menus : _m_menus;
    for (var i = 0; i < menuids.length; i++) {
        var mo = $(menuids[i], (w) ? w.document : document);
        var nodes = mo.tree("getRoots");
        node = mo.tree("find", menuid);
        if (node)
            break;
    }
    if (node)
        onClickMenu(node, undefined);
    else
        $.messager.alert('错误', '没有相应菜单权限!', 'error');
}

function PerDeskClick(title, url, spcetype, flag, menutag) {
    var edittps = {};
    edittps.isedit = true;
    edittps.issubmit = true;
    edittps.isview = true;
    edittps.isupdate = true;
    if (url.indexOf("?") >= 0)
        url = url + "&edittps=" + encodeURI(JSON.stringify(edittps)) + "&title=" + title;
    else
        url = url + "?edittps=" + encodeURI(JSON.stringify(edittps)) + "&title=" + title;
    url = url + "&spcetype=" + spcetype + "&showtype=1";
    if (flag) url = url + "&flag=" + flag;
    if (menutag) url = url + "&menutag=" + menutag;
    //alert(url);
    showTab(undefined, title, undefined, url);
}

function onClickMenu(node, objectid, swtp) {
    var mtree = $(node.target).closest(".easyui-tree");
    if (mtree) {
        if (node.state == 'open') {
            mtree.tree("collapse", node.target);
        }
        if (node.state == 'closed') {
            mtree.tree("expand", node.target);
        }
    }
    var title = node.language1;
    var url = node.weburl;
    var ifid = node.menuid;
    var iconCls = node.ico;

    if ((!url) || (url.length == 0) || (url == "NULL")) {
        //$.messager.alert('错误', '菜单没有指定URL!', 'error');
        return false;
    }
    //isedit":"2","issubmit":"1","isview":"2"
    var edittps = {};
    edittps.isedit = (parseInt(node.isedit) == 1);
    edittps.issubmit = (parseInt(node.issubmit) == 1);
    edittps.isview = (parseInt(node.isview) == 1);
    edittps.isupdate = (parseInt(node.isupdate) == 1);

    url = url + "?edittps=" + encodeURI(JSON.stringify(edittps)) + "&flag=" + node.flag + "&menutag=" + node.menutag + "&title=" + title;
    if (objectid) {
        url = url + "&id=" + objectid;
    }
    var showtype = (swtp) ? swtp : parseInt(node.showtype);
    if (showtype == 2) {
        window.open(url);
    } else
        showTab(iconCls, title, ifid, url);
}


function OnShortMenuClick(menuid) {
    findNodeClickByMenuID(undefined, menuid);
}

var _collapse_ed = false;
function resettabhdclick() {
    var ul = $('#main_tabs_id').children('div.tabs-header').find('ul.tabs');
    $(ul).children().each(function () {
        $(this).unbind("dblclick");
        $(this).dblclick(function () {
            if (!_collapse_ed) {
                $("#title_div").parent().fadeOut();
                //$("#menudiv").parent().fadeOut();
                $("body").layout("collapse", "west");
                $("#copyrightinfo").parent().parent().fadeOut("normal", function () {
                    $("body").layout("resize", {
                        width: '100%',
                        height: '100%'
                    });
                });
            } else {
                $("#title_div").parent().fadeIn();
                // $("#menudiv").parent().fadeIn();
                $("body").layout("expand", "west");
                $("#copyrightinfo").parent().parent().fadeIn("normal", function () {
                    $("body").layout("resize", {
                        width: '100%',
                        height: '100%'
                    });
                });
            }
            _collapse_ed = !_collapse_ed;
        });
    });
}

function showTab(iconCls, title, ifid, src) {
    var maintab = $('#main_tabs_id');
    if (maintab.tabs('exists', title)) {
        maintab.tabs('select', title);
    } else {
        var content = $("<iframe scrolling='no' frameborder='0' style='width:100%;height:100%;overflow: hidden'></iframe>");
        content.attr("id", ifid).attr("src", src);
        var tabop = {
            id: 'tab' + ifid,
            title: title,
            content: content.prop("outerHTML"),
            fit: true,
            closable: true
        };
        if ((iconCls) && (iconCls.length > 0) && (iconCls != "NULL"))
            tabop.iconCls = iconCls;
        maintab.tabs('add', tabop);
        var tab = maintab.tabs("getSelected");
        tab.css("overflow", "hidden");
        tab.css("padding", "0px");
        resettabhdclick();
    }
}


function maintablecloseall() {
    var maintab = $('#main_tabs_id');
    var tabs = maintab.tabs("tabs");
    var l = tabs.length;
    for (var i = 1; i < l; i++) {
        maintab.tabs('close', 1);
    }
}

function reloadCurLabPage() {
    var tab = $('#main_tabs_id').tabs('getSelected');
    var ifm = tab.find("iframe")[0];
    if (ifm) {
        var src = ifm.src;
        ifm.src = "";
        ifm.src = src;
    }
}
