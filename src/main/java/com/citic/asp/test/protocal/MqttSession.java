package com.citic.asp.test.protocal;

import org.tio.core.ChannelContext;

import java.io.Serializable;
import java.util.concurrent.BlockingDeque;

/**
 * MQTT连接会话
 *
 * @author qcb
 * @date 2021/10/11 15:09.
 */
public class MqttSession implements Serializable {
    private static final long serialVersionUID = 3772565253527460308L;

    public MqttSession() {
    }

    public MqttSession(ChannelContext channelContext, BlockingDeque messageQueue, String secretKey) {
        this.channelContext = channelContext;
        this.messageQueue = messageQueue;
        this.secretKey = secretKey;
    }

    /**
     * 连接上下文
     */
    private ChannelContext channelContext;

    /**
     * 保存收到的消息
     */
    private BlockingDeque messageQueue;

    /**
     * 密钥
     */
    private String secretKey;

    public ChannelContext getChannelContext() {
        return channelContext;
    }

    public void setChannelContext(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    public BlockingDeque getMessageQueue() {
        return messageQueue;
    }

    public void setMessageQueue(BlockingDeque messageQueue) {
        this.messageQueue = messageQueue;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
