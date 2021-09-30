package com.citic.asp.test.protocal;

import java.io.IOException;
import java.io.InputStream;

/**
 * 消息接收
 *
 * @author qcb
 * @date 2021/04/20 10:51.
 */
public interface MessageReceiver {

    /**
     * 接收消息
     * @param is
     * @return
     */
    byte[] receive(InputStream is) throws IOException;

}
