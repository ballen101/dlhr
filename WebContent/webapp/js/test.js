function aa() {
    var userdata=111;
    $ajaxjsonpost(url, {}, function () {
        userdata
    }, function () {
        
    }, false, userdata, true);
}

function bb() {
    var userdata=2222;
    $ajaxjsonpost(url, {}, function () {
        
    }, function () {
        
    }, false, {}, true);
}