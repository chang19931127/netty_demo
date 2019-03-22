package com.czd.netty.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Author changzhendong
 * @Description: 时间服务器 客户端
 * @Date: Created in 2019/3/21 20:22.
 */
public class TimeClient {
	public static void main(String[] args) {
		EventLoopGroup work = new NioEventLoopGroup();

		Bootstrap b = new Bootstrap();
		b.group(work)
		.channel(NioSocketChannel.class)
		.option(ChannelOption.SO_KEEPALIVE,true)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new TimeClientHandler());
			}
		});

		ChannelFuture f = null;
		try {
			f = b.connect("127.0.0.1", 65535).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			work.shutdownGracefully();
		}


	}
}
