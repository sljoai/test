package com.song.cn.kafka;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Addr {

    public static boolean isWindowOS() {
        boolean isWindowOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowOS = true;
        }
        return isWindowOS;
    }

    public static InetAddress getInetAddress() {
        InetAddress inetAddress = null;
        try {
            //如果是windows操作系统
            if (isWindowOS()) {
                inetAddress = InetAddress.getLocalHost();
            } else {
                boolean bFindIP = false;
                //定义一个内容都是NetworkInterface的枚举对象
                Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
                //如果枚举对象里面还有内容(NetworkInterface)
                while (netInterfaces.hasMoreElements()) {
                    if (bFindIP) {
                        break;
                    }
                    //获取下一个内容(NetworkInterface)
                    NetworkInterface ni = netInterfaces.nextElement();
                    //----------特定情况，可以考虑用ni.getName判断
                    //遍历所有IP
                    Enumeration<InetAddress> ips = ni.getInetAddresses();
                    while (ips.hasMoreElements()) {
                        inetAddress = ips.nextElement();
                        if (inetAddress.isSiteLocalAddress()         //属于本地地址
                                && !inetAddress.isLoopbackAddress()  //不是回环地址
                                && inetAddress.getHostAddress().indexOf(":") == -1) {   //地址里面没有:号
                            bFindIP = true;     //找到了地址
                            break;              //退出循环
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inetAddress;
    }

    public static String getLocalIP() {
        return getInetAddress().getHostAddress();
    }

    public static void main(String[] args) {
        System.out.println(Addr.getInetAddress());
        System.out.println(Addr.getLocalIP());
    }
}
