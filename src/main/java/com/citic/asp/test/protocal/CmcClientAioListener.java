package com.citic.asp.test.protocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

/**
 * description
 *
 * @author DELL
 * @date 2021/03/16 11:16.
 */
public class CmcClientAioListener implements ClientAioListener {

    private final Logger logger = LoggerFactory.getLogger(CmcClientAioListener.class);

    @Override
    public void onAfterConnected(ChannelContext channelContext, boolean b, boolean b1) throws Exception {

    }

    @Override
    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int i) throws Exception {

    }

    @Override
    public void onAfterReceivedBytes(ChannelContext channelContext, int i) throws Exception {

    }

    @Override
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean b) throws Exception {

    }

    @Override
    public void onAfterHandled(ChannelContext channelContext, Packet packet, long l) throws Exception {

    }

    @Override
    public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String s, boolean b) throws Exception {
        logger.error(String.format("连接断开原因:%s,channelContext:%s", s, channelContext.toString()), throwable);
        MqttManager mqttManager = MqttManagerImpl.getInstance();
        mqttManager.removeConnection(channelContext);
    }
}
