package com.czd.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @Author changzhendong
 * @Description:
 * @Date: Created in 2019/1/30 22:44.
 * 使用 NIO Channel 进行服务端编程
 * serverSocketChannel 和 socketChannel 都采用非阻塞模式
 */
public class PlainNioServer {
	public static void main(String[] args) {

	}

	static void server(int port) throws IOException {
		System.out.println("listening for connections on port " + port);
		// 打开 Selector
		Selector selector = Selector.open();
		// 创建 ServerChannel
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		// 设置 ServerSocketChannel 的配置
		serverSocketChannel.configureBlocking(false).register(selector, SelectionKey.OP_ACCEPT);
		serverSocketChannel.bind(new InetSocketAddress(port));
		ByteBuffer buffer = ByteBuffer.wrap("hello world".getBytes());
		// 循环监听
		while (true) {
			// 等待 select 事件通知
			int select = selector.select();
			if (select > 0) {
				// 获取连接的 Channel
				Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
				while (selectionKeyIterator.hasNext()) {
					SelectionKey selectionKey = selectionKeyIterator.next();
					selectionKeyIterator.remove();
					try {
						// 监听的通道获取到链接的 Channel 然后注册 读写
						if (selectionKey.isAcceptable()) {
							serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
							SocketChannel clientSocket = serverSocketChannel.accept();
							System.out.println("accepted connection from " + clientSocket);
							clientSocket.configureBlocking(false);
							// 非阻塞事件注册
							clientSocket.register(selector, SelectionKey.OP_WRITE, buffer.duplicate());
						}
						// 可写的通道
						if (selectionKey.isWritable()) {
							SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
							buffer = (ByteBuffer) selectionKey.attachment();
							while (buffer.hasRemaining()) {
								if (clientSocket.write(buffer) == 0) {
									break;
								}
							}
							// 操作完毕链接直接中断
							clientSocket.close();
						}
					} catch (IOException e) {
						// 处理异常
						selectionKey.cancel();
						selectionKey.channel().close();
					}
				}
			}
		}

	}
}
