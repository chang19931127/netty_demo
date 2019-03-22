package com.czd.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @Author changzhendong
 * @Description: 时间服务器发起客户端
 * @Date: Created in 2019/3/21 20:15.
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 链接可读 回调

		ByteBuf buf = (ByteBuf) msg;

		try {
			long currentTimeMillis = (buf.readUnsignedInt() - 220898800L) * 1000L;
			System.out.println(new Date(currentTimeMillis));
			ctx.close();
		} finally {
			buf.release();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
