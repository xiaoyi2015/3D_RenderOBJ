package com.zln.demo.GL;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;

/**
 * Created by zln on 2016/12/8.
 */

public class GLB {

    public final int bufferId;
    protected Buffer data;
    protected int length;
    int bufferType;

    public GLB(int bufferId) {
        this.bufferId = bufferId;
    }

    public GLB glSyncWithGPU(ByteBuffer buffer, int length, int usage){
        IntBuffer ib = buffer.asIntBuffer();

        for (int i = 0; i < 3; ++i) {
            System.out.println(ib.get(i));
        }
        this.data = buffer;
        this.length = length;
        glBindBuffer(bufferType, bufferId);
        glBufferData(bufferType, length, data, usage);
        glBindBuffer(bufferType, 0);

        return this;
    }

    static public GLB glGenBuffer(int target) {
        int[] temp = new int[1];
        glGenBuffers(1, temp, 0);

        switch (target){
            case 1:
                return new GLVB(temp[0]);
            case 2:
                return new GLFB(temp[0]);
            default:
                throw new RuntimeException();
        }

    }
}
