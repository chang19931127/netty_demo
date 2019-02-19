package com.czd.netty.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @Author changzhendong
 * @Description: ChannelHandler
 * @Date: Created in 2019/2/18 11:46.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf in = (ByteBuf) msg;
		try {
			// do something
			while (in.isReadable()) {
				System.out.print((char)in.readByte());
			}
		} finally {
			// 一定要释放 buffer 引用哦
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 出现异常关闭通道
		cause.printStackTrace();
		ctx.close();
	}
}
