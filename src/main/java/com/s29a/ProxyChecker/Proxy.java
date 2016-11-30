package com.s29a.ProxyChecker;

/**
 * Created by xxx on 28.02.16.
 */
public class Proxy {
    protected String ip, port;
    protected boolean isAvailable = false;

    public Proxy(String ip, String port)
    {
        this.ip = ip;
        this.port = port;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return  ip + ":" + port;
    }
}
