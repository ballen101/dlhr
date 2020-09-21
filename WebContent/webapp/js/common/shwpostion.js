/**
 * Created by Administrator on 2014-10-13.
 */
$(document).ready(function () {
    $ajaxjsonget($C.cos.findPositions, function (jsondata) {
        $("#tb_positions").datagrid({
            data: jsondata,
            onDblClickRow: function (rowIndex, rowData) {
                toolbarAction($C.action.Edit);
            }
        });
    }, function () {
    });
    $("#shwpositionform").c_initDictionary();
});


function toolbarAction(action) {
    var otb = $('#tb_positions');
    var row = otb.datagrid('getSelected');
    switch (action) {
        case $C.action.Edit:
            if (!row) {
                $.messager.alert('错误', '没选定的岗位!', 'error');
                return;
            }
            var jsondata = row;
            isnew = false;
            break;
        case $C.action.Append:
            isnew = true;
            var jsondata = {};
            break;
        case $C.action.Del:
            if (!row) {
                $.messager.alert('错误', '没选定的岗位!', 'error');
                return;
            }
            $.messager.confirm('提醒', '确认删除?', function (r) {
                if (r) {
                    $ajaxjsonget($C.cos.delPosition + "?positionid=" + row.positionid, function (jsondata) {
                        if (jsondata.result == "OK")
                            otb.datagrid("deleteRow", otb.datagrid("getRowIndex", row));
                    }, function () {
                        $.messager.alert('错误', '删除岗位错误!', 'error');
                    });
                }
            });
            return;
    }

    $("#positionInfoWindow").c_popInfo({
        isNew: isnew,
        jsonData: jsondata,
        onOK: function (jsondata) {
            $ajaxjsonpost($C.cos.savePosition, JSON.stringify(jsondata), function (jsondata) {
                if (isnew) {
                    otb.datagrid("appendRow", jsondata)
                } else {
                    var idx = otb.datagrid("getRowIndex", row);
                    otb.datagrid("updateRow", {index: idx, row: jsondata});
                    otb.datagrid("refreshRow", idx);
                }
            }, function () {
                alert("保存岗位错误")
            });
            return true;
        },
        onShow: function () {
            $ajaxjsonget($C.cos.findUsersByPosition + "?positionid=" + row.positionid, function (jsondata) {
                $("#position_users").datagrid({data: jsondata});
            }, function () {

            })
        }
    });
}

