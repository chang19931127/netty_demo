package com.czd.netty.startandstop;

import sun.misc.Signal;

import java.util.concurrent.TimeUnit;

/**
 * @Author changzhendong
 * @Description: 信号量方式停机
 * @Date: Created in 2019/1/21 19:38.
 */
public class SignalHandlerDemo {

	public static void main(String[] args) throws InterruptedException {
		SignalHandlerDemo signalHandlerDemo = new SignalHandlerDemo();

		// 1. 启动应用初始化 Signal 实例
		Signal signal = new Signal(signalHandlerDemo.getOOSSignalType());
		// 3. 将信号量 SignalHandler 注册到 Signal 中
		Signal.handle(signal, signal1 -> {
			// 4. 回调处理
			// 当系统接收到信号量，就会回调这里
			// 这里绝对不要阻塞住否则会导致 ShutdownHook 无法执行，并且系统无法退出
			System.out.println("接收到信号量通知做操作 signal：" + signal1);
			// eg cmd 下 如果 ctrl+c 控制台直接退出
			System.exit(0);
		});

		TimeUnit.SECONDS.sleep(7);

	}

	private String getOOSSignalType() {
		// 2. 根据系统来选择信号量
		return System.getProperty("os.name").toLowerCase().startsWith("win") ? "INT" : "TERM";
	}

}
