/**
 * Created by Administrator on 2015-01-07.
 */
var creport = undefined;
$(document).ready(function () {
        creport = new TCreport(prtOptions);
    }
);

function TCreport(prtOptions) {
    var finddlg = undefined;//查询窗口对象
    var prtOptions = prtOptions;
    var gdListColumns = undefined;
    var defaultListGridColumsn = undefined;//默认的
    var showChartChoicetor = false;
    this.prtOptions = prtOptions;
    var pagination = (prtOptions.pagination == undefined) ? true : prtOptions.pagination;
    var allow_colfilter = (prtOptions.allow_colfilter == undefined) ? true : prtOptions.allow_colfilter;
    if (prtOptions.comUrls)
        $C.grid.initComFormaters({
            comUrls: prtOptions.comUrls, onOK: function () {
                getHtmTemp();
            }
        });
    else
        getHtmTemp();

    function getHtmTemp() {
        if (prtOptions.beforeInitUI) {
            var newOptions = prtOptions.beforeInitUI();
            if (newOptions)
                prtOptions = $.extend(prtOptions, newOptions);
        }
        //alert(prtOptions.istree);
        if (prtOptions.istree) {
            var url = (prtOptions.htmlTempt && (prtOptions.htmlTempt.length > 0)) ? prtOptions.htmlTempt : "../templet/default/reporttree.html";
        }
        else {
            var url = (prtOptions.htmlTempt && (prtOptions.htmlTempt.length > 0)) ? prtOptions.htmlTempt : "../templet/default/report.html";
        }
        $.ajax({
            url: url,
            type: 'get',
            dataType: 'text',
            cache: false,
            async: true,
            contentType: "text/HTML; charset=utf-8",
            success: function (data) {
                $(data).appendTo("body");
                if (prtOptions.afterLoadModel) {
                    prtOptions.afterLoadModel(); //加载完模板，没有渲染
                }
                $.parser.parse();
                initUI();
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                $.messager.alert('错误', '获取模板文件错误!', 'error');
            }
        });
    }

    this.extendOptions = extendOptions;
    function extendOptions(newOptions) {
        prtOptions = $.extend(prtOptions, newOptions);
        initgrid();
    }

    function initgrid() {
        if (!prtOptions.gdListColumns) {
            $.messager.alert('错误', '没有设置列表页字段!', 'error');
        }
        if (typeof prtOptions.gdListColumns == 'function') {
            gdListColumns = prtOptions.gdListColumns();
        } else {
            gdListColumns = prtOptions.gdListColumns;
        }

        if (gdListColumns) {//兼容两种列定义方式
            var col1 = gdListColumns[0];
            gdListColumns = ($.isArray(col1)) ? gdListColumns : [gdListColumns];
        }
        defaultListGridColumsn = $.extend(true, [], gdListColumns);//复制一个默认的
        gdListColumns = $InitGridColFields(gdListColumns, "dg_datalist_id");

        var showFooter = (prtOptions.showFooter == undefined) ? false : prtOptions.showFooter;
        if (prtOptions.istree) {
            $("#dg_datalist_id").treegrid({
                border: false,
                showFooter: showFooter,
                fitColumns: (prtOptions.fitColumns == undefined) ? false : prtOptions.fitColumns,
                idField: prtOptions.idField,
                treeField: prtOptions.treeField,
                columns: gdListColumns
            });
        } else {
            $("#dg_datalist_id").datagrid({
                columns: [[]]
            });
            $("#dg_datalist_id").datagrid({
                border: false,
                showFooter: showFooter,
                fitColumns: (prtOptions.fitColumns == undefined) ? false : prtOptions.fitColumns,
                columns: gdListColumns
            });
            if (pagination)
                inipageins("#dg_datalist_id");
        }
        if (prtOptions.afterInitGrid) {
            prtOptions.afterInitGrid();
        }
    }

    function initUI() {
        $("#id_bt_exit").remove();
        if (!allow_colfilter)
            $("#id_bt_gridcolfilter").remove();
        initgrid();
        if ("function" != typeof TFindDialog) {
            alert("需要引入cFindDlg.min.js文件");
            return;
        }
        finddlg = new TFindDialog({
            width: prtOptions.findwidth,
            height: prtOptions.findheight,
            findeditable: false,
            findFields: prtOptions.findExtParms,
            onOK: function (parms) {
                return onFindWindowOK(parms);
            }
        });

        if (prtOptions.findOptionsFilter) {
            if ($(prtOptions.findOptionsFilter).length > 0) {
                var panel = finddlg.getDlgLayout().layout("panel", "center");
                panel.panel({
                    title: false,
                    content: $(prtOptions.findOptionsFilter).html()
                });
            } else
                throw new error("【" + prtOptions.findOptionsFilter + "】没有元素");
        }

        var extButtons = (prtOptions.extButtons != undefined) ? prtOptions.extButtons : [];
        for (var i = 0; i < extButtons.length; i++) {
            var ebt = extButtons[i];
            var id = (ebt.id) ? ebt.id : "ebt" + $generateUUID();
            ebt.id = id;
            var hs = "<a id='" + id + "' href='javascript:void(0)' class='easyui-linkbutton' data-options=\"plain:true,iconCls:'" + ebt.iconCls + "'\">" + ebt.text + "</a>";
            if (ebt.posion) {
                $(hs).insertBefore($(ebt.posion));
            } else {
                //$("#id_bt_new").parent().append(hs);
                $(hs).insertAfter($("#id_bt_expt"));
            }
            $("#" + id).bind('click', ebt.handler);
        }
        if (extButtons.length > 0) {
            $.parser.parse($("#id_bt_expt").parent());
            // $.parser.parse($("#id_bt_save").parent());
        }

        $("#id_bt_find").linkbutton({
            onClick: function () {
                onClick($C.action.Find);
            }
        });
        $("#id_bt_expt").linkbutton({
            onClick: function () {
                onClick($C.action.Export);
            }
        });
        $("#id_bt_exptchart").linkbutton({
            onClick: function () {
                onClick($C.action.ExportChart);
            }
        });
        $("#id_bt_exit").linkbutton({
            onClick: function () {
                onClick($C.action.Exit);
            }
        });
        $("#id_bt_gridcolfilter").linkbutton({
            onClick: function () {
                onClick($C.action.ColsFilter);
            }
        });
        if (!prtOptions.allowChart || prtOptions.istree) {
            $("#main_tabs_id").tabs("hideHeader");
            $("#main_tabs_id").tabs("close", 1);
            $("#id_bt_exptchart").remove();
        }

        $("#find_window_id").find(".easyui-linkbutton[cjoptions]").each(function (index, el) {
            var co = $.c_parseOptions($(this));
            if (co.caction == "act_ok") {
                $(this).click(function () {
                    find_window_ok_click();
                });
            } else if (co.caction == "act_cancel") {
                $(this).click(function () {
                    $('#find_window_id').window('close');
                });
            }
        });

        showChartChoicetor = (prtOptions.showChartChoicetor == undefined) ? false : prtOptions.showChartChoicetor;
        if (!showChartChoicetor) {
            $("#chart_layout").layout("remove", "east");
        }

        if (prtOptions.afterInitUI) {
            var newOptions = prtOptions.afterInitUI();
            if (newOptions)
                prtOptions = $.extend(prtOptions, newOptions);
        }
        if (prtOptions.autoFind)
            do_find([]);
    }


    function inipageins(gdfilter) {
        var gd = $(gdfilter);
        if (prtOptions.pageList == undefined)
            prtOptions.pageList = [30, 50, 100, 300];
        gd.datagrid({
            // pageNumber: 1,
            pagination: true,
            rownumbers: true,
            pageSize: prtOptions.pageList[0],
            pageList: prtOptions.pageList
        });

        var p = gd.datagrid('getPager');
        p.pagination({
            beforePageText: '第',//页数文本框前显示的汉字
            afterPageText: '页    共 {pages} 页',
            displayMsg: '共{total}条数据'
        });
    }

    function onClick(action) {
        if (action == $C.action.Find) {
            doFind();
        }
        if (action == $C.action.Export) {
            doExport();
        }
        if (action == $C.action.Exit) {
            //alert("Exit");
        }
        if (action == $C.action.ExportChart) {
            doExportChart();
        }
        if (action == $C.action.ColsFilter) {
            setColFilter();
        }
    }

    function setColFilter() {
        var grd = $("#dg_datalist_id");
        $showGridColFilter(grd, defaultListGridColumsn, gdListColumns, "dg_datalist_id", function (colparms) {
            grd.datagrid({data: grd.datagrid("getRows")});
        }, function () {

        });
    }

    function doExportChart() {
        if ($("#placeholder").html() == "") {
            alert("没有图表数据");
            return;
        }
        $("#main_tabs_id").tabs("select", 1);
        var placeholder = document.getElementById("main_tab_chart_id");
        if (placeholder) {
            html2canvas(placeholder, {
                onrendered: function (canvas) {
                    var dataURL = canvas.toDataURL("image/jpeg");
                    console.error(dataURL);
                    var jsdata = {data: dataURL};
                    var url = _serUrl + "/web/common/getDataURLImg.co";
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
                }
            });
        } else {

        }
    }

    function doFind() {
        //var parms = (prtOptions.findExtParms) ? prtOptions.findExtParms.concat(prtOptions.gdListColumns()) : prtOptions.gdListColumns();
        if (prtOptions.findOptionsFilter) {
            finddlg.show();
            //$("#find_window_id").window("open");
        } else {
            var parms = (prtOptions.findExtParms);
            if ((parms != null) && (parms != undefined) && (parms.length > 0)) {
                finddlg.show();
            } else {
                do_find([]);
            }
        }
    }

    function onFindWindowOK(parms) {
        if (prtOptions.findOptionsFilter) {
            if (prtOptions.onGetFindParms) {
                var parms = prtOptions.onGetFindParms();
                do_find(parms);
                return true;
            } else
                throw new error("设置了【findOptionsFilter】属性，也必须设置【onGetFindParms】属性");
        } else {
            if (parms != undefined) {
                if (prtOptions.onFind)
                    prtOptions.onFind(parms);
                do_find(parms);
                return true;
            } else
                return false;
        }
    }

    /*
     function find_window_ok_click() {
     if (prtOptions.findOptionsFilter) {
     if (prtOptions.onGetFindParms) {
     var parms = prtOptions.onGetFindParms();
     do_find(parms);
     } else
     throw new error("设置了【findOptionsFilter】属性，也必须设置【onGetFindParms】属性");
     } else {
     $C.grid.accept("dg_find_window_parms_id");
     var parms = $("#dg_find_window_parms_id").datagrid("getData").rows;
     do_find(parms);
     }

     }
     */
    var extParms = {};
    var perurl = undefined;
    this.do_find = do_find;
    function do_find(parms) {
        for (var i = parms.length - 1; i >= 0; i--) {
            if ((!parms[i].parmname) || (parms[i].parmname.length == 0)) {
                parms.splice(i, 1);
            }
        }
        if (!prtOptions.coURL) {
            $.messager.alert('错误', '没有设置查询URL!', 'error');
            return;
        }
        var url = (prtOptions.coURL.indexOf("?") >= 0) ? prtOptions.coURL : prtOptions.coURL + "?1=1";
        if (parms.length > 0)
            url = url + "&parms=" + JSON.stringify(parms);
        url = url + "&dt=" + (new Date()).valueOf();
        url = encodeURI(url);
        _perparms = parms;
        if (prtOptions.istree) {
            do_getTreeData(url);
        } else {
            extParms.totals = getTotalInfos();
            //alert(JSON.stringify(extParms.totals));
            var gd = $("#dg_datalist_id");
            if (pagination) {
                var p = gd.datagrid("getPager");
                setOnListselectPage(url);
                var option = p.pagination("options");
                do_getdata(url, option.pageNumber, option.pageSize);
            } else {
                do_getdata(url, 1, 10000);
            }
        }
    }

    function getTotalInfos() {
        var totles = [];
        var fcls = gdListColumns;
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

    this.setOnListselectPage = setOnListselectPage;
    function setOnListselectPage(url) {
        var gd = $("#dg_datalist_id");
        var p = gd.datagrid("getPager");
        p.pagination({
            onSelectPage: function (pageNumber, pageSize) {
                do_getdata(url, pageNumber, pageSize);
            }
        });
    }

    function do_getTreeData(url) {
        perurl = url;
        $ajaxjsonget(url, function (jsdata) {
            lastLoadedData = jsdata;
            if (prtOptions.beforeLoadData) {
                prtOptions.beforeLoadData(jsdata);
            }
            $("#dg_datalist_id").treegrid("loadData", jsdata);
            if (prtOptions.afterLoadData) {
                prtOptions.afterLoadData(jsdata);
            }
            $("#find_window_id").window("close");
        }, function (err) {
            alert(err.errmsg);
        });
    }

    var lastLoadedData = undefined;
    this.getData = function () {
        return lastLoadedData;
    };

    function do_getdata(url, page, pageSize) {
        perurl = url;
        if (!extParms)extParms = {};
        $ajaxjsonpost(url + "&page=" + page + "&rows=" + pageSize, JSON.stringify(extParms),
            function (jsdata) {
                lastLoadedData = jsdata;
                if (prtOptions.beforeLoadData) {
                    prtOptions.beforeLoadData(jsdata, page, pageSize, url);
                }
                $("#dg_datalist_id").datagrid({pageNumber: page, pageSize: pageSize});
                setOnListselectPage(url);
                $("#dg_datalist_id").datagrid("loadData", jsdata);
                if (prtOptions.afterLoadData) {
                    prtOptions.afterLoadData(jsdata, page, pageSize, url);
                }
                if (prtOptions.allowChart && (!prtOptions.istree)) {
                    do_loadchart(jsdata);
                }
                $("#find_window_id").window("close");
            }, function (err) {
                alert(err.errmsg);
            });
    }

    var allChartData = undefined, checkedChartData = undefined, loadedChartOptions = undefined;
    this.getCheckedChartData = function () {
        return checkedChartData;
    };

    this.getChartOptions = function () {
        return loadedChartOptions;
    };


    function do_loadchart(jsdata) {
        $("#main_tabs_id").tabs("select", 1);
        var options = {
            lines: {
                show: true
            },
            points: {
                show: true
            },
            xaxis: {
                tickDecimals: 0,
                tickSize: 1
            }
        };
        if (prtOptions.onLoadChartOptions) {
            var uoptions = prtOptions.onLoadChartOptions(jsdata);
            if (uoptions) {
                options = uoptions;// $.extend(options, uoptions);
            }
        }
        var data = [];
        if (prtOptions.onLoadChartData) {
            data = prtOptions.onLoadChartData(jsdata);
        } else {
            alert("显示图表需要设置【onLoadChartData】方法");
            return;
        }
        allChartData = data;
        if (showChartChoicetor)
            buildChecker(allChartData);
        showChart(allChartData, options);
        $("#main_tabs_id").tabs("select", 0);
    }

    function showChart(sdata, options) {
        $.plot("#placeholder", sdata, options);
        checkedChartData = sdata;
        loadedChartOptions = options;
        if (prtOptions.afterShowChart) {
            prtOptions.afterShowChart();
        }
    }


    function buildChecker(data) {
        var choiceContainer = $("#chart_choices");
        choiceContainer.html("");
        $.each(data, function (key, val) {
            var id = "chart_choices_id_" + key;
            choiceContainer.append("<br/><input type='checkbox' name='" + key +
                "' checked='checked' id='" + id + "'/>" +
                "<label for='" + id + "'>" + val.label + "</label>");
        });
        choiceContainer.find("input").click(plotAccordingToChoices);
        function plotAccordingToChoices() {
            var ckeddata = [];
            var choiceContainer = $("#chart_choices");
            choiceContainer.find("input:checked").each(function () {
                var key = $(this).attr("name");
                if (key && allChartData[key]) {
                    ckeddata.push(allChartData[key]);
                }
            });
            showChart(ckeddata, loadedChartOptions);
        }
    }

    function doExport() {
        if (!perurl) {
            $.messager.alert('错误', '请先查询数据!', 'error');
            return;
        }

        var fcls = gdListColumns;
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

        var _perparms = {};
        _perparms.cols = fcls;
        if (extParms && extParms.totals)
            _perparms.totals = extParms.totals;
        if (prtOptions.onExport) {
            if (!prtOptions.onExport(_perparms)) return;
        }

        var url = perurl;
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
    }

    function parsetcolfrmatparms(col) {
        if (col.formatter) {
            var comurl = col.formatter("get_com_url");
            if (comurl) {
                var multiple = (comurl["multiple"] == undefined) ? false : comurl["multiple"];
                col.formatparms = {
                    valueField: comurl["valueField"],
                    textField: comurl["textField"],
                    jsondata: comurl["jsondata"],
                    multiple: multiple
                };
            }
            if (col.formatter == $fieldDateFormatorYYYY_MM_DD) {
                col.formatparms = {
                    fttype: "date"
                }
            }
        }
    }

    this.get_finddlg = get_finddlg;
    function get_finddlg() {
        return finddlg;
    }

}