package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.message.CherryMessagePayloadType;

/**
 * <dl>消息体类型
 * <dt>ImPayloadType</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/11/19</dd>
 * </dl>
 *
 * @author maoyx
 */
public enum ImPayloadType implements CherryMessagePayloadType{

    // 透传消息推送
    PUSH_PASS_THROUGH((byte) 0x01, true, true),
    // 通知栏消息推送
    PUSH_NOTIFY((byte) 0x02, true, true),

    // 单聊
    SINGLE_CHAT((byte) 0x03, true, true),
    // 群聊
    GROUP_CHAT((byte) 0x04, true, true),
    // 密聊
    SECRET_CHAT((byte) 0x05, true, true),

    // 单聊消息撤回
    REVOKE_SINGLE((byte) 0x06, false, true),
    // 群聊消息撤回
    REVOKE_GROUP((byte) 0x07, false, true),
    // 密聊消息撤回
    REVOKE_SECRET((byte) 0x08, false, true),

    // 会话置顶通知
    SET_TOP_SESSION((byte) 0x10, false, false),
    // 离线消息拉取发送
    OFF_LINE_MSG((byte) 0x11, false, false),
    // 会话免打扰变更通知
    QUIET_SET((byte) 0x12, false, false),

    // 好友申请
    FRIEND_APPLY((byte) 0x21, false, true),
    // 同意好友申请
    FRIEND_AGREE((byte) 0x22, false, true),
    // 删除好友
    FRIEND_REMOVE((byte) 0x23, false, true),
    // 个人名片更新
    PERSON_CARD_REFRESH((byte) 0x24, false, true),

    // 邀请入群
    GROUP_INVITE((byte) 0x31, false, true),
    // 邀请入群申请群主确认消息
    GROUP_ADMIN_CONFIRM((byte) 0x32, false, true),
    // 申请入群
    GROUP_APPLY((byte) 0x33, false, true),
    // 群置顶消息
    GROUP_TOPMSG((byte) 0x34, false, false),
    // 发布群公告通知
    GROUP_NOTICE_MSG((byte) 0x35, false, false),

    // 入群通知
    GROUP_USER_ADD_MSG((byte) 0x38, false, true),
    // 群解散通知
    GROUP_REMOVE_MSG((byte) 0x39, false, true),
    // 群成员退群通知
    GROUP_USER_EXIT_MSG((byte) 0x40, false, true),
    // 移除群成员通知
    GROUP_USER_REMOVE_MSG((byte) 0x41, false, true),
    // 群详情变更通知
    GROUP_MODIFY_MSG((byte) 0x42, false, true),
    // 群成员昵称变更通知
    GROUP_USER_MODIFY_MSG((byte) 0x43, false, true),
    // 群转让通知
    GROUP_TRANSFER_MSG((byte) 0x44, false, true),


    // 单聊回执消息
    RECEIPT_SINGLE((byte) 0x91, false, false),
    // 群聊回执消息
    RECEIPT_GROUP((byte) 0x92, false, false),
    // 密聊回执消息
    RECEIPT_SECRET((byte) 0x93, false, false),
    // 系统回执消息
    RECEIPT_SYS((byte) 0x94, false, false),
    // 单聊批量回执消息
    RECEIPT_BATCH_SINGLE((byte) 0x95, false, false),
    // 群聊批量回执消息
    RECEIPT_BATCH_GROUP((byte) 0x96, false, false),
    // 密聊批量回执消息
    RECEIPT_BATCH_SECRET((byte) 0x97, false, false),
    // 角标badge变更消息
    BADGE_CHANGE((byte) 0xA1, false, false),
    //多设备同步消息
    MULTI_DEVICE_SYNC((byte) 0xB1, false, false),
    //集群节点加入通知消息
    CLUSTER_NODE_NOTIFICATION((byte) 0xFD, false, false),
    //集群节点加入消息
    CLUSTER_NODE_JOINED((byte) 0xFE, false, false),
    // 在线互挤消息
    MUTUAL_OFFLINE((byte) 0xFF, false, false)
    ;

    private byte code;
    private boolean needReceipt;
    private boolean needOfflinePush;

    ImPayloadType(byte code, boolean needOfflinePush, boolean needReceipt) {
        this.code = code;
        this.needReceipt = needReceipt;
        this.needOfflinePush = needOfflinePush;
    }

    /**
     * 根据编号获取消息体类型
     * @param code 编号
     * @return 消息体类型
     */
    public static CherryMessagePayloadType valueOf(byte code) {
        ImPayloadType[] types = values();
        for (ImPayloadType type : types) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    @Override
    public byte getCode() {
        return code;
    }

    @Override
    public boolean isNeedReceipt() {
        return needReceipt;
    }

    @Override
    public boolean isNeedOfflinePush() {
        return needOfflinePush;
    }

    @Override
    public String getGroup() {
        return null;
    }


}
