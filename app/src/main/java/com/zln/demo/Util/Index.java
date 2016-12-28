package com.zln.demo.Util;

import java.nio.ByteBuffer;

/**
 * Created by zln on 2016/12/8.
 */

public class Index {

    public static final int SIZE_AS_FLOAT = 3;
    public static final int SIZE_AS_BYTE = SIZE_AS_FLOAT * ByteUtil.FLOAT_BYTE_SIZE;

    public int a;
    public int b;
    public int c;
    public int id;

    public Index() {

    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer bb = ByteUtil.genDirectBuffer(SIZE_AS_FLOAT * ByteUtil.FLOAT_BYTE_SIZE);
        bb.putInt(a);
        bb.putInt(b);
        bb.putInt(c);
        bb.flip();
        return bb;
    }
    
}
