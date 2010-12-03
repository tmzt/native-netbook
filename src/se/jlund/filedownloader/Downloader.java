package se.jlund.filedownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Environment;

/**
 * 
 * @author Jan Lund <janne_lund@yahoo.com>
 * 
 * Class to send to nick tmzt at #android on IRC
 *
 */

public class Downloader {

    // Class constants
    private static final int BUF_SZ = 100 * 1024;
    private static final File DIRECTORY = Environment.getExternalStorageDirectory();

    // Instance variables
    private InputStream in = null;
    private OutputStream outstream = null;
    private long downloadedSize = 0;
    private final long totalSize;
    private final String filename;

    public Downloader(String url) throws IOException {
        File file = new File(DIRECTORY, "DOWNLOAD_" + url.substring(url.lastIndexOf("/") + 1));
        filename = file.getAbsolutePath();

        outstream = new FileOutputStream(file);        

        final HttpClient httpClient = new DefaultHttpClient();        
        final HttpUriRequest request = new HttpGet(url);
        //request.addHeader("Accept-Encoding", "gzip");        
        final HttpResponse response = httpClient.execute(request);

        totalSize = response.getEntity().getContentLength();

        downloadedSize = 0;

        in = response.getEntity().getContent();

    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public String getFileName() {
        return filename;
    }

    public long downloadFile() throws IOException {
        //assert !reachedEOF;

        byte[] bytes = new byte[BUF_SZ];

        int readBytes = 0; // Temporary size of the buffer

        // Read through the buffer and write to the temporary file
        if ((readBytes = in.read(bytes)) > 0) {
            outstream.write(bytes, 0, readBytes);

            downloadedSize += readBytes;
                        
        }
        //        else {
        //            reachedEOF = true;
        //        }
        return readBytes;

    }

    public void cleanup() {
        if (outstream != null) {
            try { outstream.close(); } catch (IOException e) { } 
        }
        if (in != null) {
            try { in.close(); } catch (IOException e) { }
        }
    }
}