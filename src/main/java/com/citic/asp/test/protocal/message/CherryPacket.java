package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.message.CherryMessage;
import org.tio.core.intf.Packet;

/**
 * <dl>基于cim-io的消息对象
 * <dt>CherryPacket</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/10/21</dd>
 * </dl>
 *
 * @author maoyx
 */
public class CherryPacket extends Packet {
    /**
     * 通用消息对象
     */
    private CherryMessage cherryMessage;

    public CherryPacket() {
    }

    public CherryPacket(CherryMessage citicMessage) {
        this.cherryMessage = citicMessage;
    }

    public CherryMessage getCherryMessage() {
        return cherryMessage;
    }

    public void setCherryMessage(CherryMessage cherryMessage) {
        this.cherryMessage = cherryMessage;
    }
}
