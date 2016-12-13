package com.zln.demo.GL;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glShaderSource;

/**
 * Created by zln on 2016/12/8.
 */

public class Shader {

    private String source;
    private int type;
    private int id;

    public Shader(String source, int type) {
        this.source = source;
        this.type = type;
    }

    void glInit() {
        id = glCreateShader(type);
        glShaderSource(id, this.source);
        glCompileShader(id);

        int[] result = new int[1];
        glGetShaderiv(id, GL_COMPILE_STATUS, result, 0);
        String s = glGetShaderInfoLog(id);
        System.out.println(glGetShaderInfoLog(id));
        if (result[0] == GL_FALSE) {
            throw new RuntimeException();
        }
    }

    void glAttachProgram(int id) {
        glAttachShader(id, this.id);
    }
}
