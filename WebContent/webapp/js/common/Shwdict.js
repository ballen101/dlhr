/**
 * Created by Administrator on 2014-10-14.
 */

function $OnCorsairReady() {
    initGrids();
    $("#dictform").c_initDictionary();
    $ajaxjsonget($C.cos.getDicTree, function (jsondata) {
        //$("#btnewch").linkbutton("disable");
        //$('#menuDict').menu('disableItem', $('#menuDict').menu('findItem', '新建词汇值').target);
        setNodesIco(jsondata);
        $C.tree.setTree1OpendOtherClosed(jsondata);
        $("#shwdict_grid").treegrid({
            data: jsondata,
            onDblClickRow: function (rowIndex, rowData) {
                toolbarAction(5);
            },
            onSelect: function (rowData) {
                reSetAction(rowData);
            }
        });
    }, function () {
        $.messager.alert('错误', '查询数据字典错误！', 'error');
    });
}

$(document).ready(function () {
    //
});

function setNodeIco(node) {
    if (node.dtype == 1) {
        node.iconCls = "icon-folder";
    }
    if (node.dtype == 2) {
        node.iconCls = "icon-folder_page";
    }
    if (node.dtype == 3) {
        node.iconCls = "icon-text_ab";
    }
}

function setNodesIco(nodes) {
    for (var i = 0; i < nodes.length; i++) {
        node = nodes[i];
        setNodeIco(node);
        if (node.children) {
            setNodesIco(node.children);
        }
    }
}

function reSetAction(rowData) {
    if (rowData.dtype == 1) {//分类
        $("#btnewch").linkbutton("disable");
        $('#menuDict').menu('disableItem', $('#menuDict').menu('findItem', '新建词汇值').target);
    } else {
        $("#btnewch").linkbutton("enable");
        $('#menuDict').menu('enableItem', $('#menuDict').menu('findItem', '新建词汇值').target);
    }
}

function getPid(act, snode) {
    if (act == 1) {
        var root = $('#shwdict_grid').treegrid('getRoot');
        return root.pid;
    }
    if (act == 2) {//新建组
        if (snode.dtype == 1) {
            return snode.dictid;
        } else if (snode.dtype == 2) {
            return snode.pid;
        } else if (snode.dtype == 3) {
            return $('#shwdict_grid').treegrid("getParent", snode.dictid).pid;
        }
    }
    if (act == 3) {//新建值
        if (snode.dtype == 1) {
            return null;
        } else if (snode.dtype == 2) {
            return snode.dictid;
        } else if (snode.dtype == 3) {
            return snode.pid;
        }
    }
}

function toolbarAction(act) {
    var otb = $('#shwdict_grid');
    var node = otb.treegrid('getSelected');
    if (!node) {
        $.messager.alert('错误', '没选定词汇!', 'error');
        return;
    }
    var jsondata = {};
    if (act == 1) {//新建分类
        var isnew = true;
        jsondata.dtype = 1;
        jsondata.pid = getPid(act, node);
    }
    if (act == 2) {//新建组
        var isnew = true;
        jsondata.dtype = 2;
        jsondata.pid = getPid(act, node);
    }
    if (act == 3) {//新建值
        var isnew = true;
        jsondata.dtype = 3;
        jsondata.pid = getPid(act, node);
    }
    if (act == 4) {//删除
        $.messager.confirm('提醒', '确认删除?', function (r) {
            if (r) {
                $ajaxjsonget($C.cos.delDict + "?dictid=" + node.dictid, function (jsondata) {
                    if (jsondata.result == "OK")
                        otb.treegrid("remove", node.dictid);
                }, function () {
                    $.messager.alert('错误', '删除词汇错误!', 'error');
                });
            }
        });
        return;
    }
    if (act == 5) {//编辑
        jsondata = node;
        var isnew = false;
    }

    $("#dictInfoWindow").c_popInfo({
        isNew: isnew,
        jsonData: jsondata,
        onOK: function (jsondata) {
            $ajaxjsonpost($C.cos.saveDict, JSON.stringify(jsondata), function (jsondata) {
                jsondata.id = jsondata.dictid;
                if (isnew) {
                    setNodeIco(jsondata);
                    if (jsondata.dtype == 1) {
                        otb.treegrid("append", {data: [jsondata]});
                    } else {
                        otb.treegrid("append", {parent: jsondata.pid, data: [jsondata]});
                    }
                } else {
                    otb.treegrid("update", {id: jsondata.id, row: jsondata});
                }
            }, function () {
                alert("保存错误！");
            });
            return true;
        }
    });


}
