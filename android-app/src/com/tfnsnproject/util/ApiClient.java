package com.tfnsnproject.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private final String serverUrl = "http://72.44.39.36:8080";

    private final HttpClient httpclient = new DefaultHttpClient();

    private static final ApiClient INSTANCE = new ApiClient();

    private ApiClient() {
    }

    public static ApiClient getInstance() {
        return INSTANCE;
    }

    public void checkinWithMedia(String authToken, String message, InputStream media, Double longg, Double lat) {
        try {
            HttpPost httppost = new HttpPost(serverUrl + "/checkin-with-media");
            httppost.addHeader("Authorization", authToken);
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
            response.getEntity().consumeContent();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public String login(String username, String password) {
        try {
            HttpPost httpPost = new HttpPost(serverUrl + "/auth/login");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", username));
            nvps.add(new BasicNameValuePair("password", password));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            HttpResponse response = httpclient.execute(httpPost);
            String code = null;
            if (response.getStatusLine().getStatusCode() == 200) {
                code = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }
            response.getEntity().consumeContent();
            return code;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

}

