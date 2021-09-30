package com.citic.asp.test.loader;

import java.io.Serializable;

/**
 * 平台账号
 *
 * @author qcb
 * @date 2021/04/19 17:40.
 */
public class Account implements Serializable {
    private static final long serialVersionUID = 7369235199643631187L;

    public Account() {
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account(String username, String password, String deviceId) {
        this.username = username;
        this.password = password;
        this.deviceId = deviceId;
    }

    public Account(String username, String password, String deviceId, String deviceType) {
        this.username = username;
        this.password = password;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    private String username;

    private String password;

    private String deviceId;

    private String deviceType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}

