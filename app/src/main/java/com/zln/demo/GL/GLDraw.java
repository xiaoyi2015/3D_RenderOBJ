package com.zln.demo.GL;

import android.content.Context;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.zln.demo.Util.Parse;
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
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFlush;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform3fv;
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
    private Parse obj;
    private int id;
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

    public GLDraw(Parse obj) {
        this.obj = obj;
    }

    public void glOnDrawFrame(float[] mViewMatrix, float[] mProjectionMatrix, GLB vertexBuffer,
                              GLB indexBuffer, List<Integer> texIds) {
        glUseProgram(id);
        initBuffer(vertexBuffer, indexBuffer);
        updateData(mViewMatrix, mProjectionMatrix);
        //Log.v("zln-id:", id+"");
        for(int i = 0; i < obj.getNum(); i++) {
            glUniform3f(glGetAttribLocation(id, "uDiffuse"), obj.getDiffuses().get(i).x,
                    obj.getDiffuses().get(i).y, obj.getDiffuses().get(i).z);
            glUniform3f(glGetAttribLocation(id, "uSpecular"), obj.getSpeculars().get(i).x,
                    obj.getSpeculars().get(i).y, obj.getSpeculars().get(i).z);
            glBindTexture(GL_TEXTURE_2D, texIds.get(i));
            glDrawElements(GL_TRIANGLES, obj.getCount().get(i), GL_UNSIGNED_INT, obj.getStart().get(i)*4);
            glFlush();

        }

    }

    private void updateData(float[] mViewMatrix, float[] mProjectionMatrix) {
        float[] MVPMatrix = new float[16];
        float[] MVMatrix = new float[16];
        multiplyMM(MVMatrix, 0, mViewMatrix, 0, getModelMatrix(), 0);
        multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(glGetUniformLocation(id, "uMVMatrix"), 1, false, MVMatrix, 0);
        glUniformMatrix4fv(glGetUniformLocation(id, "uMVPMatrix"), 1, false, MVPMatrix, 0);

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

        glEnableVertexAttribArray(glGetAttribLocation(id, "aPosition"));
        glEnableVertexAttribArray(glGetAttribLocation(id, "aNormal"));
        glBindBuffer(GL_ARRAY_BUFFER, vb.bufferId);

        glVertexAttribPointer(glGetAttribLocation(id, "aPosition"), 4, GL_FLOAT, false, POINT_SIZE, POINT_SIZE_POS_OFFSET);
        glVertexAttribPointer(glGetAttribLocation(id, "aNormal"), 4, GL_FLOAT, false, POINT_SIZE, POINT_SIZE_NOR_OFFSET);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ib.bufferId);

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
