
package org.androix.nativenetbook;

import java.lang.Thread;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NetbookService extends Service
{
    private static NetbookService instance;
    private static NetbookService getService() { return instance; }

    @Override
    public void onCreate() {
	    NetbookLib netbook = new NetbookLib(getBaseContext());
	    Thread netbookThread = new Thread(netbook, "Native Netbook");
        netbookThread.setDaemon(true);
	    netbookThread.start();
        
        instance = this;

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

}

