package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.message.CherryMessagePayload;
import com.citic.asp.cmc.core.message.CherryMessagePayloadFactory;
import com.citic.asp.cmc.core.message.CherryMessagePayloadType;
import com.citic.asp.core.util.string.IdUtil;
import org.springframework.stereotype.Component;

/**
 * <dl>消息体工厂实现类
 * <dt>PushPayloadFactory</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/11/19</dd>
 * </dl>
 *
 * @author maoyx
 */
public class PushPayloadFactory implements CherryMessagePayloadFactory{
    /**
     * 创建消息体对象，
     * @param type 消息体类型
     * @param data 消息体数据
     * @param timestamp 时间戳
     * @return 消息体对象
     */
    @Override
    public CherryMessagePayload createPayload(long id, CherryMessagePayloadType type, byte[] data, long timestamp) {
        return new PushPayload(id, type, data, timestamp);

    }

    /**
     * 创建消息体对象
     * @param type 消息体类型
     * @param data 消息体数据
     * @return 消息体对象
     */
    @Override
    public CherryMessagePayload createPayload(CherryMessagePayloadType type, byte[] data) {
        return createPayload(IdUtil.nextId(), type, data, System.currentTimeMillis());
    }
}
