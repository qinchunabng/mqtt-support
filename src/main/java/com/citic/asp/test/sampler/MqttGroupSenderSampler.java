package com.citic.asp.test.sampler;

import com.alibaba.fastjson.JSON;
import com.citic.asp.test.loader.Account;
import com.citic.asp.test.protocal.GroupMessage;
import com.citic.asp.test.protocal.MessageType;
import com.citic.asp.test.protocal.MqttSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * mqtt群消息sampler
 *
 * @author qcb
 * @date 2021/04/23 11:45.
 */
public class MqttGroupSenderSampler extends AbstractMqttSampler{

    private static final Logger log = LoggerFactory.getLogger(MqttSenderSampler.class);

    private static final String PARAMETER_FROM_USERNAME = "from_user";

    private static final String PARAMETER_FROM_USER_PASSWORD = "from_user_password";

    private static final String PARAMETER_GROUP_ID = "group_id";

    private static final String PARAMETER_GROUP_NAME = "group_name";

    /**
     * 发送消息超时时间
     */
    private static final String PARAMETER_SEND_TIMEOUT = "send_timeout";

    /**
     * 等待回执
     */
    private static final String PARAMETER_WAIT_RESPONSE = "wait_response";

    /**
     * 发送设备
     */
    private static final String PARAMETER_FROM_DEVICE = "from_device";
    /**
     * 发送设备类型
     */
    private static final String PARAMETER_FROM_DEVICE_TYPE = "from_device_type";

    /**
     * 是否第一次
     */
    private static volatile boolean first = true;

    /**
     * 发送人账号
     */
    private static volatile List<Account> SEND_ACCOUNTS = null;
    /**
     * 初始化锁对象
     */
    private static final Object initialLock = new Object();

    /**
     * 发送群消息取样器线程池
     */
    private static ExecutorService GROUP_SENDER_THREAD_POOL = new ThreadPoolExecutor(1, 4,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>(50), new ThreadFactory() {

        private AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "GroupSenderPool-Thread-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }, new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        //获取配置信息
        MqttConfig mqttConfig = getMqttConfig();
        String fromUser = context.getParameter(PARAMETER_FROM_USERNAME);
        setUser(fromUser);
        String fromUserPassword = context.getParameter(PARAMETER_FROM_USER_PASSWORD);
        String fromDevice = context.getParameter(PARAMETER_FROM_DEVICE);
        setDevice(fromDevice);
        String fromDeviceType = context.getParameter(PARAMETER_FROM_DEVICE_TYPE);
        setDeviceType(fromDeviceType);
        String groupId = context.getParameter(PARAMETER_GROUP_ID);
        String groupName = context.getParameter(PARAMETER_GROUP_NAME);
        int sendTimeout = Integer.valueOf(context.getParameter(PARAMETER_SEND_TIMEOUT, "5000"));
        boolean waitResponse = Boolean.parseBoolean(context.getParameter(PARAMETER_WAIT_RESPONSE, "true"));

        //获取socket连接
        log.info("===========发送群消息, 当前fromUser:{}, fromDevice:{}, fromDeviceType:{}, groupId:{}", fromUser, fromDevice, fromDeviceType, groupId);
        MqttSession session = mqttManager.getConnection(mqttConfig.getHost(), mqttConfig.getPort(),mqttConfig.getConnectTimeout(), mqttConfig.getServiceId(),
                fromUser, fromDevice, fromDeviceType, mqttConfig.getEncryptKey(), mqttConfig.isNeedLogin());
        SampleResult result = newSampleResult();
        sampleResultStart(result, getMessage());
        if(session == null){
            sampleResultFailed(result, "500", getError());
            return result;
        }

        if(StringUtils.isNotEmpty(fromUser) || StringUtils.isNotEmpty(groupId)){
            GroupMessage groupMessage = new GroupMessage.Builder()
                    .messageType(MessageType.TEXT.getCode())
                    .fromUser(fromUser)
                    .serviceId(mqttConfig.getServiceId())
                    .groupId(groupId)
                    .groupName(groupName)
                    .textContent(getMessage())
                    .build();
            String message = JSON.toJSONString(groupMessage);
            try{
                boolean success = sender.sendGroupMessage(message, mqttConfig.getServiceId(), groupId, session.getChannelContext(), mqttConfig.getEncryptKey(), waitResponse, sendTimeout);
                //判断是否发送成功
                if(success){
                    sampleResultSuccess(result, "OK");
                }else{
                    log.error("===========接收群聊消息回执超时, 当前fromUser:{}, fromDevice:{}, fromDeviceType:{}, groupId:{}", fromUser, fromDevice, fromDeviceType, groupId);
                    sampleResultFailed(result, "504", new RuntimeException("等待消息回执超时"));
                }
            }catch (Exception e){
                log.error("Send group message error", e);
                sampleResultFailed(result, "500", e);
            }
        }else{
            sampleResultFailed(result, "500", new RuntimeException("发送人或群ID为空"));
        }
        return result;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultArgs = super.getDefaultParameters();
        defaultArgs.addArgument(PARAMETER_FROM_USERNAME, "${fromUser}");
        defaultArgs.addArgument(PARAMETER_FROM_USER_PASSWORD, "${fromUserPassword}");
        defaultArgs.addArgument(PARAMETER_FROM_DEVICE, "${fromDevice}");
        defaultArgs.addArgument(PARAMETER_GROUP_ID, "${groupId}");
        defaultArgs.addArgument(PARAMETER_GROUP_NAME, "${groupName}");
        defaultArgs.addArgument(PARAMETER_SEND_TIMEOUT, "2000");
        return defaultArgs;
    }

    public static void setSendAccounts(List<Account> accountList){
        SEND_ACCOUNTS = accountList;
    }

    @Override
    public void initConnection() {
//        if(first){
//            synchronized (initialLock) {
//                if (first && SEND_ACCOUNTS != null && SEND_ACCOUNTS.size() > 0) {
//                    for (Account account : SEND_ACCOUNTS) {
//                        GROUP_SENDER_THREAD_POOL.execute(() -> {
//                            mqttManager.getConnection(getMqttConfig().getHost(), getMqttConfig().getPort(), getMqttConfig().getConnectTimeout(),
//                                    getMqttConfig().getServiceId(), account.getUsername(), account.getDeviceId(), account.getDeviceType(), getMqttConfig().getEncryptKey(),
//                                    getMqttConfig().isNeedLogin());
//                            log.info("=====> 创建连接:{}", account);
//                        });
//                    }
//                }
//                first = false;
//            }
//        }
    }

    @Override
    public void cleanup() {
        SEND_ACCOUNTS = null;
        first = true;
    }
}
