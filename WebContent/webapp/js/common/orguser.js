/**
 * Created by Administrator on 2014-09-24.
 */
$(document).ready(function () {
    $("iframe").css("display", "block");
    initGrids();
    $("#orggridform").c_initDictionary();
    $("#shwUserInfoForm").c_initDictionary();
    getorgs();
});

function initGrids() {
    $C.grid.initComFormaters({
        comUrls: [
            {
                index: "dic5",
                type: "combobox",
                url: _serUrl + "/web/dict/getdictvalues.co?dicid=5",
                valueField: 'dictvalue',
                textField: 'language1'
            },
            {
                index: "rol",
                type: "combobox",
                url: $C.cos.getRolesByUserID(),
                valueField: 'roleid',
                textField: 'rolename'
            },
            {
                index: "opt",
                type: "combobox",
                url: $C.cos.getOptionsByUserID(),
                valueField: 'positionid',
                textField: 'positiondesc'
            }
        ], onOK: function () {
            $("#userinfo_orgs").datagrid({
                columns: [
                    [
                        {
                            field: 'extorgname',
                            width: 300,
                            title: '机构'
                        },
                        {
                            field: 'isdefault',
                            width: 80,
                            title: '默认机构',
                            formatter: $C.grid.comFormaters['dic5'],
                            editor: $C.grid.comEditors['dic5']
                        },
                        {
                            field: 'inheritrole',
                            width: 80,
                            title: '继承权限',
                            formatter: $C.grid.comFormaters['dic5'],
                            editor: $C.grid.comEditors['dic5']
                        }
                    ]
                ]
            });
            $("#userinfo_roles").datagrid({
                columns: [
                    [
                        {
                            field: 'roleid',
                            width: 200,
                            title: '系统角色',
                            formatter: $C.grid.comFormaters['rol'],
                            editor: $C.grid.comEditors['rol']
                        }
                    ]
                ]
            });
            $("#userinfo_postions").datagrid({
                columns: [
                    [
                        {
                            field: 'positionid',
                            width: 200,
                            title: '系统岗位',
                            formatter: $C.grid.comFormaters['opt']
                        }
                    ]
                ]
            });
            $("#shworg_finds").datagrid({
                columns: [[
                    {
                        field: 'forgname',
                        width: 200,
                        title: '机构名称'
                    }
                ]]
            });
        }
    });
}


var selectPostionsPW = undefined;
function userinfo_postions_append() {
    var url = _serUrl + "/web/user/getorgs.co?type=gridtree";
    var wo = {
        id: "selectPostionsPW",
        JPAClass: "com.corsair.server.generic.Shwposition",  //对应后台JPAClass名
        multiRow: true,
        idField: 'positionid',
        showTitle: false,
        autoFind: true,
        width: "500px",//
        height: "400px",//
        gdListColumns: [
            {field: 'positiondesc', title: '流程岗位', width: 120}
        ],
        onResult: function (rows) {
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                if (!$C.grid.getRowByField("#userinfo_postions", row.positionid, "positionid")) {
                    $("#userinfo_postions").datagrid("appendRow", {positionid: row.positionid});
                }
            }
        }
    };
    if (!selectPostionsPW) {
        selectPostionsPW = new TSearchForm(wo);
    }
    selectPostionsPW.show();
}

function getorgs() {
    var url = _serUrl + "/web/user/getOrgsByLged.co?type=gridtree";
    $("#orggrid").treegrid({
        onClickRow: function (row) {
            findorguser();
        }, onDblClickRow: function (rowIndex, rowData) {
            showOrgInfo($C.action.Edit);
        },
        onContextMenu: onOrgContextMenu,
        url: url,
        loadFilter: function (data, parentId) {
            return data;
        }
    });
}

function setstateclosed(nodes) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        node.state = "closed";
        if (node.children != undefined) {
            setstateclosed(node.children);
        }
    }
}

function findorguser(userid) {
    var node = $('#orggrid').treegrid('getSelected');
    if (!node) {
        $.messager.alert('错误', '没选定的机构!', 'error');
        return;
    }
    $ajaxjsonget($C.cos.userlistbyorg() + "?orgid=" + node.orgid,
        function (jsondata) {
            $("#usergrid").datagrid({
                data: jsondata,
                onDblClickRow: function (rowIndex, rowData) {
                    shwUserAction($C.action.Edit);
                }
            });
            if (userid) {
                var currow = undefined;
                var us = $("#usergrid").datagrid("getRows");
                for (var i = 0; i < us.length; i++) {
                    var u = us[i];
                    if (u.userid == userid) {
                        currow = u;
                        break;
                    }
                }
                if (currow) {
                    var idx = $("#usergrid").datagrid("getRowIndex", currow);
                    $("#usergrid").datagrid("selectRow", idx);
                }
            }
        },
        function (XMLHttpRequest, textStatus, errorThrown) {
            alert("查询机构用户错误！");
        }
    );
}


/*var wdinfoex = {
 isNew: false,
 jsonData: data,
 onOK: procok,
 afterGetData:procgetdata(jsondata),
 onCancel: proccancel,
 onShow: onshow,
 otherData: {}
 }*/
function showOrgInfo(tp) {
    var node = $('#orggrid').treegrid('getSelected');
    if (!node) {
        $.messager.alert('错误', '没选定的机构!', 'error');
        return;
    }
    var isnew, jsondata;
    if (tp == $C.action.New) {//新建
        isnew = true;
        jsondata = {superid: node.orgid};//可加入初始化数据
    }
    if (tp == $C.action.Del) {//删除
        if (node.children != undefined) {
            $.messager.alert('错误', '有子机构不允许删除!', 'error');
            return;
        }
        //delete from server
        $.messager.confirm('提示', '确认删除?', function (r) {
            if (r) {
                $ajaxjsonget($C.cos.delOrg + "?orgid=" + node.orgid, function (jsondata) {
                    if (jsondata.result == "OK")
                        $('#orggrid').treegrid("remove", node.orgid);
                }, function () {
                    alert("删除机构错误!");
                })
            }
        });
        return;
    }
    if (tp == $C.action.Edit) {//编辑
        isnew = false;
        jsondata = node;
    }
    $("#orgeditwindow").c_popInfo({
        isNew: isnew,
        jsonData: jsondata,
        onShow: function () {
            $C.grid.clearData("shworg_finds");
            if (!isnew) {
                var url = _serUrl + "/web/user/getfindorgs.co?orgid=" + node.orgid;
                $ajaxjsonget(url, function (jsondata) {
                    $("#shworg_finds").datagrid("loadData", jsondata);
                }, function () {
                    $.messager.alert('错误', '获取查询机构列表错误!', 'error');
                });
            }
        },
        onOK: function (jsondata) {
            //do save
            if (isnew) {
                jsondata.orgid = getnewid("shworg");
                jsondata.idpath = node.idpath + jsondata.orgid + ",";
                jsondata = {isnew: true, jsondata: jsondata};
            } else {
                jsondata = {isnew: false, jsondata: jsondata};
            }
            jsondata.jsondata.shworg_finds = $("#shworg_finds").datagrid("getData").rows;
            var postdata = $.extend(true, {}, jsondata);//不上传子机构数据
            postdata.jsondata.children = [];
            $ajaxjsonpost($C.cos.saveOrg(), JSON.stringify(postdata), function (jsondata) {
                var newdata = $.extend(jsondata.jsondata, jsondata);
                //newdata.id = newdata.orgid;//兼容grid
                //jsondata.children = chlds;
                if (isnew) {
                    $('#orggrid').treegrid("append", {
                        parent: node.orgid,
                        data: [newdata]
                    })
                } else {
                    $('#orggrid').treegrid("update", {id: newdata.orgid, row: newdata});
                }
            }, function () {
                $.messager.alert('错误', '保存错误!', 'error');
            });
            return true;
        }
    });
}

var select_OrgFindIDP_pw = undefined;
function onADDFindIDPOrg() {
    var node = $('#orggrid').treegrid('getSelected');
    if (!node) {
        $.messager.alert('错误', '没选定的机构!', 'error');
        return;
    }
    var url = _serUrl + "/web/user/getOrgsByLged.co?type=list&extorgname=true";
    var wo = {
        id: "select_OrgFindIDP_pw",
        coURL: url,
        orderStr: " orgid asc ",
        multiRow: false,
        autoFind: false,//是否自动查询
        extParms: [],//扩展参数
        width: "500px",//
        height: "300px",//
        gdListColumns: [
            {field: 'extorgname', title: '机构', width: 400, notfind: true},
            {field: 'orgname', title: '机构', width: 100, hidden: true}
        ],
        onResult: function (rows) {
            if (rows.length > 0) {
                var row = rows[0];
                if (row.idpath.substr(0, node.idpath.length) == node.idpath) {
                    $.messager.alert('错误', '不允许选择【当前机构】或【当前机构的子机构】!', 'error');
                    return;
                }
                if (!$C.grid.getRowByField('#shworg_finds', row.orgid, "forgid")) {
                    var data = {
                        orgid: node.orgid,
                        forgid: row.orgid,
                        forgname: row.extorgname,
                        fcode: row.code,
                        fidpath: row.idpath
                    };
                    $("#shworg_finds").datagrid("appendRow", data);
                }
            }
        }
    };
    if (!select_OrgFindIDP_pw) {
        select_OrgFindIDP_pw = new TSearchForm(wo);
    }
    select_OrgFindIDP_pw.show();
}


function getOrgRow(jsondata, orgid) {
    for (var i = 0; i < jsondata.length; i++) {
        var jd = jsondata[i];
        if (jd.orgid == orgid) {
            return jd;
        } else {
            if (jd.children != undefined) {
                var rst = getOrgRow(jd.children, orgid);
                if (rst != undefined) {
                    return rst;
                }
            }
        }
    }
    return undefined;
}

function orgfindAction(tp) {
    if (tp == $C.action.New) {
        onADDFindIDPOrg();
        //$C.grid.append('shworg_finds', {orgid: node.id}, true);
    }
    if (tp == $C.action.Del) {
        var row = $('#shworg_finds').datagrid('getSelected');
        if (row == undefined) {
            $.messager.alert('错误', '没选定的查询机构!', 'error');
            return;
        }
        var idx = $('#shworg_finds').datagrid("getRowIndex", row);
        $('#shworg_finds').datagrid("deleteRow", idx);
    }
}


function shwUserAction(tp) {
    var org = $('#orggrid').treegrid('getSelected');
    var row = $('#usergrid').datagrid('getSelected');
    var idx = $('#usergrid').datagrid("getRowIndex", row);
    var isnew;
    if (tp == $C.action.New) {//新建
        isnew = true;
        var jsondata = {actived: 1, usertype: 2};
    }

    if (tp == $C.action.Del) {//删除
        if (!row) {
            $.messager.alert('错误', '没选定的用户!', 'error');
            return;
        }
        //del
        $.messager.confirm('提醒', '确认删除?', function (r) {
            if (r) {
                $ajaxjsonget($C.cos.delUser + "?userid=" + row.userid, function (jsondata) {
                    if (jsondata.result == "OK")
                        $('#usergrid').datagrid("deleteRow", idx);
                }, function () {
                    $.messager.alert('错误', '删除用户错误!', 'error');
                });
            }
        });
        return;
    }
    if (tp == $C.action.Reload) {//重置密码
        if (!row) {
            $.messager.alert('错误', '没选定的用户!', 'error');
            return;
        }
        $.messager.confirm('提醒', '确认重置密码?', function (r) {
            if (r) {
                var url = _serUrl + "/web/login/reSetPWD.co";
                var jsondata = JSON.stringify({userid: row.userid});
                $ajaxjsonpost(url, jsondata, function (jsondata) {
                    if (jsondata.result) {
                        $.messager.alert('注意', '密码重置为[' + jsondata.result + ']请牢记新密码!', 'warning');
                    }
                }, function (err) {
                    $.messager.alert('错误', '重置密码错误：' + err, 'error');
                });
            }
        });
        return;
    }
    if (tp == $C.action.Edit) {//编辑
        if (!row) {
            $.messager.alert('错误', '没选定的用户!', 'error');
            return;
        }
        var jsondata = row;
        isnew = false;
    }
    $("#usereditwindow").c_popInfo({
        isNew: isnew,
        jsonData: jsondata,
        onOK: function (jsondata) {
            $C.grid.accept("userinfo_orgs");
            $C.grid.accept("userinfo_roles");
            $C.grid.accept("userinfo_postions");
            var orgdata = $("#userinfo_orgs").datagrid("getData").rows;
            var roledata = $("#userinfo_roles").datagrid("getData").rows;
            var posidata = $("#userinfo_postions").datagrid("getData").rows;
            jsondata = {isnew: isnew, orgs: orgdata, roles: roledata, positions: posidata, jsondata: jsondata};
            //console.error(JSON.stringify(jsondata));
            $ajaxjsonpost($C.cos.saveUser(), JSON.stringify(jsondata), function (jsondata) {
                if (isnew) {
                    $('#usergrid').datagrid('appendRow', jsondata);
                } else {
                    $('#usergrid').datagrid('updateRow', {index: idx, row: jsondata});
                }
            }, function () {
                alert("保存资料错误！");
            });
            return true;
        },
        onShow: function () {
            if (isnew) {
                $C.grid.clearData("userinfo_orgs");
                $C.grid.clearData("userinfo_roles");
                $C.grid.clearData("userinfo_postions");
                $C.grid.append("userinfo_orgs", {
                    isdefault: 1,
                    inheritrole: 1,
                    orgid: org.orgid,
                    extorgname: org.extorgname
                }, false);
            } else {
                getOrgsData();
                getRolesData();
                getOptionsData();
            }
        }
    });
    function getOrgsData() {
        if (row.userid)
            $ajaxjsonget($C.cos.getOrgsByUserID() + "?userid=" + row.userid, function (jsondata) {
                $("#userinfo_orgs").datagrid({data: jsondata});
            }, function () {
                alert("getOrgsData错误");
            })
    }

    function getRolesData() {
        if (row.userid)
            $ajaxjsonget($C.cos.getRolesByUserID() + "?userid=" + row.userid, function (jsondata) {
                $("#userinfo_roles").datagrid({data: jsondata});
            }, function () {
                alert("错误");
            })
    }

    function getOptionsData() {
        if (row.userid)
            $ajaxjsonget($C.cos.getOptionsByUserID() + "?userid=" + row.userid, function (jsondata) {
                $("#userinfo_postions").datagrid({data: jsondata});
            }, function () {
                alert("错误");
            })
    }


}

function doFind(ty) {
    if (ty == 1) {
        onSelectOrg();
    }
    if (ty == 2) {
        onFinduser();
    }
}


var select_Org_pw = undefined;
function onSelectOrg() {
    var url = _serUrl + "/web/user/getOrgsByLged.co?type=list&extorgname=true";
    var wo = {
        id: "select_Org_pw",
        coURL: url,
        orderStr: " orgid asc ",
        multiRow: false,
        autoFind: false,//是否自动查询
        extParms: [],//扩展参数
        width: "500px",//
        height: "300px",//
        gdListColumns: [
            {field: 'extorgname', title: '机构', width: 400, notfind: true},
            {field: 'orgname', title: '机构', width: 100, hidden: true}
        ]
    };
    if (!select_Org_pw) {
        select_Org_pw = new TSearchForm(wo);
    }
    select_Org_pw.extendOptions({
        onResult: function (rows) {
            if (rows.length > 0) {
                var row = rows[0];
                doSelectOrg(row.orgid);
            }
        }
    });
    select_Org_pw.show();
}

function doFindOrgs(nodes, r) {
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (node.orgname.indexOf(r) >= 0) {
            doSelectOrg(node.orgid);
            return;
        }
        if (node.children) {
            doFindOrgs(node.children, r);
        }
    }
}

function doSelectOrg(orgid) {
    try {
        $('#orggrid').treegrid('expandTo', orgid);
        $('#orggrid').treegrid('select', orgid);
    } catch (e) {
        //alert(e.name + ": " + e.message);
    }
}


var select_user_pw = undefined;
function onFinduser() {
    var url = _serUrl + "/web/user/findOrgUserByLogined.co";
    var wo = {
        id: "select_user_pw",
        coURL: url,
        multiRow: false,
        idField: 'userid',
        autoFind: false,//是否自动查询
        gdListColumns: [
            {field: 'username', title: '登录名', width: 100},
            {field: 'displayname', title: '姓名', width: 100},
            {field: 'extorgname', title: '机构', width: 500, notfind: true}//不作为查询条件
        ],
        onResult: function (rows) {
            if (rows.length > 0) {
                var row = rows[0];
                doSelectOrg(row.orgid);
                try {
                    findorguser(row.userid);
                } catch (e) {
                    //alert(e.name + ": " + e.message);
                }
            }
        }
    };
    if (!select_user_pw) {
        select_user_pw = new TSearchForm(wo);
    }
    select_user_pw.show();
}

function onOrgContextMenu(e, row) {
    if (row) {
        e.preventDefault();
        $(this).treegrid('select', row.orgid);
        $('#orgmenu').menu('show', {
            left: e.pageX,
            top: e.pageY
        });
    }
}


var cur_orgAction = 0;
var cur_node = null;
var select_OrgAppend2_pw = undefined;
//tp 1 并入 2 调整所属父机构 3 更名 4 注销
function orgAction(tp) {
    var node = $('#orggrid').treegrid('getSelected');
    if (!node) {
        $.messager.alert('错误', '没选定的机构!', 'error');
        return;
    }
    cur_node = node;
    cur_orgAction = tp;
    if (tp == 1) {
        $.messager.confirm('警告', '当前机构下所有资料将并入选择的机构，当前机构资料将被清空，是否继续?', function (r) {
            if (r) {
                $.messager.confirm('提示', '根据系统数据量大小，系统将占有较长时间运算，是否继续？', function (r) {
                    if (r) {
                        onSelectOrg2Appendto(node);
                    }
                });
            }
        });
    }
    if (tp == 2) {
        $.messager.confirm('警告', '将当前机构调整至选择机构下，是否继续?', function (r) {
            if (r) {
                $.messager.confirm('提示', '根据系统数据量大小，系统将占有较长时间运算，是否继续?', function (r) {
                    if (r) {
                        onSelectOrg2Appendto(node);
                    }
                });
            }
        });
    }
    if (tp == 3) {
        $.messager.prompt('输入', '请修改机构名称', function (r) {
            if (r) {
                if (r == node.orgname)
                    return;
                var url = _serUrl + "/web/user/updateOrgName.co";
                var data = {
                    orgid: node.orgid,
                    orgname: r
                };
                $ajaxjsonpost(url, JSON.stringify(data), function (jsdata) {
                    node.orgname = jsdata.orgname;
                    $('#orggrid').treegrid('update', {
                        id: node.orgid,
                        row: node
                    });
                }, function (err) {
                    alert(err);
                });
            }
        });
        $('.messager-input').val(node.orgname).focus();
    }
}

function onSelectOrg2Appendto(node) {
    var url = _serUrl + "/web/user/getOrgsByLged.co?type=list&extorgname=true";
    var wo = {
        id: "select_Org_pw",
        coURL: url,
        orderStr: " orgid asc ",
        multiRow: false,
        autoFind: false,//是否自动查询
        extParms: [],//扩展参数
        width: "500px",//
        height: "300px",//
        gdListColumns: [
            {field: 'extorgname', title: '机构', width: 400, notfind: true},
            {field: 'orgname', title: '机构', width: 100, hidden: true}
        ]
    };
    if (!select_OrgAppend2_pw) {
        select_OrgAppend2_pw = new TSearchForm(wo);
    }
    select_OrgAppend2_pw.extendOptions({
        onResult: function (rows) {
            if (rows.length > 0) {
                var row = rows[0];
                var data = {
                    tp: cur_orgAction,
                    sorgid: node.orgid,
                    dorgid: row.orgid
                };
                var url = _serUrl + "/web/user/putOrg2Org.co";
                $ajaxjsonpost(url, JSON.stringify(data), function (jsdata) {
                    if (jsdata.result == "OK") {
                        if (cur_orgAction == 2) {
                            $('#orggrid').treegrid("remove", cur_node.orgid);
                            var parms = {
                                parent: row.orgid,
                                data: [cur_node]
                            };
                            $('#orggrid').treegrid('append', parms);
                        }
                        if (cur_orgAction == 1) {
                            if (cur_node.children) {
                                var ns = JSON.stringify(cur_node.children);
                                var nodes = JSON.parse(ns);
                                var oids = [];
                                for (var i = 0; i < nodes.length; i++) {
                                    oids.push(nodes[i].orgid);
                                }
                                for (var i = 0; i < oids.length; i++) {
                                    $('#orggrid').treegrid('remove', oids[i]);
                                }

                                var parms = {
                                    parent: row.orgid,
                                    data: nodes
                                };
                                $('#orggrid').treegrid('append', parms);


                            }
                        }
                    }
                }, function (err) {
                    alert(err);
                });
            }
        }
    });
    select_OrgAppend2_pw.show();
}


var findOrg2UserOrg_pw = undefined;

function findOrg2UserOrg() {
    var url = _serUrl + "/web/user/getOrgsByLged.co?type=list&extorgname=true";
    var wo = {
        id: "select_Org_pw",
        coURL: url,
        orderStr: " orgid asc ",
        multiRow: false,
        autoFind: false,//是否自动查询
        extParms: [],//扩展参数
        width: "500px",//
        height: "300px",//
        gdListColumns: [
            {field: 'extorgname', title: '机构', width: 400, notfind: true},
            {field: 'orgname', title: '机构', width: 100, hidden: true}
        ]
    };
    if (!findOrg2UserOrg_pw) {
        findOrg2UserOrg_pw = new TSearchForm(wo);
    }
    findOrg2UserOrg_pw.extendOptions({
        onResult: function (rows) {
            if (rows.length > 0) {
                var row = rows[0];
                if (!$C.grid.getRowByField("#userinfo_orgs", [row.orgid], ["orgid"])) {
                    var r = {};
                    r.orgid = row.orgid;
                    r.extorgname = row.extorgname;
                    r.isdefault = 2;
                    r.inheritrole = 1;
                    $C.grid.append('userinfo_orgs', r, true);
                }
            }
        }
    });
    findOrg2UserOrg_pw.show();
}

