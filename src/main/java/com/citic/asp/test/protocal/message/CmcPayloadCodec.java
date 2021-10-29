package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.message.CherryMessagePayload;
import com.citic.asp.cmc.core.message.CherryMessagePayloadCodec;
import com.citic.asp.cmc.core.message.CherryMessagePayloadFactory;
import com.citic.asp.cmc.core.message.CherryMessagePayloadType;

import java.nio.ByteBuffer;

/**
 * <dl>消息体编解码类
 * 格式：[1字节，消息体类型][1字节，预留][8字节，消息id][8字节，时间戳][业务数据]
 * <dt>CmcPayloadCodec</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/11/19</dd>
 * </dl>
 *
 * @author maoyx
 */
public class CmcPayloadCodec implements CherryMessagePayloadCodec {

    private CherryMessagePayloadFactory cherryMessagePayloadFactory;

    public CmcPayloadCodec(){
        cherryMessagePayloadFactory = new PushPayloadFactory();
    }

    /**
     * 报文定长
     */
    private final int FIXED_LENGTH = 18;

    /**
     * 消息体编码
     * 格式：[1字节，消息体类型][1字节，预留][8字节，消息id][8字节，时间戳][业务数据]
     * @param payload 消息体对象
     * @return 字节数组
     */
    @Override
    public byte[] encode(CherryMessagePayload payload) {
        byte[] data = payload.getBody();
        byte type = payload.getCherryMessagePayloadType().getCode();
        ByteBuffer buffer = ByteBuffer.allocate(FIXED_LENGTH + data.length);
        // 消息体类型
        buffer.put(type);
        // 预留
        buffer.put((byte) 0x00);
        // 消息id
        buffer.putLong(payload.getId());
        // 时间戳
        buffer.putLong(payload.getTimestamp());
        // 业务数据
        buffer.put(data);

        return buffer.array();
    }

    /**
     * 消息体解码
     * 格式：[1字节，消息体类型][1字节，预留][8字节，消息id][8字节，时间戳][业务数据]
     * @param payload 字节数组
     * @return 消息体对象
     */
    @Override
    public CherryMessagePayload decode(byte[] payload) {
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        // 消息体类型
        byte code = buffer.get();
        CherryMessagePayloadType type = CmcPayloadType.valueOf(code);
        // 预留
        buffer.get();
        // 消息id
        long id = buffer.getLong();
        // 时间戳
        buffer.getLong();
        // 业务数据
        byte[] body = new byte[payload.length - FIXED_LENGTH];
        buffer.get(body);

        // 使用服务端时间戳
        return cherryMessagePayloadFactory.createPayload(id, type, body, System.currentTimeMillis());
    }

}
