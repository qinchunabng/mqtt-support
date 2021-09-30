package com.citic.asp.test.protocal;

import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.cmc.core.message.CherryMessagePayloadType;
import com.citic.asp.core.util.string.IdUtil;
import com.citic.asp.test.protocal.message.ImPayloadType;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * MQTT
 *
 * @author qcb
 * @date 2021/04/20 11:19.
 */
public class MqttManager extends BaseMqttOperation implements MessageSender, MessageReceiver{

    private final Logger log = LoggerFactory.getLogger(MqttManager.class);

    /**
     * 单聊消息topic，格式：/im/user/【serviceId】/【userId】
     */
    protected static final String USER_MESSAGE_TOPIC = "/im/user/%s/%s";

    /**
     * 群聊消息topic，格式：/im/group/【serviceId】/【groupId】
     */
    protected static final String GROUP_MESSAGE_TOPIC = "/im/group/%s/%s";

    /**
     * 上线消息topic，格式：/online/user/【serviceId】/【userId】
     */
    protected static final String ONLINE_MESSAGE_TOPIC = "/online/user/%s/%s";

    /**
     * 下线消息topic，格式：/offline/user/【serviceId】/【userId】
     */
    protected static final String OFFLINE_MESSAGE_TOPIC = "/offline/user/%s/%s";

    /**
     * 设备消息topic，格式：/im/device/【serviceId】/【groupId】
     */
    protected static final String DEVICE_MESSAGE_TOPIC = "/im/device/%s/%s";

    private static volatile MqttManager INSTANCE;

    private MqttManager(){}

    public static MqttManager getInstance(){
        if(INSTANCE == null){
            synchronized (MqttManager.class){
                if(INSTANCE == null){
                    INSTANCE = new MqttManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 发送消息
     * @param message 消息内容
     * @param messageType 消息类型
     *        @see ImPayloadType
     * @param os 输出流
     * @param topic 消息主题
     * @param encryptKey 加密密钥
     */
    @Override
    public void send(String message, int messageType, OutputStream os, String topic, String encryptKey) throws IOException {
        CherryMessagePayloadType payloadType = ImPayloadType.valueOf((byte) messageType);
        if(payloadType == null){
            throw new IllegalArgumentException("Message type is incorrect.");
        }
        long messageId = IdUtil.nextId();
        sendMessage(messageId, topic, message, payloadType, os, true, encryptKey);
//        log.info("发送消息,messageId:{}, message:{}", messageId, message);
        log.info("发送消息,messageId:{}", messageId);
    }

    /**
     * 发送单聊消息
     * @param message 单聊消息
     * @param serviceId 服务ID
     * @param toUser 接收人
     * @param os 输出流
     */
    @Override
    public void sendSingleMessage(String message, String serviceId, String toUser, OutputStream os, String encryptKey) throws IOException {
        send(message, ImPayloadType.SINGLE_CHAT.getCode(), os, String.format(USER_MESSAGE_TOPIC, serviceId, toUser), encryptKey);
    }

    /**
     * 发送设备消息
     * @param message 消息内容
     * @param serviceId 服务ID
     * @param toDevice 接收设备ID
     * @param os 输出流
     * @param encryptKey 加密密钥
     */
    @Override
    public void sendDeviceMessage(String message, String serviceId, String toDevice, OutputStream os, String encryptKey) throws IOException {
        send(message, ImPayloadType.SINGLE_CHAT.getCode(), os, String.format(DEVICE_MESSAGE_TOPIC, serviceId, toDevice), encryptKey);
    }

    /**
     * 发送群聊消息
     * @param message 群消息内容
     * @param serviceId 服务ID
     * @param groupId 群ID
     * @param os
     * @param encryptKey 加密key
     */
    @Override
    public void sendGroupMessage(String message, String serviceId, String groupId, OutputStream os, String encryptKey) throws IOException {
        send(message, ImPayloadType.GROUP_CHAT.getCode(), os, String.format(GROUP_MESSAGE_TOPIC, serviceId, groupId), encryptKey);
    }

    /**
     * 认证
     * @param serviceId 服务ID
     * @param deviceId 设备ID
     * @param os 输出流
     */
    @Override
    public void auth(String serviceId, String deviceId, String deviceType, String encryptKey, OutputStream os) throws IOException {
        //发送认证消息
        log.info("======> 发送认证消息,serviceId:{},deviceId:{},deviceType:{}", serviceId, deviceId, deviceType);
        CherryMessage authMessage = MqttMessageFactory.createAuthMsg(deviceId, serviceId, deviceType, encryptKey);
        sendMessage(authMessage, os);
    }

    /**
     * 上线操作
     * @param serviceId 服务ID
     * @param userId 用户ID
     * @param os 输出流
     */
    @Override
    public void online(String serviceId, String userId, OutputStream os) throws IOException {
        //发送上线消息
        log.info("======> 发送上线消息,serviceId:{},userId:{}", serviceId, userId);
        CherryMessage onlineMessage = MqttMessageFactory.createSubscribeMsg(Sets.newHashSet(String.format(ONLINE_MESSAGE_TOPIC, serviceId,userId)));
        sendMessage(onlineMessage, os);
    }

    /**
     * 接收消息
     * @param is
     * @return
     */
    @Override
    public byte[] receive(InputStream is) throws IOException {
        return readMessage(is);
    }

    /**
     * 发送心跳消息
     * @param os 输出流
     */
    @Override
    public void sendPing(OutputStream os) throws IOException {
        sendMessage(MqttMessageFactory.createPingMsg(), os);
    }
}
