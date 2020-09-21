/**
 * Created by Administrator on 2014-10-15.
 */
$(document).ready(function () {
    getAllMods();
    $("#mod_form").c_initDictionary();
    $("#menu_form").c_initDictionary();
    testcss();
});

function setNodeIco(t, node) {
    if (t == "MOD") {
        node.usesable = node.state;
        node.iconCls = node.modidpath;
    }
    if (t == "MENU") {
        node.usesable = node.state;
        if (node.clas == 1) {
            if (node.isitem == 1) {
                node.iconCls = "icon-text_ab";
            }
            if (node.isitem == 2) {
                node.iconCls = "icon-folder";
            }
        }
        if (node.clas == 2) {
            node.iconCls = "icon-text_horizontalrule";
        }
    }
}

function setNodesIco(t, nodes) {
    for (var i = 0; i < nodes.length; i++) {
        node = nodes[i];
        setNodeIco(t, node);
        if (node.children) {
            setNodesIco(t, node.children);
        }
    }
}

function getAllMods() {
    var url = $C.cos.getModTree + "?roleid=-1";//-1 获取所有模块  不设置checkbox
    $ajaxjsonget(url, function (jsondata) {
        setNodesIco("MOD", jsondata);
        $("#modtree").tree({data: jsondata});
    }, function () {
        alert("错误");
    });
}

function modclick() {
    var node = $("#modtree").tree("getSelected");
    if (!node) return;
    $ajaxjsonget($C.cos.getModMenuTree + "?roleid=-1&modid=" + node.id + "&type=all",
        function (jsondata) {
            setNodesIco("MENU", jsondata);
            for (var i = 0; i < jsondata.length; i++) {
                jsondata[i].state = "closed";
            }
            $("#menutree").tree("loadData", jsondata);
        },
        function () {
        });
}

function modDClick() {
    modmenu_action(1, $C.action.Edit);
}

function menuDClick() {
    modmenu_action(2, $C.action.Edit)
}

function modmenu_action(t, act) {
    if (t == 1) {
        modaction(act);
    }
    if (t == 2) {
        menuaction(act);
    }
}

function modaction(act) {
    var isnew, jsondata;
    var otb = $('#modtree');
    var node = otb.tree('getSelected');
    var popw = $("#mod_info_w");
    if (act == $C.action.New) {
        isnew = true;
        jsondata = {};
        jsondata.modpid = 53;
        jsondata.isitem = 2;
        jsondata.usesable = 1;
        jsondata.issystem = 2;
    }
    if (act == $C.action.New2) {
        if (!node) {
            $.messager.alert('错误', '没选定的模块!', 'error');
            return;
        }
        isnew = true;
        jsondata = {};
        jsondata.usesable = 1;
        jsondata.issystem = 2;
        jsondata.isitem = 1;
        if (node.isitem == 1) {
            jsondata.modpid = node.modpid;
        }
        if (node.isitem == 2) {
            jsondata.modpid = node.modid;
        }
    } else if (act == $C.action.Edit) {
        if (!node) {
            $.messager.alert('错误', '没选定的模块!', 'error');
            return;
        }
        isnew = false;
        jsondata = node;
    } else if (act == $C.action.Del) {
        if (!node) {
            $.messager.alert('错误', '没选定的角色!', 'error');
            return;
        }
        //do del
        $.messager.confirm('提醒', '确认删除?', function (r) {
            if (r) {
                $ajaxjsonget($C.cos.delMod + "?modid=" + node.modid, function (jsondata) {
                    if (jsondata.result == "OK")
                        otb.tree("remove", node.target);
                }, function () {
                    $.messager.alert('错误', '删除模块错误!', 'error');
                });
            }
        });
        return;
    }

    popw.c_popInfo({
        isNew: isnew,
        jsonData: jsondata,
        onOK: function (jsondata) {
            jsondata.state = jsondata.usesable;//冲突
            $ajaxjsonpost($C.cos.saveMod, JSON.stringify(jsondata), function (reData) {
                jsondata.id = jsondata.modid;
                jsondata.usesable = jsondata.state;
                jsondata.text = jsondata.modname;
                jsondata.iconCls = jsondata.modidpath;
                if (isnew) {
                    if (jsondata.isitem == 2) {
                        otb.tree("append", {data: [jsondata]});
                    } else {
                        otb.tree("append", {parent: otb.tree("find", jsondata.modpid).target, data: [jsondata]});
                    }
                } else {
                    jsondata.target = node.target;
                    otb.tree("update", jsondata);
                }
            }, function () {
                alert("保存错误！");
            });
            return true;
        }
    });
}

function testcss() {
    var ics = "easyui/themes/icon.css";
    var ss = undefined;
    for (var i = 0; i < document.styleSheets.length; i++) {
        ss = document.styleSheets[i];
        var href = ss.href;
        if (href.substr(href.length - ics.length, ics.length) == ics)
            break;
    }
    if (!ss) return;
    var rus = ss.cssRules || ss.rules;
    var icons = [];
    for (var i = 0; i < rus.length; i++) {
        var s = rus[i].selectorText;
        s = s.substr(1, s.length - 1);
        icons.push({id: s, text: s});
    }
    $("#modidpath_id").combobox({
        data: icons,
        valueField: 'id',
        textField: 'text',
        formatter: function (row) {
            return "<span class=" + row.text + " style='width:50px'>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</span>";
        }
    });

    $("#menuidpath_id").combobox({
        data: icons,
        valueField: 'id',
        textField: 'text',
        formatter: function (row) {
            return "<span class=" + row.text + " style='width:50px'>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</span>";
        }
    });
}

function menuaction(act) {
    var isnew, jsondata;
    var otb = $('#menutree');
    var node = otb.tree('getSelected');
    var popw = $("#menu_info_w");
    if (act == $C.action.New) {
        isnew = true;
        jsondata = {};
        jsondata.menupid = 21;
        jsondata.isitem = 2;
        jsondata.usesable = 1;
        jsondata.issystem = 2;
        jsondata.clas = 1;
        jsondata.modid = $('#modtree').tree('getSelected').modid;
    }
    if (act == $C.action.New2) {
        if (!node) {
            $.messager.alert('错误', '没选定的菜单!', 'error');
            return;
        }
        isnew = true;
        jsondata = {};
        jsondata.usesable = 1;
        jsondata.issystem = 2;
        jsondata.isitem = 1;
        jsondata.clas = 1;
        jsondata.modid = $('#modtree').tree('getSelected').modid;
        if (node.isitem == 1) {
            jsondata.menupid = node.menupid;
        }
        if (node.isitem == 2) {
            jsondata.menupid = node.menuid;
        }
    } else if (act == $C.action.Edit) {
        if (!node) {
            $.messager.alert('错误', '没选定的数据!', 'error');
            return;
        }
        isnew = false;
        jsondata = node;
    } else if (act == $C.action.Del) {
        if (!node) {
            $.messager.alert('错误', '没选定的数据!', 'error');
            return;
        }
        //do del
        $.messager.confirm('提醒', '确认删除?', function (r) {
            if (r) {
                $ajaxjsonget($C.cos.delMenu + "?menuid=" + node.menuid, function (jsondata) {
                    if (jsondata.result == "OK")
                        otb.tree("remove", node.target);
                }, function () {
                    $.messager.alert('错误', '删除菜单错误!', 'error');
                });
            }
        });
        return;
    }
    loadMenuCosTree(jsondata.menuid);
    popw.c_popInfo({
        isNew: isnew,
        jsonData: jsondata,
        onOK: function (jsondata) {
            jsondata.state = jsondata.usesable;//冲突
            jsondata.cos = getCheckedCos();
            //console.error(JSON.stringify(jsondata));
            $ajaxjsonpost($C.cos.saveMenu, JSON.stringify(jsondata), function (jsondata) {
                jsondata.id = jsondata.menuid;
                jsondata.usesable = jsondata.state;
                jsondata.text = jsondata.menuname;
                setNodeIco("MENU", jsondata);
                if (isnew) {
                    if (jsondata.isitem == 2) {
                        otb.tree("append", {data: [jsondata]});
                    } else {
                        otb.tree("append", {parent: otb.tree("find", jsondata.menupid).target, data: [jsondata]});
                    }
                } else {
                    jsondata.target = node.target;
                    otb.tree("update", jsondata);
                }
            }, function () {
                alert("保存错误！");
            });
            return true;
        }
    });
}

function loadMenuCosTree(menuid) {
    if ($isEmpty(menuid))
        menuid = -1;
    var url = _serUrl + "/web/common/getCoTreeMenu.co?menuid=" + menuid;
    $ajaxjsonget(url, function (jsdata) {
        $("#cotreeid").tree("loadData", jsdata);
    }, function (msg) {
        alert("获取菜单CO错误:" + msg);
    });
}

function onCoTreeSelect(node) {
    if (!node) return;
    if (node.method)
        $("#methodid").html(node.method);
    else
        $("#methodid").html("");
    if (node.auth != undefined)
        $("#authid").html(node.auth);
    else
        $("#authid").html("");
    if (node.note)
        $("#noteid").html(node.note);
    else
        $("#noteid").html("");
}

function getCheckedCos() {
    var nodes = $("#cotreeid").tree("getChecked");
    var cos = [];
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (node.name) {
            cos.push({name: node.name});
        }
    }
    return cos;
}

var copyedMenuCos = undefined;

function onmenutreemenushow() {
    var item = $('#menutree_menu').menu('findItem', '粘贴CO权限');
    if (copyedMenuCos == undefined)
        $('#menutree_menu').menu('disableItem', item.target);
    else
        $('#menutree_menu').menu('enableItem', item.target);
}

function copyMCos() {
    var node = $('#menutree').tree('getSelected');
    if (!node) {
        $.messager.alert('错误', '没选定的数据!', 'error');
        return;
    }
    var menuid = node.menuid;
    if ($isEmpty(menuid)) {
        $.messager.alert('错误', '没选定的数据!', 'error');
        return;
    }
    var url = _serUrl + "/web/common/getCosByMenu.co?menuid=" + menuid;
    $ajaxjsonget(url, function (jsdata) {
        copyedMenuCos = undefined;
        copyedMenuCos = jsdata;
        $.messager.alert('提示', '已复制【' + jsdata.length + '】条CO!');
    }, function (msg) {
        alert("获取菜单CO错误:" + msg);
    });
}

function pastMCos() {
    if (copyedMenuCos == undefined) {
        $.messager.alert('错误', '请先复制菜单CO!', 'error');
        return;
    }
    var node = $('#menutree').tree('getSelected');
    if (!node) {
        $.messager.alert('错误', '没选定的数据!', 'error');
        return;
    }
    var menuid = node.menuid;
    if ($isEmpty(menuid)) {
        $.messager.alert('错误', '没选定的数据!', 'error');
        return;
    }
    $.messager.confirm('确认', '粘贴后将会覆盖菜单【' + node.menuname + '】CO权限，确认继续?', function (r) {
        if (r) {
            var postdata = {mueuid: menuid, conames: copyedMenuCos};
            var url = _serUrl + "/web/menu/saveMenuCos.co";
            $ajaxjsonpost(url, JSON.stringify(postdata), function (jsondata) {
                copyedMenuCos = undefined;
                $.messager.alert('提示', '完成粘贴!');
            }, function (msg) {
                $.messager.alert('错误', '粘贴数据错误:' + msg, 'error');
            });
        }
    });

}









