package com.citic.asp.test.protocal;

import com.alibaba.fastjson.JSON;
import com.citic.asp.cmc.core.message.*;
import com.citic.asp.core.util.security.SecurityTool;
import com.citic.asp.core.util.security.gm.SM4Util;
import com.citic.asp.core.util.string.IdUtil;
import com.citic.asp.test.protocal.message.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientTioConfig;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.Tio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;

/**
 * MQTT
 *
 * @author qcb
 * @date 2021/04/20 11:19.
 */
public class MqttManagerImpl implements MqttManager{

    private final Logger log = LoggerFactory.getLogger(MqttManagerImpl.class);

    /**
     * 单聊消息topic，格式：/im/user/【serviceId】/【userId】
     */
    protected static final String USER_MESSAGE_TOPIC = "/im/user/%s/%s";

    /**
     * 群聊消息topic，格式：/im/group/【serviceId】/【groupId】
     */
    protected static final String GROUP_MESSAGE_TOPIC = "/im/group/%s/%s";

    /**
     * 上线消息topic，格式：/online/user/【serviceId】/【userId】
     */
    protected static final String ONLINE_MESSAGE_TOPIC = "/online/user/%s/%s";

    /**
     * 下线消息topic，格式：/offline/user/【serviceId】/【userId】
     */
    protected static final String OFFLINE_MESSAGE_TOPIC = "/offline/user/%s/%s";

    /**
     * 设备消息topic，格式：/im/device/【serviceId】/【groupId】
     */
    protected static final String DEVICE_MESSAGE_TOPIC = "/im/device/%s/%s";

    private static final String SESSION_KEY = "SESSION_KEY";

    private static final String TCPKEY = "TCP";

    private static final String USERKEY = "USER";

    private static final String DEVICEKEY = "DEVICE";

    /**
     * 保存连接
     */
    private static final Map<String, MqttSession> SESSION_MAP = new ConcurrentHashMap<>();
    /**
     * 保存消息回执CountDownLatch
     */
    private static final Map<Long, CountDownLatch> WAIT_MAP = new ConcurrentHashMap<>();

    private static final ClientTioConfig clientTioConfig = new ClientTioConfig(new CmcClientAioHandler(),
            new CmcClientAioListener(), new ReconnConf(5000L, 3));

    private static volatile TioClient tioClient = null;

    private static volatile MqttManagerImpl INSTANCE;

    private MqttManagerImpl(){}

    public static MqttManagerImpl getInstance(){
        if(INSTANCE == null){
            synchronized (MqttManagerImpl.class){
                if(INSTANCE == null){
                    INSTANCE = new MqttManagerImpl();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化TioClient
     * @throws IOException
     */
    private void initClient() throws IOException {
        if(tioClient == null){
            synchronized (clientTioConfig){
                if(tioClient == null){
                    tioClient = new TioClient(clientTioConfig);
                }
            }
        }
    }

    /**
     * 发送消息
     * @param message 消息内容
     * @param messageType 消息类型
     *        @see ImPayloadType
     * @param channelContext 连接上下文
     * @param topic 消息主题
     * @param encryptKey 加密密钥
     * @param waitResponse 是否等待回执
     * @param sendTimeout 发送超时时间
     * @return 如果waitResponse为true，即需要等待服务器返回消息回执，收到服务器的消息回执返回为true，超过指定的等待时间没有收到消息回执返回false
     *         如果waitResponse为false，默认返回true
     */
    @Override
    public boolean send(String message, int messageType, ChannelContext channelContext, String topic, String encryptKey, boolean waitResponse, int sendTimeout) throws IOException {
        CherryMessagePayloadType payloadType = ImPayloadType.valueOf((byte) messageType);
        if(payloadType == null){
            throw new IllegalArgumentException("Message type is incorrect.");
        }
        long messageId = IdUtil.nextId();
        sendMessage(messageId, topic, message, payloadType, channelContext, true, encryptKey);
//        log.info("发送消息,messageId:{}, message:{}", messageId, message);
        log.info("发送消息,messageId:{}", messageId);
        boolean result = true;
        //如果需要等效消息回执，执行等待操作，
        if(waitResponse){
            CountDownLatch countDownLatch = new CountDownLatch(1);
            WAIT_MAP.put(messageId, countDownLatch);
            try {
                result = countDownLatch.await(sendTimeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("等待发送回执异常", e);
            }
            WAIT_MAP.remove(messageId);
        }
        return result;
    }

    /**
     * 发送publish消息
     * @param messageId 消息ID
     * @param topic  消息主题
     * @param message 消息内容
     * @param payloadType
     * @param channelContext 连接上下文
     * @param isEncrypted 是否加密
     */
    public void sendMessage(long messageId, String topic, String message, CherryMessagePayloadType payloadType, ChannelContext channelContext, boolean isEncrypted, String encryptKey) throws IOException {
        ImPayloadFactory imPayloadFactory = new ImPayloadFactory();
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        if(isEncrypted){
            data = SM4Util.encryptCBC(encryptKey, data);
        }
        CherryMessagePayload payload = imPayloadFactory.createPayload(messageId, payloadType, data, System.currentTimeMillis());
        CherryMessage cherryMessage = MqttMessageFactory.createPublishMessage(topic, payload);
        doSendMessage(cherryMessage, channelContext);
    }

    /**
     * 发送单聊消息
     * @param message 单聊消息
     * @param serviceId 服务ID
     * @param toUser 接收人
     * @param channelContext 连接上下文
     */
    @Override
    public void sendSingleMessage(String message, String serviceId, String toUser, ChannelContext channelContext, String encryptKey,boolean waitResponse, int sendTimeout) throws IOException {
        send(message, ImPayloadType.SINGLE_CHAT.getCode(), channelContext, String.format(USER_MESSAGE_TOPIC, serviceId, toUser), encryptKey, waitResponse, sendTimeout);
    }

    /**
     * 发送设备消息
     * @param message 消息内容
     * @param serviceId 服务ID
     * @param toDevice 接收设备ID
     * @param channelContext 连接上下文
     * @param encryptKey 加密密钥
     */
    @Override
    public void sendDeviceMessage(String message, String serviceId, String toDevice, ChannelContext channelContext, String encryptKey) throws IOException {
        send(message, ImPayloadType.SINGLE_CHAT.getCode(), channelContext, String.format(DEVICE_MESSAGE_TOPIC, serviceId, toDevice), encryptKey, false, 0);
    }

    /**
     * 发送群聊消息
     * @param message 群消息内容
     * @param serviceId 服务ID
     * @param groupId 群ID
     * @param channelContext 连接上下文
     * @param encryptKey 加密key
     */
    @Override
    public void sendGroupMessage(String message, String serviceId, String groupId, ChannelContext channelContext, String encryptKey,boolean waitResponse, int sendTimeout) throws IOException {
        send(message, ImPayloadType.GROUP_CHAT.getCode(), channelContext, String.format(GROUP_MESSAGE_TOPIC, serviceId, groupId), encryptKey, waitResponse, sendTimeout);
    }

    /**
     * 认证
     * @param serviceId 服务ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param encryptKey 加密密钥
     * @param channelContext 连接上下文
     */
    @Override
    public void auth(String serviceId, String deviceId, String deviceType, String encryptKey, ChannelContext channelContext) throws IOException {
        //发送认证消息
        log.info("======> 发送认证消息,serviceId:{},deviceId:{},deviceType:{}", serviceId, deviceId, deviceType);
        CherryMessage authMessage = MqttMessageFactory.createAuthMsg(deviceId, serviceId, deviceType, encryptKey);
        doSendMessage(authMessage, channelContext);
    }

    /**
     * 发送消息
     * @param message
     * @param channelContext
     */
    private void doSendMessage(CherryMessage message, ChannelContext channelContext){
        CherryMessageConverter<CherryPacket> converter = new CimioCherryMessageConverter();
        CherryPacket packet = converter.convert(message);
        Tio.send(channelContext, packet);
    }

    /**
     * 上线操作
     * @param serviceId 服务ID
     * @param userId 用户ID
     * @param channelContext 连接上下文
     */
    @Override
    public void online(String serviceId, String userId, ChannelContext channelContext) throws IOException {
        //发送上线消息
        log.info("======> 发送上线消息,serviceId:{},userId:{}", serviceId, userId);
        CherryMessage onlineMessage = MqttMessageFactory.createSubscribeMsg(Sets.newHashSet(String.format(ONLINE_MESSAGE_TOPIC, serviceId,userId)));
        doSendMessage(onlineMessage, channelContext);
    }

    private byte[] getReceiptData(int status) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(9);
        //状态1
        byteBuffer.put((byte) status);
        //时间戳
        byteBuffer.putLong(System.currentTimeMillis());

        return byteBuffer.array();
    }

    /**
     * 发送消息
     * @param topic
     * @param payload
     * @param channelContext
     */
    private void sendMessage(String topic, CherryMessagePayload payload, ChannelContext channelContext){
        CherryMessage cherryMessage = MqttMessageFactory.createPublishMessage(topic, payload);
        doSendMessage(cherryMessage, channelContext);
    }

    /**
     * 接收消息
     * @param cherryMessage
     * @param channelContext 连接上下文
     * @return
     */
    @Override
    public void receive(CherryMessage message, ChannelContext channelContext) throws IOException {
        if (message.getCherryMessageType() == CherryMessageType.PUBLISH){
            //将消息放入到对应的连接会话的队列中
            String sessionKey = getSessionKey(channelContext);
            if(sessionKey != null){
                MqttSession mqttSession = SESSION_MAP.get(sessionKey);
                if(mqttSession != null){
                    mqttSession.getMessageQueue().add(message);
                }
            }

            //发布消息payload部分
            byte[] payload = (byte[]) message.getPayload();
            CherryMessagePayloadCodec cherryMessagePayloadCodec = new ImPayloadCodec();
            // 解析成消息体对象
            CherryMessagePayload cherryMessagePayload = cherryMessagePayloadCodec.decode(payload);
            long messageId = cherryMessagePayload.getId();
            log.info("????????????? 收到消息，将消息加入队列，messageId:{}", messageId);
            if(cherryMessagePayload.getBody() != null){
                ImPayloadType payloadType = (ImPayloadType)cherryMessagePayload.getCherryMessagePayloadType();
                ImPayloadFactory imPayloadFactory = new ImPayloadFactory();
                if(payloadType == ImPayloadType.SINGLE_CHAT){
                    //单聊消息
                    //消息内容解析
                    String decryptData = new String(SecurityTool.decryptSymmetricData(2, cherryMessagePayload.getBody(),
                            SecurityTool.getCommonKey(2)));
                    if(StringUtils.isEmpty(decryptData)){
                        return;
                    }
                    SingleMessage singleMessage = JSON.parseObject(decryptData, SingleMessage.class);
                    //收到单聊消息
                    log.info("########## 单聊消息ID:{},消息内容:{}", messageId, singleMessage);
                    //单聊发送单聊已读回执
                    try {
                        CherryMessagePayload singleReceiptPayload = imPayloadFactory.createPayload(messageId, ImPayloadType.RECEIPT_SINGLE, getReceiptData(ImReceiptStatus.RECEIVE.getCode()), System.currentTimeMillis());
                        sendMessage(String.format(USER_MESSAGE_TOPIC, singleMessage.getServiceId(), singleMessage.getFromUser()), singleReceiptPayload, channelContext);
                        log.info(">>>>>>>>>> 收到单聊消息，messageId:{},消息内容：{}", messageId, singleMessage);
                    }catch (Exception e) {
                        log.error("发送单聊已读回执失败", e);
                    }

                }else if(payloadType == ImPayloadType.RECEIPT_SINGLE || payloadType == ImPayloadType.RECEIPT_GROUP){
                    //收到系统回执消息，唤醒发送消息的线程
                    log.debug(">>>>>>>>>>>>>> 收到单聊系统回执消息,messageId:{}", messageId);
                    CountDownLatch countDownLatch = WAIT_MAP.get(messageId);
                    if(countDownLatch != null){
                        countDownLatch.countDown();
                    }
                }else if(payloadType == ImPayloadType.GROUP_CHAT){
                    //群消息
                    //消息内容解析
                    String decryptData = new String(SecurityTool.decryptSymmetricData(2, cherryMessagePayload.getBody(),
                            SecurityTool.getCommonKey(2)));
                    if(StringUtils.isEmpty(decryptData)){
                        return;
                    }
                    GroupMessage groupMessage = JSON.parseObject(decryptData, GroupMessage.class);
                    //收到群聊消息
                    log.info("########## 群聊消息ID:{},消息内容:{}", messageId, groupMessage);
                    //单聊发送群聊已读回执
                    try {
                        CherryMessagePayload groupReceiptPayload = imPayloadFactory.createPayload(messageId, ImPayloadType.RECEIPT_GROUP, getReceiptData(ImReceiptStatus.RECEIVE.getCode()), System.currentTimeMillis());
                        sendMessage(String.format(GROUP_MESSAGE_TOPIC, groupMessage.getServiceId(), groupMessage.getGroupId()), groupReceiptPayload, channelContext);
                        log.info(">>>>>>>>>> 收到群聊消息，messageId:{},消息内容：{}", messageId, groupMessage);
                    }catch (Exception e) {
                        log.error("发送群聊已读回执失败", e);
                    }
                }
            }
        }
    }

//    /**
//     * 发送心跳消息
//     * @param os 输出流
//     */
//    @Override
//    public void sendPing(OutputStream os) throws IOException {
//        sendMessage(MqttMessageFactory.createPingMsg(), os);
//    }

    /**
     * 创建连接
     * @param host
     * @param port
     * @param timeout
     * @return
     */
    @Override
    public ChannelContext connect(String host, int port, int timeout) throws Exception {
        initClient();
        return tioClient.connect(new Node(host, port), timeout);
    }

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
     * @return
     */
    @Override
    public MqttSession getConnection(String host, int port, int timeout, String serviceId, String userId, String deviceId, String deviceType, String encryptKey, boolean needLogin){
        return SESSION_MAP.computeIfAbsent(getSessionKey(host, port, userId, deviceId), sessionKey -> {
            try {
                ChannelContext channelContext = connect(host, port, timeout);
                auth(serviceId, deviceId, deviceType, encryptKey, channelContext);
                if(needLogin){
                    online(serviceId, userId, channelContext);
                }
                return new MqttSession(channelContext, new LinkedBlockingDeque());
            } catch (Exception e) {
                log.error("获取连接失败, userId:"+ userId + ",deviceId:" + deviceId, e);
                return null;
            }
        });
    }

    /**
     * 移除连接
     * @param channelContext
     */
    @Override
    public void removeConnection(ChannelContext channelContext){
        String sessionKey = getSessionKey(channelContext);
        SESSION_MAP.remove(sessionKey);
    }

    /**
     * 获取sessionKey
     * @param channelContext
     * @return
     */
    @Override
    public String getSessionKey(ChannelContext channelContext){
        if(channelContext == null){
            return null;
        }
        return (String) channelContext.get(SESSION_KEY);
    }

    private void setSessionKey(ChannelContext channelContext, String sessionKey){
        channelContext.set(SESSION_KEY, sessionKey);
    }

    /**
     * 获取sessionKey
     * @param host 服务host
     * @param port 服务端口
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return
     */
    @Override
    public String getSessionKey(String host, int port, String userId, String deviceId){
        return TCPKEY+"#" + host + "#" + port + "#" + userId + "#" + deviceId;
    }
}
