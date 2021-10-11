package com.citic.asp.test.sampler;

import com.alibaba.fastjson.JSON;
import com.citic.asp.test.protocal.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQTT发送消息Sampler
 *
 * @author qcb
 * @date 2021/04/19 18:20.
 */
public class MqttSenderSampler extends AbstractMqttSampler {

    private static final Logger log = LoggerFactory.getLogger(MqttSenderSampler.class);

    private static final String PARAMETER_FROM_USERNAME = "from_user";

    private static final String PARAMETER_FROM_USER_PASSWORD = "from_user_password";

    private static final String PARAMETER_TO_USERNAME = "to_user";

    /**
     * 发送消息超时时间
     */
    private static final String PARAMETER_SEND_TIMEOUT = "send_timeout";
    /**
     * 读超时时间
     */
    private static final String PARAMETER_READ_TIMEOUT = "read_timeout";
    /**
     * 等待回执
     */
    private static final String PARAMETER_WAIT_RESPONSE = "wait_response";

    /**
     * 是否发送设备消息
     */
    private static final String PARAMETER_SEND_DEVICE_MSG = "send_device_message";
    /**
     * 发送设备
     */
    private static final String PARAMETER_FROM_DEVICE = "from_device";
    /**
     * 发送设备类型
     */
    private static final String PARAMETER_FROM_DEVICE_TYPE = "from_device_type";
    /**
     * 接收设备
     */
    private static final String PARAMETER_TO_DEVICE = "to_device";


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
        String toUser = context.getParameter(PARAMETER_TO_USERNAME);
        String toDevice = context.getParameter(PARAMETER_TO_DEVICE);
        int sendTimeout = Integer.valueOf(context.getParameter(PARAMETER_SEND_TIMEOUT, "5000"));
        boolean waitResponse = Boolean.parseBoolean(context.getParameter(PARAMETER_WAIT_RESPONSE, "true"));
        boolean sendDeviceMessage = Boolean.valueOf(context.getParameter(PARAMETER_SEND_DEVICE_MSG, "false"));


        //获取socket连接
        log.info("=========== 当前fromUser:{}, fromDevice:{}, fromDeviceType:{}, toUser:{}, toDevice:{}", fromUser, fromDevice, fromDeviceType, toUser, toDevice);
        MqttSession session = mqttManager.getConnection(mqttConfig.getHost(), mqttConfig.getPort(),mqttConfig.getConnectTimeout(), mqttConfig.getServiceId(),
                fromUser, fromDevice, fromDeviceType, mqttConfig.getEncryptKey(), mqttConfig.isNeedLogin());
        SampleResult result = newSampleResult();
        sampleResultStart(result, getMessage());
        if(session == null){
            sampleResultFailed(result, "500", getError());
            return result;
        }

        if(StringUtils.isNotEmpty(fromUser) || StringUtils.isNotEmpty(toUser)){
            SingleMessage singleMessage = new SingleMessage.Builder()
                    .messageType(MessageType.TEXT.getCode())
                    .fromUser(fromUser)
                    .serviceId(mqttConfig.getServiceId())
                    .toUser(toUser)
                    .textContent(getMessage())
                    .build();
            String message = JSON.toJSONString(singleMessage);
            try{
//                TimeUnit.MILLISECONDS.sleep(50);
                //判断是否发设备消息
                boolean success = true;
                if(sendDeviceMessage){
                    sender.sendDeviceMessage(message, mqttConfig.getServiceId(), toDevice, session.getChannelContext(), mqttConfig.getEncryptKey());
                }else{
                    success = sender.sendSingleMessage(message, mqttConfig.getServiceId(), toUser, session.getChannelContext(), mqttConfig.getEncryptKey(), waitResponse, sendTimeout);
                }

                //判断消息是否发送成功
                if(success){
                    sampleResultSuccess(result, "OK");
                }else{
                    //没有收到消息回执，发送超时
                    sampleResultFailed(result, "504",  new RuntimeException("等待消息回执超时"));
                }
            }catch (Exception e){
                log.error("Send single message error", e);
                sampleResultFailed(result, "500", e);
            }
        }else{
            sampleResultFailed(result, "500", new RuntimeException("发送人或接收人为空"));
        }
        return result;
    }


    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultArgs = super.getDefaultParameters();
        defaultArgs.addArgument(PARAMETER_FROM_USERNAME, "${fromUser}");
        defaultArgs.addArgument(PARAMETER_FROM_USER_PASSWORD, "${fromUserPassword}");
        defaultArgs.addArgument(PARAMETER_FROM_DEVICE, "${fromDevice}");
        defaultArgs.addArgument(PARAMETER_FROM_DEVICE_TYPE, "${fromDeviceType}");
        defaultArgs.addArgument(PARAMETER_TO_USERNAME, "${toUser}");
        defaultArgs.addArgument(PARAMETER_TO_DEVICE, "${toDevice}");
        defaultArgs.addArgument(PARAMETER_SEND_TIMEOUT, "5000");
        defaultArgs.addArgument(PARAMETER_WAIT_RESPONSE, "true");
        defaultArgs.addArgument(PARAMETER_SEND_DEVICE_MSG, "false");
        return defaultArgs;
    }

}
