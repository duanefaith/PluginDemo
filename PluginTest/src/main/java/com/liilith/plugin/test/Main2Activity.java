package com.liilith.plugin.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lilith.sdk.community.plugin.BasePluginActivity;
import com.morgoo.nativec.NativeCHelper;

public class Main2Activity extends BasePluginActivity
        implements View.OnClickListener {

    private Button mTest1Btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mTest1Btn = (Button) findViewById(R.id.test1);
        mTest1Btn.setOnClickListener(this);

        Toast.makeText(this, String.valueOf(NativeCHelper.ping()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test1:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.baidu.com"));
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
