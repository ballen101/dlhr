/**
 * Created by Administrator on 2015/4/28.
 */
var mainline = undefined;
var frmOptions = undefined, formetype = undefined, wfform = undefined, edittps = undefined;
$.parser.auto = false;
var timestamp = undefined;
timestamp = new Date().getTime();


function closes() {
    $("#loading").fadeOut("normal", function () {
        $(this).remove();
    });
}
var pc, _uicpd = false;


$(document).ready(function () {
        //传入参数   formetype   1制单 2 审批 3 浏览 4 变更
        //id      JPA数据ID 如果有传入，将在界面初始化后载入该JPA数据
        //wfform  作为wfform显示 true 显示 false不显示
        $.parser.onComplete = function () {
            _uicpd = true;
            if (pc) clearTimeout(pc);
            pc = setTimeout(closes, 1000);
        }
        $.parser.parse();
        if (frmOptions && (!mainline)) {
            var parms = $getPageParms();
            edittps = JSON.parse(parms.edittps);
            //alert(parms.edittps);
            frmOptions.initid = parms.id;
            wfform = parms.wfform;
            if ((wfform) && (wfform.toUpperCase != 'TRUE'))
                allowWF = false;
            mainline = new TMainLine(frmOptions);
        }
    }
);

var TFrmType = {ftMainLine: 1, ftSimple: 2};

var TButtons = {
    btNew: {
        disable: function () {
            $("#id_bt_new").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_new").linkbutton("enable")
        }
    },
    btCopy: {
        disable: function () {
            $("#id_bt_copy").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_copy").linkbutton("enable")
        }
    },
    btSave: {
        disable: function () {
            $("#id_bt_save").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_save").linkbutton("enable")
        }
    },
    btSubmit: {
        disable: function () {
            $("#id_bt_submit").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_submit").linkbutton("enable")
        }
    },
    btFind: {
        disable: function () {
            $("#id_bt_find").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_find").linkbutton("enable")
        }
    },
    btReload: {
        disable: function () {
            $("#id_bt_reload").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_reload").linkbutton("enable")
        }
    },
    btDel: {
        disable: function () {
            $("#id_bt_del").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_del").linkbutton("enable")
        }
    },
    btAtt: {
        disable: function () {
            $("#id_bt_att").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_att").linkbutton("enable")
        }
    },
    btPrint: {
        disable: function () {
            $("#id_bt_print").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_print").linkbutton("enable")
        }
    },
    btExpt: {
        disable: function () {
            $("#id_bt_expt").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_expt").linkbutton("enable")
        }
    },
    btExit: {
        disable: function () {
            $("#id_bt_exit").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_exit").linkbutton("enable")
        }
    },
    btDelAtt: {
        disable: function () {
            $("#id_bt_delatt").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_delatt").linkbutton("enable")
        }
    },
    btDownloadAtt: {
        disable: function () {
            $("#id_bt_downloadatt").linkbutton("disable")
        },
        enable: function () {
            $("#id_bt_downloadatt").linkbutton("enable")
        }
    },
    setClick: function (onClick) {
        $("#id_bt_new").linkbutton({
            onClick: function () {
                onClick($C.action.New);
            }
        });
        $("#id_bt_copy").linkbutton({
            onClick: function () {
                onClick($C.action.Copy);
            }
        });
        $("#id_bt_save").linkbutton({
            onClick: function () {
                onClick($C.action.Save);
            }
        });
        $("#id_bt_submit").linkbutton({
            onClick: function () {
                onClick($C.action.Submit);
            }
        });
        $("#id_bt_find").linkbutton({
            onClick: function () {
                onClick($C.action.Find);
            }
        });
        // $("#id_bt_detail").linkbutton({onClick: function () {
        //     onClick($C.action.Detail);
        // }});
        $("#id_bt_reload").linkbutton({
            onClick: function () {
                onClick($C.action.Reload);
            }
        });
        $("#id_bt_del").linkbutton({
            onClick: function () {
                onClick($C.action.Del);
            }
        });
        $("#id_bt_print").linkbutton({
            onClick: function () {
                onClick($C.action.Print);
            }
        });
        $("#id_bt_expt").linkbutton({
            onClick: function () {
                onClick($C.action.Export);
            }
        });
        $("#id_bt_exit").linkbutton({
            onClick: function () {
                onClick($C.action.Exit);
            }
        });
        $("#id_bt_att").linkbutton({
            onClick: function () {
                onClick($C.action.Upload);
            }
        });
        $("#id_bt_delatt").linkbutton({
            onClick: function () {
                onClick($C.action.DelAtt);
                //alert("asd");
                //deleteattfile()
            }
        });
        $("#id_bt_downloadatt").linkbutton({
            onClick: function () {
                onClick($C.action.Download);
            }
        });
    }
};

function rightClickDownload() {
    var row = $("#dg_att_id").datagrid('getSelected');
    if (row) {
        var furl = $C.cos.downattfile() + "?pfid=" + row.pfid;
        window.open(furl);
    } else {
        $.messager.alert('错误', '没有选择附件!', 'error');
    }
}

function deleteattfile() {
    var row = $("#dg_att_id").datagrid('getSelected');
    if (row) {
        var furl = $C.cos.delattfile() + "?pfid=" + row.pfid;
        // window.open(furl);
        $ajaxjsonget(furl, function () {
            $C.grid.remove("#dg_att_id");
            // alert("删除成功");
        }, function () {
            $.messager.alert('错误', '删除附件失败!', 'error');
        });
    } else {
        $.messager.alert('错误', '没有选择附件!', 'error');
    }
}


function TMainLine(afrmOptions) {
    if (!afrmOptions) {
        $.messager.alert('错误', '没有设置frmOptions变量!', 'error');
        return undefined;
    }
    var datachanged = false;
    var frmOptions = afrmOptions;
    var gdLinesName = afrmOptions.gdLinesName;
    var copyFind_pw = undefined;//复制单据的查询窗体
    var curMainData = undefined;//数据对象
    var isloadingdata = false;//正在载入数据
    var isnew = false;//是否新建
    var els_readonly = []; //初始化时 保存界面上所有只读 输入框
    var els_all = [];//所有输入框对象
    var lables = [];//所有Label对象
    var added_grid_ids = [];//用户添加的Grids
    var isCanEdit = (edittps.isedit || edittps.isupdate);
    var lineToolBar = [
        {
            text: '添加行',
            iconCls: 'icon-add1',
            handler: function () {
                if (frmOptions.onAddLine)
                    frmOptions.onAddLine(append)
                else
                    $C.grid.append("detail_main_grid_id", {}, true)
                function append(rowdata) {
                    $C.grid.append("detail_main_grid_id", rowdata, true);
                }
            }
        },
        "-",
        {
            text: '删除行',
            iconCls: 'icon-remove',
            handler: function () {
                var rowdata = $("#detail_main_grid_id").datagrid("getSelected");
                if (!rowdata) {
                    $.messager.alert('错误', '没有选择的数据！', 'error');
                    return;
                }
                if (frmOptions.onRemoveLine) {
                    if (frmOptions.onRemoveLine(rowdata)) {
                        $C.grid.remove("detail_main_grid_id");
                    }
                } else {
                    $C.grid.remove("detail_main_grid_id");
                }
            }
        }
    ];


    setTimeout(function () {
        initUI();
    }, 300);
//初始化界面
    function initUI() {
        initFram();
        initButtons();
        initInput();
        initGrids();
        if (edittps.isedit)
            donew();
        onReady();
        if (frmOptions.initid)
            findDetail(frmOptions.initid);
        if (wfform) {
            $("#main_tabs_id").tabs("close", "列表");
        }
        var t1 = new Date().getTime();
        var t = (t1 - timestamp) / 1000;

        function initFram() {
            $("#main_tabs_id").tabs("select", 1);
            $("#main_tabs_id").tabs("select", 0);
            $("#dg_datalist_id").datagrid({
                border: false,
                onDblClickRow: onListDbClick,
                columns: [listGridColumns]
            });
            if (formtype == TFrmType.ftMainLine) {
                $("#detail_main_grid_id").datagrid({
                    singleSelect: true, rownumbers: true, fit: true,
                    onClickRow: function (index, row) {
                        if (isCanEdit)
                            $C.grid.accept("detail_main_grid_id");
                    },
                    onDblClickRow: function (index, row) {
                        if (isCanEdit)
                            $C.grid.edit("detail_main_grid_id");
                    },
                    border: false,
                    columns: [gdLinesColumns]
                });
                getInputReadOnlys();
            }

            TButtons.setClick(OnBtClick);//设置按钮事件
        }

        function initButtons() {
            setBtsDisable();
            TButtons.btFind.enable();
            TButtons.btExpt.enable();
            TButtons.btExit.enable();
            TButtons.btReload.enable();
            TButtons.btPrint.enable();
            if (wfform) {
                TButtons.btFind.disable();
            }
            if (edittps.isedit) {
                TButtons.btNew.enable();
                TButtons.btDel.enable();
                TButtons.btCopy.enable();
                TButtons.btSubmit.enable();
                if (allowWF) {
                    TButtons.btSubmit.enable();
                }
            }
            if (edittps.issubmit) {
                TButtons.btSave.enable();
                if (allowAtt) {
                    TButtons.btDownloadAtt.enable();
                }
            }
            if (edittps.isview) {
                if (allowAtt) {
                    TButtons.btDownloadAtt.enable();
                }
            }
            if (frmOptions.onSetButtonts) {
                frmOptions.onSetButtonts();
            }
        }

        function initInput() {
            for (var i = 0; i < els_all.length; i++) {
                var co = $.c_parseOptions($(els_all[i]));
                var obj = $(els_all[i]);
                var iscombox = false;
                if (co.comidx != undefined) {
                    iscombox = true;
                    obj.combobox({onChange: onValueChange});
                    var conurl = eval("comUrl_" + co.comidx);
                    var jsondata = conurl.jsondata;
                    var type = conurl.type;
                    var valueField = conurl.valueField;
                    var textField = conurl.textField;
                    if (jsondata != undefined) {
                        if (frmOptions.onInitInputComboboxDict) {
                            frmOptions.onInitInputComboboxDict(co, jsondata);
                        }
                        if (type == "combobox") {
                            obj.combobox({
                                data: jsondata,
                                valueField: valueField,
                                textField: textField
                            });
                        }
                        if (type == "combotree") {
                            obj.tree({
                                data: jsondata
                            });
                        }
                    }
                } else {
                    obj.textbox({onChange: onValueChange});
                }
                if (iscombox) {
                    var input = obj.combobox("textbox");
                    //alert(input.parent().html());
                    //alert(input.attr("class"));
                    input.keydown(function () {
                        alert("fdsa");
                    });
                } else {
                    var input = obj.textbox("textbox");
                    input.bind('keydown', function (e) {
                        console.error("textkeydown:" + keyCode);
                    });
                }
                if (co.required) {
                    var tv = getInputLabel(lables, co.fdname);//$(this).parent().prev();
                    if (tv)
                        tv.html(tv.html() + "(<span style='color: red'>*</span>)");
                }
                if (!co.readonly && ((edittps.isedit) || (edittps.isupdate))) {
                    setInputReadOnly($(obj), false);
                } else {
                    setInputReadOnly($(obj), true);
                }
            }
        }

        function initGrids() {
            $("#dg_att_id").datagrid({
                border: false,
                columns: [
                    [
                        {field: 'displayfname', title: '附件名', width: 200},
                        {field: 'extname', title: '扩展名', width: 100},
                        {field: 'filesize', title: '大小', width: 100},
                        {field: 'filevision', title: '版本', width: 100},
                        {field: 'filecreator', title: '所有者', width: 100},
                        {field: 'filecreate_time', title: '创建时间', width: 150}
                    ]
                ],
                onDblClickRow: onAttGrigDBClick,
                onRowContextMenu: onRowRightClick
            });
            if (!listGridColumns) {
                $.messager.alert('错误', '没有设置列表页字段!', 'error');
            }

            inipageins("#dg_datalist_id");

            if (frmOptions.afterEditGrid && ((formtype == TFrmType.ftMainLine))) {
                $("#dg_att_id").datagrid({
                    onAfterEdit: function (index, row, changes) {
                        frmOptions.afterEditGrid("#dg_att_id", index, row, changes);
                    }
                });
                $("#dg_datalist_id").datagrid({
                    onAfterEdit: function (index, row, changes) {
                        frmOptions.afterEditGrid("#dg_datalist_id", index, row, changes);
                    }
                });
                if (gdLinesColumns) {
                    $("#detail_main_grid_id").datagrid({
                        onAfterEdit: function (index, row, changes) {
                            frmOptions.afterEditGrid("#detail_main_grid_id", index, row, changes);
                        }
                    });
                }
            }

        }
    }


    //根据编辑类型、单据状态刷新界面
    function reflashUI(stat) {
        isCanEdit = getCanEdit(stat);
        if (formtype == TFrmType.ftMainLine) {
            if (isCanEdit) {
                $("#detail_main_grid_id").datagrid({toolbar: lineToolBar});
            } else {
                $("#detail_main_grid_id").datagrid({toolbar: undefined});
            }
        }
        reSetBts();//设置按钮enable
        reSetInput();//设置输入框
        reSetGrids();//设置Grid
        function reSetBts() {
            setBtsDisable();
            TButtons.btFind.enable();
            TButtons.btExpt.enable();
            TButtons.btExit.enable();
            TButtons.btReload.enable();
            TButtons.btPrint.enable();
            if (wfform) {
                TButtons.btFind.disable();
            }

            if (edittps.isedit) {
                TButtons.btNew.enable();
                TButtons.btSubmit.enable();
            }
            if (isCanEdit) {
                TButtons.btSave.enable();
                TButtons.btDel.enable();
                TButtons.btCopy.enable();
                TButtons.btSubmit.enable();
                if (allowAtt) {
                    TButtons.btAtt.enable();
                    TButtons.btDelAtt.enable();
                    TButtons.btDownloadAtt.enable();
                }
            }
            if (allowAtt) {
                TButtons.btDownloadAtt.enable();
            }
        }

        function reSetInput() {
            for (var i = 0; i < els_all.length; i++) {
                var co = $.c_parseOptions($(els_all[i]));
                var obj = $(els_all[i]);
                if (co.readonly || !isCanEdit) {
                    setInputReadOnly($(obj), true);
                } else {
                    setInputReadOnly($(obj), false);
                }
            }
        }

        function reSetGrids() {
        };

    }

    this.appendLineGrid = appendLineGrid;
    function appendLineGrid(Options) {
        //title
        //gridID
        // columns;
        if (formtype == TFrmType.ftSimple) return undefined;
        var id = new Date().format("hhmmss") + "_id";
        var gdid = undefined;
        if (Options.gridID)
            gdid = Options.gridID;
        if ((!gdid) || (gdid.length == 0))
            gdid = "gd_" + id;
        $("#detail_main_tabs_id").tabs("add", {
            id: "tab_" + id,
            title: Options.title,
            content: "<table id='" + gdid + "'></table>"
        });
        $("#" + gdid).datagrid({
            border: false,
            columns: [Options.columns]
        });

        if (frmOptions.afterEditGrid) {
            $("#" + gdid).datagrid({
                onAfterEdit: function (index, row, changes) {
                    frmOptions.afterEditGrid("#" + gdid, index, row, changes);
                }
            });
        }
        added_grid_ids.push(gdid);
        return {tabid: "tab_" + id, gdid: gdid};
    }

    function setBtsDisable() {
        for (var i in TButtons) {
            if (TButtons[i].disable)
                TButtons[i].disable();
        }
    }

    function OnBtClick(act) {
        switch (act) {
            case $C.action.Find:
                find();
                break;
            case $C.action.New:
                donew();
                break;
            case $C.action.Copy:
                docopy();
                break;
            case $C.action.Save:
                dosave();
                break;
            case $C.action.Del:
                dodel();
                break;
            case $C.action.Submit:
                dosubmit();
                break;
            case $C.action.Print:
                doprint();
                break;
            case $C.action.Exit:
                if (frmOptions.onExit)
                    frmOptions.onExit();
                break;
            case $C.action.Upload:
                doupload();
                break;
            case $C.action.Download:
                rightClickDownload();
                break;
            case $C.action.DelAtt:
                deleteattfile();
                break;
        }
    }

    function clearViewData() {
        if (formtype == TFrmType.ftMainLine) {
            var grids = ( frmOptions.gdLinesName) ? added_grid_ids.concat(["detail_main_grid_id"]) : added_grid_ids;
            JSONBandingFrm.clearMainData(els_all, grids);
        } else
            JSONBandingFrm.clearMainData(els_all, []);
    }

////  new
    function donew() {
        if (datachanged) {
            $.messager.confirm('提示', '忽略数据修改?', function (r) {
                if (r) {
                    do_new();
                }
            });
        } else
            do_new();
        return true;
    }

    function do_new() {
        reSetInputReadOnly();
        isnew = true;
        clearViewData();
        curMainData = {};
        $("#main_tabs_id").c_setJsonDefaultValue(curMainData, true);
        if (frmOptions.onNew)
            frmOptions.onNew(curMainData);
        showDetail();
    }

////copy
    function docopy() {
        $.messager.confirm('提示', '确定复制单据?', function (r) {
            if (r) {
                if (!copyFind_pw) {
                    var copyFind_pw_options = {
                        id: "copyFind_pw",
                        enableIdpath: true,
                        JPAClass: frmOptions.JPAClass,  //对应后台JPAClass名
                        gdListColumns: listGridColumns,
                        chgidpath: true,
                        selfLine: false,
                        edittype: {isfind: true},
                        beforeFind: function (fdparms) {
                            fdparms.chgidpath = true;
                            return true;
                        },
                        onResult: function (rows) {
                            if (rows.length > 0) {
                                var jpa = rows[0];
                                //jpaclass  id clearfields
                                var parms = {};
                                parms.jpaclass = frmOptions.JPAClass;
                                parms.id = jpa[frmOptions.JPAIdField];
                                if (frmOptions.onCopy) {
                                    frmOptions.onCopy(parms);
                                }
                                $ajaxjsonpost($C.cos.commonCopy, JSON.stringify(parms), function (jsdata) {
                                    $("#dg_datalist_id").datagrid("appendRow", jsdata);
                                    var id = jsdata[frmOptions.JPAIdField];
                                    if ((!id) || (id.length == 0)) {
                                        alert("列表数据没发现ID值");
                                        return;
                                    }
                                    findDetail(id);
                                }, function (msg) {
                                    $.messager.alert('错误', '复制错误:' + msg, 'error');
                                });
                            }
                        }
                    };
                    copyFind_pw = new TSearchForm(copyFind_pw_options);
                }
                copyFind_pw.show();
            }
        });
    }

//save
    function dosave() {
        if (!datachanged) {
            return;
        }
        curMainData = getMainData();
        if (!JSONBandingFrm.checkNotNull(els_all, lables, curMainData)) return false;

        //////save
        var surl = ((frmOptions.saveUrl) && (frmOptions.saveUrl.length > 0)) ? frmOptions.saveUrl : $C.cos.commonsave;
        if ((frmOptions.saveUrl) && (frmOptions.saveUrl.length > 0)) {
            var postData = curMainData;
        } else
            var postData = {jpaclass: frmOptions.JPAClass, lines: true, jpadata: curMainData};
        if (frmOptions.onSave) {
            if (!frmOptions.onSave(postData)) return false;
        }
        $ajaxjsonpost(surl, JSON.stringify(postData), function (jdata) {
            curMainData = jdata;
            if (isnew) {
                $("#dg_datalist_id").datagrid("appendRow", jdata);
            }
            showDetail();
        }, function (msg) {
            $.messager.alert('错误', '保存数据错误:' + msg, 'error');
        });
    }

    function dodel() {
        curMainData = getMainData();
        var id = (curMainData[frmOptions.JPAIdField]);
        if ((!id) || (id.length == 0)) {
            $.messager.alert('错误', '未保存的或空数据，不需要删除！', 'error');
            return;
        }
        $.messager.confirm('确认', '确认删除当前记录?', function (r) {
            if (r) {
                var surl = ((frmOptions.delUrl) && (frmOptions.delUrl.length > 0)) ? frmOptions.delUrl : $C.cos.commondelete;
                surl = surl + "?jpaclass=" + frmOptions.JPAClass + "&id=" + id;
                $ajaxjsonget(surl, function (jdata) {
                    if (jdata.result == "OK") {
                        $C.grid.removeByField("#dg_datalist_id", id, frmOptions.JPAIdField);
                        donew();//成功删除后，清除界面，恢复新建状态
                    } else {
                        $.messager.alert('错误', '删除失败:' + jdata.result, 'error');
                    }

                }, function (msg) {
                    $.messager.alert('错误', '删除失败:' + msg, 'error');
                });
            }
        });
    }

    var pw_SearchWT = undefined;

    function dosubmit() {
        curMainData = getMainData();
        var jpaid = (curMainData[frmOptions.JPAIdField]);
        if ((!jpaid) || (jpaid.length == 0)) {
            $.messager.alert('错误', '未保存的或空数据，不能提交！', 'error');
            return;
        }
        if (datachanged) {
            $.messager.alert('错误', '请先保存单据！', 'error');
            return;
        }
        $.messager.confirm('确认', '确认提交当前单据?', function (r) {
            if (r) {
                var pwOption = {
                    id: "pws_selectWFTem",
                    JPAClass: "com.corsair.server.wordflow.Shwwftemp",  //对应后台JPAClass名
                    gdListColumns: [
                        {field: 'wftempname', title: '流程名称', width: 150}
                    ],
                    singleSelect: true,
                    autoFind: true, singleAutoReturn: true, notFindAutoReturn: true,
                    edittype: {isfind: true},
                    enableIdpath: false,
                    beforeFind: function (parms) {
                        parms.url = _serUrl + "/web/common/findWfTemps.co";
                        parms.jpaclass = frmOptions.JPAClass;
                        parms.id = jpaid;
                        return true;
                    },
                    onResult: function (rows) {
                        var wftempid = (rows.length == 0) ? undefined : rows[0].wftempid;
                        var parms = {jpaclass: frmOptions.JPAClass, jpaid: jpaid};
                        if (wftempid)
                            parms.wftempid = wftempid;
                        $ajaxjsonpost($C.cos.commoncreateWF, JSON.stringify(parms), function (jsdata) {
                            curMainData = jsdata;
                            showDetail();
                        }, function (msg) {
                            $.messager.alert('错误', '提交单据错误:' + msg, 'error');
                        });
                    }
                };
                if (pw_SearchWT) {
                    pw_SearchWT.TSearchForm(pwOption);
                } else {
                    pw_SearchWT = new TSearchForm(pwOption);
                }
                pw_SearchWT.show();
            }
        });
    }

////find begin
    this.find_window_ok_click = function () {
        $C.grid.accept("dg_find_window_parms_id");
        var parms = $("#dg_find_window_parms_id").datagrid("getData").rows;
        do_find(parms);
    }

    function find() {
        var peditor = {
            type: 'combobox',
            options: {
                valueField: 'field', textField: 'title', data: listGridColumns
            }
        };
        var pft = function (value, row) {
            var dt = listGridColumns;
            for (var i = 0; i < dt.length; i++) {
                var r = dt[i];
                if (value == r.field) {
                    return r.title;
                }
            }
            return value;
        };
        var redtor = {
            type: 'combobox',
            options: {
                valueField: 'id', textField: 'value',
                data: [
                    {id: 'like', value: 'like'},
                    {id: '=', value: '='},
                    {id: '>', value: '>'},
                    {id: '<', value: '<'}
                ]
            }
        };

        $("#dg_find_window_parms_id").datagrid({
            columns: [
                [
                    {field: 'parmname', title: '参数名', width: 100, editor: peditor, formatter: pft},
                    {field: 'reloper', title: '运算关系', width: 60, editor: redtor},
                    {field: 'parmvalue', title: '参数值', width: 100, editor: 'text'}
                ]
            ]
        });
        $C.grid.clearData('dg_find_window_parms_id');
        $C.grid.append('dg_find_window_parms_id', {}, true);
        $("#find_window_id").window("open");
    }

    function inipageins(gdfilter) {
        var gd = $(gdfilter);
        gd.datagrid({
            // pageNumber: 1,
            pagination: true,
            rownumbers: true,
            pageSize: 30,
            pageList: [30, 50, 100, 300]
        });

        var p = gd.datagrid('getPager');
        p.pagination({
            beforePageText: '第',//页数文本框前显示的汉字
            afterPageText: '页    共 {pages} 页',
            displayMsg: '共{total}条数据'
        });
    }

    function do_find(parms) {
        for (var i = parms.length - 1; i >= 0; i--) {
            if ((!parms[i].parmname) || (parms[i].parmname.length == 0)) {
                parms.splice(i, 1);
            }
        }
        var allparms = {
            type: "list",
            chgidpath: true,
            edittps: edittps,
            jpaclass: frmOptions.JPAClass,
            parms: parms
        };
        if (afrmOptions.onFind)
            afrmOptions.onFind(allparms);
        var url = $C.cos.commonfind + "?type=" + allparms.type + "&chgidpath=" + allparms.chgidpath + "&edittps=" + JSON.stringify(allparms.edittps) + "&jpaclass=" + allparms.jpaclass;
        if (parms.length > 0)
            url = url + "&parms=" + JSON.stringify(allparms.parms);
        url = encodeURI(url);
        var gd = $("#dg_datalist_id");
        var p = gd.datagrid("getPager");
        setOnListselectPage(url);
        var option = p.pagination("options");
        do_getdata(url, option.pageNumber, option.pageSize);
    }

    function setOnListselectPage(url) {
        var gd = $("#dg_datalist_id");
        var p = gd.datagrid("getPager");
        p.pagination({
            onSelectPage: function (pageNumber, pageSize) {
                do_getdata(url, pageNumber, pageSize);
            }
        });
    }

    function do_getdata(url, page, pageSize) {
        $ajaxjsonget(url + "&page=" + page + "&rows=" + pageSize, function (jsdata) {
            $C.grid.clearData("dg_datalist_id");
            $("#dg_datalist_id").datagrid({pageNumber: page, pageSize: pageSize});
            setOnListselectPage(url);
            $("#dg_datalist_id").datagrid("loadData", jsdata);
            $("#main_tabs_id").tabs("select", "列表");
            $("#find_window_id").window("close");
        }, function () {
            //console.error("查询错误");
        });
    }


    function onListDbClick(index, row) {
        if (!frmOptions.JPAIdField) {
            alert("没有设置JPA ID字段名");
            return;
        }
        var id = row[frmOptions.JPAIdField];
        if ((!id) || (id.length == 0)) {
            alert("列表数据没发现ID值");
            return;
        }
        findDetail(id);
    }

    function onRowRightClick(e, rowIndex, rowData) {
        e.preventDefault();
        $('#ContextMenu').menu('show', {
            left: e.pageX,
            top: e.pageY
        });
    }

    this.findDetail = findDetail;
    function findDetail(id) {
        if (datachanged) {
            $.messager.confirm('提示', '忽略数据修改?', function (r) {
                if (r) {
                    find_detail(id);
                }
            });
        } else
            find_detail(id);
        return true;
    }

    function find_detail(id) {
        curMainData = {};
        var url = $C.cos.commonfind + "?type=byid&chgidpath=true&id=" + id + "&jpaclass=" + frmOptions.JPAClass;
        $ajaxjsonget(url, function (jsdata) {
            isnew = false;
            curMainData = jsdata;
            showDetail();
        }, function () {
            alert("查询数据错误");
        });
    }

    this.showDetail = showDetail;
    function showDetail() {
        isloadingdata = true;
        reflashUI(curMainData.stat);

        //先改界面显示 再写入值，否则会消失，应该是bug
        if ((!$isEmpty(curMainData.stat)) && (parseInt(curMainData.stat) > 1) && (!edittps.isupdate)) {
            //有流程 不是更新界面
            showWFinfo();//如果有流程  根据流程刷新可以编辑的输入框
        } else {
            $("#main_tab_wf_id").html("");
            $("#main_tabs_id").tabs("select", "常规");
        }
        setJson2Inputs();
        setJson2Grids();
        isloadingdata = false;
        datachanged = false;
    }

    function showWFinfo() {
        if (!$isEmpty(curMainData.wfid)) {
            $("#main_tabs_id").tabs("select", "流程");
            var src = _serUrl + "/webapp/common/shwwf.html?wfid=" + curMainData.wfid + "&showform=false";
            var wfifrm = "<iframe scrolling='no' frameborder='0' src='" + src + "' style='width: 100%;height: 98%'></iframe>";
            $("#main_tab_wf_id").html(wfifrm);
            //$("#main_tabs_id").tabs("select", "常规");
        } else {
            $("#main_tabs_id").tabs("select", "常规");
            $("#main_tab_wf_id").html("");
        }
    }

    this.setJson2Inputs = setJson2Inputs;
    function setJson2Inputs() {
        return JSONBandingFrm.fromJsonData(els_all, curMainData)
    }

    this.setJson2Grids = setJson2Grids;
    function setJson2Grids() {
        if (formtype == TFrmType.ftMainLine) {
            if (curMainData[gdLinesName]) {
                $("#detail_main_grid_id").datagrid("loadData", curMainData[gdLinesName]);
            }
            else $C.grid.clearData("detail_main_grid_id");

            for (var i = 0; i < added_grid_ids.length; i++) {
                var grid_id = added_grid_ids[i];
                try {
                    if (curMainData[grid_id])
                        $("#" + grid_id).datagrid("loadData", curMainData[grid_id]);
                    else
                        $C.grid.clearData(grid_id);
                }
                catch (e) {

                }
            }
        }
        if (allowAtt) {
            if ((curMainData.shw_attachs) && (curMainData.shw_attachs.length > 0) && (curMainData.shw_attachs[0].shw_attach_lines)) {
                $("#dg_att_id").datagrid("loadData", curMainData.shw_attachs[0].shw_attach_lines);
            } else {
                $C.grid.clearData("dg_att_id");
            }
        }
    }


    function doprint() {
        curMainData = getMainData();
        var jpaid = (curMainData[frmOptions.JPAIdField]);
        if ((!jpaid) || (jpaid.length == 0)) {
            $.messager.alert('错误', '未保存的或空数据，不能提交！', 'error');
            return;
        }
        var url = _serUrl + "/web/common/findModels.co?jpaclass=" + frmOptions.JPAClass;
        $("#pw_list_select").c_popselectList(url, function (fi) {
            if (fi) {
                var url = $C.cos.commondowloadExcelByModel + "?jpaclass=" + frmOptions.JPAClass + "&modfilename=" + fi.fname
                    + "&jpaid=" + getid();
                window.open(url);
            }
        });

    }

//find end

///upload att
    function doupload() {
        curMainData = getMainData();
        var id = (curMainData[frmOptions.JPAIdField]);
        if ((!id) || (id.length == 0)) {
            $.messager.alert('错误', '未保存的或空数据，不允许上传附件！', 'error');
            return;
        }
        if (curMainData.shw_attachs == undefined) {
            $.messager.alert('错误', 'JPA没有设置附件属性，不允许上传附件！', 'error');
            return;
        }

        var wd = $("#pw_uploadfile iframe");
        wd.attr("src", _serUrl + "/webapp/templet/default/uploadfile.html?action=" + $C.cos.commonUpLoadFile);
        window.setTimeout(function () {
            wd[0].contentWindow._callback = function (rst) {
                var Shw_attach = ((curMainData.shw_attachs) && (curMainData.shw_attachs.length > 0)) ? curMainData.shw_attachs[0] : {};
                if (!Shw_attach.shw_attach_lines) {
                    Shw_attach.shw_attach_lines = [];
                }
                try {
                    var pfs = eval(rst);
                    for (var i = 0; i < pfs.length; i++) {
                        pf = pfs[i];
                        Shw_attach.shw_attach_lines.push({
                            pfid: pf.pfid,
                            fdrid: 0,
                            displayfname: pf.displayfname,
                            extname: pf.extname,
                            filesize: pf.filesize
                        });
                    }
                    curMainData.shw_attachs = [Shw_attach];
                    dosave();//上传附件后 需要保存对象
                    $("#pw_uploadfile").window("close");
                } catch (e) {
                    if (rst.indexOf("Request Entity Too Large") > -1) {
                        $.messager.alert('上传文件错误', "文件超大", 'error');
                    } else {
                        $.messager.alert('上传文件错误', rst, 'error');
                    }
                }
            };
        }, 500);
        $("#pw_uploadfile").window("open");
    }

    function onAttGrigDBClick(index, row) {
        var furl = $C.cos.downattfile() + "?pfid=" + row.pfid;
        window.open(furl);
    }


//public proterty
    this.isnew = function () {
        return isnew;
    }
    this.isloadingdata = function () {
        return isloadingdata;
    }

    this.getMainData = getMainData;
    function getMainData() {
        curMainData = JSONBandingFrm.toJsonData(els_all, curMainData, isnew);
        if ((formtype == TFrmType.ftMainLine) && gdLinesName) {
            var lines = $("#detail_main_grid_id").datagrid("getRows");
            curMainData[gdLinesName] = lines;
            for (var i = 0; i < added_grid_ids.length; i++) {
                try {
                    var grid_id = added_grid_ids[i];
                    var lines = $("#" + grid_id).datagrid("getRows");
                    curMainData[grid_id] = lines;
                }
                catch (e) {

                }
            }
        }
        return curMainData;
    }

    this.lineToolBar = function () {
        return lineToolBar;
    };

    this.getid = getid;
    function getid() {
        return curMainData[frmOptions.JPAIdField];
    }

    this.getEditType = function () {
        return edittps;
    }


//other functions


    function readOptions(aFrmOptions) {
        frmOptions.lineHeight = ( aFrmOptions.lineHeight) ? aFrmOptions.lineHeight : 200;
        gdLinesName = frmOptions.gdLinesName;
        lineHeight = frmOptions.lineHeight;
    }

    this.setReadOnly = function (fdname, readonly) {
        var v = undefined;
        var ipt = undefined;
        for (var i = 0; i < els_all.length; i++) {
            var co = $.c_parseOptions($(els_all[i]));
            var et = getInputType(els_all[i]);
            if (co.fdname == fdname) {
                ipt = $(els_all[i]);
                v = $(els_all[i]).textbox("getValue");
                break;
            }
        }
        setInputReadOnly(ipt, readonly);
        var iput = findElinput(els_all, fdname);
        if (iput) {
            setInputValue(iput, v);
        }
    }

    function setInputReadOnly(input, readonly) {
        //alert(input.next().find("input:first").parent().html());
        var tp = getInputType(input[0]);
        if (tp == 1)
            input.datetimebox({readonly: readonly});
        if (tp == 2)
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

    function setAllInputReadOnly(readonly) {
        for (var i = 0; i < els_all.length; i++) {
            setInputReadOnly($(els_all[i]), readonly);
        }
    }

    function getInputReadOnlys() {
        els_readonly = [];
        els_all = [];
        $("#maindata_id").find("input[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            if (co.readonly) {
                els_readonly.push(this);
            }
            els_all.push(this);
        });
        lables = [];
        $("#maindata_id").find("td[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            lables.push(this);
        });
    }

    this.reSetInputReadOnly = reSetInputReadOnly;
    function reSetInputReadOnly() {
        for (var i = 0; i < els_all.length; i++) {
            var co = $.c_parseOptions($(els_all[i]));
            if ((!co.readonly) || edittps.isedit) {
                setInputReadOnly($(els_all[i]), false);
            } else
                setInputReadOnly($(els_all[i]), true);
        }
    }

    this.getFieldValue = function (fdname) {
        var v = $getInputValue(els_all, fdname);
        if (v)
            return v;
        else
            return curMainData[fdname];
    }

    this.setFieldValue = function (fdname, value) {
        curMainData[fdname] = value;
        var iput = findElinput(els_all, fdname);
        if (iput) {
            setInputValue(iput, value);
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

    function setInputValue(iput, value) {
        var co = $.c_parseOptions($(iput));
        var et = getInputType(iput);
        var v = value;
        switch (et) {
            case 1:
            {
                if ((v == undefined) || (v == null) || (v == "NULL") || (v == "")) {
                    $(els_all[i]).datetimebox('setValue', "");
                } else {
                    var dt = (new Date()).fromStr(v);
                    $(iput).datetimebox('setValue', dt.toUIDatestr());
                }
                break;
            }
            default:
            {
                if ((v == undefined) || (v == null) || (v == "NULL"))
                    $(iput).textbox('setValue', "")
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

    this.getDataChanged = function () {
        return datachanged;
    }
    this.setDataChanged = function (value) {
        datachanged = value;
        if (datachanged)
            TButtons.btSave.enable();
    }
    function onReady() {
        if (frmOptions.OnReady) {
            frmOptions.OnReady();
        }
    }

    this.getCanEdit = function () {
        return isCanEdit;
    }

    function getCanEdit(stat) {
        if (stat == 1) {
            if (edittps.isedit)
                return true;
        } else if (stat > 1 && stat < 9) {
            return false;
        } else if (stat == 9) {
            if (edittps.isupdate)
                return true;
        } else
            return false;
        return false;
    }

    this.keydown = function (e) {
        console.log(e.keyCode);
        var key = e.keyCode;
        var et = parseInt(formetype);
        if (e.ctrlKey && (key == 78)) {//ctr+n
            if (et == 1)
                donew();
            return false;
        }
        if (e.ctrlKey && (key == 83)) {//ctr+s
            if ([1, 2, 4].contains(et))
                dosave();
            return false;
        }
        if (e.ctrlKey && (key == 67)) {//ctr+c
            if (et == 1)
                docopy();
            return false;
        }
        if (e.ctrlKey && (key == 70)) {//ctr+f
            find();
            return false;
        }
        if (e.ctrlKey && (key == 68)) {//ctr+d
            if (et == 1)
                dodel();
            return false;
        }
    }

}


var JSONBandingFrm = new function () {

    this.clearMainData = function (els_all, grids) {
        this.clearFrmData(els_all);
        for (var i = 0; i < grids.length; i++) {
            $C.grid.clearData(grids[i]);
        }
    }

    this.clearFrmData = function (els_all) {
        for (var i = 0; i < els_all.length; i++) {
            $(els_all[i]).textbox('setValue', "");
        }
    }
    this.fromJsonData = function (els_all, jsondata) {
        for (var i = 0; i < els_all.length; i++) {
            var co = $.c_parseOptions($(els_all[i]));
            var et = getInputType(els_all[i]);
            var v = jsondata[co.fdname];
            switch (et) {
                case 1:
                {
                    if ((v == undefined) || (v == null) || (v == "NULL") || (v == "")) {
                        $(els_all[i]).datetimebox('setValue', "");
                    } else {
                        var dt = (new Date()).fromStr(v);
                        $(els_all[i]).datetimebox('setValue', dt.toUIDatestr());
                    }
                    break;
                }
                default:
                {
                    if ((v == undefined) || (v == null) || (v == "NULL"))
                        $(els_all[i]).textbox('setValue', "")
                    else {
                        var iscom = false;
                        try {
                            var opt = $(this).textbox("options");
                            iscom = (("valueField" in opt) && ("textField" in opt) && ("data" in opt));
                        }
                        catch (e) {
                            //alert(e.message);
                        }
                        if ((co.dicgpid != undefined) || (co.comidx != undefined) || iscom) {
                            $(els_all[i]).combobox('select', v);
                        } else
                            $(els_all[i]).textbox('setValue', v);
                    }
                    break;
                }
            }
        }
        return true;
    }

    this.toJsonData = function (els_all, jsondata, isnew) {
        for (var i = 0; i < els_all.length; i++) {
            jsondata[$.c_parseOptions($(els_all[i])).fdname] = $(els_all[i]).textbox("getValue");
        }
        if (!isnew)
            c_setJsonDefaultValue(els_all, jsondata, false);
        return jsondata;
    }

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

    this.checkNotNull = function (els_all, lables, jsondata) {
        var msg = "";
        for (var i = 0; i < els_all.length; i++) {
            var co = $.c_parseOptions($(els_all[i]));
            var et = getInputType(els_all[i]);
            var v = jsondata[co.fdname];
            if ((co.required) && ((v == null) || (!v) || (v == ""))) {
                msg = msg + "<" + getInputLabel(lables, co.fdname).html() + "><br/>";
            }
        }
        if (msg == "") return true
        else {
            $.messager.alert('错误', msg + '不允许为空！', 'error');
            return false;
        }
    }
}

function $setInputValue(els_all, fdname, value) {
    for (var i = 0; i < els_all.length; i++) {
        var co = $.c_parseOptions($(els_all[i]));
        if (co.fdname == fdname) {
            var et = getInputType(els_all[i]);
            var v = value;
            switch (et) {
                case 1:
                {
                    if ((v == undefined) || (v == null) || (v == "NULL") || (v == "")) {
                        $(els_all[i]).datetimebox('setValue', "");
                    } else {
                        var dt = (new Date()).fromStr(v);
                        $(els_all[i]).datetimebox('setValue', dt.toUIDatestr());
                    }
                    break;
                }
                default:
                {
                    if ((v == undefined) || (v == null) || (v == "NULL"))
                        $(els_all[i]).textbox('setValue', "")
                    else {
                        if (co.dicgpid) {
                            $(els_all[i]).combobox('select', v);
                        } else
                            $(els_all[i]).textbox('setValue', v);
                    }
                    break;
                }
            }
        }
    }
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

function getInputLabel(lables, fdname) {
    for (var i = 0; i < lables.length; i++) {
        var co = $.c_parseOptions($(lables[i]));
        if (co.fdname == fdname) {
            var lb = $(lables[i]);
            return lb;
        }
    }
    return undefined;
}


function getInputType(el) {
    var cls = $(el).attr("class");
    if (cls.indexOf("easyui-datetimebox") >= 0) {
        return 1;
    } else if (cls.indexOf("easyui-combobox") >= 0) {
        return 2;//combobox
    } else if (cls.indexOf("easyui-textbox") >= 0) {
        return 5;
    }
}

function onValueChange(newValue, oldValue) {
    if (mainline) {
        mainline.setDataChanged(true);
    }
}

function TSearchForm(sfrmOptions) {
    var inited = false;
    var id = sfrmOptions.id;
    if ((!id) || (id.length < 0)) {
        $.messager.alert('错误', '没有设置id变量!', 'error');
        return undefined;
    }
    var istree, htmlTempt, idField, treeField, singleSelect, enableIdpath, autoFind, singleAutoReturn, notFindAutoReturn, edittps, showTitle, orderStr;

    function readOptions() {
        istree = (sfrmOptions.isTree == undefined) ? false : sfrmOptions.isTree;
        htmlTempt = (sfrmOptions.htmlTempt) ? sfrmOptions.htmlTempt : "../templet/default/search_grid.html";
        idField = (sfrmOptions.idField) ? sfrmOptions.idField : undefined;
        treeField = (sfrmOptions.treeField) ? sfrmOptions.treeField : undefined;
        singleSelect = (sfrmOptions.singleSelect == undefined) ? true : sfrmOptions.singleSelect;
        enableIdpath = (sfrmOptions.enableIdpath == undefined) ? true : sfrmOptions.enableIdpath;
        autoFind = (sfrmOptions.autoFind == undefined) ? false : sfrmOptions.autoFind;
        singleAutoReturn = (sfrmOptions.singleAutoReturn == undefined) ? false : sfrmOptions.singleAutoReturn;
        notFindAutoReturn = (sfrmOptions.notFindAutoReturn == undefined) ? false : sfrmOptions.notFindAutoReturn;
        edittps = (sfrmOptions.edittype == undefined) ? {isfind: true} : sfrmOptions.edittype;
        showTitle = (sfrmOptions.showTitle == undefined) ? true : sfrmOptions.showTitle;
        orderStr = (sfrmOptions.orderStr == undefined) ? "" : sfrmOptions.orderStr;
    }

    readOptions();
    var valuehtml = undefined;

    this.tag = 0;
    initPopFrm();
    this.TSearchForm = function (NsfrmOptions) {
        sfrmOptions = $.extend(sfrmOptions, NsfrmOptions);
        readOptions();
    }

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
                listgrid.datagrid({
                    border: false,
                    singleSelect: !multiRow,
                    singleSelect: singleSelect,
                    onDblClickRow: onListDbClick,
                    columns: [sfrmOptions.gdListColumns]
                });
                if (istree) {

                }
                var item = getInputByField("item");
                if (!item) {
                    $.messager.alert('错误', '没有找到item录入框!', 'error');
                    return;
                }
                item.combobox({
                    data: sfrmOptions.gdListColumns,
                    valueField: 'field',
                    textField: 'title',
                    onSelect: onItemSelect
                });
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
            $C.grid.clearData(listgrid);
            listgrid.datagrid({singleSelect: !multiRow});
        }
        if (autoFind) {
            OnFindClick();
        }
        $("#" + id).window("open");
    }

    function getInputByField(fdname) {
        var inpt = undefined;
        $("#" + id).find("input[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            var et = getInputType(el);
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
        if (showTitle) {
            var fd = getInputByField("item").textbox("getValue");
            var v = getInputByField("value").textbox("getValue");
            var parms = (fd && v) ? [
                {parmname: fd, reloper: "like", parmvalue: v}
            ] : [];
        } else
            var parms = [];

        if (sfrmOptions.extParms)
            parms = parms.concat(sfrmOptions.extParms);
        var fdparms = {
            url: $C.cos.commonfind,
            type: "list",
            enableIdpath: enableIdpath,
            edittps: edittps,
            jpaclass: sfrmOptions.JPAClass,
            parms: parms
        };
        if (!$isEmpty(orderStr))
            fdparms.order = orderStr;

        var selfLine = (sfrmOptions.selfLine == undefined) ? true : sfrmOptions.selfLine;
        sfrmOptions.selfLine = selfLine;
        if (sfrmOptions.beforeFind) {
            if (!sfrmOptions.beforeFind(fdparms))
                return;
        }
        var url = fdparms.url + "?type=" + fdparms.type + "&enableIdpath=" + fdparms.enableIdpath + "&edittps=" + JSON.stringify(fdparms.edittps)
            + "&jpaclass=" + fdparms.jpaclass + "&parms=" + JSON.stringify(fdparms.parms);
        for (var parm in fdparms) {
            if (typeof(fdparms[parm]) == "function")
                continue;
            if ((parm != "url") && (fdparms[parm] != undefined))
                if (parm == "parms")
                    url = url + "&parms=" + JSON.stringify(fdparms[parm]);
                else
                    url = url + "&" + parm + "=" + fdparms[parm];
        }

        //console.error(url);
        $ajaxjsonget(url, function (jsdata) {
            if (((jsdata.length == 1) && singleAutoReturn) || ((jsdata.length == 0) && notFindAutoReturn)) {
                if (sfrmOptions.onResult)
                    sfrmOptions.onResult(jsdata);
                $("#" + id).window("close");
                return;
            }
            var listgrid = $("#" + id).find("table[cjoptions]");
            $C.grid.clearData(listgrid);
            listgrid.datagrid("loadData", jsdata);
        }, function () {
            //console.error("查询错误");
        });
    }

    function OnOKClick() {
        var listgrid = $("#" + id).find("table[cjoptions]");
        var rows = listgrid.datagrid('getSelections');
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
        var rows = listgrid.datagrid('getSelections');
        if (rows.length == 0) {
            listgrid.datagrid("selectAll");
        } else {
            listgrid.datagrid("unselectAll");
        }
    }

    //alert(2);
}


