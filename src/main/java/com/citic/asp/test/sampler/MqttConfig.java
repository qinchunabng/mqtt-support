package com.citic.asp.test.sampler;

import java.io.Serializable;

/**
 * mqtt配置信息
 *
 * @author qcb
 * @date 2021/04/21 10:04.
 */
public class MqttConfig implements Serializable {
    private static final long serialVersionUID = -5213225659153766499L;

    private MqttConfig(Builder builder){
        this.serviceId = builder.serviceId;
        this.message = builder.message;
        this.host = builder.host;
        this.port = builder.port;
        this.needLogin = builder.needLogin;
        this.encryptKey = builder.encryptKey;
        this.messageSize = builder.messageSize;
        this.connectTimeout = builder.connectTimeout;
    }

    /**
     * 服务ID
     */
    private String serviceId;
    /**
     * 消息内容
     */
    private String message;
    /**
     * 消息服务host
     */
    private String host;
    /**
     * 消息服务端口
     */
    private int port;
    /**
     * 是否需要登录
     */
    private boolean needLogin;
    /**
     * 加密密钥
     */
    private String encryptKey;
    /**
     * 消息大小
     */
    private String messageSize;
    /**
     * 连接超时时间
     */
    private int connectTimeout;


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(String messageSize) {
        this.messageSize = messageSize;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public String toString() {
        return "MqttConfig{" +
                "serviceId='" + serviceId + '\'' +
                ", message='" + message + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", needLogin=" + needLogin +
                ", encryptKey='" + encryptKey + '\'' +
                ", messageSize='" + messageSize + '\'' +
                ", connectTimeout=" + connectTimeout +
                '}';
    }

    public static class Builder {
        /**
         * 服务ID
         */
        private String serviceId;
        /**
         * 消息内容
         */
        private String message;
        /**
         * 消息服务host
         */
        private String host;
        /**
         * 消息服务端口
         */
        private int port;
        /**
         * 是否需要登录
         */
        private boolean needLogin;
        /**
         * 加密密钥
         */
        private String encryptKey;

        /**
         * 消息大小
         */
        private String messageSize;
        /**
         * 连接超时时间
         */
        private int connectTimeout;


        public Builder serviceId(String serviceId){
            this.serviceId = serviceId;
            return this;
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder host(String host){
            this.host = host;
            return this;
        }

        public Builder port(int port){
            this.port = port;
            return this;
        }

        public Builder needLogin(boolean needLogin){
            this.needLogin = needLogin;
            return this;
        }

        public Builder encryptKey(String encryptKey){
            this.encryptKey = encryptKey;
            return this;
        }

        public Builder messageSize(String messageSize){
            this.messageSize = messageSize;
            return this;
        }

        public Builder connectTimeout(int connectTimeout){
            this.connectTimeout = connectTimeout;
            return this;
        }

        public MqttConfig build(){
            return new MqttConfig(this);
        }
    }

}
