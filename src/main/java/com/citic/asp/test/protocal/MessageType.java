package com.citic.asp.test.protocal;

/**
 * 消息类型
 *
 * @author qcb
 * @date 2021/04/20 16:29.
 */
public enum MessageType {
    TEXT(1,"文本消息");

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;

    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
