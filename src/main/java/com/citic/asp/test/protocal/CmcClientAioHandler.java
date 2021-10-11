package com.citic.asp.test.protocal;

import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.cmc.protocol.mqtt.MqttDecoder;
import com.citic.asp.cmc.protocol.mqtt.MqttEncoder;
import com.citic.asp.cmc.protocol.mqtt.MqttMessage;
import com.citic.asp.test.protocal.message.CherryPacket;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;

import java.nio.ByteBuffer;

/**
 * description
 *
 * @author DELL
 * @date 2021/03/16 11:17.
 */
public class CmcClientAioHandler implements ClientAioHandler {

    @Override
    public Packet heartbeatPacket(ChannelContext channelContext) {
        MqttMessage pingMsg = MqttMessageFactory.createPingMsg();
        return new CherryPacket(pingMsg);
    }

    @Override
    public Packet decode(ByteBuffer byteBuffer, int i, int i1, int i2, ChannelContext channelContext) throws AioDecodeException {
        return new CherryPacket(MqttDecoder.decode(byteBuffer));
    }

    @Override
    public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
        CherryPacket cherryPacket = (CherryPacket) packet;
        return MqttEncoder.encode((MqttMessage) cherryPacket.getCherryMessage());
    }

    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        CherryPacket cherryPacket = (CherryPacket) packet;
        CherryMessage cherryMessage = cherryPacket.getCherryMessage();
        MqttManager mqttManager = MqttManagerImpl.getInstance();
        mqttManager.receive(cherryMessage, channelContext);
    }
}
