package com.citic.asp.test.protocal;

import com.citic.asp.cmc.core.message.CherryMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 *
 *
 * @author qcb
 * @date 2021/10/11 14:44.
 */
public interface MqttManager extends MessageSender, MessageReceiver{

    /**
     * 获取连接
     * @param host 服务端host
     * @param port 服务端口
     * @param timeout 连接超时时间
     * @param serviceId 服务ID
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param encryptKey 加密密钥
     * @param needLogin 是否需要登录
     * @return
     */
    MqttSession getConnection(String host, int port, int timeout, String serviceId, String userId, String deviceId, String deviceType, String encryptKey, boolean needLogin);

    /**
     * 获取连接会话
     * @param sessionKey
     * @return
     */
    MqttSession getConnection(String sessionKey);

    /**
     * 移除连接
     * @param session
     */
    void removeConnection(MqttSession session);

    /**
     * 获取sessionKey
     * @param session
     * @return
     */
    String getSessionKey(MqttSession session);

    /**
     * 获取sessionKey
     * @param host 服务host
     * @param port 服务端口
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return
     */
    String getSessionKey(String host, int port, String userId, String deviceId);

    /**
     * 接收消息
     * @param cherryMessage
     * @param session 连接会话
     * @return
     */
    void receive(CherryMessage cherryMessage, MqttSession session) throws IOException;

    /**
     * 释放所有的连接
     */
    void releaseAllConnections();
}
