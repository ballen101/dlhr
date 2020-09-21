/**
 * Created by Administrator on 2015/4/28.
 */
var mainline = undefined;
var frmOptions = undefined, formetype = undefined, wfform = undefined, edittps = undefined;
var _findcolums = [];
//$.parser.auto = false;
var timestamp = new Date().getTime();
var _NUBType = {
    disable: 1,
    hide: 2
};
var TFrmType = {ftMainLine: 1, ftSimple: 2};

/*
 function closes() {
 $("#loading").fadeOut("normal", function () {
 $(this).remove();
 });
 }*/
//var pc, _uicpd = false;
var HtmlTempType = {
    htML: 1,
    htMLTree: 2,
    htMLPop: 3
};

/*$(document).ready(function () {
 //传入参数   formetype   1制单 2 审批 3 浏览 4 变更
 //id      JPA数据ID 如果有传入，将在界面初始化后载入该JPA数据
 //wfform  作为wfform显示 true 显示 false不显示
 $.parser.onComplete = function () {
 _uicpd = true;
 if (pc) clearTimeout(pc);
 pc = setTimeout(closes, 1000);
 };
 $.parser.parse();
 if (frmOptions && (!mainline)) {
 var parms = $getPageParms();
 edittps = JSON.parse(parms.edittps);
 //alert(parms.edittps);
 frmOptions.initid = parms.id;
 wfform = parms.wfform;
 var menutag = parseInt(parms.menutag);
 if ((wfform) && (wfform.toUpperCase != 'TRUE'))
 allowWF = false;
 frmOptions.menutag = menutag;
 mainline = new TMainLine(frmOptions);
 }
 }
 );*/

//传入参数   formetype   1制单 2 审批 3 浏览 4 变更
//id      JPA数据ID 如果有传入，将在界面初始化后载入该JPA数据
//wfform  作为wfform显示 true 显示 false不显示
function $OnCorsairReady() {
    if (frmOptions && (!mainline)) {
        var parms = $getPageParms();
        edittps = JSON.parse(parms.edittps);
        //alert(parms.edittps);
        frmOptions.initid = parms.id;
        wfform = parms.wfform;
        var menutag = parseInt(parms.menutag);
        if ((wfform) && (wfform.toUpperCase != 'TRUE'))
            allowWF = false;
        frmOptions.menutag = menutag;
        setTimeout(function () {
            mainline = new TMainLine(frmOptions);
        }, 300);
    }
}


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
    var idfield = afrmOptions.JPAIdField;
    var copyFind_pw = undefined;//复制单据的查询窗体
    var curMainData = undefined;//数据对象
    var isloadingdata = false;//正在载入数据
    var issetEditValue = false;//正在调用setvalue设置值
    var isnew = false;//是否新建
    var els_readonly = []; //初始化时 保存界面上所有只读 输入框
    var els_all = [];//所有输入框对象
    var lables = [];//所有Label对象
    var added_grid_ids = [];//用户添加的Grids
    var isCanEdit = (edittps.isedit || edittps.isupdate);
    var pagination = (afrmOptions.Pagination == undefined) ? true : afrmOptions.Pagination;
    var istree = (afrmOptions.istree == undefined) ? false : afrmOptions.istree;
    var treeParentField = (afrmOptions.treeParentField == undefined) ? "" : afrmOptions.treeParentField;
    var treeListWidth = (afrmOptions.treeListWidth == undefined) ? 300 : afrmOptions.treeListWidth;
    var showHeader = (afrmOptions.showtreeHeader == undefined) ? true : afrmOptions.showtreeHeader;
    var allow_new = (afrmOptions.allow_new == undefined) ? true : afrmOptions.allow_new;
    var allow_del = (afrmOptions.allow_del == undefined) ? true : afrmOptions.allow_del;
    var allow_copy = (afrmOptions.allow_copy == undefined) ? true : afrmOptions.allow_copy;
    var allow_submit = (afrmOptions.allow_submit == undefined) ? true : afrmOptions.allow_submit;
    var allow_expt = (afrmOptions.allow_expt == undefined) ? false : afrmOptions.allow_expt;
    var allow_reload = (afrmOptions.allow_reload == undefined) ? false : afrmOptions.allow_reload;
    var allow_print = (afrmOptions.allow_print == undefined) ? true : afrmOptions.allow_print;
    var htmlTemeType = (afrmOptions.htmlTemeType == undefined ) ? HtmlTempType.htML : afrmOptions.htmlTemeType;
    var datainfo_pw_title = (afrmOptions.datainfo_pw_title == undefined ) ? "单据详情" : afrmOptions.datainfo_pw_title;
    var datainfo_line_title = (afrmOptions.datainfo_line_title == undefined ) ? "明细数据" : afrmOptions.datainfo_line_title;
    var datainfo_attr_title = (afrmOptions.datainfo_attr_title == undefined ) ? "附件" : afrmOptions.datainfo_attr_title;
    var datainfo_wf_title = (afrmOptions.datainfo_wf_title == undefined ) ? "流程" : afrmOptions.datainfo_wf_title;
    var allow_exit = (afrmOptions.allow_exit == undefined) ? (htmlTemeType == HtmlTempType.htMLPop) : afrmOptions.allow_exit;
    var bttexts = (afrmOptions.bttexts == undefined) ? {} : afrmOptions.bttexts;
    var attImgThb = (afrmOptions.attImgThb == undefined) ? false : afrmOptions.attImgThb;
    var allowFindPW = (afrmOptions.allowFindPW == undefined) ? true : afrmOptions.allowFindPW;
    var autoFind = (afrmOptions.autoFind == undefined) ? false : afrmOptions.autoFind;
    var windowWidth = (afrmOptions.windowWidth == undefined) ? undefined : afrmOptions.windowWidth;
    var windowHeight = (afrmOptions.windowHeight == undefined) ? undefined : afrmOptions.windowHeight;
    var wfWindowHeight = (afrmOptions.wfWindowHeight) ? afrmOptions.wfWindowHeight : "500px";

    var findUrl = (afrmOptions.findUrl == undefined) ? $C.cos.commonfind : afrmOptions.findUrl;
    var saveUrl = (afrmOptions.saveUrl == undefined) ? $C.cos.commonsave : afrmOptions.saveUrl;
    var gdLinesColumns = (!afrmOptions.gdLinesColumns) ? undefined : afrmOptions.gdLinesColumns;
    var disableButtonType = (!afrmOptions.disableButtonType) ? _NUBType.disable : afrmOptions.disableButtonType;
    var allowEmptyLine = (afrmOptions.allowEmptyLine == undefined) ? true : afrmOptions.allowEmptyLine;
    var treeField = (afrmOptions.treeField) ? afrmOptions.treeField : undefined;

    function setFieldRequere(col) {
        if ((col) && (col.required)) {
            col.title = col.title + "(<span style='color: red'>*</span>)";
        }
    }

    if (gdLinesColumns) {//兼容两种列定义方式
        var col1 = gdLinesColumns[0];
        gdLinesColumns = ($.isArray(col1)) ? gdLinesColumns : [gdLinesColumns];
        for (var i = 0; i < gdLinesColumns.length; i++) {
            var col = gdLinesColumns[i];
            if ($.isArray(col)) {
                for (j = 0; j < col.length; j++) {
                    var coll = col[j];
                    setFieldRequere(coll);
                }
            } else {
                setFieldRequere(col);
            }
        }
    }

    if (listGridColumns) {
        var col1 = listGridColumns[0];
        listGridColumns = ($.isArray(col1)) ? listGridColumns : [listGridColumns];
        if (!treeField) {
            if ((listGridColumns[0]) && (listGridColumns[0][0]))
                treeField = listGridColumns[0][0].field;
        }
    }

    _findcolums = getFindColums(listGridColumns);//过滤查询列表

    var menutag = afrmOptions.menutag;
    this.menutag = function () {
        return menutag;
    };

    if (afrmOptions.onCreate) {
        afrmOptions.onCreate();
    }
    var JSONBandingFrm = new TJSONBandingFrm();//数据绑定
    var lineToolBar = [
        {
            text: '添加行',
            iconCls: 'icon-add1',
            handler: function () {
                if (caneditlinedata || isCanEdit) {
                    function append(rowdata) {
                        $C.grid.append("detail_main_grid_id", rowdata, true);
                    }

                    if (frmOptions.onAddLine)
                        frmOptions.onAddLine(append);
                    else
                        $C.grid.append("detail_main_grid_id", {}, true);
                    mainline.setDataChanged(true);

                }
            }
        },
        "-",
        {
            text: '删除行',
            iconCls: 'icon-remove',
            handler: function () {
                if (caneditlinedata || isCanEdit) {
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
                    mainline.setDataChanged(true);
                }
            }
        }
    ];

    function setButtonDisable(filter) {
        if (disableButtonType == _NUBType.disable)
            $(filter).linkbutton("disable");
        if (disableButtonType == _NUBType.hide)
            $(filter).css("display", "none");
    }

    function setButtonEnable(filter) {
        if (disableButtonType == _NUBType.disable)
            $(filter).linkbutton("enable");
        if (disableButtonType == _NUBType.hide)
            $(filter).css("display", "");
    }

    var TButtons = {
        btNew: {
            disable: function () {
                setButtonDisable("#id_bt_new");
                setButtonDisable("#id_bt_new2");
            },
            enable: function () {
                setButtonEnable("#id_bt_new");
                setButtonEnable("#id_bt_new2");
            },
            settext: function (text) {
                $("#id_bt_new").linkbutton({text: text});
            }

        },
        btCopy: {
            disable: function () {
                setButtonDisable("#id_bt_copy");
            },
            enable: function () {
                setButtonEnable("#id_bt_copy");
            },
            settext: function (text) {
                $("#id_bt_copy").linkbutton({text: text});
            }
        },
        btSave: {
            disable: function () {
                setButtonDisable("#id_bt_save");
            },
            enable: function () {
                setButtonEnable("#id_bt_save");
            },
            settext: function (text) {
                $("#id_bt_save").linkbutton({text: text});
            }
        },
        btSubmit: {
            disable: function () {
                setButtonDisable("#id_bt_submit");
            },
            enable: function () {
                setButtonEnable("#id_bt_submit");
            },
            settext: function (text) {
                $("#id_bt_submit").linkbutton({text: text});
            }
        },
        btFind: {
            disable: function () {
                setButtonDisable("#id_bt_find");
            },
            enable: function () {
                setButtonEnable("#id_bt_find");
            },
            settext: function (text) {
                $("#id_bt_find").linkbutton({text: text});
            }
        },
        btReload: {
            disable: function () {
                setButtonDisable("#id_bt_reload");
            },
            enable: function () {
                setButtonEnable("#id_bt_reload");
            },
            settext: function (text) {
                $("#id_bt_reload").linkbutton({text: text});
            }
        },
        btDel: {
            disable: function () {
                setButtonDisable("#id_bt_del");
            },
            enable: function () {
                setButtonEnable("#id_bt_del");
            },
            settext: function (text) {
                $("#id_bt_del").linkbutton({text: text});
            }
        },
        btAtt: {
            disable: function () {
                setButtonDisable("#id_bt_att");
            },
            enable: function () {
                setButtonEnable("#id_bt_att");
            },
            settext: function (text) {
                $("#id_bt_att").linkbutton({text: text});
            }
        },
        btPrint: {
            disable: function () {
                setButtonDisable("#id_bt_print");
            },
            enable: function () {
                setButtonEnable("#id_bt_print");
            },
            settext: function (text) {
                $("#id_bt_print").linkbutton({text: text});
            }
        },
        btExpt: {
            disable: function () {
                setButtonDisable("#id_bt_expt");
            },
            enable: function () {
                setButtonEnable("#id_bt_expt");
            },
            settext: function (text) {
                $("#id_bt_expt").linkbutton({text: text});
            }
        },
        btExit: {
            disable: function () {
                setButtonDisable("#id_bt_exit");
            },
            enable: function () {
                setButtonEnable("#id_bt_exit");
            },
            settext: function (text) {
                $("#id_bt_exit").linkbutton({text: text});
            }
        },
        btDelAtt: {
            disable: function () {
                setButtonDisable("#id_bt_delatt");
            },
            enable: function () {
                setButtonEnable("#id_bt_delatt");
            },
            settext: function (text) {
                $("#id_bt_delatt").linkbutton({text: text});
            }
        },
        btDownloadAtt: {
            disable: function () {
                setButtonDisable("#id_bt_downloadatt");
            },
            enable: function () {
                setButtonEnable("#id_bt_downloadatt");
            },
            settext: function (text) {
                $("#id_bt_downloadatt").linkbutton({text: text});
            }
        },
        setClick: function (onClick) {
            $("#id_bt_new").linkbutton({
                onClick: function () {
                    onClick($C.action.New);
                }
            });
            $("#id_bt_new2").linkbutton({
                onClick: function () {
                    onClick($C.action.New2);
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


    this.buttons = function () {
        return TButtons;
    };

    setTimeout(function () {
        initUI();
    }, 300);

//初始化界面
    function initUI() {
        initFram();
        initButtons();
        initInput();
        initGrids();

        if ((istree) || (autoFind))
            do_find([]);
        if ((edittps.isedit) && (htmlTemeType != HtmlTempType.htMLPop))
            donew();
        if (frmOptions.OnReady) {
            frmOptions.OnReady();
        }
        if (frmOptions.initid)
            findDetail(frmOptions.initid);
        if (wfform) {
            $("#main_tabs_id").tabs("close", "列表");
            $("#main_tabs_id").tabs("close", "流程");
        }
        if (wfform && (htmlTemeType == HtmlTempType.htMLPop)) {
            $("#mlp_main_sayout").css("display", "none");
            $("#datainfo_window_id").window({closable: false});
            //$("#mlp_main_div").html(.html());
        }
        //var t1 = new Date().getTime();
        //var t = (t1 - timestamp) / 1000;

        function movePopWin2Center() {
            var obj = $("#datainfo_window_id");
            var vlft = ($(window).width() - obj.width()) * 0.5;
            var vtop = ($(window).height() - obj.height()) * 0.3;
            vlft = (vlft < 0) ? 0 : vlft;
            vtop = (vtop < 0) ? 0 : vtop;
            $('#datainfo_window_id').window('move', {
                left: vlft,
                top: vtop
            });
        }

        function initFram() {
            if (htmlTemeType == HtmlTempType.htMLPop) {
                $("#datainfo_window_id").window({title: datainfo_pw_title});
                if ((windowWidth) && (windowHeight)) {
                    $('#datainfo_window_id').window('resize', {
                        width: windowWidth,
                        height: windowHeight
                    });
                    movePopWin2Center();
                }
                $("#detail_main_grid_div .datainfo_title").html(datainfo_line_title);
                $("#dg_att_div .datainfo_title").html(datainfo_attr_title);
                $("#main_tab_wf_div .datainfo_title").html(datainfo_wf_title);
                $("#main_tab_wf_id").css("height", wfWindowHeight);
            }
            $("#main_tabs_id").tabs("select", 1);
            $("#main_tabs_id").tabs("select", 0);
            if (istree)
                $("#main_tab_list_id").panel("resize", "{width:" + treeListWidth + "}");
            else
                $("#id_bt_new2").css("display", "none");
            if (istree) {
                $("#dg_datalist_id").treegrid({
                    border: false,
                    showHeader: showHeader,
                    onClickRow: onTreeListDbClick,
                    idField: idfield,
                    treeField: treeField,//listGridColumns[0].field,
                    columns: listGridColumns
                });
            } else {
                $("#dg_datalist_id").datagrid({
                    border: false,
                    onDblClickRow: onListDbClick,
                    columns: listGridColumns
                });
            }
            if (formtype == TFrmType.ftMainLine) {
                $("#detail_main_grid_id").datagrid({
                    singleSelect: true, rownumbers: true, fit: true,
                    onClickRow: function (index, row) {
                        if (caneditlinedata || isCanEdit) {
                            $C.grid.accept("detail_main_grid_id");
                        }
                    },
                    onDblClickRow: function (index, row) {
                        if (caneditlinedata || isCanEdit) {
                            if (afrmOptions.onLineGridDBClickRow) {
                                if (afrmOptions.onLineGridDBClickRow(row)) {
                                    $C.grid.edit("detail_main_grid_id");
                                    if (afrmOptions.afterInGridLineEdit)
                                        afrmOptions.afterInGridLineEdit(row);
                                }
                            } else {
                                $C.grid.edit("detail_main_grid_id");
                                if (afrmOptions.afterInGridLineEdit)
                                    afrmOptions.afterInGridLineEdit(row);
                            }
                        }
                    },
                    border: false,
                    columns: gdLinesColumns
                });
            }
            var els = JSONBandingFrm.getInputArray("#maindata_id");
            els_all = els.els_all;
            els_readonly = els.els_readonly;
            lables = els.els_lables;
            TButtons.setClick(OnBtClick);//设置按钮事件
        }


        function initButtons() {
            setBtsText();
            setBtsDisable();
            TButtons.btFind.enable();
            if (allow_expt)
                TButtons.btExpt.enable();
            if (allow_exit)
                TButtons.btExit.enable();
            if (allow_reload)
                TButtons.btReload.enable();
            if (allow_print)
                TButtons.btPrint.enable();
            if (wfform) {
                TButtons.btFind.disable();
            }
            if (edittps.isedit) {
                if (allow_new)
                    TButtons.btNew.enable();
                if (allow_del)
                    TButtons.btDel.enable();
                if (allow_copy)
                    TButtons.btCopy.enable();
                if (allow_submit)
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
                var co = els_all[i].cop;
                var et = els_all[i].et;
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
                        if (frmOptions.onInitInputComboboxDict) {
                            frmOptions.onInitInputComboboxDict(co, jsondata);
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
                    if (et == 1) {
                        obj.datetimebox({onChange: onValueChange});
                    } else
                        obj.textbox({onChange: onValueChange});
                }
                if (iscombox) {
                    //var input = obj.combobox("textbox");
                    //input.keydown(function () {
                    //});
                } else {
                    //var input = obj.textbox("textbox");
                    //input.bind('keydown', function (e) {
                    //});
                }
                if (co.required) {
                    var tv = getInputLabel(lables, co.fdname);//$(this).parent().prev();
                    if (tv)
                        tv.html(tv.html() + "(<span style='color: red'>*</span>)");
                }
                /*
                 if (!co.readonly && ((edittps.isedit) || (edittps.isupdate))) {
                 //JSONBandingFrm.setInputReadOnly(els_all[i], false);
                 //setInputReadOnly($(obj), false);
                 } else {
                 JSONBandingFrm.setInputReadOnly(els_all[i], true);
                 }
                 */
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
            if (pagination)
                inipageins("#dg_datalist_id");

            if ((formtype == TFrmType.ftMainLine) && (gdLinesColumns)) {
                $("#detail_main_grid_id").datagrid({
                    onAfterEdit: function (index, row, changes) {
                        if (mainline) {
                            mainline.setDataChanged(true);
                        }
                        if (frmOptions.afterEditGrid)
                            frmOptions.afterEditGrid("#detail_main_grid_id", index, row, changes);
                    }
                });
            }
        }
    }


    //根据编辑类型、单据状态刷新界面
    function reflashUI(stat) {
        isCanEdit = getCanEdit(stat);
        if (formtype == TFrmType.ftMainLine) {
            if (caneditlinedata || isCanEdit) {
                $("#detail_main_grid_id").datagrid({toolbar: lineToolBar});
            } else {
                $("#detail_main_grid_id").datagrid({toolbar: []});
            }
        }
        reSetBts();//设置按钮enable
        reSetInput();//设置输入框
        reSetGrids();//设置Grid
        function reSetBts() {
            setBtsDisable();
            if (!istree)
                TButtons.btFind.enable();

            if (allow_expt)
                TButtons.btExpt.enable();
            if (allow_exit)
                TButtons.btExit.enable();
            if (allow_reload)
                TButtons.btReload.enable();
            if (allow_print)
                TButtons.btPrint.enable();
            if (wfform) {
                TButtons.btFind.disable();
            }

            if (edittps.isedit) {
                if (allow_new)
                    TButtons.btNew.enable();
                if (allow_submit & (stat == 1))
                    TButtons.btSubmit.enable();
            }
            if (isCanEdit) {
                if (allow_new)
                    TButtons.btNew.enable();
                if (allow_del)
                    TButtons.btDel.enable();
                if (allow_copy)
                    TButtons.btCopy.enable();
                if (allow_submit & (stat == 1))
                    TButtons.btSubmit.enable();
                if (allowAtt) {
                    TButtons.btAtt.enable();
                    TButtons.btDelAtt.enable();
                    TButtons.btDownloadAtt.enable();
                }
            }
            if (allowAtt && (stat < 9)) {
                TButtons.btDownloadAtt.enable();
                TButtons.btDelAtt.enable();
                TButtons.btAtt.enable();
            }
            if (frmOptions.onSetButtonts) {
                frmOptions.onSetButtonts();
            }
        }

        function reSetInput() {
            for (var i = 0; i < els_all.length; i++) {
                var co = els_all[i].cop;
                if (co.readonly || !isCanEdit) {
                    JSONBandingFrm.setInputReadOnly(els_all[i], true);
                } else {
                    JSONBandingFrm.setInputReadOnly(els_all[i], false);
                }
            }
        }

        function reSetGrids() {
        }

    }

//Options:content_filter;content;  tabid;title;closable;
    this.appendMainTabContent = appendMainTabContent;
    function appendMainTabContent(Options) {
        var tabid = (Options.tabid) ? Options.tabid : "mtab" + new Date().format("hhmmss") + "_id";
        var closable = (Options.closable == undefined) ? false : Options.closable;
        var content = "";
        if (Options.content) {
            content = Options.content;
        } else {
            var o = $(Options.content_filter);
            content = o.html();
            o.html("");
        }
        $("#main_tabs_id").tabs("add", {
            id: tabid,
            title: Options.title,
            content: content,
            closable: closable
        });
        $("#main_tabs_id").tabs("select", "常规");
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

        $("#" + gdid).datagrid({
            onAfterEdit: function (index, row, changes) {
                if (mainline) {
                    mainline.setDataChanged(true);
                }
                if (frmOptions.afterEditGrid)
                    frmOptions.afterEditGrid("#" + gdid, index, row, changes);
            }
        });
        added_grid_ids.push(gdid);
        return {tabid: "tab_" + id, gdid: gdid};
    }

    function setBtsText() {
        if (bttexts.id_bt_new) TButtons.btNew.settext(bttexts.id_bt_new);
        if (bttexts.id_bt_copy) TButtons.btCopy.settext(bttexts.id_bt_copy);
        if (bttexts.id_bt_save) TButtons.btSave.settext(bttexts.id_bt_save);
        if (bttexts.id_bt_submit) TButtons.btSubmit.settext(bttexts.id_bt_submit);
        if (bttexts.id_bt_find) TButtons.btFind.settext(bttexts.id_bt_find);
        if (bttexts.id_bt_reload) TButtons.btReload.settext(bttexts.id_bt_reload);
        if (bttexts.id_bt_del) TButtons.btDel.settext(bttexts.id_bt_del);
        if (bttexts.id_bt_att) TButtons.btAtt.settext(bttexts.id_bt_att);
        if (bttexts.id_bt_print) TButtons.btPrint.settext(bttexts.id_bt_print);
        if (bttexts.id_bt_expt) TButtons.btExpt.settext(bttexts.id_bt_expt);
        if (bttexts.id_bt_exit) TButtons.btExit.settext(bttexts.id_bt_exit);
    }

    function setBtsDisable() {
        for (var i in TButtons)
            if (TButtons[i].disable)TButtons[i].disable();
    }

    function OnBtClick(act) {
        switch (act) {
            case $C.action.Find:
                find();
                break;
            case $C.action.New:
                donew(1);
                break;
            case $C.action.New2:
                donew(2);
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
                doprint(1);
                break;
            case $C.action.Export:
                doprint(2);
                break;
            case $C.action.Exit:
                if (datachanged) {
                    $.messager.confirm('提示', '忽略数据修改?', function (r) {
                        if (r) {
                            clearCurData();
                        }
                    });
                } else
                    clearCurData();
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

    this.clearGridViewData = clearGridViewData;
    function clearGridViewData() {
        if (formtype == TFrmType.ftMainLine) {
            var grids = ( frmOptions.gdLinesName) ? added_grid_ids.concat(["detail_main_grid_id"]) : added_grid_ids;
            JSONBandingFrm.clearMainData([], grids);
        }
    }

////  new
    function donew(idx) {
        if (datachanged) {
            $.messager.confirm('提示', '忽略数据修改?', function (r) {
                if (r) {
                    do_new(idx);
                }
            });
        } else
            do_new(idx);
        return true;
    }

    var isChildNodeNew = false;

    function clearCurData() {
        reSetInputReadOnly();
        isnew = true;
        clearViewData();
        curMainData = {};
        datachanged = false;
        $("#datainfo_window_id").window('close');
    }

    function do_new(idx) {
        reSetInputReadOnly();
        isnew = true;
        clearViewData();
        curMainData = {};
        $("#main_tabs_id").c_setJsonDefaultValue(curMainData, true);
        if (istree) {
            var node = $('#dg_datalist_id').treegrid('getSelected');
            if (idx == 1) {
                isChildNodeNew = false;
                if (node == undefined)
                    curMainData[treeParentField] = 0;
                else
                    curMainData[treeParentField] = node[treeParentField];
            }
            if (idx == 2) {
                isChildNodeNew = true;
                if (node == undefined) {
                    $.messager.alert('错误', '新建子节点必须先选定当前节点!', 'error');
                    return;
                } else
                    curMainData[treeParentField] = node[idfield];
            }
        }
        if (frmOptions.onNew)
            frmOptions.onNew(curMainData, idx);
        showDetail();
        if (htmlTemeType == HtmlTempType.htMLPop) {
            $("#datainfo_window_id").window('open');
        }
    }

////copy
    function docopy() {
        if (frmOptions.beforeCopy) {
            if (!frmOptions.beforeCopy(curMainData))
                return;
        }

        if (!allow_copy) {
            $.messager.alert('错误', '单据不允许复制', 'error');
            return;
        }
        $.messager.confirm('提示', '确定复制单据?', function (r) {
            if (r) {
                if (!copyFind_pw) {
                    var copyFind_pw_options = {
                        id: "copyFind_pw",
                        enableIdpath: true,
                        JPAClass: frmOptions.JPAClass,  //对应后台JPAClass名
                        gdListColumns: _findcolums,
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
                                parms.id = jpa[idfield];
                                if (frmOptions.onCopy) {
                                    frmOptions.onCopy(parms);
                                }
                                $ajaxjsonpost($C.cos.commonCopy, JSON.stringify(parms), function (jsdata) {
                                    $("#dg_datalist_id").datagrid("appendRow", jsdata);
                                    var id = jsdata[idfield];
                                    if ((!id) || (id.length == 0)) {
                                        alert("列表数据没发现ID值");
                                        return;
                                    }
                                    findDetail(id);
                                }, function (msg) {
                                    $.messager.alert('错误', '复制错误:' + msg.errmsg, 'error');
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
        if (frmOptions.beforeSave) {
            frmOptions.beforeSave();
        }
        curMainData = getMainData();
        if (!datachanged) {
            $.messager.alert('错误', '数据未修改！', 'error');
            return;
        }
        if (formtype == TFrmType.ftMainLine) {
            if (gdLinesName && gdLinesColumns)
                if (!JSONBandingFrm.checkNotNull(els_all, lables, curMainData, [gdLinesName], [gdLinesColumns])) return false;
        } else {
            if (!JSONBandingFrm.checkNotNull(els_all, lables, curMainData)) return false;
        }

        if ((!allowEmptyLine) && (formtype == TFrmType.ftMainLine)) {
            var lds = curMainData[gdLinesName];
            if ((!lds) || (lds.length == 0)) {
                $.messager.alert('错误', '明细行数据不允许为空！', 'error');
                return;
            }
        }

        //////save
        var surl = saveUrl;
        if ((frmOptions.saveUrl) && (frmOptions.saveUrl.length > 0)) {
            var postData = curMainData;
        } else
            var postData = {jpaclass: frmOptions.JPAClass, lines: true, jpadata: curMainData};
        if (frmOptions.onSave) {
            if (!frmOptions.onSave(postData)) return false;
        }
        $ajaxjsonpost(surl, JSON.stringify(postData), function (jdata) {
            curMainData = jdata;
            var dgojb = $("#dg_datalist_id");
            if (dgojb.length > 0) {//dg_datalist_id是否存在
                if (isnew) {
                    if (!allow_new) {
                        $.messager.alert('错误', '本界面不允许新建单据', 'error');
                        return;
                    }
                    if (istree) {
                        var node = dgojb.treegrid('getSelected');
                        var pid = 0;
                        if (node) {
                            if (isChildNodeNew)
                                pid = node[idfield];
                            else
                                pid = node[treeParentField];
                        }
                        dgojb.treegrid('append', {
                            parent: pid,
                            data: [jdata]
                        });
                        dgojb.treegrid('select', jdata[idfield]);
                    } else
                        dgojb.datagrid("appendRow", jdata);
                } else {
                    if (istree) {
                        var node = dgojb.treegrid('getSelected');
                        if (node) {
                            dgojb.treegrid("update", {id: node[idfield], row: jdata});
                        }
                    } else {
                        var arow = dgojb.datagrid("getSelected");
                        if (arow) {
                            var idx = dgojb.datagrid("getRowIndex", arow);
                            dgojb.datagrid("updateRow", {index: idx, row: jdata});
                            dgojb.datagrid("refreshRow", idx);
                        }
                    }
                }
            }
            isnew = false;
            showDetail();
            $.messager.alert('提示', '已经保存', 'info');
        }, function (msg) {
            $.messager.alert('错误', '保存数据错误:' + msg, 'error');
        });
    }

    function dodel() {
        curMainData = getMainData();
        var id = (curMainData[idfield]);
        if (!allow_del) {
            $.messager.alert('错误', '本页不允许删除单据！', 'error');
            return;
        }
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
                        var dgojb = $("#dg_datalist_id");
                        if (istree) {
                            var node = dgojb.treegrid('getSelected');
                            if (node) {
                                dgojb.treegrid("remove", node[idfield]);
                                if (htmlTemeType != HtmlTempType.htMLPop)
                                    donew(1);//成功删除后，清除界面，恢复新建状态
                            }
                        } else {
                            $C.grid.removeByField("#dg_datalist_id", id, idfield);
                            if (htmlTemeType != HtmlTempType.htMLPop)
                                donew(1);//成功删除后，清除界面，恢复新建状态
                            else
                                $("#datainfo_window_id").window('close');
                        }
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
        var jpaid = (curMainData[idfield]);
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
                        var allparms = {url: $C.cos.commoncreateWF, parms: parms};
                        if (frmOptions.beforeSubmit)
                            frmOptions.beforeSubmit(allparms);
                        $ajaxjsonpost(allparms.url, JSON.stringify(allparms.parms), function (jsdata) {
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
    var find_maxno = 0;
    this.find_reloper_data = [
        {id: 'like', value: '包含'},
        {id: '=', value: '等于'},
        {id: '>', value: '大于'},
        {id: '<', value: '小于'}
    ];

    function find() {
        if (allowFindPW) {
            $("#dg_find_window_parms_id tbody").html("");
            find_window_add_click();
            $("#find_window_id").window("open");
        } else
            do_find([]);
    }


    function getFindColums(listGridColumns) {
        var fcls = [];
        for (var i = 0; i < listGridColumns.length; i++) {
            var col = listGridColumns[i];
            if (col) {
                if ($.isArray(col)) {
                    for (var j = 0; j < col.length; j++) {
                        var coll = col[j];
                        if (coll && (coll.field))
                            fcls.push(coll);
                    }
                } else {
                    if (col.field)
                        fcls.push(col);
                }
            }
        }
        return fcls;
    }

    this.find_window_add_click = find_window_add_click;
    function find_window_add_click() {
        var hml = $("#findline").html();
        var rowid = "row" + find_maxno++;
        hml = hml.replace(new RegExp("{{rowid}}", "g"), rowid);
        hml = hml.replace(new RegExp("{{parmnameData}}", "g"), "valueField:'field',textField:'title',data:_findcolums");
        hml = hml.replace(new RegExp("{{reloperData}}", "g"), "valueField:'id',textField:'value',data:mainline.find_reloper_data");
        $(hml).appendTo("#dg_find_window_parms_id tbody");
        $.parser.parse("#" + rowid);
    }

    this.find_window_del_click = function (obj) {
        $(obj).parents("tr").remove();
    };

    this.find_window_selectparmname = function (rec) {
        var trparmvalue = $(this).parents("tr").find("td[tdtype]");
        var htm = undefined;
        if (rec.formatter) {
            var comurl = rec.formatter("get_com_url");
            if (comurl.type == "combobox") {
                htm = "<input class=\"easyui-combobox\" fieldname=\"parmvalue\" data-options=\"valueField:'" + comurl.valueField + "',textField:'"
                    + comurl.textField + "',data:comUrl_" + comurl.index + ".jsondata,width:150\">";
            }
        } else {
            if (rec.findtype == "date") {
                htm = "<input class=\"easyui-datebox\" fieldname=\"parmvalue\" data-options=\"formatter:$dateformattostrrYYYY_MM_DD,parser:$date4str,width:150\">";
            }
            if (rec.findtype == "datetime") {
                htm = "<input class=\"easyui-datetimebox\" fieldname=\"parmvalue\" data-options=\"formatter:$dateformattostr,parser:$date4str,width:150\">";
            }
        }
        if (!htm) {
            htm = "<input class=\"easyui-textbox\" fieldname=\"parmvalue\" data-options=\"width:150\">";
        }
        if (htm) {
            trparmvalue.html(htm);
            $.parser.parse(trparmvalue);
        } else {
            trparmvalue.html("");
        }
    };

    this.find_window_ok_click = function () {
        var parms = getFindData();
        do_find(parms);
    };

    function getFindData() {
        var data = [];
        $("#dg_find_window_parms_id tbody tr").each(function () {
            var row = {};
            $(this).find("input[fieldname]").each(function () {
                var fdname = $(this).attr("fieldname");
                if (fdname == "parmname") {
                    row.parmname = $(this).combobox("getValue");
                }
                if (fdname == "reloper") {
                    row.reloper = $(this).combobox("getValue");
                }
                if (fdname == "parmvalue") {
                    var clsname = $(this).attr("class");
                    if (clsname.indexOf("easyui-combobox") >= 0) {
                        row.parmvalue = $(this).combobox("getValue");
                    }
                    if ((clsname.indexOf("easyui-datetimebox") >= 0) || (clsname.indexOf("easyui-datebox") >= 0)) {
                        row.parmvalue = $(this).textbox("getValue");
                    }
                    if (clsname.indexOf("easyui-textbox") >= 0) {
                        row.parmvalue = $(this).textbox("getValue");
                    }
                }
            });
            if (row.parmname != undefined)
                data.push(row);
        });
        return data;
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
        if (istree) {
            allparms.type = "tree";
            allparms.parentid = treeParentField;
        }
        else
            allparms.type = "list";

        if (afrmOptions.onFind)
            afrmOptions.onFind(allparms);
        //var url = $C.cos.commonfind + "?type=" + allparms.type + "&chgidpath=" + allparms.chgidpath + "&edittps=" + JSON.stringify(allparms.edittps) + "&jpaclass=" + allparms.jpaclass;
        var url = findUrl + "?";
        for (var pn in allparms) {
            var tpstr = typeof(allparms[pn]);
            if ((tpstr == "object") || (tpstr == "function")) {
                url = url + pn + "=" + JSON.stringify(allparms[pn]) + "&";
            } else {
                url = url + pn + "=" + allparms[pn] + "&";
            }
        }
        if (url.length > 0)
            url = url.substring(0, url.length - 1);
        if (parms.length > 0)
            url = url + "&parms=" + JSON.stringify(allparms.parms);
        url = encodeURI(url);
        var gd = $("#dg_datalist_id");
        if (pagination) {
            var p = gd.datagrid("getPager");
            setOnListselectPage(url);
            var option = p.pagination("options");
            do_getdata(url, option.pageNumber, option.pageSize);
        } else
            do_getdata(url, 1, 1000);//不分页 1000行数据
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
            //$C.grid.clearData("dg_datalist_id");
            if (afrmOptions.onLoadData) {
                afrmOptions.onLoadData(jsdata);
            }
            if (pagination) {
                $("#dg_datalist_id").datagrid({pageNumber: page, pageSize: pageSize});
                setOnListselectPage(url);
            }
            //alert(JSON.stringify(jsdata));
            if (istree)
                $("#dg_datalist_id").treegrid("loadData", jsdata);
            else
                $("#dg_datalist_id").datagrid("loadData", jsdata);
            $("#main_tabs_id").tabs("select", "列表");
            $("#find_window_id").window("close");
        }, function (err) {
            alert(JSON.stringify(err));
        });
    }


    function onListDbClick(index, row) {
        onTreeListDbClick(row);
    }

    function onTreeListDbClick(row) {
        if (!idfield) {
            alert("没有设置JPA ID字段名");
            return;
        }
        var id = row[idfield];
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
        var url = findUrl + "?type=byid&chgidpath=true&id=" + id + "&jpaclass=" + frmOptions.JPAClass;
        $ajaxjsonget(url, function (jsdata) {
            if (afrmOptions.onLoadDetailData) {
                afrmOptions.onLoadDetailData(jsdata);
            }
            isnew = false;
            curMainData = jsdata;
            showDetail();
            var jpaid = (curMainData[idfield]);
            if ((!jpaid) || (jpaid.length == 0)) {
            } else {
                if (afrmOptions.afterFindDetail)
                    afrmOptions.afterFindDetail(jsdata);
            }
        }, function () {
            alert("查询数据错误");
        });
    }

    this.showDetail = showDetail;
    function showDetail() {
        isloadingdata = true;
        reflashUI(curMainData.stat);

        if (htmlTemeType == HtmlTempType.htMLPop) {
            $("#datainfo_window_id").window('open');
            if ((!$isEmpty(curMainData.stat)) && (parseInt(curMainData.stat) > 1)) {//&& (!edittps.isupdate)
                //有流程 不是更新界面
                $("#main_tab_wf_div").css("display", "");
                showWFinfo();//如果有流程  根据流程刷新可以编辑的输入框
            } else {
                $("#main_tab_wf_div").css("display", "none");
            }
        } else {
            //先改界面显示 再写入值，否则会消失，应该是bug
            if ((!$isEmpty(curMainData.stat)) && (parseInt(curMainData.stat) > 1)) {// && (!edittps.isupdate)
                //有流程 不是更新界面
                $("#main_tab_wf_div").css("display", "");
                showWFinfo();//如果有流程  根据流程刷新可以编辑的输入框
            } else {
                $("#main_tab_wf_div").css("display", "none");
                $("#main_tab_wf_id").html("");
                $("#main_tabs_id").tabs("select", "常规");
            }
        }

        getCurWFProcInfo(function () {
            setJson2Inputs();
            setJson2Grids();
            //setTimeout(function () {
            //}, 2000);
            if (afrmOptions.onSetReadOnly)
                afrmOptions.onSetReadOnly();
            if (afrmOptions.afterShowDetail)
                afrmOptions.afterShowDetail();
            isloadingdata = false;
            datachanged = false;
        });
    }

    var caneditlinedata = false;
    this.caneditlinedata = function () {
        return caneditlinedata;
    };
    function getCurWFProcInfo(onOK) {
        if (!$isEmpty(curMainData.wfid)) {
            var urlcurproc = _serUrl + "/web/wf/getWfCurProcedure.co?wfid=" + curMainData.wfid;
            $ajaxjsonget(urlcurproc, function (jsdata) {
                caneditlinedata = (parseInt(jsdata.caneditlinedata) == 1);
                var cds = jsdata.shwwfprocconditions;
                if (cds) {
                    for (var i = 0; i < cds.length; i++) {
                        var cd = cds[i];
                        if (parseInt(cd.allowedit) == 1) {
                            setReadOnly(cd.parmname, false);
                        } else {
                            if (parseInt(cd.visible) == 0) {//设置不可见
                                setVisible(cd.parmname, false);
                            }
                        }
                    }
                }

                onOK();
            }, function (msg) {
                onOK();
                alert("获取当前节点信息错误:" + msg);
            });
        } else
            onOK();
    }

    function showWFinfo() {
        if (!$isEmpty(curMainData.wfid)) {
            if (htmlTemeType != HtmlTempType.htMLPop)
                $("#main_tabs_id").tabs("select", "流程");
            var src = _serUrl + "/webapp/common/shwwf.html?wfid=" + curMainData.wfid + "&showtype=2";//只显示流程 不显示表单
            var wfifrm = "<iframe scrolling='no' frameborder='0' src='" + src + "' style='width: 100%;height: 98%'></iframe>";
            $("#main_tab_wf_id").html(wfifrm);
            //$("#main_tabs_id").tabs("select", "常规");
        } else {
            if (htmlTemeType != HtmlTempType.htMLPop) {
                $("#main_tabs_id").tabs("select", "常规");
            } else
                $("#main_tab_wf_div").css("display", "none");
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
        } else {
            $("#detail_main_grid_div").css("display", "none");
        }

        $("#maindata_id").find("table[cjoptions]").each(function (index, el) {
            var obj = $(this);
            var co = $.c_parseOptions(obj);
            if (co.readonly) {
                //
            }
            var fdname = co.fdname;
            if (fdname) {
                var cls = obj.attr("class");
                if (cls.indexOf("easyui-treegrid") != -1) {
                    if (curMainData[fdname])
                        obj.treegrid("loadData", curMainData[fdname]);
                }
                if (cls.indexOf("easyui-datagrid") != -1) {
                    if (curMainData[fdname]) {
                        obj.datagrid({data: curMainData[fdname]});
                    }
                }
            }
        });


        if (allowAtt) {
            if ((curMainData.shw_attachs) && (curMainData.shw_attachs.length > 0) && (curMainData.shw_attachs[0].shw_attach_lines)) {
                $("#dg_att_div").css("display", "");
                $("#dg_att_id").datagrid("loadData", curMainData.shw_attachs[0].shw_attach_lines);
            } else {
                $("#dg_att_div").css("display", "none");
                $C.grid.clearData("dg_att_id");
            }
        } else
            $("#dg_att_div").css("display", "none");
    }


    function doprint(tp) {
        curMainData = getMainData();
        var jpaid = (curMainData[idfield]);
        if ((!jpaid) || (jpaid.length == 0)) {
            $.messager.alert('错误', '未保存的或空数据，不能打印！', 'error');
            return;
        }
        var url = _serUrl + "/web/common/findModels.co?jpaclass=" + frmOptions.JPAClass;
        $("#pw_list_select").c_popselectList(url, function (fi) {
            if (fi) {
                var url = $C.cos.commondowloadExcelByModel + "?jpaclass=" + frmOptions.JPAClass + "&modfilename=" + fi.fname
                    + "&jpaid=" + getid() + "&tp=" + tp;
                window.open(url);
            }
        });

    }

//find end

///upload att
    function doupload() {
        curMainData = getMainData();
        var id = (curMainData[idfield]);
        if ((!id) || (id.length == 0)) {
            $.messager.alert('错误', '未保存的或空数据，不允许上传附件！', 'error');
            return;
        }
        if (curMainData.shw_attachs == undefined) {
            $.messager.alert('错误', 'JPA没有设置附件属性，不允许上传附件！', 'error');
            return;
        }
        var wd = $("#pw_uploadfile iframe");
        var action = encodeURI($C.cos.commonUpLoadFile + "?attImgThb=" + attImgThb + "&timestamp=" + (new Date()).valueOf());
        wd.attr("src", _serUrl + "/webapp/templet/default/uploadfile.html");
        var iframe = wd[0];
        if (iframe.attachEvent) {
            iframe.attachEvent("onload", function () {
                setAttPWCallback(iframe, action);
            });
        } else {
            iframe.onload = function () {
                setAttPWCallback(iframe, action);
            };
        }
        $("#pw_uploadfile").window("open");
    }

    function setAttPWCallback(iframe, action) {
        iframe.contentWindow._action = action;
        iframe.contentWindow._callback = function (rst) {
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
                datachanged = true;
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
    }

    function onAttGrigDBClick(index, row) {
        var furl = $C.cos.downattfile() + "?pfid=" + row.pfid;
        window.open(furl);
    }


//public proterty
    this.isnew = function () {
        return isnew;
    };
    this.isloadingdata = function () {
        return isloadingdata;
    };

    this.setMainData = setMainData;
    function setMainData(jsdata) {
        alert(setMainData);
        curMainData = jsdata;
        showDetail();
    }

    this.getMainData = getMainData;
    function getMainData() {
        if (!curMainData)curMainData = {};
        curMainData = JSONBandingFrm.toJsonData(els_all, curMainData, isnew);
        if ((formtype == TFrmType.ftMainLine) && gdLinesName) {
            $C.grid.accept("#detail_main_grid_id");
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
        return curMainData[idfield];
    }

    this.getEditType = function () {
        return edittps;
    };


//other functions


    function readOptions(aFrmOptions) {
        frmOptions.lineHeight = ( aFrmOptions.lineHeight) ? aFrmOptions.lineHeight : 200;
        gdLinesName = frmOptions.gdLinesName;
        lineHeight = frmOptions.lineHeight;
    }

    function setVisible(fdname, visible) {
        var v = undefined;
        var ipt = undefined;
        for (var i = 0; i < els_all.length; i++) {
            //var co = $.c_parseOptions($(els_all[i]));
            var co = els_all[i].cop;
            //var et = getInputType(els_all[i]);
            if (co.fdname == fdname) {
                ipt = $(els_all[i]);
                v = $(els_all[i]).textbox("getValue");
                break;
            }
        }
        if (ipt && (ipt.parent())) {
            ipt.parent().css("display", "none");
        }
        var tv = getInputLabel(lables, fdname);//$(this).parent().prev();
        if (tv && (tv.parent()))
            tv.parent().css("display", "none");
    }

    function setReadOnly(fdname, readonly) {
        var ipt = undefined;
        var input = findElinput(els_all, fdname);
        if (input) {
            var v = JSONBandingFrm.getInputValue(input);
            JSONBandingFrm.setInputReadOnly(input, readonly);
            JSONBandingFrm.setInputValue(input, v);
        }
    }

    this.setReadOnly = function (fdname, readonly) {
        setReadOnly(fdname, readonly);
    };


    /*
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


     function setAllInputReadOnly(readonly) {
     for (var i = 0; i < els_all.length; i++) {
     setInputReadOnly($(els_all[i]), readonly);
     }
     }
     */

    this.reSetInputReadOnly = reSetInputReadOnly;
    function reSetInputReadOnly() {
        for (var i = 0; i < els_all.length; i++) {
            //var co = $.c_parseOptions($(els_all[i]));
            var co = els_all[i].cop;
            if ((!co.readonly) || edittps.isedit) {
                JSONBandingFrm.setInputReadOnly(els_all[i], false);
                //setInputReadOnly($(els_all[i]), false);
            } else {
                JSONBandingFrm.setInputReadOnly(els_all[i], false);
                //setInputReadOnly($(els_all[i]), true);
            }
        }
    }

    this.getFieldValue = getFieldValue;
    function getFieldValue(fdname) {
        if (!curMainData)
            curMainData = getMainData();
        var v = undefined;
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            if (co.fdname == fdname) {
                v = JSONBandingFrm.getInputValue(els_all[i]);
                break;
            }
        }
        return (v) ? v : curMainData[fdname];
    }

    this.setFieldValue = function (fdname, value) {
        issetEditValue = true;
        try {
            curMainData[fdname] = value;
            var iput = findElinput(els_all, fdname);
            if (iput) {
                JSONBandingFrm.setInputValue(iput, value);
            }
        }
        finally {
            issetEditValue = false;
        }
    };

    function findElinput(els_all, fdname) {
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            if (co.fdname == fdname) {
                return els_all[i]
            }
        }
        return undefined;
    }


    this.getDataChanged = function () {
        return datachanged;
    };
    this.setDataChanged = function (value) {
        datachanged = value;
        if (datachanged)
            TButtons.btSave.enable();
    };

    this.getCanEdit = function () {
        return isCanEdit;
    };

    function getCanEdit(stat) {
        if (frmOptions.onGetCanEdit)
            return frmOptions.onGetCanEdit(stat, edittps);
        if (stat == undefined) {
            return (edittps.isedit || edittps.isupdate);
        }
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
    };

    function onValueChange(newValue, oldValue) {
        var co = this.cop;
        if ((co != undefined) && (afrmOptions.onEditChanged) && (!isloadingdata) && (!issetEditValue)) {
            afrmOptions.onEditChanged(co.fdname, newValue, oldValue);
        }
        if (mainline) {
            mainline.setDataChanged(true);
        }
    }

    this.BeforeSubmitWfNode = function (proc) {
        if (afrmOptions.beforeSubmitWfNode)
            return afrmOptions.beforeSubmitWfNode(proc);
        else
            return true;
    };


    this.exportExcelModel = exportExcelModel;
    function exportExcelModel() {
        var tp = 1;
        var url = _serUrl + "/web/common/exportExcelModel.co?jpaclass=" + frmOptions.JPAClass + "&tp=" + tp;
        window.open(url);
    }
}