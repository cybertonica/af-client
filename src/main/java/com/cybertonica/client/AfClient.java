package com.cybertonica.client;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Created by oem.
 */
@SuppressWarnings("unused")
public class AfClient {

    private HttpURLConnection connection;
    private String url;
    private String host;
    private int port;

    private AfClient() {}

    private AfClient(String url) throws MalformedURLException {
        this.url = url;
    }

    private AfClient(String host, int port) throws MalformedURLException {
        this.host = host;
        this.port = port;
    }

    public static AfClient get(String url) throws MalformedURLException {
        return new AfClient(url);
    }

    public static AfClient get(String host, int port) throws MalformedURLException {
        return new AfClient(host, port);
    }

    public JsonObject createEvent(AfOptions options) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        URL url = this.url != null ? new URL(this.url) : new URL(String.format("http://%s:%s/api/v2/scoring/createEvent", host, port));
        openConnection(url, options);
        return post(options.toString());
    }

    public JsonObject updateEvent(AfOptions options) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        URL url = this.url != null ? new URL(this.url) : new URL(String.format("http://%s:%s/api/v2/scoring/updateEvent", host, port));
        openConnection(url, options);
        return post(options.toString());
    }

    private String sign(String json, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        javax.crypto.spec.SecretKeySpec ks = new javax.crypto.spec.SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA1");
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(ks);
        byte[] result = mac.doFinal(json.getBytes("UTF-8"));
        return new sun.misc.BASE64Encoder().encode(result);
    }

    private JsonObject post(String params) throws IOException {
        byte[] postData = params.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
        } catch (Exception ex) {
            if (wr != null) {
                wr.close();
            }
        }
        Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonStr = s.hasNext() ? s.next() : "";
        return Json.parse(Json.parse(jsonStr).asString()).asObject();
    }

    private AfClient openConnection(URL url, AfOptions options) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setRequestProperty("apiUserId", options.user);
        connection.setRequestProperty("apiSignature", sign(options.toString(), options.signature));
        connection.setUseCaches(false);
        return this;
    }

    public static String SHA_256(String src) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(src.getBytes("UTF-8"));
        byte[] digest = md.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte aDigest : digest) {
            hexString.append(Integer.toHexString(0xFF & aDigest));
        }
        return hexString.toString();
    }
}
