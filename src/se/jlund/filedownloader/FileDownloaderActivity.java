package se.jlund.filedownloader;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FileDownloaderActivity extends Activity {

    private static final String TAG = FileDownloaderActivity.class.getName();
    private static final int DIALOG_PROGRESS = 1;

    private static final int MAX_PROGRESS = 100;
    private ProgressDialog mProgressDialog;

    // TEST URL
    private final String url = "http://download.thinkbroadband.com/10MB.zip";

    private void launchDownload() {
        DownloadFilesTask task = new DownloadFilesTask();
        task.execute(url);

        showDialog(DIALOG_PROGRESS);
    }
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);               

        Button b = (Button) findViewById(R.id.download_button);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                launchDownload();
            }
        });
        launchDownload();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        // Code adopted from the ApiDemos app by the Android team
        case DIALOG_PROGRESS:
            mProgressDialog = new ProgressDialog(FileDownloaderActivity.this);
            //mProgressDialog.setIcon(R.drawable.alert_dialog_icon);
            mProgressDialog.setTitle(R.string.progress_text);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(MAX_PROGRESS);
            mProgressDialog.setProgress(0); 
            mProgressDialog.setCancelable(false);
            break;
        default:
            assert false;
        }
        return mProgressDialog;
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Long> {
        Downloader downloader;
        long downloadedTotal = 0;

        protected Long doInBackground(String... urls) {
            int count = urls.length;
            if (count > 1) {
                throw new IllegalArgumentException("Multiple downloads are not supported.");                
            }

            // Set up the download
            try {
                downloader = new Downloader(url);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                Toast.makeText(FileDownloaderActivity.this, 
                        "Could not download " + url, Toast.LENGTH_LONG).show();
                // TODO: Handle this failure appropriately...
            }

            final long totalSize = downloader.getTotalSize();
            
            Log.i(TAG, "Total size = " + totalSize);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            // Do the download
            try {
                long downloadedTmp = 0;
                do {
                    try {                       
                        
                        downloadedTmp = downloader.downloadFile();
                        if (downloadedTmp > 0)
                            downloadedTotal += downloadedTmp;
                    } catch (IOException e) {
                        Toast.makeText(FileDownloaderActivity.this, 
                                "Error downloading file!", Toast.LENGTH_LONG).show();
                        // TODO: Handle this failure appropriately...
                    }

                    int progress = (int) ((downloadedTotal / (double)totalSize) * 100);

                    Log.i(TAG, "Downloaded " + downloadedTotal/1024.0 + " kB, i.e. " + downloadedTotal);

                    Log.i(TAG, "Progress = " + progress);

                    if (progress == 0) progress = 1; // Show that we have started...
                    publishProgress(progress);

                }
                while (downloadedTmp > 0);
            }
            finally {
                downloader.cleanup();
            }

            return downloadedTotal;
        }

        protected void onProgressUpdate(Integer... progress) {
            //m_progressText.setText(String.valueOf(progress[0]));
            mProgressDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(Long result) {
            dismissDialog(DIALOG_PROGRESS);
            Toast.makeText(getBaseContext(), "Download finished!", Toast.LENGTH_LONG).show();
        }
    }

}