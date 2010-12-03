package org.androix.nativenetbook;

import java.lang.Thread;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;

public class NetbookActivity extends Activity
{


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Intent intent = new Intent(this, NetbookService.class);
        startService(intent);

        TextView systemText = (TextView)findViewById(R.id.systemText);
        systemText.setText("Native Netbook started");

    }

}



