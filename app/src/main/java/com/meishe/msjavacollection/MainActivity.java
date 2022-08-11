package com.meishe.msjavacollection;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


        BindViewUtil.init(this);
        BindViewUtil.injectEvent(this);

        button.setText("自动绑定成功");

    }


    @MSBindClick({R.id.sample_text})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.sample_text:
                Log.e("lpf","自动绑定了点击事件");
                Toast.makeText(MainActivity.this,"自动绑定了点击事件",Toast.LENGTH_SHORT).show();
            break;
        }
    }

    public native String stringFromJNI();
}