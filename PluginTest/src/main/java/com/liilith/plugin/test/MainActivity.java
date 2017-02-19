package com.liilith.plugin.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lilith.sdk.community.plugin.BasePluginActivity;

public class MainActivity extends BasePluginActivity implements View.OnClickListener {

    private Button mTestBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTestBtn = (Button) findViewById(R.id.test);
        mTestBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test:
                Toast.makeText(this, "It's a test...", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
