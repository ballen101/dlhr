/**
 * Created by Administrator on 2015/4/28.
 */
var mainline = undefined;
var frmOptions = undefined, formetype = undefined, wfform = undefined, edittps = undefined;
//wfform 流程窗口
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
        if ($isEmpty(parms.edittps)) {
            alert("URL【edittps】参数为空");
            return;
        }
        edittps = JSON.parse(parms.edittps);
        alert(parms.edittps);
        frmOptions.initid = parms.id;
        frmOptions.spcetype = parms.spcetype;//特殊数据 0||undef 非特殊 1 个人所属 2 个人创建
        wfform = parms.wfform;
        var menutag = parseInt(parms.menutag);
        if ((wfform) && (wfform.toUpperCase != 'TRUE'))
            allowWF = false;
        if (wfform && $isEmpty(frmOptions.initid)) {
            alert("流程表单窗体需要实体ID参数");
            return;
        }
        frmOptions.menutag = menutag;
        setTimeout(function () {
            mainline = new TMainLine(frmOptions);
            if ((!wfform) || ($isEmpty(frmOptions.initid)))
                if (closes)closes(); 
        }, 300);
        return true;
    } else return false;
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
        return;
    }
    var dlgstuserbtts = undefined;
    var _wbsocketclient = undefined;
    var finddlg = undefined;//查询窗口对象
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
    var added_grids = [];//用户添加的Grids
    var isCanEdit = (edittps.isedit || edittps.isupdate);
    var pagination = (afrmOptions.Pagination == undefined) ? true : afrmOptions.Pagination;
    var istree = (afrmOptions.istree == undefined) ? false : afrmOptions.istree;
    var isasyntree = (afrmOptions.isasyntree == undefined) ? false : afrmOptions.isasyntree;
    var treeParentField = (afrmOptions.treeParentField == undefined) ? "" : afrmOptions.treeParentField;
    var treeListWidth = (afrmOptions.treeListWidth == undefined) ? 300 : afrmOptions.treeListWidth;
    var showHeader = (afrmOptions.showtreeHeader == undefined) ? true : afrmOptions.showtreeHeader;
    var listgrid_multiselect = (afrmOptions.listgrid_multiselect == undefined) ? false : afrmOptions.listgrid_multiselect;
    var allow_new = (afrmOptions.allow_new == undefined) ? true : afrmOptions.allow_new;
    var allow_del = (afrmOptions.allow_del == undefined) ? true : afrmOptions.allow_del;
    var allow_void = (afrmOptions.allow_void == undefined) ? false : afrmOptions.allow_void;
    var allow_copy = (afrmOptions.allow_copy == undefined) ? true : afrmOptions.allow_copy;
    //var allow_submit = (afrmOptions.allow_submit == undefined) ? true : afrmOptions.allow_submit;
    var _allow_submit = (allowWF || afrmOptions.allowWF);
    var allow_submit = (_allow_submit == undefined) ? true : _allow_submit;
    var allow_unsubmit = (afrmOptions.allow_unsubmit == undefined) ? false : afrmOptions.allow_unsubmit;
    var allow_expt = (afrmOptions.allow_expt == undefined) ? false : afrmOptions.allow_expt;
    var allow_colfilter = (afrmOptions.allow_colfilter == undefined) ? true : afrmOptions.allow_colfilter;
    var allow_expt_list = (afrmOptions.allow_expt_list == undefined) ? false : afrmOptions.allow_expt_list;
    var allow_reload = (afrmOptions.allow_reload == undefined) ? false : afrmOptions.allow_reload;
    var allow_print = (afrmOptions.allow_print == undefined) ? true : afrmOptions.allow_print;
    var printversion = (afrmOptions.printversion == undefined) ? 1 : afrmOptions.printversion;
    var printurl = (afrmOptions.printurl == undefined) ? undefined : afrmOptions.printurl;
    var showFooter = (afrmOptions.showFooter == undefined) ? false : afrmOptions.showFooter;
    var allow_navigator = (afrmOptions.allowNavigator == undefined) ? false : afrmOptions.allowNavigator;
    if (printversion == 2) {
        initWebSocketClient();
    }
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
    var findByIDUrl = (afrmOptions.findByIDUrl == undefined) ? findUrl : afrmOptions.findByIDUrl;
    var saveUrl = (afrmOptions.saveUrl == undefined) ? $C.cos.commonsave : afrmOptions.saveUrl;
    var gdLinesColumns = (!afrmOptions.gdLinesColumns) ? undefined : afrmOptions.gdLinesColumns;
    var disableButtonType = (!afrmOptions.disableButtonType) ? _NUBType.disable : afrmOptions.disableButtonType;
    var allowEmptyLine = (afrmOptions.allowEmptyLine == undefined) ? true : afrmOptions.allowEmptyLine;
    var treeField = (afrmOptions.treeField) ? afrmOptions.treeField : undefined;
    var findeditable = (afrmOptions.findeditable != undefined) ? afrmOptions.findeditable : false;
    var onSetGridDefaultToolbar = (afrmOptions.onSetGridDefaultToolbar) ? afrmOptions.onSetGridDefaultToolbar : undefined;
    var extButtons = (afrmOptions.extButtons != undefined) ? afrmOptions.extButtons : [];
    var extLineButtons = (afrmOptions.extLineButtons != undefined) ? afrmOptions.extLineButtons : [];
    var disableLineButtons = (afrmOptions.disableLineButtons != undefined) ? afrmOptions.disableLineButtons : false;
    var lineHeight = ( afrmOptions.lineHeight) ? afrmOptions.lineHeight : "300px";
    var spcetype = (afrmOptions.spcetype == undefined) ? 0 : afrmOptions.spcetype;
    var allowcheckexportfields = (afrmOptions.allowcheckexportfields == undefined) ? true : afrmOptions.allowcheckexportfields;
    var printurl = (afrmOptions.printurl == undefined) ? $C.cos.commondowloadExcelByModel : afrmOptions.printurl;
    var newaftersave = (afrmOptions.newaftersave != undefined) ? afrmOptions.newaftersave : false;
    var newaftersubmit = (afrmOptions.newaftersubmit != undefined) ? afrmOptions.newaftersubmit : false;
    if (newaftersave && newaftersubmit) {
        alert("参数【newaftersave】和【newaftersubmit】不能同时为true");
        return;
    }
    var pageList = (afrmOptions.pageList != undefined) ? afrmOptions.pageList : [30, 50, 100, 300];

    var _findcolums = [];
    this.setfindUrl = setfindUrl;
    function setfindUrl(url) {
        findUrl = url;
    }

    this.getspcetype = getspcetype;
    function getspcetype() {
        return spcetype;
    }

    function setFieldRequere(col) {
        if ((col) && (col.crequired)) {
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

    _findcolums = (afrmOptions.findColumns) ? getFindColums(afrmOptions.findColumns) : getFindColums(listGridColumns);//过滤查询列表

    var defaultListGridColumsn = $.extend(true, [], listGridColumns);//复制一个默认的
    listGridColumns = $InitGridColFields(listGridColumns, "dg_datalist_id");

    var menutag = afrmOptions.menutag;
    this.menutag = function () {
        return menutag;
    };

    if (afrmOptions.onCreate) {
        afrmOptions.onCreate();
    }

    var JSONBandingFrm = new TJSONBandingFrm({
        filter: "#maindata_id",
        onChange: onValueChange
    });//数据绑定

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
                    mainline.setDataChanged(true, "添加行");
                    //$("#detail_main_grid_id").datagrid("refreshRow", idx);
                }
            }
        },
        "-",
        {
            text: '删除行',
            iconCls: 'ico-remove_box',
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
                    mainline.setDataChanged(true, "删除行");
                }
            }
        },
        "-",
        {
            text: '清空',
            iconCls: 'ico-delete-all',
            handler: function () {
                if (caneditlinedata || isCanEdit) {
                    $.messager.confirm('确认', '确认清空所有行？', function (r) {
                        if (r) {
                            $("#detail_main_grid_id").datagrid("loadData", []);
                            if (frmOptions.afterClearGrid)
                                frmOptions.afterClearGrid("#detail_main_grid_id");
                            mainline.setDataChanged(true, "清空");
                        }
                    });
                }
            }
        }
    ];

    if (extLineButtons)
        lineToolBar = lineToolBar.concat(extLineButtons);


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
        btUnSubmit: {
            disable: function () {
                setButtonDisable("#id_bt_unsubmit");
            },
            enable: function () {
                setButtonEnable("#id_bt_unsubmit");
            },
            settext: function (text) {
                $("#id_bt_unsubmit").linkbutton({text: text});
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
        btVoid: {
            disable: function () {
                setButtonDisable("#id_bt_void");
            },
            enable: function () {
                setButtonEnable("#id_bt_void");
            },
            settext: function (text) {
                $("#id_bt_void").linkbutton({text: text});
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
        btPrintNew: {
            disable: function () {
                if (disableButtonType == _NUBType.disable)
                    $("#id_bt_print_new").splitbutton("disable");
                if (disableButtonType == _NUBType.hide)
                    $("#id_bt_print_new").css("display", "none");

            },
            enable: function () {
                if (disableButtonType == _NUBType.disable)
                    $("#id_bt_print_new").splitbutton("enable");
                if (disableButtonType == _NUBType.hide)
                    $("#id_bt_print_new").css("display", "");
            },
            settext: function (text) {
                $("#id_bt_print_new").splitbutton({text: text});
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
        btExptList: {
            disable: function () {
                setButtonDisable("#id_bt_expt_list");
            },
            enable: function () {
                setButtonEnable("#id_bt_expt_list");
            },
            settext: function (text) {
                $("#id_bt_expt_list").linkbutton({text: text});
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
        btColfilter: {
            disable: function () {
                setButtonDisable("#id_bt_gridcolfilter");
            },
            enable: function () {
                setButtonEnable("#id_bt_gridcolfilter");
            },
            settext: function (text) {
                $("#id_bt_gridcolfilter").linkbutton({text: text});
            }
        },
        btPrev: {
            disable: function () {
                setButtonDisable("#id_bt_prev");
            },
            enable: function () {
                setButtonEnable("#id_bt_prev");
            },
            settext: function (text) {
                $("#id_bt_prev").linkbutton({text: text});
            }
        },
        btNext: {
            disable: function () {
                setButtonDisable("#id_bt_next");
            },
            enable: function () {
                setButtonEnable("#id_bt_next");
            },
            settext: function (text) {
                $("#id_bt_next").linkbutton({text: text});
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
            $("#id_bt_void").linkbutton({
                onClick: function () {
                    onClick($C.action.Void);
                }
            });
            $("#id_bt_print").linkbutton({
                onClick: function () {
                    onClick($C.action.Print);
                }
            });
            $("#id_bt_print_new").splitbutton({
                onClick: function () {
                    onClick($C.action.PrintNew);
                }
            });
            $("#id_bt_print_preview").linkbutton({
                onClick: function () {
                    onClick($C.action.PrintPreview);
                }
            });
            $("#id_bt_print_design").linkbutton({
                onClick: function () {
                    onClick($C.action.PrintDesign);
                }
            });
            $("#id_bt_expt").linkbutton({
                onClick: function () {
                    onClick($C.action.Export);
                }
            });
            $("#id_bt_expt_list").linkbutton({
                onClick: function () {
                    onClick($C.action.ExportList);
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
                }
            });
            $("#id_bt_downloadatt").linkbutton({
                onClick: function () {
                    onClick($C.action.Download);
                }
            });
            $("#id_bt_gridcolfilter").linkbutton({
                onClick: function () {
                    onClick($C.action.ColsFilter);
                }
            });
            $("#bt_sumit_id").linkbutton({
                onClick: function () {
                    onClick($C.action.WFSubmit);
                }
            });
            $("#id_bt_unsubmit").linkbutton({
                onClick: function () {
                    onClick($C.action.WFUnSubmit);
                }
            });
            $("#bt_reject_id").linkbutton({
                onClick: function () {
                    onClick($C.action.WFReject);
                }
            });
            $("#bt_break_id").linkbutton({
                onClick: function () {
                    onClick($C.action.WFBreak);
                }
            });
            $("#bt_refer_id").linkbutton({
                onClick: function () {
                    onClick($C.action.WFRefer);
                }
            });
            $("#id_bt_prev").linkbutton({
                onClick: function () {
                    onClick($C.action.Prev);
                }
            });
            $("#id_bt_next").linkbutton({
                onClick: function () {
                    onClick($C.action.Next);
                }
            });
        }
    };


    this.buttons = function () {
        return TButtons;
    };

    setTimeout(function () {
        // alert("initUI-");
        initUI();
        // alert("initUI+");
    }, 300);
    //初始化界面
    function initUI() {
        initFram();
        initButtons();
        initGrids();
        if (htmlTemeType == HtmlTempType.htMLPop) {
            setTimeout(function () {
                doinitext();
            }, 300);
        } else {
            doinitext();
        }

        //var t1 = new Date().getTime();
        //var t = (t1 - timestamp) / 1000;

        function doinitext() {
            if (frmOptions.OnReady) {
                frmOptions.OnReady();
            }
            if (wfform) {//流程表单
                $("#main_tabs_id").tabs("close", "列表");
                $("#main_tabs_id").tabs("close", "流程");
                if (htmlTemeType == HtmlTempType.htMLPop) {
                    $("#mlp_main_sayout").css("display", "none");
                    $("#datainfo_window_id").window("maximize");
                    $("#datainfo_window_id").window({
                        closable: false, maximizable: false, draggable: false
                    });
                }
            } else {
                if ((istree) || (autoFind))
                    do_find([]);
                if ((edittps.isedit) && (htmlTemeType != HtmlTempType.htMLPop))
                    donew();
                if (istree) {
                    $("#main_tabs_id").tabs({showHeader: false});
                }
            }
            if (frmOptions.initid)
                findDetail(frmOptions.initid);
            if ((wfform) || (!$isEmpty(frmOptions.initid)))
                if (closes)closes();
        }

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

        function onInfoFrmFeforeClose() {
            if (datachanged) {
                $.messager.confirm('提示', '忽略数据修改?', function (r) {
                    if (r) {
                        setDataChanged(false);
                        $("#datainfo_window_id").window("close");
                    }
                });
                return false
            } else
                return true;
        }

        function onInfoFrmClose() {
            //alert(printversion);
            $("#id_bt_new").insertBefore($("#id_bt_copy"));
            if (printversion == 1)
                $("#id_bt_print").insertBefore($("#id_bt_expt"));
            if (printversion == 2)
                $("#id_bt_print_new").insertBefore($("#id_bt_expt"));
        }

        function onInfoFrmOpen() {
            //alert(printversion);
            $("#id_bt_new").insertBefore($("#id_bt_save"));
            if (printversion == 1)
                $("#id_bt_print").insertBefore($("#id_bt_void"));
            if (printversion == 2)
                $("#id_bt_print_new").insertBefore($("#id_bt_void"));
        }

        function initFram() {
            for (var i = 0; i < extButtons.length; i++) {
                var ebt = extButtons[i];

                if ((ebt.type == undefined) || (ebt.type == null))
                    ebt.type = "linkbutton";

                var id = (ebt.id) ? ebt.id : "ebt" + $generateUUID();
                ebt.id = id;
                if (!ebt.text) ebt.text = "";

                if (ebt.type == "linkbutton") {
                    var hs = "<a id='" + id + "' href='javascript:void(0)' class='easyui-linkbutton' data-options=\"plain:true,iconCls:'" + ebt.iconCls + "'\">" + ebt.text + "</a>";
                    if (ebt.posion) {
                        $(hs).insertBefore($(ebt.posion));
                    } else {
                        $("#id_bt_new").parent().append(hs);
                    }
                    $("#" + id).bind('click', ebt.handler);
                }
                if (ebt.type == "menubutton") {
                    var hs = "<a  id='" + id + "' href='javascript:void(0)' class='easyui-menubutton' data-options='menu:\"" + ebt.menu + "\",iconCls:\"" + ebt.iconCls + "\"'>" + ebt.text + "</a>";
                    if (ebt.posion) {
                        $(hs).insertBefore($(ebt.posion));
                    } else {
                        $("#id_bt_new").parent().append(hs);
                    }
                }
            }
            if (extButtons.length > 0) {
                $.parser.parse($("#id_bt_new").parent());
                $.parser.parse($("#id_bt_save").parent());
            }


            if (htmlTemeType == HtmlTempType.htMLPop) {
                $("#datainfo_window_id").window({
                    title: datainfo_pw_title,
                    onBeforeClose: onInfoFrmFeforeClose,
                    onClose: onInfoFrmClose,
                    onOpen: onInfoFrmOpen
                });
                if ((windowWidth) && (windowHeight)) {
                    if ((windowWidth == "100%") && (windowHeight == '100%')) {
                        $('#datainfo_window_id').window("maximize");
                    } else {
                        $('#datainfo_window_id').window('resize', {
                            width: windowWidth,
                            height: windowHeight
                        });
                        movePopWin2Center();
                    }
                }

                $("#detail_main_grid_div .datainfo_title").html(datainfo_line_title);
                $("#detail_main_grid_div").children().eq(1).css("height", lineHeight);
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
                    showFooter: showFooter,
                    singleSelect: !listgrid_multiselect,
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
            var els = JSONBandingFrm.getInputArray();
            els_all = els.els_all;
            els_readonly = els.els_readonly;
            lables = els.els_lables;
            TButtons.setClick(OnBtClick);//设置按钮事件
            var toolbar = $("#id_bt_new").parent();
            if (toolbar) {
                toolbar.dblclick(function () {
                    if (frmOptions.JPAClass) {
                        if (($C) && ($C.UserInfo) && (parseInt(window.top.$C.UserInfo.usertype()) == 1)) {
                            var url = _serUrl + "/web/common/getJPAInfo.co?jpaclass=" + frmOptions.JPAClass;
                            $ajaxjsonget(url, function (json) {
                                json.jpaclass = frmOptions.JPAClass;
                                var url = _serUrl + "/webapp/common/cjpainfo.html";
                                var win = window.open(url, "JPAInfo", "location=no,directories=no,scrollbars=yes,toolbar=yes,fullscreen=yes,directories=false");
                                win._jpainfo = json;
                            }, function (errmsg) {
                                $.messager.alert('错误', errmsg.errmsg, 'error');
                            });
                        }
                    }
                });
            }
            if ("function" != typeof TFindDialog) {
                $.messager.alert('错误', "需要引入cFindDlg.min.js文件", 'error');
                return;
            }
            finddlg = new TFindDialog({
                findeditable: findeditable,
                findFields: _findcolums,
                onOK: function (parms) {
                    if (parms)
                        do_find(parms);
                    return true;
                }
            });
        }


        function initButtons() {
            setBtsText();
            setBtsDisable();
            TButtons.btFind.enable();
            if (allow_colfilter)
                TButtons.btColfilter.enable();
            if (allow_expt)
                TButtons.btExpt.enable();
            if (allow_expt_list)
                TButtons.btExptList.enable();
            if (allow_exit)
                TButtons.btExit.enable();
            if (allow_reload)
                TButtons.btReload.enable();
            if (allow_print) {
                if (printversion == 1)
                    TButtons.btPrint.enable();
                if (printversion == 2)
                    TButtons.btPrintNew.enable();
            }
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
                            obj.combobox(co);
                            obj.combobox({onChange: onValueChange});
                        }
                        if (type == "combotree") {
                            obj.combotree(co);
                            obj.combotree("loadData", jsondata);
                            obj.combotree({onChange: onValueChange});
                        }
                    }
                } else {
                    if (et == 1) {
                        obj.datetimebox(co);
                        obj.datetimebox({onChange: onValueChange});
                    } else if (et == 6) {
                        obj.datebox(co);
                        obj.datebox({onChange: onValueChange});
                    } else if (et == 7) {
                        obj.numberbox(co);
                        obj.numberbox({onChange: onValueChange});
                    } else {
                        obj.textbox(co);
                        obj.textbox({onChange: onValueChange});
                    }
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
                if (co.crequired) {
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
                            mainline.setDataChanged(true, "行表编辑");
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
            if (onSetGridDefaultToolbar) {
                var tb = onSetGridDefaultToolbar(lineToolBar);
                if (tb)
                    $("#detail_main_grid_id").datagrid({toolbar: tb});
            } else {
                if ((caneditlinedata || isCanEdit) && (!disableLineButtons)) {
                    $("#detail_main_grid_id").datagrid({toolbar: lineToolBar});
                } else {
                    $("#detail_main_grid_id").datagrid({toolbar: []});
                }
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
            if (allow_expt_list)
                TButtons.btExptList.enable();
            if (allow_exit)
                TButtons.btExit.enable();
            if (allow_reload)
                TButtons.btReload.enable();
            if (allow_print) {
                if (printversion == 1)
                    TButtons.btPrint.enable();
                if (printversion == 2)
                    TButtons.btPrintNew.enable();
            }

            if (allow_colfilter)
                TButtons.btColfilter.enable();
            if ((stat == 9) && (allow_unsubmit)) {
                TButtons.btUnSubmit.enable();
            }
            if (wfform) {
                TButtons.btFind.disable();
            }
            if (edittps.isedit) {
                if (allow_new)
                    TButtons.btNew.enable();
                if (allow_submit && ((stat == 1) || (!stat)))
                    TButtons.btSubmit.enable();
            }
            if (isCanEdit) {
                if (allow_new && edittps.isedit)
                    TButtons.btNew.enable();
                if (allow_del && edittps.isedit)
                    TButtons.btDel.enable();
                if (allow_copy && edittps.isedit)
                    TButtons.btCopy.enable();
                if (allow_submit && ((stat == 1) || (!stat)))
                    TButtons.btSubmit.enable();
                if (allowAtt) {
                    TButtons.btAtt.enable();
                    TButtons.btDelAtt.enable();
                    TButtons.btDownloadAtt.enable();
                }
            }
            if (allow_void && (stat == 9)) {
                TButtons.btVoid.enable();
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

    function reSetNavgatorBts() {
        if (!allow_navigator || istree || isnew || (!curMainData)) {
            setNavgatorDisable(true);
        } else {
            var id = (curMainData[idfield]);
            if (!id) {
                setNavgatorDisable(false);
                return;
            }
            var row = getRowByID(id);
            if (!row) {
                setNavgatorDisable(false);
                return;
            }
            var idx = $("#dg_datalist_id").datagrid("getRowIndex", row);
            if (idx == -1) {
                setNavgatorDisable(false);
                return;
            }
            var isfirst = idx == 0;
            var islast = idx == ($("#dg_datalist_id").datagrid("getRows").length - 1);
            if (isfirst) {
                TButtons.btPrev.disable();
            } else {
                TButtons.btPrev.enable();
            }
            if (islast) {
                TButtons.btNext.disable();
            } else {
                TButtons.btNext.enable();
            }
        }
    }

    function setNavgatorDisable(disable) {
        if (disable) {
            TButtons.btPrev.disable();
            TButtons.btNext.disable();
        } else {
            TButtons.btPrev.enable();
            TButtons.btNext.enable();
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
    // filter:filter,//必填 如果没有 则在行表tab区域新建一个
    // title:title, //允许为空
    // gdLinesName: 'css_fix_lines',   //行Grid ID对应到json 明细行属性 名
    // columns:columns
    function appendLineGrid(Options) {
        if (formtype == TFrmType.ftSimple) return undefined;
        var filter = Options.filter;
        if ($(filter).length && $(filter).length > 0) {

        } else {//不存在GRID 需要创建
            var extglid = "extglid" + $generateUUID();
            filter = "#" + extglid;
            Options.filter = filter;
            $("#detail_main_tabs_id").tabs("add", {
                id: "tab_" + extglid,
                title: Options.title,
                content: "<table id='" + extglid + "'></table>"
            });
        }
        $(filter).datagrid({
            border: false,
            columns: [Options.columns]
        });
        $(filter).datagrid({
            onAfterEdit: function (index, row, changes) {
                if (mainline) {
                    mainline.setDataChanged(true, "自定义行表编辑");
                }
                if (frmOptions.afterEditGrid)
                    frmOptions.afterEditGrid(filter, index, row, changes);
            }
        });
        added_grid_ids.push(filter);
        added_grids.push(Options);
        return Options;
    }

    function setBtsText() {
        if (bttexts.id_bt_new) TButtons.btNew.settext(bttexts.id_bt_new);
        if (bttexts.id_bt_copy) TButtons.btCopy.settext(bttexts.id_bt_copy);
        if (bttexts.id_bt_save) TButtons.btSave.settext(bttexts.id_bt_save);
        if (bttexts.id_bt_submit) TButtons.btSubmit.settext(bttexts.id_bt_submit);
        if (bttexts.id_bt_unsubmit) TButtons.btUnSubmit.settext(bttexts.id_bt_unsubmit);
        if (bttexts.id_bt_find) TButtons.btFind.settext(bttexts.id_bt_find);
        if (bttexts.id_bt_reload) TButtons.btReload.settext(bttexts.id_bt_reload);
        if (bttexts.id_bt_del) TButtons.btDel.settext(bttexts.id_bt_del);
        if (bttexts.id_bt_void) TButtons.btDel.settext(bttexts.id_bt_void);
        if (bttexts.id_bt_att) TButtons.btAtt.settext(bttexts.id_bt_att);
        if (bttexts.id_bt_print) TButtons.btPrint.settext(bttexts.id_bt_print);
        if (bttexts.id_bt_print_new) TButtons.btPrint.settext(bttexts.id_bt_print_new);
        if (bttexts.id_bt_print_preview) TButtons.btPrint.settext(bttexts.id_bt_print_preview);
        if (bttexts.id_bt_print_design) TButtons.btPrint.settext(bttexts.id_bt_print_design);
        if (bttexts.id_bt_expt) TButtons.btExpt.settext(bttexts.id_bt_expt);
        if (bttexts.id_bt_expt_list) TButtons.btExptList.settext(bttexts.id_bt_expt_list);
        if (bttexts.id_bt_exit) TButtons.btExit.settext(bttexts.id_bt_exit);
        if (bttexts.id_bt_colfilter) TButtons.btColfilter.settext(bttexts.id_bt_colfilter);
        if (bttexts.id_bt_prev) TButtons.btPrev.settext(bttexts.id_bt_prev);
        if (bttexts.id_bt_next) TButtons.btNext.settext(bttexts.id_bt_next);
    }

    function setBtsDisable() {
        for (var i in TButtons) {
            if (TButtons[i].disable)TButtons[i].disable();
        }
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
                dosave(false, true);//自动提交 ； 点击保存按钮
                break;
            case $C.action.Del:
                dodel();
                break;
            case $C.action.Void:
                dovoid();
                break;
            case $C.action.Submit:
                dosubmit(true);
                break;
            case $C.action.WFUnSubmit:
                dounsubmit();
                break;
            case $C.action.Print:
                doprint(1);
                break;
            case $C.action.Export:
                doprint(2);
                break;
            case $C.action.PrintNew:
                doprint_new();
                break;
            case $C.action.PrintPreview:
                doprint_preview();
                break;
            case $C.action.Prev:
                doPrevRecord();
                break;
            case $C.action.Next:
                doNextRecord();
                break;
            case $C.action.PrintDesign:
                doprint_design();
                break;
            case $C.action.ExportList:
                if (allowcheckexportfields)
                    doexportlst();
                else
                    doexportlst1();
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
            case $C.action.ColsFilter:
                setColFilter();
                break;
            case $C.action.WFSubmit:
                wfbtaction(1);
                break;
            case $C.action.WFReject:
                wfbtaction(2);
                break;
            case $C.action.WFBreak:
                wfbtaction(3);
                break;
            case $C.action.WFRefer:
                wfbtaction(4);
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
            JSONBandingFrm.clearGridViewData(grids);
        }
    }

////  new
    this.donew = donew;
    function donew(idx, silent) {
        if (silent == undefined) silent = false;
        if (datachanged && (!silent)) {
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
        if (htmlTemeType != HtmlTempType.htMLPop) {
            reSetInputReadOnly();
            isnew = true;
            clearViewData();
        }
        curMainData = {};
        datachanged = false;
        $("#datainfo_window_id").window('close');
    }

    function do_new(idx) {
        reSetInputReadOnly();
        isnew = true;
        //clearViewData();
        clearGridViewData();
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
        } else
            isChildNodeNew = false;

        if (frmOptions.onNew)
            frmOptions.onNew(curMainData, idx);
        showDetail();
        if (htmlTemeType == HtmlTempType.htMLPop) {
            $("#datainfo_window_id").window('open');
        }
    }

    ////copy
    this.docopy = docopy;
    function docopy() {
        if (frmOptions.beforeCopy) {
            if (!frmOptions.beforeCopy(curMainData))
                return;
        }
        if (!allow_copy) {
            $.messager.alert('错误', '单据不允许复制', 'error');
            return;
        }
        if (!copyFind_pw) {
            var copyFind_pw_options = {
                id: "copyFind_pw",
                JPAClass: frmOptions.JPAClass,  //对应后台JPAClass名
                multiRow: false,
                autoFind: false,//是否自动查询
                gdListColumns: _findcolums,
                edittype: {isfind: true},
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
                                $.messager.alert('错误', "列表数据没发现ID值", 'error');
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

//save
    this.dosave = dosave;
    //andSubmit 保存后自动提交  isclicksave 点击按钮保存的 synch 同步保存
    function dosave(andSubmit, isclicksave, synch) {
        if (frmOptions.beforeSave) {
            frmOptions.beforeSave(andSubmit);
        }
        curMainData = getMainData();
        // console.error(JSON.stringify(curMainData));
        if (!datachanged) {
            $.messager.alert('错误', '数据未修改！', 'error');
            return false;
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
                $.messager.alert('错误', '【' + datainfo_line_title + '】不允许为空！', 'error');
                return false;
            }
        }
        if (isnew && (!allow_new)) {
            $.messager.alert('错误', '本界面不允许新建单据', 'error');
            return false;
        }
        //////save
        //var surl = saveUrl;
        if ((frmOptions.saveUrl) && (frmOptions.saveUrl.length > 0)) {
            var postData = curMainData;
        } else
            var postData = {jpaclass: frmOptions.JPAClass, lines: true, jpadata: curMainData};
        if (frmOptions.onSave) {
            if (!frmOptions.onSave(postData, andSubmit)) return false;
        }
        return postData2Save(postData, andSubmit, isclicksave, synch);
    }


    function getRowByID(id) {
        var dgojb = $("#dg_datalist_id");
        var rows = dgojb.datagrid("getRows");
        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            if (row[idfield] == id)
                return row;
        }
        return null;
    }

    function getNodeByID(id) {
        return $("#dg_datalist_id")
    }

    this.postData2Save = postData2Save;
    //synch 同步
    // 返回true： 同步 表示保存成功，异步 表示提交成功
    function postData2Save(postData, andSubmit, isclicksave, synch) {
        var rst = true;
        $ajaxjsonpost(saveUrl, JSON.stringify(postData), function (jdata) {
            curMainData = jdata;
            var dgojb = $("#dg_datalist_id");
            if (dgojb.length > 0) {//dg_datalist_id是否存在
                if (isnew) {
                    if (istree) {
                        var node = dgojb.treegrid('getSelected');//dgojb.treegrid("find", jdata[idfield]); myh 170615 添加新节点 挂的位置不对
                        var pid = 0;
                        if (node) {
                            pid = (isChildNodeNew) ? node[idfield] : node[treeParentField];
                        }
                        dgojb.treegrid('append', {
                            parent: pid,
                            data: [jdata]
                        });
                        dgojb.treegrid('select', jdata[idfield]);
                    } else {
                        dgojb.datagrid("appendRow", jdata);
                        var idx = dgojb.datagrid("getRowIndex", jdata);
                        dgojb.datagrid("selectRow", idx);
                    }
                    isnew = false;
                } else {
                    if (istree) {
                        var node = dgojb.treegrid("find", jdata[idfield]);
                        if (node) {
                            dgojb.treegrid("update", {id: node[idfield], row: jdata});
                        }
                    } else {
                        var arow = getRowByID(jdata[idfield]);
                        if (arow) {
                            var idx = dgojb.datagrid("getRowIndex", arow);
                            dgojb.datagrid("updateRow", {index: idx, row: jdata});
                            dgojb.datagrid("refreshRow", idx);
                        }
                    }
                }
            }
            isnew = false;
            if (frmOptions.afterSave) {
                frmOptions.afterSave(jdata);
            }
            if (newaftersave && isclicksave && isCanEdit) {//保存后新建 ；点击保存按钮
                donew(1, true);
            } else if (!synch) {//异步才刷新数据
                find_detail(jdata[idfield], true, andSubmit);
            }
        }, function (msg) {
            $.messager.alert('错误', '保存数据错误:' + msg.errmsg, 'error');
            rst = false;
        }, !synch);
        return rst;
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

        if (frmOptions.onDel) {
            if (!frmOptions.onDel(curMainData))
                return;
        }

        $.messager.confirm('确认', '确认删除当前表单?', function (r) {
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
                    $.messager.alert('错误', '删除失败:' + msg.errmsg, 'error');
                });
            }
        });
    }

    function dovoid() {
        curMainData = getMainData();
        var id = (curMainData[idfield]);
        if (!allow_del) {
            $.messager.alert('错误', '本页不允许作废单据！', 'error');
            return;
        }
        if ((!id) || (id.length == 0)) {
            $.messager.alert('错误', '未保存的或空数据，不需要作废！', 'error');
            return;
        }
        if (curMainData.stat != 9) {
            $.messager.alert('错误', '未生效的表单，不需要作废！', 'error');
            return;
        }
        $.messager.confirm('确认', '确认作废当前表单?', function (r) {
            if (r) {
                var dftvdurl = _serUrl + "/web/common/dovoid.co";
                var surl = ((frmOptions.voidUrl) && (frmOptions.voidUrl.length > 0)) ? frmOptions.voidUrl : dftvdurl;
                surl = surl + "?jpaclass=" + frmOptions.JPAClass + "&id=" + id;
                $ajaxjsonget(surl, function (jdata) {
                    curMainData = jdata;
                    var dgojb = $("#dg_datalist_id");
                    if (dgojb.length > 0) {//dg_datalist_id是否存在
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
                                showDetail();
                            }
                        }
                    }
                }, function (msg) {
                    $.messager.alert('错误', '删除失败:' + msg.errmsg, 'error');
                });
            }
        });
    }

    var pw_SearchWT = undefined;

    //反审核
    this.dounsubmit = dounsubmit;
    function dounsubmit() {
        curMainData = getMainData();
        var jpaid = curMainData[idfield];
        if ((!jpaid) || (jpaid.length == 0)) {
            $.messager.alert('错误', '空数据，不能反审核！', 'error');
            return;
        }
        var stat = parseInt(curMainData.stat);
        if (stat != 9) {
            $.messager.alert('错误', '只能反审核已经审批通过的单据！', 'error');
            return;
        }
        $.messager.confirm('确认', '确认反审核单据?', function (r) {
            if (r) {
                var url = _serUrl + "/web/common/unsubmitWF.co";
                var pdata = {jpaclass: frmOptions.JPAClass, jpaid: jpaid};
                $ajaxjsonpost(url, JSON.stringify(pdata), function (jsdata) {
                    var dgojb = $("#dg_datalist_id");
                    if (istree) {
                        var node = dgojb.treegrid("find", jsdata[idfield]);
                        if (node) {
                            dgojb.treegrid("update", {id: node[idfield], row: jsdata});
                        }
                    } else {
                        var arow = getRowByID(jsdata[idfield]);
                        if (arow) {
                            var idx = dgojb.datagrid("getRowIndex", arow);
                            dgojb.datagrid("updateRow", {index: idx, row: jsdata});
                            dgojb.datagrid("refreshRow", idx);
                        }
                    }
                    find_detail(jsdata[idfield], true, false);
                }, function (msg) {
                    $.messager.alert('错误', '反审核单据错误:' + msg.errmsg, 'error');
                });
            }
        });
    }

    this.dosubmit = dosubmit;
    //showcfinfo:显示确认信息
    function dosubmit(showcfinfo) {
        curMainData = getMainData();
        var jpaid = (curMainData[idfield]);
       if (datachanged) {
            dosave(true);//保存后自动提交
            return;
            //$.messager.alert('错误', '请先保存单据！', 'error');
            //return;
        }

        if ((!jpaid) || (jpaid.length == 0)) {
            $.messager.alert('错误', '空数据，不能提交！', 'error');
            return;
        }

        if (showcfinfo) {
            $.messager.confirm('确认', '确认提交当前单据?', function (r) {
                if (r) {
                    showselectwftemp();
                }
            });
        } else   showselectwftemp();

        //选择审批人的按钮
        dlgstuserbtts = [
            {text: '添加', iconCls: 'icon-user'},
            {text: '确定', iconCls: 'ico-check-fill'},
            {
                text: '取消', handler: function () {
                $("#pw_select_user").dialog("close");
            }
            }
        ];
    }

    function showselectwftemp() {
        var jpaid = (curMainData[idfield]);
        var pwOption = {
            id: "pws_selectWFTem",
            JPAClass: "com.corsair.server.wordflow.Shwwftemp",  //对应后台JPAClass名
            gdListColumns: [
                {field: 'wftempname', title: '流程名称', width: 150}
            ],
            showTitle: false,
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
                postsubmit(wftempid);
            }
        };
        if (pw_SearchWT) {
            pw_SearchWT.TSearchForm(pwOption);
        } else {
            pw_SearchWT = new TSearchForm(pwOption);
        }
        pw_SearchWT.show(true);
    }


    //如果需要选择审批用户，users为选择的用户
    function postsubmit(wftempid, users) {
        var jpaid = (curMainData[idfield]);
        var parms = {jpaclass: frmOptions.JPAClass, jpaid: jpaid};
        if (wftempid)
            parms.wftempid = wftempid;
        if (users)
            parms.userids = users;
        var allparms = {url: $C.cos.commoncreateWF, parms: parms};
        if (frmOptions.beforeSubmit)
            if (!frmOptions.beforeSubmit(allparms))
                return;
        doPostWorkFlowParms(allparms);
    }

    this.doPostWorkFlowParms = doPostWorkFlowParms;
    function doPostWorkFlowParms(allparms) {
        $ajaxjsonpost(allparms.url, JSON.stringify(allparms.parms), function (jsdata) {
            if (jsdata.type == "procuser") {
                //显示用户选择列表 选择后重新提交
                $('#pw_select_user_list').datalist({
                    valueField: "userid",
                    textField: "displayname",
                    data: jsdata.users,
                    singleSelect: false,
                    checkbox: true
                });
                dlgstuserbtts[0].handler = function () {
                    onFindUserAddWFlist();
                };
                dlgstuserbtts[1].handler = function () {
                    var rows = $('#pw_select_user_list').datalist("getChecked");
                    if ((!rows) || (rows.length <= 0)) {
                        alert("没有选择数据");
                        return;
                    }
                    var uids = [];
                    for (var i = 0; i < rows.length; i++) {
                        var row = rows[i];
                        uids.push(row.userid);
                    }
                    var wftempid = allparms.parms.wftempid;
                    postsubmit(wftempid, uids.join(","));
                    $("#pw_select_user").dialog("close");
                };
                $("#pw_select_user").dialog({buttons: dlgstuserbtts});
                $("#pw_select_user").dialog("open");
            } else {
                if (frmOptions.afterCreateWF)
                    frmOptions.afterCreateWF(jsdata);

                var dgojb = $("#dg_datalist_id");
                if (istree) {
                    var node = dgojb.treegrid("find", jsdata[idfield]);
                    if (node) {
                        dgojb.treegrid("update", {id: node[idfield], row: jsdata});
                    }
                } else {
                    var arow = getRowByID(jsdata[idfield]);
                    if (arow) {
                        var idx = dgojb.datagrid("getRowIndex", arow);
                        dgojb.datagrid("updateRow", {index: idx, row: jsdata});
                        dgojb.datagrid("refreshRow", idx);
                    }
                }

                if (newaftersubmit) {
                    donew(1, true);
                } else {
                    curMainData = jsdata;
                    showDetail();
                }
            }
        }, function (msg) {
            $.messager.alert('错误', '提交单据错误:' + msg.errmsg, 'error');
        });
    }

    var select_user_pw = undefined;

    function onFindUserAddWFlist() {
        var wo = {
            id: "select_user_pw",
            coURL: _serUrl + "/web/user/findOrgUser.co",
            orderStr: " orgid asc ",
            multiRow: false,
            idField: 'orgid',
            autoFind: false,//是否自动查询
            gdListColumns: [
                {field: 'username', title: '用户名', width: 80},
                {field: 'displayname', title: '姓名', width: 80},
                {field: 'extorgname', title: '机构', width: 200, notfind: true}
            ],
            onResult: function (rows) {
                for (var i = 0; i < rows.length; i++) {
                    var row = {userid: rows[i].userid, displayname: rows[i].displayname};
                    $('#pw_select_user_list').datalist('appendRow', row);
                    var idx = $('#pw_select_user_list').datalist("getRowIndex", row);
                    $('#pw_select_user_list').datalist('checkRow', idx);
                }
            }
        };
        if (!select_user_pw) {
            select_user_pw = new TSearchForm(wo);
        }
        select_user_pw.show();
    }

////find begin
    function find() {
        if (allowFindPW) {
            finddlg.show();
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

    function inipageins(gdfilter) {
        var gd = $(gdfilter);
        gd.datagrid({
            // pageNumber: 1,
            pagination: true,
            rownumbers: true,
            pageSize: pageList[0],
            pageList: pageList
        });

        var p = gd.datagrid('getPager');
        p.pagination({
            beforePageText: '第',//页数文本框前显示的汉字
            afterPageText: '页    共 {pages} 页',
            displayMsg: '共{total}条数据'
        });
    }

    var _perparms = undefined;

    function getsearchstr(url) {

    }

    function geturlparms(url) {
        var urls = url.substr(url.indexOf("?"));
        var qs = urls.split("+").join(" ");
        var params = {}, tokens,
            re = /[?&]?([^=]+)=([^&]*)/g;
        while (tokens = re.exec(qs)) {
            params[decodeURIComponent(tokens[1])] = decodeURIComponent(tokens[2]);
        }
        return params;
    }

    var extParms = {};
    this.do_find = do_find;
    function do_find(parms) {
        if (parms == undefined) parms = [];
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
        } else
            allparms.type = "list";

        if (afrmOptions.onFind)
            afrmOptions.onFind(allparms);
        _perparms = allparms;
        var url = buildUrl(findUrl, allparms);
        var gd = $("#dg_datalist_id");
        if (istree && isasyntree) {
            gd.treegrid({url: url});
        } else {
            extParms.totals = getTotalInfos();
            if (pagination) {
                var p = gd.datagrid("getPager");
                setOnListselectPage(url);
                var option = p.pagination("options");
                do_getdata(url, option.pageNumber, option.pageSize);
            } else
                do_getdata(url, 1, 1000);//不分页 1000行数据
        }
    }

    function getTotalInfos() {
        var totles = [];
        var fcls = listGridColumns;
        for (var i = 0; i < fcls.length; i++) {
            var col = fcls[i];
            if (col) {
                if ($.isArray(col)) {
                    for (var j = 0; j < col.length; j++) {
                        var coll = col[j];
                        if (coll && coll.field) {
                            getTotlInfoCol(coll, totles);
                        }
                    }
                } else {
                    if (col.field) {
                        getTotlInfoCol(col, totles);
                    }
                }
            }
        }
        return totles;
    }

    function getTotlInfoCol(col, totles) {
        if (col.total_title || col.total_type) {
            var t = {};
            t.field = col.field;
            if (col.total_title)
                t.total_title = col.total_title;
            if (col.total_type)
                t.total_type = col.total_type;
            totles.push(t);
        }
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
        if (pagination && (page == 0)) page = 1;
        //alert("do_getdata");
        if (!extParms)extParms = {};
        $ajaxjsonpost(url + "&page=" + page + "&rows=" + pageSize, JSON.stringify(extParms), function (jsdata) {
            //$C.grid.clearData("dg_datalist_id");
            //alert(jsdata);
            if (afrmOptions.onLoadData) {
                var rst = afrmOptions.onLoadData(jsdata);
                if (rst)
                    jsdata = rst;
            }
            if (pagination) {
                $("#dg_datalist_id").datagrid({pageNumber: page, pageSize: pageSize});
                setOnListselectPage(url);
            }
            //alert(JSON.stringify(jsdata));
            if (istree)
                $("#dg_datalist_id").treegrid("loadData", jsdata);
            else {
                //if (page == 1) {
                //    $("#dg_datalist_id").datagrid("options").pageNumber = 1;
                // }
                $("#dg_datalist_id").datagrid("loadData", jsdata);
                $("#dg_datalist_id").datagrid({data: jsdata});//与分页冲突
                if (pagination) {
                    $("#dg_datalist_id").datagrid({pageNumber: page, pageSize: pageSize});
                    setOnListselectPage(url);
                }
            }
            $("#main_tabs_id").tabs("select", "列表");
            $("#find_window_id").window("close");
            if (afrmOptions.afterShowListData)
                afrmOptions.afterShowListData(jsdata);
        }, function (err) {
            $.messager.alert('错误', JSON.stringify(err), 'error');
        });
    }


    function onListDbClick(index, row) {
        var obj = $("#dg_datalist_id");
        if (istree) {
            obj.treegrid("select", row[idfield]);
        } else {
            obj.datagrid("unselectAll");
            obj.datagrid("selectRow", index);
        }
        onTreeListDbClick(row);
    }

    function onTreeListDbClick(row) {
        if (!idfield) {
            $.messager.alert('错误', "没有设置JPA ID字段名", 'error');
            return;
        }
        var id = row[idfield];
        if ((!id) || (id.length == 0)) {
            $.messager.alert('错误', "列表数据没发现ID值", 'error');
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

    this.buildUrl = buildUrl;
    function buildUrl(aurl, parms) {
        var ups = geturlparms(aurl);
        var allparms = $.extend(parms, ups);
        var url = (aurl.indexOf("?") < 0) ? aurl : aurl.substr(0, aurl.indexOf("?"));
        url = url + "?spcetype=" + spcetype + "&";
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
        return encodeURI(url);
    }

    // id :查询数据的id
    //showsaveinfo:显示保存成功信息
    //andSubmit 自动提交
    function find_detail(id, showsaveinfo, andSubmit) {
        var allparms = {
            type: "byid",
            id: id,
            chgidpath: true,
            jpaclass: frmOptions.JPAClass
        };
        var url = buildUrl(findByIDUrl, allparms);

        curMainData = {};
        $ajaxjsonget(url, function (jsdata) {
            if ((jsdata.rows) && (jsdata.rows.length > 0 )) {
                jsdata = jsdata.rows[0];
            }
            if (afrmOptions.onLoadDetailData) {
                afrmOptions.onLoadDetailData(jsdata, andSubmit);
            }
            isnew = false;
            curMainData = jsdata;
            showDetail(function () {
                var jpaid = (curMainData[idfield]);
                if ((!jpaid) || (jpaid.length == 0)) {
                } else {
                    if (afrmOptions.afterFindDetail)
                        afrmOptions.afterFindDetail(jsdata, andSubmit);
                }
                if (andSubmit) {
                    //alert("will submit");
                    dosubmit(false);
                } else if (showsaveinfo == true)
                    $.messager.alert('提示', '已经保存', 'info');
            });
        }, function () {
            $.messager.alert('错误', "查询数据错误", 'error');
        });
    }

    this.showDetail = showDetail;
    function showDetail(callback) {
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
            reSetNavgatorBts();
            if (afrmOptions.onSetReadOnly)
                afrmOptions.onSetReadOnly();
            if (afrmOptions.afterShowDetail)
                afrmOptions.afterShowDetail(curMainData);
            isloadingdata = false;
            datachanged = false;
            if (callback)
                callback();
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
                $.messager.alert('错误', "获取当前节点信息错误:" + msg.errmsg, 'error');
            });
        } else
            onOK();
    }

    this.getWFInfo = getWFInfo;
    function getWFInfo() {
        var wd = $("#main_tab_wf_id iframe");
        var iframe = wd[0];
        if (!iframe) return;
        var pw = iframe.contentWindow;
        if (pw)
            return pw._wfinfo;
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
            } else $C.grid.clearData("detail_main_grid_id");
            for (var i = 0; i < added_grids.length; i++) {
                var gridop = added_grids[i];
                try {
                    if (curMainData[gridop.gdLinesName])
                        $(gridop.filter).datagrid("loadData", curMainData[gridop.gdLinesName]);
                    else
                        $C.grid.clearData(gridop.filter);
                } catch (e) {
                    console.error(e);
                }
            }
        } else {
            $("#detail_main_grid_div").css("display", "none");
        }

        //将maindata_id 下面 grid 根据属性 fdname 与 数据属性同名进行赋值
        $("#maindata_id").find("table[cjoptions]").each(function (index, el) {
            var obj = $(this);
            var co = $.c_parseOptions(obj);
            if (co.readonly) {
                //
            }
            var fdname = co.fdname;
            if (fdname) {
                var cls = obj.attr("class");
                if (cls)
                    cls = cls + obj.attr("cjoptions");
                else
                    cls = obj.attr("cjoptions");

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
        var w = $("#datainfo_window_id").window("window");
        if (w.css("display") == 'none') {//没有显示明细窗体
            var srows = $("#dg_datalist_id").datagrid("getSelections");
            if (srows.length == 0) {
                $.messager.alert('错误', '没有选择需要打印的数据！', 'error');
                return;
            }
            var jpaids = "";
            for (var i = 0; i < srows.length; i++) {
                jpaids = jpaids + srows[i][idfield] + ",";
            }
        } else {//显示了明细窗体
            curMainData = getMainData();
            var id = (curMainData[idfield]);
            if ((!id) || (id.length == 0)) {
                $.messager.alert('错误', '未保存的或空数据，不允许打印！', 'error');
                return;
            }
            jpaids = id + ",";
        }
        if (jpaids.length > 0)
            jpaids = jpaids.substr(0, jpaids.length - 1);
        var url = _serUrl + "/web/common/findModels.co?jpaclass=" + frmOptions.JPAClass;
        $("#pw_list_select").c_popselectList(url, function (fi) {
            if (fi) {
                dopostprint(printurl, frmOptions.JPAClass, jpaids, fi.fname, tp);
            }
        });
    }

    function initWebSocketClient() {
        if (!printurl) {
            $.messager.alert('错误', "【printversion】为2时候需要设置【printurl】参数", 'error');
            return;
        } else {
            if ("function" != typeof CWBSKTClient) {
                $.messager.alert('错误', "需要引入cwbsktclient.min.js文件", 'error');
                return;
            } else {
                var options = {
                    url: printurl,
                    CallBack_StatChanged: function (stat) {
                        if (stat == 1) {
                            $("#id_skt_stat").html("HTTP2DLL连接成功");
                            $("#id_skt_stat").css("color", "darkgreen");
                        } else if (stat == 2) {
                            $("#id_skt_stat").html("HTTP2DLL连接断开");
                            $("#id_skt_stat").css("color", "red");
                        } else if (stat == 3) {
                            $("#id_skt_stat").html("HTTP2DLL连接重连...");
                            $("#id_skt_stat").css("color", "blue");
                        }
                    }
                };
                _wbsocketclient = new CWBSKTClient(options);
                var prtActionOptions = {DillType: 1, DllFileName: "print\\phprint.dll", MethodName: "phprint.ClassMain.dpprint"};
                _wbsocketclient.regAction("doPrintFastReport", prtActionOptions);//注册Action
            }
        }
    }

    this.getWebsocketClient = getWebsocketClient;
    function getWebsocketClient() {
        return _wbsocketclient;
    }

    //新打印
    function doprint_new() {
        doPrint_NewAction(1);
    }

    //新预览
    function doprint_preview() {
        doPrint_NewAction(2);
    }

    //新设计
    function doprint_design() {
        doPrint_NewAction(3);
    }

    //1 打印 2 预览 3 设计
    function doPrint_NewAction(tp) {
        var w = $("#datainfo_window_id").window("window");
        if (w.css("display") == 'none') {//没有显示明细窗体
            var srows = $("#dg_datalist_id").datagrid("getSelections");
            if (srows.length == 0) {
                $.messager.alert('错误', '没有选择需要打印的数据！', 'error');
                return;
            }
            var jpaids = "";
            for (var i = 0; i < srows.length; i++) {
                jpaids = jpaids + srows[i][idfield] + ",";
            }
        } else {//显示了明细窗体
            curMainData = getMainData();
            var id = (curMainData[idfield]);
            if ((!id) || (id.length == 0)) {
                $.messager.alert('错误', '未保存的或空数据，不允许打印！', 'error');
                return;
            }
            jpaids = id + ",";
        }
        if (jpaids.length > 0)
            jpaids = jpaids.substr(0, jpaids.length - 1);
        var url = _serUrl + "/web/common/findModels.co?tp=2&jpaclass=" + frmOptions.JPAClass;
        $("#pw_list_select").c_popselectList(url, function (fi) {
            if (fi) {
                if ((!_wbsocketclient) || (!_wbsocketclient.isConnected())) {
                    $.messager.alert('错误', '未链接服务！', 'error');
                    return;
                } else {
                    doPrintNew(fi.fname, fi.isnew, jpaids, tp);
                }
            }
        }, tp == 3);
    }

    this.doPrintNew = doPrintNew;
    function doPrintNew(mdfname, isnew, jpaids, tp) {
        var url = _serUrl + "/web/common/getFrxPrintData.co";
        var data = {jpaclass: frmOptions.JPAClass, mdfname: mdfname, jpaids: jpaids};
        $ajaxjsonpost(url, JSON.stringify(data), function (printdata) {
            printdata.serUrl = _serUrl;//可能需要这个下载 上传文件
            printdata.jpaclass = frmOptions.JPAClass;//可能打印控件需要这个获取或上传打印模板
            printdata.mdfname = mdfname;
            printdata.action = tp;//1 打印 2 预览 3 编辑
            printdata.isnew = (isnew) ? true : false;
            var doprintaction = true;
            if (frmOptions.BeforePrint) {
                doprintaction = frmOptions.BeforePrint(printdata);
            }
            if (doprintaction)
                _wbsocketclient.callAction("doPrintFastReport", printdata, function (msg) {
                });
        }, function (err) {
            alert(err.errmsg);
        });
    }


    function dopostprint(url, jpaclass, ids, fname, tp) {
        var jsdata = {};
        jsdata.jpaclass = jpaclass;
        jsdata.modfilename = fname;
        jsdata.jpaids = ids;
        jsdata.tp = tp;
        jsdata.fields = getExptColsEx();
        var wd = $("#downloadfile_form");
        if (wd.length <= 0) {
            $("<form id='downloadfile_form' style='display:none'><input type='hidden' name='_pjdata'/></form>").appendTo("#common_divs_id");
            var wd = $("#downloadfile_form");
        }
        $("#downloadfile_form input").val(JSON.stringify(jsdata));
        wd.attr("target", "");
        wd.attr("action", url);
        wd.attr("method", "post");
        wd.submit();
        wd.remove();
    }

    //导出列表
    this.doexportlst = doexportlst;
    function doexportlst() {
        if (!_perparms) {
            $.messager.alert('错误', '请先查询数据!', 'error');
            return;
        }
        var actionformatter = function (value, row, index) {
            //var choiceh = "<span class='corsair_link_button schmactionbutton' actiontype='1' jesid='" + row.jesid + "'>选用</span>";
            //var setpublic = "<span class='corsair_link_button schmactionbutton' actiontype='2' jesid='" + row.jesid + "'>公开</span>";
            //var setprivate = "<span class='corsair_link_button schmactionbutton' actiontype='3' jesid='" + row.jesid + "'>私藏</span>";
            var delh = "<span class='corsair_link_button schmactionbutton' actiontype='4' jesid='" + row.jesid + "'>删除</span>";
            var isown = parseInt(row.isown);
            //var ispublic = parseInt(row.ispublic);
            var rst = "";//choiceh;
            if (isown == 1) {
                //rst = rst + ((ispublic == 1) ? setprivate : setpublic);
                rst = rst + delh;
            }
            return rst;
        };
        var yesnoformatter = function (value, row, index) {
            var setpublic = "<span class='corsair_link_button schmactionbutton' actiontype='2' jesid='" + row.jesid + "'>公开</span>";
            var setprivate = "<span class='corsair_link_button schmactionbutton' actiontype='3' jesid='" + row.jesid + "'>私藏</span>";
            var isown = parseInt(row.isown);
            var v = parseInt(value);
            if (v == 1) {
                if (isown == 1) {
                    return "是(" + setprivate + ")";
                } else return "是";
            }
            if (v == 2) {
                if (isown == 1) {
                    return "否(" + setpublic + ")";
                } else
                    return "否";
            }

        };

        var setWindow = $("#exportpw_id");
        var fieldlist_grid = setWindow.find(".export-fieldlist");
        var schema_grid = setWindow.find(".export-schema");
        var fieldlist_fields = [
            {field: 'chked', checkbox: true},
            {field: 'title', title: '字段', width: 100}
        ];

        var schema_fields = [
            {field: 'jesname', title: '方案', width: 80},
            {field: 'ispublic', title: '公开', width: 80, formatter: yesnoformatter},
            {field: 'owndisplayname', title: '用户', width: 80},
            {field: 'action', title: '操作', width: 100, formatter: actionformatter}
        ];
        fieldlist_grid.datagrid({
            columns: [fieldlist_fields],
            onCheck: function (index, row) {
                fieldlist_grid.datagrid("unselectAll");
                fieldlist_grid.datagrid("selectRow", index);
            },
            data: createFitlters(defaultListGridColumsn)
        });
        fieldlist_grid.datagrid("checkAll");

        function loadschema() {
            var url = _serUrl + "/web/common/getJPAExportSchm.co?jpaclass=" + frmOptions.JPAClass;
            $ajaxjsonget(url, function (jsdata) {
                var schema_data = jsdata;
                schema_grid.datagrid({
                    columns: [schema_fields],
                    data: schema_data,
                    onClickRow: function (index, row) {
                        var fds = eval(row.expfields);
                        setFieldsChkByFds(fds);
                    }
                });
                setWindow.find(".schmactionbutton").click(function (e) {
                    var o = $(e.target);
                    var jesid = o.attr("jesid");
                    var sm = getschmbyid(jesid);
                    var actiontype = parseInt(o.attr("actiontype"));
                    if (actiontype == 1) {//选用方案
                        var fds = eval(sm.expfields);
                        setFieldsChkByFds(fds);
                    } else {
                        var pdata = {
                            jesid: jesid,
                            edittype: actiontype
                        };
                        var url = _serUrl + "/web/common/editJPAExportSchm.co";
                        if ((actiontype == 2) || (actiontype == 3)) {//公开方案 //私藏方案
                            $ajaxjsonpost(url, JSON.stringify(pdata), function (jsdata) {
                                loadschema();
                            }, function (err) {
                                alert(err.errmsg);
                            });
                        }
                        if (actiontype == 4) {//删除方案
                            $.messager.confirm('确认', '确认删除？', function (r) {
                                if (r) {
                                    $ajaxjsonpost(url, JSON.stringify(pdata), function (jsdata) {
                                        loadschema();
                                    }, function (err) {
                                        alert(err.errmsg);
                                    });
                                }
                            });
                        }
                    }
                    e.stopImmediatePropagation();
                    return false;
                    //  alert(actiontype + " " + jesid);
                });
                function getschmbyid(jesid) {
                    for (var i = 0; i < schema_data.length; i++) {
                        var sm = schema_data[i];
                        if (sm.jesid == jesid) {
                            return sm;
                        }
                    }
                }
            }, function (err) {
                alert(err.errmsg);
            });
        }

        loadschema();

        setWindow.find("a[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            if (co.caction == "act_ok") {
                el.onclick = function () {
                    var chks = fieldlist_grid.datagrid("getChecked");
                    if (chks.length == 0) {
                        alert("没有选择导出字段");
                        return;
                    }
                    var expflds = [];
                    var fcls = getExptCols();
                    for (var i = 0; i < fcls.length; i++) {
                        var fd = fcls[i];
                        if ($.isArray(fd)) {
                            for (var j = 0; j < fd.length; j++) {
                                if (isexistsfield2(fd[j].field, chks)) {
                                    expflds.push(fd[j]);
                                }
                            }
                        } else if (isexistsfield2(fd.field, chks)) {
                            expflds.push(fd);
                        }
                    }
                    _perparms.cols = expflds;
                    if (!extParms) extParms = {};
                    _perparms.totals = extParms.totals;
                    if (afrmOptions.onExport) {
                        //afrmOptions.onExport(_perparms);
                        if (!afrmOptions.onExport(_perparms)) return;
                        //if (wfcls)_perparms.cols = wfcls;
                    }
                    var url = findUrl;
                    var wd = $("#downloadfile_form");
                    if (wd.length <= 0) {
                        $("<form id='downloadfile_form' style='display:none'><input type='hidden' name='_pjdata'/></form>").appendTo("#common_divs_id");
                        var wd = $("#downloadfile_form");
                    }
                    $("#downloadfile_form input").val(JSON.stringify(_perparms));
                    wd.attr("target", "");
                    wd.attr("action", url);
                    wd.attr("method", "post");
                    wd.submit();
                    wd.remove();
                    setWindow.window('close');
                };
            }
            if (co.caction == "act_cancel") {
                el.onclick = function () {
                    setWindow.window('close');
                }
            }
            if (co.caction == 'act_saveas') {
                el.onclick = function () {
                    var chks = fieldlist_grid.datagrid("getChecked");
                    if (chks.length == 0) {
                        alert("没有选择导出字段");
                        return;
                    }
                    var fields = [];
                    for (var i = 0; i < chks.length; i++) {
                        var chk = chks[i];
                        fields.push(chk.field);
                    }
                    $.messager.prompt('保存', '请输入方案名称：', function (r) {
                        if (r) {
                            if ($isEmpty(r)) {
                                alert("需要输入名称");
                                return;
                            }
                            var postdata = {
                                jpaclass: frmOptions.JPAClass,
                                jpaname: $(document).attr("title"),
                                fields: JSON.stringify(fields),
                                jesname: r,
                                ispublic: 2
                            };
                            var url = _serUrl + "/web/common/saveJPAExportSchm.co";
                            $ajaxjsonpost(url, JSON.stringify(postdata), function (jsdata) {
                                loadschema();
                                //setWindow.window('close');
                            }, function (err) {
                                alert(err.errmsg);
                            });
                        }
                    });
                }
            }
        });

        function createFitlters(fields) {
            var rows = [];
            var idx = 0;
            for (var i = 0; i < fields.length; i++) {
                for (var j = 0; j < fields[i].length; j++) {
                    var row = {};
                    row.sidx = idx++;
                    row.field = fields[i][j].field;
                    row.title = fields[i][j].title;
                    rows.push(row);
                }
            }
            return rows;
        }

        function setFieldsChkByFds(fds) {
            fieldlist_grid.datagrid("uncheckAll");
            var rows = fieldlist_grid.datagrid("getRows");
            for (var i = rows.length - 1; i >= 0; i--) {
                var row = rows[i];
                var field = row.field;
                if (isexitsfield(field, fds)) {
                    fieldlist_grid.datagrid("checkRow", i);
                }
            }
        }

        function isexistsfield2(field, chks) {
            for (var i = 0; i < chks.length; i++) {
                //alert(JSON.stringify(chks[i]) + "     " + field);
                if (chks[i].field == field)
                    return true;
            }
        }

        function isexitsfield(field, fds) {
            for (var i = 0; i < fds.length; i++) {
                if (fds[i] == field)
                    return true;
            }
        }

        setWindow.window("open");
    }

    //导出列表
    this.doexportlst1 = doexportlst1;
    function doexportlst1() {
        if (!_perparms) {
            $.messager.alert('错误', '请先查询数据!', 'error');
            return;
        }
        var fcls = getExptCols();
        _perparms.cols = fcls;
        if (!extParms) extParms = {};
        _perparms.totals = extParms.totals;
        if (afrmOptions.onExport) {
            if (!afrmOptions.onExport(_perparms)) return;
            // afrmOptions.onExport(_perparms);
            //if (wfcls)_perparms.cols = wfcls;
        }

        var url = findUrl;
        var wd = $("#downloadfile_form");
        if (wd.length <= 0) {
            $("<form id='downloadfile_form' style='display:none'><input type='hidden' name='_pjdata'/></form>").appendTo("#common_divs_id");
            var wd = $("#downloadfile_form");
        }
        $("#downloadfile_form input").val(JSON.stringify(_perparms));
        wd.attr("target", "");
        wd.attr("action", url);
        wd.attr("method", "post");
        wd.submit();
        wd.remove();
    }

    function getExptColsEx() {
        var rst = {};
        var fds = [];
        var fcls = listGridColumns.concat();
        for (var i = 0; i < fcls.length; i++) {
            var col = fcls[i];
            if (col) {
                if ($.isArray(col)) {
                    for (var j = 0; j < col.length; j++) {
                        var coll = col[j];
                        if (coll && (coll.field)) {
                            parsetcolfrmatparms(coll);
                            fds.push(coll);
                        }
                    }
                } else {
                    if (col.field) {
                        parsetcolfrmatparms(col);
                        fds.push(col);
                    }
                }
            }
        }
        rst.cols = fds;
        if (frmOptions.gdLinesColumns) {
            var lfcls = frmOptions.gdLinesColumns.concat();
            for (var i = 0; i < lfcls.length; i++) {
                var col = lfcls[i];
                if (col) {
                    if ($.isArray(col)) {
                        for (var j = 0; j < col.length; j++) {
                            var coll = col[j];
                            if (coll && (coll.field)) {
                                parsetcolfrmatparms(coll);
                            }
                        }
                    } else {
                        if (col.field) {
                            parsetcolfrmatparms(col);
                        }
                    }
                }
            }
            if (!rst.linecols)
                rst.linecols = {};
            rst.linecols[gdLinesName] = lfcls;
        }
        return rst;
    }

    function getExptCols() {
        var fcls = listGridColumns.concat();
        for (var i = 0; i < fcls.length; i++) {
            var col = fcls[i];
            if (col) {
                if ($.isArray(col)) {
                    for (var j = 0; j < col.length; j++) {
                        var coll = col[j];
                        if (coll && (coll.field)) {
                            parsetcolfrmatparms(coll);
                        }
                    }
                } else {
                    if (col.field) {
                        parsetcolfrmatparms(col);
                    }
                }
            }
        }
        return fcls;
    }

    function doownloadfff(iframe, _perparms) {
        var frameDoc = iframe.contentDocument || iframe.contentWindow.document;
        var frm = frameDoc.getElementById("downloadform");
        //alert(frm);
    }

    function parsetcolfrmatparms(col) {
        if (col.formatter) {
            var comurl = col.formatter("get_com_url");
            if (comurl) {
                col.formatparms = {
                    valueField: comurl["valueField"],
                    textField: comurl["textField"],
                    multiple: comurl["multiple"],
                    jsondata: comurl["jsondata"]
                };
            }
            if (col.formatter == $fieldDateFormatorYYYY_MM_DD) {
                col.formatparms = {
                    fttype: "date"
                }
            }
        }
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
                dosave(false);//上传附件后 需要保存对象
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

    this.setisloadingdata = function (value) {
        isloadingdata = value;
    };

    this.setMainData = setMainData;
    function setMainData(jsdata) {
        //alert(setMainData);
        curMainData = jsdata;
        showDetail();
    }

    this.getMainData = getMainData;
    function getMainData() {
        if (!curMainData)curMainData = {};
        curMainData = JSONBandingFrm.toJsonData(els_all, curMainData, isnew);
        //console.error(JSON.stringify(curMainData));
        if ((formtype == TFrmType.ftMainLine) && gdLinesName) {
            $C.grid.accept("#detail_main_grid_id");
            var lines = $("#detail_main_grid_id").datagrid("getRows");
            curMainData[gdLinesName] = lines;
            for (var i = 0; i < added_grids.length; i++) {
                try {
                    var gridop = added_grid_ids[i];
                    $C.grid.accept(gridop.filter);
                    var lines = $(gridop.filter).datagrid("getRows");
                    curMainData[gridop.gdLinesName] = lines;
                } catch (e) {
                    console.error(e);
                }
            }
        }

        //将maindata_id 下面 grid 根据属性 fdname 与 数据属性同名进行赋值
        $("#maindata_id").find("table[cjoptions]").each(function (index, el) {
            var obj = $(this);
            var co = $.c_parseOptions(obj);
            if (co.readonly) {
                //
            }
            var fdname = co.fdname;
            if (fdname) {
                var cls = obj.attr("class");
                if (cls)
                    cls = cls + obj.attr("cjoptions");
                else
                    cls = obj.attr("cjoptions");
                if (cls.indexOf("easyui-treegrid") != -1) {
                    curMainData[fdname] = obj.treegrid("getRoots");
                }
                if (cls.indexOf("easyui-datagrid") != -1) {
                    curMainData[fdname] = obj.datagrid("getRows");
                }
            }
        });
        return curMainData;
    }

    this.getSelectedLine = getSelectedLine;
    function getSelectedLine(filter) {
        var go = (filter == undefined) ? $("#detail_main_grid_id") : $(filter);
        return go.datagrid("getSelected");
    }

    this.removeSelectLine = removeSelectLine;
    function removeSelectLine(filter) {
        var go = (filter == undefined) ? $("#detail_main_grid_id") : $(filter);
        var row = go.datagrid("getSelected");
        if (row) {
            var idx = go.datagrid("getRowIndex", row);
            go.datagrid("deleteRow", idx);
            setDataChanged(true, "删除选定行" + filter);
        }
    }

    this.addLineData = addLineData;
    function addLineData(filter, row) {
        var go = (filter == undefined) ? $("#detail_main_grid_id") : $(filter);
        go.datagrid("appendRow", row);
        setDataChanged(true, "添加行" + filter);
    }

    this.updateLineData = updateLineData;
    function updateLineData(filter, rowdata) {
        var go = (filter == undefined) ? $("#detail_main_grid_id") : $(filter);
        var row = go.datagrid("getSelected");
        if (row) {
            var idx = go.datagrid("getRowIndex", row);
            go.datagrid("updateRow", {
                index: idx,
                row: rowdata
            });
            go.datagrid("refreshRow", idx);
            setDataChanged(true, "updateLineData:" + filter);
        }
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

    this.setAllReadOnly = setAllReadOnly;
    function setAllReadOnly(readonly) {
        JSONBandingFrm.setAllInputReadOnly(els_all, readonly);
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

    this.reSetInputReadOnly = reSetInputReadOnly;
    function reSetInputReadOnly() {
        for (var i = 0; i < els_all.length; i++) {
            //var co = $.c_parseOptions($(els_all[i]));
            var co = els_all[i].cop;
            if ((!co.readonly) || edittps.isedit) {
                JSONBandingFrm.setInputReadOnly(els_all[i], false);
                //setInputReadOnly($(els_all[i]), false);
            } else {
                JSONBandingFrm.setInputReadOnly(els_all[i], true);
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

    this.getField = getField;
    function getField(fdname) {
        //alert(els_all.length);
        for (var i = 0; i < els_all.length; i++) {
            var co = els_all[i].cop;
            if (co.fdname == fdname) {
                return $(els_all[i]);
            }
        }
    }

    this.getFieldCount = getFieldCount;
    function getFieldCount() {
        return els_all.length;
    }

    this.getFieldByIndex = getFieldByIndex;
    function getFieldByIndex(idx) {
        return $(els_all[idx]);
    }

    this.getLabel = getLabel;
    function getLabel(fdname) {
        return JSONBandingFrm.getLabel(fdname);
    }

    this.setFieldHide = setFieldHide;
    function setFieldHide(fdname) {
        JSONBandingFrm.setFieldHide(fdname);
    }

    this.setFieldShow = setFieldShow;
    function setFieldShow(fdname) {
        JSONBandingFrm.setFieldShow(fdname);
    }

    this.setFieldValue = function (fdname, value) {
        issetEditValue = true;
        try {
            var odv = curMainData[fdname];
            if (!JSONBandingFrm.setFieldValue(fdname, value)) {//单独设置变更事件
            }
            curMainData[fdname] = value;
        } catch (err) {
            alert(err.message);
        } finally {
            issetEditValue = false;
        }
    };

    this.setFeildCaption = function (fdname, caption) {
        JSONBandingFrm.setFeildCaption(fdname, caption);
    };

    this.getInput = getInput;
    function getInput(fdname) {
        return JSONBandingFrm.getInput(fdname);
    }

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

    this.setDataChanged = setDataChanged;
    function setDataChanged(value, mask) {
        datachanged = value;
        if (datachanged) {
            TButtons.btSave.enable();
        }
    }


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
            return edittps.isedit;
        } else if (stat == 9) {
            return edittps.isupdate;
        } else
            return false;
        return false;
    }

    /*
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
     */
    function onValueChange(fdname, newValue, oldValue, newRowValue, ele) {
        if (oldValue != newValue) {
            if ((afrmOptions.onEditChanged) && (!isloadingdata)) {//&& (!issetEditValue)
                afrmOptions.onEditChanged(fdname, newValue, oldValue, newRowValue, ele);
            }
            if ((mainline) && (!isloadingdata)) {
                mainline.setDataChanged(true, "onValueChange:" + fdname);
            }
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


    function setColFilter() {
        $showGridColFilter($("#dg_datalist_id"), defaultListGridColumsn, listGridColumns, "dg_datalist_id", function (colparms) {

        }, function () {

        });
    }

    this.setwfbt = setwfbt;
    function setwfbt(id, value) {
        $(id).linkbutton(value);
    }

    this.appendInputArray = appendInputArray;
    function appendInputArray(vfilter) {
        var els = JSONBandingFrm.appendInputArray(vfilter);
        els_all = els.els_all;
        els_readonly = els.els_readonly;
        lables = els.els_lables;
    }

    this.reGetInputArray = reGetInputArray;
    function reGetInputArray(vfilter) {
        var els = JSONBandingFrm.regetInputArray(vfilter);
        els_all = els.els_all;
        els_readonly = els.els_readonly;
        lables = els.els_lables;
    }

    function wfbtaction(tp) {
        document.getElementById('main_tab_wf_id').scrollIntoView();
        var wd = $("#main_tab_wf_id iframe");
        var iframe = wd[0];
        if (!iframe) return;
        var pw = iframe.contentWindow;
        if (pw && pw.btaction) {
            // main_tab_wf_div
            pw.btaction(tp);
        }
    }

    function doPrevRecord() {
        var obj = $("#dg_datalist_id");
        var idx = getCurIdx(obj);
        if (idx <= 0) return;
        idx--;
        showidx(obj, idx);
    }

    function doNextRecord() {
        var obj = $("#dg_datalist_id");
        var idx = getCurIdx(obj);
        if (idx == -1) return;
        idx++;
        if (idx > (obj.datagrid("getRows").length - 1)) return;
        showidx(obj, idx);
    }

    function showidx(obj, idx) {
        obj.datagrid("unselectAll");
        obj.datagrid("selectRow", idx);
        var row = obj.datagrid("getSelected");
        if (!row) return;
        findDetail(row[idfield]);
    }

    function getCurIdx(obj) {
        var id = curMainData[idfield];
        if (!id)return -1;
        var row = getRowByID(id);
        if (!row)return -1;
        var idx = obj.datagrid("getRowIndex", row);
        return idx;
    }
}