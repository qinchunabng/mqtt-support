package com.citic.asp.test.sampler;

import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.test.loader.Account;
import com.citic.asp.test.protocal.MqttManager;
import com.citic.asp.test.protocal.MqttManagerImpl;
import com.citic.asp.test.protocal.MqttSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * mqtt消息接收sampler
 *
 * @author qcb
 * @date 2021/04/22 11:01.
 */
public class MqttReceiverSampler extends AbstractMqttSampler{

    private static final Logger log = LoggerFactory.getLogger(MqttReceiverSampler.class);

    private static final String PARAMETER_TO_USERNAME = "to_user";

    private static final String PARAMETER_TO_DEVICE = "to_device";

    private static final String PARAMETER_RECEIVE_TIMEOUT = "receive_timeout";

    private static final String PARAMETER_TO_DEVICE_TYPE = "to_device_type";

    /**
     * 接收账号
     */
    private static volatile List<Account> RECEIVE_ACCOUNTS = null;

    /**
     * 是否第一次
     */
    private static volatile boolean first = true;
    /**
     * 初始化锁对象
     */
    private static final Object initialLock = new Object();

    /**
     * 接收取样器线程池
     */
    private static ExecutorService RECEIVER_THREAD_POOL = new ThreadPoolExecutor(1, 4,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>(50), new ThreadFactory() {

        private AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "ReceiverPool-Thread-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }, new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        MqttConfig mqttConfig = getMqttConfig();
        String toUser = context.getParameter(PARAMETER_TO_USERNAME);
        setUser(toUser);
        String toDevice = context.getParameter(PARAMETER_TO_DEVICE);
        setDevice(toDevice);
        String deviceType = context.getParameter(PARAMETER_TO_DEVICE_TYPE);
        setDeviceType(deviceType);
        int receiveTimeout = Integer.valueOf(context.getParameter(PARAMETER_RECEIVE_TIMEOUT, "3000"));

        //获取连接上下文
        //获取socket连接
        log.info("=========== 当前 toUser:{}, toDevice:{}, deviceType:{}", toUser, toDevice, deviceType);
        MqttSession session = mqttManager.getConnection(mqttConfig.getHost(), mqttConfig.getPort(),mqttConfig.getConnectTimeout(), mqttConfig.getServiceId(),
                toUser, toDevice, deviceType, mqttConfig.getEncryptKey(), mqttConfig.isNeedLogin());
        SampleResult result = newSampleResult();
        sampleResultStart(result, "");
        if(session == null){
            sampleResultFailed(result, "500", getError());
            return result;
        }

        if(StringUtils.isNotEmpty(toUser)){
            try{
//                log.info("=============> 开始接收消息,sessionKey:{}, session:{}, currentTime:{}", mqttManager.getSessionKey(session.getChannelContext()), session, System.currentTimeMillis());
                CherryMessage message = receiver.receive(session, receiveTimeout);
//                log.info("=============> 接收消息结束,sessionKey:{}, session:{}, message:{}, currentTime:{}", mqttManager.getSessionKey(session.getChannelContext()), session, message, System.currentTimeMillis());
                if(message == null){
                    sampleResultFailed(result, "504", new RuntimeException("接收消息超时"));
                }else{
                    sampleResultSuccess(result, message.toString());
                }
            }catch (Exception e){
                log.error("Receive message error", e);
                sampleResultFailed(result, "500", e);
            }
        }else{
            sampleResultFailed(result, "500", new RuntimeException("发送人或接收人为空"));
        }
        return result;
    }

    public static void setReceiveAccounts(List<Account> accountList){
        RECEIVE_ACCOUNTS = accountList;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultArgs = super.getDefaultParameters();
        defaultArgs.addArgument(PARAMETER_TO_USERNAME, "${toUser}");
        defaultArgs.addArgument(PARAMETER_RECEIVE_TIMEOUT, "3000");
        defaultArgs.addArgument(PARAMETER_TO_DEVICE, "${toDevice}");
        defaultArgs.addArgument(PARAMETER_TO_DEVICE_TYPE, "${toDeviceType}");
        return defaultArgs;
    }

    /**
     * 初始化连接
     */
    @Override
    public void initConnection() {
        if(first){
            synchronized (initialLock) {
                if (first && RECEIVE_ACCOUNTS != null && RECEIVE_ACCOUNTS.size() > 0) {
                    for (Account account : RECEIVE_ACCOUNTS) {
                        RECEIVER_THREAD_POOL.execute(() -> {
                            mqttManager.getConnection(getMqttConfig().getHost(), getMqttConfig().getPort(), getMqttConfig().getConnectTimeout(),
                                    getMqttConfig().getServiceId(), account.getUsername(), account.getDeviceId(), account.getDeviceType(), getMqttConfig().getEncryptKey(),
                                    getMqttConfig().isNeedLogin());
                            log.info("=====> 创建连接:{}", account);
                        });
                    }
                }
                first = false;
            }
        }
    }

    /**
     * 清理工作
     */
    @Override
    public void cleanup(){
        RECEIVE_ACCOUNTS = null;
        first = true;
    }
}
