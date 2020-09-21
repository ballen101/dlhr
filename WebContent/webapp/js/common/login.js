/**
 * Created by shangwen on 2015/12/9.
 */
if (!window.localStorage) {
    alert("建议使用支持localStorage的浏览器");
}

$(document).ready(function () {
    $.parser.auto = false;
    initUserInfoEx();
    $.parser.parse();
});

function $OnCorsairReady() {
    //setTimeout("initUserInfo()", 300);
    setTimeout("initUserInfoEvent()", 500);
}

function initUserInfoEx() {
    var cked = localStorage.getItem(_contextPath + ".saveuserpwd");
    if (cked == undefined)
        cked = true;
    else
        cked = (cked == "true");
    document.getElementById("savepwd").checked = cked;
    $("#username").val(localStorage.getItem(_contextPath + ".username"));
    $("#userpass").val(localStorage.getItem(_contextPath + ".userpass"));
    //alert(localStorage.getItem(_contextPath + ".username"));
}

function initUserInfoEvent() {
    $('#username').textbox('textbox').bind('keydown', function (e) {
        var kcode = e.keyCode;
        if (kcode == 13) {
            $('#userpass').textbox('textbox')[0].focus();
        }
    });

    $('#userpass').textbox('textbox').bind('keydown', function (e) {
        var kcode = e.keyCode;
        if (kcode == 13) {
            dologin();
        }
    });
}

function dologinex() {
    var usname = $("#username").val();
    var userpass = $("#userpass").val();
    var savepwd = document.getElementById("savepwd").checked;
    var url = _serUrl + "/web/login/lg.co";
    var data = getlgdata(usname, userpass);
    $ajaxjsonpost(url, JSON.stringify(data), function (user) {
        getuserinfos(user, function (r) {
            //alert(JSON.stringify(user));
            //console.error(JSON.stringify(user));
            if (r == 1) {

                localStorage.setItem(_contextPath + ".token", user.token);
                localStorage.setItem(_contextPath + ".saveuserpwd", savepwd);
                if (savepwd)
                    localStorage.setItem(_contextPath + ".userpass", userpass);
                else
                    localStorage.removeItem(_contextPath + ".userpass");
                if (window.top == window)
                    location = _serUrl + "/webapp/index.html";
                else if (window.parent) {
                    window.top.location.reload();
                    //window.top.$("#login_window").window("close");
                    //
                }
                var userParms = user.userParms;
                if (userParms) {
                    for (var i = 0; i < userParms.length; i++) {
                        var up = userParms[i];
                        localStorage.setItem(_contextPath + "." + up.parmname, up.parmvalue);
                    }
                }
                //      window.location.reload();
            }
        });
    }, function (err) {
        alert("登录错误:" + err.errmsg);
		if(err.errmsg.indexOf("初始化密码")>-1){
			$('#chguspwd_popwin').window('open');
		}
    });

}

function dologin() {
    dologinex();
}

/*
 function dologin() {
 var usname = encodeURI($("#username").val());
 var userpass = $("#userpass").val();
 var savepwd = document.getElementById("savepwd").checked;
 var urlstr = $C.cos.login() + "?username=" + usname + "&userpass=" + userpass + "&version=1.1";
 $ajaxjsonget(urlstr, function (user) {
 getuserinfos(user, function (r) {
 //alert(JSON.stringify(user));
 //console.error(JSON.stringify(user));
 if (r == 1) {
 localStorage.setItem(_contextPath + ".saveuserpwd", savepwd);
 if (savepwd)
 localStorage.setItem(_contextPath + ".userpass", userpass);
 else
 localStorage.removeItem(_contextPath + ".userpass");
 if (window.top == window)
 location = _serUrl + "/webapp/index.html";
 else if (window.parent) {
 window.top.location.reload();
 //window.top.$("#login_window").window("close");
 //
 }
 //      window.location.reload();
 }
 });
 }, function (err) {
 alert("登录错误:" + err.errmsg);
 });
 }*/

function getuserinfos(user, rcsuc) {//登录后，客户端获取用户参数
    $C.UserInfo.removelogininfos();
    localStorage.setItem(_contextPath + ".userid", user.userid);
    localStorage.setItem(_contextPath + ".username", user.username);
    localStorage.setItem(_contextPath + ".displayname", user.displayname);
    localStorage.setItem(_contextPath + ".usertype", user.usertype);
    localStorage.setItem(_contextPath + ".languageid", "1");
    $ajaxjsonget(_serUrl + "/web/login/getdefaultorg.co",
        function (jsondata) {
            localStorage.setItem(_contextPath + ".entid", jsondata.entid);
            localStorage.setItem(_contextPath + ".dforgid", jsondata.orgid);
            localStorage.setItem(_contextPath + ".dforgidpath", jsondata.idpath);
            rcsuc(1);
        }, function () {
            alert("用户参数错误！");
            rcsuc(0);
        })
}

function initUserInfo() {
    var cked = localStorage.getItem(_contextPath + ".saveuserpwd");
    if (cked == undefined)
        cked = true;
    else
        cked = (cked == "true");
    document.getElementById("savepwd").checked = cked;
    $("#username").textbox("setValue", localStorage.getItem(_contextPath + ".username"));
    $("#userpass").textbox("setValue", localStorage.getItem(_contextPath + ".userpass"));

    $('#username').textbox('textbox').bind('keydown', function (e) {
        var kcode = e.keyCode;
        if (kcode == 13) {
            $('#userpass').textbox('textbox')[0].focus();
        }
    });

    $('#userpass').textbox('textbox').bind('keydown', function (e) {
        var kcode = e.keyCode;
        if (kcode == 13) {
            dologin();
        }
    });
}