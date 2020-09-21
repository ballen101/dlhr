/**
 * Created by shangwen on 2016/6/14.
 * for jsonbanding1.js
 */



function CPopInfoWindow(options) {
    var issetEditValue = false;
    var voptions = options;
    if (!voptions.windowfilter)
        throw new Error("没有设置windowfilter属性!");
    var iwobj = $(voptions.windowfilter);
    if (iwobj.attr('class').indexOf("easyui-window") < 0)
        throw new Error("非easyui-window窗口，不允许弹出!");
    var els_all = [];//所有输入框对象
    var lables = [];
    var JSONBandingFrm = new TJSONBandingFrm();//数据绑定

    initbts();//初始化按钮事件

    function initbts() {
        var els = JSONBandingFrm.getInputArray(voptions.windowfilter);
        els_all = els.els_all;
        //els_readonly = els.els_readonly;
        lables = els.els_lables;
        initInput();
        var bts = iwobj.find("a[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            if (co.caction == "act_ok") {
                el.onclick = function () {
                    acceptAllGrid();
                    //从界面获取数据
                    var jdata = iwobj.c_toJsonData(voptions.jsonData, voptions.isNew);
                    //获取列表数据
                    //需要重新load grid 数据吗？  可能不需要！

                    if (!JSONBandingFrm.checkNotNull(els_all, lables, jdata)) return false;

                    if (voptions.afterGetData) {
                        voptions.afterGetData(jdata);
                    }
                    if ((voptions.onOK) && (voptions.onOK(voptions.isNew, jdata)))
                        iwobj.window('close');
                };
            }
            if (co.caction == "act_cancel") {
                el.onclick = function () {
                    if (voptions.onCancel) {
                        var errmsg = voptions.onCancel();
                        if (errmsg)alert(errmsg);
                    }
                    iwobj.window('close');
                }
            }
        });
    }

    function initInput() {
        for (var i = 0; i < els_all.length; i++) {
            var co = $.c_parseOptions($(els_all[i]));
            var obj = $(els_all[i]);
            var iscombox = false;
            if (co.comidx != undefined) {
                iscombox = true;
                var conurl = eval("comUrl_" + co.comidx);
                var jsondata = conurl.jsondata;
                var type = conurl.type;
                var valueField = conurl.valueField;
                var textField = conurl.textField;
                if (jsondata != undefined) {
                    if (frmvoptions.onInitInputComboboxDict) {
                        frmvoptions.onInitInputComboboxDict(co, jsondata);
                    }
                    if (type == "combobox") {
                        obj.combobox({
                            data: jsondata,
                            valueField: valueField,
                            textField: textField
                        });
                        obj.combobox({onChange: onValueChange});
                    }
                    if (type == "combotree") {
                        obj.combotree("loadData", jsondata);
                        obj.combotree({onChange: onValueChange});
                    }
                }
            } else {
                var et = getInputType(els_all[i]);
                if (et == 1) {
                    obj.datetimebox({onChange: onValueChange});
                } else
                    obj.textbox({onChange: onValueChange});
            }
            if (iscombox) {
                var input = obj.combobox("textbox");
                //alert(input.parent().html());
                //alert(input.attr("class"));
                //input.keydown(function () {
                //alert("fdsa");
                //});
            } else {
                var input = obj.textbox("textbox");
                //input.bind('keydown', function (e) {
                //console.error("textkeydown:" + keyCode);
                //});
            }
            if (co.required) {
                var tv = getInputLabel(lables, co.fdname);//$(this).parent().prev();
                if (tv)
                    tv.html(tv.html() + "(<span style='color: red'>*</span>)");
            }
            if (!co.readonly) {
                setInputReadOnly($(obj), false);
            } else {
                setInputReadOnly($(obj), true);
            }
        }
    }

    function setInputReadOnly(input, readonly) {
        //alert(input.next().find("input:first").parent().html());
        var tp = getInputType(input[0]);
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

    this.show = show;
    function show(options) {
        if (options)
            extendOptions(options);
        if (voptions.isNew)
            iwobj.c_setJsonDefaultValue(voptions.jsonData, true);
        clearInputData();
        setData2Input();
        setData2Grid();
        iwobj.window({onOpen: onWindowShow});
        iwobj.window('open');
        iwobj.find(".easyui-tabs").each(function (index, el) {
            $(this).tabs("select", 0);
        });
    }

    this.extendOptions = extendOptions;
    function extendOptions(NewOptions) {
        voptions = $.extend(voptions, NewOptions);
    }

    this.getOptions = function () {
        return voptions;
    };

    function onWindowShow() {
        if (voptions.onShow) {
            voptions.onShow(voptions.jsonData);
        }
    }


    function clearInputData() {
        iwobj.find("input[cjoptions]").each(function (index, el) {
            var tp = $(this).attr("type");
            if (tp == "checkbox")
                $(this).attr("checked", false);
            else
                $(this).textbox('setValue', "");
        });
        iwobj.find("table[cjoptions][class*='easyui-datagrid']").each(function (index, el) {
            $(this).datagrid("loadData", []);
        });
        iwobj.find("table[cjoptions][class*='easyui-treegrid']").each(function (index, el) {
            $(this).treegrid("loadData", []);
        });
    }

    this.getFieldValue = function (fdname) {
        var v = $getInputValue(els_all, fdname);
        if (v)
            return v;
        else
            return voptions.jsonData[fdname];
    };


    this.setFieldValue = setFieldValue;
    function setFieldValue(fdname, value) {
        issetEditValue = true;
        try {
            voptions.jsonData[fdname] = value;
            var iput = findElinput(els_all, fdname);
            if (iput) {
                setInputValue(iput, value);
            }
        }
        finally {
            issetEditValue = false;
        }
    }


    function findElinput(els_all, fdname) {
        for (var i = 0; i < els_all.length; i++) {
            var co = $.c_parseOptions($(els_all[i]));
            if (co.fdname == fdname) {
                return els_all[i]
            }
        }
        return undefined;
    }

    function setData2Input() {
        JSONBandingFrm.fromJsonData(els_all, voptions.jsonData);
    }

    function setData2Grid() {
        iwobj.find(".easyui-datagrid[id]").each(function (index, el) {
            var id = $(el).attr("id");
            var tbdata = voptions.jsonData[id];
            if ($(this).datagrid) {
                if (tbdata != undefined) {
                    //if (tbdata.length > 0)  如果为空 也需要载入 数据 以载入数据和Grid的关联关系，否则空数据无法保存
                    $(this).datagrid("loadData", tbdata);
                    //else
                    //    $(this).datagrid("loadData", []);
                }
            }
        });
    }

    function setInputValue(iput, value) {
        var co = $.c_parseOptions($(iput));
        var et = getInputType(iput);
        var v = value;
        switch (et) {
            case 1:
            {
                if ((v == undefined) || (v == null) || (v == "NULL") || (v == "")) {
                    $(iput).datetimebox('setValue', "");
                } else {
                    var dt = (new Date()).fromStr(v);
                    $(iput).datetimebox('setValue', dt.toUIDatestr());
                }
                break;
            }
            case 3://combotree
            {
                if ((v == undefined) || (v == null) || (v == "NULL"))
                    $(iput).combotree('setValues', "");
                else {
                    $(iput).combotree('setValues', v);
                }
                break;
            }
            default:
            {
                if ((v == undefined) || (v == null) || (v == "NULL"))
                    $(iput).textbox('setValue', "");
                else {
                    if (co.dicgpid || co.comidx) {
                        $(iput).combobox('select', v);
                    } else
                        $(iput).textbox('setValue', v);
                }
                break;
            }
        }
    }

    function acceptAllGrid() {
        iwobj.find(".easyui-datagrid[id]").each(function (index, el) {
            var id = $(el).attr("id");
            var tbdata = voptions.jsonData[id];
            if ((tbdata) && ($(this).datagrid)) {
                $(this).datagrid("acceptChanges");
            }
        });
    }


    function $getInputValue(els_all, fdname) {
        for (var i = 0; i < els_all.length; i++) {
            var co = $.c_parseOptions($(els_all[i]));
            var et = getInputType(els_all[i]);
            if (co.fdname == fdname) {
                var v = $(els_all[i]).textbox("getValue");
                return v;
            }
        }
        return undefined;
    }

    function onValueChange(newValue, oldValue) {
        var obj = $(this);
        var co = $.c_parseOptions(obj);
        if ((co != undefined) && (voptions.onEditChanged) && (!issetEditValue)) {
            voptions.onEditChanged(co.fdname, newValue, oldValue);
        }
    }
}
