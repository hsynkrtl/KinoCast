package com.ov3rk1ll.kinocast.api.mirror;

import android.os.SystemClock;
import android.util.Log;

import com.ov3rk1ll.kinocast.R;
import com.ov3rk1ll.kinocast.ui.DetailActivity;
import com.ov3rk1ll.kinocast.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SharedSx extends Host {
    private static final String TAG = SharedSx.class.getSimpleName();
    public static final int HOST_ID = 52;

    public SharedSx(int id) {
        super(id);
    }

    @Override
    public String getVideoPath(DetailActivity.QueryPlayTask queryTask) {
        Log.d(TAG, "resolve " + url);
        queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_getdatafrom, url));
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(Utils.USER_AGENT)
                    .timeout(3000)
                    .get();

            String hash = doc.select("form > input[name=hash]").val();
            String expires = doc.select("form > input[name=expires]").val();
            String timestamp = doc.select("form > input[name=timestamp]").val();

            for(int i = 7; i >= 0; i--){
                queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_wait, String.valueOf(i)));
                SystemClock.sleep(1000);
            }

            Log.d(TAG, "Send data [hash: " + hash + ", expires: " + expires + ", timestamp: " + timestamp + "]");
            queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_senddatato, url));
            doc = Jsoup.connect(url)
                    .data("hash", hash)
                    .data("expires", expires)
                    .data("timestamp", timestamp)
                    .userAgent(Utils.USER_AGENT)
                    .timeout(3000)
                    .post();

            String link = doc.select("div.stream-content").attr("data-url");
            queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_gettinglink));
            Log.d(TAG, "File is at " + link);
            return link;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
