package com.czd.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @Author changzhendong
 * @Description:
 * @Date: Created in 2019/1/30 23:06.
 * 使用 NettyNIO 来实现 NIO Server Netty 就是屏蔽底层的 API
 */
public class NettyNioServer {
	public static void main(String[] args) {

	}

	static void server(int port) {
		final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8));
		// 事件循环组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			// 引导类来引导
			ServerBootstrap bootstrap = new ServerBootstrap();
			// 增加配置外加处理链路
			bootstrap.group(group).channel(NioServerSocketChannel.class).localAddress(port)
					.childHandler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							// 添加处理链路逻辑
							ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									// 连接后，写消息，然后关闭连接
									ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
								}
							});
						}
					});
			// 绑定连接
			ChannelFuture channelFuture = bootstrap.bind().sync();
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// 优雅关闭释放资源
			group.shutdownGracefully();
		}


	}
}
