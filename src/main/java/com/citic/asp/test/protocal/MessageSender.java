package com.citic.asp.test.protocal;

import com.citic.asp.test.protocal.message.ImPayloadType;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 消息发送
 *
 * @author qcb
 * @date 2021/04/20 10:50.
 */
public interface MessageSender {

    /**
     * 发送消息
     * @param message 消息内容
     * @param messageType 消息类型
     *        @see ImPayloadType
     * @param topic 消息主题
     * @param encryptKey 加密密钥
     */
    void send(String message, int messageType, OutputStream os, String topic, String encryptKey) throws IOException;

    /**
     * 发送单聊消息
     * @param message 单聊消息
     * @param serviceId 服务ID
     * @param toUser 接收人
     * @param os 输出流
     * @param encryptKey 加密密钥
     */
    void sendSingleMessage(String message, String serviceId, String toUser, OutputStream os, String encryptKey) throws IOException;

    /**
     * 发送设备消息
     * @param message 消息内容
     * @param serviceId 服务ID
     * @param toDevice 接收设备ID
     * @param os 输出流
     * @param encryptKey 加密密钥
     */
    void sendDeviceMessage(String message, String serviceId, String toDevice, OutputStream os, String encryptKey) throws IOException;

    /**
     * 发送群聊消息
     * @param message 群消息内容
     * @param serviceId 服务ID
     * @param groupId 群ID
     * @param os
     * @param encryptKey 加密key
     */
    void sendGroupMessage(String message, String serviceId, String groupId, OutputStream os, String encryptKey) throws IOException;

    /**
     * 认证
     * @param serviceId 服务ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param encryptKey 加密密钥
     * @param os 输出流
     */
    void auth(String serviceId, String deviceId, String deviceType, String encryptKey,  OutputStream os) throws IOException;

    /**
     * 上线操作
     * @param serviceId 服务ID
     * @param userId 用户ID
     * @param os 输出流
     */
    void online(String serviceId, String userId, OutputStream os) throws IOException;

    /**
     * 发送心跳消息
     * @param os 输出流
     */
    void sendPing(OutputStream os) throws IOException;
}
