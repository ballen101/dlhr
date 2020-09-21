/**
 * Created by Administrator on 2014-10-21.
 */

var wf = undefined;  //流程图对象
var wfoptions = undefined;//流程图初始化参数

var curWfWInfo = undefined;//当前编辑的流程
var curNdWinfo = undefined;//当前编辑的节点
//var curLinWinfo = undefined;//当前编辑的连线


//var selectWFbuildinParms = undefined;//流程节点内置参数选择框

var wftemprocInfo = undefined;//节点窗体
var wftempproclineinfo = undefined;//节点间连线

function $OnCorsairReady() {
    initSelects();
    initGrids();
    var urlwftty = _serUrl + "/web/wf/getwftemtypetree.co";
    $ajaxjsonget(urlwftty, function (jsondata) {
        $("#shwfdr").treegrid({
            onClickRow: function (rowData) {
                loadWFList(rowData);
            }, data: jsondata
        });
    }, function () {
        $.messager.alert("错误", "显示流程类别错误!", "error");
    }, true, null, false);
    wfoptions = {
        el: document.getElementById("wftemgr_id"),
        icourl: '../js/easyui/themes/icons/user.png',
        initBENodes: false,
        onNodeClick: onNodeClick,
        onNodeDBClick: onNodeDBClick,
        onLineDBClick: onLineDBClick,
        onGetLineData: onGetLineData
    };
    $("#shwwftempform").c_initDictionary();
    $("#wftemInfotabmain").tabs({onSelect: onTagSelect});
}

function loadWFList(rowData) {
    $ajaxjsonget($C.cos.getWftemps + "?fdrid=" + rowData.wftpid, function (jsondata) {
        $("#shwftemp_id").datagrid({
            onDblClickRow: function (rowIndex, rowData) {
                findWftemData(rowData.wftempid, $C.action.Edit);
            }, data: jsondata
        });
    }, function () {
        $.messager.alert("错误", "获取流程列表错误!", "error");
    }, true, null, false);
}

function initSelects() {
    var poptions = {
        url: "shwwftemprocinfo.html",
        title: "节点信息",
        woptions: "modal:true,closed:true,border:false,collapsible:false,minimizable:false,iconCls:'application_home'",
        style: "width:800px;height:500px;padding: 0px",
        chtch: false
    };
    wftemprocInfo = new $showModalDialog(poptions);

    var poptions = {
        url: "shwwftempprocline.html",
        title: "节点连线",
        woptions: "modal:true,closed:true,border:false,collapsible:false,minimizable:false,iconCls:'application_home'",
        style: "width:600px;height:400px;padding: 0px",
        chtch: false
    };
    wftempproclineinfo = new $showModalDialog(poptions);
}
function initGrids() {
    $("#shwwftemparmss").datagrid({
        columns: [
            [
                {
                    field: 'rowreloper', title: '行关系', width: 60, editor: {
                    type: 'combobox',
                    options: {
                        valueField: 'id', textField: 'value',
                        data: [
                            {id: 'AND', value: 'AND'},
                            {id: 'OR', value: 'OR'}
                        ]
                    }
                }
                },
                {field: 'parmname', title: '参数名', width: 60, editor: 'text'},
                {
                    field: 'reloper', title: '运算关系', width: 60, editor: {
                    type: 'combobox',
                    options: {
                        valueField: 'id', textField: 'value',
                        data: [
                            {id: '>', value: '>'},
                            {id: '<', value: '<'},
                            {id: '=', value: '='}
                        ]
                    }
                }
                },
                {field: 'parmvalue', title: '参数值', width: 60, editor: 'text'},
                {field: 'note', title: '备注', width: 100, editor: 'text'}
            ]
        ]
    });
}

function toolbarAction(act) {
    var row = $("#shwftemp_id").datagrid("getSelected");
    if (act == $C.action.Edit) {
        if (!row) {
            $.messager.alert('错误', '没选定的流程', 'error');
            return;
        }
        findWftemData(row.wftempid, act);
    } else {
        showWFTempInfoEx(row, act);
    }
}

function copyclick() {
    var row = $("#shwftemp_id").datagrid("getSelected");
    if (!row) {
        $.messager.alert("错误", "选择模板");
        return;
    }
    $.messager.prompt("复制模板", "输入新模板名称", function (r) {
        if (r) {
            var url = _serUrl + "/web/wf/copyWfTemp.co";
            pdata = {
                wftempid: row.wftempid,
                new_wftempname: r
            };
            $ajaxjsonpost(url, JSON.stringify(pdata), function (jsdata) {
                var fd = $("#shwfdr").datagrid("getSelected");
                loadWFList(fd);
            }, function (err) {
                $.messager.alert('错误', err, 'error');
            });
        }
    });
    $(".messager-input").val(row.wftempname+"copy");
}

function findWftemData(wftempid) {
    $ajaxjsonget($C.cos.getWftemp + "?wftempid=" + wftempid, function (wftempdata) {
        showWFTempInfoEx(wftempdata, $C.action.Edit);
    }, function () {
    });
}

function onGetLineData(line) {
    var ldata = line.getData();
    var fndata = line.fnode.getData();
    if ((!fndata.proctempid) || (fndata.proctempid.length == 0)) {
        fndata.proctempid = getnewid("shwwftempproc");
    }
    var tndata = line.tnode.getData();
    if ((!tndata.proctempid) || (tndata.proctempid.length == 0)) {
        tndata.proctempid = getnewid("shwwftempproc");
    }
    ldata.fromproctempid = fndata.proctempid;
    ldata.toproctempid = tndata.proctempid;
}

var loadWfed = false;

//检查节点合法否
function checkwf() {
    if (wf && (wf.checkWF() == 0)) {
        $.messager.alert('错误', '流程未完成', 'error');
        return false;
    }
    return true;
}

function showWFTempInfoEx(wftempdata, tp) {
    var row = $("#shwftemp_id").datagrid("getSelected");
    var isnew = false;
    if (tp == $C.action.New) {//新建
        var fd = $("#shwfdr").datagrid("getSelected");
        if (!fd) {
            $.messager.alert('错误', '没选定的流程类别', 'error');
            return;
        }
        var fdrid = fd.wftpid;
        isnew = true;
        wftempdata = {fdrid: fdrid, shwwftemparmss: [], shwwftempprocs: []};
    }
    if (tp == $C.action.Del) {//删除
        if (!row) {
            $.messager.alert('错误', '没选定的流程', 'error');
            return;
        }
        $.messager.confirm('提醒', '确认删除?', function (r) {
            if (r) {
                var url = _serUrl + "/web/wf/delWFTem.co";
                var data = JSON.stringify({wftempid: row.wftempid});
                $ajaxjsonpost(url, data, function (jsondata) {
                    var idx = $("#shwftemp_id").datagrid("getRowIndex", row);
                    $("#shwftemp_id").datagrid("deleteRow", idx);
                    $.messager.alert('提示', '已经删除!');
                }, function (err) {
                    $.messager.alert('错误', '删除流程错误:' + err, 'error');
                });
            }
        });
        return;
    }
    if (tp == $C.action.Edit) {//编辑
        if (!wftempdata) {
            $.messager.alert('错误', '没选定的流程', 'error');
            return;
        }
        isnew = false;
    }

    curWfWInfo = {
        isNew: isnew,
        jsonData: wftempdata,
        afterGetData: function (jsondata) {
            //将流程图数据从界面载入
        },
        onOK: function (jsondata) {
            //do save
            if (wf) {
                if (!checkwf()) return false;
                var wfdata = wf.getData();
                jsondata.shwwftempprocs = wfdata.procs;
                jsondata.shwwftemplinklines = wfdata.lines;
            }
            jsondata = {isnew: isnew, jsondata: jsondata};
            $ajaxjsonpost($C.cos.saveWfTemp, JSON.stringify(jsondata), function (jsondata) {
                if (isnew) {
                    $("#shwftemp_id").datagrid("appendRow", jsondata)
                } else {
                    $("#shwftemp_id").datagrid("updateRow", {
                        index: $("#shwftemp_id").datagrid("getRowIndex", row),
                        row: jsondata
                    });
                }
                $("#wftempInfoWindow").window('close');
            }, function () {
                $.messager.alert('错误', '保存流程模板错误！', 'error');
            });
            return false;
        },
        onShow: function () {
            if (wf) wf.removeall();
            loadWfed = false;
            $("#wftemInfotabmain").tabs("select", "常规");
            //  if (isnew) {
            //      $('#wftemInfotabmain').tabs('disableTab', 1);
            //      $('#wftemInfotabmain').tabs('disableTab', 2);
            //  } else {
            //      $('#wftemInfotabmain').tabs('enableTab', 1);
            //      $('#wftemInfotabmain').tabs('enableTab', 2);
            //  }
        },
        otherData: {}
    };
    $("#wftempInfoWindow").c_popInfo(curWfWInfo);
}

function onTagSelect(title, index) {
    if (index == 2) {
        if (wf == undefined) {
            wf = new WFEdit(wfoptions);
        }
        if (!loadWfed) {
            loadWfed = true;
            if (curWfWInfo.isNew) {
                wf.initBEnodes();
                var wfdata = wf.getData();
                for (var i = 0; i < wfdata.procs.length; i++) {
                    wfdata.procs[i].proctempname = wfdata.procs[i].title;
                    wfdata.procs[i].allowemptyuser = 1;
                }
                //curWfWInfo.jsonData.shwwftempprocs = [];//添加初始化节点
            } else {
                if (!hasBEProces(curWfWInfo.jsonData.shwwftempprocs)) {
                    wf.initBEnodes();
                    var wfdata = wf.getData();
                    for (var i = 0; i < wfdata.procs.length; i++) {
                        wfdata.procs[i].proctempname = wfdata.procs[i].title;
                        wfdata.procs[i].allowemptyuser = 1;
                    }
                } else
                    wf.loadFromWftemp(curWfWInfo.jsonData);
            }
        }
    }

    function hasBEProces(ps) {
        var hb = false, he = false;
        for (var i = 0; i < ps.length; i++) {
            var p = ps[i];
            hb = hb || (parseInt(p.isbegin) == 1);
            he = he || (parseInt(p.isend) == 1);
        }
        return (hb && he);
    }
}

function onNodeClick(evt, node) {
    if (creteLineIng) {
        if (node == pnode) {
            creteLineIng = false;
            return;
        }
        wf.addLine({title: "", idx: 0}, node, pnode);
        creteLineIng = false;
    }
    pnode = node;
}

//////////////////////流程节点 begin/////////////////////////////

function onNodeDBClick(evt, node) {
    var parms = {};// {data: node.getData()};
    curNdWinfo = node;
    wftemprocInfo.show(parms, function (jsondata) {
        jsondata.users = [];
        for (var i = 0; i < jsondata.shwwftempprocusers.length; i++) {
            jsondata.users.push({username: jsondata.shwwftempprocusers[i].displayname});
        }
        jsondata.title = jsondata.proctempname;
        //setProcOption(jsondata);
        wf.getActiveNode().setData(jsondata);
        return true;
    });
}


//////////////////////流程节点 end/////////////////////////////

function onLineDBClick(evt, line) {
    var linedata = line.getData();
    wftempproclineinfo.show(linedata, function (jsondata) {
        if (!jsondata.idx) jsondata.idx = 0;
        jsondata.title = jsondata.lltitle + "(" + jsondata.idx + ")";
        linedata = jsondata;
        line.setData(linedata);
        line.draw();
        return true;
    });
}

