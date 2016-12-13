package com.zln.demo.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by zln on 2016/12/8.
 */

public class ByteUtil {

    public static final int FLOAT_BYTE_SIZE = 4;

    static public ByteBuffer genDirectBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }
}
