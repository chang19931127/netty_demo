package com.czd.netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author changzhendong
 * @Description: netty 写出来的服务端简单 echo 服务器
 * @Date: Created in 2019/1/18 17:47.
 *
 * ServerBootstrap 服务端引导器，因引导绑定和启动服务器
 * EventLoopGroup 接受处理新连接，接受新连接、接受数据、写数据等等。
 * NioServerSocketChannel 通道类型
 * 设置 childHandler 执行所有的连接请求
 *
 * Netty 使用 future 来进行回调概念，所以 sync 来进行阻塞
 *
 */
public class EchoServer {
	private final int port;

	public EchoServer(int port) {
		this.port = port;
	}

	public void start() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			// 创建 ServerBootstrap 实例来进行服务引导
			ServerBootstrap b = new ServerBootstrap();
			// 规范的 NIO 传输，绑定本地端口
			b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).localAddress(port)
					.childHandler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							// 添加处理器到 channel pipeline
							// 这里是核心，可以给 pipeline 添加不同的 handler 添加顺序决定处理顺序,出站入站，编码解码，业务逻辑
							ch.pipeline().addLast(new EchoServerHandler());
						}
					});

			// 绑定服务，并等待服务关闭，然后释放资源
			ChannelFuture f = b.bind().sync();
			System.out.println(EchoServer.class.getName() + "started and listen on " + f.channel().localAddress());
			// 如果这里不阻塞会导致服务端直接退出
			f.channel().closeFuture().sync();
		} finally {
			// 优雅退出
			bossGroup.shutdownGracefully().sync();
			workerGroup.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new EchoServer(65535).start();
	}
}
