package com.jin.rings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jin.ringslibrary.Circle;

public class MainActivity extends AppCompatActivity {
    Circle circle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circle=this.findViewById(R.id.rings);
        circle.animation();
    }
}
