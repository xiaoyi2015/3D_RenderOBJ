package com.zln.demo.Acitivity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zln.demo.GL.MyGLView;
import com.zln.demo.R;

import static android.opengl.Matrix.setIdentityM;

public class MainActivity extends Activity {

    private TextView RefreshView;
    private TextView ExitView;
    private MyGLView myGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        RefreshView = (TextView) findViewById(R.id.refresh);
        ExitView = (TextView) findViewById(R.id.exit);
        myGLView = (MyGLView) findViewById(R.id.model);
        RefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                setIdentityM(myGLView.getGlDraw().getMatrix(),0);
                myGLView.requestRender();
            }
        });
        ExitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });


    }
}
