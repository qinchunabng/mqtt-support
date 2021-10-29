package com.citic.asp.test.protocal;

import io.netty.channel.Channel;

import java.io.Serializable;
import java.util.concurrent.BlockingDeque;

/**
 * MQTT连接会话
 *
 * @author qcb
 * @date 2021/10/11 15:09.
 */
public class MqttSession<T> implements Serializable {
    private static final long serialVersionUID = 3772565253527460308L;

    public MqttSession() {
    }

    public MqttSession(T channel, BlockingDeque messageQueue, String secretKey) {
        this.channel = channel;
        this.messageQueue = messageQueue;
        this.secretKey = secretKey;
    }

    public MqttSession(T channel) {
        this.channel = channel;
    }

    /**
     * 连接上下文
     */
    private T channel;

    /**
     * 保存收到的消息
     */
    private BlockingDeque messageQueue;

    /**
     * 密钥
     */
    private String secretKey;

    public T getChannel() {
        return channel;
    }

    public void setChannel(T channel) {
        this.channel = channel;
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
