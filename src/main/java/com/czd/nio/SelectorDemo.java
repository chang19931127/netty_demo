package com.czd.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author changzhendong
 * @Description: selector 相关操作
 * @Date: Created in 2019/1/26 14:45.
 */
public class SelectorDemo {
	public static void main(String[] args) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		// serverSocketChannel 也可以注册接收时间
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(new InetSocketAddress(8080));
		Selector selector = Selector.open();
		// 这里的兴趣可以取 或运算
		System.out.println("server start");
		for (; ; ) {
			SocketChannel socketChannel = serverSocketChannel.accept();
			if (socketChannel != null) {
				socketChannel.configureBlocking(false);
				// 只有注册了关注才会收到通知
				socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE);
				// linux 环境下这里可能有 bug 永远为0
				int readyChannels = selector.select();
				if (readyChannels == 0) {
					continue;
				}
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while (iterator.hasNext()) {
					// 获取连接的 Channel 不需要关注 isAcceptable
					SelectionKey key = iterator.next();
					if (key.isConnectable()) {
						System.out.println("isConnectable");
					}  else if (key.isReadable()) {
						System.out.println("isReadable");
						ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
						socketChannel = ((SocketChannel) key.channel());
						socketChannel.read(byteBuffer);
						System.out.println(new String(byteBuffer.array()));
						byteBuffer.flip();
						socketChannel.write(byteBuffer);
					} else if (key.isWritable()) {
						System.out.println("isWritable");
					}
					key.channel().close();
					iterator.remove();
				}
			}
		}

	}
}
