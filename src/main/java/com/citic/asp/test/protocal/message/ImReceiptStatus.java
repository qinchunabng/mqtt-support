package com.citic.asp.test.protocal.message;

/**
 * <dl>回执状态类
 * <dt>ImReceiptStatus</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/10/28</dd>
 * </dl>
 *
 * @author maoyx
 */
public enum ImReceiptStatus {
    // 已发送
    SEND(1),
    // 已接收
    RECEIVE(2),
    // 已读
    READ(3);

    private int code;

    ImReceiptStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
