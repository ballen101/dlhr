/**
 * Created by Administrator on 2014-10-09.
 * 有时间重搞
 */
var allMenuTree = undefined;
$(document).ready(function () {
    showAllRoles();
    getAllMenuTree();
});

function showAllRoles() {
    $ajaxjsonget($C.cos.getRoles(), function (jsondata) {
        $("#tb_roles").datagrid({
            data: jsondata,
            onDblClickRow: function (rowIndex, rowData) {
                toolbarAction(0, $C.action.Edit);
            }
        });
    }, function (err) {
        $.messager.alert('错误', err, 'error');
    }, true);
}

function getAllMenuTree() {
    var url = _serUrl + "/web/menu/getAllMenuTree.co";
    $ajaxjsonget(url, function (jsdata) {
        setNodesIco(jsdata);
        allMenuTree = jsdata;
        $("#menutree").tree({onCheck: onMenuTreeCheck});
    }, function (err) {
        $.messager.alert('错误', err, 'error');
    });

}

function toolbarAction(mod, action) {
    switch (action) {
        case $C.action.Edit:
            showRoleInfo($C.action.Edit);
            break;
        case $C.action.Append:
            showRoleInfo($C.action.New);
            break;
        case $C.action.Del:
            showRoleInfo($C.action.Del);
            break;
    }
}

function showRoleInfo(tp) {
    var otb = $('#tb_roles');
    var row = otb.datagrid('getSelected');
    var isnew, posturl;
    if (tp == $C.action.New) {//新建
        isnew = true;
        var jsondata = {};
    }
    if (tp == $C.action.Del) {//删除
        if (!row) {
            $.messager.alert('错误', '没选定的角色!', 'error');
            return;
        }
        $.messager.confirm('提醒', '确认删除?', function (r) {
            if (r) {
                $ajaxjsonget($C.cos.delRole + "?roleid=" + row.roleid, function (jsondata) {
                    if (jsondata.result == "OK")
                        otb.datagrid("deleteRow", otb.datagrid("getRowIndex", row));
                }, function () {
                    $.messager.alert('错误', '删除角色错误!', 'error');
                });
            }
        });
        return;
    }
    if (tp == $C.action.Edit) {//编辑
        if (!row) {
            $.messager.alert('错误', '没选定的角色!', 'error');
            return;
        }
        var jsondata = row;
        isnew = false;
    }

    $("#roleInfoWindow").c_popInfo({
        isNew: isnew,
        jsonData: jsondata,
        onOK: function (jsondata) {
            var mms = getChecketModMenus();
            var pdata = {isnew: isnew, menus: mms, jsondata: jsondata};
            var url = _serUrl + "/web/role/saveRole.co";
            $ajaxjsonpost(url, JSON.stringify(pdata), function (jsondata) {
                if (isnew) {
                    otb.datagrid("appendRow", jsondata)
                } else {
                    otb.datagrid("updateRow", {index: otb.datagrid("getRowIndex", row), row: jsondata});
                }
            }, function () {
                alert("保存角色错误")
            });
            return true;
            /*jsondata = {isnew: isnew.isNew, mods: mms.mods, menus: mms.menus, jsondata: jsondata};
             */
        },
        onShow: function () {
            _AllMenuTreeData = [];
            /*
             var url = $C.cos.getModTree + "?roleid=" + row.roleid;
             $ajaxjsonget(url, function (jsondata) {
             setTreeChecked(jsondata);
             setNodesIco("MOD", jsondata);
             $("#modtree").tree({data: jsondata, onClick: modclick});
             }, function () {
             $.messager.alert('错误', '查询数据错误！', 'error');
             });*/

            if ((row.roleid) && (!isnew)) {
                showRoleMenuCheck();
                $ajaxjsonget($C.cos.getUsersByRoleid() + "?roleid=" + row.roleid, function (jsondata) {
                    //setTreeChecked(jsondata);
                    $("#role_users").datagrid({data: jsondata});
                }, function () {
                    $.messager.alert('错误', '查询数据错误！', 'error');
                });
            } else {
                $("#role_users").datagrid("loadData", []);
                $("#menutree").tree("loadData", []);
            }
            $("#tabmain").tabs("select", 0);
        }
    });
}


function setNodesIco(nodes) {
    for (var i = 0; i < nodes.length; i++) {
        node = nodes[i];
        node.id = node.menuid;
        node.text = node.language1;
        if (parseInt(node.clas) == 2) {
            node.iconCls = "icon-text_horizontalrule";
            node.text = "------";
        } else {
            if ((node.ico) && (node.ico != "NULL") && (node.ico.length > 0)) {
                node.iconCls = node.ico;
            } else {
                if (node.children)
                    node.iconCls = "icon-folder";
                else
                    node.iconCls = "icon-text_horizontalrule";
            }
        }
        if (node.children) {
            setNodesIco(node.children);
        }
    }
}

//check tree

//设置所有菜单取消选择
function setAllMenuCheckFalse(nodes) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        node.checked = false;
        if (node.children) {
            node.state = "closed";
            setAllMenuCheckFalse(node.children);
        }
    }
}
//获取角色拥有的菜单权限
var _menuids = undefined;
function showRoleMenuCheck() {
    var node = $("#tb_roles").treegrid("getSelected");
    if (!node) {
        $.messager.alert('错误', '没选定的角色!', 'error');
        return;
    }
    var url = _serUrl + "/web/menu/getRoleMenus.co?roleid=" + node.roleid;
    $ajaxjsonget(url, function (jsdata) {
        setAllMenuCheckFalse(allMenuTree);
        setMenuRoleChecked(allMenuTree, jsdata);
        _menuids = jsdata;
        $("#menutree").tree("loadData", allMenuTree);
    }, function (err) {
        $.messager.alert('错误', err, 'error');
    });
}

function setMenuRoleChecked(nodes, mids) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        node.checked = isMenuIDInIDs(mids, node.menuid);
        if (node.children)
            setMenuRoleChecked(node.children, mids);
    }
}

function isMenuIDInIDs(mids, menuid) {
    for (var i = 0; i < mids.length; i++) {
        // console.error(mids[i].menuid + ":" + menuid);
        if (mids[i].menuid == menuid)
            return true;
    }
    return false;
}


//////////////////old

/*
 var _AllMenuTreeData = [];
 function setTreeChecked(nodes) {
 for (var i = 0; i < nodes.length; i++) {
 var node = nodes[i];
 if ((node.roleid != undefined) && (node.roleid != 'NULL')) {
 node.checked = true;
 $ajaxjsonget($C.cos.getModMenuTree + "?roleid=" + $('#tb_roles').datagrid("getSelected").roleid + "&modid=" + node.id + "&type=all",
 function (jsondata, node) {
 setTreeChecked(jsondata);
 _AllMenuTreeData[node.id] = jsondata;
 },
 function () {
 }, true, node);
 }
 if (node.children) {
 setTreeChecked(node.children);
 }
 }
 }

 function showMenuChecked() {
 $ajaxjsonget($C.cos.getModMenuTree + "?roleid=" + $('#tb_roles').datagrid("getSelected").roleid + "&type=all",
 function (jsondata, node) {
 setTreeChecked(jsondata);
 setNodesIco(jsondata);
 _AllMenuTreeData[node.id] = jsondata;
 var menudata = JSON.parse(JSON.stringify(jsondata));
 $("#menutree").tree("loadData", menudata);
 },
 function () {
 }, true, node);
 }

 function modclick(node) {
 if (_AllMenuTreeData[node.id]) {
 var menudata = JSON.parse(JSON.stringify(_AllMenuTreeData[node.id]));
 $("#menutree").tree("loadData", menudata);
 } else {
 $ajaxjsonget($C.cos.getModMenuTree + "?roleid=" + $('#tb_roles').datagrid("getSelected").roleid + "&modid=" + node.id + "&type=all",
 function (jsondata, node) {
 setTreeChecked(jsondata);
 setNodesIco("MENU", jsondata);
 _AllMenuTreeData[node.id] = jsondata;
 var menudata = JSON.parse(JSON.stringify(jsondata));
 $("#menutree").tree("loadData", menudata);
 },
 function () {
 }, true, node);
 }
 }*/


function setAllCheckFalse(nodes) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        delete node.checked;
        if (node.children) {
            setAllCheckFalse(node.children);
        }
    }
}

function resetAllChildCheck(node) {
    if (node.children) {
        for (var i = 0; i < node.children.length; i++) {
            var cnode = node.children[i];
            var checked = isMenuIDInIDs(_menuids, cnode.menuid);
            if (checked)
                $('#menutree').tree("check", cnode.target);
            else
                $('#menutree').tree("uncheck", cnode.target);
            resetAllChildCheck(cnode);
        }
    }
}

function setAllChildCheck(node, checked) {
    if (node.children) {
        for (var i = 0; i < node.children.length; i++) {
            var cnode = node.children[i];
            if (checked)
                $('#menutree').tree("check", cnode.target);
            else
                $('#menutree').tree("uncheck", cnode.target);
            setAllChildCheck(cnode, checked);
        }
    }
}

function setAllParentCheck(node, checked) {
    var pnode = $('#menutree').tree("getParent", node.target);
    if (pnode) {
        if (checked)
            $('#menutree').tree("check", pnode.target);
        else
            $('#menutree').tree("uncheck", pnode.target);
        setAllParentCheck(pnode, checked);
    }
}

function reSetChecked(nodes, chkdnodes) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        for (var j = 0; j < chkdnodes.length; j++) {
            var cnode = chkdnodes[j];
            if (cnode.id == node.id)
                node.checked = cnode.checked;
        }
        if (node.children) {
            reSetChecked(node.children, chkdnodes);
        }
    }
}
/*
 function onMenuTreeClick(node, checked) {
 var modnode = $('#modtree').tree('getSelected');
 if (modnode == null) {
 $.messager.alert('错误', '没有选择的模块！', 'error');
 return;
 }
 var nodes = $('#menutree').tree('getChecked', ['checked', 'indeterminate']);
 setAllCheckFalse(_AllMenuTreeData[modnode.id]);
 reSetChecked(_AllMenuTreeData[modnode.id], nodes);
 }

 function getCheckedMenus(nodes, amenus) {
 for (var i = 0; i < nodes.length; i++) {
 if (nodes[i].checked) {
 amenus[amenus.length] = nodes[i].id;
 }
 if (nodes[i].children) {
 getCheckedMenus(nodes[i].children, amenus);
 }
 }
 }
 */


function getChecketModMenus() {
    var rst = [];
    var ms = $('#menutree').tree('getChecked', ['checked', 'indeterminate']);
    for (var i = 0; i < ms.length; i++) {
        var m = ms[i];
        var mid = {};
        mid.menuid = m.menuid;
        rst.push(mid);
    }
    return rst;
}

var onloading = false;
function onMenuTreeCheck(node, checked) {
    if (onloading) return;
    onloading = true;
    try {
        if (checked) {
            //选中所有上级节点
            setAllParentCheck(node, true);
            //恢复所有下级节点选中
            resetAllChildCheck(node);
        } else {
            //取消所有下级节点选中
            setAllChildCheck(node, false);
        }
    }
    catch (e) {

    }
    onloading = false;
}