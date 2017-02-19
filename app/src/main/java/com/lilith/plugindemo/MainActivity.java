package com.lilith.plugindemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lilith.sdk.community.plugin.BasePluginContainerActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartBtn = (Button) findViewById(R.id.start);
        mStartBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                Intent intent = new Intent(this, PluginContainerActivity.class);
                intent.putExtra(BasePluginContainerActivity.PARAM_PLUGIN_ACTIVITY, "com.liilith.plugin.test.MainActivity");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
