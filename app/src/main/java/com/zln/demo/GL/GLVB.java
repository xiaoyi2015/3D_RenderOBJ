package com.zln.demo.GL;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;


/**
 * Created by zln on 2016/12/8.
 */

public class GLVB extends GLB{

    public GLVB(int bufferId) {
        super(bufferId);
        this.bufferType = GL_ARRAY_BUFFER;
    }

}
