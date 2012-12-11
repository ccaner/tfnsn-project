package com.tfnsnproject.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class ApiClient {

    private final String serverUrl = "http://72.44.39.36:8080";

    private final HttpClient httpclient = new DefaultHttpClient();

    public void checkinWithMedia(String message, InputStream media, Double longg, Double lat) {
        try {
            HttpPost httppost = new HttpPost(serverUrl + "/checkin-with-media");

            InputStreamBody bin = new InputStreamBody(media, "somefile.jpg");

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("media", bin);
            reqEntity.addPart("message", new StringBody(message));
            reqEntity.addPart("long", new StringBody(longg.toString()));
            reqEntity.addPart("lat", new StringBody(lat.toString()));

            httppost.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == 200) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
    }

}
