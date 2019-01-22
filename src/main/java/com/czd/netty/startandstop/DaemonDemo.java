package com.czd.netty.startandstop;

import java.util.concurrent.TimeUnit;

/**
 * @Author changzhendong
 * @Description: JVM 关闭的时候会停止守护进程的
 * @Date: Created in 2019/1/21 13:31.
 */
public class DaemonDemo {
	public static void main(String[] args) throws InterruptedException {
		long startTime = System.nanoTime();
		Thread thread = new Thread(() -> {
			try {
				TimeUnit.DAYS.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		// 守护进程 默认入参false
		thread.setDaemon(true);
		thread.start();
		TimeUnit.SECONDS.sleep(15);
		System.out.println("系统推出，程序执行" + (System.nanoTime() - startTime) / 1000 / 1000 / 1000 + "s");
	}
}
