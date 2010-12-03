package org.androix.nativenetbook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NetbookActivity extends Activity
{

    private int[] pidArray = new int[1];

    private FileDescriptor nativeFd;	
    private FileInputStream nativeIn;

    private byte[] buffer = new byte[256];

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	nativeFd = NativeInit.createProcess(pidArray);
	nativeIn = new FileInputStream(nativeFd);

	buffer[0] = 'X';
	buffer[1] = 'Y';
	buffer[2] = 'Z';

/*	while(true) {	*/
		try { 
			int read = nativeIn.read(buffer);
		} catch (IOException io) {};
		Log.d("NativeNetbook", new String(buffer));
/*	}	*/

    }
}
