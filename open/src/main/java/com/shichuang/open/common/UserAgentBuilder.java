package com.shichuang.open.common;

import android.text.TextUtils;

/**
 * Created by Administrator on 2018/1/19.
 */

public class UserAgentBuilder {
    private static String addressInfo = "";
    public static final String DEFAULT_INFO = "ahometandroid";

    public static void setAddressInfo(String addressInfo) {
        UserAgentBuilder.addressInfo = addressInfo;
    }

    public static String ua() {
        if (TextUtils.isEmpty(addressInfo)) {
            return DEFAULT_INFO;
        } else {
            return "ahometandroid" + "[" + addressInfo + "]";
        }
    }
}
