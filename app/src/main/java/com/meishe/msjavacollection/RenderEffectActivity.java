package com.meishe.msjavacollection;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.meishe.msjavacollection.inject.MSAutowired;
import com.meishe.msjavacollection.inject.MSInjectView;

import java.util.Arrays;
import java.util.List;

/**
 * 动态获取变量的类型
 * 加深对反射和注解的理解
 */
public class RenderEffectActivity extends AppCompatActivity {


    @MSAutowired("name")
    String name;

    @MSAutowired("attr")
    String attr;

    @MSAutowired
    int[] array;

    @MSAutowired
    UserParcelable userParcelable;

    @MSAutowired
    UserParcelable[] userParcelables;

    @MSAutowired
    List<UserParcelable> userParcelableList;

    @MSAutowired("users")
    UserSerializable[] userSerializables;

    @MSInjectView(R.id.tv_01)
    private TextView tv01;
    @MSInjectView(R.id.tv_02)
    private TextView tv02;
    @MSInjectView(R.id.tv_03)
    private TextView tv03;
    @MSInjectView(R.id.tv_04)
    private TextView tv04;
    @MSInjectView(R.id.tv_05)
    private TextView tv05;
    @MSInjectView(R.id.tv_06)
    private TextView tv06;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_effect);
        BindViewUtil.injectView(this);
        BindViewUtil.injectAutowired(this);

        tv01.setText(name);
        tv02.setText(attr);
        tv03.setText(Arrays.toString(array));
        tv04.setText(userParcelable.name);

    }
}