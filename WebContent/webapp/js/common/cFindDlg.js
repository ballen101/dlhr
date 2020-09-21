function TFindDialog(dlgOptions) {
    var findFields = getFindColums(dlgOptions.findFields);
    var findeditable = (dlgOptions.findeditable == undefined) ? false : dlgOptions.findeditable;
    //if (!findFields) alert("TFindDialog构造函数未设置findFields");
    if (dlgOptions.width == undefined)dlgOptions.width = 450;
    if (dlgOptions.height == undefined)dlgOptions.height = 400;
    var find_window = undefined;
    var findline = $getHtmlFragment("#findline");
    var dlgid = 'dlg' + $generateUUID();
    initDlgUI();

    var find_reloper_data = [
        {id: 'like', value: '包含'},
        {id: '=', value: '等于'},
        {id: '>', value: '大于'},
        {id: '>=', value: '大于等于'},
        {id: '<', value: '小于'},
        {id: '<=', value: '小于等于'},
        {id: '<>', value: '不等于'},
        {id: 'not like', value: '不包含'}
    ];

    function initDlgUI() {
        if ($("#common_divs_id").length <= 0) {
            $("<div id='common_divs_id' style='display: none'></div>").appendTo("body");
        }
        find_window = $getHtmlFragment("#_find_window_id");
        find_window = $(find_window).attr("id", dlgid).appendTo("#common_divs_id");
        $.parser.parse("#common_divs_id");
        find_window.window("resize", {
            width: dlgOptions.width,
            height: dlgOptions.height
        });
        find_window.keydown(onFindDlgKeydown);
        find_window.find("a[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            if (co.caction == "act_ok") {
                el.onclick = function () {
                    onOKClick();
                };
            }
            if (co.caction == "act_cancel") {
                el.onclick = function () {
                    onCancelClick();
                }
            }
        });
        var cpanel = find_window.find(".easyui-layout").layout("panel", "center");
        cpanel.panel({
            tools: [
                {
                    iconCls: 'icon-add1',
                    handler: function () {
                        find_window_add_click();
                    }
                }
            ]
        });
    }

    function onOKClick() {
        var parms = getFindData();
        if (dlgOptions.onOK) {
            if (dlgOptions.onOK(parms))
                find_window.window('close');
        }
    }

    function onCancelClick() {
        if (dlgOptions.onCancel) {
            if (dlgOptions.onCancel()) {
                find_window.window('close');
            }
        } else find_window.window('close');
    }

    function onFindDlgKeydown() {
        //console.log(event.keyCode);
        switch (event.keyCode) {
            case 13://enter
                onOKClick();
                return false;
            case 27://cancel
                onCancelClick();
                return false;
            default:
                break;
        }
    }

    this.getDlgLayout = getDlgLayout;
    function getDlgLayout() {
        return find_window.find(".easyui-layout");
    }

    this.show = show;
    function show() {
        if (find_window.find("tbody tr").children().length == 0)
            addNotNullParms();
        if (find_window.find("tbody tr").children().length == 0)
            find_window_add_click();
        find_window.window("open");
    }

    this.close = close;
    function close() {
        find_window.window('close');
    }


    function addNotNullParms() {
        for (var i = 0; i < findFields.length; i++) {
            var fFd = findFields[i];
            if (fFd.notnull) {
                find_window_add_click(fFd.field);
            }
        }
    }

    function isNotNullField(field) {
        for (var i = 0; i < findFields.length; i++) {
            var fFd = findFields[i];
            if (fFd.field == field) {
                if (fFd.notnull) {
                    return fFd;
                } else return undefined;
            }
        }
    }

    function checkNotNull(parms) {
        for (var i = 0; i < parms.length; i++) {
            var parm = parms[i];
            var FD = isNotNullField(parm.parmname);
            if (FD) {
                if ($isEmpty(parm.parmvalue)) {
                    alert("参数【" + FD.title + "】不允许为空");
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    function find_window_add_click(field) {
        var hml = findline;
        var rowid = "row" + $generateUUID();
        hml = hml.replace(new RegExp("{{rowid}}", "g"), rowid);
        hml = hml.replace(new RegExp("{{parmnameData}}", "g"), "");
        hml = hml.replace(new RegExp("{{reloperData}}", "g"), "valueField:'id',textField:'value'");
        find_window.find(".dg_find_window_parms_id tbody").append(hml);
        $.parser.parse("#" + rowid);

        $("#" + rowid).find("input[fieldname]").each(function () {
            var fdname = $(this).attr("fieldname");
            if (fdname == "parmname") {
                $(this).combobox({
                    valueField: 'field',
                    textField: 'title',
                    editable: findeditable,
                    data: findFields,
                    onSelect: onParmNameSelect
                });
                if (field) {
                    $(this).combobox("setValue", field);
                    $(this).combobox("readonly", true);
                    $(this).parent().append("(<span style='color: red'>*</span>)");
                    $(this).next().find("input:first").addClass("tabs-tool");
                }
            }
            if (fdname == "reloper") {
                $(this).combobox({
                    editable: findeditable,
                    data: find_reloper_data
                });
                $(this).combobox("setValue", "=");
            }
        });
        if (field)
            $("#" + rowid).find("a[fieldname='actiondel']").html("");
        else
            $("#" + rowid).find("a[fieldname='actiondel']").click(function (event) {
                onFindRowDel(this);
            });
    }

    function getFindColums(listGridColumns) {
        if (!listGridColumns) return [];
        var fcls = [];
        for (var i = 0; i < listGridColumns.length; i++) {
            var col = listGridColumns[i];
            if (col) {
                if ($.isArray(col)) {
                    for (var j = 0; j < col.length; j++) {
                        var coll = col[j];
                        if (coll && (coll.field) && (!col.notfind))
                            fcls.push(coll);
                    }
                } else {
                    if (col.field && (!col.notfind))
                        fcls.push(col);
                }
            }
        }
        return fcls;
    }

    function onParmNameSelect(rec) {
        var fieldinput = $(this);
        fieldinput.attr("precision", rec.precision);
        var tdreloper = fieldinput.parents("tr").find("td[tdtype='reloper']");
        var htm = "<input type='text' fieldname='reloper' style='width:70px;height: 20px'>";
        tdreloper.html(htm);
        $.extend(rec.relOptions, {valueField: 'id', textField: 'value'});
        var inputrel = tdreloper.find("input");
        if (rec.relOptions) {
            inputrel.combobox(rec.relOptions);
            if (rec.relOptions.data.length > 0) {
                inputrel.combobox("setValue", rec.relOptions.data[0].id);
            }
        } else {
            inputrel.combobox({
                valueField: 'id', textField: 'value',
                editable: findeditable,
                data: find_reloper_data
            });
            inputrel.combobox("setValue", "=");
        }

        var trparmvalue = $(this).parents("tr").find("td[tdtype='parmvalue']");
        var htm = "<input type='text' fieldname='parmvalue' style='width:150px;height: 20px'>";
        trparmvalue.html(htm);
        var inputo = trparmvalue.find("input");
        $setFindTextByRow(rec, inputo, findeditable);
    }


    function onFindRowDel(obj) {
        $(obj).parents("tr").remove();
    }

    function getFindData() {
        var data = [];
        find_window.find("tbody tr").each(function () {
            var row = {};
            $(this).find("input[fieldname]").each(function () {
                var objthis = $(this);
                var fdname = objthis.attr("fieldname");
                if (fdname == "parmname") {
                    row.parmname = objthis.combobox("getValue");
                    row.precision = objthis.attr("precision");
                }
                if (fdname == "reloper") {
                    row.reloper = objthis.combobox("getValue");
                }
                if (fdname == "parmvalue") {
                    var clsname = objthis.attr("class");
                    if ((clsname.indexOf("datetimebox") >= 0) || (clsname.indexOf("datebox") >= 0)) {
                        //$fieldDateFormatorYYYY_MM_DD
                        row.parmvalue = objthis.textbox("getValue");
                    } else if (clsname.indexOf("combo") >= 0) {
                        row.parmvalue = objthis.combobox("getValue");
                    } else {
                        row.parmvalue = objthis.textbox("getValue");
                    }
                }
            });
            if (row.parmname != undefined)
                data.push(row);
        });
        if (checkNotNull(data))
            return data;
    }


}