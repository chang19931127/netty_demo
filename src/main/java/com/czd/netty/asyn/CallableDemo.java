package com.czd.netty.asyn;

/**
 * @Author changzhendong
 * @Description: 异步的一种实现方式就是 回调
 * @Date: Created in 2019/1/16 19:51.
 * 回调模式，就是完成后我来通知你
 */
public class CallableDemo {

	public static void main(String[] args) {
		IFetcher fetcher = new Fetcher();
		fetcher.fetchData(new FetchCallable() {
			@Override
			public void onData() throws Exception {
				System.out.println("data is OK");
			}
			@Override
			public void onError() {
				System.out.println("data is error");
			}
		});
	}

	interface FetchCallable {
		void onData() throws Exception;
		void onError();
	}

	interface IFetcher {
		void fetchData(FetchCallable callable);
	}

	static class Fetcher implements IFetcher {
		@Override
		public void fetchData(FetchCallable callable) {
			try {

				// todo something
				System.out.println("模拟远程调用 暂停 5s 中");
				Thread.sleep(5000);
				callable.onData();
			} catch (Exception e) {
				callable.onError();
			}
		}
	}
}
