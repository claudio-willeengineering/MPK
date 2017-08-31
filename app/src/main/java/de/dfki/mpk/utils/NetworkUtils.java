package de.dfki.mpk.utils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by student on 31.08.17.
 */

public class NetworkUtils {

    public static final String FB_POST_URL = "http://uni-data.wearcom.org/submit/mpk_facebook/";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body).header("X-Auth-Token","0cbd834a8b99db006006d44f21e69e8b")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}