package com.citic.asp.test.protocal;

import com.citic.asp.test.protocal.message.ImPayloadType;
import org.tio.core.ChannelContext;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 消息发送
 *
 * @author qcb
 * @date 2021/04/20 10:50.
 */
public interface MessageSender extends BaseOperation{

    /**
     * 发送消息
     * @param message 消息内容
     * @param messageType 消息类型
     *        @see ImPayloadType
     * @param channelContext 连接上下文
     * @param topic 消息主题
     * @param encryptKey 加密密钥
     * @param waitResponse 是否等待回执
     * @param sendTimeout 发送超时时间
     * @return 如果waitResponse为true，即需要等待服务器返回消息回执，收到服务器的消息回执返回为true，超过指定的等待时间没有收到消息回执返回false
     *         如果waitResponse为false，默认返回true
     */
    boolean send(String message, int messageType, ChannelContext channelContext, String topic, String encryptKey, boolean waitResponse, int sendTimeout) throws IOException;

    /**
     * 发送单聊消息
     * @param message 单聊消息
     * @param serviceId 服务ID
     * @param toUser 接收人
     * @param channelContext 连接上下文
     * @param encryptKey 加密密钥
     * @param waitResponse 是否等待回执
     * @param sendTimeout 发送超时时间
     * @return 如果waitResponse为true，即需要等待服务器返回消息回执，收到服务器的消息回执返回为true，超过指定的等待时间没有收到消息回执返回false
     *         如果waitResponse为false，默认返回true
     */
    boolean sendSingleMessage(String message, String serviceId, String toUser, ChannelContext channelContext, String encryptKey, boolean waitResponse, int sendTimeout) throws IOException;

    /**
     * 发送设备消息
     * @param message 消息内容
     * @param serviceId 服务ID
     * @param toDevice 接收设备ID
     * @param channelContext 连接上下文
     * @param encryptKey 加密密钥
     * @param waitResponse 是否等待回执
     * @param sendTimeout 发送超时时间
     * @return 如果waitResponse为true，即需要等待服务器返回消息回执，收到服务器的消息回执返回为true，超过指定的等待时间没有收到消息回执返回false
     *         如果waitResponse为false，默认返回true
     */
    boolean sendDeviceMessage(String message, String serviceId, String toDevice, ChannelContext channelContext, String encryptKey, boolean waitResponse, int sendTimeout) throws IOException;

    /**
     * 发送群聊消息
     * @param message 群消息内容
     * @param serviceId 服务ID
     * @param groupId 群ID
     * @param channelContext 连接上下文
     * @param encryptKey 加密key
     * @param waitResponse 是否等待回执
     * @param sendTimeout 发送超时时间
     * @return 如果waitResponse为true，即需要等待服务器返回消息回执，收到服务器的消息回执返回为true，超过指定的等待时间没有收到消息回执返回false
     *         如果waitResponse为false，默认返回true
     */
    boolean sendGroupMessage(String message, String serviceId, String groupId, ChannelContext channelContext, String encryptKey, boolean waitResponse, int sendTimeout) throws IOException;

    /**
     * 认证
     * @param serviceId 服务ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param encryptKey 加密密钥
     * @param channelContext 连接上下文
     */
    void auth(String serviceId, String deviceId, String deviceType, String encryptKey, ChannelContext channelContext) throws IOException;

    /**
     * 上线操作
     * @param serviceId 服务ID
     * @param userId 用户ID
     * @param channelContext 连接上下文
     */
    void online(String serviceId, String userId, ChannelContext channelContext) throws IOException;

//    /**
//     * 发送心跳消息
//     * @param os 输出流
//     */
//    void sendPing(OutputStream os) throws IOException;
}
