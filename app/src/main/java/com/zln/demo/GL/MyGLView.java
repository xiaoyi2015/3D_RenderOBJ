package com.zln.demo.GL;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

import com.zln.demo.Util.Parse;
import com.zln.demo.Util.Vec2;
import com.zln.demo.Util.Vec3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.mode;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glViewport;

/**
 * Created by zln on 2016/12/5.
 */

public class MyGLView extends GLSurfaceView implements GLSurfaceView.Renderer{

    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private Parse obj = ReadObj("bishop.obj");
    private GLB vertexBuffer;
    private GLB indexBuffer;
    private int textureId;
    private float aspect;
    private GLDraw glDraw;

    public GLDraw getGlDraw() {
        return glDraw;
    }

    public MyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //init open gl for renderer
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        // Set the camera position (View matrix)
        initLookAt();

        //init buffer
        glInitBuffer();
        glInitTexture();

        //init draw program
        glDraw = new GLDraw(obj.getIndexNumber());
        glDraw.glOnSurfaceCreated(getContext());
    }

    private void glInitTexture() {
        int[] textureIds = new int[1];
        glGenTextures(1, textureIds, 0);
        glBindTexture(GL_TEXTURE_2D, textureIds[0]);
        textureId = textureIds[0];
        try {
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, BitmapFactory.decodeStream(getContext().getAssets().open("clay.png")), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    private void glInitBuffer() {
        ByteBuffer vertexData = obj.getVertexes();
        vertexBuffer = GLB.glGenBuffer(1).glSyncWithGPU(vertexData, vertexData.limit(), GL_STATIC_DRAW);

        ByteBuffer indexData = obj.getIndexes();
        indexBuffer = GLB.glGenBuffer(2).glSyncWithGPU(indexData, indexData.limit(), GL_STATIC_DRAW);

    }

    private void initLookAt() {
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 0, 0f, 0f, -1f, 0.0f, 1.0f, 0.0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        aspect = (float) width / height;

        initProjectionMatrix(aspect);
    }

    private final static float TAN_22_5 = 0.40402622583516f;
    private final static int NEAR = 1;

    private void initProjectionMatrix(float aspect) {
        float left = -aspect * TAN_22_5;
        float right = aspect * TAN_22_5;
        float bottom = -TAN_22_5;
        float top = TAN_22_5;
        float far = 100.0f;
//        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1, 1, NEAR, 100);
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, NEAR, far);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glDraw.glOnDrawFrame(mViewMatrix, mProjectionMatrix, vertexBuffer, indexBuffer);
    }

    private void init() {
        //OpenGL ES version
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //glEnable(GL_CULL_FACE);

    }


    private Parse ReadObj(String objFileName) {
        InputStream inputStream;
        try {
            inputStream = getContext().getAssets().open(objFileName);
            //如果从网上下载，则用下面导入路径文件流的
            //inputStream = new FileInputStream(new File(getContext().getExternalCacheDir(), objFileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        Parse obj;
        try {
            obj = new Parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return obj;
    }

    private float lastX;
    private float lastY;
    private float lastX1;
    private float lastY1;
    private float oldDis;
    private int mode = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        event.getAction();
        event.getActionIndex();
        event.getActionMasked();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                mode = 0;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode = 1;
                oldDis = spacing(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == 1) {
                    float nowDis = spacing(event);
                    float scale = (float)Math.sqrt(nowDis / oldDis);
                    glDraw.scale(scale);
                    oldDis = spacing(event);
                    requestRender();

                } else {
                    float deltaX = event.getX() - lastX;
                    float deltaY = event.getY() - lastY;
                    if (deltaX == 0 || deltaY == 0) {
                        break;
                    }
                    glDraw.rotate(new Vec2(deltaY, deltaX));
                    lastX = event.getX(0);
                    lastY = event.getY(0);
                    requestRender();
                }

                break;
            case MotionEvent.ACTION_UP:
                break;

        }

        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
