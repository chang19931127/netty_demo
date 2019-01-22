package com.czd.netty.startandstop;

import java.util.concurrent.TimeUnit;

/**
 * @Author changzhendong
 * @Description: JDK 注册 ShutdownHookDemo
 * @Date: Created in 2019/1/21 13:45.
 */
public class ShutdownHookDemo {
	public static void main(String[] args) throws InterruptedException {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("ShutdownHook execute start...");
			System.out.println("Netty NioEventLoopGroup shutdownGracefully...");
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("ShutdownHook execute end...");
		}));

		System.out.println("main thread will sleep 7 s");
		TimeUnit.SECONDS.sleep(7);
		System.out.println("main thread sleep 7 s");
		System.exit(0);
	}
}
