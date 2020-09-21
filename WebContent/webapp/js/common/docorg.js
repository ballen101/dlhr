/**
 * Created by shangwen on 2016/3/1.
 */
var MENU_POP_TYPE = {
    Folder: 1,
    FileDiv: 2,
    FileItem: 3
};
var ACTION_COPY_CUT_TYPE = {
    Copy: 1,
    Cut: 2
};

var cur_files = [];
var selected_files = [];
var cur_menu_pop_type = 0;
var files_cur_folder = undefined;//当前文件列 所属文件夹
//cporct:复制还是剪切  dfdrid 目标文件夹id
var cpOrctInfo = {
    inpaste: false,
    rfdrid: undefined,//如果是剪切 剪切后需要刷新的文件夹
    cporct: undefined,//复制还是剪切
    sinfo: undefined,//复制的文件和文件夹
    dfdrid: undefined//目标文件夹id
};

function clearPaste() {
    cpOrctInfo.inpaste = false;
    cpOrctInfo.rfdrid = undefined;
    cpOrctInfo.cporct = undefined;
    cpOrctInfo.sinfo = undefined;
    cpOrctInfo.dfdrid = undefined;
}

F_M_T = {
    REFRESH: "刷新",
    DOWONLOAD: "下载",
    UPLOAD: "上传",
    COPY: "复制",
    CUT: "剪切",
    PASTE: "粘贴",
    RENAME: "重命名",
    DEL: "删除",
    NEW: "新建",
    SAHRE: "共享",
    ACL: "权限",
    TEST: "测试",
    PROPERTY: "属性"
};
var FOLDER_MENU_DATA = [
    //{iconCls: "", text: F_M_T.TEST, onclick: dotest},
    {iconCls: "icon-reload", text: F_M_T.REFRESH, onclick: doRefreshMenuClick},
    {separator: true},
    {iconCls: "", text: F_M_T.NEW, onclick: newFolder},
    {iconCls: "icon-ml_upload", text: F_M_T.UPLOAD, onclick: uploadfiles},
    {iconCls: "", text: F_M_T.RENAME, onclick: doRenameClick},
    {separator: true},
    {iconCls: "icon-ml_download", text: F_M_T.DOWONLOAD, onclick: doDownLoadClick},
    {separator: true},
    {iconCls: "icon-copy", text: F_M_T.COPY, onclick: doCopyClick},
    {iconCls: "icon-cut", text: F_M_T.CUT, onclick: doCutClick},
    {iconCls: "icon-paste", text: F_M_T.PASTE, onclick: doPasteClick},
    {iconCls: "icon-ml_del", text: F_M_T.DEL, onclick: doRemoveClick},
    //{separator: true},
    //{iconCls: "", text: F_M_T.SAHRE},
    {separator: true},
    {iconCls: "", text: F_M_T.ACL, onclick: getFolderAcl},
    {iconCls: "icon-ml_detail", text: F_M_T.PROPERTY, onclick: getFolderAttr}
];

function setFolderMenuAllDisable() {
    $.each(F_M_T, function () {
        var item = $("#foldermenu").menu("findItem", this);
        if (item)
            $('#foldermenu').menu('disableItem', item.target);
    });
}

function setFolderMenuEanble(t) {
    var item = $("#foldermenu").menu("findItem", t);
    if (item)
        $('#foldermenu').menu('enableItem', item.target);
}

var SACL = {ADMIN: 0, WD: 1, W: 2, D: 3, R: 4, ROUTE: 5, REJECT: 6, EMPTY: 7};

function closes() {
    closed = true;
    $("#loading").fadeOut("normal", function () {
        $(this).remove();
    });
}
var pc;
var closed = false;
$.parser.auto = false;
$.parser.onComplete = function () {
    if (pc) clearTimeout(pc);
    pc = setTimeout(closes, 1000);
};

$().ready(function () {
    $.parser.parse();
    setTimeout(function () {
        if (!closed)
            closes();
    }, 3000);
    initGrids();
    initfldtree();
    initFolderMenu();
    initOrgUsers();
    initFileDivMenu();
});

var startX = 0;
var startY = 0;
var moveflag = false;
var mouseDownAndUpTimer = null;
var onMouseDownFlag = false;

function initFileDivMenu() {
    $("#filediv").bind('contextmenu', function (e) {
        e.preventDefault();
        onFileDivMenu(e);
    }).mousedown(function (e) {
        e.preventDefault();
        e.stopPropagation();
        onMouseDownFlag = false;
        mouseDownAndUpTimer = setTimeout(function () {
            //down
            $(".mouseSelect").css("width", "0px");
            $(".mouseSelect").css("height", "0px");
            $(".mouseSelect").css("display", "block");
            var offtp = $('#filediv').offset().top;
            var offleft = $('#filediv').offset().left;
            startX = e.pageX - offleft;// (e.originalEvent.x || e.originalEvent.layerX || 0);
            startY = e.pageY;// (e.originalEvent.y || e.originalEvent.layerY || 0) + offtp;
            $(".mouseSelect").css("left", startX + "px");
            $(".mouseSelect").css("top", startY + "px");
            moveflag = true;

            onMouseDownFlag = true;
        }, 200);
    }).mouseup(function (e) {
        e.preventDefault();
        e.stopPropagation();
        moveflag = false;
        $(".mouseSelect").css("display", "none");
        if (onMouseDownFlag) {
            //up
            // $(".mouseSelect").css("display", "none");
        } else {
            //click
            selected_files = [];
            resetActiveFiles();

            clearTimeout(mouseDownAndUpTimer); // 清除延迟时间
        }
    }).mousemove(function (e) {
        e.preventDefault();
        e.stopPropagation();
        if (moveflag) {
            var offtp = $('#filediv').offset().top;
            var offleft = $('#filediv').offset().left;
            var endX = e.pageX - offleft;// (e.originalEvent.x || e.originalEvent.layerX || 0);
            var endY = e.pageY;// (e.originalEvent.y || e.originalEvent.layerY || 0) + offtp;
            var left = (startX > endX) ? endX : startX;
            var top = (startY > endY) ? endY : startY;
            $(".mouseSelect").css("width", Math.abs(endX - startX) + "px");
            $(".mouseSelect").css("height", Math.abs(endY - startY) + "px");
            $(".mouseSelect").css("left", left + "px");
            $(".mouseSelect").css("top", top + "px");
            resetSelectFiles();
        }
    });
}

function resetSelectFiles() {
    selected_files = [];
    var rleft = $(".mouseSelect").position().left;
    var rtop = $(".mouseSelect").position().top;
    var rright = rleft + $(".mouseSelect").width();
    var rbuttom = rtop + $(".mouseSelect").height();

    var offtp = $('#filediv').offset().top;
    var offleft = $('#filediv').offset().left;
    for (var i = 0; i < cur_files.length; i++) {
        var item = cur_files[i].target;
        var ileft = item.position().left;
        var itop = item.position().top;
        var iright = ileft + item.width();
        var ibuttom = itop + item.height();
        if ((iright > rleft) && (ileft < rright) && (ibuttom > rtop) && (itop < rbuttom)) {
            selected_files.push(cur_files[i])
        }
    }
    resetActiveFiles();
}

function getFileByDomObj(domobj) {
    for (var i = 0; i < cur_files.length; i++) {
        if (cur_files[i].target[0] == domobj) {
            return cur_files[i];
        }
    }
    return null;
}

function isFileSelected(file) {
    for (var i = 0; i < selected_files.length; i++) {
        if (selected_files[i] == file) {
            return true;
        }
    }
    return false;
}

function setActiveFile(file) {
    selected_files = [file];
    resetActiveFiles();
}

function removeActiveFile(file) {
    for (var i = 0; i < selected_files.length; i++) {
        if (selected_files[i] == file) {
            selected_files.splice(i, 1);
            return;
        }
    }
}

function addActiveFile(file) {
    if (isFileSelected(file))
        return;
    selected_files.push(file);
}

function resetActiveFiles() {
    for (var i = 0; i < cur_files.length; i++) {
        var file = cur_files[i];
        var finded = false;
        for (var j = 0; j < selected_files.length; j++) {
            var stfile = selected_files[j];
            if (file == stfile) {
                finded = true;
                break;
            }
        }
        if (finded) {
            file.target.addClass("file_item_div_active");
        } else {
            file.target.removeClass("file_item_div_active");
        }
    }
}


function initFileItemMenu() {
    $(".file_item_div").unbind("contextmenu");
    $(".file_item_div").bind("contextmenu", function (e) {
        e.preventDefault();
        var file = getFileByDomObj(this);
        if (file == null) {
            alert("文件为空");
        }
        onFileDivMenu(e, file);
    });
    $(".file_item_div").mousedown(function (e) {
        e.preventDefault();
        e.stopPropagation();
        moveflag = false;
    });
    $(".file_item_div").mouseup(function (e) {
        e.preventDefault();
        e.stopPropagation();
    });
    $(".file_item_div").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var file = getFileByDomObj(this);
        if (e.ctrlKey) {
            if (isFileSelected(file)) {
                removeActiveFile(file);
            } else {
                addActiveFile(file);
            }
            resetActiveFiles();
        } else if (!isFileSelected(file)) {
            selected_files = [file];
            resetActiveFiles();
        }
    });

    $(".file_item_div").dblclick(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var file = getFileByDomObj(this);
        if (file.type == 1) {
            $("#foldergrid").treegrid("select", file.fdrid);
            showfolderfiles(file);
        }
        if (file.type == 2) {
            setActiveFile(file);
            var furl = $C.cos.downattfile() + "?pfid=" + file.pfid;
            window.open(furl);
        }
    });
}

function getfilebyid(pfid) {
    if ((cur_files == undefined) || (cur_files == null) || (cur_files.length == 0)) {
        return null;
    }
    for (var i = 0; i < cur_files.length; i++) {
        var file = cur_files[i];
        if (file.pfid == pfid) {
            return file;
        }
    }
    return null;
}

function initGrids() {
    $C.grid.initComFormaters({
        comUrls: [
            {
                index: "dic637",
                type: "combobox",
                url: _serUrl + "/web/dict/getdictvalues.co?dicid=637",
                valueField: 'dictvalue',
                textField: 'language1'
            },
            {
                index: "dic643",
                type: "combobox",
                url: _serUrl + "/web/dict/getdictvalues.co?dicid=643",
                valueField: 'dictvalue',
                textField: 'language1'
            }
        ],
        onOK: function () {
            $("#grid_shwfdracl").datagrid({
                columns: [[{field: 'ownername', title: '对象', width: 70},
                    {field: 'access', title: '权限', width: 60, formatter: $C.grid.comFormaters['dic637']},
                    {field: 'acltype', title: '类型', width: 70, formatter: $C.grid.comFormaters['dic643']},
                    {field: 'statime', title: '生效', width: 80},
                    {field: 'endtime', title: '失效', width: 80}]]
            });
            $("#cbx_access").combobox({
                valueField: 'dictvalue',
                textField: 'language1',
                data: $C.grid.comDatas['dic637'].data
            });
        }
    });
}

function initFolderMenu() {
    for (var i = 0; i < FOLDER_MENU_DATA.length; i++) {
        $('#foldermenu').menu('appendItem', FOLDER_MENU_DATA[i]
        );
    }
    $('#foldermenu').menu({onShow: onMenuShow, onHide: onMenuHide});
}

function onMenuShow() {

}

function onMenuHide() {
}

function initfldtree() {
    $("#foldergrid").treegrid({
        url: _serUrl + "/web/doc/getDocFolders.co",
        loadFilter: folderFilter,
        onClickRow: showfolderfiles,
        onContextMenu: onFolderMenu
    });
}

function aclInArr(acl, acls) {
    for (var i = 0; i < acls.length; i++) {
        if (acl == acls[i])
            return true;
    }
    return false;
}

function setFolderMenuIsEnableByAcl(acl) {
    setFolderMenuAllDisable();
    setFolderMenuEanble(F_M_T.TEST);
    setFolderMenuEanble(F_M_T.REFRESH);
    setFolderMenuEanble(F_M_T.DOWONLOAD);
    if ((cur_menu_pop_type == MENU_POP_TYPE.Folder) || (cur_menu_pop_type == MENU_POP_TYPE.FileItem))
        setFolderMenuEanble(F_M_T.COPY);
    if (aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.W, SACL.D])) {
        setFolderMenuEanble(F_M_T.UPLOAD);
        setFolderMenuEanble(F_M_T.RENAME);
        if ((cur_menu_pop_type == MENU_POP_TYPE.Folder) || (cur_menu_pop_type == MENU_POP_TYPE.FileItem))
            setFolderMenuEanble(F_M_T.CUT);
    }
    if (aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.W])) {
        if (cpOrctInfo.inpaste)
            setFolderMenuEanble(F_M_T.PASTE);
    }
    if (aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.D])) {
        setFolderMenuEanble(F_M_T.DEL);
    }
    if (aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.W])) {  //if ((aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.W])) && (cur_menu_pop_type == MENU_POP_TYPE.Folder)) {
        setFolderMenuEanble(F_M_T.NEW);
    }
    if (aclInArr(acl, [SACL.ADMIN])) {
        setFolderMenuEanble(F_M_T.ACL);
        setFolderMenuEanble(F_M_T.SAHRE);
    }
    setFolderMenuEanble(F_M_T.PROPERTY);
}

function setFileMenuIsEnableByAcl(acl) {
    setFolderMenuAllDisable();
    setFolderMenuEanble(F_M_T.TEST);
    setFolderMenuEanble(F_M_T.REFRESH);
    setFolderMenuEanble(F_M_T.DOWONLOAD);
    if ((cur_menu_pop_type == MENU_POP_TYPE.Folder) || (cur_menu_pop_type == MENU_POP_TYPE.FileItem))
        setFolderMenuEanble(F_M_T.COPY);
    if (aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.W, SACL.D])) {
        setFolderMenuEanble(F_M_T.UPLOAD);
        if (selected_files.length == 1)
            setFolderMenuEanble(F_M_T.RENAME);
        if ((cur_menu_pop_type == MENU_POP_TYPE.Folder) || (cur_menu_pop_type == MENU_POP_TYPE.FileItem))
            setFolderMenuEanble(F_M_T.CUT);
    }
    if (aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.W])) {
        if (cpOrctInfo.inpaste)
            setFolderMenuEanble(F_M_T.PASTE);
    }
    if (aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.D])) {
        setFolderMenuEanble(F_M_T.DEL);
    }

    if (aclInArr(acl, [SACL.ADMIN, SACL.WD, SACL.W])) {
        setFolderMenuEanble(F_M_T.NEW);
    }

    if (aclInArr(acl, [SACL.ADMIN])) {
        //setFolderMenuEanble(F_M_T.ACL);
        setFolderMenuEanble(F_M_T.SAHRE);
    }
    setFolderMenuEanble(F_M_T.PROPERTY);
}

function onFolderMenu(e, node) {
    cur_menu_pop_type = MENU_POP_TYPE.Folder;
    //$('#foldergrid').treegrid("select", node.fdrid);
    //showfolderfiles(node);
    showFolderMenu(e, node);
}

function onFileDivMenu(e, file) {
    e.stopPropagation();
    if (files_cur_folder == null) return;
    if (!file) {
        cur_menu_pop_type = MENU_POP_TYPE.FileDiv;
        showFolderMenu(e, files_cur_folder);
    } else {
        cur_menu_pop_type = MENU_POP_TYPE.FileItem;
        showFileMenu(e, file);
    }
}

function showFolderMenu(e, node) {
    var acl = parseInt(node.acl);
    setFolderMenuIsEnableByAcl(acl);
    e.preventDefault();
    $("#foldergrid").treegrid('select', node.fdrid);
    $('#foldermenu').menu('show', {
        left: e.pageX,
        top: e.pageY
    });
}

function showFileMenu(e, file) {
    var node = $('#foldergrid').treegrid('getSelected');
    if (!file.acl)
        file.acl = node.acl;

    if (!isFileSelected(file))
        setActiveFile(file);

    var acl = parseInt(file.acl);
    setFileMenuIsEnableByAcl(acl);
    e.preventDefault();
    $('#foldermenu').menu('show', {
        left: e.pageX,
        top: e.pageY
    });
}

function showfolderfiles(row) {
    files_cur_folder = row;
    $('#foldergrid').treegrid("expand", row.fdrid);
    $("#filediv").panel("setTitle", getrootnode("#foldergrid", row));
    var url = _serUrl + "/web/doc/getFolderDocs.co?fdrid=" + row.fdrid;
    $ajaxjsonget(url, function (jsdata) {
        cur_files = jsdata;
        showfiles();
    }, function (err) {
        alert(JSON.stringify(err));
    });
}

function showfiles() {
    $("#filediv").html("<div class='mouseSelect'></div>");
    if ((cur_files == undefined) || (cur_files == null) || (cur_files.length == 0)) {
        return;
    }
    var h = $("#html_temp_file_item").html();
    for (var i = 0; i < cur_files.length; i++) {
        var file = cur_files[i];
        if (file.type == 1) {
            var ht = $putStrParmValue(h, "pfid", file.fdrid);
            ht = $putStrParmValue(ht, "file_ico", "icon-file-folder32");
            ht = $putStrParmValue(ht, "displayfname", file.fdrname);
        }
        if (file.type == 2) {
            var ht = $putStrParmValue(h, "pfid", file.pfid);
            ht = $putStrParmValue(ht, "file_ico", $getfileicobyfileext(file.extname));
            ht = $putStrParmValue(ht, "displayfname", file.displayfname);
        }
        ht = $putStrParmValue(ht, "type", file.type);
        var tgt = $(ht);
        tgt.appendTo("#filediv");
        file.target = tgt;
    }
    initFileItemMenu();
}

function $putStrParmValue(src, parmname, value) {
    return src.replace(new RegExp("{{" + parmname + "}}", "g"), value);
}

function folderFilter(jsondata, parentId) {
    if (!parentId)
        parentId = 0;
    else
        parentId = parseInt(parentId);
    jsondata = setICONode(parentId, jsondata);
    return jsondata;
}

function setICONode(pid, jsondata) {
    if (pid == 0) {
        for (var i = 0; i < jsondata.length; i++) {
            var node = jsondata[i];
            //node.state = "closed";
            var fdtype = parseInt(node.fdtype);
            if (fdtype == 1) {
                node.iconCls = "icon-xietongmg";
            }
            if (fdtype == 2) {
                node.iconCls = "icon-folderuser";
            }
            if (fdtype == 4) {
                node.iconCls = "icon-reload";
            }
            if (fdtype == 3) {
                node.iconCls = "icon-folderlink";
            }
        }
    } else {
        for (var i = 0; i < jsondata.length; i++) {
            var node = jsondata[i];
            //node.state = "closed";
            var fdtype = parseInt(node.fdtype);
            if (fdtype == 1) {
                node.iconCls = "icon-folder";
            }
            if (fdtype == 2) {
                node.iconCls = "icon-folderuser";
            }
            if (fdtype == 4) {
                node.iconCls = "icon-folder";
            }
            if (fdtype == 3) {
                node.iconCls = "icon-folderlink";
            }
        }
    }
    return jsondata;
}

//新建文件夹
function newFolder() {
    var fd = $('#foldergrid').treegrid('getSelected');
    if (fd == null) {
        alert("选择文件夹");
        return;
    }
    $.messager.prompt('提示', '输入文件夹名称:', function (r) {
        if (r) {
            var id = fd.fdrid;
            var url = _serUrl + "/web/doc/createFloder.co";
            var data = {id: id, name: r};
            $ajaxjsonpost(url, JSON.stringify(data), function (rst) {
                $('#foldergrid').treegrid('append', {
                    parent: id,
                    data: [rst]
                });
            }, function (err) {
                alert(JSON.stringify(err));
            });
        }
    });
}

//删除文件夹 或文件
function doRemoveClick() {
    var node = $('#foldergrid').treegrid('getSelected');
    if (node == null) return;
    var dinfo = {};
    var cinfo = [];
    if (cur_menu_pop_type == MENU_POP_TYPE.Folder) {
        cinfo = [{type: 1, fdrid: node.fdrid}];
        var pnode = $("#foldergrid").treegrid("getParent", node.fdrid);
        dinfo.rfdrid = node.fdrid;
    } else if (cur_menu_pop_type == MENU_POP_TYPE.FileItem) {
        dinfo.rfdrid = node.fdrid;
        for (var i = 0; i < selected_files.length; i++) {
            var file = selected_files[i];
            if (file.type == 1) {//folder
                cinfo.push({
                    type: 1,
                    fdrid: file.fdrid
                });
            }
            if (file.type == 2) {//file
                cinfo.push({
                    type: 2,
                    pfid: file.pfid
                });
            }
        }
    } else return;
    if (cinfo.length == 0) return;
    dinfo.data = cinfo;
    $.messager.confirm('提示', '确定删除选中的【' + cinfo.length + '】项？', function (r) {
        if (r) {
            var id = node.fdrid;
            var url = _serUrl + "/web/doc/removeFolderOrFiles.co";
            $ajaxjsonpost(url, JSON.stringify(dinfo), function (rst) {
                if (cur_menu_pop_type == MENU_POP_TYPE.Folder) {
                    $('#foldergrid').treegrid('remove', node.fdrid);
                } else {
                    //刷新目录
                    $("#foldergrid").treegrid("reload", dinfo.rfdrid);
                    doRefreshMenuClick();
                }

            }, function (err) {
                alert(JSON.stringify(err));
            });
        }
    });
}

function getrootnode(filter, node) {
    var rst = "/";
    getsupernode(filter, node);
    return "." + rst;
    function getsupernode(filter, nd) {
        rst = "/" + nd.fdrname + rst;
        if (nd.superid == 0) {
            return nd;
        } else {
            var pnd = $(filter).treegrid("getParent", nd.fdrid);
            var root = getsupernode(filter, pnd);
            if (root) return root;
        }
    }
}


function dofindFolderACL() {
    var node = $('#foldergrid').treegrid('getSelected');
    $("#folder_alc_path").html(getrootnode("#foldergrid", node));
    var url = _serUrl + "/web/doc/getFolderAcled.co?atp=2&id=" + node.fdrid;
    $ajaxjsonget(url, function (jsdata) {
        $("#grid_shwfdracl").datagrid("loadData", jsdata);
    }, function (err) {
        alert("错误：" + JSON.stringify(err));
    });
    return node;
}

function getFolderAcl() {
    var node = dofindFolderACL();
    $("#wfolderalc").window({iconCls: node.iconCls});
    $("#wfolderalc").window("open");
}

function initOrgUsers() {
    $ajaxjsonget($C.cos.getorgs + "?type=orgusrtree", function (data) {
        $C.tree.setTree1OpendOtherClosed(data);
        setNodesIco(data);
        $("#tree_orguser").combotree("loadData", data);
        function setNodesIco(nodes) {
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                if (node.tp == "usr")
                    node.iconCls = "icon-user";
                if (node.children) {
                    setNodesIco(node.children);
                }
            }
        }
    }, function (err) {
        alert(err);
    });
}

function addacl() {
    //var ownerid = $('#tree_orguser').combotree("getValue");
    var data = {};
    var t = $('#tree_orguser').combotree('tree');	// get the tree object
    var n = t.tree('getSelected');		// get selected node
    if ((n == undefined) || (n == null)) {
        alert("选择机构或用户");
        return;
    }
    data.ownerid = n.id;
    if (n.tp == "usr")data.acltype = 3; else data.acltype = 1;
    data.access = $('#cbx_access').combobox("getValue");
    if ((data.access == undefined) || (data.access == null)) {
        alert("选择权限");
        return;
    }

    data.statime = $('#dt_statime').datebox("getValue");
    if ((data.statime == undefined) || (data.statime == null)) {
        alert("选择开始日期");
        return;
    }
    data.endtime = $('#dt_endtime').datebox("getValue");
    if ((data.endtime == undefined) || (data.endtime == null)) {
        alert("选择截止日期");
        return;
    }
    var node = $('#foldergrid').treegrid('getSelected');
    data.id = node.fdrid;
    var url = _serUrl + "/web/doc/setFolderAcl.co";
    var data = [data];
    $ajaxjsonpost(url, JSON.stringify(data), function (jsdata) {
        dofindFolderACL();
        $('#waddalc').window('close');
    }, function (err) {
        alert(JSON.stringify(err));
    });
}

function delacl() {
    var rows = $("#grid_shwfdracl").datagrid("getSelections");
    if (rows.length == null) {
        alert("没有选择的权限");
    }
    var pdata = [];
    for (var i = 0; i < rows.length; i++) {
        var row = rows[i];
        pdata.push({aclid: row.aclid});
    }
    var url = _serUrl + "/web/doc/removeFolderAcl.co";
    $ajaxjsonpost(url, JSON.stringify(pdata), function (json) {
        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var idx = $("#grid_shwfdracl").datagrid("getRowIndex", row);
            $("#grid_shwfdracl").datagrid("deleteRow", idx);
        }
    }, function (err) {
        alert(JSON.stringify(err));
    });
}

function getFolderAttr() {
    var node = $('#foldergrid').treegrid('getSelected');
    $("#folder_attr_path").html(getrootnode("#foldergrid", node));
    var url = _serUrl + "/web/doc/getFolderAttr.co?type=2&id=" + node.fdrid;
    $ajaxjsonget(url, function (jsdata) {
        if ((jsdata.size < 1024)) {
            var size = jsdata.size + "(K)";
        } else {
            var size = (jsdata.size / 1024).toFixed(2) + "(M)";
        }
        $("#attr_size").html(size);
        $("#attr_createtime").html(jsdata.createtime);
        $("#attr_creator").html(jsdata.creator);
        $("#attr_updatetime").html(jsdata.updatetime);
        $("#attr_updator").html(jsdata.updator);
        $("#attr_childfoldercount").html(jsdata.childfoldercount);
        $("#attr_childfilecount").html(jsdata.fcount);
    }, function (err) {
        alert("错误：" + JSON.stringify(err));
    });
    $("#wfolderattr").window({iconCls: node.iconCls});
    $("#wfolderattr").window("open");
}

function doRenameClick() {
    var node = $('#foldergrid').treegrid('getSelected');
    if (cur_menu_pop_type == MENU_POP_TYPE.Folder) {
        $.messager.prompt('提示', '输入新文件夹名称:', function (r) {
            if (r) {
                if (node.fdrname == r) {
                    alert("新旧文件同名");
                    return;
                }
                var id = node.fdrid;
                var url = _serUrl + "/web/doc/UpdateFloderName.co";
                var data = {id: id, name: r};
                $ajaxjsonpost(url, JSON.stringify(data), function (rst) {
                    $('#foldergrid').treegrid('update', {
                        id: id,
                        row: rst
                    });
                }, function (err) {
                    alert(JSON.stringify(err));
                });
            }
        });
        $('.messager-input').val(node.fdrname).focus();
    }
    if (cur_menu_pop_type == MENU_POP_TYPE.FileItem) {
        if (selected_files.length != 1) return;
        var file = selected_files[0];
        if (file.type == 2) {//文件重命名
            $.messager.prompt('提示', '输入新文件名称:', function (r) {
                if (r) {
                    if (file.displayfname == r) {
                        alert("新旧文件同名");
                        return;
                    }
                    var url = _serUrl + "/web/doc/UpdateFileName.co";
                    var data = {fdrid: node.fdrid, pfid: file.pfid, name: r};
                    $ajaxjsonpost(url, JSON.stringify(data), function (rst) {
                        file.displayfname = rst.displayfname;
                        showfiles();
                        setActiveFile(file);
                    }, function (err) {
                        alert(JSON.stringify(err));
                    });
                }
            });
            $('.messager-input').val(file.displayfname).focus();
        }
        if (file.type == 1) {//文件夹重命名
            $.messager.prompt('提示', '输入新文件夹名称:', function (r) {
                if (r) {
                    if (file.fdrname == r) {
                        alert("新旧文件同名");
                        return;
                    }
                    var id = file.fdrid;
                    var url = _serUrl + "/web/doc/UpdateFloderName.co";
                    var data = {id: id, name: r};
                    $ajaxjsonpost(url, JSON.stringify(data), function (rst) {
                        $('#foldergrid').treegrid('update', {
                            id: id,
                            row: rst
                        });
                        file.fdrname = rst.fdrname;
                        showfiles();
                        setActiveFile(file);
                    }, function (err) {
                        alert(JSON.stringify(err));
                    });
                }
            });
            $('.messager-input').val(file.fdrname).focus();
        }
    }
}

function uploadfiles() {
    var node = files_cur_folder;
    if (node == null) {
        alert("没有选择文件夹");
        return;
    }
    var url = _serUrl + "/web/doc/uploadfile.co?attImgThb=true&fdrid=" + node.fdrid;
    $uploadfile(url, null, function (err) {
        $.messager.alert('上传文件错误', err, 'error');
    }, function (jsdata) {
        try {
            var pfs = jsdata;
            if (pfs.errmsg) {
                alert(pfs.errmsg);
            } else {
                for (var i = 0; i < pfs.length; i++) {
                    pfs[i].type = 2;
                }
                cur_files = cur_files.concat(pfs);
                showfiles();
                $("#pw_uploadfile").window("close");
            }
        } catch (e) {
            if (JSON.stringify(jsdata).indexOf("Request Entity Too Large") > -1) {
                $.messager.alert('上传文件错误', "文件超大", 'error');
            } else {
                $.messager.alert('上传文件错误', JSON.stringify(jsdata), 'error');
            }
        }
    });
}


function doDownLoadClick() {
    var dinfo = [];
    if ((cur_menu_pop_type == MENU_POP_TYPE.Folder) || (cur_menu_pop_type == MENU_POP_TYPE.FileDiv)) {
        var node = $('#foldergrid').treegrid('getSelected');
        if (node == null) return;
        dinfo = [{type: 1, fdrid: node.fdrid}];
    } else if (cur_menu_pop_type == MENU_POP_TYPE.FileItem) {
        for (var i = 0; i < selected_files.length; i++) {
            var file = selected_files[i];
            if (file.type == 1) {//folder
                dinfo.push({
                    type: 1,
                    fdrid: file.fdrid
                });
            }
            if (file.type == 2) {//file
                dinfo.push({
                    type: 2,
                    pfid: file.pfid
                });
            }
        }
    } else return;
    var furl = _serUrl + "/web/doc/downloadFiles.co?dinfo=" + JSON.stringify(dinfo);
    window.open(furl);
}

//刷新
function doRefreshMenuClick() {
    var node = $('#foldergrid').treegrid('getSelected');
    if (node == null) {
        alert("没有选择文件夹");
        return;
    }
    if (cur_menu_pop_type == MENU_POP_TYPE.Folder) {
        $("#foldergrid").treegrid("reload", node.fdrid);
    }
    if ((cur_menu_pop_type == MENU_POP_TYPE.FileDiv) || (cur_menu_pop_type == MENU_POP_TYPE.FileItem)) {
        showfolderfiles(node);
    }
}

function dotest() {
    alert($getfileicobyfileext(".docx"));
}

function doCopyClick() {
    doCutOrCopy(ACTION_COPY_CUT_TYPE.Copy);
}

function doCutClick() {
    doCutOrCopy(ACTION_COPY_CUT_TYPE.Cut);
}

function getStdFids() {
    var rst = [];
    for (var i = 0; i < selected_files.length; i++) {
        rst.push({pfid: selected_files[i].pfid, fdrid: selected_files[i].fdrid});
    }
    return rst;
}

function doCutOrCopy(acttp) {
    clearPaste();
    var node = $('#foldergrid').treegrid('getSelected');
    if (node == null) return;
    var cinfo = [];
    if ((cur_menu_pop_type == MENU_POP_TYPE.Folder) || (cur_menu_pop_type == MENU_POP_TYPE.FileDiv)) {
        cinfo = [{type: 1, fdrid: node.fdrid}];
        var pnode = $("#foldergrid").treegrid("getParent", node.fdrid);
        cpOrctInfo.rfdrid = node.fdrid;
    } else if (cur_menu_pop_type == MENU_POP_TYPE.FileItem) {
        cpOrctInfo.rfdrid = node.fdrid;
        for (var i = 0; i < selected_files.length; i++) {
            var file = selected_files[i];
            if (file.type == 1) {//folder
                cinfo.push({
                    type: 1,
                    fdrid: file.fdrid
                });
            }
            if (file.type == 2) {//file
                cinfo.push({
                    type: 2,
                    pfid: file.pfid
                });
            }
        }
    } else return;
    cpOrctInfo.inpaste = true;
    cpOrctInfo.sinfo = cinfo;
    cpOrctInfo.cporct = acttp;
}

function doPasteClick() {
    if (!cpOrctInfo.inpaste)
        return;
    var node = $('#foldergrid').treegrid('getSelected');
    if (node == null) return;
    cpOrctInfo.dfdrid = node.fdrid;
    var url = _serUrl + "/web/doc/Paste.co";
    $ajaxjsonpost(url, JSON.stringify(cpOrctInfo), function (jsdata) {
            if ((cpOrctInfo.cporct == ACTION_COPY_CUT_TYPE.Cut) && (cpOrctInfo.rfdrid)) {
                $("#foldergrid").treegrid("reload", cpOrctInfo.rfdrid);
            }
            $("#foldergrid").treegrid("reload", cpOrctInfo.dfdrid);
            doRefreshMenuClick();
            clearPaste();
        },
        function () {
            alert(JSON.stringify(err));
        }
    );
}