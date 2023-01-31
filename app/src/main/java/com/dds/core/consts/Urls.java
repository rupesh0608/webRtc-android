package com.dds.core.consts;

/**
 * Created by dds on 2020/4/19.
 * ddssingsong@163.com
 */
public class Urls {

    //    private final static String IP = "192.168.2.111";
    public final static String IP = "172.20.10.2:5000";

    private final static String HOST = "http://" + IP + "/";

    // signaling address
    public final static String WS = "ws://" + IP + "/ws";

    // get user list
    public static String getUserList() {
        return HOST + "userList";
    }

    // get room list
    public static String getRoomList() {
        return HOST + "roomList";
    }
}
