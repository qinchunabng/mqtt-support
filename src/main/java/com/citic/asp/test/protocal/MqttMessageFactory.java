package com.citic.asp.test.protocal;


import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.cmc.core.message.CherryMessagePayload;
import com.citic.asp.cmc.core.message.CherryMessagePayloadType;
import com.citic.asp.core.util.security.SecurityTool;
import com.citic.asp.core.util.security.gm.SM4Util;
import io.netty.handler.codec.mqtt.*;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * description
 *
 * @author DELL
 * @date 2021/03/16 11:19.
 */
public class MqttMessageFactory {

    private static MqttVersion version = MqttVersion.MQTT_3_1_1;
    /**
     * 报文定长
     */
    private static final int FIXED_LENGTH = 18;

    /**
     * 创建ping消息
     * @return
     */
    public static MqttMessage createPingMsg(){
        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);

        return new MqttMessage(mqttFixedHeader);
    }

    /**
     * 创建认证消息
     * @return
     */
    public static MqttMessage createAuthMsg(String deviceId, String serviceId, String deviceType, String secretKey){
        //serviceId
        String userName = "7VT7UCVA7A4PBNAGZGKJ";

        String appId = "064145119";
//        String serviceId = "7VT7UCVA7A4PBNAGZGKJ";
//        String deviceId = "87702080-6a86-32db-aba1-172a9bfd43d8";
        long time = System.currentTimeMillis();

        String password = appId + ";" + serviceId + ";" + deviceId
                + ";" + deviceType + ";" + time;

        //秘钥 MDkxYmJiYjA4MTgxNTkxYw==
//        String secretKey = "MTY0OTI4MDAwMDM4NTcxNQ==";
        //加密password
//        byte[] passwordByte = SecurityTool.encryptSymmetricData(2, password.getBytes(), secretKey);
        byte[] passwordByte = SM4Util.encryptCBC(secretKey, password.getBytes());

        return MqttMessageBuilders.connect()
                .clientId("123456")
                .hasPassword(true)
                .hasUser(true)
                .username(serviceId)
                .password(passwordByte)
                .build();
    }

    public static CherryMessage createPublishMessage(String topic, CherryMessagePayload payload) {
        MqttQoS qos;
        CherryMessagePayloadType type = payload.getCherryMessagePayloadType();
        if (!type.isNeedOfflinePush() && !type.isNeedReceipt()) {
            qos = MqttQoS.AT_MOST_ONCE;
        } else if (!type.isNeedOfflinePush() && type.isNeedReceipt()) {
            qos = MqttQoS.EXACTLY_ONCE;
        } else if (type.isNeedOfflinePush() && type.isNeedReceipt()) {
            qos = MqttQoS.AT_LEAST_ONCE;
        } else {
            qos = MqttQoS.FAILURE;
        }

        byte[] payloadBytes = encode(payload);

        return MqttMessageBuilders.publish()
                .qos(qos)
                .payload(payloadBytes)
                .topicName(topic)
                .messageId(1)
                .build();
    }

    private static byte[] encode(CherryMessagePayload payload){
        byte[] data = payload.getBody();
        CherryMessagePayloadType type = payload.getCherryMessagePayloadType();
        ByteBuffer buffer = ByteBuffer.allocate(FIXED_LENGTH + data.length);
        // 消息体类型
        buffer.put(type.getCode());
        // 预留
        buffer.put((byte) 0x00);
        // 消息id
        buffer.putLong(payload.getId());
        // 时间戳
        buffer.putLong(payload.getTimestamp());
        // 业务数据
        buffer.put(data);

        return buffer.array();
    }

    public static CherryMessage createSubscribeMsg(Set<String> topics){
        MqttMessageBuilders.SubscribeBuilder builder = MqttMessageBuilders.subscribe()
                .messageId(1);
        for (String topic : topics) {
            builder.addSubscription(MqttQoS.AT_MOST_ONCE, topic);
        }
        return builder.build();
    }

    public static CherryMessage createUnsubscribeMsg(Set<String> topics){
        MqttMessageBuilders.UnsubscribeBuilder builder = MqttMessageBuilders.unsubscribe()
                .messageId(1);
        topics.forEach(builder::addTopicFilter);
        return builder.build();
    }
}
