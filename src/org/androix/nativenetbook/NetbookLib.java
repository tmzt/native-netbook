
package org.androix.nativenetbook;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;


public class NetbookLib implements Runnable
{

    Context context;

    public NetbookLib(Context context) {
        this.context = context;
    }

    @Override
    public void run()
    {

	    int[] pidArray = new int[1];

	    FileDescriptor nativeFd;	
	    FileInputStream nativeIn;

        BufferedReader br;

//	    byte[] buffer = new byte[80];
        String line;

	    this.copyFile("/data/data/org.androix.nativenetbook/nativeinit", 755, R.raw.nativeinit);

	    nativeFd = NativeInit.createProcess(pidArray);
	    nativeIn = new FileInputStream(nativeFd);

        br = new BufferedReader(new InputStreamReader(nativeIn), 80);

        while(true) {
		    try { 
//			    int read = br.readline(buffer);
                line = br.readLine();
    		    Log.d("NativeNetbook", "stdout: " + line);
		    } catch (IOException io) {};
        }

    }

	private void copyFile(String filename, int mode, int res)
	{
		this.copyFile(filename, res);
		NativeInit.chmod(filename, mode);
	}

	private void copyFile(String filename, int res)
	{
		InputStream is = this.context.getResources().openRawResource(res);
		byte buf[] = new byte[1024];
		int len;		

        Log.d("nativenetbook", "Copying resource to " + filename);

		try {
			OutputStream os = new FileOutputStream(filename);
			while((len = is.read(buf)) > 0) {
				os.write(buf, 0, len);
			};
			os.close();
			is.close();
		} catch (IOException io) { Log.d("nativenetbook", "Installing "+filename+" failed: " + io.toString()); };

	}
}

