/**
 * Created by Administrator on 2014-09-19.
 */

var _menus;

$(document).ready(function () {
        //gemenus();
    }
);

function gemenus() {
    var modid = $getPageParms().modid;
    $ajaxjsonget($C.cos.getmenusbymod() + "?modid=" + modid, function (data) {
        _menus = data;
        buildmenus(_menus);
    }, function (XMLHttpRequest, textStatus, errorThrown) {
        alert("获取菜单错误！");
    });
}

function haschildmenu(menuid) {
    for (var i = 0; i < _menus.length; i++) {
        var menu = _menus[i];
        if (menu.menupid == menuid) {
            return true;
            break;
        }
    }
    return false;
}

function buildmenus(menus) {
    for (var i = 0; i < menus.length; i++) {
        var menu = menus[i];
        if ((menu.isitem == "2") && (menu.clas == "1")) {
            var menubt = $("<a href='#' class='easyui-menubutton'>" + menu.language1 + "</a>").appendTo($("#menudiv_id"));
            if (haschildmenu(menu.menuid)) {
                menubt.attr("data-options", "menu:'#mm" + menu.menuid + "',iconCls:'" + menu.menuidpath + "'");
                //创建子菜单
                var submeus = $("<div id='mm" + menu.menuid + "' style='width:100px;'></div>").appendTo(menubt);
                for (var j = 0; j < menus.length; j++) {
                    var submenu = menus[j];
                    if (submenu.menupid == menu.menuid) {
                        if (submenu.clas == "1") {
                            var menuiten = $("<div onclick='menuonclick(\"" + submenu.menuid + "\")'>" + submenu.language1 + "</div>").appendTo(submeus);
                            if (submenu.menuidpath != "") {
                                menuiten.attr("data-options", "iconCls:'" + submenu.menuidpath + "'");
                            }
                        } else {
                            $("<div class='menu-sep'></div>").appendTo(submeus);
                        }
                    }
                }
            }
        }
    }
    $.parser.parse($("#menudiv_id").parent());
}

function menuonclick(menuid) {
    var menu = undefined;
    for (var i in _menus) {
        if (_menus[i].menuid == menuid) {
            menu = _menus[i];
            break;
        }
    }
    if (!menu) return false;

    var title = menu.language1;
    var maintab = $('#sub_main_tabs_id');
    maintab.css("display", "block");
    if (maintab.tabs('exists', title)) {
        maintab.tabs('select', title);
    } else {
        var url = menu.weburl;
        if ((!url) || (url.length == 0)) {
            $.messager.alert('错误', '菜单没有指定URL!', 'error');
            return false;
        }
        //console.log(url + "?menu=" + JSON.stringify(menu));
        var ifid = "ifid_" + menu.menuid;
        var content = "<iframe id='" + ifid + "' scrolling='no' frameborder='0' src='../" + url + "' style='width:100%;height:100%;'></iframe>";
        maintab.tabs('add', {
                id: 'tab' + menu.menuid,
                title: title,
                content: content,
                iconCls: menu.menuidpath,
                fit: true,
                closable: true
            }
        );
        var tab = maintab.tabs("getSelected");
        tab.css("overflow", "hidden");
        $("#" + ifid)[0].contentWindow._menu = menu;
    }
}
