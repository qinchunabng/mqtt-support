package com.citic.asp.test.protocal;

import java.io.Serializable;

/**
 * 单聊消息
 *
 * @author qcb
 * @date 2021/04/20 16:25.
 */
public class SingleMessage implements Serializable {

    public SingleMessage(){}

    private SingleMessage(Builder builder){
        this.fromUser = builder.fromUser;
        this.messageType = builder.messageType;
        this.textContent = builder.textContent;
        this.serviceId = builder.serviceId;
        this.toUser = builder.toUser;
    }

    /**
     * 发送人ID
     */
    private String fromUser;

    /**
     * 消息内容
     */
    private String textContent;

    /**
     * 消息类型
     * @see MessageType
     */
    private Integer messageType;

    /**
     * 接收消息用户
     */
    private String toUser;

    /**
     * 服务ID
     */
    private String serviceId;

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    @Override
    public String toString() {
        return "SingleMessage{" +
                "fromUser='" + fromUser + '\'' +
                ", textContent='" + textContent + '\'' +
                ", messageType=" + messageType +
                ", toUser='" + toUser + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }

    public static class Builder {
        /**
         * 发送人ID
         */
        private String fromUser;

        /**
         * 服务ID
         */
        private String serviceId;

        /**
         * 消息内容
         */
        private String textContent;

        /**
         * 消息类型
         * @see MessageType
         */
        private Integer messageType;

        private String toUser;

        public Builder fromUser(String fromUser){
            this.fromUser = fromUser;
            return this;
        }

        public Builder textContent(String textContent){
            this.textContent = textContent;
            return this;
        }
        public Builder messageType(Integer messageType){
            this.messageType = messageType;
            return this;
        }

        public Builder serviceId(String serviceId){
            this.serviceId = serviceId;
            return this;
        }

        public Builder toUser(String toUser){
            this.toUser = toUser;
            return this;
        }

        public SingleMessage build(){
            return new SingleMessage(this);
        }
    }
}
