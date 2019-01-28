package com.czd.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author changzhendong
 * @Description:
 * @Date: Created in 2019/1/25 19:52.
 *
 * 总结 1. Channel 和 ByteBuffer 分离比较舒服
 * 总结 2. ByteBuffer 读写模式转化很烦人，一些 api 是提供给读模式的，一些 api 是提供给写模式的
 */
public class FileChannelDemo {

	public static void main(String[] args) throws IOException {
		RandomAccessFile accessFile = new RandomAccessFile("C:\\Users\\richard\\Downloads\\cm", "rw");
		FileChannel fileChannel = accessFile.getChannel();

		ByteBuffer buffer = ByteBuffer.allocate(2);

		int bytesRead = fileChannel.read(buffer);
		// 只有 -1 的时候才算是读完 0 的时候是不支持读
		while (bytesRead != -1) {
			System.out.println("read " + bytesRead);
			// 读完转化成写需要 ByteBuffer.flip()
			buffer.flip();
			while (buffer.hasRemaining()) {
				System.out.print((char) buffer.get());
			}
			System.out.println();
			// 清理一波
			buffer.clear();
			bytesRead = fileChannel.read(buffer);
		}
		accessFile.close();
	}
}
