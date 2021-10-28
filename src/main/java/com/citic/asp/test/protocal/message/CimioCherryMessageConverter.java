package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.cmc.core.message.CherryMessageConverter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;

/**
 * <dl>cim-io消息转换类
 * <dt>CimioCherryMessageConverter</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/10/21</dd>
 * </dl>
 *
 * @author maoyx
 */
public class CimioCherryMessageConverter implements CherryMessageConverter<MqttMessage> {

    @Override
    public MqttMessage convert(CherryMessage message) {
        NettyMessage nettyMessage = (NettyMessage) message;
        if (nettyMessage.getPayload() instanceof byte[]) {
            nettyMessage.setPayload(Unpooled.copiedBuffer((byte[]) nettyMessage.getPayload()));
        }
        return MqttMessageFactory.newMessage(nettyMessage.getMqttFixedHeader(), nettyMessage.getVariableHeader(), nettyMessage.getPayload());
    }

    @Override
    public CherryMessage convert(MqttMessage message) {
        CherryMessage cherryMessage = new NettyMessage(message);
        if (message == null || message.payload() == null) {
            return cherryMessage;
        } else if (message.payload() instanceof ByteBuf) {
            byte[] payload = ByteBufUtil.getBytes((ByteBuf) message.payload());
            cherryMessage.setPayload(payload);
        }
        return cherryMessage;
    }
}
