package com.citic.asp.test.protocal;

import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.cmc.core.message.CherryMessagePayload;
import com.citic.asp.cmc.core.message.CherryMessagePayloadType;
import com.citic.asp.cmc.protocol.mqtt.MqttEncoder;
import com.citic.asp.cmc.protocol.mqtt.MqttMessage;
import com.citic.asp.core.util.security.SecurityTool;
import com.citic.asp.core.util.security.gm.SM4Util;
import com.citic.asp.test.protocal.message.ImPayloadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * MQTT基本操作
 *
 * @author qcb
 * @date 2021/04/20 10:53.
 */
public abstract class BaseMqttOperation {

    private final static Logger log = LoggerFactory.getLogger(BaseMqttOperation.class);

    /**
     * 发送publish消息
     * @param messageId 消息ID
     * @param topic  消息主题
     * @param message 消息内容
     * @param payloadType
     * @param os 输出流
     * @param isEncrypted 是否加密
     */
    public void sendMessage(long messageId, String topic, String message, CherryMessagePayloadType payloadType, OutputStream os, boolean isEncrypted, String encryptKey) throws IOException {
        ImPayloadFactory imPayloadFactory = new ImPayloadFactory();
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        if(isEncrypted){
            data = SM4Util.encryptCBC(encryptKey, data);
        }
        CherryMessagePayload payload = imPayloadFactory.createPayload(messageId, payloadType, data, System.currentTimeMillis());
        sendMessage(topic, payload, os);
    }

    /**
     * 发送消息
     * @param topic
     * @param payload
     * @param os
     */
    public void sendMessage(String topic, CherryMessagePayload payload, OutputStream os) throws IOException {
        CherryMessage cherryMessage = MqttMessageFactory.createPublishMessage(topic, payload);
        sendMessage(cherryMessage, os);
    }

    /**
     * 发送消息
     * @param cherryMessage
     * @param os
     * @throws IOException
     */
    public void sendMessage(CherryMessage cherryMessage, OutputStream os) throws IOException {
        byte[] data = MqttEncoder.encode((MqttMessage) cherryMessage).array();
        os.write(data);
        os.flush();
    }

    /**
     * 读取内容
     * @param is
     * @return
     * @throws IOException
     */
    public byte[] readMessage(InputStream is) throws IOException {
        ByteArrayOutputStream w = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int x = 0;
        while ((x = is.read(buffer)) > -1) {
            w.write(buffer, 0, x);
            boolean remaining = is.available() > 0;
            if(!remaining){
                return w.toByteArray();
            }
        }
        return w.toByteArray();
    }
}
