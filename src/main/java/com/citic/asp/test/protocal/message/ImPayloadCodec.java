package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.message.CherryMessagePayload;
import com.citic.asp.cmc.core.message.CherryMessagePayloadCodec;
import com.citic.asp.cmc.core.message.CherryMessagePayloadFactory;
import com.citic.asp.cmc.core.message.CherryMessagePayloadType;

import java.nio.ByteBuffer;

/**
 * <dl>im消息体编解码类
 * <dt>CherryMessagePayloadCodec</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/11/19</dd>
 * </dl>
 *
 * @author maoyx
 */
public class ImPayloadCodec implements CherryMessagePayloadCodec{

    public ImPayloadCodec(){
        cherryMessagePayloadFactory = new ImPayloadFactory();
    }

    private CherryMessagePayloadFactory cherryMessagePayloadFactory;

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
        CherryMessagePayloadType type = payload.getCherryMessagePayloadType();
        ByteBuffer buffer = ByteBuffer.allocate(FIXED_LENGTH + data.length);
        // 消息体类型
        buffer.put(type.getCode());
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

        byte code = buffer.get();
        CherryMessagePayloadType type = ImPayloadType.valueOf(code);
        buffer.get();
        long id = buffer.getLong();
        long timestamp = buffer.getLong();
        byte[] body = new byte[payload.length - FIXED_LENGTH];
        buffer.get(body);

        return cherryMessagePayloadFactory.createPayload(id, type, body, timestamp);
    }

}
