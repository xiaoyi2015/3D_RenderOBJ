package com.zln.demo.GL;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;

/**
 * Created by zln on 2016/12/8.
 */

public class GLFB extends GLB {

    public GLFB(int bufferId) {
        super(bufferId);
        this.bufferType = GL_ELEMENT_ARRAY_BUFFER;
    }
}
