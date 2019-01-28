package com.czd.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author changzhendong
 * @Description: 分散聚集
 * @Date: Created in 2019/1/26 14:25.
 * Channel 可以将数据同时读到多个 Buffer 中，也可以通过多个 Buffer 来读取到一个 Channel 中
 *
 * 文件内容很简单。一般就是协议可以使用
 */
public class ScatGatherDemo {

	public static void main(String[] args) throws IOException {
		RandomAccessFile accessFile = new RandomAccessFile("C:\\Users\\richard\\Downloads\\scatergather", "rw");
		FileChannel fileChannel = accessFile.getChannel();

		ByteBuffer head = ByteBuffer.allocate(6);
		ByteBuffer body = ByteBuffer.allocate(4);
		ByteBuffer[] bufferArray = { head, body };
		long bytesRead = fileChannel.read(bufferArray);
		// 只有 -1 的时候才算是读完 0 的时候是不支持读
		while (bytesRead != -1) {
			// 读完转化成写需要 ByteBuffer.flip()
			head.flip();
			body.flip();
			System.out.println("head:" + new String(head.array()));
			System.out.println("body:" + new String(body.array()));
			System.out.println();
			// 清理一波
			head.clear();
			body.clear();
			bytesRead = fileChannel.read(bufferArray);
		}
		accessFile.close();
		// head:head
		//
		// body:body
	}
}
