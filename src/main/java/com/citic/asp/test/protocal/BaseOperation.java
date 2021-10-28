package com.citic.asp.test.protocal;

import io.netty.channel.ChannelInboundHandler;

/**
 * 基本操作
 *
 * @author qcb
 * @date 2021/10/11 14:35.
 */
public interface BaseOperation {

    /**
     * 创建连接
     * @param host
     * @param port
     * @param timeout
     * @return
     */
    ChannelInboundHandler connect(String host, int port, int timeout) throws Exception;
}
