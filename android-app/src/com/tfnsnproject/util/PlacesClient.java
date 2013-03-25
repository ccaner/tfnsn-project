package com.tfnsnproject.util;

import android.net.Uri;
import com.tfnsnproject.to.PlaceInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlacesClient {

    private static final String API_KEY = "AIzaSyBlksj8lap_8j97MdX1lQoID4qmhwDjD-g";

    private final HttpClient httpclient = sslClient(new DefaultHttpClient());

    private static final PlacesClient INSTANCE = new PlacesClient();

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private PlacesClient() {
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                (int)TimeUnit.SECONDS.toMillis(3));
    }

    public static PlacesClient getInstance() {
        return INSTANCE;
    }

    public List<PlaceInfo> searchNearby(Double lat, Double longg, int radius) {
        Uri uri = new Uri.Builder().scheme("https").authority("maps.googleapis.com")
                .path("/maps/api/place/nearbysearch/json")
                .appendQueryParameter("location", lat + "," + longg)
                .appendQueryParameter("radius", Integer.toString(radius))
                .appendQueryParameter("sensor", "true")
                .appendQueryParameter("key", API_KEY).build();

        List<PlaceInfo> places = null;
        try {
            HttpGet get = new HttpGet(uri.toString());
            HttpResponse response = httpclient.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                places = new ArrayList<PlaceInfo>();
                JsonNode rootNode = jsonMapper.readTree(response.getEntity().getContent());
                List<JsonNode> results = rootNode.findValues("results");
                if (results != null && !results.isEmpty()) {
                    for (JsonNode result : results.get(0)) {
                        PlaceInfo placeInfo = new PlaceInfo();
                        placeInfo.setId(result.findValue("id").asText());
                        placeInfo.setName(result.findValue("name").asText());
                        placeInfo.setLat(result.get("geometry").get("location").get("lat").asDouble());
                        placeInfo.setLong(result.get("geometry").get("location").get("lng").asDouble());
                        places.add(placeInfo);
                    }
                }
            }
            response.getEntity().consumeContent();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return places;
    }

    private static HttpClient sslClient(HttpClient client) {
        try {
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new UnsafeSSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, client.getParams());
        } catch (Exception ex) {
            return null;
        }
    }
}