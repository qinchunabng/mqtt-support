package com.citic.asp.test.protocal;

import java.io.Serializable;

/**
 * 群消息
 *
 * @author qcb
 * @date 2021/04/23 11:57.
 */
public class GroupMessage implements Serializable {
    private static final long serialVersionUID = -7682679196661110484L;

    public GroupMessage(){

    }

    public GroupMessage(Builder builder){
        this.fromUser = builder.fromUser;
        this.textContent = builder.textContent;
        this.messageType = builder.messageType;
        this.groupId = builder.groupId;
        this.groupName = builder.groupName;
        this.serviceId = builder.serviceId;
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
     * 群ID
     */
    private String groupId;

    /**
     * 群名称
     */
    private String groupName;

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "GroupMessage{" +
                "fromUser='" + fromUser + '\'' +
                ", textContent='" + textContent + '\'' +
                ", messageType=" + messageType +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }

    public static class Builder {
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
         * 群ID
         */
        private String groupId;

        /**
         * 群名称
         */
        private String groupName;

        /**
         * 服务ID
         */
        private String serviceId;

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

        public Builder groupId(String groupId){
            this.groupId = groupId;
            return this;
        }

        public Builder groupName(String groupName){
            this.groupName = groupName;
            return this;
        }

        public Builder serviceId(String serviceId){
            this.serviceId = serviceId;
            return this;
        }

        public GroupMessage build(){
            return new GroupMessage(this);
        }
    }
}
