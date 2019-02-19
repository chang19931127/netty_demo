package com.czd.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Author changzhendong
 * @Description:
 * @Date: Created in 2019/1/22 13:27.
 */
public class NettyClientPool {

	public static void main(String[] args) throws InterruptedException {
		initClientPool(10);
	}

	static void initClientPool(int poolSize) throws InterruptedException {
		// 说白了就是客户端共用 EventLoopGroup
		EventLoopGroup group = new NioEventLoopGroup();
		// Bootstrap 不是并发安全的，所以对 Bootstrap 并发操作是很危险的
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
				ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						// 链接活跃完毕之后断掉链接
						System.out.println("channelActive");
					}

					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						cause.printStackTrace();
						ctx.close();
					}
				});
			}
		});

		for (int i = 0; i < poolSize; i++) {
			bootstrap.connect("127.0.0.1", 65535).sync();
			// 链接使用完毕会自动关闭，千万不能调用 group.shutdownGracefully()
		}
	}
}
