package com.citic.asp.test.sampler;

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
        String socketKey = getSocketKey(toUser, toDevice);
        log.info("=========== 当前socketKey:{}, toUser:{}, toDevice:{}", socketKey, toUser, toDevice);
        Socket socket = getSocket(socketKey);
        SampleResult result = newSampleResult();
        sampleResultStart(result, null);
        if(socket == null){
            sampleResultFailed(result, "500", getError());
            return result;
        }
        try {
            socket.setSoTimeout(receiveTimeout);
        } catch (SocketException e) {
            log.error("设置超时时间异常", e);
            sampleResultFailed(result, "500", e);
            return result;
        }

        if(StringUtils.isNotEmpty(toUser)){
            try{
                InputStream is = socket.getInputStream();
                byte[] data = receiver.receive(is);
                String response = Hex.toHexString(data);
                sampleResultSuccess(result, response);
            }catch (Exception e){
                log.error("Receive message error", e);
                if(e instanceof SocketTimeoutException){
                    //超时
                    sampleResultFailed(result, "504", e);
                }else{
                    sampleResultFailed(result, "500", e);
                }
            }
        }else{
            sampleResultFailed(result, "500", new RuntimeException("发送人或接收人为空"));
        }
        return result;
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
}
