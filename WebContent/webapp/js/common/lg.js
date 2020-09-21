/**
 * Created by shangwen on 2018/6/8.
 */

function getlgdata(username, userpass) {
    if ((username == null) || (username.length == 0))
        return;
    if (!userpass)userpass = "";
    var noncestr = randomString(32);
    var timestr = new Date().format("yyyyMMddhhmmss");
    var md5str1 = "username=" + username + "&noncestr=" + noncestr + "&timestr=" + timestr + "&password=" + userpass;
    //console.error(md5str1);
    var md5str = md5(md5str1);
    //console.error(md5str);
    var data = {
        username: username,
        noncestr: noncestr,
        timestr: timestr,
        md5sign: md5str
    };
    return data;
}

function randomString(len) {
    var len = len || 32;
    var $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var maxPos = $chars.length;
    var pwd = '';
    for (var i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    };

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
};

