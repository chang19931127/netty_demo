package com.czd.oio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author changzhendong
 * @Description:
 * @Date: Created in 2019/1/30 22:35.
 * 使用最基础的 Socket OIO 编程模型来编程。
 */
public class PlainOioServer {

	private static ExecutorService singleThreadPool = Executors.newFixedThreadPool(5);

	public static void main(String[] args) throws IOException {
		server(8080);
	}

	static void server(int port) throws IOException {
		// 创建 服务端 Socket 对象
		final ServerSocket serverSocket = new ServerSocket(port);
		while (true) {
			// 循环接受链接 accept 方法阻塞式
			final Socket clientSocket = serverSocket.accept();
			System.out.println("accept connection from " + clientSocket);
			// 创建线程去处理链接请求
			singleThreadPool.submit(() -> {
				try {
					OutputStream out = clientSocket.getOutputStream();
					// 返回数据
					out.write("hello world".getBytes(Charset.forName("UTF-8")));
					out.flush();
					// 中断链接
					clientSocket.close();
				} catch (IOException e) {
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
	}
}
