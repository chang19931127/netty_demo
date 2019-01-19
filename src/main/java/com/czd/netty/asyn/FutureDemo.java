package com.czd.netty.asyn;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author changzhendong
 * @Description:
 * @Date: Created in 2019/1/16 20:15.
 * future 模式就是预先告知结果存到哪里，你自己去取
 */
public class FutureDemo {
	private static ExecutorService executorService = Executors.newCachedThreadPool();

	public static void main(String[] args) throws Exception {
		Runnable task1 = new Runnable() {
			@Override
			public void run() {
				System.out.println("i am task1");
			}
		};

		Callable<Integer> task2 = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("i am task2");
				return 10;
			}
		};

		Future f1 = executorService.submit(task1);
		Future<Integer> f2 = executorService.submit(task2);
		System.out.println("task1 is complete:" + f1.isDone());
		System.out.println("task2 is complete:" + f2.isDone());
		// wait
		TimeUnit.SECONDS.sleep(1);
		while (!f1.isDone()) {
			System.out.println("wait for task1");
		}
		System.out.println("task1 is complete.");
		while (!f2.isDone()) {
			System.out.println("wait for task2");
		}
		System.out.println("task2 is complete return value:" + f2.get());
	}
}
