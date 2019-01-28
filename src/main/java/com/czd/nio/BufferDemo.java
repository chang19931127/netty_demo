package com.czd.nio;

import java.nio.IntBuffer;
import java.security.SecureRandom;

/**
 * @Author changzhendong
 * @Description:
 * @Date: Created in 2019/1/26 10:37.
 * 总结 1. buffer 里面的数据写入了就暂时不会被删除，就是后续修改都是覆盖操作
 * 总结 2. buffer 里面的数据读写都是通过几个标记位来操作的,通过标记逻辑来控制读写
 */
public class BufferDemo {

	public static void main(String[] args) {
		IntBuffer intBuffer = IntBuffer.allocate(10);
		for(int i = 0;i<intBuffer.capacity();i++) {
			int b = new SecureRandom().nextInt(20);
			//此方法为相对方法(relative),他会导致position的变化
			intBuffer.put(b);
			//此方法是绝对方法（absolute),他的使用只会讲对应位置的值替换到，并不会更改position
			//intBuffer.put(i,new SecureRandom().nextInt(20));

			System.out.print(b + " ");
		}
		System.out.println("intBuffer capacity:" + intBuffer.capacity() + " 这个不会变");
		//翻转 buffer
		intBuffer.flip();
		System.out.println("读取到的数据：" + intBuffer.get());
		System.out.println("读取到的数据：" + intBuffer.get());
		System.out.println("before mark: buffer position=" + intBuffer.position() + " limit=" + intBuffer.limit());
		//标记 buffer 状态
		intBuffer.mark();
		System.out.println("after mark: buffer position=" + intBuffer.position() + " limit=" + intBuffer.limit());
		System.out.println("读取到的数据：" + intBuffer.get());
		System.out.println("读取到的数据：" + intBuffer.get());
		System.out.println("before reset: buffer position=" + intBuffer.position() + " limit=" + intBuffer.limit());
		//恢复 buffer 状态
		intBuffer.reset();
		// 会发现读的数据一样，就好像 mark 位暂存了 position 然后好让 position 回退回来
		System.out.println("after reset: buffer position=" + intBuffer.position() + " limit=" + intBuffer.limit());
		System.out.println("读取到的数据：" + intBuffer.get());
		System.out.println("读取到的数据：" + intBuffer.get());
		System.out.println("before compact: buffer position=" + intBuffer.position() + " limit=" + intBuffer.limit());
		// 压缩数据，将未读的数据 copy buffer 的开始,position=0+未读数据，就是重置 buffer 只是把未读的数据放到 buffer 的开始
		// compact 和 clear 后就别读了，需要写了
		intBuffer.compact();
		// 6个未读，所以是6
		System.out.println("after compact: buffer position=" + intBuffer.position() + " limit=" + intBuffer.limit());
		System.out.println("读取到的数据：" + intBuffer.get());
		System.out.println("读取到的数据：" + intBuffer.get());
		System.out.println("buffer.compact() position=" + intBuffer.position() + " limit=" + intBuffer.limit());
		// 标记重置 intBuffer 并不是数据 clear
		intBuffer.clear();
		System.out.println("buffer.clear() position=" + intBuffer.position() + " limit=" + intBuffer.limit());
	}
}
