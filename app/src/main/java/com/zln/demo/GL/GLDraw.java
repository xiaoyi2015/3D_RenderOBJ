package com.zln.demo.GL;

import android.content.Context;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.zln.demo.Util.Vec2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFlush;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLU.gluErrorString;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setRotateM;


/**
 * Created by zln on 2016/12/8.
 */

public class GLDraw {

    private List<Shader> shaders = new ArrayList<>();
    private int id;
    private int Num;
    //private int ATTR1_LOCATION = 0;
    //private int ATTR2_LOCATION = 1;
    private int POINT_SIZE = 32;
    private int POINT_SIZE_POS_OFFSET = 0;
    private int POINT_SIZE_NOR_OFFSET = 16;
    private float[] modelMatrix = new float[16];

    {
        setIdentityM(modelMatrix, 0);
    }

    public float[] getMatrix(){
        return modelMatrix;
    }

    public GLDraw(int num) {
        this.Num = num;
    }

    public void glOnDrawFrame(float[] mViewMatrix, float[] mProjectionMatrix, GLB vertexBuffer, GLB indexBuffer) {
        glUseProgram(id);
        //Log.v("zln-id:", id+"");
        initBuffer(vertexBuffer, indexBuffer);
        updateData(mViewMatrix, mProjectionMatrix);
        glDrawElements(GL_TRIANGLES, Num * 3, GL_UNSIGNED_INT, 0);
        glFlush();
        //Log.v("zln-error:", gluErrorString(glGetError()));

    }

    private void updateData(float[] mViewMatrix, float[] mProjectionMatrix) {
        float[] MVPMatrix = new float[16];
        float[] MVMatrix = new float[16];
        multiplyMM(MVMatrix, 0, mViewMatrix, 0, getModelMatrix(), 0);
        multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(glGetUniformLocation(id, "mvMatrix"), 1, false, MVMatrix, 0);
        glUniformMatrix4fv(glGetUniformLocation(id, "mvpMatrix"), 1, false, MVPMatrix, 0);
//        for(int i = 0; i < 16; i++)
//            //Log.v("matrix:    ", MVMatrix[i] + "");
//
//        for(int i = 0; i < 16; i++)
//            //Log.v("matrix2:    ", MVPMatrix[i] + "");

    }

    private float[] getModelMatrix() {
        float[] translateMatrix = new float[16];
        Matrix.setIdentityM(translateMatrix, 0);
        Matrix.translateM(translateMatrix, 0, 0, 0, -5);
        float[] res = new float[16];
        multiplyMM(res, 0, translateMatrix, 0, modelMatrix, 0);
        return res;
    }

    public void glOnSurfaceCreated(Context c) {
        initProgram(c);
    }

    public void glCompileAndLink() {
        this.id = glCreateProgram();
        for (Shader s : shaders) {
            s.glInit();
            s.glAttachProgram(id);
        }
        glLinkProgram(id);
    }

    public void addShader(Shader shader) {
        shaders.add(shader);
    }

    private void initBuffer(GLB vb, GLB ib) {

        glEnableVertexAttribArray(glGetAttribLocation(id, "position"));
        glEnableVertexAttribArray(glGetAttribLocation(id, "normal"));
        glBindBuffer(GL_ARRAY_BUFFER, vb.bufferId);
        //Log.v("zln-bid:", vb.bufferId+"");
        glVertexAttribPointer(glGetAttribLocation(id, "position"), 4, GL_FLOAT, false, POINT_SIZE, POINT_SIZE_POS_OFFSET);
        glVertexAttribPointer(glGetAttribLocation(id, "normal"), 4, GL_FLOAT, false, POINT_SIZE, POINT_SIZE_NOR_OFFSET);
        //glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ib.bufferId);
        //Log.v("zln-bid:", ib.bufferId+"");

    }

    private void initProgram(Context c) {
        initShader(c, "vertex.glsl", GL_VERTEX_SHADER);
        initShader(c, "fragment.glsl", GL_FRAGMENT_SHADER);
        glCompileAndLink();

    }

    private void initShader(Context c, String fileName, int type) {
        if (type != GL_VERTEX_SHADER && type != GL_FRAGMENT_SHADER) {
            Log.e(TAG, "shader's type is wrong");
            throw new RuntimeException();
        }
        String source;
        try {
            source = convertStreamToString(c.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        Shader shader = new Shader(source, type);
        addShader(shader);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void rotate(Vec2 v) {
        float[] sTemp = new float[16];
        setRotateM(sTemp, 0, 6, v.x, v.y, 0);
        multiplyMM(modelMatrix, 0, sTemp, 0, modelMatrix, 0);

    }

    public void scale(float scale) {

        scaleM(modelMatrix, 0, scale, scale, scale);
    }
}
