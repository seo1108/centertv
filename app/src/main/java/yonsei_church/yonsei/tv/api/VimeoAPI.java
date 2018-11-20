package yonsei_church.yonsei.tv.api;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class VimeoAPI {
    private static String videoUrl = "";

    public static String vimeoUrl(final String id) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String mUrl = "http://player.vimeo.com/video/" + id + "/config";
                DefaultHttpClient client = new DefaultHttpClient();
                BufferedReader reader = null;
                try {
                    HttpPost kRequest = new HttpPost(mUrl);
                    kRequest.setHeader("Content-Type", "application/json");
                    HttpResponse kResponse = client.execute(kRequest);
                    String jsonResult = EntityUtils.toString(kResponse.getEntity());

                    try {
                        JSONObject json = new JSONObject(jsonResult);
                        JSONObject json1 = (JSONObject) json.get("request");
                        JSONObject json2 = (JSONObject) json1.get("files");
                        JSONArray jarr = (JSONArray) json2.get("progressive");

                        for (int i = 0; i < jarr.length(); i++) {
                            JSONObject jarr1 = (JSONObject) jarr.get(i);
                            if ("720".equals(jarr1.get("height"))) {
                                videoUrl = jarr1.get("url").toString();
                                break;
                            } else {
                                videoUrl = jarr1.get("url").toString();
                            }
                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    client.getConnectionManager().closeExpiredConnections();
                }
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return videoUrl;
    }
}
