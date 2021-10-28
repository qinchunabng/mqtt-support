package com.citic.asp.test.protocal.message;

/**
 * description
 *
 * @author DELL
 * @date 2021/10/27 18:02.
 */

import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.cmc.core.message.CherryMessageType;
import io.netty.handler.codec.mqtt.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <dl>
 * <dt>NettyMessage</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2021/10/22</dd>
 * </dl>
 *
 * @author maoyx
 */
public class NettyMessage implements CherryMessage {
    /**
     * 消息是否需要转发 默认true 需要， false 不需要
     **/
    private boolean needForward = true;
    private boolean needReceipt = true;
    private boolean needOfflinePush = true;

    private Object payload;

    /**
     * publish消息中payload类型，如0x03单聊
     **/
    private byte payloadType;

    private byte[] token;

    private String serviceId;

    private Set<String> topics;

    private FixedHeader fixedHeader;

    private Object variableHeader;

    private byte cherryMessageType;

    /**
     * 构造消息对象
     *
     * @param mqttMessage mqtt消息对象
     */
    public NettyMessage(MqttMessage mqttMessage) {
        setCherryMessageType(mqttMessage);
        setTopics(mqttMessage);
        setPayload(mqttMessage.payload());
        setNeedOfflinePush(mqttMessage);
        setNeedReceipt(mqttMessage);
        setServiceId(mqttMessage);
        setToken(mqttMessage);
        this.payload = mqttMessage.payload();
        this.fixedHeader = new FixedHeader(mqttMessage.fixedHeader());
        this.variableHeader = mqttMessage.variableHeader();
    }

    public FixedHeader getFixedHeader() {
        return fixedHeader;
    }

    public MqttFixedHeader getMqttFixedHeader(){
        return new MqttFixedHeader(fixedHeader.getMessageType(), fixedHeader.isDup(),
                fixedHeader.getQosLevel(), fixedHeader.isRetain(), fixedHeader.getRemainingLength());
    }

    public Object getVariableHeader() {
        return variableHeader;
    }

    private void setCherryMessageType(MqttMessage mqttMessage) {
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNECT:
                cherryMessageType = CherryMessageType.AUTH;
                return;
            case CONNACK:
                cherryMessageType = CherryMessageType.AUTH_ACK;
                return;
            case PINGREQ:
                cherryMessageType = CherryMessageType.PING;
                return;
            case PINGRESP:
                cherryMessageType = CherryMessageType.PONG;
                return;
            case PUBLISH:
                cherryMessageType = CherryMessageType.PUBLISH;
                return;
            case SUBSCRIBE:
                cherryMessageType = CherryMessageType.SUBSCRIBE;
                return;
            case UNSUBSCRIBE:
                cherryMessageType = CherryMessageType.UNSUBSCRIBE;
                return;
            case DISCONNECT:
                cherryMessageType = CherryMessageType.DISCONNECT;
                return;
            default:
                cherryMessageType = 0x00;
        }
    }

    private void setTopics(MqttMessage mqttMessage) {
        if (mqttMessage instanceof MqttPublishMessage) {
            MqttPublishVariableHeader mqttPublishVariableHeader = (MqttPublishVariableHeader) mqttMessage.variableHeader();
            String topic = mqttPublishVariableHeader.topicName();
            topics = Collections.singleton(topic);
        } else if (mqttMessage instanceof MqttSubscribeMessage) {
            MqttSubscribePayload mqttSubscribePayload = (MqttSubscribePayload) mqttMessage.payload();
            List<MqttTopicSubscription> topicSubscriptions = mqttSubscribePayload.topicSubscriptions();
            topics = topicSubscriptions.stream().map(MqttTopicSubscription::topicName).collect(Collectors.toSet());
        } else if (mqttMessage instanceof MqttUnsubscribeMessage) {
            MqttUnsubscribePayload mqttUnsubscribePayload = (MqttUnsubscribePayload) mqttMessage.payload();
            List<String> topicUnSubscriptions = mqttUnsubscribePayload.topics();
            topics = topicUnSubscriptions.stream().collect(Collectors.toSet());
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                .append('[')
                .append("fixedHeader=").append(fixedHeader != null ? fixedHeader.toString() : "")
                .append(", variableHeader=").append(variableHeader != null ? variableHeader.toString() : "")
                .append(", payload=").append(payload != null ? payload.toString() : "")
                .append(']')
                .toString();
    }


    @Override
    public byte getCherryMessageType() {
        return cherryMessageType;
    }

    @Override
    public Set<String> getTopics() {
        return topics;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    private void setServiceId(MqttMessage mqttMessage) {
        if (mqttMessage instanceof MqttConnectMessage) {
            serviceId = ((MqttConnectPayload) mqttMessage.payload()).userName();
        }
    }

    @Override
    public byte[] getToken() {
        return token;
    }

    private void setToken(MqttMessage mqttMessage) {
        if (mqttMessage instanceof MqttConnectMessage) {
            token = ((MqttConnectPayload) mqttMessage.payload()).passwordInBytes();
        }
    }

    @Override
    public boolean needReceipt() {
        return needReceipt;
    }

    private void setNeedReceipt(MqttMessage mqttMessage) {
        // qos!=0 需要发送回执
        needReceipt = mqttMessage.fixedHeader().qosLevel() != MqttQoS.AT_MOST_ONCE;
    }

    @Override
    public boolean needOfflinePush() {
        // qos==2 需要支持离线推送
        return needOfflinePush;
    }

    private void setNeedOfflinePush(MqttMessage mqttMessage) {
        // qos==2 需要支持离线推送
        needOfflinePush = mqttMessage.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE;
    }

    @Override
    public void setNeedForward(boolean needForward) {
        this.needForward = needForward;
    }

    @Override
    public boolean getNeedForward() {
        return this.needForward;
    }

    @Override
    public void setPayloadType(byte payloadType) {
        this.payloadType = payloadType;
    }

    @Override
    public byte getPayloadType() {
        return this.payloadType;
    }


    public static class FixedHeader implements Serializable {

        private static final long serialVersionUID = 1532056154746586366L;

        private MqttMessageType messageType;
        private boolean isDup;
        private MqttQoS qosLevel;
        private boolean isRetain;
        private int remainingLength;

        public FixedHeader(MqttFixedHeader fixedHeader) {
            this.messageType = fixedHeader.messageType();
            this.isDup = fixedHeader.isDup();
            this.qosLevel = fixedHeader.qosLevel();
            this.isRetain = fixedHeader.isRetain();
            this.remainingLength = fixedHeader.remainingLength();
        }

        public MqttMessageType getMessageType() {
            return messageType;
        }

        public void setMessageType(MqttMessageType messageType) {
            this.messageType = messageType;
        }

        public boolean isDup() {
            return isDup;
        }

        public void setDup(boolean dup) {
            isDup = dup;
        }

        public MqttQoS getQosLevel() {
            return qosLevel;
        }

        public void setQosLevel(MqttQoS qosLevel) {
            this.qosLevel = qosLevel;
        }

        public boolean isRetain() {
            return isRetain;
        }

        public void setRetain(boolean retain) {
            isRetain = retain;
        }

        public int getRemainingLength() {
            return remainingLength;
        }

        public void setRemainingLength(int remainingLength) {
            this.remainingLength = remainingLength;
        }

        @Override
        public String toString() {
            return "FixedHeader{" +
                    "messageType=" + messageType +
                    ", isDup=" + isDup +
                    ", qosLevel=" + qosLevel +
                    ", isRetain=" + isRetain +
                    ", remainingLength=" + remainingLength +
                    '}';
        }
    }
}
