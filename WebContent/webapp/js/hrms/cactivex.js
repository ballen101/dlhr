/**
 * Created by shangwen on 2016/10/15.
 */

$(document).ready(function () {
    var hrmsact = document.getElementById("hrmsact");
    if (hrmsact == undefined) {
        var hm = "<OBJECT ID='hrmsact' style='display: none' CLASSID='CLSID:DD300566-1717-4382-84B1-D6AB4D492E0B' align=center hspace=0 vspace=0></OBJECT>";
        $(hm).appendTo("body");
    }
});

function $ReadIDCardInfo() {
    return docallactproc(1);
}

function $TakePhoto() {
    return docallactproc(2);
}
function $ScanPhoto() {
    return docallactproc(3);
}

function docallactproc(tp) {
    if (browinfo.browser == "IE") {
        try {
            hrmsact.testproc()
            var ocxinited = true;
        } catch (e) {
            var ocxinited = false;
        }
        if (ocxinited) {
            if (tp == 1)
                return JSON.parse(hrmsact.readidcardinfo());
            if (tp == 2)
                return hrmsact.takephoto();
            if (tp == 3)
                return hrmsact.sacnphoto();
        } else {
            alert("HRMS插件未能正确安装 或 浏览器安全级别已禁止Activex运行!");
        }
    } else {
        alert("硬件接口插件暂时只支持IE系列浏览器!");
    }
}

