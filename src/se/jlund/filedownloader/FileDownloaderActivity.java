package se.jlund.filedownloader;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class FileDownloaderActivity extends Activity {

    private static final String TAG = FileDownloaderActivity.class.getName();
    private static final int DIALOG_PROGRESS = 1;

    private static final int MAX_PROGRESS = 100;

    //private TextView m_progressText;
    private ProgressDialog mProgressDialog;
    // private Handler mProgressHandler;
    //private int mProgress;

    // TEST
    private final String url = "http://download.thinkbroadband.com/1MB.zip";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //m_progressText = (TextView) findViewById(R.id.progresstext);

        DownloadFilesTask task = new DownloadFilesTask();
        task.execute(url);

        showDialog(DIALOG_PROGRESS);
        /*
        mProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mProgress >= MAX_PROGRESS) {
                    mProgressDialog.dismiss();
                } else {
                    mProgress++;
                    mProgressDialog.incrementProgressBy(1);
                    mProgressHandler.sendEmptyMessageDelayed(0, 100);
                }
            }
        };
        mProgressHandler.sendEmptyMessage(0);
         */

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
            mProgressDialog.setProgress(0); // remove?
            break;
        default:
            assert false;
        }
        return mProgressDialog;
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Long> {
        Downloader downloader;
        long downloadedBytes = 0;

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

            Log.i(TAG, "Total size = " + downloader.getTotalSize());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            // Do the download
            downloadedBytes = 0;
            try {
                do {
                    try {
                        downloadedBytes += downloader.downloadFile();
                    } catch (IOException e) {
                        Toast.makeText(FileDownloaderActivity.this, 
                                "Error downloading file!", Toast.LENGTH_LONG);
                        // TODO: Handle this failure appropriately...
                    }

                    // Not optimized to call downloader.getTotalSize(), but leave for now
                    int progress = (int) ((downloadedBytes / downloader.getTotalSize()) * 100);

                    Log.i(TAG, "Downloaded " + downloadedBytes/1024.0 + " kB, i.e. " + downloadedBytes);

                    Log.i(TAG, "Progress = " + progress);

                    if (progress == 0) progress = 1; // Show that we have started...
                    publishProgress(progress);

                }
                while (downloadedBytes > 0);
            }
            finally {
                downloader.cleanup();
            }

            return downloadedBytes;
        }

        protected void onProgressUpdate(Integer... progress) {
            //m_progressText.setText(String.valueOf(progress[0]));
            mProgressDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(Long result) {
            dismissDialog(DIALOG_PROGRESS);
            Toast.makeText(FileDownloaderActivity.this, "Download finished!", Toast.LENGTH_LONG);
        }
    }

}