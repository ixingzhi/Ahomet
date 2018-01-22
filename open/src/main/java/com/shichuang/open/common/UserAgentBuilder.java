package com.shichuang.open.common;

import android.text.TextUtils;

/**
 * Created by Administrator on 2018/1/19.
 */

public class UserAgentBuilder {
    private static String addressInfo = "";

    public static void setAddressInfo(String addressInfo) {
        UserAgentBuilder.addressInfo = addressInfo;
    }

    public static String ua() {
        if (TextUtils.isEmpty(addressInfo)) {
            return "ahometandroid";
        } else {
            return "ahometandroid" + "[" + addressInfo + "]";
        }
    }
}
