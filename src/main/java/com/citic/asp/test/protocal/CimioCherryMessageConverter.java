package com.citic.asp.test.protocal;

import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.cmc.core.message.CherryMessageConverter;
import com.citic.asp.test.protocal.message.CherryPacket;

/**
 * <dl>cim-io消息转换类
 * <dt>CimioCherryMessageConverter</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/10/21</dd>
 * </dl>
 *
 * @author maoyx
 */
public class CimioCherryMessageConverter implements CherryMessageConverter<CherryPacket> {
    @Override
    public CherryPacket convert(CherryMessage message) {
        return new CherryPacket(message);
    }

    @Override
    public CherryMessage convert(CherryPacket message) {
        return message.getCherryMessage();
    }
}
