/**
 * Created by shangwen on 2015/7/7.
 */

function TSearchForm(sfrmOptions) {
    var inited = false;
    var id = sfrmOptions.id;
    if ((!id) || (id.length < 0)) {
        $.messager.alert('错误', '没有设置id变量!', 'error');
        return undefined;
    }
    var istree, htmlTempt, idField, pidField, treeField, closeAllTree, enableIdpath, autoFind, singleAutoReturn, notFindAutoReturn, edittps, showTitle, orderStr;
    var coURL;

    function readOptions() {
        istree = (sfrmOptions.isTree == undefined) ? false : sfrmOptions.isTree;
        if (istree)
            htmlTempt = (sfrmOptions.htmlTempt) ? sfrmOptions.htmlTempt : "../templet/default/search_grid_tree.html";
        else
            htmlTempt = (sfrmOptions.htmlTempt) ? sfrmOptions.htmlTempt : "../templet/default/search_grid.html";
        idField = (sfrmOptions.idField) ? sfrmOptions.idField : undefined;
        pidField = (sfrmOptions.pidField) ? sfrmOptions.pidField : undefined;
        treeField = (sfrmOptions.treeField) ? sfrmOptions.treeField : undefined;
        closeAllTree = (sfrmOptions.closeAllTree) ? sfrmOptions.closeAllTree : false;
        enableIdpath = (sfrmOptions.enableIdpath == undefined) ? true : sfrmOptions.enableIdpath;
        autoFind = (sfrmOptions.autoFind == undefined) ? false : sfrmOptions.autoFind;
        singleAutoReturn = (sfrmOptions.singleAutoReturn == undefined) ? false : sfrmOptions.singleAutoReturn;
        notFindAutoReturn = (sfrmOptions.notFindAutoReturn == undefined) ? false : sfrmOptions.notFindAutoReturn;
        edittps = (sfrmOptions.edittype == undefined) ? {isfind: true} : sfrmOptions.edittype;
        showTitle = (sfrmOptions.showTitle == undefined) ? true : sfrmOptions.showTitle;
        orderStr = (sfrmOptions.orderStr == undefined) ? "" : sfrmOptions.orderStr;
        coURL = (sfrmOptions.coURL == undefined) ? $C.cos.commonfind : sfrmOptions.coURL;
    }

    readOptions();
    var valuehtml = undefined;

    this.tag = 0;
    initPopFrm();
    this.TSearchForm = function (NewOptions) {
        sfrmOptions = $.extend(sfrmOptions, NewOptions);
        readOptions();
    };

    this.extendOptions = function (NewOptions) {
        sfrmOptions = $.extend(sfrmOptions, NewOptions);
        readOptions();
    };

    this.getOptions = function () {
        return sfrmOptions;
    };

    function initPopFrm() {
        $.ajax({
            url: htmlTempt,
            type: 'get',
            dataType: 'text',
            cache: false,
            async: true,
            contentType: "text/HTML; charset=utf-8",
            success: function (data) {
                //alert(data);
                if ($("#pop_search_divs").length == 0)
                    $("<div id='pop_search_divs'></div>").appendTo("body");
                data = data.replace("$pwid$", "'" + id + "'");
                var pwidth = (sfrmOptions.width) ? sfrmOptions.width : "400px";
                var pheight = (sfrmOptions.height) ? sfrmOptions.height : "300px";
                data = data.replace("$pwwidth$", pwidth);
                data = data.replace("$pwheigth$", pheight);
                var pw = $(data).appendTo("#pop_search_divs");
                valuehtml = getInputByField("value").parent().html();
                $("#" + id).find(".easyui-linkbutton[cjoptions]").each(function (index, el) {
                    var co = $.c_parseOptions($(this));
                    if (co.caction == "act_search") {
                        $(this).click(function () {
                            OnFindClick();
                        });
                    } else if (co.caction == "act_ok") {
                        $(this).click(function () {
                            OnOKClick();
                        });
                    } else if (co.caction == "act_select") {
                        $(this).click(function () {
                            OnSelectClick();
                        });
                    } else if (co.caction == "act_cancel") {
                        $(this).click(function () {
                            $("#" + id).window("close");
                        });
                    }
                });
                $.parser.parse(pw.parent());///////////////////////刷新整个界面？？
                var listgrid = $("#" + id).find("table[cjoptions]");
                var multiRow = (sfrmOptions.multiRow == undefined) ? false : sfrmOptions.multiRow;
                var findcols = filtFindFields(sfrmOptions.gdListColumns);
                if (istree) {
                    listgrid.treegrid({
                        idField: idField,
                        treeField: treeField,
                        onDblClickRow: onTreeDbClick,
                        columns: [sfrmOptions.gdListColumns]
                    });
                    if (!showTitle) {
                        listgrid.treegrid({
                            url: coURL
                        });
                    }
                } else {
                    listgrid.datagrid({
                        border: false,
                        singleSelect: !multiRow,
                        onDblClickRow: onListDbClick,
                        columns: [sfrmOptions.gdListColumns]
                    });
                }
                var item = getInputByField("item");
                if (!item) {
                    $.messager.alert('错误', '没有找到item录入框!', 'error');
                    return;
                }
                item.combobox({
                    data: findcols,
                    valueField: 'field',
                    textField: 'title',
                    onSelect: onItemSelect
                });
                if (findcols.length > 0) {
                    item.combobox("setValue", findcols[0].field);
                }
                if (!showTitle) {
                    $("#" + id).find(".easyui-layout").each(function (index, el) {
                        if (index == 1) {
                            $(this).layout("remove", "north");
                        }
                    });
                }
                //show();
                inited = true;
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                $.messager.alert('错误', '获取Search模板文件错误!', 'error');
            }
        });
    }

    function filtFindFields(columns) {
        var rst = [];
        for (var i = 0; i < columns.length; i++) {
            var col = columns[i];
            if (!col.notfind) {
                rst.push(col);
            }
        }
        return rst;
    }

    this.show = show;

    function show(clear) {
        if (!inited) {
            setTimeout(function () {
                show(clear);
            }, 100);
        } else doshow(clear);
    }

    function doshow(clear) {
        if (clear) {
            var listgrid = $("#" + id).find("table[cjoptions]");
            var multiRow = (sfrmOptions.multiRow == undefined) ? false : sfrmOptions.multiRow;
            if (istree) {
                listgrid.treegrid("loadData", []);
            } else {
                $C.grid.clearData(listgrid);
                listgrid.datagrid({singleSelect: !multiRow});
            }
        }
        if (autoFind) {
            OnFindClick();
        }
        $("#" + id).window("open");
    }

    function getInputTypeod(el) {
        var cls = $(el).attr("class");
        if ((cls.indexOf("easyui-datetimebox") >= 0) || (cls.indexOf("easyui-datebox") >= 0)) {
            return 1;
        } else if (cls.indexOf("easyui-combobox") >= 0) {
            return 2;//combobox
        } else if (cls.indexOf("easyui-combotree") >= 0) {
            return 3;//combotree
        } else if ((cls.indexOf("easyui-textbox") >= 0) || (cls.indexOf("easyui-validatebox") >= 0) || (cls.indexOf("easyui-numberbox") >= 0)) {
            return 5;
        } else if (cls.indexOf("corsair-label") >= 0) {
            return 11;
        }
    }

    function getInputByField(fdname) {
        var inpt = undefined;
        $("#" + id).find("input[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            var et = getInputTypeod(el);
            if (co.fdname == fdname) {
                inpt = $(this);
                return;
            }
        });
        return inpt;
    }

    function onListDbClick() {
        OnOKClick();
    }

    function onTreeDbClick(row) {
        OnOKClick();
    }


    function getFdItemdataByvalue(value) {
        if (!value) return undefined;
        var cdata = getInputByField("item").combobox("getData");
        for (var i = 0; i < cdata.length; i++) {
            var idata = cdata[i];
            if (idata["field"] == value)
                return idata;
        }
    }

    function onItemSelect(row) {
        var p = getInputByField("value").parent();
        p.html(valuehtml);
        $.parser.parse(p);
        var v = getInputByField("value");
        if (row.formatter) {
            var comurl = row.formatter("get_com_url");
            if (comurl) {
                v.combobox({
                    data: comurl.data,
                    valueField: comurl.valueField,
                    textField: comurl.textField
                });
            } else {
                //isdate?
            }
        } else {
            // v.textbox("setValue", "");
        }
    }

    function OnFindClick() {
        var parms = [];
        var max = undefined;
        if (showTitle) {
            var fd = getInputByField("item").textbox("getValue");
            var v = getInputByField("value").textbox("getValue");
            max = getInputByField("max_rst").textbox("getValue");
            parms = (fd && v) ? [
                {parmname: fd, reloper: "like", parmvalue: v}
            ] : [];
        }
        if (sfrmOptions.extParms)
            parms = parms.concat(sfrmOptions.extParms);
        var fdparms = {
            url: coURL,
            edittps: edittps,
            jpaclass: sfrmOptions.JPAClass,
            parms: parms
        };
        if (istree) {
            fdparms.type = "tree";
            fdparms.parentid = pidField;
            fdparms.textfield = treeField;
        } else {
            if (max)
                fdparms.max = max;
            fdparms.type = "list";
        }
        if (!$isEmpty(orderStr))
            fdparms.order = orderStr;

        var selfLine = (sfrmOptions.selfLine == undefined) ? true : sfrmOptions.selfLine;
        sfrmOptions.selfLine = selfLine;
        if (sfrmOptions.beforeFind) {
            if (!sfrmOptions.beforeFind(fdparms))
                return;
        }
        var url = fdparms.url;
        if (url.indexOf("?") < 0) {
            url = url + "?1=1";
        }
        for (var parm in fdparms) {
            if (typeof(fdparms[parm]) == "function")
                continue;

            if ((parm != "url") && (fdparms[parm] != undefined))
                if (parm == "parms")
                    url = url + "&parms=" + JSON.stringify(fdparms[parm]);
                else if (typeof(fdparms[parm]) == "object") {
                    if (fdparms[parm] != null)
                        url = url + "&" + parm + "=" + JSON.stringify(fdparms[parm]);
                } else
                    url = url + "&" + parm + "=" + fdparms[parm];
        }
        $ajaxjsonget(url, function (jsdata) {
            if (((jsdata.length == 1) && singleAutoReturn) || ((jsdata.length == 0) && notFindAutoReturn)) {
                if (sfrmOptions.onResult)
                    sfrmOptions.onResult(jsdata);
                $("#" + id).window("close");
                return;
            }
            var listgrid = $("#" + id).find("table[cjoptions]");
            if (istree) {
                if (closeAllTree)
                    $C.tree.setTreeNodesState(jsdata, "closed");
                else
                    jsdata = $C.tree.setTree1OpendOtherClosed(jsdata);//第一级开着 第二级后全关上
                listgrid.treegrid("loadData", jsdata);
            } else {
                $C.grid.clearData(listgrid);
                listgrid.datagrid("loadData", jsdata);
            }
        }, function () {
            //console.error("查询错误");
        });
    }

    this.close = function () {
        $("#" + id).window("close");
    };

    this.getData = function () {
        var listgrid = $("#" + id).find("table[cjoptions]");
        if (istree) {
            return listgrid.treegrid('getData');
        } else {
            return listgrid.datagrid('getData');
        }
    };

    function OnOKClick() {
        var listgrid = $("#" + id).find("table[cjoptions]");
        if (istree) {
            var rows = listgrid.treegrid('getSelections');
        } else {
            var rows = listgrid.datagrid('getSelections');
        }
        if (rows.length == 0) {
            $.messager.alert('错误', '没有选择数据!', 'error');
        } else {
            if (sfrmOptions.onResult) {
                sfrmOptions.onResult(rows);
            }
            $("#" + id).window("close");
        }
    }

    function OnSelectClick() {
        var listgrid = $("#" + id).find("table[cjoptions]");
        if (istree) {
            var rows = listgrid.treegrid('getSelections');
            if (rows.length == 0) {
                listgrid.treegrid("selectAll");
            } else {
                listgrid.treegrid("unselectAll");
            }
        } else {
            var rows = listgrid.datagrid('getSelections');
            if (rows.length == 0) {
                listgrid.datagrid("selectAll");
            } else {
                listgrid.datagrid("unselectAll");
            }
        }
    }
}