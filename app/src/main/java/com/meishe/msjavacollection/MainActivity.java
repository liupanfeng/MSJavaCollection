package com.meishe.msjavacollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.meishe.msjavacollection.inject.MSBindClick;
import com.meishe.msjavacollection.inject.MSInjectView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @MSInjectView(R.id.sample_text)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
//        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());


        BindViewUtil.injectView(this);
        BindViewUtil.injectEvent(this);

        button.setText("自动绑定成功");

    }


    @MSBindClick({R.id.sample_text})
    public void onTestClick(View view){
        switch (view.getId()){
            case R.id.sample_text:
//                Log.e("lpf","自动绑定了点击事件");
//                Toast.makeText(MainActivity.this,"自动绑定了点击事件",Toast.LENGTH_SHORT).show();

                ArrayList<UserParcelable> userParcelableList = new ArrayList<>();

                userParcelableList.add(new UserParcelable("唐三"));
                Intent intent = new Intent(this, RenderEffectActivity.class)
                        .putExtra("name", "lpf")
                        .putExtra("attr","cool")
                        .putExtra("array", new int[]{1, 2, 3, 4, 5, 6})
                        .putExtra("userParcelable", new UserParcelable("小舞"))
                        .putExtra("userParcelables", new UserParcelable[]{new UserParcelable("宁荣荣")})
                        .putExtra("users",new UserSerializable[]{new UserSerializable("比比东")})
                        .putExtra("strs",new String[]{"1","2"})
                        .putParcelableArrayListExtra("userParcelableList", userParcelableList);
                startActivity(intent);

            break;
        }
    }

    public native String stringFromJNI();
}