package com.citic.asp.test.sampler;

import com.citic.asp.test.protocal.MessageReceiver;
import com.citic.asp.test.protocal.MessageSender;
import com.citic.asp.test.protocal.MqttManager;
import com.citic.asp.test.protocal.MqttManagerImpl;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.logging.log4j.core.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mqtt抽象sampler，定义mqtt sampler的基本操作
 *
 * @author qcb
 * @date 2021/04/21 09:44.
 */
public abstract class AbstractMqttSampler extends AbstractJavaSamplerClient {

    private final Logger log = LoggerFactory.getLogger(AbstractMqttSampler.class);

    /**
     * 消息服务器HOST
     */
    protected static final String PARAMETER_SERVER_HOST = "server_host";

    /**
     * 消息服务器端口
     */
    protected static final String PARAMETER_SERVER_PORT = "server_port";

    /**
     * 发送消息内容
     */
    protected static final String PARAMETER_MESSAGE = "message";

    /**
     * 消息大小
     */
    private static final String PARAMETER_MESSAGE_SIZE = "message_size";

    /**
     * 服务ID
     */
    protected static final String PARAMETER_SERVICE_ID = "service_id";
    /**
     * 是否需要登录
     */
    protected static final String PARAMETER_NEED_LOGIN = "need_login";
    /**
     * 加密密钥
     */
    private static final String PARAMETER_ENCRYPT_KEY = "encryptKey";
    /**
     * 连接超时
     */
    private static final String PARAMETER_CONNECT_TIMEOUT = "connect_timeout";

    private static final String TCPKEY = "TCP";

    private static final String ERRKEY = "ERR";

    private static final String USERKEY = "USER";

    private static final String DEVICEKEY = "DEVICE";

    private static final String DEVICETYPEKEY = "DEVICETYPE";

    /**
     * socket缓存
     */
    private static final Map<String, Socket> SOCKET_MAP = new ConcurrentHashMap<>();
    /**
     * 账号设备映射MAP
     */
    private static final Map<String, Set<String>> USER_MAP = new ConcurrentHashMap<>();
    /**
     * 消息大小正则表达式
     */
    private static final Pattern MESSAGE_SIZE_PATTERN = Pattern.compile("^[1-9]+[0-9]*[b|k|m]{1}$");
    /**
     * 本地缓存
     */
    private static final Map<String, Object> LOCAL_CACHE = new ConcurrentHashMap<>();

    private static volatile boolean first = true;

    /**
     * 定时任务线程池
     */
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4, new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("mqtt-heartbeat-thread-" + count.getAndIncrement());
            return thread;
        }
    });

    /**
     * MQTT配置
     */
    private MqttConfig mqttConfig;

    protected MessageSender sender = MqttManagerImpl.getInstance();

    protected MessageReceiver receiver = MqttManagerImpl.getInstance();

    protected MqttManager mqttManager = MqttManagerImpl.getInstance();

    private static final ThreadLocal<Map<String, Object>> tp =
            ThreadLocal.withInitial(HashMap::new);

    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    public void setMqttConfig(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        mqttConfig = loadConfig(context);
        log.info("######### Setup Test. Current Thread:{}, MqttConfig:{}", Thread.currentThread().getName(), mqttConfig);
        initConnection();
    }

    /**
     * 初始化连接
     */
    public abstract void initConnection();

    /**
     * 清理工作
     */
    public abstract void cleanup();

//    /**
//     * 获取socket key
//     * @param username
//     * @return
//     */
//    protected String getSocketKey(String username, String deviceId){
//        return TCPKEY+"#" + mqttConfig.getHost()+"#"+mqttConfig.getPort()+"#"+username+"#"+deviceId;
//    }

    protected Exception getError(){
        Map<String, Object> cp = tp.get();
        return (Exception) cp.get(ERRKEY);
    }

    protected String getUser(){
        Map<String, Object> cp = tp.get();
        return (String) cp.get(USERKEY);
    }

    protected void setUser(String user){
        Map<String, Object> cp = tp.get();
        cp.put(USERKEY, user);
    }

    protected void setDevice(String device){
        Map<String, Object> cp = tp.get();
        cp.put(DEVICEKEY, device);
    }

    protected String getDevice(){
        Map<String, Object> cp = tp.get();
        return (String) cp.get(DEVICEKEY);
    }

    protected void setDeviceType(String deviceType){
        Map<String, Object> cp = tp.get();
        cp.put(DEVICETYPEKEY, deviceType);
    }

    protected String getDeviceType(){
        Map<String, Object> cp = tp.get();
        return (String) cp.get(DEVICETYPEKEY);
    }

    /**
     * 加载MQTT配置
     * @param context
     * @return
     */
    protected MqttConfig loadConfig(JavaSamplerContext context){
        Assert.requireNonEmpty(context.getParameter(PARAMETER_SERVER_PORT), "Please set server port.");
        Assert.requireNonEmpty(context.getParameter(PARAMETER_SERVER_HOST), "Please set server host.");
        return new MqttConfig.Builder()
                .message(context.getParameter(PARAMETER_MESSAGE))
                .serviceId(context.getParameter(PARAMETER_SERVICE_ID))
                .host(context.getParameter(PARAMETER_SERVER_HOST))
                .port(Integer.valueOf(context.getParameter(PARAMETER_SERVER_PORT)))
                .needLogin(Boolean.valueOf(context.getParameter(PARAMETER_NEED_LOGIN, "true")))
                .encryptKey(context.getParameter(PARAMETER_ENCRYPT_KEY))
                .messageSize(context.getParameter(PARAMETER_MESSAGE_SIZE))
                .connectTimeout(Integer.valueOf(context.getParameter(PARAMETER_CONNECT_TIMEOUT, "10000")))
                .build();
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultArgs = new Arguments();
        defaultArgs.addArgument(PARAMETER_SERVER_HOST, null);
        defaultArgs.addArgument(PARAMETER_SERVER_PORT, null);
        defaultArgs.addArgument(PARAMETER_CONNECT_TIMEOUT, "10000");
        defaultArgs.addArgument(PARAMETER_MESSAGE, null);
        defaultArgs.addArgument(PARAMETER_MESSAGE_SIZE, null);
        defaultArgs.addArgument(PARAMETER_SERVICE_ID, null);
        defaultArgs.addArgument(PARAMETER_NEED_LOGIN, "true");
        defaultArgs.addArgument(PARAMETER_ENCRYPT_KEY, null);
        return defaultArgs;
    }

    /**
     * Use UTF-8 for encoding of strings
     */
    private static final String ENCODING = "UTF-8";

    /**
     * Factory for creating new {@link SampleResult}s.
     */
    protected SampleResult newSampleResult() {
        SampleResult result = new SampleResult();
        result.setDataEncoding(ENCODING);
        result.setDataType(SampleResult.TEXT);
        return result;
    }

    /**
     * Start the sample request and set the {@code samplerData} to {@code data}.
     *
     * @param result
     *          the sample result to update
     * @param data
     *          the request to set as {@code samplerData}
     */
    protected void sampleResultStart(SampleResult result, String data) {
        result.setSamplerData(data);
        result.sampleStart();
    }

    /**
     * Mark the sample result as {@code end}ed and {@code successful} with an "OK" {@code responseCode},
     * and if the response is not {@code null} then set the {@code responseData} to {@code response},
     * otherwise it is marked as not requiring a response.
     *
     * @param result sample result to change
     * @param response the successful result message, may be null.
     */
    protected void sampleResultSuccess(SampleResult result, /* @Nullable */ String response) {
        result.sampleEnd();
        result.setSuccessful(true);
        result.setResponseCodeOK();
        if (response != null) {
            result.setResponseData(response, ENCODING);
        }
        else {
            result.setResponseData("No response required", ENCODING);
        }
    }

    /**
     * Mark the sample result as @{code end}ed and not {@code successful}, and set the
     * {@code responseCode} to {@code reason}.
     *
     * @param result the sample result to change
     * @param reason the failure reason
     */
    protected void sampleResultFailed(SampleResult result, String reason) {
        result.sampleEnd();
        result.setSuccessful(false);
        result.setResponseCode(reason);
    }

    /**
     * Mark the sample result as @{code end}ed and not {@code successful}, set the
     * {@code responseCode} to {@code reason}, and set {@code responseData} to the stack trace.
     *
     * @param result the sample result to change
     * @param exception the failure exception
     */
    protected void sampleResultFailed(SampleResult result, String reason, Exception exception) {
        sampleResultFailed(result, reason);
        result.setResponseMessage("Exception: " + exception);
        result.setResponseData(getStackTrace(exception), ENCODING);
    }

    /**
     * Return the stack trace as a string.
     *
     * @param exception the exception containing the stack trace
     * @return the stack trace
     */
    protected String getStackTrace(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        super.teardownTest(context);
        log.info("######### Teardown Test. Current Thread:{}. ", Thread.currentThread().getName());
//        MqttManager.getInstance().releaseAllConnection();
        cleanup();
        mqttManager.releaseAllConnections();
        if(!SOCKET_MAP.isEmpty()){
            SOCKET_MAP.clear();
        }
        if(!USER_MAP.isEmpty()){
            USER_MAP.clear();
        }
        if(!LOCAL_CACHE.isEmpty()){
            LOCAL_CACHE.clear();
        }
    }

    private void releaseSocket(){
        if(SOCKET_MAP!=null && !SOCKET_MAP.isEmpty()){
            synchronized (SOCKET_MAP){
                for(Map.Entry<String, Socket> entry : SOCKET_MAP.entrySet()){
                    Socket socket = entry.getValue();
                    if(socket != null && !socket.isClosed()){
                        try {
                            socket.close();
                        } catch (IOException e) {
                            log.error("关闭socket异常", e);
                        }
                    }
                }
            }

        }
        SOCKET_MAP.clear();
        USER_MAP.clear();
        LOCAL_CACHE.clear();
    }

    /**
     * 获取消息内容
     * @return
     */
    protected String getMessage(){
        return (String) LOCAL_CACHE.computeIfAbsent("message", k -> {
            if(mqttConfig == null){
                log.info("==========> MqttConfig为空");
                return "";
            }
            if(mqttConfig.getMessage() != null && mqttConfig.getMessage().length() > 0){
                log.info("==========> MqttConfig.getMessage不为空");
                return mqttConfig.getMessage();
            }
            if(mqttConfig.getMessageSize() != null){
                Matcher matcher = MESSAGE_SIZE_PATTERN.matcher(mqttConfig.getMessageSize());
                if(!matcher.matches()){
                    throw new RuntimeException("消息大小格式错误，消息大小包含b(字节数),k(千字节数),m(兆字节数)");
                }
                String messageSize = mqttConfig.getMessageSize();
                int size = Integer.valueOf(messageSize.substring(0, messageSize.length() - 1));
                String unit = mqttConfig.getMessageSize().substring(messageSize.length() - 1);
                return getContent(size, unit);
            }
            return null;
        });
    }

    public String getContent(int size, String unit){
        if("b".equals(unit)){
            return getContent(size);
        }else if("k".equals(unit)){
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i < size;i++){
                sb.append(getContent(1024));
            }
            return sb.toString();
        }else if("m".equals(unit)){
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i<size;i++){
                sb.append(getContent(1024, "k"));
            }
            return sb.toString();
        }
        return "";
    }


    private String getContent(int length){
        byte[] bytes = new byte[length];
        for(int i=0;i<length;i++){
            //49对应ASCII码为字符1
            bytes[i] = 49;
        }
        return new String(bytes);
    }
}
