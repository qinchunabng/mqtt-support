package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.message.CherryMessageGroup;
import com.citic.asp.cmc.core.message.CherryMessagePayloadType;

/**
 * <dl>业务消息体body类型
 * <dt>CmcPayloadType</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/11/19</dd>
 * </dl>
 *
 * @author maoyx
 */
public enum CmcPayloadType implements CherryMessagePayloadType{
    // 透传消息推送
    PUSH_PASS_THROUGH((byte) 0x01, true, true, CherryMessageGroup.PUSH),
    // 通知栏消息推送
    PUSH_NOTIFY((byte) 0x02, true, true, CherryMessageGroup.PUSH),
    // 单聊
    SINGLE_CHAT((byte) 0x03, true, true, CherryMessageGroup.IM),
    // 群聊
    GROUP_CHAT((byte) 0x04, true, true, CherryMessageGroup.IM),
    // 密聊
    SECRET_CHAT((byte) 0x05, true, true, CherryMessageGroup.IM),

    // 单聊消息撤回
    REVOKE_SINGLE((byte) 0x06, false, true, CherryMessageGroup.IM),
    // 群聊消息撤回
    REVOKE_GROUP((byte) 0x07, false, true, CherryMessageGroup.IM),
    // 密聊消息撤回
    REVOKE_SECRET((byte) 0x08, false, true, CherryMessageGroup.IM),

    // 会话置顶通知
    SET_TOP_SESSION((byte) 0x10, false, false, CherryMessageGroup.IM),
    // 离线消息拉取发送
    OFF_LINE_MSG((byte) 0x11, false, false, CherryMessageGroup.PUSH),

    // 会话免打扰变更通知
    QUIET_SET((byte) 0x12, false, false, CherryMessageGroup.IM),
    // 会话配置设置通知
    USER_CONFIG_SET((byte) 0x13, false, false, CherryMessageGroup.IM),
    // 用户状态开启和关闭通知
    USER_STATUS_CONFIG((byte) 0x14, false, false, CherryMessageGroup.IM),

    // 公众号消息
    PUBLIC_MSG((byte) 0x15, false, true, CherryMessageGroup.IM),

    // 文件传输助手消息
    FILE_TRANSF_MSG((byte) 0x16, false, true, CherryMessageGroup.IM),

    // 好友申请
    FRIEND_APPLY((byte) 0x21, false, true, CherryMessageGroup.IM),
    // 同意好友申请
    FRIEND_AGREE((byte) 0x22, false, true, CherryMessageGroup.IM),
    // 删除好友
    FRIEND_REMOVE((byte) 0x23, false, true, CherryMessageGroup.IM),
    // 个人名片更新
    PERSON_CARD_REFRESH((byte) 0x24, false, true, CherryMessageGroup.IM),

    // 邀请入群
    GROUP_INVITE((byte) 0x31, false, true, CherryMessageGroup.IM),
    // 邀请入群申请群主确认消息
    GROUP_ADMIN_CONFIRM((byte) 0x32, false, true, CherryMessageGroup.IM),
    // 申请入群
    GROUP_APPLY((byte) 0x33, false, true, CherryMessageGroup.IM),
    // 群置顶消息
    GROUP_TOPMSG((byte) 0x34, false, false, CherryMessageGroup.IM),
    // 发布群公告通知
    GROUP_NOTICE_MSG((byte) 0x35, false, false, CherryMessageGroup.IM),

    // 入群通知
    GROUP_USER_ADD_MSG((byte) 0x38, false, true, CherryMessageGroup.IM),
    // 群解散通知
    GROUP_REMOVE_MSG((byte) 0x39, false, true, CherryMessageGroup.IM),
    // 群成员退群通知
    GROUP_USER_EXIT_MSG((byte) 0x40, false, true, CherryMessageGroup.IM),
    // 移除群成员通知
    GROUP_USER_REMOVE_MSG((byte) 0x41, false, true, CherryMessageGroup.IM),
    // 群详情变更通知
    GROUP_MODIFY_MSG((byte) 0x42, false, true, CherryMessageGroup.IM),
    // 群成员昵称变更通知
    GROUP_USER_MODIFY_MSG((byte) 0x43, false, true, CherryMessageGroup.IM),
    // 群转让通知
    GROUP_TRANSFER_MSG((byte) 0x44, false, true, CherryMessageGroup.IM),
    // 群成员退群多端同步通知消息(发送给人的通知消息)
    GROUP_USER_EXIT_SYNC_MSG((byte) 0x45, false, true, CherryMessageGroup.IM),

    // 回执同步通知消息
    RECEIPT_SYNC((byte) 0x90, false, false, CherryMessageGroup.IM),
    // 单聊回执消息
    RECEIPT_SINGLE((byte) 0x91, false, false, CherryMessageGroup.IM),
    // 群聊回执消息
    RECEIPT_GROUP((byte) 0x92, false, false, CherryMessageGroup.IM),
    // 密聊回执消息
    RECEIPT_SECRET((byte) 0x93, false, false, CherryMessageGroup.IM),
    // 系统回执消息
    RECEIPT_SYS((byte) 0x94, false, false, CherryMessageGroup.PUSH),
    // 单聊批量回执消息
    RECEIPT_BATCH_SINGLE((byte) 0x95, false, false, CherryMessageGroup.IM),
    // 群聊批量回执消息
    RECEIPT_BATCH_GROUP((byte) 0x96, false, false, CherryMessageGroup.IM),
    // 密聊批量回执消息
    RECEIPT_BATCH_SECRET((byte) 0x97, false, false, CherryMessageGroup.IM),
    // 自己发送给自己(多端同步的)单聊回执消息
    RECEIPT_SINGLE_MULTI((byte) 0x98, false, false, CherryMessageGroup.IM),
    // 系统推送消息批量回执消息
    RECEIPT_BATCH_PUSH((byte) 0x99, false, false, CherryMessageGroup.PUSH),
    // 群聊批量回执的回执
    RECEIPT_BATCH_GROUP_RECEIPT((byte) 0x9A, false, false, CherryMessageGroup.IM),
    // 文件传输助手回执
    RECEIPT_FILE_TRANSF((byte) 0x9B, false, false, CherryMessageGroup.IM),
    // 文件传输助手批量回执
    RECEIPT_BATCH_FILE_TRANSF((byte) 0x9C, false, false, CherryMessageGroup.IM),

    // 角标badge变更消息
    BADGE_CHANGE((byte) 0xA1, false, false, CherryMessageGroup.PUSH),
    // 远程控制消息
    REMOTE_CONTROL((byte) 0xA2, false, false, CherryMessageGroup.IM),
    //多设备同步消息
    MULTI_DEVICE_SYNC((byte) 0xB1, false, false, CherryMessageGroup.IM),
    //设备上线操作
    DEVICE_ONLINE((byte) 0xC1, false, false, CherryMessageGroup.IM),
    ;

    /**
     * 消息类型
     */
    private byte code;
    /**
     * 是否需要回执
     */
    private boolean needReceipt;
    /**
     * 是否离线推送
     */
    private boolean needOfflinePush;
    /**
     * 消息body组
     */
    private String group;

    CmcPayloadType(byte code, boolean needOfflinePush, boolean needReceipt, String group) {
        this.code = code;
        this.needReceipt = needReceipt;
        this.needOfflinePush = needOfflinePush;
        this.group = group;
    }

    /**
     * 根据编号获取消息体类型
     * @param code 编号
     * @return 消息体类型
     */
    public static CmcPayloadType valueOf(byte code) {
        CmcPayloadType[] types = values();
        for (CmcPayloadType type : types) {
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
        return group;
    }
}
