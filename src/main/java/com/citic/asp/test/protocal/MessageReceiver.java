package com.citic.asp.test.protocal;

import com.citic.asp.cmc.core.message.CherryMessage;

/**
 * 消息接收
 *
 * @author qcb
 * @date 2021/04/20 10:51.
 */
public interface MessageReceiver extends BaseOperation{

    /**
     * 接收消息
     * @param mqttSession 连接会话
     * @param readTimeout 读超时时间
     * @return
     */
    CherryMessage receive(MqttSession mqttSession, int readTimeout);

}
