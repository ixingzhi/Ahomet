package com.shichuang.ahomet.event;

public class MessageEvent {
    public static final String NEED_GPS = "needGps";
    public static final String SHOW_NAV_BAR = "showNavBar";
    public static final String HIDE_NAV_BAR = "hideNavBar";
    public static final String OPEN_MENU = "openMenu";
    public static final String UPDATE_LOGIN_STATUS = "updateLoginStatus";

    public String message;

    public MessageEvent(String message) {
        this.message = message;
    }

}