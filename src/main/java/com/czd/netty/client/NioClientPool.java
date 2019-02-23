package com.czd.netty.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author changzhendong
 * @Description: 使用 NIO 的方式来进行
 * NIO 的事件通知有点坑爹
 * 事件很坑，多留意，多百度吸取家训，这就是为什么要使用 Netty
 * 其实一定要注意，有什么事件就注册什么时间，然后轮训出来方便做对应的操作
 * @Date: Created in 2019/1/22 20:40.
 */
public class NioClientPool {

	private static Selector selector;

	static {
		try {
			selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 线程池自己根据需求写参数肯定可配,然后线程池工厂抽离出来
	private static ExecutorService threadPool = new ThreadPoolExecutor(4,
			4,
			0,
			TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "我的专属-" + threadNumber.getAndIncrement());
			if (t.isDaemon()) {
				t.setDaemon(true);
			}
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}

	});

	public static void main(String[] args) throws IOException {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("caught    "+e);
			}
		});
		for (int i = 0; i < 20; i++) {
			threadPool.execute(new ReactorTask());
		}
	}

	static class ReactorTask implements Runnable {
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + "开始执行");
			try {
				// 1. 打开 SocketChannel
				SocketChannel socketChannel = SocketChannel.open();
				// 2. 设置成非阻塞并且设置 Socket 的相关参数
				socketChannel.configureBlocking(false);
				// 3. 异步连接服务器，异步连接都返回 false
				boolean connected = socketChannel.connect(new InetSocketAddress("127.0.0.1", 65535));
				// 4. 判断连接结果 打开多路复用器
				if (connected) {
					// 连接成功 10. 注册读事件 这里肯定不会走到
					System.out.println(Thread.currentThread().getName() + " 直接建立成功>isConnectable");
					socketChannel.register(selector, SelectionKey.OP_READ);
				} else {
					// 连接失败注册连接事件
					// 5. 注册监听时间
					socketChannel.register(selector, SelectionKey.OP_CONNECT);
					// 其实就可以开线程做读操作了
					start:
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
								System.out.println(Thread.currentThread().getName() + "isAcceptable");
							}
							// 这个事件会在链接成功和失败的时候响应
							if (key.isConnectable()) {
								System.out.println(Thread.currentThread().getName() + "isConnectable");
								if (key.isValid() && key.isConnectable()) {
									SocketChannel sc = (SocketChannel)key.channel();
									if (sc.finishConnect()) {
										System.out.println(Thread.currentThread().getName() + "isConnectable success");
										for (int i = 0; i < 10; i++) {
											System.out.println(Thread.currentThread().getName()+"do it");
											Thread.sleep(1000);
										}
										// 10. 注册读事件
										// socketChannel.register(selector, SelectionKey.OP_READ，ioHandler);

										break start;
									} else {
										throw new RuntimeException("连接失败");
									}
								} else {
									throw new RuntimeException("连接失败");
								}

							}
							else if (key.isReadable()) {
								System.out.println(Thread.currentThread().getName() + "isReadable");
								// 11. 对读的数据进行操作
								// channel.read(buffer)
								SocketChannel sc = (SocketChannel)key.channel();
								ByteBuffer buffer = ByteBuffer.allocate(1024);
								sc.write(buffer);
								while (buffer.hasRemaining()) {
									buffer.mark();
									// 编码 buffer
									// 然后获取到编码后的buffer
								}
								// clear buffer 或者 compact
								// 再去处理 总共的 buffer

							}
							else if (key.isWritable()) {
								System.out.println(Thread.currentThread().getName() + "isWritable");
							}
						}
					}
				}
				System.out.println(Thread.currentThread().getName() + "结束");
			} catch (Exception e) {
				System.out.println("出现异常信息" + e);
			}
		}
	}



}
