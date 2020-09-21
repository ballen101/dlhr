/**
 * Created by shangwen on 2015-06-30.
 */

function getInputType(co) {
    //var cls = $(el).attr("class");
    var cls = co.easyui_class;
    if (!cls)alert(JSON.stringify(co) + "没有设置easyui_class");
    if (cls.indexOf("easyui-datetimebox") >= 0) {
        return 1;
    } else if (cls.indexOf("easyui-datebox") >= 0) {
        return 6;//datebox
    } else if (cls.indexOf("easyui-combobox") >= 0) {
        return 2;//combobox
    } else if (cls.indexOf("easyui-combotree") >= 0) {
        return 3;//combotree
    } else if ((cls.indexOf("easyui-textbox") >= 0) || (cls.indexOf("easyui-validatebox") >= 0)) {
        return 5;
    } else if (cls.indexOf("easyui-numberbox") >= 0) {
        return 7;//numberbox
    } else if (cls.indexOf("easyui-timespinner") >= 0) {
        return 8;//timespinner
    } else if (cls.indexOf("corsair-label") >= 0) {
        return 11;
    } else if (cls.indexOf("corsair-combogrid") >= 0) {
        return 12;
    }
}

function getEasuiFuncName(co) {
    var clsname = co.easyui_class;
    if (!clsname)alert(JSON.stringify(co) + "没有设置easyui_class");

    if (co.comidx != undefined) {
        var conurl = eval("comUrl_" + co.comidx);
        var type = conurl.type;
        if (type == "combobox") {
            return "combobox"
        }
        if (type == "combotree") {
            return "combotree";
        }
    }
    return getEasuiFuncNameByCls(clsname);
}

function getEasuiFuncNameByCls(clsname) {
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
    else if (clsname == "easyui-timespinner")
        return "timespinner";
    else if (clsname == "easyui-combogrid") {
        return "combogrid";
    } else if (clsname == "corsair-label")
        return "label";
    else return "textbox";
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
var JSONBandingFrmOptions = {
    filter: undefined,//需要绑定的界面 filter
    onChange: function (fieldname, newValue, oldValue) {

    }
};
function TJSONBandingFrm(options) {
    var filter = (options) ? options.filter : undefined;
    var onChange = (options) ? options.onChange : undefined;
    var isloading = false;
    var _els = (filter) ? getInputArray(filter) : undefined;
    this.getInputArray = function (vfilter) {
        if (!_els) {
            if (!vfilter)
                vfilter = filter;
            _els = getInputArray(vfilter);
        }
        return _els;
    };

    //全部重新处理
    this.regetInputArray = function (vfilter) {
        if (!vfilter)
            vfilter = filter;
        _els = getInputArray(vfilter);
        return _els;
    };

    //好像有问题 先不查了
    this.appendInputArray = function (vfilter) {
        if (!vfilter) {
            throw "parm vfilter can not null ";
        }
        var aels = getInputArray(vfilter);
        _els = $.extend(_els, aels);
        return _els;
    };

    function onValueChange(newValue, oldValue) {
        var co = this.cop;
        if ((co != undefined) && (onChange)) {
            if (!isloading) {
                if (this.fn == "combogrid") {
                    var obj = $(this);
                    var cbop = obj.combogrid("options");
                    if (!cbop.idField) {
                        alert("【combogrid】控件需要设置【idField】属性");
                        return;
                    }
                    if (!cbop.textField) {
                        alert("【combogrid】控件需要设置【textField】属性");
                        return;
                    }
                    var data = obj.combogrid("grid").datagrid("getData");
                    var rows = (data.rows) ? data.rows : data;
                    var selectedrow = undefined;
                    for (var i = 0; i < rows.length; i++) {
                        if (rows[i][cbop.idField] == newValue) {
                            selectedrow = rows[i];
                            //alert(rows[i][cbop.idField] + ":" + newValue + ":" + selectedrow);
                            break;
                        }
                    }
                    //if (selectedrow)newValue = selectedrow;
                    onChange(co.fdname, newValue, oldValue, selectedrow, this);
                } else
                    onChange(co.fdname, newValue, oldValue, undefined, this);
            }
        }
    }

    function getInputArray(filter) {
        var rst = {els_all: [], els_readonly: [], els_lables: []};
        $(filter).find("input[cjoptions],select[cjoptions]").each(function (index, el) {
            var s = $.trim($(this).attr("cjoptions"));
            var co = $.c_parseOptions($(this));
            //alert(JSON.stringify(co));
            this.cop = co;
            this.et = getInputType(co);


            var fn = getEasuiFuncName(co);
            this.fn = fn;

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
        for (var i = 0; i < rst.els_all.length; i++) {
            var el = rst.els_all[i];
            var co = el.cop;
            var obj = $(el);
            var iscombox = false;
            //alert(el.fn + " " + co.fdname);
            obj[el.fn](co);
            if (onChange) {
                obj[el.fn]({onChange: onValueChange});
            }
            if (co.comidx != undefined) {
                iscombox = true;
                var conurl = eval("comUrl_" + co.comidx);
                var jsondata = conurl.jsondata;
                var type = conurl.type;
                var valueField = conurl.valueField;
                var textField = conurl.textField;
                if (jsondata != undefined) {
                    if (type == "combobox") {
                        obj.combobox({
                            data: jsondata,
                            valueField: valueField,
                            textField: textField
                        });
                        obj.combobox(co);
                    }
                    if (type == "combotree") {
                        obj.combotree(co);
                        obj.combotree("loadData", jsondata);
                    }
                }
            } else {

            }
            if (co.crequired) {
                var tv = getInputLabel(rst.els_lables, co.fdname);//$(this).parent().prev();
                if (tv && (!tv.attr("seted"))) {
                    tv.attr("seted", true);
                    tv.html(tv.html() + "(<span style='color: red'>*</span>)");
                }

            }
            setInputReadOnly(el, co.readonly);
        }

        //设置table
        $(filter).find("table[cjoptions]").each(function (index, el) {
            var obj = $(this);
            var co = $.c_parseOptions(obj);
            var fdname = co.fdname;
            if (fdname) {
                var cls = obj.attr("class");
                if (cls)
                    cls = cls + obj.attr("cjoptions");
                else
                    cls = obj.attr("cjoptions");
                if (cls.indexOf("easyui-treegrid") != -1) {
                    obj.treegrid(co);
                }
                if (cls.indexOf("easyui-datagrid") != -1) {
                    obj.datagrid(co);
                }
            }
        });
        _els = rst;
        return rst;
    }

    this.cparser = cparser;
    function cparser(els_all) {
        for (var i = 0; i < els_all.length; i++) {
            var e = els_all[i];
            var co = e.cop;
            if ((co) && (co.easyui_class)) {
                var cls = co.easyui_class;
                cls = cls.substr(cls.indexOf("-") + 1, cls.length);
                //alert(JSON.stringify(co));
                //alert(cls);
                $(e)[cls](co);
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
        isloading = true;
        //if (!els_all)
        els_all = _els.els_all;
        clearFrmData(els_all);
        for (var i = 0; i < grids.length; i++) {
            $C.grid.clearData(grids[i]);
        }
        isloading = false;
    };

    this.clearGridViewData = function (grids) {
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

    this.setFieldReadOnly = setFieldReadOnly;
    function setFieldReadOnly(fdname, readonly) {
        var input = findElinput(_els.els_all, fdname);
        if (input) {
            setInputReadOnly(input, readonly);
        }
    }

    this.setInputReadOnly = setInputReadOnly;
    function setInputReadOnly(copInput, readonly) {
        //var tp = copInput.et;
        var v = getInputValue(copInput);
        var fn = copInput.fn;
        var input = $(copInput);
        //alert(copInput.cop.fdname + ":" + readonly + ":" + tp);
        input[fn]({readonly: readonly});
        if ($.parser.plugins.indexOf("sidemenu") >= 0) {
            input.resize();
        }
        if (readonly) {
            input.next().find("input:first").addClass("tabs-tool");
            input.next().find("textarea:first").addClass("tabs-tool");
        } else {
            input.next().find("input:first").removeClass("tabs-tool");
            input.next().find("textarea:first").removeClass("tabs-tool");
        }
        setInputValue(copInput, v);
    }

    this.clearFrmData = clearFrmData;
    function clearFrmData(els_all) {
        //if (!els_all)
        els_all = _els.els_all;
        isloading = true;
        for (var i = 0; i < els_all.length; i++) {
            var et = els_all[i];
            var iobj = $(et);
            var tp = iobj.attr("type");
            if (tp == "checkbox") {
                $(this).attr("checked", false);
                return;
            }
            var co = et.cop;
            iobj[et.fn]('setValue', undefined);//""
        }
        isloading = false;
    }

    this.findElinput = findElinput;
    function findElinput(els_all, fdname) {
        //if (!els_all)
        els_all = _els.els_all;
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            if (co.fdname == fdname) {
                return els_all[i]
            }
        }
        return undefined;
    }

    this.getInput = getInput;
    function getInput(fdname) {
        return findElinput(_els.els_all, fdname);
    }

    this.getLabel = getLabel;
    function getLabel(fdname) {
        var els_all = _els.els_lables;
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            if (co.fdname == fdname) {
                return els_all[i]
            }
        }
        return undefined;
    }


    this.fromJsonData = function (els_all, jsondata) {
        isloading = true;
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            var v = jsondata[co.fdname];
            setInputValue(els_all[i], v);
        }
        isloading = false;
        return true;
    };


    this.setFieldValue = setFieldValue;
    function setFieldValue(fdname, value) {
        var iput = findElinput(_els.els_all, fdname);
        if (iput) {
            setInputValue(iput, value);
            return true;
        } else
            return false;
    }

    this.getFieldValue = getFieldValue;
    function getFieldValue(fdname) {
        var iput = findElinput(_els.els_all, fdname);
        if (iput)
            return getInputValue(iput);
        else
            return null;
    }

    this.setFeildCaption = setFeildCaption;
    function setFeildCaption(fdname, caption) {
        var leb = getLabel(fdname);
        if (leb) {
            $(leb).html(caption);
        }
    }

    this.setFieldHide = setFieldHide;
    function setFieldHide(fdname) {
        var leb = getLabel(fdname);
        if (leb) {
            $(leb).css("display", "none");
        }
        var ipt = getInput(fdname);
        if (ipt) {
            //alert(.html());
            $(ipt).parent().css("display", "none");
        }
    }

    this.setFieldShow = setFieldShow;
    function setFieldShow(fdname) {
        var leb = getLabel(fdname);
        if (leb) {
            $(leb).css("display", "");
        }
        var ipt = getInput(fdname);
        if (ipt) {
            $(ipt).parent().css("display", "");
        }
    }

    function isValidDate(date) {
        return (date instanceof Date) && isNaN(date.getTime())
    }

    this.setInputValue = setInputValue;
    function setInputValue(copInput, v) {
        //isloading = true;
        var co = copInput.cop;
        var fn = copInput.fn;
        var et = copInput.et;
        if ((fn == "datetimebox") || (fn == "datebox")) {
            if ((v == undefined) || (v == null) || (v == "NULL") || (v == "")) {
                $(copInput)[fn]('setValue', "");
            } else {
                var dt = (new Date()).fromStr(v);
                if (isValidDate(dt))
                    $(copInput)[fn]('setValue', "");
                else
                    $(copInput)[fn]('setValue', dt.toUIDatestr());
            }
        } else if (fn == "combotree") {
            if ((v == undefined) || (v == null) || (v == "NULL"))
                $(copInput).combotree('setValue', "");
            else {
                $(copInput).combotree('setValue', v);
            }
        } else if (fn == "label") {
            if ((v == undefined) || (v == null) || (v == "NULL"))
                $(copInput).attr("value", "");
            else
                $(copInput).attr("value", getDisplayValue(co, v));
        } else {
            if ((v == undefined) || (v == null) || (v == "NULL"))
                $(copInput)[fn]('setValue', "");
            else {
                var opt = {};
                var iscom = (fn == "combobox");
                if (!iscom) {
                    try {
                        opt = $(copInput)[fn]("options");
                        iscom = (("valueField" in opt) && ("textField" in opt) );//&& ("data" in opt)
                    } catch (e) {
                        //alert(e.message);
                    }
                }
                if ((co.dicgpid != undefined) || (co.comidx != undefined) || iscom) {
                    if (co.multiple || opt.multiple) {
                        var tv = eval("[" + v + "]");
                        $(copInput).combobox('setValues', tv);
                    } else {
                        $(copInput).combobox('setValue', v);//select
                    }
                } else
                    $(copInput)[fn]('setValue', v);
            }
        }
        //isloading = false;
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
        var fn = copInput.fn;
        var et = copInput.et;
        if (fn == "label") {
            return $(copInput).attr("value");
        } else if ((fn == "combobox") || (fn == "combotree")) {
            if (fn == "combobox") {
                if ($(copInput)[fn]("options").multiple) {
                    var tv = $(copInput)[fn]("getValues");
                    return tv.toString();
                } else {
                    return $(copInput)[fn]("getValue");
                }
            } else {
                return $(copInput)[fn]("getValue");
            }
        } else {
            return $(copInput)[fn]("getValue");
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
        //console.error(JSON.stringify(els_all));
        //console.error(JSON.stringify(jsondata));
        for (var i = 0; i < els_all.length; i++) {
            //var co = $.c_parseOptions($(els_all[i]));
            var co = els_all[i].cop;
            //var et = getInputType(co);
            var v = jsondata[co.fdname];
            var rdo = Boolean(co.readonly);

            if ((co.crequired) && (!rdo) && ((v == null) || (!v) || (v == ""))) {
                var lab = getInputLabel(lables, co.fdname);
                if (lab)
                    msg = msg + "<" + lab.html() + "><br/>";
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
                        } else if (col.crequired) {
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
            if ((col.crequired) && ((v == null) || (v == undefined) || (v.length == 0)))
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