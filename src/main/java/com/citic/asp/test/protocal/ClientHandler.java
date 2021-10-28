package com.citic.asp.test.protocal;

import com.citic.asp.cmc.core.message.CherryMessage;
import com.citic.asp.cmc.core.message.CherryMessageConverter;
import com.citic.asp.cmc.core.message.CherryMessageFactory;
import com.citic.asp.test.protocal.message.CimioCherryMessageConverter;
import com.citic.asp.test.protocal.message.MqttCherryMessageFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端handler
 *
 * @author qcb
 * @date 2021/10/27 15:53.
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private CherryMessageFactory messageFactory;

    private MessageReceiver messageReceiver;

    private MqttManager mqttManager;

    private CherryMessageConverter<MqttMessage> cherryMessageConverter;

    public ClientHandler(){
        messageFactory = new MqttCherryMessageFactory();
        messageReceiver = MqttManagerImpl.getInstance();
        mqttManager = MqttManagerImpl.getInstance();
        cherryMessageConverter = new CimioCherryMessageConverter();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if(idleStateEvent.state() == IdleState.WRITER_IDLE){
                //发送心跳消息
                CherryMessage pingMessage = messageFactory.createPingMessage();
                ctx.writeAndFlush(pingMessage);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ClientHandler#exceptionCaught", cause);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        CherryMessage cherryMessage = cherryMessageConverter.convert(mqttMessage);
        mqttManager.receive(cherryMessage, channelHandlerContext);
    }
}
