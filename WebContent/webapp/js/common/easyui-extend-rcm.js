(function ($) {
    /**
     * jQuery EasyUI 1.4 --- 功能扩展
     *
     * Copyright (c) 2009-2015 RCM
     *
     * 新增 validatebox 校验规则
     *
     */
    $.extend($.fn.validatebox.defaults.rules, {
        idcard: {
            validator: function (value, param) {
                //return idCardNoUtil.checkIdCardNo(value);
                return true;
            },
            message: '请输入正确的身份证号码'
        },
        checkNum: {
            validator: function (value, param) {
                return /^([0-9]+)$/.test(value);
            },
            message: '请输入整数'
        },
        checkFloat: {
            validator: function (value, param) {
                return /^[+|-]?([0-9]+\.[0-9]+)|[0-9]+$/.test(value);
            },
            message: '请输入合法数字'
        },
        checkFloatMinMax: {
            validator: function (value, param) {
                if (!(/^[+|-]?([0-9]+\.[0-9]+)|[0-9]+$/.test(value)))
                    return false;
                var num = parseFloat(value);
                return ((num >= param[0]) && (num <= param[1]));
            },
            message: '请输入合法数字'
        }
    });
})(jQuery);  