package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.handler.AuthMessageHandler;
import com.citic.asp.cmc.core.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * <dl>mqtt消息工厂
 * <dt>MqttCherryMessageFactory</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/10/22</dd>
 * </dl>
 *
 * @author maoyx
 */
@Component
public class MqttCherryMessageFactory implements CherryMessageFactory {
    @Autowired
    private CherryMessagePayloadCodec cherryMessagePayloadCodec;

    @Override
    public CherryMessage createAuthMessage(String serviceId, byte[] token) {
        return new NettyMessage(MqttMessageBuilders.connect()
                .hasPassword(true)
                .hasUser(true)
                .username(serviceId)
                .password(token)
                .build());
    }

    @Override
    public CherryMessage createAuthAckMessage(AuthMessageHandler.AuthStatus status) {
        MqttConnectReturnCode code;
        switch (status) {
            case SUCCESS:
                code = MqttConnectReturnCode.CONNECTION_ACCEPTED;
                break;
            case FAIL:
                code = MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD;
                break;
            default:
                code = MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
        }

        return new NettyMessage(MqttMessageBuilders.connAck()
                .returnCode(code)
                .build());
    }

    @Override
    public CherryMessage createSubcrebeMessage(Set<String> topics) {
        MqttMessageBuilders.SubscribeBuilder builder = MqttMessageBuilders.subscribe();
        for (String topic : topics) {
            builder.addSubscription(MqttQoS.AT_MOST_ONCE, topic);
        }
        return new NettyMessage(builder.build());
    }

    @Override
    public CherryMessage createUnsubcrebeMessage(Set<String> topics) {
        MqttMessageBuilders.UnsubscribeBuilder builder = MqttMessageBuilders.unsubscribe();
        topics.forEach(builder::addTopicFilter);
        return new NettyMessage(builder.build());
    }

    @Override
    public CherryMessage createPublishMessage(String topic, CherryMessagePayload payload) {
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

        byte[] payloadBytes = cherryMessagePayloadCodec.encode(payload);
        ByteBuf payloadBuf = Unpooled.copiedBuffer(payloadBytes);

        return new NettyMessage(MqttMessageBuilders.publish()
                .qos(qos)
                .payload(payloadBuf)
                .topicName(topic)
                .messageId(1)
                .build());
    }

    @Override
    public CherryMessage createPingRespMessage() {
        return new NettyMessage(MqttMessage.PINGRESP);
    }

    /**
     * 创建心跳消息
     * @return
     */
    @Override
    public CherryMessage createPingMessage(){
        return new NettyMessage(MqttMessage.PINGREQ);
    }
}

