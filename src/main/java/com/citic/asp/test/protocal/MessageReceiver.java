package com.citic.asp.test.protocal;

import com.citic.asp.cmc.core.message.CherryMessage;
import org.tio.core.ChannelContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * 消息接收
 *
 * @author qcb
 * @date 2021/04/20 10:51.
 */
public interface MessageReceiver extends BaseOperation{

    /**
     * 接收消息
     * @param cherryMessage
     * @param channelContext 连接上下文
     * @return
     */
    void receive(CherryMessage cherryMessage, ChannelContext channelContext) throws IOException;

}
