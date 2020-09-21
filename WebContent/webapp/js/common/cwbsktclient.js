/**
 * Created by Administrator on 2020/01/13.
 */

if (!"WebSocket" in window) {
    alert("您的浏览器不支持 WebSocket");
}

//
/**
 *
 * options{
 * url:"ws://localhost:8081/calldll.co",//server socket url 服务器地址
 * CallBack_StatChanged:function(stat){//skt状态变化 1 连上 2 断开 3 重连中
 * },
 * ReconnectTime:1000,//重连时间间隔 1S
 * }
 *
 * action.dlltype = actPropertys.dlltype;
 action.dllfname = actPropertys.dllfname;
 action.MethodName = actPropertys.MethodName;
 * function regAction(actname, actPropertys) {//注册Action
 * }
 *
 *
 */
function CWBSKTClient(options) {
    var url = options.url;
    var CallBack_StatChanged = options.CallBack_StatChanged;
    var ReconnectTime = options.ReconnectTime;
    if (!ReconnectTime) ReconnectTime = 1000;
    var _ws = undefined;
    var _sktstat = 2;//1 连上 2 断开 3 重连中
    var _actions = {};
    if (!url) {
        $.messager.alert('错误', "需要设置URL参数", 'error');
        return;
    }

    this.getStat = getStat;
    function getStat() {
        return _sktstat;
    }

    this.isConnected = isConnected;
    function isConnected() {
        return _sktstat == 1;
    }


    function connect() {
        _ws = new WebSocket(url);
        _ws.onopen = function () {
            _sktstat = 1;
            if (CallBack_StatChanged)
                CallBack_StatChanged(_sktstat);
        };
        _ws.onclose = function () {
            _sktstat = 2;
            if (CallBack_StatChanged)
                CallBack_StatChanged(_sktstat);
            setTimeout(function () {
                _sktstat = 3;
                CallBack_StatChanged(_sktstat);
                connect();
            }, ReconnectTime);
        };
    }

    connect();

    this.regAction = regAction;
    function regAction(ActionName, actPropertys) {
        var action = _actions[ActionName];
        if (!action) {
            action = {};
        }
        action.DillType = actPropertys.DillType;
        action.DllFileName = actPropertys.DllFileName;
        action.MethodName = actPropertys.MethodName;
        _actions[ActionName] = action;
    }

    this.callAction = callAction;
    function callAction(ActionName, printdata, OnMessageCallBack) {
        var action = _actions[ActionName];
        if (!action) {
            $.messager.alert('错误', "【" + ActionName + "】未注册", 'error');
            return;
        }
        if ((_sktstat != 1) || !_ws) {
            $.messager.alert('错误', "【" + url + "】服务未连接", 'error');
            return;
        }
        _ws.onmessage = OnMessageCallBack;
        var data = {};
        data.DillType = action.DillType;//表示c#的dll
        data.DllFileName = action.DllFileName;//dll名称
        data.MethodName = action.MethodName;//方法名
        if (printdata)
            data.parms = [printdata];
        _ws.send(JSON.stringify(data));
    }
}


