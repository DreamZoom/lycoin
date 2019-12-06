package com.ying.cloud.lycoin.utils;

import java.net.InetAddress;

public class SystemUtils {
    public static String getLocalAddress(){
        String ipAddress="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            ipAddress= address.getHostAddress();
            return  ipAddress;
        }
        catch (Exception err){}
        return "127.0.0.1";
    }
}
