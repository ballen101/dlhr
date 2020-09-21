/**
 * Created by shangwen on 2017/9/7.
 */
var $Error_IO_Text = new Array(
    "未知错误",//0
    "校验错误",
    "其它应答错误",
    "写命令错误",
    "串口接收未结束而中断",
    "不能打开端口",
    "命令错误",
    "打开端口失败",
    "通讯超时",
    "IC卡密码错",
    "帧头错误",//10
    "帧尾错误",
    "返回长度与数据长度不对应",
    "总长度错误",
    "该功能本机型不支持",
    "目标地址与源地址不对应",
    "参数溢出，超出设定范围",
    "记录校验错误",
    "输入的值与设备存储的值相同",
    "卡不存在",
    "卡被移开",//20
    "IC卡没有通过身份验证",
    "IC卡访问身份验证错误",
    "IC卡卡片序列号错误",
    "其它卡访问错误",
    "数据写入卡片出错",
    "机具正忙",
    "命令参数错误",
    "机具密钥验证失败",
    "命令执行失败",
    "未设置有效值",//30
    "名单不存在",
    "名单索引错误",
    "时间格式错误",
    "星期值非法",
    "实钟无法访问",
    "系统实钟错误",
    "系统时钟复位",
    "名单存储己满",
    "无效名单信息",
    "网络通讯错误，记录读取超时",//40
    "设备无应答",
    "名单己被删除",
    "己登记为黑名单"
);

//读取卡序号
function $readcardsn() {
    if (MyActiveX) {
        try {
            MyActiveX.ClockID = 1;
            MyActiveX.CommPort = 5;
            MyActiveX.CommBaudrate = 9600;
            MyActiveX.OpenCurUsbPort();
            MyActiveX.ReadICCardSerialNo();
            var result = MyActiveX.GetErrorCode();
            if (result == 0) {
                return MyActiveX.CardSerialNo;
            }
            else {
                alert($Error_IO_Text[result]);
            }
            MyActiveX.CloseCurUsbPort();
        } catch (e) {
            alert(e.message);
        }
    }
}

//空卡卡号为：FFFFFFFFFFFF 或 00000000000 读取卡信息
//cardinfo:{"cardno":"900000","empname":"王亚云"}
function $ReadCardInfo(showerinfo) {
    if (MyActiveX) {
        try {
            MyActiveX.ClockID = 1;
            /*发卡器机号*/
            MyActiveX.OpenCurUsbPort();
            /*打开发卡器端口*/
            MyActiveX.ReadCusMealCardInfo();
            /*调用 读消费卡 函数*/
            var result = MyActiveX.GetErrorCode();
            if (result == 0) {
                return {cardno: MyActiveX.ReadCardNo, empname: MyActiveX.ReadCardName};
            } else {
                if (showerinfo)
                    alert($Error_IO_Text[result]);
            }
        } catch (e) {
            alert(e.message);
        } finally {
            MyActiveX.CloseCurUsbPort();
        }
    }
}

//空卡卡号为：FFFFFFFFFFFF 或 00000000000 读取卡信息
function $ReadCardInfoold(showerinfo) {
    if (MyActiveX) {
        try {
            MyActiveX.ClockID = 1;
            MyActiveX.CommPort = 5;
            MyActiveX.CommBaudrate = 9600;
            MyActiveX.OpenCurUsbPort();
            MyActiveX.ReadCardInfo();
            var result = MyActiveX.GetErrorCode();
            if (result == 0) {
                return {cardno: MyActiveX.ReadCardNo, empname: MyActiveX.ReadCardName};
            } else {
                if (showerinfo)
                    alert($Error_IO_Text[result]);
            }
            MyActiveX.CloseCurUsbPort();
        } catch (e) {
            alert(e.message);
        }
    }
}

//发卡
function $WriteCardInfo(cardno, empname, showerinfo) {
    if (MyActiveX) {
        try {
            if ($isEmpty(cardno) || $isEmpty(empname)) {
                alert("卡号、姓名不能为空");
                return false;
            }
            MyActiveX.ClockID = 1;
            /*发卡器机号*/
            // MyActiveX.CommPort=5;
            // MyActiveX.CommBaudrate=9600;
            MyActiveX.CardNo = cardno;
            MyActiveX.CardName = empname;
            MyActiveX.GroupInfo = "1";
            MyActiveX.DayCountttl = 2;
            MyActiveX.MealMoney = 860;
            MyActiveX.CardEndDate = "991231";
            MyActiveX.CardPIN = "A";
            MyActiveX.OpenCurUsbPort();
            MyActiveX.WriteCusMealCardInfo();
            var result = MyActiveX.GetErrorCode();
            if (result == 0) {
                return true;
            } else {
                if (showerinfo)
                    alert($Error_IO_Text[result]);
            }
        } catch (e) {
            alert(e.message);
        } finally {
            MyActiveX.CloseCurUsbPort();
        }
    }
}

//发卡
function $WriteCardInfoold(cardno, empname, showerinfo) {
    if (MyActiveX) {
        try {
            if ($isEmpty(cardno) || $isEmpty(empname)) {
                alert("卡号、姓名不能为空");
                return false;
            }
            MyActiveX.ClockID = 1;
            MyActiveX.CommPort = 5;
            MyActiveX.CommBaudrate = 9600;
            MyActiveX.CardNo = cardno;
            MyActiveX.CardName = empname;
            MyActiveX.OpenCurUsbPort();
            MyActiveX.WriteCardInfo();
            var result = MyActiveX.GetErrorCode();
            MyActiveX.CloseCurUsbPort();
            if (result == 0) {
                return true;
            } else {
                if (showerinfo)
                    alert($Error_IO_Text[result]);
            }
        } catch (e) {
            alert(e.message);
        }
    }
}

//清卡
function clear_card(showerinfo) {
    try {
        MyActiveX.ClockID = 1;
        MyActiveX.CommPort = 5;
        MyActiveX.CommBaudrate = 9600;
        MyActiveX.Block = 2;
        MyActiveX.SectorCode = 0;
        MyActiveX.OpenCurUsbPort();//MyActiveX.OpenCommPort();
        MyActiveX.InitializeICCard();
        var result = MyActiveX.GetErrorCode();
        if (result == 0) {
            return true;
        } else {
            if (showerinfo)
                alert(Error_IO_Text[result]);
        }
    } catch (e) {
        alert(e.message);
    } finally {
        MyActiveX.CloseCurUsbPort();
    }
}

function open_door(IPAddress, IPPort, ClockID, ReaderID, OpenDoorKind) {
    console.log("opendoor IPAddress:" + IPAddress + ";IPPort:" + IPPort + ";ClockID:" + ClockID + ";ReaderID:" + ReaderID + ";OpenDoorKind:" + OpenDoorKind);
    try {
        MyActiveX.IPAddress = IPAddress;
        MyActiveX.IPPort = IPPort;
        var rst = MyActiveX.OpenIPAddressPort();
        if (rst == true) {
            MyActiveX.ClockID = ClockID;
            MyActiveX.ReaderID = ReaderID;
            MyActiveX.OpenDoorKind = OpenDoorKind;
            var rst = MyActiveX.PCSoftOpenDoor();//MyActiveX.OpenCommPort();
            if (rst == true)
                return true;
            else {
                var result = MyActiveX.GetErrorCode();
                alert("远程开门错误:" + $Error_IO_Text[result]);
            }
        } else {
            var result = MyActiveX.GetErrorCode();
            if (result)
                alert("打开端口错误:" + $Error_IO_Text[result]);
        }
    } catch (e) {
        alert(e.message);
    } finally {
        MyActiveX.CloseIPAddressPort();
    }

}