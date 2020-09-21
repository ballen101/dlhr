/**
 * Created by shangwen on 2015-06-30.
 */

function getInputType(el) {
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

function getEasuiFuncName(clsname) {
    if (clsname == "easyui-datetimebox")
        return "datetimebox";
    else if (clsname == "easyui-datebox")
        return "datebox";
    else if (clsname == "easyui-combobox")
        return "combobox";
    else if (clsname == "easyui-combotree")
        return "combotree";
    else if (clsname == "easyui-textbox")
        return "textbox";
    else if (clsname == "easyui-validatebox")
        return "validatebox";
    else if (clsname == "easyui-numberbox")
        return "numberbox";
}


function getInputLabel(lables, fdname) {
    for (var i = 0; i < lables.length; i++) {
        //var co = $.c_parseOptions($(lables[i]));
        var co = lables[i].cop;
        if (co.fdname == fdname) {
            var lb = $(lables[i]);
            return lb;
        }
    }
    return undefined;
}

function TJSONBandingFrm() {
    this.getInputArray = function (filter) {
        var rst = {els_all: [], els_readonly: [], els_lables: []};
        $(filter).find("input[cjoptions],select[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            this.cop = co;
            this.et = getInputType(this);
            if (co.readonly) {
                rst.els_readonly.push(this);
                $(this).attr("readonly", "readonly");
                //$(this).css("background-color", "#F5F5F5");
            }
            rst.els_all.push(this);
        });
        $(filter).find("td[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            this.cop = co;
            rst.els_lables.push(this);
        });
        return rst;
    };

    this.cparser = cparser;
    function cparser(els_all) {
        for (var i = 0; i < els_all.length; i++) {
            var e = els_all[i];
            var co = e.co;
            if (co.easyui_class) {
                $(e)[co.easyui_class](co);
            }
        }
    }

    this.acceptAllGrid = function (filter, jsondata) {
        $(filter).find(".easyui-datagrid[id]").each(function (index, el) {
            var id = $(el).attr("id");
            var tbdata = jsondata[id];
            if ((tbdata) && ($(this).datagrid)) {
                $(this).datagrid("acceptChanges");
            }
        });
    };

    this.clearMainData = function (els_all, grids) {
        this.clearFrmData(els_all);
        for (var i = 0; i < grids.length; i++) {
            $C.grid.clearData(grids[i]);
        }
    };

    this.setAllInputReadOnly = setAllInputReadOnly;
    function setAllInputReadOnly(els_all, readonly) {
        for (var i = 0; i < els_all.length; i++) {
            setInputReadOnly(els_all[i], readonly);
        }
    }

    this.setInputReadOnly = setInputReadOnly;
    function setInputReadOnly(copInput, readonly) {
        var tp = copInput.et;
        var input = $(copInput);
        if (tp == 1)
            input.datetimebox({readonly: readonly});
        if ((tp == 2) || (tp == 3))
            input.combobox({readonly: readonly});
        if (tp == 5)
            input.textbox({readonly: readonly});
        if (readonly) {
            input.next().find("input:first").css("background-color", "#E8EEFA");
            input.next().find("textarea:first").css("background-color", "#E8EEFA");
        } else {
            input.next().find("input:first").css("background-color", "#FFFFFF");
            input.next().find("textarea:first").css("background-color", "#FFFFFF");
        }
    }

    this.clearFrmData = function (els_all) {
        for (var i = 0; i < els_all.length; i++) {
            $(els_all[i]).textbox('setValue', "");
        }
    };

    this.fromJsonData = function (els_all, jsondata) {
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            var v = jsondata[co.fdname];
            setInputValue(els_all[i], v);
        }
        return true;
    };

    this.setInputValue = setInputValue;
    function setInputValue(copInput, v) {
        var co = copInput.cop;
        var et = copInput.et;
        switch (et) {
            case 1:
            {
                if ((v == undefined) || (v == null) || (v == "NULL") || (v == "")) {
                    $(copInput).datetimebox('setValue', "");
                } else {
                    var dt = (new Date()).fromStr(v);
                    $(copInput).datetimebox('setValue', dt.toUIDatestr());
                }
                break;
            }
            case 3:
            {
                if ((v == undefined) || (v == null) || (v == "NULL"))
                    $(copInput).combotree('setValues', "");
                else {
                    $(copInput).combotree('setValues', v);
                }
                break;
            }
            case 11:
            {
                if ((v == undefined) || (v == null) || (v == "NULL"))
                    $(copInput).attr("value", "");
                else
                    $(copInput).attr("value", getDisplayValue(co, v));
                break;
            }
            default:
            {
                if ((v == undefined) || (v == null) || (v == "NULL"))
                    $(copInput).textbox('setValue', "");
                else {
                    var iscom = false;
                    try {
                        var opt = $(this).textbox("options");
                        iscom = (("valueField" in opt) && ("textField" in opt) && ("data" in opt));
                    } catch (e) {
                        //alert(e.message);
                    }
                    if ((co.dicgpid != undefined) || (co.comidx != undefined) || iscom) {
                        $(copInput).combobox('select', v);
                    } else
                        $(copInput).textbox('setValue', v);
                }
                break;
            }
        }
    }

    function gettreeDisplayValue(jsondata, valueField, textField, v) {
        for (var i = 0; i < jsondata.length; i++) {
            var row = jsondata[i];
            if (row[valueField] == v)
                return row[textField];
            if (row.children) {
                var dv = gettreeDisplayValue(row.children, valueField, textField, v);
                if (dv) return dv;
            }
        }
    }

    function getDisplayValue(co, v) {
        if (co) {
            if (co.comidx) {
                var conurl = eval("comUrl_" + co.comidx);
                var jsondata = conurl.jsondata;
                var type = conurl.type;
                var valueField = conurl.valueField;
                var textField = conurl.textField;
                if (type == "combobox") {
                    for (var i = 0; i < jsondata.length; i++) {
                        var row = jsondata[i];
                        if (row[valueField] == v)
                            return row[textField];
                    }
                }
                if (type == "combotree") {
                    return gettreeDisplayValue(jsondata, valueField, textField, v);
                }
            } else if (co.formatter && (typeof co.formatter == 'function')) {
                return co.formatter(v);
            } else
                return v;
        } else
            return v;
    }

    this.gridFromJSONdata = function (filter, jsondata) {
        $(filter).find(".easyui-datagrid[id]").each(function (index, el) {
            var id = $(el).attr("id");
            var tbdata = jsondata[id];
            //alert("id:" + id + ":" + JSON.stringify(tbdata));
            if ($(this).datagrid) {
                if (tbdata != undefined) {
                    //if (tbdata.length > 0)  如果为空 也需要载入 数据 以载入数据和Grid的关联关系，否则空数据无法保存
                    $(this).datagrid("loadData", tbdata);
                    //else
                    //    $(this).datagrid("loadData", []);
                }
            }
        });
    };

    this.getInputValue = getInputValue;
    function getInputValue(copInput) {
        var co = copInput.cop;
        var et = copInput.et;
        switch (et) {
            case 2:
                return $(copInput).combobox("getValues");
            case 3:
            {
                var ov = $(copInput).combotree("getValues");
                if (ov)
                    return ov.toString();
                else
                    return "";
            }
            case 11:
            {
                //获取显示值
            }
            default :
            {
                return $(copInput).textbox("getValue");
            }
        }
    }

    this.toJsonData = function (els_all, jsondata, isnew) {
        for (var i = 0; i < els_all.length; i++) {
            var input = els_all[i];
            var co = input.cop;
            jsondata[co.fdname] = getInputValue(input);
        }
        if (!isnew)
            c_setJsonDefaultValue(els_all, jsondata, false);
        return jsondata;
    };

    function c_setJsonDefaultValue(els_all, jsondata, isnew) {
        var ctimes = ["createtime", "create_time"];
        var utimes = ["updatetime", "update_time"];
        var ctors = ["creator"];
        var utors = ["updator"];
        var ent = "entid";
        var std = "stat";
        var usb = "usable";
        var vsb = "isvisible";

        for (var i = 0; i < els_all.length; i++) {
            var fdn = $.c_parseOptions($(els_all[i])).fdname;
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
        }
    }

    this.checkNotNull = function (els_all, lables, jsondata, gdLinesNames, columnss) {
        var msg = "";
        for (var i = 0; i < els_all.length; i++) {
            //var co = $.c_parseOptions($(els_all[i]));
            var co = els_all[i].cop;
            var et = getInputType(els_all[i]);
            var v = jsondata[co.fdname];
            var rdo = Boolean(co.readonly);

            if ((co.required) && (!rdo) && ((v == null) || (!v) || (v == ""))) {
                msg = msg + "<" + getInputLabel(lables, co.fdname).html() + "><br/>";
            }
        }
        var lmsgs = [];
        if (gdLinesNames && columnss) {
            for (var i = 0; i < gdLinesNames.length; i++) {
                var gln = gdLinesNames[i];
                var columns = columnss[i];
                var rows = jsondata[gln];
                for (var j = 0; j < rows.length; j++) {
                    var row = rows[j];
                    for (var k = 0; k < columns.length; k++) {
                        var col = columns[k];
                        if ($.isArray(col)) {
                            for (m = 0; m < col.length; m++) {
                                var coll = col[m];
                                checkv(lmsgs, row, coll);
                            }
                        } else if (col.required) {
                            checkv(lmsgs, row, col);
                        }
                    }
                }
            }
        }
        if (lmsgs.length != 0) {
            msg = msg + "明细行:<br>";
            for (var i = 0; i < lmsgs.length; i++) {
                msg = msg + lmsgs[i] + "<br>";
            }
        }
        if (msg == "") return true;
        else {
            $.messager.alert('错误', msg + '不允许为空！', 'error');
            return false;
        }

        function checkv(lmsgs, row, col) {
            if (!col)return;
            var v = row[col.field];
            if ((col.required) && ((v == null) || (v == undefined) || (v.length == 0)))
                addV(lmsgs, col.title);
        }

        function addV(lmsgs, v) {
            for (var i = 0; i < lmsgs.length; i++) {
                if (lmsgs[i] == v)
                    return;
            }
            lmsgs.push(v);
        }
    };
}