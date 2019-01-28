package com.czd.netty.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author changzhendong
 * @Description: 使用 NIO 的方式来进行
 * @Date: Created in 2019/1/22 20:40.
 */
public class NioClientPool {

	private static final int BUFFER_SIZE = 1024;
	private static Selector selector;

	public static void main(String[] args) throws IOException {
		initClientPool(10);
	}

	static void initClientPool(int poolSize) throws IOException {
		// 1. 打开 SocketChannel
		SocketChannel socketChannel = SocketChannel.open();
		// 2. 设置成非阻塞并且设置 Socket 的相关参数
		socketChannel.configureBlocking(false);
		socketChannel.socket().setReuseAddress(true);
		socketChannel.socket().setReceiveBufferSize(BUFFER_SIZE);
		socketChannel.socket().setSendBufferSize(BUFFER_SIZE);
		// 3. 异步连接服务器，异步连接都返回 false
		boolean connected = socketChannel.connect(new InetSocketAddress("127.0.0.1", 18081));
		// 打开多路复用器
		selector = Selector.open();
		if (connected) {
			// 连接成功 10. 注册读事件
			socketChannel.register(selector, SelectionKey.OP_READ);
		} else {
			// 连接失败注册连接事件
			socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
			while (true) {
				int num = selector.select();
				if (num == 0) {
					continue;
				}
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator iterator = selectionKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					if (key.isAcceptable()) {
						// handlerConnect()
						System.out.println("isAcceptable");
					}
					if (key.isConnectable()) {
						System.out.println("isConnectable");
					}
					if (key.isReadable()) {
						System.out.println("isReadable");
					}
					if (key.isWritable()) {
						System.out.println("isWritable");
					}
				}
			}
		}
	}

	static class ReactorTask implements Runnable {
		@Override
		public void run() {
			try {
				for (;;) {
					int num = selector.select();
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					Iterator iterator = selectionKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey key = (SelectionKey) iterator.next();
						if (key.isConnectable()) {
							// handlerConnect()
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
