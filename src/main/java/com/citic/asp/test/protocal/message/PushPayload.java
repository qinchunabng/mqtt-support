package com.citic.asp.test.protocal.message;

import com.citic.asp.cmc.core.message.CherryMessagePayload;
import com.citic.asp.cmc.core.message.CherryMessagePayloadType;

import java.util.Arrays;

/**
 * <dl>消息体
 * <dt>PushPayload</dt>
 * <dd>Description:</dd>
 * <dd>CreateDate: 2020/11/19</dd>
 * </dl>
 *
 * @author maoyx
 */
public class PushPayload implements CherryMessagePayload {
    private long id;
    private CherryMessagePayloadType type;
    private byte[] data;
    private long timestamp;

    public PushPayload(long id, CherryMessagePayloadType type, byte[] data, long timestamp) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }

    @Override
    public CherryMessagePayloadType getCherryMessagePayloadType() {
        return type;
    }

    @Override
    public byte[] getBody() {
        return data;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "PushPayload{" +
                "id=" + id +
                ", type=" + type +
                ", data=" + Arrays.toString(data) +
                ", timestamp=" + timestamp +
                '}';
    }
}
