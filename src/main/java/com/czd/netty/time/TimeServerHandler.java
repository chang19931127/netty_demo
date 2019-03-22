package com.czd.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author changzhendong
 * @Description: 时间服务器 Handler
 * @Date: Created in 2019/3/21 20:03.
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

	// 1 重写 channel 活跃的方法
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 2 通过 ctx 来分配一个 buffer
		final ByteBuf time = ctx.alloc().buffer(4);
		time.writeInt((int) (System.currentTimeMillis() / 1000 + 2208988800L));

		// channelFuture 来异步写
		final ChannelFuture f = ctx.writeAndFlush(time);

		f.addListener((ChannelFutureListener) future -> {
			assert f == future;
			// 使用完毕 就关闭了
			ctx.close();
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
