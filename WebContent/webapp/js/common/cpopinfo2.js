/**
 * Created by shangwen on 2016/6/14.
 * need jsbanding2.js
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
    var isloadingdata = false;
    var datachanged = true;

    function onValueChange(fdname, newValue, oldValue, newRowValue, ele) {
        if (oldValue != newValue) {
            if ((voptions.onValueChange) && (!isloadingdata)) {//&& (!issetEditValue)
                voptions.onValueChange(fdname, newValue, oldValue, newRowValue, ele);
            }
            setDataChanged(true, "");
        }

    }

    this.setDataChanged = setDataChanged;
    function setDataChanged(value, mask) {
        datachanged = value;
        if (datachanged) {

        }
    }

    var JSONBandingFrm = new TJSONBandingFrm({
            filter: voptions.windowfilter,
            onChange: onValueChange
        })
        ;//数据绑定
    initbts();//初始化按钮事件
    function initbts() {
        var els = JSONBandingFrm.getInputArray();
        els_all = els.els_all;
        //els_readonly = els.els_readonly;
        lables = els.els_lables;
        //initInput();
        var bts = iwobj.find("a[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            if (co.caction == "act_ok") {
                el.onclick = function () {
                    acceptAllGrid();
                    //从界面获取数据
                    var jdata = voptions.jsonData; //iwobj.c_toJsonData(voptions.jsonData, voptions.isNew);
                    JSONBandingFrm.toJsonData(els_all, jdata, voptions.isNew);
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

    this.close = close;
    function close() {
        clearInputData();
        iwobj.window('close');
    }

    this.getdata = getdata;
    function getdata() {
        //  return iwobj.c_toJsonData(voptions.jsonData, voptions.isNew);
        var jdata = voptions.jsonData; //iwobj.c_toJsonData(voptions.jsonData, voptions.isNew);
        return JSONBandingFrm.toJsonData(els_all, jdata, voptions.isNew);
    }

    this.setFieldReadOnly = setFieldReadOnly;
    function setFieldReadOnly(fdname, readonly) {
        JSONBandingFrm.setFieldReadOnly(fdname, readonly);
    }

    this.show = show;
    function show(options) {
        if (options)
            extendOptions(options);
        if (voptions.isNew)
            iwobj.c_setJsonDefaultValue(voptions.jsonData, true);

        if (voptions.onShow) {
            voptions.onShow(voptions.isNew, voptions.jsonData);
        }
        showDetail();
        //iwobj.window({onOpen: onWindowShow});
        iwobj.window('open');
        iwobj.find(".easyui-tabs").each(function (index, el) {
            $(this).tabs("select", 0);
        });
    }

    this.showDetail = showDetail;
    function showDetail(jsdata) {
        isloadingdata = true;
        if (jsdata)
            voptions.jsonData = jsdata;
        clearInputData();
        setData2Input();
        setData2Grid();
        if (voptions.afterShowDetail)
            voptions.afterShowDetail(voptions.jsonData);
        setDataChanged(false);
        isloadingdata = false;
    }

    this.setAllReadOnly = setAllReadOnly;
    function setAllReadOnly(readonly) {
        JSONBandingFrm.setAllInputReadOnly(els_all, readonly);
    }

    this.reSetInputReadOnly = reSetInputReadOnly;
    function reSetInputReadOnly() {
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            if (!co.readonly) {
                JSONBandingFrm.setInputReadOnly(els_all[i], false);
            } else {
                JSONBandingFrm.setInputReadOnly(els_all[i], true);
            }
        }
    }

    this.getDataChanged = function () {
        return datachanged;
    };

    this.extendOptions = extendOptions;
    function extendOptions(NewOptions) {
        voptions = $.extend(voptions, NewOptions);
    }

    this.getOptions = function () {
        return voptions;
    };

    function onWindowShow() {
        if (voptions.onShow) {
            voptions.onShow(voptions.isNew, voptions.jsonData);
        }
    }


    function clearInputData() {
        JSONBandingFrm.clearMainData(undefined, []);
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

    this.getField = getField;
    function getField(fdname) {
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            //alert(co.fdname + " " + fdname);
            if (co.fdname == fdname) {
                return $(els_all[i]);
            }
        }
    }


    this.setFieldValue = setFieldValue;
    function setFieldValue(fdname, value) {
        issetEditValue = true;
        try {
            voptions.jsonData[fdname] = value;
            JSONBandingFrm.setFieldValue(fdname, value);
        }
        finally {
            issetEditValue = false;
        }
    }

    this.setFieldReadOnly = setFieldReadOnly;
    function setFieldReadOnly(fdname, readonly) {
        JSONBandingFrm.setFieldReadOnly(fdname, readonly);
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
            //var et = getInputType(els_all[i]);
            if (co.fdname == fdname) {
                var v = $(els_all[i]).textbox("getValue");
                return v;
            }
        }
        return undefined;
    }

    /*
     function onValueChange(newValue, oldValue) {
     var obj = $(this);
     var co = $.c_parseOptions(obj);
     if ((co != undefined) && (voptions.onEditChanged) && (!issetEditValue)) {
     voptions.onEditChanged(co.fdname, newValue, oldValue);
     }
     }*/
}
